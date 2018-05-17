package com.xiaolanba.commonlib.location;

import com.amap.api.location.AMapLocation;

/**
 * 定位回调
 *
 * @author xutingz
 */
public interface LocationCallBack {
	
	public static final int NOT_OPENED = 1;
	public static final int TIME_OUT = 2;

	/**
	 * 定位成功
	 * 
	 * @param location
	 */
	void locationSuccess(LocationBean location, AMapLocation aMapLocation);

	/**
	 * 定位失败返回
	 */
	void locationFail(int errorCode,String errorInfo);

}
