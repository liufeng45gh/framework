package com.xn.pento.common;

import com.xn.pento.util.DateConverter;
import com.xn.pento.util.Log;
import net.csdn.ServiceFramework;
import net.csdn.jpa.model.Model;
import net.csdn.modules.cache.RedisClient;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-2-20
 * Time: PM7:11
 * To change this template use File | Settings | File Templates.
 */
public class BaseModel extends Model {
    @Override
    public boolean save() {
        String now = DateConverter.now();
        if (this.id() == null) {
            this.attr("created_at", now);
        }

        this.attr("updated_at", now);
        return super.save();
    }

    @Override
    public boolean update() {
        this.attr("updated_at", DateConverter.now());
        return super.update();
    }

    public boolean update(String cacheKey) {
        boolean ok = update();
        if (!ok) return false;
        try {
            RedisClient client = ServiceFramework.findService(RedisClient.class);
            client.del(cacheKey);
        } catch (Exception e) {
            // ignore cache failed
            Log.warn("cache del failed: [%s], %s", cacheKey, e.getMessage());
        }

        return true;
    }

    public void updateAttr(Map<String, String> params) {
        for (String key : params.keySet()) {
            attr(key, params.get(key));
        }
    }

    public String attr(String name) {
        return attr(name, String.class);
    }

    public int attrAsInt(String name) {
        return attr(name, Integer.class);
    }

    public long attrAsLong(String name) {
        return attr(name, Long.class);
    }

    protected <T> T findService(Class<T> clz) {
        return ServiceFramework.injector.getInstance(clz);
    }
}
