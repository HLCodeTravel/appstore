package net.bat.store.ux.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.download.DownloadState;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.ux.acticivty.DownloadListActivity;
import net.bat.store.ux.adapter.viewholder.DownloadParentViewHolder;
import net.bat.store.ux.adapter.viewholder.DownloadTaskViewHolder;

import java.io.File;
import java.util.List;


/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/6
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private List<DownloadTask> mDownloadingTasks;
    private List<DownloadTask> mFinishedDownloadTasks;
    private static final int TYPE_DOWNLOADING_TITLE = 0;
    private static final int TYPE_DOWNLOADING_ITEM = 1;
    private static final int TYPE_DOWNLOADED_TITLE = 2;
    //是否为编辑模式
    private boolean isEditMode;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    private DownloadListActivity activity;

    private boolean clickFlag = true;

    public DownloadAdapter(DownloadListActivity activity,List<DownloadTask> downloadingTasks,
                           List<DownloadTask> finishedDownloadTasks) {
        this.activity = activity;
        mDownloadingTasks = downloadingTasks;
//        DownloadTask tempTask = null;
//        for (DownloadTask task:mDownloadingTasks){
//            if (task.getPackageName().equals(BaseApplication.getContext().getApplicationInfo().packageName)){
//                tempTask = task;
//            }
//        }
//        if (tempTask != null){
//            mDownloadingTasks.remove(tempTask);
//        }
        mFinishedDownloadTasks = finishedDownloadTasks;
    }

    @Override
    public int getItemCount() {
        if (mDownloadingTasks.isEmpty() && mFinishedDownloadTasks.isEmpty())
            return 0;
        return mDownloadingTasks.size() + mFinishedDownloadTasks.size() + 2;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        if (holder instanceof DownloadParentViewHolder){
            if (position == mDownloadingTasks.size() + 1){
                ((DownloadParentViewHolder) holder).tv_group_title.setText(R.string.downloaded_group_title);
            }
        }else if (holder instanceof DownloadTaskViewHolder){

            setMargin(((DownloadTaskViewHolder) holder).tv_FileName,R.dimen.dimen_7dp,0);
            setMargin(((DownloadTaskViewHolder) holder).tv_FileSize,R.dimen.dimen_11dp,1);

            DownloadTask task = null;

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && clickFlag){
                        itemClickListener.onItemClick(position);
                    }
                }
            });

            //下载任务设置点击事件
            if (position <= mDownloadingTasks.size()) {
                task = mDownloadingTasks.get(position - 1);
            }else {
                task = mFinishedDownloadTasks.get(position-mDownloadingTasks.size()-2);
            }
            ((DownloadTaskViewHolder)holder).tv_Divider.setVisibility(View.VISIBLE);
            ((DownloadTaskViewHolder) holder).iv_CheckItem.setVisibility(View.GONE);
            ((DownloadTaskViewHolder) holder).tv_FileName.setText(task.getTitle());
            if (TextUtils.isEmpty(task.getFileName())) {
                task.setFileName(BaseApplication.getContext().getString(R.string.missing_title));
            }

            //下载完成的任务设置样式
            if (position >= mDownloadingTasks.size() + 2) {

                setMargin(((DownloadTaskViewHolder) holder).tv_FileName,R.dimen.dimen_20dp,0);
                setMargin(((DownloadTaskViewHolder) holder).tv_FileSize,R.dimen.dimen_20dp,1);
                ((DownloadTaskViewHolder)holder).iv_DownloadStatus.setVisibility(View.GONE);
                ((DownloadTaskViewHolder)holder).tv_DownloadStatus.setVisibility(View.GONE);
                ((DownloadTaskViewHolder)holder).pb.setVisibility(View.GONE);
                ((DownloadTaskViewHolder)holder).pb_fail.setVisibility(View.GONE);
                ((DownloadTaskViewHolder)holder).tv_FileSize.setText(getSizeText(task.getTotalSize()));
            } else {

                //下载中的任务设置样式
                ((DownloadTaskViewHolder) holder).iv_DownloadStatus.setVisibility(View.VISIBLE);
                ((DownloadTaskViewHolder) holder).tv_DownloadStatus.setVisibility(View.VISIBLE);
                ((DownloadTaskViewHolder) holder).pb.setVisibility(View.VISIBLE);

                boolean indeterminate = (task.getDownloadState() == DownloadState.WAITING);
                if (task.getDownloadState() == DownloadState.FAILED) {
                    ((DownloadTaskViewHolder) holder).pb.setVisibility(View.INVISIBLE);
                    ((DownloadTaskViewHolder) holder).pb_fail.setVisibility(View.VISIBLE);
                    ((DownloadTaskViewHolder) holder).pb_fail.setIndeterminate(indeterminate);
                    if (!indeterminate) {
                        ((DownloadTaskViewHolder) holder).pb_fail.setMax(100);
                        ((DownloadTaskViewHolder) holder).pb_fail.setProgress(getProgressValue(task.getTotalSize(), task.getFinishedSize()));
                    }
                } else {

                    ((DownloadTaskViewHolder) holder).pb_fail.setVisibility(View.INVISIBLE);
                    ((DownloadTaskViewHolder) holder).pb.setVisibility(View.VISIBLE);
                    ((DownloadTaskViewHolder) holder).pb.setIndeterminate(indeterminate);
                    if (!indeterminate) {
                        ((DownloadTaskViewHolder) holder).pb.setMax(100);
                        ((DownloadTaskViewHolder) holder).pb.setProgress(getProgressValue(task.getTotalSize(), task.getFinishedSize()));

                    }
                }
            }
            setDownloadStatus(((DownloadTaskViewHolder)holder).tv_DownloadStatus, ((DownloadTaskViewHolder)holder).iv_DownloadStatus, task);
            if (task.getTotalSize() == -1){
                ((DownloadTaskViewHolder) holder).tv_FileSize.setText(R.string.unknow);
            }else {
                if (task.getDownloadState() == DownloadState.FINISHED && (task.getTotalSize() != task.getFinishedSize())){
                    File file = new File(task.getFilePath() + "/" + task.getFileName());
                    if (file.exists() && file.isFile()) {
                        ((DownloadTaskViewHolder) holder).tv_FileSize.setText(getSizeText(file.length()) + "/" + getSizeText(file.length()));
                    }
                }else {
                    ((DownloadTaskViewHolder) holder).tv_FileSize.setText(getSizeText(task.getFinishedSize()) + "/" + getSizeText(task.getTotalSize()));
                }
            }
            ((DownloadTaskViewHolder) holder).iv_DownloadStatus.setTag(task);
            ((DownloadTaskViewHolder) holder).iv_DownloadStatus.setOnClickListener(this);
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_DOWNLOADED_TITLE:
                return new DownloadParentViewHolder(LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.item_group_download,parent,false));
            case TYPE_DOWNLOADING_ITEM:
                return new DownloadTaskViewHolder(LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.item_child_download,parent,false));
            case TYPE_DOWNLOADING_TITLE:
                return new DownloadParentViewHolder(LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.item_group_download,parent,false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_DOWNLOADING_TITLE;
        }else if (position == mDownloadingTasks.size() + 1){
            return TYPE_DOWNLOADED_TITLE;
        }else {
            return TYPE_DOWNLOADING_ITEM;
        }
    }

    private void setDownloadStatus(TextView tv, ImageView iv, DownloadTask task) {
        switch (task.getDownloadState()) {
            case FAILED:
                tv.setText(BaseApplication.getContext().getString(R.string.download_error));
                if (iv != null) {
                    iv.setImageResource(R.drawable.download_status_failed);
                }
                break;
            case PAUSE:
                tv.setText(BaseApplication.getContext().getString(R.string.download_status_pause));
                if (iv != null) {
                    iv.setImageResource(R.drawable.download_status_paused);
                }
                break;
            case DOWNLOADING:
                tv.setText(BaseApplication.getContext().getString(R.string.download_status_downloading));
//                tv.setText(getSizeText(task.speed)+"/s");
                if (iv != null) {
                    iv.setImageResource(R.drawable.download_status_downloading);
                }
                break;
            case FINISHED:
                break;
            case WAITING:
                tv.setText(BaseApplication.getContext().getString(R.string.download_status_init));
                if (iv != null) {
                    iv.setImageResource(R.drawable.download_status_downloading);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Log.e("adapter","do click");
        switch (v.getId()){
            case R.id.iv_download_status:
                DownloadTask task = (DownloadTask) v.getTag();
                switch (task.getDownloadState()){
                    case WAITING:
                    case DOWNLOADING:
                        Log.e("adapter","do pause");
                        DownloadTaskManager.getInstance(BaseApplication.getContext()).pauseDownload(task);
                        break;
                    case PAUSE:
                    case FAILED:
                        DownloadTaskManager.getInstance(BaseApplication.getContext()).continueDownload(task);
                        break;
                }
        }
    }

    private String getSizeText(long sizeBytes) {
        String sizeText = "0.00B";
        if (sizeBytes >= 0) {
            sizeText = Formatter.formatFileSize(BaseApplication.getContext(), sizeBytes);
        }
        return sizeText;
    }

    public int getProgressValue(long totalBytes, long currentBytes) {
        if (totalBytes == -1) {
            return 0;
        }
        if (totalBytes == 0){
            return 0;
        }
        return (int) (currentBytes * 100 / totalBytes);
    }

    private void setMargin(View view,int dimenId,int flag){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (flag == 1) {
            params.bottomMargin = (int) BaseApplication.getContext().getResources().getDimension(dimenId);
        }else {
            params.topMargin = (int) BaseApplication.getContext().getResources().getDimension(dimenId);
        }
        view.setLayoutParams(params);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener){
        this.itemLongClickListener = itemLongClickListener;
    }

    public void setClickFlag(boolean flag){
        clickFlag = flag;
    }

    private OnItemClickListener getOnItemClickListener(){
        if (itemClickListener != null)
            return itemClickListener;
        return null;
    }

    private OnItemLongClickListener getOnItemLongClickListener(){
        if (itemLongClickListener != null)
            return itemLongClickListener;
        return null;
    }

    private void removeListener(){
        itemClickListener = null;
        itemLongClickListener = null;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(int position);
    }
}
