package com.xiaolanba.passenger.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.text.TextUtils;

import com.framework.common.utils.IAppUtil;
import com.framework.common.utils.IDisplayUtil;
import com.xiaolanba.passenger.LBApplication;
import com.xiaolanba.passenger.logic.control.LBController;

import java.util.HashMap;
import java.util.Map;

/**
 * Http 请求头需要获取的系统参数
 *
 * @author xutingz
 */
public class HttpParams {

    public String androidid;
    public String imsi;
    public String imei;
    public int versionCode;
    public String versionName;
    public String src;// 分辨率
    public String channelId; // 渠道号
    public String density;
    public String systemVer;
    public String token;

    public HttpParams() {

    }

    public HttpParams init(Context context) {

        try {
            androidid = IAppUtil.getAndroidId(context);

            PackageManager pm = context.getPackageManager();
            try {
                PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
                versionCode = info.versionCode;
                versionName = info.versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            imei = IAppUtil.getDeviceId(context);
            if (TextUtils.isEmpty(imei)) {
                imei = "000000000000000";
            }
            imsi = IAppUtil.getSubscriberId(context);
            if (TextUtils.isEmpty(imsi)) {
                imsi = "000000000000000";
            }
            src = IDisplayUtil.getScreenWidth(context) + "x" + IDisplayUtil.getScreenHeight(context);
            channelId = LBApplication.getInstance().getChannelValue();
            density = context.getResources().getDisplayMetrics().density + "";
            systemVer = Build.VERSION.RELEASE;
            token = LBController.getInstance().getCacheManager().getLoginUser().token;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public Map<String,String> getHeaderMap(){
        Map<String,String> map = new HashMap<>();
        map.put("token",token == null?"":token); //因为token可能会因为修改密码、修改手机号等原因被重置，所以每次都new一个新的Map
        return map;
    }

    public Map<String,String> getEmptyMap(){ //有些接口可能不需要公共参数，new一个空的Map
        return  new HashMap<String,String>();
    }
}
