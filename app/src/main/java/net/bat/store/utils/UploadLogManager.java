package net.bat.store.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.transsion.core.deviceinfo.DeviceInfo;
import com.transsion.http.HttpClient;
import com.transsion.http.impl.StringCallback;

import net.bat.store.BuildConfig;
import net.bat.store.bean.log.Option;
import net.bat.store.bean.log.UploadOption;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.AhaLogCallBack;
import net.bat.store.network.NetworkClient;

import java.io.File;

/**
 * Created by bingbing.li on 2017/5/4.
 */

public class UploadLogManager {

    private static final String TAG = "UploadLogManager";
    private static UploadLogManager sInstance;
    private UploadOption mUpdateOption;
    private long mStartRunTime;
    private Context mContext;
    private final boolean sUploadLogOn = BuildConfig.APP_MODE == 1;

    private UploadLogManager() {
    }

    public static UploadLogManager getInstance() {
        if (sInstance == null) {
            synchronized (UploadLogManager.class) {
                if (sInstance == null) {
                    sInstance = new UploadLogManager();
                }
            }
        }
        return sInstance;
    }

    public void getUploadConfig(Context context, final AhaLogCallBack ahaLogCallBack) {
        if (!sUploadLogOn) {
            return;
        }
        mContext = context;
        final long current = System.currentTimeMillis();
        long lasttime = PreferencesUtils.getLong(context, AppStoreConstant.PRE_KEY_LAST_GET_UPLOAD_CONFIGURE_TIME, 0);
        if (Math.abs(current - lasttime) > (AppStoreConstant.ONE_HOUR_TIME * AppStoreConstant.MILLISECOND)) {
            if (ConstantUtils.checkNetworkState(context)) {

                NetworkClient.getUploadOption(new StringCallback() {
                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int code, String result) {
                        try {
                            if (!TextUtils.isEmpty(result)) {
                                PreferencesUtils.putLong(mContext, AppStoreConstant.PRE_KEY_LAST_GET_UPLOAD_CONFIGURE_TIME, System.currentTimeMillis());
                                Gson gson = new Gson();
                                mUpdateOption = gson.fromJson(result, UploadOption.class);
                                Log.i(TAG, "onSuccess: " + result);
                                FileManagerUtils.writeLog(AppStoreConstant.APPCENTER_LOG_CONFIGURE, result, false);
                                ahaLogCallBack.callback();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


//                HttpUtils.httpGet(NetworkConstant.URL_LOG_GET_CONFIG, new HttpUtils.HttpListener() {
//                    @Override
//                    protected void onPostGet(HttpResponse httpResponse) {
//                        super.onPostGet(httpResponse);
//                        Log.i(TAG, "onPostGet: ");
//                        if (httpResponse != null) {
//                            String result = httpResponse.getResponseBody();
//                            try {
//                                if (!TextUtils.isEmpty(result)) {
//                                    PreferencesUtils.putLong(mContext, AppStoreConstant.PRE_KEY_LAST_GET_UPLOAD_CONFIGURE_TIME, System.currentTimeMillis());
//                                    Gson gson = new Gson();
//                                    mUpdateOption = gson.fromJson(result, UploadOption.class);
//                                    Log.i(TAG, "onSuccess: " + result);
//                                    FileManagerUtils.writeLog(AppStoreConstant.APPCENTER_LOG_CONFIGURE, result, false);
//                                    ahaLogCallBack.callback();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
            }
        } else {
            File file = new File(AppStoreConstant.APPCENTER_LOG_CONFIGURE);
            if (file.exists()) {
                String page_group_json = FileManagerUtils.getConfigure(AppStoreConstant.APPCENTER_LOG_CONFIGURE);
                Gson gson = new Gson();
                mUpdateOption = gson.fromJson(page_group_json, UploadOption.class);
                ahaLogCallBack.callback();
            }
        }
    }

    public void uploadFirstStartLog(Context context) {
        if (!sUploadLogOn) {
            return;
        }
        mContext = context;
        boolean flag = PreferencesUtils.getBoolean(mContext, AppStoreConstant.PRE_KEY_HAVE_UPLOAD_FIRST, false);
        if (ConstantUtils.checkNetworkState(context) && !flag) {
            String firststartLog = generateFirstStartLog(context, false, false);
            NetworkClient.UploadLog(AppStoreConstant.FIRST_START_LOG, firststartLog, new StringCallback() {
                @Override
                public void onFailure(int i, String s, Throwable throwable) {

                }

                @Override
                public void onSuccess(int code, String s) {
                    if (200 == code) {
                        PreferencesUtils.putBoolean(mContext, AppStoreConstant.PRE_KEY_HAVE_UPLOAD_FIRST, true);
                    }
                }
            });

//            HttpUtils.getInstance().httpPostString(NetworkConstant.URL_LOG_UPLOAD, AppStoreConstant.FIRST_START_LOG, firststartLog, new HttpUtils.HttpListener() {
//                @Override
//                protected void onPostGet(HttpResponse httpResponse) {
//                    super.onPostGet(httpResponse);
//                    Log.i(TAG, "uploadFirstStartLog---onPostGet: ");
//                    if (httpResponse != null) {
//                        int code = httpResponse.getResponseCode();
//                        Log.i(TAG, "onPostGet: " + code);
//                        if (code == 200) {
//                            PreferencesUtils.putBoolean(mContext, AppStoreConstant.PRE_KEY_HAVE_UPLOAD_FIRST, true);
//                        }
//                    }
//                }
//            });
        }
    }

    public void uploadAhaLog(Context context) {
        if (!sUploadLogOn) {
            return;
        }
        if (ConstantUtils.checkNetworkState(context)) {
            final long current = System.currentTimeMillis();
            long lasttime = PreferencesUtils.getLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, 0);
            Option option = mUpdateOption.getOption();
            if (option.getSwitch_aha() != 1) {
                return;
            }
            if (Math.abs(current - lasttime) > option.getDuration()) {
                if (option.getStartrunlog() == 1) {
                    uploadStartOrExitLog(context);
                }
                if (option.getOperationlog() == 1) {
                    uploadUserOperatorLog(context);
                }
                if (option.getSearchlog() == 1) {
                    uploadSearchLog(context);
                }
                if (option.getResourcelog() == 1) {
                    uploadResourceLog(context);
                }
                if (option.getUpdatelog() == 1) {
                    uploadUpdateLog(context);
                }
            }
        }
    }

    private void uploadStartOrExitLog(final Context context) {
        if (!sUploadLogOn) {
            return;
        }
        String startOrExitLog = FileManagerUtils.getConfigure(AppStoreConstant.APPCENTER_LOG_START_EXIT_LOG);
        if (TextUtils.isEmpty(startOrExitLog)) {
            return;
        }

        // HttpClient.post().params(parms).url(url).build().execute(httpListener);
        NetworkClient.UploadLog(AppStoreConstant.START_RUNLOG, startOrExitLog, new StringCallback() {
            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int code, String s) {
                if (code == 200) {
                    FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_START_EXIT_LOG);
                    PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
                }
            }
        });
//        HttpUtils.getInstance().httpPostString(NetworkConstant.URL_LOG_UPLOAD, AppStoreConstant.START_RUNLOG, startOrExitLog, new HttpUtils.HttpListener() {
//            @Override
//            protected void onPostGet(HttpResponse httpResponse) {
//                super.onPostGet(httpResponse);
//                Log.i(TAG, "uploadStartOrExitLog---onPostGet: ");
//                if (httpResponse != null) {
//                    int code = httpResponse.getResponseCode();
//                    Log.i(TAG, "onPostGet: " + code);
//                    if (code == 200) {
//                        FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_START_EXIT_LOG);
//                        PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
//                    }
//                }
//            }
//        });
    }

    private void uploadUserOperatorLog(final Context context) {
        if (!sUploadLogOn) {
            return;
        }
        String startOrExitLog = FileManagerUtils.getConfigure(AppStoreConstant.APPCENTER_LOG_USER_OPERATOR_LOG);
        if (TextUtils.isEmpty(startOrExitLog)) {
            return;
        }

        NetworkClient.UploadLog(AppStoreConstant.OPERATION_LOG, startOrExitLog, new StringCallback() {
            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int code, String s) {
                if (code == 200) {
                    FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_USER_OPERATOR_LOG);
                    PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
                }
            }
        });

//        HttpUtils.getInstance().httpPostString(NetworkConstant.URL_LOG_UPLOAD, AppStoreConstant.OPERATION_LOG, startOrExitLog, new HttpUtils.HttpListener() {
//            @Override
//            protected void onPostGet(HttpResponse httpResponse) {
//                super.onPostGet(httpResponse);
//                Log.i(TAG, "uploadUserOperatorLog---onPostGet: ");
//                if (httpResponse != null) {
//                    int code = httpResponse.getResponseCode();
//                    Log.i(TAG, "onPostGet: " + code);
//                    if (code == 200) {
//                        FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_USER_OPERATOR_LOG);
//                        PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
//                    }
//                }
//            }
//        });
    }

    private void uploadResourceLog(final Context context) {
        if (!sUploadLogOn) {
            return;
        }
        String startOrExitLog = FileManagerUtils.getConfigure(AppStoreConstant.APPCENTER_LOG_RESOURCE_LOG);
        if (TextUtils.isEmpty(startOrExitLog)) {
            return;
        }

        NetworkClient.UploadLog(AppStoreConstant.RESOURCE_LOG, startOrExitLog, new StringCallback() {
            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int code, String s) {
                if (code == 200) {
                    FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_RESOURCE_LOG);
                    PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
                }
            }
        });
