package net.bat.store.ux.dataprovider;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.transsion.http.impl.StringCallback;

import net.bat.store.BaseApplication;
import net.bat.store.bean.base.BaseBean;
import net.bat.store.bean.group.ResultPageGroupMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.constans.NetworkConstant;
import net.bat.store.download.DownloadNotificationListener;
import net.bat.store.download.DownloadState;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.network.NetworkClient;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FileManagerUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

/**
 * usage
 * 用于AhaMainActivity获取数据类
 * @author 俞剑兵.
 * @data 2017/7/11.
 * ======================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */

public class MainDataProvider {
    private static final String TAG = "MainDataProvider";
    private Context mApplicationContext;
    private DataProviderListener mDataProviderListener = null;

    public interface DataProviderListener{
        /**加载本地数据结果*/
        void onInitLocalDataResult(BaseBean<ResultPageGroupMapBean> data);
        /**初始化请求网络数据结果(没有本地数据的情况)*/
        void onInitNetDataResult(BaseBean<ResultPageGroupMapBean> data);
        /**刷新请求网络数据结果*/
        void onRefreshNetDataResult(BaseBean<ResultPageGroupMapBean> data);
        /**请求数据中间状态*/
        void onDataGetProgress(int progressCode);
    }
    public MainDataProvider(Context context){
        mApplicationContext = context.getApplicationContext();
    }

    public void setDataProviderListener(DataProviderListener dataProviderListener){
        this.mDataProviderListener = dataProviderListener;
    }

    public void initData() {
        mDataProviderListener.onDataGetProgress(NetworkConstant.MSG_LOADING);
        String path = AppStoreConstant.APPCENTER_JSON_DIR + AppStoreConstant.APPCENTER_PAGEGROUP_JSON;
        if (new File(path).exists()) {   //如果存在缓存，则先用缓存数据
            Log.i(TAG, "initData: local");
            String page_group_json = FileManagerUtils.getConfigure(path);
            Gson gson = new Gson();
            Type jsonType = new TypeToken<BaseBean<ResultPageGroupMapBean>>() {
            }.getType();
            BaseBean<ResultPageGroupMapBean> localBaseBean = gson.fromJson(page_group_json, jsonType);
            mDataProviderListener.onInitLocalDataResult(localBaseBean);
        } else {  //如果没有缓存，则直接请求网络获取数据
            Log.i(TAG, "initData: network");
            if (ConstantUtils.checkNetworkState(mApplicationContext)) {
                NetworkClient.startRequestQueryPageGroup(mApplicationContext, initRequestGroupCallBack);
            } else {
                mDataProviderListener.onDataGetProgress(NetworkConstant.MSG_NETWORK_ERROR);
            }
        }

        List<DownloadTask> tasks = DownloadTaskManager.getInstance(BaseApplication.getContext()).getDownloadingTask();
        if (tasks != null && !tasks.isEmpty()) {
            NotificationManager nm = (NotificationManager)mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

            for (DownloadTask task : tasks) {
                if (task.getDownloadState() == DownloadState.DOWNLOADING || task.getDownloadState() == DownloadState.WAITING) {
                    task.setDownloadState(DownloadState.PAUSE);
                    DownloadTaskManager.getInstance(BaseApplication.getContext()).updateDownloadTask(task);
                    nm.cancel(task.getFileName().hashCode());
                    DownloadTaskManager.getInstance(BaseApplication.getContext()).registerListener(task, new DownloadNotificationListener(mApplicationContext, task));
                }
            }
        }
    }

    /** 访问服务器获取最新数据*/
    public void refreshData(){
        NetworkClient.startRequestQueryPageGroup(mApplicationContext, refreshRequestGroupCallBack);
    }

    /**
     * 初始化请求CallBack
     * */
    private StringCallback initRequestGroupCallBack = new StringCallback() {
        @Override
        public void onFailure(int code, String result, Throwable throwable) {

        }

        @Override
        public void onSuccess(int code, String result) {
            BaseBean<ResultPageGroupMapBean> resultBean = parseRequestCallBack(code,result);
            if (resultBean != null){
                mDataProviderListener.onInitNetDataResult(resultBean);
            }
        }
    };

    /**
     * 刷新请求CallBack
     * */
    private StringCallback refreshRequestGroupCallBack = new StringCallback() {
        @Override
        public void onFailure(int code, String result, Throwable throwable) {

        }

        @Override
        public void onSuccess(int code, String result) {
            BaseBean<ResultPageGroupMapBean> resultBean = parseRequestCallBack(code,result);
            mDataProviderListener.onRefreshNetDataResult(resultBean);
        }
    };

    private BaseBean<ResultPageGroupMapBean> parseRequestCallBack(int code,String result){
        BaseBean<ResultPageGroupMapBean> netWorkBaseBean = null;
        if (200 == code) {
            Log.i(TAG, "onPostGet: " + result);
            Gson gson = new Gson();
            Type jsonType = new TypeToken<BaseBean<ResultPageGroupMapBean>>() {
            }.getType();
            try {
                netWorkBaseBean = gson.fromJson(result, jsonType);
                if (netWorkBaseBean != null && netWorkBaseBean.getResultMap().getPageGroupMapList().size() != 0) {  //先将最新数据缓存起来
                    FileManagerUtils.writeFile(AppStoreConstant.APPCENTER_JSON_DIR + AppStoreConstant.APPCENTER_PAGEGROUP_JSON, result, false);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return netWorkBaseBean;
    }

    public void onDestroy(){
        refreshRequestGroupCallBack = null;
        initRequestGroupCallBack = null;
    }
}
