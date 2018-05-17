package com.xiaolanba.passenger.logic.control;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.xiaolanba.commonlib.location.GdLocationManager;
import com.xiaolanba.commonlib.push.PushManager;
import com.xiaolanba.passenger.LBApplication;
import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.logic.manager.CacheManager;
import com.xiaolanba.passenger.logic.manager.DaoManager;
import com.xiaolanba.passenger.logic.manager.ListenerManager;
import com.xiaolanba.passenger.logic.manager.PageManager;
import com.xiaolanba.passenger.module.main.activity.MainActivity;

import java.util.ListIterator;
import java.util.Stack;


/**
 * 小蓝巴多业务主控制器
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class LBController {

    private static LBController instance;

    /**
     * Hanldr Looper
     */
    public static Handler MainHandler = new Handler(Looper.getMainLooper());

    /**
     * 缓存管理
     */
    private CacheManager cacheManager;

    /**
     * 数据库操作管理
     */
    private DaoManager daoManager;

    /**
     * 页面管理
     */
    private PageManager pageManager;

    /**
     * 监听管理
     */
    private ListenerManager listenerManager;

    /**
     * 定位管理类
     */
    private GdLocationManager locationManager;

    public LBController() {
        cacheManager = new CacheManager();
        daoManager = new DaoManager();
        pageManager = new PageManager();
        listenerManager = new ListenerManager();
        locationManager = new GdLocationManager();

    }

    public static LBController getInstance() {
        synchronized (LBController.class) {
            if (null == instance) {
                instance = new LBController();
            }
        }
        return instance;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public DaoManager getDaoManager() {
        return daoManager;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public PageManager getPageManager() {
        return pageManager;
    }

    public GdLocationManager getLocationManager() {
        return locationManager;
    }

    /**
     * 登录成功
     */
    public void loginSuccess(User loginUser) {
        if (loginUser != null && loginUser.member_id != 0) {
            cacheManager.setLoginUser(loginUser);
            LBApplication.getInstance().httpParams.token = loginUser.token;
            daoManager.getDaoSession().getLoginUserDao().login(loginUser);
            PushManager.setJpushAlias(LBApplication.getInstance(),String.valueOf(loginUser.member_id));
        }
    }

    /**
     * 用户修改昵称、头像、手机号等，同步更新一下
     */
    public void updateUser(User loginUser) {
        if (loginUser != null && loginUser.member_id != 0) {
            cacheManager.setLoginUser(loginUser);
            LBApplication.getInstance().httpParams.token = loginUser.token;
            daoManager.getDaoSession().getLoginUserDao().updateLoginUser(loginUser);
        }
    }

    /**
     * 登出
     */
    public void logout() {
        daoManager.getDaoSession().getLoginUserDao().logout();
        cacheManager.setLoginUser(null);
        LBApplication.getInstance().httpParams.token = null;
        PushManager.clearAlias(LBApplication.getInstance());
    }

    /**
     * 清除MainActivity以外的页面
     */
    public void clearPageNoMainActivity(){
        Stack<Activity> pageStack =  pageManager.getPageStack();
        if (pageStack != null && !pageStack.isEmpty()) {
            ListIterator<Activity> iterator = pageStack.listIterator();
            while (iterator.hasNext()){
                Activity activity = iterator.next();
                if (!(activity instanceof MainActivity)) {
                    activity.finish();
                    iterator.remove();
                }
            }
        }
    }

    public void clear() {
        pageManager.clearPage();
    }

}
