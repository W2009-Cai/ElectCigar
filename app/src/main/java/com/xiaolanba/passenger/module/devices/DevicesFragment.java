package com.xiaolanba.passenger.module.devices;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framework.common.utils.ICommonUtil;
import com.framework.common.utils.IDisplayUtil;
import com.xiaolanba.passenger.bluetooth.LeDevice;
import com.xiaolanba.passenger.common.base.BaseFragment;
import com.xiaolanba.passenger.common.bean.DevicesBind;
import com.xiaolanba.passenger.common.bean.DevicesGridItem;
import com.xiaolanba.passenger.logic.manager.SharedPreferencesManager;
import com.xiaolanba.passenger.module.devices.activity.BindDevicesActivity;
import com.xiaolanba.passenger.module.devices.adapter.DevicesGridAdapter;
import com.xiaolanba.passenger.module.main.activity.LimitActivity;
import com.xiaolanba.passenger.module.main.activity.MainActivity;
import com.xlb.elect.cigar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/02
 */

public class DevicesFragment extends BaseFragment {

    private GridView mGridView;
    private DevicesGridAdapter mAdapter;
    private ImageButton mLeftBtn;
    private TextView mTodayNum,mBluetoothTxt,mOilTxt;
    private TextView mBatteryTxt,mTotalNum;
    private EditText mInput;
    private Button mSendTxt;
    private SharedPreferencesManager mPreferencesManager;
    @Override
    public void setContentView() {
        setContentView(R.layout.fragment_devices);
    }

    @Override
    public void findView() {
        mInput = (EditText) findViewById(R.id.input);
        mSendTxt = (Button) findViewById(R.id.send_txt);
        mSendTxt.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mLeftBtn = (ImageButton) findViewById(R.id.left_img_btn);
        mLeftBtn.setImageResource(R.drawable.nav_menu_nor);
        mLeftBtn.setOnClickListener(this);
        mOilTxt = (TextView) findViewById(R.id.oil_txt);
        RelativeLayout topLayout = (RelativeLayout) findViewById(R.id.top_area);
        mTodayNum = (TextView) findViewById(R.id.today_num);
        mBluetoothTxt = (TextView) findViewById(R.id.bluetooth_txt);
        mBatteryTxt = (TextView) findViewById(R.id.battery_txt);
        mTotalNum = (TextView) findViewById(R.id.total_num);
    }

