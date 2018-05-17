package com.xiaolanba.passenger.module.devices.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
//import android.graphics.Camera;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.common.utils.IKeyboardUtils;
import com.framework.common.utils.ILog;
import com.google.zxing.Result;
import com.google.zxing.client.android.AutoScannerView;
import com.google.zxing.client.android.BaseCaptureActivity;
import com.xiaolanba.passenger.bluetooth.LeDevice;
import com.xiaolanba.passenger.common.bean.DevicesBind;
import com.xiaolanba.passenger.common.view.MyToast;
import com.xiaolanba.passenger.rxandroid.rxbus.RxAction;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBus;
import com.xiaolanba.passenger.rxandroid.rxbus.RxBusObserver;
import com.xiaolanba.passenger.rxandroid.rxbus.RxConstants;
import com.xlb.elect.cigar.R;

import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 二维码扫描界面
 */
public class QrCodeCaptureActivity extends BaseCaptureActivity implements View.OnClickListener {

    private static final String TAG = QrCodeCaptureActivity.class.getSimpleName();
    private static final String REQUEST_CODE = "request_code";
    private int requestCode;
    private ImageButton mLeftBtn;
    private SurfaceView surfaceView;
    private AutoScannerView autoScannerView;
    private TextView mLightTxt, mInputTxt;
    private Disposable mDisposable;
    private Context context;
    private String blueName;
    private CameraManager manager;// 声明CameraManager对象
    private Camera m_Camera = null;// 声明Camera对象
    private boolean isLight;
    public static void startActivity(Context context,String name) {
        Intent intent = new Intent(context, QrCodeCaptureActivity.class);
        intent.putExtra("blueName",name);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity context, int requestCode,String name) {
        Intent intent = new Intent(context, QrCodeCaptureActivity.class);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra("blueName",name);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_capture);
        context = this;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(REQUEST_CODE)) {
            requestCode = intent.getIntExtra(REQUEST_CODE, 0);
            blueName = intent.getStringExtra("blueName");
        }
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        autoScannerView = (AutoScannerView) findViewById(R.id.autoscanner_view);
        mLeftBtn = (ImageButton) findViewById(R.id.left_img_btn);
        mLeftBtn.setOnClickListener(this);
        mLightTxt = (TextView) findViewById(R.id.light_txt);
        mInputTxt = (TextView) findViewById(R.id.input_txt);
        mInputTxt.setOnClickListener(this);
        mLightTxt.setOnClickListener(this);

        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        registRxBus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoScannerView.setCameraManager(cameraManager);
    }

    /**
     * 通过RxBus注册监听事件
     */
    private void registRxBus() {
        List<Integer> list = Arrays.asList(RxConstants.ACTION_QRCODE_RESULT);
        RxBus.getDefault().toObservableWithCodes(list).subscribeWith(new RxBusObserver<RxAction>() {
            @Override
            public void onRxSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onRxNext(RxAction value) {
                switch (value.code) {
                    case RxConstants.ACTION_QRCODE_RESULT://
                        if (value.data != null && value.data instanceof LeDevice) {
                            finish();
                        }
                        break;
                }
            }
        });
    }

    public void showToast(String title, int duration) {
        MyToast.makeText(context, title, duration).show();
    }


    @Override
    public SurfaceView getSurfaceView() {
        return (surfaceView == null) ? (SurfaceView) findViewById(R.id.preview_view) : surfaceView;
    }

    @Override
    public void dealDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        Log.i(TAG, "dealDecode ~~~~~ " + rawResult.getText() + " " + barcode + " " + scaleFactor);
        playBeepSoundAndVibrate(true, true);
        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_LONG).show();
        if (requestCode != 0) {
            Intent intent = new Intent();
            intent.putExtra("codeString", rawResult.getText());
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
//        对此次扫描结果不满意可以调用
//        reScan();
    }

    @Override
    protected void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_img_btn:
                finish();
                break;
            case R.id.light_txt:
//                if (isLight){
//                    lightSwitch(false);
//                } else {
//                    lightSwitch(true);
//                }
                break;
            case R.id.input_txt:
                QrCodeInputActivity.startActivity(context,blueName);
                break;
        }
    }

    /**
     * 手电筒控制方法
     *
     * @param lightStatus
     * @return
     */
    private void lightSwitch(final boolean lightStatus) {
        if (lightStatus) { //
            isLight = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                final PackageManager pm = getPackageManager();
                final FeatureInfo[] features = pm.getSystemAvailableFeatures();
                for (final FeatureInfo f : features) {
                    if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // 判断设备是否支持闪光灯
                        if (null == m_Camera) {
                            m_Camera = Camera.open();
                        }
                        final Camera.Parameters parameters = m_Camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        m_Camera.setParameters(parameters);
                        m_Camera.startPreview();
                    }
                }
            }

        } else { //
            isLight = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (m_Camera != null) {
                    m_Camera.stopPreview();
                    m_Camera.release();
                    m_Camera = null;
                }
            }
        }
    }


    /**
     * 判断Android系统版本是否 >= M(API23)
     */
    private boolean isM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
