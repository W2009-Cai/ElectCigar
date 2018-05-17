package com.framework.common.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * ListAdapter
 *
 * @author xutingz
 */
public abstract class IBaseAdapter<T> extends BaseAdapter {

    protected List<T> mList = new ArrayList<T>();
    protected LayoutInflater mInflater;
    protected Context mContext;

    public IBaseAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void showToast(String title) {
        if (mContext != null) {
            if (mContext instanceof IBaseActivity) {
                ((IBaseActivity) mContext).showToast(title);
            } else if (mContext instanceof IBaseFragmentActivity) {
                ((IBaseFragmentActivity) mContext).showToast(title);
            }
        }
    }

    public void setList(List<T> list) {
        this.mList.clear();
        if (null != list && !list.isEmpty()) {
            this.mList.addAll(list);
        }
    }

    /**
     * 清除全部数据
     */
    public void clearList() {
        this.mList.clear();
        notifyDataSetChanged();
    }

    public void addList(List<T> list) {
        if (null != list && !list.isEmpty()) {
            this.mList.addAll(list);
        }
    }

    public void addListFirst(List<T> list) {
        if (null != list && !list.isEmpty()) {
            this.mList.addAll(0, list);
        }
    }

    /**
     * 最先一个
     *
     * @return
     */
    public T getFirstItem() {
        if (!mList.isEmpty()) {
            return mList.get(0);
        }
        return null;
    }

    /**
     * 最后一个
     *
     * @return
     */
    public T getLast() {
        if (!mList.isEmpty()) {
            return mList.get(getCount() - 1);
        }
        return null;
    }

    public void addItem(T item) {
        mList.add(item);
    }

    public void addItemFirst(T item) {
        mList.add(0, item);
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
    public T getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    abstract public View getView(int position, View convertView, ViewGroup parent);

}
