package com.xiaolanba.passenger.module.common.download;

import android.os.Environment;

import com.framework.common.utils.IFileUtil;
import com.framework.common.utils.ILog;
import com.xiaolanba.passenger.LBApplication;
import com.xiaolanba.passenger.logic.control.LBController;
import com.xiaolanba.passenger.logic.manager.SharedPreferencesManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载apk工具类,暂不支持断点续传，下载未完成前杀死进程后不保存之前的任务
 *
 * @author xutingz
 * @E-mail xutz@xianlanba.com
 * @date 2018/04/13
 */

public class DownloadUtil {
    private final static String TAG = "DownloadUtil";
    private static DownloadUtil sDownloadUtil;
    private final OkHttpClient okHttpClient;
    private List<String> downloadingList;
    private List<String> successList;

    public static DownloadUtil getInstance() {
        if (sDownloadUtil == null) {
            sDownloadUtil = new DownloadUtil();
        }
        return sDownloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient(); //默认超时时间为10秒
        downloadingList = new ArrayList<>();
        successList = new ArrayList<>();
    }

    /**
     * 该文件名对应文件是否正在下载中
     */
    public boolean hasDownloadingTask(String fileName){
        return downloadingList.contains(fileName);
    }

    /**
     * 该文件名对应文件是否下载成功
     */
    public boolean hasSuccessTask(String fileName){
        return successList.contains(fileName);
    }

    private OnDownloadListener downloadListener;

    public void setOnDownloadListener(OnDownloadListener listener){
        this.downloadListener = listener;
    }

    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的文件夹路径
     * @param fileName  储存下载文件的具体名称(不包括文件夹路径),下载的apk每一个版本必须对应一个唯一的名字
     */
    public void download(final String url, final String saveDir, final String fileName) {
        if (!hasSDCard()) {
            ILog.i(TAG, "---没有sd卡");
            return;
        }
        SharedPreferencesManager preferencesManager = new SharedPreferencesManager(SharedPreferencesManager.USER_MODULE);
        preferencesManager.save(SharedPreferencesManager.KEY_UPDATE_SUC_VER,"");
        IFileUtil.deleteDirectoryOnlyFile(saveDir); //下载前清空文件夹内的文件。
        downloadingList.add(fileName);
        successList.clear();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postFailed();
                downloadingList.remove(fileName);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[4096];
                int len = 0;
                FileOutputStream fos = null;
                String savePath = isExistDir(saveDir); // 储存下载文件的目录
                int lastProgress = -1;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, fileName);
                    fos = new FileOutputStream(file);
                    final String obsolutePath = file.getAbsolutePath();
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        if (progress > 100){
                            progress = 100;
                        }
                        if (progress > lastProgress){ //拦截同一个百分比回调多次
                            lastProgress = progress;
                            postProgress(lastProgress);
//                            ILog.i(TAG, "---apk下载中"+lastProgress);
                        }
                    }
                    fos.flush();
                    SharedPreferencesManager preferencesManager = new SharedPreferencesManager(SharedPreferencesManager.USER_MODULE);
                    preferencesManager.save(SharedPreferencesManager.KEY_UPDATE_SUC_VER,fileName);
                    successList.add(fileName);
                    postSuccess(obsolutePath);
                    ILog.i(TAG, "---apk下载完成");
                } catch (Exception e) {
                    postFailed();
                } finally {
                    downloadingList.remove(fileName);
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 判断下载目录是否存在
     *
     * @param saveDir
     * @return
     * @throws IOException
     */
    private String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(saveDir);// 下载位置
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    public boolean hasSDCard() {
        String storageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(storageState);
    }

    private void postSuccess(final String obsolutePath){ //必须回调到主线程
        LBController.MainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (downloadListener != null) {
                    downloadListener.onDownloadSuccess(obsolutePath); // 下载完成
                }
                if (LBController.getInstance().getPageManager().isMainActivityActive()) {
                    IFileUtil.installApk(LBApplication.getInstance(), obsolutePath);
                }
            }
        },1000); //延迟是因为避免出现解析包异常。
    }

    private void postFailed(){ //必须回调到主线程
        LBController.MainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (downloadListener != null) {
                    downloadListener.onDownloadFailed();
                }
            }
        });
    }

    private void postProgress(final int progress){ //必须回调到主线程
        LBController.MainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (downloadListener != null) {
                    downloadListener.onDownloading(progress);// 下载中
                }
            }
        });
    }
}
