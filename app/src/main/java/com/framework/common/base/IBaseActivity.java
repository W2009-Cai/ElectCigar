package com.framework.common.base;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;

import com.framework.common.utils.ILog;
import com.framework.common.utils.IStringUtil;
import com.framework.common.view.IProgressDialog;
import com.xiaolanba.passenger.common.view.MyToast;

/**
 * Activity基类
 *
 * @author xutingz
 */
public abstract class IBaseActivity extends Activity implements OnClickListener, IActivity {

    protected String TAG = getClass().getSimpleName();

    protected LayoutInflater mInflater;

    protected Bundle mSavedInstanceState;

    protected IProgressDialog mProgressDialog;

    protected IBaseActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ILog.i(TAG, "TAG:" + TAG);

        context = this;
        mInflater = LayoutInflater.from(this);
        mSavedInstanceState = savedInstanceState;
        setContentView();
        findView();
        initData();

    }

    public boolean isDialogShowing() {
        if (mProgressDialog != null) {
            return mProgressDialog.isShowing();
        }
        return false;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new IProgressDialog(this);
        }

        mProgressDialog.show("");
    }

    /**
     * @param content
     */
    public void showProgressDialog(String content) {
        if (mProgressDialog == null) {
            mProgressDialog = new IProgressDialog(this);
        }
        mProgressDialog.show(content);

    }

    public void dismissProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }


    /**
     * 显示提示
     *
     * @param resId
     */
    @Override
    public void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    @Override
    public void showToast(@StringRes int resId, int duration) {
        showToast(getString(resId), duration);
    }

    @Override
    public void showToast(String title) {
        if (IStringUtil.isEmpty(title)) {
            return;
        }
        showToast(title, 1500);
    }

    /**
     * 显示提示
     *
     * @param title
     */
    @Override
    public void showToast(String title, int duration) {
        if (IStringUtil.isEmpty(title)) {
            return;
        }
        MyToast.makeText(context, title, duration).show();
    }

    /**
     * 刷新Activity
     */
    public void refreshActivity() {
        setContentView();
        findView();
        initData();
    }

    /**
     * 模拟返回键（解决finish时 有键盘先隐藏键盘的问题）
     */
    public void onBack() {
        new Thread() {
            public void run() {
                try {
                    // 不能再主线程中  否则会报错
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {
                }
            }
        }.start();
    }


    public abstract void setContentView();

    public abstract void findView();

    public abstract void initData();

    public void setActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }
}
