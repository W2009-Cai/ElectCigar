package com.xiaolanba.passenger.module.market.presenter.contract;

import com.xiaolanba.passenger.common.bean.MultiShopList;
import com.xiaolanba.passenger.common.bean.SubMultiLease;
import com.xiaolanba.passenger.common.bean.SubMultiSale;

import java.util.ArrayList;

/**
 * 订单数据请求类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public interface SubShopContract {
    /**
     * 界面回调逻辑
     */
    interface ViewControl{
        void onSubSaleList(boolean isSuccess, int code, String message, ArrayList<SubMultiSale> lists); //
        void onSubLeaseList(boolean isSuccess, int code, String message, ArrayList<SubMultiLease> lists); //
    }

    /**
     * 界面调用请求的逻辑
     */
    interface Presenter {
         void getSubSaleList();
        void getSubLeaseList();
    }

}
