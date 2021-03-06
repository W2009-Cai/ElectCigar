package com.xiaolanba.commonlib.fresco;

import android.graphics.drawable.Animatable;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.xiaolanba.commonlib.utils.DisplayUtil;

/**
 * 单张图片显示控制器
 *
 * @author xutingz
 */
public class SingleImageControllerListener extends BaseControllerListener<ImageInfo> {

    private final SimpleDraweeView draweeView;

    public SingleImageControllerListener(SimpleDraweeView draweeView) {
        this.draweeView = draweeView;
    }

    @Override
    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
        if (imageInfo == null || draweeView == null) {
            return;
        }

        ViewGroup.LayoutParams vp = draweeView.getLayoutParams();
        int maxWidthSize = DisplayUtil.getScreenWidth(draweeView.getContext());
        int maxHeightSize = DisplayUtil.getScreenHeight(draweeView.getContext());
        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();

        if (width > height) {
            int maxWidth = DisplayUtil.dip2px(draweeView.getContext(), maxWidthSize);
            if (width > maxWidth) {
                width = maxWidth;
            }
            vp.width = width;
            vp.height = (int) (imageInfo.getHeight() / (float) imageInfo.getWidth() * vp.width);
        } else {
            // width <= height
            int maxHeight = DisplayUtil.dip2px(draweeView.getContext(), maxHeightSize);
            if (height > maxHeight) {
                height = maxHeight;
            }

            vp.height = height;
            vp.width = (int) ((float) imageInfo.getWidth() / imageInfo.getHeight() * vp.height);
        }

        draweeView.requestLayout();
    }

}