//        HttpUtils.getInstance().httpPostString(NetworkConstant.URL_LOG_UPLOAD, AppStoreConstant.RESOURCE_LOG, startOrExitLog, new HttpUtils.HttpListener() {
//            @Override
//            protected void onPostGet(HttpResponse httpResponse) {
//                super.onPostGet(httpResponse);
//                Log.i(TAG, "uploadResourceLog---onPostGet: ");
//                if (httpResponse != null) {
//                    int code = httpResponse.getResponseCode();
//                    Log.i(TAG, "onPostGet: " + code);
//                    if (code == 200) {
//                        FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_RESOURCE_LOG);
//                        PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
//                    }
//                }
//            }
//        });
    }

    private void uploadSearchLog(final Context context) {
        if (!sUploadLogOn) {
            return;
        }
        String startOrExitLog = FileManagerUtils.getConfigure(AppStoreConstant.APPCENTER_LOG_SEARCH_LOG);
        if (TextUtils.isEmpty(startOrExitLog)) {
            return;
        }

        NetworkClient.UploadLog(AppStoreConstant.SEARCH_LOG, startOrExitLog, new StringCallback() {
            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int code, String s) {
                if (code == 200) {
                    FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_SEARCH_LOG);
                    PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
                }
            }
        });

//        HttpUtils.getInstance().httpPostString(NetworkConstant.URL_LOG_UPLOAD, AppStoreConstant.SEARCH_LOG, startOrExitLog, new HttpUtils.HttpListener() {
//            @Override
//            protected void onPostGet(HttpResponse httpResponse) {
//                super.onPostGet(httpResponse);
//                Log.i(TAG, "uploadSearchLog---onPostGet: ");
//                if (httpResponse != null) {
//                    int code = httpResponse.getResponseCode();
//                    Log.i(TAG, "onPostGet: " + code);
//                    if (code == 200) {
//                        FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_SEARCH_LOG);
//                        PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
//                    }
//                }
//            }
//        });
    }

    private void uploadUpdateLog(final Context context) {
        if (!sUploadLogOn) {
            return;
        }
        String startOrExitLog = FileManagerUtils.getConfigure(AppStoreConstant.APPCENTER_LOG_UPDATE_LOG);
        if (TextUtils.isEmpty(startOrExitLog)) {
            return;
        }

        NetworkClient.UploadLog(AppStoreConstant.UPDATE_LOG, startOrExitLog, new StringCallback() {
            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int code, String s) {
                if (code == 200) {
                    FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_UPDATE_LOG);
                    PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
                }
            }
        });
