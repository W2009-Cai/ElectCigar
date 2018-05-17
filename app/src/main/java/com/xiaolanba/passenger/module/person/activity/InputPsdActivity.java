package com.xiaolanba.passenger.module.person.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.framework.common.utils.ICommonUtil;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.common.utils.Constants;
import com.xiaolanba.passenger.logic.control.LBController;
import com.xiaolanba.passenger.module.main.activity.MainActivity;
import com.xiaolanba.passenger.module.person.presenter.LoginRegistControl;
import com.xiaolanba.passenger.module.person.presenter.LoginRegistPresenter;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBus;
import com.xiaolanba.passenger.rxandroid.rxbus.RxConstants;
import com.xlb.elect.cigar.R;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/05
 */

public class InputPsdActivity extends BaseActivity {
    private Button mNextBtn;
    private String phone;
    private String smsCode;
    private EditText mInput1,mInput2;
    public static void startActivity(Context context,String phone,String smsCode){
        Intent intent = new Intent(context,InputPsdActivity.class);
        intent.putExtra(Constants.PHONE_NUM,phone);
        intent.putExtra(Constants.SMS_CODE,smsCode);
        context.startActivity(intent);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.regist_set_psd);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.PHONE_NUM)){
            phone = intent.getStringExtra(Constants.PHONE_NUM);
            smsCode = intent.getStringExtra(Constants.SMS_CODE);
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
        mInput1 = (EditText) findViewById(R.id.input_first);
        mInput2 = (EditText) findViewById(R.id.input_second);
    }

    @Override
    public void initData() {

    }


    @Override
    public void onClick(View v) {
        if (ICommonUtil.isFastDoubleClick()) return;
        switch (v.getId()){
            case R.id.left_img_btn:
                finish();
                break;
            case R.id.login_btn:
                String psd1 = mInput1.getText().toString();
                String psd2 = mInput2.getText().toString();
                if (psd1.length() == 0 || psd2.length() == 0){
                    showToast("输入信息不完整");
                    return;
                }
                if (!psd1.equals(psd2)){
                    showToast("两次输入不一致");
                    return;
                }
                mPresenter.registUser(phone,psd1,smsCode);
//                Intent intent = new Intent(context, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(intent);
                break;
        }
    }

    private LoginRegistPresenter mPresenter = new LoginRegistPresenter(new LoginRegistControl() { //使用共用的ViewControl，减少回调个数
        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void onRegistResult(boolean isSuccess, String message, User result) {
            dismissProgressDialog();
            if (isSuccess) {
                if (result == null || result.member_id == 0) {
                    return;
                }
                LBController.getInstance().loginSuccess(result);
                RxBus.getDefault().postWithCode(RxConstants.ACTION_LOGIN_SUCCESS);
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                finish();
            } else {
                if (message != null) { //账户被禁用或重复注册之类的错误提示语
                    showToast(message);
                }
            }
        }
    });
}
