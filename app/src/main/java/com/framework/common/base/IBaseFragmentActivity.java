package com.framework.common.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;

import com.framework.common.utils.ILog;
import com.framework.common.utils.IStringUtil;
import com.framework.common.view.IProgressDialog;
import com.xiaolanba.passenger.common.view.MyToast;

/**
 * FragmentActivity 基类
 *
 * @author xutingz
 */
public abstract class IBaseFragmentActivity extends FragmentActivity implements
        DialogInterface.OnKeyListener, OnClickListener, IActivity {

    protected String TAG = getClass().getSimpleName();

    protected IBaseFragment fragment;

    public IProgressDialog mProgressDialog;

    protected IBaseFragment[] fragments;
    protected IBaseFragmentActivity context;
    protected LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        ILog.i(TAG, "TAG:" + TAG);

        context = this;
        mInflater = LayoutInflater.from(this);

        setContentView();
        findView();
        initData();

    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new IProgressDialog(this);
        }
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show("");
    }

    public void showProgressDialog(boolean touchOutSideHide) {
        if (mProgressDialog == null) {
            mProgressDialog = new IProgressDialog(this);
        }
        if (touchOutSideHide) {
            mProgressDialog.setCanceledOnTouchOutside(true);
        }
        mProgressDialog.show("");
    }

    public boolean isDialogShowing() {
        if (mProgressDialog != null) {
            return mProgressDialog.isShowing();
        }
        return false;
    }

    public void showProgressDialog(String content) {
        if (mProgressDialog == null) {
            mProgressDialog = new IProgressDialog(this);
        }
        mProgressDialog.show(content);
    }

    public void dismissProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (MyToast.myToast != null) {
            MyToast.myToast.cancleByActivityDestory();
        }
        super.onDestroy();
    }

    @Override
    public void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    @Override
    public void showToast(@StringRes int resId, int duration) {
        showToast(getString(resId), duration);
    }

    @Override
    public void showToast(String title) {
        if (IStringUtil.isEmpty(title)) {
            return;
        }
        showToast(title, MyToast.LENGTH_LONG);
    }

    /**
     * 显示提示
     *
     * @param title
     */
    @Override
    public void showToast(String title, int duration) {
        if (IStringUtil.isEmpty(title)) {
            return;
        }
        MyToast.makeText(context, title, duration).show();
    }


    /**
     * 找到tag对应的fragment
     *
     * @param tag
     * @return
     */
    public Fragment findFragByTag(String tag) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(tag);
        return frag;
    }

    /**
     * @param cls
     * @return 返回是否存在
     */
    public boolean showFragment(@SuppressWarnings("rawtypes") Class cls) {
        IBaseFragment hideFragment = (IBaseFragment) findFragByTag(cls.getCanonicalName());
        if (null != hideFragment) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            if (null != fragment) {
                ft.hide(fragment);
            }

            this.fragment = hideFragment;

            ft.show(hideFragment);
            ft.commitAllowingStateLoss();

            return true;
        }
        return false;
    }

    public void addFragment(int resId, IBaseFragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (null != fragment) ft.hide(fragment);

        this.fragment = frag;

        String tag = frag.getClass().getCanonicalName();
        ft.add(resId, frag, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commitAllowingStateLoss();
    }

    public void replaceFragment(int resId, IBaseFragment frag) {

        this.fragment = frag;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String tag = frag.getClass().getName();
        ft.replace(resId, frag, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commitAllowingStateLoss();

    }

    protected int currentIndex = -1;

    public void switchFragment(int resId, int index) {
        if (isFinishing()) {
            return;
        }
        if (fragments == null) {
            return;
        }
        if (index == currentIndex) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (!fragments[index].isAdded()) {
            ft.add(resId, fragments[index]);
        }
        for (int i = 0, len = fragments.length; i < len; i++) {
            IBaseFragment f = fragments[i];
            if (!f.isAdded()) continue;
            if (index == i) {
                f.show();
                ft.show(f);
                continue;
            }
            f.hide();
            ft.hide(f);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commitAllowingStateLoss();
        currentIndex = index;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ILog.i(TAG, "----onActivityResult");
        if (null != fragment) {
            fragment.onActivityResult(requestCode, resultCode, data);
        } else if (null != fragments && currentIndex != -1) {
            fragments[currentIndex].onActivityResult(requestCode, resultCode, data);
        }
    }

    public abstract void setContentView();

    public abstract void findView();

    public abstract void initData();

}
