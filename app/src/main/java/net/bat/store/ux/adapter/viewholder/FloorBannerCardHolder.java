package net.bat.store.ux.adapter.viewholder;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.transsion.iad.core.TAdError;
import com.transsion.iad.core.TAdListener;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.bean.Ad;
import com.transsion.iad.core.bean.TAdNativeInfo;

import net.bat.store.BaseApplication;
import net.bat.store.R;
import net.bat.store.bean.ad.AdBean;
import net.bat.store.bean.floor.BannerMapBean;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.widget.banner.ConvenientBanner;
import net.bat.store.widget.banner.holder.CBViewHolderCreator;
import net.bat.store.widget.banner.listener.OnItemClickListener;
import net.bat.store.widget.banner.view.NetworkImageHolderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liang.he on 2017/7/7.
 */

public class FloorBannerCardHolder extends BaseFloorViewHolder {
    public ConvenientBanner vp_selection;

    public FloorBannerCardHolder(Context context, View itemView) {
        super(context, itemView);
        vp_selection = (ConvenientBanner) itemView.findViewById(R.id.vp_selection);
    }

    @Override
    public void onBindView(FloorMapBean floorMapBean) {

        vp_selection.setManualPageable(true);
        vp_selection.startTurning(4000);
        vp_selection.setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused});
        List<BannerMapBean> bannerMapBeanList = floorMapBean.getBannerLinkImage();
        String name = floorMapBean.getFloorTitle();
        if (TextUtils.isEmpty(name)) {
            name = floorMapBean.getFloorName();
        }
        initNetWorkBannerUrl(vp_selection, bannerMapBeanList, name, floorMapBean.getFloorId());
    }

    private void initNetWorkBannerUrl(ConvenientBanner flyBanner,
                                      final List<BannerMapBean> bannerMapBeanList, final String name, final String id) {
        if (bannerMapBeanList != null && bannerMapBeanList.size() > 0) {
            Map<String, AdBean> map = new HashMap<>();
            for (BannerMapBean bannerMapBean : bannerMapBeanList) {
                String sold = bannerMapBean.getSlotId();
                String appId = bannerMapBean.getAppId();
                if (!TextUtils.isEmpty(sold)) {
                    if (mNativeInfoMaps.containsKey(appId)) {
                        AdBean adBean = new AdBean();
                        TAdNativeInfo adInfo = mNativeInfoMaps.get(appId);
                        adBean.settAdNativeInfo(adInfo);
                        adBean.settAdNative(mAdNativeMaps.get(adInfo));
                        map.put(appId, adBean);
                        adBean = null;
                    } else {
                        requestAdNative(appId, sold);
                    }
                }
            }

            flyBanner.setPages(new CBViewHolderCreator() {
                @Override
                public Object createHolder() {
                    return new NetworkImageHolderView();
                }
            }, bannerMapBeanList, map);

            flyBanner.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    BannerMapBean bannerMapBean = bannerMapBeanList.get(position);
                    ConstantUtils.onItemClickBannerCombiner(bannerMapBean, appContext);

                    Bundle param1 = new Bundle();
                    param1.putString("bannerRank", bannerMapBean.getRank() + "");
                    param1.putString("bannerLinkType", bannerMapBean.getLinkType() + "");
                    param1.putString("bannerAttribute", bannerMapBean.getAppAttribute() + "");
                    FirebaseStat.logEvent("Banner_Rank_" + bannerMapBean.getRank() + "", param1);
                    FirebaseStat.logEvent("Banner_LinkType_" + bannerMapBean.getLinkType() + "", param1);
                    FirebaseStat.logEvent("Banner_Attribute_" + bannerMapBean.getAppAttribute() + "", param1);

                    UploadLogManager.getInstance().generateUseroperationlog(appContext, 5, name, id, "", bannerMapBean.getRank() + "", bannerMapBean.getLinkType() + "");
                    //UploadLogManager.getInstance().generateResourceLog(context,1,"",bannerMapBean.getAppAttribute(),4,bannerMapBean.getRank()+"",bannerMapBean.getLinkType()+"");
                }
            });
        }
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
}
