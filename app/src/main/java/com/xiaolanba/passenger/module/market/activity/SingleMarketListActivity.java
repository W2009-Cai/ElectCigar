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
import com.xiaolanba.passenger.common.bean.LeaseItem;
import com.xiaolanba.passenger.common.bean.SaleItem;
import com.xiaolanba.passenger.common.bean.SubMultiLease;
import com.xiaolanba.passenger.common.view.LoadingNodataLayout;
import com.xiaolanba.passenger.module.market.adapter.MarketSubAdapter;
import com.xiaolanba.passenger.module.market.presenter.SingleListPresenter;
import com.xiaolanba.passenger.module.market.presenter.contract.SingleListContract;
import com.xlb.elect.cigar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 *
 * @author xutingz
 * @email：914603097@qq.com
 */

public class SingleMarketListActivity extends BaseActivity implements SingleListContract.ViewControl {
    private RecyclerView mRecyclerView;
    private MarketSubAdapter mAdapter;
    private ImageButton mRightImg;
    private LoadingNodataLayout mNodtaLayout;

    SingleListPresenter mPresenter = new SingleListPresenter(this);

    private int groupNum;
    public long class_id;

    public static void startActivity(Context context, int groupNum, long id) {
        Intent intent = new Intent(context, SingleMarketListActivity.class);
        intent.putExtra("groupnum", groupNum);
        intent.putExtra("class_id", id);
        context.startActivity(intent);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_sub_market);
        Intent intent = getIntent();
        groupNum = intent.getIntExtra("groupnum", 0);
        class_id = intent.getLongExtra("class_id", 0);
    }

    @Override
    public void findView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText("分类商品列表");
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
        if (groupNum == 0) {
            mPresenter.getSingleLeaseList(class_id);
        } else {
            mPresenter.getSingleSaleList(class_id);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_img_btn:
                finish();
                break;
        }
    }

    @Override
    public void onSingleSaleList(boolean isSuccess, int code, String message, ArrayList<SaleItem> lists) {

    }

    @Override
    public void onSingleLeaseList(boolean isSuccess, int code, String message, ArrayList<LeaseItem> lists) {
        if (isSuccess) {
            if (lists != null && lists.size() > 0) {
                mNodtaLayout.setVisibility(View.GONE);
                List<BaseBean> list = new ArrayList<>();
                list.addAll(lists);
                mAdapter.addChild(list);
            } else {
                mNodtaLayout.setIsLoadingUi(false);
            }
        } else {
            mNodtaLayout.setIsLoadingUi(false);
        }
    }
}
