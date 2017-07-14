package net.bat.store.ux.adapter.viewholder;

/* Top Secret */

import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class DownloadParentViewHolder extends RecyclerView.ViewHolder{
    public TextView tv_group_title;
    public TextView tv_pause_all;


    public DownloadParentViewHolder(View itemView) {
        super(itemView);
        tv_group_title = (TextView) itemView.findViewById(R.id.tv_group_title);
        tv_pause_all = (TextView) itemView.findViewById(R.id.tv_pause_all);
    }


}
