package com.wiikzz.ikz.util;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.TimeZone;

/**
 * Created by wiikii on 2016-4-23.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class SystemUtils {
    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    public static DisplayMetrics getDisplayMetrics(Context context){
        return context.getResources().getDisplayMetrics();
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.heightPixels;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = getDisplayMetrics(context).density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = getDisplayMetrics(context).density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static boolean isSameDay(long millis, long anotherMillis) {
        long interval = Math.abs(millis - anotherMillis);
        long day = (millis + TimeZone.getDefault().getOffset(millis))/MILLIS_IN_DAY;
        long anotherDay = (anotherMillis + TimeZone.getDefault().getOffset(anotherMillis))/MILLIS_IN_DAY;
        return interval <= MILLIS_IN_DAY && day == anotherDay;
    }
}
