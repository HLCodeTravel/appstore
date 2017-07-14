//package net.bat.store.ux.home.ad;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.transsion.iad.core.TAdNative;
//import com.transsion.iad.core.TNativeAdView;
//import com.transsion.iad.core.bean.TAdNativeInfo;
//
//import net.bat.store.R;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//
///* Top Secret */
//
///**
// * 填充广告时适配器的viewholder
// *
// * @author 李诗蒙
// * @date 2017/2/20
// * ==================================
// * Copyright (c) 2017 TRANSSION.Co.Ltd.
// * All rights reserved
// */
//
//public class AdSinglerGirdViewHolder extends RecyclerView.ViewHolder {
//    public ImageView iv_icon;
//    public TextView iv_top;
//    public TextView tv_app_name;
//    public Button bn_free;
//    public TextView mAdType;
//    public TNativeAdView adView;
//    public ImageView iv_top_bg;
//
//    public AdSinglerGirdViewHolder(View itemView, String appId, Map<String, TAdNativeInfo> nativeInfoMap, Map<TAdNativeInfo, TAdNative> adNativeMaps) {
//        super(itemView);
//        TAdNativeInfo tAdNativeInfo = null;
//        TAdNative tAdNative = null;
//        tAdNativeInfo = nativeInfoMap.get(appId);
//        tAdNative = adNativeMaps.get(tAdNativeInfo);
//        if (tAdNativeInfo != null && ((ViewGroup) itemView).getChildCount() == 0) {
//            adView = (TNativeAdView) View.inflate(itemView.getContext(), R.layout.appcenter_gridcard_tworow_item_ad, null);
//            adView.addNativeAdView((ViewGroup) itemView, tAdNativeInfo);
//            iv_icon = (ImageView) adView.findViewById(R.id.iv_app_icon);
//            iv_top = (TextView) adView.findViewById(R.id.app_top);
//            tv_app_name = (TextView) adView.findViewById(R.id.app_name);
//            bn_free = (Button) adView.findViewById(R.id.app_install);
//            mAdType = (TextView) adView.findViewById(R.id.ad_type);
//            iv_top_bg = (ImageView)adView.findViewById(R.id.iv_app_top_bg);
//            adView.setCallToActionView(bn_free);
//            adView.setHeadlineView(tv_app_name);
//            adView.setIconView(iv_icon);
//            List<View> viewList=new ArrayList<>();
//            viewList.add(bn_free);
//            viewList.add(tv_app_name);
//            viewList.add(tv_app_name);
//            tAdNative.registerViews(adView,viewList);
//        }
//    }
//}
