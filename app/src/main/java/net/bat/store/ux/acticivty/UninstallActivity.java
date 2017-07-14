package net.bat.store.ux.acticivty;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.bean.AppInfo;
import net.bat.store.ux.adapter.AppInfoRecyclerViewAdapter;
import net.bat.store.widget.WrapContentLinearLayoutManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class UninstallActivity extends Activity {
    private DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");

    public static final int MB_2_BYTE = 1024 * 1024;
    public static final int KB_2_BYTE = 1024;

    private RecyclerView appListView;
    private ImageView mLoadingProgress;
    private TextView tv_title;
    private List<AppInfo> appInfoList;
    private AppInfoRecyclerViewAdapter adapter;
    private AdapterHandler handle;
    private UninstallBroadCast broadCast;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall);
        mContext=BaseApplication.getContext();
        initView();
        appInfoList = new ArrayList<>();
        handle = new AdapterHandler();

        //查询app信息线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryAppInfo();
            }
        }).start();

        broadCast = new UninstallBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);  // Intent.ACTION_PACKAGE_ADDED安装后。
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addDataScheme("package");
        registerReceiver(broadCast, filter);

    }

    private void initView() {
        appListView = (RecyclerView) findViewById(R.id.rv_installed_app_list);
        mLoadingProgress = (ImageView) findViewById(R.id.v_loading);
        Glide.with(this).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingProgress);
        tv_title = (TextView) findViewById(R.id.category_top_bar_title);
        tv_title.setText(R.string.app_manager);
        findViewById(R.id.category_top_bar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Bundle param = new Bundle();
                param.putString("Action", "iv_back");
                FirebaseStat.logEvent("UninstallBackClick", param);
            }
        });
        ImageView back_icon= (ImageView) findViewById(R.id.iv_back);
        if(ConstantUtils.isIconShouldRevert()){
            back_icon.setImageResource(R.drawable.icon_back_revert);
        }
    }

    private void initAdapter() {
        mLoadingProgress.setVisibility(View.GONE);
        if (adapter == null) {
            adapter = new AppInfoRecyclerViewAdapter(this, appInfoList);
            appListView.setAdapter(adapter);
            appListView.setLayoutManager(new WrapContentLinearLayoutManager(this));
        } else {
            adapter.notifyDataSetChanged();
        }
        if (appInfoList.isEmpty()) {
            Toast.makeText(this, R.string.no_app_exist, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 遍历查询已安装的应用
     */
    public void queryAppInfo() {
        Log.i("queryAppInfo", "queryAppInfo: ");
        PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> applicationInfoList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo applicationInfo : applicationInfoList) {
            //获取非系统应用
            if ((applicationInfo.FLAG_SYSTEM & applicationInfo.flags) <= 0
                    && !applicationInfo.packageName.equals(getPackageName())) {
                String appLabel = applicationInfo.loadLabel(pm).toString();
                //Drawable appIcon = applicationInfo.loadIcon(pm);
                String dir = applicationInfo.publicSourceDir;
                String appSize = getAppSize(dir);
                String packageName = applicationInfo.packageName;
                Date date = null;
                try {
                    date = new Date(pm.getPackageInfo(applicationInfo.packageName, 0).firstInstallTime);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                AppInfo appInfo = new AppInfo();
                //appInfo.setAppIcon(appIcon);
                appInfo.setAppLabel(appLabel);
                appInfo.setAppSize(appSize);
                appInfo.setPackageName(packageName);
                if (date != null) {
                    appInfo.setInstallTime(date.toString());
                } else {
                    appInfo.setInstallTime("未知");
                }
                if(appInfoList==null){
                    appInfoList=new ArrayList<>();
                }
                appInfoList.add(appInfo);
            }
        }
        if (handle != null) {
            handle.sendEmptyMessageDelayed(1000, 1000);
        }
    }

    public String getAppSize(String dir) {
        File tempFile = new File(dir);
        long size = -1;
        if (tempFile.exists()) {
            size = tempFile.length();
        }
        if (size <= 0) {
            return "0M".toString();
        }

        if (size >= MB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE)).append("M").toString();
        } else if (size >= KB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double) size / KB_2_BYTE)).append("K").toString();
        } else {
            return (size + "B").toString();
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("TAG","on destroy");
        super.onDestroy();
        if (broadCast != null) {
            unregisterReceiver(broadCast);
        }
        if (appInfoList != null) {
            appInfoList.clear();
            appInfoList = null;
        }
        if (handle != null) {
            handle.removeCallbacksAndMessages(null);
            handle = null;
        }
        adapter = null;
    }

    public class AdapterHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    initAdapter();
                    break;
            }
        }
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.e("queryAppInfo", "onRestart: ");
//        if (!appInfoList.isEmpty()) {
//            appInfoList.clear();
//            //查询app信息线程
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    queryAppInfo();
//                }
//            }).start();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseStat.setCurrentScreen(this, "UninstallActivity", null);
    }

    class UninstallBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.e("uninstall", packageName);
                //int position = intent.getIntExtra("position",-1);
                //Log.e("position",""+position);
                //Log.e("size",appInfoList.get(0).getPackageName()+"");
                AppInfo tempInfo = null;
                if (appInfoList != null) {
                    for (AppInfo info : appInfoList) {
                        if (info.getPackageName().equals(packageName)) {
                            tempInfo = info;
                        }
                    }
                }
                if (tempInfo != null) {
                    int position = appInfoList.indexOf(tempInfo);
                    appInfoList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeRemoved(position, appInfoList.size() - position);
                    //adapter.notifyDataSetChanged();
                    Log.e("size", appInfoList.size() + "");
                    adapter.setClickFlag(true);
                }
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.e("uninstall", packageName);
                //int position = intent.getIntExtra("position",-1);
                //Log.e("position",""+position);
                //Log.e("size",appInfoList.get(0).getPackageName()+"");
                AppInfo tempInfo = null;
                if (appInfoList != null) {
                    for (AppInfo info : appInfoList) {
                        if (info.getPackageName().equals(packageName)) {
                            tempInfo = info;
                        }
                    }
                }
                if (appInfoList != null) {
                    if (tempInfo == null) {
                        try {
                            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(packageName, 0);
                            String appLabel = applicationInfo.loadLabel(getPackageManager()).toString();
                            Drawable appIcon = applicationInfo.loadIcon(getPackageManager());
                            String dir = applicationInfo.publicSourceDir;
                            String appSize = getAppSize(dir);
                            AppInfo appInfo = new AppInfo();
                            appInfo.setAppIcon(appIcon);
                            appInfo.setAppLabel(appLabel);
                            appInfo.setAppSize(appSize);
                            appInfo.setPackageName(packageName);
                            appInfoList.add(appInfo);
//                    adapter.notifyItemRemoved(position);
//                    adapter.notifyItemRangeRemoved(position, appInfoList.size() - position);
                            adapter.notifyDataSetChanged();
                            Log.e("size", appInfoList.size() + "");
                            adapter.setClickFlag(true);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
