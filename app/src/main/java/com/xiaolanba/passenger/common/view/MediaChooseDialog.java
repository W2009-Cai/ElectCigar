package com.xiaolanba.passenger.common.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.framework.common.utils.ICommonUtil;
import com.framework.common.utils.IDisplayUtil;
import com.framework.common.utils.IFileUtil;
import com.framework.common.utils.IImageUtil;
import com.framework.common.utils.ILog;
import com.framework.common.utils.IStringUtil;
import com.framework.manager.ICacheManager;
import com.xlb.elect.cigar.R;
import com.xiaolanba.passenger.common.interfaces.SelectImageCallBack;
import com.xiaolanba.passenger.common.utils.CropFileUtils;
import com.xiaolanba.passenger.logic.manager.CacheManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 媒体选择框
 *
 * @author xutingz
 */
public class MediaChooseDialog extends Dialog implements OnClickListener, SelectImageCallBack {

    /**
     * 相册选择
     */
    public static final int CODE_ALBUM = 5001;
    /**
     * 拍照
     */
    public static final int CODE_TAKEPHOTO = 5002;
    /**
     * 裁剪照片
     */
    public static final int CODE_CROP = 5005;
    /**
     * 拍照缓存文件路径
     */
    private String cameraFilePath;
    private Uri photoUri;
    private Uri cropUri;

    protected Context mContext;
    private Activity activity;
    private MediaChooseListener listener;

    private boolean needCrop = false;
    private String title;
    private IImageUtil.CropOutParam cropOutParam;

    private int maxSelectNum;
    private boolean mHasGif;

    public MediaChooseDialog(Context context, MediaChooseListener listener) {
        super(context, R.style.BaseDialog);
        activity = (Activity) context;
        mContext = context;
        this.listener = listener;
        setCanceledOnTouchOutside(true);

        init();
    }

    public MediaChooseDialog(Context context, String title, boolean needCrop, MediaChooseListener listener) {
        super(context, R.style.BaseDialog);
        activity = (Activity) context;
        mContext = context;
        this.needCrop = needCrop;
        this.title = title;
        this.listener = listener;
        setCanceledOnTouchOutside(true);

        init();
    }

    public MediaChooseDialog(Context context, String title, boolean needCrop, IImageUtil.CropOutParam cropOutParam, MediaChooseListener listener) {
        super(context, R.style.BaseDialog);
        activity = (Activity) context;
        mContext = context;
        this.needCrop = needCrop;
        this.cropOutParam = cropOutParam;
        this.title = title;
        this.listener = listener;
        setCanceledOnTouchOutside(true);

        init();
    }

    private void init() {
        setContentView();
        findView();
        initData();
    }

    public void setMaxSelectNum(int maxSelectNum) {
        this.maxSelectNum = maxSelectNum;
    }

    public void setHasGif(boolean mHasGif) {
        this.mHasGif = mHasGif;
    }

    public void setContentView() {
        setContentView(R.layout.dialog_media_choose_layout);
    }

    public void findView() {
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_album).setOnClickListener(this);
        findViewById(R.id.btn_tackPhoto).setOnClickListener(this);

