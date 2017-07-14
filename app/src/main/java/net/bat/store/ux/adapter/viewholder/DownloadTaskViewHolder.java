package net.bat.store.ux.adapter.viewholder;

/* Top Secret */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.bat.store.R;

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/16
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class DownloadTaskViewHolder extends RecyclerView.ViewHolder{
    public ImageView iv_CheckItem;
    public ImageView iv_FileType;
    public TextView tv_FileName;
    public TextView tv_FileSize;
    public ProgressBar pb;
    public ProgressBar pb_fail;
    public TextView tv_DownloadStatus;
    public ImageView iv_DownloadStatus;
    public TextView tv_Divider;
    public LinearLayout ll;

    public DownloadTaskViewHolder(View itemView){
        super(itemView);
        iv_CheckItem = (ImageView) itemView.findViewById(R.id.iv_check_item);
        iv_FileType = (ImageView) itemView.findViewById(R.id.iv_file_type);
        tv_FileName = (TextView) itemView.findViewById(R.id.tv_file_name);
        tv_FileSize = (TextView) itemView.findViewById(R.id.tv_file_size);
        pb = (ProgressBar) itemView.findViewById(R.id.pb_progress);
        pb_fail = (ProgressBar) itemView.findViewById(R.id.pb_progress_fail);
        tv_DownloadStatus = (TextView) itemView.findViewById(R.id.tv_download_status);
        iv_DownloadStatus = (ImageView) itemView.findViewById(R.id.iv_download_status);
        tv_Divider = (TextView) itemView.findViewById(R.id.tv_divider);
        ll = (LinearLayout) itemView.findViewById(R.id.ll);
    }
}
