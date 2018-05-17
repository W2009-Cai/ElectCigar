package com.xiaolanba.commonlib.fresco.listener;

import android.view.View;

/**
 * @author xutingz
 * @company xiaolanba.com
 */

public class SimpleImageLoadingListener<T> implements ImageLoadingListener<T> {

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, T obj) {

    }

    @Override
    public void onProgressUpdate(String imageUri, View view, int progress) {

    }
}
