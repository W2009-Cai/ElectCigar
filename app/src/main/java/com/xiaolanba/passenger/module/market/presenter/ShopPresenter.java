package com.xiaolanba.passenger.module.market.presenter;

import com.xiaolanba.passenger.common.base.BasePresenter;
import com.xiaolanba.passenger.common.bean.BaseObserver;
import com.xiaolanba.passenger.common.bean.MultiShopList;
import com.xiaolanba.passenger.module.market.presenter.contract.ShopContract;
import com.xiaolanba.passenger.rxandroid.api.ApiManager;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/11
 */

public class ShopPresenter extends BasePresenter implements ShopContract.Presenter {

    private ShopContract.ViewControl mViewControl;

    public ShopPresenter(ShopContract.ViewControl vcl){
        this.mViewControl = vcl;
    }

    @Override
    public void getShopIndexList() {
        ApiManager.getInstence().getShopServie().shopIndexList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MultiShopList>(){

                    @Override
                    protected void onSuccees(String message, MultiShopList data) {
                        if (isDestoryed) return;
                        mViewControl.onShopIndexList(true,200,message,data);
                    }

                    @Override
                    protected void onFailed(int code, String message, MultiShopList data) {
                        if (isDestoryed) return;
                        mViewControl.onShopIndexList(false,code,message,data);
                    }
                });
    }
}
