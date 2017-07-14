package net.bat.store.ux.adapter.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.bat.store.R;
import net.bat.store.ux.listener.RecyclerViewItemClickListener;

/**
 * usage
 *
 * @author cheng.qian.
 * @date 2016/9/26.
 * ============================================
 * Copyright (c) 2016 TRANSSION.Co.Ltd.
 * All right reserved.
 **/


 public class SortsViewHolder extends RecyclerView.ViewHolder{
    RecyclerViewItemClickListener listener;
    public TextView sortName;
    public ImageView icon;
    public   SortsViewHolder(View itemView) {
        super(itemView);
        sortName = (TextView) itemView.findViewById(R.id.category_games_name);
        icon= (ImageView) itemView.findViewById(R.id.category_games_icon);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v,getAdapterPosition());
                }
            }
        });
    }

    public  void setItemClickListener(RecyclerViewItemClickListener listener) {
        this.listener = listener;
    }
}
