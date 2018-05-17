package com.xiaolanba.passenger.module.main.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.xiaolanba.passenger.module.person.activity.LoginActivity;
import com.xlb.elect.cigar.R;
import com.xiaolanba.passenger.common.base.BaseActivity;
import com.xiaolanba.passenger.logic.control.LBController;

/**
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/19
 */

public class LanucherActivity extends BaseActivity {
    private ImageView mSplashImg;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_lanucher);
    }

    @Override
    public void findView() {
        mSplashImg = (ImageView) findViewById(R.id.splash_img);

    }

    @Override
    public void initData() {
        mSplashImg.setImageResource(R.drawable.splash);
        LBController.MainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context,LoginActivity.class);
                context.startActivity(intent);
                finish();
            }
        }, 1500);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
