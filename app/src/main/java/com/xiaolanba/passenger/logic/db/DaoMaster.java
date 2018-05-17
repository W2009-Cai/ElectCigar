package com.xiaolanba.passenger.logic.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.framework.common.utils.ILog;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;


/**
 * @author xutingz
 * 1、创建poi搜索表，创建登录用户表
 */
public class DaoMaster extends AbstractDaoMaster {

    public static final String DATABASE_NAME = "passenger.db";
    public static final int DATABASE_VERSION = 1;

    public DaoMaster(SQLiteDatabase db) {
        super(db, DATABASE_VERSION);

        registerDaoClass(DbLoginUser.class);
//        registerDaoClass(DbPoiRecord.class);
    }

    /**
     * 创建表，把需要的表都在这里创建
     */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        DbLoginUser.createTable(db, ifNotExists);
//        DbPoiRecord.createTable(db, ifNotExists);
    }

    /**
     * 删除表
     */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        DbLoginUser.dropTable(db, ifExists);
//        DbPoiRecord.dropTable(db, ifExists);
    }

    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            ILog.i("greenDAO", "Creating tables for schema version " + DATABASE_VERSION);
            createAllTables(db, false);
        }
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        public DevOpenHelper(Context context) {
            super(context, null, null);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            ILog.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            // 数据库升级时备份，以便升级后将旧数据导入新表
//            dataUpgrade = new VersionDataUpgrade();
//            dataUpgrade.startDbDataExport(db, oldVersion, newVersion);
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    public void upgradeData() {
//        if (dataUpgrade != null) {
//            dataUpgrade.startDbDataImport();
//            dataUpgrade = null;
//        }
    }

}
