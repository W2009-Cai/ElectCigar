package com.xiaolanba.passenger.module.person.presenter;

import com.xiaolanba.passenger.LBApplication;
import com.xiaolanba.passenger.common.base.BasePresenter;
import com.xiaolanba.passenger.common.bean.BaseObserver;
import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.module.person.presenter.contract.LoginRegistContract;
import com.xiaolanba.passenger.rxandroid.api.ApiManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/02
 */

public class LoginRegistPresenter extends BasePresenter implements LoginRegistContract.Presenter {

    private LoginRegistContract.ViewControl mViewControl;

    public LoginRegistPresenter(LoginRegistContract.ViewControl vcl){
        this.mViewControl = vcl;
    }

    @Override
    public void isUserExist(String phone) {
        mViewControl.showProgress();
        ApiManager.getInstence().getUserServie().isUserExist(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Object>(){

                    @Override
                    protected void onSuccees(String message,Object data) {
                        if (isDestoryed) return;
                        mViewControl.onUserExist(true,200,message);
                    }

                    @Override
                    protected void onFailed(int code, String message, Object data) {
                        if (isDestoryed) return;
                        mViewControl.onUserExist(false,code,message);
                    }
                });
    }

    @Override
    public void registUser(String account, String password,String sms_code) {
        mViewControl.showProgress();
        Map<String,String> map = new HashMap<>();
        map.put("account",account);
        map.put("sms_code",String.valueOf(sms_code));
        map.put("password",password);
        map.put("device_number",LBApplication.getInstance().httpParams.androidid);
        map.put("system", LBApplication.getInstance().httpParams.systemVer);
        ApiManager.getInstence().getUserServie().registNewUser(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<User>(){

                    @Override
                    protected void onSuccees(String message,User data) {
                        if (isDestoryed) return;
                        mViewControl.onRegistResult(true,null,data);
                    }

                    @Override
                    protected void onFailed(int code, String message, User data) {
                        if (isDestoryed) return;
                        mViewControl.onRegistResult(false,message,data);
                    }
                });
    }

    @Override
    public void login(String account, String password) {
        mViewControl.showProgress();

        Map<String,String> map = new HashMap<>();
        map.put("account",account);
        map.put("password",password);
        map.put("device_number",LBApplication.getInstance().httpParams.androidid);
        map.put("system", LBApplication.getInstance().httpParams.systemVer);
        ApiManager.getInstence().getUserServie().login(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<User>(){

                    @Override
                    protected void onSuccees(String message,User data) {
                        if (isDestoryed) return;
                        mViewControl.onLoginResult(true,null,data);
                    }

                    @Override
                    protected void onFailed(int code, String message, User data) {
                        if (isDestoryed) return;
                        mViewControl.onLoginResult(false, message, data);
                    }
                });
    }

    @Override
    public void createCheckCode(String account) {
//        mViewControl.showProgress();
        ApiManager.getInstence().getUserServie().createIdentifyCode(account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Object>(){
                    @Override
                    protected void onSuccees(String message,Object data) {
                        if (isDestoryed) return;
                        mViewControl.onCreateCheckCode(true,message);
                    }

                    @Override
                    protected void onFailed(int code, String message, Object data) {
                        if (isDestoryed) return;
                        mViewControl.onCreateCheckCode(false,message);
                    }
                });
    }


    @Override
    public void reSetPsd(String phone, String newPsd) {
        mViewControl.showProgress();
        Map<String,String> map = new HashMap<>();
        map.put("userPhone",phone);
        map.put("userNewPassword",newPsd);
        ApiManager.getInstence().getUserServie().reSetPsd(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<User>(){

                    @Override
                    protected void onSuccees(String message,User data) {
                        if (isDestoryed) return;
                        mViewControl.onReSetPsd(true,message,data);
                    }

                    @Override
                    protected void onFailed(int code, String message, User data) {
                        if (isDestoryed) return;
                        mViewControl.onReSetPsd(false,message,data);
                    }
                });
    }

    @Override
    public void loginOut(long userId) {
        ApiManager.getInstence().getUserServie().loginOut(String.valueOf(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Object>(){

                    @Override
                    protected void onSuccees(String message,Object data) {
                        if (isDestoryed) return;
                    }

                    @Override
                    protected void onFailed(int code, String message, Object data) {
                        if (isDestoryed) return;
                    }
                });
    }
}
