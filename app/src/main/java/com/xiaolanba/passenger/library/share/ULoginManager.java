package com.xiaolanba.passenger.library.share;

import android.app.Activity;
import android.content.Intent;

import com.xiaolanba.passenger.library.share.qq.QQManager;
import com.xiaolanba.passenger.library.share.sina.SinaManager;
import com.xiaolanba.passenger.library.share.weixin.WXManager;


/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class ULoginManager {

    private WXManager wxManager;
    private QQManager qqManager;
    private SinaManager sinaManager;

    private static ULoginManager instance;

    public static ULoginManager getInstance() {
        synchronized (ULoginManager.class) {
            if (null == instance) {
                instance = new ULoginManager();
            }
        }
        return instance;
    }

    public ULoginManager initLogin(Activity activity, BaseUManager.UMLoginListener loginListener) {
        wxManager = WXManager.getInstance().init(activity);
        qqManager = QQManager.getInstance().init(activity);
        sinaManager = SinaManager.getInstance().init(activity);
        wxManager.setLoginListener(loginListener);
        qqManager.setLoginListener(loginListener);
        sinaManager.setLoginListener(loginListener);
        return instance;
    }

    /**
     * 微信登录
     */
    public void loginWeiXin() {
        if (wxManager != null) {
            wxManager.loginWeiXin();
        }
    }

    /**
     * QQ登录
     */
    public void loginQQ() {
        if (qqManager != null) {
            try {
                qqManager.loginQQ();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 新浪登录
     */
    public void loginSina() {
        if (sinaManager != null) {
            sinaManager.loginSina();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (qqManager != null) {
            qqManager.onActivityResult(requestCode, resultCode, data);
        }

        if (sinaManager != null) {
            sinaManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
