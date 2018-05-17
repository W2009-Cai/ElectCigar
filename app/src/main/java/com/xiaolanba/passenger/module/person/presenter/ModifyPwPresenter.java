package com.xiaolanba.passenger.module.person.presenter;

import com.xiaolanba.passenger.common.base.BasePresenter;
import com.xiaolanba.passenger.common.bean.BaseObserver;
import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.module.person.presenter.contract.ModifyPwContract;
import com.xiaolanba.passenger.rxandroid.api.ApiManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 修改密码的presenter
 *
 * @Author hai
 * @E-mail shihh@xianlanba.com
 * @Date 2018/03/29
 */
public class ModifyPwPresenter extends BasePresenter implements ModifyPwContract.Presenter {
    private ModifyPwContract.ViewControl mViewControl;

    public ModifyPwPresenter(ModifyPwContract.ViewControl mViewControl) {
        this.mViewControl = mViewControl;
    }

    @Override
    public void changePassword(String userPhone, String userPassword, String userNewPassword, String token) {
        Map<String,String> map = new HashMap<>();
        map.put("userPhone",userPhone);
        map.put("userPassword",userPassword);
        map.put("userNewPassword",userNewPassword);
        map.put("token",token);
        ApiManager.getInstence().getUserServie().changePassword(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<User>(){
                    @Override
                    protected void onSuccees(String message,User data) {
                        if (isDestoryed) return;
//                        mViewControl.onChangePassword(true,"200",message,data);
                    }

                    @Override
                    protected void onFailed(int code, String message, User data) {
                        if (isDestoryed) return;
//                        mViewControl.onChangePassword(false,code,message,data);
                    }
                });
    }
}
