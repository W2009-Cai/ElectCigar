package com.framework.common.view;

import android.content.Context;
import android.widget.TextView;

import com.framework.common.base.IBaseDialog;
import com.xlb.elect.cigar.R;

public class IProgressDialog extends IBaseDialog {

    private TextView mContentTxt;

    public IProgressDialog(Context context) {
        super(context, R.style.TransparentDialog);//设置窗体样式
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.rotating_progress_dialog_layout);
    }

    @Override
    public void findView() {
        mContentTxt = (TextView) findViewById(R.id.content_txt);
    }

    public void show(String content) {
        if (isFinishing()) {
            return;
        }
        mContentTxt.setText(content);
        if (!isShowing()) {
            show();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void initData() {

    }

}
