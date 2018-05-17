package com.xiaolanba.passenger.library.share;

import com.framework.common.utils.IJsonUtil;
import com.framework.common.utils.ILog;
import com.framework.http.HttpRequest;
import com.framework.http.HttpRequestCallback;
import com.framework.http.HttpResponse;
import com.xiaolanba.passenger.common.listener.ServiceListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/02
 */

public class WeiboUtil {

    /**
     * 获取新浪微博短链接，正常的话可能链接太长
     *
     * @param source
     * @param access_token
     * @param url
     * @param listener
     */
    public static void getSinaWeiboShortUrl(String source, String access_token, final String url, final ServiceListener listener) {
        HttpRequest request = new HttpRequest(HttpRequest.GET);
        request.setUrl("https://api.weibo.com/2/short_url/shorten.json");
        request.setTimeOut(3 * 1000);
        request.addRequestParam("source", source);
        String encodeUrl = url;
        try {
            encodeUrl = URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
        }
        request.addRequestParam("url_long", encodeUrl);
        request.setHttpRequestListener(new HttpRequestCallback() {
            @Override
            public void requestBefore() {

            }

            @Override
            public void requestAfter(HttpResponse response) {
                ILog.i("shoreURL", response.getResponseString());
                //{"urls":[{"object_type":"","result":true,"url_short":"http:\/\/t.cn\/Ry6wqaM","object_id":"","url_long":"http:\/\/www.21yaya.com","type":0}]}
                JSONArray urls = IJsonUtil.getJSONArray("urls", response.getResponse());
                if (null != urls) {
                    JSONObject shortUrl = IJsonUtil.getJSONObject(0, urls);
                    boolean result = IJsonUtil.getBoolean("result", shortUrl);
                    if (result) {
                        String url = IJsonUtil.getString("url_short", shortUrl);
                        listener.serviceSuccess(ServiceListener.ActionTypes.TYPE_SINA_SHORT_URL, null, url);
                        return;
                    }
                }
                listener.serviceFailure(ServiceListener.ActionTypes.TYPE_SINA_SHORT_URL, response.code, response.errorMsg, null);
            }
        });
    }
}
