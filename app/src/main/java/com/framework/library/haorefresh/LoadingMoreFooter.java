package com.framework.library.haorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaolanba.passenger.LBApplication;
import com.xlb.elect.cigar.R;
import com.xiaolanba.passenger.common.utils.ResUtil;
import com.xiaolanba.passenger.logic.control.LBController;

/**
 *  底部加载更多的布局
 */
public class LoadingMoreFooter extends LinearLayout {

    private Context context;
    private LinearLayout loading_view_layout;
    private LinearLayout end_layout;
    private TextView mNoMoreTxt;
    public LoadingMoreFooter(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public LoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.hao_refresh_footer_layout,
                null);
        loading_view_layout = (LinearLayout) view.findViewById(R.id.loading_view_layout);
        end_layout = (LinearLayout) view.findViewById(R.id.end_layout);

        ProgressView progressView = new ProgressView(LBApplication.getInstance());
        progressView.setIndicatorId(ProgressView.BallPulse);
        progressView.setIndicatorColor(ResUtil.getColor(R.color.theme_green));
        addFootLoadingView(progressView);

        mNoMoreTxt = new TextView(context);
        mNoMoreTxt.setText(ResUtil.getString(R.string.no_more_data));
        addFootEndView(mNoMoreTxt);

        addView(view);
    }


    //设置底部加载中效果
    public void addFootLoadingView(View view) {
        loading_view_layout.removeAllViews();
        loading_view_layout.addView(view);
    }

    //设置底部到底了布局
    public void addFootEndView(View view) {
        end_layout.removeAllViews();
        end_layout.addView(view);
    }


    //设置已经没有更多数据
    public void setEnd() {
        setVisibility(VISIBLE);
        loading_view_layout.setVisibility(GONE);
        end_layout.setVisibility(VISIBLE);
        mNoMoreTxt.setAlpha(0); //暂时先隐藏，然后再显示
        LBController.MainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mNoMoreTxt != null){
                    mNoMoreTxt.setAlpha(1f);
                }
            }
        }, 200);
    }

    /**
     * 清除没有更多状态
     */
    public void clearShowEnd(){
        if (isShowEnd()) {
            loading_view_layout.setVisibility(VISIBLE);
            end_layout.setVisibility(GONE);
        }
    }

    public boolean isShowEnd(){
        return end_layout.getVisibility() == View.VISIBLE;
    }


    public void setVisible(){
        setVisibility(VISIBLE);
        loading_view_layout.setVisibility(VISIBLE);
        end_layout.setVisibility(GONE);
    }


    public void setGone(){
        setVisibility(GONE);
    }


}
