package net.bat.store.download;

/* Top Secret */

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.transsion.api.TLog;
import com.transsion.http.RequestCall;
import com.transsion.http.impl.DownloadCallback;

import net.bat.store.R;

import java.io.File;

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/7/14
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class NotificationCallback extends DownloadCallback {

    private static final String TAG = "DownloadNotificationLis";
    private Context mContext;
    private int mId;
    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;
    private int mProgress = 0;
    private String mTitle;

    public NotificationCallback(Context context, RequestCall requestCall) {
        mContext = context;
        DownloadTask task = (DownloadTask) requestCall.getRequest().getTag();

        mId = task.getFileName().hashCode();
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = null;
        mTitle = task.getTitle();
        Log.i(TAG, "DownloadNotificationListener: ");
    }

    @Override
    public void onStart() {
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new Notification.Builder(mContext);
            if (mNotificationBuilder == null) {
                return;
            }
            mNotificationBuilder.setAutoCancel(true).setOngoing(false)
                    .setSmallIcon(android.R.drawable.stat_sys_download).setWhen(System.currentTimeMillis());
        }
        Notification no = mNotificationBuilder
                .setContentTitle(mTitle)
                .setContentText(mContext.getString(Res.getInstance(mContext).getString("download_status_downloading")))
                .setSmallIcon(android.R.drawable.stat_sys_download).build();
        mNotificationManager.notify(mId, no);
    }

    @Override
    public void onFailure(String s, String s1) {
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new Notification.Builder(mContext);
            if (mNotificationBuilder == null) {
                return;
            }
            mNotificationBuilder.setAutoCancel(true).setOngoing(false)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done).setWhen(System.currentTimeMillis());
        }

        Notification no = mNotificationBuilder
                .setContentTitle(mContext.getString(Res.getInstance(mContext).getString("download_error")))
                .setSmallIcon(android.R.drawable.stat_sys_download_done).build();
        mNotificationManager.notify(mId, no);
        mNotificationManager.cancel(mId);
    }

    @Override
    public void onSuccess(String s, File file) {
        Log.i(TAG, "onDownloadFinish: ");
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new Notification.Builder(mContext);
            if (mNotificationBuilder == null) {
                return;
            }
            mNotificationBuilder.setAutoCancel(true).setOngoing(true)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done).setWhen(System.currentTimeMillis());
        }

        Notification no = mNotificationBuilder
                .setContentTitle(mTitle)
                .setContentText(mContext.getString(R.string.download_complete))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setOngoing(true)
                .setProgress(100, 100, false).setContentInfo("").build();
        no.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(mId, no);
    }

    @Override
    public void onLoading(String s, long l, long l1) {
        TLog.i("notification onDownloadProgress, id=" + mId);
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new Notification.Builder(mContext);
            if (mNotificationBuilder == null) {
                return;
            }
            mNotificationBuilder.setAutoCancel(false).setOngoing(false)
                    .setSmallIcon(android.R.drawable.stat_sys_download).setWhen(System.currentTimeMillis());
        }

        Notification no = mNotificationBuilder
                .setContentTitle(mTitle)
                .setContentText(mContext.getString(Res.getInstance(mContext).getString("download_status_downloading")))
                .setSmallIcon(android.R.drawable.stat_sys_download).build();
        no.flags = Notification.FLAG_NO_CLEAR;
        int percent = (int) l * 100 / (int) l1;
        mProgress = percent;
        String percentStr = "" + percent + "%";
        mNotificationBuilder.setProgress(100, percent, false).setContentInfo(percentStr);
        mNotificationManager.notify(mId, no);
    }

    @Override
    public void onPause() {
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new Notification.Builder(mContext);
            if (mNotificationBuilder == null) {
                return;
            }
            mNotificationBuilder.setAutoCancel(false).setOngoing(false)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done).setWhen(System.currentTimeMillis());
        }

        Notification no = mNotificationBuilder
                .setContentTitle(mTitle)
                .setContentText(mContext.getString(Res.getInstance(mContext)
                        .getString("download_status_pause")))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setProgress(100, mProgress, false)
                .build();
        no.flags = Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(mId, no);
    }

    @Override
    public void onCancel() {
        if (mNotificationBuilder == null) {
            mNotificationBuilder = new Notification.Builder(mContext);
            if (mNotificationBuilder == null) {
                return;
            }
            mNotificationBuilder.setAutoCancel(true).setOngoing(false)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done).setWhen(System.currentTimeMillis());
        }
        Notification no = mNotificationBuilder
                .setContentTitle(mContext.getString(Res.getInstance(mContext)
                        .getString("download_status_pause"))).setSmallIcon(android.R.drawable.stat_sys_download_done).build();
        mNotificationManager.notify(mId, no);
        mNotificationManager.cancel(mId);
    }
}
