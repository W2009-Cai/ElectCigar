//package com.xiaolanba.passenger.module.common.activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.framework.common.utils.INetworkUtils;
//import com.framework.common.utils.IStringUtil;
//import com.xlb.elect.cigar.R;
//import com.xiaolanba.passenger.common.base.BaseActivity;
//import com.xiaolanba.passenger.common.bean.UpdateInfo;
//
///**
// * 升级提示弹框，所有选择使用activity来实现,如果用Dialog来实现必须绑定Activity
// *
// * @author xutingz
// * @e-mail xutz@xiaolanba.com
// */
//public class UpgradeActivity extends BaseActivity implements OnClickListener {
//
//    private UpdateInfo info;
//
//    private Button mSure;
//
//    public static void startActivity(Context context, UpdateInfo info) {
//        Intent intent = new Intent(context, UpgradeActivity.class);
//        if (context instanceof Activity) { //去掉自带动画
//            ((Activity)context).overridePendingTransition(0, 0);
//        } else {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//        intent.putExtra("info", info);
//        context.startActivity(intent);
//    }
//
//    private void finishWidthTran() {
//        finish();
//        overridePendingTransition(0, R.anim.common_scale_large_to_small);
//    }
//
//    @Override
//    public void onBackPressed() {
//        finishWidthTran();
////		super.onBackPressed();
//    }
//
//
//    public void setContentView() {
//        info = (UpdateInfo) getIntent().getSerializableExtra("info");
//        if (info == null) {
//            finishWidthTran();
//        }
//        setContentView(R.layout.dialog_upgrade_layout);
//    }
//
//    public void findView() {
//        TextView titleText = (TextView) findViewById(R.id.dialog_title);
//        TextView messageText = (TextView) findViewById(R.id.dialog_message);
//        titleText.setText(IStringUtil.toString(info.title));
//        messageText.setText(IStringUtil.toString(info.description));
//        if (info.isForce) { // 强制升级
//            findViewById(R.id.cancel_line).setVisibility(View.GONE);
//            findViewById(R.id.cancel_btn).setVisibility(View.GONE);
//        } else {
//            findViewById(R.id.cancel_line).setVisibility(View.VISIBLE);
//            findViewById(R.id.cancel_btn).setVisibility(View.VISIBLE);
//        }
//        findViewById(R.id.cancel_btn).setOnClickListener(this);
//        mSure = (Button) findViewById(R.id.ok_btn);
//        mSure.setOnClickListener(this);
//    }
//
//    public void initData() {
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.cancel_btn: {
//                finishWidthTran();
//            }
//            break;
//            case R.id.ok_btn: {
//                if (!INetworkUtils.getInstance().isNetworkAvailable()) {
//                    return;
//                }
//                if (info != null && info.isForce) { //强制升级
//
//                } else {
//                    finishWidthTran();
//                }
//            }
//            break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) { //屏蔽系统返回键
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return false;
//        }
//        return super.onKeyUp(keyCode, event);
//    }
//
//}
//
