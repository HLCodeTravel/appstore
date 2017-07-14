package net.bat.store.ux.acticivty;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.transsion.http.impl.StringCallback;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.base.BaseBean;
import net.bat.store.bean.category.CategoryMapBean;
import net.bat.store.bean.floor.BannerMapBean;
import net.bat.store.bean.floor.ResultFloorMapBean;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.ux.listener.OnVerticalScrollListener;
import net.bat.store.network.NetworkClient;
import net.bat.store.receiver.AppCenterDynamicBroadcastReceiver;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FileManagerUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.adapter.MainRecyclerViewAdapter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;


/**
 * usage
 *
 * @author cheng.qian.
 * @date 2016/9/27.
 * ============================================
 * Copyright (c) 2016 TRANSSION.Co.Ltd.
 * All right reserved.
 **/


public class SortDetailActivity extends AppCompatActivity implements View.OnClickListener,AppCenterDynamicBroadcastReceiver.AppCenterReceiverListener{

    private static final String TAG = "SortDetailActivity";
    public static final int LOADING = 0;
    public static final int LOADED = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int REQUEST_FAIL = 3;
    private MainRecyclerViewAdapter mainRecyclerViewAdapter;
    private ImageView mLoadingProgress;
    private View mNoNetWorkView;
    private View mRequestFailView;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private String mResourceSnapshotId;
    private String mPath;
    private String mRequestUrlName;
    private CategoryMapBean mCategoryMapBean;
    private BaseBean<ResultFloorMapBean> mBaseBeanResult;
    private FloatingActionButton mFloatActionButton;
    private BannerMapBean mBannerMapBean;
    private Bitmap mFloatBitmap;
    private String mGroupName,mFloatWindowFId;


    private StringCallback requestPageCallBack =  new StringCallback() {
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
                        if((AppStoreConstant.STATUS_REQUEST_SUCCESS.equals(mBaseBeanResult.getCode()))
                                && mBaseBeanResult.getResultMap().getWebstoreFloorMapList().size()>0){
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
//                            if((AppStoreConstant.STATUS_REQUEST_SUCCESS.equals(mBaseBeanResult.getCode()))
//                                    && mBaseBeanResult.getResultMap().getWebstoreFloorMapList().size()>0){
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_detail_list);
        mContext=BaseApplication.getContext();
        AppCenterDynamicBroadcastReceiver.registerListener(this);
        initView();

        findViewById(R.id.category_top_bar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortDetailActivity.this.finish();
            }
        });

    }

    private void initView(){
        mCategoryMapBean= (CategoryMapBean) getIntent().getSerializableExtra("categoryMapBean");
        mGroupName=getIntent().getStringExtra("groupName");
        mLoadingProgress = (ImageView) findViewById(R.id.v_loading);
        Glide.with(this).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingProgress);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_sorts_detail);
        mNoNetWorkView = findViewById(R.id.no_network);
        mRequestFailView = findViewById(R.id.request_fail);
        Button refreshBt = (Button) findViewById(R.id.request_fail_refresh);
        mFloatActionButton= (FloatingActionButton) findViewById(R.id.FabPlus);
        refreshBt.setOnClickListener(this);
        mFloatActionButton.setOnClickListener(this);
        View back = findViewById(R.id.category_top_bar_back);
        back.setOnClickListener(this);
        ImageView back_icon= (ImageView) findViewById(R.id.iv_back);
        if(ConstantUtils.isIconShouldRevert()){
            back_icon.setImageResource(R.drawable.icon_back_revert);
        }

        TextView title = (TextView)findViewById(R.id.category_top_bar_title);
        title.setText(mCategoryMapBean.getAppCategory());
        initData();
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
    }

    private void initData(){
        String url=mCategoryMapBean.getPageUrl();
        if(!TextUtils.isEmpty(url)){
            mRequestUrlName= ConstantUtils.generateImageName(url);
        }
        mResourceSnapshotId=mCategoryMapBean.getResourceSnapshotId();
        if(TextUtils.isEmpty(mResourceSnapshotId)){
            mResourceSnapshotId="";
        }
        mPath = AppStoreConstant.APPCENTER_JSON_DIR + mRequestUrlName+"_"+mResourceSnapshotId;
        if(ConstantUtils.checkNetworkState(mContext)){
            if(new File(mPath).exists()){
                loadLocalData();
            }else{
                if(!TextUtils.isEmpty(url)){
                    NetworkClient.startRequestQueryPageDetail(mContext,url,requestPageCallBack);
                }else{
                    switchLoadingType(REQUEST_FAIL);
                }
            }
        }else{
            if(new File(mPath).exists()){
                loadLocalData();
            }else{
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

    private void refreshView(){
        if(mBaseBeanResult==null){
            switchLoadingType(REQUEST_FAIL);
            return;
        }
        ResultFloorMapBean resultFloorMapBean=mBaseBeanResult.getResultMap();
        if(resultFloorMapBean==null||resultFloorMapBean.getWebstoreFloorMapList().size()==0){
            switchLoadingType(REQUEST_FAIL);
            return;
        }
        switchLoadingType(LOADED);
        List<FloorMapBean> webstoreFloorMapList=resultFloorMapBean.getWebstoreFloorMapList();
        for(FloorMapBean floorMapBean :webstoreFloorMapList){
            if(floorMapBean.getFloorType()==10){
                List<BannerMapBean> bannerMapBeanList= floorMapBean.getBannerLinkImage();
                if(bannerMapBeanList.size()>0){
                    mBannerMapBean=bannerMapBeanList.get(0);
                    mFloatWindowFId= floorMapBean.getFloorId();
                    if(ConstantUtils.isShowFloatWindows(mBannerMapBean.getStartTime(),mBannerMapBean.getEndTime())){
                        showFloatWindows();
                    }
                }
                break;
            }
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        mainRecyclerViewAdapter=new MainRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mainRecyclerViewAdapter);
        mainRecyclerViewAdapter.setData(webstoreFloorMapList);
    }

    private void showFloatWindows(){
        String imageUrl=mBannerMapBean.getImageUrl();
        Log.i(TAG, "showFloatWindows: "+imageUrl);
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,getResources().getDisplayMetrics());
        if(!TextUtils.isEmpty(imageUrl)){
            Glide.with(BaseApplication.getContext()).load(imageUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.category_top_bar_back:
                finish();
                Bundle param1= new Bundle();
                param1.putString("Action", "category_top_bar_back");
                FirebaseStat.logEvent("CategoryDetailBack", param1);
                break;
            case R.id.request_fail_refresh:
                switchLoadingType(LOADING);
                initData();
                Bundle param2= new Bundle();
                param2.putString("Action", "request_fail_refresh");
                FirebaseStat.logEvent("CategoryRefreshClick", param2);
                break;
            case R.id.FabPlus:
                if(mBannerMapBean!=null){
                    ConstantUtils.onItemClickBannerCombiner(mBannerMapBean,mContext);
                    UploadLogManager.getInstance().generateUseroperationlog(mContext,4,mGroupName,mFloatWindowFId,"","",mBannerMapBean.getLinkType()+"");
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

    /**
     * 改变页面状态
     *
     * @param type
     *            类型
     */
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
    public void onNetworkChanged(boolean isAvailable) {
        if(isAvailable){
            switchLoadingType(LOADING);
            initData();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        FirebaseStat.setCurrentScreen(this,"SortDetailActivity",null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCenterDynamicBroadcastReceiver.unRegisterListener(this);
        requestPageCallBack=null;
        mainRecyclerViewAdapter=null;
        mCategoryMapBean=null;
        mBaseBeanResult=null;
        mFloatBitmap=null;
    }

}
