package com.xiaolanba.passenger.module.market.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaolanba.commonlib.fresco.FrescoUtil;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.common.bean.BaseBean;
import com.xiaolanba.passenger.common.bean.LeaseItem;
import com.xiaolanba.passenger.common.bean.SaleItem;
import com.xiaolanba.passenger.common.bean.ShowItem;
import com.xiaolanba.passenger.module.market.adapter.ShopDetailAdapter;
import com.xiaolanba.passenger.module.market.holder.ShopHeaderHolder;
import com.xiaolanba.passenger.rxandroid.api.ApiManager;
import com.xlb.elect.cigar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 *
 * @author xutingz
 * @email：914603097@qq.com
 */

public class ShopDetailActivity extends BaseActivity {

    private ViewGroup mBuyerLayout,mLeaseLayout;
    public BaseBean mBaseBean;
    private ShopDetailAdapter mAdapter;
    private RecyclerView mRecyclerView;
    public static void startActivity(Context context, BaseBean baseBean){
        Intent intent = new Intent(context,ShopDetailActivity.class);
        intent.putExtra("baseBean",baseBean);
        context.startActivity(intent);
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_shop_detail);
        Intent intent = getIntent();
        mBaseBean = (BaseBean) intent.getSerializableExtra("baseBean");
    }

    @Override
    public void findView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText("商品详情");
        findViewById(R.id.left_img_btn).setOnClickListener(this);
        mBuyerLayout = (ViewGroup) findViewById(R.id.buy_layout);
        mLeaseLayout = (ViewGroup) findViewById(R.id.lease_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ShopDetailAdapter(context);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void initData() {
        ShopHeaderHolder headerHolder = new ShopHeaderHolder(context,LayoutInflater.from(context),mRecyclerView);
        headerHolder.bindData(mBaseBean);
        mAdapter.addHeaderView(headerHolder.itemView);
        List<BaseBean> list = new ArrayList<>();
        for (int i=0;i<3;i++){
            list.add(new BaseBean());
        }
        mAdapter.setChild(list);
        if (mBaseBean instanceof SaleItem){
            SaleItem saleItem = (SaleItem) mBaseBean;
            mLeaseLayout.setVisibility(View.GONE);
            mBuyerLayout.setVisibility(View.VISIBLE);
        } else if (mBaseBean instanceof LeaseItem){
            LeaseItem leaseItem = (LeaseItem) mBaseBean;
            mLeaseLayout.setVisibility(View.VISIBLE);
            mBuyerLayout.setVisibility(View.GONE);
        } else if (mBaseBean instanceof ShowItem){
            ShowItem showItem = (ShowItem) mBaseBean;
            mLeaseLayout.setVisibility(View.GONE);
            mBuyerLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_img_btn:
                finish();
                break;
        }
    }
}
