package net.bat.store.ux.adapter;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.utils.PreferencesUtils;
import net.bat.store.ux.acticivty.UpdateAcitivity;
import net.bat.store.bean.AppInfo;
import net.bat.store.ux.adapter.viewholder.UpdateViewHolder;
import net.bat.store.widget.UpdateDescriptionView;

import java.util.ArrayList;
import java.util.List;


/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/10
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class UpdateAdapter extends RecyclerView.Adapter<UpdateViewHolder> implements UpdateDescriptionView.OnChangedListener{
    private static final String TAG = "UpdateAdapter";
    private static final String GOOGLE_MARTKET = "market://details?id=";

    private UpdateAcitivity mAcitivy;
    private List<AppInfo> appInfoList;
    private PackageManager mPackageManager;
    private List<String> mUpdateDescOpenStatePackageList;

    public UpdateAdapter(UpdateAcitivity activity, List<AppInfo> appInfoList) {
        mAcitivy = activity;
        this.appInfoList = appInfoList;
        mPackageManager = BaseApplication.getContext().getPackageManager();
    }

    @Override
    public int getItemCount() {
        if (appInfoList == null) {
            return 0;
        }
        return appInfoList.size();
    }

    @Override
    public void onBindViewHolder(UpdateViewHolder holder, final int position) {

        holder.tv_old_version.setText("V" + appInfoList.get(position).getOldVersion());
        holder.tv_new_version.setText("V" + appInfoList.get(position).getNewVersion());
        holder.tv_app_label.setText(appInfoList.get(position).getAppLabel());
        holder.iv_app_icon.setImageDrawable(appInfoList.get(position).getAppIcon());

        holder.update_view.setChangedListener(this);
        holder.update_view.setDescriptionContentText(appInfoList.get(position).getRecentChange());
        holder.update_view.setTag(appInfoList.get(position).getPackageName());
        if (mUpdateDescOpenStatePackageList != null && mUpdateDescOpenStatePackageList.contains(appInfoList.get(position).getPackageName())) {
            //打开操作
            holder.update_view.openOrClose(true);
        } else {
            //收起操作
            holder.update_view.openOrClose(false);
        }

        holder.ignore_update_btn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"write ignore" + appInfoList.get(position).getUpdateUrl());
                PreferencesUtils.putString(BaseApplication.getContext(),appInfoList.get(position).getUpdateUrl(),
                        appInfoList.get(position).getNewVersion());
                appInfoList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(0,appInfoList.size());
                if (appInfoList.isEmpty()){
                    mAcitivy.no_update_app_prompt_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInstallGooglePlay()){
  //                  Intent intent = new Intent();
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    intent.setAction(Intent.ACTION_MAIN);
//                    intent.setData(Uri.parse(GOOGLE_MARTKET + appInfoList.get(position).getPackageName()));
//                    intent.setComponent(new ComponentName("com.android.vending", "com.google.android.finsky.activities.LaunchUrlHandlerActivity"));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent intent= new Intent(Intent.ACTION_VIEW);
                    Log.e(TAG,appInfoList.get(position).getPackageName());
                    intent.setData(Uri.parse(GOOGLE_MARTKET + appInfoList.get(position).getPackageName()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BaseApplication.getContext().startActivity(intent);
                }else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(appInfoList.get(position).getUpdateUrl()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BaseApplication.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public UpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UpdateViewHolder(LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.item_update, parent, false));
    }

    private boolean isInstallGooglePlay(){
        try {
            PackageInfo info = mPackageManager.getPackageInfo("com.android.vending",0);
            if (info != null){
                return true;
            }
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
        return false;
    }

    @Override
    public void onChanged(boolean isOpen, String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (isOpen) {
            //展开
            if (mUpdateDescOpenStatePackageList == null) {
                mUpdateDescOpenStatePackageList = new ArrayList<String>();
            }
            if (!mUpdateDescOpenStatePackageList.contains(key)) {
                mUpdateDescOpenStatePackageList.add(key);
            }
        } else {
            //收起
            if (mUpdateDescOpenStatePackageList != null && mUpdateDescOpenStatePackageList.contains(key)) {
                mUpdateDescOpenStatePackageList.remove(key);
            }
        }
    }
}
