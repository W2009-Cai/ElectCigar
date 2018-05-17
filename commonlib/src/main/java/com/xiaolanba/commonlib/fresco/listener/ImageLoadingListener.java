package com.xiaolanba.commonlib.fresco.listener;

import android.view.View;

/**
 * @author xutingz
 * @company xiaolanba.com
 */
public interface ImageLoadingListener<T>{

    void onLoadingStarted(String imageUri, View view);

    void onLoadingFailed(String imageUri, View view);

    void onLoadingComplete(String imageUri, View view, T obj);

    void onProgressUpdate(String imageUri, View view, int progress);
}
