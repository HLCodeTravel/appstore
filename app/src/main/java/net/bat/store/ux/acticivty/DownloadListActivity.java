
package net.bat.store.ux.acticivty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.download.DownloadListener;
import net.bat.store.download.DownloadNotificationListener;
import net.bat.store.download.DownloadOpenFile;
import net.bat.store.download.DownloadState;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.utils.CommonUtils;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.ux.adapter.DownloadAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DownloadListActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "DownloadListActivity";

    private List<DownloadTask> mDownloadingTasks;
    private List<DownloadTask> mFinishedTasks;

    public LinearLayout ll_no_task;
    private RecyclerView rv_download;
    private DownloadAdapter mAdapter;
    private PopupWindow popupWindow;
    private int location;

    private View popupWindowView;
    private TextView btn_install;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"ONCREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initData();
        initView();

        for (final DownloadTask task : mDownloadingTasks) {
            if (!task.getDownloadState().equals(DownloadState.FINISHED)) {
                Log.e(TAG, "add listener");
                addListener(task);
            }
        }

        //DownloadOperator.check(mContext);
    }

    private void initView() {
        ll_no_task = (LinearLayout) findViewById(R.id.ll_no_task);
        rv_download = (RecyclerView) findViewById(R.id.elv_list_view);
        TextView tv_title = (TextView) findViewById(R.id.category_top_bar_title);
        tv_title.setText(R.string.download_manager);
        findViewById(R.id.category_top_bar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Bundle param = new Bundle();
                param.putString("Action", "iv_back");
                FirebaseStat.logEvent("DownloadBackClick", param);
            }
        });
        ImageView back_icon= (ImageView) findViewById(R.id.iv_back);
        if(ConstantUtils.isIconShouldRevert()){
            back_icon.setImageResource(R.drawable.icon_back_revert);
        }

        if (mDownloadingTasks.isEmpty() && mFinishedTasks.isEmpty()) {
            rv_download.setVisibility(View.INVISIBLE);
            ll_no_task.setVisibility(View.VISIBLE);
        } else {
            ll_no_task.setVisibility(View.GONE);
        }

        mAdapter = new DownloadAdapter(this, mDownloadingTasks, mFinishedTasks);
        rv_download.setLayoutManager(new LinearLayoutManager(this));
        rv_download.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DownloadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                initPopupWindow();
                mAdapter.setClickFlag(false);
                if (position > mDownloadingTasks.size()){
                    btn_install.setVisibility(View.VISIBLE);
                }else {
                    btn_install.setVisibility(View.GONE);
                }
                setLocation(position);
                Bundle param = new Bundle();
                param.putString("Action", "mAdapter");
                FirebaseStat.logEvent("DownloadItemClick", param);
            }
        });

    }

    private void initData() {
        mDownloadingTasks = DownloadTaskManager.getInstance(BaseApplication.getContext()).getDownloadingTask();
        mFinishedTasks = DownloadTaskManager.getInstance(BaseApplication.getContext()).getFinishedDownloadTask();


        List<DownloadTask> removeTask = new ArrayList<>();
        for (DownloadTask task:mFinishedTasks){
            File file = new File(task.getFilePath()+File.separator + task.getFileName());
            if (!file.exists()){
                Log.e(TAG,"file do not exists");
                removeTask.add(task);
                DownloadTaskManager.getInstance(BaseApplication.getContext()).deleteDownloadTask(task);
            }
        }

        if (!removeTask.isEmpty()){
            mFinishedTasks.removeAll(removeTask);
            if (CommonUtils.getDwonloadTask()!=null && !CommonUtils.getDwonloadTask().isEmpty()) {
                CommonUtils.getDwonloadTask().removeAll(removeTask);
            }
        }
//        DownloadTask downloadingtempTask = null;
//        DownloadTask finishtempTask = null;
//        for (DownloadTask task:mDownloadingTasks){
//            if (task.getPackageName().equals(BaseApplication.getContext().getApplicationInfo().packageName)){
//                downloadingtempTask = task;
//            }
//        }
//        if (downloadingtempTask != null){
//            mDownloadingTasks.remove(downloadingtempTask);
//        }
//
//        for (DownloadTask task:mDownloadingTasks){
//            if (task.getPackageName().equals(BaseApplication.getContext().getApplicationInfo().packageName)){
//                finishtempTask = task;
//            }
//        }
//        if (finishtempTask != null){
//            mDownloadingTasks.remove(finishtempTask);
//        }
    }

    protected void initPopupWindow() {
        popupWindowView = LayoutInflater.from(this).inflate(R.layout.bottm_popmenu, null);
        //内容，高度，宽度
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        //动画效果

        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);

        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        //宽度
        //popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
        //高度
        //popupWindow.setHeight(LayoutParams.FILL_PARENT);
        //显示位置

        popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_download, null), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.5f);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
                if (mAdapter != null){
                    mAdapter.setClickFlag(true);
                }
            }
        });

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if( popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    popupWindow=null;
                }*/
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });

        TextView btn_cancel = (TextView) popupWindowView.findViewById(R.id.tv_delete_task);
        TextView btn_delete = (TextView) popupWindowView.findViewById(R.id.tv_cancel);

        btn_install = (TextView)popupWindowView.findViewById(R.id.tv_install);

        btn_cancel.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_install.setOnClickListener(this);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                Bundle param = new Bundle();
                param.putString("Action", "tv_cancel");
                FirebaseStat.logEvent("DownloadCancel", param);
                break;
            case R.id.tv_delete_task:

                if (location <= mDownloadingTasks.size()) {
                    DownloadTask task = mDownloadingTasks.get(location - 1);
                    Log.e(TAG, task.getFileName());
                    mDownloadingTasks.remove(task);
                    CommonUtils.getDwonloadTask().remove(task);
                    DownloadTaskManager.getInstance(this).deleteDownloadTaskFile(task);
                    DownloadTaskManager.getInstance(this).deleteDownloadTask(task);
                    mAdapter.notifyDataSetChanged();
                    popupWindow.dismiss();
                } else {
                    DownloadTask task = mFinishedTasks.get(location - mDownloadingTasks.size() - 2);
                    mFinishedTasks.remove(task);
                    CommonUtils.getDwonloadTask().remove(task);
                    DownloadTaskManager.getInstance(this).deleteDownloadTaskFile(task);
                    DownloadTaskManager.getInstance(this).deleteDownloadTask(task);
                    mAdapter.notifyDataSetChanged();
                    popupWindow.dismiss();
                }

                if (mDownloadingTasks.isEmpty() && mFinishedTasks.isEmpty()) {
                    rv_download.setVisibility(View.INVISIBLE);
                    ll_no_task.setVisibility(View.VISIBLE);
                }
                Bundle param1 = new Bundle();
                param1.putString("Action", "tv_delete_task");
                FirebaseStat.logEvent("DownloadTaskDelete", param1);
                break;

            case R.id.tv_install:
                DownloadTask task = mFinishedTasks.get(location - mDownloadingTasks.size() - 2);
                Intent installIntent = DownloadOpenFile.getApkFileIntent(task.getFilePath() + "/" +task.getFileName());
                startActivity(installIntent);
                popupWindow.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<DownloadTask> removeTask = new ArrayList<>();
        for (DownloadTask task:mFinishedTasks){
            File file = new File(task.getFilePath()+File.separator + task.getFileName());
            if (!file.exists()){
                Log.e(TAG,"file do not exists");
                removeTask.add(task);
                DownloadTaskManager.getInstance(BaseApplication.getContext()).deleteDownloadTask(task);
            }
        }

        if (!removeTask.isEmpty()){
            mFinishedTasks.removeAll(removeTask);
            if (CommonUtils.getDwonloadTask()!=null && !CommonUtils.getDwonloadTask().isEmpty()) {
                CommonUtils.getDwonloadTask().removeAll(removeTask);
            }
        }

        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }

        if (mDownloadingTasks.isEmpty() && mFinishedTasks.isEmpty()) {
            rv_download.setVisibility(View.INVISIBLE);
            ll_no_task.setVisibility(View.VISIBLE);
        } else {
            ll_no_task.setVisibility(View.GONE);
        }
        FirebaseStat.setCurrentScreen(this, "DownloadListActivity", null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (popupWindow != null){
            popupWindow.dismiss();
        }
    }

    private void setLocation(int position) {
        location = position;
    }

    class MyDownloadListener implements DownloadListener {
        private DownloadTask task;

        public MyDownloadListener(DownloadTask downloadTask) {
            task = downloadTask;
        }

        @Override
        public void onDownloadInit() {
            task.setDownloadState(DownloadState.WAITING);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onDownloadFinish(String filepath) {
            Log.d(TAG, "onDownloadFinish");
            task.setDownloadState(DownloadState.FINISHED);
            task.setFinishedSize(task.getTotalSize());
            task.setPercent(100);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mDownloadingTasks != null) {
                        mDownloadingTasks.remove(task);
                        mFinishedTasks.add(0, task);
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

        @Override
        public void onDownloadStart() {
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onDownloadPause() {
            Log.d(TAG, "onDownloadPause");
            task.setDownloadState(DownloadState.PAUSE);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

        }

        @Override
        public void onDownloadStop() {
            Log.d(TAG, "onDownloadStop");
            //task.setDownloadState(DownloadState.PAUSE);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

        }

        @Override
        public void onDownloadFail() {
            Log.d(TAG, "onDownloadFail");
            task.setDownloadState(DownloadState.FAILED);
            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onDownloadProgress(final long finishedSize, final long totalSize,
                                       int speed) {
            // Log.d(TAG, "download " + finishedSize);
            task.setDownloadState(DownloadState.DOWNLOADING);
            task.setFinishedSize(finishedSize);
            task.setTotalSize(totalSize);
            task.setPercent((int) finishedSize * 100 / (int) totalSize);
            task.setSpeed(speed);

            DownloadListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onInstallComplete() {
        }

        @Override
        public void onInstallFail() {

        }

    }

    public void addListener(DownloadTask task) {
        if (DownloadTaskManager.getInstance(BaseApplication.getContext()).getListeners(task).isEmpty()){
            DownloadTaskManager.getInstance(BaseApplication.getContext()).registerListener(task, new DownloadNotificationListener(BaseApplication.getContext(),task));
        }
        DownloadTaskManager.getInstance(BaseApplication.getContext()).registerListener(task, new MyDownloadListener(task));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"destroy");
        for (DownloadTask tasks : mDownloadingTasks) {
            Iterator<DownloadListener> iterator = DownloadTaskManager.getInstance(BaseApplication.getContext()).getListeners(tasks).iterator();
            while (iterator.hasNext()) {
                DownloadListener listener = iterator.next();
                if (listener instanceof MyDownloadListener) {
                    Log.e(TAG, "remove listener");
                    DownloadTaskManager.getInstance(BaseApplication.getContext()).getListeners(tasks).remove(listener);
                }
            }
        }
        if (mFinishedTasks != null) {
            mFinishedTasks.clear();
            mFinishedTasks = null;
        }
        if (mDownloadingTasks != null) {
            mDownloadingTasks.clear();
            mDownloadingTasks = null;
        }
        mAdapter = null;
    }
}
