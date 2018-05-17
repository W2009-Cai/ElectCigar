package com.framework.common.view.swipemenulistview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;

import com.framework.common.view.pulltorefresh.OverscrollHelper;
import com.framework.common.view.pulltorefresh.PullToRefreshAdapterViewBase;
import com.framework.common.view.pulltorefresh.internal.EmptyViewMethodAccessor;

/**
 * SwipeMenuListView 右滑删除
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class PullToRefreshSwipeMenuListView extends PullToRefreshAdapterViewBase<SwipeMenuListView> {

	public PullToRefreshSwipeMenuListView(Context context) {
		super(context);
	}

	public PullToRefreshSwipeMenuListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshSwipeMenuListView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshSwipeMenuListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected SwipeMenuListView createRefreshableView(Context context, AttributeSet attrs) {
		final SwipeMenuListView lv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			lv = new InternalExpandableListViewSDK9(context, attrs);
		} else {
			lv = new InternalExpandableListView(context, attrs);
		}

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	class InternalExpandableListView extends SwipeMenuListView implements EmptyViewMethodAccessor {

		public InternalExpandableListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshSwipeMenuListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}
	}

	@TargetApi(9)
	final class InternalExpandableListViewSDK9 extends SwipeMenuListView {

		public InternalExpandableListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshSwipeMenuListView.this, deltaX, scrollX, deltaY, scrollY,
					isTouchEvent);

			return returnValue;
		}
	}
}