package com.xiaolanba.passenger.library.share.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xiaolanba.passenger.library.share.BaseUManager;

/**
 * 
 * @author xutingz
 * @company xiaolanba.com
 */
public class WXCallbackActivity extends Activity implements IWXAPIEventHandler {
	
    private IWXAPI wxApi;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

		wxApi = WXAPIFactory.createWXAPI(this, BaseUManager.WXAPPID, false);
		wxApi.registerApp(BaseUManager.WXAPPID);

		wxApi.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
		if (wxApi != null) {
			wxApi.handleIntent(intent, this);
		}
	}
	
	@Override
	public void onReq(BaseReq baseReq) {
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		WXManager.getInstance().onResp(resp);
		finish();
	}
}