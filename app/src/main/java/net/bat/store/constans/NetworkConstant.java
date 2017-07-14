package net.bat.store.constans;

import net.bat.store.BuildConfig;

;

/**
 * Created by bingbing.li on 2017/1/17.
 */

public class NetworkConstant {

    /*请求code*/
    public static final String CODE_SUCCESS = "1000";
    public static final String INTERFACE_VERSION = "2";  //接口版本号

    /**
     * 服务器域名切换
     */
    public static final String HOST = BuildConfig.APP_MODE == 0 ? "http://app.shtranssion.com" : "http://sta.tekdowndata.com";
    public static final String HOST_UPDATE = BuildConfig.APP_MODE == 0 ? "http://mis.transsion.com" : "http://sta.tekdowndata.com";
    public static final String URL_QUERY_GROUP = HOST + "/leo-api/api?api_code=leo.api.pageGroup";
    public static final String URL_QUERY_PACKAGE_DETAIL = HOST + "/leo-api/api?api_code=leo.api.pageDetail";
    public static final String URL_QUERY_PACKAGE_DETA = HOST + "/leo-api/api?api_code=leo.api.pageDetail";
    public static final String URL_QUERY_UPDATE = HOST_UPDATE + "/webapp/RlkMi/app/checkApkVersion";
    public static final String URL_Notification_SEND=HOST+"/leo-api/api?api_code=leo.api.pollingMessage";
    public static final String URL_APK_SIGN = HOST+"/leo-api/api?api_code=leo.api.appSignUrl";

    public static final String URL_QUERY_GPUPDATE = HOST+"/leo-api/api?api_code=leo.api.gpMultUpdate";
    public static final String URL_GOOGLE_SEARCH = "https://play.google.com/store/search?q=";

    public static final String URL_LOG_HOST=BuildConfig.APP_MODE == 0 ?"http://dat.transacme.com/data-api":"http://dat.transacme.com/data-api-test";
    public static final String URL_LOG_GET_CONFIG=URL_LOG_HOST+"/UploadOptionAha?mcc=0";
    public static final String URL_LOG_UPLOAD=URL_LOG_HOST+"/UploadFlumeAha";

    public static final String PARMS_VERSION = "version";
    public static final String PARMS_CLIENTVERSION = "clientVersionName";
    public static final String PARMS_COUNTRY = "country";
    public static final String PARMS_BRAND = "brand";
    public static final String PARMS_MODEL = "model";
    public static final String PARMS_DEVICE = "device";
    public static final String PARMS_LANGUAGE = "language";
    public static final String PARMS_SOURCE = "source";
    public static final String PARMS_PAGEID = "pageId";
    public static final String PARMS_OPERATOR = "operator";
    public static final String PARMS_APPTYPE = "appType";
    public static final String PARAMS_PACKAGENAME = "packageName";
    public static final String PARAMS_APPTYPE = "appType";
    public static final String PARAMS_VERSIONCODE = "versionCode";
    public static final String PARAMS_MCC = "mcc";
    public static final String PARAMS_INTERFACE_VERSION = "interfaceVersion";

    /*页面类型*/
    public static final String TYPE_PAGE_DETAIL = "leo.api.pageDetail";
    public static final String TYPE_APP_CAGETORY = "leo.api.appCategory";

    /**
     * 请求网络handler Message 状态
     * */
    public static final int MSG_LOADING = 0;
    public static final int MSG_LOADED = 1;
    public static final int MSG_NETWORK_ERROR = 2;
    public static final int MSG_REQUEST_FAIL = 3;
    public static final int MSG_LOADED_NO_CHANGE = 4;
}
