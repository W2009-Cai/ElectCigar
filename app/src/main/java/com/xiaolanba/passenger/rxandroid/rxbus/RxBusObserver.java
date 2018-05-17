package com.xiaolanba.passenger.rxandroid.rxbus;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 对RxBus的观察者封装，减少上层代码行数
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/03/27
 */

public abstract class RxBusObserver<T> implements Observer<T> {

    public RxBusObserver() {

    }

    @Override
    public void onSubscribe(Disposable d) {
        onRxSubscribe(d);
    }

    @Override
    public void onNext(T value) {
        onRxNext(value);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    protected abstract void onRxSubscribe(Disposable d);

    protected abstract void onRxNext(T value);
}
