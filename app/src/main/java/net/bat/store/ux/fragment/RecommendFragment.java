package net.bat.store.ux.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.transsion.http.impl.StringCallback;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.base.BaseBean;
import net.bat.store.bean.floor.BannerMapBean;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.bean.floor.ResultFloorMapBean;
import net.bat.store.bean.group.PageMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.ux.listener.OnVerticalScrollListener;
import net.bat.store.network.NetworkClient;
import net.bat.store.receiver.AppCenterDynamicBroadcastReceiver;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FileManagerUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.view.ProgressWebView;
import net.bat.store.ux.adapter.MainRecyclerViewAdapter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;


/**
 * usage
 *
 * @author cheng.qian.
 * @date 2016/9/26.
 * ============================================
 * Copyright (c) 2016 TRANSSION.Co.Ltd.
 * All right reserved.
 **/


@SuppressLint("ValidFragment")
public class RecommendFragment extends Fragment implements AppCenterDynamicBroadcastReceiver.AppCenterReceiverListener,View.OnClickListener{

    private static final String TAG = "RecommendFragment";
    public static final int LOADING = 0;
    public static final int LOADED = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int REQUEST_FAIL = 3;
    private  String flag;
    private RecyclerView mRecyclerView;
    private View mNoNetWorkView, mRequestFailView;
    private ImageView mLoadingProgress;
    private PageMapBean mPageMapBean;
    private MainRecyclerViewAdapter adapter;
    private List<FloorMapBean> webstoreFloorMapList;
    private BaseBean<ResultFloorMapBean> mBaseBeanResult;
    private String mRequestUrlName;
    private Context mContext;
    private String mResourceSnapshotId;
    private String mPath;
    private FloatingActionButton mFloatActionButton;
    private ProgressWebView mWebView;
    private String mUrl;
    private long mOldTime;
    private boolean mIsThirdUrl;
    private LinearLayout mLayout;
    private BannerMapBean mBannerMapBean;
    private Bitmap mFloatBitmap;

