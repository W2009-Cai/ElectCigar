package com.framework.library.imageblurring;

import android.graphics.Bitmap;

import com.framework.common.utils.ILog;

/**
 * Created by Qiujuer
 * on 2014/4/19.
 */
public class ImageBlur {
    public static native void blurIntArray(int[] pImg, int w, int h, int r);

    public static native void blurBitMap(Bitmap bitmap, int r);

    static {
        System.loadLibrary("ImageBlur");
    }

    public static Bitmap getDefaultBlur(Bitmap sentBitmap) {
        return ImageBlur.doBlurJniArray(sentBitmap, 5, false);
    }

    public static Bitmap doBlurJniArray(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        try {
            Bitmap bitmap;
            if (canReuseInBitmap) {
                bitmap = sentBitmap;
            } else {
                bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            }

            if (radius < 1) {
                return (null);
            }

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int[] pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);
            //Jni Pixels
            ImageBlur.blurIntArray(pix, w, h, radius);

            bitmap.setPixels(pix, 0, w, 0, 0, w, h);
            return (bitmap);
        } catch (OutOfMemoryError outOfMemoryError) {
            ILog.e("ImageBlur", null != outOfMemoryError.getMessage() ? outOfMemoryError.getMessage().toString() : "OutOfMemoryError");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
