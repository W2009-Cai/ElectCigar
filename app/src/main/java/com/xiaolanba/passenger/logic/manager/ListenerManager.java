package com.xiaolanba.passenger.logic.manager;

import com.xiaolanba.passenger.common.bean.User;
import com.xiaolanba.passenger.common.listener.DataChangedListener;
import com.xiaolanba.passenger.common.listener.LoginUserListener;
import com.xiaolanba.passenger.common.listener.NetworkListener;
import com.xiaolanba.passenger.logic.control.LBController;

import java.util.Vector;

/**
 * 回调监听管理类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class ListenerManager {


    public ListenerManager() {
    }

    // *********************** 登录用户数据更新监听 start *********************** //

    private Vector<LoginUserListener> loginUserListener = new Vector<LoginUserListener>();

    public void registLoginUserListener(LoginUserListener listener) {
        loginUserListener.add(listener);
    }

    public void unregistLoginUserListener(LoginUserListener listener) {
        loginUserListener.remove(listener);
    }

    public void postLoginSuccessListener() {
        LBController.MainHandler.post(new Runnable() {
            @Override
            public void run() {
                User loginUser = LBController.getInstance().getCacheManager().getLoginUser();
                for (LoginUserListener listener : loginUserListener) {
                    listener.loginSuccess(loginUser);
                }
            }
        });
    }

    public void postUpdateLoginUserListener() {
        LBController.MainHandler.post(new Runnable() {
            @Override
            public void run() {
                User loginUser = LBController.getInstance().getCacheManager().getLoginUser();
                for (LoginUserListener listener : loginUserListener) {
                    listener.updateLoginUser(loginUser);
                }
            }
        });
    }

    // *********************** 网络变化回调start *********************** //
    private Vector<NetworkListener> networkListener = new Vector<NetworkListener>();

    public void registNetworkListener(NetworkListener listener) {
        if (!networkListener.contains(listener)) {
            networkListener.add(listener);
        }
    }

    public void unRegistNetworkListener(NetworkListener listener) {
        if (networkListener.contains(listener)) {
            networkListener.remove(listener);
        }
    }

    public void postNetworkListener(final boolean hasNetwork) {
        LBController.MainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (NetworkListener listener : networkListener) {
                    listener.changeNetwork(hasNetwork);
                }
            }
        });
    }

    //用户事件回调监听， 可以使用RxBus代替
    private Vector<DataChangedListener> videoDataChangedListenerVector = new Vector<DataChangedListener>();

    public void addDataChangedListener(final DataChangedListener videoDataChangedListener) {
        LBController.MainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!videoDataChangedListenerVector.contains(videoDataChangedListener)) {
                    videoDataChangedListenerVector.add(videoDataChangedListener);
                }
            }
        });

    }

    public void removeDataChangedListener(final DataChangedListener videoDataChangedListener) {
        LBController.MainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (videoDataChangedListenerVector.contains(videoDataChangedListener)) {
                    videoDataChangedListenerVector.remove(videoDataChangedListener);
                }
            }
        });

    }

    public void postDataChangedListener(final String action, final Object who, final Object newValue) {
        LBController.MainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (DataChangedListener dataChangedListener : videoDataChangedListenerVector) {
                    if (dataChangedListener.isContain(action)) {
                        dataChangedListener.onDataChanged(action, who, newValue);
                    }
                }
            }
        });
    }
}
