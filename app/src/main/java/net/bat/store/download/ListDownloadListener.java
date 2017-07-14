package net.bat.store.download;

/* Top Secret */

import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import net.bat.store.ux.adapter.viewholder.MainListHolder;
import net.bat.store.ux.adapter.viewholder.MainViewHolder;
import net.bat.store.ux.adapter.viewholder.SinglerGirdViewHolder;

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/9
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class ListDownloadListener implements DownloadListener {
    private DownloadTask task;
    private DownloadHanlder hanlder;
    private RecyclerView recyclerView;
    private int position;

    private int mProgress = 0;

    public ListDownloadListener(RecyclerView recyclerView, DownloadTask task,int position) {
        this.recyclerView =recyclerView;
        this.task = task;
        this.position = position;
        hanlder = new DownloadHanlder();
    }

    @Override
    public void onDownloadInit() {
        //task.setDownloadState(DownloadState.WAITING);
        Message msg = new Message();
        msg.what = DownloadState.WAITING.ordinal();
        msg.arg1 = (int) task.getTotalSize();
        msg.arg2 = (int) task.getFinishedSize();
        Log.e("position ", "" + position);
        //Log.e("holder:",getViewHolderByPosition().getClass().toString());
        msg.obj = getViewHolderByPosition();
        hanlder.sendMessage(msg);
    }

    @Override
    public void onDownloadFinish(String filepath) {
        //task.setDownloadState(DownloadState.FINISHED);
        Message msg = new Message();
        msg.what = DownloadState.FINISHED.ordinal();
        msg.obj = getViewHolderByPosition();
        hanlder.sendMessage(msg);
        Log.e("ListDownloadListener","install");
        //DownloadOpenFile.installAPK(filepath);
    }

    @Override
    public void onDownloadStart() {
        //task.setDownloadState(DownloadState.DOWNLOADING);
        Message msg = new Message();
        msg.what = DownloadState.DOWNLOADING.ordinal();
        msg.obj = getViewHolderByPosition();
        hanlder.sendMessage(msg);
    }

    @Override
    public void onDownloadPause() {
        //task.setDownloadState(DownloadState.PAUSE);
        Message msg = new Message();
        msg.what = DownloadState.PAUSE.ordinal();
        msg.obj = getViewHolderByPosition();
        if (msg.obj == null){
            Log.e("null", "null");
        }
        hanlder.sendMessage(msg);
    }

    @Override
    public void onDownloadStop() {
        //task.setDownloadState(DownloadState.STOP);
        Message msg = new Message();
        msg.what = DownloadState.STOP.ordinal();
        msg.obj = getViewHolderByPosition();
        hanlder.sendMessage(msg);
    }

    @Override
    public void onDownloadFail() {
        //task.setDownloadState(DownloadState.FAILED);
        Message msg = new Message();
        msg.what = DownloadState.FAILED.ordinal();
        msg.obj = getViewHolderByPosition();
        hanlder.sendMessage(msg);

    }

    @Override
    public void onDownloadProgress(long finishedSize, long totalSize, int speed) {
        RecyclerView.ViewHolder holder = getViewHolderByPosition();
        //task.setDownloadState(DownloadState.DOWNLOADING);
        if (holder instanceof MainViewHolder) {
            setProgress(((MainViewHolder) holder).mDownloadButton,((MainViewHolder) holder).pb_current_size,finishedSize,totalSize);
        }else if (holder instanceof MainListHolder){
            setProgress(((MainListHolder) holder).btn_download,((MainListHolder) holder).progressBar,finishedSize,totalSize);
        }else if (holder instanceof SinglerGirdViewHolder){
            setProgress(((SinglerGirdViewHolder) holder).bn_free,((SinglerGirdViewHolder) holder).progressBar,finishedSize,totalSize);
        }
    }

    @Override
    public void onInstallComplete() {
        //task.setInstallState(InstallState.INSTALLSUCCESS);
        Message msg = new Message();
        msg.what = 6;
        msg.obj = getViewHolderByPosition();
        hanlder.sendMessage(msg);
    }

    @Override
    public void onInstallFail() {
        //task.setInstallState(InstallState.INSTALLFAILED);
        Message msg = new Message();
        msg.what = 7;
        msg.obj = getViewHolderByPosition();
        hanlder.sendMessage(msg);
    }

    private void setProgress(Button button, ProgressBar progressBar, long finishedSize, long totalSize){
        //button.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        int percent = (int) finishedSize * 100 / (int) totalSize;
        if (percent - mProgress > 1) { // 降低状态栏进度刷新频率，性能问题
            mProgress = percent;
            progressBar.setMax(100);
            progressBar.setProgress(percent);
        }
    }

    public DownloadHanlder getHanlder(){
        return hanlder;
    }

    private RecyclerView.ViewHolder getViewHolderByPosition(){
        View view = recyclerView.getLayoutManager().findViewByPosition(position);
        if (view != null) {
            return recyclerView.getChildViewHolder(view);
        }

        return null;

    }
}
