package com.xiaolanba.passenger.rxandroid;

import com.framework.common.utils.IJsonUtil;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 自定义响应的JsonResponseBody
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/09
 */

public class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson mGson;//gson对象
    private final TypeAdapter<T> adapter;

    /**
     * 构造器
     */
    public JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.mGson = gson;
        this.adapter = adapter;
    }

    /**
     * 转换
     *
     * @param value
     * @return
     * @throws IOException
     */
//    @Override
//    public T convert(ResponseBody value) throws IOException {
//        String response = value.string();
//        ILog.i("JsonResponseBody","---JsonResponseBody value="+response);
//        JsonReader jsonReader = mGson.newJsonReader(value.charStream());
//        try {
//            return adapter.read(jsonReader);
//        } finally {
//            value.close();
//        }
//    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            if (response != null && response.startsWith("{")){
                JSONObject jsonObject = IJsonUtil.newJSONObject(response);
                if (jsonObject != null){
                    String data = IJsonUtil.getString("data",jsonObject);
                    if (data != null && ("".equals(data) || "[]".equals(data)||"{}".equals(data))){
                        IJsonUtil.putString(jsonObject,"data",null);
                        response = jsonObject.toString();
                    }
                }
            }
            return adapter.fromJson(response);
        } finally {
            value.close();
        }
    }

}
