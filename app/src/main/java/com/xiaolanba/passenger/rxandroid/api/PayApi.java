package com.xiaolanba.passenger.rxandroid.api;

import com.xiaolanba.passenger.common.bean.BaseParse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/10
 */

public interface PayApi {
//    https://api.weixin.qq.com/cgi-
//    bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET

    @FormUrlEncoded
    @POST("pay/unifiedPay")
    Observable<BaseParse<Object>> unifiedPay(@FieldMap Map<String, String> params);

    //查询订单结果接口
    @FormUrlEncoded
    @POST("pay/querypayresult")
    Observable<BaseParse<Object>> queryPayResult(@FieldMap Map<String, String> params);
}
