package com.xiaolanba.passenger.rxandroid.api;

import com.xiaolanba.passenger.common.bean.BaseParse;
import com.xiaolanba.passenger.common.bean.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 与用户相关的接口api
 *
 * @author xutingz
 */
public interface UserApi {

    @GET("empty")
    Observable<BaseParse<Object>> isUserExist(@Query("userPhone") String phone); //判断用户是否注册过

    @FormUrlEncoded
    @POST("sms_code")
    Observable<BaseParse<Object>> createIdentifyCode(@Field("account") String phone); //发送验证码

    @FormUrlEncoded
    @POST("register")
    Observable<BaseParse<User>> registNewUser(@FieldMap Map<String, String> map); //注册新用户

    @FormUrlEncoded
    @POST("login")
    Observable<BaseParse<User>> login(@FieldMap Map<String, String> map); //用户登录

    @FormUrlEncoded
    @POST("resetPassword")
    Observable<BaseParse<User>> reSetPsd(@FieldMap Map<String, String> map); //用户重置密码

    @FormUrlEncoded
    @POST("logout")
    Observable<BaseParse<Object>> loginOut(@Field("userId") String userId);    //用户退出登录

    @FormUrlEncoded
    @POST("changePassword")
    Observable<BaseParse<User>> changePassword(@FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("feedbackList")
    Observable<BaseParse<List<Object>>> feedbackList(@FieldMap Map<String, String> map); //反馈列表

}
