package net.bat.store.ux.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transsion.iad.core.TAdError;
import com.transsion.iad.core.TAdListener;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.bean.Ad;
import com.transsion.iad.core.bean.TAdNativeInfo;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.GlideRoundTransform;
import net.bat.store.ux.adapter.viewholder.FloorListCardHolder;
import net.bat.store.ux.adapter.viewholder.FloorNormalCardHolder;
import net.bat.store.ux.adapter.viewholder.FloorGridCardHolder;
import net.bat.store.ux.adapter.viewholder.FloorHorizonGridViewHolder;
import net.bat.store.ux.adapter.viewholder.MainListAdHolder;
import net.bat.store.ux.adapter.viewholder.FloorBannerCardHolder;
import net.bat.store.ux.adapter.viewholder.MainViewAdHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * usage
 *
 * @author cheng.qian.
 * @date 2016/9/22.
 * ============================================
 * Copyright (c) 2016 TRANSSION.Co.Ltd.
 * All right reserved.
 **/


public class MainRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MainRecyclerViewAdapter";

    private Context context;
    private LayoutInflater layoutInflater;
    private List<FloorMapBean> webstoreFloorMapList;
    private DownloadTaskManager manager;

    public final static int TYPE_BANNER_URL = 5;
    public final static int TYPE_BANNER_APP = 6;
    public final static int TYPE_NORMAL = 3;
    public final static int TYPE_NORMAL_AD = 8;
    public final static int TYPE_GRID = 4;
    public final static int TYPE_HORIZON_GRID = 1;
    public final static int TYPE_LIST = 2;
    public final static int TYPE_LIST_AD = 7;
    public final static int TYPE_BANNER_COMBINER = 9;
    public final static int TYPE_FLOAT_WINDOW = 10;
    private String mSlodId;
    private String mAppId;
    private Map<String, TAdNativeInfo> mNativeInfoMaps;
    private Map<TAdNativeInfo, TAdNative> mAdNativeMaps;
    private List<String> mTempRequestSolds;
    //    private GameManager mGameManager;
    private int lastPosition = -1;
    private List<DownloadTask> mDownloadTaskList, mDownloadMainViewList;
    private DownloadTask mDownloadTask, mDownloadMainViewTask;
    private List<DownloadTask> mListTempTask, mListMainViewTempTask;

    public MainRecyclerViewAdapter(Context context, List<FloorMapBean> webstoreFloorMapList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.webstoreFloorMapList = getFilterWebStoreFloorMapBeans(webstoreFloorMapList);
        manager = DownloadTaskManager.getInstance(BaseApplication.getContext());
        mNativeInfoMaps = null;
        mAdNativeMaps = null;
        mTempRequestSolds = null;
        mNativeInfoMaps = new HashMap<>();
        mAdNativeMaps = new HashMap<>();
        mTempRequestSolds = new ArrayList<>();
        mListTempTask = new ArrayList<>();
        mListMainViewTempTask = new ArrayList<>();
    }

    public MainRecyclerViewAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        manager = DownloadTaskManager.getInstance(BaseApplication.getContext());
        mNativeInfoMaps = null;
        mAdNativeMaps = null;
        mTempRequestSolds = null;
        mNativeInfoMaps = new HashMap<>();
        mAdNativeMaps = new HashMap<>();
        mTempRequestSolds = new ArrayList<>();
        mListTempTask = new ArrayList<>();
        mListMainViewTempTask = new ArrayList<>();
    }

    private List<FloorMapBean> getFilterWebStoreFloorMapBeans(List<FloorMapBean> webstoreFloorMapList) {
        List<FloorMapBean> tempWebs = new ArrayList<>();
        for (FloorMapBean floorMapBean : webstoreFloorMapList) {
            int floorType = floorMapBean.getFloorType();
            if (!(floorType == TYPE_BANNER_APP || floorType == TYPE_BANNER_URL
                    || floorType == TYPE_LIST_AD || floorType == TYPE_NORMAL_AD
                    || floorType == TYPE_FLOAT_WINDOW)) {
                tempWebs.add(floorMapBean);
            }
        }
        return tempWebs;
    }

    public void setData(List<FloorMapBean> webstoreFloorMapList) {
        this.webstoreFloorMapList = getFilterWebStoreFloorMapBeans(webstoreFloorMapList);
        notifyDataSetChanged();
    }

    public void requestAdNative(final String appId, final String slodId) {
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
                    Log.i(TAG, "title:" + tAdNativeInfo.getTitle());
                    mNativeInfoMaps.put(appId, tAdNativeInfo);
                    mAdNativeMaps.put(tAdNativeInfo, tAdNative);
                } else {
                    mTempRequestSolds.remove(appId);
                }
            }

            @Override
            public void onError(TAdError adError) {
                Log.i(TAG, "adError:" + adError.getErrorMessage() + adError.getErrorCode());
                mTempRequestSolds.remove(appId);
            }
        });
        tAdNative.loadAd();
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                    .anim.item_bottom_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.i(TAG, "onViewDetachedFromWindow: ");
        try {
            holder.itemView.clearAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder mViewHolder = null;
        switch (viewType) {
            case TYPE_BANNER_COMBINER:
                mViewHolder = new FloorBannerCardHolder(context, layoutInflater.inflate(R.layout.floor_banner_card, parent, false));
                break;
            case TYPE_NORMAL:
                mViewHolder = new FloorNormalCardHolder(context, layoutInflater.inflate(R.layout.floor_normal_card, parent, false));
                break;
            case TYPE_NORMAL_AD:
                mViewHolder = new MainViewAdHolder(new RelativeLayout(parent.getContext()), mAppId, mNativeInfoMaps, mAdNativeMaps);
                break;
            case TYPE_GRID:
                mViewHolder = new FloorGridCardHolder(context, layoutInflater.inflate(R.layout.floor_grid_card, parent, false));
                break;
            case TYPE_HORIZON_GRID:
                mViewHolder = new FloorHorizonGridViewHolder(context, layoutInflater.inflate(R.layout.floor_horizontal_grid_card, parent, false));
                break;
            case TYPE_LIST:
                mViewHolder = new FloorListCardHolder(context, layoutInflater.inflate(R.layout.floor_list_card, parent, false));
                break;
            case TYPE_LIST_AD:
                mViewHolder = new MainListAdHolder(new RelativeLayout(parent.getContext()), mAppId, mNativeInfoMaps, mAdNativeMaps);
                break;
            default:
                break;
        }
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int card = getCardByPosition(position);
        final int index = getIndexInCardByPosition(position);
        if (holder instanceof FloorNormalCardHolder) {
            final FloorNormalCardHolder viewHolder = (FloorNormalCardHolder) holder;
            setAnimation(holder.itemView, position);
            final FloorMapBean floorMapBean = webstoreFloorMapList.get(card);
            viewHolder.onBindView(floorMapBean);
        } else if (holder instanceof MainViewAdHolder) {
            MainViewAdHolder viewHolder = (MainViewAdHolder) holder;
            setAnimation(holder.itemView, position);
            viewHolder.adView.setCallToActionView(viewHolder.mDownloadButton);
        } else if (holder instanceof FloorGridCardHolder) {
            final FloorGridCardHolder viewHolder = (FloorGridCardHolder) holder;
            setAnimation(holder.itemView, position);
            viewHolder.onBindView(webstoreFloorMapList.get(card));
        } else if (holder instanceof FloorHorizonGridViewHolder) {
            final FloorHorizonGridViewHolder viewHolder = (FloorHorizonGridViewHolder) holder;
            setAnimation(holder.itemView, position);
            viewHolder.onBindView(webstoreFloorMapList.get(card));
        } else if (holder instanceof FloorListCardHolder) {
            FloorMapBean floorMapBean = webstoreFloorMapList.get(card);
            FloorListCardHolder viewHolder = (FloorListCardHolder) holder;
            setAnimation(holder.itemView, position);
            viewHolder.onBindView(floorMapBean);
        } else if (holder instanceof MainListAdHolder) {
            MainListAdHolder viewHolder = (MainListAdHolder) holder;
            setAnimation(holder.itemView, position);
            FloorMapBean floorMapBean = webstoreFloorMapList.get(card);
            TAdNativeInfo tAdNativeInfo = null;
            if (!TextUtils.isEmpty(mSlodId) && mNativeInfoMaps.containsKey(mAppId)) {
                tAdNativeInfo = mNativeInfoMaps.get(mAppId);
            }
            if (tAdNativeInfo != null) {
                if (ConstantUtils.iSAdmobNativeAd(tAdNativeInfo.getFlag())) {
                    viewHolder.mAdType.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mAdType.setVisibility(View.GONE);
                }
                viewHolder.adView.setCallToActionView(viewHolder.btn_download);
                viewHolder.tv_app_label.setText(tAdNativeInfo.getTitle());
                viewHolder.tv_desc.setText(tAdNativeInfo.getDescription());
                viewHolder.btn_download.setText(R.string.appcenter_open_text);
                if (floorMapBean.getIsShowTag() == 1) {
                    if (ConstantUtils.LAN_AR.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_FA.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_UR_RPK.equals(ConstantUtils.getLanguage()) || ConstantUtils.LAN_IW.equals(ConstantUtils.getLanguage())) {
                        setRight(index, viewHolder.tv_app_rank);
                    } else {
                        setRank(index, viewHolder.tv_app_rank);
                    }
                } else {
                    viewHolder.tv_app_rank.setVisibility(View.GONE);
                }
                Glide.with(context).load(tAdNativeInfo.getImage().getIcon().getIconUrl())
                        .placeholder(R.drawable.image_default).centerCrop().transform(new GlideRoundTransform(context, 12)).into(viewHolder.iv_app_icon);
            }
        } else if (holder instanceof FloorBannerCardHolder) {
            final FloorBannerCardHolder viewHolder = (FloorBannerCardHolder) holder;
            setAnimation(holder.itemView, position);
            FloorMapBean floorMapBean = webstoreFloorMapList.get(card);
            viewHolder.onBindView(floorMapBean);

        }

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





    @Override
    public int getItemViewType(int position) {
        int card = getCardByPosition(position);
        int type = webstoreFloorMapList.get(card).getFloorType();
        return type;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int n = 0; n < getViewCardCount(); n++) {
            count += getCountByCard(n);
        }
        //return count;
        return 4;
    }


    public int getViewCardCount() {
        if (webstoreFloorMapList != null) {
            return webstoreFloorMapList.size();
        }
        return 0;
    }

    public final int getCountByCard(int n) {
        try {
            switch (webstoreFloorMapList.get(n).getFloorType()) {
//                case TYPE_NORMAL:
//                    return webstoreFloorMapList.get(n).getAppMapList().size();
//                case TYPE_LIST:
//                    return webstoreFloorMapList.get(n).getAppMapList().size();
                default:
                    return 1;
            }
        } catch (Exception e) {
            return 0;
        }

    }

    public final int getCardByPosition(int position) {
        if (getViewCardCount() == 0) {
            return 0;
        }

        int count = getCountByCard(0);
        for (int n = 0; n < getViewCardCount(); n++) {
            if (position >= 0 && position < count) {
                return n;
            } else {
                position -= count;
                if (n + 1 < getViewCardCount()) {
                    count = getCountByCard(n + 1);
                }
            }
        }

        return -1;
    }

    public final int getIndexInCardByPosition(int position) {
        if (getViewCardCount() > 0) {
            int count = getCountByCard(0);
            for (int n = 0; n < getViewCardCount(); n++) {
                if (position >= 0 && position < count) {
                    return position;
                } else {
                    position -= count;
                    if (n + 1 < getViewCardCount()) {
                        count = getCountByCard(n + 1);
                    }
                }
            }
        }

        return 0;
    }

}
