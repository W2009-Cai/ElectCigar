package com.xiaolanba.passenger.common.bean;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/03
 */

public class ShopsBean extends BaseBean {
    public String title;
    public String imageUrl;
    public float price;
    public String unit;

    public ShopsBean(){

    }

    public ShopsBean(String title, String imageUrl, float price,String unit) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.unit = unit;
    }
}
