package com.xiaolanba.passenger.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.framework.common.utils.ILog;
import com.framework.common.utils.INetworkUtils;
import com.xiaolanba.passenger.LBApplication;
import com.xiaolanba.passenger.logic.control.LBController;

/**
 * 网络转换广播接收器
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class NetworkChangedReceive extends BroadcastReceiver {

    private static final String TAG = "NetworkChangedReceive";
    public static final String NETWORK_CHANGED_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static BroadcastReceiver mReceive = null;
    public static int isnet = 0;

    public NetworkChangedReceive() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (null == connManager) {
                return;
            }

            // 判断是否正在使用WIFI网络
            State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            if (null == state) {
                return;
            }
            isnet = State.CONNECTED == state ? 0 : -1;
            // WIFI 连接不成功的情况下开启GPRS连接网络
            if (State.CONNECTED != state) {
                state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

                if (null == state) {
                    return;
                }

                // 判断是否正在使用GPRS网络
                isnet = State.CONNECTED == state ? 0 : -1;
            }
            if (isnet == -1) {
                //无网络
                ILog.e(TAG, "------无网络连接------");
                LBController.getInstance().getListenerManager().postNetworkListener(false);
            } else {
                //有网络
                ILog.e(TAG, "已连接网络，网络类型:" + INetworkUtils.getInstance().getType());
                LBController.getInstance().getListenerManager().postNetworkListener(true);
            }

            LBController controller = LBController.getInstance();
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 注册网络监听广播
     */
    public static void beginListenNetworkChange() {
        if (null == mReceive) {
            try {
                IntentFilter filter = new IntentFilter(NetworkChangedReceive.NETWORK_CHANGED_ACTION);
                mReceive = new NetworkChangedReceive();
                LBApplication.getInstance().registerReceiver(mReceive, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /***
     * 注销网络监听广播
     */
    public static void finishListenNetworkChange() {
        if (null != mReceive) {
            try {
                LBApplication.getInstance().unregisterReceiver(mReceive);
                mReceive = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

