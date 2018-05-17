package com.xiaolanba.passenger.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.framework.common.utils.ILog;
import com.framework.common.utils.INetworkUtils;
import com.xiaolanba.passenger.LBApplication;

import java.text.DecimalFormat;

/**
 * @author xutingz
 * @company xiaolanba.com
 * @description 一些公共静态方法的集合
 */

public class Tools {
    private static final String TAG = Tools.class.getSimpleName();

    /**
     * 注册一个广播
     *
     * @param receiver
     * @param actions
     */
    public static void registReceiver(BroadcastReceiver receiver, String[] actions) {
        if (null == receiver) {
            ILog.e(TAG, "[receiver] receiver is null!!!");
            return;
        }
        if (null == actions || actions.length == 0) {
            ILog.e(TAG, "[receiver]actions length is empty!!!");
            return;
        }
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        LBApplication.getInstance().registerReceiver(receiver, filter);
    }

    /**
     * 取消注册广播
     *
     * @param receiver
     */
    public static void unregistReceiver(BroadcastReceiver receiver) {
        LBApplication.getInstance().unregisterReceiver(receiver);
    }

    /**
     * 检查网络是否可用，不可用就toast提示
     *
     * @return 网络是否可用
     */
    public static boolean checkNetWorkAndToast() {
        INetworkUtils networkUtils = INetworkUtils.getInstance();
        boolean isNetAvailable = networkUtils.isWifiConnectedOrConnecting() || networkUtils.isMobileConnectedOrConnectiong();
        if (!isNetAvailable) {
            //提示网络异常，请稍后再试
        }
        return isNetAvailable;
    }

    /**
     * 魅蓝手机必须要在设置里打开允许弹提示，才能弹系统级的Toast
     * 如果未允许，必须将WindowManager绑定Activity的方式才能弹
     *
     * @return 网络是否可用
     */
    public static boolean checkNetWorkAndToast(Context context) {
        INetworkUtils networkUtils = INetworkUtils.getInstance();
        boolean isNetAvailable = networkUtils.isWifiConnectedOrConnecting() || networkUtils.isMobileConnectedOrConnectiong();
        if (!isNetAvailable) {
            //提示网络异常，请稍后再试
        }
        return isNetAvailable;
    }


    /**
     * 调用系统播放器
     *
     * @param videoPath
     */
    public static void playVideo(String videoPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String strend = "";
        if (videoPath.toLowerCase().endsWith(".mp4")) {
            strend = "mp4";
        } else if (videoPath.toLowerCase().endsWith(".3gp")) {
            strend = "3gp";
        } else if (videoPath.toLowerCase().endsWith(".mov")) {
            strend = "mov";
        } else if (videoPath.toLowerCase().endsWith(".wmv")) {
            strend = "wmv";
        }
        intent.setDataAndType(Uri.parse(videoPath), "video/" + strend);
        LBApplication.getInstance().startActivity(intent);
    }

    public static String strAction(int action) {
        String strAct = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                strAct = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_MOVE:
                strAct = "ACTION_MOVE";
                break;
            case MotionEvent.ACTION_UP:
                strAct = "ACTION_UP";
                break;
            case MotionEvent.ACTION_CANCEL:
                strAct = "ACTION_CANCEL";
                break;
            case MotionEvent.ACTION_OUTSIDE:
                strAct = "ACTION_OUTSIDE";
                break;
            default:
                break;
        }
        return strAct;
    }

    /**
     * 字节转KB_MB_GB
     *
     * @param bytes
     * @return
     */
    public static String byteConvertSize(long bytes) {
        if (bytes <= 0) {
            return "0KB";
        }
        ILog.wr("byteConvertSize", "size:" + bytes);
        DecimalFormat formater = new DecimalFormat("####.00");
        long kb = 1024 * 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < kb) {
            float kbsize = bytes / 1024f;
            return formater.format(kbsize) + "KB";
        } else if (bytes < mb) {
            float mbsize = bytes / 1024f / 1024f;
            return formater.format(mbsize) + "MB";
        } else if (bytes < gb) {
            float gbsize = bytes / 1024f / 1024f / 1024f;
            return formater.format(gbsize) + "GB";
        } else {
            return "0KB";
        }
    }

    /**
     * 获取视频时长
     *
     * @param mUri
     * @return 返回秒
     */
    public static int getVideoDuring(String mUri) {
        int duration = 0;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();

        try {
            if (!TextUtils.isEmpty(mUri)) {
//                HashMap<String, String> headers=null;
//                if (headers == null) {
//                    headers = new HashMap<String, String>();
//                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
//                }
//                mmr.setDataSource(mUri, headers);
                mmr.setDataSource(mUri);

                String d = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
                if (!TextUtils.isEmpty(d)) {
                    duration = Integer.parseInt(d) / 1000;
                    if (duration == 0) {
                        duration = 1;
                    }
                }
                ILog.wr("duration", "d = " + d + "duration: " + duration + " ,url:" + mUri + " ,d:" + d);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            duration = -1;
            ILog.wr("duration", "视频时长获取异常 " + mUri);
        } finally {
            mmr.release();
        }
        ILog.wr("duration", "duration: " + duration + " ,url:" + mUri);
        return duration;
    }
}
