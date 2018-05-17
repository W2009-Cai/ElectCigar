package com.xiaolanba.passenger.common.base;

import android.graphics.PixelFormat;
import android.os.Bundle;

import com.framework.common.base.IBaseFragmentActivity;
import com.framework.common.utils.IKeyboardUtils;
import com.xiaolanba.passenger.common.utils.SoftKeyBoardListener;
import com.xiaolanba.passenger.logic.control.LBController;

/**
 * FragmentActivity基类
 *
 * @author xutingz
 */
public abstract class BaseFragmentActivity extends IBaseFragmentActivity {

    public LBController controller;

    /**
     * 是否更改状态栏背景颜色
     */
    public boolean changeSystemBarColor = false;
    /**
     * 是否设置键盘监听
     */
    public boolean settingSoftListener = true;

    protected boolean isSoftShowing = false;

    public boolean isResumed = false;//判断是否离开

    @Override
    protected void onCreate(Bundle bundle) {
        controller = LBController.getInstance();
        controller.getPageManager().addPage(this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        super.onCreate(bundle);

        changeSystemBarColor();
    }

    @Override
    protected void onDestroy() {
        controller.getPageManager().removePage(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (controller == null) {
            controller = LBController.getInstance();
        }
        isResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    /**
     * 判断是否已登录
     * @return
     */
    protected boolean hasLogin(){
        return controller.getCacheManager().hasLogin();
    }

    /**
     * 更改状态栏背景颜色
     */
    public void changeSystemBarColor() {
        if (!changeSystemBarColor) {
            return;
        }
//		new SystemBarTintHelper().onCreate(this, true, R.color.title_background);
    }

    /**
     * 监听键盘显示，隐藏，以及高度
     */
    public void setSoftListener() {
        if (!settingSoftListener) {
            return;
        }
        SoftKeyBoardListener.setListener(context, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {

            @Override
            public void keyBoardShow(int height) {
                isSoftShowing = true;
            }

            @Override
            public void keyBoardHide(int height) {
                isSoftShowing = false;

            }
        });
    }


    @Override
    public void finish() {
        if (isFinishing()) {
            return;
        }
        if (isSoftShowing) {
            IKeyboardUtils.closeKeybord(context);
            LBController.MainHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    superFinish();
                }
            }, 200);
        } else {
            IKeyboardUtils.closeKeybord(context);
            superFinish();
        }
    }

    private void superFinish() {
        super.finish();
    }
}
