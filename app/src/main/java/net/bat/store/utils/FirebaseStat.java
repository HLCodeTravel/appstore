package net.bat.store.utils;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.bat.store.BuildConfig;
import net.bat.store.BaseApplication;

/**
 * usage
 *
 * @author 金新
 * @date 2017/3/27
 * =====================================
 * Copyright (c)2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */

public class FirebaseStat {

    public static void logEvent(String s, Bundle bundle) {
        if (BuildConfig.APP_MODE == 0) {
            FirebaseAnalytics.getInstance(BaseApplication.getContext()).logEvent(s, bundle);

        }
    }
    public static void  setUserProperty(String s1,String s2 ){
        if (BuildConfig.APP_MODE == 0) {
            FirebaseAnalytics.getInstance(BaseApplication.getContext()).setUserProperty(s1,s2);
        }
    }



    public static void setCurrentScreen(Activity context, String s1, String s2) {
        if (BuildConfig.APP_MODE == 0) {
            FirebaseAnalytics.getInstance(BaseApplication.getContext()).setCurrentScreen(context,s1,s2);
        }
    }
}
