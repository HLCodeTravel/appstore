package net.bat.store.widget.floorcard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.bat.store.R;
import net.bat.store.bean.floor.AppMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.acticivty.AppDetailActivity;
import net.bat.store.ux.acticivty.H5Activity;
import net.bat.store.widget.TouchMaskRelativeLayout;

import java.util.List;


/**
 * @author libingbing
 */
public class HorizontalScrollCardView extends LinearLayout{

    private static final String TAG = "HorizontalScrollCardVie";
    private Context mContext;
    private LinearLayout mScrollParent;
    private int mViewNum;
    private ImageView mAppIcon;
    private TextView mAppTitle;
    private RatingBar mRating;
    private TouchMaskRelativeLayout mItemParent;
    private AppMapBean mAppInfo;
    private LayoutInflater mInflate;
    private List<AppMapBean> mAppMapBeanList;
    private String floorId;

    public HorizontalScrollCardView(Context context) {
        super(context);
    }

    public HorizontalScrollCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContext = getContext();
        mInflate = LayoutInflater.from(mContext);
        initView();
    }

    /**
     * 添加数据
     */
    public void addData(List<AppMapBean> appMapBeanList,String floorId) {
        this.floorId=floorId;
        if (appMapBeanList == null || appMapBeanList.size() == 0) {
            return;
        }
        if (appMapBeanList == mAppMapBeanList) {
            //同一个卡片，不需要再次刷新view
            return;
        }
        mAppMapBeanList = appMapBeanList;
        if (mScrollParent.getChildCount() > 1) {
            mScrollParent.removeViews(1, mScrollParent.getChildCount() - 1);
        }
        loadData();
    }

    public void initView() {
        mScrollParent = (LinearLayout) findViewById(R.id.scroll_parent);
    }

    private void loadData() {
        mViewNum = mAppMapBeanList.size();
        for (int i = 0; i < mViewNum; i++) {
            View itemView = null;
            if (itemView == null) {
                itemView = mInflate.inflate(R.layout.appcenter_horizontal_card_item,mScrollParent,false);
                mItemParent = (TouchMaskRelativeLayout) itemView.findViewById(R.id.item_parent);
                mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
                mAppTitle = (TextView) itemView.findViewById(R.id.app_title);
                mRating = (RatingBar) itemView.findViewById(R.id.app_score);
            }
            final int position = i;
            mAppInfo = mAppMapBeanList.get(i);
            String title = mAppInfo.getAppName();
            mAppTitle.setText(title);
            try {
                mRating.setRating(Float.parseFloat(mAppInfo.getAppGrade()));
            } catch (Exception e) {
                mRating.setRating(5);
            }
            Glide.with(mContext).load(mAppInfo.getIconUrl()).placeholder(R.drawable.image_default).error(R.drawable.image_default).into(mAppIcon);
            mScrollParent.addView(itemView);

            View itemPaddingView = new View(mContext); //左右的间距view
            int itemPaddingSize = (int) mContext.getResources().getDimension(R.dimen.appcenter_horizontalcard_item_marginleft);
            itemPaddingView.setLayoutParams(new LinearLayout.LayoutParams(itemPaddingSize, LinearLayout.LayoutParams.MATCH_PARENT));
            mScrollParent.addView(itemPaddingView);
            mItemParent.setTag(mAppInfo);
            mItemParent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(mAppMapBeanList.get(position),position);
                }
            });

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
        if(!TextUtils.isEmpty(appMapBean.getAppCategory())){
            FirebaseStat.logEvent("DLS_Category_" + appMapBean.getAppCategory().replace(" ","_").replace("&","_"), param1);
        }
        if(!TextUtils.isEmpty(appMapBean.getDeveloper())){
            FirebaseStat.logEvent("DLS_Developer_" + appMapBean.getDeveloper().replace(" ","_").replace("&","_"), param1);
        }
        FirebaseStat.logEvent("DLS_Attribute_" + appMapBean.getAppAttribute(), param1);
        UploadLogManager.getInstance().generateResourceLog(mContext,1,appMapBean.getAppNameEn(),appMapBean.getAppAttribute(),2,"","");
    }

    private void onClickItem(AppMapBean appMapBean,int position) {
        int appType = appMapBean.getAppAttribute();
        if (AppStoreConstant.APP_SELF == appType
                || AppStoreConstant.APP_H5_GAME == appType) {
            Bundle fiebase_bundle = new Bundle();
            fiebase_bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mAppInfo.getAppNameEn());
            fiebase_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, position + "");
            fiebase_bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Horizion_" + floorId);
            FirebaseStat.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, fiebase_bundle);
            Intent intent = new Intent(mContext, AppDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("appBean", appMapBean);
            bundle.putInt("position", position);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        } else if (AppStoreConstant.APP_H5 == appType||appType==AppStoreConstant.APP_APPSFLER) {//H5游戏
            thirdFireBaseSta(appMapBean,floorId);
            H5Activity.openLink(mContext, appMapBean.getAppUrl());
        } else if (AppStoreConstant.APP_GOOGLE == appType) {//Google应用
            thirdFireBaseSta(appMapBean,floorId);
            String uriString=appMapBean.getAppUrl();
            if(ConstantUtils.isExistsProcess(AppStoreConstant.PKG_GOOGLE_PLAY)){
                ConstantUtils.goGooglePlayMarket(uriString,mContext);
            }else{
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    }
}
