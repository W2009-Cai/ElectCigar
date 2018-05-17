package com.xiaolanba.passenger.rxandroid.api;

import com.framework.common.utils.ILog;
import com.xiaolanba.commonlib.https.HttpsUtils;
import com.xiaolanba.passenger.rxandroid.CustomInterceptor;
import com.xiaolanba.passenger.rxandroid.JsonConvertFactory;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 集中处理Api相关配置的Manager类
 *
 * @author xutingz
 * @e-mail xutz@xiaolanba.com
 */
public class ApiManager {
    public static final String BASE_URL = "http://120.79.21.206/smoke/app/";
    public static final String HEAR_URL = "http://120.79.21.206/smoke/upload/";//http://120.79.21.206/heater/upload/
    private UserApi mUserApi;
    private ShopApi mShopApi;
    private static ApiManager sApiManager;

    private static OkHttpClient mClient;
    private static HttpsUtils.SSLParams sslParams;

    private ApiManager() {

    }

    public static ApiManager getInstence() {
        if (sApiManager == null) {
            synchronized (ApiManager.class) {
                if (sApiManager == null) {
                    sApiManager = new ApiManager();
                }
            }
        }
        if (sslParams == null){
            sslParams = HttpsUtils.getSSLParamsCer(null,null);
        }
        if (mClient == null) {
            OkHttpClient.Builder build = new OkHttpClient.Builder();
            if (ILog.DEBUG) { // 日志关闭时，不能打印请求日志
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                build.addInterceptor(interceptor);
            }
            build.addInterceptor(new CustomInterceptor())
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .hostnameVerifier(new HostnameVerifier() {//验证域名是否正确
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
            if (sslParams.sSLSocketFactory != null){
                if (sslParams.trustManager != null){
                    build.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
                } else {
                    build.sslSocketFactory(sslParams.sSLSocketFactory);
                }
            }
            mClient = build.build();
        }
        return sApiManager;
    }

    public UserApi getUserServie() {
        if (mUserApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL+"login/")
                    .client(mClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(JsonConvertFactory.create())
                    .build();
            mUserApi = retrofit.create(UserApi.class);
        }
        return mUserApi;
    }


    public ShopApi getShopServie() {
        if (mShopApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL+"Shop/")
                    .client(mClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(JsonConvertFactory.create())
                    .build();
            mShopApi = retrofit.create(ShopApi.class);
        }
        return mShopApi;
    }


}
