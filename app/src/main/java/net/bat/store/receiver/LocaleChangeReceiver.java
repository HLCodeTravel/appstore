package net.bat.store.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.bat.store.constans.AppStoreConstant;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.PreferencesUtils;

/**
 * Created by bingbing.li on 2017/4/27.
 */

public class LocaleChangeReceiver extends BroadcastReceiver{

    private static final String TAG = "LocaleChangeReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        Log.i(TAG, "onReceive: "+action);
        if(Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
            String last_lan= PreferencesUtils.getString(context, AppStoreConstant.PRE_KEY_LAN_LAST_LANGUAGE,null);
            String new_lan= ConstantUtils.getLanguage();
            Log.i(TAG, "onReceive: "+last_lan+"  "+new_lan);
            if(!new_lan.equals(last_lan)){
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
    }
}
