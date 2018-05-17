package com.xiaolanba.passenger.module.market.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaolanba.commonlib.fresco.FrescoUtil;
import com.xiaolanba.passenger.common.base.BaseRecyclerViewHolder;
import com.xiaolanba.passenger.common.bean.BaseBean;
import com.xiaolanba.passenger.common.bean.LeaseItem;
import com.xiaolanba.passenger.common.bean.SaleItem;
import com.xiaolanba.passenger.common.bean.ShowItem;
import com.xiaolanba.passenger.rxandroid.api.ApiManager;
import com.xlb.elect.cigar.R;

/**
 * 类描述
 *
 * @author xutingz
 * @email：914603097@qq.com
 */
public class ShopHeaderHolder extends BaseRecyclerViewHolder<BaseBean> {
    private TextView mShopTitle,mPriceTxt;
    private SimpleDraweeView mTopImg;

    public ShopHeaderHolder(Context context, LayoutInflater inflater, ViewGroup viewGroup) {
        super(R.layout.shop_detail_header, context, inflater, viewGroup);
    }

    @Override
    public void findView() {
        mTopImg = (SimpleDraweeView) itemView.findViewById(R.id.top_img);
        mShopTitle = (TextView) itemView.findViewById(R.id.shop_title);
        mPriceTxt = (TextView) itemView.findViewById(R.id.price_txt);
    }

    @Override
    public void bindData(BaseBean bean, int position) {
        if (bean instanceof SaleItem){
            SaleItem saleItem = (SaleItem) bean;
            loadImage(saleItem.image);
            showTitle(saleItem.sales_name);
            showPrice(saleItem.price,false);
        } else if (bean instanceof LeaseItem){
            LeaseItem leaseItem = (LeaseItem) bean;
            loadImage(leaseItem.image);
            showTitle(leaseItem.lease_name);
            showPrice(leaseItem.price,true);
        } else if (bean instanceof ShowItem){
            ShowItem showItem = (ShowItem) bean;
            loadImage(showItem.image);
            showTitle(showItem.sales_name);
            showPrice(showItem.price,false);
        }
    }

    private void loadImage(String url){
        FrescoUtil.loadImage(mTopImg, ApiManager.HEAR_URL+url);
    }

    private void showTitle(String title){
        mShopTitle.setText(title);
    }
    private void showPrice(float price,boolean isLease){
        if (isLease){
            mPriceTxt.setText("¥"+String.valueOf(price));
        } else {
            mPriceTxt.setText("¥"+String.valueOf(price)+"/天");
        }

    }
}
