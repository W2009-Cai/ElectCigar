package com.xiaolanba.passenger.common.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.framework.common.base.IBaseDialog;
import com.framework.common.utils.IDisplayUtil;
import com.xlb.elect.cigar.R;

/**
 * 公共dialog
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class CommonDialog extends IBaseDialog {

    private Context mContext;
    private View dialogView;

    public CommonDialog(Context context) {
        super(context, R.style.BaseDialog);
        this.mContext = context;
        setCanceledOnTouchOutside(true);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogPopupAnimation);
    }

    public CommonDialog(Activity context, View view) {
        super(context, R.style.BaseDialog);
        this.mContext = context;
        setCanceledOnTouchOutside(true);

        setContentView(view);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogPopupAnimation);
    }

    public CommonDialog(Activity context, View view, int resId) {
        super(context, R.style.BaseDialog);
        this.mContext = context;
        setCanceledOnTouchOutside(true);

        setContentView(view);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);

        if (resId != 0) {
            window.setWindowAnimations(resId);
        }
    }

    public CommonDialog(Activity context, View view, boolean touchOut) {
        super(context, R.style.BaseDialog);
        this.mContext = context;
        setCanceledOnTouchOutside(touchOut);

        setContentView(view);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogPopupAnimation);
    }

    public CommonDialog(Activity context, View view, int style, boolean touchOut) {
        super(context, style);
        this.mContext = context;

        setCanceledOnTouchOutside(touchOut);

        setContentView(view);

    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        dialogView = view;
    }

    public void setWindowAnimations(int style) {
        Window window = getWindow();
        window.setWindowAnimations(style);
    }

    public void setGravity(int gravity) {
        Window window = getWindow();
        window.setGravity(gravity);
    }

    public void setMarginTop(int dp) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = IDisplayUtil.dip2px(mContext, dp);
        window.setAttributes(lp);
    }

    public void setMarginBottom(int dp) {
        setMarginTop(dp);
    }

    public void setMarginLeft(int dp) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = IDisplayUtil.dip2px(mContext, dp);
        window.setAttributes(lp);
    }

    public void setMarginRight(int dp) {
        setMarginLeft(dp);
    }

    public void setFullWidth() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = IDisplayUtil.getScreenWidth(mContext);
        window.setAttributes(lp);
    }

    public void setFullWidthMarginHorizontal(int dp) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = IDisplayUtil.getScreenWidth(mContext) - (IDisplayUtil.dip2px(mContext, dp) * 2);
        window.setAttributes(lp);
    }

    public void setFullHeightOut(int dp) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = IDisplayUtil.getScreenHeight(mContext) -
                IDisplayUtil.getStatusHeight(mContext) - IDisplayUtil.dip2px(mContext, dp);
        window.setAttributes(lp);
    }

    /**
     * @return the dialogView
     */
    public View getDialogView() {
        return dialogView;
    }

    @Override
    public void setContentView() {

    }

    @Override
    public void findView() {

    }

    @Override
    public void initData() {

    }
}
