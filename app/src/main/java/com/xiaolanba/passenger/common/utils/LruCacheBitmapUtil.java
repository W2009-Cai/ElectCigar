package com.xiaolanba.passenger.common.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruCacheBitmapUtil {
    private static LruCacheBitmapUtil instance;

    public static synchronized LruCacheBitmapUtil getInstance() {
        if (null == instance) {
            instance = new LruCacheBitmapUtil();
        }
        return instance;
    }

    private LruCache<String, Bitmap> lruCache;

    LruCacheBitmapUtil() {
        long maxMemory = Runtime.getRuntime().maxMemory();//最大的内存
        int cacheMemory = (int) (maxMemory / 8);//取内存的1/8作为缓存(此处是以字节为最小单位)
        lruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            //true if the entry is being removed to make space, false if the removal was caused by a put(K, V) or remove(K).
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                oldValue.recycle();
            }
        };
    }

    public Bitmap getCache(String key) {
        return lruCache.get(key);
    }

    public void addCache(String key, Bitmap bitmap) {
        lruCache.put(key, bitmap);
    }

    public void removeCache(String key) {
        lruCache.remove(key);
    }

    public void removeAllCache() {
        lruCache.evictAll();
    }
}
