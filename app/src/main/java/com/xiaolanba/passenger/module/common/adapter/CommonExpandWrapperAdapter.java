package com.xiaolanba.passenger.module.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.framework.common.base.IBaseActivity;
import com.framework.common.base.IBaseFragmentActivity;
import com.xiaolanba.passenger.common.bean.BaseBean;

import java.util.List;


/**
 * 扩展的可包含头部header和底部footer的Adapter
 * @author Angus
 * @company 9zhitx.com
 */
public abstract class CommonExpandWrapperAdapter extends CommonExpandableAdapter<BaseBean> {

    // Defines available view type integers for headers and footers.
    public static final int BASE_HEADER_VIEW_TYPE = -1 << 10;
    public static final int BASE_FOOTER_VIEW_TYPE = -1 << 11;

    public class FixedViewInfo extends BaseBean{
        public View view;
        public int viewType;
    }

    public CommonExpandWrapperAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeader(viewType)) {
            int whichHeader = Math.abs(viewType - BASE_HEADER_VIEW_TYPE);
            BaseBean bean = getHeader(whichHeader);
            if (bean != null && bean instanceof FixedViewInfo) {
                View headerView = ((FixedViewInfo)bean).view;
                return createHeaderFooterViewHolder(headerView);
            }
        }
        else if (isFooter(viewType)) {
            int whichFooter = Math.abs(viewType - BASE_FOOTER_VIEW_TYPE);
            BaseBean bean = getFooter(whichFooter);
            if (bean != null && bean instanceof FixedViewInfo) {
                View footerView = ((FixedViewInfo)bean).view;
                return createHeaderFooterViewHolder(footerView);
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        BaseBean bean = getItem(position);
        if (bean != null && bean instanceof FixedViewInfo) { //头部或者尾部
            type = ((FixedViewInfo)bean).viewType;
        } else {
            type = super.getItemViewType(position);
        }
        return type;
    }

    /**
     * Adds a header view
     * @param view
     */
    public void addHeaderView(View view) {
        if (null == view) {
            return;
        }

        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_HEADER_VIEW_TYPE + getHeaderCount();
        addHeader(info);
    }

    public void addHeaderView(int headerPosition, View view) {
        if (null == view) {
            return;
        }

        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_HEADER_VIEW_TYPE + getHeaderCount();
        addHeader(headerPosition, info);
    }

    /**
     * Adds a footer view
     * @param view
     */
    public void addFooterView(View view) {
        if (null == view) {
            return;
        }
        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_FOOTER_VIEW_TYPE + getFooterCount();
        addFooter(info);
    }

    protected boolean isHeader(int viewType) {
        return viewType >= BASE_HEADER_VIEW_TYPE
                && viewType < (BASE_HEADER_VIEW_TYPE + getHeaderCount());
    }

    protected boolean isFooter(int viewType) {
        return viewType >= BASE_FOOTER_VIEW_TYPE
                && viewType < (BASE_FOOTER_VIEW_TYPE + getFooterCount());
    }

    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        return new RecyclerView.ViewHolder(view){};
    }

    public void showToast(String title){
        if (mContext != null ){
            if (mContext instanceof IBaseActivity){
                ((IBaseActivity)mContext).showToast(title);
            }
            else if (mContext instanceof IBaseFragmentActivity){
                ((IBaseFragmentActivity)mContext).showToast(title);
            }
        }
    }

    public void setChild(List<BaseBean> list) {
        clearChild();
        addChild(list);
    }


}
