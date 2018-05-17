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
 * @Date 2018/04/20
 */
public interface CommonApi {
    @FormUrlEncoded
    @POST("carBasicService/openCityListAll")
    Observable<BaseParse<List<Object>>> openCityListAll(@FieldMap Map<String, String> map); //已开通城市列表-直接查询所有已经开通城市

}
