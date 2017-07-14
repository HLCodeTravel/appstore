package net.bat.store.ux.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.transsion.iad.core.TAdError;
import com.transsion.iad.core.TAdListener;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.bean.Ad;
import com.transsion.iad.core.bean.TAdNativeInfo;

import net.bat.store.BaseApplication;
import net.bat.store.R;
import net.bat.store.bean.floor.AppMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.download.DownloadNotificationListener;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.utils.CommonUtils;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.GlideRoundTransform;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.acticivty.AppDetailActivity;
import net.bat.store.ux.acticivty.H5Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.bat.store.download.InstallState.INSTALLFAILED;

/**
 * Created by liang.he on 2017/7/7.
 */

public class FloorListCardItemHolder extends RecyclerView.ViewHolder {

    public ImageView iv_app_icon;
    public TextView tv_app_label;
    public RatingBar rb_score;
    public TextView tv_filesize;
    public Button btn_download;
    public TextView tv_app_rank;
    public TextView tv_desc;
    public ProgressBar progressBar;
    Context mContext = null;
    private List<DownloadTask> mDownloadTaskList;
    private DownloadTask mDownloadTask;
    private DownloadTaskManager manager;

    public FloorListCardItemHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        iv_app_icon = (ImageView) itemView.findViewById(R.id.iv_app_icon);
        tv_app_label = (TextView) itemView.findViewById(R.id.tv_app_label);
        tv_desc = (TextView) itemView.findViewById(R.id.tv_description);
        rb_score = (RatingBar) itemView.findViewById(R.id.rb_score);
        tv_filesize = (TextView) itemView.findViewById(R.id.tv_filesize);
        btn_download = (Button) itemView.findViewById(R.id.btn_download);
        tv_app_rank = (TextView) itemView.findViewById(R.id.tv_app_rank);
        progressBar = (ProgressBar) itemView.findViewById(R.id.pb_current_size);
        manager = DownloadTaskManager.getInstance(BaseApplication.getContext());

    }


    public void OnBinderData(final AppMapBean appMapBean, int postion, int isShowTag, final String floorId, final String floorTitle) {
        String sold = appMapBean.getSlotId();
        String appId = appMapBean.getAppId();

        if (!TextUtils.isEmpty(sold) && !mNativeInfoMaps.containsKey(appId)) {
            requestAdNative(appId, sold);
        }

        itemClick(appMapBean, floorId, postion);

        tv_app_label.setText(appMapBean.getAppName());
        tv_filesize.setText(ConstantUtils.getAppSize(appMapBean.getAppSize()));
        tv_desc.setText(appMapBean.getBriefDesc());
        if (isShowTag == 1) {
            if (ConstantUtils.LAN_AR.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_FA.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_UR_RPK.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_IW.equals(ConstantUtils.getLanguage())) {
                setRight(postion, tv_app_rank);
            } else {
                setRank(postion, tv_app_rank);
            }
        } else {
            tv_app_rank.setVisibility(View.GONE);
        }
        try {
            rb_score.setRating(Float.parseFloat(appMapBean.getAppGrade()));
        } catch (Exception e) {
            rb_score.setRating(5);
        }

        int appType = appMapBean.getAppAttribute();
        if (appType == AppStoreConstant.APP_H5) {
            tv_filesize.setVisibility(View.INVISIBLE);
            btn_download.setText(R.string.appcenter_play);
        } else if (AppStoreConstant.APP_H5_GAME == appType) {
            tv_filesize.setVisibility(View.VISIBLE);
            btn_download.setText(R.string.appcenter_play);
        } else if (AppStoreConstant.APP_GOOGLE == appType || AppStoreConstant.APP_APPSFLER == appType) {
            tv_filesize.setVisibility(View.VISIBLE);
            btn_download.setText(R.string.appcenter_download_text);
        } else if (appType == AppStoreConstant.APP_SELF) {
            tv_filesize.setVisibility(View.VISIBLE);
            btn_download.setText(R.string.appcenter_download_text);
            mDownloadTaskList = CommonUtils.getDwonloadTask();
            for (DownloadTask downloadTask : mDownloadTaskList) {
                if (appMapBean.getAppId().equals(downloadTask.getAppId())) {
                    mDownloadTask = downloadTask;
                    break;
                }
            }
            if (ConstantUtils.isExistsProcess(appMapBean.getPackageName())) {
                btn_download.setText(R.string.appcenter_open_text);
            } else {
                if (mDownloadTask != null && mDownloadTask.getAppId().equals(appMapBean.getAppId())) {
                    mDownloadTask.setInstallState(INSTALLFAILED);
                    CommonUtils.refreshDownloadView(manager, mDownloadTask, btn_download, progressBar);
                }
            }
        }
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int appType = appMapBean.getAppAttribute();
                if (AppStoreConstant.APP_SELF == appType) {
                    if (ConstantUtils.isExistsProcess(appMapBean.getPackageName())) {
                        Intent resolveIntent = BaseApplication.getContext().getPackageManager().getLaunchIntentForPackage(appMapBean.getPackageName());
                        BaseApplication.getContext().startActivity(resolveIntent);
                        return;
                    }
                    for (DownloadTask downloadTask : mDownloadTaskList) {
                        if (appMapBean.getAppId().equals(downloadTask.getAppId())) {
                            mDownloadTask = downloadTask;
                            break;
                        }
                    }
                    if (mDownloadTaskList.size() == 0) {
                        mDownloadTask = null;
                    }
                    if (mDownloadTask == null || !mDownloadTask.getAppId().equals(appMapBean.getAppId())) {
                        mDownloadTask = new DownloadTask(appMapBean.getAppUrl(), appMapBean.getPackageName(),
                                ConstantUtils.getDownloadPath(), appMapBean.getAppName() + ".apk", appMapBean.getAppName()
                                , null, floorTitle, appMapBean.getAppCategory()
                                , appMapBean.getDeveloper(), appMapBean.getAppAttribute(), floorId
                                , appMapBean.getAppId(), appMapBean.getAppNameEn());
                        if (manager.getListeners(mDownloadTask).isEmpty()) {
                            manager.registerListener(mDownloadTask, new DownloadNotificationListener(BaseApplication.getContext(), mDownloadTask));
                        }
                    }
                    if (manager.getListeners(mDownloadTask).size() < 2) {
                        manager.registerListener(mDownloadTask, new DownloadNotificationListener(BaseApplication.getContext(), mDownloadTask));
                    }
                    if (mDownloadTaskList.indexOf(mDownloadTask) != -1) {
                        CommonUtils.downloadBtnClick(manager, mDownloadTask, FloorListCardItemHolder.this);
                    } else {
                        CommonUtils.downloadBtnClick(manager, mDownloadTask, FloorListCardItemHolder.this);
                        UploadLogManager.getInstance().generateResourceLog(mContext, 2, appMapBean.getAppNameEn(), appMapBean.getAppAttribute(), 2, "", "");
                    }
                    Bundle param = new Bundle();
                    param.putString("ListNormalName", appMapBean.getAppNameEn());
                    param.putString("ListNormalStyle", appMapBean.getAppCategory());
                    FirebaseStat.logEvent("ListNormalClick", param);
                } else if (AppStoreConstant.APP_H5 == appType || appType == AppStoreConstant.APP_APPSFLER) {//H5游戏
                    thirdFireBaseSta(appMapBean, floorId);
                    H5Activity.openLink(mContext, appMapBean.getAppUrl());
                } else if (AppStoreConstant.APP_GOOGLE == appType) {//Google应用
                    thirdFireBaseSta(appMapBean, floorId);
                    String uriString = appMapBean.getAppUrl();
                    if (ConstantUtils.isExistsProcess(AppStoreConstant.PKG_GOOGLE_PLAY)) {
                        ConstantUtils.goGooglePlayMarket(uriString, mContext);
                    } else {
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                } else if (AppStoreConstant.APP_H5_GAME == appType) {
                    Toast.makeText(mContext, R.string.not_supprt_game, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Glide.with(mContext)
                .load(appMapBean.getIconUrl())
                .placeholder(R.drawable.image_default)
                .centerCrop()
                .transform(new GlideRoundTransform(mContext, 12))
                .into(iv_app_icon);
    }


    private Map<String, TAdNativeInfo> mNativeInfoMaps = new HashMap<>();
    private Map<TAdNativeInfo, TAdNative> mAdNativeMaps = new HashMap<>();
    private List<String> mTempRequestSolds = new ArrayList<>();

    void requestAdNative(final String appId, final String slodId) {
        if (mTempRequestSolds.contains(appId)) {
            return;
        } else {
            mTempRequestSolds.add(appId);
        }
        final TAdNative tAdNative = new TAdNative(BaseApplication.getContext(), slodId);
        tAdNative.setAdListener(new TAdListener() {
            @Override
            public void onAdLoaded(Ad ad) {
                if (ad != null && ad instanceof TAdNativeInfo) {
                    TAdNativeInfo tAdNativeInfo = (TAdNativeInfo) ad;
                    //Log.i(TAG, "title:" + tAdNativeInfo.getTitle());
                    mNativeInfoMaps.put(appId, tAdNativeInfo);
                    mAdNativeMaps.put(tAdNativeInfo, tAdNative);
                } else {
                    mTempRequestSolds.remove(appId);
                }
            }

            @Override
            public void onError(TAdError adError) {
                // Log.i(TAG, "adError:" + adError.getErrorMessage() + adError.getErrorCode());
                mTempRequestSolds.remove(appId);
            }
        });
        tAdNative.loadAd();
    }


    private void itemClick(final AppMapBean appMapBean,
                           final String title, final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int appType = appMapBean.getAppAttribute();
                if (AppStoreConstant.APP_SELF == appType
                        || AppStoreConstant.APP_H5_GAME == appType) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, appMapBean.getAppNameEn());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, position + "");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Normal+List_" + title);
                    FirebaseStat.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                } else if (AppStoreConstant.APP_H5 == appType ||
                        AppStoreConstant.APP_GOOGLE == appType || appType == AppStoreConstant.APP_APPSFLER) {
                    thirdFireBaseSta(appMapBean, title);
                }
                onClickItem(appMapBean, position);
            }
        });
    }


    private void onClickItem(AppMapBean appMapBean, int position) {
        int appType = appMapBean.getAppAttribute();
        if (AppStoreConstant.APP_SELF == appType
                || AppStoreConstant.APP_H5_GAME == appType) {
            Intent intent = new Intent(mContext, AppDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("appBean", appMapBean);
            bundle.putInt("position", position);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        } else if (AppStoreConstant.APP_H5 == appType || appType == AppStoreConstant.APP_APPSFLER) {//H5游戏
            H5Activity.openLink(mContext, appMapBean.getAppUrl());
        } else if (AppStoreConstant.APP_GOOGLE == appType) {//Google应用
            String uriString = appMapBean.getAppUrl();
            if (ConstantUtils.isExistsProcess(AppStoreConstant.PKG_GOOGLE_PLAY)) {
                ConstantUtils.goGooglePlayMarket(uriString, mContext);
            } else {
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    }

    private void thirdFireBaseSta(AppMapBean appMapBean, String foorId) {
        Bundle param1 = new Bundle();
        param1.putString("AppId", appMapBean.getAppNameEn());
        param1.putString("Appfloortitle", foorId);
        param1.putString("AppappCategory", appMapBean.getAppCategory());
        param1.putString("AppDeveloper", appMapBean.getDeveloper());
        param1.putInt("AppAttribute", appMapBean.getAppAttribute());
        FirebaseStat.logEvent("DLS_Id_" + appMapBean.getAppNameEn(), param1);
        FirebaseStat.logEvent("DLS_FloorId_" + foorId, param1);
        if (!TextUtils.isEmpty(appMapBean.getAppCategory())) {
            FirebaseStat.logEvent("DLS_Category_" + appMapBean.getAppCategory().replace(" ", "_").replace("&", "_"), param1);
        }
        if (!TextUtils.isEmpty(appMapBean.getDeveloper())) {
            FirebaseStat.logEvent("DLS_Developer_" + appMapBean.getDeveloper().replace(" ", "_").replace("&", "_"), param1);
        }
        FirebaseStat.logEvent("DLS_Attribute_" + appMapBean.getAppAttribute(), param1);
        UploadLogManager.getInstance().generateResourceLog(mContext, 1, appMapBean.getAppNameEn(), appMapBean.getAppAttribute(), 2, "", "");
    }

    private void setRight(int index, TextView textview) {

        switch (index) {
            case 0:
                textview.setBackgroundResource(R.mipmap.appcenter_app_top_one);
                textview.setText("");
                break;
            case 1:
                textview.setBackgroundResource(R.mipmap.appcenter_app_top_two);
                textview.setText("");
                break;
            case 2:
                textview.setBackgroundResource(R.mipmap.appcenter_app_top_three);
                textview.setText("");
                break;
            default:
                textview.setBackgroundResource(R.mipmap.appcenter_app_top_normal_right);
                int rank = index + 1;
                if (index < 10) {
                    textview.setText(" " + rank);
                } else if (index < 100) {
                    textview.setText("" + rank);
                } else {
                    textview.setText("");
                    textview.setBackgroundDrawable(null);
                }
                break;
        }
    }

    private void setRank(int index, TextView textview) {
        switch (index) {
            case 0:
                textview.setBackgroundResource(R.mipmap.appcenter_app_top_one);
                textview.setText("");
                break;
            case 1:
                textview.setBackgroundResource(R.mipmap.appcenter_app_top_two);
                textview.setText("");
                break;
            case 2:
                textview.setBackgroundResource(R.mipmap.appcenter_app_top_three);
                textview.setText("");
                break;
            default:
                textview.setBackgroundResource(R.mipmap.appcenter_app_top_normal);
                int rank = index + 1;
                if (index < 10) {
                    textview.setText(" " + rank);
                } else if (index < 100) {
                    textview.setText("" + rank);
                } else {
                    textview.setText("");
                    textview.setBackgroundDrawable(null);
                }
                break;
        }
    }
}
