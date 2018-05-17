package com.xiaolanba.passenger.common.bean;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/02
 */

public class DevicesGridItem {
    public int picture;
    public String desc;
    public String numberData;
    public boolean stateOk;
    public DevicesGridItem(int picture,String desc,String numData,boolean stateOk){
        this.picture = picture;
        this.desc = desc;
        this.numberData = numData;
        this.stateOk= stateOk;
    }
}
