package net.bat.store.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.bat.store.R;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.acticivty.H5Activity;
import net.bat.store.ux.acticivty.AhaMainActivity;

/**
 * usage
 *
 * @author 金新
 * @date 2017/2/14
 * =====================================
 * Copyright (c)2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private NotificationCompat.Builder mBuilder;
    private NotificationManager notificationManager;
    private String type;
    private String Notificationimage;
    private String Notificationtitle;
    private String NotificationUrl;
    private String NotificationdetailUrl;
    private String style;
    private String NotificationBanner;
    private String Notificationcontent;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    }
    @Override
    public void zzm(Intent intent) {
        initNotify();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent.getExtras() != null) {
            type = intent.getExtras().getString("Type");
            style = intent.getExtras().getString("Style");
            if ("Banner".equals(style)) {
                NotificationUrl = intent.getExtras().getString("NotificationUrl");
                NotificationBanner = intent.getExtras().getString("NotificationBanner");
                if (TextUtils.isEmpty(NotificationUrl) || TextUtils.isEmpty(NotificationBanner)) {
                    Bundle param1 = new Bundle();
                    param1.putString("Action", "ErrorAction");
                    FirebaseStat.logEvent("Error_Notification", param1);
                    return;
                }
                if ("url".equals(type)) {
                    mHandler.sendEmptyMessage(6);
                    Bundle params1 = new Bundle();
                    params1.putString("Action","send_notification");
                    FirebaseStat.logEvent("send_banner_url_notification", params1);

                } else if ("topic".equals(type)) {
                    mHandler.sendEmptyMessage(5);
                    Bundle params1 = new Bundle();
                    params1.putString("Action","send_notification");
                    FirebaseStat.logEvent("send_banner_topic_notification", params1);
                } else if("detail".equals(type)){
                    if(!NotificationUrl.contains("leo.api.appDetail")){
                        Bundle param1 = new Bundle();
                        param1.putString("Action", "ErrorAction");
                        FirebaseStat.logEvent("Banner_Detail_Error", param1);
                    }
                    mHandler.sendEmptyMessage(4);
                    Bundle params1 = new Bundle();
                    params1.putString("Action","send_notification");
                    FirebaseStat.logEvent("send_banner_detail_notification", params1);
                }else{
                    Bundle param1 = new Bundle();
                    param1.putString("Action", "ErrorAction");
                    FirebaseStat.logEvent("Error_Notification", param1);
                }
            } else if("Normal".equals(style)){
                Notificationimage = intent.getExtras().getString("NotificationImage");
                Notificationtitle = intent.getExtras().getString("Notificationtitle");
                Notificationcontent = intent.getExtras().getString("Notificationcontent");
                if ("url".equals(type)) {
                    NotificationUrl = intent.getExtras().getString("NotificationUrl");
                    if (TextUtils.isEmpty(Notificationimage) || TextUtils.isEmpty(Notificationtitle) || TextUtils.isEmpty(Notificationcontent) || TextUtils.isEmpty(NotificationUrl)) {
                        Bundle param1 = new Bundle();
                        param1.putString("Action", "ErrorAction");
                        FirebaseStat.logEvent("Error_Notification", param1);
                        return;
                    }
                   mHandler.sendEmptyMessage(3);
                } else if ("topic".equals(type)) {
                    NotificationUrl = intent.getExtras().getString("NotificationUrl");
                    if (TextUtils.isEmpty(Notificationimage) || TextUtils.isEmpty(Notificationtitle) || TextUtils.isEmpty(Notificationcontent) || TextUtils.isEmpty(NotificationUrl)) {
                        Bundle param1 = new Bundle();
                        param1.putString("Action", "ErrorAction");
                        FirebaseStat.logEvent("Error_Notification", param1);
                        return;
                    }
                   mHandler.sendEmptyMessage(2);
                } else if("detail".equals(type)){
                    NotificationdetailUrl = intent.getExtras().getString("NotificationdetailUrl");
                    if (TextUtils.isEmpty(Notificationimage) || TextUtils.isEmpty(Notificationtitle) || TextUtils.isEmpty(Notificationcontent) || TextUtils.isEmpty(NotificationdetailUrl)) {
                        Bundle param1 = new Bundle();
                        param1.putString("Action", "ErrorAction");
                        FirebaseStat.logEvent("Error_Notification", param1);
                        return;
                    }
                    if(!NotificationdetailUrl.contains("leo.api.appDetail")){
                        Bundle param1 = new Bundle();
                        param1.putString("Action", "ErrorAction");
                        FirebaseStat.logEvent("Normal_Detail_Error", param1);
                    }
                    mHandler.sendEmptyMessage(1);

                }else{
                    Bundle param1 = new Bundle();
                    param1.putString("Action", "ErrorAction");
                    FirebaseStat.logEvent("Error_Notification", param1);
                }

            }else{
                Bundle param1 = new Bundle();
                param1.putString("Action", "ErrorAction");
                FirebaseStat.logEvent("Error_Notification", param1);
            }

        }
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    sendAppNotification(Notificationimage, Notificationtitle, Notificationcontent, NotificationdetailUrl);
                    Bundle params1 = new Bundle();
				    params1.putString("Action","send_notification");
				    FirebaseStat.logEvent("send_normal_detail_notification", params1);
                    break;
                case 2:
                    sendTopicNotification(Notificationimage, Notificationtitle, Notificationcontent, NotificationUrl);
                    Bundle params2 = new Bundle();
                    params2.putString("Action","send_notification");
                    FirebaseStat.logEvent("send_normal_topic_notification", params2);
                    break;
                case 3:
                    sendUrlNotification(Notificationimage, Notificationtitle, Notificationcontent, NotificationUrl);
                    Bundle params3 = new Bundle();
                    params3.putString("Action","send_notification");
                    FirebaseStat.logEvent("send_normal_url_notification", params3);
                    break;
                case 4:
                    sendBannerdetail(NotificationBanner, NotificationUrl);
                    break;
                case 5:
                    sendBannertopic(NotificationBanner, NotificationUrl);
                    break;
                case 6:
                    sendBannerurl(NotificationBanner, NotificationUrl);
                    break;
            }
        }
    };

    private void initNotify() {
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_VIBRATE);
    }

    private void sendAppNotification(final String notificationimage, final String notificationtitle, final String notificationcontent, String  NotificationdetailUrl ) {
        Intent intent = new Intent(this,AhaMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("notificationurl",NotificationdetailUrl);
        bundle.putString("style","Normal_activity_notification");
        intent.putExtras(bundle);
        final PendingIntent pending = PendingIntent.getActivity(this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Glide.with(getApplicationContext()).load(notificationimage).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Log.i(TAG, "onResourceReady: ");
                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
                style.bigText(notificationcontent);
                style.setBigContentTitle(notificationtitle);
                Notification notification = mBuilder
                        .setContentTitle(notificationtitle)
                        .setStyle(style)
                        .setContentText(notificationcontent)
                        .setTicker(notificationtitle)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.mipmap.firebase_icon)
                        .setLargeIcon(resource)
                        .setContentIntent(pending)
                        .build();
                notificationManager.notify(1, notification);
                UploadLogManager.getInstance().generateResourceLog(getApplicationContext(),5,notificationtitle,0,1,"","");
            }
        });
    }

    private void sendTopicNotification(final String notificationimage, final String notificationtitle, final String notificationcontent, String notificationUrl) {
        Intent intent = new Intent(this,H5Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Url",notificationUrl);
        bundle.putString("style","Normal_topic_notification");
        bundle.putString("title",notificationtitle);
        intent.putExtras(bundle);
        final PendingIntent pending = PendingIntent.getActivity(this,2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Glide.with(getApplicationContext()).load(notificationimage).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Log.i(TAG, "onResourceReady: ");
                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
                style.bigText(notificationcontent);
                style.setBigContentTitle(notificationtitle);
                Notification notification = mBuilder
                        .setContentTitle(notificationtitle)
                        .setStyle(style)
                        .setContentText(notificationcontent)
                        .setTicker(notificationtitle)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.mipmap.firebase_icon)
                        .setLargeIcon(resource)
                        .setContentIntent(pending)
                        .build();
                notificationManager.notify(1, notification);
                UploadLogManager.getInstance().generateResourceLog(getApplicationContext(),5,notificationtitle,0,1,"","");
            }
        });
    }

    private void sendUrlNotification(final String notificationimage, final String notificationtitle, final String notificationcontent, String notificationUrl) {
        Uri uri = Uri.parse(notificationUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        Bundle param1 = new Bundle();
        param1.putString("Action", "UrlAction");
        FirebaseStat.logEvent("Normal_url_notification", param1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent pending = PendingIntent.getActivity(this,3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Glide.with(getApplicationContext()).load(notificationimage).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Log.i(TAG, "onResourceReady: ");
                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
                style.bigText(notificationcontent);
                style.setBigContentTitle(notificationtitle);
                Notification notification = mBuilder
                        .setContentTitle(notificationtitle)
                        .setStyle(style)
                        .setContentText(notificationcontent)
                        .setTicker(notificationtitle)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.mipmap.firebase_icon)
                        .setLargeIcon(resource)
                        .setContentIntent(pending)
                        .build();
                notificationManager.notify(1, notification);
                UploadLogManager.getInstance().generateResourceLog(getApplicationContext(),5,notificationtitle,0,1,"","");
            }
        });
    }

    private void sendBannerdetail(final String notificationBanner, String notificationUrl) {
        Log.i(TAG, "sendBannerdetail: ");
        Intent intent = new Intent(this,AhaMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("notificationurl",notificationUrl);
        bundle.putString("style","Banner_activity_notification");
        intent.putExtras(bundle);
        final PendingIntent pending = PendingIntent.getActivity(this,4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notification_banner);

        Glide.with(getApplicationContext()).load(notificationBanner).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                view_custom.setImageViewBitmap(R.id.image_banner,resource);
                mBuilder.setContent(view_custom)
                        .setSmallIcon(R.mipmap.firebase_icon)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pending);
                Notification notify = mBuilder.build();
                if (Build.VERSION.SDK_INT >= 16) {
                    notify.bigContentView =view_custom;
                }
                notify.contentView = view_custom;
                notificationManager.notify(4, notify);
                UploadLogManager.getInstance().generateResourceLog(getApplicationContext(),5,"",0,1,"","");
            }
        });
    }

    private void sendBannertopic(final String notificationBanner, String notificationUrl) {
        Log.i(TAG, "sendBannertopic: ");
        Intent intent = new Intent(this, H5Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Url",notificationUrl);
        bundle.putString("style","Banner_topic_notification");
        intent.putExtras(bundle);
        final PendingIntent pending = PendingIntent.getActivity(this,5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notification_banner);

        Glide.with(getApplicationContext()).load(notificationBanner).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                view_custom.setImageViewBitmap(R.id.image_banner,resource);
                mBuilder.setContent(view_custom)
                        .setSmallIcon(R.mipmap.firebase_icon)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pending);
                Notification notify = mBuilder.build();
                if (Build.VERSION.SDK_INT >= 16) {
                    notify.bigContentView =view_custom;
                }
                notify.contentView = view_custom;
                notificationManager.notify(4, notify);
                UploadLogManager.getInstance().generateResourceLog(getApplicationContext(),5,"",0,1,"","");
            }
        });
    }


    private void sendBannerurl(final String notificationBanner, String notificationUrl) {
        Log.i(TAG, "sendBannerurl: ");
        Uri uri = Uri.parse(notificationUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle param1 = new Bundle();
        param1.putString("Action", "UrlAction");
        FirebaseStat.logEvent("Banner_url_notification", param1);
        final PendingIntent pending = PendingIntent.getActivity(this,6, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notification_banner);

        Glide.with(getApplicationContext()).load(notificationBanner).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                view_custom.setImageViewBitmap(R.id.image_banner,resource);
                mBuilder.setContent(view_custom)
                        .setSmallIcon(R.mipmap.firebase_icon)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pending);
                Notification notify = mBuilder.build();
                if (Build.VERSION.SDK_INT >= 16) {
                    notify.bigContentView =view_custom;
                }
                notify.contentView = view_custom;
                notificationManager.notify(4, notify);
                UploadLogManager.getInstance().generateResourceLog(getApplicationContext(),5,"",0,1,"","");
            }
        });
    }
}
