package com.xiaolanba.passenger.rxandroid.rxbus;

/**
 * RxBus 的一些共有常量定义
 *
 * @author xutingz
 */
public class RxConstants {

    public static final int ACTION_LOGIN_SUCCESS = 10001;//登录成功的事件
    public static final int ACTION_SELECT_CITY = 10004;//选择定位城市
    public static final int ACTION_LOGIN_OUT = 1005; //用户主动退出登录
    public static final int ACTION_MODIFY_PHONE = 1006; //用户修改手机号成功
    public static final int ACTION_TOKEN_INVILED = 1010; //token失效，（只有主界面需要，其他界面不需要）
    public static final int ACTION_QRCODE = 1011; //
    public static final int ACTION_QRCODE_RESULT = 1012; //
    public static final int ACTION_REMOVE = 1013; //
    public static final int ACTION_REMOVE_RESULT = 1014; //
    public static final int ACTION_LIMIT = 1015; //
}
