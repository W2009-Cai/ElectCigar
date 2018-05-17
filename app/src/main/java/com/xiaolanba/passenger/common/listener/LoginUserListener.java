package com.xiaolanba.passenger.common.listener;


import com.xiaolanba.passenger.common.bean.User;

/**
 * 登录用户信息更新
 *
 * @author xutingz
 */
public interface LoginUserListener {
    /**
     * 用户登陆成功
     *
     * @param loginUser
     */
    void loginSuccess(User loginUser);

    /**
     * 用户信息更改回调
     *
     * @param loginUser
     */
    void updateLoginUser(User loginUser);

}
