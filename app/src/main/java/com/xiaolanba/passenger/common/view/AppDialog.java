package com.xiaolanba.passenger.common.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.framework.common.base.IBaseDialog;
import com.framework.common.utils.IDisplayUtil;
import com.framework.common.utils.IStringUtil;
import com.xlb.elect.cigar.R;

import java.util.List;

/**
 * app Dialog
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class AppDialog extends IBaseDialog {

    private int mContentGravity = 0; //messageTextĬ��Ϊ���ж��룬 ������Ǿ����봫����Ӧ�Ķ��뷽ʽ

    public AppDialog(Context context) {
        super(context, R.style.BaseDialog);
        setCanceledOnTouchOutside(false);
    }

    public AppDialog(Context context, int style) {
        super(context, style);
        setCanceledOnTouchOutside(true);
    }

    public AppDialog(Activity context, View view) {
        super(context, R.style.BaseDialog);

        setCanceledOnTouchOutside(false);

        setContentView(view);
    }

    public AppDialog(Activity context, View view, boolean touchOut) {
        super(context, R.style.BaseDialog);

        setCanceledOnTouchOutside(touchOut);

        setContentView(view);
    }


    /**
     * Confirm Dialog
     *
     * @param context
     * @return
     */
    public void createConfirmDialog(Context context, String title,
                                    String message, final OnConfirmButtonDialogListener confirmBtnListener) {

        createConfirmDialog(context, title, message, context.getString(R.string.cancel), context.getString(R.string.ok), confirmBtnListener);
    }

    /**
     * Confirm Dialog
     *
     * @param context
     * @return
     */
    public void createConfirmDialog(Context context, String title,
                                    String message, String leftButtonTxt, String rightButtonTxt, final OnConfirmButtonDialogListener confirmBtnListener) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_confim_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.dialog_title);
        if (IStringUtil.isEmpty(title)) {
            titleText.setVisibility(View.GONE);
            view.findViewById(R.id.title_line).setVisibility(View.GONE);
        } else {
            titleText.setText(title);
        }

        TextView messageText = (TextView) view.findViewById(R.id.dialog_message);
        messageText.setText(message);
        if (mContentGravity != 0) {
            messageText.setGravity(mContentGravity);
        }
        Button leftButton = (Button) view.findViewById(R.id.left_btn);
        Button rightButton = (Button) view.findViewById(R.id.right_btn);
        leftButton.setText(leftButtonTxt);
        rightButton.setText(rightButtonTxt);

        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (confirmBtnListener != null) {
                    confirmBtnListener.onLeftButtonClick();
                }
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (confirmBtnListener != null) {
                    confirmBtnListener.onRightButtonClick();
                }
            }
        });

        setContentView(view);
    }


    /**
     * Confirm Dialog
     *
     * @param context
     * @return
     */
    public void createSignBtnDialog(Context context, String title,
                                    String message, String btnText, final OnSignBtnListener onSignListener) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_confim_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.dialog_title);
        if (IStringUtil.isEmpty(title)) {
            titleText.setVisibility(View.GONE);
            view.findViewById(R.id.title_line).setVisibility(View.GONE);
        } else {
            titleText.setText(title);
        }

        TextView messageText = (TextView) view.findViewById(R.id.dialog_message);
        messageText.setText(message);
        if (mContentGravity != 0) {
            messageText.setGravity(mContentGravity);
        }

        view.findViewById(R.id.left_btn).setVisibility(View.GONE);
        Button rightButton = (Button) view.findViewById(R.id.right_btn);
        rightButton.setText(IStringUtil.toString(btnText));

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onSignListener != null) {
                    onSignListener.onClick();
                }
            }
        });

        setContentView(view);
    }


    public void createItemDialog(Context context, String title, final List<String> itemList, final OnDialogItemClickListener listener) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_items_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.dialog_title);
        titleText.setText(title);

        if (null != itemList) {
            int size = itemList.size();

            for (int i = 0; i < size; i++) {
                final int index = i;
                Button btn = (Button) inflater.inflate(R.layout.dialog_item_layout, null);
                btn.setText(itemList.get(i));
                view.addView(btn, new LayoutParams(LayoutParams.FILL_PARENT, IDisplayUtil.dip2px(context, 45)));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        listener.onClick(itemList.get(index));
                    }
                });
            }

        }
        view.setMinimumWidth(getWidthFromPercent(context, 0.8f));

        setContentView(view);

    }

    @Override
    public void show() {
        setFullWidth();
        super.show();
    }

    public void setFullWidth() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = IDisplayUtil.getScreenWidth(mContext);
        window.setAttributes(lp);
    }

    public int getWidthFromPercent(Context context, float percent) {
        return (int) (percent > 1 ? IDisplayUtil.getScreenWidth(context) : IDisplayUtil.getScreenHeight(context) * percent);
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface OnCallBackListener {
        void callBack();
    }

    public interface OnSignBtnListener {
        void onClick();
    }

    public interface OnConfirmButtonDialogListener {
        void onLeftButtonClick();

        void onRightButtonClick();
    }

    public interface OnDialogItemClickListener {
        void onClick(String iten);
    }

    @Override
    public void setContentView() {

    }

    @Override
    public void findView() {

    }

    @Override
    public void initData() {

    }

    public void setContentGravity(int mContentGravity) {
        this.mContentGravity = mContentGravity;
    }
}
