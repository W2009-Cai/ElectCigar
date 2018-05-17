package com.framework.common.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * 键盘相关工具类
 *
 * @author xutingz
 */
public class IKeyboardUtils {

    /**
     * 关闭键盘
     *
     * @param context
     */
    public static void closeKeybord(final Context context) {
        InputMethodManager imm = ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE));
        final View currentFocusView = ((Activity) context).getCurrentFocus();
        if (currentFocusView != null) {
            final IBinder windowToken = currentFocusView.getWindowToken();
            if (imm != null && windowToken != null) {
                imm.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    /**
     * 打开键盘
     *
     * @param context
     * @param view
     */
    public static void openKeybord(final Context context, final View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public static boolean isActive(final Activity activity) {
        return activity.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
    }


        /**
         * 根据传入控件的坐标和用户的焦点坐标，判断是否隐藏键盘，如果点击的位置在控件内，则不隐藏键盘
         *
         * @param event 焦点位置
         */
        public static void shouldHideKeyboard(MotionEvent event, Activity activity) {
            try {
                View view = activity.getCurrentFocus();
                if (view != null && view instanceof EditText
                        /*|| view instanceof VoiceEditText
                        || view instanceof VoiceDescriptionEditText*/) {
                    int[] location = {0, 0};
                    view.getLocationInWindow(location);
                    int left = location[0], top = location[1], right = left + view.getWidth(), bootom = top + view.getHeight();
                    // 判断焦点位置坐标是否在空间内，如果位置在控件外，则隐藏键盘
                    if (event.getRawX() < left || event.getRawX() > right || event.getRawY() < top || event.getRawY() > bootom) {
                        closeKeybord(activity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
