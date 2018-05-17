package com.framework.common.utils;

import java.util.Map;

/**
 * 公共工具类
 *
 * @author xutingz
 */
public class ICommonUtil {

    private static long lastClickTime;

    /**
     * 按钮是否短时间内重复点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 600) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 当800ms不符合需求时，可自己传入一个短暂时间(一般用于快速选择)
     *
     * @return
     */
    public static boolean isFastDoubleClick(long timeMill) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < timeMill) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    private static long viewId;

    public static boolean isFastDoubleClick(int viewId) {
        if (ICommonUtil.viewId == viewId) {
            return isFastDoubleClick();
        } else {
            ICommonUtil.viewId = viewId;
            return false;
        }
    }

    /**
     * 拼接参数url
     *
     * @param baseUrl
     * @param paramsMap
     * @return
     */
    public static String getParamsUrl(String baseUrl, Map<String, String> paramsMap) {
        if (paramsMap != null && !paramsMap.isEmpty()) {
            StringBuffer buf = new StringBuffer(baseUrl == null ? "" : baseUrl);
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                if (buf.indexOf("?") > 0) {
                    buf.append("&");
                } else {
                    buf.append("?");
                }
                buf.append(entry.getKey()).append("=").append(entry.getValue());
            }
            baseUrl = buf.toString();
        }

        return baseUrl;
    }

    /**
     * 剔除掉url中指定的参数
     *
     * @param url
     * @param params
     * @return
     */
    public static String removeParams(String url, String... params) {
        try {
            String reg = null;
            for (int i = 0; i < params.length; i++) {
                reg = "(?<=[\\?&])" + params[i] + "=[^&]*&?";
                url = url.replaceAll(reg, "");
            }
            url = url.replaceAll("&+$", "");
        } catch (Exception e) {

        }
        return url;
    }

    /**
     * 九宫格图，取缩略图用1
     *
     * @param imageUrl
     * @param size
     * @return
     */
    public static String getThumbImageUrlForGrid(String imageUrl, int size) {
        return getThumbImageUrl(imageUrl, size, size, 1);
    }

    /**
     * 等比缩放，不裁剪
     *
     * @param imageUrl
     * @param width
     * @return
     */
    public static String getThumbImageUrlNotCrop(String imageUrl, int width, int height) {
        return getThumbImageUrl(imageUrl, width, height, 0);
    }

    /**
     * 获取缩略图， 针对七牛
     *
     * @param imageUrl
     * @param size
     * @return
     */
    public static String getThumbImageUrl(String imageUrl, int size) {
        return getThumbImageUrl(imageUrl, size, size);
    }

    public static String getThumbImageUrl(String imageUrl, int width, int height) {
        return getThumbImageUrl(imageUrl, width, height, 2);
    }

    public static String getThumbImageUrl(String imageUrl, int width, int height, int model) {
        StringBuffer buf = new StringBuffer(imageUrl == null ? "" : imageUrl);
        if (buf.indexOf("?") > 0) {
            buf.append("&");
        } else {
            buf.append("?");
        }
        buf.append("imageView2/").append(model);
        buf.append("/w/").append(width);
        buf.append("/h/").append(height);
        //buf.append("/interlace/1");// interlace 渐进显示
        buf.append("/ignore-error/1"); // 处理的结果失败，则返回原图

        imageUrl = buf.toString();
        return imageUrl;

        //buf.append("imageMogr2/auto-orient/thumbnail/100x100");
    }

}
