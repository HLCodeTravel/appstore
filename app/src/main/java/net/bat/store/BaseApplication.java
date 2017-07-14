package net.bat.store;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.transsion.core.CoreUtil;
import com.transsion.iad.core.TAdManager;

import net.bat.store.BuildConfig;



/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/1/11
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class BaseApplication extends Application {

    private static Context sContext;


    @Override
    public void onCreate() {
        super.onCreate();
        TAdManager.init(new TAdManager.AdConfigBuilder(this).setDebuggable(BuildConfig.APP_MODE != 0).setLoggable(BuildConfig.APP_MODE != 0).build());
        sContext = getApplicationContext();
        CoreUtil.init(this);
//        GameManager.init(sContext,BuildConfig.APP_MODE != 0);
        //加入内存泄漏检测
//        if(BuildConfig.APP_MODE != 0){
//            LeakCanary.install(this);
//        }
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }
}
