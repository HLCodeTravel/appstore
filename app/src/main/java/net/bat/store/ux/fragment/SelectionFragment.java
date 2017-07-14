package net.bat.store.ux.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import net.bat.store.bean.group.PageGroupMapBean;
import net.bat.store.bean.group.PageMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.ux.acticivty.AhaMainActivity;
import net.bat.store.constans.NetworkConstant;

import net.bat.store.ux.listener.OnVerticalScrollListener;
import net.bat.store.network.NetworkClient;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FileManagerUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.adapter.MainRecyclerViewAdapter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

/**
 * selection fragment for AhaMainActivity
 *
 * @author cheng.qian.
 * @date 2016/9/20.
 * ============================================
 * Copyright (c) 2016 TRANSSION.Co.Ltd.
 * All right reserved.
 **/

public class SelectionFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "SelectionFragment";
    private static SelectionFragment sInstance;
    private RecyclerView recyclerView;
    private MainRecyclerViewAdapter adapter;
//    private View mNoNetWorkView, mRequestFailView;
//    private ImageView mLoadingProgress;
    private PageGroupMapBean mPageGroupBean;
    private BaseBean<ResultFloorMapBean> mBaseBeanResult;
    private List<FloorMapBean> webstoreFloorMapList;
    private String mRequestUrlName;
    private Context mContext;
    private String mResourceSnapshotId;
    private String mPath;
    private FloatingActionButton mFloatActionButton;
    private BannerMapBean mBannerMapBean;
    private Bitmap mFloatBitmap;
    private String mFloatWindowFId;
    private AhaMainActivity mFragmentController = null;
    private boolean mIsDataBinded = false;  //数据是否已经绑定

    public static SelectionFragment newInstance() {
        SelectionFragment fragment = new SelectionFragment();
        return fragment;
    }

    /**绑定数据与加载界面分开*/
    public void bindData(PageGroupMapBean pageGroupMapBean) {
        Log.d("yujianbing","bindData() execute..., mIsDataBinded: "+mIsDataBinded+",recyclerView :"+recyclerView);
        if(!mIsDataBinded) {
            this.mPageGroupBean = pageGroupMapBean;
            if(recyclerView == null) {
                return;
            }
            initData();
            mIsDataBinded = true;
        }
        else {
            refreshView();
        }
    }



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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        mFragmentController = (AhaMainActivity)this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext= BaseApplication.getContext();
        View root = inflater.inflate(R.layout.fragment_selection, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.rv_selection);
        mFloatActionButton= (FloatingActionButton) root.findViewById(R.id.FabPlus);

        mFloatActionButton.setOnClickListener(this);
        recyclerView.addOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledUp() {
                super.onScrolledUp();
                Log.e(TAG, "onScrolledUp: ");
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
        recyclerView.setHasFixedSize(true);
        return root;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentController = (AhaMainActivity) activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_fail_refresh:
//                switchLoadingType(LOADING);
                initData();
                Bundle param1 = new Bundle();
                param1.putString("Action", "request_fail_refresh");
                FirebaseStat.logEvent("SelectionFragmentRefresh", param1);
                break;
            case R.id.FabPlus:
                if(mBannerMapBean!=null){
                    Bundle param = new Bundle();
                    param.putString("floatWindowLinkType", mBannerMapBean.getLinkType()+"");
                    param.putString("floatWindowAttribute", mBannerMapBean.getAppAttribute()+"");
                    FirebaseStat.logEvent("Float_Window_LinkType_" +  mBannerMapBean.getLinkType()+"", param);
                    FirebaseStat.logEvent("Float_Window_Attribute_" +  mBannerMapBean.getAppAttribute()+"", param);
                    ConstantUtils.onItemClickBannerCombiner(mBannerMapBean,mContext);
                    UploadLogManager.getInstance().generateUseroperationlog(mContext,4,mPageGroupBean.getGroupNameEn(),mFloatWindowFId,"","",mBannerMapBean.getLinkType()+"");
                    //UploadLogManager.getInstance().generateResourceLog(mContext,1,"",mBannerMapBean.getAppAttribute(),5,"",mBannerMapBean.getLinkType()+"");
                }
                break;
        }
    }

    private void initData() {
        if(mPageGroupBean==null){
            mFragmentController.sendUiMessage(NetworkConstant.MSG_REQUEST_FAIL);
            return;
        }
        List<PageMapBean> pageMapBeans = mPageGroupBean.getPageMapList();
        if (pageMapBeans != null && pageMapBeans.size() == 0) {
            mFragmentController.sendUiMessage(NetworkConstant.MSG_REQUEST_FAIL);
            return;
        }
        PageMapBean pageMapBean = pageMapBeans.get(0);
        String url=pageMapBean.getPageUrl();
        mRequestUrlName= ConstantUtils.generateImageName(url);
        mResourceSnapshotId=pageMapBean.getResourceSnapshotId();
        if(TextUtils.isEmpty(mResourceSnapshotId)){
            mResourceSnapshotId="";
        }
        mPath = AppStoreConstant.APPCENTER_JSON_DIR + mRequestUrlName+"_"+mResourceSnapshotId;
        if(new File(mPath).exists()){
            loadLocalData();
            return;
        }
        if(ConstantUtils.checkNetworkState(mContext)){
            NetworkClient.startRequestQueryPageDetail(mContext, url, requestPageCallBack);
        }else{
            mFragmentController.sendUiMessage(NetworkConstant.MSG_NETWORK_ERROR);
        }
    }

    private void loadLocalData(){
        String page_group_json=FileManagerUtils.getConfigure(mPath);
        Gson gson=new Gson();
        Type jsonType = new TypeToken<BaseBean<ResultFloorMapBean>>() {
        }.getType();
        mBaseBeanResult = gson.fromJson(page_group_json, jsonType);
        refreshView();
    }

    public void refreshView() {
        Log.i(TAG, "refreshView: ");
        if (mBaseBeanResult == null) {
            mFragmentController.sendUiMessage(NetworkConstant.MSG_REQUEST_FAIL);
            return;
        }
        ResultFloorMapBean resultFloorMapBean=mBaseBeanResult.getResultMap();
        if(resultFloorMapBean==null){
            mFragmentController.sendUiMessage(NetworkConstant.MSG_REQUEST_FAIL);
            return;
        }
        webstoreFloorMapList =resultFloorMapBean.getWebstoreFloorMapList();
        for(FloorMapBean floorMapBean :webstoreFloorMapList){
            if(floorMapBean.getFloorType()==10){
                List<BannerMapBean> bannerMapBeanList= floorMapBean.getBannerLinkImage();
                if(bannerMapBeanList.size()>0){
                    mBannerMapBean=bannerMapBeanList.get(0);
                    mFloatWindowFId= floorMapBean.getFloorId();
                    if(ConstantUtils.isShowFloatWindows(mBannerMapBean.getStartTime(),mBannerMapBean.getEndTime())){
                        showFloatWindows();
                    }else{
                        Log.i(TAG, "refreshView: not between start end");
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
        adapter = new MainRecyclerViewAdapter(context, webstoreFloorMapList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
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
                    mFloatActionButton.setImageBitmap(mFloatBitmap);
                }
            });
        }else{
            mFloatActionButton.hide();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter=null;
        mFloatBitmap=null;
    }
}
