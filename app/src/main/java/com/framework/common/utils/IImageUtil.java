package com.framework.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.framework.common.base.IBaseActivity;
import com.framework.common.base.IBaseFragmentActivity;
import com.framework.manager.ICacheManager;
import com.xiaolanba.passenger.logic.control.LBController;
import com.xiaolanba.passenger.logic.manager.CacheManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图片操作工具类
 *
 * @author xutingz
 */
public class IImageUtil {
    public static final String TAG = "IImageUtil";
    /**
     * 剪切头像大小
     */
    public static final int HEAD_SIZE = 320;
    public static final int ACTIVE_SIZE = 800;

    public static class CropOutParam implements Serializable {
        public int aspectX;
        public int aspectY;
        public int outputX;
        public int outputY;

        public CropOutParam(int aspectX, int aspectY, int outputX, int outputY) {
            this.aspectX = aspectX;
            this.aspectY = aspectY;
            this.outputX = outputX;
            this.outputY = outputY;
        }
    }

    /**
     * 获取头像裁剪参数
     *
     * @return
     */
    public static CropOutParam getHeadCropParam() {
        return new CropOutParam(1, 1, HEAD_SIZE, HEAD_SIZE);
    }

    public static CropOutParam getCropParam(int aspectX, int aspectY) {
        return new CropOutParam(aspectX, aspectY, ACTIVE_SIZE, ACTIVE_SIZE / aspectX * aspectY);
    }

    /**
     * 图片裁剪成正方形
     *
     * @param path
     * @param context
     */
    public static Uri startPhotoCrop(String path, CropOutParam cropParam, Activity context, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
//		intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
        Uri uri = getSourceUri(path);
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", cropParam == null ? 1 : cropParam.aspectX);
        intent.putExtra("aspectY", cropParam == null ? 1 : cropParam.aspectY);

        calculCropOutParam(uri, cropParam);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", cropParam == null ? HEAD_SIZE : cropParam.outputX);
        intent.putExtra("outputY", cropParam == null ? HEAD_SIZE : cropParam.outputY);

        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边

        context.startActivityForResult(intent, requestCode);

        return uri;
    }

