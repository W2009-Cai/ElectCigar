package com.xiaolanba.passenger.module.person.adapter.holder;

import android.content.Context;
import android.view.View;

import com.xiaolanba.passenger.common.base.BaseRecyclerViewHolder;

/**
 *
 * @Author hai
 * @E-mail shihh@xianlanba.com
 * @Date 2018/04/02
 */
public class MsgViewHolder extends BaseRecyclerViewHolder<Object> {
    public MsgViewHolder(Context context, View inflate) {
        super(context,inflate);
    }

    @Override
    public void findView() {
        // TODO: 2018/4/2
        //        itemView.findViewById(R.id.)
    }

    @Override
    public void bindData(final Object bean, int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转条目详情
            }
        });
    }
}
