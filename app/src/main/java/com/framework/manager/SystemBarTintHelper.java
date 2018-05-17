package com.framework.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 导航条颜色设置还有问题，不要使用此功能
 * @author xutingz
 * @readme 兼容4.4和4.4以上版本的状态栏颜色设置
 */
public class SystemBarTintHelper {

    public void onCreate(Activity act, boolean statusBar, int statusRes) {
        onCreate(act, statusBar, statusRes, false, 0);
    }

    public void onCreate(Activity act, boolean statusBar, int statusRes,
                         boolean navBar, int navRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            onCreateLollipop(act, statusBar, statusRes, navBar, navRes);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            onCreateKitkat(act, statusBar, statusRes, navBar, navRes);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onCreateLollipop(Activity act, boolean statusBar,
                                  int statusRes, boolean navBar, int navRes) {
        Window window = act.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (statusBar || navBar) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (statusBar) {
            window.setStatusBarColor(act.getResources().getColor(statusRes));
        }

        if (navBar) {
            window.setNavigationBarColor(act.getResources().getColor(navRes));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void onCreateKitkat(Activity act, boolean statusBar, int statusRes,
                                boolean navBar, int navRes) {
        Window window = act.getWindow();
        int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (statusBar) {
            window.addFlags(flags);
        } else {
            window.clearFlags(flags);
        }
        flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (navBar) {
            window.addFlags(flags);
        } else {
            window.clearFlags(flags);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(act);
        tintManager.setStatusBarTintEnabled(statusBar);
        tintManager.setStatusBarTintResource(statusRes);
        tintManager.setNavigationBarTintEnabled(navBar);
        tintManager.setNavigationBarTintResource(navRes);
    }

    // @TargetApi(19)
    // private void setTranslucentStatus(Activity act, boolean statusBar,
    // boolean navBar) {
    // Window window = act.getWindow();
    // int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
    // if (statusBar) {
    // window.addFlags(flags);
    // } else {
    // window.clearFlags(flags);
    // }
    //
    // flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
    // if (navBar) {
    // window.addFlags(flags);
    // } else {
    // window.clearFlags(flags);
    // }
    // }

    // @TargetApi(19)
    // private void setTranslucentStatus(boolean on) {
    // Window win = getWindow();
    // WindowManager.LayoutParams winParams = win.getAttributes();
    // final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
    // if (on) {
    // winParams.flags |= bits;
    // } else {
    // winParams.flags &= ~bits;
    // }
    // win.setAttributes(winParams);
    // }
}
