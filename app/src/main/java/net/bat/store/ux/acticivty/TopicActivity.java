package net.bat.store.ux.acticivty;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import net.bat.store.bean.floor.BannerMapBean;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.bean.floor.ResultFloorMapBean;
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
 * Created by bingbing.li on 2017/6/8.
 */

public class TopicActivity extends AppCompatActivity implements AppCenterDynamicBroadcastReceiver.AppCenterReceiverListener,View.OnClickListener{

    private static final String TAG = "TopicActivity";
    public static final int LOADING = 0;
    public static final int LOADED = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int REQUEST_FAIL = 3;
    private RecyclerView mRecyclerView;
    private View mNoNetWorkView, mRequestFailView;
    private ImageView mLoadingProgress;
    private FloatingActionButton mFloatActionButton;
    private String mTopicLink;
    private String mRequestUrlName;
    private String mPath;
    private MainRecyclerViewAdapter adapter;
    private List<FloorMapBean> webstoreFloorMapList;
    private BaseBean<ResultFloorMapBean> mBaseBeanResult;
    private BannerMapBean mBannerMapBean;
    private Bitmap mFloatBitmap;
    private String mFloatWindowFId;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recommend);
        AppCenterDynamicBroadcastReceiver.registerListener(this);
        mTopicLink=getIntent().getStringExtra(AppStoreConstant.FLAG_TOPIC_LINK);
        initView();
        loadData();
    }

    private void initView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_recommend);
        mLoadingProgress= (ImageView)findViewById(R.id.loading);
        mFloatActionButton= (FloatingActionButton)findViewById(R.id.FabPlus);
        ImageView iv_back= (ImageView) findViewById(R.id.icon_back);
        iv_back.setVisibility(View.VISIBLE);
        Glide.with(BaseApplication.getContext()).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingProgress);
        mNoNetWorkView =findViewById(R.id.no_network);
        mRequestFailView =findViewById(R.id.request_fail);
        Button refreshBt = (Button)findViewById(R.id.request_fail_refresh);
        refreshBt.setOnClickListener(this);
        mFloatActionButton.setOnClickListener(this);
        iv_back.setOnClickListener(this);
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

    private void loadData() {
        switchLoadingType(LOADING);
        mRequestUrlName = ConstantUtils.generateImageName(mTopicLink);
        mPath = AppStoreConstant.APPCENTER_JSON_DIR + mRequestUrlName;
        if (ConstantUtils.checkNetworkState(this)) {
            NetworkClient.startRequestQueryPageDetail(BaseApplication.getContext(), mTopicLink, requestPageCallBack);
        } else {
            if (new File(mPath).exists()) {
                loadLocalData();
            } else {
                switchLoadingType(NETWORK_ERROR);
            }
        }
    }

    private void loadLocalData(){
        String page_group_json= FileManagerUtils.getConfigure(mPath);
        Gson gson=new Gson();
        Type jsonType = new TypeToken<BaseBean<ResultFloorMapBean>>() {
        }.getType();
        mBaseBeanResult=gson.fromJson(page_group_json,jsonType);
        refreshView();
    }

    private void refreshView(){
        if(mBaseBeanResult==null||(mBaseBeanResult!=null&&mBaseBeanResult.getResultMap().getWebstoreFloorMapList().size()==0)){
            switchLoadingType(REQUEST_FAIL);
            return;
        }
        switchLoadingType(LOADED);
        webstoreFloorMapList=mBaseBeanResult.getResultMap().getWebstoreFloorMapList();
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
        adapter = new MainRecyclerViewAdapter(this,webstoreFloorMapList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);
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
        mFloatBitmap=null;
    }

    @Override
    public void onNetworkChanged(boolean isAvailable) {
        if(isAvailable){
            loadData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.request_fail_refresh:
                loadData();
                Bundle param= new Bundle();
                param.putString("Action", "request_fail_refresh");
                FirebaseStat.logEvent("RecommendRefresh", param);
                break;
            case R.id.FabPlus:
                if(mBannerMapBean!=null){
                    ConstantUtils.onItemClickBannerCombiner(mBannerMapBean,TopicActivity.this);
                    UploadLogManager.getInstance().generateUseroperationlog(this,4,"",mFloatWindowFId,"","",mBannerMapBean.getLinkType()+"");
                    //UploadLogManager.getInstance().generateResourceLog(this,1,"",mBannerMapBean.getAppAttribute(),5,"",mBannerMapBean.getLinkType()+"");
                    Bundle param_fires = new Bundle();
                    param_fires.putString("floatWindowLinkType", mBannerMapBean.getLinkType()+"");
                    param_fires.putString("floatWindowAttribute", mBannerMapBean.getAppAttribute()+"");
                    FirebaseStat.logEvent("Float_Window_LinkType_" +  mBannerMapBean.getLinkType()+"", param_fires);
                    FirebaseStat.logEvent("Float_Window_Attribute_" +  mBannerMapBean.getAppAttribute()+"", param_fires);
                }
                break;
            case R.id.icon_back:
                TopicActivity.this.finish();
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
}
