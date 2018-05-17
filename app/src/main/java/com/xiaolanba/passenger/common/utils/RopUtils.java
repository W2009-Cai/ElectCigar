package com.xiaolanba.passenger.common.utils;


import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <pre>
 * 功能说明：签名算法
 * </pre>
 */
public class RopUtils {

    /**
     * 使用<code>secret</code>对paramValues按以下算法进行签名： <br/>
     * uppercase(hex(sha1(secretkey1value1key2value2...secret))
     *
     * @param paramValues 参数列表
     * @param secret
     * @return
     */
    public static String sign(Map<String, String> paramValues, String secret) {
        return sign(paramValues, null, secret);
    }

    /**
     * 对paramValues进行签名，其中ignoreParamNames这些参数不参与签名
     *
     * @param paramValues
     * @param ignoreParamNames
     * @param secret
     * @return
     */
    public static String sign(Map<String, String> paramValues, List<String> ignoreParamNames, String secret) {
        try {
            StringBuilder sb = new StringBuilder();
            List<String> paramNames = new ArrayList<String>(paramValues.size());
            paramNames.addAll(paramValues.keySet());
            if (ignoreParamNames != null && ignoreParamNames.size() > 0) {
                for (String ignoreParamName : ignoreParamNames) {
                    paramNames.remove(ignoreParamName);
                }
            }
            Collections.sort(paramNames);

            sb.append(secret);
            for (String paramName : paramNames) {
                sb.append(paramName).append(paramValues.get(paramName));
            }
            sb.append(secret);
            //byte[] sha1Digest = getSHA1Digest(sb.toString());
            //return byte2hex(sha1Digest)
            byte[] md5Digest = getMD5Digest(sb.toString());
            return byte2hex(md5Digest);
        } catch (IOException e) {
            return null;
        }
    }


    public static String sign(JSONObject params, List<String> ignoreParamNames, String secret) {
        try {
            StringBuilder sb = new StringBuilder();
            if (null != params) {
                List<String> paramKeys = new ArrayList<String>();
                Iterator<String> iterator = params.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    paramKeys.add(key);
                }

                if (ignoreParamNames != null && ignoreParamNames.size() > 0) {
                    for (String ignoreParamName : ignoreParamNames) {
                        paramKeys.remove(ignoreParamName);
                    }
                }
                Collections.sort(paramKeys);

                sb.append(secret);
                for (String key : paramKeys) {
                    sb.append(key).append(params.get(key).toString());
                }
                sb.append(secret);
                byte[] md5Digest = getMD5Digest(sb.toString());
                return byte2hex(md5Digest);
            }

        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 需要加密的字符串
     *
     * @param message
     * @return
     * @author xutingz
     * @version 创建时间 2015-9-21
     */
    public static String sign(String message) {
        String signMessage = "";
        try {
            byte[] md5Digest = getMD5Digest(message);
            signMessage = byte2hexLowerCase(md5Digest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signMessage;
    }

    /**
     * md5使用小写字母
     *
     * @param bytes
     * @return
     * @author xutingz
     * @version 创建时间 2015-9-21
     */
    private static String byte2hexLowerCase(byte[] bytes) {
        return byte2hex(bytes).toLowerCase();
    }

    public static String utf8Encoding(String value, String sourceCharsetName) {
        try {
            return new String(value.getBytes(sourceCharsetName), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static byte[] getSHA1Digest(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            bytes = md.digest(data.getBytes("utf-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse);
        }
        return bytes;
    }

    private static byte[] getMD5Digest(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data.getBytes("utf-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse);
        }
        return bytes;
    }

    /**
     * 二进制转十六进制字符串
     *
     * @param bytes
     * @return
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
    }

}

