package net.bat.store.utils;

/* Top Secret */

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import net.bat.store.ux.download.DownloadInfo;
import net.bat.store.ux.download.DownloadManagerPro;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/1/18
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class DownloadUtils {

    static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");

    public static final int    MB_2_BYTE             = 1024 * 1024;
    public static final int    KB_2_BYTE             = 1024;

    private static final String DOWNLOAD_FOLDER_NAME = "AppStore";
    private static DownloadManager manager;
    private static DownloadManagerPro pro;

    private static DownloadUtils mInstance;
    public long speed;
    //    下载任务的id
    public long id;
    //    下载任务的状态
    public int status;
    //    下载的文件名
    public String fileName;
    //    下载的文件类型
    public String fileType;
    //    下载的文件总大小
    public long totalFileSize;
    //    已下载的字节数
    public long downloadedFileSize;
    //    在编辑模式下，是否被选中
    public boolean isSelected;
    //    最后修改日期
    public long date;
    //    下载文件的本地路径
    public String localUri;
    public int reason;
    public String uri;
    public boolean pausedOnce;
    public Drawable icon;

    @Override
    public String toString() {
        return "DownloadBean{" +
                "id=" + id +
                ", status=" + status +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", totalFileSize=" + totalFileSize +
                ", downloadedFileSize=" + downloadedFileSize +
                ", isSelected=" + isSelected +
                ", date=" + date +
                ", localUri='" + localUri + '\'' +
                ", reason=" + reason +
                ", uri='" + uri + '\'' +
                '}';
    }

    private DownloadUtils(Context context) {
        if (manager == null) {
            manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            pro = new DownloadManagerPro(manager);
        }
    }

    public static DownloadUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DownloadUtils.class) {
                if (mInstance == null) {
                    mInstance = new DownloadUtils(context);
                }
            }
        }
        return mInstance;
    }

    public long startDownload(String url, String fileName) {
        File folder = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, fileName + ".apk");
        request.setTitle(fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setMimeType("application/cn.trinea.download.file");

        long downloadId = manager.enqueue(request);

        return downloadId;
    }

    public int removeDownload(long downloadId){
        return manager.remove(downloadId);
    }

    public boolean isFileDownloading(String fileName) {
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = manager.query(query);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String downloadFileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                if (downloadFileName != null && downloadFileName.contains(fileName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getStatusById(long downloadId) {
        return pro.getStatusById(downloadId);
    }

    public List<DownloadInfo> getDownloadingInfo() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_RUNNING);
        List<DownloadInfo> infoList = new ArrayList<DownloadInfo>();
        Cursor cursor = manager.query(query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DownloadInfo info = new DownloadInfo();
                info.setFileName(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
                info.setCurrentSize(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
                info.setTotalSize(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
                info.setStatus(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)));

                infoList.add(info);
            }
        }

        return infoList;
    }

    public CharSequence getAppSize(long size) {
        if (size <= 0) {
            return "0M";
        }

        if (size >= MB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / MB_2_BYTE)).append("M");
        } else if (size >= KB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / KB_2_BYTE)).append("K");
        } else {
            return size + "B";
        }
    }
}
