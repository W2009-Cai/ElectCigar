package com.xiaolanba.passenger.logic.manager;

import android.app.Activity;

import com.framework.common.utils.ILog;
import com.xiaolanba.passenger.module.main.activity.MainActivity;

import java.util.Stack;

/**
 * 页面管理
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class PageManager {

    private static final String TAG = PageManager.class.getName();

    private Stack<Activity> pageStack;

    public PageManager() {
        pageStack = new Stack<>();
    }

    public Stack<Activity> getPageStack() {
        return pageStack;
    }

    /**
     * 移除页面对象
     *
     * @param activity
     */
    public void removePage(Activity activity) {
        if (activity != null) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
            pageStack.remove(activity);
            ILog.d(TAG, "PageManager.removePage 移除页面:" + activity.getClass().getName());
        }
    }

    /**
     * 添加新页面
     *
     * @param activity
     */
    public void addPage(Activity activity) {
        if (!pageStack.contains(activity)) {
            pageStack.add(activity);
            ILog.d(TAG, "PageManager.addPage 添加新页面:" + activity.getClass().getName());
        } else {
            ILog.d(TAG, "PageManager.addPage 页面已存在");
        }
    }

    /**
     * pageStack 中是否只有当前一个页面
     *
     * @param activity
     * @return
     */
    public boolean isOnlyThis(Activity activity) {
        boolean bool = false;
        if (pageStack != null && !pageStack.isEmpty()) {
            if (pageStack.contains(activity) && pageStack.size() == 1) {
                bool = true;
            }
        }
        return bool;
    }

    /**
     * 页面清理
     */
    public void clearPage() {
        clearPage(false);
    }

    /**
     * 页面清理
     *
     * @param isSavaLast 是否保留最后一个
     */
    public void clearPage(boolean isSavaLast) {
        int size = pageStack.size();
        if (isSavaLast) {
            size = size - 1;
        }

        for (int i = 0; i < size; i++) {
            Activity activity = pageStack.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        pageStack.clear();
    }

    public void clear() {
        pageStack.clear();
    }

    /**
     * 用来判断MainActivity是否存活，即是否已进入app
     */
    public boolean isMainActivityActive() {
        int size = pageStack.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Activity activity = pageStack.get(i);
                if (activity != null && (activity instanceof MainActivity)) {
                    if (activity.isFinishing()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Activity getBeforeLastActivity() {
        if (pageStack.size() < 2) {
            return null;
        }
        return pageStack.get(pageStack.size() - 2);
    }

    public Activity getTopActivity() {
        if (pageStack == null) {
            return null;
        }
        int size = pageStack.size();
        if (size > 0) {
            return pageStack.get(size - 1);
        }
        return null;
    }
}
