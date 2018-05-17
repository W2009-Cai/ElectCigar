package com.xiaolanba.commonlib.utils;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 日志的功能操作类 可将日志保存至SD卡
 *
 * @author xutingz
 */
public class CLog {

    /**
     * 是否允许打印日志开关
     */
    private static boolean DEBUG = true;

    public static void setIsDebug(boolean flag){
        DEBUG = flag;
    }

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
        }
    }

    /**
     * log太长，显示不全，拼接输出
     *
     * @param level
     * @param tag
     * @param text
     * @author qinbaowei
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
            if (DEBUG) {
                String log = String.format(logFormat, logParam);
                String[] logs = createLog(log);
                if (DEBUG) {
                    log(type, logs[0], logs[1]);
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
