package com.xiaolanba.passenger.module.common.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.xiaolanba.passenger.logic.manager.CacheManager;

/**
 * 下载升级apk的服务
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/13
 */

public class DownloadService extends Service {
    private final String TAG = "DownloadService";
    private DownloadUtil downloadUtil;
    private boolean hasSetListener = false;
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadUtil = DownloadUtil.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hasSetListener = false;
    }

    /**
     * 主界面和设置界面都有可能需要监听下载状态
     * @param url
     * @param apkVer
     */
    public void startDownload(String url,final String apkVer,OnDownloadListener downloadListener) {
        DownloadUtil.getInstance().setOnDownloadListener(downloadListener);
        hasSetListener = true;
        if(DownloadUtil.getInstance().hasDownloadingTask(apkVer)){
            return;
        }
        DownloadUtil.getInstance().download(url, CacheManager.SD_SAVE_DIR, apkVer);
    }

    public void setDownloadListener(OnDownloadListener listener){
        if (hasSetListener){
            return;
        }
        DownloadUtil.getInstance().setOnDownloadListener(listener);
    }

    public void removeDownloadListener(){
        DownloadUtil.getInstance().setOnDownloadListener(null);
        hasSetListener = false;
    }

}
