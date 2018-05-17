package com.xiaolanba.passenger.library.share.sina;

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
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.xiaolanba.passenger.common.bean.OtherLogin;
import com.xiaolanba.passenger.library.share.BaseUManager;
import com.xiaolanba.passenger.library.share.ShareInfo;
import com.xiaolanba.passenger.library.share.qq.Util;

import org.json.JSONObject;

/**
 * 微博
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class SinaManager extends BaseUManager {

    protected static final String TAG = SinaManager.class.getSimpleName();

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * <p>
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * <p>
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * <p>
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    private static SinaManager instance;

    private AuthInfo mAuthInfo;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    public SsoHandler mSsoHandler;
    private LoginAuthListener loginAuthListener;

    /**
     * 微博微博分享接口实例
     */
    public IWeiboShareAPI mWeiboShareAPI = null;

    private int shareType;
    private long bussinessId;
    private boolean isVideo;

    public static SinaManager getInstance() {
        synchronized (SinaManager.class) {
            if (null == instance) {
                instance = new SinaManager();
            }
        }
        return instance;
    }

    public void onCreate(Intent intent, Bundle savedInstanceState, IWeiboHandler.Response response) {

        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null && mWeiboShareAPI != null) {
            mWeiboShareAPI.handleWeiboResponse(intent, response);
        }
    }

    public void onNewIntent(Intent intent, IWeiboHandler.Response response) {

        if (mWeiboShareAPI != null) {
            // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
            // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
            // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
            mWeiboShareAPI.handleWeiboResponse(intent, response);
        }
    }

    /**
     * 新浪是否安装
     *
     * @return
     */
    public boolean isSinaClientInstalled(boolean showToast) {
        if (activity == null) {
            return false;
        }
        if (Util.isAppInstalled("com.sina.weibo", activity.getApplicationContext())) {
            return true;
        } else {
            if (showToast) {
                showToast("您没有安装新浪微博，请先下载安装新浪微博~");
            }
            return false;
        }
    }

    /**
     * 初始化
     *
     * @param context
     * @return
     */
    public SinaManager init(Activity context) {
        this.activity = context;
        if (mAuthInfo == null) {
            // 创建授权认证信息
            mAuthInfo = new AuthInfo(activity, SINAAPPKEY, SINA_CALLBACK_URL, SCOPE);
        }
        if (mSsoHandler == null) {
            mSsoHandler = new SsoHandler(activity, mAuthInfo);
        }

        registWeiboShareAPI();

        return instance;
    }

    private void registWeiboShareAPI() {
        if (mWeiboShareAPI == null) {
            // 创建微博分享接口实例
            mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, SINAAPPKEY);

            // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
            // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
            // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
            mWeiboShareAPI.registerApp();
        }
    }

    private void clearCache() {
        try {
            mWeiboShareAPI = null;
            mAuthInfo = null;
            mSsoHandler = null;
            init(activity);
            AccessTokenKeeper.clear(activity);
        } catch (Exception e) {

        }
    }

    /**
     * 分享sina
     *
     * @param shareInfo
     */
    public void shareSina(final ShareInfo shareInfo) {
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                share(shareInfo);
            }
        });
    }

    public void share(ShareInfo shareInfo) {
        if (mWeiboShareAPI == null) {
            registWeiboShareAPI();
        }
        this.shareType = shareInfo.shareType;
        this.bussinessId = shareInfo.bussinessId;

        String title = shareInfo.getTitle();
        String content = shareInfo.getContent();
        String contentUrl = shareInfo.getContentUrl();
        content = getSinaContent(title, content, contentUrl);
        Object imgUrl = shareInfo.imgUrl;

        isVideo = false;
        WeiboMultiMessage message = new WeiboMultiMessage();
        if (shareInfo.isImageType()) {
        } else {
            TextObject textObject = new TextObject();
            textObject.text = content + contentUrl;
            message.textObject = textObject;

            ImageObject imageObject = new ImageObject();
            imageObject.imageData = getThumbDataForSina(activity, imgUrl);
            message.imageObject = imageObject;
        }

        // 2. 初始化从第三方到微博的消息请求
        final SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = message;

        if (isSinaClientInstalled(false)) { // 安装了微博
            ILog.d(TAG, "share sina by client");
            if (!AccessTokenKeeper.readLastIsClient(activity)) { // 如果最后一次授权不是客户端则清除
                clearCache();
            }
        } else { // 网页版分享
            ILog.d(TAG, "share sina by client");
            if (AccessTokenKeeper.readLastIsClient(activity)) {
                clearCache();
            }
        }

        String token = "";
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(activity);
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(activity, request, mAuthInfo, token, new WeiboAuthListener() {
            @Override
            public void onWeiboException(WeiboException arg0) {
                onShareFailure(false);
            }

            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(activity, newToken);
                AccessTokenKeeper.writeLastIsClient(activity, false);
            }

            @Override
            public void onCancel() {
                onShareFailure(true);
            }
        });
    }

    /**
     * 新浪登录
     */
    public void loginSina() {
        if (mAuthInfo == null || mSsoHandler == null) {
            return;
        }
        onLoginStart();
        if (isSinaClientInstalled(false)) { // 客户端登录
            ILog.d(TAG, "login sina by client");
            if (!AccessTokenKeeper.readLastIsClient(activity)) {
                clearCache();
            }
        } else {
            ILog.d(TAG, "login sina by web");
            if (AccessTokenKeeper.readLastIsClient(activity)) {
                clearCache();
            }
        }
//        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(activity);
//        if (accessToken.isSessionValid()) {
//            getUserInfo(accessToken);
//        } else {
//            mSsoHandler.authorize(loginAuthListener = new LoginAuthListener());
//        }

        // 登录 每次都重新授权
        mSsoHandler = new SsoHandler(activity, mAuthInfo);
        mSsoHandler.authorize(loginAuthListener = new LoginAuthListener());
    }


    public void onResponse(BaseResponse baseResp) {
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    ILog.d(TAG, "sina share success");
                    onShareSuccess(Platform.SINA, shareType, bussinessId, isVideo);
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    ILog.d(TAG, "sina share cancel");
                    onShareFailure(true);
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    ILog.d(TAG, "sina share fail");
                    onShareFailure(false);
                    break;
                default:
                    onShareFailure(false);
                    break;
            }
        }
    }

    /**
     * 登入按钮的监听器，接收授权结果。
     */
    private class LoginAuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            ILog.d(TAG, "sina login onComplete" + values.toString());
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken != null && accessToken.isSessionValid()) {

                AccessTokenKeeper.writeAccessToken(activity, accessToken);
                AccessTokenKeeper.writeLastIsClient(activity, isSinaClientInstalled(false));
                getUserInfo(accessToken);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                ILog.d(TAG, "sina login onComplete error");
                onLoginFailure(false);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ILog.d(TAG, "sina login onWeiboException" + e);
            onLoginFailure(false);
        }

        @Override
        public void onCancel() {
            ILog.d(TAG, "sina login onCancel");
            onLoginFailure(true);
        }
    }

    private void getUserInfo(Oauth2AccessToken accessToken) {
        // 获取用户信息接口
//        UsersAPI usersAPI = new UsersAPI(activity, SINAAPPKEY, accessToken);
//        long uid = Long.parseLong(accessToken.getUid());
//        usersAPI.show(uid, new RequestListener() {
//            @Override
//            public void onComplete(String response) {
//                ILog.d(TAG, "sina login getUserInfo onComplete");
//                if (!TextUtils.isEmpty(response)) {
//                    ILog.d(TAG, "sina login getUserInfo onComplete json:"+response);
//                    JSONObject json = IJsonUtil.newJSONObject(response);
//                    if (json != null && json.length() > 0) {
//                        OtherLogin otherLogin = new OtherLogin();
//                        otherLogin.parser(json);
//                        onLoginSuccess(otherLogin);
//                    } else {
//                        onLoginFailure();
//                    }
//                } else {
//                    onLoginFailure();
//                }
//            }
//
//            @Override
//            public void onWeiboException(WeiboException e) {
//                ILog.d(TAG, "sina login getUserInfo onWeiboException");
//                onLoginFailure();
//            }
//        });

        HttpRequest request = new HttpRequest();
        request.setUrl(UsersAPI.getUserInfoUrl());
        request.setRequestMethod(HttpRequest.GET);
        request.addRequestParam("access_token", accessToken.getToken());
        request.addRequestParam("uid", accessToken.getUid());
        request.setHttpRequestListener(new HttpRequestCallback() {

            @Override
            public void requestBefore() {

            }

            @Override
            public void requestAfter(final HttpResponse response) {
                String result = response.getResult();
                if (!IStringUtil.isEmpty(result)) {
                    JSONObject json = IJsonUtil.newJSONObject(result);
                    if (json != null && json.length() > 0 && !json.has("error_code")) {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // SSO 授权回调
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        } catch (Exception e) {

        }
    }

}
