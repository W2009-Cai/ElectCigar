package com.framework.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 文件工具类
 *
 * @author xutingz
 */
public class IFileUtil {

    private static final String TAG = "IFileUtil";

    /**
     * 判断是否有SDCard
     *
     * @return 有为true，没有为false
     */
    public static boolean hasSDCard() {
        String storageState = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(storageState);
    }

    /**
     * 获取SD卡剩余容量
     *
     * @return
     */
    public static long getAvailableStorage() {

        String storageDirectory = null;
        storageDirectory = Environment.getExternalStorageDirectory().toString();

        try {
            StatFs stat = new StatFs(storageDirectory);
            @SuppressWarnings("deprecation")
            long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
            return avaliableSize;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    @SuppressWarnings("deprecation")
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量  
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 判断是否为空文件
     *
     * @return
     */
    public static boolean isEmptyFile(String path) {
        if (IStringUtil.isEmpty(path)) {
            return true;
        }
        long size = 0;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            size = fis.available();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ILog.i(TAG, "isEmptyFile:" + size);
        return size <= 0;
    }

    public static void mkdirs(String path) {
        if (IStringUtil.isEmpty(path)) {
            ILog.d(TAG, "IFileUtil.mkdirs:文件目录为空");
            return;
        }

        mkdirs(new File(path));
    }

    public static void mkdirs(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 根据Bitmap保存文件
     *
     * @param bitmap
     */
    public static void saveFileByBitmap(Bitmap bitmap, String filepath) {
        if (null != bitmap) {
            File file = new File(filepath);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != bos) {
                        bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 通过url删除本地缓存的文件
     *
     * @param url
     * @param fileDir
     */
    public static void deleteFileByUrl(String url, String fileDir) {
        try {
            String fileName = getFileNameFromUrl(url, ".jpg");
            File file = new File(fileDir + File.separator + fileName);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isExistsByUrl(String url, String fileDir) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            String fileName = getFileNameFromUrl(url, ".jpg");
            File file = new File(fileDir + File.separator + fileName);
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isExists(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 只删除目录下的文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteDirectoryOnlyFile(String filePath) {
        return deleteDirectory(filePath, false, true);
    }


    /**
     * 删除目录下的文件
     *
     * @param filePath
     * @param delDir   是否删除目录
     * @return
     */
    public static boolean deleteDirectory(String filePath, boolean delDir) {
        return deleteDirectory(filePath, delDir, false);
    }

    public static boolean deleteDirectory(String filePath, boolean delDir, boolean onlyFile) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符

        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } // 删除子目录
            else {
                if (!onlyFile) {
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }
        }
        if (!flag) {
            return false;
        }

        if (delDir) {
            // 删除当前目录
            if (dirFile.delete()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        return deleteDirectory(filePath, true);
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public static long getFileSize(String path) {
        if (IStringUtil.isEmpty(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    /**
     * 获取文件名
     *
     * @param path 文件路径
     * @return
     */
    public static String getFileName(String path) {
        String filename = null;
        if (!IStringUtil.isEmpty(path)) {
            filename = path.substring(path.lastIndexOf("/") + 1, path.length());
        }
        return filename;
    }

    /**
     * 获取目录名
     *
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {
        if (IStringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * 创建文件
     *
     * @param filePath
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createNewFile(String filePath, String fileName) throws IOException {
        File folderFile = new File(filePath);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }

        File file = new File(filePath, fileName);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }

    /**
     * 根据URL获取文件名
     *
     * @param url
     * @param suffix
     * @return
     */
    public static String getFileNameFromUrl(String url, String suffix) {
        // 通过 ‘？’ 和 ‘/’ 判断文件名
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        int index = url.lastIndexOf('?');
        String filename = null;
        try {
            int startLastxg = url.lastIndexOf('/') + 1;
            if (index > 1) {
                if (index < startLastxg) {
                    return getFileNameFromUrl(url.substring(0, index), suffix);
                }
                filename = url.substring(startLastxg, index);
            } else {
                filename = url.substring(startLastxg);
            }
        } catch (Exception e) {
        }
        if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
            filename = UUID.randomUUID() + suffix;// 默认取一个文件名
        }
        if (filename.indexOf(".") == -1) {
            filename = filename + suffix;
        }
        return filename;
    }


    /**
     * 复制单个文件
     *
     * @param oldPath
     * @param newPath
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {                                    // 文件存在时
                InputStream inStream = new FileInputStream(oldPath);    // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;                                // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断是否是图片文件
     *
     * @param file
     * @return
     */
    public static boolean isImage(File file) {
        boolean flag = false;
        try {
            if (file != null) {
                return isImage(file.getName());
            }
        } catch (Exception e) {
        }

        return flag;
    }

    /**
     * 判断是否是图片文件
     *
     * @param name
     * @return
     */
    public static boolean isImage(String name) {
        boolean flag = false;
        try {
            name = name.toLowerCase();
            if (name.endsWith(".jpg") || name.endsWith(".png")
                    || name.endsWith(".jpeg") || name.endsWith(".bmp")
                    || name.endsWith(".gif")) {
                flag = true;
            }
        } catch (Exception e) {
        }
        return flag;
    }

    /**
     * 写入文件内容
     *
     * @param str
     * @param savePath
     * @return
     */
    public static boolean saveFileByString(String str, String savePath) {
        boolean flag = false;
        if (null != str) {
            FileOutputStream o = null;
            try {
                File file = new File(savePath);
                if (file.exists()) {
                    file.delete();
                }
                o = new FileOutputStream(file);
                o.write(str.getBytes("UTF-8"));
                o.close();
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
            return flag;
        }
        return flag;
    }


    /**
     * 读取文件内容
     *
     * @param savePath 读取的文件路径
     * @return 返回读取的内容
     */
    public static String readFileToString(String savePath) {
        String str = "";
        File file = new File(savePath);
        if (!file.exists()) {
            return str;
        }
        FileInputStream in = null;
        ;
        try {
            in = new FileInputStream(file);
            int len = in.available();
            byte[] buf = new byte[len];
            in.read(buf);
            str = new String(buf, "UTF-8");
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return str;
    }

    /**
     * 获取文件后缀
     *
     * @param path
     * @return
     */
    public static String getFileSuffix(String path) {
        String type = "jpg";
        try {
            int index = path.lastIndexOf('.');
            if (index != -1) {
                type = path.substring(index + 1).toLowerCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return type;
    }

    /**
     * 获取文件后缀
     *
     * @param file
     * @return
     */
    public static String getFileSuffix(File file) {
        String path = file.getAbsolutePath();
        return getFileSuffix(path);
    }

    /**
     * 通过文件后缀判断是不是gif
     *
     * @param url
     * @return
     */
    public static boolean isGif(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String fileName = MimeTypeMap.getFileExtensionFromUrl(url);
        return "gif".equals(fileName);
    }

    /**
     * @param path
     * @return 文件后缀
     */
    public static String getFileExtensionFromPath(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    public static String getFileNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 根据路径获取名字
     *
     * @param path
     * @return
     */
    public static String getFilePathFromName(String path) {
        return path.substring(0, path.lastIndexOf("."));
    }

    /**
     * @param extension 文件后缀(文件格式)
     * @return 文件格式对应的Mime类型(比如'jpg'->'image/*')
     */
    public static String getMimeTypeFromExtension(String extension) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @throws Exception
     */
    public static long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
                fis.close();
            } else {
                ILog.wr("getFileSize", "文件不存在!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            ILog.wr("getFileSize", "获取文件大小异常");
        }
        return size;
    }

    /**
     * 安装apk
     * @param context
     * @param filePath
     */
    public static void installApk(Context context,String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) return;
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
