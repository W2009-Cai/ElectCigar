package com.xiaolanba.passenger.module.market.presenter.contract;

import com.xiaolanba.passenger.common.bean.MultiShopList;

import java.util.List;

/**
 * 订单数据请求类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public interface ShopContract {
    /**
     * 界面回调逻辑
     */
    interface ViewControl{
        void onShopIndexList(boolean isSuccess, int code, String message, MultiShopList lists); //
    }

    /**
     * 界面调用请求的逻辑
     */
    interface Presenter {
         void getShopIndexList();
    }

}
