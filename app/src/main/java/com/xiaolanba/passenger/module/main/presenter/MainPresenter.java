package com.xiaolanba.passenger.module.main.presenter;


import android.util.Log;
import com.xiaolanba.passenger.common.base.BasePresenter;
import com.xiaolanba.passenger.module.main.presenter.contract.MainContract;

/**
 * @author xutingz
 * @company xiaolanba.com
 */

public class MainPresenter extends BasePresenter implements MainContract.Presenter {
    private static final String TAG = "MainPresenter";
    private MainContract.ViewControl mViewControl;

    public MainPresenter(MainContract.ViewControl mViewControl) {
        this.mViewControl = mViewControl;
    }

}
