package com.xiaolanba.passenger.rxandroid.rxbus;


import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * rxbus ,用于代替EventBus，使用监听者模式，进行事件的分发与回调
 *
 * @author xutingz
 */

public class RxBus {

    private static volatile RxBus defaultInstance;

    private final Subject<Object> bus;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    private RxBus() {
            bus = PublishSubject.create().toSerialized();
        }

        // 单例RxBus
    public static RxBus getDefault() {
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new RxBus();
                }
            }
        }
        return defaultInstance;
    }

    /**
     * 发送不包含code的事件
     *
     * @param action
     */
//    public void post(Object action) {
//        bus.onNext(action);
//    }

    /**
     * 发送包含code和action的事件
     *
     * @param code
     * @param action
     */
    public void postWithCode(int code, Object action) {
        bus.onNext(new RxAction(code, action));
    }

    public void postWithCode(int code) {
        bus.onNext(new RxAction(code, null));
    }


    /**
     * 对特定T类型，注册一个监听事件
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    /**
     * 对Action类型，注册一个监听事件
     */
    public Observable<RxAction> toObservable() {
        return bus.ofType(RxAction.class);
    }

//    /**
//     * 一个code，一个返回类型
//     * @param code
//     * @param eventType
//     * @param <T>
//     * @return 特定的类型
//     */
//    public <T> Observable<T> toObservableWithCode(final int code, Class<T> eventType) {
//        return bus.ofType(RxAction.class)
//                .filter(new Predicate<RxAction>() {
//                    @Override
//                    public boolean test(RxAction action) throws Exception {
//                        return action.code == code;
//                    }
//                })
//                .map(new Function<RxAction, Object>() {
//                    @Override
//                    public Object apply(RxAction action) throws Exception {
//                        return action.data;
//                    }
//                })
//                .cast(eventType);
//    }

    /**
     * 一个界面多个响应多个code事件，需要指定所响应的code号，
     * 只有当code号与接受的Action.code吻合时才会继续传递
     *
     * @param codeList
     * @return RxAction, 自己根据action解出返回action中的数据
     */
    public Observable<RxAction> toObservableWithCodes(@NonNull final List<Integer> codeList) {
        return bus.ofType(RxAction.class)
                .filter(new Predicate<RxAction>() {
                    @Override
                    public boolean test(RxAction action) throws Exception {
                        return codeList.contains(action.code);
                    }
                });
    }

}
