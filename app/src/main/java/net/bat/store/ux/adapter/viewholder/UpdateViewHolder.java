package net.bat.store.ux.adapter.viewholder;

/* Top Secret */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.bat.store.R;
import net.bat.store.widget.UpdateDescriptionView;

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/10
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class UpdateViewHolder extends RecyclerView.ViewHolder{
    public ImageView iv_app_icon;
    public TextView tv_app_label;
    public TextView tv_old_version;
    public TextView tv_new_version;
    public TextView tv_update;
    public UpdateDescriptionView update_view;
    public LinearLayout ignore_update_btn_layout;

    public UpdateViewHolder(View itemView) {
        super(itemView);
        iv_app_icon = (ImageView)itemView.findViewById(R.id.iv_app_icon);
        tv_app_label = (TextView)itemView.findViewById(R.id.tv_app_label);
        tv_old_version = (TextView)itemView.findViewById(R.id.tv_old_version);
        tv_update = (TextView)itemView.findViewById(R.id.tv_update);
        tv_new_version = (TextView)itemView.findViewById(R.id.tv_new_version);
        update_view = (UpdateDescriptionView)itemView.findViewById(R.id.update_description_view);
        ignore_update_btn_layout = (LinearLayout)itemView.findViewById(R.id.ignore_update_btn_layout);
    }
}
