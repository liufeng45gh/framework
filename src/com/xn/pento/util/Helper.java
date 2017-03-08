package com.xn.pento.util;

import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-13
 * Time: PM4:20
 * To change this template use File | Settings | File Templates.
 */
public class Helper {
    public static String encrypt(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuffer buffer = new StringBuffer();
            for (byte b : md.digest(str.getBytes("UTF-8"))) {
                buffer.append(String.format("%02X", b));
            }

            return buffer.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
