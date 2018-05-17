package com.xiaolanba.passenger.module.devices.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.framework.common.base.IBaseAdapter;
import com.xiaolanba.passenger.common.bean.DevicesGridItem;
import com.xlb.elect.cigar.R;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/05/03
 */

public class DevicesGridAdapter extends IBaseAdapter<DevicesGridItem> {
    public DevicesGridAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null){
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.devices_grid_item,null);
            holder.mImage = (ImageButton) convertView.findViewById(R.id.image);
            holder.mNumTxt = (TextView) convertView.findViewById(R.id.num_txt);
            holder.mTitleTxt = (TextView) convertView.findViewById(R.id.title_txt);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        DevicesGridItem item = mList.get(position);
        if (item.picture == 0){
            holder.mImage.setVisibility(View.GONE);
            holder.mNumTxt.setVisibility(View.VISIBLE);
            holder.mNumTxt.setText(item.numberData);
        } else {
            holder.mImage.setVisibility(View.VISIBLE);
            holder.mNumTxt.setVisibility(View.GONE);
            holder.mImage.setImageResource(item.picture);
        }
//        if (position<3){
//            holder.mTitleTxt.setText(item.desc+"("+(item.stateOk?"开":"关")+")");
//        } else {
            holder.mTitleTxt.setText(item.desc);
//        }

        return convertView;
    }

    private class Holder{
        private ImageButton mImage;
        private TextView mNumTxt,mTitleTxt;
    }
}
