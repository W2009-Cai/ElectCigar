package com.xiaolanba.passenger.common.utils;


/**
 * 常量定义
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public abstract class Constants {

    /** 回车换行 */
    public static final String ENTER = "\r\n";
    /** 分页   每页数量 */
    public static final int PAGE_SIZE = 10;
    public static final int PAGE_SIZE_20 = 20;

    /** 刷新 */
    public static final int REFRESH = 1;
    /** 加载更多 */
    public static final int LOADMORE = 2;

    public static final int PAGE_TYPE_REGIST = 1;  //注册
    public static final int PAGE_TYPE_FINDPSD = 2; //找回密码
    public static final int PAGE_TYPE_BINDPHONE = 3;//绑定手机号

    public static final String IDENTIFY_TYPE = "identify_type"; //验证码类型传值（忘记密码or注册）
    public static final String PHONE_NUM = "phone_num"; //手机号传值
    public static final String SMS_CODE = "sms_code"; //短信验证码
    public static final String LEDEVICES = "ledevices"; //
    public static final String IS_CONNECTED = "isconnected"; //

    public static final String TYPE_SET = "type_set"; //跳转密码界面方式传值（输入密码，注册设置密码、重新设置密码）
    public static final String IS_FORGET = "isForget";//跳转到输入手机号界面传值（是否是点击忘记密码跳转的）
    public static final String TOAST_STR = "toastStr";//跳转到输入手机号界面是否要弹toast提示

    public static final int PAY_ALI = 1;
    public static final int PAY_WEIIN = 2;

    public static final String ORDER_LIST_DATE_FORMAT = "yyyy年MM月dd日 HH:mm";
    /** 当前展示页面Log的TAG */

    public static final String ORDER_DETAIL = "orderDetail";  // 跳转订单详情传值
}

