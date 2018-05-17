package com.xiaolanba.passenger.logic.db;

import android.database.sqlite.SQLiteDatabase;

import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.logic.dao.LoginUserDao;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;


/**
 * {@inheritDoc}
 *
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userConfig;
    private final LoginUserDao userDao;

//    private final DaoConfig poiSearchConfig;
//    private final PoiRecordDao poiSearchDao;


    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userConfig = daoConfigMap.get(DbLoginUser.class).clone();
        userConfig.initIdentityScope(type);
        userDao = new LoginUserDao(userConfig, this);

//        poiSearchConfig = daoConfigMap.get(DbPoiRecord.class).clone();
//        poiSearchConfig.initIdentityScope(type);
//        poiSearchDao = new PoiRecordDao(poiSearchConfig, this);
        //
        registerDao(User.class, userDao);
//        registerDao(PoiSearchRecord.class, poiSearchDao);
    }

    public void clear() {
        userConfig.getIdentityScope().clear();
//        poiSearchConfig.getIdentityScope().clear();
    }


    public LoginUserDao getLoginUserDao() {
        return userDao;
    }

//    public PoiRecordDao getPoiSearchDao(){
//        return poiSearchDao;
//    }

}
