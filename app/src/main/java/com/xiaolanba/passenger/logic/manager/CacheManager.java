package com.xiaolanba.passenger.logic.manager;

import com.framework.common.utils.IFileUtil;
import com.framework.manager.ICacheManager;
import com.xiaolanba.commonlib.location.LocationBean;
import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.logic.control.LBController;

import java.io.File;


/**
 * 项目缓存管理类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class CacheManager extends ICacheManager {

    public static final String SD_SPLASH_DIR = SD_IMAGE_DIR + File.separator + "splash";

    /**
     * 本地存储管理类
     */
    private SharedPreferencesManager spfManager;
    /**
     * 登录用户
     */
    private User loginUser;

    /**
     * 服务器时间和本地时间差值
     */
    public long serverTimeDvalue;

    public void setServerTime(long serverTime) {
        if (serverTime > 0) {
            serverTimeDvalue = System.currentTimeMillis() - serverTime;
        }
    }

    /**
     * 最后定位时间
     */
    public long lastLocationTime;


    public CacheManager() {
        super();

        spfManager = new SharedPreferencesManager();

    }

    public static void clearUploadFile() {
        IFileUtil.deleteDirectory(SD_IMAGE_CHOOSE_DIR, false);
        IFileUtil.deleteDirectory(SD_IMAGE_COMPRESS_DIR, false);
    }

    /**
     * 判断用户是否是登录状态
     */
    public boolean hasLogin(){
        if (null == loginUser){
            getLoginUser();
        }
        return loginUser.member_id != 0;
    }

    public User getLoginUser() {
        if (null == loginUser) {
            LBController controller = LBController.getInstance();
            loginUser = controller.getDaoManager().getDaoSession().getLoginUserDao().queryLoginUser();
        }
        if (null == loginUser) {
            loginUser = new User();
        }
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }


    public SharedPreferencesManager getSharedPreferencesManager() {
        return spfManager;
    }

    /**
     * 获取保存的经纬度和城市名
     */
    public LocationBean getMyLocation() {
        LocationBean locationBean = null;
        double latitude = spfManager.read(SharedPreferencesManager.KEY_LATITUDE, 0f);
        double longitude = spfManager.read(SharedPreferencesManager.KEY_LONGITUDE, 0f);
        String cityCode = spfManager.read(SharedPreferencesManager.KEY_CITY_NAME, "0");
        if (latitude != 0 && longitude != 0) {
            locationBean = new LocationBean(latitude, longitude, cityCode);
        }
        return locationBean;
    }

    /**
     * 保存当前经纬度和城市名
     */
    public void saveMyLocation(double latitude, double longitude, String city) {
        lastLocationTime = System.currentTimeMillis();
        spfManager.save(SharedPreferencesManager.KEY_LATITUDE, (float) latitude);
        spfManager.save(SharedPreferencesManager.KEY_LONGITUDE, (float) longitude);
        spfManager.save(SharedPreferencesManager.KEY_CITY_NAME, city);
    }

}
