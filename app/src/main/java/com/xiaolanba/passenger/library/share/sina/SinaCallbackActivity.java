package com.xiaolanba.passenger.library.share.sina;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;

/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class SinaCallbackActivity extends Activity implements IWeiboHandler.Response {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SinaManager.getInstance().onCreate(getIntent(), savedInstanceState, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO 暂时把回调放入onResume
        SinaManager.getInstance().onNewIntent(getIntent(), this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        SinaManager.getInstance().onNewIntent(intent, this);
    }


    @Override
    public void onResponse(BaseResponse baseResp) {
        SinaManager.getInstance().onResponse(baseResp);
        finish();
    }
}
