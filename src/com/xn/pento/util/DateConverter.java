package com.xn.pento.util;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-6
 * Time: PM6:00
 * To change this template use File | Settings | File Templates.
 */
public class DateConverter implements Converter {
    public final static String pattern = "yyyy-MM-dd hh:mm:ss";

    public Object convert(Class type, Object value) {
        if(value == null){
            return (null);
        }

        Date dateObj = null;
        if(value instanceof String){
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try{
                dateObj = sdf.parse((String)value);
            }catch(Exception pe){
                return null;
            }
        }

        return dateObj;
    }

    public static void register() {
        Object converter = ConvertUtils.lookup(Date.class);

        if (converter != null && converter instanceof DateConverter) {
            return;
        }

        ConvertUtils.register(new DateConverter(), Date.class);
    }

    public static String now() {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }
}
