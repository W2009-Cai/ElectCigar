package com.framework.library.haorefresh;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.framework.common.utils.ILog;

/**
 * HaoRecyclerView，封装底部加载更多
 */
public class HaoRecyclerView extends RecyclerView {

    private Context mContext;

    private LoadMoreListener loadMoreListener;
    //是否可加载更多
    private boolean canloadMore = true;

    private Adapter mAdapter;

    private Adapter mFooterAdapter;

    private ListView list;

    private boolean isLoadingData = false;
    //加载更多布局
    private LoadingMoreFooter loadingMoreFooter;

    public HaoRecyclerView(Context context) {
        this(context, null);
    }

    public HaoRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HaoRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LoadingMoreFooter footView = new LoadingMoreFooter(mContext);
        addFootView(footView);
        footView.setGone();
    }


    //点击监听
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (mFooterAdapter != null && mFooterAdapter instanceof FooterAdapter) {
            ((FooterAdapter) mFooterAdapter).setOnItemClickListener(onItemClickListener);
        }
    }


    //长按监听
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        if (mFooterAdapter != null && mFooterAdapter instanceof FooterAdapter) {
            ((FooterAdapter) mFooterAdapter).setOnItemLongClickListener(listener);
        }
    }

    /**
     * 底部加载更多视图
     *
     * @param view
     */
    public void addFootView(LoadingMoreFooter view) {
        loadingMoreFooter = view;
    }

    //设置底部加载中效果
    public void setFootLoadingView(View view) {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.addFootLoadingView(view);
        }
    }

    //设置底部到底了布局
    public void setFootEndView(View view) {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.addFootEndView(view);
        }
    }

    //下拉刷新后初始化底部状态
    public void refreshComplete() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.clearShowEnd();
            loadingMoreFooter.setGone();
        }
        isLoadingData = false;
    }

    public void loadMoreComplete() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.setGone();
        }
        isLoadingData = false;
    }

    public boolean isLoading(){
        return isLoadingData;
    }

    public void setLoading(boolean flag){
        isLoadingData = flag;
    }

    //到底了
    public void loadMoreEnd() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.setEnd();
        }
    }

    public boolean isShowEnd(){
        if (loadingMoreFooter != null){
            return loadingMoreFooter.isShowEnd();
        }
        return false;
    }

    //设置是否可加载更多
    public void setCanloadMore(boolean flag) {
        canloadMore = flag;
    }

    //设置加载更多监听
    public void setLoadMoreListener(LoadMoreListener listener) {
        loadMoreListener = listener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mFooterAdapter = new FooterAdapter(this, loadingMoreFooter, adapter);
        super.setAdapter(mFooterAdapter);
        mAdapter.registerAdapterDataObserver(mDataObserver);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if (state == RecyclerView.SCROLL_STATE_IDLE && loadMoreListener != null && !isLoadingData && canloadMore && !isShowEnd()) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = last(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (layoutManager.getChildCount() > 1
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount()) {
                if (loadingMoreFooter != null) {
                    loadingMoreFooter.setVisible();
                }
                isLoadingData = true;
                loadMoreListener.onLoadMore();
            }
        }
    }


    // 取到最后的一个节点
    private int last(int[] lastPositions) {
        int last = lastPositions[0];
        for (int value : lastPositions) {
            if (value > last) {
                last = value;
            }
        }
        return last;
    }

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mFooterAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            ILog.i("mm","---mDataObserver onItemRangeInserted= positionStart"+positionStart+
            "--itemCount="+itemCount);
            mFooterAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mFooterAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mFooterAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mFooterAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mFooterAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };


}
