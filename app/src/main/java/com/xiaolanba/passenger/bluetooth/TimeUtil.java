package com.xiaolanba.passenger.bluetooth;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by JiaJiefei on 2017/2/20.
 */

public class TimeUtil {
    private TimeUtil() {
    }

    public static String getTimeStamp(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        return format.format(new Date(System.currentTimeMillis()));
    }
}
