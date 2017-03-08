package net.csdn.modules.http;

import com.xn.pento.util.Log;
import net.csdn.common.Booleans;
import net.csdn.common.Unicode;
import net.csdn.common.io.Streams;
import net.csdn.common.unit.ByteSizeValue;
import net.csdn.common.unit.TimeValue;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.util.JSONUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static net.csdn.common.unit.ByteSizeValue.parseBytesSizeValue;
import static net.csdn.common.unit.TimeValue.parseTimeValue;
import static net.csdn.modules.http.ApplicationController.list;

/**
 * BlogInfo: WilliamZhu
 * Date: 12-6-12
 * Time: 下午10:27
 */
public class DefaultRestRequest implements RestRequest {
    private static final Pattern commaPattern = Pattern.compile(",");

    private final HttpServletRequest servletRequest;

    private final Method method;

    private final Map<String, String> params;

    private byte[] content;

    List<FileItem> fileItems = list();

    public DefaultRestRequest(HttpServletRequest servletRequest) throws Exception {
        this.servletRequest = servletRequest;

        // check multiple files
        boolean isMultipart = ServletFileUpload.isMultipartContent(servletRequest);
        if (isMultipart) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(1024*1024*50);
            fileItems = upload.parseRequest(servletRequest);
        }

        this.method = Method.valueOf(servletRequest.getMethod());

        this.params = new HashMap<String, String>();

        content = null;
        String contentType = servletRequest.getHeader("content-type");
        if ("application/json".equals(contentType)) {
            content = Streams.copyToByteArray(servletRequest.getInputStream());
        } else if ("application/x-www-form-urlencoded".equals(contentType)) {
            if (servletRequest.getQueryString() != null) {
                RestUtils.decodeQueryString(servletRequest.getQueryString(), 0, params);
            }

            Enumeration<String> names = servletRequest.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                params.put(name, servletRequest.getParameter(name));
            }
        }
    }

    @Override
    public Method method() {
        return this.method;
    }

    @Override
    public String uri() {
        return servletRequest.getRequestURI();
    }

    @Override
    public String rawPath() {
        return servletRequest.getRequestURI();
    }

    @Override
    public boolean hasContent() {
        return content.length > 0;
    }

    @Override
    public boolean contentUnsafe() {
        return false;
    }

    @Override
    public byte[] contentByteArray() {
        return content;
    }

    @Override
    public int contentByteArrayOffset() {
        return 0;
    }

    @Override
    public int contentLength() {
        return content.length;
    }

    @Override
    public String contentAsString() {
        return Unicode.fromBytes(contentByteArray(), contentByteArrayOffset(), contentLength());
    }

    @Override
    public String header(String name) {
        return servletRequest.getHeader(name);
    }

    @Override
    public HttpSession getSession() {
        return servletRequest.getSession();
    }

    @Override
    public String getRemoteAddr() {
        return servletRequest.getRemoteAddr();
    }

    @Override
    public List<FileItem> getFileItems() {
        return fileItems;
    }

    @Override
    public Map<String, String> params() {
        return params;
    }

    @Override
    public String cookie(String name) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies == null || cookies.length == 0) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }


    @Override
    public boolean hasParam(String key) {
        return params.containsKey(key);
    }

    @Override
    public String param(String key) {
        return params.get(key);
    }

    @Override
    public String paramMultiKey(String... keys) {
        for (String key : keys) {
            String temp = param(key);
            if (!StringUtils.isEmpty(temp))
                return temp;
        }
        return null;
    }

    public String param(String key, String defaultValue) {
        String value = params.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }


    @Override
    public final String path() {
        return RestUtils.decodeComponent(rawPath());
    }

    @Override
    public float paramAsFloat(String key, float defaultValue) {
        String sValue = param(key);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(sValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse float parameter [" + key + "] with value [" + sValue + "]", e);
        }
    }

    @Override
    public int paramAsInt(String key, int defaultValue) {
        String sValue = param(key);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse int parameter [" + key + "] with value [" + sValue + "]", e);
        }
    }

    @Override
    public long paramAsLong(String key, long defaultValue) {
        String sValue = param(key);
        if (sValue == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(sValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse int parameter [" + key + "] with value [" + sValue + "]", e);
        }
    }

    @Override
    public boolean paramAsBoolean(String key, boolean defaultValue) {
        return Booleans.parseBoolean(param(key), defaultValue);
    }

    @Override
    public Boolean paramAsBoolean(String key, Boolean defaultValue) {
        String sValue = param(key);
        if (sValue == null) {
            return defaultValue;
        }
        return !(sValue.equals("false") || sValue.equals("0") || sValue.equals("off"));
    }

    @Override
    public TimeValue paramAsTime(String key, TimeValue defaultValue) {
        return parseTimeValue(param(key), defaultValue);
    }

    @Override
    public ByteSizeValue paramAsSize(String key, ByteSizeValue defaultValue) {
        return parseBytesSizeValue(param(key), defaultValue);
    }

    @Override
    public String[] paramAsStringArray(String key, String[] defaultValue) {
        String value = param(key);
        if (value == null) {
            return defaultValue;
        }
        return commaPattern.split(value);
    }
}
