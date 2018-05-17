package com.xiaolanba.passenger.module.market.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaolanba.commonlib.fresco.FrescoUtil;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.common.bean.BaseBean;
import com.xiaolanba.passenger.common.bean.SubMultiLease;
import com.xiaolanba.passenger.common.bean.SubMultiSale;
import com.xiaolanba.passenger.common.view.LoadingNodataLayout;
import com.xiaolanba.passenger.module.market.adapter.MarketMainAdapter;
import com.xiaolanba.passenger.module.market.adapter.MarketSubAdapter;
import com.xiaolanba.passenger.module.market.presenter.ShopPresenter;
import com.xiaolanba.passenger.module.market.presenter.SubShopPresenter;
import com.xiaolanba.passenger.module.market.presenter.contract.SubShopContract;
import com.xlb.elect.cigar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 *
 * @author xutingz
 * @email：914603097@qq.com
 */

public class SubMarketActivity extends BaseActivity implements  SubShopContract.ViewControl{
    private RecyclerView mRecyclerView;
    private MarketSubAdapter mAdapter;
    private ImageButton mRightImg;
    private LoadingNodataLayout mNodtaLayout;
    private SubShopPresenter mPresenter = new SubShopPresenter(this);
    private int groupNum;
    public static void startActivity(Context context,int groupNum){
        Intent intent = new Intent(context,SubMarketActivity.class);
        intent.putExtra("groupnum",groupNum);
        context.startActivity(intent);
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_sub_market);
        Intent intent = getIntent();
        groupNum = intent.getIntExtra("groupnum",0);
    }

    @Override
    public void findView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        if (groupNum == 0){
            mTitle.setText("租赁列表");
        } else {
            mTitle.setText("商品列表");
        }

        findViewById(R.id.left_img_btn).setOnClickListener(this);
        mNodtaLayout = (LoadingNodataLayout) findViewById(R.id.nodata_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getItemViewType(position) == MarketSubAdapter.VIEW_TYPE_SHOPS ? 1 : 3;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addOnScrollListener(FrescoUtil.getPauseOnScrollListener());
        mAdapter = new MarketSubAdapter(context);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        mNodtaLayout.setVisibility(View.VISIBLE);
        mNodtaLayout.setIsLoadingUi(true);
        if (groupNum == 0){
            mPresenter.getSubLeaseList();
        } else {
            mPresenter.getSubSaleList();
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

    @Override
    public void onSubSaleList(boolean isSuccess, int code, String message, ArrayList<SubMultiSale> lists) {
        if (isSuccess){
            if (lists != null&& lists.size() >0){
                mNodtaLayout.setVisibility(View.GONE);
                for (SubMultiSale multiSale:lists){
                    mAdapter.addGroups(1,multiSale.class_id,multiSale.class_name);
                }
                int count = 0;
                for (SubMultiSale multiSale:lists){
                    if (multiSale.list != null && multiSale.list.size()>0) {
                        List<BaseBean> list = new ArrayList<>();
                        list.addAll(multiSale.list);
                        mAdapter.setRentalList(count,list );
                        count++;
                    }
                }
            } else {
                mNodtaLayout.setIsLoadingUi(false);
            }
        } else {
            mNodtaLayout.setIsLoadingUi(false);
        }
    }

    @Override
    public void onSubLeaseList(boolean isSuccess, int code, String message, ArrayList<SubMultiLease> lists) {
        if (isSuccess){
            if (lists != null&& lists.size() >0){
                mNodtaLayout.setVisibility(View.GONE);
                for (SubMultiLease multiSale:lists){
                    mAdapter.addGroups(0,multiSale.class_id,multiSale.class_name);
                }
                int count = 0;
                for (SubMultiLease multiSale:lists){
                    if (multiSale.list != null && multiSale.list.size()>0) {
                        List<BaseBean> list = new ArrayList<>();
                        list.addAll(multiSale.list);
                        mAdapter.setRentalList(count,list );
                        count++;
                    }
                }
            } else {
                mNodtaLayout.setIsLoadingUi(false);
            }
        } else {
            mNodtaLayout.setIsLoadingUi(false);
        }
    }
}
