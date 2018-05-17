package com.xiaolanba.passenger.common.receiver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/**
 * 短信验证码 读取
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class SmsObserver extends ContentObserver {

    public static final int CODE_SMSOBSERVER_RESULT = 9999;

    public Uri SMS_INBOX = Uri.parse("content://sms/");
    private Context mContext;
    private Handler mHandler;

    public SmsObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        this.mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        ContentResolver cr = mContext.getContentResolver();
        String[] projection = new String[]{"body"};
        String where = " date >  " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String body = cur.getString(cur.getColumnIndex("body"));
            if (!TextUtils.isEmpty(body)) {
                String flag = "验证码：";
                int index = body.indexOf(flag);
                if (index != -1) {
                    try {
                        String code = body.substring(index + flag.length());
                        Message message = new Message();
                        message.what = CODE_SMSOBSERVER_RESULT;
                        message.obj = code;
                        mHandler.sendMessage(message);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
