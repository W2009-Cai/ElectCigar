package com.xiaolanba.passenger.module.market.presenter.contract;

import java.util.List;

/**
 * 订单数据请求类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public interface OrderContract {
    /**
     * 界面回调逻辑
     */
    interface ViewControl{
        void onOrderList(boolean isSuccess, String code, String message, List<Object> list, boolean isRefresh); //isRefresh表示是否下拉刷新
        void onOrderDetail();
    }

    /**
     * 界面调用请求的逻辑
     */
    interface Presenter {
         void getOrderList(int currPage,int pageSize);
         void getOrderDetail();
    }

}
