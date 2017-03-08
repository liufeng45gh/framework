package com.xn.pento.service;

import com.google.inject.Inject;
import com.xn.pento.common.BaseService;
import com.xn.pento.util.Log;
import net.csdn.ServiceFramework;
import net.csdn.common.settings.Settings;
import net.csdn.jpa.model.Model;
import org.apache.commons.fileupload.FileItem;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-24
 * Time: PM9:06
 * To change this template use File | Settings | File Templates.
 */
public class FileStoreService extends BaseService {
    private static final int DIRECTORY_LEVE = 4;

    private static String path;
    private static String url;

    @Inject
    public FileStoreService(Settings settings) {
        try {
            path = settings.get(ServiceFramework.mode + ".datasources.filestore.path");
            url = settings.get(ServiceFramework.mode + ".datasources.filestore.url");
        } catch (Exception e) {
            Log.error("FileStoreService init failed, %s", e.getMessage());
        }
    }

    public String getBaseUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    private String getRelativePath(String md5) {
        StringBuffer pathBuffer = new StringBuffer();
        for (int i = 0; i < DIRECTORY_LEVE; i++) {
            pathBuffer.append(new String(md5.substring(i, i+1)));
            pathBuffer.append("/");
        }

        return pathBuffer.toString();
    }

    public File getFilePath(String md5) {
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        filePath = filePath.toPath().resolve(getRelativePath(md5)).toFile();
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

//        for (int i = 0; i < DIRECTORY_LEVE; i++) {
//            filePath = filePath.toPath().resolve(new String(md5.substring(i, i+1))).toFile();
//            if (!filePath.exists()) {
//                filePath.mkdirs();
//            }
//        }

        return filePath.toPath().resolve(md5).toFile();
    }

    public String saveFile(FileItem fileItem) throws Exception {
        InputStream is = fileItem.getInputStream();
        try {
//            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(UUID.randomUUID().toString());
            File file = getFilePath(md5);

            if (!file.exists()) {
                fileItem.write(file);
                Log.info("new file: %s", file.getAbsolutePath());
            } else {
                Log.info("exist file: %s", file.getAbsolutePath());
            }
            return md5;
        } finally {
            is.close();
        }
    }

    public boolean md5Exist(String md5) {
        File file = getFilePath(md5);
        return file.exists();
    }

    public String getFileUrl(String md5) {
        if (md5 == null || md5.isEmpty()) {
            return null;
        }
        return getBaseUrl() + "/" + getRelativePath(md5) + md5;
    }

    public void filterFileUrl(List models, String field) {
        for (Object model : models) {
            filterFileUrl((Model)model, field);
        }
    }

    public void filterFileUrl(Model model, String field) {
        if (model.attr(field, String.class) != null) {
            model.attr(field, getFileUrl(model.attr(field, String.class)));
        }
    }

    public void filterFileUrl(Map model, String field) {
        if (model.get(field) != null) {
            model.put(field, getFileUrl((String)model.get(field)));
        }
    }
}
