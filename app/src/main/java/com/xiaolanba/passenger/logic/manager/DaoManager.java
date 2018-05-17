package com.xiaolanba.passenger.logic.manager;


import com.xiaolanba.passenger.LBApplication;
import com.xiaolanba.passenger.logic.db.DaoMaster;
import com.xiaolanba.passenger.logic.db.DaoSession;

/**
 * 数据库管理类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class DaoManager {

    private DaoMaster daoMaster;
    private DaoSession daoSession;

    public DaoManager() {

    }

    private DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(LBApplication.getInstance());
            daoMaster = new DaoMaster(helper.getWritableDatabase());
            daoMaster.upgradeData();
        }
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

}
