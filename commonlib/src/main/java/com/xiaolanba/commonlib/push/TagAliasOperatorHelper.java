package com.xiaolanba.commonlib.push;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.xiaolanba.commonlib.utils.CLog;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;

/**
 * 极光推送提供的工具类，处理tag 、alias相关失败重试的逻辑
 */
public class TagAliasOperatorHelper {
    private static final String TAG = "Jpush";
    public static int sequence = 1;
    /**
     * 增加
     */
    public static final int ACTION_ADD = 1;
    /**
     * 覆盖
     */
    public static final int ACTION_SET = 2;
    /**
     * 删除部分
     */
    public static final int ACTION_DELETE = 3;
    /**
     * 删除所有
     */
    public static final int ACTION_CLEAN = 4;
    /**
     * 查询
     */
    public static final int ACTION_GET = 5;

    public static final int ACTION_CHECK = 6;

    public static final int DELAY_SEND_ACTION = 1;

    public static final int DELAY_SET_MOBILE_NUMBER_ACTION = 2;

    private Context context;

    private static TagAliasOperatorHelper mInstance;

    private TagAliasOperatorHelper() {
    }

    public static TagAliasOperatorHelper getInstance() {
        if (mInstance == null) {
            synchronized (TagAliasOperatorHelper.class) {
                if (mInstance == null) {
                    mInstance = new TagAliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    private SparseArray<Object> setActionCache = new SparseArray<Object>();

    public Object get(int sequence) {
        return setActionCache.get(sequence);
    }

    public Object remove(int sequence) {
        return setActionCache.get(sequence);
    }

    public void put(int sequence, Object tagAliasBean) {
        setActionCache.put(sequence, tagAliasBean);
    }

    /**
     * 对于错误码为服务器繁忙的情况下，进行重试
     */
    @SuppressLint("HandlerLeak")
    private Handler delaySendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_SEND_ACTION:
                    if (msg.obj != null && msg.obj instanceof TagAliasBean) {
                        CLog.i(TAG, "on delay time");
                        sequence++;
                        TagAliasBean tagAliasBean = (TagAliasBean) msg.obj;
                        setActionCache.put(sequence, tagAliasBean);
                        if (context != null) {
                            handleAction(context, sequence, tagAliasBean);
                        }
                    }
                    break;
                case DELAY_SET_MOBILE_NUMBER_ACTION:
                    if (msg.obj != null && msg.obj instanceof String) {
                        CLog.i(TAG, "retry set mobile number");
                        sequence++;
                        String mobileNumber = (String) msg.obj;
                        setActionCache.put(sequence, mobileNumber);
                        if (context != null) {
                            handleAction(context, sequence, mobileNumber);
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 重新设置手机号
     */
    public void handleAction(Context context, int sequence, String mobileNumber) {
        put(sequence, mobileNumber);
        CLog.d(TAG, "sequence:" + sequence + ",mobileNumber:" + mobileNumber);
        JPushInterface.setMobileNumber(context, sequence, mobileNumber);
    }

    /**
     * 重新处理设置tag
     */
    public void handleAction(Context context, int sequence, TagAliasBean tagAliasBean) {
        init(context);
        if (tagAliasBean == null) {
            return;
        }
        put(sequence, tagAliasBean);
        if (tagAliasBean.isAliasAction) {
            switch (tagAliasBean.action) {
                case ACTION_GET:
                    JPushInterface.getAlias(context, sequence);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteAlias(context, sequence);
                    break;
                case ACTION_SET:
                    JPushInterface.setAlias(context, sequence, tagAliasBean.alias);
                    break;
                default:
                    CLog.w(TAG, "unsupport alias action type");
                    return;
            }
        } else {
            switch (tagAliasBean.action) {
                case ACTION_ADD:
                    JPushInterface.addTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_SET:
                    JPushInterface.setTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_CHECK:
                    //一次只能check一个tag
                    String tag = (String) tagAliasBean.tags.toArray()[0];
                    JPushInterface.checkTagBindState(context, sequence, tag);
                    break;
                case ACTION_GET:
                    JPushInterface.getAllTags(context, sequence);
                    break;
                case ACTION_CLEAN:
                    JPushInterface.cleanTags(context, sequence);
                    break;
                default:
                    CLog.w(TAG, "unsupport tag action type");
                    return;
            }
        }
    }

    private boolean RetryActionIfNeeded(int errorCode, TagAliasBean tagAliasBean) {
        if (!ExampleUtil.isConnected(context)) {
            CLog.w(TAG, "no network");
            return false;
        }
        //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
        if (errorCode == 6002 || errorCode == 6014) {
            CLog.d(TAG, "建议重试");
            if (tagAliasBean != null) {
                Message message = new Message();
                message.what = DELAY_SEND_ACTION;
                message.obj = tagAliasBean;
                delaySendHandler.sendMessageDelayed(message, 1000 * 60);
                String logs = getRetryStr(tagAliasBean.isAliasAction, tagAliasBean.action, errorCode);
                ExampleUtil.showLog(logs, context);
                return true;
            }
        }
        return false;
    }

    private boolean RetrySetMObileNumberActionIfNeeded(int errorCode, String mobileNumber) {
        if (!ExampleUtil.isConnected(context)) {
            CLog.w(TAG, "no network");
            return false;
        }
        //返回的错误码为6002 超时,6024 服务器内部错误,建议稍后重试
        if (errorCode == 6002 || errorCode == 6024) {
            Message message = new Message();
            message.what = DELAY_SET_MOBILE_NUMBER_ACTION;
            message.obj = mobileNumber;
            delaySendHandler.sendMessageDelayed(message, 1000 * 60);
            String str = "设置手机号失败了，原因是 %s，60秒后将重试.";
            str = String.format(str, (errorCode == 6002 ? "超时" : "服务繁忙"));
            ExampleUtil.showLog(str, context);
            return true;
        }
        return false;

    }

    private String getRetryStr(boolean isAliasAction, int actionType, int errorCode) {
        String str = "Failed to %s %s due to %s. 60秒后重试";
        str = String.format(str, getActionStr(actionType), (isAliasAction ? "alias" : " tags"), (errorCode == 6002 ? "连接超时" : "服务器繁忙"));
        return str;
    }

    private String getActionStr(int actionType) {
        switch (actionType) {
            case ACTION_ADD:
                return "add";
            case ACTION_SET:
                return "set";
            case ACTION_DELETE:
                return "delete";
            case ACTION_GET:
                return "get";
            case ACTION_CLEAN:
                return "clean";
            case ACTION_CHECK:
                return "check";
        }
        return "unkonw operation";
    }

    //操作设置Tag的回调
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        CLog.i(TAG, "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags());
        CLog.i(TAG, "tags size:" + jPushMessage.getTags().size());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            ExampleUtil.showLog("获取缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) { // 0 表示设置成功了
            CLog.i(TAG, "action - 设置Tag成功 ,sequence:" + sequence);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tags success";
            CLog.i(TAG, logs);
            ExampleUtil.showLog(logs, context);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags";
            if (jPushMessage.getErrorCode() == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tag数量超过限制了";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            CLog.e(TAG, logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                ExampleUtil.showLog(logs, context);
            }
        }
    }

    //操作核对Tag的回调
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        CLog.i(TAG, "action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            ExampleUtil.showLog("获取缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            CLog.i(TAG, "tagBean:" + tagAliasBean);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
            CLog.i(TAG, logs);
            ExampleUtil.showLog(logs, context);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " tags, errorCode:" + jPushMessage.getErrorCode();
            CLog.e(TAG, logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                ExampleUtil.showLog(logs, context);
            }
        }
    }

    //操作别名的回调
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        CLog.i(TAG, "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        TagAliasBean tagAliasBean = (TagAliasBean) setActionCache.get(sequence);
        if (tagAliasBean == null) {
            ExampleUtil.showLog("获取缓存记录失败", context);
            return;
        }
        if (jPushMessage.getErrorCode() == 0) {
            CLog.i(TAG, "action - 设置别名成功,sequence:" + sequence);
            setActionCache.remove(sequence);
            String logs = getActionStr(tagAliasBean.action) + " alias success";
            CLog.i(TAG, logs);
            ExampleUtil.showLog(logs, context);
        } else {
            String logs = "Failed to " + getActionStr(tagAliasBean.action) + " alias, errorCode:" + jPushMessage.getErrorCode();
            CLog.e(TAG, logs);
            if (!RetryActionIfNeeded(jPushMessage.getErrorCode(), tagAliasBean)) {
                ExampleUtil.showLog(logs, context);
            }
        }
    }

    //操作手机号码回调
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        CLog.i(TAG, "action - onMobileNumberOperatorResult, sequence:" + sequence + ",mobileNumber:" + jPushMessage.getMobileNumber());
        init(context);
        if (jPushMessage.getErrorCode() == 0) {
            CLog.i(TAG, "action - 设置手机号成功,sequence:" + sequence);
            setActionCache.remove(sequence);
        } else {
            String logs = "设置手机号失败, errorCode:" + jPushMessage.getErrorCode();
            CLog.e(TAG, logs);
            if (!RetrySetMObileNumberActionIfNeeded(jPushMessage.getErrorCode(), jPushMessage.getMobileNumber())) {
                ExampleUtil.showLog(logs, context);
            }
        }
    }

    public static class TagAliasBean {
        int action;
        Set<String> tags;
        String alias;
        boolean isAliasAction;

        @Override
        public String toString() {
            return "TagAliasBean{" +
                    "action=" + action +
                    ", tags=" + tags +
                    ", alias='" + alias + '\'' +
                    ", isAliasAction=" + isAliasAction +
                    '}';
        }
    }


}
