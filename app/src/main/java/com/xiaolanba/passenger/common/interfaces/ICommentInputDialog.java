package com.xiaolanba.passenger.common.interfaces;

/**
 * @author xutingz
 * @description 评论弹出框接口
 */

public interface ICommentInputDialog {
    void showCommentInputDialog(boolean showEmoji, final String content, Object param);

    void dismissCommentInputDialog();
}
