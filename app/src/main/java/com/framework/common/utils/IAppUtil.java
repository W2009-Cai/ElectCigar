package com.framework.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xiaolanba.passenger.LBApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.StringTokenizer;

/**
 * app 工具类，与安装包相关
 *
 * @author xutingz
 */
public class IAppUtil {


    /**
     * 获取应用程序包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {

        return context.getPackageName();
    }

    /**
     * 获取版本
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * whether application is in background
     * <ul>
     * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
     * </ul>
     *
     * @param context
     * @return if application is in background return true, otherwise return false
     */
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取IMEI
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = null;
        try{
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (deviceId == null){
            deviceId = "unkonw";
        }
        return deviceId;
    }

    /**
     * 获取IMSI
     *
     * @param context
     * @return
     */
    public static String getSubscriberId(Context context) {
        String subId = null;
        try{
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
            subId = tm.getSubscriberId();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (subId == null){
            subId = "unkonw";
        }
        return subId;
    }

    /**
     * 获取 topActivity ComponentName
     *
     * @param context
     * @return
     */
    public static ComponentName getTopComponentName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentName = taskInfo.get(0).topActivity;
        return componentName;
    }

    /**
     * 获取AndroidId
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        ContentResolver cr = context.getContentResolver();
        String androidid = Settings.System.getString(cr, Settings.Secure.ANDROID_ID);
        if (IStringUtil.isEmpty(androidid)) {
            androidid = Build.SERIAL; //如果为空，使用Build.SERIAL作保险
        }
        return androidid;
    }

    public static boolean isTopActivity(Context context, Class c) {
        boolean isTop = false;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if (cn.getClassName().contains(c.getCanonicalName())) {
                isTop = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isTop;
    }

    /**
     * 获取正在运行的任务这里一定要注意，这里我们获取的时候，
     * 你的任务或者其中的activity可能没结束，但是当你在后边使用的时候，很有可能已经被kill了哦。
     * 意思很简单，系统返给你的正在运行的task，是暂态的，仅仅代表你调用该方法时系统中的状态，
     * 至于后边是否发生了该变，系统概不负责、
     * 返回true表示在前台运行，
     * 返回false表示activity没有运行
     */
    public static boolean isActivityRunning(Context context, String activityName) {
        ActivityManager activityManager = (ActivityManager) context
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager
                .getRunningTasks(50);
        if (runningTasks != null) {
            for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
                String info = taskInfo.baseActivity.getClassName();
                if (info.equals(activityName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void exitApp() {
        ILog.d("IAppUtil", "Exit On Global Exception");
        String packageName = LBApplication.getInstance().getPackageName();
        String processId = "";
        try {
            Runtime r = Runtime.getRuntime();
            java.lang.Process p = r.exec("ps");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inline;
            while ((inline = br.readLine()) != null) {
                if (inline.endsWith(packageName)) {
                    break;
                }
            }
            br.close();
            StringTokenizer processInfoTokenizer = new StringTokenizer(inline);
            int count = 0;
            while (processInfoTokenizer.hasMoreTokens()) {
                count++;
                processId = processInfoTokenizer.nextToken();
                if (count == 2) {
                    break;
                }
            }
            r.exec("kill -15 " + processId);

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            ILog.e("IAppUtil", "uncaughtException: ", e);
        }
    }

    // You may uncomment next line if using Android Annotations library,
    // otherwise just be sure to run it in on the UI thread
    // @UiThread
    public static String getDefaultUserAgentString(Context context) {
        if (Build.VERSION.SDK_INT >= 17) {
            return NewApiWrapper.getDefaultUserAgent(context);
        }

        try {
            Constructor<WebSettings> constructor = WebSettings.class.getDeclaredConstructor(Context.class, WebView.class);
            constructor.setAccessible(true);
            try {
                WebSettings settings = constructor.newInstance(context, null);
                return settings.getUserAgentString();
            } finally {
                constructor.setAccessible(false);
            }
        } catch (Exception e) {
            return new WebView(context).getSettings().getUserAgentString();
        }
    }

    @TargetApi(17)
    static class NewApiWrapper {
        static String getDefaultUserAgent(Context context) {
            return WebSettings.getDefaultUserAgent(context);
        }
    }


    /**
     * 跳转到app详细设置页面
     *
     * @param context
     */
    public static void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

    /**
     * @param context
     * @return 程序版本名
     */
    public static String getAppVerName(Context context) {
        String vername = null;
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            vername = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        vername = null == vername ? "" : vername;
        return vername;
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isGpsOpen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        if (gps) { //  || network
            ILog.e("mm","---isGpsOpen true");
            return true;
        }
        ILog.e("mm","---isGpsOpen false");
        return false;
    }

    /**
     * 跳转到设置打开Gps
     * @param activity
     */
    public static void openGpsSetting(Activity activity,int requestCode){
        try{
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(intent, requestCode); // 设置完成后返回到原来的界面
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 强制帮用户打开GPS,(经测试此方法无效)
     * @param context
     */
    public static final void openGps(Context context) {
        Intent gpsIntent = new Intent();
        try {
            gpsIntent.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
            gpsIntent.setData(Uri.parse("custom:3"));
            PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
            ILog.e("mm","---强制帮用户打开GPS 异常");
        }
        ILog.e("mm","---openGPS finish");
    }

}

