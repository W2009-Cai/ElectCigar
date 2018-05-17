package com.xiaolanba.passenger.common.bean;

/**
 * 设备模块，绑定设备的模型
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/07
 */

public class DevicesBind extends BaseBean {
    private boolean mPinned; //是否打开，true为打开
    public String name;
    public String address;

    public boolean isPinned() {
        return mPinned;
    }

    public void setPinned(boolean mPinned) {
        this.mPinned = mPinned;
    }
}
