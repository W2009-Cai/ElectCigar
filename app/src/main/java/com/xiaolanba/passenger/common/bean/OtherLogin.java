package com.xiaolanba.passenger.common.bean;

import com.framework.common.utils.IClassUtil;
import com.framework.common.utils.IJsonUtil;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 第三方登录 （微信 ，QQ，新浪微博）
 *
 * @author xutingz
 */
public class OtherLogin implements Serializable {

    private static final long serialVersionUID = -8875451557539411581L;

    /**
     * 类型
     */
    public int type;
    /**
     * 授权用户唯一标识
     */
    public String openId;
    /**
     * 普通用户昵称
     */
    public String nickName;
    /**
     * 普通用户性别，[微信 1为男性，2为女性] [QQ “男” “女”]
     */
    public String sex;
    /**
     * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
     */
    public String headUrl;
    /**
     * 普通用户个人资料填写的省份
     */
    public String province;
    /**
     * 普通用户个人资料填写的城市
     */
    public String city;
    /**
     * 国家，如中国为CN
     */
    public String country;
    /**
     * 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
     */
    public String unionid;

    public String uid;

    @Override
    public String toString() {
        return IClassUtil.printObject(this);
    }

    public void parser(JSONObject json) {
        if (json == null) {
            return;
        }
        if (json.has("openid")) {
            openId = IJsonUtil.getString("openid", json);
        }

        //##########微信 start#############
        nickName = IJsonUtil.getString("nickname", json);
        if (json.has("sex")) {
            int sex = IJsonUtil.getInt("sex", json);
            this.sex = sex == 1 ? "M" : "F";
        }
        province = IJsonUtil.getString("province", json);
        city = IJsonUtil.getString("city", json);
        country = IJsonUtil.getString("country", json);
        if (json.has("headimgurl")) {
            headUrl = IJsonUtil.getString("headimgurl", json);
        }
        //##########微信end#############

        //##########QQ start#############
        if (json.has("gender")) {
            String gender = IJsonUtil.getString("gender", json);
            this.sex = gender.equals("男") ? "M" : "F";
        }
        if (json.has("figureurl_qq_2")) {
            headUrl = IJsonUtil.getString("figureurl_qq_2", json);
        }
        //##########QQ end#############

        if (json.has("profile_image_url")) {
            headUrl = IJsonUtil.getString("profile_image_url", json);
        }
        if (json.has("idstr")) {
            uid = IJsonUtil.getString("idstr", json);
        }
        if (json.has("screen_name")) {
            nickName = IJsonUtil.getString("screen_name", json);
        }

        if (json.has("unionid")) {
            unionid = IJsonUtil.getString("unionid", json);
        }
    }
}
