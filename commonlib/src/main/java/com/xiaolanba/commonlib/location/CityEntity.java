package com.xiaolanba.commonlib.location;

import java.io.Serializable;

/*
 * 城市实体类
 */
public class CityEntity implements Serializable {

	private static final long serialVersionUID = 2005295701925847160L;

	public int id;
	public String name;
	public String province;// 城市的省份
	public String firstChar;// 首字母
	public String spell;// 全拼
	public int thirdCode;// 地图对应的thirdCode
	
	public double  longitude;//经度
	public double  latitude;//纬度，在城市定位时候用到

	public CityEntity(int id, String name, String province, String shortCitySpell, String spell, int thirdCode) {
		this.id = id;
		this.name = name;
		this.province = province;
		this.firstChar = shortCitySpell.substring(0, 1).toUpperCase();
		this.spell = spell;
		this.thirdCode = thirdCode;
	}

	public CityEntity() {
		super();
	}

}
