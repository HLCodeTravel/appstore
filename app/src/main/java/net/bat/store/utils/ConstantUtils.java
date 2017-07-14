package net.bat.store.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.transsion.iad.core.utils.AdTypeManager;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.bean.floor.BannerMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.ux.acticivty.AppDetailActivity;
import net.bat.store.ux.acticivty.H5Activity;
import net.bat.store.ux.acticivty.TopicActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Created by bingbing.li on 2017/1/10.
 */

public class ConstantUtils {

    public static final String LAN_AR = "ar";
    public static final String LAN_UR_RPK = "ur";
    public static final String LAN_FA = "fa";
    public static final String LAN_IW = "iw";
    public static final int MILLISECOND = 1000;
    public static final int ONE_DAY_TIME = 24 * 60 * 60;

    public static boolean checkNetworkState(Context mContext) {
        ConnectivityManager connectMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectMgr == null) {
            return false;
        }
        NetworkInfo nwInfo = connectMgr.getActiveNetworkInfo();
        if (nwInfo == null || !nwInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public static PackageInfo getPackageInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    public static String getIMSI(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if ((TelephonyManager.SIM_STATE_READY != manager.getSimState()) || (manager == null)) {
            return null;
        }
        String imsi = manager.getSubscriberId();
        if (imsi != null && imsi.length() > 0) {
            return imsi;
        }
        return null;
    }

    public static String getCountryCode(Context context) {
        String imsi = getIMSI(context);
        if (imsi != null) {
            return imsi.substring(0, 3);
        } else {
            return "";
        }
    }

    public static String getOperator(Context context) {
        String imsi = getIMSI(context);
        if (imsi != null) {
            return imsi.substring(0, 5);
        } else {
            return "";
        }
    }

    /**
     * 判断当前设备是手机还是平板
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String getNetworkType(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(networkInfo.isConnected()){
            return "Wifi";
        }
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(networkInfo.isConnected()){
            return "Mobile";
        }
        return "u_unknown";
    }

    public static String getPhoneModel() {
        return Build.MODEL;
    }

    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getAppSize(String size) {
        return size + "MB";
    }

    public static String getSimOperatorName(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if ((TelephonyManager.SIM_STATE_READY != manager.getSimState()) || (manager == null)) {
            return null;
        }
        String operateName = manager.getSimOperatorName();
        if (operateName != null && operateName.length() > 0) {
            return operateName;
        }
        return null;
    }


    public static String addComma(String str) {
        String reverseStr = new StringBuilder(str).reverse().toString();
        String strTemp = "";
        for (int i = 0; i < reverseStr.length(); i++) {
            if (i * 3 + 3 > reverseStr.length()) {
                strTemp += reverseStr.substring(i * 3, reverseStr.length());
                break;
            }
            strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
        }
        if (strTemp.endsWith(",")) {
            strTemp = strTemp.substring(0, strTemp.length() - 1);
        }
        String resultStr = new StringBuilder(strTemp).reverse().toString();
        return resultStr;
    }

//    public static Bitmap getNetWorkBitmap(String urlString) {
//        URL imgUrl = null;
//        Bitmap bitmap = null;
//        try {
//            imgUrl = new URL(urlString);
//            HttpURLConnection urlConn = (HttpURLConnection) imgUrl.openConnection();
//            urlConn.setDoInput(true);
//            urlConn.setReadTimeout(1000);
//            urlConn.setConnectTimeout(1000);
//            urlConn.connect();
//            InputStream is = urlConn.getInputStream();
//            bitmap = BitmapFactory.decodeStream(is);
//            is.close();
//        } catch (MalformedURLException e) {
//            // TODO Auto-generated catch block
//            System.out.println("[getNetWorkBitmap->]MalformedURLException");
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("[getNetWorkBitmap->]IOException");
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    public static String getDownloadPath() {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString() + "/ahaapps";
        } else
            return "/data/data/ahaapps";
    }

    public static boolean iSAdmobNativeAd(int type) {
        if (type == AdTypeManager.AD_ADMOB_APP_INSTALL
                || type == AdTypeManager.AD_ADMOB_CONTENT) {
            return true;
        }
        return false;
    }

    public static boolean isExistsProcess(String packageName) {
        try {
            BaseApplication.getContext().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean lagerCurrent1Day(Context context) {
        long lasttime = PreferencesUtils.getLong(context, AppStoreConstant.PRE_KEY_LAST_SHOW_AD_TIME, 0);
        long current = System.currentTimeMillis();
        int random = (int) (Math.random() * 60);
        if (Math.abs(current - lasttime) > (1 * ONE_DAY_TIME * MILLISECOND) + random * 60 * MILLISECOND) {
            return true;
        } else {
            return false;
        }
    }

    public static void goGooglePlayMarket(String uriString,Context context) {
        if (!uriString.startsWith(AppStoreConstant.GOOGLE_MARKET_APP_DETAIL)) {
            if (uriString.startsWith(AppStoreConstant.BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTP)) {
                int start = uriString.indexOf("id=");
                uriString = uriString.substring(start + "id=".length());
            } else if (uriString.startsWith(AppStoreConstant.BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS)) {
                int start = uriString.indexOf("id=");
                uriString = uriString.substring(start + "id=".length());
            }
            uriString = AppStoreConstant.GOOGLE_MARKET_APP_DETAIL + uriString;
        }
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        marketIntent.setPackage(AppStoreConstant.PKG_GOOGLE_PLAY);
        if (context instanceof Activity) {
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }else {
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            marketIntent.addFlags(0X00008000);
        }
        context.startActivity(marketIntent);
    }

    public static <K, V> boolean isEmpty(Map<K, V> sourceMap) {
        return (sourceMap == null || sourceMap.size() == 0);
    }

    public static String generateImageName(String imageUrl) {
        String imgName = MD5Utils.md5(imageUrl);
        if (!TextUtils.isEmpty(imgName)) {
            return imgName;
        }
        return Integer.toString(imageUrl.hashCode());
    }

    public static boolean isIconShouldRevert(){
        String lan = getLanguage();
        if (LAN_AR.equals(lan)
                ||LAN_UR_RPK.equals(lan)
                ||LAN_FA.equals(lan)
                ||LAN_IW.equals(lan)) {
            return true;
        } else {
            return false;
        }
    }

    public static void onItemClickBannerCombiner(BannerMapBean bannerMapBean,Context context){
        int linktype=bannerMapBean.getLinkType();
        String topicLink=bannerMapBean.getLink();
        if(linktype==AppStoreConstant.LINK_TYPE_THRID){
            try {
                H5Activity.openLink(context,topicLink);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, R.string.invaild_banner_link, Toast.LENGTH_SHORT).show();
            }
        }else if(linktype==AppStoreConstant.LINK_TYPE_DETAIL){
            int appType = bannerMapBean.getAppAttribute();
            if (AppStoreConstant.APP_SELF == appType
                    ||AppStoreConstant.APP_H5_GAME==appType) {
                Intent intent = new Intent(context, AppDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("notificationurl", bannerMapBean.getLink());
                bundle.putBoolean("newsfloor",true);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (AppStoreConstant.APP_H5 == appType||appType==AppStoreConstant.APP_APPSFLER) {//H5游戏
                H5Activity.openLink(context, bannerMapBean.getLink());
            } else if (AppStoreConstant.APP_GOOGLE == appType) {//Google应用
                String uriString=bannerMapBean.getLink();
                if(ConstantUtils.isExistsProcess(AppStoreConstant.PKG_GOOGLE_PLAY)){
                    ConstantUtils.goGooglePlayMarket(uriString,context);
                }else{
                    Uri uri = Uri.parse(uriString);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }else if(linktype==AppStoreConstant.LINK_TYPE_PAGE){
            if(TextUtils.isEmpty(topicLink)){
                Toast.makeText(context,R.string.appcenter_search_no_result_text,Toast.LENGTH_SHORT).show();
                return;
            }
            Intent topicIntent=new Intent(context, TopicActivity.class);
            topicIntent.putExtra(AppStoreConstant.FLAG_TOPIC_LINK,topicLink);
            topicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(topicIntent);
        }
    }

    public static boolean isShowFloatWindows(String startTimestr,String endTimestr){
        long startTime=0,endTime=0;
        if(!TextUtils.isEmpty(startTimestr)){
           startTime=Long.parseLong(startTimestr);
        }
        if(!TextUtils.isEmpty(endTimestr)){
           endTime=Long.parseLong(endTimestr);
        }
        long currentTime=System.currentTimeMillis();
        if(currentTime>=startTime&&currentTime<=endTime){
            return true;
        }
        return false;
    }

//    public static int getScreenWidth(Context context){
//        WindowManager wm = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(outMetrics);
//        return outMetrics.widthPixels;
//    }
//
//    public static int getScreenHeight(Context context){
//        WindowManager wm = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(outMetrics);
//        return outMetrics.heightPixels;
//    }

//    public static Bitmap getScaleBitmap(Bitmap resource,Context context){
//        int imageWidth = resource.getWidth();
//        int imageHeight = resource.getHeight();
//        int scrrenWidth=ConstantUtils.getScreenWidth(context);
//        int scrrenHeight=ConstantUtils.getScreenHeight(context);
//        float scaleX = ((float) scrrenWidth) / imageWidth;
//        float scaleY = ((float) scrrenHeight) / imageHeight;
//        int newW=0,newH=0;
//        if(scaleX > scaleY){
//            newW = (int) (imageWidth * scaleY);
//            newH = (int) (imageHeight * scaleY);
//        }else if(scaleX <= scaleY){
//            newW = (int) (imageWidth * scaleX);
//            newH = (int) (imageHeight * scaleX);
//        }
//        return Bitmap.createScaledBitmap(resource, newW, newH, true);
//    }

    public static String getCurrentTime(){
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyyMMddhhmmss");
        return sDateFormat.format(new java.util.Date());
    }

    public static long getCurrentData(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long time = System.currentTimeMillis();
        String dateTime = df.format(time);
        try {
            return df.parse(dateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