    /**
     * 选图时候裁剪
     *
     * @param path
     * @param context
     * @param requestCode
     * @param uri
     */
    public static void startPhotoCrop(String path, final CropOutParam cropParam, final Activity context, final int requestCode, final Uri uri) {
        final Uri pathUri = getSourceUri(path);
        final File file = new File(path);
        if (file.exists()) {
            long length = file.length();
            ILog.i(TAG, "---startPhotoCrop file length=" + length);
            if (length == 0) {
                // 为了解决魅族手机,length=0时裁剪图片提示"无法保存经过裁剪的图片"
                String brandmanu = "" + android.os.Build.BRAND + "," + android.os.Build.MANUFACTURER;
                if (brandmanu.toLowerCase().contains("meizu")) {
                    if (context instanceof IBaseActivity) {
                        ((IBaseActivity) context).showProgressDialog();
                    } else if (context instanceof IBaseFragmentActivity) {
                        ((IBaseFragmentActivity) context).showProgressDialog();
                    }
                    new Thread() {//启动线程轮循，直到length不为0
                        @Override
                        public void run() {
                            super.run();
                            long start = System.currentTimeMillis();
                            long fileLength = 0;
                            while ((fileLength = file.length()) == 0) {
                                long current = System.currentTimeMillis();
                                if (start - current > 10000) {
                                    break;
                                }
                                try {
                                    Thread.sleep(400);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            ILog.i(TAG, "---startPhotoCrop file length=" + fileLength);
                            if (fileLength != 0) {//回调到主线程去裁剪
                                LBController.MainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (context != null && !context.isFinishing()) {
                                            if (context instanceof IBaseActivity) {
                                                ((IBaseActivity) context).dismissProgressDialog();
                                            } else if (context instanceof IBaseFragmentActivity) {
                                                ((IBaseFragmentActivity) context).dismissProgressDialog();
                                            }
                                            startCropDelay(pathUri, cropParam, context, requestCode, uri);
                                        }
                                    }
                                });
                            }
                        }
                    }.start();
                } else {//立即截图
                    startCropDelay(pathUri, cropParam, context, requestCode, uri);
                }
            } else {//立即截图
                startCropDelay(pathUri, cropParam, context, requestCode, uri);
            }
        }

    }

    public static void startCropDelay(Uri pathUri, final CropOutParam cropParam, final Activity context, final int requestCode, final Uri uri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(pathUri, "image/*");
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", "true");

            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", cropParam == null ? 1 : cropParam.aspectX);
            intent.putExtra("aspectY", cropParam == null ? 1 : cropParam.aspectY);

            calculCropOutParam(pathUri, cropParam);
            // outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputX", cropParam == null ? HEAD_SIZE : cropParam.outputX);
            intent.putExtra("outputY", cropParam == null ? HEAD_SIZE : cropParam.outputY);

            intent.putExtra("return-data", false);
            intent.putExtra("scale", true);// 去黑边
            intent.putExtra("scaleUpIfNeeded", true);// 去黑边
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true); // no face detection

            context.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取裁剪输出大小
     *
     * @param path
     * @param size
     * @return
     */
    public static int getCropSize(String path, int size) {
        if (IFileUtil.isImage(path)) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            int width = options.outWidth;
            int height = options.outHeight;

            if (width < size && width != 0) {
                size = width;
                if (height < width) {
                    size = height;
                }
            }
        }
        return size;
    }

    public static void calculCropOutParam(Uri uri, CropOutParam cropOutParam) {
        if (uri == null) {
            return;
        }
        String path = uri.getPath();
        if (cropOutParam != null && IFileUtil.isImage(path)) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int width = options.outWidth;
            int height = options.outHeight;
            if (width == 0 || height == 0) {
                return;
            }

            if (cropOutParam.outputX > width || cropOutParam.outputY > height) {
                int tempwidth = width;
                int tempheight = tempwidth / cropOutParam.aspectX * cropOutParam.aspectY;
                if (tempheight > height) {
                    tempheight = height;
                    tempwidth = tempheight * cropOutParam.aspectX / cropOutParam.aspectY;
                }
                cropOutParam.outputX = tempwidth;
                cropOutParam.outputY = tempheight;
            }
        }
    }

    public static Uri getSourceUri(String path) {
//		return compressImageFile(Uri.parse("file://"+path), generateUri(CacheManager.SD_IMAGE_COMPRESS_DIR));
        return compressImageFile(Uri.fromFile(new File(path)), generateUri(CacheManager.SD_IMAGE_COMPRESS_DIR));
    }

    public static Uri generateUri(String dirPath) {
        File cacheFolder = new File(dirPath);
        if (!cacheFolder.exists()) {
            cacheFolder.mkdir();
        }
        String name = String.format("image-%d", IDateFormatUtil.getTimeMillis()) + "_" + String.valueOf(IRandomUtil.getRandom(20)) + ".jpg";
        return Uri
                .fromFile(cacheFolder)
                .buildUpon()
                .appendPath(name)
                .build();
    }

    /**
     * 将图片放大到手机屏幕宽
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static Bitmap toScreenSize(Context context, Bitmap bitmap) {
        if (null != bitmap) {
            try {
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                float sx = ((float) IDisplayUtil.getScreenWidth(context)) / w;
                Matrix matrix = new Matrix();
                matrix.postScale(sx, sx); //
                return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 生成图片缩略图
     *
     * @param mContext
     * @param resId
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getImageThumbnail(Context mContext, int resId, int width, int height) {
        return getImageThumbnail(BitmapFactory.decodeResource(mContext.getResources(), resId), width, height);
    }

    /**
     * 生成图片缩略图
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getImageThumbnail(Bitmap bitmap, int width, int height) {
        FileInputStream is = null;
        try {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获取颜色值
     *
     * @param resId
     * @return
     */
    public static int getColor(Context mContext, int resId) {
        return mContext.getResources().getColor(resId);
    }

    /**
     * TextView的点击效果
     *
     * @param resId
     * @return
     */
    public static ColorStateList getColorStateList(Context mContext, int resId) {
        Resources resource = (Resources) mContext.getResources();

        return resource.getColorStateList(resId);

    }

    /**
     * 是否为gif图片
     *
     * @param url
     * @return
     */
    public static boolean isGif(String url) {
        if (!IStringUtil.isEmpty(url)) {
            String fileName = IFileUtil.getFileNameFromUrl(url, ".jpg");
            if (!IStringUtil.isEmpty(fileName)) {
                return fileName.toLowerCase().endsWith(".gif");
            }
        }
        return false;
    }

    public static boolean isGif(InputStream imageStream) {
        if (imageStream != null) {
            byte[] b = new byte[3];
            try {
                imageStream.read(b);
                if (b[0] == (byte) 'G' && b[1] == (byte) 'I' && b[2] == (byte) 'F') {
                    return true;
                }
            } catch (Exception e) {

            }
        }
        return false;
    }

    /**
     * 将View 转换为Bitmap
     */
    public static Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }

    /**
     * 圆角处理
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = bitmap;
        try {
            output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return output;
    }

    /**
     * 处理成圆形
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = getImageThumbnail(bitmap, 80, 80);
        return toRoundCorner(output, 50);
    }

    /**
     * Drawable 转换成 Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将bitmap保存为图片
     *
     * @param photo
     * @param spath
     */
    public static String saveImage(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return spath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取系统默认存储真实文件路径
     * 两个路径 一个是最后一张所拍图片路径  (由于设置了存储路径所以DCIM中会存储两张一模一样的图片，所以在此获取两张图片路径以便于缩放处理后全部删除)
     *
     * @param context
     * @return
     */
    public static String getRealFilePath(Context context) {
        String filePath = "";
        try {
//			Cursor cursor = context.getContentResolver().query(
//					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
//					null, Media.DATE_MODIFIED + " desc");

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                    MediaStore.Images.Media.MIME_TYPE + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media._ID + " DESC");

            if (cursor != null) {
                if (cursor.moveToNext()) {
                    filePath = cursor.getString(cursor.getColumnIndex(Media.DATA));
                }
                cursor.close();
            }

        } catch (Exception e) {

        }

        return filePath;
    }

    /**
     * 在指定目录下创建图片文件
     *
     * @param dir
     * @return
     */
    public static File createImageFile(File dir) {

        try {
            File imageF = File.createTempFile("IMG" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), ".jpg", dir);
            return imageF;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 针对本地图片裁剪 图片压缩控制
     *
     * @param originUri
     * @param compressUri
     * @return
     */
    public static Uri compressImageFile(Uri originUri, Uri compressUri) {
        Bitmap bitmap = null;
        FileOutputStream fOut = null;
        try {
            String path = originUri.getPath();
            File file = new File(path);
            if (file.exists()) {
                long length = file.length();

                int maxImageSize = 1 * 1024 * 1024;
//				int minImageSize = 1 * 1024 * 1024;
                // 暂时只处理大于4M的图
                if (length > maxImageSize) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(originUri.getPath(), options);
                    options.inSampleSize = computeSampleSize(options, 640, 854);
                    options.inJustDecodeBounds = false;
                    options.inDither = false;
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    bitmap = BitmapFactory.decodeFile(originUri.getPath(), options);

                    File newFile = new File(compressUri.getPath());
                    fOut = new FileOutputStream(newFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
                    fOut.flush();

                    return compressUri;

                }
//				else if (length  > minImageSize) {
//					bitmap = BitmapFactory.decodeFile(originUri.getPath());
//
//					File newFile = new File(compressUri.getPath());
//					fOut = new FileOutputStream(newFile);
//					bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
//					fOut.flush();
//
//					return compressUri;
//				}
            }

        } catch (Exception e) {

        } finally {
            if (bitmap != null)
                bitmap.recycle();
            try {
                if (fOut != null)
                    fOut.close();
            } catch (IOException ignore) {
            }
        }
        return originUri;
    }

    /**
     * 文件压缩保存
     *
     * @param filepath
     * @return
     */
    public static File imageCompressionSave(String filepath, int maxSize) {
        if (TextUtils.isEmpty(filepath)) {
            return null;
        }
        File souceFile = new File(filepath);
        if (IImageUtil.isGif(filepath)) {
            return souceFile;
        }

        File dir = new File(ICacheManager.SD_IMAGE_COMPRESS_DIR);
        if (!dir.exists()) {
            IFileUtil.mkdirs(dir);
        }

        String newFilePath = ICacheManager.SD_IMAGE_COMPRESS_DIR + File.separator + IFileUtil.getFileName(filepath);
        if (souceFile.exists()) {
            long fileLength = souceFile.length();
            ILog.i(TAG, "fileLength:" + fileLength / 1024 + " KB");
            // 拍照上传时， 有些手机立马点击上传， 获取到的图片size 为0，图片形成要几秒的时间
            // 小米4
            if (fileLength == 0) {
                long start = System.currentTimeMillis();
                while ((fileLength = souceFile.length()) == 0) {
                    long current = System.currentTimeMillis();
                    if (start - current > 10000) {
                        break;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {

                    }
                }
            }

            if (fileLength < maxSize) {
                return souceFile;
            } else {
                FileOutputStream fos = null;
                try {
//					BitmapFactory.Options options = new BitmapFactory.Options();
//					options.inSampleSize = 1;
//					options.inJustDecodeBounds = true;
//					BitmapFactory.decodeFile(filepath, options);
//					// 480 * 800
//					options.inSampleSize = computeSampleSize(options, 480, 800);
//					options.inJustDecodeBounds = false;
//					options.inDither = false;
//					options.inPurgeable = true;
//					options.inInputShareable = true;
//					options.inPreferredConfig = Bitmap.Config.RGB_565;
//					Bitmap sourceBitmap = BitmapFactory.decodeFile(filepath, options);
//
//					byte[] bytes = compressBitmap(sourceBitmap, 100,  Math.min(maxSize, (int) fileLength), isPngUrl(filepath));
//					if (null != bytes) {
//						fos = new FileOutputStream(new File(newFilePath), false);
//						fos.write(bytes);
//						fos.flush();
//					}

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filepath, options);
                    int width = options.outWidth;
                    int height = options.outHeight;
                    if (width > 0 && height > 0 && height / width >= 3 && height > 3000) {
                        // 长图上传时处理大小
                        options.inSampleSize = width < 480 ? 1 : 2;
                        options.inJustDecodeBounds = false;
                        options.inDither = false;
                        options.inPurgeable = true;
                        options.inInputShareable = true;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        Bitmap sourceBitmap = BitmapFactory.decodeFile(filepath, options);

                        byte[] bytes = compressBitmap(sourceBitmap, 75, Math.min(maxSize, (int) fileLength), isPngUrl(filepath));
                        if (null != bytes) {
                            fos = new FileOutputStream(new File(newFilePath), false);
                            fos.write(bytes);
                            fos.flush();
                        }
                    } else {
//						DisplayImageOptions.Builder builder = ImageLoaderUtil.getNoCacheBuilder();
//						Object object =  ImageLoader.getInstance().loadImageSync(ImageDownloader.Scheme.FILE.wrap(filepath), new ImageSize(480, 800), builder.build().setLoadLongImg(true));
//						if (object != null && object instanceof Bitmap) {
//							Bitmap sourceBitmap = (Bitmap) object;
//							byte[] bytes = compressBitmap(sourceBitmap, 80,  maxSize, isPngUrl(filepath));
//							if (null != bytes) {
//								fos = new FileOutputStream(new File(newFilePath), false);
//								fos.write(bytes);
//								fos.flush();
//							}
//						}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fos) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return new File(newFilePath);
    }

    public static boolean isPngUrl(String path) {
        if (path != null) {
            return path.toLowerCase().endsWith(".png");
        }
        return false;
    }

    public static byte[] compressBitmap(Bitmap bitmap, int defaultQuality, int size, boolean isPng) {
        byte[] data = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            if (bitmap == null) {
                return baos.toByteArray();
            }
            bitmap.compress(isPng ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, defaultQuality, baos);
            if (isPng && baos.toByteArray().length / 1024 > size) {
                // 如果是png 不能压缩 则转成jpg
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
            while (baos.toByteArray().length > size) {
                if (defaultQuality <= 0) {
                    break;
                }
                defaultQuality -= 10;
                ILog.i(TAG, "compressImage:" + baos.toByteArray().length / 1024 + " KB");
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, defaultQuality, baos);
            }
            data = baos.toByteArray();
            int tsize = data.length / 1024;
            ILog.i(TAG, "### compress size = " + tsize + " KB");
            ILog.i(TAG, "compress image width:" + bitmap.getWidth() + "--compress image height:" + bitmap.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (null != bitmap) {
                    bitmap.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * 获取压缩比例
     *
     * @param options
     * @param targeW
     * @param targeH
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int targeW, int targeH) {
        int w = options.outWidth;
        int h = options.outHeight;
        float candidateW = w / targeW;
        float candidateH = h / targeH;

        float candidate = Math.max(candidateW, candidateH);
        if (candidate == 0) {
            return 1;
        }
        if (candidate > 1) {
            if ((w > targeW) && (w / candidate) < targeW) {
                candidate -= 1;
            }
        }

        if (candidate > 1) {
            if ((h > targeH) && (h / candidate) < targeH) {
                candidate -= 1;
            }
        }

        return (int) candidate;
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        if (context == null || uri == null) return null;

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Drawable readDrawable(Context context, int resId) {
        try {
            Bitmap bitmap = readBitMap(context, resId);
            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return context.getResources().getDrawable(resId);
        }
    }

    /**
     * 设置背景
     */
    @SuppressLint("NewApi")
    public static void setBackground(Context context, int resid, View view) {
        try {
            Bitmap bitmap = readBitMap(context, resid);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
            view.setBackgroundDrawable(bitmapDrawable);
            bitmap = null;
            bitmapDrawable = null;
        } catch (Exception e) {
            view.setBackgroundResource(resid);
        }
    }

    public static void setImageViewSrc(Context context, int resid, ImageView view) {
        try {
            view.setImageBitmap(readBitMap(context, resid));
        } catch (Exception e) {
            view.setImageResource(resid);
        }
    }

    /**
     * 获取bitmap 大小
     *
     * @param bitmap
     * @return
     */
    public static int getBitmapsize(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();

    }

}
