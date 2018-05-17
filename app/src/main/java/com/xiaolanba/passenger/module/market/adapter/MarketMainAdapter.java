package com.xiaolanba.passenger.module.market.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaolanba.passenger.common.base.BaseRecyclerViewHolder;
import com.xiaolanba.passenger.common.bean.BaseBean;
import com.xiaolanba.passenger.common.bean.LeaseItem;
import com.xiaolanba.passenger.common.bean.SaleItem;
import com.xiaolanba.passenger.common.bean.ShopsBean;
import com.xiaolanba.passenger.common.bean.ShowItem;
import com.xiaolanba.passenger.module.common.adapter.CommonExpandWrapperAdapter;
import com.xiaolanba.passenger.module.market.activity.SubMarketActivity;
import com.xiaolanba.passenger.module.market.holder.ShopsHolder;
import com.xlb.elect.cigar.R;

import java.util.List;

/**
 * @author Angus
 * @company 9zhitx.com
 */
public class MarketMainAdapter extends CommonExpandWrapperAdapter {
    public static final int GROUP_RENTAL = 0;    //租赁区    (第一组)
    public static final int GROUP_LAMPBLACK = 1; //烟油商品区 (第二组)
    public static final int GROUP_BUYER_SHOW = 2;  //买家秀  (第三组)

    /** 商品 */
    public static final int VIEW_TYPE_SHOPS = 100;
    /** 标题导航 */
    public static final int VIEW_TYPE_GROUP = 101;

    public MarketMainAdapter(Context context) {
        super(context);
        addGroup(new GroupNavigation(0,"租赁区",true,false));//(第一组)
        addGroup(new GroupNavigation(1,"烟油商城",true,false));//(第二组)
        addGroup(new GroupNavigation(2,"买家秀秀",true,false));//(第三组)
    }

    /**
     * 设置租赁区数据列表
     */
    public void setRentalList(List<BaseBean> list) {
        if (list != null && !list.isEmpty()) {
            setGroupShow(GROUP_RENTAL, true,true);
        } else {
            setGroupShow(GROUP_RENTAL, false,false);
        }
        clearGroupChild(GROUP_RENTAL);
        addGroupChild(GROUP_RENTAL,list);
    }

    /**
     * 设置烟油商城数据
     */
    public void setLampList(List<BaseBean> list) {
        if (list != null && !list.isEmpty()) {
            setGroupShow(GROUP_LAMPBLACK, true,true);
        } else {
            setGroupShow(GROUP_LAMPBLACK, false,false);
        }
        clearGroupChild(GROUP_LAMPBLACK);
        addGroupChild(GROUP_LAMPBLACK,list);
    }

    /**
     * 设置买家秀数据
     */
    public void setBuyerShowList(List<BaseBean> list) {
        if (list != null && !list.isEmpty()) {
            setGroupShow(GROUP_BUYER_SHOW, true,false);
        } else {
            setGroupShow(GROUP_BUYER_SHOW, false,false);
        }
        clearGroupChild(GROUP_BUYER_SHOW);
        addGroupChild(GROUP_BUYER_SHOW,list);
    }


    public void setGroupShow(int group, boolean isShow,boolean showMore){
        BaseBean bean =  getGroup(group);
        if (bean != null && bean instanceof GroupNavigation) {
            GroupNavigation titleNav = (GroupNavigation)bean;
            titleNav.isVisibility = isShow;
            titleNav.showMore = showMore;
            updateGroup(group, titleNav);
        }
    }

    /**
     * 给对应的分组添加数据在后面
     * @param list
     */
    public void addList(int group,List<BaseBean> list) {
        addGroupChild(group, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_SHOPS:
                ShopsHolder postsReplyViewHolder = new ShopsHolder(mContext,mLayoutInflater,parent);
                viewHolder = postsReplyViewHolder;
                break;
            case VIEW_TYPE_GROUP:
                viewHolder = new GroupNavHolder(mContext,mLayoutInflater,parent);
                break;
            default:
                viewHolder = super.onCreateViewHolder(parent, viewType);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(getItem(position));
    }

    @Override
    public int getItemViewType(BaseBean bean) {
        int type = 0;
        if (bean instanceof SaleItem || bean instanceof LeaseItem || bean instanceof ShowItem){
            type = VIEW_TYPE_SHOPS;
        } else if (bean instanceof GroupNavigation){
            type = VIEW_TYPE_GROUP;
        } else {
            type = super.getItemViewType(bean);
        }
        return type;
    }

    /**
     * 分组的Holder
     */
    public class GroupNavHolder extends BaseRecyclerViewHolder<GroupNavigation> {

        private TextView mTitleTxt;
        private TextView mMoreTxt;

        public GroupNavHolder(Context mContext, LayoutInflater mInflater, ViewGroup viewGroup) {
            super(R.layout.market_group_nav, mContext, mInflater, viewGroup);
        }

        @Override
        public void findView() {
            mTitleTxt = (TextView) itemView.findViewById(R.id.title_txt);
            mMoreTxt = (TextView) itemView.findViewById(R.id.more_txt);
        }

        @Override
        public void bindData(final GroupNavigation bean, int position) {
            setVisibility(bean.isVisibility);
            mTitleTxt.setText(bean.title);
            mMoreTxt.setVisibility(bean.showMore?View.VISIBLE:View.GONE);
            mMoreTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.groupNum != 2) {
                        SubMarketActivity.startActivity(mContext, bean.groupNum);
                    }
                }
            });
        }

        public void setVisibility(boolean isVisibility){
            itemView.getLayoutParams().height = isVisibility ? ViewGroup.LayoutParams.WRAP_CONTENT : 0;
            itemView.requestLayout();
        }
    }

    /**
     * 分组对象
     */
    private class GroupNavigation extends  BaseBean{
        public String title;
        public boolean showMore;
        public boolean isVisibility;
        public int groupNum;
        public GroupNavigation(int group,String title, boolean showMore, boolean isVisibility) {
            this.title = title;
            this.showMore = showMore;
            this.isVisibility = isVisibility;
            groupNum = group;
        }
    }

}
