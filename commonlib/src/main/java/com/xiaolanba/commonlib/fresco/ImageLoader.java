package com.xiaolanba.commonlib.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.xiaolanba.commonlib.fresco.blur.BitmapBlurHelper;
import com.xiaolanba.commonlib.fresco.listener.ImageLoadingListener;
import com.xiaolanba.commonlib.fresco.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 提供基于Fresco的图片加载工具类
 * <p>
 * 在程序入口处添加下面代码，建议在Application的onCreate()方法内添加
 * Fresco.initialize(this, ImageLoaderConfig.getImagePipelineConfig(this));
 * <p>
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class ImageLoader {

    public static Uri getUri(String url) {
        if (url == null) {
            url = "";
        }
        Uri uri = Uri.parse(url);
        return uri;
    }

    public static Uri getFileUri(String filePath) {
        if (filePath == null) {
            filePath = "";
        }
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_FILE_SCHEME)
                .path(filePath)
                .build();
        return uri;
    }

    public static Uri getSourceUri(int resId) {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resId))
                .build();
        return uri;
    }

    public static Uri getAssetUri(String filename) {
        if (filename == null) {
            filename = "";
        }
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_ASSET_SCHEME)
                .path(filename)
                .build();
        return uri;
    }

    /*******************************************************************************************
     * 加载网络图片相关的方法                              *
     *******************************************************************************************/
    public static void loadImage(SimpleDraweeView simpleDraweeView, String url) {
        loadImage(simpleDraweeView, url, false);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView, String url, boolean autoPlay) {
        loadImage(simpleDraweeView, getUri(url), 0, 0, null, null, null, false, autoPlay);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView, String url, final int reqWidth, final int reqHeight) {
        loadImage(simpleDraweeView, getUri(url), reqWidth, reqHeight, null, null, null, false, false);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView, String url, BasePostprocessor processor) {
        loadImage(simpleDraweeView, getUri(url), 0, 0, processor, null, null, false, false);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView, String url,
                                 final int reqWidth, final int reqHeight, BasePostprocessor processor) {
        loadImage(simpleDraweeView, getUri(url), reqWidth, reqHeight, processor, null, null, false, false);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView, String url, ControllerListener<ImageInfo> controllerListener) {
        loadImage(simpleDraweeView, getUri(url), 0, 0, null, controllerListener, null, false, false);
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView, String url, BaseBitmapDataSubscriber mBitmapDataSubscriber) {
        loadImage(simpleDraweeView, getUri(url), 0, 0, null, null, mBitmapDataSubscriber, false, false);
    }


    public static void loadImageSmall(SimpleDraweeView simpleDraweeView, String url) {
        loadImage(simpleDraweeView, getUri(url), 0, 0, null, null, null, true, false);
    }

    public static void loadImageSmall(SimpleDraweeView simpleDraweeView, String url, final int reqWidth, final int reqHeight) {
        loadImage(simpleDraweeView, getUri(url), reqWidth, reqHeight, null, null, null, true, false);
    }

    public static void loadImageSmall(SimpleDraweeView simpleDraweeView, String url, BasePostprocessor processor) {
        loadImage(simpleDraweeView, getUri(url), 0, 0, processor, null, null, true, false);
    }

    public static void loadImageSmall(SimpleDraweeView simpleDraweeView, String url,
                                      final int reqWidth, final int reqHeight, BasePostprocessor processor) {
        loadImage(simpleDraweeView, getUri(url), reqWidth, reqHeight, processor, null, null, true, false);
    }

    /*******************************************************************************************
     * 加载本地文件相关的方法                              *
     *******************************************************************************************/

    public static void loadFile(final SimpleDraweeView simpleDraweeView, String filePath) {
        loadImage(simpleDraweeView, getFileUri(filePath), 0, 0, null, null, null, false, false);
    }

    public static void loadFile(final SimpleDraweeView simpleDraweeView, String filePath, boolean isAutoPlay) {
        loadImage(simpleDraweeView, getFileUri(filePath), 0, 0, null, null, null, false, isAutoPlay);
    }

    public static void loadFile(final SimpleDraweeView simpleDraweeView, String filePath, ControllerListener<ImageInfo> controllerListener) {
        loadImage(simpleDraweeView, getFileUri(filePath), 0, 0, null, controllerListener, null, false, false);
    }

    public static void loadFile(final SimpleDraweeView simpleDraweeView, String filePath, BaseBitmapDataSubscriber mBitmapDataSubscriber) {
        loadImage(simpleDraweeView, getFileUri(filePath), 0, 0, null, null, mBitmapDataSubscriber, false, false);
    }

    public static void loadFile(final SimpleDraweeView simpleDraweeView, String filePath, final int reqWidth, final int reqHeight) {
        BaseControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }

                ViewGroup.LayoutParams vp = simpleDraweeView.getLayoutParams();
                vp.width = reqWidth;
                vp.height = reqHeight;
                simpleDraweeView.requestLayout();
            }
        };
        loadImage(simpleDraweeView, getFileUri(filePath), reqWidth, reqHeight, null, controllerListener, null, false, false);
    }

    public static void loadFile(SimpleDraweeView simpleDraweeView, String filePath, BasePostprocessor processor) {
        loadImage(simpleDraweeView, getFileUri(filePath), 0, 0, processor, null, null, false, false);
    }

    public static void loadFile(SimpleDraweeView simpleDraweeView, String filePath,
                                final int reqWidth, final int reqHeight, BasePostprocessor processor, ControllerListener<ImageInfo> controllerListener) {
        loadImage(simpleDraweeView, getFileUri(filePath), reqWidth, reqHeight, processor, controllerListener, null, false, false);
    }

    /*******************************************************************************************
     * 加载本地res下面资源相关的方法                             *
     *******************************************************************************************/

    public static void loadDrawable(SimpleDraweeView simpleDraweeView, int resId) {
        loadImage(simpleDraweeView, getSourceUri(resId), 0, 0, null, null, null, false, false);
    }

    public static void loadDrawable(SimpleDraweeView simpleDraweeView, int resId,boolean isAutoPlay) {
        loadImage(simpleDraweeView, getSourceUri(resId), 0, 0, null, null, null, false, isAutoPlay);
    }

    public static void loadDrawable(SimpleDraweeView simpleDraweeView, int resId, final int reqWidth, final int reqHeight) {
        loadImage(simpleDraweeView, getSourceUri(resId), reqWidth, reqHeight, null, null, null, false, false);
    }

    public static void loadDrawable(SimpleDraweeView simpleDraweeView, int resId, BasePostprocessor processor) {
        loadImage(simpleDraweeView, getSourceUri(resId), 0, 0, processor, null, null, false, false);
    }

    public static void loadDrawable(SimpleDraweeView simpleDraweeView, int resId,
                                    final int reqWidth, final int reqHeight, BasePostprocessor processor) {
        loadImage(simpleDraweeView, getSourceUri(resId), reqWidth, reqHeight, processor, null, null, false, false);
    }

    /*******************************************************************************************
     * 加载本地asset下面资源相关的方法                             *
     *******************************************************************************************/

    public static void loadAssetDrawable(SimpleDraweeView simpleDraweeView, String filename) {
        loadImage(simpleDraweeView, getAssetUri(filename), 0, 0, null, null, null, false, false);
    }

    public static void loadAssetDrawable(SimpleDraweeView simpleDraweeView, String filename,boolean isAutoPlay) {
        loadImage(simpleDraweeView, getAssetUri(filename), 0, 0, null, null, null, false, isAutoPlay);
    }

    public static void loadAssetDrawable(SimpleDraweeView simpleDraweeView, String filename, final int reqWidth, final int reqHeight) {
        loadImage(simpleDraweeView, getAssetUri(filename), reqWidth, reqHeight, null, null, null, false, false);
    }

    public static void loadAssetDrawable(SimpleDraweeView simpleDraweeView, String filename, BasePostprocessor processor) {
        loadImage(simpleDraweeView, getAssetUri(filename), 0, 0, processor, null, null, false, false);
    }

    public static void loadAssetDrawable(SimpleDraweeView simpleDraweeView, String filename,
                                         final int reqWidth, final int reqHeight, BasePostprocessor processor) {
        loadImage(simpleDraweeView, getAssetUri(filename), reqWidth, reqHeight, processor, null, null, false, false);
    }

    /*******************************************************************************************
     *                                        高斯模糊相关的方法                                 *
     *******************************************************************************************/

    /**
     * 从网络加载图片，并对图片进行高斯模糊处理
     *
     * @param view View
     * @param url  URL
     */
    public static void loadImageBlur(final View view, String url) {
        loadImage(view.getContext(), url, new SimpleImageLoadingListener<Bitmap>() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap source) {
                Bitmap blurBitmap = BitmapBlurHelper.blur(view.getContext(), source);
                view.setBackgroundDrawable(new BitmapDrawable(view.getContext().getResources(), blurBitmap));
            }
        });
    }

    public static void loadImageBlur(final View view, String url, final int reqWidth, final int reqHeight) {
        loadImage(view.getContext(), url, reqWidth, reqHeight, new SimpleImageLoadingListener<Bitmap>() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap source) {
                Bitmap blurBitmap = BitmapBlurHelper.blur(view.getContext(), source);
                view.setBackgroundDrawable(new BitmapDrawable(view.getContext().getResources(), blurBitmap));
            }
        });
    }

    public static void loadImageBlur(final SimpleDraweeView draweeView, String url) {
        loadImage(draweeView, url, new BasePostprocessor() {
            @Override
            public String getName() {
                return "blurPostprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                BitmapBlurHelper.blur(bitmap, 35);
            }
        });
    }

    public static void loadImageBlur(final SimpleDraweeView draweeView, String url, final int reqWidth, final int reqHeight) {
        loadImage(draweeView, url, reqWidth, reqHeight, new BasePostprocessor() {
            @Override
            public String getName() {
                return "blurPostprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                BitmapBlurHelper.blur(bitmap, 35);
            }
        });
    }

    public static void loadFileBlur(final SimpleDraweeView draweeView, String filePath) {
        loadFile(draweeView, filePath, new BasePostprocessor() {
            @Override
            public String getName() {
                return "blurPostprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                BitmapBlurHelper.blur(bitmap, 35);
            }
        });
    }

    public static void loadFileBlur(final SimpleDraweeView draweeView, String filePath, final int reqWidth, final int reqHeight) {
        loadFile(draweeView, filePath, reqWidth, reqHeight, new BasePostprocessor() {
            @Override
            public String getName() {
                return "blurPostprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                BitmapBlurHelper.blur(bitmap, 35);
            }
        }, null);
    }

    public static void loadDrawableBlur(SimpleDraweeView simpleDraweeView, int resId, final int reqWidth, final int reqHeight) {
        loadDrawable(simpleDraweeView, resId, reqWidth, reqHeight, new BasePostprocessor() {
            @Override
            public String getName() {
                return "blurPostprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                BitmapBlurHelper.blur(bitmap, 35);
            }
        });
    }

    public static void loadDrawableBlur(SimpleDraweeView simpleDraweeView, int resId) {
        loadDrawable(simpleDraweeView, resId, new BasePostprocessor() {
            @Override
            public String getName() {
                return "blurPostprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                BitmapBlurHelper.blur(bitmap, 35);
            }
        });
    }

    public static void loadImage(SimpleDraweeView simpleDraweeView,
                                 Uri uri,
                                 final int reqWidth,
                                 final int reqHeight,
                                 BasePostprocessor postprocessor,
                                 ControllerListener<ImageInfo> controllerListener,
                                 BaseBitmapDataSubscriber mBitmapDataSubscriber,
                                 boolean isSmall,
                                 boolean autoPlay) {

        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        imageRequestBuilder.setRotationOptions(RotationOptions.autoRotate());
        if (!autoPlay) { //不需要自动播放gif的时候
            ImageDecodeOptions imageDecodeOptions = new ImageDecodeOptionsBuilder()
                    .setForceStaticImage(true)
                    .setBitmapConfig(Bitmap.Config.RGB_565).build();
            imageRequestBuilder.setImageDecodeOptions(imageDecodeOptions);
        }
        // 不支持图片渐进式加载，理由：https://github.com/facebook/fresco/issues/1204
        imageRequestBuilder.setProgressiveRenderingEnabled(false);

        if (isSmall) {
            imageRequestBuilder.setCacheChoice(ImageRequest.CacheChoice.SMALL);
        }

        if (reqWidth > 0 && reqHeight > 0) {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(reqWidth, reqHeight));
        }

        if (UriUtil.isLocalFileUri(uri)) {
            imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true);
        }

        if (postprocessor != null) {
            imageRequestBuilder.setPostprocessor(postprocessor);
        }

        ImageRequest imageRequest = imageRequestBuilder.build();

        PipelineDraweeControllerBuilder draweeControllerBuilder = Fresco.newDraweeControllerBuilder();
        draweeControllerBuilder.setOldController(simpleDraweeView.getController());
        draweeControllerBuilder.setImageRequest(imageRequest);

        if (controllerListener != null) {
            draweeControllerBuilder.setControllerListener(controllerListener);
        }

        if (mBitmapDataSubscriber != null) {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();

            DataSource<CloseableReference<CloseableImage>> dataSource =
                    imagePipeline.fetchDecodedImage(imageRequest, simpleDraweeView.getContext());
            dataSource.subscribe(mBitmapDataSubscriber, CallerThreadExecutor.getInstance());
        }

        draweeControllerBuilder.setTapToRetryEnabled(false); // 开启重试功能
        draweeControllerBuilder.setAutoPlayAnimations(autoPlay); // 自动播放gif动画
        DraweeController draweeController = draweeControllerBuilder.build();
        simpleDraweeView.setController(draweeController);
    }

    /**
     * 加载图片提供进度
     *
     * @param simpleDraweeView
     * @param url
     * @param listener
     */
    public static void loadImage(final SimpleDraweeView simpleDraweeView, final String url, final ImageLoadingListener listener) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return;
        }
        Uri uri = Uri.parse(url);

        if (listener != null) {
            listener.onLoadingStarted(url, null);
        }

        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
        imageRequestBuilder.setRotationOptions(RotationOptions.autoRotate());
        // 不支持图片渐进式加载，理由：https://github.com/facebook/fresco/issues/1204
        imageRequestBuilder.setProgressiveRenderingEnabled(false);

        if (UriUtil.isLocalFileUri(uri)) {
            imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true);
        }
        ImageRequest imageRequest = imageRequestBuilder.build();

        PipelineDraweeControllerBuilder draweeControllerBuilder = Fresco.newDraweeControllerBuilder();
        draweeControllerBuilder.setOldController(simpleDraweeView.getController());
        draweeControllerBuilder.setImageRequest(imageRequest);

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, simpleDraweeView.getContext());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {

            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }

            @Override
            public void onProgressUpdate(DataSource<CloseableReference<CloseableImage>> dataSource) {
                super.onProgressUpdate(dataSource);
                int progress = (int) (dataSource.getProgress() * 100);
                if (listener != null) {
                    listener.onProgressUpdate(url, null, progress);
                }
            }
        }, UiThreadImmediateExecutorService.getInstance());

        ControllerListener controllerListener = null;
        if (listener != null) {
            controllerListener = new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    if (listener != null) {
                        listener.onLoadingComplete(url, simpleDraweeView, imageInfo);
                    }
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    if (listener != null) {
                        listener.onLoadingFailed(url, simpleDraweeView);
                    }
                }
            };
        }
        if (controllerListener != null) {
            draweeControllerBuilder.setControllerListener(controllerListener);
        }

        draweeControllerBuilder.setTapToRetryEnabled(false); // 开启重试功能
        draweeControllerBuilder.setAutoPlayAnimations(true); // 自动播放gif动画
        DraweeController draweeController = draweeControllerBuilder.build();
        simpleDraweeView.setController(draweeController);
    }


    /**
     * 加载网络图片 提供进度
     * 加载gif 时simpleDraweeView 显示的第一帧， 点击加载gif时， 调用上面的方法会 显示灰色背景， 暂时用此方法
     *
     * @param simpleDraweeView
     * @param url
     * @param listener
     */
    public static void loadImageDelayed(final SimpleDraweeView simpleDraweeView,
                                        final String url, final ImageLoadingListener<ImageInfo> listener) {

        if (listener != null) {
            listener.onLoadingStarted(url, null);
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setRotationOptions(RotationOptions.autoRotate())
                .setProgressiveRenderingEnabled(false)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, null);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                loadImage(simpleDraweeView, url, new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        if (listener != null) {
                            listener.onLoadingComplete(url, simpleDraweeView, imageInfo);
                        }
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        if (listener != null) {
                            listener.onLoadingFailed(url, null);
                        }
                    }
                });
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                if (listener != null) {
                    listener.onLoadingFailed(url, null);
                }
            }

            @Override
            public void onProgressUpdate(DataSource dataSource) {
                super.onProgressUpdate(dataSource);
                int progress = (int) (dataSource.getProgress() * 100);
                if (listener != null) {
                    listener.onProgressUpdate(url, null, progress);
                }
            }
        }, UiThreadImmediateExecutorService.getInstance());

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .build();
        controller.onClick();
    }


    public static void loadImage(Context context, String url, final ImageLoadingListener<Bitmap> listener) {
        loadOriginalImage(context, url, listener, UiThreadImmediateExecutorService.getInstance());
    }

    /**
     * 根据提供的图片URL加载原始图（该方法仅针对大小在100k以内的图片，若不确定图片大小，
     * 请使用下面的downloadImage(String url, final DownloadImageResult loadFileResult) ）
     *
     * @param url      图片URL
     * @param listener LoadImageResult
     */
    public static void loadOriginalImage(Context context, String url, final ImageLoadingListener<Bitmap> listener) {
        loadOriginalImage(context, url, listener, Executors.newSingleThreadExecutor());
    }

    /**
     * 根据提供的图片URL加载原始图（该方法仅针对大小在100k以内的图片，若不确定图片大小，
     * 请使用下面的downloadImage(String url, final DownloadImageResult loadFileResult) ）
     *
     * @param url      图片URL
     * @param listener LoadImageResult
     * @param executor 的取值有以下三个：
     *                 UiThreadImmediateExecutorService.getInstance() 在回调中进行任何UI操作
     *                 CallerThreadExecutor.getInstance() 在回调里面做的事情比较少，并且不涉及UI
     *                 Executors.newSingleThreadExecutor() 你需要做一些比较复杂、耗时的操作，并且不涉及UI（如数据库读写、文件IO），你就不能用上面两个Executor。
     *                 你需要开启一个后台Executor，可以参考DefaultExecutorSupplier.forBackgroundTasks。
     */
    public static void loadOriginalImage(Context context, final String url, final ImageLoadingListener<Bitmap> listener, Executor executor) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest imageRequest = builder.build();
        // 获取已解码的图片，返回的是Bitmap
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<CloseableBitmap>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }

                CloseableReference<CloseableBitmap> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    final CloseableReference<CloseableBitmap> closeableReference = imageReference.clone();
                    try {
                        CloseableBitmap closeableBitmap = closeableReference.get();
                        Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
                        if (bitmap != null && !bitmap.isRecycled()) {
                            // https://github.com/facebook/fresco/issues/648
                            final Bitmap tempBitmap = bitmap.copy(bitmap.getConfig(), false);
                            if (listener != null) {
                                listener.onLoadingComplete(url, null, tempBitmap);
                            }
                        }
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Throwable throwable = dataSource.getFailureCause();
                if (throwable != null) {
                    FLog.e("ImageLoader", "onFailureImpl = " + throwable.toString());
                }
                if (listener != null) {
                    listener.onLoadingFailed(url, null);
                }
            }
        };
        dataSource.subscribe(dataSubscriber, executor);
    }

    /**
     * 下载图片到本地缓存
     *
     * @param url
     * @param listener
     */
    public static void loadImage(final String url, final ImageLoadingListener listener) {
        if (listener != null) {
            listener.onLoadingStarted(url, null);
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setRotationOptions(RotationOptions.autoRotate())
                .setProgressiveRenderingEnabled(false)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, null);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                if (listener != null) {
                    listener.onLoadingComplete(url, null, bitmap);
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                if (listener != null) {
                    listener.onLoadingFailed(url, null);
                }
            }

            @Override
            public void onProgressUpdate(DataSource dataSource) {
                super.onProgressUpdate(dataSource);
                int progress = (int) (dataSource.getProgress() * 100);
                if (listener != null) {
                    listener.onProgressUpdate(url, null, progress);
                }
            }
        }, UiThreadImmediateExecutorService.getInstance());

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .build();
        controller.onClick();
    }

    /**
     * 从网络下载图片
     * 1、根据提供的图片URL，获取图片数据流
     * 2、将得到的数据流写入指定路径的本地文件
     *
     * @param url      URL
     * @param listener LoadFileResult
     */
    public static void downloadImage(Context context, final String url, final String path, final ImageLoadingListener<String> listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest imageRequest = builder.build();

        // 获取未解码的图片数据
        DataSource<CloseableReference<PooledByteBuffer>> dataSource = imagePipeline.fetchEncodedImage(imageRequest, context);
        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<PooledByteBuffer>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                if (!dataSource.isFinished() || TextUtils.isEmpty(path)) {
                    return;
                }

                CloseableReference<PooledByteBuffer> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    final CloseableReference<PooledByteBuffer> closeableReference = imageReference.clone();
                    try {
                        PooledByteBuffer pooledByteBuffer = closeableReference.get();
                        InputStream inputStream = new PooledByteBufferInputStream(pooledByteBuffer);

                        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1) {
                            outSteam.write(buffer, 0, len);
                        }
                        outSteam.flush();
                        outSteam.close();
                        inputStream.close();
                        byte[] data = outSteam.toByteArray();

                        FileOutputStream fileOutputStream = new FileOutputStream(path);
                        fileOutputStream.write(data);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        if (listener != null) {
                            listener.onLoadingComplete(url, null, path);
                        }
                    } catch (IOException e) {
                        if (listener != null) {
                            listener.onLoadingFailed(url, null);
                        }
                        e.printStackTrace();
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onProgressUpdate(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                int progress = (int) (dataSource.getProgress() * 100);
                if (listener != null) {
                    listener.onProgressUpdate(url, null, progress);
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                if (listener != null) {
                    listener.onLoadingFailed(url, null);
                }

                Throwable throwable = dataSource.getFailureCause();
                if (throwable != null) {
                    FLog.e("ImageLoader", "onFailureImpl = " + throwable.toString());
                }
            }
        }, Executors.newSingleThreadExecutor());
    }

    /**
     * 从本地文件或网络获取Bitmap
     *
     * @param context
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @param listener
     */
    public static void loadImage(Context context, final String url, final int reqWidth, final int reqHeight, final ImageLoadingListener<Bitmap> listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Uri uri = Uri.parse(url);
        if (!UriUtil.isNetworkUri(uri)) {
            uri = getFileUri(url);
        }

        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (reqWidth > 0 && reqHeight > 0) {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(reqWidth, reqHeight));
        }
        ImageRequest imageRequest = imageRequestBuilder.build();

        // 获取已解码的图片，返回的是Bitmap
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<CloseableBitmap>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }

                CloseableReference<CloseableBitmap> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    final CloseableReference<CloseableBitmap> closeableReference = imageReference.clone();
                    try {
                        CloseableBitmap closeableBitmap = closeableReference.get();
                        Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
                        if (bitmap != null && !bitmap.isRecycled()) {
                            final Bitmap tempBitmap = bitmap.copy(bitmap.getConfig(), false);
                            if (listener != null) {
                                listener.onLoadingComplete(url, null, tempBitmap);
                            }
                        }
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Throwable throwable = dataSource.getFailureCause();
                if (throwable != null) {
                    FLog.e("ImageLoader", "onFailureImpl = " + throwable.toString());
                }
                if (listener != null) {
                    listener.onLoadingFailed(url, null);
                }
            }
        };
        dataSource.subscribe(dataSubscriber, UiThreadImmediateExecutorService.getInstance());
    }
}
