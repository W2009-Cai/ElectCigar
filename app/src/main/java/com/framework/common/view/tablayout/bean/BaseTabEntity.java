package com.framework.common.view.tablayout.bean;

import com.framework.common.view.tablayout.listener.CustomTabEntity;

/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class BaseTabEntity implements CustomTabEntity {

    public String title;
    public int selectedIcon;
    public int unSelectedIcon;

    public BaseTabEntity(String title, int selectedIcon, int unSelectedIcon) {
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }

    public BaseTabEntity(String title) {
        this.title = title;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }
}
