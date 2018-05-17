package com.xiaolanba.passenger.module.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.framework.common.utils.ILog;
import com.qbw.recyclerview.expandable.ExpandableAdapter;
import com.xiaolanba.passenger.common.base.BaseRecyclerViewHolder;

/**
 * @author QBW
 * @createtime 2016/09/06 16:07
 * @company 9zhitx.com
 * @description 可以扩展的RecyclerView适配器
 */


public abstract class CommonExpandableAdapter<T> extends ExpandableAdapter<T> {

    protected LayoutInflater mLayoutInflater;
    protected Context mContext;

    public CommonExpandableAdapter(Context context) {
        super();
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseRecyclerViewHolder) {
            BaseRecyclerViewHolder bViewHolder = (BaseRecyclerViewHolder) holder;
            bViewHolder.bindData(getItem(position), position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = getItemViewType(getItem(position));
        return -1 != viewType ? viewType : super.getItemViewType(position);
    }

    public int getItemViewType(T t) {
        return -1;
    }

    public int findGroupPosition(int viewType) {
        int pos = -1;
        int groupCount = getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            if (viewType == getItemViewType(getGroup(i))) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    public void removeGroupByViewType(int viewType) {
        int groupPos = findGroupPosition(viewType);
        if (-1 != groupPos) {
            removeGroup(groupPos);
        } else {
            ILog.le("No group's viewType is %d", viewType);
        }
    }

    public void removeGroups() {
        int groupCount = getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            removeGroup(0);
        }
    }

    /**
     * @param viewType
     * @return -1,表示没有找到
     */
    public int findFirstViewTypePosition(int viewType) {
        int targetPosition = -1;
        int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            if (viewType == getItemViewType(i)) {
                targetPosition = i;
                break;
            }
        }
        return targetPosition;
    }

    public int findLastViewTypePosition(int viewType) {
        int targetPosition = -1;
        int itemCount = getItemCount();
        for (int i = itemCount - 1; i >= 0; i--) {
            if (viewType == getItemViewType(i)) {
                targetPosition = i;
                break;
            }
        }
        return targetPosition;
    }

    public boolean removeFirstGroupByViewType(int viewType) {
        boolean success = false;
        int adapterGroupPos = findFirstViewTypePosition(viewType);
        if (-1 != adapterGroupPos) {
            int groupPos = getGroupPosition(adapterGroupPos);
            if (-1 != groupPos) {
                removeGroup(groupPos);
                success = true;
            }
        }
        return success;
    }

    /**
     * 这些viewType必须是连在一起的中间不能穿插其他的viewtype，否则得到的结果是错误的
     *
     * @param viewType
     * @return viewType的数量
     */
    public int getItemCount(int viewType) {
        int count = 0;
        int firstPos = findFirstViewTypePosition(viewType);
        if (-1 != firstPos) {
            int lastPos = findLastViewTypePosition(viewType);
            if (-1 != lastPos) {
                count = lastPos - firstPos + 1;
            } else {
                ILog.le("firstShowMapPos %d, lastShowMapPos %d", firstPos, lastPos);
            }
        }
        return count;
    }
}
