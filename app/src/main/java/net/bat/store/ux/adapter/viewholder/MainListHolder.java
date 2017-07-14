package net.bat.store.ux.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import net.bat.store.R;

/**
 * Created by liang.he on 2017/7/7.
 */

public class MainListHolder extends RecyclerView.ViewHolder {

    public ImageView iv_app_icon;
    public TextView tv_app_label;
    public RatingBar rb_score;
    public TextView tv_filesize;
    public Button btn_download;
    public TextView tv_app_rank;
    public TextView tv_desc;
    public ProgressBar progressBar;
    public View re_title;
    public TextView tv_title;
    public View topic_header;
    public TextView topic_textview;
    public ImageView iv_topic;

    public MainListHolder(View itemView) {
        super(itemView);
        iv_app_icon = (ImageView) itemView.findViewById(R.id.iv_app_icon);
        tv_app_label = (TextView) itemView.findViewById(R.id.tv_app_label);
        tv_desc = (TextView) itemView.findViewById(R.id.tv_description);
        rb_score = (RatingBar) itemView.findViewById(R.id.rb_score);
        tv_filesize = (TextView) itemView.findViewById(R.id.tv_filesize);
        btn_download = (Button) itemView.findViewById(R.id.btn_download);
        tv_app_rank = (TextView) itemView.findViewById(R.id.tv_app_rank);
        progressBar = (ProgressBar) itemView.findViewById(R.id.pb_current_size);
        re_title = itemView.findViewById(R.id.re_title);
        tv_title = (TextView) itemView.findViewById(R.id.card_name);
        topic_header = itemView.findViewById(R.id.v_card_topic_header);
        iv_topic = (ImageView) itemView.findViewById(R.id.iv_background);
        topic_textview = (TextView) itemView.findViewById(R.id.tv_card_name);
    }
}
