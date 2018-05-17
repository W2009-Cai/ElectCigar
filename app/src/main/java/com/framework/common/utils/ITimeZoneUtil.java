package com.framework.common.utils;

import java.text.DateFormat;
import java.util.TimeZone;

/**
 * 时区转换相关工具类
 *
 * @author xutingz
 */
public class ITimeZoneUtil {
    /**
     * 0区
     */
    public static String Zone0 = "GMT+0";
    /**
     * 东八区
     */
    public static String Zone8 = "GMT+8";

    public static TimeZone localTimeZone = null;
    public static TimeZone GMT0_TIME_ZONE = TimeZone.getTimeZone(Zone0);
    public static TimeZone GMT8_TIME_ZONE = TimeZone.getTimeZone(Zone8);
    public static DateFormat dateFormat = null;

    public static DateFormat dateFormatTo = null;

    public static TimeZone getLocalTimeZone() {
        if (localTimeZone == null) {
            localTimeZone = TimeZone.getDefault();
        }
        return localTimeZone;
    }

}
