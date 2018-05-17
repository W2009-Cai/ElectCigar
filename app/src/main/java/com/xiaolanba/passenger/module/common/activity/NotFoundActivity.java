package com.xiaolanba.passenger.module.common.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xlb.elect.cigar.R;
import com.xiaolanba.passenger.common.base.BaseActivity;


/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class NotFoundActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTitleTxt, mNoDataTxt;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_not_found);
    }

    @Override
    public void findView() {
        findViewById(R.id.left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.left_btn).setOnClickListener(this);
        mNoDataTxt = (TextView) findViewById(R.id.no_data_txt);
        mTitleTxt = (TextView) findViewById(R.id.title_txt);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("title")) {
                String title = intent.getStringExtra("title");
                if (!TextUtils.isEmpty(title)) {
                    mTitleTxt.setText(title);
                }
            }
            if (intent.hasExtra("nodataTxt")) {
                String nodataTxt = intent.getStringExtra("nodataTxt");
                if (!TextUtils.isEmpty(nodataTxt)) {
                    mNoDataTxt.setText(nodataTxt);
                }
            }
        }
    }

    @Override
    public void initData() {

    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, NotFoundActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, NotFoundActivity.class);
        intent.putExtra("title", title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivityNodata(Context context, String text) {
        Intent intent = new Intent(context, NotFoundActivity.class);
        intent.putExtra("nodataTxt", text);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回图标
            case R.id.left_btn:
                finish();
                break;
        }
    }
}
