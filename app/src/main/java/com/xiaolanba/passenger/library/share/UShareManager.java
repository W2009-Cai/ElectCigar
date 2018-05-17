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
public class UShareManager {

    private WXManager wxManager;
    private QQManager qqManager;
    private SinaManager sinaManager;

    private static UShareManager instance;

    public static UShareManager getInstance() {
        synchronized (UShareManager.class) {
            if (null == instance) {
                instance = new UShareManager();
            }
        }
        return instance;
    }

    public UShareManager initShare(Activity activity, BaseUManager.UMShareListener shareListener) {
        wxManager = WXManager.getInstance().init(activity);
        qqManager = QQManager.getInstance().init(activity);
        sinaManager = SinaManager.getInstance().init(activity);
        wxManager.setShareListener(shareListener);
        qqManager.setShareListener(shareListener);
        sinaManager.setShareListener(shareListener);
        return instance;
    }

    /**
     * 分享微信
     *
     * @param shareInfo
     */
    public void shareWeiXin(ShareInfo shareInfo) {
        if (wxManager != null) {
            wxManager.shareWeiXin(shareInfo);
        }
    }


    /**
     * 分享微信朋友圈
     *
     * @param shareInfo
     */
    public void shareWeiXinCircle(ShareInfo shareInfo) {
        if (wxManager != null) {
            wxManager.shareWeiXinCircle(shareInfo);
        }
    }

    /**
     * 分享QQ
     *
     * @param shareInfo
     */
    public void shareQQ(ShareInfo shareInfo) {
        if (qqManager != null) {
            qqManager.shareQQ(shareInfo);
        }
    }

    /**
     * 分享QQ
     *
     * @param shareInfo
     */
    public void shareQZone(ShareInfo shareInfo) {
        if (qqManager != null) {
            qqManager.shareQZone(shareInfo);
        }
    }

    /**
     * 分享sina
     *
     * @param shareInfo
     */
    public void shareSina(ShareInfo shareInfo) {
        if (sinaManager != null) {
            sinaManager.shareSina(shareInfo);
        }
    }

    /**
     * 分享上报
     *
     * @param platform
     * @param shareType
     */
    public void shareReport(BaseUManager.Platform platform, int shareType, long bussinessId, boolean isVideo) {
        if (platform != null && shareType > 0) { // 互动相关不走统一上报入口
        }
    }

    public static int getPlatformType(BaseUManager.Platform platform) {
        int type = 0;
        if (platform == null) {
            return type;
        }
        switch (platform) {
            case WEIXIN:
                type = 1;
                break;
            case WEIXIN_CIRCLE:
                type = 2;
                break;
            case QQ:
                type = 3;
                break;
            case QZONE:
                type = 4;
                break;
            case SINA:
                type = 5;
                break;
            default:
                break;
        }
        return type;
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
