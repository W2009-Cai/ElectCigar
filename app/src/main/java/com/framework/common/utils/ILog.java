package com.framework.common.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.framework.manager.ICacheManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志的功能操作类 可将日志保存至SD卡
 *
 * @author xutingz
 */
public class ILog {

    /**
     * DEBUG级别开关
     */
    public static boolean DEBUG = true;

    /**
     * 是否保存至SD卡
     */
    private static final boolean SAVE_TO_SD = false;

    /**
     * 保存LOG日志的目录
     */
    private static String LogDirPath = ICacheManager.SD_LOG_DIR;

    /**
     * 日志打印时间Format
     */
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat LOG_FMT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 是否显示json格式的日志
     * 1.true,json字符串打印成json格式的日志
     * 2.false,打印普通字符串
     */
    public static final boolean isShowJsonLog = false;
    /**
     * 过滤日志
     */
    private static String filterTag = "xiaolanba";

    /**
     * 用于打印错误级的日志信息
     *
     * @param strModule LOG TAG
     * @param strErrMsg 打印信息
     */
    public static void e(String strModule, String strErrMsg) {
        if (DEBUG) {
            //Log.e(strModule, "----" + strErrMsg + "----");
            log('e', strModule, "----" + strErrMsg);
            if (SAVE_TO_SD) {
                storeLog(strModule, strErrMsg);
            }
        }
    }

    /**
     * 用于打印异常的的日志信息
     *
     * @param strModule LOG TAG
     * @param strErrMsg 打印信息
     */
    public static void e(String strModule, String strErrMsg, Exception e) {
        if (DEBUG) {
            //Log.e(strModule, "----" + strErrMsg + "----", e);
            log('e', strModule, "----" + strErrMsg);
            if (SAVE_TO_SD) {
                storeLog(strModule, strErrMsg);
            }
        }
    }

    /**
     * 用于打印描述级的日志信息
     *
     * @param strModule LOG TAG
     * @param strErrMsg 打印信息
     */
    public static void d(String strModule, String strErrMsg) {
        if (DEBUG) {
            //Log.d(strModule, "----" + strErrMsg + "----");
            log('d', strModule, "----" + strErrMsg);
            if (SAVE_TO_SD) {
                storeLog(strModule, strErrMsg);
            }
        }
    }

    /**
     * 用于打印info级的日志信息
     *
     * @param strModule LOG TAG
     * @param strErrMsg 打印信息
     */
    public static void i(String strModule, String strErrMsg) {
        if (DEBUG) {
            //Log.i(strModule, "----" + strErrMsg + "----");
            log('i', strModule, "----" + strErrMsg);
            if (SAVE_TO_SD) {
                storeLog(strModule, strErrMsg);
            }
        }
    }

    /**
     * 用于打印v级的日志信息
     *
     * @param strModule LOG TAG
     * @param strErrMsg 打印信息
     */
    public static void v(String strModule, String strErrMsg) {
        if (DEBUG) {
            //Log.v(strModule, "----" + strErrMsg + "----");
            log('v', strModule, "----" + strErrMsg);
            if (SAVE_TO_SD) {
                storeLog(strModule, strErrMsg);
            }
        }
    }

    /**
     * 用于打印w级的日志信息
     *
     * @param strModule LOG TAG
     * @param strErrMsg 打印信息
     */
    public static void w(String strModule, String strErrMsg) {
        if (DEBUG) {
            //Log.w(strModule, "----" + strErrMsg + "----");
            log('w', strModule, "----" + strErrMsg);
            if (SAVE_TO_SD) {
                storeLog(strModule, strErrMsg);
            }
        }
    }

