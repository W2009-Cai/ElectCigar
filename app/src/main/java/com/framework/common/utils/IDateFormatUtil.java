package com.framework.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.xiaolanba.passenger.logic.control.LBController;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间格式工具类
 *
 * @author xutingz
 */
@SuppressLint("SimpleDateFormat")
public class IDateFormatUtil {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;
    public static final long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;
    public static final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;
    public static final long MONTH_IN_MILLIS = 30 * DAY_IN_MILLIS;

    // 一天
    private final static long ONE_DAYS = 86400 * 1000;

    public static final String ENG_DATE_FROMAT = "EEE, d MMM yyyy HH:mm:ss z";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String MM_DD_HH_MM = "MM-dd HH:mm";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY = "yyyy";
    public static final String MM_DD = "MM-dd";
    public static final String HH_MM = "HH:mm";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String EEE_HH_MM = "EEE HH:mm";
    public static final String MM = "MM";
    public static final String DD = "dd";
    public static final String YYYY_MM_CHINA = "yyyy年MM月";
    public static final String YYYY_MM_DD_CHINA = "yyyy年MM月dd日";
    public static final String YYYY_MM_DD_CHINA_HH_MM = "yyyy年MM月dd日 HH:mm";
    public static final String MM_DD_CHINA = "MM月dd日";
    public final static String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public final static String weekNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    public final static String MMDOTDD = "MM.dd";
    public final static String DD_CHINA = "dd日";
    public static final String YYYY_MM = "yyyyMM";
    public static final String CONSTELL[] = {"水瓶座", "双鱼座", "牧羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座",
            "天蝎座", "射手座", "摩羯座"};
    public static final String MONTH_ARRAY[] = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    public static final String DAY_ARRAY[] = {"1日", "2日", "3日", "4日", "5日", "6日", "7日", "8日", "9日", "10日",
            "11日", "12日", "13日", "14日", "15日", "16日", "17日", "18日", "19日", "20日",
            "21日", "22日", "23日", "24日", "25日", "26日", "27日", "28日", "29日", "30日", "31日"};

    public static DateFormat yyyyMMddHHmmssDateFormat;
    private static DateFormat monthDateFormate;
    private static DateFormat hhmmFormate;
    private static DateFormat weekDateFormate;

