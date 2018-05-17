package com.xiaolanba.passenger.module.common.download;

/**
 * apk升级的下载回调监听
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/13
 */

public interface OnDownloadListener {
    /**
     * 下载成功
     */
    void onDownloadSuccess(String absolutePath);

    /**
     * @param progress
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载失败
     */
    void onDownloadFailed();
}
