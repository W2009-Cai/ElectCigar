package com.xiaolanba.passenger.logic.dao;

import com.framework.common.utils.IDateFormatUtil;
import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.logic.db.DaoSession;
import com.xiaolanba.passenger.logic.db.DbLoginUser;

import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 登录用户表的数据库操作类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class LoginUserDao extends DbLoginUser {

    public LoginUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * 用户登录
     *
     * @param loginUser
     */
    public void login(User loginUser) {
        try {
            deleteAll();
            if (loginUser != null) {
                loginUser.loginTime = IDateFormatUtil.getTimeMillis();
                insert(loginUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户登出
     */
    public void logout() {
        try {
            deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询登录用户
     */
    public User queryLoginUser() {
        try {
            QueryBuilder<User> queryBuilder = queryBuilder();
            return queryBuilder.unique();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新用户信息,比如昵称、头像、手机号等更改了
     */
    public void updateLoginUser(User loginUser) {
        try {
            if (loginUser != null) {
                User dbLoginUser = queryLoginUserById(loginUser.member_id);
                if (dbLoginUser != null) {
                    loginUser._id = dbLoginUser._id;
                    loginUser.loginTime = IDateFormatUtil.getTimeMillis();
                    update(loginUser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户id查询
     *
     * @return
     */
    public User queryLoginUserById(long userId) {

        try {
            QueryBuilder<User> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Id.eq(userId));
            return queryBuilder.unique();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
