package com.framework.common.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * 震动相关
 *
 * @author xutingz
 */
public class IVibratorHelper {

    private static Vibrator vib;

    public static void vibrate(final Context context, long milliseconds) {
        if (null == vib) {
            vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        }
        vib.vibrate(milliseconds);

    }

    public static void vibrate(final Context context, long[] pattern,
                               boolean isRepeat) {
        if (null == vib) {
            vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        }
        vib.vibrate(pattern, isRepeat ? 1 : -1);

    }

}

