package com.xiaolanba.passenger.module.market.presenter;

import com.xiaolanba.passenger.common.base.BasePresenter;
import com.xiaolanba.passenger.common.bean.BaseObserver;
import com.xiaolanba.passenger.common.bean.LeaseItem;
import com.xiaolanba.passenger.common.bean.SaleItem;
import com.xiaolanba.passenger.common.bean.SubMultiLease;
import com.xiaolanba.passenger.common.bean.SubMultiSale;
import com.xiaolanba.passenger.module.market.presenter.contract.SingleListContract;
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

public class SingleListPresenter extends BasePresenter implements SingleListContract.Presenter {

    private SingleListContract.ViewControl mViewControl;

    public SingleListPresenter(SingleListContract.ViewControl vcl){
        this.mViewControl = vcl;
    }

    @Override
    public void getSingleSaleList(long id) {
        ApiManager.getInstence().getShopServie().singSaleList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ArrayList<SaleItem>>(){

                    @Override
                    protected void onSuccees(String message, ArrayList<SaleItem> data) {
                        if (isDestoryed) return;
                        mViewControl.onSingleSaleList(true,200,message,data);
                    }

                    @Override
                    protected void onFailed(int code, String message, ArrayList<SaleItem> data) {
                        if (isDestoryed) return;
                        mViewControl.onSingleSaleList(false,code,message,data);
                    }
                });
    }

    @Override
    public void getSingleLeaseList(long id) {
        ApiManager.getInstence().getShopServie().singLeaseList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ArrayList<LeaseItem>>(){

                    @Override
                    protected void onSuccees(String message, ArrayList<LeaseItem> data) {
                        if (isDestoryed) return;
                        mViewControl.onSingleLeaseList(true,200,message,data);
                    }

                    @Override
                    protected void onFailed(int code, String message, ArrayList<LeaseItem> data) {
                        if (isDestoryed) return;
                        mViewControl.onSingleLeaseList(false,code,message,data);
                    }
                });
    }

//    @Override
//    public void getSubSaleList() {
//        ApiManager.getInstence().getShopServie().saleMoreList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserver<ArrayList<SubMultiSale>>(){
//
//                    @Override
//                    protected void onSuccees(String message, ArrayList<SubMultiSale> data) {
//                        if (isDestoryed) return;
//                        mViewControl.onSubSaleList(true,200,message,data);
//                    }
//
//                    @Override
//                    protected void onFailed(int code, String message, ArrayList<SubMultiSale> data) {
//                        if (isDestoryed) return;
//                        mViewControl.onSubSaleList(false,code,message,data);
//                    }
//                });
//    }

}
