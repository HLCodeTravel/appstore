package net.bat.store.constans;

import android.os.Environment;

/**
 * Created by bingbing.li on 2017/1/16.
 */

public class AppStoreConstant {

    //主页--推荐Tab
    public static final int MAIN_RECOMMEND_TAB_INDEX = 0;
    //主页--游戏Tab
    public static final int MAIN_GAMES_TAB_INDEX = MAIN_RECOMMEND_TAB_INDEX + 1;
    //主页--应用Tab
    public static final int MAIN_APPS_TAB_INDEX = MAIN_GAMES_TAB_INDEX + 1;
    //主页--美化Tab
    public static final int MAIN_HTML_TAB_INDEX = MAIN_APPS_TAB_INDEX + 1;
    //主页--管理Tab(侧边栏)
    //主页默认Tab
    public static final int MAIN_DEFAULT_TAB_INDEX = MAIN_RECOMMEND_TAB_INDEX;
    //++++++++++++++++++++++++++++二级页面+++++++++++++++++++++++++
    //二级页面--推荐Tab
    public static final int TWO_PAGE_RECOMMEND_TAB_INDEX = 0;
    //二级页面--分类Tab
    public static final int TWO_PAGE_CATEGORY_TAB_INDEX = TWO_PAGE_RECOMMEND_TAB_INDEX + 1;
    //二级页面--榜单Tab
    public static final int TWO_PAGE_TOP_TAB_INDEX = TWO_PAGE_CATEGORY_TAB_INDEX + 1;
    //二级页面--默认Tab
    public static final int TWO_PAGE_DEFAULT_TAB_INDEX = TWO_PAGE_RECOMMEND_TAB_INDEX;

    //下载文件保存路径
    public static final String SDCARD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    public static final int APP_H5 = 1;
    public static final int APP_SELF = 2;
    public static final int APP_GOOGLE = 3;
    public static final int APP_H5_GAME = 4;
    public static final int APP_APPSFLER = 5;

    public static final int LINK_TYPE_THRID=1;
    public static final int LINK_TYPE_DETAIL=2;
    public static final int LINK_TYPE_PAGE=3;

    public static final String FLAG_TOPIC_LINK="topic_link";

    public static final String APPCENTER_JSON_DIR="/data/data/net.bat.store/files/appcenter/";
    public static final String APPCENTER_PAGEGROUP_JSON="pagegroup";
    public static final String APPCENTER_LOG_DIR="/data/data/net.bat.store/files/appcenter/uploadlog/";
    public static final String APPCENTER_LOG_CONFIGURE=APPCENTER_LOG_DIR+"configurelog";
    public static final String APPCENTER_LOG_START_EXIT_LOG=APPCENTER_LOG_DIR+"start_exit_log";
    public static final String APPCENTER_LOG_USER_OPERATOR_LOG=APPCENTER_LOG_DIR+"user_operator_log";
    public static final String APPCENTER_LOG_RESOURCE_LOG=APPCENTER_LOG_DIR+"resource_log";
    public static final String APPCENTER_LOG_SEARCH_LOG=APPCENTER_LOG_DIR+"search_log";
    public static final String APPCENTER_LOG_UPDATE_LOG=APPCENTER_LOG_DIR+"update_log";
    public static final String STATUS_REQUEST_SUCCESS="1000";

    public static final String PRE_KEY_LAST_SHOW_AD_TIME = "last_show_ad_time";

    public static final String PKG_GOOGLE_PLAY = "com.android.vending";
    public static final String GOOGLE_MARKET_APP_DETAIL = "market://details?id=";
    public static final String BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTP = "http://play.google.com/store/apps/details";
    public static final String BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS = "https://play.google.com/store/apps/details";

    public static final String PRE_KEY_LAN_LAST_LANGUAGE="lan_last_language";

    public static final int FLAG_SHOW_TITLE=1;
    public static final int REQUEST_ADMOB_TIMES=15;

    public static final String PRE_KEY_PHONE_DISPLAY = "phone_display";
    public static final String PRE_KEY_LAST_GET_UPLOAD_CONFIGURE_TIME = "last_get_upload_configure_time";
    public static final String PRE_KEY_LAST_UPLOAD_AHA_LOG_TIME = "last_upload_aha_log_time";
    public static final String PRE_KEY_HAVE_UPLOAD_FIRST = "have_upload_first";
    public static final String PRE_KEY_HAVE_CLEAN_CACHE = "have_clean_cache";
    public static final int MILLISECOND = 1000;
    public static final int ONE_HOUR_TIME = 1 * 60 * 60;

    //upload log
    public final static String ASCII_US = "\u001f";
    public final static String ASCII_FS = "\u001c";

    public static final String LOG_IMEI= "imei";
    public static final String LOG_GAID= "gadid";
    public static final String LOG_ANDROIDID= "androidid";
    public static final String LOG_CTM = "ctm";
    public static final String LOG_IP = "ip";
    public static final String LOG_UA = "ua";
    public static final String LOG_OPERATOR = "operator";
    public static final String LOG_VERSION = "version";
    public static final String LOG_BIZID = "bizid";
    public static final String LOG_OSID = "osid";
    public static final String LOG_APCODE = "apcode";

    public static final String LOG_ACTION = "action";
    public static final String LOG_DURATION = "duration";

    public static final String LOG_GROUPNAME = "groupname";
    public static final String LOG_PAGENAME = "pagename";
    public static final String LOG_CATEGORYID = "categoryid";
    public static final String LOG_RANK = "rank";
    public static final String LOG_LINKTYPE = "linktype";

    public static final String LOG_APPID = "appid";//英文应用名
    public static final String LOG_ATTRIBUTE = "attribute";
    public static final String LOG_SOURCE = "source";

    public static final String LOG_KEYWORDS = "keywords";

    public static final String LOG_PREVERSION = "preversion";

    public static final String EXPIRES       = "expires";
    public static final String CACHE_CONTROL = "cache-control";

    public static final String FIRST_START_LOG = "firststartlog";
    public static final String START_RUNLOG = "startrunlog";
    public static final String OPERATION_LOG = "operationlog";
    public static final String RESOURCE_LOG = "resourcelog";
    public static final String SEARCH_LOG = "searchlog";
    public static final String UPDATE_LOG = "updatelog";

    public static final int CONNECT_TIME_OUT=25000;
    public static final int READ_TIME_OUT=25000;
}
