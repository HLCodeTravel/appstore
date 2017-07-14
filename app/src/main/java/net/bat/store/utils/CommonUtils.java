package net.bat.store.utils;

/* Top Secret */

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.update.UpdateAppInfo;
import net.bat.store.download.DownloadNotificationListener;
import net.bat.store.download.DownloadOpenFile;
import net.bat.store.download.DownloadState;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.download.InstallState;
import net.bat.store.ux.acticivty.DownloadListActivity;
import net.bat.store.ux.adapter.viewholder.MainListHolder;
import net.bat.store.ux.adapter.viewholder.MainViewHolder;
import net.bat.store.ux.adapter.viewholder.SinglerGirdViewHolder;
import net.bat.store.bean.AppInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.bat.store.download.DownloadState.DOWNLOADING;
import static net.bat.store.download.DownloadState.FAILED;
import static net.bat.store.download.DownloadState.FINISHED;
import static net.bat.store.download.DownloadState.PAUSE;

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/13
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class CommonUtils {
    private static String TAG = "CommonUtils";

    public static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");
    public static final int MB_2_BYTE = 1024 * 1024;
    public static final int KB_2_BYTE = 1024;

    private static List<DownloadTask> mDwonloadTask  = DownloadTaskManager.getInstance(BaseApplication.getContext()).getDownloadTask();
    public static String oldPackageName = "";

    public static List<AppInfo> queryAppInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        List<AppInfo> appInfoList = new ArrayList<>();

        List<ApplicationInfo> applicationInfoList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);

        for (ApplicationInfo applicationInfo : applicationInfoList) {
            //获取非系统应用
            if ((applicationInfo.FLAG_SYSTEM & applicationInfo.flags) <= 0
                    && !applicationInfo.loadLabel(pm).toString().equals(context.getPackageName())) {
                String appLabel = applicationInfo.loadLabel(pm).toString();
                Drawable appIcon = applicationInfo.loadIcon(pm);
                String dir = applicationInfo.publicSourceDir;
                String appSize = getAppSize(dir);
                String packageName = applicationInfo.packageName;
                Date date = null;
                try {
                    date = new Date(pm.getPackageInfo(applicationInfo.packageName, 0).firstInstallTime);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                AppInfo appInfo = new AppInfo();
                appInfo.setAppIcon(appIcon);
                appInfo.setAppLabel(appLabel);
                appInfo.setAppSize(appSize);
                appInfo.setPackageName(packageName);
                if (date != null) {
                    appInfo.setInstallTime(date.toString());
                } else {
                    appInfo.setInstallTime("未知");
                }
                appInfoList.add(appInfo);
            }
        }

        return appInfoList;
    }

    public static String getPackageNameByUrl(String url) {
        if (url == null) {
            return null;
        }
        String[] ulrSpilt = url.split("=");
        return ulrSpilt[1].split("&")[0];
    }

    public static List<UpdateAppInfo> parseUpdateInfo(String result) {
        try {
            List<UpdateAppInfo> appInfoList = new ArrayList<>();
            JSONObject object = new JSONObject(result);
            if (object.get("desc").equals("success")) {
                JSONArray array = object.getJSONObject("resultMap").getJSONArray("gpAppDataList");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object2 = (JSONObject) array.opt(i);
                    UpdateAppInfo appInfo = new UpdateAppInfo();
                    if (object2.getString("name") != null) {
                        appInfo.setName(object2.getString("name"));
                        appInfo.setRecentChange(object2.getString("recentChange"));
                        appInfo.setUrl(object2.getString("url"));
                        appInfo.setVersion(object2.getString("version"));
                        appInfoList.add(appInfo);
                    }
                }

                return appInfoList;
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getAppSize(String dir) {
        File tempFile = new File(dir);
        long size = -1;
        if (tempFile.exists()) {
            size = tempFile.length();
        }
        if (size <= 0) {
            return "0M".toString();
        }

        if (size >= MB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE)).append("M").toString();
        } else if (size >= KB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double) size / KB_2_BYTE)).append("K").toString();
        } else {
            return (size + "B").toString();
        }
    }

    public static void refreshDownloadView(DownloadTaskManager manager,DownloadTask task, Button downloadBtn, ProgressBar
            progressBar) {
        if (FINISHED==task.getDownloadState()) {
            if (task.getInstallState() == InstallState.INSTALLSUCCESS){
                Log.i(TAG, "refreshDownloadView: INSTALLSUCCESS");
                downloadBtn.setText(R.string.appcenter_open_text);
                UploadLogManager.getInstance().generateResourceLog(BaseApplication.getContext(),4,task.getAppNameEn(),task.getAppAttribute(),2,"","");
            }else if (task.getInstallState() == InstallState.INSTALLFAILED){
                if(new File(task.getFilePath(),task.getFileName()).exists()){
                    downloadBtn.setText(R.string.install_app);
                }else{
                    downloadBtn.setText(R.string.appcenter_download_text);
                    task.setDownloadState(null);
                    manager.deleteDownloadTask(task);
                    CommonUtils.getDwonloadTask().remove(task);
                }
            }
        }
    }

    public static void downloadBtnClick(DownloadTaskManager manager, DownloadTask task, RecyclerView.ViewHolder viewHolder) {
        Log.i(TAG, "downloadBtnClick: "+task.getDownloadState()+"  "+task.getInstallState());
        boolean flag = false;
        for (int i=0;i<mDwonloadTask.size();i++){
            if ((task.getFileName().equals(mDwonloadTask.get(i).getFileName()))){
                flag = true;
                if(PAUSE==task.getDownloadState()||DOWNLOADING==task.getDownloadState()||FAILED==task.getDownloadState()){
                    DownloadTaskManager.getInstance(BaseApplication.getContext()).continueDownload(task);
                    Intent intent = new Intent(BaseApplication.getContext(), DownloadListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BaseApplication.getContext().startActivity(intent);
                    return;
                }
            }
        }
        if (flag) {
            if (task.getInstallState() == InstallState.INSTALLSUCCESS) {
                try {
                    PackageInfo info = BaseApplication.getContext().getPackageManager().getPackageInfo(task.getPackageName(),PackageManager.GET_ACTIVITIES);
                }catch (PackageManager.NameNotFoundException e){
                    task.setInstallState(InstallState.INSTALLFAILED);
                    manager.updateDownloadTask(task);
                    Intent installIntent = DownloadOpenFile.getApkFileIntent(task.getFilePath() + "/" + task.getFileName());
                    BaseApplication.getContext().startActivity(installIntent);
                    return;
                }
                Intent resolveIntent = BaseApplication.getContext().getPackageManager().getLaunchIntentForPackage(task.getPackageName());
                BaseApplication.getContext().startActivity(resolveIntent);
            } else {
                if (task.getInstallState() == InstallState.INSTALLFAILED){
                    try {
                        PackageInfo info = BaseApplication.getContext().getPackageManager().getPackageInfo(task.getPackageName(),PackageManager.GET_ACTIVITIES);
                        task.setInstallState(InstallState.INSTALLSUCCESS);
                        manager.updateDownloadTask(task);
                        Intent resolveIntent = BaseApplication.getContext().getPackageManager().getLaunchIntentForPackage(task.getPackageName());
                        BaseApplication.getContext().startActivity(resolveIntent);
                        return;
                    }catch (PackageManager.NameNotFoundException e){
                        task.setInstallState(InstallState.INSTALLFAILED);
                        manager.updateDownloadTask(task);
                        Intent installIntent = DownloadOpenFile.getApkFileIntent(task.getFilePath() + "/" + task.getFileName());
                        BaseApplication.getContext().startActivity(installIntent);
                        return;
                    }
                }else {
                    Toast.makeText(BaseApplication.getContext(), R.string.task_downloading, Toast.LENGTH_LONG).show();
                }
            }
        }else {
            if (manager.getListeners(task).isEmpty()){
                manager.registerListener(task,new DownloadNotificationListener(BaseApplication.getContext(),task));
            }
                task.setDownloadState(DownloadState.WAITING);
                task.setInstallState(InstallState.INIT);
                // save to database if the download task is valid, and start download.
                if (!task.equals(manager.queryDownloadTask(task.getFileName()))) {
                    manager.insertDownloadTask(task);
                }
                mDwonloadTask.add(task);
                Log.e(TAG,""+mDwonloadTask.size());
                manager.updateDownloadTask(task);
                Log.e(TAG, task.getUrl());
                manager.startDownload(task);

                Intent intent = new Intent(BaseApplication.getContext(), DownloadListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                BaseApplication.getContext().startActivity(intent);
        }
        if (viewHolder instanceof MainViewHolder){
            refreshDownloadView(manager,task,((MainViewHolder) viewHolder).mDownloadButton,((MainViewHolder) viewHolder).pb_current_size);
        }else if (viewHolder instanceof MainListHolder){
            refreshDownloadView(manager,task,((MainListHolder) viewHolder).btn_download,((MainListHolder) viewHolder).progressBar);
        }else if (viewHolder instanceof SinglerGirdViewHolder){
            refreshDownloadView(manager,task,((SinglerGirdViewHolder) viewHolder).bn_free,((SinglerGirdViewHolder) viewHolder).progressBar);
        }
    }

    private static void setViewHolderView(Button button, ProgressBar progressBar) {
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.skin_download_item_file_size_color));
        progressBar.setVisibility(View.VISIBLE);
        button.setText(R.string.download_status_init);
    }

    public static boolean isFinishedDownload(List<DownloadTask> finishedDownloadTasks, String appName) {
        if (finishedDownloadTasks != null && !finishedDownloadTasks.isEmpty()) {
            for (int i = 0; i < finishedDownloadTasks.size(); i++) {
                if (finishedDownloadTasks.get(i).getFileName().equals(appName) &&
                        (finishedDownloadTasks.get(i).getInstallState() == InstallState.INSTALLING
                        || finishedDownloadTasks.get(i).getInstallState() == InstallState.INSTALLFAILED)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isInstalled(List<DownloadTask> installCompleteTasks, String appName) {
        if (installCompleteTasks != null && !installCompleteTasks.isEmpty()) {
            for (int i = 0; i < installCompleteTasks.size(); i++) {
                if (installCompleteTasks.get(i).getFileName().equals(appName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDownloading(List<DownloadTask> downloadingTasks, String appName) {
        if (downloadingTasks != null && !downloadingTasks.isEmpty()) {
            for (int i = 0; i < downloadingTasks.size(); i++) {
                if (downloadingTasks.get(i).getFileName().equals(appName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static List<DownloadTask> getDwonloadTask() {
        return mDwonloadTask;
    }

    public static void setDwonloadTask(List<DownloadTask> mDwonloadTasks) {
        Log.e(TAG,"tasklist1 "+mDwonloadTasks.size());
        mDwonloadTask.removeAll(mDwonloadTask);
        mDwonloadTask.addAll(mDwonloadTasks);
        Log.e(TAG,"tasklist2 "+mDwonloadTask.size());
    }
}
