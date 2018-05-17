package com.xiaolanba.passenger.common.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.framework.common.utils.ILog;
import com.xlb.elect.cigar.R;

/**
 * 显示一个圆形转圈的view
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class ProgressLayout extends FrameLayout {

    protected ImageView mLoadingImg;
    protected TextView mLoadingTxt;
    private AnimationDrawable animationDrawable;
    public ProgressLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_layout, this, false);
        addView(view);
        initView(view);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ILog.lw("invalid click!");//屏蔽掉点击事件
            }
        });
    }

    private void initView(View rootView) {
        mLoadingImg = (ImageView) rootView.findViewById(R.id.loading_img);
        mLoadingTxt = (TextView) rootView.findViewById(R.id.loading_txt);
        animationDrawable = (AnimationDrawable) mLoadingImg.getDrawable();
        if (animationDrawable != null){
            animationDrawable.start();
        }
    }

    private void show() {
        setVisibility(VISIBLE);
        if (animationDrawable != null){
            animationDrawable.start();
        }
    }

    private void hide() {
        setVisibility(GONE);
        if (animationDrawable != null){
            animationDrawable.stop();
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            show();
        } else {
            hide();
        }
    }

    /**
     * 设置下方的文字
     * @param text
     */
    public void setText(String text){
        if (text == null){
            mLoadingTxt.setVisibility(View.GONE);
        } else {
            mLoadingTxt.setVisibility(View.VISIBLE);
            mLoadingTxt.setText(text);
        }
    }

    /**
     * 设置加载中提示文字的大小
     * @param spValue
     */
    public void setTextSize(int spValue){
        mLoadingTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,spValue);
    }

    /**
     * 重设图片的宽高
     * @param width
     */
    public void setImageWidth(int width) {
        LayoutParams params = (LayoutParams) mLoadingImg.getLayoutParams();
        params.width = width;
        params.height = width;
        mLoadingImg.setLayoutParams(params);
    }

}
