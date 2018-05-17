package com.framework.common.base;

import android.support.annotation.StringRes;

/**
 * @author xutingz
 * @createtime 2016/09/25 10:27
 */

public interface IActivity {
    void showToast(String message);
    void showToast(String message, int duration);
    void showToast(@StringRes int messageRes, int duration);
    void showToast(@StringRes int messageRes);
}
