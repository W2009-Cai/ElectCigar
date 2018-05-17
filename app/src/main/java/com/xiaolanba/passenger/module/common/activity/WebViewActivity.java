package com.xiaolanba.passenger.module.common.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framework.common.utils.ILog;
import com.xlb.elect.cigar.R;

/**
 * 加载网页的webview
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/03/28
 */

public class WebViewActivity extends BaseWebViewActivity {
    private static final String WEB_URL = "webUrl";
    private FrameLayout mWebLayout;
    private ProgressBar mProgressBar;
    private TextView mTitle;
    /**
     * webview跳转入口
     * @param context
     * @param url
     */
    public static void startActivity(Context context,String url){
        Intent intent = new Intent(context,WebViewActivity.class);
        intent.putExtra(WEB_URL,url);
        context.startActivity(intent);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_webview_layout);
        Intent intent = getIntent();
        if (intent != null) {
            webUrl = intent.getStringExtra(WEB_URL);
        }
    }

    @Override
    public void findView() {
        ImageButton mLeftBtn = (ImageButton) findViewById(R.id.left_img_btn);
        mLeftBtn.setVisibility(View.VISIBLE);
        mLeftBtn.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.title_txt);
        mWebLayout = (FrameLayout) findViewById(R.id.webview_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWebView = new WebView(context); //webview在Activity中创建才能即时销毁，写在xml里面会导致销毁不及时
        setWebView();
        FrameLayout.LayoutParams paramsw = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mWebLayout.addView(mWebView, paramsw);
    }

    @Override
    public void initData() {
        loadUrl();
    }

    protected void loadUrl() {
        try {
            ILog.i(TAG, "---webUrl=" + webUrl);
            mWebView.loadUrl(webUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载进度
     *
     * @param newProgress
     */
    public void setProgressBar(int newProgress) {
        if (mProgressBar != null) {
            if (newProgress >= 100) {
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
            } else {
                if (mProgressBar.getVisibility() == View.GONE)
                    mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
        }
    }

    @Override
    protected void onWebProgressChanged(WebView view, int newProgress) {
        super.onWebProgressChanged(view, newProgress);
        setProgressBar(newProgress);
    }

    @Override
    protected void onWebReceivedTitle(WebView view, String t) {
        mTitle.setText(t == null ? "":t);
        super.onWebReceivedTitle(view, t);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_img_btn:
                finish();
                break;
            default:
                break;
        }
    }
}
