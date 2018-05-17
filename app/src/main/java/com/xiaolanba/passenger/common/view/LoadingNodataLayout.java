package com.xiaolanba.passenger.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xlb.elect.cigar.R;

/**
 * 无数据提示界面、加载中的公共布局
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/03/20
 */

public class LoadingNodataLayout extends FrameLayout {
    private LinearLayout mRootLayout;
    private ImageView mIcon;
    private TextView mTipTxt;
    private ProgressLayout mProgressLayout;
    public boolean mFlagIsloading;//是否是加载中的状态

    public LoadingNodataLayout(Context context) {
        super(context);
        init(context);
    }

    public LoadingNodataLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingNodataLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.no_data_layout, null);
        mRootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
        mIcon = (ImageView) view.findViewById(R.id.no_data_icon);
        mTipTxt = (TextView) view.findViewById(R.id.no_data_txt);
        mProgressLayout = (ProgressLayout) view.findViewById(R.id.progress_layout);
        mProgressLayout.setVisible(false);
        addView(view);
    }

    /**
     * 是否显示转圈，true是转圈，false是显示无数据、无结果界面
     *
     * @param flag
     */
    public void setIsLoadingUi(boolean flag) {
        mFlagIsloading = flag;
        if (flag) {
            mProgressLayout.setVisible(true);
            mIcon.setVisibility(View.GONE);
            mTipTxt.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
        } else {
            mProgressLayout.setVisible(false);
            mIcon.setVisibility(View.VISIBLE);
            mTipTxt.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置为上下居中样式
     */
    public void setCenterGravity() {
        mRootLayout.setGravity(Gravity.CENTER);
        setMarginTop(0);
    }

    /**
     * 设置上边距
     * @param marginTop
     */
    public void setMarginTop(int marginTop) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mIcon.getLayoutParams();
        layoutParams.topMargin = marginTop;
        mIcon.setLayoutParams(layoutParams);
    }

    /**
     * 设置背景颜色
     * @param color
     */
    public void setBackgroundColor(int color) {
        mRootLayout.setBackgroundColor(color);
    }

    /**
     * 设置无数据提示的文字
     * @param title
     */
    public void setTitle(String title) {
        mTipTxt.setText(title);
    }

    /**
     * 设置无数据提示的icon
     * @param drawableId
     */
    public void setIcon(int drawableId) {
        mIcon.setImageResource(drawableId);
    }
}
