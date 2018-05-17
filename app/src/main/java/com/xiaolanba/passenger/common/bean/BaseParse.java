package com.xiaolanba.passenger.common.bean;

/**
 * bean基类, 包装公共的字段,当后台返回data字段作为数据时
 * 单个对象作为返回数据，使用data字段
 * @author xutingz
 * @company xiaolanba.com
 */
public class BaseParse<T>{

    public int code;
    public String msg;
    public T data;
    public T list;

    public T getData(){
        if (list != null){
            return list;
        }
        return data;
    }

    public BaseParse() {

    }

    public boolean isRequestSuccess(){
        return code == 200;
    }

}
