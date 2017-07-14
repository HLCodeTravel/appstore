
package net.bat.store.download;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.transsion.http.DownloadEngine;
import com.transsion.http.HttpClient;
import com.transsion.http.RequestCall;
import com.transsion.http.impl.DownloadCallback;
import com.transsion.http.impl.StringCallback;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.download.DownloadUrlBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.network.NetworkClient;
import net.bat.store.constans.NetworkConstant;
import net.bat.store.utils.CommonUtils;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Iterator;


/**
 * Download worker
 *
 * @author offbye@gmail.com
 */
public class DownloadOperator{

    /**
     * debug tag
     */
    private static final String TAG = "DownloadOperator";


    private RequestCall mRequestCall;

    private DownloadTask mDownloadTask;

    /**
     * DownloadTaskManager
     */
    private DownloadTaskManager mDlTaskMng;




    /**
     * Constructor
     *
     * @param dlTaskMng
     */
    DownloadOperator(DownloadTaskManager dlTaskMng, DownloadTask downloadTask) {
        mDlTaskMng = dlTaskMng;
        mDownloadTask = downloadTask;
        mRequestCall = HttpClient.download(BaseApplication.getContext())
                .pathname(mDownloadTask.getFilePath())
                .url(mDownloadTask.getUrl())
                .log(true)
                .tag(mDownloadTask)
                .build();


        Log.d(TAG, "file path : " + mDownloadTask.getFilePath());
        Log.d(TAG, "file name : " + mDownloadTask.getFileName());
        Log.d(TAG, "download url : " + mDownloadTask.getUrl());
    }


    /**
     * pauseDownload
     */
    void pauseDownload() {
        DownloadEngine.getEngine().pauseLoad(mDownloadTask);
    }

    /**
     * stopDownload
     */
    @Deprecated
    void stopDownload() {
        Log.i(TAG, "stop download.");
        DownloadEngine.getEngine().cancelLoad(mDownloadTask);
    }

    /**
     * continueDownload
     */
    void continueDownload() {
        DownloadEngine.getEngine().continueLoad(mDownloadTask);
    }

    /**
     * startDownload
     */
    void startDownload() {
        DownloadEngine.getEngine().execute(mRequestCall, new DownloadCallback() {
            @Override
            public void onFailure(String s, String s1) {
                for (DownloadCallback callback:mDlTaskMng.getCallbacks(mRequestCall)){
                    callback.onFailure(s,s1);
                }

            }

            @Override
            public void onSuccess(String s, File file) {
                for (DownloadCallback callback:mDlTaskMng.getCallbacks(mRequestCall)){
                    callback.onSuccess(s,file);
                }
            }

            @Override
            public void onLoading(String s, long l, long l1) {
                for (DownloadCallback callback:mDlTaskMng.getCallbacks(mRequestCall)){
                    callback.onLoading(s,l,l1);
                }
            }

            @Override
            public void onPause() {
                for (DownloadCallback callback:mDlTaskMng.getCallbacks(mRequestCall)){
                    callback.onPause();
                }
            }

            @Override
            public void onCancel() {
                for (DownloadCallback callback:mDlTaskMng.getCallbacks(mRequestCall)){
                    callback.onCancel();
                }
            }

            @Override
            public void onStart() {
                for (DownloadCallback callback:mDlTaskMng.getCallbacks(mRequestCall)){
                    callback.onStart();
                }
            }
        });
    }

}
