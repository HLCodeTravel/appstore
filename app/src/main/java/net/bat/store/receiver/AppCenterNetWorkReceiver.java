package net.bat.store.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.bat.store.AhaLogCallBack;
import net.bat.store.service.AppCenterNotifyService;
import net.bat.store.utils.UploadLogManager;

/**
 * Created by bingbing.li on 2017/3/14.
 */

public class AppCenterNetWorkReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction()==null){
            return;
        }
        Intent notiIntent = new Intent(context, AppCenterNotifyService.class);
        context.startService(notiIntent);
        UploadLogManager.getInstance().getUploadConfig(context, new AhaLogCallBack() {
            @Override
            public void callback() {
                UploadLogManager.getInstance().uploadAhaLog(context);
            }
        });
    }
}
