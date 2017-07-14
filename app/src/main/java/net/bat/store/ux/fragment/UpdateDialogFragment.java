package net.bat.store.ux.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.transsion.http.impl.StringCallback;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.update.AppUpdateBean;
import net.bat.store.bean.update.DataBean;
import net.bat.store.download.DownloadNotificationListener;
import net.bat.store.download.DownloadState;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.download.InstallState;
import net.bat.store.network.NetworkClient;
import net.bat.store.constans.NetworkConstant;
import net.bat.store.utils.CommonUtils;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.PreferencesUtils;
import net.bat.store.ux.acticivty.DownloadListActivity;

import java.util.List;


/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/1/19
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class UpdateDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private static final String TAG = "UpdateDialogFragment";

    private static final String UPDATE_MD5 = "update_md5";
    /**
     * 消息标题
     */
    public static final String DIALOG_TITLE = "dialog_title";
    /**
     * 消息内容
     */
    public static final String DIALOG_MSG = "dialog_message";
    /**
     * 按钮显示内容
     */
    private TextView tvTitle, tvMsg, tvBtnCancle, tvBtnConfirm;
    private TextView tvSingleBtn;
    private LinearLayout ll_button;
    private String title, msg;
    private int type;
    private AppUpdateBean updateBean;
    private OnClickConfirmDialogListener onClickConfirmListener;
    private OnClickCancleDialogListener onClickCancleListener;


    private StringCallback requestDataCallBack = new StringCallback() {
        @Override
        public void onFailure(int i, String s, Throwable throwable) {
            refreshView();
        }

        @Override
        public void onSuccess(int code, String ResponseBody) {
            {
                String json_result = ResponseBody;
                Log.i(TAG, "onPostGet: " + json_result);
                Gson gson = new Gson();
                try {
                    updateBean = gson.fromJson(json_result, AppUpdateBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            refreshView();
        }
    };

//    private HttpUtils.HttpListener requestDataCallBack=new HttpUtils.HttpListener() {
//        @Override
//        protected void onPostGet(HttpResponse httpResponse) {
//            super.onPostGet(httpResponse);
//            if(httpResponse!=null) {
//                int code = httpResponse.getResponseCode();
//                Log.i(TAG, "onPostGet: " + code);
//                if (code == 200) {
//                    String json_result = httpResponse.getResponseBody();
//                    Log.i(TAG, "onPostGet: "+json_result);
//                    Gson gson=new Gson();
//                    try{
//                        updateBean=gson.fromJson(json_result,AppUpdateBean.class);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//            refreshView();
//        }
//    };

    @Override
    protected int getLayoutResId() {
        return R.layout.appcenter_dialog;
    }

    @Override
    protected void onCancel() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        title = bundle.getString(DIALOG_TITLE);
        msg = bundle.getString(DIALOG_MSG);
        type = bundle.getInt(DIALOG_TYPE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }


    public void refreshView() {
        if (updateBean == null) {
            tvMsg.setText(R.string.is_new_version);
            tvSingleBtn.setText(R.string.ok);
        } else {
            if ((updateBean.getStatus() == 1)) {
                ll_button.setVisibility(View.VISIBLE);
                tvSingleBtn.setVisibility(View.GONE);
                tvMsg.setText(R.string.get_new_version);
                tvBtnConfirm.setOnClickListener(this);
            } else {
                tvMsg.setText(R.string.is_new_version);
                tvSingleBtn.setText(R.string.ok);
            }
        }
    }

    @Override
    protected void initView(View view) {
        tvTitle = (TextView) view.findViewById(R.id.tv_single_operation_dialog_title);
        tvMsg = (TextView) view.findViewById(R.id.tv_single_operation_dialog_message);
        ll_button = (LinearLayout) view.findViewById(R.id.ll_button);
        tvBtnConfirm = (TextView) view.findViewById(R.id.tv_single_operation_dialog_confirm);
        tvBtnCancle = (TextView) view.findViewById(R.id.tv_single_operation_dialog_cancle);
        tvSingleBtn = (TextView) view.findViewById(R.id.tv_single_operation_dialog);

    }


    @Override
    protected void initEvent() {
        //此处在子类中设置点击对话框外部，对话框不消失
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        tvBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickCancleListener != null) {
                    onClickCancleListener.onClick(v);
                }
            }
        });
        tvBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickConfirmListener != null) {
                    onClickConfirmListener.onClick(v);
                }
            }
        });
        tvSingleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickCancleListener != null) {
                    onClickCancleListener.onClick(v);
                }
            }
        });
    }

    @Override
    protected void setSubView() {
        if (type == 0) {
            tvSingleBtn.setVisibility(View.VISIBLE);
            ll_button.setVisibility(View.GONE);
            tvTitle.setText(title);
            tvMsg.setText(msg);
            NetworkClient.startRequestQueryAppVersion(BaseApplication.getContext(), NetworkConstant.URL_QUERY_UPDATE, requestDataCallBack);
        } else {
            tvTitle.setText(title);
            tvSingleBtn.setVisibility(View.GONE);
            tvMsg.setText(msg);
            ll_button.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClickConfirmDialogListener(OnClickConfirmDialogListener onClickDialogListener) {
        this.onClickConfirmListener = onClickDialogListener;
    }

    public void setOnClickCancleDialogListener(OnClickCancleDialogListener onClickDialogListener) {
        this.onClickCancleListener = onClickDialogListener;
    }

    public interface OnClickConfirmDialogListener {
        void onClick(View view);
    }

    public interface OnClickCancleDialogListener {
        void onClick(View view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateBean = null;
        requestDataCallBack = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_single_operation_dialog_confirm:
                dismiss();
                boolean isDownloaded = false;
                List<DownloadTask> tasks = DownloadTaskManager.getInstance(BaseApplication.getContext()).getFinishedDownloadTask();
                DataBean dataBean = updateBean.getDataBean();
                String appLabel = BaseApplication.getContext().getPackageManager().getApplicationLabel(BaseApplication.getContext().getApplicationInfo()).toString();
                DownloadTask task = new DownloadTask(dataBean.getDownloadPackageUrl() + "?md5=" + dataBean.getMd5(), dataBean.getPackageName(), ConstantUtils.getDownloadPath(),
                        appLabel + ".apk", appLabel, null, null, null, null, 0, null, null, null);

//                DownloadTask task = new DownloadTask(dataBean.getDownloadPackageUrl(),dataBean.getPackageName(), ConstantUtils.getDownloadPath(),
//                        appLabel,appLabel,null,null,null,null,0,null);
                if (CommonUtils.getDwonloadTask().indexOf(task) != -1) {
                    isDownloaded = true;
                }

                //是否为已下载过的更新包
                if (isDownloaded) {

                    //通过md5判断已下载的更新包是否为最新版本
                    if (PreferencesUtils.getString(BaseApplication.getContext(), UPDATE_MD5) != null &&
                            PreferencesUtils.getString(BaseApplication.getContext(), UPDATE_MD5).equals(dataBean.getMd5())) {
                        Toast.makeText(BaseApplication.getContext(), R.string.task_exist, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(BaseApplication.getContext(), DownloadListActivity.class);
                        startActivity(intent);
                    } else {
                        DownloadTaskManager.getInstance(BaseApplication.getContext()).deleteDownloadTask(task);
                        DownloadTaskManager.getInstance(BaseApplication.getContext()).deleteDownloadTaskFile(task);

                        task.setDownloadState(DownloadState.WAITING);
                        task.setInstallState(InstallState.INIT);

                        DownloadTaskManager.getInstance(BaseApplication.getContext()).registerListener(task,
                                new DownloadNotificationListener(BaseApplication.getContext(), task));
                        CommonUtils.getDwonloadTask().add(task);

                        if (!task.equals(DownloadTaskManager.getInstance(BaseApplication.getContext()).queryDownloadTask(task.getFileName()))) {
                            DownloadTaskManager.getInstance(BaseApplication.getContext()).insertDownloadTask(task);
                        }
                        DownloadTaskManager.getInstance(BaseApplication.getContext()).updateDownloadTask(task);
                        Toast.makeText(getActivity(), R.string.download_status_downloading, 0).show();
                        PreferencesUtils.putString(BaseApplication.getContext(), UPDATE_MD5, dataBean.getMd5());
                        DownloadTaskManager.getInstance(BaseApplication.getContext()).startDownload(task);

                        Intent intent = new Intent(BaseApplication.getContext(), DownloadListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        BaseApplication.getContext().startActivity(intent);
                    }
                } else {
                    DownloadTaskManager.getInstance(BaseApplication.getContext()).registerListener(task,
                            new DownloadNotificationListener(BaseApplication.getContext(), task));

                    task.setDownloadState(DownloadState.WAITING);
                    task.setInstallState(InstallState.INIT);

                    CommonUtils.getDwonloadTask().add(task);
                    if (!task.equals(DownloadTaskManager.getInstance(BaseApplication.getContext()).queryDownloadTask(task.getFileName()))) {
                        DownloadTaskManager.getInstance(BaseApplication.getContext()).insertDownloadTask(task);
                    }
                    DownloadTaskManager.getInstance(BaseApplication.getContext()).updateDownloadTask(task);
                    Toast.makeText(getActivity(), R.string.download_status_downloading, 0).show();
                    PreferencesUtils.putString(BaseApplication.getContext(), UPDATE_MD5, dataBean.getMd5());

                    DownloadTaskManager.getInstance(BaseApplication.getContext()).startDownload(task);

                    Intent intent = new Intent(BaseApplication.getContext(), DownloadListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BaseApplication.getContext().startActivity(intent);
                }
                break;
        }
    }
}

