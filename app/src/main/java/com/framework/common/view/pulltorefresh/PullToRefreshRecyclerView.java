package com.framework.common.view.pulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import com.xlb.elect.cigar.R;

/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {

    public PullToRefreshRecyclerView(Context context) {
        super(context);
        init();
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
        init();
    }

    public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        init();
    }

    private void init() {
        setScrollingWhileRefreshingEnabled(true);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView scrollView;

        //		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
        //			scrollView = new InternalHorizontalScrollViewSDK9(context, attrs);
        //		} else {
        //			scrollView = new RecyclerView(context, attrs);
        //		}
        scrollView = new RecyclerView(context, attrs);
        scrollView.setOverScrollMode(OVER_SCROLL_NEVER);
        scrollView.setItemAnimator(null);
        scrollView.setId(R.id.scrollview);
        scrollView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                /**
                 * Check that the scrolling has stopped, and that the last item is
                 * visible.
                 */
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener && isReadyForPullEnd()) {
                    mOnLastItemVisibleListener.onLastItemVisible();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

        });
        return scrollView;
    }


    @Override
    protected boolean isReadyForPullStart() {
        RecyclerView.LayoutManager layoutManager = mRefreshableView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        int count = layoutManager.getItemCount();
        if (0 == count) {
            return true;
        }
        if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (0 == position) {
                return true;
            } else if (position == RecyclerView.NO_POSITION) {
                int itemCount = mRefreshableView.getAdapter().getItemCount();
                if (itemCount > 0) {
                    RecyclerView.ViewHolder viewHolder = mRefreshableView.findViewHolderForAdapterPosition(0);
                    if (null != viewHolder) {
                        return 0 == viewHolder.itemView.getTop();
                    }
                }
            } else {
                // 有item隐藏时调用此方法
                //				if (mRefreshableView.getChildCount() > 0) {
                //					final View firstVisibleChild = mRefreshableView.getChildAt(0);
                //					if (firstVisibleChild != null) {
                //						return firstVisibleChild.getTop() >= mRefreshableView.getTop();
                //					}
                //				}
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] positions = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null);
            boolean b = false;
            for (int p : positions) {
                if (0 == p) {
                    b = true;
                    break;
                }
            }
            return b;
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        RecyclerView.LayoutManager layoutManager = mRefreshableView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        if (0 == layoutManager.getItemCount()) {
            return true;
        }

        //TODO 这里暂时排除掉添加的footview (by comment nodata)
        int footCount = 0;
        final RecyclerView.Adapter adapter = mRefreshableView.getAdapter();
//        if (adapter != null && adapter instanceof ExpandableRecyclerViewWrapperAdapter) {
//            footCount = ((ExpandableRecyclerViewWrapperAdapter) adapter).getFootersCount();
//        }

        if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int lastCompletePos = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            if (linearLayoutManager.getItemCount() - footCount - 1 == lastCompletePos) {
                return true;
            }

            // TODO 评论数据没有占满一屏时上拉问题
            if (footCount > 0 && linearLayoutManager.getItemCount() - 1 == lastCompletePos) {
                return true;
            }

            /**
             * 当item的高度大于recyclerview的高度的时候,lastCompletePos=-1或者有些item的高度为0的时候，lastCompletePos也不是itemCount-1
             */

            //if (RecyclerView.NO_POSITION == lastCompletePos) {
            int itemCount = mRefreshableView.getAdapter().getItemCount();
            if (itemCount > 0) {
                RecyclerView.ViewHolder viewHolder = mRefreshableView.findViewHolderForAdapterPosition(itemCount - 1);
                if (null != viewHolder) {
                    int bottomMargin = linearLayoutManager.getBottomDecorationHeight(viewHolder.itemView);
                    int bottom = viewHolder.itemView.getBottom();
                    int rheight = mRefreshableView.getHeight();
                    return rheight == bottom + bottomMargin;
                }
            }
            //}
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] positions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
            boolean b = false;
            for (int p : positions) {
                if (staggeredGridLayoutManager.getItemCount() - footCount - 1 == p) {
                    b = true;
                    break;
                }
            }
            return b;
        }

        return false;
    }

    @TargetApi(9)
    final class InternalHorizontalScrollViewSDK9 extends RecyclerView {

        public InternalHorizontalScrollViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshRecyclerView.this, deltaX, scrollX, deltaY, scrollY, getScrollRange(), isTouchEvent);

            return returnValue;
        }

        /**
         * Taken from the AOSP ScrollView source
         */
        private int getScrollRange() {
            int scrollRange = 0;
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                scrollRange = Math.max(0, child.getWidth() - (getWidth() - getPaddingLeft() - getPaddingRight()));
            }
            return scrollRange;
        }
    }


