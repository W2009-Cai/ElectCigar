package com.xiaolanba.passenger.common.bean;

import java.io.Serializable;

/**
 * 版本升级模型
 *
 * @author xutingz
 */
public class UpdateInfo implements Serializable {

    public String versionName = "1.0.0";
    public int versionCode = 1;
    public String title = "发现新版本";       //升级标题
    public String description = "新版本提示";//升级简介
    public long fileSize;     //升级大小
    public String downUrl = "https://xiaolanba.oss-cn-shenzhen.aliyuncs.com/xiaolanba.apk";  //升级下载地址
    public boolean isForce = false;//升级是否强制

    public String replaceVersionName;

    public String getApkVerStr() {
        if (replaceVersionName == null) {
            replaceVersionName = replaceVersionName();
        }
        return "xlb_" + versionCode + "_" + replaceVersionName + ".apk";
    }

    private String replaceVersionName() {
//        String replaceVer = versionName.replaceAll(".", "");
        return versionName;
    }

}
