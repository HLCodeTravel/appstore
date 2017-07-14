package net.bat.store.network;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.transsion.http.HttpClient;
import com.transsion.http.impl.StringCallback;

import net.bat.store.constans.NetworkConstant;
import net.bat.store.utils.CommonUtils;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.bean.AppInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bingbing.li on 2017/1/17.
 */

public class NetworkClient {

    public static void startRequestQueryPageGroup(Context context, StringCallback listener) {
        PackageInfo packageInfo = ConstantUtils.getPackageInfo(context);
        Map<String, String> parms = new HashMap<>();
        parms.put(NetworkConstant.PARMS_VERSION, packageInfo.versionName + "_" + packageInfo.versionCode);
        parms.put(NetworkConstant.PARMS_CLIENTVERSION, packageInfo.versionName);
        parms.put(NetworkConstant.PARMS_COUNTRY, ConstantUtils.getCountryCode(context));
        parms.put(NetworkConstant.PARMS_BRAND, ConstantUtils.getPhoneBrand());
        parms.put(NetworkConstant.PARMS_MODEL, ConstantUtils.getPhoneModel());
        parms.put(NetworkConstant.PARMS_DEVICE, "phone");
        parms.put(NetworkConstant.PARMS_OPERATOR, ConstantUtils.getOperator(context));
        parms.put(NetworkConstant.PARMS_LANGUAGE, ConstantUtils.getLanguage());
        parms.put(NetworkConstant.PARMS_SOURCE, packageInfo.packageName);
        parms.put(NetworkConstant.PARAMS_INTERFACE_VERSION,NetworkConstant.INTERFACE_VERSION);
        //HttpUtils.getInstance().httpPostString(NetworkConstant.URL_QUERY_GROUP, parms, listener);
        HttpClient.post().params(parms).url(NetworkConstant.URL_QUERY_GROUP).build().execute(listener);
    }

    public static void startRequestQueryPageDetail(Context context, String url, StringCallback httpListener) {
        PackageInfo packageInfo = ConstantUtils.getPackageInfo(context);
        Map<String, String> parms = new HashMap<>();
        parms.put(NetworkConstant.PARMS_VERSION, packageInfo.versionName + "_" + packageInfo.versionCode);
        parms.put(NetworkConstant.PARMS_CLIENTVERSION, packageInfo.versionName);
        parms.put(NetworkConstant.PARMS_COUNTRY, ConstantUtils.getCountryCode(context));
        parms.put(NetworkConstant.PARMS_BRAND, ConstantUtils.getPhoneBrand());
        parms.put(NetworkConstant.PARMS_MODEL, ConstantUtils.getPhoneModel());
        parms.put(NetworkConstant.PARMS_DEVICE, "phone");
        parms.put(NetworkConstant.PARMS_LANGUAGE, ConstantUtils.getLanguage());
        parms.put(NetworkConstant.PARMS_SOURCE, packageInfo.packageName);
        parms.put(NetworkConstant.PARMS_OPERATOR, ConstantUtils.getOperator(context));
        parms.put(NetworkConstant.PARAMS_INTERFACE_VERSION,NetworkConstant.INTERFACE_VERSION);
        HttpClient.post().params(parms).url(url).build().execute(httpListener);
    }


    public static void startRequestQueryAppCategory(Context context, String url, StringCallback httpListener) {
        PackageInfo packageInfo = ConstantUtils.getPackageInfo(context);
        Map<String, String> parms = new HashMap<>();
        parms.put(NetworkConstant.PARMS_VERSION, packageInfo.versionName + "_" + packageInfo.versionCode);
        parms.put(NetworkConstant.PARMS_CLIENTVERSION, packageInfo.versionName);
        parms.put(NetworkConstant.PARMS_COUNTRY, ConstantUtils.getCountryCode(context));
        parms.put(NetworkConstant.PARMS_BRAND, ConstantUtils.getPhoneBrand());
        parms.put(NetworkConstant.PARMS_MODEL, ConstantUtils.getPhoneModel());
        parms.put(NetworkConstant.PARMS_DEVICE, "phone");
        parms.put(NetworkConstant.PARMS_LANGUAGE, ConstantUtils.getLanguage());
        parms.put(NetworkConstant.PARMS_SOURCE, packageInfo.packageName);
        parms.put(NetworkConstant.PARMS_OPERATOR, ConstantUtils.getOperator(context));
        parms.put(NetworkConstant.PARAMS_INTERFACE_VERSION,NetworkConstant.INTERFACE_VERSION);
        //HttpUtils.getInstance().httpPostString(url,parms,httpListener);
        HttpClient.post().params(parms).url(url).build().execute(httpListener);
    }

