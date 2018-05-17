package com.xiaolanba.passenger.common.utils;


import com.framework.http.HttpErrorCode;

/**
 * 服务器返回的错误码信息
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class ErrorCode extends HttpErrorCode{

    /**
     * 服务不可用
     */
    public static final int CODE_SERVICE_NOT_AVAILABLE = 1;
    /**
     * 开发者权限不足
     */
    public static final int CODE_DEVELOPERS_PERMISSIONS_DENIED = 2;
    /**
     * 服务方法用户权限不足
     */
    public static final int CODE_METHOD_PERMISSIONS_DENIED = 3;
    /**
     * 服务方法图片上传失败
     */
    public static final int CODE_IMAGE_UPLOAD_FAILED = 4;
    /**
     * 服务方法HTT方法被禁止
     */
    public static final int CODE_METHOD_BE_BANNED = 5;
    /**
     * 服务方法编码错误
     */
    public static final int CODE_METHOD_CODING_ERRORS = 6;
    /**
     * 服务方法请求被禁止
     */
    public static final int CODE_METHOD_REQUEST_PROHIBITED = 7;
    /**
     * 服务方法已经作废
     */
    public static final int CODE_METHOD_IS_INVALID = 8;
    /**
     * 服务方法业务逻辑出错
     */
    public static final int CODE_METHOD_BUSINESS_LOGIC_ERRORS = 9;
    /**
     * 服务方法缺少会话参数
     */
    public static final int CODE_METHOD_LACKOF_SESSION_PARAMETERS = 20;
    /**
     * 服务方法的会话ID无效
     */
    public static final int CODE_METHOD_SESSION_ID_IS_INVALID = 21;
    /**
     * 服务方法缺少应用键参数
     */
    public static final int CODE_METHOD_LACKOF_KEY_PARAMETERS = 22;
    /**
     * 服务方法的应用键参数无效
     */
    public static final int CODE_METHOD_KEY_PARAMETERS_INVALID = 23;
    /**
     * 服务方法需要签名,缺少签名参数
     */
    public static final int CODE_METHOD_NEED_SIGNATURE = 24;
    /**
     * 服务方法的签名无效
     */
    public static final int CODE_METHOD_SIGNATURE_INVALID = 25;
    /**
     * 服务请求缺少方法名参数
     */
    public static final int CODE_REQUEST_LACKS_PARAMETERS = 26;
    /**
     * 调用不存在的服务方法
     */
    public static final int CODE_SERVICE_METHOD_NO = 27;
    /**
     * 服务方法缺少版本参数
     */
    public static final int CODE_METHOD_LACKOF_VERSION_PARAMETERS = 28;
    /**
     * 服务方法不存在对应的版本
     */
    public static final int CODE_METHOD_NO_CORRESPONDING_VERSION = 29;
    /**
     * 服务方法不支持对应的版本号
     */
    public static final int CODE_METHOD_NOT_SUPPORT_CORRESPONDING_VERSION = 30;
    /**
     * 服务方法的报文数据格式无效
     */
    public static final int CODE_METHOD_MESSAGE_DATA_FORMAT_INVALID = 31;
    /**
     * 服务方法缺少必要的参数
     */
    public static final int CODE_METHOD_LACK_NECESSARY_PARAMETERS = 32;
    /**
     * 服务方法的参数非法
     */
    public static final int CODE_METHOD_PARAMETER_ILLEGAL = 33;
    /**
     * 服务方法的调用次数超限
     */
    public static final int CODE_METHOD_TRANSFINITE_CALLNUMBER = 34;
    /**
     * 用户会话调用服务的次数超限
     */
    public static final int CODE_SESSION_INVOKE_OVERRUN = 35;
    /**
     * 应用调用服务的次数超限
     */
    public static final int CODE_APPLICATIONS_INVOKE_OVERRUN = 36;
    /**
     * 应用调用服务的频率超限
     */
    public static final int CODE_APPLICATIONS_INVOKE_FREQUENCY_OVERRUN = 37;


}
