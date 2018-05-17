package com.xiaolanba.passenger.module.market.presenter;

import com.xiaolanba.passenger.common.base.BasePresenter;
import com.xiaolanba.passenger.common.bean.BaseObserver;
import com.xiaolanba.passenger.common.bean.MultiShopList;
import com.xiaolanba.passenger.common.bean.SubMultiLease;
import com.xiaolanba.passenger.common.bean.SubMultiSale;
import com.xiaolanba.passenger.module.market.presenter.contract.ShopContract;
import com.xiaolanba.passenger.module.market.presenter.contract.SubShopContract;
import com.xiaolanba.passenger.rxandroid.api.ApiManager;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/11
 */

public class SubShopPresenter extends BasePresenter implements SubShopContract.Presenter {

    private SubShopContract.ViewControl mViewControl;

    public SubShopPresenter(SubShopContract.ViewControl vcl){
        this.mViewControl = vcl;
    }

    @Override
    public void getSubSaleList() {
        ApiManager.getInstence().getShopServie().saleMoreList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ArrayList<SubMultiSale>>(){

                    @Override
                    protected void onSuccees(String message, ArrayList<SubMultiSale> data) {
                        if (isDestoryed) return;
                        mViewControl.onSubSaleList(true,200,message,data);
                    }

                    @Override
                    protected void onFailed(int code, String message, ArrayList<SubMultiSale> data) {
                        if (isDestoryed) return;
                        mViewControl.onSubSaleList(false,code,message,data);
                    }
                });
    }

    @Override
    public void getSubLeaseList() {
        ApiManager.getInstence().getShopServie().leaseMoreList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ArrayList<SubMultiLease>>(){

                    @Override
                    protected void onSuccees(String message, ArrayList<SubMultiLease> data) {
                        if (isDestoryed) return;
                        mViewControl.onSubLeaseList(true,200,message,data);
                    }

                    @Override
                    protected void onFailed(int code, String message, ArrayList<SubMultiLease> data) {
                        if (isDestoryed) return;
                        mViewControl.onSubLeaseList(false,code,message,data);
                    }
                });
    }

//    @Override
//    public void getShopIndexList() {
//        ApiManager.getInstence().getShopServie().shopIndexList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserver<MultiShopList>(){
//
//                    @Override
//                    protected void onSuccees(String message, MultiShopList data) {
//                        if (isDestoryed) return;
//                        mViewControl.onShopIndexList(true,200,message,data);
//                    }
//
//                    @Override
//                    protected void onFailed(int code, String message, MultiShopList data) {
//                        if (isDestoryed) return;
//                    }
//                });
//    }
}
