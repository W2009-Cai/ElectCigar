package com.xiaolanba.commonlib.statistics;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;

/**
 * 统计业务相关管理类
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/03/05
 */

public class StatisticManager {

    /**
     * 初始化友盟统计库
     * 参数1:上下文，不能为空
     * 参数2:友盟 app key
     * 参数3:渠道号 channel
     * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
     * 参数5:Push推送业务的secret 没有推送业务为空
     */
    public static void init(Context context,String channel){
        UMConfigure.init(context, "5abca9a4f29d985c3a0000fb", channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL );
//        EScenarioType.E_UM_NORMAL 普通统计场景类型
//        EScenarioType.E_UM_GAME 游戏场景类型
    }

    /**
     * 设置组件化的Log开关
     * 参数: boolean 默认为false，如需查看LOG设置为true
     */
    public static void setLogEnable(boolean flag){
        UMConfigure.setLogEnabled(flag);
    }

    /**
     * 默认是以设备为统计依据的，如果要用登录账户的id统计请调这个方法
     * @param userId
     */
    public static void setSignIn(String userId){
        MobclickAgent.onProfileSignIn(userId);
    }

    /**
     * 对于发送到后台的数据是否加密，默认不加密
     * @param encrypt
     */
    public static void setEncrypt(boolean encrypt){
        UMConfigure.setEncryptEnabled (encrypt);
    }

    /**
     * 自定义事件
     * @param context
     * @param eventId
     */
    public static void onEvent(Context context, String eventId){
        MobclickAgent.onEvent(context, eventId);
    }

    /**
     * 某个事件有多个属性时，设置各个属性的次数。
     * @param context
     * @param eventId
     * @param map
     */
    public static void onEvent(Context context, String eventId, HashMap map){
        MobclickAgent.onEvent(context,eventId,map);
    }

    /**
     * 如果开发者调用 Process.kill 或者 System.exit 之类的方法杀死进程，请务必在此之前调用此方法，用来保存统计数据。
     * @param context
     */
    public static void onKillProcess(Context context){
        MobclickAgent.onKillProcess(context);
    }
}
