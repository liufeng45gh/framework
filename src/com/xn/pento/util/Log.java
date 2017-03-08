package com.xn.pento.util;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-6
 * Time: PM9:05
 * To change this template use File | Settings | File Templates.
 */
public class Log {
    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }


    public static void trace(String formatMsg, Object... params) {
        logMessage(LogLevel.TRACE, null, formatMsg, params);
    }

    public static void trace(Throwable throwable, String formatMsg, Object... params) {
        logMessage(LogLevel.TRACE, throwable, formatMsg, params);
    }

    public static void debug(String formatMsg, Object... params) {
        logMessage(LogLevel.DEBUG, null, formatMsg, params);
    }

    public static void debug(Throwable throwable, String formatMsg, Object... params) {
        logMessage(LogLevel.DEBUG, throwable, formatMsg, params);
    }

    public static void info(String formatMsg, Object... params) {
        logMessage(LogLevel.INFO, null, formatMsg, params);
    }

    public static void info(Throwable throwable, String formatMsg, Object... params) {
        logMessage(LogLevel.INFO, throwable, formatMsg, params);
    }

    public static void warn(String formatMsg, Object... params) {
        logMessage(LogLevel.WARN, null, formatMsg, params);
    }

    public static void warn(Throwable throwable, String formatMsg, Object... params) {
        logMessage(LogLevel.WARN, throwable, formatMsg, params);
    }

    public static void error(String formatMsg, Object... params) {
        logMessage(LogLevel.ERROR, null, formatMsg, params);
    }

    public static void error(Throwable throwable, String formatMsg, Object... params) {
        logMessage(LogLevel.ERROR, throwable, formatMsg, params);
    }

    private static void logMessage(LogLevel level, Throwable throwable, String msg, Object... params) {
        StackTraceElement caller = getCallerMethod();
        Logger logger = Logger.getLogger(caller.getClassName());
        String logString = null;
        if (params.length == 0) {
            logString = formatMsg(msg, caller);
        } else {
            logString = formatMsg(String.format(msg, params), caller);
        }
        if (level == LogLevel.TRACE) {
            logger.trace(logString, throwable);
        } else if (level == LogLevel.DEBUG) {
            logger.debug(logString, throwable);
        } else if (level == LogLevel.INFO) {
            logger.info(logString, throwable);
        } else if (level == LogLevel.WARN) {
            logger.warn(logString, throwable);
        } else if (level == LogLevel.ERROR) {
            logger.error(logString, throwable);
        }
    }

    private static StackTraceElement getCallerMethod() {
        return new Throwable().getStackTrace()[3];
    }

    private static String formatMsg(String msg, StackTraceElement caller) {
        return String.format("%s:%s \n%s", caller.getMethodName(), caller.getLineNumber(), msg);
    }

    public static void printCallStack(LogLevel level) {
        printCallStack(level, "com.xn.pbb");
    }

    public static void printCallStack(LogLevel level, String... packageNames) {
        StringBuffer sb = new StringBuffer("call stacks: \n");
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        for (StackTraceElement stack : stacks) {
            for (String packageName : packageNames) {
                if (stack.getClassName().startsWith(packageName)) {
                    sb.append(String.format("%s: %s: %s\n", stack.getClassName(), stack.getMethodName(), stack.getLineNumber()));
                    break;
                }
            }
        }
        logMessage(level,null, sb.toString());
    }
}
