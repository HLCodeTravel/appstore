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

public class FloorNormalCardItemHolder extends RecyclerView.ViewHolder {

    public View mItemParent;
    public ImageView mBannerImageView;
    public ImageView mAppIconImageView;
    public TextView mAppNameTextView;
    public RatingBar mAppRatingBar;
    public Button mDownloadButton;
    public TextView mAppDetailsTextView;
    public TextView mAppSizeTextView;
    public TextView mCategoryName;
    public ProgressBar pb_current_size;
    public ImageView mImageViewIcon;
    Context mContext;

    private List<DownloadTask>  mDownloadMainViewList;
    private DownloadTask  mDownloadMainViewTask;
    private DownloadTaskManager manager;


    public FloorNormalCardItemHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mAppSizeTextView = (TextView) itemView.findViewById(R.id.app_size_textview);
        mItemParent = itemView.findViewById(R.id.banner_parent);
        mBannerImageView = (ImageView) itemView.findViewById(R.id.banner_imageview);
        mAppIconImageView = (ImageView) itemView.findViewById(R.id.app_icon_imageview);
        mAppNameTextView = (TextView) itemView.findViewById(R.id.app_name_textview);
        mAppRatingBar = (RatingBar) itemView.findViewById(R.id.app_ratingBar);
        mDownloadButton = (Button) itemView.findViewById(R.id.download_button);
        mAppDetailsTextView = (TextView) itemView.findViewById(R.id.tv_description);
        pb_current_size = (ProgressBar) itemView.findViewById(R.id.pb_current_size);
        mImageViewIcon = (ImageView) itemView.findViewById(R.id.app_size_image);
        mCategoryName = (TextView) itemView.findViewById(R.id.category_name_textview);
        manager = DownloadTaskManager.getInstance(BaseApplication.getContext());
    }

    public void onBinderData(final AppMapBean appMapBean, int postion, int isShowTag, final String floorId, final String floorTitle) {
        String sold = appMapBean.getSlotId();
        String appId = appMapBean.getAppId();
        if (!TextUtils.isEmpty(sold) && !mNativeInfoMaps.containsKey(appId)) {
            requestAdNative(appId, sold);
        }
        itemClick( appMapBean, floorId, postion);
        try {
            mAppRatingBar.setRating(Float.parseFloat(appMapBean.getAppGrade()));
        } catch (Exception e) {
            //默认5星
            mAppRatingBar.setRating(5);
        }

        mAppNameTextView.setText(appMapBean.getAppName());
        mAppDetailsTextView.setText(appMapBean.getBriefDesc());
        mCategoryName.setText(appMapBean.getAppCategory());
        final int appType = appMapBean.getAppAttribute();
        if (appType == AppStoreConstant.APP_H5) {
            mImageViewIcon.setVisibility(View.INVISIBLE);
            mAppSizeTextView.setVisibility(View.INVISIBLE);
            mDownloadButton.setText(R.string.appcenter_play);
        } else if (AppStoreConstant.APP_H5_GAME == appType) {
            mDownloadButton.setText(R.string.appcenter_play);
            mAppSizeTextView.setText(ConstantUtils.getAppSize(appMapBean.getAppSize()));
            mImageViewIcon.setVisibility(View.VISIBLE);
            mAppSizeTextView.setVisibility(View.VISIBLE);
        } else if (AppStoreConstant.APP_GOOGLE == appType || AppStoreConstant.APP_APPSFLER == appType) {
            mDownloadButton.setText(R.string.appcenter_download_text);
            mAppSizeTextView.setText(ConstantUtils.getAppSize(appMapBean.getAppSize()));
            mImageViewIcon.setVisibility(View.VISIBLE);
            mAppSizeTextView.setVisibility(View.VISIBLE);
        } else if (appType == AppStoreConstant.APP_SELF) {
            mAppSizeTextView.setText(ConstantUtils.getAppSize(appMapBean.getAppSize()));
            mAppSizeTextView.setVisibility(View.VISIBLE);
            mImageViewIcon.setVisibility(View.VISIBLE);
            mDownloadButton.setText(R.string.appcenter_download_text);
            mDownloadMainViewList = CommonUtils.getDwonloadTask();
            for (DownloadTask downloadTask : mDownloadMainViewList) {
                if (appMapBean.getAppId().equals(downloadTask.getAppId())) {
                    mDownloadMainViewTask = downloadTask;
                    break;
                }
            }
            if (ConstantUtils.isExistsProcess(appMapBean.getPackageName())) {
                mDownloadButton.setText(R.string.appcenter_open_text);
            } else {
                if (mDownloadMainViewTask != null && mDownloadMainViewTask.getAppId().equals(appMapBean.getAppId())) {
                    mDownloadMainViewTask.setInstallState(INSTALLFAILED);
                    CommonUtils.refreshDownloadView(manager, mDownloadMainViewTask, mDownloadButton, pb_current_size);
                }
            }
        }
        Glide.with(mContext)
                .load(appMapBean.getThemeUrl())
                .placeholder(R.drawable.image_default)
                .centerCrop()
                .into(mBannerImageView);

        Glide.with(mContext)
                .load(appMapBean.getIconUrl())
                .placeholder(R.drawable.image_default)
                .centerCrop()
                .transform(new GlideRoundTransform(mContext, 12))
                .into(mAppIconImageView);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appType == AppStoreConstant.APP_GOOGLE) {
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
                } else if (appType == AppStoreConstant.APP_H5 || appType == AppStoreConstant.APP_APPSFLER) {
                    thirdFireBaseSta(appMapBean, floorId);
                    H5Activity.openLink(mContext, appMapBean.getAppUrl());
                } else if (appType == AppStoreConstant.APP_SELF) {
                    if (ConstantUtils.isExistsProcess(appMapBean.getPackageName())) {
                        Intent resolveIntent = BaseApplication.getContext().getPackageManager().getLaunchIntentForPackage(appMapBean.getPackageName());
                        BaseApplication.getContext().startActivity(resolveIntent);
                        return;
                    }
                    for (DownloadTask downloadTask : mDownloadMainViewList) {
                        if (appMapBean.getAppId().equals(downloadTask.getAppId())) {
                            mDownloadMainViewTask = downloadTask;
                            break;
                        }
                    }
                    if (mDownloadMainViewList.size() == 0) {
                        mDownloadMainViewTask = null;
                    }
                    if (mDownloadMainViewTask == null || !mDownloadMainViewTask.getAppId().equals(appMapBean.getAppId())) {
                        mDownloadMainViewTask = new DownloadTask(appMapBean.getAppUrl(), appMapBean.getPackageName(),
                                ConstantUtils.getDownloadPath(), appMapBean.getAppName() + ".apk", appMapBean.getAppName()
                                , null, floorTitle, appMapBean.getAppCategory()
                                , appMapBean.getDeveloper(), appMapBean.getAppAttribute(), floorId
                                , appMapBean.getAppId(), appMapBean.getAppNameEn());
                        if (manager.getListeners(mDownloadMainViewTask).isEmpty()) {
                            manager.registerListener(mDownloadMainViewTask, new DownloadNotificationListener(BaseApplication.getContext(), mDownloadMainViewTask));
                        }
                    }
                    if (manager.getListeners(mDownloadMainViewTask).size() < 2) {
                        manager.registerListener(mDownloadMainViewTask, new DownloadNotificationListener(BaseApplication.getContext(), mDownloadMainViewTask));
                    }
                    if (mDownloadMainViewList.indexOf(mDownloadMainViewTask) != -1) {
                        CommonUtils.downloadBtnClick(manager, mDownloadMainViewTask, FloorNormalCardItemHolder.this);
                    } else {
                        CommonUtils.downloadBtnClick(manager, mDownloadMainViewTask, FloorNormalCardItemHolder.this);
                        UploadLogManager.getInstance().generateResourceLog(mContext, 2, appMapBean.getAppNameEn(), appMapBean.getAppAttribute(), 2, "", "");
                    }
                    Bundle param3 = new Bundle();
                    param3.putString("NormalAppname", appMapBean.getAppNameEn());
                    param3.putString("NormalAppStyle", appMapBean.getAppCategory());
                    FirebaseStat.logEvent("NormalDownloadClick", param3);
                } else if (AppStoreConstant.APP_H5_GAME == appType) {
                    Toast.makeText(mContext, R.string.not_supprt_game, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
}