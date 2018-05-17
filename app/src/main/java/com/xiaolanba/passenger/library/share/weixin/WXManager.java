package com.xiaolanba.passenger.library.share.weixin;

import android.app.Activity;
import android.content.Context;

import com.framework.common.utils.IJsonUtil;
import com.framework.common.utils.ILog;
import com.framework.common.utils.IStringUtil;
import com.framework.http.HttpRequest;
import com.framework.http.HttpRequestCallback;
import com.framework.http.HttpResponse;
import com.framework.http.ProtocolManager;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xiaolanba.passenger.common.bean.OtherLogin;
import com.xiaolanba.passenger.library.share.BaseUManager;
import com.xiaolanba.passenger.library.share.ShareInfo;

import org.json.JSONObject;

/**
 * 第三方分享管理
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class WXManager extends BaseUManager {

    protected static final String TAG = WXManager.class.getSimpleName();

    // 通过code获取access_token
    public static final String URL_WX_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
    // 刷新access_token有效期
    public static final String URL_WX_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    // 获取微信用户信息
    public static final String URL_WX_USERINFO = "https://api.weixin.qq.com/sns/userinfo";

    private static WXManager instance;

    protected IWXAPI wxApi;
    private boolean isShare = false;
    private Platform platform;
    private int shareType;
    private long bussinessId;
    private boolean isVideo;

    public static WXManager getInstance() {
        synchronized (WXManager.class) {
            if (null == instance) {
                instance = new WXManager();
            }
        }
        return instance;
    }

    /**
     * 初始化微信
     *
     * @param context
     * @return
     */
    public WXManager init(Activity context) {
        this.activity = context;
        regToWX(context);

        return instance;
    }

    /**
     * 判断微信是否安装
     *
     * @return
     */
    public boolean isWXClientInstalled() {
        if (wxApi == null) {
            return false;
        }
        if (wxApi.isWXAppInstalled()) {
            return true;
        } else {
            showToast("您没有安装微信，请先下载安装微信~");
            return false;
        }
    }

    /**
     * 注册微信api
     *
     * @param context
     * @return
     */
    public IWXAPI regToWX(Context context) {
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(context, WXAPPID, true);
            wxApi.registerApp(WXAPPID);
        }
        return wxApi;
    }

    /**
     * 微信授权
     *
     * @param resp
     */
    public void onResp(BaseResp resp) {
        if (resp == null) {
            return;
        }
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK: // (用户同意)
                ILog.i(TAG, "用户同意");
                if (isShare) {
                    onShareSuccess(platform, shareType, bussinessId, isVideo);
                } else {
                    SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                    doOauthVerify(sendResp.code);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL: //（用户取消）
                ILog.i(TAG, "用户取消");
                if (isShare) {
                    onShareFailure(true);
                } else {
                    onLoginFailure(true);
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://（用户拒绝授权）
                ILog.i(TAG, "用户拒绝授权");
                if (isShare) {
                    onShareFailure(false);
                } else {
                    onLoginFailure(false);
                }
                break;
            default:
                if (isShare) {
                    onShareFailure(false);
                } else {
                    onLoginFailure(false);
                }
                break;
        }
    }

    /**
     * 分享微信
     *
     * @param shareInfo
     */
    public void shareWeiXin(final ShareInfo shareInfo) {
        ILog.d(TAG, "share weixin");
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                platform = Platform.WEIXIN;
                share(shareInfo, false);
            }
        });
    }


    /**
     * 分享微信朋友圈
     *
     * @param shareInfo
     */
    public void shareWeiXinCircle(final ShareInfo shareInfo) {
        ILog.d(TAG, "share weixin circle");
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                platform = Platform.WEIXIN_CIRCLE;
                share(shareInfo, true);
            }
        });
    }

    private void share(ShareInfo shareInfo, boolean isCircle) {
        if (isWXClientInstalled()) {
            isShare = true;
            this.shareType = shareInfo.shareType;
            this.bussinessId = shareInfo.bussinessId;

            String title = shareInfo.getTitle();
            String content = shareInfo.getContent();
            String contentUrl = shareInfo.getContentUrl();
            Object imgUrl = shareInfo.imgUrl;

            isVideo = false;
            WXMediaMessage msg = new WXMediaMessage();
            if (shareInfo.isImageType()) {
            } else {
                msg.title = title;
                msg.description = content;
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = contentUrl;
                msg.thumbData = getThumbData(activity, imgUrl);
                msg.mediaObject = webpage;
            }

            //构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis()); // 唯一标识一个请求
            req.message = msg;
            req.scene = isCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
            wxApi.sendReq(req);
        }
    }

    /**
     * 登录微信
     */
    public void loginWeiXin() {
        if (isWXClientInstalled()) {
            isShare = false;
            onLoginStart();
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "com.attention.app";
            wxApi.sendReq(req);
        }
    }

    /**
     * 微信用户授权
     *
     * @param code
     */
    private void doOauthVerify(String code) {
        HttpRequest request = new HttpRequest();
        request.setUrl(URL_WX_ACCESS_TOKEN);
        request.setRequestMethod(HttpRequest.GET);
        request.addRequestParam("appid", WXAPPID);
        request.addRequestParam("secret", WXAPPSECRET);
        request.addRequestParam("code", code);
        request.addRequestParam("grant_type", "authorization_code");
        request.setHttpRequestListener(new HttpRequestCallback() {

            @Override
            public void requestBefore() {

            }

            @Override
            public void requestAfter(final HttpResponse response) {
                String result = response.getResult();
                if (!IStringUtil.isEmpty(result)) {
                    JSONObject json = IJsonUtil.newJSONObject(result);
                    if (json != null && json.length() > 0 && json.has("access_token")) {
                        String accessToken = IJsonUtil.getString("access_token", json);
                        String openid = IJsonUtil.getString("openid", json);
                        wxUserInfo(accessToken, openid);
                        return;
                    }
                }
                onLoginFailure(false);
            }
        });
        ProtocolManager.getInstance().submitRequest(request);
    }

    /**
     * 获取微信用户信息
     *
     * @param accessToken
     * @param openid
     */
    private void wxUserInfo(String accessToken, String openid) {
        HttpRequest request = new HttpRequest();
        request.setUrl(URL_WX_USERINFO);
        request.setRequestMethod(HttpRequest.GET);
        request.addRequestParam("access_token", accessToken);
        request.addRequestParam("openid", openid);
        request.setHttpRequestListener(new HttpRequestCallback() {

            @Override
            public void requestBefore() {

            }

            @Override
            public void requestAfter(final HttpResponse response) {
                String result = response.getResult();
                if (!IStringUtil.isEmpty(result)) {
                    JSONObject json = IJsonUtil.newJSONObject(result);
                    if (json != null && json.length() > 0 && json.has("openid")) {
                        OtherLogin otherLogin = new OtherLogin();
                        otherLogin.parser(json);
                        onLoginSuccess(otherLogin);
                        return;
                    }
                }
                onLoginFailure(false);

            }
        });
        ProtocolManager.getInstance().submitRequest(request);
    }

}