    /**
     * 将日志信息保存至SD卡
     *
     * @param strModule LOG TAG
     * @param strErrMsg 保存的打印信息
     */
    public static void storeLog(String strModule, String strErrMsg) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            synchronized (LOG_FMT) {
                try {
                    String logname = LOG_FMT.format(new Date(System.currentTimeMillis())) + ".txt";
                    File file = new File(LogDirPath + "/" + logname);
                    // 输出
                    FileOutputStream fos = new FileOutputStream(file, true);
                    PrintWriter out = new PrintWriter(fos);

                    out.println(fmt.format(System.currentTimeMillis()) + "  >>" + strModule + "<<  " + strErrMsg + '\r');
                    out.flush();
                    out.close();

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取DEBUG状态
     *
     * @return
     */
    public static boolean isDebuggable() {
        return DEBUG;
    }

    /**
     * 用于打印wr级的日志信息
     *
     * @param strModule LOG TAG
     * @param strErrMsg 打印信息
     */
    public static void wr(String strModule, String strErrMsg) {
        if (DEBUG) {
            Log.w(strModule, "----" + strErrMsg);
            if (SAVE_TO_SD) {
                storeLog(strModule, strErrMsg);
            }
        }
    }

    /**
     * log太长，显示不全，拼接输出
     *
     * @param level
     * @param tag
     * @param text
     * @author xutingz
     */
    public static void log(char level, String tag, String text) {
        final int PART_LEN = 3000;

        do {
            int clipLen = text.length() > PART_LEN ? PART_LEN : text.length();
            String clipText = text.substring(0, clipLen);
            text = clipText.length() == text.length() ? "" : text.substring(clipLen);
            switch (level) {
                case 'i':
                    Log.i(tag, clipText);
                    break;
                case 'd':
                    Log.d(tag, clipText);
                    break;
                case 'w':
                    Log.w(tag, clipText);
                    break;
                case 'v':
                    Log.v(tag, clipText);
                    break;
                case 'e':
                    Log.e(tag, clipText);
                    break;
                default:
                    break;
            }
        } while (text.length() > 0);
    }

    //-----------------------begin print json-----------------------------------
    public static final int JSON_INDENT = 4;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static Object mutexPrintJson = new Object();

    public static void printJson(final String tag, final String msg, final String header,final String medhod) {
        if (!DEBUG) {
            return;
        }

        if (!isShowJsonLog) {
            i(tag, "http-("+medhod+")-responseBody:" + msg);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                synchronized (mutexPrintJson) {

                    String message;

                    if (msg != null) {
                        try {
                            if (msg.startsWith("{")) {
                                JSONObject jsonObject = new JSONObject(msg);
                                message = jsonObject.toString(JSON_INDENT);
                            } else if (msg.startsWith("[")) {
                                JSONArray jsonArray = new JSONArray(msg);
                                message = jsonArray.toString(JSON_INDENT);
                            } else {
                                message = msg;
                            }
                        } catch (JSONException e) {
                            message = msg;
                        }
                    } else {
                        message = "null";
                    }

                    log('i', tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
                    message = header + medhod+ LINE_SEPARATOR + message;
                    String[] lines = message.split(LINE_SEPARATOR);
                    for (String line : lines) {
                        log('i', tag, "║ " + line);
                    }
                    log('i', tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
                }
            }
        }.start();

    }

    public static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || line.equals("\n") || line.equals("\t") || TextUtils.isEmpty(line.trim());
    }

    public static void line(boolean isTop) {
        if (isTop) {
            l('v', "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            l('v', "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }
    //--------------------------end printjson----------------------------------------------------


    /**
     * @param depth 2,the method it self;3,the method who call this method
     * @return filename + method name + line number
     */
    private static String getFileNameMethodLineNumber(int depth) {
        String info = new String("");
        try {
            StackTraceElement e = getTraceElement(depth);
            if (null != e) {
                info = String.format("[%1$s,%2$s,%3$d]", e.getFileName(), e.getMethodName(), e.getLineNumber());
            }
        } catch (Exception e) {
            Log.e("log", "get filename,method and linenumber failed!!!");
        }
        return info;
    }

    private static StackTraceElement getTraceElement(int depth) {
        try {
            return Thread.currentThread().getStackTrace()[depth];
        } catch (Exception e) {
            //Log.e("log", "get stack trace element failed!!!");
        }
        return null;
    }

    public static void lv(String logFormat, Object... logParam) {
        l('v', logFormat, logParam);
    }

    public static void ld(String logFormat, Object... logParam) {
        l('d', logFormat, logParam);
    }

    public static void li(String logFormat, Object... logParam) {
        l('i', logFormat, logParam);
    }

    public static void lw(String logFormat, Object... logParam) {
        l('w', logFormat, logParam);
    }

    public static void lw(Throwable e) {
        if (null != e) {
            String message = e.getMessage();
            if (!TextUtils.isEmpty(message)) {
                l('w', message);
            }
        }
    }

    public static void le(String logFormat, Object... logParam) {
        l('e', logFormat, logParam);
    }

    public static void le(Throwable e) {
        if (null != e) {
            String message = e.getMessage();
            if (!TextUtils.isEmpty(message)) {
                l('e', message);
            }
        }
    }

    private static void l(char type, String logFormat, Object... logParam) {
        try {
            if (DEBUG || SAVE_TO_SD) {
                String log = String.format(logFormat, logParam);
                String[] logs = createLog(log);
                if (DEBUG) {
                    log(type, logs[0], logs[1]);
                }
                if (SAVE_TO_SD) {
                    storeLog(logs[0], logs[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] createLog(String log) {
        if (null == log) {
            log = new String("");
        }
        String tag = getFileNameMethodLineNumber(7);
        if (null == tag) {
            tag = new String("");
        } else {
            tag = "[" + filterTag + "]" + tag;
        }
        return new String[]{tag, log};
    }
}
