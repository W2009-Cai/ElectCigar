package com.xiaolanba.passenger.common.bean;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author xutingz
 */
public class User implements Serializable {

    public long _id; //数据库自增长字段，与后台接口无关
    public long member_id;
    public String userPhone;
    public String userNickName;
    public String image;
    public String token;
    public String account;
    public String password;
    public String device_number;
    public int sex;//1男2女
    public long loginTime; //登录时间

    public User() {

    }

    public User(long id, String account, String token) {
        this.member_id = id;
        this.account = account;
        this.token = token;
    }

    /**
     * 数据库构造方式
     */
    public User(long _id, long id, String userPhone, String userNickName, String userHeadPortrait,
                String token, String account, String password, String device_number, int sex, long loginTime) {
        this._id = _id;
        this.member_id = id;
        this.userPhone = userPhone;
        this.userNickName = userNickName;
        this.image = userHeadPortrait;
        this.token = token;
        this.account = account;
        this.password = password;
        this.device_number = device_number;
        this.sex = sex;
        this.loginTime = loginTime;
    }
}
