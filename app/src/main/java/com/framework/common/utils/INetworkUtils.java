package com.framework.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xiaolanba.passenger.LBApplication;

/**
 * 网络检测工具类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class INetworkUtils {
    /**
     * Current network is TD_SCDMA {@hide}
     */
    public static final int NETWORK_TYPE_TD_SCDMA = 17;

    public static final int NETWORK_TYPE_UNKNOWN = -1;

    public static final int NETWORK_TYPE_WIFI = 0;

    public static final int NETWORK_TYPE_3G = 1;

    public static final int NETWORK_TYPE_2G = 2;

    public static final int NETWORK_TYPE_4G = 3;

    /**
     * 中国移动
     **/
    public static final int CHINA_MOBILE_CMCC = 1;
    /**
     * 中国联通
     ***/
    public static final int CHINA_UNICOM = 2;
    /**
     * 中国电信
     **/
    public static final int CHINA_TELECOM = 3;
    /**
     * 运营商未知
     */
    public static final int UNKONW = 4;
    private ConnectivityManager mConnManager;

    private TelephonyManager mPhonyManager;

    private static INetworkUtils mInstance;
    private Context mContext;

    public String getType() {
        String type = "Unknown";
        switch (getNetworkType()) {
            case INetworkUtils.NETWORK_TYPE_WIFI:
                type = "WiFi";
                break;
            case INetworkUtils.NETWORK_TYPE_4G:
                type = "4G";
                break;
            case INetworkUtils.NETWORK_TYPE_3G:
                type = "3G";
                break;
            case INetworkUtils.NETWORK_TYPE_2G:
                type = "2G";
                break;
            default:
                break;
        }
        return type;
    }

    private INetworkUtils() {
        mContext = LBApplication.getInstance();
        mConnManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mPhonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static INetworkUtils getInstance() {
        if (mInstance == null) {
            synchronized (INetworkUtils.class) {
                mInstance = new INetworkUtils();
            }
        }
        return mInstance;
    }

    /**
     * @return
     */
    public int getNetworkType() {
        if (isWifiAvailable()) {
            return NETWORK_TYPE_WIFI;
        }
        if (isMobileNetAvailable()) {
            if (isConnection4G()) {
                return NETWORK_TYPE_4G;
            }

            if (isConnection3G()) {
                return NETWORK_TYPE_3G;
            }

            if (isConnection2G()) {
                return NETWORK_TYPE_2G;
            }
        }
        return NETWORK_TYPE_UNKNOWN;
    }

    /***
     * @return
     */
    public boolean isConnection4G() {
        boolean result = false;
        switch (mPhonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_LTE: // 4G
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * @return
     */
    public boolean isConnection3G() {
        boolean result = false;
        switch (mPhonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
            case TelephonyManager.NETWORK_TYPE_EVDO_0: // 3G
            case TelephonyManager.NETWORK_TYPE_EVDO_A: // 3G
            case TelephonyManager.NETWORK_TYPE_HSDPA: // 3G
            case TelephonyManager.NETWORK_TYPE_HSUPA: // 3G
            case TelephonyManager.NETWORK_TYPE_HSPA: // 3G
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case NETWORK_TYPE_TD_SCDMA:
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * @return
     */
    public boolean isConnection2G() {
        boolean result = false;
        switch (mPhonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS: // 2.5G
            case TelephonyManager.NETWORK_TYPE_EDGE: // 2.75G
            case TelephonyManager.NETWORK_TYPE_CDMA: // 2G
            case TelephonyManager.NETWORK_TYPE_1xRTT: // 2G
            case TelephonyManager.NETWORK_TYPE_IDEN: // 2G
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    public boolean isNetworkConnectedOrConnectiong() {
        NetworkInfo info = mConnManager.getActiveNetworkInfo();
        return null != info && info.isAvailable() && info.isConnectedOrConnecting();
    }

    public boolean isWifiConnectedOrConnecting() {
        NetworkInfo info = mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return null != info && info.isAvailable() && info.isConnectedOrConnecting();
    }

    public boolean isMobileConnectedOrConnectiong() {
        NetworkInfo info = mConnManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return null != info && info.isAvailable() && info.isConnectedOrConnecting();
    }

    /***
     * 根据IMSI
     */
    public int getTelephoneDetailByIMSI() {
        /**
         * 获取SIM卡的IMSI码 SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile
         * SubscriberBean Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         */
        String imsi = mPhonyManager.getSubscriberId();
        if (imsi != null) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
                // 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
                // 中国移动
                return CHINA_MOBILE_CMCC;
            } else if (imsi.startsWith("46001")) {
                // 中国联通
                return CHINA_UNICOM;
            } else if (imsi.startsWith("46003")) {
                // 中国电信
                return CHINA_TELECOM;
            }
        }
        return UNKONW;
    }

    /**
     * 根据运营商
     */
    public int getTelephoneDetailByOperator() {
        String operator = mPhonyManager.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002")) {
                // 中国移动
                return CHINA_MOBILE_CMCC;
            } else if (operator.equals("46001")) {
                // 中国联通
                return CHINA_UNICOM;
            } else if (operator.equals("46003")) {
                // 中国电信
                return CHINA_TELECOM;
            }
        }
        return UNKONW;
    }

    /**
     *  当前网络是否可用
     * @param
     * @return
     */
    public boolean isNetworkAvailable() {
        NetworkInfo info = mConnManager.getActiveNetworkInfo();
        return isLinkable(info);
    }


    /**
     * @return
     */
    public boolean isMobileNetAvailable() {
        NetworkInfo info = mConnManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return isLinkable(info);
    }

    /**
     * 根据Wifi信息获取本地Mac
     *
     * @param
     * @return
     */
    public String getLocalMacAddressFromWifiInfo() {
        WifiManager wifi = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return "00:00:00:00:00:00";
        }
        WifiInfo info = wifi.getConnectionInfo();
        if (info == null) {
            return "00:00:00:00:00:00";
        }
        String wifiMac = info.getMacAddress();
        if (TextUtils.isEmpty(wifiMac)) {
            return "00:00:00:00:00:00";
        } else {
            return wifiMac;
        }
    }

    /**
     * @return
     */
    public boolean isWifiAvailable() {
        NetworkInfo info = mConnManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return isLinkable(info);
    }

    /**
     * @param info
     * @return
     */
    private boolean isLinkable(NetworkInfo info) {
        if ((info != null) && info.isConnected() && info.isAvailable()) {
            return true;
        }
        return false;
    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = null;
        if(android.os.Build.VERSION.SDK_INT > 10){  // 3.0以上
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        }else{
            intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
        }
        activity.startActivity(intent);
    }
}
