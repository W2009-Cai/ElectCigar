package com.xiaolanba.passenger.common.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.framework.common.base.IBaseActivity;
import com.framework.common.base.IBaseFragmentActivity;
import com.framework.common.utils.ILog;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerViewAdapter
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mList = new ArrayList<T>();
    protected LayoutInflater mInflater;
    protected Context mContext;

    public BaseRecyclerViewAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    abstract public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position);

    @Override
    abstract public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType);

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size();
    }

    public T getItem(int position) {
        if (position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    /**
     * 获取最后一个
     **/
    public T getLastItem() {
        if (mList == null || getItemCount() == 0) return null;
        return mList.get(getItemCount() - 1);
    }

    /**
     * 获取第一个
     **/
    public T getFirstItem() {
        if (mList == null || getItemCount() == 0) return null;
        return mList.get(0);
    }

    public void removeItem(int position) {
        mList.remove(position);
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
        if (null != list) {
            this.mList.addAll(list);
            notifyDataChanged();
        } else {
            notifyDataChanged();
        }
    }

    protected void notifyDataChanged() {
        notifyDataSetChanged();
    }

    public void clearList() {
        this.mList.clear();
        notifyDataChanged();
    }

    public void clearListWithoutRefresh() {
        if (null != mList) {
            this.mList.clear();
        }
    }

    public void clearList(int beginPos) {
        if (null != mList && mList.size() > beginPos) {
            mList.subList(beginPos, mList.size()).clear();
            notifyDataSetChanged();
        }
    }

    public void addList(List<T> list) {
        int oldItemCount = getItemCount();
        ILog.i("mm","---addList oldItemCount="+oldItemCount);
        if (null != list && !list.isEmpty()) {
            this.mList.addAll(list);
            notifyItemRangeInserted(oldItemCount, list.size());
        }
    }

    public void addList1(List<T> list) {
        int oldItemCount = getItemCount();
//        ILog.i("mm","---addList1 oldItemCount="+oldItemCount);
        if (null != list && !list.isEmpty()) {
            this.mList.addAll(list);
            notifyDataChanged();
        }
    }

    public void addListFirst(List<T> list) {
        if (null != list && !list.isEmpty()) {
            this.mList.addAll(0, list);
            notifyItemRangeInserted(0, list.size());
        }
    }

    public void addItem(int position, T obj) {
        if (mList != null && position < mList.size()) {
            this.mList.add(position, obj);
            notifyDataSetChanged();
        }
    }

    public void addItem(T obj) {
        if (mList != null && obj != null) {
            int oldSize = mList.size();
            this.mList.add(obj);
            notifyItemInserted(oldSize);
//            notifyDataSetChanged();
        }
    }

    public void addListWithoutRefresh(List<T> list) {
        if (null != list && !list.isEmpty()) {
            this.mList.addAll(0, list);
        }
    }

    public void addItemFirst(T item) {
        if (item != null) {
            this.mList.add(0, item);
            notifyDataSetChanged();
        }
    }

    public void addItemWithoutRefresh(T item) {
        if (item != null) {
            this.mList.add(item);
        }
    }

    public List<T> getList() {
        return mList;
    }

}
