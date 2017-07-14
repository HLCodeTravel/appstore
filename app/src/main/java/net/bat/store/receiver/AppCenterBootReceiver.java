package net.bat.store.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.bat.store.service.AppCenterNotifyService;

/**
 * Created by bingbing.li on 2017/3/14.
 */

public class AppCenterBootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notiIntent = new Intent(context, AppCenterNotifyService.class);
        notiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startService(notiIntent);
    }
}
