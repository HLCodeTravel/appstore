package net.bat.store.ux.acticivty;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.transsion.http.impl.StringCallback;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.base.BaseBean;
import net.bat.store.bean.detail.AppDetailBean;
import net.bat.store.bean.detail.ResultAppMapBean;
import net.bat.store.bean.floor.AppMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.download.DownloadListener;
import net.bat.store.download.DownloadNotificationListener;
import net.bat.store.download.DownloadOpenFile;
import net.bat.store.download.DownloadState;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.download.InstallState;
import net.bat.store.network.NetworkClient;
import net.bat.store.utils.CommonUtils;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import static net.bat.store.constans.AppStoreConstant.SDCARD;


/**
 * Created by bingbing.li on 2017/1/11.
 */
public class AppDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AppDetailActivity";

    private ImageView v_loading;

    private DetailScrollView sv_content;

    private ImageView iv_top_banner;

    private TextView tv_grade_score;

    private TextView tv_grade_num;

    private ImageView iv_category;

    private TextView tv_download_num;

    private LinearLayout ly_download_num;

    private RatingBar rb_score;

    private TextView tv_category;

    private LinearLayout ly_screen_shots;

    private TextView tv_app_name;

    private ImageView iv_app_icon;


    private TextView tv_app_version;


    private TextView tv_app_size;

    private TextView tv_app_developer;

    private TextView tv_single_line_brief;

    private TextView tv_app_detail_desc;

    private TextView btn_download;

    private ProgressBar pb_current_size;

    DownloadTask task;
    AppMapBean appBean;
    AppDetailBean appDetailBean;
    private DownloadTaskManager manager;
    //下载中与下载完成任务列表
    private List<DownloadTask> downloadingTasks;
    private List<DownloadTask> finishedDownloadTasks;
    private List<DownloadTask> installCompleteTasks;
    private String foolertitle;
    private Context mContext;
    private String detailurl;
    private ActionBar mActionBar;
    private boolean mIsNewsFloor;



    private  StringCallback requestDataCallBack = new StringCallback() {
        @Override
        public void onFailure(int i, String s, Throwable throwable) {
            v_loading.setVisibility(View.GONE);
            sv_content.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(int code, String json_result) {
            if (code == 200) {
                Log.i(TAG, "onPostGet: "+json_result);
                Gson gson = new Gson();
                Type jsonType = new TypeToken<BaseBean<ResultAppMapBean>>() {
                }.getType();
                try {
                    BaseBean<ResultAppMapBean> baseBean = gson.fromJson(json_result, jsonType);
                    appDetailBean = baseBean.getResultMap().getAppMap();
                    initDownloadManager();
                    tv_app_name.setText(appDetailBean.getAppName());
                    tv_app_version.setText(appDetailBean.getAppVersionName());
                    tv_app_developer.setText(appDetailBean.getDeveloper());
                    tv_app_detail_desc.setText(appDetailBean.getDetailDesc());
                    if (TextUtils.isEmpty(appDetailBean.getAppSize())) {
                        tv_app_size.setText("");
                    } else {
                        tv_app_size.setText(appDetailBean.getAppSize() + "MB");
                    }
                    if(mIsNewsFloor){
                        mActionBar.setTitle(appDetailBean.getAppName());
                    }
                    Glide.with(mContext).load(appDetailBean.getAppCategoryIconUrl()).centerCrop().into(iv_category);
                    tv_category.setText(appDetailBean.getAppCategory());

                    Glide.with(mContext).load(appDetailBean.getIconUrl()).placeholder(R.drawable.image_default).centerCrop().into(iv_app_icon);
                    Glide.with(mContext).load(appDetailBean.getThemeUrl()).centerCrop().into(iv_top_banner);

                    Integer downloads = appDetailBean.getDownloadNumber() / 1000;
                    if (downloads <= 0) {
                        downloads = 1;
                    } else if (downloads >= 10000) {
                        downloads = downloads / 1000;
                        ly_download_num.setBackgroundResource(R.mipmap.download_icon_mill);
                    }
                    tv_download_num.setText(String.valueOf(downloads));
                    tv_grade_score.setText(appDetailBean.getAppGrade());
                    try {
                        rb_score.setRating(Float.parseFloat(appDetailBean.getAppGrade()));
                    } catch (Exception e) {
                        rb_score.setRating(5);
                    }

                    tv_grade_num.setText(ConstantUtils.addComma(appDetailBean.getCommentNumber()));

                    if (!TextUtils.isEmpty(detailurl)) {
                        if (detailurl.contains("leo.api.appDetail")) {
                            UploadLogManager.getInstance().generateResourceLog(mContext,6,appDetailBean.getAppNameEn(),appDetailBean.getAppAttribute(),1,"","");
                        }
                    }

                    String screenshotUrls = appDetailBean.getScreenshotUrl();
                    if (!TextUtils.isEmpty(screenshotUrls)) {
                        final String[] urls = screenshotUrls.split(",");
                        if (urls != null) {
                            for (int n = 0; n < urls.length; n++) {
                                final int m = n;
                                View view = View.inflate(AppDetailActivity.this, R.layout.item_app_detail_grid, null);
                                ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);

                                ly_screen_shots.addView(view);
                                Glide.with(mContext).load(urls[n]).centerCrop().into(imageView);

                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(AppDetailActivity.this, ImagePageActivity.class);
                                        intent.putExtra(ImagePageActivity.EXTRA_IMAGE_URLS, urls);
                                        intent.putExtra(ImagePageActivity.EXTRA_IMAGE_INDEX, m);
                                        startActivity(intent);
                                        Bundle param = new Bundle();
                                        param.putString("Action", "iv_icon");
                                        FirebaseStat.logEvent("AppScreenClick", param);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    }
                                });
                            }
                        }
                    }else{
                        findViewById(R.id.hs_screen_shots).setVisibility(View.GONE);
                    }
                    //判断是否为已下载或正在下载的文件
                    if (CommonUtils.isFinishedDownload(finishedDownloadTasks, appDetailBean.getAppName() + ".apk")) {
                        task = manager.queryDownloadTask(appDetailBean.getAppName() + ".apk");
                        if (task.getInstallState() == InstallState.INSTALLING) {
                            btn_download.setText(R.string.installing);
                        } else if (task.getInstallState() == InstallState.INSTALLFAILED) {
                            btn_download.setText(R.string.install_app);
                        }
                    } else if (CommonUtils.isInstalled(installCompleteTasks, appDetailBean.getAppName() + ".apk")) {
                        btn_download.setText(R.string.appcenter_open_text);
                    } else if (CommonUtils.isDownloading(downloadingTasks, appDetailBean.getAppName() + ".apk")) {
                        final DownloadTask oldtask = DownloadTaskManager.getInstance(mContext).queryDownloadTask(appDetailBean.getAppName() + ".apk");
                        if (oldtask != null) {
                            Log.e(TAG, oldtask.getDownloadState().toString());
                            task = oldtask;
                            if (task.getDownloadState().equals(DownloadState.DOWNLOADING) ||
                                    task.getDownloadState().equals(DownloadState.WAITING)) {
                                //btn_download.setVisibility(View.GONE);
                                btn_download.setBackgroundColor(Color.TRANSPARENT);
                                if (task.getDownloadState().equals(DownloadState.WAITING)) {
                                    btn_download.setText(R.string.download_status_init);
                                } else {
                                    btn_download.setText(R.string.download_status_pause);
                                }
                                pb_current_size.setMax((int) task.getTotalSize());
                                pb_current_size.setProgress((int) task.getFinishedSize());
                                pb_current_size.setVisibility(View.VISIBLE);
                            } else if (task.getDownloadState().equals(DownloadState.PAUSE)) {
                                btn_download.setText(getString(R.string.download_continue));
                            } else {
                                btn_download.setText(R.string.appcenter_download_text);
                            }
                        }
                    } else if (AppStoreConstant.APP_H5_GAME == appDetailBean.getAppAttribute()) {
                        btn_download.setText(R.string.appcenter_play);
                    } else {
                        btn_download.setText(R.string.appcenter_download_text);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            }

            v_loading.setVisibility(View.GONE);
            sv_content.setVisibility(View.VISIBLE);
        }
    };

