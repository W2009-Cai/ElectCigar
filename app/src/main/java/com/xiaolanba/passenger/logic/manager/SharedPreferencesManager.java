package com.xiaolanba.passenger.logic.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xiaolanba.passenger.LBApplication;

import java.util.Map;

import static com.framework.common.utils.IAppUtil.getVersionName;

/**
 * 本地缓存管理
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class SharedPreferencesManager {

    public static final String KEY_VERSIONNAME = "versionName";

    // 纬度
    public static final String KEY_LATITUDE = "LATITUDE";
    // 经度
    public static final String KEY_LONGITUDE = "LONGITUDE";
    // 城市
    public static final String KEY_CITY_NAME = "CITY_NAME";

    /**
     * 存储的全局的SharedPreferences文件名
     */
    private final static String NAME = "xiaolanba.passenger";

    /**
     * 保存与用户模块相关的 sharedpreferces
     */
    public final static String  USER_MODULE = "passenger.user";
    public static final String KEY_LAST_CODE_TIME = "last_code_time"; //发送短信验证码的时间
    public static final String KEY_UPDATE_SUC_VER = "update_suc_ver"; //下载apk成功的版本号

    public static final String KEY_LOST = "KEY_LOST"; //防丢
    public static final String KEY_CHILD_LOCK = "KEY_CHILD_LOCK"; //童锁
    public static final String KEY_AUTO_LOCK = "KEY_AUTO_LOCK"; //自动锁
    public static final String KEY_LIMIT_NUM = "KEY_LIMIT_NUM"; //自动锁

    /**
     * 读写权限
     */
    private final static int MODE = Context.MODE_PRIVATE;

    private SharedPreferences share;
    private Editor editor;

    public SharedPreferencesManager() {
        share = LBApplication.getInstance().getSharedPreferences(NAME, MODE);
        editor = share.edit();
    }

    public SharedPreferencesManager(String name) {
        share = LBApplication.getInstance().getSharedPreferences(name, MODE);
        editor = share.edit();
    }

    public boolean save(String key, Boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean read(String key, Boolean defValue) {
        boolean result = share.getBoolean(key, defValue);
        return result;
    }

    public boolean save(String key, float value) {
        editor.putFloat(key, value);
        return editor.commit();
    }

    public float read(String key, float defValue) {
        return share.getFloat(key, defValue);
    }

    public boolean save(String key, int value) {
        editor.putInt(key, value);
        return editor.commit();
    }

    public int read(String key, int defValue) {
        return share.getInt(key, defValue);
    }

    public boolean save(String key, long value) {
        editor.putLong(key, value);
        return editor.commit();
    }

    public long read(String key, long defValue) {
        return share.getLong(key, defValue);
    }

    public boolean save(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }

    public String read(String key, String defValue) {
        return share.getString(key, defValue);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public boolean remove(String key) {
        editor.remove(key);
        return editor.commit();
    }

    /**
     * 清除所有数据
     */
    public boolean clear() {
        editor.clear();
        return editor.commit();
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return share.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public Map<String, ?> getAll() {
        return share.getAll();
    }


    /**
     * 是否第一次运行（启动引导图）
     *
     * @param context
     * @return
     */
    public boolean isFirstRun(Context context, boolean hasSave) {
        boolean bool = false;
        String historyName = read(KEY_VERSIONNAME, "");
        String versionName = getVersionName(context);
        if (hasSave) {
            save(KEY_VERSIONNAME, versionName);
        }
        if (historyName.equals("")) {
            bool = true;
        }
        return bool;
    }


}

