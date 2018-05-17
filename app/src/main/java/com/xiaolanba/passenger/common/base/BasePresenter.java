package com.xiaolanba.passenger.common.base;

import com.xiaolanba.passenger.LBApplication;
import com.xiaolanba.passenger.common.utils.HttpParams;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Presenter基类
 *
 * @author xutingz
 * @e-mail : xutz@xiaolanba.com
 */
public class BasePresenter {

    //将所有正在处理的Subscription都添加到CompositeSubscription中。统一退出的时候注销观察
    private CompositeDisposable mCompositeDisposable;
    protected boolean isDestoryed;
    protected HttpParams httpParams; //方便获取公共参数

    protected BasePresenter(){
        httpParams = LBApplication.getInstance().httpParams;
    }


    public void addDisposable(Disposable subscription) {
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) { //csb 如果解绑了的话添加 sb 需要新的实例否则绑定时无效的
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }

    //在界面退出等需要解绑观察者的情况下调用此方法统一解绑，防止Rx造成的内存泄漏
    public void dispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    //彻底清除其内部的set集合，并且执行dispose
    public void clear(){
        isDestoryed = true;
        if (mCompositeDisposable != null){
            mCompositeDisposable.clear();
        }
    }
}
