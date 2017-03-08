package com.xn.pento.cache;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-1
 * Time: PM4:12
 * To change this template use File | Settings | File Templates.
 */
public interface CacheProvider {
    public Class getDataClass();
    public Object getData();
}
