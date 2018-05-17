package com.xiaolanba.passenger.rxandroid;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

/**
 * 自定义请求的JsonResponseBody
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/09
 */

public class JsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    /**
     * 构造器
     */

    public JsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }


//    @Override
//    public RequestBody convert(T value) throws IOException {
//        //加密
//        ILog.i("JsonRequestBody", "JsonRequestBodyt中传递的json数据：" + value.toString());
//        String postBody = gson.toJson(value.toString()); //对象转化成json
//
//        ILog.i("JsonRequestBody", "JsonRequestBody转化后的数据：" + postBody);
//        return RequestBody.create(MEDIA_TYPE, postBody);
//    }

    @Override
    public RequestBody convert(T value) throws IOException {
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        adapter.write(jsonWriter, value);
        jsonWriter.close();
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }

}
