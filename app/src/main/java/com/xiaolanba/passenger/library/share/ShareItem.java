package com.xiaolanba.passenger.library.share;

/**
 * 分享的单个item模型
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/03
 */

public class ShareItem {

    public int mImgId;
    public String mItemName;
    public int type;

    public ShareItem(int mImgId, String mItemName, int type) {
        this.mImgId = mImgId;
        this.mItemName = mItemName;
        this.type = type;
    }
}
