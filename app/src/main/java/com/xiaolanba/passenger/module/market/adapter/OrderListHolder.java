package com.xiaolanba.passenger.module.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaolanba.passenger.common.base.BaseRecyclerViewHolder;
import com.xlb.elect.cigar.R;

/**
 * 订单列表的item项
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/03/01
 */

public class OrderListHolder extends BaseRecyclerViewHolder<Object> {
    private TextView mTimeTxt,mStateTxt,mUpAddress,mDownAddress;
    public OrderListHolder(Context context, LayoutInflater inflater, ViewGroup viewGroup) {
        super(R.layout.order_list_item, context, inflater, viewGroup);
    }

    @Override
    public void findView() {
        mTimeTxt = (TextView) itemView.findViewById(R.id.time_txt);
        mStateTxt = (TextView) itemView.findViewById(R.id.state_txt);
        mUpAddress = (TextView) itemView.findViewById(R.id.up_address);
        mDownAddress = (TextView) itemView.findViewById(R.id.down_address);
    }

    @Override
    public void bindData(Object bean, int position) {
        registerItemClick(bean,position);
//        mTimeTxt.setText(bean.orderTime);
//        mStateTxt.setText(switchState(bean.orderState));
//        mUpAddress.setText(bean.orderUpAddress);
//        mDownAddress.setText(bean.orderDownAddress);
    }

    public String switchState(int state){ // 0:匹配中 1:已派车 2:行程中 3:故障中 4:已取消 5:待支付 6:已完成
        switch (state){
            case 0:
            case 1:
                return "等待乘车";
            case 2:
            case 3:
                return "行程中";
            case 4:
                return "已取消";
            case 5:
                return "待支付";
            case 6:
                return "已完成";
        }
        return "等待乘车";
    }

    @Override
    public void onItemClick(Object bean, int position) {
//        showToast("点击了第"+position);
//        CompleteOrderActivity.startActivity(mContext,bean);
    }
}
