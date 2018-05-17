package com.xiaolanba.passenger.rxandroid.api;

import com.xiaolanba.passenger.common.bean.BaseParse;

import java.util.Map;

import io.reactivex.Observable;
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

public interface OrderApi {

    @FormUrlEncoded
    @POST("placeOrder")
    Observable<BaseParse<Object>> createOrder(@FieldMap Map<String, String> map); //用户下单

    @FormUrlEncoded
    @POST("cancelOrder")
    Observable<BaseParse<Object>> cancelOrder(@FieldMap Map<String, String> map); //用户取消订单

    @FormUrlEncoded
    @POST("getOrderList")
    Observable<BaseParse<Object>> getOrderList(@FieldMap Map<String, String> map); //我的订单

    @FormUrlEncoded
    @POST("getOrderDtails")
    Observable<BaseParse<Object>> orderDetail(@FieldMap Map<String, String> map); //订单的详情

    @FormUrlEncoded
    @POST("findLateCarRunMess")
    Observable<BaseParse<Object>> getCarOnTime(@FieldMap Map<String, String> map); //获取车子上车时间

    @FormUrlEncoded
    @POST("findKilometersCarRunList")
    Observable<BaseParse<Object>> getCarListBounds(@FieldMap Map<String, String> map); //获取一公里内的车子列表


}
