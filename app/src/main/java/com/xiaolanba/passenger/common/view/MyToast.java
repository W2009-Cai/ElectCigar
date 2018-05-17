package com.xiaolanba.passenger.common.view;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.common.utils.IDisplayUtil;
import com.xiaolanba.passenger.LBApplication;
import com.xlb.elect.cigar.R;
import com.xiaolanba.passenger.logic.control.LBController;

/**
 * Toast 跳过魅族等机器toast限制
 *
 * @author xutingz
 */
public class MyToast {

    public static final int LENGTH_SHORT = 1000;
    public static final int LENGTH_LONG = 1500; //产品要求1.5秒 modify at 2017/1/12

    private Context mContext;
    private WindowManager wm;
    private int mDuration;
    private View mToastView;
    private TextView mToastTxt;
    private Toast mToastSys;
    private CharSequence mText;

    public static MyToast myToast;

    private MyToast(Context context) {
        mContext = context;
    }

    @SuppressLint("InflateParams")
    public static MyToast makeText(Context context, CharSequence text, int duration) {

        try {
            if (myToast != null) {
                myToast.cancel();
                myToast = null;
            }

            myToast = new MyToast(context);
            boolean isSysToast = true;
            // 为了解决魅族等一些手机， 有关闭系统toast的开关， 所以采用WindowManager的方式来显示toast
            String brand = android.os.Build.BRAND + "";
            String manufactuer = android.os.Build.MANUFACTURER + "";
            if (brand.toLowerCase().contains("meizu") || manufactuer.toLowerCase().contains("meizu")) {
                isSysToast = false;
                if (context instanceof Application) {
                    isSysToast = true;
                }
            }
            LayoutInflater inflate = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mToastView = inflate.inflate(R.layout.common_toast, null);
            TextView mToastTxt = (TextView) mToastView.findViewById(R.id.toast_txt);

            myToast.mToastView = mToastView;
            myToast.mToastTxt = mToastTxt;
            myToast.mToastTxt.setText(text);

            myToast.mToastSys = isSysToast ? Toast.makeText(LBApplication.getInstance(), text, duration) : null;

            myToast.mText = text;
            myToast.mDuration = duration;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myToast;
    }

    public static MyToast makeText(Context context, int resId, int duration) {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public void show() {
        try {
            if (mToastSys != null && mToastView != null) {
                //系统toast必须重新创建
                mToastSys = Toast.makeText(LBApplication.getInstance(), mText, mDuration);
                mToastSys.setView(mToastView);
                mToastSys.show();
            } else if (mToastView != null) {
                final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.format = PixelFormat.TRANSLUCENT;
                params.windowAnimations = R.style.ToastAnimation;
                params.y = IDisplayUtil.dip2px(mContext, 64);
                if (mContext instanceof Application) {
                    params.type = WindowManager.LayoutParams.TYPE_TOAST;
                } else {
                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                }

                wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                if (mToastView.getParent() != null) {
                    wm.removeView(mToastView);
                }
                wm.addView(mToastView, params);
                LBController.MainHandler.removeCallbacks(cancelRunable);
                LBController.MainHandler.postDelayed(cancelRunable, mDuration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable cancelRunable = new Runnable() {
        @Override
        public void run() {
            cancel();
        }
    };

    public void cancel() {
        try {
            if (mToastSys != null) {
                mToastSys.cancel();
                mToastSys = null;
            } else if (mToastView != null && wm != null) {
                wm.removeViewImmediate(mToastView);
                mToastView = null;
                mToastTxt = null;
                wm = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity onDestory的时候调用
     * 如果是系统Toast，应该让它继续弹不能消失;
     * 如果是wm，应该立即移除（兼容魅族手机toast方案,与Activity依赖紧密）
     */
    public void cancleByActivityDestory() {
        if (mToastView != null && wm != null) {
            try {
                wm.removeViewImmediate(mToastView);
                mToastView = null;
                mToastTxt = null;
                wm = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
