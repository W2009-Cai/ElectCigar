package com.xiaolanba.passenger.common.interfaces;

import android.view.View;

/**
 * @author xutingz
 * @company 9zhitx.com
 */

public interface EventListener {
    void onItemPinned(int position);

    void onItemViewClicked(View v);

    void onUnderSwipeableViewButtonClicked(View v, View layout);
}
