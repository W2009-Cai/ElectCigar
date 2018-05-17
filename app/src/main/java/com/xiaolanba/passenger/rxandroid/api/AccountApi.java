package com.xiaolanba.passenger.rxandroid.api;

import com.xiaolanba.passenger.common.bean.BaseParse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @Author hai
 * @E-mail shihh@xianlanba.com
 * @Date 2018/04/17
 */
public interface AccountApi {
    @FormUrlEncoded
    @POST("querySystemNotifyList")
    Observable<BaseParse<List<Object>>> querySystemNotifyList(@FieldMap Map<String, String> map); //系统通知列表
    @FormUrlEncoded
    @POST("queryParticularSystemNotify")
    Observable<BaseParse<Object>> queryParticularSystemNotify(@FieldMap Map<String, String> map); //系统通知详情

}
