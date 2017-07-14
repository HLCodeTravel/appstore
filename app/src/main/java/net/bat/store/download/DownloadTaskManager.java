
package net.bat.store.download;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.transsion.http.RequestCall;
import com.transsion.http.impl.DownloadCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A single instance Download Manager, we use this class manage all download task.
 *
 * @author offbye@gmail.com
 */
public class DownloadTaskManager {

    private static final String TAG = "DownloadTaskManager";

    /**
     * default  save path: /sdcard/download
     */
    private static final String DEFAULT_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/download/";

    /**
     * single instance
     */
    private static DownloadTaskManager sMe;

    private static int mMaxTask = 0;

    /**
     * Download Database Helper
     */
    private DownloadDBHelper mDownloadDBHelper;

    /**
     * one download task own a download worker
     */
    private HashMap<DownloadTask, DownloadOperator> mDownloadMap;

    private HashMap<DownloadTask, CopyOnWriteArraySet<DownloadCallback>> mDownloadCallbackMap;


    /**
     * private constructor
     *
     * @param context
     */
    private DownloadTaskManager(Context context) {
        mDownloadMap = new HashMap<DownloadTask, DownloadOperator>();
        mDownloadCallbackMap = new HashMap<DownloadTask, CopyOnWriteArraySet<DownloadCallback>>();
        // 数据库操作对象实例化
        mDownloadDBHelper = new DownloadDBHelper(context, "download.db");
    }

    /**
     * Get a single instance of DownloadTaskManager
     *
     * @param context Context
     * @return DownloadTaskManager instance
     */
    public static synchronized DownloadTaskManager getInstance(Context context) {
        if (sMe == null) {
            sMe = new DownloadTaskManager(context);
//            if (DownloadOperator.check(context)< 2){
//                mMaxTask  = 5;
//            }
        }
        return sMe;
    }

    /**
     * Start new download Task, if a same download Task already existed,it will exit and leave a "task existed" log.
     *
     */
    public void startDownload(DownloadTask downloadTask) {
        if (downloadTask.getFilePath() == null || downloadTask.getFilePath().trim().length() == 0) {
            Log.w(TAG, "file path is invalid. file path : " + downloadTask.getFilePath()
                    + ", use default file path : " + DEFAULT_FILE_PATH);
            downloadTask.setFilePath(DEFAULT_FILE_PATH);
        }

        if (downloadTask.getFileName() == null || downloadTask.getFileName().trim().length() == 0) {
            Log.w(TAG, "file name is invalid. file name : " + downloadTask.getFileName());
            throw new IllegalArgumentException("file name is invalid");
        }

//        if (null == downloadTask.getUrl() || !URLUtil.isHttpUrl(downloadTask.getUrl())) {
//            Log.w(TAG, "invalid http url: " + downloadTask.getUrl());
//            throw new IllegalArgumentException("invalid http url");
//        }

        if (mDownloadMap.containsKey(downloadTask)) {
            Log.w(TAG, "task existed");
            return;
        }

        if (mMaxTask > 0 && mDownloadMap.size() > mMaxTask) {
            Log.w(TAG, "trial version can only add " + mMaxTask + " download task, please buy  a lincense");
            return;
        }

        if (null == mDownloadCallbackMap.get(downloadTask)) {
            CopyOnWriteArraySet<DownloadCallback> set = new CopyOnWriteArraySet<DownloadCallback>();
            mDownloadCallbackMap.put(downloadTask, set);
        }

        for (DownloadCallback l : getCallbacks(downloadTask)) {
            l.onStart();
        }

        DownloadOperator dlOperator = new DownloadOperator(this, downloadTask);
        mDownloadMap.put(downloadTask, dlOperator);
        dlOperator.startDownload();
    }

    /**
     * Pause a downloading task
     */
    public void pauseDownload(DownloadTask downloadTask) {
        if (mDownloadMap.containsKey(downloadTask)) {
            mDownloadMap.get(downloadTask).pauseDownload();
            //mDownloadMap.remove(downloadTask);
        }

    }

    public void pauseAllTask() {
        if (!mDownloadMap.isEmpty()) {
            for (DownloadOperator operator : mDownloadMap.values()) {
                operator.pauseDownload();
            }
        }
    }

    /**
     * Continue or restart a downloadTask.
     *
     */
    public void continueDownload(DownloadTask downloadTask) {
        if (downloadTask.getFilePath() == null || downloadTask.getFilePath().trim().length() == 0) {
            Log.w(TAG, "file path is invalid. file path : " + downloadTask.getFilePath()
                    + ", use default file path : " + DEFAULT_FILE_PATH);
            downloadTask.setFilePath(DEFAULT_FILE_PATH);
        }

        if (downloadTask.getFileName() == null || downloadTask.getFileName().trim().length() == 0) {
            Log.w(TAG, "file name is invalid. file name : " + downloadTask.getFileName());
            throw new IllegalArgumentException("file name is invalid");
        }

//        if (null == downloadTask.getUrl() || !URLUtil.isHttpUrl(downloadTask.getUrl())) {
//            Log.w(TAG, "invalid http url: " + downloadTask.getUrl());
//            throw new IllegalArgumentException("invalid http url");
//        }

        if (null == mDownloadCallbackMap.get(downloadTask)) {
            CopyOnWriteArraySet<DownloadCallback> set = new CopyOnWriteArraySet<DownloadCallback>();
            mDownloadCallbackMap.put(downloadTask, set);
        }

        downloadTask.setDownloadState(DownloadState.WAITING);
        updateDownloadTask(downloadTask);
        for (DownloadCallback l : getCallbacks(downloadTask)) {
            l.onStart();
        }

        // save to database if the download task is valid, and start download.
        //已downloadtask的name为标志位
        if (!downloadTask.equals(queryDownloadTask(downloadTask.getFileName()))) {
            insertDownloadTask(downloadTask);
        }

        DownloadOperator dlOperator = new DownloadOperator(this, downloadTask);
        mDownloadMap.put(downloadTask, dlOperator);

        dlOperator.continueDownload();
    }

