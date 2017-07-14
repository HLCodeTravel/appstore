package net.bat.store.ux.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.bat.store.R;


/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/1/4
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class AppInfoViewHolder extends RecyclerView.ViewHolder {

    public ImageView iv_app_icon;
    public TextView tv_app_label;
    public TextView tv_app_size;
    public TextView tv_uninstall;

    public AppInfoViewHolder(View itemView) {
        super(itemView);

        iv_app_icon = (ImageView)itemView.findViewById(R.id.iv_app_icon);
        tv_app_label = (TextView)itemView.findViewById(R.id.tv_app_label);
        tv_app_size = (TextView)itemView.findViewById(R.id.tv_app_size);
        tv_uninstall = (TextView) itemView.findViewById(R.id.tv_uninstall);
    }
}
