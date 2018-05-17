package com.xiaolanba.passenger.module.person.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.framework.common.utils.ICommonUtil;
import com.framework.common.utils.IDisplayUtil;
import com.framework.common.utils.ILog;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.common.listener.SimpleTextWatcher;
import com.xiaolanba.passenger.common.utils.Constants;
import com.xiaolanba.passenger.common.utils.MatcherUtil;
import com.xiaolanba.passenger.common.utils.ResUtil;
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
 * @date 2018/05/02
 */

public class LoginActivity extends BaseActivity {

    private Button mLoginBtn;
    private EditText mPsdEdit, mPhoneEdit;
    private ImageView mClearImg, mEyeImg;
    private ScrollView mScrollView;
    private int dip100;
    private boolean hasScrolled;
    private boolean canSee = false;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
        settingSoftListener = true;
    }

    @Override
    public void findView() {
        dip100 = IDisplayUtil.dip2px(context, 130);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(this);
        mPsdEdit = (EditText) findViewById(R.id.psd_edit);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mPhoneEdit = (EditText) findViewById(R.id.phone_edit);
        mClearImg = (ImageView) findViewById(R.id.clear_img);
        mClearImg.setOnClickListener(this);
        mEyeImg = (ImageView) findViewById(R.id.see_eye_img);
        mEyeImg.setOnClickListener(this);
        findViewById(R.id.regist_txt).setOnClickListener(this);
        findViewById(R.id.forget_txt).setOnClickListener(this);
        mPsdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mPsdEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) { //获取焦点
                    ILog.i(TAG, "----onShowKeyboard");
                    if (isSoftShowing && !hasScrolled) {
                        mPsdEdit.post(new Runnable() {
                            @Override
                            public void run() {
                                hasScrolled = true;
                                mScrollView.scrollBy(0, dip100);
                            }
                        });
                    }
                }
            }
        });
        mPhoneEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mPhoneEdit.getText();
                int totalLen = editable.toString().length();
                mClearImg.setVisibility(totalLen > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    @Override
    public void initData() {
        if (hasLogin()){
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (ICommonUtil.isFastDoubleClick()) return;
        switch (v.getId()) {
            case R.id.login_btn:
                String inputWords = mPhoneEdit.getText().toString().trim();
                if (!MatcherUtil.isPhoneNumberValid(inputWords)){
                    showToast(ResUtil.getString(R.string.phone_invalid));
                    return;
                }
                String password = mPsdEdit.getText().toString().trim();
                if (!MatcherUtil.isPasswordValid(password)) {
                    showToast(ResUtil.getString(R.string.psd_invalid));
                    return;
                }
//                InputPhoneActivity.startActivity(context);
                mPresenter.login(inputWords,password);
//                Intent intent = new Intent(context, MainActivity.class);
//                context.startActivity(intent);
//                finish();
                break;
            case R.id.see_eye_img:
                int length = mPsdEdit.getText().length();
                if (canSee) {  // 可见切换为不可见
                    mPsdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mEyeImg.setImageResource(R.drawable.mine_eye_see);
                    canSee = false;
                } else { // 不可见切换为可见
                    mPsdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mEyeImg.setImageResource(R.drawable.mine_eye_notsee);
                    canSee = true;
                }
                mPsdEdit.setSelection(length);
                break;
            case R.id.clear_img:
                mPhoneEdit.setText("");
                break;
            case R.id.regist_txt:
                InputPhoneActivity.startActivity(context);
                break;
            case R.id.forget_txt:
                InputPhoneActivity.startActivity(context);
                break;
        }
    }

    @Override
    public void onShowKeyboard() {
        ILog.i(TAG, "----onShowKeyboard");
        boolean hasPsdFocus = mPsdEdit.hasFocus();
        if (hasPsdFocus) {
            mPsdEdit.post(new Runnable() {
                @Override
                public void run() {
                    hasScrolled = true;
                    mScrollView.scrollBy(0, dip100);
                }
            });
        }
    }

    @Override
    public void onHideKeyboard() {
        ILog.i(TAG, "----onHideKeyboard");
        hasScrolled = false;
    }

    private LoginRegistPresenter mPresenter = new LoginRegistPresenter(new LoginRegistControl() { //使用共用的ViewControl，减少回调个数
        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void onLoginResult(boolean isSuccess, String message, User result) {
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
                if (message != null) {
                    showToast(message);
                }
            }
        }
    });
}
