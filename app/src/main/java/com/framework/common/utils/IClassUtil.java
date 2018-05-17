package com.framework.common.utils;

import java.lang.reflect.Field;

/**
 * Class 工具类
 *
 * @author xutingz
 */
public class IClassUtil {

    public static String printObject(Object obj) {
        try {
            StringBuffer buffer = new StringBuffer();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                buffer.append(field.getName());
                buffer.append(":");
                buffer.append(field.get(obj));
                buffer.append(" || ");
            }
            return buffer.toString();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}

