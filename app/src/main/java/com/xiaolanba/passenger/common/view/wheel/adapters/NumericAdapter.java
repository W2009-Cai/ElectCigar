package com.xiaolanba.passenger.common.view.wheel.adapters;

import android.content.Context;

import com.xlb.elect.cigar.R;


/**
 * @author xutingz
 * @company xiaolanba.com
 */

public class NumericAdapter extends NumericWheelAdapter {

    public NumericAdapter(Context context, int minValue, int maxValue) {
        super(context, minValue, maxValue);
        setItemResource(R.layout.wheel_text_item);
        setItemTextResource(R.id.text);
    }
}
