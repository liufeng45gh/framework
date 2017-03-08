package com.xn.pento.cache;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-8
 * Time: PM1:59
 * To change this template use File | Settings | File Templates.
 */
public class CacheData {
    Object data;

    public CacheData() {
        this.data = null;
    }

    public CacheData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
