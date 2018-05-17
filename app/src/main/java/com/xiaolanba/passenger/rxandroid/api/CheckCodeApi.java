package com.xiaolanba.passenger.rxandroid.api;

import com.xiaolanba.passenger.common.bean.BaseParse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/03
 */

public interface CheckCodeApi {
//    @FormUrlEncoded
//    @POST("sms_code")
//    Observable<BaseParse<Object>> createIdentifyCode(@Field("phone") String phone); //发送验证码

    @FormUrlEncoded
    @POST("verifyCode")
    Observable<BaseParse<Object>> verifyCode(@FieldMap Map<String, String> map); //验证验证码是否成功


//    //获取某一时间之前的日报（本例用于加载更多），使用Get方法的用例
//    @GET("news/before/{date}")
//    Observable<User> getUserByDate(@Path("date") String date);
//
//    @GET("news/{id}")
//    Observable<User> getUserById(@Path("id") String id);
}
