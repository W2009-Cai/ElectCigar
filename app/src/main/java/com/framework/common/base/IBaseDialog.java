package com.framework.common.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.framework.common.utils.IDisplayUtil;
import com.framework.manager.SystemBarTintManager;
import com.xlb.elect.cigar.R;

/**
 * Dialog 基类
 *
 * @author xutingz
 */
public abstract class IBaseDialog extends Dialog {

    protected Context mContext;

    /**
     * 是否更改状态栏背景颜色
     */
    public boolean changeSystemBarColor = false;

    /**
     * @return 是否需要矫正，setContentView,findView,initData的位置，应该放在onCreate里面，为了不影响以前的，通过函数控制
     * @author qbw
     */
    protected boolean adjust() {
        return false;
    }

    protected IBaseDialog(Context context, int theme) {
        super(context, theme);

        this.mContext = context;
        changeSystemBarColor();
        if (!adjust()) {
            setContentView();
            findView();
            initData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (adjust()) {
            setContentView();
            findView();
            initData();
        }
    }

    /**
     * 如果对话框需要宽度完全展示，请调这个方法
     */
    public void setFullWidth() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = IDisplayUtil.getScreenWidth(mContext);
        window.setAttributes(lp);
    }

    public void showToast(String title) {
        if (mContext != null) {
            if (mContext instanceof IBaseActivity) {
                ((IBaseActivity) mContext).showToast(title);
            } else if (mContext instanceof IBaseFragmentActivity) {
                ((IBaseFragmentActivity) mContext).showToast(title);
            }
        }
    }

    public void showToast(String title, int duration) {
        if (mContext != null) {
            if (mContext instanceof IBaseActivity) {
                ((IBaseActivity) mContext).showToast(title, duration);
            } else if (mContext instanceof IBaseFragmentActivity) {
                ((IBaseFragmentActivity) mContext).showToast(title, duration);
            }
        }
    }

    public boolean isFinishing() {
        if (mContext != null) {
            if (mContext instanceof IBaseActivity) {
                return ((IBaseActivity) mContext).isFinishing();
            } else if (mContext instanceof IBaseFragmentActivity) {
                return ((IBaseFragmentActivity) mContext).isFinishing();
            }
        }
        return false;
    }

    public void showToast(int titleResId) {
        if (mContext != null) {
            showToast(mContext.getResources().getString(titleResId));
        }
    }

    public abstract void setContentView();

    public abstract void findView();

    public abstract void initData();

    /**
     * 更改状态栏背景颜色
     */
    public void changeSystemBarColor() {
        if (!changeSystemBarColor) {
            return;
        }

        try {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // 创建状态栏的管理实例
            SystemBarTintManager tintManager = new SystemBarTintManager((Activity) mContext);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            // 激活导航栏设置
            tintManager.setNavigationBarTintEnabled(false);

            tintManager.setStatusBarTintColor(mContext.getResources().getColor(R.color.title_background));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
