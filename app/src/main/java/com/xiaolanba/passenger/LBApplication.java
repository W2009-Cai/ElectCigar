package com.xiaolanba.passenger;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.framework.common.base.IApplication;
import com.framework.common.utils.ICommonUtil;
import com.framework.common.utils.ILog;
import com.xiaolanba.commonlib.fresco.FrescoUtil;
import com.xiaolanba.commonlib.utils.CLog;
import com.xiaolanba.passenger.common.utils.HttpParams;
import com.xiaolanba.passenger.logic.control.LBController;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBus;
import com.xiaolanba.passenger.rxandroid.rxbus.RxConstants;

/**
 * 小蓝巴Application
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class LBApplication extends IApplication {

    protected static LBApplication instance;

    public HttpParams httpParams;
    private boolean isFirstForeground = true;
    public int mPageCount = 0;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try {
            // 初始化http头携带的系统参数
            httpParams = new HttpParams().init(getApplicationContext());
            // 初始化其他联网平台
            initThirdPlatform();
            registerActivityLifecycleCallbacks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第三方的初始化
     */
    public void initThirdPlatform() {
        Context context = getApplicationContext();
        // 图片加载
        FrescoUtil.init(context);
//        PushManager.initJPushInterface(this,ILog.DEBUG);    //必须在主线程
//        PushManager.setJpushAlias(context,"xutingz");  //测试别名
//        PushManager.setJpushTag(context,"tag_xutingz");
//        StatisticManager.init(instance,getChannelValue()); //必须在主线程
//        StatisticManager.setLogEnable(ILog.DEBUG);
        CLog.setIsDebug(ILog.DEBUG);
    }

    public static LBApplication getInstance() {
        return instance;
    }

    /**
     * 获取渠道号
     *
     * @return channel
     */
    public String getChannelValue() {
        String channel = "";
        try {
            if (instance != null) {
                ApplicationInfo appInfo = instance.getPackageManager()
                        .getApplicationInfo(instance.getPackageName(),
                                PackageManager.GET_META_DATA);
                channel = appInfo.metaData.getString("UMS_CHANNEL");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    /**
     * token 失效
     */
    public void tokenOffline(final String message) {
        // 防止多次网络请求之后重复弹出
        if (ICommonUtil.isFastDoubleClick(2999999)) return;
        LBController.MainHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Context context = getApplicationContext();
                LBController.getInstance().logout();
                LBController.getInstance().clearPageNoMainActivity();
//                InputPhoneActivity.startActivity(context, false, ResUtil.getString(R.string.token_inviled_please_login));
                RxBus.getDefault().postWithCode(RxConstants.ACTION_TOKEN_INVILED);
            }
        }, 200);
    }

    public void registerActivityLifecycleCallbacks() {
        mPageCount = 0;
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mPageCount++;
                // 从后台到前台
                if (mPageCount == 1) {
                    ILog.wr("application", "app is  foreground >>>");
                    if (isFirstForeground) {
                        isFirstForeground = false;
                        return;
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mPageCount--;
                // 前台到后台
                if (mPageCount == 0) {
                    ILog.wr("application", "app is backgroud >>>");
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

}

