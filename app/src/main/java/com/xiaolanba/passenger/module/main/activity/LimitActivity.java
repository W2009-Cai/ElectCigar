package com.xiaolanba.passenger.module.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.framework.common.utils.IKeyboardUtils;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.logic.manager.SharedPreferencesManager;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBus;
import com.xiaolanba.passenger.rxandroid.rxbus.RxConstants;
import com.xlb.elect.cigar.R;

/**
 * Created by aaa on 2018/5/13.
 */

public class LimitActivity extends BaseActivity {
    private Button mLimitBtn,mSureBtn;
    private EditText mInput;
    private View mBottom;
    private TextView mLimitNum;

    public static void startActivity(Context context){
        Intent intent = new Intent(context,LimitActivity.class);
        context.startActivity(intent);
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_limit_input);
    }

    @Override
    public void findView() {
        findViewById(R.id.left_img_btn).setOnClickListener(this);
        mLimitBtn = (Button) findViewById(R.id.limit_btn);
        mSureBtn = (Button) findViewById(R.id.sure_btn);
        mInput = (EditText) findViewById(R.id.input);
        mLimitBtn.setOnClickListener(this);
        mSureBtn.setOnClickListener(this);
        mBottom = findViewById(R.id.bottom_layout);
        mLimitNum = (TextView) findViewById(R.id.limit_num);
    }

    @Override
    public void initData() {
        int limit = new SharedPreferencesManager().read(SharedPreferencesManager.KEY_LIMIT_NUM,0);
        mLimitNum.setText(String.valueOf(limit));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_img_btn:
                finish();
                break;
            case R.id.limit_btn:
                if (mBottom.getVisibility() != View.VISIBLE){
                    mBottom.setVisibility(View.VISIBLE);
                    IKeyboardUtils.openKeybord(context,mInput);
                }
                break;
            case R.id.sure_btn:
                String inputStr = mInput.getText().toString();
                if (inputStr.length() == 0){
                    showToast("请输入正确的数值");
                    return;
                }
                int num = Integer.parseInt(inputStr);
                if (num ==0 || num >= 1000){
                    showToast("数值在0-1000之间");
                    return;
                }
                mLimitNum.setText(inputStr);
                RxBus.getDefault().postWithCode(RxConstants.ACTION_LIMIT,num);
                finish();
                break;
        }
    }
}