    @Override
    public void initData() {
        List<DevicesGridItem> list = new ArrayList<>();
        mPreferencesManager = new SharedPreferencesManager();
        boolean isLost = mPreferencesManager.read(SharedPreferencesManager.KEY_LOST,false);
        boolean isChildLock = mPreferencesManager.read(SharedPreferencesManager.KEY_CHILD_LOCK,false);
        boolean isAutoLock = mPreferencesManager.read(SharedPreferencesManager.KEY_AUTO_LOCK,false);
        int limit = mPreferencesManager.read(SharedPreferencesManager.KEY_LIMIT_NUM,0);
        list.add(new DevicesGridItem(isLost?R.drawable.button_lost_on:R.drawable.button_lost_off,"防丢模式",null,isLost));
        list.add(new DevicesGridItem(isChildLock?R.drawable.button_children_lock_on:R.drawable.button_children_lock_off,"儿童锁",null,isChildLock));
        list.add(new DevicesGridItem(isAutoLock?R.drawable.button_automatic_lock_on:R.drawable.button_automatic_lock_off,"自动锁",null,isAutoLock));
        list.add(new DevicesGridItem(0,"口数限制",String.valueOf(limit),false));
        list.add(new DevicesGridItem(R.drawable.home_icon_ranking_nor,"排行榜",null,false));
        list.add(new DevicesGridItem(R.drawable.home_icon_statistics_nor,"数据统计",null,false));
        list.add(new DevicesGridItem(R.drawable.button_lost_off,"固件升级",null,false));
        list.add(new DevicesGridItem(R.drawable.button_lost_off,"数据分享",null,false));
        mAdapter = new DevicesGridAdapter(context);
        mAdapter.setList(list);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_img_btn:
                if (context instanceof MainActivity){
                    LeDevice device = ((MainActivity)context).saveDevice;
                    DevicesBind devicesBind = null;
                    if (device != null){
                        devicesBind = new DevicesBind();
                        devicesBind.name = device.getName();
                        devicesBind.address = device.getAddress();
                    }
                    BindDevicesActivity.startActivity(context,devicesBind,((MainActivity)context).isConnected); //((MainActivity)context).saveDevice
                }

                break;
            case R.id.send_txt:
                if (context instanceof MainActivity){
                    ((MainActivity)context).sendLocalData(mInput.getText().toString());
                }
                break;
        }
    }

    public void setTodayNum(int num){
        mTodayNum.setText(String.valueOf(num));
    }

    public void setBluetoothTxt(boolean flag){
        mBluetoothTxt.setText(flag?"蓝牙已连接":"蓝牙未连接");
        mOilTxt.setVisibility(flag?View.VISIBLE:View.GONE);
        if (!flag){
            disConnection();
        }
    }

    public void disConnection(){
        mBatteryTxt.setVisibility(View.GONE);
        mTodayNum.setText(String.valueOf(0));
        mTotalNum.setText(String.valueOf(0));
        mOilTxt.setVisibility(View.GONE);
    }

    public void setBattery(int num){
        mBatteryTxt.setText(num+"%");
    }

    public void setOilPercent(float f,int totalLeft){
        mTotalNum.setText(String.valueOf(totalLeft));
        mOilTxt.setText(String.valueOf((int)f)+"%");
    }

    public void setIsCharging(boolean flag){
        mBatteryTxt.setVisibility(flag?View.VISIBLE:View.INVISIBLE);
    }

    public void setLimitNum(int num){
        DevicesGridItem item = mAdapter.getItem(3);
        item.numberData = String.valueOf(num);
        mAdapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (ICommonUtil.isFastDoubleClick()) return;
            DevicesGridItem item = mAdapter.getItem(position);
            switch (position){
                case 0:
                    boolean flagLost = item.stateOk;
                    boolean changeFlag = !flagLost;
                    boolean flag =((MainActivity)context).makeDataF3(changeFlag,(mAdapter.getItem(1)).stateOk,(mAdapter.getItem(2)).stateOk);
                    if (flag){
                        item.stateOk = changeFlag;
                        item.picture = changeFlag?R.drawable.button_lost_on:R.drawable.button_lost_off;
                        mPreferencesManager.save(SharedPreferencesManager.KEY_LOST, changeFlag);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        showToast("尚未连接设备");
                    }
                    break;
                case 1:
                    boolean flagChild = item.stateOk;
                    boolean changeFlag1 = !flagChild;
                    boolean flag1 = ((MainActivity)context).makeDataF3((mAdapter.getItem(0)).stateOk,changeFlag1,(mAdapter.getItem(2)).stateOk);
                    if (flag1){
                        item.stateOk = changeFlag1;
                        item.picture = changeFlag1?R.drawable.button_children_lock_on:R.drawable.button_children_lock_off;
                        mPreferencesManager.save(SharedPreferencesManager.KEY_CHILD_LOCK, changeFlag1);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        showToast("尚未连接设备");
                    }

                    break;
                case 2:
                    boolean flagAuto = item.stateOk;
                    boolean changeFlag2 = !flagAuto;
                    boolean flag2 = ((MainActivity)context).makeDataF3((mAdapter.getItem(0)).stateOk,(mAdapter.getItem(1)).stateOk,changeFlag2);
                    if (flag2){
                        item.stateOk = changeFlag2;
                        item.picture = changeFlag2?R.drawable.button_automatic_lock_on:R.drawable.button_automatic_lock_off;
                        mPreferencesManager.save(SharedPreferencesManager.KEY_AUTO_LOCK, changeFlag2);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        showToast("尚未连接设备");
                    }
                    break;
                case 3:
                    LimitActivity.startActivity(context);
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
            }
        }
    };
}
