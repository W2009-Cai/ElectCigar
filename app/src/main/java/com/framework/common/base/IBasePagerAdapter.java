package com.framework.common.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * PagerAdapter 基类
 *
 * @author xutingz
 */
public abstract class IBasePagerAdapter<T> extends PagerAdapter {

    protected List<T> mList = new ArrayList<T>();
    protected LayoutInflater mInflater;
    protected Context mContext;

    public IBasePagerAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setList(List<T> list) {
        this.mList.clear();
        if (null != list) {
            this.mList.addAll(list);
        }
    }


    public void addList(List<T> list) {
        if (null != list) {
            this.mList.addAll(list);
        }
    }

    public void addListFirst(List<T> list) {
        if (null != list) {
            this.mList.addAll(0, list);
        }
    }

    public void addItem(T item) {
        mList.add(item);
    }

    public List<T> getList() {
        return mList;
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0.equals(arg1);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    abstract public Object instantiateItem(ViewGroup container, int position);


}
