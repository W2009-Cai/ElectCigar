package com.xiaolanba.passenger.common.interfaces;

import com.framework.common.utils.IImageUtil;
import com.xiaolanba.passenger.common.view.MediaChooseDialog;

/**
 * @author xutingz
 * @description 图片浏览
 */

public interface IMediaChooseDialog {
    void showMediaChooseDialog(String title, boolean needCrop, IImageUtil.CropOutParam cropOutParam, MediaChooseDialog.MediaChooseListener mediaChooseListener);

    void showMediaChooseDialog(String title, MediaChooseDialog.MediaChooseListener mediaChooseListener);

    void dismissMediaChooseDialog();
}
