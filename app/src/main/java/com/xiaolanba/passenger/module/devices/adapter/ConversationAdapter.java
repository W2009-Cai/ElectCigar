package com.xiaolanba.passenger.module.devices.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.framework.common.utils.IDisplayUtil;
import com.xiaolanba.passenger.common.bean.BaseBean;
import com.xiaolanba.passenger.common.bean.DevicesBind;
import com.xiaolanba.passenger.common.interfaces.EventListener;
import com.xiaolanba.passenger.library.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.xiaolanba.passenger.library.advrecyclerview.swipeable.SwipeableItemConstants;
import com.xiaolanba.passenger.library.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.xiaolanba.passenger.library.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.xiaolanba.passenger.module.common.adapter.CommonExpandWrapperAdapter;
import com.xlb.elect.cigar.R;

import static com.umeng.analytics.pro.i.a.i;

/**
 * 会话消息列表，可右滑删除
 *
 * @author xutingz
 * @company 9zhitx.com
 */

public class ConversationAdapter extends CommonExpandWrapperAdapter
        implements SwipeableItemAdapter<ConversationAdapter.SwipeLeftHolder> {
    private float mScreenWith = 0;
    private int mSwipDistance = 0;
    private EventListener mEventListener;
    private static final int TYPE = 101;

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    /**
     * item的点击事件
     */
    private View.OnClickListener mSwipeableContainerOnClickListener;
    /**
     * 可滑出按钮的点击事件
     */
    private View.OnClickListener mUnderSwipeableBtnOnClickListener;

    public ConversationAdapter(Context context) {
        super(context);

        mScreenWith = IDisplayUtil.getScreenWidth(context);
        mSwipeableContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };
        mUnderSwipeableBtnOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnderSwipeableViewButtonClick(v);
            }
        };
        setHasStableIds(true);
    }

    /**
     * 回调响应点击item事件
     *
     * @param v
     */
    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    /**
     * 回调响应点滑出button的事件
     *
     * @param v
     */
    private void onUnderSwipeableViewButtonClick(View v) {
        if (mEventListener != null) {
            mEventListener.onUnderSwipeableViewButtonClicked(v, RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (isHeader(viewType) || isFooter(viewType)) {
            viewHolder = super.onCreateViewHolder(parent, viewType);
        } else {
            viewHolder = new SwipeLeftHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_chat_item, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SwipeLeftHolder) {
            ((SwipeLeftHolder) viewHolder).bindData((DevicesBind) getItem(position), position);
        }
    }


    @Override
    public int getItemViewType(int position) {
        int type;
        BaseBean bean = getItem(position);
        if (bean instanceof FixedViewInfo) {
            type = ((FixedViewInfo) bean).viewType;
        } else if (bean instanceof DevicesBind) {
            type = TYPE;
        } else {
            type = super.getItemViewType(position);
        }
        return type;
    }

    protected int getDataCount() {
        int groupCount = getGroupCount();
        if (groupCount > 0) {
            return getItemCount() - getHeaderCount() - getFooterCount();
        }
        return getChildCount();
    }

    @Override
    public int onGetSwipeReactionType(SwipeLeftHolder holder, int position, int x, int y) {
        if (com.xiaolanba.passenger.library.advrecyclerview.utils.ViewUtils.hitTest(holder.getSwipeableContainerView(), x, y)) {
            return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
        } else {
            return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H;
        }
    }

    @Override
    public void onSetSwipeBackground(SwipeLeftHolder holder, int position, int type) {
        int bgRes = 0;
        switch (type) {
            case SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_left;
                break;
            case SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_right;
                break;
        }
        holder.itemView.setBackgroundResource(bgRes);
    }

    @Override
    public com.xiaolanba.passenger.library.advrecyclerview.swipeable.action.SwipeResultAction onSwipeItem(SwipeLeftHolder holder, int position, int result) {
        switch (result) {
            //滑出来了
            case com.xiaolanba.passenger.library.advrecyclerview.swipeable.SwipeableItemConstants.RESULT_SWIPED_LEFT:
                return new SwipeLeftResultAction(this, position);
            //滑回去了
            case com.xiaolanba.passenger.library.advrecyclerview.swipeable.SwipeableItemConstants.RESULT_SWIPED_RIGHT:
            case com.xiaolanba.passenger.library.advrecyclerview.swipeable.SwipeableItemConstants.RESULT_CANCELED:
            default:
                if (position != RecyclerView.NO_POSITION) {
                    return new UnpinResultAction(this, position);
                } else {
                    return null;
                }
        }
    }

    /**
     * 滑出来了响应的action
     */
    private static class SwipeLeftResultAction extends com.xiaolanba.passenger.library.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection {
        private ConversationAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(ConversationAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            BaseBean baseBean = mAdapter.getItem(mPosition);
            if (baseBean instanceof DevicesBind) {
                DevicesBind item = (DevicesBind) baseBean;
                if (!item.isPinned()) {
                    item.setPinned(true);
                    mAdapter.notifyItemChanged(mPosition);
                    mSetPinned = true;
                }
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();
            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemPinned(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            mAdapter = null;
        }
    }

    /**
     * 滑回去了响应的action
     */
    private static class UnpinResultAction extends com.xiaolanba.passenger.library.advrecyclerview.swipeable.action.SwipeResultActionDefault {
        private ConversationAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(ConversationAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            BaseBean baseBean = mAdapter.getItem(mPosition);
            if (baseBean instanceof DevicesBind) {
                DevicesBind item = (DevicesBind) baseBean;
                if (item.isPinned()) {
                    item.setPinned(false);
                    mAdapter.notifyItemChanged(mPosition);
                }
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            mAdapter = null;
        }
    }


    public void removeByAddress(String address) {
        int count = getItemCount();
        for (int i=0;i<count;i++){
            if (getItem(i) instanceof DevicesBind){
                DevicesBind bind = (DevicesBind) getItem(i);
                if (address.equals(bind.address)){
                    removeItem(i);
                }
            }
        }
    }

    /**
     * 会话列表的单个holder
     */
    public class SwipeLeftHolder extends AbstractSwipeableItemViewHolder {
        public LinearLayout mContainer;
        public Button mUnbind;
        public Button mRename;
        public TextView mNameTxt;

        public SwipeLeftHolder(View v) {
            super(v);
            mContainer = (LinearLayout) v.findViewById(R.id.container);
            mUnbind = (Button) v.findViewById(R.id.unbind_txt);
            mRename = (Button) v.findViewById(R.id.rename_txt);
            mNameTxt = (TextView) v.findViewById(R.id.name_txt);
        }

        public void bindData(DevicesBind item, int position) {
            mNameTxt.setText(item.name);
            mContainer.setOnClickListener(mSwipeableContainerOnClickListener);
            mRename.setOnClickListener(mUnderSwipeableBtnOnClickListener);
            mUnbind.setOnClickListener(mUnderSwipeableBtnOnClickListener);
            mUnbind.setVisibility(View.VISIBLE);
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mUnbind.measure(w, h);
            mRename.measure(w, h);
            int unbindWidth = mUnbind.getMeasuredWidth();
            int deleteBtnWidth = mRename.getMeasuredWidth();
            mSwipDistance = unbindWidth + deleteBtnWidth;
            float value = -(mSwipDistance / mScreenWith);
            setMaxLeftSwipeAmount(value);
            setMaxRightSwipeAmount(0);
            setSwipeItemHorizontalSlideAmount(item.isPinned() ? -0.5f : 0);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }

    }
}