        ((Button) findViewById(R.id.title)).setText((!IStringUtil.isEmpty(title)) ? title : "操作");

    }

    public void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_album:
                if (ICommonUtil.isFastDoubleClick()) return;
                if (maxSelectNum > 0 && !needCrop) { //跳转系统相册
//				GalleryDialog dialog = new GalleryDialog(mContext, this, maxSelectNum, mHasGif);
//				dialog.show();
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        activity.startActivityForResult(intent, CODE_ALBUM);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("找不到系统相册");
                        dismiss();
                    }
                }
                break;
            case R.id.btn_tackPhoto:
                if (ICommonUtil.isFastDoubleClick()) return;
                cameraFilePath = null;
                photoUri = null;
                cropUri = null;
                Intent intentTackPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentTackPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intentTackPhoto.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                File dir = new File(CacheManager.SD_IMAGE_CHOOSE_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
//			try {
//				ContentValues values = new ContentValues(1);
//				values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
//				photoUri = mContext.getContentResolver()
//						.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
                try {
                    photoUri = Uri.fromFile(IImageUtil.createImageFile(dir));
                    intentTackPhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    intentTackPhoto.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    activity.startActivityForResult(intentTackPhoto, CODE_TAKEPHOTO);
                } catch (Exception e) {

                }
                break;
            default:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CODE_ALBUM) {
                if (null != data) {
                    Uri uri = data.getData();
                    String imagePath = IFileUtil.getRealFilePath(mContext, uri);
                    ILog.i("MediaChooseDialog", "imagePath:" + imagePath);

                    if (needCrop) {
                        cropUri = IImageUtil.generateUri(ICacheManager.SD_IMAGE_CHOOSE_DIR);
                        IImageUtil.startPhotoCrop(imagePath, cropOutParam, activity, CODE_CROP, cropUri);
                    } else {
                        if (listener != null) {
                            listener.chooseMedia(imagePath, requestCode);
                            dismiss();
                        }
                    }
                }
            }
            // 发布图文时的拍照
            else if (requestCode == CODE_TAKEPHOTO) {
                if (photoUri != null) {
                    Cursor cursor = activity.getContentResolver().query(photoUri, null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            cameraFilePath = cursor.getString(cursor.getColumnIndex("_data"));
                        }
                        cursor.close();
                    }
                }
                if (!IFileUtil.isImage(cameraFilePath) && photoUri != null) {
                    cameraFilePath = photoUri.getPath();
                }
                if (IFileUtil.isEmptyFile(cameraFilePath)) {
                    getPath(data);
                }
                if (!IFileUtil.isExists(cameraFilePath)) {
                    cameraFilePath = IImageUtil.getRealFilePath(mContext);
                }
                ILog.i("MediaChooseDialog", "imagePath:" + cameraFilePath);
                if (Activity.RESULT_OK == resultCode) {
                    if (needCrop) {
                        // 1.拍照时 使用此方法 裁剪， 兼容朵唯等手机，提示无法保存裁剪后的图片问题
                        // 2.裁剪取消，再拍照裁剪，获取照片不对的问题
                        // 3.部分手机拍照跳转不到裁剪页面的问题
                        // cropUri = IImageUtil.startPhotoCrop(cameraFilePath, cropOutParam, activity, CODE_CROP);
                        dismiss();
                        cropUri = IImageUtil.generateUri(ICacheManager.SD_IMAGE_CHOOSE_DIR);
                        IImageUtil.startPhotoCrop(cameraFilePath, cropOutParam, activity, CODE_CROP, cropUri);
                    } else {
                        if (listener != null) {
                            listener.chooseMedia(cameraFilePath, requestCode);
                            dismiss();
                        }
                    }
                }
            } else if (requestCode == CODE_CROP) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = null;
                    if (bundle != null) {
                        bitmap = bundle.getParcelable("data");
                    }
                    if (bitmap != null) {
                        String savePath = ICacheManager.SD_IMAGE_CHOOSE_DIR + "/" + System.currentTimeMillis() + ".jpg";
                        IFileUtil.saveFileByBitmap(bitmap, savePath);
                        if (listener != null) {
                            listener.chooseMedia(savePath, requestCode);
                            dismiss();
                        }
                    } else {
                        String path = null;
                        if (cropUri != null) {
                            path = CropFileUtils.getSmartFilePath(mContext, cropUri);
                            if (IStringUtil.isEmpty(path)) {
                                path = cropUri.getPath();
                            }
                        } else if (data.getData() != null) {
                            path = CropFileUtils.getSmartFilePath(mContext, data.getData());
                        }
                        if (path != null) {
                            if (listener != null) {
                                listener.chooseMedia(path, requestCode);
                                dismiss();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("拍照或裁剪出现异常");
            dismiss();
        }
    }

    private void getPath(Intent data) {
        // 有些手机有时候获取不到
        Object object = null;
        if (data != null) {
            if (data.getData() != null) {
                Cursor cursor = mContext.getContentResolver().query(data.getData(), null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        cameraFilePath = cursor.getString(cursor.getColumnIndex("_data"));
                    }
                    cursor.close();
                }
            } else {//不会自动存储DCIM
                object = (Bitmap) (data.getExtras() == null ? null : data.getExtras().get("data"));
            }
        }
        Bitmap bitmap = object == null ? null : (Bitmap) object;
        if (bitmap != null && TextUtils.isEmpty(cameraFilePath)) {
            String filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
            cameraFilePath = IImageUtil.saveImage(bitmap, CacheManager.SD_IMAGE_CHOOSE_DIR + File.separator + filename);
        }
    }

    @Override
    public void show() {

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogPopupAnimation);

        super.show();

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = IDisplayUtil.getScreenWidth(mContext);
        window.setAttributes(lp);

    }

    @Override
    public void selectImage(List<String> list) {
        // 多选模式时，如果是裁剪，则跳转到裁剪页面，否则直接返回list
        if (needCrop && list != null && list.size() == 1) {
            cropUri = IImageUtil.generateUri(ICacheManager.SD_IMAGE_CHOOSE_DIR);
            IImageUtil.startPhotoCrop(list.get(0), cropOutParam, activity, CODE_CROP, cropUri);
            return;
        }
        if (listener != null) {
            listener.chooseMedia(list);
        }
    }

    public interface MediaChooseListener {
        void chooseMedia(String path, int type);

        void chooseMedia(List<String> pathList);
    }


    /**
     * toast提示
     *
     * @param title
     */
    private void showToast(String title) {
        MyToast.makeText(mContext, title, MyToast.LENGTH_LONG).show();
    }

}

