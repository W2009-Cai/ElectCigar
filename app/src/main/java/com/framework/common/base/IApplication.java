package com.framework.common.base;

import android.app.Application;
import android.content.res.Configuration;

import com.framework.common.utils.ILog;

/**
 * Application 基类
 * @author xutingz
 * @company xiaolanba.com
 */
public class IApplication extends Application {

	private final String TAG = getClass().getCanonicalName();
	
	@Override
	public void onCreate() {
		super.onCreate();
		ILog.d(TAG, "Application初始化");
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		ILog.w(TAG, "IApplication.onConfigurationChanged:配置改变");
		
		super.onConfigurationChanged(newConfig);
		
	}
	
	@Override
	public void onTerminate() {
		// 当终止应用程序对象时调用，不保证一定被调用，当程序是被内核终止以便为其他应用程序释放资源，
		// 那么将不会提醒，并且不调用应用程序的对象的onTerminate方法而直接终止进程
		
		ILog.e(TAG, "IApplication.onTerminate:程序中止");
		
		super.onTerminate();
		
	}
	
	@Override
	public void onLowMemory() {
		// 当后台程序已经终止资源还匮乏时会调用这个方法。
		// 好的应用程序一般会在这个方法里面释放一些不必要的资源来应付当后台程序已经终止，前台应用程序内存还不够时的情况。
		
		ILog.w(TAG, "IApplication.onLowMemory:内存不够");
//		ImageLoader.getInstance().clearMemoryCache();
		super.onLowMemory();
		
	}

}
