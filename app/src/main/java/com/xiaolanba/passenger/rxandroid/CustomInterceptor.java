package com.xiaolanba.passenger.rxandroid;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 处理请求头header的拦截器
 *
 * @author xutingz
 * @e-mail xutz@xiaolanba.com
 */
public class CustomInterceptor implements Interceptor {
    /**
     * 统一拦截器，在这里加上header参数
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request()
//                .newBuilder()
//                .header("User-agent", "Mozilla/4.0")
//                .build();
//        return chain.proceed(request);

        Request.Builder builder = chain.request().newBuilder();
//        builder.addHeader("token", "abc");
        return chain.proceed(builder.build());
    }
}
