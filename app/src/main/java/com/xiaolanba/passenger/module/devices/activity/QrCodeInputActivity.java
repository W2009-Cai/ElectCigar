package com.xiaolanba.passenger.module.devices.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.framework.common.utils.IKeyboardUtils;
import com.framework.common.utils.ILog;
import com.xiaolanba.passenger.bluetooth.LeDevice;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.logic.control.LBController;
import com.xiaolanba.passenger.rxandroid.rxbus.RxAction;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBus;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBusObserver;
import com.xiaolanba.passenger.rxandroid.rxbus.RxConstants;
import com.xlb.elect.cigar.R;

import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 类描述,二维码扫描时的输入框
 *
 * @author xutingz
 * @email：914603097@qq.com
 */

public class QrCodeInputActivity extends BaseActivity {
    private ImageButton mLeftBtn;
    private EditText mInputEdit;
    private String blueName;
    private Disposable mDisposable;
    public static void startActivity(Context context,String name){
        Intent intent = new Intent(context,QrCodeInputActivity.class);
        intent.putExtra("blueName",name);
        context.startActivity(intent);
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_decode_input);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("blueName")) {
            blueName = intent.getStringExtra("blueName");
        }
    }

    @Override
    public void findView() {
        mLeftBtn = (ImageButton) findViewById(R.id.left_img_btn);
        mLeftBtn.setOnClickListener(this);
        mInputEdit = (EditText) findViewById(R.id.input);
        mInputEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) { //点击回车,收起键盘
                    IKeyboardUtils.closeKeybord(QrCodeInputActivity.this);
                    checkInput();
                }
                return false;
            }
        });
    }

    public void checkInput() {
        String input = mInputEdit.getText().toString().trim().toUpperCase();
        if (input.length() > 0 && input.startsWith("TTC")) {
            if (blueName != null && blueName.startsWith(input)){
                input = blueName;
            }
            RxBus.getDefault().postWithCode(RxConstants.ACTION_QRCODE,input);
        } else {
            showToast("未找到相应设备", 1500);
        }
    }

    @Override
    public void initData() {
        LBController.MainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLeftBtn.setVisibility(View.VISIBLE);
            }
        }, 500);
        registRxBus();
        IKeyboardUtils.openKeybord(context,mInputEdit);
    }

    /**
     * 通过RxBus注册监听事件
     */
    private void registRxBus() {
        List<Integer> list = Arrays.asList(RxConstants.ACTION_QRCODE_RESULT);
        RxBus.getDefault().toObservableWithCodes(list).subscribeWith(new RxBusObserver<RxAction>() {
            @Override
            public void onRxSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onRxNext(RxAction value) {
                switch (value.code) {
                    case RxConstants.ACTION_QRCODE_RESULT://
                        if (value.data != null && value.data instanceof LeDevice) {
                            finish();
                        } else {
                            ILog.e("mm", "---没发现蓝牙设备");
                            showToast("未找到相应设备", 1500);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_img_btn:
                mLeftBtn.setVisibility(View.GONE);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null){
            mDisposable.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        mLeftBtn.setVisibility(View.GONE);
        super.onBackPressed();
    }
}
