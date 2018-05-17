package com.framework.http;

import com.framework.common.utils.ILog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 请求管理类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class ProtocolManager {

    private static final String TAG = "ProtocolManager";
    private ThreadPoolExecutor threadPool;
    private static ProtocolManager instance;
    // 下载线程池（下载文件时另开线程）
    private ExecutorService downExecutorService;

    /**
     * 控制是否允许联网
     */
    private static boolean isAppNetEnabled = true;

    public static void setAppNetEnabled(boolean enabled) {
        isAppNetEnabled = enabled;
    }

    public static ProtocolManager getInstance() {
        if (instance == null) {
            instance = new ProtocolManager();
        }
        return instance;
    }

    private ProtocolManager() {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public void submitRequest(HttpRequest request) {
        if (request == null) return;
        try {
            Future<?> furture = threadPool.submit(new ServiceTask(request));
            request.setFuture(furture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ServiceTask implements Runnable {
        private HttpRequest request;

        public ServiceTask(HttpRequest request) {
            this.request = request;
        }

        @Override
        public void run() {

            request.requestBefore();
            if (!request.needLoad) return;

            HttpResponse response = null;
            if (!isAppNetEnabled) {
                ILog.w(TAG, "app not use the network");
            } else {

                response = HttpUtil.request(request);
            }
            if (response == null) {
                response = new HttpResponse(null);
            }
            request.requestAfter(response);
        }
    }

    public boolean submitFileUploadRequest(HttpRequest request) {
        if (request == null)
            return false;
        try {
            Future<?> furture = threadPool.submit(new FileUploadServiceTask(request));
            request.setFuture(furture);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private class FileUploadServiceTask implements Runnable {
        private HttpRequest request;

        public FileUploadServiceTask(HttpRequest request) {
            this.request = request;
        }

        @Override
        public void run() {

            HttpResponse response = null;
            if (!isAppNetEnabled) {
                ILog.w(TAG, "app not use the network");
            } else {
                request.setRequestMethod(HttpRequest.UPLOAD);
                response = HttpUtil.request(request);
            }
            if (response == null) {
                response = new HttpResponse(null);
            }
            request.requestAfter(response);

        }
    }

    public boolean submitFileDownloadRequest(HttpRequest request) {
        if (request == null)
            return false;
        try {
            // 下载时启用三条线程池
            if (downExecutorService == null) {
                downExecutorService = Executors.newScheduledThreadPool(3);
            }

            Future<?> furture = downExecutorService.submit(new FileDownloadServiceTask(request));
            request.setFuture(furture);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private class FileDownloadServiceTask implements Runnable {
        private HttpRequest request;

        public FileDownloadServiceTask(HttpRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            if (!isAppNetEnabled) {
                ILog.w(TAG, "app not use the network");
                return;
            }

            boolean bool = HttpUtil.downloadFile(request.getUrl(), request.getDownloadDir(), request.getDownloadFileName());
            request.requestAfter(new HttpResponse(bool ? HttpErrorCode.CODE_SUCCESS : HttpErrorCode.CODE_FAILURE));
        }
    }

}
