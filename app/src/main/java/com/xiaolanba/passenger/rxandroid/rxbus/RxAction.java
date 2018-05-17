package com.xiaolanba.passenger.rxandroid.rxbus;

/**
 * rxbus 响应对象封装
 *
 * @author xutingz
 */

public class RxAction {
    public int code;
    public Object data;

    public RxAction(int code, Object data) {
        this.code = code;
        this.data = data;
    }
}
