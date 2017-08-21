//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xt.bluecard.lib.util;

import com.orhanobut.logger.Logger;

public class LogUtil {
    public static final String DEFAULT_TAG = "CardManager";

    public LogUtil() {
    }

    public static void v(String logText) {
        Logger.t(DEFAULT_TAG).v(String.valueOf(logText));
    }

    public static void v(String TAG, String logText) {
        Logger.t(DEFAULT_TAG).v("[" + TAG + "]" + logText);
    }

    public static void d(String logText) {
        Logger.t(DEFAULT_TAG).d(String.valueOf(logText));
    }

    public static void e(String logText) {
        Logger.t(DEFAULT_TAG).e(String.valueOf(logText));
    }

    public static void d(String TAG, String logText) {
        Logger.t(DEFAULT_TAG).d("[" + TAG + "]" + logText);
    }

    public static void i(String TAG, String logText) {
        Logger.t(DEFAULT_TAG).i("[" + TAG + "]" + logText);
    }

    public static void w(String TAG, String logText) {
        Logger.t(DEFAULT_TAG).w("[" + TAG + "]" + logText);
    }

    public static void w(String logText) {
        Logger.t(DEFAULT_TAG).w(String.valueOf(logText));
    }

    public static void e(String TAG, String logText) {
        Logger.t(DEFAULT_TAG).e("[" + TAG + "]" + logText);
    }

    public static void d(Class<?> c, String logText) {
        Logger.t(DEFAULT_TAG).d("[" + c.getSimpleName() + "]" + logText);
    }

    public static void d(Object c, String logText) {
        Logger.t(DEFAULT_TAG).d("[" + c.getClass().getSimpleName() + "]" + logText);
    }
}
