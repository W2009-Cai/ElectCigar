package com.xiaolanba.passenger.common.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.xiaolanba.passenger.LBApplication;

/**
 * Resource资源类
 *
 * @author xutingz
 */

public class ResUtil {

    public static String getString(@StringRes int resId) {
        return LBApplication.getInstance().getResources().getString(resId);
    }

    public static int getDimension(@DimenRes int resId) {
        return (int) LBApplication.getInstance().getResources().getDimension(resId);
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return LBApplication.getInstance().getResources().getDrawable(resId);
    }

    public static int getColor(@ColorRes int resId) {
        return LBApplication.getInstance().getResources().getColor(resId);
    }
}
