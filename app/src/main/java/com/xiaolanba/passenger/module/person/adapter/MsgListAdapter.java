package com.xiaolanba.passenger.module.person.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xiaolanba.passenger.common.base.BaseRecyclerViewAdapter;
import com.xiaolanba.passenger.module.person.adapter.holder.MsgViewHolder;
import com.xlb.elect.cigar.R;

/**
 *
 * @Author hai
 * @E-mail shihh@xianlanba.com
 * @Date 2018/04/02
 */
public class MsgListAdapter extends BaseRecyclerViewAdapter<Object> {

    public MsgListAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((MsgViewHolder) viewHolder).bindData(getItem(position),position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View inflate = mInflater.inflate(R.layout.activity_lanucher, null);
        return new MsgViewHolder(mContext,inflate);
    }
}
