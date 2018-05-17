package com.xiaolanba.commonlib.location;

import android.text.TextUtils;

import com.amap.api.location.AMapLocation;

import java.io.Serializable;

/**
 * 定位信息实体
 *
 * @author xutingz
 */
public class LocationBean implements Serializable {

    private static final long serialVersionUID = 6947079497970717772L;

    public double latitude;                        // 纬度
    public double longitude;                       // 经度
    public String province;                         // 省
    public String city;                             // 城市
    public String district;                         // 区域
    public String adcode;                           // 区域编码
    public String desc;                             // 详细地址
    public float accuracy;                         // 精度

    // 此字段提供给后台使用
    public String cityCode = "0";                // 城市编码

    public LocationBean(AMapLocation location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            province = location.getProvince();
            city = location.getCity();
            district = location.getDistrict();
            adcode = location.getAdCode();
            desc = location.getAddress();
            if (!TextUtils.isEmpty(adcode) && adcode.length() >= 4) {
                // 行政区代码只精确到省市
                cityCode = adcode.substring(0, 4) + "00";
//                CLog.i("location", "cityCode:" + cityCode);
            }
        }
    }

    public LocationBean(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationBean(double latitude, double longitude, String cityCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityCode = cityCode;
    }

}
