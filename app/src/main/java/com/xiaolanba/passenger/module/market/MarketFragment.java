package com.xiaolanba.passenger.module.market;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaolanba.commonlib.fresco.FrescoUtil;
import com.xiaolanba.passenger.common.base.BaseFragment;
import com.xiaolanba.passenger.common.bean.BaseBean;
import com.xiaolanba.passenger.common.bean.MultiShopList;
import com.xiaolanba.passenger.common.view.LoadingNodataLayout;
import com.xiaolanba.passenger.module.market.adapter.MarketMainAdapter;
import com.xiaolanba.passenger.module.market.presenter.ShopPresenter;
import com.xiaolanba.passenger.module.market.presenter.contract.ShopContract;
import com.xlb.elect.cigar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/02
 */

public class MarketFragment extends BaseFragment implements ShopContract.ViewControl{

    private RecyclerView mRecyclerView;
    private MarketMainAdapter mAdapter;
    private ImageButton mRightImg;
    private LoadingNodataLayout mNodtaLayout;

    private ShopPresenter mPresenter = new ShopPresenter(this);
    @Override
    public void setContentView() {
        setContentView(R.layout.fragment_market);
    }

    @Override
    public void findView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText("商城");
        findViewById(R.id.left_img_btn).setVisibility(View.GONE);
        mNodtaLayout = (LoadingNodataLayout) findViewById(R.id.nodata_layout);
        mRightImg = (ImageButton) findViewById(R.id.right_img_btn);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setImageResource(R.drawable.nav_icon_shopping);
        mRightImg.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getItemViewType(position) == MarketMainAdapter.VIEW_TYPE_SHOPS ? 1 : 3;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addOnScrollListener(FrescoUtil.getPauseOnScrollListener());
        mAdapter = new MarketMainAdapter(context);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        mNodtaLayout.setVisibility(View.VISIBLE);
        mNodtaLayout.setIsLoadingUi(true);
        mPresenter.getShopIndexList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right_img_btn:
                showToast("点击了购物车");
                break;
        }
    }

    @Override
    public void onShopIndexList(boolean isSuccess, int code, String message, MultiShopList lists) {

        if (isSuccess){
            if (lists != null&& lists.getTotalSize() >0){
                mNodtaLayout.setVisibility(View.GONE);
                if (lists.lease != null ){
                    List<BaseBean> list = new ArrayList<>();
                    list.addAll(lists.lease);
                    mAdapter.setRentalList(list);
                }
                if (lists.sales != null ){
                    List<BaseBean> list = new ArrayList<>();
                    list.addAll(lists.sales);
                    mAdapter.setLampList(list);
                }
                if (lists.show != null ){
                    List<BaseBean> list = new ArrayList<>();
                    list.addAll(lists.show);
                    mAdapter.setBuyerShowList(list);
                }
            } else {
                mNodtaLayout.setIsLoadingUi(false);
            }
        } else {
            mNodtaLayout.setIsLoadingUi(false);
        }
    }
}
