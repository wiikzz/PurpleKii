package com.wiikzz.ikz.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

/**
 * Created by wiikii on 2016-4-23.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class PlatformUtils {
    // 工具类

    /**

     * Judge the network is available.

     * @param context

     * @return boolean

     */
    public static boolean networkDetect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

    /**

     * get the current available network's type

     * @param context

     * @return -1 if the network is unavailable, or return other types defined by ConnectivityManager.

     */
    public static int getCurNetworkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null) {
            return -1;
        }

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isAvailable()) {
            return -1;
        }

        return networkInfo.getType();
    }

    /**

     * Judge the current network is WIFI.

     * @param context

     * @return boolean

     */
    public static boolean isCurrentUseWifi(Context context) {
        int type = getCurNetworkType(context);
        if(type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }

        return false;
    }

    /**

     * Judge the current network is MOBILE.

     * @param context

     * @return boolean

     */
    public static boolean isCurrentUseMobile(Context context) {
        int type = getCurNetworkType(context);
        if(type == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }

        return false;
    }


    /**

     * Judge the service is running.

     * @param context

     * @param className must full name, include the package name.

     * @return  true if the service is running, otherwise false.

     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(40);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }
}
