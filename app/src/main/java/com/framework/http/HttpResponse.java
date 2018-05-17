package com.framework.http;


import android.text.TextUtils;

import com.framework.common.utils.IDateFormatUtil;
import com.framework.common.utils.IJsonUtil;
import com.framework.common.utils.ILog;
import com.framework.common.utils.IStringUtil;
import com.xiaolanba.passenger.LBApplication;
import com.xlb.elect.cigar.R;
import com.xiaolanba.passenger.common.utils.ErrorCode;
import com.xiaolanba.passenger.logic.control.LBController;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Headers;

/**
 * HTTP 响应信息
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class HttpResponse {

    /**
     * 错误码
     */
    public int code = -1;
    /**
     * 错误消息
     */
    public String errorMsg;
    /**
     * http响应json对象
     */
    private JSONObject response;
    /**
     * http响应头
     */
    private Headers headers;
    /**
     * http响应
     */
    private String result;

    /**
     * http 状态码, 提供给寻址使用
     */
    public int statusCode = -1;

    public JSONObject getResponse() {
        return response;
    }

    public String getResponseString() {
        if (response == null) {
            return "";
        }
        return response.toString();
    }

    /**
     * 获取JsonObject 类型数据
     * 默认字段为data
     *
     * @return
     */
    public JSONObject getJsonObjectData() {
        if (response != null) {
            return IJsonUtil.getJSONObject("data", response);
        }
        return null;
    }

    /**
     * 获取JsonObject 类型数据
     *
     * @param key 自定义data字段
     * @return
     */
    public JSONObject getJsonObjectData(String key) {
        if (response != null) {
            return IJsonUtil.getJSONObject(key, response);
        }
        return null;
    }

    /**
     * 获取JsonArray 类型数据
     * 默认字段为list
     *
     * @return
     */
    public JSONArray getJsonArrayData() {
        if (response != null) {
            return IJsonUtil.getJSONArray("list", response);
        }
        return null;
    }

    /**
     * 获取JsonArray 类型数据
     *
     * @param key 自定义list字段
     * @return
     */
    public JSONArray getJsonArrayData(String key) {
        if (response != null) {
            return IJsonUtil.getJSONArray(key, response);
        }
        return null;
    }

    /**
     * 获取String 类型数据
     * 默认字段为url
     *
     * @return
     */
    public String getStringData() {
        if (response != null) {
            return IJsonUtil.getString("url", response);
        }
        return null;
    }

    /**
     * 获取String 类型数据
     *
     * @param key 自定义url字段
     * @return
     */
    public String getStringData(String key) {
        if (response != null) {
            return IJsonUtil.getString(key, response);
        }
        return null;
    }

    /**
     * 获取long 类型数据
     *
     * @return
     */
    public long getLongData() {
        if (response != null) {
            return IJsonUtil.getLong("id", response);
        }
        return 0;
    }

    /**
     * 获取long 类型数据
     *
     * @param key 自定义id字段
     * @return
     */
    public long getLongData(String key) {
        if (response != null) {
            return IJsonUtil.getLong(key, response);
        }
        return 0;
    }

    /**
     * @param headers
     * @param responseBody
     */
    public HttpResponse(Headers headers, String responseBody) {
        this(responseBody);
        this.headers = headers;
        if (headers != null) {
            String serverDate = headers.get("Date");
            if (!TextUtils.isEmpty(serverDate)) {
                long serverTime = IDateFormatUtil.getServerTimestamp(serverDate);
                LBController.getInstance().getCacheManager().setServerTime(serverTime);

                ILog.w("------------server time-----------", IDateFormatUtil.getFormatTime(serverTime, IDateFormatUtil.YYYY_MM_DD_HH_MM_SS));
            }
        }
    }

    /**
     * @param result
     */
    public HttpResponse(String result) {
        this.result = result;

        if (!IStringUtil.isEmpty(result) && !result.startsWith("<html>")) {
            JSONObject jsonObj = IJsonUtil.newJSONObject(result);
            if (jsonObj != null) {

                if (!jsonObj.isNull("code")) {
                    code = IJsonUtil.getInt("code", jsonObj);

                    // session 无效
                    if (code == ErrorCode.CODE_METHOD_SESSION_ID_IS_INVALID) {
                        errorMsg = IJsonUtil.getString("message", jsonObj);
                        ILog.e("HttpResponse", "session 无效");
                    }

                    // 如果有错误提示，取出第一个错误提示
                    JSONArray errorArray = IJsonUtil.getJSONArray("subErrors", jsonObj);
                    if (errorArray != null && errorArray.length() > 0) {
                        JSONObject errorObject = IJsonUtil.getJSONObject(0, errorArray);
                        if (errorObject != null) {
                            errorMsg = IJsonUtil.getString("message", errorObject);

                            if (!IStringUtil.isEmpty(errorMsg) && errorMsg.length() > 50) {
                                errorMsg = LBApplication.getInstance().getResources().getString(R.string.server_err);
                            }
                        }
                    }
                } else {
                    code = ErrorCode.CODE_SUCCESS;

                    if (!jsonObj.isNull("isSuccess")) {
                        boolean isSuccess = IJsonUtil.getBoolean("isSuccess", jsonObj);
                        if (isSuccess) {
                            code = ErrorCode.CODE_SUCCESS;
                        } else {
                            code = ErrorCode.CODE_LOGIC_FAILURE;
                        }
                    }
                }

                if (IStringUtil.isEmpty(errorMsg)) {
                    errorMsg = IJsonUtil.getString("message", jsonObj);
                }

                if (IStringUtil.isEmpty(errorMsg)) {
                    errorMsg = IJsonUtil.getString("prompt", jsonObj);
                }

                response = jsonObj;

            }
        }
    }

    public HttpResponse(int errorCode) {
        code = errorCode;

        if (HttpErrorCode.CODE_TIMEOUT == errorCode) {
            errorMsg = LBApplication.getInstance().getResources().getString(R.string.failed);
        }
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    /**
     * 请求url
     */
    private String requestUrl;

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
