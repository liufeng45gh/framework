package com.xn.pento.common;

import com.xn.pento.util.JSONHelper;
import net.sf.json.JsonConfig;
import org.apache.commons.lang.ArrayUtils;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-2-21
 * Time: PM2:45
 * To change this template use File | Settings | File Templates.
 */
public class Result {
    boolean ok = true;
    Object message = null;
    Object data = null;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static Result result(boolean ok, Object message, Object data) {
        Result result = new Result();
        result.setOk(ok);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static Result ok() {
        return Result.result(true, null, null);
    }

    public static Result ok(Object data) {
        return Result.result(true, null, data);
    }

    public static Result fail() {
        return Result.result(false, null, null);
    }

    public static Result fail(Object msg) {
        return Result.result(false, msg, null);
    }

    public String toJson(JsonConfig jsonConfig) {
        String[] excludes = jsonConfig.getExcludes();
        if (data == null) {
            excludes = (String[])ArrayUtils.add(excludes, "data");
        }

        if (message == null) {
            excludes = (String[])ArrayUtils.add(excludes, "message");
        }

        jsonConfig.setExcludes(excludes);

        return JSONHelper.toJson(this, jsonConfig);
    }

    public String toJson() {
        return toJson(new JsonConfig());
    }

}
