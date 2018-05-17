package com.xiaolanba.passenger.module.person.activity;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.common.listener.SimpleTextWatcher;
import com.xiaolanba.passenger.common.utils.Constants;
import com.xiaolanba.passenger.common.utils.ResUtil;
import com.xiaolanba.passenger.logic.control.LBController;
import com.xiaolanba.passenger.logic.manager.SharedPreferencesManager;
import com.xiaolanba.passenger.module.main.activity.MainActivity;
import com.xiaolanba.passenger.module.person.presenter.LoginRegistControl;
import com.xiaolanba.passenger.module.person.presenter.LoginRegistPresenter;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBus;
import com.xiaolanba.passenger.rxandroid.rxbus.RxConstants;
import com.xlb.elect.cigar.R;

/**
 * 注册，找回密码，绑定手机号的，输入验证码界面
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/05
 */

public class InputIdentifyActivity extends BaseActivity {

    private Button mNextBtn;
    private String phone;
    private EditText mInput;
    private TextView mResendTxt;
    private TextView mPhoneTxt;
    private ImageView mClearImg;
    public static void startActivity(Context context,String phone){
        Intent intent = new Intent(context,InputIdentifyActivity.class);
        intent.putExtra(Constants.PHONE_NUM,phone);

        context.startActivity(intent);
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.regist_identify);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.PHONE_NUM)){
            phone = intent.getStringExtra(Constants.PHONE_NUM);

        }
    }

    @Override
    public void findView() {
        findViewById(R.id.common_title_root).setBackgroundResource(R.drawable.bg_title);
        ImageButton leftBtn = (ImageButton) findViewById(R.id.left_img_btn);
        leftBtn.setImageResource(R.drawable.return_back);
        leftBtn.setOnClickListener(this);
        mNextBtn = (Button) findViewById(R.id.login_btn);
        mNextBtn.setOnClickListener(this);
        mResendTxt = (TextView) findViewById(R.id.resend_txt);
        mResendTxt.setOnClickListener(this);
        mInput = (EditText) findViewById(R.id.input);
        mPhoneTxt = (TextView) findViewById(R.id.phone_txt);
        mClearImg = (ImageView) findViewById(R.id.clear_img);
        mClearImg.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mPhoneTxt.setText(phone);
        mInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mInput.getText();
                int totalLen = editable.toString().length();
                mClearImg.setVisibility(totalLen > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
        startCount(true, normalDownTimer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_img_btn:
                finish();
                break;
            case R.id.clear_img:
                mInput.setText("");
                break;
            case R.id.login_btn:
                String input = mInput.getText().toString();
                if (input.length() == 0){
                    showToast("请输入验证码");
                    return;
                }
                InputPsdActivity.startActivity(context,phone,input);
                break;
            case R.id.resend_txt:
                startCount(true, normalDownTimer);
                break;
        }
    }

    private boolean isSendCodeSuccess;

    private LoginRegistPresenter mPresenter = new LoginRegistPresenter(new LoginRegistControl() { //使用共用的ViewControl，减少回调个数

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void onCreateCheckCode(boolean isSuccess, String message) {
            dismissProgressDialog();
            isSendCodeSuccess = isSuccess;
            if (isSuccess) { //发送验证码成功

            } else {
                if (message != null) {
                    showToast(message);
                }
            }
        }

    });

    private boolean isCountStarted;
    /**
     * 开始倒计时,由于倒计时总时长不固定，所以传入倒计时对象
     */
    private void startCount(boolean isNeedRequest, MyCountDown countDownTimer) { //是否需要请求短信验证码接口
        if (isCountStarted) {
            return;
        }
        if (isNeedRequest) {
            mPresenter.createCheckCode(phone);
        }
        mResendTxt.setText("重新发送 60s");
        mResendTxt.setClickable(false);
        countDownTimer.start();
        isCountStarted = true;
    }

    private MyCountDown normalDownTimer = new MyCountDown(60 * 1000, 1000);
    /**
     * 因为倒计时的时间并不是固定的，所以写一个子类
     */
    private class MyCountDown extends CountDownTimer {

        public MyCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mResendTxt.setText("重新发送 "+(millisUntilFinished / 1000)+"s");
            mResendTxt.setClickable(false);
//            mCountTxt.setText(String.format(ResUtil.getString(R.string.some_time_resend), millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            mResendTxt.setText("重新发送");
            mResendTxt.setClickable(true);
//            mCountLayout.setVisibility(View.GONE);
//            mReSendTxt.setVisibility(View.VISIBLE);
//            mReSendTxt.setText(ResUtil.getString(R.string.re_send));
            isCountStarted = false;
        }
    }
}
