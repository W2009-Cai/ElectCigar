package com.xiaolanba.passenger.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framework.common.base.IBaseActivity;
import com.framework.common.base.IBaseFragmentActivity;
import com.framework.common.utils.ICommonUtil;

/**
 * RecyclerViewHolder 基类
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public abstract class BaseRecyclerViewHolder<T> extends RecyclerView.ViewHolder {
    protected final String TAG = getClass().getSimpleName();
    protected Context mContext;

    public BaseRecyclerViewHolder(int resource, Context context, LayoutInflater inflater, ViewGroup viewGroup) {
        this(context, inflater.inflate(resource, viewGroup, false));
    }

    public BaseRecyclerViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        findView();
    }

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        findView();
    }


    public abstract void findView();

    public abstract void bindData(T bean, int position);

    public void registerItemClick(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    public void registerItemClick(final T bean, final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ICommonUtil.isFastDoubleClick(v.getId())) {
                    return;
                }
                onItemClick(bean, position);
            }
        });
    }

    public void unregisterItemClick() {
        itemView.setOnClickListener(null);
    }

    public void onItemClick(T bean, int position) {

    }

    public void bindData(T bean) {
        bindData(bean, 0);
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

    public void startActivity(Class cls) {
        if (mContext == null) {
            return;
        }
        Intent intent = new Intent(mContext, cls);
        if (!(mContext instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }
}
