package com.xiaolanba.passenger.module.market.presenter.contract;

import com.xiaolanba.passenger.common.bean.LeaseItem;
import com.xiaolanba.passenger.common.bean.SaleItem;
import com.xiaolanba.passenger.common.bean.SubMultiLease;
import com.xiaolanba.passenger.common.bean.SubMultiSale;

import java.util.ArrayList;

/**
 * 订单数据请求类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public interface SingleListContract {
    /**
     * 界面回调逻辑
     */
    interface ViewControl{
        void onSingleSaleList(boolean isSuccess, int code, String message, ArrayList<SaleItem> lists); //
        void onSingleLeaseList(boolean isSuccess, int code, String message, ArrayList<LeaseItem> lists); //
    }

    /**
     * 界面调用请求的逻辑
     */
    interface Presenter {
         void getSingleSaleList(long id);
        void getSingleLeaseList(long id);
    }

}