    /**
     * Stop a task,this method  not used now。Please use pauseDownload instead.
     *
     */
    @Deprecated
    public void stopDownload(DownloadTask downloadTask) {
        mDownloadMap.get(downloadTask).stopDownload();
        mDownloadMap.remove(downloadTask);
    }

    /**
     * get all Download task from database
     *
     * @return DownloadTask list
     */
    public List<DownloadTask> getAllDownloadTask() {
        return mDownloadDBHelper.queryAll();
    }

    /**
     * get all Downloading task from database
     *
     * @return DownloadTask list
     */
    public List<DownloadTask> getDownloadingTask() {
        return mDownloadDBHelper.queryUnDownloaded();
    }

    public List<DownloadTask> getDownloadTask(){
        return mDownloadDBHelper.queryDownloadTask();
    }

    /**
     * get installed download task from database
     *
     * @return DownloadTask list
     */
    public List<DownloadTask> getInstalledTask() {
        return mDownloadDBHelper.queryInstalled();
    }

    /**
     * get all download finished task from database
     *
     * @return DownloadTask list
     */
    public List<DownloadTask> getFinishedDownloadTask() {
        return mDownloadDBHelper.queryDownloaded();
    }

    /**
     * insert a download task to database
     */
    public void insertDownloadTask(DownloadTask downloadTask) {
        mDownloadDBHelper.insert(downloadTask);
    }

    /**
     * update a download task to database
     */
    public void updateDownloadTask(DownloadTask downloadTask) {
        mDownloadDBHelper.update(downloadTask);
    }

    /**
     * delete a download task from download queue, remove it's listeners, and delete it from database.
     *
     */
    public void deleteDownloadTask(DownloadTask downloadTask) {
        mDownloadDBHelper.delete(downloadTask);

        //删除下载任务回调
        for (DownloadCallback l : getCallbacks(downloadTask)) {
            l.onCancel();
        }

        getCallbacks(downloadTask).clear();
        mDownloadMap.remove(downloadTask);
        mDownloadCallbackMap.remove(downloadTask);
    }

    /**
     * delete a download task's download file.
     *
     */
    public void deleteDownloadTaskFile(DownloadTask downloadTask) {
        deleteFile(downloadTask.getFilePath() + "/" + downloadTask.getFileName());
    }

    public DownloadTask queryDownloadTask(String fileName) {
        return mDownloadDBHelper.query(fileName);
    }

    /**
     * query a download task is already running.
     *
     * @param requestCall
     * @return
     */
    public boolean existRunningTask(RequestCall requestCall) {
        DownloadTask downloadTask = (DownloadTask) requestCall.getRequest().getTag();
        return mDownloadMap.containsKey(downloadTask);
    }

    /**
     * Get all Listeners of a download task
     * @return
     */
    public CopyOnWriteArraySet<DownloadCallback> getCallbacks(DownloadTask downloadTask) {
        if (null != mDownloadCallbackMap.get(downloadTask)) {
            return mDownloadCallbackMap.get(downloadTask);
        } else {
            return new CopyOnWriteArraySet<DownloadCallback>();//avoid null pointer exception
        }
    }

    /**
     * Register a DownloadCallback to a downloadTask.
     * You can register many DownloadCallback to a downloadTask in any time.
     * Such as register a listener to update you own progress bar, do something after file download finished.
     *
     */
    public void registerListener(DownloadTask downloadTask, DownloadCallback callback) {
        if (null != mDownloadCallbackMap.get(downloadTask)) {
            mDownloadCallbackMap.get(downloadTask).add(callback);
            Log.d(TAG, downloadTask.getFileName() + " addListener ");
        } else {
            CopyOnWriteArraySet<DownloadCallback> set = new CopyOnWriteArraySet<DownloadCallback>();
            mDownloadCallbackMap.put(downloadTask, set);
            mDownloadCallbackMap.get(downloadTask).add(callback);
        }
    }

    /**
     * Remove Listeners from  a downloadTask, you do not need manually call this method.
     */
    public void removeListener(RequestCall requestCall) {
        DownloadTask downloadTask = (DownloadTask) requestCall.getRequest().getTag();
        mDownloadCallbackMap.remove(downloadTask);
    }

    /**
     * delete a file
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
