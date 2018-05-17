package com.xiaolanba.passenger.common.base;

import android.os.Bundle;
import android.view.View;

import com.framework.common.base.IBaseFragment;
import com.xiaolanba.passenger.logic.control.LBController;

/**
 * fragment 基类
 *
 * @author xutingz
 */
public abstract class BaseFragment extends IBaseFragment {

    public LBController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        controller = LBController.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void hide() {
    }

    /**
     * 刷新数据
     */
    public void refreshData() {

    }

    @Override
    public void onClick(View v) {

    }

}
