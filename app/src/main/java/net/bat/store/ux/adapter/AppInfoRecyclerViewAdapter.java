package net.bat.store.ux.adapter;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.bean.AppInfo;
import net.bat.store.ux.acticivty.UninstallActivity;
import net.bat.store.ux.adapter.viewholder.AppInfoViewHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


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

public class AppInfoRecyclerViewAdapter extends RecyclerView.Adapter<AppInfoViewHolder> {

    private static final String TAG = "AppInfoRecyclerView";
    private List<AppInfo> appInfoList;
    private FirebaseAnalytics mFirebaseAnalytics;
    public List<Boolean> isUninstalling;
    private UninstallActivity uninstallActivity;
    private boolean clickFlag = true;

    public AppInfoRecyclerViewAdapter(UninstallActivity activity, List<AppInfo> appInfoList) {
        super();
        this.uninstallActivity = activity;
        this.appInfoList = appInfoList;
        isUninstalling = new ArrayList<>();
        for (int i=0;i<appInfoList.size();i++){
            isUninstalling.add(false);
        }
    }

    public AppInfoRecyclerViewAdapter() {
        super();
    }

    @Override
    public AppInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.item_recyclerview_installedapp, parent, false);
        return new AppInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AppInfoViewHolder holder, final int position) {
        holder.tv_app_size.setText(appInfoList.get(position).getAppSize());
        holder.tv_app_label.setText(appInfoList.get(position).getAppLabel());

        try {
            PackageInfo info = BaseApplication.getContext().getPackageManager().getPackageInfo(appInfoList.get(position).getPackageName(),0);
            Drawable drawable = info.applicationInfo.loadIcon(BaseApplication.getContext().getPackageManager());

            holder.iv_app_icon.setImageDrawable(drawable);
        }catch (PackageManager.NameNotFoundException e){
            holder.iv_app_icon.setImageResource(R.drawable.image_default);
        }

        //holder.iv_app_icon.setImageDrawable(appInfoList.get(position).getAppIcon());
//        if (!isUninstalling.get(position)) {
//            holder.tv_uninstall.setText(R.string.uninstall);
//            holder.tv_uninstall.setBackgroundResource(R.drawable.appcenter_bt_free_selector);
//        }else {
//            holder.tv_uninstall.setText(R.string.uninstalling);
//            holder.tv_uninstall.setBackgroundResource(R.drawable.uninstall_bg);
//        }
        holder.tv_uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("AppName", appInfoList.get(position).getAppLabel());
                FirebaseStat.logEvent("UninstallStart", bundle);
                if(!ConstantUtils.isExistsProcess(appInfoList.get(position).getPackageName())){
                    appInfoList.remove(position);
                    notifyDataSetChanged();
                    return;
                }
                if (BaseApplication.getContext().getApplicationInfo().uid < 10000) {
                    showDialog(position);
                }else {
                    unInstallAppByIntent(position);
                }
            }
        });
    }

    private void showDialog(final int position){
        LayoutInflater inflater = LayoutInflater.from(BaseApplication.getContext());
        View view = inflater.inflate(R.layout.uninstall_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(uninstallActivity,R.style.AlertDialogCustom);
        builder.setView(view);
        //final AlertDialog dialog = builder.create();

        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_uninstall = (TextView)view.findViewById(R.id.tv_uninstall);
        TextView tv_app_name = (TextView)view.findViewById(R.id.tv_app_name);

        final AlertDialog dialog = builder.show();

        tv_app_name.setText(appInfoList.get(position).getAppLabel());

        tv_cancel.setOnClickListener(new android.view.View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.e("click","dismiss");
                dialog.dismiss();
            }
        });

        tv_uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                if (BaseApplication.getContext().getApplicationInfo().uid < 10000) {
                    new Uninstall(position).execute();
                } else {
                    unInstallAppByIntent(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (appInfoList != null) {
            return appInfoList.size();
        }
        return 0;
    }


    public boolean isClickFlag() {
        return clickFlag;
    }

    public void setClickFlag(boolean clickFlag) {
        this.clickFlag = clickFlag;
    }

    private String uninstallApp(int postion) {
        String result = "";
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;

        Log.e("packagename", appInfoList.get(postion).getPackageName());
        try {
            process = Runtime.getRuntime().exec("pm uninstall " + appInfoList.get(postion).getPackageName());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            result = new String(baos.toByteArray());
            if (inIs != null)
                inIs.close();
            if (errIs != null)
                errIs.close();

        } catch (IOException e) {
            result = e.getMessage();
        }
        if (process != null) {
            process.destroy();
        }
        return result;
    }

    private void unInstallAppByIntent(int position){
        Log.e(TAG,"uninstall intent");
        if (position == appInfoList.size() && position != 0){
            position = position - 1 ;
        }
        if (appInfoList.size() != 0 && position< appInfoList.size()) {
            Uri packageUri = Uri.parse("package:" + appInfoList.get(position).getPackageName());
            Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("position", position);
            BaseApplication.getContext().startActivity(intent);
            Bundle bundle = new Bundle();
            bundle.putString("AppName", appInfoList.get(position).getAppLabel());
            FirebaseStat.logEvent("UninstallFinish", bundle);
        }

    }

    class Uninstall extends AsyncTask<Void,Void,Void>{

        int position;
        AlertDialog dialog;

        public Uninstall(int position) {
            super();
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LayoutInflater inflater = LayoutInflater.from(BaseApplication.getContext());
            View view = inflater.inflate(R.layout.un_dialog, null);

            final AlertDialog.Builder builder = new AlertDialog.Builder(uninstallActivity,R.style.AlertDialogCustom);
            builder.setView(view);
            //final AlertDialog dialog = builder.create();

            TextView tv_app_name = (TextView)view.findViewById(R.id.tv_app_name);

            dialog = builder.show();

            dialog.setCancelable(false);

            tv_app_name.setText(appInfoList.get(position).getAppLabel());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appInfoList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position, appInfoList.size() - position);
            dialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PackageInfo info = BaseApplication.getContext().getPackageManager().getPackageInfo(appInfoList.get(position).getPackageName(),0);
                uninstallApp(position);
            }catch (PackageManager.NameNotFoundException e){
                //notifyDataSetChanged();
                cancel(false);
            }

            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            dialog.dismiss();
        }
    }

}