//        HttpUtils.getInstance().httpPostString(NetworkConstant.URL_LOG_UPLOAD, AppStoreConstant.UPDATE_LOG, startOrExitLog, new HttpUtils.HttpListener() {
//            @Override
//            protected void onPostGet(HttpResponse httpResponse) {
//                super.onPostGet(httpResponse);
//                Log.i(TAG, "uploadUpdateLog---onPostGet: ");
//                if (httpResponse != null) {
//                    int code = httpResponse.getResponseCode();
//                    Log.i(TAG, "onPostGet: " + code);
//                    if (code == 200) {
//                        FileManagerUtils.deleteFile(AppStoreConstant.APPCENTER_LOG_UPDATE_LOG);
//                        PreferencesUtils.putLong(context, AppStoreConstant.PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME, System.currentTimeMillis());
//                    }
//                }
//            }
//        });
    }

    public void generateStartOrExitRunLog(Context context, boolean isStart) {
        if (!sUploadLogOn) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(generateFirstStartLog(context, false, false));
        builder.append(AppStoreConstant.ASCII_US);
        if (isStart) {
            mStartRunTime = System.currentTimeMillis();
            builder.append(AppStoreConstant.LOG_ACTION).append(AppStoreConstant.ASCII_FS).append("1");
            builder.append(AppStoreConstant.ASCII_US);
            builder.append(AppStoreConstant.LOG_DURATION).append(AppStoreConstant.ASCII_FS).append("");
        } else {
            builder.append(AppStoreConstant.LOG_ACTION).append(AppStoreConstant.ASCII_FS).append("2");
            builder.append(AppStoreConstant.ASCII_US);
            builder.append(AppStoreConstant.LOG_DURATION).append(AppStoreConstant.ASCII_FS).append(System.currentTimeMillis() - mStartRunTime);
        }
        String startOrExitLog = builder.toString();
        FileManagerUtils.writeLog(AppStoreConstant.APPCENTER_LOG_START_EXIT_LOG, startOrExitLog, true);
    }

    public void generateUseroperationlog(Context context, int action, String groupname, String pagename, String categoryid, String rank, String linktype) {
        if (!sUploadLogOn) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(generateFirstStartLog(context, true, false));
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_ACTION).append(AppStoreConstant.ASCII_FS).append(action + "");
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_GROUPNAME).append(AppStoreConstant.ASCII_FS).append(groupname);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_PAGENAME).append(AppStoreConstant.ASCII_FS).append(pagename);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_CATEGORYID).append(AppStoreConstant.ASCII_FS).append(categoryid);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_RANK).append(AppStoreConstant.ASCII_FS).append(rank);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_LINKTYPE).append(AppStoreConstant.ASCII_FS).append(linktype);
        String userOpeartorLog = builder.toString();
        FileManagerUtils.writeLog(AppStoreConstant.APPCENTER_LOG_USER_OPERATOR_LOG, userOpeartorLog, true);
    }

    public void generateResourceLog(Context context, int action, String appIdEn, int attribute, int source, String rank, String linktype) {
        if (!sUploadLogOn) {
            return;
        }
        String attributess = attribute + "";
        if ((!TextUtils.isEmpty(linktype)) && attribute == 0) {
            attributess = "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(generateFirstStartLog(context, true, false));
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_ACTION).append(AppStoreConstant.ASCII_FS).append(action);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_APPID).append(AppStoreConstant.ASCII_FS).append(appIdEn);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_ATTRIBUTE).append(AppStoreConstant.ASCII_FS).append(attributess);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_SOURCE).append(AppStoreConstant.ASCII_FS).append(source);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_RANK).append(AppStoreConstant.ASCII_FS).append(rank);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_LINKTYPE).append(AppStoreConstant.ASCII_FS).append(linktype);
        String resourceLog = builder.toString();
        FileManagerUtils.writeLog(AppStoreConstant.APPCENTER_LOG_RESOURCE_LOG, resourceLog, true);
    }

    public void generateSearchLog(Context context, String content) {
        if (!sUploadLogOn) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(generateFirstStartLog(context, true, false));
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_KEYWORDS).append(AppStoreConstant.ASCII_FS).append(content);
        String searchLog = builder.toString();
        FileManagerUtils.writeLog(AppStoreConstant.APPCENTER_LOG_SEARCH_LOG, searchLog, true);
    }

    public void generateAhaUpdateLog(Context context, String newVersion, int action) {
        if (!sUploadLogOn) {
            return;
        }
        PackageInfo packageInfo = ConstantUtils.getPackageInfo(context);
        StringBuilder builder = new StringBuilder();
        builder.append(generateFirstStartLog(context, true, true));
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_VERSION).append(AppStoreConstant.ASCII_FS).append(newVersion);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_PREVERSION).append(AppStoreConstant.ASCII_FS).append(packageInfo.versionName);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_ACTION).append(AppStoreConstant.ASCII_FS).append(action);
        String updateLog = builder.toString();
        FileManagerUtils.writeLog(AppStoreConstant.APPCENTER_LOG_UPDATE_LOG, updateLog, true);
    }

    private String generateFirstStartLog(Context context, boolean isUserOperation, boolean isAhaUpdate) {
        PackageInfo packageInfo = ConstantUtils.getPackageInfo(context);
        StringBuilder builder = new StringBuilder();
        builder.append(AppStoreConstant.LOG_IMEI).append(AppStoreConstant.ASCII_FS).append(DeviceInfo.getIMEI());
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_GAID).append(AppStoreConstant.ASCII_FS).append(DeviceInfo.getGAId());
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_ANDROIDID).append(AppStoreConstant.ASCII_FS).append(DeviceInfo.getAndroidID());
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_CTM).append(AppStoreConstant.ASCII_FS).append(ConstantUtils.getCurrentTime());
        builder.append(AppStoreConstant.ASCII_US);
        String ip = "";
        if (mUpdateOption != null) {
            ip = mUpdateOption.getIp();
        }
        builder.append(AppStoreConstant.LOG_IP).append(AppStoreConstant.ASCII_FS).append(ip);
        if (!isUserOperation) {
            builder.append(AppStoreConstant.ASCII_US);
            builder.append(AppStoreConstant.LOG_UA)
                    .append(AppStoreConstant.ASCII_FS).append(ConstantUtils.getPhoneBrand() + "/" + ConstantUtils.getPhoneModel() + "/" + Build.DISPLAY + "/" + "Android" + Build.VERSION.RELEASE + "/" + PreferencesUtils.getString(context, AppStoreConstant.PRE_KEY_PHONE_DISPLAY, ""));
        }
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_OPERATOR).append(AppStoreConstant.ASCII_FS).append(ConstantUtils.getOperator(context));
        if (!isAhaUpdate) {
            builder.append(AppStoreConstant.ASCII_US);
            builder.append(AppStoreConstant.LOG_VERSION).append(AppStoreConstant.ASCII_FS).append(packageInfo.versionName);
        }
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_BIZID).append(AppStoreConstant.ASCII_FS).append(packageInfo.packageName);
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_OSID).append(AppStoreConstant.ASCII_FS).append(ConstantUtils.isPad(context) ? "androidpad" : "android");
        builder.append(AppStoreConstant.ASCII_US);
        builder.append(AppStoreConstant.LOG_APCODE).append(AppStoreConstant.ASCII_FS).append(ConstantUtils.getNetworkType(context));
        return builder.toString();
    }

}
