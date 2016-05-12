package com.wiikzz.ikz.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.wiikzz.ikz.R;


/**
 * Created by wiikii on 16/4/8.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class NotificationUtil {

    public static String NF_KEY_TITLE = "title";
    public static String NF_KEY_TEXT = "text";
    public static String NF_KEY_SUBTEXT = "subText";
    public static String NF_KEY_EXPAND = "expand";

    public static Bundle createNotificationBundle(String title, String text, String subText) {
        return createNotificationBundle(title, text, subText, false);
    }

    public static Bundle createNotificationBundle(String title, String text, String subText, boolean expand) {
        Bundle bundle = new Bundle();
        if(!TextUtils.isEmpty(title)) {
            bundle.putString(NF_KEY_TITLE, title);
        }
        if(!TextUtils.isEmpty(text)) {
            bundle.putString(NF_KEY_TEXT, text);
        }
        if(!TextUtils.isEmpty(subText)) {
            bundle.putString(NF_KEY_SUBTEXT, subText);
        }

        bundle.putBoolean(NF_KEY_EXPAND, expand);

        return bundle;
    }


    /**
     * Send a sample notification using the NotificationCompat API.
     * @param context
     * @param notificationId A numeric value that identifies the notification that we'll be sending.
     *                       This value needs to be unique within this app, but it doesn't need to be
     *                       unique system-wide.
     * @param pendingIntent  eg:
     *                       Intent intent = new Intent(Intent.ACTION_VIEW,
     *                       Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
     *                       PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
     */
    public static void sendBasicNotification(Context context, int notificationId, PendingIntent pendingIntent, Bundle notificationData, boolean headsUp) {
        if(context == null || notificationData == null) {
            return;
        }

        String notificationTitle = notificationData.getString(NF_KEY_TITLE);
        String notificationText = notificationData.getString(NF_KEY_TEXT);
        String notificationSubText = notificationData.getString(NF_KEY_SUBTEXT);

        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        /** Set the icon that will appear in the notification bar. This icon also appears
         * in the lower right hand corner of the notification itself.
         *
         * Important note: although you can use any drawable as the small icon, Android
         * design guidelines state that the icon should be simple and monochrome. Full-color
         * bitmaps or busy images don't render well on smaller screens and can end up
         * confusing the user.
         */
        builder.setSmallIcon(R.mipmap.ic_notification);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        if (headsUp) {
            // displaying Heads-Up Notifications
            builder.setFullScreenIntent(pendingIntent, true);
        }

        // Set the notification to auto-cancel. This means that the notification will disappear
        // after the user taps it, rather than remaining until it's explicitly dismissed.
        builder.setAutoCancel(true);

        /**
         * Build the notification's appearance.
         * Set the large icon, which appears on the left of the notification. In this
         * sample we'll set the large icon to be the same as our app icon. The app icon is a
         * reasonable default if you don't have anything more compelling to use as an icon.
         */
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        /**
         * Set the text of the notification. This sample sets the three most commononly used
         * text areas:
         * 1. The content title, which appears in large type at the top of the notification
         * 2. The content text, which appears in smaller text below the title
         * 3. The subtext, which appears under the text on newer devices. Devices running
         *    versions of Android prior to 4.2 will ignore this field, so don't use it for
         *    anything vital!
         */
        if(!TextUtils.isEmpty(notificationTitle)) {
            builder.setContentTitle(notificationTitle);
        }
        if(!TextUtils.isEmpty(notificationText)) {
            builder.setContentText(notificationText);
        }
        if(!TextUtils.isEmpty(notificationSubText)) {
            builder.setSubText(notificationSubText);
        }

        /**
         * Set the default sound, vibrate and lights.
         */
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);

        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }


    /**
     * This sample demonstrates notifications with custom content views.
     *
     * <p>On API level 16 and above a big content view is also defined that is used for the
     * 'expanded' notification. The notification is created by the NotificationCompat.Builder.
     * The expanded content view is set directly on the {@link Notification} once it has been build.
     * (See {@link Notification#bigContentView}.) </p>
     *
     * <p>The content views are inflated as {@link RemoteViews} directly from their XML layout
     * definitions using {@link RemoteViews#RemoteViews(String, int)}.</p>
     */
    public static void sendCustomNotification(Context context, int notificationId, PendingIntent pendingIntent, Bundle notificationData, boolean headsUp) {
        if(context == null || notificationData == null) {
            return;
        }

        String notificationTitle = notificationData.getString(NF_KEY_TITLE);
        String notificationText = notificationData.getString(NF_KEY_TEXT);
        String notificationSubText = notificationData.getString(NF_KEY_SUBTEXT);
        boolean notificationExpand = notificationData.getBoolean(NF_KEY_EXPAND);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentIntent(pendingIntent);

        if (headsUp) {
            // displaying Heads-Up Notifications
            builder.setFullScreenIntent(pendingIntent, true);
        }

        // Sets the ticker text
        builder.setTicker(notificationTitle);

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.mipmap.ic_notification);

        // Cancel the notification when clicked
        builder.setAutoCancel(true);

        // Build the notification
        Notification notification = builder.build();

        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);

        // TODO do something for the contentView
        // Set text on a TextView in the RemoteViews programmatically.
//        contentView.setTextViewText(R.id.textView, notificationText);

        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */
        notification.contentView = contentView;

        // Add a big content view to the notification if supported.
        // Support for expanded notifications was added in API level 16.
        // (The normal contentView is shown when the notification is collapsed, when expanded the
        // big content view set here is displayed.)
        if (notificationExpand && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Inflate and set the layout for the expanded notification view
            RemoteViews expandedView =
                    new RemoteViews(context.getPackageName(), R.layout.notification_expanded);

            // TODO do something for the expandedView
            // ...

            notification.bigContentView = expandedView;
        }

        /**
         * Set the default sound, vibrate and lights.
         */
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);

        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationId, notification);
    }

}
