package com.xiaolanba.passenger.module.market.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaolanba.commonlib.fresco.FrescoUtil;
import com.xiaolanba.passenger.common.base.BaseRecyclerViewHolder;
import com.xiaolanba.passenger.common.bean.BaseBean;
import com.xiaolanba.passenger.common.bean.LeaseItem;
import com.xiaolanba.passenger.common.bean.SaleItem;
import com.xiaolanba.passenger.common.bean.ShowItem;
import com.xlb.elect.cigar.R;

/**
 * Created by aaa on 2018/5/17.
 */

public class ShopDetilImageHolder extends BaseRecyclerViewHolder<BaseBean> {
    private SimpleDraweeView mImg;
    public ShopDetilImageHolder(Context context, LayoutInflater inflater, ViewGroup viewGroup) {
        super(R.layout.shop_detail_list_item, context, inflater, viewGroup);
    }

    @Override
    public void findView() {
        mImg = (SimpleDraweeView) itemView.findViewById(R.id.image);
    }

    @Override
    public void bindData(BaseBean bean, int position) {
        if (bean instanceof SaleItem){
            SaleItem saleItem = (SaleItem) bean;
            FrescoUtil.loadImage(mImg,saleItem.image);
        } else if (bean instanceof LeaseItem){
            LeaseItem leaseItem = (LeaseItem) bean;
            FrescoUtil.loadImage(mImg,leaseItem.image);
        } else if (bean instanceof ShowItem){
            ShowItem showItem = (ShowItem) bean;
            FrescoUtil.loadImage(mImg,showItem.image);
        }
    }
}