//    private HttpUtils.HttpListener requestDataCallBack=new HttpUtils.HttpListener() {
//        /**
//         * @param httpResponse
//         */
//        @Override
//        protected void onPostGet(HttpResponse httpResponse) {
//            super.onPostGet(httpResponse);
//            if(httpResponse!=null) {
//                int code = httpResponse.getResponseCode();
//                Log.i(TAG, "onPostGet: " + code);
//                if (code == 200) {
//                    String json_result = httpResponse.getResponseBody();
//                    Log.i(TAG, "onPostGet: "+json_result);
//                    Gson gson = new Gson();
//                    Type jsonType = new TypeToken<BaseBean<ResultAppMapBean>>() {
//                    }.getType();
//                    try {
//                        BaseBean<ResultAppMapBean> baseBean = gson.fromJson(json_result, jsonType);
//                        appDetailBean = baseBean.getResultMap().getAppMap();
//                        initDownloadManager();
//                        tv_app_name.setText(appDetailBean.getAppName());
//                        tv_app_version.setText(appDetailBean.getAppVersionName());
//                        tv_app_developer.setText(appDetailBean.getDeveloper());
//                        tv_app_detail_desc.setText(appDetailBean.getDetailDesc());
//                        if (TextUtils.isEmpty(appDetailBean.getAppSize())) {
//                            tv_app_size.setText("");
//                        } else {
//                            tv_app_size.setText(appDetailBean.getAppSize() + "MB");
//                        }
//                        if(mIsNewsFloor){
//                            mActionBar.setTitle(appDetailBean.getAppName());
//                        }
//                        Glide.with(mContext).load(appDetailBean.getAppCategoryIconUrl()).centerCrop().into(iv_category);
//                        tv_category.setText(appDetailBean.getAppCategory());
//
//                        Glide.with(mContext).load(appDetailBean.getIconUrl()).placeholder(R.drawable.image_default).centerCrop().into(iv_app_icon);
//                        Glide.with(mContext).load(appDetailBean.getThemeUrl()).centerCrop().into(iv_top_banner);
//
//                        Integer downloads = appDetailBean.getDownloadNumber() / 1000;
//                        if (downloads <= 0) {
//                            downloads = 1;
//                        } else if (downloads >= 10000) {
//                            downloads = downloads / 1000;
//                            ly_download_num.setBackgroundResource(R.mipmap.download_icon_mill);
//                        }
//                        tv_download_num.setText(String.valueOf(downloads));
//                        tv_grade_score.setText(appDetailBean.getAppGrade());
//                        try {
//                            rb_score.setRating(Float.parseFloat(appDetailBean.getAppGrade()));
//                        } catch (Exception e) {
//                            rb_score.setRating(5);
//                        }
//
//                        tv_grade_num.setText(ConstantUtils.addComma(appDetailBean.getCommentNumber()));
//
//                        if (!TextUtils.isEmpty(detailurl)) {
//                            if (detailurl.contains("leo.api.appDetail")) {
//                                UploadLogManager.getInstance().generateResourceLog(mContext,6,appDetailBean.getAppNameEn(),appDetailBean.getAppAttribute(),1,"","");
//                            }
//                        }
//
//                        String screenshotUrls = appDetailBean.getScreenshotUrl();
//                        if (!TextUtils.isEmpty(screenshotUrls)) {
//                            final String[] urls = screenshotUrls.split(",");
//                            if (urls != null) {
//                                for (int n = 0; n < urls.length; n++) {
//                                    final int m = n;
//                                    View view = View.inflate(AppDetailActivity.this, R.layout.item_app_detail_grid, null);
//                                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
//
//                                    ly_screen_shots.addView(view);
//                                    Glide.with(mContext).load(urls[n]).centerCrop().into(imageView);
//
//                                    imageView.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Intent intent = new Intent(AppDetailActivity.this, ImagePageActivity.class);
//                                            intent.putExtra(ImagePageActivity.EXTRA_IMAGE_URLS, urls);
//                                            intent.putExtra(ImagePageActivity.EXTRA_IMAGE_INDEX, m);
//                                            startActivity(intent);
//                                            Bundle param = new Bundle();
//                                            param.putString("Action", "iv_icon");
//                                            FirebaseStat.logEvent("AppScreenClick", param);
//                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                                        }
//                                    });
//                                }
//                            }
//                        }else{
//                            findViewById(R.id.hs_screen_shots).setVisibility(View.GONE);
//                        }
//                        //判断是否为已下载或正在下载的文件
//                        if (CommonUtils.isFinishedDownload(finishedDownloadTasks, appDetailBean.getAppName() + ".apk")) {
//                            task = manager.queryDownloadTask(appDetailBean.getAppName() + ".apk");
//                            if (task.getInstallState() == InstallState.INSTALLING) {
//                                btn_download.setText(R.string.installing);
//                            } else if (task.getInstallState() == InstallState.INSTALLFAILED) {
//                                btn_download.setText(R.string.install_app);
//                            }
//                        } else if (CommonUtils.isInstalled(installCompleteTasks, appDetailBean.getAppName() + ".apk")) {
//                            btn_download.setText(R.string.appcenter_open_text);
//                        } else if (CommonUtils.isDownloading(downloadingTasks, appDetailBean.getAppName() + ".apk")) {
//                            final DownloadTask oldtask = DownloadTaskManager.getInstance(mContext).queryDownloadTask(appDetailBean.getAppName() + ".apk");
//                            if (oldtask != null) {
//                                Log.e(TAG, oldtask.getDownloadState().toString());
//                                task = oldtask;
//                                if (task.getDownloadState().equals(DownloadState.DOWNLOADING) ||
//                                        task.getDownloadState().equals(DownloadState.WAITING)) {
//                                    //btn_download.setVisibility(View.GONE);
//                                    btn_download.setBackgroundColor(Color.TRANSPARENT);
//                                    if (task.getDownloadState().equals(DownloadState.WAITING)) {
//                                        btn_download.setText(R.string.download_status_init);
//                                    } else {
//                                        btn_download.setText(R.string.download_status_pause);
//                                    }
//                                    pb_current_size.setMax((int) task.getTotalSize());
//                                    pb_current_size.setProgress((int) task.getFinishedSize());
//                                    pb_current_size.setVisibility(View.VISIBLE);
//                                } else if (task.getDownloadState().equals(DownloadState.PAUSE)) {
//                                    btn_download.setText(getString(R.string.download_continue));
//                                } else {
//                                    btn_download.setText(R.string.appcenter_download_text);
//                                }
//                            }
//                        } else if (AppStoreConstant.APP_H5_GAME == appDetailBean.getAppAttribute()) {
//                            btn_download.setText(R.string.appcenter_play);
//                        } else {
//                            btn_download.setText(R.string.appcenter_download_text);
//                        }
//                    } catch (JsonSyntaxException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }else{
//                Log.i(TAG, "onPostGet: null");
//            }
//            v_loading.setVisibility(View.GONE);
//            sv_content.setVisibility(View.VISIBLE);
//        }
//    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        mContext = BaseApplication.getContext();
        initView();
        btn_download.setOnClickListener(this);
        initData();
    }

    private void initView() {
        v_loading = (ImageView) findViewById(R.id.v_loading);
        Glide.with(this).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(v_loading);
        sv_content = (DetailScrollView) findViewById(R.id.sv_content);
        iv_top_banner = (ImageView) findViewById(R.id.iv_top_banner);
        tv_grade_score = (TextView) findViewById(R.id.tv_grade_score);
        tv_grade_num = (TextView) findViewById(R.id.tv_grade_num);
        iv_category = (ImageView) findViewById(R.id.iv_category);
        tv_download_num = (TextView) findViewById(R.id.tv_download_num);
        ly_download_num = (LinearLayout) findViewById(R.id.ly_download_num);
        rb_score = (RatingBar) findViewById(R.id.rb_score);
        tv_category = (TextView) findViewById(R.id.tv_category);
        ly_screen_shots = (LinearLayout) findViewById(R.id.ly_screen_shots);
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);
        iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
        tv_app_version = (TextView) findViewById(R.id.tv_app_version);
        tv_app_size = (TextView) findViewById(R.id.tv_app_size);
        tv_app_developer = (TextView) findViewById(R.id.tv_app_developer);
        tv_single_line_brief = (TextView) findViewById(R.id.tv_single_line_brief);
        tv_app_detail_desc = (TextView) findViewById(R.id.tv_app_detail_desc);
        btn_download = (TextView) findViewById(R.id.btn_download);
        pb_current_size = (ProgressBar) findViewById(R.id.pb_current_size);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                Bundle param = new Bundle();
                param.putString("Action", "iv_back");
                FirebaseStat.logEvent("AppDetailBack", param);
                break;
            case R.id.btn_download:
                if (appBean != null && appBean.getAppAttribute() == AppStoreConstant.APP_H5_GAME) {
                    Toast.makeText(mContext,R.string.not_supprt_game,Toast.LENGTH_SHORT).show();
//                    Game game = new Game(appBean.getAppName(), appBean.getAppUrl()
//                            , appBean.getAppId(), appBean.getIconUrl(), appBean.getThemeUrl()
//                            , appBean.getBriefDesc(), appBean.getAppSize()
//                            , appBean.getGrading(), appBean.getSlotId());
//                    GameManager.getInstance(mContext).startGame(this, game, 0);
//                    Bundle param1 = new Bundle();
//                    param1.putString("AppId", appBean.getAppNameEn());
//                    param1.putString("AppappCategory", appBean.getAppCategory());
//                    param1.putString("AppDeveloper", appBean.getDeveloper());
//                    param1.putInt("AppAttribute", appBean.getAppAttribute());
//                    FirebaseStat.logEvent("DLS_Id_" + appBean.getAppNameEn(), param1);
//                    if(!TextUtils.isEmpty(appBean.getAppCategory())){
//                        FirebaseStat.logEvent("DLS_Category_" + appBean.getAppCategory().replace(" ","_").replace("&","_"), param1);
//                    }
//                    if(!TextUtils.isEmpty(appBean.getDeveloper())){
//                        FirebaseStat.logEvent("DLS_Developer_" + appBean.getDeveloper().replace(" ","_").replace("&","_"), param1);
//                    }
//                    FirebaseStat.logEvent("DLS_Attribute_" + appBean.getAppAttribute(), param1);
//                    UploadLogManager.getInstance().generateResourceLog(mContext,1,appDetailBean.getAppNameEn(),appDetailBean.getAppAttribute(),2);
                    return;
                }
                if (task != null) {
                    if (manager.getListeners(task).size() < 3) {
                        manager.registerListener(task, new DetailDownloadListener());
                    }
                    if (task.getDownloadState() != null) {
                        switch (task.getDownloadState()) {
                            case FAILED:
                                manager.continueDownload(task);
                                Bundle param1 = new Bundle();
                                param1.putString("Action", "Fail");
                                FirebaseStat.logEvent("AppDetailFail", param1);
                                break;
                            case PAUSE:
                                manager.continueDownload(task);
                                Bundle param3 = new Bundle();
                                param3.putString("Action", "PAUSE");
                                FirebaseStat.logEvent("AppDetailPause", param3);
                                break;
                            case WAITING:
                            case DOWNLOADING:
                                manager.pauseDownload(task);
                                Bundle param4 = new Bundle();
                                param4.putString("Action", "DOWNLOADING");
                                FirebaseStat.logEvent("AppDetailPause", param4);
                                break;
                            case FINISHED:
                                if (ConstantUtils.isExistsProcess(task.getPackageName())) {
                                    if(appBean != null) {
                                        Intent resolveIntent = getPackageManager().getLaunchIntentForPackage(appBean.getPackageName());
                                        startActivity(resolveIntent);
                                        task.setInstallState(InstallState.INSTALLSUCCESS);
                                        manager.updateDownloadTask(task);
                                        UploadLogManager.getInstance().generateResourceLog(mContext,3,appBean.getAppNameEn(),appBean.getAppAttribute(),2,"","");
                                    }else{
                                        Intent resolveIntent = getPackageManager().getLaunchIntentForPackage(appDetailBean.getPackageName());
                                        startActivity(resolveIntent);
                                        task.setInstallState(InstallState.INSTALLSUCCESS);
                                        manager.updateDownloadTask(task);
                                        UploadLogManager.getInstance().generateResourceLog(mContext,3,appDetailBean.getAppNameEn(),appDetailBean.getAppAttribute(),2,"","");
                                    }
                                    Bundle param2 = new Bundle();
                                    param2.putString("Action", "FINISHED");
                                    FirebaseStat.logEvent("AppDetailFinish", param2);
                                } else {
                                    if (new File(task.getFilePath(),task.getFileName()).exists()) {
                                        Intent intent = DownloadOpenFile.getApkFileIntent(task.getFilePath() + "/"
                                                + task.getFileName());
                                        BaseApplication.getContext().startActivity(intent);
                                    } else {
                                        task.setDownloadState(DownloadState.WAITING);
                                        task.setInstallState(InstallState.INIT);

                                        // save to database if the download task is valid, and start download.
                                        if (!task.equals(manager.queryDownloadTask(task.getFileName()))) {
                                            manager.insertDownloadTask(task);
                                        }

                                        CommonUtils.getDwonloadTask().add(task);
                                        manager.updateDownloadTask(task);
                                        manager.startDownload(task);
                                    }
                                }
                                break;
                        }
                    } else {
                        if (appBean != null) {

                            if (manager.queryDownloadTask(appBean.getAppName() + ".apk") == null && appBean.getAppAttribute() == AppStoreConstant.APP_SELF) {

                                task.setDownloadState(DownloadState.WAITING);
                                task.setInstallState(InstallState.INIT);
                                task.setAppId(appBean.getAppId());

                                // save to database if the download task is valid, and start download.
                                if (!task.equals(manager.queryDownloadTask(task.getFileName()))) {
                                    manager.insertDownloadTask(task);
                                }
                                CommonUtils.getDwonloadTask().add(task);
                                manager.updateDownloadTask(task);
                                manager.startDownload(task);
                                UploadLogManager.getInstance().generateResourceLog(mContext,2,appBean.getAppNameEn(),appBean.getAppAttribute(),2,"","");
                            } else {
                                Log.e(TAG, "OPNE GOOGLE APP");
                                Uri uri = Uri.parse(appBean.getAppUrl());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }


                        } else {

                            if (appDetailBean.getAppAttribute() == 0) {
                                Toast.makeText(mContext, R.string.invaild_banner_link, Toast.LENGTH_SHORT).show();
                                Bundle param1 = new Bundle();
                                param1.putString("Action", "ErrorAction");
                                FirebaseStat.logEvent("Error_Notification", param1);
                                return;
                            }
                            if (manager.queryDownloadTask(appDetailBean.getAppName() + ".apk") == null && appDetailBean.getAppAttribute() == AppStoreConstant.APP_SELF) {

                                task.setDownloadState(DownloadState.WAITING);
                                task.setInstallState(InstallState.INIT);
                                task.setDownloadstyle("FromNotification");
                                task.setAppId(appDetailBean.getAppId());
                                task.setPackageName(appDetailBean.getPackageName());
                                // save to database if the download task is valid, and start download.
                                if (!task.equals(manager.queryDownloadTask(task.getFileName()))) {
                                    manager.insertDownloadTask(task);
                                }
                                CommonUtils.getDwonloadTask().add(task);
                                manager.updateDownloadTask(task);
                                manager.startDownload(task);
                                UploadLogManager.getInstance().generateResourceLog(mContext,2,appDetailBean.getAppNameEn(),appDetailBean.getAppAttribute(),2,"","");
                            } else {
                                Log.e(TAG, "OPNE GOOGLE APP");
                                Uri uri = Uri.parse(appDetailBean.getAppUrl());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }

                    }
                }
                break;
        }
    }


    private void initData() {
        foolertitle = (String) getIntent().getSerializableExtra("floorname");
        appBean = (AppMapBean) getIntent().getSerializableExtra("appBean");
        detailurl = getIntent().getStringExtra("notificationurl");
        mIsNewsFloor=getIntent().getBooleanExtra("newsfloor",false);
        //utils = DownloadUtils.getInstance(getApplicationContext());
        manager = DownloadTaskManager.getInstance(mContext);
        downloadingTasks = manager.getDownloadingTask();
        finishedDownloadTasks = manager.getFinishedDownloadTask();
        installCompleteTasks = manager.getInstalledTask();

        mActionBar=getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //缺少H5判断
        if (appBean != null) {
            if (TextUtils.isEmpty(appBean.getAppSize())) {
                tv_app_size.setText("");
            } else {
                tv_app_size.setText(appBean.getAppSize() + "MB");
            }

            tv_app_name.setText(appBean.getAppName());
            tv_single_line_brief.setText(appBean.getBriefDesc());
            mActionBar.setTitle(appBean.getAppName());
            Glide.with(mContext).load(appBean.getAppCategoryIconUrl()).centerCrop().into(iv_category);
            tv_category.setText(appBean.getAppCategory());

            Glide.with(mContext).load(appBean.getIconUrl()).placeholder(R.drawable.image_default).centerCrop().into(iv_app_icon);
            Glide.with(mContext).load(appBean.getThemeUrl()).centerCrop().into(iv_top_banner);
            refreshBnStatus();
        }else if (appDetailBean != null){
            if (TextUtils.isEmpty(appDetailBean.getAppSize())) {
                tv_app_size.setText("");
            } else {
                tv_app_size.setText(appDetailBean.getAppSize() + "MB");
            }

            tv_app_name.setText(appDetailBean.getAppName());
            mActionBar.setTitle(appDetailBean.getAppName());
            tv_single_line_brief.setText(appDetailBean.getBriefDesc());

            Glide.with(mContext).load(appDetailBean.getAppCategoryIconUrl()).centerCrop().into(iv_category);
            tv_category.setText(appDetailBean.getAppCategory());

            Glide.with(mContext).load(appDetailBean.getIconUrl()).placeholder(R.drawable.image_default).centerCrop().into(iv_app_icon);
            Glide.with(mContext).load(appDetailBean.getThemeUrl()).centerCrop().into(iv_top_banner);
            refreshBnStatusFromNotify();
        }


        if (!TextUtils.isEmpty(detailurl)) {
            if (detailurl.contains("leo.api.appDetail")) {
                NetworkClient.startRequestQueryAppDetail(mContext, detailurl, requestDataCallBack);
            } else {
                Toast.makeText(mContext, R.string.invaild_banner_link, Toast.LENGTH_SHORT).show();
                AppDetailActivity.this.finish();
                return;
            }
        } else {
            NetworkClient.startRequestQueryAppDetail(mContext, appBean.getDetailPageUrl(), requestDataCallBack);
        }
    }

    private void refreshBnStatusFromNotify() {
        if (CommonUtils.isFinishedDownload(finishedDownloadTasks, appDetailBean.getAppName() + ".apk")) {
            task = manager.queryDownloadTask(appDetailBean.getAppName() + ".apk");
            if (task.getInstallState() == InstallState.INSTALLING) {
                btn_download.setText(R.string.installing);
            } else if (task.getInstallState() == InstallState.INSTALLFAILED) {
                if(new File(task.getFilePath(),task.getFileName()).exists()){
                    btn_download.setText(R.string.install_app);
                }else{
                    btn_download.setText(R.string.appcenter_download_text);
                    finishedDownloadTasks.clear();
                    downloadingTasks.clear();
                    installCompleteTasks.clear();
                    task.setDownloadState(null);
                    manager.deleteDownloadTask(task);
                }
            }
        } else if (CommonUtils.isInstalled(installCompleteTasks, appDetailBean.getAppName() + ".apk")) {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(appDetailBean.getPackageName(), PackageManager.GET_ACTIVITIES);
                btn_download.setText(R.string.appcenter_open_text);
            } catch (PackageManager.NameNotFoundException e) {
                if(new File(task.getFilePath(),task.getFileName()).exists()){
                    btn_download.setText(R.string.install_app);
                }else{
                    btn_download.setText(R.string.appcenter_download_text);
                    finishedDownloadTasks.clear();
                    downloadingTasks.clear();
                    installCompleteTasks.clear();
                    task.setDownloadState(null);
                    manager.deleteDownloadTask(task);
                }
            }
        } else if (CommonUtils.isDownloading(downloadingTasks, appDetailBean.getAppName() + ".apk")) {
            final DownloadTask oldtask = DownloadTaskManager.getInstance(mContext).queryDownloadTask(appDetailBean.getAppName() + ".apk");
            if (oldtask != null) {
                Log.e(TAG, oldtask.getDownloadState().toString());
                task = oldtask;
                if (task.getDownloadState().equals(DownloadState.DOWNLOADING) ||
                        task.getDownloadState().equals(DownloadState.WAITING)) {
                    //btn_download.setVisibility(View.GONE);
                    btn_download.setBackgroundColor(Color.TRANSPARENT);
                    if (task.getDownloadState().equals(DownloadState.WAITING)) {
                        btn_download.setText(R.string.download_status_init);
                        pb_current_size.setVisibility(View.VISIBLE);
                        pb_current_size.setProgress(0);
                    } else {
                        btn_download.setText(R.string.download_status_pause);
                        pb_current_size.setMax((int) task.getTotalSize());
                        pb_current_size.setProgress((int) task.getFinishedSize());
                        pb_current_size.setVisibility(View.VISIBLE);
                    }

                }else if (task.getDownloadState().equals(DownloadState.PAUSE)) {
                    btn_download.setText(this.getString(R.string.download_continue));
                }else if(DownloadState.FINISHED.equals(task.getDownloadState())){
                    pb_current_size.setVisibility(View.GONE);
                    btn_download.setTextColor(AppDetailActivity.this.getResources().getColor(R.color.appcenter_free_normal_color));
                    btn_download.setBackgroundResource(R.drawable.app_detail_btn);
                    btn_download.setText(R.string.install_app);
                }else {
                    btn_download.setText(R.string.appcenter_download_text);
                }
            }
        } else if (AppStoreConstant.APP_H5_GAME == appDetailBean.getAppAttribute()) {
            btn_download.setText(R.string.appcenter_play);
        } else {
            btn_download.setText(R.string.appcenter_download_text);
        }
    }

    private void refreshBnStatus() {
        if (CommonUtils.isFinishedDownload(finishedDownloadTasks, appBean.getAppName() + ".apk")) {
            task = manager.queryDownloadTask(appBean.getAppName() + ".apk");
            if (task.getInstallState() == InstallState.INSTALLING) {
                btn_download.setText(R.string.installing);
            } else if (task.getInstallState() == InstallState.INSTALLFAILED) {
                if(new File(task.getFilePath(),task.getFileName()).exists()){
                    btn_download.setText(R.string.install_app);
                }else{
                    btn_download.setText(R.string.appcenter_download_text);
                    finishedDownloadTasks.clear();
                    downloadingTasks.clear();
                    installCompleteTasks.clear();
                    task.setDownloadState(null);
                    manager.deleteDownloadTask(task);
                }
            }
        } else if (CommonUtils.isInstalled(installCompleteTasks, appBean.getAppName() + ".apk")) {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(appBean.getPackageName(), PackageManager.GET_ACTIVITIES);
                btn_download.setText(R.string.appcenter_open_text);
            } catch (PackageManager.NameNotFoundException e) {
                if(new File(task.getFilePath(),task.getFileName()).exists()){
                    btn_download.setText(R.string.install_app);
                }else{
                    btn_download.setText(R.string.appcenter_download_text);
                    finishedDownloadTasks.clear();
                    downloadingTasks.clear();
                    installCompleteTasks.clear();
                    task.setDownloadState(null);
                    manager.deleteDownloadTask(task);
                }
            }
        } else if (CommonUtils.isDownloading(downloadingTasks, appBean.getAppName() + ".apk")) {
            final DownloadTask oldtask = DownloadTaskManager.getInstance(mContext).queryDownloadTask(appBean.getAppName() + ".apk");
            if (oldtask != null) {
                Log.e(TAG, oldtask.getDownloadState().toString());
                task = oldtask;
                if (task.getDownloadState().equals(DownloadState.DOWNLOADING) ||
                        task.getDownloadState().equals(DownloadState.WAITING)) {
                    //btn_download.setVisibility(View.GONE);
                    btn_download.setBackgroundColor(Color.TRANSPARENT);
                    if (task.getDownloadState().equals(DownloadState.WAITING)) {
                        btn_download.setText(R.string.download_status_init);
                        pb_current_size.setVisibility(View.VISIBLE);
                        pb_current_size.setProgress(0);
                    } else {
                        btn_download.setText(R.string.download_status_pause);
                        pb_current_size.setMax((int) task.getTotalSize());
                        pb_current_size.setProgress((int) task.getFinishedSize());
                        pb_current_size.setVisibility(View.VISIBLE);
                    }

                } else if (task.getDownloadState().equals(DownloadState.PAUSE)) {
                    btn_download.setText(this.getString(R.string.download_continue));
                }else if(DownloadState.FINISHED.equals(task.getDownloadState())){
                    pb_current_size.setVisibility(View.GONE);
                    btn_download.setTextColor(AppDetailActivity.this.getResources().getColor(R.color.appcenter_free_normal_color));
                    btn_download.setBackgroundResource(R.drawable.app_detail_btn);
                    btn_download.setText(R.string.install_app);
                }else {
                    btn_download.setText(R.string.appcenter_download_text);
                }
            }
        } else if (AppStoreConstant.APP_H5_GAME == appBean.getAppAttribute()) {
            btn_download.setText(R.string.appcenter_play);
        } else {
            btn_download.setText(R.string.appcenter_download_text);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initDownloadManager() {
        if(manager==null){
            manager = DownloadTaskManager.getInstance(mContext);
        }
        if (appBean != null) {
            if (manager.queryDownloadTask(appBean.getAppName() + ".apk") != null) {
                task = manager.queryDownloadTask(appBean.getAppName() + ".apk");
            } else {
                Log.e(TAG, SDCARD);
                task = new DownloadTask(appBean.getAppUrl(), appBean.getPackageName(),
                        ConstantUtils.getDownloadPath(), appBean.getAppName() + ".apk", appBean.getAppName(), null, foolertitle, appBean.getAppCategory(), appBean.getDeveloper(), appBean.getAppAttribute(), appBean.getFloorId(), appBean.getAppId(),appBean.getAppNameEn());
            }
            manager.registerListener(task, new DownloadNotificationListener(BaseApplication.getContext(), task));
            manager.registerListener(task, new DetailDownloadListener());
        } else {
            if (appDetailBean != null) {
                if (manager.queryDownloadTask(appDetailBean.getAppName() + ".apk") != null) {
                    task = manager.queryDownloadTask(appDetailBean.getAppName() + ".apk");
                } else {
                    Log.e(TAG, SDCARD);
                    task = new DownloadTask(appDetailBean.getAppUrl(), appDetailBean.getPackageName(),
                            ConstantUtils.getDownloadPath(), appDetailBean.getAppName() + ".apk", appDetailBean.getAppName(), null, foolertitle, appDetailBean.getAppCategory(), appDetailBean.getDeveloper(), appDetailBean.getAppAttribute(), null, appDetailBean.getAppId(),appDetailBean.getAppNameEn());
                }
                manager.registerListener(task, new DownloadNotificationListener(BaseApplication.getContext(), task));
                manager.registerListener(task, new DetailDownloadListener());
            }
        }
        //DownloadTaskManager.getInstance(BaseApplication.getContext()).startDownload(task);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
        if(appBean!=null){
            refreshBnStatus();
        }else if(appDetailBean!=null){
            refreshBnStatusFromNotify();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseStat.setCurrentScreen(this, "AppDetailActivity", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestDataCallBack = null;
        //task = null;
        appBean = null;
        appDetailBean = null;
        manager = null;
        if (downloadingTasks != null) {
            downloadingTasks.clear();
            downloadingTasks = null;
        }
        if (finishedDownloadTasks != null) {
            finishedDownloadTasks.clear();
            finishedDownloadTasks = null;
        }
        if (installCompleteTasks != null) {
            installCompleteTasks.clear();
            installCompleteTasks = null;
        }

        Iterator<DownloadListener> iterator = DownloadTaskManager.getInstance(mContext).getListeners(task).iterator();
        while (iterator.hasNext()) {
            DownloadListener listener = iterator.next();
            if (listener instanceof DetailDownloadListener) {
                DownloadTaskManager.getInstance(mContext).getListeners(task).remove(listener);
            }
        }
    }

    class DetailDownloadListener implements DownloadListener {
        @Override
        public void onDownloadInit() {
            task.setDownloadState(DownloadState.WAITING);
            AppDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_download.setBackgroundColor(Color.TRANSPARENT);
                    //pb_current_size.setMax((int) task.getTotalSize());
                    //pb_current_size.setProgress((int) task.getFinishedSize());
                    pb_current_size.setVisibility(View.VISIBLE);
                    pb_current_size.setProgress(0);
                    btn_download.setTextColor(AppDetailActivity.this.getResources().getColor(R.color.skin_download_item_file_size_color));
                    btn_download.setText(R.string.download_status_init);
                }
            });
        }

        @Override
        public void onDownloadFinish(final String filepath) {
            task.setDownloadState(DownloadState.FINISHED);
            finishedDownloadTasks.add(task);
            AppDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "install");
                    //DownloadOpenFile.installAPK(filepath);
                    pb_current_size.setVisibility(View.GONE);
                    btn_download.setTextColor(AppDetailActivity.this.getResources().getColor(R.color.appcenter_free_normal_color));
                    btn_download.setBackgroundResource(R.drawable.app_detail_btn);
                    if (!ConstantUtils.isExistsProcess(task.getPackageName())) {
                        btn_download.setText(R.string.install_app);
                    } else {
                        btn_download.setText(R.string.appcenter_open_text);
                    }

                }
            });
        }

        @Override
        public void onDownloadStart() {
            Log.e(TAG, "START");
            task.setDownloadState(DownloadState.DOWNLOADING);
            AppDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    pb_current_size.setVisibility(View.VISIBLE);
                    btn_download.setBackgroundColor(Color.TRANSPARENT);
                    if (task.getTotalSize() != -1) {
                        pb_current_size.setMax((int) task.getTotalSize());
                        pb_current_size.setProgress((int) task.getFinishedSize());
                    }
                    btn_download.setTextColor(AppDetailActivity.this.getResources().getColor(R.color.skin_download_item_file_size_color));
                    btn_download.setText(R.string.download_status_pause);
                }
            });

        }

        @Override
        public void onDownloadPause() {
            task.setDownloadState(DownloadState.PAUSE);
            AppDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_download.setTextColor(AppDetailActivity.this.getResources().getColor(R.color.appcenter_free_normal_color));
                    btn_download.setText(AppDetailActivity.this.getString(R.string.download_continue));
                    btn_download.setBackgroundResource(R.drawable.app_detail_btn);
                    pb_current_size.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onDownloadStop() {
            AppDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_download.setTextColor(AppDetailActivity.this.getResources().getColor(R.color.appcenter_free_normal_color));
                    btn_download.setText(AppDetailActivity.this.getString(R.string.appcenter_download_text));
                    btn_download.setBackgroundResource(R.drawable.app_detail_btn);
                    pb_current_size.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onDownloadFail() {
            Log.e(TAG, "FAIL");
            task.setDownloadState(DownloadState.FAILED);
            AppDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_download.setBackgroundResource(R.drawable.app_detail_btn);
                    btn_download.setTextColor(AppDetailActivity.this.getResources().getColor(R.color.appcenter_free_normal_color));
                    pb_current_size.setVisibility(View.GONE);
                    btn_download.setText(R.string.appcenter_download_text);
                    //Toast.makeText(AppDetailActivity.this,R.string.download_error,Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onDownloadProgress(long finishedSize, long totalSize, int speed) {
            if (speed == 0) {
                Log.e(TAG, "speed = 0");
            }
            task.setDownloadState(DownloadState.DOWNLOADING);
            pb_current_size.setMax((int) totalSize);
            pb_current_size.setProgress((int) finishedSize);
            pb_current_size.setVisibility(View.VISIBLE);
            btn_download.setBackgroundColor(AppDetailActivity.this.getResources().getColor(R.color.skin_download_item_file_size_color));
            btn_download.setBackgroundColor(Color.TRANSPARENT);
            btn_download.setText(R.string.download_status_pause);

        }

        @Override
        public void onInstallComplete() {
            installCompleteTasks.add(task);
            AppDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    task.setInstallState(InstallState.INSTALLSUCCESS);
                    //Toast.makeText(AppDetailActivity.this,"安装成功",Toast.LENGTH_LONG).show();
                    btn_download.setText(R.string.appcenter_open_text);
                }
            });
        }

        @Override
        public void onInstallFail() {
            AppDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    task.setInstallState(InstallState.INSTALLFAILED);
                    btn_download.setText(R.string.install_app);
                }
            });
        }
    }


}
