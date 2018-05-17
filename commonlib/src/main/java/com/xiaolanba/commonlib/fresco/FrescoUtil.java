package com.xiaolanba.commonlib.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.xiaolanba.commonlib.fresco.listener.ImageLoadingListener;

import java.io.File;
import java.util.Locale;
import java.util.UUID;

/**
 * Fresco的使用帮助类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public final class FrescoUtil extends ImageLoader{

    public static void init(Context context) {
        Fresco.initialize(context, ImageLoaderConfig.getInstance(context).getImagePipelineConfig());
        FLog.setMinimumLoggingLevel(FLog.ASSERT);
    }

    /**
     * 滑动时暂停加载图片
     * @return
     */
    public static PauseOnScrollListener getPauseOnScrollListener() {
        return new PauseOnScrollListener();
    }

    /**
     * 从内存缓存中移除指定图片的缓存
     *
     * @param uri
     */
    public static void evictFromMemoryCache(final Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        if (imagePipeline.isInBitmapMemoryCache(uri)) {
            imagePipeline.evictFromMemoryCache(uri);
        }
    }

    /**
     * 从磁盘缓存中移除指定图片的缓存
     *
     * @param uri
     */
    public static void evictFromDiskCache(final Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        if (imagePipeline.isInDiskCacheSync(uri)) {
            imagePipeline.evictFromDiskCache(uri);
        }
    }

    /**
     * 移除指定图片的所有缓存（包括内存+磁盘）
     *
     * @param uri
     */
    public static void evictFromCache(final Uri uri) {
        evictFromMemoryCache(uri);
        evictFromDiskCache(uri);
    }

    /**
     * 从内存缓存中移除指定图片的缓存
     *
     * @param url
     */
    public static void evictFromMemoryCache(final String url) {
        evictFromMemoryCache(imageUri(url));
    }

    /**
     * 从磁盘缓存中移除指定图片的缓存
     *
     * @param url
     */
    public static void evictFromDiskCache(final String url) {
        evictFromDiskCache(imageUri(url));
    }

    /**
     * 移除指定图片的所有缓存（包括内存+磁盘）
     *
     * @param url
     */
    public static void evictFromCache(final String url) {
        evictFromCache(imageUri(url));
    }

    public static Uri imageUri(String url) {
        Uri uri = Uri.parse(url);
        if (!UriUtil.isNetworkUri(uri)) {
            uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_FILE_SCHEME)
                    .path(url)
                    .build();
        }
        return uri;
    }

    /**
     * 清空所有内存缓存
     */
    public static void clearMemoryCaches() {
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    /**
     * 清空所有磁盘缓存，若你配置有两个磁盘缓存，则两个都会清除
     */
    public static void clearDiskCaches() {
        Fresco.getImagePipeline().clearDiskCaches();
    }

    /**
     * 清除所有缓存（包括内存+磁盘）
     */
    public static void clearCaches() {
        clearMemoryCaches();
        clearDiskCaches();
    }

    /**
     * 查找一张图片在已解码的缓存中是否存在
     *
     * @param url
     * @return
     */
    public static boolean isInBitmapMemoryCache(final String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return Fresco.getImagePipeline().isInBitmapMemoryCache(Uri.parse(url));
    }

    /**
     * 查找一张图片在磁盘缓存中是否存在，若配有两个磁盘缓存，则只要其中一个存在，就会返回true
     *
     * @param uri
     * @return
     */
    public static boolean isInDiskCacheSync(final Uri uri) {
        return isInDiskCacheSync(uri, ImageRequest.CacheChoice.SMALL) ||
                isInDiskCacheSync(uri, ImageRequest.CacheChoice.DEFAULT);
    }

    /**
     * 查找一张图片在磁盘缓存中是否存在，可以指定是哪个磁盘缓存
     *
     * @param uri
     * @param cacheChoice
     * @return
     */
    public static boolean isInDiskCacheSync(final Uri uri, final ImageRequest.CacheChoice cacheChoice) {
        return Fresco.getImagePipeline().isInDiskCacheSync(uri, cacheChoice);
    }

    /**
     * 需要暂停网络请求时调用
     */
    public static void pause() {
        Fresco.getImagePipeline().pause();
    }

    /**
     * 需要恢复网络请求时调用
     */
    public static void resume() {
        Fresco.getImagePipeline().resume();
    }

    /**
     * 当前网络请求是否处于暂停状态
     *
     * @return
     */
    public static boolean isPaused() {
        return Fresco.getImagePipeline().isPaused();
    }

    /**
     * 预加载到内存缓存并解码
     *
     * @param url
     */
    public static void prefetchToBitmapCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build();
        Fresco.getImagePipeline().prefetchToBitmapCache(imageRequest, null);
    }

    /**
     * 预加载到磁盘缓存（未解码）
     *
     * @param url
     */
    public static void prefetchToDiskCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build();
        Fresco.getImagePipeline().prefetchToDiskCache(imageRequest, null);
    }

    /**
     * 获取磁盘上主缓存文件缓存的大小
     *
     * @return
     */
    public static long getMainDiskStorageCacheSize() {
        Fresco.getImagePipelineFactory().getMainFileCache().trimToMinimum();
        return Fresco.getImagePipelineFactory().getMainFileCache().getSize();
    }

    /**
     * 获取磁盘上副缓存（小文件）文件缓存的大小
     *
     * @return
     */
    public static long getSmallDiskStorageCacheSize() {
        Fresco.getImagePipelineFactory().getSmallImageFileCache().trimToMinimum();
        return Fresco.getImagePipelineFactory().getSmallImageFileCache().getSize();
    }

    /**
     * 获取磁盘上缓存文件的大小
     *
     * @return
     */
    public static long getDiskStorageCacheSize() {
        return getMainDiskStorageCacheSize() + getSmallDiskStorageCacheSize();
    }

    /**
     * 从fresco的本地缓存拿到图片,注意文件的结束符并不是常见的.jpg,.png等，如果需要另存，可自行另存
     *
     * @param url
     */
    public static File getFileFromDiskCache(String url) {
        File localFile = null;
        if (!TextUtils.isEmpty(url)) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(url), null);
            if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageFileCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }

        if (localFile != null) {
//            ILog.i("local", "---file: " + localFile.getAbsolutePath()+"");
        }
        return localFile;
    }

    /**
     * 拷贝缓存文件,指定目标路径和文件名
     * @param url
     * @param dir
     * @param fileName
     * @return
     */
    public static boolean copyCacheFile(String url, File dir, String fileName) {
        File path = new File(dir, fileName);
        return copyCacheFile(url, path);
    }

    /**
     * 拷贝到某一个文件,已指定文件名
     * @param url  图片的完整url
     * @param path 目标文件路径
     * @return
     */
    public static boolean copyCacheFile(String url, File path) {
        if (path == null) {
            return false;
        }
        File file = getFileFromDiskCache(url);
        if (file == null) {
            return false;
        }

        if (path.isDirectory()) {
            throw new RuntimeException(path + "is a directory,you should call copyCacheFileToDir(String url,File dir)");
        }
        boolean isSuccess = file.renameTo(path);

        return isSuccess;
    }

    /**
     * 拷贝到某一个目录中,自动命名
     * @param url
     * @param dir
     * @return
     */
    public static File copyCacheFileToDir(String url, File dir) {
        if (dir == null) {
            return null;
        }
        if (!dir.isDirectory()) {
            throw new RuntimeException(dir + "is not a directory,you should call copyCacheFile(String url,File path)");
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = URLUtil.guessFileName(url, "", "");//android SDK 提供的方法.
        // 注意不能直接采用file的getName拿到文件名,因为缓存文件是用cacheKey命名的
        if (TextUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID().toString();
        }
        File newFile = new File(dir, fileName);

        boolean isSuccess = copyCacheFile(url, newFile);
        if (isSuccess) {
            return newFile;
        } else {
            return null;
        }
    }

    /**
     * 根据url获取缓存中的bitmap
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromCache(String url) {
        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchImageFromBitmapCache(imageRequest, CallerThreadExecutor.getInstance());
        try {
            CloseableReference<CloseableImage> imageReference = dataSource.getResult();
            if (imageReference != null) {
                try {
                    CloseableBitmap image = (CloseableBitmap) imageReference.get();
                    //TODO  do something with the image
                    Bitmap loadedImage = image.getUnderlyingBitmap();
                    if (loadedImage != null) {
                        return loadedImage;
                    } else {
                        return null;
                    }
                } finally {
                    CloseableReference.closeSafely(imageReference);
                }
            }
        } finally {
            dataSource.close();
        }
        return null;
    }



    public static Builder with(SimpleDraweeView simpleDraweeView, String url) {
        return new Builder().build(simpleDraweeView, url);
    }

    public static Builder with(Context context) {
        return new Builder().build(context);
    }

    public static Builder with(String url) {
        return new Builder().build(url);
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {

        private Context mContext;
        private SimpleDraweeView mSimpleDraweeView;

        private String mUrl;

        private int mWidth;
        private int mHeight;
        private float mAspectRatio;

        private boolean mNeedBlur;
        private boolean mSmallDiskCache;
        private boolean mAutoPlay = true;

        private BasePostprocessor mPostprocessor;
        private ControllerListener<ImageInfo> mControllerListener;

        private String downloadPath;
        private ImageLoadingListener loadingListener;

        public Builder build(String url) {
            this.mUrl = url;
            return this;
        }

        public Builder build(Context context) {
            this.mContext = context.getApplicationContext();
            return this;
        }

        public Builder build(SimpleDraweeView simpleDraweeView, String url) {
            this.mSimpleDraweeView = simpleDraweeView;
            this.mUrl = url;
            return this;
        }

        public Builder setSize(int reqWidth, int reqHeight) {
            this.mWidth = reqWidth;
            this.mHeight = reqHeight;
            return this;
        }

        public Builder setAspectRatio(float aspectRatio) {
            this.mAspectRatio = aspectRatio;
            return this;
        }

        public Builder setNeedBlur(boolean needBlur) {
            this.mNeedBlur = needBlur;
            return this;
        }

        public Builder setSmallDiskCache(boolean smallDiskCache) {
            this.mSmallDiskCache = smallDiskCache;
            return this;
        }

        public Builder setAutoPlay(boolean mAutoPlay) {
            this.mAutoPlay = mAutoPlay;
            return this;
        }

        public Builder setLoadingListener(ImageLoadingListener loadingListener) {
            this.loadingListener = loadingListener;
            return this;
        }

        public Builder setBasePostprocessor(BasePostprocessor postprocessor) {
            this.mPostprocessor = postprocessor;
            return this;
        }

        public Builder setControllerListener(ControllerListener<ImageInfo> controllerListener) {
            this.mControllerListener = controllerListener;
            return this;
        }

        public Builder setDownloadPath(String downloadPath) {
            this.downloadPath = downloadPath;
            return this;
        }

        public void download() {
            if (TextUtils.isEmpty(mUrl)
                    || !UriUtil.isNetworkUri(Uri.parse(mUrl))
                    || TextUtils.isEmpty(downloadPath)) {
                return;
            }

            ImageLoader.downloadImage(mContext, mUrl, downloadPath, loadingListener);
        }

        public void load() {
            load(mUrl);
        }

        public void load(String url) {
            if (TextUtils.isEmpty(url)) {
                return;
            }

            if (!mNeedBlur) {
                loadNormal(url);
            } else {
                loadBlur(url);
            }
        }

        public void load(int resId) {
            if (resId == 0 || mSimpleDraweeView == null) {
                return;
            }

            adjustLayoutParams();
            if (!mNeedBlur) {
                ImageLoader.loadDrawable(mSimpleDraweeView, resId, mWidth, mHeight);
            } else {
                ImageLoader.loadDrawableBlur(mSimpleDraweeView, resId, mWidth, mHeight);
            }
        }

        private void loadNormal(String url) {
            adjustLayoutParams();

            Uri uri = Uri.parse(url);

            if (!UriUtil.isNetworkUri(uri)) {
                uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_FILE_SCHEME)
                        .path(url)
                        .build();
            }

            ImageLoader.loadImage(mSimpleDraweeView, uri, mWidth, mHeight, mPostprocessor,
                    mControllerListener, null, mSmallDiskCache, mAutoPlay);
        }

        private void loadBlur(String url) {
            Uri uri = Uri.parse(url);
            adjustLayoutParams();
            if (UriUtil.isNetworkUri(uri)) {
                ImageLoader.loadImageBlur(mSimpleDraweeView, url, mWidth, mHeight);
            } else {
                ImageLoader.loadFileBlur(mSimpleDraweeView, url, mWidth, mHeight);
            }
        }

        private void adjustLayoutParams() {
            if (mSimpleDraweeView == null) {
                return;
            }

            if (mWidth > 0 && mHeight > 0) {
                ViewGroup.LayoutParams lvp = mSimpleDraweeView.getLayoutParams();
                lvp.width = mWidth;
                lvp.height = mHeight;
            } else if (mAspectRatio > 0 && (mWidth > 0 || mHeight > 0)) {
                ViewGroup.LayoutParams lvp = mSimpleDraweeView.getLayoutParams();
                if (mWidth > 0) {
                    lvp.width = mWidth;
                    lvp.height = (int) (mWidth / mAspectRatio);
                } else {
                    lvp.height = mHeight;
                    lvp.width = (int) (mHeight * mAspectRatio);
                }
            }
        }
    }

    /** Represents supported schemes(protocols) of URI. Provides convenient methods for work with schemes and URIs. */
    public enum Scheme {
        HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content"), ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        Scheme(String scheme) {
            this.scheme = scheme;
            uriPrefix = scheme + "://";
        }

        /**
         * Defines scheme of incoming URI
         *
         * @param uri URI for scheme detection
         * @return Scheme of incoming URI
         */
        public static Scheme ofUri(String uri) {
            if (uri != null) {
                for (Scheme s : values()) {
                    if (s.belongsTo(uri)) {
                        return s;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean belongsTo(String uri) {
            return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
        }

        /** Appends scheme to incoming path */
        public String wrap(String path) {
            return uriPrefix + path;
        }

        /** Removed scheme part ("scheme://") from incoming URI */
        public String crop(String uri) {
            if (!belongsTo(uri)) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
            }
            return uri.substring(uriPrefix.length());
        }
    }

}