//    public PullToRefreshRecyclerView(Context context) {
//        super(context);
//    }
//
//    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public PullToRefreshRecyclerView(Context context, Mode mode) {
//        super(context, mode);
//    }
//
//    public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle style) {
//        super(context, mode, style);
//    }
//
//    @Override
//    public final Orientation getPullToRefreshScrollDirection() {
//        return Orientation.VERTICAL;
//    }
//
//    @Override
//    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
//        RecyclerView scrollView;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            scrollView = new InternalHorizontalScrollViewSDK9(context, attrs);
//        } else {
//            scrollView = new RecyclerView(context, attrs);
//        }
//
//        scrollView.setId(R.id.scrollview);
//        return scrollView;
//    }
//
//
//    @Override
//    protected boolean isReadyForPullStart() {
//        RecyclerView.LayoutManager layoutManager = mRefreshableView.getLayoutManager();
//        if (0 == layoutManager.getItemCount()) {
//            return true;
//        }
//        if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
//            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
//            if (0 == linearLayoutManager.findFirstCompletelyVisibleItemPosition()) {
//                return true;
//            }
//        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
//            int[] positions = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null);
//            boolean b = false;
//            for (int p : positions) {
//                if (0 == p) {
//                    b = true;
//                    break;
//                }
//            }
//            return b;
//        }
//        return false;
//    }
//
//    @Override
//    protected boolean isReadyForPullEnd() {
//
//        RecyclerView.LayoutManager layoutManager = mRefreshableView.getLayoutManager();
//        if (0 == layoutManager.getItemCount()) {
//            return true;
//        }
//        if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
//            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
//            if (linearLayoutManager.getItemCount() - 1 == linearLayoutManager.findLastCompletelyVisibleItemPosition()) {
//                return true;
//            }
//        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
//            int[] positions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
//            boolean b = false;
//            for (int p : positions) {
//                if (staggeredGridLayoutManager.getItemCount() - 1 == p) {
//                    b = true;
//                    break;
//                }
//            }
//            return b;
//        }
//
//        return false;
//    }
//
//    @TargetApi(9)
//    final class InternalHorizontalScrollViewSDK9 extends RecyclerView {
//
//        public InternalHorizontalScrollViewSDK9(Context context, AttributeSet attrs) {
//            super(context, attrs);
//        }
//
//        @Override
//        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
//                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
//
//            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
//                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
//
//            // Does all of the hard work...
//            OverscrollHelper.overScrollBy(PullToRefreshRecyclerView.this, deltaX, scrollX, deltaY, scrollY,
//                    getScrollRange(), isTouchEvent);
//
//            return returnValue;
//        }
//
//        /**
//         * Taken from the AOSP ScrollView source
//         */
//        private int getScrollRange() {
//            int scrollRange = 0;
//            if (getChildCount() > 0) {
//                View child = getChildAt(0);
//                scrollRange = Math.max(0, child.getWidth() - (getWidth() - getPaddingLeft() - getPaddingRight()));
//            }
//            return scrollRange;
//        }
//    }
}