package com.xiaolanba.passenger.module.market.presenter;

import com.xiaolanba.passenger.common.base.BasePresenter;
import com.xiaolanba.passenger.module.market.presenter.contract.OrderContract;

import java.util.Map;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/11
 */

public class OrderPresenter extends BasePresenter implements OrderContract.Presenter {

    private OrderContract.ViewControl mViewControl;

    public OrderPresenter(OrderContract.ViewControl vcl){
        this.mViewControl = vcl;
    }

    @Override
    public void getOrderList(final int currPage,int pageSize) {
        Map<String,String> map = httpParams.getHeaderMap();
        map.put("currPage",String.valueOf(currPage));
        map.put("pageSize",String.valueOf(pageSize));
//        ApiManager.getInstence().getOrderServie().getOrderList(map)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserver<OrderList>(){
//
//                    @Override
//                    protected void onSuccees(String message, OrderList data) {
//                        if (isDestoryed) return;
//                        mViewControl.onOrderList(true,BaseObserver.SUCCESS,message,data,currPage ==1);
//                    }
//
//                    @Override
//                    protected void onFailed(String code, String message, OrderList data) {
//                        if (isDestoryed) return;
//                        mViewControl.onOrderList(false,code,message,data,currPage ==1);
//                    }
//                });
    }

    @Override
    public void getOrderDetail() {

    }
}
