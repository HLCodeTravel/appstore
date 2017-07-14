package net.bat.store.ux.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.transsion.http.impl.StringCallback;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.base.BaseBean;
import net.bat.store.bean.category.CategoryMapBean;
import net.bat.store.bean.category.ResultCategoryMapBean;
import net.bat.store.bean.group.PageMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.network.NetworkClient;
import net.bat.store.receiver.AppCenterDynamicBroadcastReceiver;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FileManagerUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.ux.adapter.SortsRecyclerViewAdapter;
import net.bat.store.ux.view.ProgressWebView;

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


public class SortsFragment extends Fragment implements AppCenterDynamicBroadcastReceiver.AppCenterReceiverListener, View.OnClickListener {

    private static final String TAG = "SortsFragment";
    public static final int LOADING = 0;
    public static final int LOADED = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int REQUEST_FAIL = 3;
    private RecyclerView mRecyclerView;
    private View mNoNetWorkView, mRequestFailView;
    private ImageView mLoadingProgress;
    private PageMapBean mPageMapBean;
    private SortsRecyclerViewAdapter adapter;
    private List<CategoryMapBean> categoryMap;
    private BaseBean<ResultCategoryMapBean> mBaseBeanResult;
    private String mRequestUrlName;
    private Context mContext;
    private String mGroupNameEn;

    private ProgressWebView mWebView;
    private String mUrl;
    private long mOldTime;
    private boolean mIsThirdUrl;
    private LinearLayout mLayout;


    private StringCallback requestPageCallBack = new StringCallback() {
        @Override
        public void onFailure(int i, String s, Throwable throwable) {
            refreshView();
        }

        @Override
        public void onSuccess(int code, String json_result) {
            if (code == 200) {
                String path = AppStoreConstant.APPCENTER_JSON_DIR + mRequestUrlName;
                Log.i(TAG, "onPostGet: " + json_result);
                if (!TextUtils.isEmpty(json_result)) {
                    Gson gson = new Gson();
                    Type jsonType = new TypeToken<BaseBean<ResultCategoryMapBean>>() {
                    }.getType();
                    try {
                        mBaseBeanResult = gson.fromJson(json_result, jsonType);
                        if ((AppStoreConstant.STATUS_REQUEST_SUCCESS.equals(mBaseBeanResult.getCode()))
                                && mBaseBeanResult.getResultMap().getCategoryMap().size() > 0) {
                            FileManagerUtils.writeFile(path, json_result, false);
                        }
                    } catch (JsonSyntaxException e) {
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
//                    String path=AppStoreConstant.APPCENTER_JSON_DIR+mRequestUrlName;
//                    String json_result = httpResponse.getResponseBody();
//                    Log.i(TAG, "onPostGet: "+json_result);
//                    if (!TextUtils.isEmpty(json_result)) {
//                        Gson gson=new Gson();
//                        Type jsonType = new TypeToken<BaseBean<ResultCategoryMapBean>>() {
//                        }.getType();
//                        try{
//                            mBaseBeanResult=gson.fromJson(json_result,jsonType);
//                            if((AppStoreConstant.STATUS_REQUEST_SUCCESS.equals(mBaseBeanResult.getCode()))
//                                    && mBaseBeanResult.getResultMap().getCategoryMap().size()>0){
//                                FileManagerUtils.writeFile(path, json_result, false);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = BaseApplication.getContext();
        AppCenterDynamicBroadcastReceiver.registerListener(this);
        View rootView = inflater.inflate(R.layout.fragment_sorts, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_sorts);
        mLoadingProgress = (ImageView) rootView.findViewById(R.id.loading);
        Glide.with(this).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingProgress);
        Bundle bundle = getArguments();
        mPageMapBean = (PageMapBean) bundle.getSerializable("data");
        mIsThirdUrl = mPageMapBean.getIsThirdUrl() == 1 ? true : false;
        mGroupNameEn = bundle.getString("groupname", "");
        mNoNetWorkView = rootView.findViewById(R.id.no_network);
        mRequestFailView = rootView.findViewById(R.id.request_fail);
        Button refreshBt = (Button) rootView.findViewById(R.id.request_fail_refresh);
        refreshBt.setOnClickListener(this);
        if (mIsThirdUrl) {
            mRecyclerView.setVisibility(View.GONE);
            mLoadingProgress.setVisibility(View.GONE);
            mUrl = mPageMapBean.getPageUrl();
            initWebView(rootView, mPageMapBean.getPageUrl());
        } else {
            initData();
        }
        return rootView;
    }

    private void initWebView(View view, String url) {
        mLayout = (LinearLayout) view.findViewById(R.id.web_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);

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
                        //getActivity().finish();
                        return false;
                    }
                }
                return false;
            }
        });
        mWebView.loadUrl(url);
    }

    private void initData() {
        if (mIsThirdUrl) {
            if (mWebView != null) {
                mWebView.loadUrl(mPageMapBean.getPageUrl());
            }
        } else {
            switchLoadingType(LOADING);
            String url = mPageMapBean.getPageUrl();
            mRequestUrlName = ConstantUtils.generateImageName(url);
            if (ConstantUtils.checkNetworkState(mContext)) {
                NetworkClient.startRequestQueryAppCategory(mContext, url, requestPageCallBack);
            } else {
                String path = AppStoreConstant.APPCENTER_JSON_DIR + mRequestUrlName;
                if (new File(path).exists()) {
                    String page_group_json = FileManagerUtils.getConfigure(path);
                    Gson gson = new Gson();
                    Type jsonType = new TypeToken<BaseBean<ResultCategoryMapBean>>() {
                    }.getType();
                    mBaseBeanResult = gson.fromJson(page_group_json, jsonType);
                    refreshView();
                } else {
                    switchLoadingType(REQUEST_FAIL);
                }
            }
        }
    }

    private void refreshView() {
        if (mBaseBeanResult == null || (mBaseBeanResult != null && mBaseBeanResult.getResultMap().getCategoryMap().size() == 0)) {
            switchLoadingType(REQUEST_FAIL);
            return;
        }
        switchLoadingType(LOADED);
        categoryMap = mBaseBeanResult.getResultMap().getCategoryMap();
        String secondaryMenuName = "";
        if (mPageMapBean != null) {
            secondaryMenuName = mPageMapBean.getSecondaryMenuNameEn();
        }
        Context context = getContext();
        if (context == null) {
            context = getActivity();
        }
        if (context == null) {
            return;
        }
        adapter = new SortsRecyclerViewAdapter(context, categoryMap, mGroupNameEn, secondaryMenuName);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: SortsFragment");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppCenterDynamicBroadcastReceiver.unRegisterListener(this);
        mBaseBeanResult = null;
        if (categoryMap != null) {
            categoryMap.clear();
            categoryMap = null;
        }
        requestPageCallBack = null;
        adapter = null;
        mPageMapBean = null;
        mWebView = null;
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_fail_refresh:
                initData();
                Bundle param = new Bundle();
                param.putString("Action", "request_fail_refresh");
                FirebaseStat.logEvent("CategoryRefresh", param);
                break;
        }
    }

    @Override
    public void onNetworkChanged(boolean isAvailable) {
        if (isAvailable) {
            initData();
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
            if (newProgress > 50) {
                mLayout.setVisibility(View.VISIBLE);
                mLoadingProgress.setVisibility(View.GONE);
            }
        }
    };
}
