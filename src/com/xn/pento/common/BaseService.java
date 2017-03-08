package com.xn.pento.common;

import net.csdn.ServiceFramework;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-18
 * Time: PM2:43
 * To change this template use File | Settings | File Templates.
 */
public class BaseService {
    protected <T> T findService(Class<T> clz) {
        return ServiceFramework.injector.getInstance(clz);
    }
}
