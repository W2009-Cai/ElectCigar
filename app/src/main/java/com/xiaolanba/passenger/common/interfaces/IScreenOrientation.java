package com.xiaolanba.passenger.common.interfaces;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public interface IScreenOrientation {
    void initScreen();

    void clickFullScreen();

    void onScreenLandscape();

    void onScreenPortrait();

    void lockScreenPortrait();

    void lockScreenLandscape();

    void unlockScreenOrientation();

    boolean isPortrait();

    boolean isLandscape();

    class ScreenOrientationImpl implements IScreenOrientation {

        private Activity mActivity;

        private Handler mHandler;

        /**
         * 不锁定屏幕方向
         */
        private Runnable mUnlockScreenOrientationRunn = new Runnable() {
            @Override
            public void run() {
                unlockScreenOrientation();
            }
        };

        public ScreenOrientationImpl(Activity activity) {
            mActivity = activity;
            mHandler = new Handler();
        }

        @Override
        public void initScreen() {
            View decorView = mActivity.getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    //全屏弹对话框导致状态栏显示出来
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && (visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        onScreenLandscape();
                    }
                }
            });
        }

        @Override
        public void clickFullScreen() {
            mHandler.removeCallbacks(mUnlockScreenOrientationRunn);
            int orientation = mActivity.getResources().getConfiguration().orientation;
            switch (orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    lockScreenLandscape();
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    lockScreenPortrait();
                    break;
                default:
                    break;
            }
            mHandler.postDelayed(mUnlockScreenOrientationRunn, 5000);
        }

        @Override
        public void onScreenLandscape() {
            Window window = mActivity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        @Override
        public void onScreenPortrait() {
            Window window = mActivity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                View decorView = window.getDecorView();
                int source = decorView.getSystemUiVisibility();
                source &= (~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                source &= (~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                source &= (~View.SYSTEM_UI_FLAG_FULLSCREEN);
                decorView.setSystemUiVisibility(source);
            }
            WindowManager.LayoutParams attrs = window.getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attrs);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        @Override
        public void lockScreenPortrait() {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        @Override
        public void lockScreenLandscape() {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @Override
        public void unlockScreenOrientation() {
            // 检测系统旋转方向开关是否打开
            boolean autoRotateOn = (Settings.System.getInt(mActivity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) ;
            if (autoRotateOn) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        }

        @Override
        public boolean isPortrait() {
            return Configuration.ORIENTATION_PORTRAIT == mActivity.getResources().getConfiguration().orientation;
        }

        @Override
        public boolean isLandscape() {
            return Configuration.ORIENTATION_LANDSCAPE == mActivity.getResources().getConfiguration().orientation;
        }
    }
}

