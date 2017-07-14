package net.bat.store.widget.banner.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.TNativeAdView;

import net.bat.store.R;
import net.bat.store.bean.ad.AdBean;
import net.bat.store.bean.floor.AppMapBean;
import net.bat.store.bean.floor.BannerMapBean;
import net.bat.store.widget.banner.holder.Holder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkImageHolderView implements Holder<Object> {
    private static final String TAG = "NetworkImageHolderView";
    private ImageView imageView;
    private LinearLayout view;
    private TNativeAdView tNativeAdView;

    public NetworkImageHolderView() {
    }

    @Override
    public View createView(Context context, Object object, Map<String, AdBean> map) {
        if (object instanceof AppMapBean) {
            AppMapBean appMapBean = (AppMapBean) object;
            String sold = appMapBean.getSlotId();
            String appId=appMapBean.getAppId();
            if (!TextUtils.isEmpty(sold)) {
                AdBean adBean = map.get(appId);
                if (adBean != null) {
                    Log.i(TAG, "createView: ad--"+sold);
                    view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.ad_viewpager_item, null, false);
                    tNativeAdView = (TNativeAdView) LayoutInflater.from(context).inflate(R.layout.layout_pager_item, null, false);
                    imageView = (ImageView) tNativeAdView.findViewById(R.id.iv_content_image);
                    return view;
                }else{
                    imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.viewpager_item, null, false);
                    return imageView;
                }
            }else{
                imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.viewpager_item, null, false);
                return imageView;
            }
        }else{
            imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.viewpager_item, null, false);
            return imageView;
        }

    }


    @Override
    public void UpdateUI(Context context, int position, Object object, Map<String, AdBean> map) {
        if (object instanceof AppMapBean) {
            AppMapBean appMapBean = (AppMapBean) object;
            String sold = appMapBean.getSlotId();
            String appId=appMapBean.getAppId();
            if (!TextUtils.isEmpty(sold)) {
                AdBean adBean = map.get(appId);
                if (adBean != null) {
                    tNativeAdView.addNativeAdView(view, adBean.gettAdNativeInfo());
                    TAdNative adNative = adBean.gettAdNative();
                    tNativeAdView.setImageView(imageView);
                    List<View> viewList=new ArrayList<>();
                    viewList.add(imageView);
                    adNative.registerViews(tNativeAdView,viewList);
                    Glide.with(context)
                            .load(adBean.gettAdNativeInfo().getImage().getUrl())
                            .placeholder(R.drawable.menu_top)
                            .into(imageView);
                } else {
                    Glide.with(context)
                            .load(appMapBean.getThemeUrl())
                            .placeholder(R.drawable.image_default)
                            .centerCrop()
                            .into(imageView);
                }
            } else {
                AppMapBean bannerMapBean = (AppMapBean) object;
                Glide.with(context)
                        .load(bannerMapBean.getThemeUrl())
                        .placeholder(R.drawable.image_default)
                        .centerCrop()
                        .into(imageView);

            }
        } else {
            BannerMapBean bannerMapBean = (BannerMapBean) object;
            Glide.with(context)
                    .load(bannerMapBean.getImageUrl())
                    .placeholder(R.drawable.image_default)
                    .centerCrop()
                    .into(imageView);

        }
    }
}
