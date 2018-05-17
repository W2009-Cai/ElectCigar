package com.xiaolanba.passenger.library.share.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.framework.common.utils.IJsonUtil;
import com.framework.common.utils.ILog;
import com.framework.common.utils.IStringUtil;
import com.framework.http.HttpRequest;
import com.framework.http.HttpRequestCallback;
import com.framework.http.HttpResponse;
import com.framework.http.ProtocolManager;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xiaolanba.passenger.common.bean.OtherLogin;
import com.xiaolanba.passenger.library.share.BaseUManager;
import com.xiaolanba.passenger.library.share.ShareInfo;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class QQManager extends BaseUManager {

    protected static final String TAG = QQManager.class.getSimpleName();

    private static QQManager instance;
    private Tencent mTencent;
    private BaseLoginListener baseLoginListener;
    private BaseShareListener baseQQShareListener;
    private BaseShareListener baseQzoneShareListener;

    public static QQManager getInstance() {
        synchronized (QQManager.class) {
            if (null == instance) {
                instance = new QQManager();
            }
        }
        return instance;
    }

    /**
     * 初始化QQ
     *
     * @param context
     * @return
     */
    public QQManager init(Activity context) {
        this.activity = context;
        try {
            if (mTencent == null) {
                mTencent = Tencent.createInstance(QQAPPID, context.getApplicationContext());
            }
        } catch (Exception e) {

        }
        return instance;
    }

    /**
     * 判断qq是否安装
     *
     * @return
     */
    public boolean isQQClientInstalled() {
        if (mTencent == null || activity == null) {
            return false;
        }
        if (Util.isAppInstalled("com.tencent.mobileqq", activity.getApplicationContext())) {
            return true;

        } else if (Util.isAppInstalled("com.tencent.tim", activity.getApplicationContext())) {
            return true;
        } else {
            showToast("您没有安装QQ，请先下载安装QQ~");
            return false;
        }
    }

    /**
     * 分享QQ
     *
     * @param shareInfo
     */
    public void shareQQ(final ShareInfo shareInfo) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                share(shareInfo, false);
            }
        });
    }

    /**
     * 分享QQ空间
     *
     * @param shareInfo
     */
    public void shareQZone(final ShareInfo shareInfo) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                share(shareInfo, true);
            }
        });
    }

    /**
     * 分享，QQ分享或QQ空间分享
     *
     * @param shareInfo
     * @param isQZone
     */
    private void share(ShareInfo shareInfo, boolean isQZone) {
        if (mTencent == null || activity == null) {
            return;
        }
        if (isQQClientInstalled()) {
            String title = shareInfo.getTitle();
            String content = shareInfo.getContent();
            String contentUrl = shareInfo.getContentUrl();
            Object imgUrl = shareInfo.imgUrl;
            int shareType = shareInfo.shareType;
            long bussinessId = shareInfo.bussinessId;

            final Bundle params = new Bundle();
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, contentUrl);

            String sendPath = null;
            boolean isVideo = false;
            if (shareInfo.isImageType()) {
                // TODO 现目前项目中 纯图分享的只有朋友圈和微信好友
            } else {
                String url = null;
                File file = null;
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                if (imgUrl != null && imgUrl instanceof String) {
                    url = imgUrl.toString();
                }
                file = getThumbFile(activity, imgUrl);
                if (file != null && file.exists()) {
                    sendPath = file.getAbsolutePath();
                }

                if (IStringUtil.isEmpty(sendPath)) {
                    sendPath = url;
                }
            }

            if (isQZone) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(sendPath);
                params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list);
                mTencent.shareToQzone(activity, params, baseQzoneShareListener = new BaseShareListener(Platform.QZONE, shareType, bussinessId, isVideo));
            } else {
                params.putString(shareInfo.isImageType() ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL : QQShare.SHARE_TO_QQ_IMAGE_URL, sendPath);
                mTencent.shareToQQ(activity, params, baseQQShareListener = new BaseShareListener(Platform.QQ, shareType, bussinessId, isVideo));
            }
        }
    }

    /**
     * 登录
     */
    public void loginQQ() {
        if (isQQClientInstalled()) {
            if (null != loginListener) {
                loginListener.onUMStart();
            }
            if (baseLoginListener == null) {
                baseLoginListener = new BaseLoginListener();
            }
            mTencent.login(activity, "all", baseLoginListener);
        }
    }

    private class BaseLoginListener implements IUiListener {

        @Override
        public void onCancel() {
            ILog.d(TAG, "qq login onCancel");
            onLoginFailure(true);
        }

        @Override
        public void onComplete(Object response) {
            ILog.d(TAG, "qq login onComplete");
            if (response == null) {
                onLoginFailure(false);
                return;
            }
            JSONObject json = (JSONObject) response;
            ILog.d(TAG, "qq login onComplete response json:" + json.toString());
            int ret = IJsonUtil.getInt("ret", json);
            if (ret == 0) {
                ILog.d(TAG, "qq login success");
                final String openID = IJsonUtil.getString("openid", json);
                String accessToken = IJsonUtil.getString("access_token", json);
                String expires = IJsonUtil.getString("expires_in", json);
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                doOauthVerify(accessToken);
            } else {
                onLoginFailure(false);
            }
        }

        @Override
        public void onError(UiError arg0) {
            ILog.d(TAG, "qq login onError" + arg0);
            onLoginFailure(false);
        }
    }

    private void getUserInfo(final String unionid) {
        QQToken qqToken = mTencent.getQQToken();
        UserInfo info = new UserInfo(activity, qqToken);
        info.getUserInfo(new IUiListener() {

            @Override
            public void onError(UiError arg0) {
                ILog.d(TAG, "qq login getUserInfo onError");
                onLoginFailure(false);
            }

            @Override
            public void onComplete(Object info) {
                ILog.d(TAG, "qq login getUserInfo onComplete");
                if (info == null) {
                    onLoginFailure(false);
                    return;
                }
                JSONObject json = (JSONObject) info;
                ILog.d(TAG, "qq login getUserInfo json:" + json.toString());
                int ret = IJsonUtil.getInt("ret", json);
                if (ret == 0) {
                    ILog.d(TAG, "qq login getUserInfo success");
                    OtherLogin otherLogin = new OtherLogin();
                    otherLogin.openId = mTencent.getOpenId();
                    otherLogin.parser(json);
                    otherLogin.unionid = unionid;
                    onLoginSuccess(otherLogin);
                } else {
                    onLoginFailure(false);
                }
            }

            @Override
            public void onCancel() {
                ILog.d(TAG, "qq login getUserInfo onCancel");
                onLoginFailure(true);
            }
        });
    }

    private class BaseShareListener implements IUiListener {
        public Platform platform = Platform.QQ;
        public int shareType;
        public long bussinessId;
        public boolean isVideo;

        public BaseShareListener(Platform platform, int shareType, long bussinessId, boolean isVideo) {
            this.platform = platform;
            this.shareType = shareType;
            this.bussinessId = bussinessId;
            this.isVideo = isVideo;
        }

        @Override
        public void onComplete(Object o) {
            ILog.d(TAG, "qq share onComplete");
            onShareSuccess(platform, shareType, bussinessId, isVideo);
        }

        @Override
        public void onError(UiError uiError) {
            ILog.d(TAG, "qq share onError");
            onShareFailure(false);
        }

        @Override
        public void onCancel() {
            ILog.d(TAG, "qq share onCancel");
            onShareFailure(true);
        }
    }

    private void doOauthVerify(String access_token) {
        HttpRequest request = new HttpRequest();
        request.setUrl("https://graph.qq.com/oauth2.0/me");
        request.setRequestMethod(HttpRequest.GET);
        request.addRequestParam("access_token", access_token);
        request.addRequestParam("unionid", "1");
        request.setHttpRequestListener(new HttpRequestCallback() {

            @Override
            public void requestBefore() {

            }

            @Override
            public void requestAfter(final HttpResponse response) {
                String result = response.getResult();
                String unionid = "";
                if (!IStringUtil.isEmpty(result)) {
                    result = result.replaceFirst("callback\\( ", "").replace(" );\n", "");
                    JSONObject json = IJsonUtil.newJSONObject(result);
                    if (json != null && json.length() > 0 && json.has("unionid")) {
//						String client_id = IJsonUtil.getString("client_id", json);
//						String openid = IJsonUtil.getString("openid", json);
                        unionid = IJsonUtil.getString("unionid", json);
                    }
                }
                getUserInfo(unionid);
            }
        });
        ProtocolManager.getInstance().submitRequest(request);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == Constants.REQUEST_QZONE_SHARE) {
                Tencent.onActivityResultData(requestCode, resultCode, data, baseQzoneShareListener);
            } else if (requestCode == Constants.REQUEST_QQ_SHARE) {
                Tencent.onActivityResultData(requestCode, resultCode, data, baseQQShareListener);
            } else if (requestCode == Constants.REQUEST_LOGIN) {
                Tencent.onActivityResultData(requestCode, resultCode, data, baseLoginListener);
            }

            if (requestCode == Constants.REQUEST_API) {
                if (resultCode == Constants.REQUEST_LOGIN) {
                    Tencent.handleResultData(data, baseLoginListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
