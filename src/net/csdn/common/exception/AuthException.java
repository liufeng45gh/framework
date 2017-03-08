package net.csdn.common.exception;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-12
 * Time: PM2:10
 * To change this template use File | Settings | File Templates.
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
