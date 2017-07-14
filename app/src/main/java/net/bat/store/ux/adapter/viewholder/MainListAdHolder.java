package net.bat.store.ux.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.TNativeAdView;
import com.transsion.iad.core.bean.TAdNativeInfo;

import net.bat.store.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MainListAd广告适配器
 *
 * @author 李诗蒙
 * @data 2017/2/21
 * ========================================
 * CopyRight (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */

public class MainListAdHolder extends RecyclerView.ViewHolder {

    public ImageView iv_app_icon;
    public TextView tv_app_label;
    public RatingBar rb_score;
    public Button btn_download;
    public TextView tv_app_rank;
    public TextView tv_desc;
    public ProgressBar progressBar;
    public TextView mAdType;
    public TNativeAdView adView;

    public MainListAdHolder(View itemView, String mAppId, Map<String, TAdNativeInfo> nativeInfoMap, Map<TAdNativeInfo, TAdNative> adNativeMaps) {
        super(itemView);

        TAdNativeInfo tAdNativeInfo = nativeInfoMap.get(mAppId);
        TAdNative tAdNative = adNativeMaps.get(tAdNativeInfo);

        if (tAdNativeInfo != null && ((ViewGroup) itemView).getChildCount() == 0) {
            adView = (TNativeAdView) View.inflate(itemView.getContext(), R.layout.item_more_detail_ad, null);
            adView.addNativeAdView((ViewGroup) itemView, tAdNativeInfo);

            iv_app_icon = (ImageView) adView.findViewById(R.id.iv_app_icon);
            tv_app_label = (TextView) adView.findViewById(R.id.tv_app_label);
            tv_desc = (TextView) adView.findViewById(R.id.tv_description);
            rb_score = (RatingBar) adView.findViewById(R.id.rb_score);
            btn_download = (Button) adView.findViewById(R.id.btn_download);
            tv_app_rank = (TextView) adView.findViewById(R.id.tv_app_rank);
            progressBar = (ProgressBar) adView.findViewById(R.id.pb_current_size);
            mAdType = (TextView) adView.findViewById(R.id.ad_type);

            adView.setCallToActionView(btn_download);
            adView.setHeadlineView(tv_app_label);
            adView.setBodyView(tv_desc);
            adView.setIconView(iv_app_icon);
            List<View> viewList=new ArrayList<>();
            viewList.add(btn_download);
            viewList.add(tv_desc);
            viewList.add(iv_app_icon);
            viewList.add(tv_app_label);
            tAdNative.registerViews(adView,viewList);
        }
    }
}
