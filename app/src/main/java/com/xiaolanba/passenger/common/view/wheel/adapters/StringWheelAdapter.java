package com.xiaolanba.passenger.common.view.wheel.adapters;

import android.content.Context;

import com.xlb.elect.cigar.R;


/**
 * 年月日，星座等的选择器
 * @author xutingz
 * @company xiaolanba.com
 */

public class StringWheelAdapter extends ArrayWheelAdapter {

    /**
     * 构造方法传入一个数组
     * @param context
     * @param items
     */
    public StringWheelAdapter(Context context, String[] items) {
        super(context,items);
        setItemResource(R.layout.wheel_text_item);
        setItemTextResource(R.id.text);
    }

}