    private StringCallback requestPageCallBack = new StringCallback() {
        @Override
        public void onFailure(int i, String s, Throwable throwable) {
            refreshView();
        }

        @Override
        public void onSuccess(int code, String json_result) {
            if (code == 200) {
                Log.i(TAG, "onPostGet: "+json_result);
                if (!TextUtils.isEmpty(json_result)) {
                    Gson gson=new Gson();
                    Type jsonType = new TypeToken<BaseBean<ResultFloorMapBean>>() {
                    }.getType();
                    try{
                        mBaseBeanResult=gson.fromJson(json_result,jsonType);
                        if ((AppStoreConstant.STATUS_REQUEST_SUCCESS.equals(mBaseBeanResult.getCode()))
                                && mBaseBeanResult.getResultMap().getWebstoreFloorMapList().size() > 0) {
                            FileManagerUtils.writeFile(mPath, json_result, false);
                        }
                    }catch (JsonSyntaxException e){
                        e.printStackTrace();
                    }
                }
            }
            refreshView();
        }
    };

//    private HttpUtils.HttpListener requestPageCallBack=new HttpUtils.HttpListener() {
//        /**
//         * @param httpResponse
//         */
//        @Override
//        protected void onPostGet(HttpResponse httpResponse) {
//            super.onPostGet(httpResponse);
//            if(httpResponse!=null) {
//                int code = httpResponse.getResponseCode();
//                Log.i(TAG, "onPostGet: " + code);
//                if (code == 200) {
//                    String json_result = httpResponse.getResponseBody();
//                    Log.i(TAG, "onPostGet: "+json_result);
//                    if (!TextUtils.isEmpty(json_result)) {
//                        Gson gson=new Gson();
//                        Type jsonType = new TypeToken<BaseBean<ResultFloorMapBean>>() {
//                        }.getType();
//                        try{
//                            mBaseBeanResult=gson.fromJson(json_result,jsonType);
//                            if ((AppStoreConstant.STATUS_REQUEST_SUCCESS.equals(mBaseBeanResult.getCode()))
//                                    && mBaseBeanResult.getResultMap().getWebstoreFloorMapList().size() > 0) {
//                                FileManagerUtils.writeFile(mPath, json_result, false);
//                            }
//                        }catch (JsonSyntaxException e){
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }else{
//                Log.i(TAG, "onPostGet: null");
//            }
//            refreshView();
//        }
//    };

    public RecommendFragment(){}

    @SuppressLint("ValidFragment")
    public RecommendFragment(String flag) {
        this.flag=flag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppCenterDynamicBroadcastReceiver.registerListener(this);
        mContext= BaseApplication.getContext();
        View rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
        Bundle bundle = getArguments();
        mPageMapBean= (PageMapBean) bundle.getSerializable("data");
        mIsThirdUrl = mPageMapBean.getIsThirdUrl() == 1 ? true:false;
        // find views
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recommend);
        mLoadingProgress= (ImageView) rootView.findViewById(R.id.loading);
        mFloatActionButton= (FloatingActionButton) rootView.findViewById(R.id.FabPlus);
        Glide.with(mContext).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingProgress);
        mNoNetWorkView = rootView.findViewById(R.id.no_network);
        mRequestFailView = rootView.findViewById(R.id.request_fail);
        Button refreshBt = (Button) rootView.findViewById(R.id.request_fail_refresh);
        refreshBt.setOnClickListener(this);
        if (mIsThirdUrl){
            mRecyclerView.setVisibility(View.GONE);
            mLoadingProgress.setVisibility(View.GONE);
            mUrl = mPageMapBean.getPageUrl();
            initWebView(rootView,mUrl);
        }else {
            initData();
        }
        mFloatActionButton.setOnClickListener(this);
        mRecyclerView.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledUp() {
                super.onScrolledUp();
                Log.i(TAG, "onScrolledUp: ");
                if(mFloatBitmap!=null&&ConstantUtils.isShowFloatWindows(mBannerMapBean.getStartTime(),mBannerMapBean.getEndTime())){
                    mFloatActionButton.setImageBitmap(mFloatBitmap);
                    mFloatActionButton.show();
                }else{
                    mFloatActionButton.hide();
                }
            }

            @Override
            public void onScrolledDown() {
                super.onScrolledDown();
                Log.i(TAG, "onScrolledDown: ");
                mFloatActionButton.hide();
            }

            @Override
            public void onScrolledToBottom() {
                super.onScrolledToBottom();
                Log.i(TAG, "onScrolledToBottom: ");
                mFloatActionButton.hide();
            }

            @Override
            public void onScrolledToTop() {
                super.onScrolledToTop();
                Log.i(TAG, "onScrolledToTop: ");
                if(mFloatBitmap!=null&&ConstantUtils.isShowFloatWindows(mBannerMapBean.getStartTime(),mBannerMapBean.getEndTime())){
                    mFloatActionButton.setImageBitmap(mFloatBitmap);
                    mFloatActionButton.show();
                }else{
                    mFloatActionButton.hide();
                }
            }
        });
        return rootView;
    }

    private void initWebView(View view,String url) {
        mLayout = (LinearLayout) view.findViewById(R.id.area_webview);
        mLayout.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        mWebView = new ProgressWebView(mContext);
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);
        mWebView.setFocusableInTouchMode(true);
        mWebView.requestFocus();
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.e(TAG, "back");
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (System.currentTimeMillis() - mOldTime < 1500) {
                            mWebView.clearHistory();
                            mWebView.loadUrl(mUrl);
                        } else if (mWebView.canGoBack()) {
                            Log.e(TAG, "history back");
                            mWebView.goBack();
                            mOldTime = System.currentTimeMillis();
                            return true;
                        }
                        return false;
                    }
                }
                return false;
            }
        });
        mWebView.loadUrl(url);
    }

    private void refreshView(){
        if(mBaseBeanResult==null){
            switchLoadingType(REQUEST_FAIL);
            return;
        }
        ResultFloorMapBean resultFloorMapBean=mBaseBeanResult.getResultMap();
        if(resultFloorMapBean==null||(resultFloorMapBean.getWebstoreFloorMapList().size()==0)){
            switchLoadingType(REQUEST_FAIL);
            return;
        }
        switchLoadingType(LOADED);
        webstoreFloorMapList=resultFloorMapBean.getWebstoreFloorMapList();
        for(FloorMapBean floorMapBean :webstoreFloorMapList){
            if(floorMapBean.getFloorType()==10){
                List<BannerMapBean> bannerMapBeanList= floorMapBean.getBannerLinkImage();
                if(bannerMapBeanList.size()>0){
                    mBannerMapBean=bannerMapBeanList.get(0);
                    if(ConstantUtils.isShowFloatWindows(mBannerMapBean.getStartTime(),mBannerMapBean.getEndTime())){
                        showFloatWindows();
                    }
                }
                break;
            }
        }
        Context context=getContext();
        if(context==null){
            context=getActivity();
        }
        if(context==null){
            return;
        }
        adapter = new MainRecyclerViewAdapter(context,webstoreFloorMapList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);
    }

    private void showFloatWindows(){
        String imageUrl=mBannerMapBean.getImageUrl();
        Log.i(TAG, "showFloatWindows: "+imageUrl);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,getResources().getDisplayMetrics());
        if(!TextUtils.isEmpty(imageUrl)){
            Glide.with(BaseApplication.getContext()).load(imageUrl).asBitmap().override(width,height).centerCrop().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Log.i(TAG, "onResourceReady: ");
                    mFloatBitmap=resource;
                    mFloatActionButton.show();
                    mFloatActionButton.setImageBitmap(resource);
                }
            });
        }else{
            mFloatActionButton.hide();
        }
    }

    private void initData(){
        if(mIsThirdUrl){
            if (mWebView != null){
                mWebView.loadUrl(mPageMapBean.getPageUrl());
            }
        }else {
            switchLoadingType(LOADING);
            String url = mPageMapBean.getPageUrl();
            Bundle data = new Bundle();
            data.putString(FirebaseAnalytics.Param.ITEM_ID, flag + "_" + mPageMapBean.getSecondaryMenuNameEn());
            data.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "pageClick");
            FirebaseStat.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, data);
            mRequestUrlName = ConstantUtils.generateImageName(url);
            mResourceSnapshotId = mPageMapBean.getResourceSnapshotId();
            if (TextUtils.isEmpty(mResourceSnapshotId)) {
                mResourceSnapshotId = "";
            }
            mPath = AppStoreConstant.APPCENTER_JSON_DIR + mRequestUrlName + "_" + mResourceSnapshotId;
            if (new File(mPath).exists()) {
                loadLocalData();
                return;
            }
            if (ConstantUtils.checkNetworkState(mContext)) {
                NetworkClient.startRequestQueryPageDetail(mContext, url, requestPageCallBack);
            } else {
                switchLoadingType(NETWORK_ERROR);
            }
        }
    }

    private void loadLocalData(){
        String page_group_json=FileManagerUtils.getConfigure(mPath);
        Gson gson=new Gson();
        Type jsonType = new TypeToken<BaseBean<ResultFloorMapBean>>() {
        }.getType();
        mBaseBeanResult=gson.fromJson(page_group_json,jsonType);
        refreshView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: RecommendFragment");
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppCenterDynamicBroadcastReceiver.unRegisterListener(this);
        mBaseBeanResult=null;
        if(webstoreFloorMapList!=null){
            webstoreFloorMapList.clear();
            webstoreFloorMapList=null;
        }
        requestPageCallBack=null;
        adapter=null;
        mPageMapBean=null;
        mWebView = null;
        mFloatBitmap=null;
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onNetworkChanged(boolean isAvailable) {
       if(isAvailable){
           initData();
       }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.request_fail_refresh:
                initData();
                Bundle param= new Bundle();
                param.putString("Action", "request_fail_refresh");
                FirebaseStat.logEvent("RecommendRefresh", param);
                break;
            case R.id.FabPlus:
                if(mBannerMapBean!=null){
                    ConstantUtils.onItemClickBannerCombiner(mBannerMapBean,mContext);
                    UploadLogManager.getInstance().generateUseroperationlog(mContext,4,flag,mPageMapBean.getSecondaryMenuNameEn(),"","",mBannerMapBean.getLinkType()+"");
                    //UploadLogManager.getInstance().generateResourceLog(mContext,1,"",mBannerMapBean.getAppAttribute(),5,"",mBannerMapBean.getLinkType()+"");
                    Bundle param_fires = new Bundle();
                    param_fires.putString("floatWindowLinkType", mBannerMapBean.getLinkType()+"");
                    param_fires.putString("floatWindowAttribute", mBannerMapBean.getAppAttribute()+"");
                    FirebaseStat.logEvent("Float_Window_LinkType_" +  mBannerMapBean.getLinkType()+"", param_fires);
                    FirebaseStat.logEvent("Float_Window_Attribute_" +  mBannerMapBean.getAppAttribute()+"", param_fires);
                }
                break;
        }
    }

    public void switchLoadingType(int type) {
        switch (type) {
            case LOADING:
                mRecyclerView.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.VISIBLE);
                mNoNetWorkView.setVisibility(View.GONE);
                mRequestFailView.setVisibility(View.GONE);
                break;
            case LOADED:
                mRecyclerView.setVisibility(View.VISIBLE);
                mLoadingProgress.setVisibility(View.GONE);
                mNoNetWorkView.setVisibility(View.GONE);
                mRequestFailView.setVisibility(View.GONE);
                break;
            case NETWORK_ERROR:
                mLoadingProgress.setVisibility(View.GONE);
                mNoNetWorkView.setVisibility(View.VISIBLE);
                mRequestFailView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                break;
            case REQUEST_FAIL:
                mRecyclerView.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.GONE);
                mNoNetWorkView.setVisibility(View.GONE);
                mRequestFailView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    WebChromeClient webChromeClient = new WebChromeClient() {

        // 扩充数据库的容量（在WebChromeClinet中实现）
        @Override
        public void onExceededDatabaseQuota(String url,
                                            String databaseIdentifier, long currentQuota,
                                            long estimatedSize, long totalUsedQuota,
                                            WebStorage.QuotaUpdater quotaUpdater) {

            quotaUpdater.updateQuota(estimatedSize * 2);
        }

        // 扩充缓存的容量
        @Override
        public void onReachedMaxAppCacheSize(long spaceNeeded,
                                             long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {

            quotaUpdater.updateQuota(spaceNeeded * 2);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }


        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mLayout.setVisibility(View.GONE);
            mLoadingProgress.setVisibility(View.VISIBLE);
            if (newProgress > 50){
                mLayout.setVisibility(View.VISIBLE);
                mLoadingProgress.setVisibility(View.GONE);
            }
        }
    };
}
