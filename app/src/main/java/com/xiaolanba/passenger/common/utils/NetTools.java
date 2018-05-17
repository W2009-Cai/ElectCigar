package com.xiaolanba.passenger.common.utils;


import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class NetTools {
    /**
     * 判断网络状态是否是可用的
     *
     * @param context
     * @return 返回true是有网络，返回false没网络
     */
    public static boolean isNetAvailble(Context context) {

        boolean netSataus = false;
        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager != null) {
            NetworkInfo info = cwjManager.getActiveNetworkInfo();
            if (info != null) {
                netSataus = info.isAvailable();
            }
        }
        return netSataus;
    }

    /**
     * 判断当前网络是否为wifi
     *
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.isAvailable()
                    && activeNetInfo.isConnected() && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    public void initWifi(final Activity activity) {
        Builder b = new Builder(activity).setTitle("没有可用的网络")
                .setMessage("是否对网络进行设置？");
        b.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent mIntent = new Intent("/");
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    mIntent = new Intent(Settings.ACTION_SETTINGS);

                } else {
                    ComponentName comp = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    mIntent.setComponent(comp);
                    mIntent.setAction("android.intent.action.VIEW");
                }
                activity.startActivityForResult(mIntent, 1);
            }

        }).setNeutralButton("否",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        dialog.cancel();
                    }
                }).show();
    }

}
