package com.xiaolanba.passenger.common.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

/**
 * @author xutingz
 * @company xiaolanba.com
 */

public class ColorTextUtil {

    /**
     * 从开头开始变颜色，
     * @param num  变颜色处对应的数字
     * @param afterNum  未变色部分的文字
     * @param textView  TextView
     * @param colorStr  需要变成的颜色(# 开头的16进制字符串)，如果传入null表示不需要变色
     * @param biggerThan 需要变大或变小的比例
     */
    public static void setColorTextCeil(float num,String afterNum,TextView textView,String colorStr,float biggerThan){
        String numberStr = String.valueOf(num);
        setColorTextCeil(numberStr,afterNum,textView,colorStr,biggerThan);
    }

    public static void setColorTextCeil(String numberStr,String afterNum,TextView textView,String colorStr,float biggerThan){
        String targetNum = numberStr + afterNum;
        SpannableString typeStr = new SpannableString(targetNum);
        if (colorStr != null){
            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor(colorStr));
            typeStr.setSpan(redSpan, 0, numberStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (biggerThan != 1f) {
            typeStr.setSpan(new RelativeSizeSpan(biggerThan), 0, numberStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(typeStr);
    }

}
