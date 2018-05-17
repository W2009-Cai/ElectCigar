package com.xiaolanba.passenger.module.market.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.xiaolanba.passenger.common.base.BaseRecyclerViewAdapter;
import com.xiaolanba.passenger.common.base.BaseRecyclerViewHolder;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/03/01
 */

public class OrderListAdapter extends BaseRecyclerViewAdapter<Object> {

    public OrderListAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((BaseRecyclerViewHolder)viewHolder).bindData(mList.get(position),position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new OrderListHolder(mContext,mInflater,viewGroup);
    }
}
