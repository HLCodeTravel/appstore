package net.bat.store.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 所有公用监听器--动态注册方式
 * @author hogan
 *
 */
public class AppCenterDynamicBroadcastReceiver extends BroadcastReceiver {
    //==========================网络相关===========================
    //ConnectivityManager
    private ConnectivityManager mConnectivityManager;
    private boolean mIsNetworkAvailable;
    //网络信息类
    private NetworkInfo mNetworkInfo;

    //监听器集合
    private static AppCenterReceiverListener sListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            //网络状态变化
            if (mConnectivityManager == null) {
                mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            //网络是否可用标识
            boolean isNetworkOk = (mNetworkInfo != null && mNetworkInfo.isAvailable());
            if(isNetworkOk){
                 if(!mIsNetworkAvailable){
                     mIsNetworkAvailable=true;
                     if (sListener != null) {
                         sListener.onNetworkChanged(isNetworkOk);
                     }
                 }
            }else{
                if(mIsNetworkAvailable){
                    mIsNetworkAvailable=false;
                    if (sListener != null) {
                        sListener.onNetworkChanged(isNetworkOk);
                    }
                }
            }
        }
    }

    /**
     * 注册监听
     * @param listener
     */
    public static void registerListener(AppCenterReceiverListener listener) {
        sListener=listener;
    }
    /**
     * 注销监听
     * @param listener
     */
    public static void unRegisterListener(AppCenterReceiverListener listener) {
        if (listener != null && sListener != null) {
            sListener=null;
        }
    }
    /**
     * 事件监听
     * @author hogan
     *
     */
    public static interface AppCenterReceiverListener {
        /**
         * 网络状态变化监听
         * @param isAvailable 网络是否可用(true:可用; false:不可用)
         */
        public void onNetworkChanged(boolean isAvailable);
    }
}
