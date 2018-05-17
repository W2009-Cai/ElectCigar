package com.xiaolanba.passenger.rxandroid.api;

import com.xiaolanba.passenger.common.bean.BaseParse;
import com.xiaolanba.passenger.common.bean.LeaseItem;
import com.xiaolanba.passenger.common.bean.MultiShopList;
import com.xiaolanba.passenger.common.bean.SaleItem;
import com.xiaolanba.passenger.common.bean.SubMultiLease;
import com.xiaolanba.passenger.common.bean.SubMultiSale;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 对应后台/order工程
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/11
 */

public interface ShopApi {

    @POST("index")
    Observable<BaseParse<MultiShopList>> shopIndexList(); //商城首页

    @POST("salesClass")
    Observable<BaseParse<ArrayList<SubMultiSale>>> saleMoreList(); //商城更多

    @POST("leaseClass")
    Observable<BaseParse<ArrayList<SubMultiLease>>> leaseMoreList(); //商城更多

    @FormUrlEncoded
    @POST("salesGoods")
    Observable<BaseParse<ArrayList<SaleItem>>> singSaleList(@Field("class_id") long class_id); //商城更多

    @FormUrlEncoded
    @POST("leaseGoods")
    Observable<BaseParse<ArrayList<LeaseItem>>> singLeaseList(@Field("class_id") long class_id); //商城更多

}
