package com.framework.http;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * HTTP 请求体
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class HttpRequest {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String UPLOAD = "UPLOAD";

    public static final String NAMEVALUEPAIR = "NameValuePair";
    public static final String JSON = "json";

    private String url;
    private Map<String, String> headers = new HashMap<String, String>();
    private String requestMethod = POST;
    private String paramType = NAMEVALUEPAIR;
    /**
     * http 访问超时时间，设置为0 则默认HttpUtil.TIME_OUT
     */
    private int mTimeOut = 0;

    private String apiMethod;

    /**
     * 上传文件的文件路径
     */
    private String uploadFilepath;

    private List<File> mUploadFileList;

    //protected JSONObject params = new JSONObject();

    protected List<NameValuePair> paramsPairList = new ArrayList<NameValuePair>();

    protected Future<?> future;

    protected boolean isLoading;

    /**
     * 是否需要下载，处理存在数据库缓存时不请求网络直接返回数据库缓存
     */
    protected boolean needLoad = true;
    protected HttpRequestCallback listener;

    /**
     * 下载的文件目录
     */
    private String downloadDir;
    /**
     * 下载的文件名
     */
    private String downloadFileName;

    public HttpRequest() {

    }

    public HttpRequest(String requestMethod) {
        setRequestMethod(requestMethod);
    }

    public void isGet() {
        requestMethod = GET;
    }

    public void isPost() {
        requestMethod = POST;
    }

    public void setTimeOut(int mTimeOut) {
        this.mTimeOut = mTimeOut;
    }

    public int getTimeOut() {
        return mTimeOut;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamType() {
        return paramType;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setHttpRequestListener(HttpRequestCallback listener) {
        this.listener = listener;
    }

    /**
     * 增加请求参数
     *
     * @param param
     * @param value
     */
    public void addRequestParam(String param, String value) {
        try {
            if (TextUtils.isEmpty(value)) {
                value = "";
            }
            paramsPairList.add(new NameValuePair(param, value));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 增加请求参数
     *
     * @param paramList
     */
    public void addRequestParam(List<NameValuePair> paramList) {
        try {
            if (null != paramList && paramList.size() > 0) {
                for (NameValuePair param : paramList) {
                    addRequestParam(param.getName(), param.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRequestParam(Object obj) {
        try {
            if (obj != null) {
                Field[] fields = obj.getClass().getFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    Object value = field.get(obj);
                    if (value == null) {
                        value = "";
                    }

                    paramsPairList.add(new NameValuePair(field.getName(), value.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addRequestHeader(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * 获取请求数据
     *
     * @return
     */
    public String getRequestJson() {


        return getRequestJsonParams().toString();
    }

    public JSONObject getRequestJsonParams() {

        JSONObject params = new JSONObject();
        try {
            if (null != paramsPairList && !paramsPairList.isEmpty()) {
                for (NameValuePair pair : paramsPairList) {
                    params.put(pair.getName(), pair.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }

    public Map<String, String> getRequestMap() {
        Map<String, String> map = new HashMap<String, String>();
        try {
            if (null != paramsPairList && !paramsPairList.isEmpty()) {
                for (NameValuePair pair : paramsPairList) {
                    map.put(pair.getName(), pair.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<NameValuePair> getRequestNameValuePair() {
        return paramsPairList;
    }

    public Object getRequestData() {
        if (NAMEVALUEPAIR.equals(paramType)) {
            return getRequestNameValuePair();
        } else {
            return getRequestJson();
        }
    }

    public void addUploadFile(File file) {
        if (mUploadFileList == null) {
            mUploadFileList = new ArrayList<File>();
        }
        if (file != null) {
            mUploadFileList.add(file);
        }
    }

    public List<File> getUploadFileList() {
        return mUploadFileList;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getUploadFilepath() {
        return uploadFilepath;
    }

    public void setUploadFilepath(String uploadFilepath) {
        this.uploadFilepath = uploadFilepath;
    }

    public void setNeedLoad(boolean needLoad) {
        this.needLoad = needLoad;
    }

    public boolean isNeedLoad() {
        return needLoad;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    /**
     * 回调后还在线程中
     */
    public void requestBefore() {
        if (null != listener) listener.requestBefore();
    }

    /**
     * 回调后还在线程中
     *
     * @param response
     */
    public void requestAfter(HttpResponse response) {
        if (null != listener) listener.requestAfter(response);
    }

    /**
     * 是否处理响应数据
     */
    private boolean processResponseData = true;

    public boolean getProcessResponseData() {
        return processResponseData;
    }

    public void setProcessResponseData(boolean processResponseData) {
        this.processResponseData = processResponseData;
    }

    private String mUserAgent;

    public void setUserAgent(String mUserAgent) {
        this.mUserAgent = mUserAgent;
    }

    public String getUserAgent() {
        return mUserAgent;
    }
}
