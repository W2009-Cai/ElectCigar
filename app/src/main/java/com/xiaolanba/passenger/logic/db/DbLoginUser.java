package com.xiaolanba.passenger.logic.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.xiaolanba.passenger.common.bean.User;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * LOGIN_USER 表的相关创建与绑定
 *
 * @author xutingz
 */
public class DbLoginUser extends AbstractDao<User, Long> {

    public static final String TABLENAME = "LOGIN_USER";


    /**
     * Properties of entity LoginUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _Id = new Property(0, Long.class, "_id", true, "_ID");
        public final static Property Id = new Property(1, Long.class, "id", false, "ID");
        public final static Property UserPhone = new Property(2, String.class, "userPhone", false, "USERPHONE");
        public final static Property UserNick = new Property(3, String.class, "userNick", false, "USERNICK");
        public final static Property UserHead = new Property(4, String.class, "userHead", false, "USERHEAD");
        public final static Property Token = new Property(5, String.class, "token", false, "TOKEN");
        public final static Property Account = new Property(6, String.class, "account", false, "ACCOUNT");
        public final static Property Password = new Property(7, String.class, "password", false, "PASSWORD");
        public final static Property DeviceNumber = new Property(8, String.class, "deviceNumber", false, "DEVICENUMBER");
        public final static Property Sex = new Property(9, Integer.class, "sex", false, "SEX");
        public final static Property LoginTime = new Property(10, Long.class, "loginTime", false, "LOGINTIME");
    }

    public DbLoginUser(DaoConfig config) {
        super(config);
    }

    public DbLoginUser(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOGIN_USER\" (" + //
                "\"_ID\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"ID\" REAL," +                                   // 1: id
                "\"USERPHONE\" TEXT," +                           // userPhone
                "\"USERNICK\" TEXT," +                            // userNick
                "\"USERHEAD\" TEXT," +                            // userHead
                "\"TOKEN\" TEXT," +                               // token
                "\"ACCOUNT\" TEXT," +                        // ACCOUNT
                "\"PASSWORD\" TEXT," +                        // PASSWORD
                "\"DEVICENUMBER\" TEXT," +                        // DEVICENUMBER
                "\"SEX\" INTEGER," +                        // SEX
                "\"LOGINTIME\" REAL);");                         //: loginTime
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOGIN_USER\"";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
        long _id = entity._id;
        if (_id != 0) {
            stmt.bindLong(1, _id);
        }
        long id = entity.member_id;
        if (id != 0) {
            stmt.bindLong(2, id);
        }
        String phone = entity.userPhone;
        if (phone != null) {
            stmt.bindString(3, phone);
        }
        String nick = entity.userNickName;
        if (nick != null) {
            stmt.bindString(4, nick);
        }
        String head = entity.image;
        if (head != null) {
            stmt.bindString(5, head);
        }
        String token = entity.token;
        if (token != null) {
            stmt.bindString(6, token);
        }
        String account = entity.account;
        if (account != null) {
            stmt.bindString(7, account);
        }
        String password = entity.password;
        if (password != null) {
            stmt.bindString(8, password);
        }
        String devicesNum = entity.device_number;
        if (devicesNum != null) {
            stmt.bindString(9, devicesNum);
        }
        int sex = entity.sex;
        if (sex != 0) {
            stmt.bindLong(10, sex);
        }
        long loginTime = entity.loginTime;
        if (loginTime != 0) {
            stmt.bindLong(11, loginTime);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User(
                cursor.isNull(offset + 0) ? 0 : cursor.getLong(offset + 0),       // _id
                cursor.isNull(offset + 1) ? 0 : cursor.getLong(offset + 1),       // id
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // phone
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // nick
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // headimg
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // token
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6),  // account
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7),  // password
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8),  // devicesNum
                cursor.isNull(offset + 9) ? 0 : cursor.getInt(offset + 9),  // sex
                cursor.isNull(offset + 10) ? 0 : cursor.getLong(offset + 10)       // loginTime
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity._id = cursor.isNull(offset + 0) ? 0 : cursor.getLong(offset + 0);
        entity.member_id = cursor.isNull(offset + 1) ? 0 : cursor.getLong(offset + 1);
        entity.userPhone = cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2);
        entity.userNickName = cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3);
        entity.image = cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4);
        entity.token = cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5);
        entity.account = cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6);
        entity.password = cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7);
        entity.device_number = cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8);
        entity.sex = cursor.isNull(offset + 9) ? 0 : cursor.getInt(offset + 9);
        entity.loginTime = cursor.isNull(offset + 10) ? 0 : cursor.getLong(offset + 10);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity._id = rowId;
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(User entity) {
        if (entity != null) {
            return entity._id;
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
