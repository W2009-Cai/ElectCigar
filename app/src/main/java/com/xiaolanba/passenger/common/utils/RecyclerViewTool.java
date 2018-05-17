package com.xiaolanba.passenger.common.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ScrollView;

import com.framework.common.utils.ILog;

/**
 * RecyclerView工具类
 *
 * @author xutingz
 */

public class RecyclerViewTool {

    /**
     * @param view
     * @return view是否滚动到顶部了
     */
    public static boolean isContentToTop(View view) {

        if (null == view) {
            ILog.le("null == view");
            return true;
        }

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int itemCount = layoutManager.getItemCount();
            ILog.lv("itemCount = %d", itemCount);
            if (0 == itemCount) {
                return true;
            }

            int[] poss = getRecyclerViewFirstCompleteVisiblePos(recyclerView);
            boolean iTop = false;
            for (int p : poss) {
                ILog.lv("first complete visible position = %d", p);
                if (0 == p) {
                    iTop = true;
                    break;
                } else if (RecyclerView.NO_POSITION == p) {//NO_POSITION,比如第一个view高度很高,这种情况就没有完全显示的item
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(
                            0);
                    if (null == viewHolder) {
                        ILog.lv("null == viewHolder");
                        iTop = false;
                    } else {
                        int top = viewHolder.itemView.getTop();
                        ILog.lv("first item view top = %d", top);
                        iTop = 0 == top;
                        if (iTop) {
                            break;
                        }
                    }
                }
            }
            return iTop;
        } else if (view instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) view;
            if (0 == scrollView.getScrollY()) {
                return true;
            }
        } else if (view instanceof View) {
            return true;
        }
        return false;
    }


    public static int[] getRecyclerViewFirstCompleteVisiblePos(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            return new int[]{linearLayoutManager.findFirstCompletelyVisibleItemPosition()};
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            return staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null);
        }
        return null;
    }

    public static boolean isContentToBottom(View view) {
        int targetVisibleItemIndex = -1;
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            targetVisibleItemIndex = recyclerView.getAdapter().getItemCount() - 1;
        }
        return isContentToBottom(view, targetVisibleItemIndex);
    }


    /**
     * @param view * @param targetVisibleItemIndex 已哪一个item（索引）显示为标准
     * @return content view是否已经到达最底部了
     */
    public static boolean isContentToBottom(View view, int targetVisibleItemIndex) {
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (0 == layoutManager.getItemCount()) {
                return false;
            }
            if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (targetVisibleItemIndex == linearLayoutManager.findLastCompletelyVisibleItemPosition()) {
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] positions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(
                        null);
                boolean b = false;
                for (int p : positions) {
                    if (targetVisibleItemIndex == p) {
                        b = true;
                        break;
                    }
                }
                return b;
            }
        } else if (view instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) view;
            if (scrollView.getScrollY() + scrollView.getHeight() >= scrollView.getMeasuredHeight()) {
                return true;
            }
        } else if (view instanceof View) {
            return true;
        }
        return false;
    }

    public static boolean isContentNearBottom(RecyclerView recyclerView) {
        return isContentNearBottom(recyclerView, 0);
    }

    /**
     * 滚动到底了，但是还没有完全到底的情况
     *
     * @param recyclerView
     * @return
     */
    public static boolean isContentNearBottom(RecyclerView recyclerView, int offset) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (0 == layoutManager.getItemCount()) {
            return false;
        }
        if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (linearLayoutManager.getItemCount() - 1 - offset == linearLayoutManager.findLastVisibleItemPosition()) {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] positions = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
            boolean b = false;
            for (int p : positions) {
                if (staggeredGridLayoutManager.getItemCount() - 1 - offset == p) {
                    b = true;
                    break;
                }
            }
            return b;
        }
        return false;
    }

    /**
     * 滚动到顶了，但是还没有完全到顶的情况
     *
     * @param recyclerView
     * @return
     */
    public static boolean isContentNearTop(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (0 == layoutManager.getItemCount()) {
            return false;
        }
        if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (0 == linearLayoutManager.findFirstVisibleItemPosition()) {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] positions = staggeredGridLayoutManager.findFirstVisibleItemPositions(null);
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

    /**
     * @param recyclerView
     * @param isFirst      true,第一个;false,最后一个
     * @return
     */
    public static int[] getRecyclerViewFirstCompleteVisiblePos(RecyclerView recyclerView,
                                                               boolean isComplete,
                                                               boolean isFirst) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager || layoutManager instanceof GridLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (isComplete) {
                if (isFirst) {
                    return new int[]{linearLayoutManager.findFirstCompletelyVisibleItemPosition()};
                } else {
                    return new int[]{linearLayoutManager.findLastCompletelyVisibleItemPosition()};
                }
            } else {
                if (isFirst) {
                    return new int[]{linearLayoutManager.findFirstVisibleItemPosition()};
                } else {
                    return new int[]{linearLayoutManager.findLastVisibleItemPosition()};
                }
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            if (isComplete) {
                if (isFirst) {
                    return staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null);
                } else {
                    return staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
                }
            } else {
                if (isFirst) {
                    return staggeredGridLayoutManager.findFirstVisibleItemPositions(null);
                } else {
                    return staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                }
            }
        }
        return null;
    }

}
