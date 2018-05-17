package com.xiaolanba.passenger.common.listener;


/**
 * 接口请求返回监听
 *
 * @author xutingz
 */
public interface ServiceListener {

    /**
     * 接口请求前
     *
     * @param type
     * @param returnObj
     */
    void serviceBefore(ActionTypes type, Object returnObj);

    /**
     * 执行动作成功
     *
     * @param bandObj   当前操作
     * @param returnObj 返回对象
     */
    void serviceSuccess(ActionTypes type, Object bandObj, Object returnObj);

    /**
     * 执行动作失败
     *
     * @param errorMsg  当前操作
     * @param returnObj 返回对象
     */
    void serviceFailure(ActionTypes type, int errorCode, String errorMsg, Object returnObj);


    public enum ActionTypes {
        TYPE_SINA_SHORT_URL
    }

}
