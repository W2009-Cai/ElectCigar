package com.xiaolanba.passenger.common.bean;

/**
 * 列表对象作为返回数据，使用list字段
 */

public class BaseListParse<T> {

    public int code;
    public String msg;
    public T list;

    public T getList(){
        return list;
    }

    public BaseListParse() {

    }

    public boolean isRequestSuccess(){
        return code == 200;
    }
}
