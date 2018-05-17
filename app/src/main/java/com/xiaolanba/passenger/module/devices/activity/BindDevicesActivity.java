package com.xiaolanba.passenger.module.devices.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.framework.common.utils.ICommonUtil;
import com.framework.common.utils.ILog;
import com.xiaolanba.passenger.bluetooth.LeDevice;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.common.bean.BaseBean;
import com.xiaolanba.passenger.common.bean.DevicesBind;
import com.xiaolanba.passenger.common.interfaces.EventListener;
import com.xiaolanba.passenger.common.utils.Constants;
import com.xiaolanba.passenger.common.utils.ResUtil;
import com.xiaolanba.passenger.library.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.xiaolanba.passenger.library.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.xiaolanba.passenger.module.devices.adapter.ConversationAdapter;
import com.xiaolanba.passenger.rxandroid.rxbus.RxAction;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBus;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBusObserver;
import com.xiaolanba.passenger.rxandroid.rxbus.RxConstants;
import com.xlb.elect.cigar.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/05
 */

public class BindDevicesActivity extends BaseActivity{
    private int qrRequestCode = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ConversationAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter; //侧滑删除包装类
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private View mAddLayout;
    private DevicesBind mLedevices;
    private Disposable mDisposable;
    private boolean isConnected;

    public static void startActivity(Context context,DevicesBind mLedevices,boolean flag){
        Intent intent = new Intent(context,BindDevicesActivity.class);
        if (mLedevices != null) {
            intent.putExtra(Constants.LEDEVICES, mLedevices);
            intent.putExtra(Constants.IS_CONNECTED,flag);
        }
        context.startActivity(intent);
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.devices_manage);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.LEDEVICES)){
            Serializable obj = intent.getSerializableExtra(Constants.LEDEVICES);
            if (obj != null){
                mLedevices = (DevicesBind) obj;
            }
            isConnected = intent.getBooleanExtra(Constants.IS_CONNECTED,false);
        }
    }

    @Override
    public void findView() {
        findViewById(R.id.left_img_btn).setOnClickListener(this);
        findViewById(R.id.add_btn).setOnClickListener(this);
        TextView rightBtn = (TextView) findViewById(R.id.right_txt);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("添加");
        rightBtn.setTextColor(ResUtil.getColor(R.color.white));
        rightBtn.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAddLayout = findViewById(R.id.add_layout);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();
        mRecyclerViewSwipeManager.setOnItemSwipeEventListener(new RecyclerViewSwipeManager.OnItemSwipeEventListener() {
            @Override  //开始滑动时，之前展开的item要关闭回去
            public void onItemSwipeStarted(int position) {
                closeSwipe();
            }

            @Override
            public void onItemSwipeFinished(int position, int result, int afterSwipeReaction) {

            }
        });
        mAdapter = new ConversationAdapter(context);
        mAdapter.setEventListener(new EventListener() {
            @Override
            public void onItemPinned(int position) {
                // item被展开了
            }

            @Override
            public void onItemViewClicked(View v) {
                if (ICommonUtil.isFastDoubleClick()) return;
                boolean hasItemOpen = closeSwipe();
                if (hasItemOpen) {
                    return;
                }
                int position = mRecyclerView.getChildAdapterPosition(v);
                if (position != RecyclerView.NO_POSITION) {
                }
            }

            @Override
            public void onUnderSwipeableViewButtonClicked(View v, View layout) {
                final int position = mRecyclerView.getChildAdapterPosition(layout);
                if (position != RecyclerView.NO_POSITION) {
                    final DevicesBind item = (DevicesBind) mAdapter.getItem(position);
                    switch (v.getId()) {
                        case R.id.unbind_txt:
//                            showToast("1");
                            RxBus.getDefault().postWithCode(RxConstants.ACTION_REMOVE);
                            break;
                        case R.id.rename_txt:
//                            showToast("2");
                            break;
                    }
                }

            }
        });
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
    }

    @Override
    public void initData() {
        if (isConnected && mLedevices!=null){
            mAdapter.addChild(mLedevices);
            mAddLayout.setVisibility(View.GONE);
        }
        registRxBus();
    }
    /**
     * 通过RxBus注册监听事件
     */
    private void registRxBus() {
        List<Integer> list = Arrays.asList(RxConstants.ACTION_QRCODE_RESULT,RxConstants.ACTION_REMOVE_RESULT);
        RxBus.getDefault().toObservableWithCodes(list).subscribeWith(new RxBusObserver<RxAction>() {
            @Override
            public void onRxSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onRxNext(RxAction value) {
                switch (value.code) {
                    case RxConstants.ACTION_QRCODE_RESULT://
                        dismissProgressDialog();
                        if (value.data != null && value.data instanceof LeDevice) {
                            LeDevice device = (LeDevice) value.data;
                            DevicesBind bean = new DevicesBind();
                            bean.name = device.getName();
                            bean.address = device.getAddress();
                            mAdapter.addChild(bean);
                            mAddLayout.setVisibility(View.GONE);
                        } else {
                            ILog.e("mm","---没发现蓝牙设备");
                        }
                        break;
                    case RxConstants.ACTION_REMOVE_RESULT:
                        if (value.data != null && value.data instanceof String) {
                            mAdapter.removeByAddress((String) value.data);
                            showToast("解绑成功");
                        }
                        if (mAdapter.getItemCount() ==0){
                            mAddLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });
    }
    /**
     * 关闭滑动块
     */
    private boolean closeSwipe() {
        //关闭其它item
        boolean hasItemOpen = false;
        if (mAdapter != null) {
            int count = mAdapter.getChildCount();
            for (int i = 0; i < count; i++) {
                DevicesBind msg = (DevicesBind) mAdapter.getChild(i);
                if (msg != null && msg.isPinned()) {
                    msg.setPinned(false);
                    hasItemOpen = true;
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
        return hasItemOpen;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null){
            mDisposable.dispose();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_btn:
                QrCodeCaptureActivity.startActivityForResult(context,qrRequestCode,mLedevices==null?null:mLedevices.name);
                break;
            case R.id.left_img_btn:
                finish();
                break;
            case R.id.right_txt:
                QrCodeCaptureActivity.startActivityForResult(context,qrRequestCode,mLedevices==null?null:mLedevices.name);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == qrRequestCode && resultCode == Activity.RESULT_OK){
            if (data != null && data.hasExtra("codeString")){
                String codeStr = data.getStringExtra("codeString");
                ILog.i(TAG,"---得到结果="+codeStr);
                showProgressDialog();
                RxBus.getDefault().postWithCode(RxConstants.ACTION_QRCODE,codeStr);
            }
        }
    }
}
