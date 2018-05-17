package com.xiaolanba.passenger.module.person.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.framework.common.utils.ICommonUtil;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.common.utils.MatcherUtil;
import com.xiaolanba.passenger.common.utils.ResUtil;
import com.xlb.elect.cigar.R;

/**
 * 注册，找回密码，绑定手机号的，输入手机号
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/05
 */

public class InputPhoneActivity extends BaseActivity {
    private Button mNextBtn;
    private EditText mPhoneEdit;
    public static void startActivity(Context context){
        Intent intent = new Intent(context,InputPhoneActivity.class);
        context.startActivity(intent);
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.regist_input);
    }

    @Override
    public void findView() {
        findViewById(R.id.common_title_root).setBackgroundResource(R.drawable.bg_title);
        ImageButton leftBtn = (ImageButton) findViewById(R.id.left_img_btn);
        leftBtn.setImageResource(R.drawable.return_back);
        leftBtn.setOnClickListener(this);
        mNextBtn = (Button) findViewById(R.id.login_btn);
        mNextBtn.setOnClickListener(this);
        mPhoneEdit = (EditText) findViewById(R.id.phone_edit);
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
                String inputWords = mPhoneEdit.getText().toString().trim();
                if (!MatcherUtil.isPhoneNumberValid(inputWords)){
                    showToast(ResUtil.getString(R.string.phone_invalid));
                    return;
                }
                InputIdentifyActivity.startActivity(context,inputWords);
                break;
        }
    }
}
