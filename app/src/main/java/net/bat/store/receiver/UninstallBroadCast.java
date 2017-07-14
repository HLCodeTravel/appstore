package net.bat.store.receiver;

/* Top Secret */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.bat.store.download.DownloadListener;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.download.InstallState;
import net.bat.store.utils.CommonUtils;

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/28
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class UninstallBroadCast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("broadcast", "receiver");
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.e("uninstall", packageName);
            DownloadTask task = DownloadTaskManager.getInstance(context).queryDownloadTask(packageName);
            //Log.e("broadcast",task.toString());
            if (task != null && task.getInstallState() == InstallState.INSTALLSUCCESS) {
                //if (!packageName.equals(CommonUtils.oldPackageName)) {
                    Log.e("uninstall", packageName);
//                    DownloadTaskManager.getInstance(context).deleteDownloadTask(task);
//                    DownloadTaskManager.getInstance(context).deleteDownloadTaskFile(task);
//
//                    if (CommonUtils.getDwonloadTask().indexOf(task) != -1) {
//                        CommonUtils.getDwonloadTask().remove(task);
//                    }
               // }
                task.setInstallState(InstallState.INSTALLFAILED);

                if (CommonUtils.getDwonloadTask() != null) {
                    if (CommonUtils.getDwonloadTask().indexOf(task) != -1) {
                        CommonUtils.getDwonloadTask().get(CommonUtils.getDwonloadTask().indexOf(task)).setInstallState(InstallState.INSTALLFAILED);
                    }
                }
                DownloadTaskManager.getInstance(context).updateDownloadTask(task);

            }

        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.e("install", packageName);
            DownloadTask task = DownloadTaskManager.getInstance(context).queryDownloadTask(packageName);

            if (task != null) {
                task.setInstallState(InstallState.INSTALLSUCCESS);
                DownloadTaskManager.getInstance(context).updateDownloadTask(task);
                for (DownloadListener l : DownloadTaskManager.getInstance(context).getListeners(task)) {
                    l.onInstallComplete();
                }
//                if (CommonUtils.oldPackageName.equals(task.getPackageName())){
//                    CommonUtils.oldPackageName = "";
//                }else {
//                    CommonUtils.oldPackageName = task.getPackageName();
//                }
            }

            //Log.e("set state",task.getFileName());
            Log.e("set state","1");
//            for (DownloadTask task1:CommonUtils.getDwonloadTask()){
//                Log.e("get name",task1.getFileName());
//            }
            if (CommonUtils.getDwonloadTask() != null && CommonUtils.getDwonloadTask().indexOf(task) != -1){
                Log.e("set state","2");
                CommonUtils.getDwonloadTask().get(CommonUtils.getDwonloadTask().indexOf(task)).setInstallState(InstallState.INSTALLSUCCESS);
            }
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.e("replace", packageName);
            DownloadTask task = DownloadTaskManager.getInstance(context).queryDownloadTask(packageName);
            if (task != null) {
                task.setInstallState(InstallState.INSTALLSUCCESS);
                DownloadTaskManager.getInstance(context).updateDownloadTask(task);
//            Toast.makeText(BaseApplication.getContext(), task.getFileName() + " " +BaseApplication.getContext().getResources().getString(R.string.install_complete),
//                    Toast.LENGTH_LONG).show();
                for (DownloadListener l : DownloadTaskManager.getInstance(context).getListeners(task)) {
                    l.onInstallComplete();
                }
            }

            if (CommonUtils.getDwonloadTask() != null && CommonUtils.getDwonloadTask().indexOf(task) != -1){
                CommonUtils.getDwonloadTask().get(CommonUtils.getDwonloadTask().indexOf(task)).setInstallState(InstallState.INSTALLSUCCESS);
            }
        }
    }
}
