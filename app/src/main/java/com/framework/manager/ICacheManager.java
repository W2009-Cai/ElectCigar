package com.framework.manager;

import android.os.Environment;

import com.framework.common.utils.IFileUtil;
import com.framework.common.utils.ILog;

import java.io.File;

/**
 * 缓存管理
 *
 * @author xutingz
 */
public class ICacheManager {

    protected static final String TAG = "ICacheManager";

    /**
     * SD卡路径
     */
    public static final String SD_DIR = Environment.getExternalStorageDirectory().getPath();
    public static final String SD_APP_DIR_NAME = "xiaolanba";
    /**
     * 项目根路径
     */
    public static final String SD_APP_DIR = SD_DIR + "/" + SD_APP_DIR_NAME;
    /**
     * Log保存目录
     */
    public static final String SD_LOG_DIR = SD_APP_DIR + "/.log";
    /**
     * 文件保存路径
     */
    public static final String SD_SAVE_DIR = SD_APP_DIR + "/file";
    /**
     * 缓存路径
     */
    public static final String SD_CACHE_DIR = SD_APP_DIR + "/.cache";
    /**
     * 图片路径
     */
    public static final String SD_IMAGE_DIR = SD_APP_DIR + "/image";
    /**
     * 音频路径
     */
    public static final String SD_AUDIO_DIR = SD_APP_DIR + "/audio";

    /**
     * 图片路径 --> 选择图片路径 拍照，或者裁剪
     */
    public static final String SD_IMAGE_CHOOSE_DIR = SD_IMAGE_DIR + File.separator + "choose";
    /**
     * 图片路径--> 压缩图片路径
     */
    public static final String SD_IMAGE_COMPRESS_DIR = SD_IMAGE_DIR + File.separator + "compress";
    /**
     * 图片路径--> 压缩分享图片路径(大图片必须压缩后才能分享成功)
     */
    public static final String SD_IMAGE_SHARE_DIR = SD_IMAGE_DIR + File.separator + "share";

    public ICacheManager() {
        init();
    }

    private void init() {
        createAppDir();
    }

    /**
     * 创建项目文件夹
     */
    public void createAppDir() {
        if (IFileUtil.hasSDCard()) {
            IFileUtil.mkdirs(new File(SD_APP_DIR));
            IFileUtil.mkdirs(new File(SD_LOG_DIR));
            IFileUtil.mkdirs(new File(SD_SAVE_DIR));
            IFileUtil.mkdirs(new File(SD_CACHE_DIR));
            IFileUtil.mkdirs(new File(SD_IMAGE_DIR));
            IFileUtil.mkdirs(new File(SD_AUDIO_DIR));
            IFileUtil.mkdirs(new File(SD_IMAGE_CHOOSE_DIR));
            IFileUtil.mkdirs(new File(SD_IMAGE_COMPRESS_DIR));

        } else {
            ILog.d(TAG, "createProjectDir:手机无SD卡");
        }
    }
}
