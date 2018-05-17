package com.framework.http;

import java.io.Serializable;

/**
 * @author xutingz
 * @company xiaolanba.com
 */
public class NameValuePair implements Serializable {
    private String name;
    private String value;

    public NameValuePair(String name, String value) {
        this.name = null;
        this.value = null;
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