    public static void startRequestQueryAppDetail(Context context, String url, StringCallback httpListener) {
        PackageInfo packageInfo = ConstantUtils.getPackageInfo(context);
        Map<String, String> parms = new HashMap<>();
        parms.put(NetworkConstant.PARMS_VERSION, packageInfo.versionName + "_" + packageInfo.versionCode);
        parms.put(NetworkConstant.PARMS_CLIENTVERSION, packageInfo.versionName);
        parms.put(NetworkConstant.PARMS_COUNTRY, ConstantUtils.getCountryCode(context));
        parms.put(NetworkConstant.PARMS_BRAND, ConstantUtils.getPhoneBrand());
        parms.put(NetworkConstant.PARMS_MODEL, ConstantUtils.getPhoneModel());
        parms.put(NetworkConstant.PARMS_DEVICE, "phone");
        parms.put(NetworkConstant.PARMS_LANGUAGE, ConstantUtils.getLanguage());
        parms.put(NetworkConstant.PARMS_SOURCE, packageInfo.packageName);
        parms.put(NetworkConstant.PARMS_OPERATOR, ConstantUtils.getOperator(context));
        parms.put(NetworkConstant.PARAMS_INTERFACE_VERSION,NetworkConstant.INTERFACE_VERSION);
        //HttpUtils.getInstance().httpPostString(url,parms,httpListener);
        HttpClient.post().params(parms).url(url).build().execute(httpListener);
    }

    public static void startRequestQueryAppVersion(Context context, String url, StringCallback httpListener) {
        PackageInfo packageInfo = ConstantUtils.getPackageInfo(context);
        Map<String, String> params = new HashMap<>();
        params.put(NetworkConstant.PARAMS_PACKAGENAME, packageInfo.packageName);
        params.put(NetworkConstant.PARAMS_VERSIONCODE, packageInfo.versionCode + "");
        //HttpUtils.getInstance().httpPostString(url,params, httpListener);
        HttpClient.post().log(true).params(params).url(url).build().execute(httpListener);
    }

    public static void startRequestQueryUpdateInfo(Context context, String url, StringCallback httpListener) {
        Map<String, String> params = new HashMap<>();
        params.put(NetworkConstant.PARMS_VERSION, "1");
        params.put(NetworkConstant.PARMS_LANGUAGE, ConstantUtils.getLanguage());
        StringBuffer buffer = new StringBuffer();
        List<AppInfo> appInfoList = CommonUtils.queryAppInfo(context);
        if (appInfoList != null && !appInfoList.isEmpty()) {
            for (int i = 0; i < appInfoList.size(); i++) {
                if (i + 1 != appInfoList.size()) {
                    buffer.append(appInfoList.get(i).getPackageName() + ",");
                } else {
                    buffer.append(appInfoList.get(i).getPackageName());
                }
            }
        }
        params.put(NetworkConstant.PARAMS_PACKAGENAME, buffer.toString());
        // HttpUtils.getInstance().httpPostString(url,params,httpListener);
        HttpClient.post().log(true).params(params).url(url).build().execute(httpListener);
    }

    public static void startRequestAppSign(String url, StringCallback httpListener, String paramUrl) {
        Map<String, String> params = new HashMap<>();
        params.put("appUrl", paramUrl);
        //HttpUtils.getInstance().httpPostString(url,params,httpListener);
        HttpClient.post().log(true).params(params).url(url).build().execute(httpListener);
    }


    public static void UploadLog(String logName, String logContent, StringCallback httpListener) {
        Map<String, String> params = new HashMap<>();
        params.put(logName, logContent);
        HttpClient.post().log(true).params(params).url(NetworkConstant.URL_LOG_UPLOAD).build().execute(httpListener);
    }


    public static void getUploadOption(StringCallback httpListener) {
        HttpClient.get().log(true).url(NetworkConstant.URL_LOG_GET_CONFIG).build().execute(httpListener);
    }

}
