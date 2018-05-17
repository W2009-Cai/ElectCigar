package com.framework.common.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.xiaolanba.passenger.common.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * FragmentPagerAdapter基类
 *
 * @author xutingz
 */
public class IFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragmentsList = new ArrayList<BaseFragment>();
    private FragmentManager fragmentManager;

    public IFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
    }

    public void setList(List<BaseFragment> fragmentList) {
        try {
            if (fragmentManager != null) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                for (BaseFragment fragment : fragmentsList) {
                    ft.remove(fragment);
                }
                ft.commitAllowingStateLoss();
            }
            fragmentsList.clear();
            if (fragmentList != null) {
                fragmentsList.addAll(fragmentList);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public BaseFragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == ((Fragment) obj).getView();
    }

}