    public static SimpleDateFormat serverDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);

    public static long getTimeMillis() {

        return System.currentTimeMillis() - LBController.getInstance().getCacheManager().serverTimeDvalue;

    }

    public static String parseLongToTime(long time) {
        if (time == 0) {
            return "";
        }
        Date d = new Date(time);
        SimpleDateFormat simpledf = new SimpleDateFormat(MM_DD_HH_MM);
        return simpledf.format(d);
    }

    public static boolean isToDay(long startTime) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String startDate = sdf.format(new Date(startTime));
        String endDate = sdf.format(new Date(getTimeMillis()));
        if (startDate.compareTo(endDate) == 0) {
            return true;
        }

        return false;
    }

    public static boolean isDurationOneDay(long startTime, long secondTime) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String startDate = sdf.format(new Date(startTime));
        String endDate = sdf.format(new Date(secondTime));
        if (startDate.compareTo(endDate) == 0) {
            return true;
        }

        return false;
    }

    /**
     * 返回（年）月日时分
     *
     * @param time
     * @param dateFormat
     * @return
     */
    public static String getRealTimeString(long time, SimpleDateFormat dateFormat) {
        String interval = "";
        if (isTimeInThisYear(time)) {
            // 在今年内，则显示正常的时间，但是不显示秒
            if (dateFormat != null) {
                interval = dateFormat.format(new Date(time));
            } else { //如果传过来的匹配方式为空,使用默认匹配
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                interval = sdf.format(new Date(time));
            }
        } else {
            // 不在今年内，则显示正常的时间，但是不显示秒
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            interval = sdf.format(new Date(time));
        }
        return interval;
    }


    /**
     * 返回对应的时间
     *
     * @param time  时间
     * @param formart 格式化类型
     * @return
     */
    public static long getRealTime(String time, String formart) {
        String interval = "";
        long time1 = 0;
        try {
            time1 = new SimpleDateFormat(formart).parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time1;
    }


    /**
     * 时间是否在同一年
     *
     * @param millis
     * @return
     */
    private static boolean isTimeInThisYear(long millis) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        int year = c.get(Calendar.YEAR);
        if (year - getCurrentYear() == 0) {//在同一年
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取今年的年份
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getTimeMillis());
        return c.get(Calendar.YEAR);
    }

    /**
     * yyyyMMddHHmmss 格式的日期解析器
     */
    public static DateFormat getyyyyMMddHHmmssDateFormat() {
        if (null == yyyyMMddHHmmssDateFormat) {
            yyyyMMddHHmmssDateFormat.setTimeZone(ITimeZoneUtil.getLocalTimeZone());
        }
        return yyyyMMddHHmmssDateFormat;
    }

    /**
     * 得到UTC时间
     *
     * @return
     */
    public static String getUtcTime() {
        return getDateTime(ITimeZoneUtil.GMT0_TIME_ZONE);
    }

    /**
     * 根据utc的毫秒级获取本地日期
     *
     * @param utcMillis
     * @return
     */
    public static Date getLocalDateByUtc(long utcMillis) {
        return new Date(utcMillis);
    }

    /**
     * 根据本地的毫秒级获取UTC日期
     *
     * @param utcMillis
     * @return
     */
    public static long getUtcDateByLocal(long utcMillis) {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        cal.setTimeInMillis(utcMillis);
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        return cal.getTime().getTime();
    }

    /**
     * @param @return
     * @return String
     * @Title: getDateTime
     * @Description: 根据当前时间yyyyMMddHHmmss格式的字符串
     */
    public static String getDateTime(TimeZone timeZone) {
        DateFormat dateFormat = getyyyyMMddHHmmssDateFormat();
        dateFormat.setTimeZone(timeZone);
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        return dateFormat.format(date);
    }

    /**
     *
     * @param formatYMD
     * @param date      2014-03-24
     * @return
     */
    public static String getWeek(SimpleDateFormat formatYMD, String date) {
        String str = "";
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatYMD.parse(date));
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            str = dayNames[dayOfWeek - 1];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static long nd = 1000 * 24 * 60 * 60;    // 一天的毫秒数
    public static long nh = 1000 * 60 * 60;            // 一小时的毫秒数
    public static long nm = 1000 * 60;                // 一分钟的毫秒数

    /**
     * 用于几天前的显示时间
     *
     * @param context 上下文
     * @param millis  utc毫秒级
     * @return 列表显示的时间字符串
     */
    public static String kikShowDate(long millis, Context context) {
        try {

            Calendar cal = Calendar.getInstance();

            long nowTime = getTimeMillis();
            cal.setTimeInMillis(nowTime);
            int week1 = cal.get(Calendar.DAY_OF_WEEK);

            cal.setTimeInMillis(millis);
            long tempTime = cal.getTimeInMillis();
            int week2 = cal.get(Calendar.DAY_OF_WEEK);

            //如果显示的时间超过当前时间
            if (nowTime < tempTime) {
                return "刚刚";
            }
            long diff = nowTime - tempTime;
            double day = Math.abs(diff / ONE_DAYS);

            // 今天(24小时之内，而且都是星期几)
            if (day < 1 && week1 == week2) {

                long hour = diff % nd / nh;            // 计算差多少小时
                long min = diff % nd % nh / nm;        // 计算差多少分钟

                if (hour > 0) {
                    return hour + "小时前";
                } else {
                    return (min <= 0 ? 1 : min) + "分钟前";
                }
            }
            // 昨天(24小时之内，1、在同一周，星期几相差1，不在同一周，星期几相差6)
            else if (day < 2 && (Math.abs(week1 - week2) == 1 || Math.abs(week1 - week2) == 6)) {
                return "昨天";
            } else {
                long diffDay = diff / nd;            // 	计算差多少天
                return diffDay + "天前";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private synchronized static DateFormat getDateFormate_month() {
        if (monthDateFormate == null) {
            DateFormatSymbols symbols = new DateFormatSymbols();
            monthDateFormate = new SimpleDateFormat(MM_DD_HH_MM, symbols);
        }
        return monthDateFormate;
    }

    private synchronized static DateFormat getHhMmDateFormate() {
        if (hhmmFormate == null) {
            hhmmFormate = new SimpleDateFormat(HH_MM);
        }
        return hhmmFormate;
    }

    private synchronized static DateFormat getWeekDateFormate() {
        if (weekDateFormate == null) {
            weekDateFormate = new SimpleDateFormat(EEE_HH_MM);
        }
        return weekDateFormate;
    }

    /**
     * 星座
     */
    public static String getStarFromDate(int month, int day) {
        if (month == 3 && day >= 21 || month == 4 && day <= 20) {
            return "白羊座";
        }
        if (month == 4 && day >= 21 || month == 5 && day <= 21) {
            return "金牛座";
        }
        if (month == 5 && day >= 22 || month == 6 && day <= 21) {
            return "双子座";
        }
        if (month == 6 && day >= 22 || month == 7 && day <= 22) {
            return "巨蟹座";
        }
        if (month == 7 && day >= 23 || month == 8 && day <= 22) {
            return "狮子座";
        }
        if (month == 8 && day >= 23 || month == 9 && day <= 22) {
            return "处女座";
        }
        if (month == 9 && day >= 23 || month == 10 && day <= 23) {
            return "天秤座";
        }
        if (month == 10 && day >= 24 || month == 11 && day <= 22) {
            return "天蝎座";
        }
        if (month == 11 && day >= 23 || month == 12 && day <= 21) {
            return "射手座";
        }
        if (month == 12 && day >= 22 && day <= 31 || (month == 1 && day >= 1 && day <= 20)) {
            return "摩羯座";
        }
        if (month == 1 && day >= 21 || month == 2 && day <= 19) {
            return "水瓶座";
        }
        if (month == 2 && day >= 20 || month == 3 && day <= 20) {
            return "双鱼座";
        }
        return "";
    }

    /**
     * 时间转毫秒级
     *
     * @return
     */
    public static long getTimestamp(String time, String format) {
        Date date1 = null;
        Date date2 = null;
        long l = 0;
        try {
            date1 = new SimpleDateFormat(format).parse(time);
            date2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM).parse("1970-01-01 08:00");
            l = date1.getTime() - date2.getTime() > 0 ? date1.getTime() - date2.getTime() : date2.getTime() - date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }


    //当前时间是星期几
    public static String getWeekOfDate(long time) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        Date curDate = new Date(time);
        cal.setTime(curDate);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getDayOfWeek(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 0;
        return weekNames[w];
    }

    /**
     * 根据出生日期计算年龄
     *
     * @param dateStr yyyy-MM-dd格式
     * @return
     */
    public static int getAgeFromDate(String dateStr) {
        int deltaAge = 22;
        if (null == dateStr || "".equals(dateStr) || "0000-00-00".equals(dateStr)) {
            return deltaAge;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD, Locale.CHINA);
        try {
            long deltaTime = sdf.parse(sdf.format(new Date(getTimeMillis()))).getTime()
                    - sdf.parse(dateStr).getTime();
            long date = deltaTime / (1 * 24 * 60 * 60 * 1000) - 1;
            String ageStr = new DecimalFormat("#.00").format(date / 365.25f);
            String age[] = ageStr.split("\\.");
            int tempLength = age[0].replace("-", "0").length();
            if (tempLength < 1) {

            } else {
                deltaAge = Integer.parseInt(age[0].replace("-", "0"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deltaAge;
    }

    /**
     * 格式化时长
     *
     * @param time
     * @return
     */
    public static String getReplaceTime(long time) {
        long abs = time < 0 ? 0 : time;

        long days = abs / DAY_IN_MILLIS;
        long hours = (abs % DAY_IN_MILLIS) / HOUR_IN_MILLIS;
        long minutes = ((abs % DAY_IN_MILLIS) % HOUR_IN_MILLIS)
                / MINUTE_IN_MILLIS;
        long seconds = (((abs % DAY_IN_MILLIS) % HOUR_IN_MILLIS) % MINUTE_IN_MILLIS)
                / SECOND_IN_MILLIS;
        hours += days * 24;
        return (hours > 9 ? hours : "0" + hours) + ":"
                + (minutes > 9 ? minutes : "0" + minutes) + ":"
                + (seconds > 9 ? seconds : "0" + seconds);
    }

    public static String getReplaceTime(long time, boolean isChinese, boolean hasHours, boolean hasMin, boolean hasSec) {
        long abs = time < 0 ? 0 : time;

        long days = abs / DAY_IN_MILLIS;
        long hours = (abs % DAY_IN_MILLIS) / HOUR_IN_MILLIS;
        long minutes = ((abs % DAY_IN_MILLIS) % HOUR_IN_MILLIS)
                / MINUTE_IN_MILLIS;
        long seconds = (((abs % DAY_IN_MILLIS) % HOUR_IN_MILLIS) % MINUTE_IN_MILLIS)
                / SECOND_IN_MILLIS;
        hours += days * 24;

        String result = "";
        if (hasHours) {
            result += (isChinese ? hours + "小时" : (hours > 9 ? hours : "0" + hours) + ":");
        }

        if (hasMin) {
            result += (isChinese ? minutes + "分钟" : (minutes > 9 ? minutes : "0" + minutes) + ":");
        }

        if (hasSec) {
            result += (isChinese ? seconds + "秒" : (seconds > 9 ? seconds : "0" + seconds));
        }

        return result;
    }

    public static String getReplaceTime(long startTime, long endTime) {
        long time = endTime - startTime;
        return getReplaceTime(time);
    }

    /**
     * 格式化时长 中文tag
     *
     * @param abs
     * @return
     */
    public static String getReplaceTimeToChinese(long abs) {
        return getReplaceTimeToChinese(abs, true);
    }

    public static String getReplaceTimeToChinese(long abs, boolean hasSeconds) {

        abs = abs < 0 ? 0 : abs;

        long days = abs / DAY_IN_MILLIS;
        long hours = (abs % DAY_IN_MILLIS) / HOUR_IN_MILLIS;
        long minutes = ((abs % DAY_IN_MILLIS) % HOUR_IN_MILLIS)
                / MINUTE_IN_MILLIS;
        long seconds = (((abs % DAY_IN_MILLIS) % HOUR_IN_MILLIS) % MINUTE_IN_MILLIS)
                / SECOND_IN_MILLIS;
        hours += days * 24;

        String result = "";
        if (hours > 0) {
            result += hours + "小时";
        }
        if (minutes > 0) {
            result += minutes + "分钟";
        }
        if (hasSeconds) {
            if (seconds > 0) {
                result += seconds + "秒";
            }
        }

        return result;
    }

    public static String getReplaceTimeToChinese(long startTime, long endTime) {
        long abs = endTime - startTime;

        abs = abs < 0 ? 0 : abs;

        long days = abs / DAY_IN_MILLIS;
        long hours = (abs % DAY_IN_MILLIS) / HOUR_IN_MILLIS;
        long minutes = ((abs % DAY_IN_MILLIS) % HOUR_IN_MILLIS)
                / MINUTE_IN_MILLIS;
        long seconds = (((abs % DAY_IN_MILLIS) % HOUR_IN_MILLIS) % MINUTE_IN_MILLIS)
                / SECOND_IN_MILLIS;
        hours += days * 24;

        String result = "";
        if (hours > 0) {
            result += hours + "小时";
        }
        if (minutes > 0) {
            result += minutes + "分钟";
        }
        if (seconds > 0) {
            result += seconds + "秒";
        }
        return result;
    }


    /**
     * 时间转换
     *
     * @param time
     * @param oldFormat
     * @param newFormat
     * @return
     */
    public static String getFormatTime(String time, String oldFormat, String newFormat) {
        if (IStringUtil.isEmpty(time)) {
            return "";
        }
        try {
            Date date = new SimpleDateFormat(oldFormat).parse(time);
            return getFormatTime(date, newFormat);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 时间转换
     *
     * @param time
     * @param format
     * @return
     */
    public static String getFormatTime(long time, String format) {
        if (time == 0) {
            return "";
        }
        Date date = getLocalDateByUtc(time);

        return getFormatTime(date, format);
    }


    /**
     * 时间转换
     *
     * @param date
     * @param format
     * @return
     */
    public static String getFormatTime(Date date, String format) {
        String str = "";
        SimpleDateFormat sdFormat = new SimpleDateFormat(format);
        str = sdFormat.format(date);
        return str;
    }


    /**
     * 获取时长 s 保留一位小数  四舍五入
     */
    public static float getDurationToFloat(long duration) {
        float f = 0;
        try {
            BigDecimal b = new BigDecimal(Long.toString(duration));
            BigDecimal one = new BigDecimal("1000");
            f = b.divide(one, 1, BigDecimal.ROUND_HALF_UP).floatValue();

        } catch (Exception e) {
            e.printStackTrace();
            f = 0;
        }
        return f;
    }

    /**
     * 获取时长，取整
     */
    public static int getDurationToInt(long duration) {
        int d = 0;
        try {
            BigDecimal b = new BigDecimal(Long.toString(duration));
            BigDecimal one = new BigDecimal("1000");
            d = b.divide(one, 0, BigDecimal.ROUND_CEILING).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            d = 0;
        }
        return d;
    }

    /**
     * 获取服务器时间
     *
     * @param time
     * @return
     */
    public static long getServerTimestamp(String time) {
        Date date1 = null;
        try {
            serverDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            date1 = serverDateFormat.parse(time);

            return date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取时间轴格式 6.20 周一
     *
     * @param date 2016-07-02
     * @return
     */
    public static String getTimelineMonthDayWeek(String date) {
        String str = "";
        SimpleDateFormat formatYMD = new SimpleDateFormat(YYYY_MM_DD);
        SimpleDateFormat fromatMD = new SimpleDateFormat(MMDOTDD);
        Calendar calendar = Calendar.getInstance();
        try {

            str = fromatMD.format(formatYMD.parse(date));

            calendar.setTime(formatYMD.parse(date));
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            str = str + " " + weekNames[dayOfWeek - 1];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 根据某个时间，获取这个时间是几月份
     *
     * @param firstTime
     * @return
     */
    public static String getMonthFromTime(long firstTime) {

        SimpleDateFormat fromatMM = new SimpleDateFormat("MM");
        return fromatMM.format(new Date(firstTime));
    }

    public static String getDayInMonthFromTime(long time) {
        SimpleDateFormat fromatMM = new SimpleDateFormat("dd");
        return fromatMM.format(new Date(time));
    }


    /**
     * 判断两个时间是否在同一个月
     *
     * @param firstTime
     * @param secondTime
     * @return
     */
    public static boolean isTimeInSameMonth(long firstTime, String secondTime) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(firstTime);

        Calendar cal2 = Calendar.getInstance();
        try {
            SimpleDateFormat formatYM = new SimpleDateFormat(YYYY_MM);
            cal2.setTimeInMillis(formatYM.parse(secondTime).getTime());
            boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
            return isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

    /**
     * 将秒转成时间
     *
     * @param l
     * @return
     */
    public static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue();
        if (second == 60) {
            return "01:00";
        }
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String hourStr;
        if (hour > 0) {
            hourStr = (getTwoLength(hour)) + ":";
        } else {
            hourStr = "";
        }
        return hourStr + getTwoLength(minute) + ":" + getTwoLength(second);
    }

    private static String getTwoLength(final int data) {
        if (data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }


    /**
     * 根据秒数，转化为时间格式字符串
     *
     * @param second
     * @return
     */
    public static String secondToFormatText(long second) {
        long hh = second / 3600;
        long mm = second % 3600 / 60;
        long ss = second % 60;
        String strTemp = "";
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        return strTemp;
    }

}
