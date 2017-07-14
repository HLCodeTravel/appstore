package net.bat.store.download;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.ux.adapter.viewholder.MainListHolder;
import net.bat.store.ux.adapter.viewholder.MainViewAdHolder;
import net.bat.store.ux.adapter.viewholder.MainViewHolder;
import net.bat.store.ux.adapter.viewholder.SinglerGirdViewHolder;


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

public class DownloadHanlder extends Handler {
    private static final String TAG = "DownloadHanlder";

    private int totalSize;
    private int finishedSize;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.obj != null) {
            Log.e(TAG,""+msg.what);
            refreshViewByViewHolder(msg.what, (RecyclerView.ViewHolder) msg.obj);
            if (msg.arg1 != -1 && msg.arg2 != 0){
                totalSize = msg.arg1;
                finishedSize = msg.arg2;
            }
        }
    }

    private void refreshViewByViewHolder(int state, RecyclerView.ViewHolder holder) {
        if (holder instanceof MainViewHolder) {
            refreshDownloadView(state, ((MainViewHolder) holder).mDownloadButton,
                    ((MainViewHolder) holder).pb_current_size);
        }else if (holder instanceof MainViewAdHolder){
            refreshDownloadView(state, ((MainViewAdHolder) holder).mDownloadButton,
                    ((MainViewAdHolder) holder).pb_current_size);
        }else if (holder instanceof MainListHolder) {
            refreshDownloadView(state, ((MainListHolder) holder).btn_download,
                    ((MainListHolder) holder).progressBar);
        } else if (holder instanceof SinglerGirdViewHolder) {
            refreshDownloadView(state, ((SinglerGirdViewHolder) holder).bn_free,
                    ((SinglerGirdViewHolder) holder).progressBar);
        }
    }

    public void refreshDownloadView(int state, Button downloadBtn, ProgressBar progressBar) {
        switch (state) {
            case 0:
                Log.e(TAG,"waiting");
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(totalSize);
                progressBar.setProgress(0);
                downloadBtn.setText(R.string.download_status_init);
                downloadBtn.setBackgroundColor(Color.TRANSPARENT);
                downloadBtn.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.skin_download_item_file_size_color));
                break;
            case 1:
                Log.e(TAG,"downloading");
                progressBar.setVisibility(View.VISIBLE);
                downloadBtn.setText(R.string.download_status_pause);
                downloadBtn.setBackgroundColor(Color.TRANSPARENT);
                downloadBtn.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.skin_download_item_file_size_color));
//                imageView.setVisibility(View.VISIBLE);
//                imageView.setImageResource(R.drawable.download_pause);
                break;
            case 4:
                Log.e(TAG,"pause");
                downloadBtn.setText(BaseApplication.getContext().getString(R.string.download_continue));
                downloadBtn.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.appcenter_free_normal_color));
                downloadBtn.setBackgroundResource(R.drawable.app_detail_btn);
                break;
            case 2:
                Log.e(TAG,"failed");
                progressBar.setVisibility(View.GONE);
                downloadBtn.setBackgroundResource(R.drawable.app_detail_btn);
                downloadBtn.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.appcenter_free_normal_color));
                downloadBtn.setText(R.string.appcenter_download_text);
                //Toast.makeText(BaseApplication.getContext(),R.string.download_error,Toast.LENGTH_LONG).show();
                break;
            case 3:
                Log.e(TAG,"installing");
                downloadBtn.setVisibility(View.VISIBLE);
                downloadBtn.setBackgroundResource(R.drawable.app_detail_btn);
                progressBar.setVisibility(View.GONE);
                downloadBtn.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.appcenter_free_normal_color));
                //imageView.setVisibility(View.GONE);
                downloadBtn.setText(R.string.installing);
                break;
            case 6:
                Log.e(TAG,"install success");
                downloadBtn.setText(R.string.appcenter_open_text);
                break;
            case 5:
                Log.e(TAG,"delete");
                progressBar.setVisibility(View.GONE);
                downloadBtn.setBackgroundResource(R.drawable.app_detail_btn);
                downloadBtn.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.appcenter_free_normal_color));
                downloadBtn.setText(R.string.appcenter_download_text);
                break;
            case 7:
                Log.e(TAG,"install failed");
                downloadBtn.setText(R.string.install_app);
                break;
            default:
                Log.e(TAG,"unknow");
                break;
        }
    }
}
