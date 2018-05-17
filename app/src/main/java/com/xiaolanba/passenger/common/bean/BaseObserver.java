package com.xiaolanba.passenger.common.bean;

import com.framework.common.utils.ILog;
import com.framework.common.utils.INetworkUtils;
import com.xiaolanba.passenger.LBApplication;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 对Retrofit请求数据的观察者的封装。当后台返回list作为数据时使用BaseListObserver
 * 当后台返回data作为数据时使用BaseObserver
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/03/26
 */

public abstract class BaseObserver<T> implements Observer<BaseParse<T>> {
    private static final String TAG = "BaseObserver";
    public static final int DEFAULT_CODE = 19999;
    public static final int SUCCESS = 200;
    public static final int TOKEN_OFF = 300;
    public static final String DEFAULT_DESC = "后台服务繁忙,请稍后再试";
    public static final String NO_NET_DESC = "网络不稳定,请稍后再试";
    protected Disposable mDisposable;

    public BaseObserver() {

    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
    }

    @Override
    public void onNext(BaseParse<T> baseParse) {
        if (baseParse.isRequestSuccess()) {
            onSuccees(baseParse.msg, baseParse.getData());
        } else {
            if (TOKEN_OFF == baseParse.code){ //拦截token失效的错误码，统一到登录处理
                onFailed(baseParse.code, null, baseParse.getData()); //把提示语设置为空
                LBApplication.getInstance().tokenOffline("");
            } else {
                onFailed(baseParse.code, baseParse.msg, baseParse.getData());
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        ILog.i(TAG,"---onError()");
        e.printStackTrace();
        String message;
        if (INetworkUtils.getInstance().isNetworkAvailable()){
            message = DEFAULT_DESC;
        } else {
            message = NO_NET_DESC;
        }
        onFailed(DEFAULT_CODE, message, null); //如果是进到onError，则message一定不为空
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onComplete() {
        ILog.i(TAG,"---onComplete()");
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    /**
     * 返回成功
     *
     * @param baseBean
     * @throws Exception
     */
    protected abstract void onSuccees(String message, T baseBean);

    protected abstract void onFailed(int code, String message, T baseBean);

}
