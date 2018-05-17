package com.xiaolanba.passenger.common.base;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;

import com.framework.common.base.IBaseActivity;
import com.framework.common.utils.IKeyboardUtils;
import com.xiaolanba.passenger.common.utils.SoftKeyBoardListener;
import com.xiaolanba.passenger.logic.control.LBController;

/**
 * Activity基类
 *
 * @author xutingz
 */
public abstract class BaseActivity extends IBaseActivity {

    protected LBController controller;

    /**
     * 是否更改状态栏背景颜色
     */
    public boolean changeSystemBarColor = false;
    /**
     * 是否设置键盘监听
     */
    public boolean settingSoftListener = true;
    protected boolean isSoftShowing = false;

    protected boolean isResumed = false;//判断是否离开

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        controller = LBController.getInstance();
        controller.getPageManager().addPage(this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        super.onCreate(savedInstanceState);

        changeSystemBarColor();

        setSoftListener();
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
                onShowKeyboard();
            }

            @Override
            public void keyBoardHide(int height) {
                isSoftShowing = false;
                onHideKeyboard();
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

    protected void superFinish() {
        super.finish();
    }

    @Override
    public void onClick(View v) {

    }

    public void onShowKeyboard(){

    }

    public void onHideKeyboard(){

    }


}

