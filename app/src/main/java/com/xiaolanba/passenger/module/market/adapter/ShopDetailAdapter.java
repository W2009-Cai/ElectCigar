package com.xiaolanba.passenger.module.market.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.xiaolanba.passenger.common.bean.BaseBean;
import com.xiaolanba.passenger.common.bean.LeaseItem;
import com.xiaolanba.passenger.common.bean.SaleItem;
import com.xiaolanba.passenger.common.bean.ShowItem;
import com.xiaolanba.passenger.module.common.adapter.CommonExpandWrapperAdapter;
import com.xiaolanba.passenger.module.market.holder.ShopDetilImageHolder;
import com.xiaolanba.passenger.module.market.holder.ShopsHolder;

/**
 * 可以添加头部和尾部的Adapter
 * Created by aaa on 2018/5/17.
 */

public class ShopDetailAdapter extends CommonExpandWrapperAdapter {

    /** 图片列表 */
    public static final int VIEW_TYPE_IMAGE = 101;

    public ShopDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (isHeader(viewType) || isFooter(viewType)) {
            viewHolder = super.onCreateViewHolder(parent, viewType);
        } else {
            viewHolder = new ShopDetilImageHolder(mContext,mLayoutInflater, parent);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ShopDetilImageHolder) {
            ((ShopDetilImageHolder) viewHolder).bindData(getItem(position), position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        BaseBean bean = getItem(position);
        if (bean instanceof FixedViewInfo) {
            type = ((FixedViewInfo) bean).viewType;
        } else if (bean instanceof BaseBean) {
            type = VIEW_TYPE_IMAGE;
        } else {
            type = super.getItemViewType(position);
        }
        return type;
    }

}
