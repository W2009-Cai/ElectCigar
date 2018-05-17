package com.framework.http;


/**
 * HTTP返回code基类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class HttpErrorCode {

    /**
     * 成功
     */
    public static final int CODE_SUCCESS = 0;
    /**
     * 失败
     */
    public static final int CODE_FAILURE = -1;
    /**
     * 失败(isSuccess返回false)
     */
    public static final int CODE_LOGIC_FAILURE = -2;//(http请求成功，但是业务内部逻辑不满足，类似于用已注册的手机号去注册时的错误)
    /**
     * 无网络
     */
    public static final int CODE_NO_NETWORK = 9000;
    /**
     * 其他地方登录
     */
    public static final int CODE_OFFLINE = -9999;
    /**
     * 找不到资源
     */
    public static final int CODE_NOT_RESOURCE = 9001;
    /**
     * timeout
     */
    public static final int CODE_TIMEOUT = 9002;

    /**
     * HTTP访问成功
     */
    public static final int CODE_HTTP_STATUS_SUCCESS_CODE = 200;
    /**
     * 404
     */
    public static final int CODE_404 = 404;

}
