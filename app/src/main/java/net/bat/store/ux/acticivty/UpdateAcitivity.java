package net.bat.store.ux.acticivty;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.transsion.http.impl.StringCallback;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.update.GpUpdateAppBean;
import net.bat.store.bean.update.UpdateAppInfo;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.network.NetworkClient;
import net.bat.store.constans.NetworkConstant;
import net.bat.store.utils.CommonUtils;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.PreferencesUtils;
import net.bat.store.ux.adapter.UpdateAdapter;
import net.bat.store.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;


/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/1/22
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class UpdateAcitivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UpdateAcitivity";

    private RecyclerView updateView;
    private ImageView progress;
    public LinearLayout no_update_app_prompt_layout;
    private View request_failed;
    private Button request_fail_refresh;


    private StringCallback requestDataCallBack = new StringCallback() {
        @Override
        public void onFailure(int i, String s, Throwable throwable) {
            UpdateInfoAction(null);
        }

        @Override
        public void onSuccess(int code, String json_result) {
            GpUpdateAppBean gpUpdateAppBean = null;
            if (code == 200) {
                Log.i(TAG, "onPostGet: " + json_result);
                Gson gson = new Gson();
                try {
                    gpUpdateAppBean = gson.fromJson(json_result, GpUpdateAppBean.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            UpdateInfoAction(gpUpdateAppBean);
        }
    };

//    private HttpUtils.HttpListener requestDataCallBack = new HttpUtils.HttpListener() {
//        @Override
//        protected void onPostGet(HttpResponse httpResponse) {
//            super.onPostGet(httpResponse);
//            GpUpdateAppBean gpUpdateAppBean = null;
//
//            if (httpResponse != null) {
//                int code = httpResponse.getResponseCode();
//                Log.i(TAG, "onPostGet: " + code);
//                if (code == 200) {
//                    String json_result = httpResponse.getResponseBody();
//                    Log.i(TAG, "onPostGet: " + json_result);
//                    Gson gson = new Gson();
//                    try {
//                        gpUpdateAppBean = gson.fromJson(json_result, GpUpdateAppBean.class);
//                    } catch (JsonSyntaxException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            UpdateInfoAction(gpUpdateAppBean);
//        }
//    };


    private void UpdateInfoAction(GpUpdateAppBean gpUpdateAppBean) {
        if (gpUpdateAppBean != null && AppStoreConstant.STATUS_REQUEST_SUCCESS.equals(gpUpdateAppBean.getCode())) {
            refreshView(gpUpdateAppBean);
        } else {
            if (ConstantUtils.checkNetworkState(UpdateAcitivity.this)) {
                request_failed.setVisibility(View.GONE);
                no_update_app_prompt_layout.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            } else {
                request_failed.setVisibility(View.VISIBLE);
                no_update_app_prompt_layout.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initView();
        NetworkClient.startRequestQueryUpdateInfo(BaseApplication.getContext(), NetworkConstant.URL_QUERY_GPUPDATE, requestDataCallBack);
    }

    private void initView() {
        updateView = (RecyclerView) findViewById(R.id.rv_app_update);
        progress = (ImageView) findViewById(R.id.v_loading);
        Glide.with(this).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(progress);
        no_update_app_prompt_layout = (LinearLayout) findViewById(R.id.no_update_app_prompt_layout);
        request_failed = findViewById(R.id.request_fail);
        request_fail_refresh = (Button) request_failed.findViewById(R.id.request_fail_refresh);
        request_fail_refresh.setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.category_top_bar_title);
        tv_title.setText(R.string.installed_app_update);
        findViewById(R.id.category_top_bar_back).setOnClickListener(this);
        ImageView back_icon = (ImageView) findViewById(R.id.iv_back);
        if (ConstantUtils.isIconShouldRevert()) {
            back_icon.setImageResource(R.drawable.icon_back_revert);
        }
    }

    private void refreshView(GpUpdateAppBean result) {
        Log.e("refresh", "" + result.getDesc());
        if (AppStoreConstant.STATUS_REQUEST_SUCCESS.equals(result.getCode())) {
            List<UpdateAppInfo> updateAppInfoList = result.getResultMap().getGpAppDataList();
            List<AppInfo> appInfoList = new ArrayList<>();
            if (!updateAppInfoList.isEmpty()) {
                //将返回结果中删除没有在GP上架或者不是新版本的的应用从列表中删除
                for (int i = 0; i < updateAppInfoList.size(); i++) {
                    Log.e(TAG, PreferencesUtils.getString(UpdateAcitivity.this, updateAppInfoList.get(i).getUrl(), "no_ignore"));
                    if (!(updateAppInfoList.get(i).getName() == null) && isNewVersion(updateAppInfoList.get(i))
                            && PreferencesUtils.getString(UpdateAcitivity.this, updateAppInfoList.get(i).getUrl(), "no_ignore").equals("no_ignore")) {
                        try {
                            PackageInfo packageInfo = getPackageManager().getPackageInfo(CommonUtils.getPackageNameByUrl(updateAppInfoList.get(i).getUrl()), 0);
                            AppInfo appInfo = new AppInfo();
                            appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                            appInfo.setAppLabel(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                            appInfo.setNewVersion(updateAppInfoList.get(i).getVersion());
                            appInfo.setOldVersion(packageInfo.versionName);
                            appInfo.setRecentChange(updateAppInfoList.get(i).getRecentChange());
                            appInfo.setUpdateUrl(updateAppInfoList.get(i).getUrl());
                            appInfo.setPackageName(CommonUtils.getPackageNameByUrl(updateAppInfoList.get(i).getUrl()));
                            appInfoList.add(appInfo);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }
                Log.e("appinfo size", "" + appInfoList.size());
                if (appInfoList.isEmpty()) {
                    progress.setVisibility(View.GONE);
                    no_update_app_prompt_layout.setVisibility(View.VISIBLE);
                    request_failed.setVisibility(View.GONE);
                } else {
                    UpdateAdapter adapter = new UpdateAdapter(this, appInfoList);
                    updateView.setLayoutManager(new LinearLayoutManager(this));
                    updateView.setAdapter(adapter);
                    progress.setVisibility(View.GONE);
                    no_update_app_prompt_layout.setVisibility(View.GONE);
                    request_failed.setVisibility(View.GONE);
                }
            }
        }
    }

    private boolean isNewVersion(UpdateAppInfo appInfo) {
        try {
            Log.e("packageName", CommonUtils.getPackageNameByUrl(appInfo.getUrl()));
            PackageInfo packageInfo = getPackageManager().getPackageInfo(CommonUtils.getPackageNameByUrl(appInfo.getUrl()), 0);
            Log.e("old version", packageInfo.versionName);
            Log.e("new version", appInfo.getVersion());
            if (appInfo.getVersion().equals(packageInfo.versionName)) {
                Log.e("enqual", "not new Version");
                return false;
            } else {
                String[] appInfoVersionArray = appInfo.getVersion().split("\\.");
                //Log.e("new version array",appInfoVersionArray.toString());
                String[] packageVersionArray = packageInfo.versionName.split("\\.");
                Log.e("array size", appInfoVersionArray.length + " " + packageVersionArray.length);
                if (appInfoVersionArray.length >= 2 && packageVersionArray.length >= 2) {
                    int resultNewVersion = 0;
                    int resultOldVersion = 0;
                    for (int i = 0; i < Math.min(appInfoVersionArray.length, packageVersionArray.length); i++) {
//                        if (Integer.parseInt(appInfoVersionArray[i]) > Integer.parseInt(packageVersionArray[i])) {
//                            Log.e("not enqual", "is new Version");
//                            return true;
//                        }
                        try {
                            resultNewVersion += Integer.parseInt(appInfoVersionArray[i]) * Math.pow(10, Math.min(appInfoVersionArray.length, packageVersionArray.length) - i);
                            resultOldVersion += Integer.parseInt(packageVersionArray[i]) * Math.pow(10, Math.min(appInfoVersionArray.length, packageVersionArray.length) - i);
                        } catch (NumberFormatException e) {
                            return false;
                        }

                    }

                    if (resultNewVersion > resultOldVersion) {
                        Log.e("not enqual", resultNewVersion + " " + resultOldVersion);
                        return true;
                    }
                }
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.category_top_bar_back:
                finish();
                Bundle param = new Bundle();
                param.putString("Action", "iv_back_arrow");
                FirebaseStat.logEvent("AppUpdateBack", param);
                break;
            case R.id.request_fail_refresh:
                NetworkClient.startRequestQueryUpdateInfo(BaseApplication.getContext(), NetworkConstant.URL_QUERY_GPUPDATE, requestDataCallBack);
                request_failed.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseStat.setCurrentScreen(this, "UpdateActivity", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestDataCallBack = null;
    }
}
