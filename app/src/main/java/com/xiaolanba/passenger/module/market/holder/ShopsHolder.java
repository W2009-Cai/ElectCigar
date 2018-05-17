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
import com.xiaolanba.passenger.common.bean.ShopsBean;
import com.xiaolanba.passenger.common.bean.ShowItem;
import com.xiaolanba.passenger.module.market.activity.ShopDetailActivity;
import com.xiaolanba.passenger.rxandroid.api.ApiManager;
import com.xlb.elect.cigar.R;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/03
 */

public class ShopsHolder extends BaseRecyclerViewHolder<BaseBean> {

    private TextView mTitleTxt,mPriceTxt;
    private SimpleDraweeView mImage;
    public ShopsHolder(Context context, LayoutInflater inflater, ViewGroup viewGroup) {
        super(R.layout.shops_single_item, context, inflater, viewGroup);
    }

    @Override
    public void findView() {
        mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
        mTitleTxt = (TextView) itemView.findViewById(R.id.title_txt);
        mPriceTxt = (TextView) itemView.findViewById(R.id.price_txt);
    }

    @Override
    public void bindData(BaseBean bean, int position) {
        registerItemClick(bean,position);
        if (bean instanceof LeaseItem){
            setLease((LeaseItem) bean);
        } else if (bean instanceof SaleItem){
            setSale((SaleItem) bean);
        } else if (bean instanceof ShowItem){
            setShow((ShowItem) bean);
        }
    }

    private void setLease(LeaseItem item){
        FrescoUtil.loadImage(mImage, ApiManager.HEAR_URL + item.image);
        mTitleTxt.setText(item.lease_name);
        mPriceTxt.setText("￥"+item.price+"/天");
    }
    private void setSale(SaleItem item){
        FrescoUtil.loadImage(mImage, ApiManager.HEAR_URL + item.image);
        mTitleTxt.setText(item.sales_name);
        mPriceTxt.setText("￥"+item.price+"/套");
    }
    private void setShow(ShowItem item){
        FrescoUtil.loadImage(mImage, ApiManager.HEAR_URL + item.image);
        mTitleTxt.setText(item.sales_name);
        mPriceTxt.setText("￥"+item.price);
    }

    @Override
    public void onItemClick(BaseBean bean, int position) {
        ShopDetailActivity.startActivity(mContext,bean);
    }
}
