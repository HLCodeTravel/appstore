package net.bat.store.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.transsion.iad.core.TAdError;
import com.transsion.iad.core.TAdListener;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.bean.Ad;
import com.transsion.iad.core.bean.TAdNativeInfo;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.PreferencesUtils;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.acticivty.AdDetailActivity;

/**
 * usage
 *
 * @author 金新
 * @date 2017/2/28
 * =====================================
 * Copyright (c)2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */

public class AppCenterNotifyService extends Service {

    private static final String TAG = "AppCenterNotifyService";
    private static final String AD_SOLD_ID="145";
    private Context mContext;
    private NotificationManager mNotiManager;

    private NotificationCompat.Builder mBuilder;
    public static TAdNativeInfo mNativeInfo;
    public static TAdNative mAdNative;
    public static Bitmap mBitmap;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        if (!ConstantUtils.lagerCurrent1Day(this)) {
            Log.i(TAG, "onStartCommand: small one day");
            return START_STICKY;
        }
        if(ConstantUtils.checkNetworkState(mContext)){
            String currentTime=ConstantUtils.getCurrentData()+"";
            int request_admob_times=PreferencesUtils.getInt(mContext,currentTime,0);
            if(request_admob_times>AppStoreConstant.REQUEST_ADMOB_TIMES){
                return START_STICKY;
            }
            request_admob_times++;
            PreferencesUtils.putInt(mContext,currentTime,request_admob_times);
            requestAdNative(AD_SOLD_ID);
        }
        return START_STICKY;
    }

    private void requestAdNative(final String slodId) {
        Log.i(TAG, "requestAdNative: "+slodId);
        final TAdNative tAdNative = new TAdNative(BaseApplication.getContext(), slodId);
        tAdNative.setAdListener(new TAdListener() {
            @Override
            public void onAdLoaded(Ad ad) {
                if (ad != null && ad instanceof TAdNativeInfo) {
                    mNativeInfo = (TAdNativeInfo) ad;
                    Log.i(TAG, "title:" + mNativeInfo.getTitle());
                    mAdNative=tAdNative;
                    notifyAdMsg();
                }
            }
            @Override
            public void onError(TAdError adError) {
                Log.i(TAG, "adError:" + adError.getErrorMessage() + adError.getErrorCode());
            }
        });
        tAdNative.loadAd();
    }

    private void initNotify() {
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_VIBRATE);
    }

    private void notifyAdMsg(){
        Log.i(TAG, "notifyAdMsg: ");
        initNotify();
        String iconUrl=mNativeInfo.getImage().getIcon().getIconUrl();
        if(!TextUtils.isEmpty(iconUrl)){
            Glide.with(mContext).load(iconUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Log.i(TAG, "onResourceReady: ");
                    mBitmap=resource;
                    NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
                    style.bigText(mNativeInfo.getDescription());
                    style.setBigContentTitle(mNativeInfo.getTitle());
                    Notification notification = mBuilder
                            .setContentTitle(mNativeInfo.getTitle())
                            .setStyle(style)
                            .setContentText(mNativeInfo.getDescription())
                            .setTicker(mNativeInfo.getTitle())
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setSmallIcon(R.mipmap.ad_icon)
                            .setLargeIcon(resource)
                             .setContentIntent(getPendingIntent())
                            .build();
                    mNotiManager.notify(111, notification);
                    PreferencesUtils.putLong(mContext, AppStoreConstant.PRE_KEY_LAST_SHOW_AD_TIME,System.currentTimeMillis());
                    UploadLogManager.getInstance().generateResourceLog(mContext,5,mNativeInfo.getTitle(),mNativeInfo.getFlag(),3,"","");
                }
            });
        }
    }

    private PendingIntent getPendingIntent(){
        Intent intent = new Intent(this, AdDetailActivity.class);
        intent.putExtra("flag","true");
        return PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

//    private RequestDataCallBack<NotificationBean> requestNotificationCallBack = new RequestDataCallBack<NotificationBean>() {
//        @Override
//        public void onSuccess(NotificationBean result) {
//            super.onSuccess(result);
//            mBase = result;
//            PreferencesUtils.putLong(mContext,AppStoreConstant.PRE_KEY_LAST_SHOW_AD_TIME, System.currentTimeMillis());
//        }
//
//        @Override
//        public void onError(Throwable ex, boolean isOnCallback) {
//            super.onError(ex, isOnCallback);
//        }
//
//        @Override
//        public void onCancelled(Callback.CancelledException cex) {
//            super.onCancelled(cex);
//        }
//
//        @Override
//        public void onFinished() {
//            super.onFinished();
//           // sendNotificationMessage();
//        }
//    };


//    private void sendNotificationMessage() {
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        type = mBase.getResultMap().getMessageType();
//        style = mBase.getResultMap().getLinkType();
//        NotificationUrl = mBase.getResultMap().getLinkUrl();
//        if ("banner".equals(type)) {
//            NotificationBanner = mBase.getResultMap().getBannerUrl();
//            if ("link".equals(style)) {
//                sendBannerurl(NotificationBanner, NotificationUrl);
//            } else if ("webview".equals(style)) {
//                sendBannertopic(NotificationBanner, NotificationUrl);
//            } else {
//                sendBannerdetail(NotificationBanner, NotificationUrl);
//            }
//
//        } else {
//            Notificationimage = mBase.getResultMap().getIconUrl();
//            Notificationtitle = mBase.getResultMap().getTitle();
//            Notificationcontent = mBase.getResultMap().getDescription();
//            if ("url".equals(style)) {
//                sendUrlNotification(Notificationimage, Notificationtitle, Notificationcontent, NotificationUrl);
//            } else if ("webview".equals(style)) {
//                sendTopicNotification(Notificationimage, Notificationtitle, Notificationcontent, NotificationUrl);
//            } else {
//                sendAppNotification(Notificationimage, Notificationtitle, Notificationcontent, NotificationUrl);
//            }
//
//        }
//
//    }
//
//
//    private void sendAppNotification(final String notificationimage, final String notificationtitle, final String notificationcontent, String NotificationdetailUrl) {
//        Intent intent = new Intent(this, AhaMainActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("notificationurl", NotificationdetailUrl);
//        bundle.putString("style", "Normal_activity_notification");
//        intent.putExtras(bundle);
//        final PendingIntent pending = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        final RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notification_layout_one);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                b = getNetWorkBitmap(notificationimage);
//                view_custom.setImageViewBitmap(R.id.custom_icon, b);
//                view_custom.setTextViewText(R.id.tv_custom_title, notificationtitle);
//                view_custom.setTextViewText(R.id.tv_custom_content, notificationcontent);
//                mBuilder = new NotificationCompat.Builder(getApplicationContext());
//                mBuilder.setContent(view_custom)
//                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pending);
//                Notification notify = mBuilder.build();
//                notify.contentView = view_custom;
//                mNotiManager.notify((int) System.currentTimeMillis(), notify);
//
//            }
//        }).start();
//
//    }
//
//    private void sendTopicNotification(final String notificationimage, final String notificationtitle, final String notificationcontent, String notificationUrl) {
//        Intent intent = new Intent(this, NotificationWebView.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("Url", notificationUrl);
//        bundle.putString("style", "Normal_topic_notification");
//        intent.putExtras(bundle);
//        final PendingIntent pending = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        final RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notification_layout_one);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                b = getNetWorkBitmap(notificationimage);
//                view_custom.setImageViewBitmap(R.id.custom_icon, b);
//                view_custom.setTextViewText(R.id.tv_custom_title, notificationtitle);
//                view_custom.setTextViewText(R.id.tv_custom_content, notificationcontent);
//                mBuilder = new NotificationCompat.Builder(getApplicationContext());
//                mBuilder.setContent(view_custom)
//                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pending);
//                Notification notify = mBuilder.build();
//                notify.contentView = view_custom;
//                mNotiManager.notify((int) System.currentTimeMillis(), notify);
//            }
//        }).start();
//
//    }
//
//    private void sendUrlNotification(final String notificationimage, final String notificationtitle, final String notificationcontent, String notificationUrl) {
//        Uri uri = Uri.parse(notificationUrl);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(uri);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Bundle param1 = new Bundle();
//        param1.putString("Action", "UrlAction");
//        mFirebaseAnalytics.logEvent("Normal_url_notification", param1);
//        final PendingIntent pending = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        final RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notification_layout_one);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                b = getNetWorkBitmap(notificationimage);
//                view_custom.setImageViewBitmap(R.id.custom_icon, b);
//                view_custom.setTextViewText(R.id.tv_custom_title, notificationtitle);
//                view_custom.setTextViewText(R.id.tv_custom_content, notificationcontent);
//                mBuilder = new NotificationCompat.Builder(getApplicationContext());
//                mBuilder.setContent(view_custom)
//                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pending);
//                Notification notify = mBuilder.build();
//                notify.contentView = view_custom;
//                mNotiManager.notify((int) System.currentTimeMillis(), notify);
//            }
//        }).start();
//
//    }
//
//    private void sendBannerdetail(final String notificationBanner, String notificationUrl) {
//
//        Intent intent = new Intent(this, AhaMainActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("notificationurl", notificationUrl);
//        bundle.putString("style", "Banner_activity_notification");
//        intent.putExtras(bundle);
//        final PendingIntent pending = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        final RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notification_banner);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                b = getNetWorkBitmap(notificationBanner);
//                view_custom.setImageViewBitmap(R.id.image_banner, b);
//                mBuilder = new NotificationCompat.Builder(getApplicationContext());
//                mBuilder.setContent(view_custom)
//                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pending);
//                Notification notify = mBuilder.build();
//                notify.contentView = view_custom;
//                mNotiManager.notify((int) System.currentTimeMillis(), notify);
//
//            }
//        }).start();
//
//
//    }
//
//    private void sendBannertopic(final String notificationBanner, String notificationUrl) {
//
//        Intent intent = new Intent(this, NotificationWebView.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("Url", notificationUrl);
//        bundle.putString("style", "Banner_topic_notification");
//        intent.putExtras(bundle);
//        final PendingIntent pending = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        final RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notification_banner);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                b = getNetWorkBitmap(notificationBanner);
//                view_custom.setImageViewBitmap(R.id.image_banner, b);
//                mBuilder = new NotificationCompat.Builder(getApplicationContext());
//                mBuilder.setContent(view_custom)
//                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pending);
//                Notification notify = mBuilder.build();
//                if (Build.VERSION.SDK_INT >= 16) {
//                    notify.bigContentView = view_custom;
//                }
//                notify.contentView = view_custom;
//                mNotiManager.notify(11, notify);
//            }
//        }).start();
//
//
//    }
//
//    private void sendBannerurl(final String notificationBanner, String notificationUrl) {
//        Uri uri = Uri.parse(notificationUrl);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(uri);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Bundle param1 = new Bundle();
//        param1.putString("Action", "UrlAction");
//        mFirebaseAnalytics.logEvent("Banner_url_notification", param1);
//        final PendingIntent pending = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        final RemoteViews view_custom = new RemoteViews(getPackageName(), R.layout.notification_banner);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                b = getNetWorkBitmap(notificationBanner);
//                view_custom.setImageViewBitmap(R.id.image_banner, b);
//                mBuilder = new NotificationCompat.Builder(getApplicationContext());
//                mBuilder.setContent(view_custom)
//                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pending);
//                Notification notify = mBuilder.build();
//                if (Build.VERSION.SDK_INT >= 16) {
//                    notify.bigContentView = view_custom;
//                }
//                notify.contentView = view_custom;
//                mNotiManager.notify((int) System.currentTimeMillis(), notify);
//            }
//        }).start();
//    }

}
