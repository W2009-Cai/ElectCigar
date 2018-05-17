package com.xiaolanba.passenger.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.framework.common.utils.ILog;
import com.xiaolanba.passenger.logic.control.LBController;
import com.xiaolanba.passenger.module.main.activity.LanucherActivity;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/25
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            ILog.e("BootBroadcastReceiver","---onReceive:BOOT_COMPLETED");
            LBController.MainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ILog.e("BootBroadcastReceiver","--- 2秒之后 onReceive:BOOT_COMPLETED");
                    Intent mainActivityIntent = new Intent(context, LanucherActivity.class);  // 要启动的Activity
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mainActivityIntent);
                }
            }, 2000);

        }
    }
}
