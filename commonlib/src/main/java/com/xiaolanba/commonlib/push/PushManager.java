package com.xiaolanba.commonlib.push;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * 推送业务设置管理
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/03/05
 */

public class PushManager {
    private static final int TAG_SECQUENCE = 101;
    private static final int ALIA_SECQUENCE = 201;
    private static final int CLEAR_TAG = 301;
    private static final int CLEAR_ALIAS = 401;

    /**
     * 初始化极光推送相关配置
     */
    public static void initJPushInterface(Context context, boolean isDebug) {
        try {
            // 极光推送的init
            JPushInterface.setDebugMode(isDebug);    // 设置开启日志,发布时请关闭日志
            JPushInterface.init(context);            // 初始化 JPush
            JPushInterface.stopCrashHandler(context); //不需要错误日志收集上报
            JPushInterface.setStatisticsEnable(false); //不需要页面统计信息(已废弃)
            if (JPushInterface.isPushStopped(context)) {
                JPushInterface.resumePush(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 极光推送的setAlias
     *
     * @param userId
     */
    public static void setJpushAlias(Context context, String userId) {
        JPushInterface.setAlias(context,ALIA_SECQUENCE,userId);// 新版本的设置Alias回调在广播中处理
    }

    public static void clearAlias(Context context){
        JPushInterface.deleteAlias(context,CLEAR_ALIAS); //在广播中处理回调
    }

    public static void clearTags(Context context,Set<String> set){
        JPushInterface.deleteTags(context,CLEAR_TAG,set); //在广播中处理回调
    }

    /**
     * 设置tag
     *
     * @param tag
     */
    public static void setJpushTag(Context context, String tag) {
        Set<String> set = new HashSet<String>();
        set.add(tag);
        JPushInterface.setTags(context,TAG_SECQUENCE,set); // 新版本的设置Tag回调在广播中处理
    }
}
