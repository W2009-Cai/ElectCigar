package com.xiaolanba.commonlib.location;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xiaolanba.commonlib.utils.CLog;

/**
 * 高德定位管理类
 *
 * @author xutingz
 */
public class GdLocationManager implements AMapLocationListener {
    public static final int TIME_OUT = 60 * 1000;

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption = null;
    private LocationCallBack mCallBack;
    private boolean needStopWhenLocation = true;

    public GdLocationManager() {

    }

    private void initLocationClient(Context context) {
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(context);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
        }
    }

    private void initLocationOptionBase() {
        if (mLocationOption == null) {
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            mLocationOption.setGpsFirst(true);// gps优先
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(false);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置定位间隔,单位毫秒,默认为10000ms
            mLocationOption.setInterval(10000);
            mLocationOption.setSensorEnable(false); //是否允许感应器
            //设置超时时间
            mLocationOption.setHttpTimeOut(TIME_OUT);
        }
    }

    /**
     * 定位一次之后就停止
     *
     * @param callBack
     */
    public void startLocationOnce(Context context, LocationCallBack callBack) {
        try {
            this.mCallBack = callBack;
            initLocationOptionBase();
            initLocationClient(context);
            mLocationClient.setLocationListener(this);
            mLocationOption.setInterval(10000);
            mLocationOption.setOnceLocation(false);
            if (mLocationClient.isStarted()) { // 已经启动定位
                mLocationClient.stopLocation();
            }
            needStopWhenLocation = true;
            mLocationClient.startLocation(); //启动定位
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按照指定的间隔启动定位
     *
     * @param callBack
     */
    public void startLocationIntevral(Context context, long intevral, LocationCallBack callBack) {
        try {
            this.mCallBack = callBack;
            initLocationOptionBase();
            initLocationClient(context);
            mLocationClient.setLocationListener(this);
            mLocationOption.setOnceLocation(false);
            mLocationOption.setInterval(intevral);
            if (mLocationClient.isStarted()) { // 已经启动定位
                mLocationClient.stopLocation();
            }
            needStopWhenLocation = false;
            mLocationClient.startLocation(); //启动定位
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  刷新地图定位蓝点，需要传入AMapLocationListener监听对象
     * @param context
     * @param intevral
     * @param mapLocationListener
     */
    public void startLocationMapUi(Context context, long intevral,AMapLocationListener mapLocationListener){
        try {
            this.mCallBack = null;
            initLocationOptionBase();
            initLocationClient(context);
            mLocationClient.setLocationListener(mapLocationListener);
            mLocationOption.setOnceLocation(false);
            mLocationOption.setInterval(intevral);
            if (mLocationClient.isStarted()) { // 已经启动定位
                mLocationClient.stopLocation();
            }
            needStopWhenLocation = false;
            mLocationClient.startLocation(); //启动定位
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    /**
     * 退出时销毁client和option
     */
    public void destory() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mCallBack = null;
        mLocationClient = null;
        mLocationOption = null;
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        try {
            if (location != null && location.getErrorCode() == 0) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                if (latitude > 0 && longitude > 0) {
                    String str = ("定位成功:(" + longitude + "," + latitude + ") 海拔 ： " + location.getAltitude()
                            + "\n精    度    :" + location.getAccuracy() + "米"
                            + "\n定位方式:" + location.getProvider()
                            + "\n位置描述:" + location.getAddress()
                            + "\n省:" + location.getProvince()
                            + "\n市:" + location.getCity()
                            + "\n城市编码1:" + location.getCityCode()
                            + "\n区(县):" + location.getDistrict()
                            + "\n区域编码:" + location.getAdCode())
                            + "\n道路:" + location.getStreet()
                            + "\n角度:" + location.getBearing();
//                    CLog.i("location", str);
                    LocationBean bean = new LocationBean(location);
                    if (mCallBack != null) {
                        mCallBack.locationSuccess(bean, location);
                    }
                }
            } else {
                // 定位错误
                CLog.i("location", "location error --> ErrCode:" + location.getErrorCode() + ", errInfo:" + location.getErrorInfo());
                if (mCallBack != null) {
                    mCallBack.locationFail(location == null ? 0 : location.getErrorCode(), location == null ? "" : location.getErrorInfo());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (needStopWhenLocation) {
            stopLocation();
        }
    }


}
