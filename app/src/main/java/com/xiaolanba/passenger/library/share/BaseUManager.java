package com.xiaolanba.passenger.library.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;

import com.framework.common.base.IBaseActivity;
import com.framework.common.base.IBaseFragmentActivity;
import com.framework.common.utils.IFileUtil;
import com.framework.common.utils.IImageUtil;
import com.framework.common.utils.IKeyboardUtils;
import com.framework.common.utils.ILog;
import com.framework.common.utils.IStringUtil;
import com.xiaolanba.commonlib.fresco.FrescoUtil;
import com.xlb.elect.cigar.R;
import com.xiaolanba.passenger.common.bean.OtherLogin;
import com.xiaolanba.passenger.logic.manager.CacheManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.System.currentTimeMillis;

/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class BaseUManager {
    // QQ
    public static final String QQAPPID = "101468649";
    public static final String QQAPPKEY = "a05d5ec8d87b17278205cdd5418211ff";
    // WEIXIN
    public static final String WXAPPID = "wx0f456f206fd83008";
    public static final String WXAPPSECRET = "cd1765516a76a048cc289b6983e7c47f";
    // SINA
    public static final String SINAAPPKEY = "1379750742";
    public static final String SINAAPPSECRET = "5c3459f498f8457d5389cc4078dc2955";
    // SINA CALL BACK
    public static final String SINA_CALLBACK_URL = "http://sns.whalecloud.com/sina2/callback";

    protected Activity activity;
    protected Handler handler = new Handler(Looper.getMainLooper());
    protected ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    protected UMLoginListener loginListener;
    protected UMShareListener shareListener;

    private int imageWidth = 280;
    private int imageHeight = 280;
    private int imageSize = 30;

    public void showToast(final String title) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (activity instanceof IBaseActivity) {
                    ((IBaseActivity) activity).showToast(title);
                } else if (activity instanceof IBaseFragmentActivity) {
                    ((IBaseFragmentActivity) activity).showToast(title);
                }
            }
        });
    }

    public void showProgressDialog() {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (activity instanceof IBaseActivity) {
                    ((IBaseActivity) activity).showProgressDialog();
                } else if (activity instanceof IBaseFragmentActivity) {
                    ((IBaseFragmentActivity) activity).showProgressDialog();
                }
            }
        });
    }

    public void dismissProgressDialog() {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (activity instanceof IBaseActivity) {
                    ((IBaseActivity) activity).dismissProgressDialog();
                } else if (activity instanceof IBaseFragmentActivity) {
                    ((IBaseFragmentActivity) activity).dismissProgressDialog();
                }
            }
        });
    }

    /**
     * 获取新浪分享内容
     *
     * @param title
     * @param content
     * @param contentUrl
     * @return
     */
    public String getSinaContent(String title, String content, String contentUrl) {
        try {
            int length = title.length() + contentUrl.length() + 2;
            int size = 140 - length;
            if (size > 0) {
                if (content.length() > size) {
                    content = content.substring(0, size);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title + "//" + content;
    }

    private boolean isPngUrl(Object imgUrl) {
        // TODO 暂时屏蔽掉png压缩， 使用png会导致图片压缩不到32k以下， 加此判断是为了解决png透明图片变黑问题引发，现不处理
        if (imgUrl != null && imgUrl instanceof String) {
            return imgUrl.toString().toLowerCase().endsWith(".png");
        }
        return false;
    }

    private boolean isPng(String imgUrl) {
        if (imgUrl != null) {
            return imgUrl.toLowerCase().endsWith(".png");
        }
        return false;
    }

    /**
     * for weixin
     * 获取图片数据
     *
     * @param context
     * @param imgUrl
     * @return
     */
    protected byte[] getThumbData(Context context, Object imgUrl) {
        return compressImage(getBitmap(context, imgUrl, true), imageSize, isPngUrl(imgUrl));
    }

    /**
     * for sina
     *
     * @param context
     * @param imgUrl
     * @return
     */
    protected byte[] getThumbDataForSina(Context context, Object imgUrl) {
        return compressImage(getBitmap(context, imgUrl, false), imageSize, isPngUrl(imgUrl));
    }

    /**
     * for qq
     * 获取图片文件
     *
     * @param context
     * @param imgUrl
     * @return
     */
    protected File getThumbFile(Context context, Object imgUrl) {
        File file = null;
        boolean isPng = isPngUrl(imgUrl);
        byte[] bytes = compressImage(getBitmap(context, imgUrl, true), imageSize, false, isPng);
        String imagePath = CacheManager.SD_IMAGE_SHARE_DIR + File.separator + "share_" + currentTimeMillis() + (isPng ? ".png" : ".jpg");
        File dir = new File(CacheManager.SD_IMAGE_SHARE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        IFileUtil.deleteDirectory(CacheManager.SD_IMAGE_SHARE_DIR, false);
        try {
            FileOutputStream fos = new FileOutputStream(new File(imagePath), false);
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
        }
        if (!IStringUtil.isEmpty(imagePath)) {
            file = new File(imagePath);
            if (file.exists() && file.length() > 0) {
                return file;
            }
        }
        return null;
    }

    protected Bitmap getBitmap(Context context, Object imgUrl, boolean isThumbnail) {
        long startTime = System.currentTimeMillis();
        int resId = R.drawable.logo;
        Bitmap bitmap = null;
        if (null != imgUrl) {
            if (imgUrl instanceof String) {
                String url = imgUrl.toString();
                ILog.d("--share- image:", url);
                //TODO 根据url，获取要分享的bitmap
                bitmap = getImageLoadBitmap(url, isThumbnail);
            } else if (imgUrl instanceof Integer) {
                resId = Integer.parseInt(imgUrl.toString());
                if (resId == 0) {
                    resId = R.drawable.logo;
                }
            } else if (imgUrl instanceof Bitmap) {
                bitmap = (Bitmap) imgUrl;
            }
        }

        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            bitmap = IImageUtil.readBitMap(context, resId);
        }

        int lastsize = IImageUtil.getBitmapsize(bitmap) / 1024;
        ILog.i("--share image size :-", "### bitmap size = " + lastsize + " KB");
        ILog.i("--share image size :-", "bitmap.getWidth():" + bitmap.getWidth() + "--bitmap.getHeight():" + bitmap.getHeight());

        if (isThumbnail) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            if (bitmapWidth > imageWidth || bitmapHeight > imageHeight) {
                int thumbWidth = Math.min(bitmapWidth, bitmapHeight);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, thumbWidth, thumbWidth, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                int tsize = IImageUtil.getBitmapsize(bitmap) / 1024;
                ILog.i("--share image size :-", "### thumb bitmap size = " + tsize + " KB");
                ILog.i("--share image size :-", "thumb bitmap.getWidth():" + bitmap.getWidth() + "--thumb bitmap.getHeight():" + bitmap.getHeight());
            }
        }
        long endTime = System.currentTimeMillis();
        ILog.i("getBitmap:time->", (endTime - startTime) + " ms");
        return bitmap;
    }

    private byte[] compressImage(Bitmap bitmap, int size, boolean isPng) {
        return compressImage(bitmap, size, true, isPng);
    }

    private byte[] compressImage(Bitmap bitmap, int size, boolean compressEnable, boolean isPng) {
        byte[] data = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            if (bitmap == null) {
                return baos.toByteArray();
            }
            bitmap.compress(isPng ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 100, baos);
            if (isPng && baos.toByteArray().length / 1024 > size) {
                // 如果是png 不能压缩 则转成jpg
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
            if (compressEnable) {
                int options = 100;
                while (baos.toByteArray().length / 1024 > size) {
                    if (options <= 0) {
                        break;
                    }
                    options -= 15;
                    ILog.i("compressImage:", baos.toByteArray().length / 1024 + " KB");
                    baos.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                }
            }
            data = baos.toByteArray();
            int tsize = data.length / 1024;
            ILog.i("--share image size :-", "### compress bitmap size = " + tsize + " KB");
            ILog.i("--share image size :-", "compress bitmap.getWidth():" + bitmap.getWidth() + "--compress bitmap.getHeight():" + bitmap.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private Bitmap getImageLoadBitmap(String url, boolean isThumb) {
        Bitmap object = FrescoUtil.getBitmapFromCache(url);
        if (object != null && object instanceof Bitmap) {
            Bitmap bitmap = (Bitmap) object;
            int size = IImageUtil.getBitmapsize(bitmap) / 1024;
            ILog.i("--share image size for ImageLoader:-", "###ImageLoader bitmap size = " + size + " KB");
            ILog.i("--share image size for ImageLoader:-", "ImageLoader bitmap.getWidth():" + bitmap.getWidth() + "--ImageLoader bitmap.getHeight():" + bitmap.getHeight());

            return bitmap;
        }
        return null;
    }

    /**
     * 平台类型
     *
     * @author xutingz
     * @company jxgis.com
     */
    public enum Platform {
        WEIXIN,
        WEIXIN_CIRCLE,
        QQ,
        QZONE,
        SINA,
    }

    public void setLoginListener(UMLoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setShareListener(UMShareListener shareListener) {
        this.shareListener = shareListener;
    }

    protected void onLoginFailure(final boolean isCancel) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (null != loginListener) {
                    loginListener.onUMFailure();
                }
                if (isCancel) {
                    //showToast("登录取消");
                } else {
                    showToast("登录失败");
                }
            }
        });
    }

    protected void onLoginStart() {
        if (null != loginListener) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loginListener.onUMStart();
                }
            });
        }
    }

    protected void onLoginSuccess(final OtherLogin otherLogin) {
        if (null != loginListener) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loginListener.onUMSuccess(otherLogin);
                }
            });
        }
    }

    protected void onShareStart(final Platform platform) {
        if (null != shareListener) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    shareListener.onUMShareStart(platform);
                }
            });
        }
    }

    protected void onShareSuccess(final Platform platform, final int shareType, final long bussinessId, final boolean isVideo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                IKeyboardUtils.closeKeybord(activity);
                if (null != shareListener) {
                    shareListener.onUMShareSuccess(platform);
                }
                showToast("分享成功");
                UShareManager.getInstance().shareReport(platform, shareType, bussinessId, isVideo);
            }
        });
    }

    protected void onShareFailure(final boolean isCancel) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                IKeyboardUtils.closeKeybord(activity);
                if (null != shareListener) {
                    shareListener.onUMShareFailure();
                }
                if (isCancel) {
                    //showToast("分享取消");
                } else {
                    showToast("分享失败");
                }
            }
        });
    }


    public interface UMLoginListener {

        void onUMSuccess(OtherLogin otherLogin);

        void onUMFailure();

        void onUMStart();
    }

    public interface UMShareListener {

        void onUMShareStart(Platform platform);

        void onUMShareFailure();

        void onUMShareSuccess(Platform platform);
    }
}
