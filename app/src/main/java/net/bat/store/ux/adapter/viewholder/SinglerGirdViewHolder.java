package net.bat.store.ux.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.bat.store.R;


/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/9
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class SinglerGirdViewHolder extends RecyclerView.ViewHolder {
    public ImageView iv_icon;
    public TextView iv_top;
    public TextView tv_app_name;
    public TextView tv_download_time;
    public Button bn_free;
    public ProgressBar progressBar;
    public ImageView iv_top_bg;

    public SinglerGirdViewHolder(View itemView) {
        super(itemView);
        iv_icon = (ImageView) itemView.findViewById(R.id.iv_app_icon);
        iv_top = (TextView) itemView.findViewById(R.id.app_top);
        tv_app_name = (TextView) itemView.findViewById(R.id.app_name);
        tv_download_time = (TextView) itemView.findViewById(R.id.app_filesize);
        bn_free = (Button) itemView.findViewById(R.id.app_install);
        progressBar = (ProgressBar) itemView.findViewById(R.id.pb_current_size);
        iv_top_bg = (ImageView)itemView.findViewById(R.id.iv_app_top_bg);
    }
}
