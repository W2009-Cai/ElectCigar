package com.xiaolanba.passenger.module.common.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.framework.common.utils.ILog;
import com.xiaolanba.passenger.common.base.BaseActivity;

/**
 *  webview基类，可满足子类webview的不同配置需求
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/03/28
 */

public abstract class BaseWebViewActivity extends BaseActivity {

    protected WebView mWebView;
    protected boolean isPageFinished=false;
    protected boolean isReceivedError=false;
    protected String webUrl;
    /**
     * 初始化设置webview
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    protected void setWebView(){
        if (mWebView != null) {
            mWebView.setVerticalScrollBarEnabled(false);
            mWebView.setScrollbarFadingEnabled(false);
            mWebView.setDownloadListener(new MyWebViewDownLoadListener());//设置下载监听
            final WebSettings webSettings = mWebView.getSettings();
            webSettings.setBuiltInZoomControls(false);
            webSettings.setDomStorageEnabled(true);
            webSettings.setUseWideViewPort(false);// 禁止缩放
            webSettings.setSupportZoom(false);
            try{
                webSettings.setJavaScriptEnabled(true);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            webSettings.setAllowFileAccess(true);
            webSettings.setBlockNetworkImage(Build.VERSION.SDK_INT == 19 ? false : true);
            // 链接是https的， 资源是http的，出现错误 This request has been blocked; the content must be served over HTTPS.
            if (Build.VERSION.SDK_INT >= 21) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String t) {
                    if (isFinishing()) return;
                    onWebReceivedTitle(view, t);
                    super.onReceivedTitle(view, t);
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (isFinishing()) return;
                    onWebProgressChanged(view, newProgress);
                }

                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    ILog.i(TAG, "----Console日志");
                    onWebConsoleMessage(consoleMessage);
                    return super.onConsoleMessage(consoleMessage);
                }

            });
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                    onWebLoadResource(view, url);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(url.startsWith("http:") || url.startsWith("https:")) {
                        return false;
                    }
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    isPageFinished=true;
                    onWebPageFinished(view,url);
                    webSettings.setBlockNetworkImage(false);

                }
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    isPageFinished=false;
                    onWebPageStarted(view,url,favicon);
                }
                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    if(isFinishing()){
                        return;
                    }
                    mWebView.stopLoading();
                    onWebReceivedError(view, errorCode, description, failingUrl);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();  // 接受信任所有网站的证书  // Ignore SSL certificate errors
                }
            });
            mWebView.setSaveEnabled(false);
        }
    }

    /**
     * 加载错误页面
     */
    protected void loadErrorPage(){
        isReceivedError = true;
    }

    protected void onWebReceivedError(WebView view, int errorCode,String description, String failingUrl){
        loadErrorPage();
    }

    /**
     * 加载进度， 子类中可实现此方法，自行添加处理
     */
    protected void onWebProgressChanged(WebView view, int newProgress){}

    /**
     * console 日志
     * @param consoleMessage
     */
    protected void onWebConsoleMessage(ConsoleMessage consoleMessage){}

    protected void onWebReceivedTitle(WebView view, String t){}

    protected void onWebPageFinished(WebView view, String url){}

    protected void onWebPageStarted(WebView view, String url, Bitmap favicon){}

    protected void onWebLoadResource(WebView view, String url) {}

    /**
     * webview的下载监听
     */
    private class MyWebViewDownLoadListener implements DownloadListener {
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            if (url == null) {
                return;
            }
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.resumeTimers();
            mWebView.onResume();
            try {
                mWebView.getClass().getMethod("onResume").invoke(mWebView,(Object[])null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.pauseTimers();
            mWebView.onPause();
            try {
                mWebView.getClass().getMethod("onPause").invoke(mWebView,(Object[])null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (mWebView != null) {
                mWebView.stopLoading();
                mWebView.getSettings().setJavaScriptEnabled(false);
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if(parent != null){
                    parent.removeAllViews();
                }
                mWebView.removeAllViews();
                mWebView.clearCache(true);
                mWebView.clearHistory();
                mWebView.setWebChromeClient(null);
                mWebView.setWebViewClient(null);
                mWebView.setVisibility(View.GONE);
                mWebView.destroy();
                mWebView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
