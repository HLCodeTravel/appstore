package net.bat.store.ux.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transsion.iad.core.TAdError;
import com.transsion.iad.core.TAdListener;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.bean.Ad;
import com.transsion.iad.core.bean.TAdNativeInfo;

import net.bat.store.BaseApplication;
import net.bat.store.R;
import net.bat.store.ScrollGridLayoutManager;
import net.bat.store.bean.floor.AppMapBean;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.ux.acticivty.MoreDetailActivity;
import net.bat.store.ux.adapter.FloorGridCardItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liang.he on 2017/7/7.
 */

public class FloorGridCardHolder extends BaseFloorViewHolder {

    private TextView tv_card_bean_more;
    private RecyclerView mGridView;
    private LinearLayout ll_card_bean_more;
    private TextView card_bean_name;
    private View card_bean_header;
    private View topic_header;
    private TextView topic_textview;
    private FloorGridCardItemAdapter floorGridCardItemAdapter;
    private ImageView iv_topic;
    //Context appContext;

    public FloorGridCardHolder(Context context, View itemView) {
        super(context, itemView);
        ScrollGridLayoutManager gridManager = new ScrollGridLayoutManager(itemView.getContext(), 3);
        //GridLayoutManager gridManager = new GridLayoutManager(itemView.getContext(), 3);
        gridManager.setOrientation(GridLayoutManager.VERTICAL);
        gridManager.setScrollEnabled(false);

        mGridView = (RecyclerView) itemView.findViewById(R.id.personalitation_grid);
        mGridView.setHasFixedSize(true);
        mGridView.setLayoutManager(gridManager);
        ll_card_bean_more = (LinearLayout) itemView.findViewById(R.id.ll_card_bean_more);
        tv_card_bean_more = (TextView) itemView.findViewById(R.id.tv_card_bean_more);
        card_bean_name = (TextView) itemView.findViewById(R.id.card_bean_name);
        card_bean_header = itemView.findViewById(R.id.card_bean_header);
        topic_header = itemView.findViewById(R.id.v_card_topic_header);
        topic_textview = (TextView) itemView.findViewById(R.id.tv_card_name);
        iv_topic = (ImageView) itemView.findViewById(R.id.iv_background);
        floorGridCardItemAdapter = new FloorGridCardItemAdapter(itemView.getContext());
        mGridView.setAdapter(floorGridCardItemAdapter);
    }

    @Override
    public void onBindView(FloorMapBean FloorMapBean) {
        final FloorMapBean floorMapBean = FloorMapBean;
        List<AppMapBean> appMapList = floorMapBean.getAppMapList();
        String CardBeanName = floorMapBean.getFloorTitle();
        card_bean_name.setText(CardBeanName);
        for (AppMapBean appMapBean : appMapList) {
            String sold = appMapBean.getSlotId();
            String appId = appMapBean.getAppId();
            if (!TextUtils.isEmpty(sold) && !mNativeInfoMaps.containsKey(appId)) {
                requestAdNative(appId, sold);
            }
        }
        floorGridCardItemAdapter.setData(appMapList, floorMapBean.getFloorId(), floorMapBean.getIsShowTag(), mNativeInfoMaps, mAdNativeMaps);
        // floorGridCardItemAdapter.setData(appMapList, floorMapBean.getFloorId(), floorMapBean.getIsShowTag(), null, null);
        final String moreUrl = floorMapBean.getMoreUrl();
        if (TextUtils.isEmpty(moreUrl)) {
            ll_card_bean_more.setVisibility(View.GONE);
        } else {
            ll_card_bean_more.setVisibility(View.VISIBLE);
            tv_card_bean_more.setText(R.string.more);
        }

        if (AppStoreConstant.FLAG_SHOW_TITLE == floorMapBean.getIsShowTitle()) {
            card_bean_header.setVisibility(View.VISIBLE);
        } else {
            card_bean_header.setVisibility(View.GONE);
        }
        String picTitleUrl = floorMapBean.getTitlePicUrl();
        String picTitleColor = floorMapBean.getTitlePicColor();
        if (AppStoreConstant.FLAG_SHOW_TITLE == floorMapBean.getIsShowTitlePic() && !TextUtils.isEmpty(picTitleUrl)) {
            card_bean_header.setVisibility(View.GONE);
            topic_header.setVisibility(View.VISIBLE);
            Glide.with(appContext).load(picTitleUrl).centerCrop().into(iv_topic);
            if (!TextUtils.isEmpty(picTitleColor)) {
                topic_textview.setTextColor(Color.parseColor(picTitleColor));
            }
            topic_textview.setText(CardBeanName);
        } else {
            if (AppStoreConstant.FLAG_SHOW_TITLE == floorMapBean.getIsShowTitle()) {
                card_bean_header.setVisibility(View.VISIBLE);
            }
            topic_header.setVisibility(View.GONE);
        }

        ll_card_bean_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle param = new Bundle();
                param.putString("Action", "tv_card_bean_more");
                FirebaseStat.logEvent("MoreClick", param);
                Intent intent = new Intent(appContext, MoreDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("moreurl", moreUrl);
                intent.putExtra("moretitle", floorMapBean.getFloorTitle());
                intent.putExtra("moreSnapshotId", floorMapBean.getMoreSnapshotId());
                appContext.startActivity(intent);
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

}
