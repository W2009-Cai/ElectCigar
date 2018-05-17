package com.framework.http;

import android.net.Uri;
import android.text.TextUtils;

import com.framework.common.utils.IFileUtil;
import com.framework.common.utils.IImageUtil;
import com.framework.common.utils.ILog;
import com.framework.common.utils.INetworkUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HTTP 工具类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class HttpUtil {

    /**
     * 请求超时
     */
    public static final int CONNECT_TIME_OUT = 10 * 1000;
    /**
     * 等待数据超时
     */
    public static final int SO_TIME_OUT = 20 * 1000;
    /**
     * 读取缓冲大小
     */
    public static final int BYTE_BUF = 1024;
    /**
     * 数据类型
     */
    public static final String TYPE_JSON = "application/json";
    /**
     * 默认编码：UTF-8
     */
    public static final String ENCODE_DEFAULT = "UTF-8";
    /**
     * 下载资源临时文件夹
     */
    public static final String DOWNLOAD_TEMP_FOLDER = ".tmp";

    private static final String TAG = "HttpUtil";

    private static OkHttpClient sOkHttpClient;

    public static OkHttpClient getHttpClient() {
        if (sOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                    .readTimeout(SO_TIME_OUT, TimeUnit.MILLISECONDS);
//			HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//				@Override
//				public void log(String message) {
//					ILog.i(TAG, message);
//				}
//			});
//			loggingInterceptor.setLevel(ILog.isDebuggable() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
//			builder.addInterceptor(loggingInterceptor);
            sOkHttpClient = builder.build();
        }
        return sOkHttpClient;
    }

    /**
     * 请求网络接口
     *
     * @param request
     * @return
     */
    public static HttpResponse request(HttpRequest request) {
        if (!isNetworkAvailable()) {
            return new HttpResponse(HttpErrorCode.CODE_NO_NETWORK);
        }
        HttpResponse response = null;
        try {
            OkHttpClient httpClient = getHttpClient();
            Map<String, String> headers = request.getHeaders();
            Map<String, String> params = request.getRequestMap();
            String url = request.getUrl();
            ILog.i(TAG, "http--request url: " + url);
            final Request.Builder reqBuilder = new Request.Builder();
            // get httpdns url and host
//			url = QiniuDnsManager.getInstance().setHttpdnsUrl(url, reqBuilder);
            if (HttpRequest.UPLOAD.equals(request.getRequestMethod())) {
                List<File> list = request.getUploadFileList();
                if (list == null || list.isEmpty()) {
                    ILog.w(TAG, "没有需要上传的文件");
                    return new HttpResponse(HttpErrorCode.CODE_NO_NETWORK);
                }

                MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                for (int i = 0; i < list.size(); i++) {
                    File file = list.get(i);
                    String path = file.getAbsolutePath();
                    if (IFileUtil.isImage(file)) {
                        //最大不超过500KB
                        file = IImageUtil.imageCompressionSave(path, 500 * 1024);
                    }

                    bodyBuilder.addPart(RequestBody.create(MediaType.parse(IFileUtil.getMimeTypeFromExtension(
                            IFileUtil.getFileExtensionFromPath(path))), file));
                    ILog.i(TAG, "http--request-upload-file-:" + file.getAbsolutePath());
                }

                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }

                reqBuilder.url(url).post(bodyBuilder.build());
            } else if (HttpRequest.POST.equals(request.getRequestMethod())) {
                StringBuilder requestStr = new StringBuilder();
                FormBody.Builder formBuilder = new FormBody.Builder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    formBuilder.add(entry.getKey(), entry.getValue());

                    requestStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                reqBuilder.url(url).post(formBuilder.build());

                ILog.i(TAG, "http--request param: " + requestStr);
            } else {
                StringBuilder urlBuilder = new StringBuilder(url);
                urlBuilder.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    urlBuilder.append(entry.getKey())
                            .append("=")
                            .append(entry.getValue())
                            .append("&");
                }
                urlBuilder = urlBuilder.replace(urlBuilder.length() - 1, urlBuilder.length(), "");
                url = urlBuilder.toString();
                reqBuilder.url(url);
            }

            // TODO
            if (!TextUtils.isEmpty(request.getUserAgent())) {
                reqBuilder.removeHeader("User-Agent").addHeader("User-Agent", request.getUserAgent());
            }
            // 添加HTTP头参数
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    reqBuilder.addHeader(header.getKey(), header.getValue());
                }
            }

            Response res = httpClient.newCall(reqBuilder.build()).execute();
            int statusCode = res.code();
            // 读取内容
            if (request.getProcessResponseData()) {
                if (res.isSuccessful()) {
                    String responseBody = res.body().string();
                    response = new HttpResponse(res.headers(), responseBody);
                    ILog.printJson(TAG, responseBody, "http--responseBody:", request.getApiMethod());
                } else {
                    response = new HttpResponse(statusCode);
                    ILog.i(TAG, "http--response status code: " + statusCode);
                }
            } else {
                response = new HttpResponse(null);
            }
            response.statusCode = statusCode;
        } catch (Exception e) {
            e.printStackTrace();

            int errorCode = HttpErrorCode.CODE_FAILURE;
            if (e instanceof SocketTimeoutException) {
                errorCode = HttpErrorCode.CODE_TIMEOUT;
            } else if (e instanceof ConnectException) {
                errorCode = HttpErrorCode.CODE_NO_NETWORK;
            } else if (e instanceof UnknownHostException || e instanceof UnknownServiceException) {
                errorCode = HttpErrorCode.CODE_404;
            }
            String message = e.getClass().getCanonicalName() + "，" + (null != e.getMessage() ? e.getMessage() : "");
            response = new HttpResponse(errorCode);
            response.statusCode = errorCode;

            ILog.e(TAG, "http--response:" + message);
        }

        if (response == null) {
            response = new HttpResponse(null);
        }
        return response;
    }


    /**
     * 下载文件
     *
     * @param url      下载的url
     * @param fileDir  文件目录--此处需要用到临时目录
     * @param fileName 文件名
     * @return
     */
    public static boolean downloadFile(String url, String fileDir, String fileName) {
        boolean bool = false;
        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            String encodedUrl = Uri.encode(url, "@#&=*+-_.,:!?()/~'%");
            conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
            conn.setConnectTimeout(SO_TIME_OUT);
            conn.setReadTimeout(SO_TIME_OUT);

            int code = conn.getResponseCode();
            is = conn.getInputStream();
            if (code == HttpURLConnection.HTTP_OK && is != null) {
                IFileUtil.mkdirs(new File(fileDir));
                // 由于文件读写操作需要区分，这里先把文件下载到一个临时文件夹中，等下载好在移动至目标文件夹，读取的线程既可以找到下载好的文件
                String tempDir = fileDir + File.separator + DOWNLOAD_TEMP_FOLDER;
                File tempDirFile = new File(tempDir);
                IFileUtil.mkdirs(tempDirFile);

                String tempPath = tempDir + File.separator + fileName;
                ILog.i(TAG, "downloadFile:" + tempPath);

                os = new FileOutputStream(tempPath);
                byte[] buffer = new byte[32 * 1024];
                int readLen = 0;
                while ((readLen = is.read(buffer)) > 0) {
                    os.write(buffer, 0, readLen);
                }
                os.flush();
                File file = new File(tempPath);
                file.renameTo(new File(fileDir + "/" + fileName));
                bool = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            ILog.e(TAG, "downloadFile MalformedURLException: ", e);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            ILog.e(TAG, "downloadFile IOException: ", e);
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (null != is) {
                    is.close();
                }
                if (null != conn) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                ILog.e(TAG, "downloadFile IOException: ", e);
            }
        }
        return bool;
    }

    public static boolean isNetworkAvailable() {
        return INetworkUtils.getInstance().isNetworkAvailable();
    }

}
