package com.xiaolanba.passenger.library.share;

import android.text.TextUtils;

import com.xiaolanba.passenger.logic.manager.SettingManager;

/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class ShareInfo {
    public String title;
    public String content;
    public String contentUrl, shortContentUrl;
    public Object imgUrl;
    public long bussinessId;  //分享的业务id
    public int shareType;     //分享的模块类型

    /**
     * 是否是纯图分享模式
     */
    private boolean isImageType = false;

    public ShareInfo(String title, String content, String contentUrl, Object imgUrl,long bussinessId) {
        this.title = title;
        this.content = content;
        this.contentUrl = contentUrl;
        this.imgUrl = imgUrl;
        this.bussinessId = bussinessId;
        addParamsForContentUrl();
    }

    public String getTitle() {
        return TextUtils.isEmpty(title) ? "牙牙关注" : title;
    }

    public String getContent() {
        return TextUtils.isEmpty(content) ? "[牙牙-娱乐岂止于乐]" : content;
    }

    public String getContentUrl() {
        if (!TextUtils.isEmpty(shortContentUrl)) {
            return shortContentUrl;
        }
        return TextUtils.isEmpty(contentUrl) ? SettingManager.URL_YAYA_OFFICIAL : contentUrl;
    }

    /**
     * 设置纯图分享
     *
     * @param bool
     */
    public void setImageShare(boolean bool) {
        isImageType = bool;
    }

    public boolean isImageType() {
        return isImageType && imgUrl != null;
    }

    public void addParamsForContentUrl() {
        //如果需要对分享的url统一加上时间戳，请修改这里
//        if(contentUrl.contains("#/")){
//            contentUrl = contentUrl + "?t=" + IDateFormatUtil.getTimeMillis();
//        } else {
//            Map<String,String> map = new HashMap<>();
//            map.put("t", String.valueOf(IDateFormatUtil.getTimeMillis()));
//            contentUrl = ICommonUtil.getParamsUrl(contentUrl, map);
//        }
    }
}
