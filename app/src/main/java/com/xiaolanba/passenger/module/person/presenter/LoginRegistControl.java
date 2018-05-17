package com.xiaolanba.passenger.module.person.presenter;

import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.module.person.presenter.contract.LoginRegistContract;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/02
 */

public class LoginRegistControl implements LoginRegistContract.ViewControl{
    @Override
    public void showProgress() {

    }

    @Override
    public void onUserExist(boolean isSuccess,int code,String message) {

    }

    @Override
    public void onRegistResult(boolean isSuccess,String message, User result) {

    }

    @Override
    public void onLoginResult(boolean isSuccess, String message, User result) {

    }

    @Override
    public void onCreateCheckCode(boolean isSuccess,String message) {

    }


    @Override
    public void onReSetPsd(boolean isSuccess, String message,User data) {

    }

    @Override
    public void onLoginOut(boolean isSuccess) {

    }

}
