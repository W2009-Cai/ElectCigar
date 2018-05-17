package com.xiaolanba.commonlib.fresco;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xutingz
 * @company xiaolanba.com
 */

public class ImageLoaderConfig {

    protected static final String IMAGE_PIPELINE_CACHE_DIR = "image_cache";

    protected static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "image_small_cache";

    protected Context mContext;
    protected ImagePipelineConfig mImagePipelineConfig;

    private static ImageLoaderConfig sImageLoaderConfig;

    protected ImageLoaderConfig(Context context) {
        mContext = context.getApplicationContext();
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
    }

    public static ImageLoaderConfig getInstance(Context context) {
        if(sImageLoaderConfig == null) {
            synchronized (ImageLoaderConfig.class) {
                if(sImageLoaderConfig == null) {
                    sImageLoaderConfig = new ImageLoaderConfig(context);
                }
            }
        }
        return sImageLoaderConfig;
    }

    /**
     * Creates config using android http stack as network backend.
     */
    public ImagePipelineConfig getImagePipelineConfig() {
        if (mImagePipelineConfig == null) {
            mImagePipelineConfig = ImagePipelineConfig.newBuilder(mContext)
                    .setBitmapsConfig(Bitmap.Config.RGB_565) // 若不是要求忒高清显示应用，就用使用RGB_565吧（默认是ARGB_8888)
                    .setDownsampleEnabled(true) // 在解码时改变图片的大小，支持PNG、JPG以及WEBP格式的图片，与ResizeOptions配合使用
                    // 设置Jpeg格式的图片支持渐进式显示
//                    .setProgressiveJpegConfig(new ProgressiveJpegConfig() {
//                        @Override
//                        public int getNextScanNumberToDecode(int scanNumber) {
//                            return scanNumber + 2;
//                        }
//
//                        public QualityInfo getQualityInfo(int scanNumber) {
//                            boolean isGoodEnough = (scanNumber >= 5);
//                            return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
//                        }
//                    })
                    .setRequestListeners(getRequestListeners())
                    .setMemoryTrimmableRegistry(getMemoryTrimmableRegistry()) // 报内存警告时的监听
                    // 设置内存配置
                    .setBitmapMemoryCacheParamsSupplier(new BitmapMemoryCacheParamsSupplier(
                            (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)))
                    .setMainDiskCacheConfig(getMainDiskCacheConfig()) // 设置主磁盘配置
                    .setSmallImageDiskCacheConfig(getSmallDiskCacheConfig()) // 设置小图的磁盘配置
                    .build();
        }
        return mImagePipelineConfig;
    }

    /**
     * 当内存紧张时采取的措施
     * @return
     */
    protected MemoryTrimmableRegistry getMemoryTrimmableRegistry() {
        MemoryTrimmableRegistry memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
        memoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                FLog.i(getClass(), "Fresco onCreate suggestedTrimRatio = " + suggestedTrimRatio);

                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                        ) {
                    // 清除内存缓存
                    Fresco.getImagePipeline().clearMemoryCaches();
                }
            }
        });
        return memoryTrimmableRegistry;
    }

    /**
     * 设置网络请求监听
     * @return
     */
    protected Set<RequestListener> getRequestListeners() {
        Set<RequestListener> requestListeners = new HashSet<>();
//        requestListeners.add(new RequestLoggingListener());
        return requestListeners;
    }

    /**
     * 获取主磁盘配置
     * @return
     */
    protected DiskCacheConfig getMainDiskCacheConfig() {
        return DiskCacheConfig.newBuilder(mContext)
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                .setBaseDirectoryPath(getCacheDir())
                .setMaxCacheSize(100 * ByteConstants.MB)
                .setMaxCacheSizeOnLowDiskSpace(50 * ByteConstants.MB)
                .setMaxCacheSizeOnVeryLowDiskSpace(30 * ByteConstants.MB)
                .build();
    }

    /**
     * 获取小图的磁盘配置（辅助）
     * @return
     */
    protected DiskCacheConfig getSmallDiskCacheConfig() {
        return DiskCacheConfig.newBuilder(mContext)
                .setBaseDirectoryPath(getCacheDir())
                .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)
                .setMaxCacheSize(10 * ByteConstants.MB)
                .setMaxCacheSizeOnLowDiskSpace(5 * ByteConstants.MB)
                .build();
    }

    /**
     * 获取缓存目录
     * @return
     */
    protected File getCacheDir(){
        File fileCacheDir = mContext.getCacheDir();
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                fileCacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fresco");
//            }
        return fileCacheDir;
    }
}
