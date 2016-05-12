package com.wiikzz.library.util;

import android.util.Log;

/**
 * Created by wiikii on 16/4/5.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class Logger {
    public static boolean DEBUG = false;
    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public static void v(String tag, String message) {
        if(DEBUG) {
            Log.v(tag, buildMessage(message));
        }
    }

    public static void d(String tag, String message) {
        if(DEBUG) {
            Log.d(tag, buildMessage(message));
        }
    }

    public static void i(String tag, String message) {
        if(DEBUG) {
            Log.i(tag, buildMessage(message));
        }
    }

    public static void w(String tag, String message) {
        if(DEBUG) {
            Log.w(tag, buildMessage(message));
        }
    }

    public static void e(String tag, String message) {
        if(DEBUG) {
            Log.e(tag, buildMessage(message));
        }
    }

    public static void e(String tag, String message, Throwable tr) {
        if(DEBUG) {
            Log.e(tag, buildMessage(message), tr);
        }
    }

    public static void wtf(String tag, String message) {
        if(DEBUG) {
            Log.wtf(tag, buildMessage(message));
        }
    }

    public static void wtf(String tag, String message, Throwable tr) {
        if(DEBUG) {
            Log.wtf(tag, buildMessage(message), tr);
        }
    }

    private static String buildMessage(String message) {
        return "[[" + message + "]]";
    }
}
