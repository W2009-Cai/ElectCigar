package com.xiaolanba.passenger.rxandroid;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 自定义的JsonConvert转换器，可以自己拿到后台返回的String作相应处理
 * 解决data字段空判断问题
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/09
 */
public class JsonConvertFactory extends Converter.Factory {

    public static JsonConvertFactory create() {
        return create(new Gson());
    }

    public static JsonConvertFactory create(Gson gson) {
        return new JsonConvertFactory(gson);

    }

    private final Gson gson;

    private JsonConvertFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }


    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {

        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonResponseBodyConverter<>(gson, adapter); //响应
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {

        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonRequestBodyConverter<>(gson, adapter); //请求
    }

}

