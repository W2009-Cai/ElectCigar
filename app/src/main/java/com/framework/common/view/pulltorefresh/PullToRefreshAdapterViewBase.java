/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.framework.common.view.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.framework.common.view.pulltorefresh.internal.EmptyViewMethodAccessor;

@SuppressLint("InflateParams")
public abstract class PullToRefreshAdapterViewBase<T extends AbsListView> extends PullToRefreshBase<T> implements
		OnScrollListener {

	private static FrameLayout.LayoutParams convertEmptyViewLayoutParams(ViewGroup.LayoutParams lp) {
		FrameLayout.LayoutParams newLp = null;

		if (null != lp) {
			newLp = new FrameLayout.LayoutParams(lp);

			if (lp instanceof LinearLayout.LayoutParams) {
				newLp.gravity = ((LinearLayout.LayoutParams) lp).gravity;
			} else {
				newLp.gravity = Gravity.CENTER;
			}
		}

		return newLp;
	}

	protected OnScrollListener mOnScrollListener;

	private View mEmptyView;
	
	private boolean mScrollEmptyView = true;
	
	public PullToRefreshAdapterViewBase(Context context) {
		super(context);
		init();
	}

	public PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshAdapterViewBase(Context context, Mode mode) {
		super(context, mode);
		init();
	}

	public PullToRefreshAdapterViewBase(Context context, Mode mode, AnimationStyle animStyle) {
		super(context, mode, animStyle);
		init();
	}
	
	private void init(){
		mRefreshableView.setOnScrollListener(this);
	}
	
	public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
			final int totalItemCount) {
		
		//ILog.d(TAG, "First Visible: " + firstVisibleItem + ". Visible Count: " + visibleItemCount + ". Total Items:" + totalItemCount);

		/**
		 * Set whether the Last Item is Visible. lastVisibleItemIndex is a
		 * zero-based index, so we minus one totalItemCount to check
		 */
		if (null != mOnLastItemVisibleListener) {
			mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
		}
		
		// Finally call OnScrollListener if we have one
		if (null != mOnScrollListener) {
			mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public void onScrollStateChanged(final AbsListView view, final int state) {
		/**
		 * Check that the scrolling has stopped, and that the last item is
		 * visible.
		 */
		if (state == OnScrollListener.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener && mLastItemVisible) {
			mOnLastItemVisibleListener.onLastItemVisible();
		}

		if (null != mOnScrollListener) {
			mOnScrollListener.onScrollStateChanged(view, state);
		}
	}

	/**
	 * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
	 * getRefreshableView()}.
	 * {@link AdapterView#setAdapter(android.widget.Adapter)}
	 * setAdapter(adapter)}. This is just for convenience!
	 * 
	 * @param adapter - Adapter to set
	 */
	public void setAdapter(ListAdapter adapter) {
		((AdapterView<ListAdapter>) mRefreshableView).setAdapter(adapter);
	}

	/**
	 * Sets the Empty View to be used by the Adapter View.
	 * <p/>
	 * We need it handle it ourselves so that we can Pull-to-Refresh when the
	 * Empty View is shown.
	 * <p/>
	 * Please note, you do <strong>not</strong> usually need to call this method
	 * yourself. Calling setEmptyView on the AdapterView will automatically call
	 * this method and set everything up. This includes when the Android
	 * Framework automatically sets the Empty View based on it's ID.
	 * 
	 * @param newEmptyView - Empty View to be used
	 */
	public final void setEmptyView(View newEmptyView) {
		FrameLayout refreshableViewWrapper = getRefreshableViewWrapper();

		if (null != newEmptyView) {
			// New view needs to be clickable so that Android recognizes it as a
			// target for Touch Events
			newEmptyView.setClickable(true);

			ViewParent newEmptyViewParent = newEmptyView.getParent();
			if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup) {
				((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
			}

			// We need to convert any LayoutParams so that it works in our
			// FrameLayout
			FrameLayout.LayoutParams lp = convertEmptyViewLayoutParams(newEmptyView.getLayoutParams());
			if (null != lp) {
				refreshableViewWrapper.addView(newEmptyView, lp);
			} else {
				refreshableViewWrapper.addView(newEmptyView);
			}
		}

		if (mRefreshableView instanceof EmptyViewMethodAccessor) {
			((EmptyViewMethodAccessor) mRefreshableView).setEmptyViewInternal(newEmptyView);
		} else {
			mRefreshableView.setEmptyView(newEmptyView);
		}
		mEmptyView = newEmptyView;
	}

	/**
	 * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
	 * getRefreshableView()}.
	 * {@link AdapterView#setOnItemClickListener(OnItemClickListener)
	 * setOnItemClickListener(listener)}. This is just for convenience!
	 * 
	 * @param listener - OnItemClickListener to use
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		mRefreshableView.setOnItemClickListener(listener);
	}
	
	
	public void setOnScrollListener(OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	public final void setScrollEmptyView(boolean doScroll) {
		mScrollEmptyView = doScroll;
	}

	@Override
	protected void onPullToRefresh() {
		super.onPullToRefresh();
	}
	
	protected void onRefreshing(boolean doScroll) {
		super.onRefreshing(doScroll);
	}
	
	@Override
	public void onRefreshComplete() {
		super.onRefreshComplete();
	}
	
	@Override
	protected void onReleaseToRefresh() {
		super.onReleaseToRefresh();
	}

	@Override
	protected void onReset() {
		super.onReset();
	}

	@Override
	protected void handleStyledAttributes(TypedArray a) {
		
	}

	protected boolean isReadyForPullStart() {
		return isFirstItemVisible();
	}

	protected boolean isReadyForPullEnd() {
		return isLastItemVisible();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
		if (null != mEmptyView && !mScrollEmptyView) {
			mEmptyView.scrollTo(-l, -t);
		}
		
	}

	@Override
	protected void updateUIForMode() {
		super.updateUIForMode();
	}

	private boolean isFirstItemVisible() {
		final Adapter adapter = mRefreshableView.getAdapter();

		if (null == adapter || adapter.isEmpty()) {
			
			//Log.d(TAG, "isFirstItemVisible. Empty View.");
			
			//TODO
			if (isRefreshing()) {
				return false;
			}
			if (mRefreshableView.getChildCount() > 0) {
				final View firstVisibleChild = mRefreshableView.getChildAt(0);
				if (firstVisibleChild != null) {
					return firstVisibleChild.getTop() >= mRefreshableView.getTop();
				}
			}
			
			return true;

		} else {

			/**
			 * This check should really just be:
			 * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
			 * internally use a HeaderView which messes the positions up. For
			 * now we'll just add one to account for it and rely on the inner
			 * condition which checks getTop().
			 */
			if (mRefreshableView.getFirstVisiblePosition() <= 1) {
				final View firstVisibleChild = mRefreshableView.getChildAt(0);
				if (firstVisibleChild != null) {
					return firstVisibleChild.getTop() >= mRefreshableView.getTop();
				}
			}
		}

		return false;
	}

	private boolean isLastItemVisible() {
		final Adapter adapter = mRefreshableView.getAdapter();

		if (null == adapter || adapter.isEmpty()) {
			
			//Log.d(TAG, "isLastItemVisible. Empty View.");
			
			//TODO
			if (isRefreshing()) {
				return false;
			}
			if (mRefreshableView.getChildCount() > 0) {
				final View lastVisibleChild = mRefreshableView.getChildAt(mRefreshableView.getChildCount()-1);
				if (lastVisibleChild != null) {
					return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
				}
			}
			
			return true;
		} else {
			final int lastItemPosition = mRefreshableView.getCount() - 1;
			final int lastVisiblePosition = mRefreshableView.getLastVisiblePosition();

			// ILog.d(TAG, "isLastItemVisible. Last Item Position: " + lastItemPosition + " Last Visible Pos: " + lastVisiblePosition);

			/**
			 * This check should really just be: lastVisiblePosition ==
			 * lastItemPosition, but PtRListView internally uses a FooterView
			 * which messes the positions up. For me we'll just subtract one to
			 * account for it and rely on the inner condition which checks
			 * getBottom().
			 */
			if (lastVisiblePosition >= lastItemPosition - 1) {
				final int childIndex = lastVisiblePosition - mRefreshableView.getFirstVisiblePosition();
				final View lastVisibleChild = mRefreshableView.getChildAt(childIndex);
				if (lastVisibleChild != null) {
					return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
				}
			}
		}

		return false;
	}
}
