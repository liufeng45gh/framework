package com.xn.pento.cache;

import com.xn.pento.util.JSONHelper;
import com.xn.pento.util.DateConverter;
import com.xn.pento.util.Log;
import net.csdn.ServiceFramework;
import net.csdn.modules.cache.RedisClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-8
 * Time: PM1:54
 * To change this template use File | Settings | File Templates.
 */
public class AppCache {
    public static Object fetch(String key, CacheProvider provider) throws Exception {
        RedisClient redis = ServiceFramework.findService(RedisClient.class);

        byte[] value = null;
        try {
            value = redis.get(key.getBytes());
        } catch (Exception e) {
            Log.info("cache failed [%s], %s", key, e.getMessage());
        }

        if (value != null) {
            JSONObject jsonObject = JSONObject.fromObject(new String(value));

            Object data = null;
            try {
                JSONObject dataJson = jsonObject.getJSONObject("data");
                data = fromJSONObject(dataJson, provider.getDataClass());
            } catch (Exception e) {
                // not json object
            }

            try {
                JSONArray arrayJson = jsonObject.getJSONArray("data");
                data = fromJSONArray(arrayJson, provider.getDataClass());
            } catch (Exception e) {
                // not json array
            }

            Object object = jsonObject.get("data");
            if (
                    object instanceof String ||
                            object instanceof Integer ||
                            object instanceof Long ||
                            object instanceof Double ||
                            object instanceof Boolean)
            {
                data = object;
            }

            if (data != null) {
                Log.info("cache fetched [%s]", key);
                return data;
            }
        }

        Log.info("cache missed: [%s]", key);

        Object data = provider.getData();

        try {
            if (data != null) {
                redis.set(key.getBytes(), JSONHelper.toJson(new CacheData(data)).getBytes());
            }
        } catch (Exception e) {
            Log.info("cache set failed: [%s], %s", key, e.getMessage());
        }

        return data;
    }

    public static void update(String key, Object data) throws Exception {
        RedisClient redis = ServiceFramework.findService(RedisClient.class);

        try {
            redis.set(key.getBytes(), JSONHelper.toJson(new CacheData(data)).getBytes());
        } catch (Exception e) {
            Log.info("cache set failed: [%s], %s", key, e.getMessage());
        }
    }

    public static void remove(String key) throws Exception {
        RedisClient redis = ServiceFramework.findService(RedisClient.class);

        try {
            redis.del(key.getBytes());
        } catch (Exception e) {
            Log.info("cache set failed: [%s], %s", key, e.getMessage());
        }
    }

    private static Object fromJSONObject(JSONObject jsonObject, Class clz) throws Exception {
        Object object = clz.newInstance();
        for (Object key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                BeanUtils.setProperty(object, (String) key, fromJSONObject((JSONObject) value, clz));
            } else if (value instanceof JSONArray) {
                BeanUtils.setProperty(object, (String) key, fromJSONArray((JSONArray) value, clz));
            } else {
                DateConverter.register();
                BeanUtils.setProperty(object, (String)key, value);
            }
        }

        return object;
    }

    private static List fromJSONArray(JSONArray jsonArray, Class clz) throws Exception {
        List list = new ArrayList();
        for (Object element : jsonArray.toArray()) {
            if (element instanceof JSONArray) {

            } else if (element instanceof JSONObject) {
                list.add(fromJSONObject((JSONObject)element, clz));
            }
        }

        return list;
    }

}
