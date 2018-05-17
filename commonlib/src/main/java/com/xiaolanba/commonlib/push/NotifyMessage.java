package com.xiaolanba.commonlib.push;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 对推送的消息不同类型做一层封装，点击时决定要跳转到哪里
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class NotifyMessage implements Serializable {

	public long id; // id
	public int type;// 类型
	public boolean isSelfDefine; //是否是自定义类型

	public NotifyMessage(String extra) {
		if (!TextUtils.isEmpty(extra)) {
			try {
				JSONObject json = new JSONObject(extra);
				parse(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void parse(JSONObject json) {
		if (json == null) {
			return;
		}
		try{
			type = json.getInt("type");
			id = json.getLong("id");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
