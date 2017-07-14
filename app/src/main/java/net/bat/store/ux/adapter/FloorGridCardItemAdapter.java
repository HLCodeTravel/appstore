package net.bat.store.ux.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.TNativeAdView;
import com.transsion.iad.core.bean.TAdNativeInfo;

import net.bat.store.R;
import net.bat.store.BaseApplication;
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
 * Created by bingbing.li on 2017/1/8.
 */

public class FloorGridCardItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static enum ITEM_TYPE {
        ITEM_TYPE_APP,
        ITEM_TYPE_AD
    }

    private static final String TAG = "FloorGridCardItemAdapter";
    private List<AppMapBean> appMapBeanList;
    private Context mContext;
    private int isShowTag;
    private DownloadTaskManager manager;
    private String mSlodId;
    private String mAppId;
    //    private GameManager mGameManager;
    private Map<String, TAdNativeInfo> mNativeInfoMaps;
    private Map<TAdNativeInfo, TAdNative> mAdNativeMaps;
    private String floorId;
    private LayoutInflater mLayoutInflater;
    private List<DownloadTask> mDownloadTaskList;
    private DownloadTask mDownloadTask;

    public FloorGridCardItemAdapter(Context context) {
        mContext = context;
        manager = DownloadTaskManager.getInstance(BaseApplication.getContext());
//        mGameManager=GameManager.getInstance(BaseApplication.getContext());
        mNativeInfoMaps = new HashMap<>();
        mAdNativeMaps = new HashMap<>();
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setData(List<AppMapBean> appMapBean, String floorId,
                        int isShowTag, Map<String, TAdNativeInfo> nativeInfoMaps, Map<TAdNativeInfo, TAdNative> adNativeMaps) {
        this.isShowTag = isShowTag;
        this.floorId = floorId;
        this.appMapBeanList = appMapBean;
        this.mNativeInfoMaps = nativeInfoMaps;
        this.mAdNativeMaps = adNativeMaps;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (appMapBeanList != null) {
            return appMapBeanList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        mSlodId = appMapBeanList.get(position).getSlotId();
        mAppId = appMapBeanList.get(position).getAppId();
        if (!TextUtils.isEmpty(mSlodId)) {
            return mNativeInfoMaps.get(mAppId) != null ? ITEM_TYPE.ITEM_TYPE_AD.ordinal() : ITEM_TYPE.ITEM_TYPE_APP.ordinal();
        }
        return ITEM_TYPE.ITEM_TYPE_APP.ordinal();
    }

    private void thirdFireBaseSta(AppMapBean appMapBean, String foorId) {
        Bundle param1 = new Bundle();
        param1.putString("AppId", appMapBean.getAppNameEn());
        param1.putString("Appfloortitle", foorId);
        param1.putString("AppappCategory", appMapBean.getAppCategory());
        param1.putString("AppDeveloper", appMapBean.getDeveloper());
        param1.putInt("AppAttribute", appMapBean.getAppAttribute());
        FirebaseStat.logEvent("DLS_Id_" + appMapBean.getAppNameEn(), param1);
        FirebaseStat.logEvent("DLS_FloorId_" + floorId, param1);
        if (!TextUtils.isEmpty(appMapBean.getAppCategory())) {
            FirebaseStat.logEvent("DLS_Category_" + appMapBean.getAppCategory().replace(" ", "_").replace("&", "_"), param1);
        }
        if (!TextUtils.isEmpty(appMapBean.getDeveloper())) {
            FirebaseStat.logEvent("DLS_Developer_" + appMapBean.getDeveloper().replace(" ", "_").replace("&", "_"), param1);
        }
        FirebaseStat.logEvent("DLS_Attribute_" + appMapBean.getAppAttribute(), param1);
        UploadLogManager.getInstance().generateResourceLog(mContext, 1, appMapBean.getAppNameEn(), appMapBean.getAppAttribute(), 2, "", "");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        Log.i(TAG, "onBindViewHolder: ");
        SinglerGirdViewHolder singlerGirdViewHolder = null;
        AdSinglerGirdViewHolder adSinglerGirdViewHolder = null;
        if (viewHolder instanceof SinglerGirdViewHolder) {
            singlerGirdViewHolder = (SinglerGirdViewHolder) viewHolder;
            final AppMapBean appMapBean = appMapBeanList.get(position);
            singlerGirdViewHolder.tv_app_name.setText(appMapBean.getAppName());
            singlerGirdViewHolder.tv_download_time.setText(ConstantUtils.getAppSize(appMapBean.getAppSize()));
            singlerGirdViewHolder.bn_free.setBackgroundResource(R.drawable.app_detail_btn);
            int imgSize = (int) mContext.getResources().getDimension(R.dimen.appcenter_card_body_appicon_width);
            Glide.with(mContext).load(appMapBean.getIconUrl())
                    .placeholder(R.drawable.image_default)
                    .centerCrop().override(imgSize, imgSize)
                    .into(singlerGirdViewHolder.iv_icon);

            final int appType = appMapBean.getAppAttribute();
            if (appType == AppStoreConstant.APP_H5) {
                singlerGirdViewHolder.bn_free.setText(R.string.appcenter_play);
                singlerGirdViewHolder.tv_download_time.setVisibility(View.INVISIBLE);
            } else if (AppStoreConstant.APP_H5_GAME == appType) {
                singlerGirdViewHolder.tv_download_time.setVisibility(View.VISIBLE);
                singlerGirdViewHolder.bn_free.setText(R.string.appcenter_play);
            } else if (AppStoreConstant.APP_GOOGLE == appType || AppStoreConstant.APP_APPSFLER == appType) {
                singlerGirdViewHolder.tv_download_time.setVisibility(View.VISIBLE);
                singlerGirdViewHolder.bn_free.setText(R.string.appcenter_download_text);
            } else if (appType == AppStoreConstant.APP_SELF) {
                singlerGirdViewHolder.tv_download_time.setVisibility(View.VISIBLE);
                singlerGirdViewHolder.bn_free.setText(R.string.appcenter_download_text);
                mDownloadTaskList = CommonUtils.getDwonloadTask();
                for (DownloadTask downloadTask : mDownloadTaskList) {
                    if (appMapBean.getAppId().equals(downloadTask.getAppId())) {
                        mDownloadTask = downloadTask;
                        break;
                    }
                }
                if (ConstantUtils.isExistsProcess(appMapBean.getPackageName())) {
                    singlerGirdViewHolder.bn_free.setText(R.string.appcenter_open_text);
                } else {
                    if (mDownloadTask != null && mDownloadTask.getAppId().equals(appMapBean.getAppId())) {
                        mDownloadTask.setInstallState(INSTALLFAILED);
                        CommonUtils.refreshDownloadView(manager, mDownloadTask, singlerGirdViewHolder.bn_free, null);
                    }
                }
            }
            if (ConstantUtils.LAN_AR.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_FA.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_UR_RPK.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_IW.equals(ConstantUtils.getLanguage())) {
                setRight(position, singlerGirdViewHolder.iv_top, singlerGirdViewHolder.iv_top_bg);
            } else {
                setRank(position, singlerGirdViewHolder.iv_top, singlerGirdViewHolder.iv_top_bg);
            }
            singlerGirdViewHolder.bn_free.setOnClickListener(new View.OnClickListener() {
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
                                    , null, floorId, appMapBean.getAppCategory()
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
                            CommonUtils.downloadBtnClick(manager, mDownloadTask, viewHolder);
                        } else {
                            CommonUtils.downloadBtnClick(manager, mDownloadTask, viewHolder);
                            UploadLogManager.getInstance().generateResourceLog(mContext, 2, appMapBean.getAppNameEn(), appMapBean.getAppAttribute(), 2, "", "");
                        }
                        Bundle param = new Bundle();
                        param.putString("GridViewNormalName", appMapBean.getAppNameEn());
                        param.putString("GridViewNormalStyle", appMapBean.getAppCategory());
                        FirebaseStat.logEvent("GridviewBtnNormalClick", param);
                    } else if (appType == AppStoreConstant.APP_H5_GAME) {
                        Toast.makeText(mContext, R.string.not_supprt_game, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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
                    } else if (appType == AppStoreConstant.APP_SELF
                            || appType == AppStoreConstant.APP_H5_GAME) {
                        Bundle data = new Bundle();
                        data.putString(FirebaseAnalytics.Param.ITEM_ID, appMapBeanList.get(position).getAppNameEn());
                        data.putString(FirebaseAnalytics.Param.ITEM_NAME, position + "");
                        data.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "GridView_" + floorId);
                        FirebaseStat.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, data);
                        Intent intent = new Intent(mContext, AppDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("appBean", appMapBean);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                }
            });
        } else if (viewHolder instanceof AdSinglerGirdViewHolder) {
            adSinglerGirdViewHolder = (AdSinglerGirdViewHolder) viewHolder;
            TAdNativeInfo tAdNativeInfo = null;
            if (!TextUtils.isEmpty(mSlodId) && mNativeInfoMaps.containsKey(mAppId)) {
                tAdNativeInfo = mNativeInfoMaps.get(mAppId);
            }

            if (tAdNativeInfo != null) {
                if (ConstantUtils.iSAdmobNativeAd(tAdNativeInfo.getFlag())) {
                    adSinglerGirdViewHolder.mAdType.setVisibility(View.VISIBLE);
                } else {
                    adSinglerGirdViewHolder.mAdType.setVisibility(View.INVISIBLE);
                }
                adSinglerGirdViewHolder.adView.setCallToActionView(adSinglerGirdViewHolder.bn_free);
                adSinglerGirdViewHolder.tv_app_name.setText(tAdNativeInfo.getTitle());
                adSinglerGirdViewHolder.bn_free.setBackgroundResource(R.drawable.app_detail_btn);
                adSinglerGirdViewHolder.bn_free.setText(R.string.appcenter_open_text);
                int imgSize = (int) mContext.getResources().getDimension(R.dimen.appcenter_card_body_appicon_width);
                if (ConstantUtils.LAN_AR.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_FA.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_UR_RPK.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_IW.equals(ConstantUtils.getLanguage())) {
                    setRight(position, adSinglerGirdViewHolder.iv_top, adSinglerGirdViewHolder.iv_top_bg);
                } else {
                    setRank(position, adSinglerGirdViewHolder.iv_top, adSinglerGirdViewHolder.iv_top_bg);
                }
                Glide.with(mContext).load(tAdNativeInfo.getImage().getIcon().getIconUrl())
                        .placeholder(R.drawable.image_default)
                        .centerCrop().override(imgSize, imgSize)
                        .transform(new GlideRoundTransform(mContext, 12))
                        .into(adSinglerGirdViewHolder.iv_icon);
            }
        }

    }

    private void setRight(int position, TextView iv_top, ImageView iv_top_bg) {
        if (isShowTag == 1) {
            switch (position) {
                case 0:
                    iv_top.setBackgroundResource(R.mipmap.appcenter_app_top_one);
                    iv_top.setText("");
                    break;
                case 1:
                    iv_top.setBackgroundResource(R.mipmap.appcenter_app_top_two);
                    iv_top.setText("");
                    break;
                case 2:
                    iv_top.setBackgroundResource(R.mipmap.appcenter_app_top_three);
                    iv_top.setText("");
                    break;
                default:
                    iv_top_bg.setVisibility(View.VISIBLE);
                    iv_top_bg.setImageResource(R.mipmap.appcenter_app_top_normal_right);
                    iv_top.setBackgroundDrawable(null);
                    int rank = position + 1;
                    if (position < 10) {
                        iv_top.setText(rank + "");
                    } else if (position < 100) {
                        iv_top.setText(rank + "");
                    } else {
                        iv_top.setText("");
                        //iv_top.setBackgroundDrawable(null);
                        iv_top_bg.setVisibility(View.GONE);
                    }
                    break;
            }
        } else {
            iv_top.setVisibility(View.GONE);
        }

    }

    private void setRank(int position, TextView iv_top, ImageView iv_top_bg) {
        if (isShowTag == 1) {
            switch (position) {
                case 0:
                    iv_top.setBackgroundResource(R.mipmap.appcenter_app_top_one);
                    iv_top.setText("");
                    break;
                case 1:
                    iv_top.setBackgroundResource(R.mipmap.appcenter_app_top_two);
                    iv_top.setText("");
                    break;
                case 2:
                    iv_top.setBackgroundResource(R.mipmap.appcenter_app_top_three);
                    iv_top.setText("");
                    break;
                default:
                    iv_top_bg.setVisibility(View.VISIBLE);
                    iv_top_bg.setImageResource(R.mipmap.appcenter_app_top_normal);
                    iv_top.setBackgroundDrawable(null);
                    int rank = position + 1;
                    if (position < 10) {
                        iv_top.setText("" + rank);
                    } else if (position < 100) {
                        iv_top.setText("" + rank);
                    } else {
                        iv_top.setText("");
                        iv_top.setBackgroundDrawable(null);
                    }
                    break;
            }
        } else {
            iv_top.setVisibility(View.GONE);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_AD.ordinal()) {
            return new AdSinglerGirdViewHolder(new RelativeLayout(parent.getContext()), mAppId, mNativeInfoMaps, mAdNativeMaps);
        } else {
            return new SinglerGirdViewHolder(mLayoutInflater.inflate(R.layout.appcenter_gridcard_tworow_item, parent, false));
        }
    }

    private void reSetBtnState(Button button, ProgressBar progressBar) {
        button.setBackgroundResource(R.drawable.app_detail_btn);
        button.setTextColor(mContext.getResources().getColor(R.color.appcenter_free_normal_color));
        button.setText(R.string.appcenter_download_text);
        progressBar.setProgress(0);
    }

    public static class SinglerGirdViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_icon;
        public TextView iv_top;
        public TextView tv_app_name;
        public TextView tv_download_time;
        public Button bn_free;
        public ProgressBar progressBar;
        public ImageView iv_top_bg;

        public SinglerGirdViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_app_icon);
            iv_top = (TextView) itemView.findViewById(R.id.app_top);
            tv_app_name = (TextView) itemView.findViewById(R.id.app_name);
            tv_download_time = (TextView) itemView.findViewById(R.id.app_filesize);
            bn_free = (Button) itemView.findViewById(R.id.app_install);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pb_current_size);
            iv_top_bg = (ImageView) itemView.findViewById(R.id.iv_app_top_bg);
        }
    }

    public static class AdSinglerGirdViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_icon;
        public TextView iv_top;
        public TextView tv_app_name;
        public Button bn_free;
        public TextView mAdType;
        public TNativeAdView adView;
        public ImageView iv_top_bg;

        public AdSinglerGirdViewHolder(View itemView, String appId, Map<String, TAdNativeInfo> nativeInfoMap, Map<TAdNativeInfo, TAdNative> adNativeMaps) {
            super(itemView);
            TAdNativeInfo tAdNativeInfo = null;
            TAdNative tAdNative = null;
            tAdNativeInfo = nativeInfoMap.get(appId);
            tAdNative = adNativeMaps.get(tAdNativeInfo);
            if (tAdNativeInfo != null && ((ViewGroup) itemView).getChildCount() == 0) {
                adView = (TNativeAdView) View.inflate(itemView.getContext(), R.layout.appcenter_gridcard_tworow_item_ad, null);
                adView.addNativeAdView((ViewGroup) itemView, tAdNativeInfo);
                iv_icon = (ImageView) adView.findViewById(R.id.iv_app_icon);
                iv_top = (TextView) adView.findViewById(R.id.app_top);
                tv_app_name = (TextView) adView.findViewById(R.id.app_name);
                bn_free = (Button) adView.findViewById(R.id.app_install);
                mAdType = (TextView) adView.findViewById(R.id.ad_type);
                iv_top_bg = (ImageView) adView.findViewById(R.id.iv_app_top_bg);
                adView.setCallToActionView(bn_free);
                adView.setHeadlineView(tv_app_name);
                adView.setIconView(iv_icon);
                List<View> viewList = new ArrayList<>();
                viewList.add(bn_free);
                viewList.add(tv_app_name);
                viewList.add(tv_app_name);
                tAdNative.registerViews(adView, viewList);
            }
        }
    }

}
