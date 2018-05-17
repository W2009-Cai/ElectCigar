package com.xiaolanba.passenger.common.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送事件action
 * 比如：如果用interface，同时存在100个此Listener对象，当某处要发一个事件时，这100个Listener
 * 对象都会响应onDataChanged()方法，但是用abstract的话可以做到在发送的源头对action拦截，
 * 只有满足条件的才会响应
 */

public abstract class DataChangedListener {
    public List<String> actionList = new ArrayList<>();

    public boolean isContain(String action){
        return actionList.contains(action);
    }

    public void addAction(String action){
        if (!isContain(action)){
            actionList.add(action);
        }
    }

    /**
     * 测试通过action事件的方式
     */
    public static final String ACTION_TEST1 = "ACTION_TEST1";
    public static final String ACTION_TEST2 = "ACTION_TEST2";
    public static final String ACTION_TEST3 = "ACTION_TEST3";
    public static final String ACTION_TEST4 = "ACTION_TEST4";

    /**
     * @param action
     * @param who      谁的数据改变了
     * @param newValue 新的数据
     */
    public abstract void onDataChanged(String action, Object who, Object newValue);
}

