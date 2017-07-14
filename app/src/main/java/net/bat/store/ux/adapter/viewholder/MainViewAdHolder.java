package net.bat.store.ux.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.TNativeAdView;
import com.transsion.iad.core.bean.TAdNativeInfo;

import net.bat.store.R;
import net.bat.store.utils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * usage
 *
 * @author 李诗蒙
 * @data 2017/2/21
 * ========================================
 * CopyRight (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */

public class MainViewAdHolder extends RecyclerView.ViewHolder {
    public View mItemParent;
    public ImageView mBannerImageView;
    public ImageView mAppIconImageView;
    public TextView mAppNameTextView;
    public RatingBar mAppRatingBar;
    public Button mDownloadButton;
    public TextView mAppDetailsTextView;
    public TextView mCategoryName;
    public ProgressBar pb_current_size;
    public ImageView mImageViewIcon;
    public TextView mAdType;
    public TNativeAdView adView;

    public MainViewAdHolder(View itemView, String mAppId, Map<String, TAdNativeInfo> nativeInfoMap, Map<TAdNativeInfo, TAdNative> adNativeMaps) {
        super(itemView);

        TAdNativeInfo tAdNativeInfo = nativeInfoMap.get(mAppId);
        TAdNative tAdNative =adNativeMaps.get(tAdNativeInfo);
        if (tAdNativeInfo != null && ((ViewGroup)itemView).getChildCount() == 0) {
            adView = (TNativeAdView) View.inflate(itemView.getContext(), R.layout.appcenter_style_oneitem_onebanner_ad_layout, null);
            adView.addNativeAdView((ViewGroup) itemView, tAdNativeInfo);

            mItemParent = adView.findViewById(R.id.banner_parent);
            mBannerImageView = (ImageView) adView.findViewById(R.id.banner_imageview);
            mAppIconImageView = (ImageView) adView.findViewById(R.id.app_icon_imageview);
            mAppNameTextView = (TextView) adView.findViewById(R.id.app_name_textview);
            mAppRatingBar = (RatingBar) adView.findViewById(R.id.app_ratingBar);
            mDownloadButton = (Button) adView.findViewById(R.id.download_button);
            mAppDetailsTextView = (TextView) adView.findViewById(R.id.tv_description);
            mAdType = (TextView) adView.findViewById(R.id.ad_type);
            pb_current_size = (ProgressBar) adView.findViewById(R.id.pb_current_size);
            mImageViewIcon = (ImageView) adView.findViewById(R.id.app_size_image);
            mCategoryName = (TextView) adView.findViewById(R.id.category_name_textview);

            if (ConstantUtils.iSAdmobNativeAd(tAdNativeInfo.getFlag())) {
                mAdType.setVisibility(View.VISIBLE);
            } else {
                mAdType.setVisibility(View.GONE);
            }

            adView.setCallToActionView(mDownloadButton);
            adView.setHeadlineView(mAppNameTextView);
            adView.setImageView(mBannerImageView);
            adView.setBodyView(mAppDetailsTextView);
            adView.setIconView(mAppIconImageView);
            List<View> viewList=new ArrayList<>();
            viewList.add(mDownloadButton);
            viewList.add(mBannerImageView);
            viewList.add(mAppDetailsTextView);
            viewList.add(mAppIconImageView);
            tAdNative.registerViews(adView,viewList);

            mAppRatingBar.setRating(5);
            mAppNameTextView.setText(tAdNativeInfo.getTitle());
            mAppDetailsTextView.setText(tAdNativeInfo.getDescription());
            mCategoryName.setText("");
            mDownloadButton.setText(R.string.appcenter_open_text);
            Glide.with(itemView.getContext()).load(tAdNativeInfo.getImage().getUrl()).placeholder(R.drawable.image_default).centerCrop().into(mBannerImageView);
            Glide.with(itemView.getContext()).load(tAdNativeInfo.getImage().getIcon().getIconUrl()).placeholder(R.drawable.image_default).centerCrop().into(mAppIconImageView);
        }
    }
}
