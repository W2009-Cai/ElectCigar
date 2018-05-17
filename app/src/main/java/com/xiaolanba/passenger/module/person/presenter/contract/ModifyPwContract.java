package com.xiaolanba.passenger.module.person.presenter.contract;

import com.xiaolanba.passenger.common.bean.User;

/**
 * 类的说明⽂字 *
 *
 * @Author hai
 * @E-mail shihh@xianlanba.com
 * @Date 2018/03/29
 */
public interface ModifyPwContract {
    /**
     * 界面回调逻辑
     */
    interface ViewControl{
        void onChangePassword(boolean isSuccess, int code, String message, User data);
    }

    /**
     * 界面调用请求的逻辑
     */
    interface Presenter {
        void changePassword(String userPhone, String userPassword, String userNewPassword,String token);
    }
}
