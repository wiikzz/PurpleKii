package com.wiikzz.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by wiikii on 16/4/5.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class SharedPrefUtil {
    private static final String SP_NAME = "shared_data";

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences sharedPreferences = getSharedPref(context);
        return sharedPreferences.edit();
    }

    private static void setValueBase(Context context, String key, Object value) {
        if(context == null || TextUtils.isEmpty(key) || value == null) {
            return;
        }

        try {
            SharedPreferences.Editor editor = getEditor(context);
            String type = value.getClass().getSimpleName();
            if ("String".equals(type)) {
                editor.putString(key, (String) value);
            } else if ("Integer".equals(type)) {
                editor.putInt(key, (Integer) value);
            } else if ("Boolean".equals(type)) {
                editor.putBoolean(key, (Boolean) value);
            } else if ("Float".equals(type)) {
                editor.putFloat(key, (Float) value);
            } else if ("Long".equals(type)) {
                editor.putLong(key, (Long) value);
            }
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getValueBase(Context context, String key, Object defaultValue) {
        if(context == null || TextUtils.isEmpty(key)) {
            return null;
        }

        try {
            SharedPreferences sharedPreferences = getSharedPref(context);
            String type = defaultValue.getClass().getSimpleName();
            if ("String".equals(type)) {
                return sharedPreferences.getString(key, (String) defaultValue);
            } else if ("Integer".equals(type)) {
                return sharedPreferences.getInt(key, (Integer) defaultValue);
            } else if ("Boolean".equals(type)) {
                return sharedPreferences.getBoolean(key, (Boolean) defaultValue);
            } else if ("Float".equals(type)) {
                return sharedPreferences.getFloat(key, (Float) defaultValue);
            } else if ("Long".equals(type)) {
                return sharedPreferences.getLong(key, (Long) defaultValue);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void setValue(Context context, String key, String value) {
        if(value == null) {
            return;
        }

        setValueBase(context, key, value);
    }
    public static void setValue(Context context, String key, int value) {
        setValueBase(context, key, value);
    }
    public static void setValue(Context context, String key, boolean value) {
        setValueBase(context, key, value);
    }
    public static void setValue(Context context, String key, float value) {
        setValueBase(context, key, value);
    }
    public static void setValue(Context context, String key, long value) {
        setValueBase(context, key, value);
    }

    public static String getValue(Context context, String key, String defaultValue) {
        if(defaultValue == null) {
            return null;
        }

        Object object = getValueBase(context, key, defaultValue);
        if(object != null) {
            return (String) object;
        }
        return defaultValue;
    }
    public static int getValue(Context context, String key, int defaultValue) {
        Object object = getValueBase(context, key, defaultValue);
        if(object != null) {
            return (int) object;
        }
        return defaultValue;
    }
    public static boolean getValue(Context context, String key, boolean defaultValue) {
        Object object = getValueBase(context, key, defaultValue);
        if(object != null) {
            return (boolean) object;
        }
        return defaultValue;
    }
    public static float getValue(Context context, String key, float defaultValue) {
        Object object = getValueBase(context, key, defaultValue);
        if(object != null) {
            return (float) object;
        }
        return defaultValue;
    }
    public static long getValue(Context context, String key, long defaultValue) {
        Object object = getValueBase(context, key, defaultValue);
        if(object != null) {
            return (long) object;
        }
        return defaultValue;
    }

}
