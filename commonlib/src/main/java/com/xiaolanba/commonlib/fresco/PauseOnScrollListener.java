package com.xiaolanba.commonlib.fresco;

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class PauseOnScrollListener extends RecyclerView.OnScrollListener implements AbsListView.OnScrollListener {

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                onResume();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                onPause();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                onResume();
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                onResume();
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                onPause();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                onResume();
                break;
        }
    }

    private void onPause() {
        FrescoUtil.pause();
    }

    private void onResume() {
        FrescoUtil.resume();
    }
}
