package com.xn.pento.util;

import net.csdn.ServiceFramework;
import net.csdn.modules.http.DateJsonValueProcessor;
import net.sf.json.*;
import net.sf.json.util.CycleDetectionStrategy;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-1
 * Time: PM5:07
 * To change this template use File | Settings | File Templates.
 */
public class JSONHelper {
    public static JSON _toJson(Object object, JsonConfig jsonConfig) {
        JsonConfig thisConfig = jsonConfig.copy();
        thisConfig.setIgnoreDefaultExcludes(false);
        thisConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
        thisConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
        return JSONObject.fromObject(object, thisConfig);
    }

    public static String toJson(Object object, JsonConfig jsonConfig) {
        // set pretty if it's development
        if (ServiceFramework.mode == ServiceFramework.Mode.development) {
            return _toJson(object, jsonConfig).toString(2);
        }

        return _toJson(object, jsonConfig).toString();
    }

    public static String toJson(Object object) {
        return toJson(object, new JsonConfig());
    }
}
