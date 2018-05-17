package com.framework.library.swipedismiss;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.framework.common.utils.IDisplayUtil;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.logic.control.LBController;

/**
 * 含viewpager的Activity，可上下滑动view使Activity消失
 * 滑动的是Activity中指定的某个view，而不是整个Activity
 *
 * @author xutingz
 */
public abstract class BaseDismissViewpagerActivity extends BaseActivity implements OnDismissListener, SwipeToDismissListener.OnViewMoveListener {
    /**
     * 套在最外层的viewgroup
     */
    private ViewGroup dismissContainer;
    protected View mBackgroundView;
    protected View mExtraView;//除了黑色背景外，还需要额外透明度渐变的view
    protected FrameLayout mDismissFrameLayout;//包含viewpager的父viewgroup
    protected MultiTouchViewPager mPager;
    private SwipeDirectionDetector directionDetector;
    private SwipeToDismissListener swipeDismissListener;
    private SwipeDirectionDetector.Direction direction;

    private boolean wasScaled;
    private boolean isSwipeToDismissAllowed = true;

    private int screenHeight;
    private int dip48;
    private int dip4;

    /**
     * 子类的findView要调用super
     */
    @Override
    public void findView() {
        initBaseNeedView();
    }

    @Override
    public void initData() {
        initGestureListeners();
        screenHeight = IDisplayUtil.getScreenHeight(this);
        dip48 = IDisplayUtil.dip2px(this, 48);
        dip4 = IDisplayUtil.dip2px(this, 4);
    }

    /**
     * 初始化父类所必须的View，子类必须实现
     */
    protected abstract void initBaseNeedView();

    /**
     * photoView是否未被放大
     *
     * @return
     */
    public abstract boolean isItemSizeNormal();

    /**
     * 点击item
     */
    public abstract void onSingTapListener();

    /**
     * 设置滑动消失必须的一些view
     *
     * @param dismissContainer
     * @param mBackgroundView
     * @param mDismissFrameLayout
     * @param mPager
     */
    protected void setSwipeMustView(ViewGroup dismissContainer, View mBackgroundView, FrameLayout mDismissFrameLayout,
                                    MultiTouchViewPager mPager, View mExtraView) {
        this.dismissContainer = dismissContainer;
        this.mBackgroundView = mBackgroundView;
        this.mDismissFrameLayout = mDismissFrameLayout;
        this.mPager = mPager;
        this.mExtraView = mExtraView;
    }

    public void initGestureListeners() {
        swipeDismissListener = new SwipeToDismissListener(mDismissFrameLayout, this, this);
        dismissContainer.setOnTouchListener(swipeDismissListener);

        directionDetector = new SwipeDirectionDetector(this) {
            @Override
            public void onDirectionDetected(Direction direction1) {
                direction = direction1;
            }
        };
    }

    /**
     * viewpager Dismiss监听
     */
    @Override
    public void onDismiss() {
        finishNoAnim();
    }

    /**
     * 滑动距离及百分比监听
     */
    @Override
    public void onViewMove(float translationY, int translationLimit) {
        float rate = (1.0f / translationLimit / 8) * Math.abs(translationY);
        float alpha = 1.0f - (1.7f * rate) * (1.7f * rate);
        if (alpha > 1) {
            alpha = 1;
        } else if (alpha < 0) {
            alpha = 0;
        }
        if (mBackgroundView != null) {
            mBackgroundView.setAlpha(alpha);
        }
        if (mExtraView != null) { //bottom的透明速度快一倍
            float extraAlpah = 0.95f - (2.6f * rate) * (2.5f * rate);
            if (extraAlpah > 1) {
                extraAlpah = 1;
            } else if (extraAlpah < 0) {
                extraAlpah = 0;
            }
            mExtraView.setAlpha(extraAlpah);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        onUpDownEvent(event);

        if (direction == null) {
            if (event.getPointerCount() > 1) {
                wasScaled = true;
                return mPager.dispatchTouchEvent(event);
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            isInDownloadArea = false;
        }
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        if (rawX > 0 && rawX <= dip48 && rawY <= screenHeight && rawY > (screenHeight - dip48)) { //点击了下载按钮区域
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isInDownloadArea = true;
            }
            return super.dispatchTouchEvent(event);
        } else {
            if (isItemSizeNormal()) { //判断是否放大，true为未放大
                directionDetector.onTouchEvent(event);
                if (direction != null) {
                    switch (direction) {
                        case UP:
                        case DOWN:
                            if (isSwipeToDismissAllowed && !wasScaled && mPager.isScrolled()) {
                                return swipeDismissListener.onTouch(dismissContainer, event);
                            } else break;
                        case LEFT:
                        case RIGHT:
                            return mPager.dispatchTouchEvent(event);
                    }
                }
                return true;
            } else {
                return super.dispatchTouchEvent(event);
            }
        }
    }

    private void onUpDownEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            onActionUp(event);
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onActionDown(event);
        }
    }

    private float downX, downY;
    private long downTimeMill, upTimeMill;
    private boolean onClickWhenUp = false;//ActionUp的时候，是否需要判断满足点击事件的条件
    private boolean isInDownloadArea = false;//是否点击在下载按钮的区域

    private void onActionDown(MotionEvent event) {
        onClickWhenUp = false;
        direction = null;
        wasScaled = false;

        downX = event.getX();
        downY = event.getY();
        downTimeMill = System.currentTimeMillis();
        if (downTimeMill - upTimeMill > 180) {
            onClickWhenUp = true;
        } else {
            onClickWhenUp = false;
        }

        mPager.dispatchTouchEvent(event);
        swipeDismissListener.onTouch(dismissContainer, event);
    }

    private void onActionUp(MotionEvent event) {
        float upX = event.getX();
        float upY = event.getY();
        upTimeMill = System.currentTimeMillis();
        if (!isInDownloadArea && onClickWhenUp && Math.abs(upX - downX) < dip4 && Math.abs(upY - downY) < dip4 && (upTimeMill - downTimeMill) < 180) { //模拟发送点击事件
            final long tempTimeMill = downTimeMill;
            LBController.MainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (downTimeMill == tempTimeMill) {
                        onSingTapListener();
                    }
                }
            }, 180);
        }

        swipeDismissListener.onTouch(dismissContainer, event);
        mPager.dispatchTouchEvent(event);
    }

    /**
     * 带消失动画的finish
     */
    protected void finishWidthAnim() {
        superFinish();
        overridePendingTransition(0, 0);
    }

    /**
     * 去掉系统自带的消失动画
     */
    protected void finishNoAnim() {
        superFinish();
        overridePendingTransition(0, 0);
    }

}
