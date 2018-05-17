package com.xiaolanba.passenger.module.main.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ble.api.DataUtil;
import com.ble.api.EncodeUtil;
import com.ble.ble.BleService;
import com.framework.common.base.IBaseFragment;
import com.framework.common.utils.ICommonUtil;
import com.framework.common.utils.ILog;
import com.xiaolanba.passenger.bluetooth.LeDevice;
import com.xiaolanba.passenger.bluetooth.LeProxy;
import com.xiaolanba.passenger.bluetooth.StringUtil;
import com.xiaolanba.passenger.bluetooth.TimeUtil;
import com.xiaolanba.passenger.common.base.BaseFragmentActivity;
import com.xiaolanba.passenger.logic.control.LBController;
import com.xiaolanba.passenger.logic.manager.SharedPreferencesManager;
import com.xiaolanba.passenger.module.community.CommunityFragment;
import com.xiaolanba.passenger.module.devices.DevicesFragment;
import com.xiaolanba.passenger.module.market.MarketFragment;
import com.xiaolanba.passenger.module.person.MineFragment;
import com.xiaolanba.passenger.rxandroid.rxbus.RxAction;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBus;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBusObserver;
import com.xiaolanba.passenger.rxandroid.rxbus.RxConstants;
import com.xlb.elect.cigar.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseFragmentActivity implements RadioGroup.OnCheckedChangeListener, Handler.Callback {
    public static final int TAG_HOME = 0;
    public static final int TAG_MARKET = 1;
    public static final int TAG_COMMUNITY = 2;
    public static final int TAG_MY = 3;

    private RadioGroup mTabGroup;
    private RadioButton mHomeBtn, mMarketBtn, mCommunityBtn, mMyBtn;
    private Handler mHandler;
    private long lastClickTabId;
    private BluetoothAdapter mBluetoothAdapter;
    public LeProxy mLeProxy;
    private boolean mScanning;
    private static final long SCAN_PERIOD = 5000;
    public static final String EXTRA_DEVICE_ADDRESS = "extra_device_address";
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public LeDevice saveDevice;
    private static final int REQUEST_ENABLE_BT = 2;
    private Disposable mDisposable;
    private SharedPreferencesManager mPreferencesManager;
    private int saveF5;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
        mHandler = new Handler(this);
        mLeProxy = LeProxy.getInstance();
        checkBLEFeature();

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, makeFilter());
        LocalBroadcastManager.getInstance(context).registerReceiver(mDataReceiver, makeDataReceiveFilter());
        BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bm != null) {
            mBluetoothAdapter = bm.getAdapter();
        }
        bindService(new Intent(this, BleService.class), mConnection, BIND_AUTO_CREATE);

    }

    @Override
    public void findView() {
        mTabGroup = (RadioGroup) findViewById(R.id.tab_layout);
        mTabGroup.setOnCheckedChangeListener(this);
        mHomeBtn = (RadioButton) findViewById(R.id.home_tab);
        mCommunityBtn = (RadioButton) findViewById(R.id.community_tab);
        mMyBtn = (RadioButton) findViewById(R.id.my_tab);
        mMarketBtn = (RadioButton) findViewById(R.id.market_tab);

        mHomeBtn.setOnClickListener(this);
        mCommunityBtn.setOnClickListener(this);
        mMyBtn.setOnClickListener(this);
        mMarketBtn.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mPreferencesManager = new SharedPreferencesManager();
        fragments = new IBaseFragment[4];
        fragments[TAG_HOME] = new DevicesFragment();
        fragments[TAG_MARKET] = new MarketFragment();
        fragments[TAG_COMMUNITY] = new CommunityFragment();
        fragments[TAG_MY] = new MineFragment();
        mHomeBtn.setChecked(true);
        lastClickTabId = R.id.home_tab;
        boolean hasLogin = hasLogin();
        registRxBus();

    }

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 通过RxBus注册监听事件
     */
    private void registRxBus() {
        List<Integer> list = Arrays.asList(RxConstants.ACTION_QRCODE,RxConstants.ACTION_REMOVE,RxConstants.ACTION_LIMIT);
        RxBus.getDefault().toObservableWithCodes(list).subscribeWith(new RxBusObserver<RxAction>() {
            @Override
            public void onRxSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onRxNext(RxAction value) {
                switch (value.code) {
                    case RxConstants.ACTION_QRCODE://
                        if (value.data != null && value.data instanceof String) {
                            String name = (String) value.data;
                            if (saveDevice != null && name.equals(saveDevice.getName())) {
                                if (isConnected){
                                    showToast("该设备已连接");
                                    return;
                                }
                                boolean ok = mLeProxy.connect(saveDevice.getAddress(), false);// TODO
                                Log.e(TAG, saveDevice.getAddress() + " ---onLeScan connect():" + ok);
                            } else {
                                showToast("没有可连接的蓝牙设备");
                                RxBus.getDefault().postWithCode(RxConstants.ACTION_QRCODE_RESULT, null);
                            }
                        }
                        break;
                    case RxConstants.ACTION_REMOVE:
                        if (isConnected && saveDevice != null){
                            mLeProxy.disconnect(saveDevice.getAddress());
                        }
                        break;
                    case RxConstants.ACTION_LIMIT:
                        if (value.data != null && value.data instanceof Integer) {
                            int num = (int) value.data;
                            boolean flag = makeDataF2(num);
                            if (flag){
                                mPreferencesManager.save(SharedPreferencesManager.KEY_LIMIT_NUM,num);
                                ((DevicesFragment) fragments[TAG_HOME]).setLimitNum(num);
                            } else {
                                showToast("尚未连接设备");
                            }
                        }
                        break;
                }
            }
        });
    }

    private IntentFilter makeDataReceiveFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_RSSI_AVAILABLE);
        filter.addAction(LeProxy.ACTION_DATA_AVAILABLE);
        return filter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataReceiver);
        unbindService(mConnection);
        controller.clear();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        if (isConnected && saveDevice != null){
            mLeProxy.disconnect(saveDevice.getAddress());
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mScanning)
                    return;
                mScanning = true;
                mHandler.postDelayed(mScanRunnable, SCAN_PERIOD);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                showToast("蓝牙未打开");
            }
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mHandler.removeCallbacks(mScanRunnable);
            mScanning = false;
        }
    }

    private final Runnable mScanRunnable = new Runnable() {

        @Override
        public void run() {
            scanLeDevice(false);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String name = device.getName();
                    if (name != null && name.startsWith("TTC")) { //TTC_CC2640_SDK
                        saveDevice = new LeDevice(device.getName(), device.getAddress(), rssi, scanRecord);
//                        boolean ok = mLeProxy.connect(saveDevice.getAddress(), false);// TODO
                    }
                }
            });
        }
    };


    private void checkBLEFeature() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast("BLE not support");
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            showToast("蓝牙未打开");
            finish();
            return;
        }
    }

    public void sendLocalData(String input) {
        ILog.e(TAG, "----sendLocalData=" + input);
        if (saveDevice != null) {
            byte[] bytes = DataUtil.hexToByteArray(input);
            mLeProxy.send(saveDevice.getAddress(), bytes, true);
        }
    }

    private long connectTimeMill;
    public boolean isConnected;
    private final BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);
            switch (intent.getAction()) {
                case LeProxy.ACTION_GATT_CONNECTED:
                    showToast("已连接");
                    isConnected = true;
                    ILog.e(TAG, "----已连接");
                    connectTimeMill = System.currentTimeMillis();
                    setDevicesFragmentState(true, 0);
                    RxBus.getDefault().postWithCode(RxConstants.ACTION_QRCODE_RESULT, saveDevice);
                    LBController.MainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isFinishing()) return;
                            sendLocalData("F000");
                        }
                    }, 100);
                    break;
                case LeProxy.ACTION_GATT_DISCONNECTED:
                    ILog.e(TAG, "----断开连接");
                    isConnected = false;
                    setDevicesFragmentState(false, 0);
                    RxBus.getDefault().postWithCode(RxConstants.ACTION_REMOVE_RESULT, address);
                    if ((System.currentTimeMillis() - connectTimeMill) < 1000 && saveDevice != null) {
                        boolean ok = mLeProxy.connect(saveDevice.getAddress(), false);// TODO
                        Log.e(TAG, saveDevice.getAddress() + " ---onLeScan connect():" + ok);//尝试重新连接
                    }
                    break;
                case LeProxy.ACTION_CONNECT_ERROR:
                    ILog.e(TAG, "----连接异常");
                    RxBus.getDefault().postWithCode(RxConstants.ACTION_QRCODE_RESULT, null);
                    isConnected = false;
                    setDevicesFragmentState(false, 0);
                    break;
                case LeProxy.ACTION_CONNECT_TIMEOUT:
                    ILog.e(TAG, "----连接超时");
                    RxBus.getDefault().postWithCode(RxConstants.ACTION_QRCODE_RESULT, null);
                    isConnected = false;
                    setDevicesFragmentState(false, 0);
                    break;
                case LeProxy.ACTION_GATT_SERVICES_DISCOVERED:
                    ILog.e(TAG, "----发现设备");

                    break;
            }
        }
    };

    private void setDevicesFragmentState(boolean isConcect, int num) {
        ((DevicesFragment) fragments[TAG_HOME]).setBluetoothTxt(isConcect);
        if (num > 0) {
            ((DevicesFragment) fragments[TAG_HOME]).setTodayNum(num);
        }
    }

    private void setDevicesFragmentF8(boolean isCharging, int percent) {
        ((DevicesFragment) fragments[TAG_HOME]).setIsCharging(isCharging);
        ((DevicesFragment) fragments[TAG_HOME]).setBattery(percent);
    }

    private final BroadcastReceiver mDataReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);
            switch (intent.getAction()) {
                case LeProxy.ACTION_GATT_DISCONNECTED:// 断线
                    break;

                case LeProxy.ACTION_RSSI_AVAILABLE: {// 更新rssi
                }
                break;

                case LeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                    displayRxData(intent);
                    break;
            }
        }
    };
    private EncodeUtil mEncodeUtil = new EncodeUtil();

    private int sendF5Count;

    private void displayRxData(Intent intent) {
        String address = intent.getStringExtra(LeProxy.EXTRA_ADDRESS);
        String uuid = intent.getStringExtra(LeProxy.EXTRA_UUID);
        byte[] data = intent.getByteArrayExtra(LeProxy.EXTRA_DATA);
        //如果数据加密，这里就解密一下
        data = mEncodeUtil.decodeMessage(data);
        String dataStr = DataUtil.byteArrayToHex(data);
        ILog.e(TAG, "---dataStr=" + dataStr);
//        showToast(dataStr);
        if (dataStr != null) {
            String[] arrays = dataStr.split(" ");
            if (dataStr.startsWith("F0")) {
                mHander.post(F5Runnable); //发送f5数据
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                int second = c.get(Calendar.SECOND);
                int week = c.get(Calendar.DAY_OF_WEEK);
                StringBuilder sb = new StringBuilder();
                sb.append("F100");
                String yearStr = StringUtil.tenChangeToHex(year);
                if (yearStr.length() == 4) {
                    String before = yearStr.substring(0, 2);
                    String after = yearStr.substring(2, 4);
                    sb.append(after + before);
                } else {
                    sb.append("E207");
                }
                sb.append(StringUtil.tenChangeToHex(month + 1));
                sb.append(StringUtil.tenChangeToHex(day));
                sb.append(StringUtil.tenChangeToHex(hour));
                sb.append(StringUtil.tenChangeToHex(minute));
                sb.append(StringUtil.tenChangeToHex(second));
                sb.append(StringUtil.tenChangeToHex(week));
                sendLocalData(sb.toString());
            } else if (dataStr.startsWith("F1")) {

            } else if (dataStr.startsWith("F2")) {

            } else if (dataStr.startsWith("F3")) {

            } else if (dataStr.startsWith("F4")) {

            } else if (dataStr.startsWith("F5")) {
//                ILog.e(TAG, "---F5长度=" + arrays.length);
                int total = 0;
                for (int i = 1; i < arrays.length; i += 2) {
                    if ((i + 1) < arrays.length) {
                        int k = StringUtil.hexChangeToTen(arrays[i + 1] + arrays[i]);
                        total += k;
                    }
                }
                saveF5 += total;
                if (sendF5Count <3){ //第一次要立即展示
                    setDevicesFragmentState(true, saveF5);
                }

//                showToast("F5 口数="+total);
            } else if (dataStr.startsWith("F6")) {

            } else if (dataStr.startsWith("F7")) {

            } else if (dataStr.startsWith("F8")) {
                String battery = arrays[1] + arrays[2];
                String charging = arrays[3];
                int batteryInt = StringUtil.hexChangeToTen(battery);
                int chargeInt = StringUtil.hexChangeToTen(charging);
                setDevicesFragmentF8(chargeInt == 1, batteryInt);
            } else if (dataStr.startsWith("F9")) { // f9 00 00 10 e8 03
                if (arrays.length == 6) {
                    int leftTotal = StringUtil.hexChangeToTen(arrays[5] + arrays[4]); //剩余总口数
                    int mouthNum = StringUtil.hexChangeToTen(arrays[3] + arrays[2]); //已经吸烟了的口数
                    ((DevicesFragment) fragments[TAG_HOME]).setOilPercent(leftTotal * 100 / (leftTotal + mouthNum) * 1.0f, mouthNum);
                }
            }
        }
    }

    private Runnable F5Runnable = new Runnable() {
        @Override
        public void run() {
            if (isConnected) {
                sendF5Count ++;
                sendLocalData("F500");
                mHander.postDelayed(F5Runnable, 10000);
                setDevicesFragmentState(true, saveF5);
            }
            saveF5 = 0;
        }
    };

    public  boolean makeDataF2(int num) { //设置口数限制
        if (isConnected){
            StringBuilder sb = new StringBuilder();
            String hex = StringUtil.tenChangeToHex(num);
            if (hex.length() == 2) {
                hex = "00" + hex;
            } else if (hex.length() == 3) {
                hex = "0" + hex;
            }
            sb.append("F200").append(hex.substring(2, 4)).append(hex.substring(0, 2));
            sendLocalData(sb.toString());
            return true;
        }
        return false;
    }

    public boolean makeDataF3(boolean hasLost, boolean hasChildLock, boolean autoLock) { //设置童锁等
        if (isConnected){
            StringBuilder sb = new StringBuilder("F300");
            sb.append(hasLost ? "01" : "00");
            sb.append(hasChildLock ? "01" : "00");
            sb.append(autoLock ? "01" : "00");
            sendLocalData(sb.toString());
            return true;
        }
        return false;
    }

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeProxy.ACTION_GATT_CONNECTED);
        filter.addAction(LeProxy.ACTION_GATT_DISCONNECTED);
        filter.addAction(LeProxy.ACTION_CONNECT_ERROR);
        filter.addAction(LeProxy.ACTION_CONNECT_TIMEOUT);
        filter.addAction(LeProxy.ACTION_GATT_SERVICES_DISCOVERED);
        return filter;
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "---onServiceDisconnected()");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "---onServiceConnected()");
            LeProxy.getInstance().setBleService(service);
            if (mBluetoothAdapter != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanLeDevice(true);
                    }
                }, 200);
            }
        }
    };

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_tab:
                if (fragments[TAG_HOME].isAdded()) {
                    if (!ICommonUtil.isFastDoubleClick(200L)) { //防止以双击方式切换tab,同时执行下拉刷新
                        tabAnimator(mHomeBtn);
                    }
                }
                break;
            case R.id.market_tab:
                if (fragments[TAG_MARKET].isAdded()) {
                    if (!ICommonUtil.isFastDoubleClick(200L)) {
                        tabAnimator(mMarketBtn);
                    }
                }

                break;
            case R.id.community_tab:
                if (fragments[TAG_COMMUNITY].isAdded()) {
                    if (!ICommonUtil.isFastDoubleClick(200L)) {
                        tabAnimator(mCommunityBtn);
                    }
                }
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                int second = c.get(Calendar.SECOND);
                int week = c.get(Calendar.DAY_OF_WEEK);
                StringBuilder sb = new StringBuilder();
                sb.append("F100");
                String yearStr = StringUtil.tenChangeToHex(year);
                if (yearStr.length() == 4) {
                    String before = yearStr.substring(0, 2);
                    String after = yearStr.substring(2, 4);
                    sb.append(after + before);
                } else {
                    sb.append("E207");
                }
                sb.append(StringUtil.tenChangeToHex(month));
                sb.append(StringUtil.tenChangeToHex(day));
                sb.append(StringUtil.tenChangeToHex(hour));
                sb.append(StringUtil.tenChangeToHex(minute));
                sb.append(StringUtil.tenChangeToHex(second));
                sb.append(StringUtil.tenChangeToHex(week));
                String a = sb.toString();
                break;
            case R.id.my_tab:
                if (fragments[TAG_MY].isAdded()) {
                    if (!ICommonUtil.isFastDoubleClick(200L)) {
                        tabAnimator(mMyBtn);
                    }
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        switch (checkedId) {
            // 首页
            case R.id.home_tab:
                switchFragment(R.id.content_frame, TAG_HOME);
                break;
            // 商城
            case R.id.market_tab:
                switchFragment(R.id.content_frame, TAG_MARKET);
                break;
            // 社区
            case R.id.community_tab:
                switchFragment(R.id.content_frame, TAG_COMMUNITY);
                break;
            // 我的
            case R.id.my_tab:
                switchFragment(R.id.content_frame, TAG_MY);
                break;
            default:
                break;
        }
    }

    public boolean isCheckedHome() {
        if (null != mTabGroup && R.id.home_tab == mTabGroup.getCheckedRadioButtonId()) {
            return true;
        }
        return false;
    }

    public boolean isCheckedMarket() {
        if (null != mTabGroup && R.id.market_tab == mTabGroup.getCheckedRadioButtonId()) {
            return true;
        }
        return false;
    }

    public boolean isCheckedCommunity() {
        if (null != mTabGroup && R.id.community_tab == mTabGroup.getCheckedRadioButtonId()) {
            return true;
        }
        return false;
    }

    public boolean isCheckedMine() {
        if (null != mTabGroup && R.id.my_tab == mTabGroup.getCheckedRadioButtonId()) {
            return true;
        }
        return false;
    }


    private void tabAnimator(final View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleX", 1.15f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 1.15f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f);

        ObjectAnimator animator5 = ObjectAnimator.ofFloat(view, "scaleX", 1.10f);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(view, "scaleY", 1.10f);
        ObjectAnimator animator7 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f);
        ObjectAnimator animator8 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f);
        animatorSet.play(animator1).with(animator2).before(animator3);
        animatorSet.play(animator3).with(animator4).before(animator5);
        animatorSet.play(animator5).with(animator6).before(animator7);
        animatorSet.play(animator7).with(animator8);
        animatorSet.setDuration(100);
        animatorSet.start();
    }

    /**
     * 设置再按一次退出程序
     */
    private boolean isExit = false;

    @Override
    public void onBackPressed() {
        if (!isExit) {
            isExit = true;
            showToast("再按一次退出程序");
            LBController.MainHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
            return;
        } else {
            super.onBackPressed();
        }
    }
}
