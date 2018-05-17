package com.xiaolanba.passenger.module.person.presenter.contract;

import com.xiaolanba.passenger.common.bean.User;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/02
 */

public interface LoginRegistContract {
    /**
     * 界面回调逻辑
     */
    interface ViewControl{
        void showProgress();
        void onUserExist(boolean isSuccess,int code,String message);
        void onRegistResult(boolean isSuccess,String message,User result);
        void onLoginResult(boolean isSuccess,String message,User result);
        void onCreateCheckCode(boolean isSuccess,String message); //发送验证码结果
        void onReSetPsd(boolean isSuccess,String message,User result);  //重置密码
        void onLoginOut(boolean isSuccess);//退出登录
    }

    /**
     * 界面调用请求的逻辑
     */
    interface Presenter{
        void isUserExist(String phone);
        void registUser(String phone, String psd,String sms_code);
        void login(String account, String password);
        void createCheckCode(String phone); //发送验证码
        void reSetPsd(String phone,String newPsd); //重置密码
        void loginOut(long userId);
    }

}
