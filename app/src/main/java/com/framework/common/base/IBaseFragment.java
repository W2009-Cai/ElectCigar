package com.framework.common.base;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.framework.common.utils.ILog;

/**
 * Fragment 基类
 *
 * @author xutingz
 */
public abstract class IBaseFragment extends Fragment implements OnClickListener {

    protected String TAG = getClass().getSimpleName();

    public IBaseFragmentActivity context;

    protected View mMainView;

    protected ViewGroup mContainer;

    protected LayoutInflater mInflater;

    protected Bundle mSavedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.TAG = getClass().getCanonicalName();

        ILog.i(TAG, "TAG:" + TAG);

        context = (IBaseFragmentActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        inflater = getActivity().getLayoutInflater();

        this.mInflater = inflater;
        this.mContainer = container;
        this.mSavedInstanceState = savedInstanceState;

        setContentView();
        findView();
        initData();

        return mMainView;
    }

    /**
     * 当activity创建时调用，相当于oncreated（）；
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //IBaseFragment子类的onCreateView之中，有可能需要改变ui，为避免太多的判空，'findView','initData'放到‘onCreateView’中
//		findView();
//		initData();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void showToast(int resId) {
        showToast(getFragString(resId));
    }

    public void showToast(String title) {
        context.showToast(title);
    }

    public void showProgressDialog() {
        context.showProgressDialog();
    }

    public boolean isDialogShowing() {
        return context.isDialogShowing();
    }


    public void showProgressDialog(String content) {
        context.showProgressDialog(content);
    }

    public void dismissProgressDialog() {
        context.dismissProgressDialog();
    }

    /**
     * SetContentView
     * 初始化添加布局
     *
     * @param id 布局id
     */
    public void setContentView(int id) {
        this.mMainView = (View) this.mInflater.inflate(id, mContainer, false);
    }

    /**
     * SetContentView
     * 初始化添加布局
     *
     * @param view
     */
    public void setContentView(View view) {
        this.mMainView = view;
    }

    /**
     * findViewById
     *
     * @param id
     * @return
     */
    public View findViewById(int id) {
        return mMainView.findViewById(id);
    }

    /**
     * 颜色值
     *
     * @param id
     * @return
     */
    public int getColor(int id) {
        return getFragResources().getColor(id);
    }

    /**
     * getString
     *
     * @param id
     * @return
     */
    public String getFragString(int id) {
        return getFragResources().getString(id);
    }

    /**
     * getResources
     * 获得资源的操作
     *
     * @return
     */
    public Resources getFragResources() {
        return context.getResources();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 替换fragment(注意参数类型)
     *
     * @param resId 存放的位置
     */
    public void replaceFragment(int resId, Fragment frag) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String tag = frag.getClass().getName();
        ft.replace(resId, frag, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commitAllowingStateLoss();
    }

    /**
     * 替换fragment(注意参数类型)
     *
     * @param resId 存放的位置
     */
    public void replaceFragment(int resId, IBaseFragment frag) {

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        String tag = frag.getClass().getName();
        ft.replace(resId, frag, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commitAllowingStateLoss();

    }


    public abstract void setContentView();

    public abstract void findView();

    public abstract void initData();

    public void show() {

    }

    public void hide() {

    }

}
