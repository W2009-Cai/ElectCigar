package com.xiaolanba.passenger.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.framework.common.utils.ILog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xutingz
 * @description 图片保存处理工具类
 */


public class BitmapUtils {
    private static BitmapUtils instance;

    public static BitmapUtils getInstance() {
        if (null == instance) {
            instance = new BitmapUtils();
        }
        return instance;
    }

    private final String TAG = getClass().getSimpleName();

    public void compressAndSave(final String sourceFilePath, final int reqSize, final String saveFileDir, final CompressListener compressListener) {
        new Thread() {
            @Override
            public void run() {
                if (null != compressListener) {
                    compressListener.onComressAndSaveStart();
                    compressListener.onComressAndSaveFinish(compressAndSave(sourceFilePath, reqSize, saveFileDir));
                }
            }
        }.start();
    }

    public String compressAndSave(String sourceFilePath, int reqSize, String saveFileDir) {
        Bitmap compressedBitmap = compressBitmap(sourceFilePath, reqSize);
        if (null == compressedBitmap) {
            return "";
        }
        return saveBitmap(compressedBitmap, getImageFormat(sourceFilePath), saveFileDir);
    }

    public String saveBitmap(Bitmap bitmap, Bitmap.CompressFormat compressFormat, String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = simpleDateFormat.format(new Date(System.currentTimeMillis())) + (Bitmap.CompressFormat.PNG == compressFormat ? ".png" : ".jpg");
        String filePath = dir + File.separator + fileName;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(compressFormat, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            filePath = "";
        } catch (IOException e) {
            e.printStackTrace();
            filePath = "";
        }
        return filePath;
    }


    public Bitmap compressBitmap(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public Bitmap compressBitmap(String filePath, long reqSize) {//reqSize unit byte
        Bitmap bitmap = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {//之所以要一开始就压缩,而没有判断fileLength是因为此时得到的fileLength和经过BitmapFactory.decodeFile得到的bitmap大小不等
                long fileLength = file.length();
                ILog.i(TAG, String.format("file [%s] fileLength=[%d], reqSize=[%d]", filePath, fileLength, reqSize));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                int inSampleSize = 2;
                do {
                    options.inSampleSize = inSampleSize;
                    if (null != bitmap) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    bitmap = BitmapFactory.decodeFile(filePath, options);
                    if (null != bitmap) {
                        fileLength = bitmap.getRowBytes() * bitmap.getHeight();
                    }
                    ILog.i(TAG, String.format("file [%s] size after compress[%d]=[%d], reqSize[%d]", filePath, inSampleSize, fileLength, reqSize));
                    inSampleSize *= 2;
                } while (fileLength > reqSize);
            } else {
                ILog.e(TAG, String.format("file [%s] not exist!!!", filePath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            if (0 == inSampleSize) {
                return 1;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further.
            // 任何超过2倍的请求像素，我们将品尝简化
            // 更多。
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public Bitmap.CompressFormat getImageFormat(String filePath) {
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        int index = filePath.lastIndexOf(".");
        String format = "";
        if (index >= 0) {
            format = filePath.substring(index + 1);
        }
        if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format)) {
            compressFormat = Bitmap.CompressFormat.JPEG;
        } else {
            compressFormat = Bitmap.CompressFormat.PNG;
        }
        return compressFormat;
    }

    public interface CompressListener {
        void onComressAndSaveStart();

        void onComressAndSaveFinish(String savedImagePath);
    }
}
