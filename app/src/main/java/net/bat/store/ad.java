package net.bat.store;

import com.transsion.iad.core.TAdError;
import com.transsion.iad.core.TAdListener;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.bean.Ad;
import com.transsion.iad.core.bean.TAdNativeInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by liang.he on 2017/7/10.
 */

public class ad {


    private Map<String, TAdNativeInfo> mNativeInfoMaps;
    private Map<TAdNativeInfo, TAdNative> mAdNativeMaps;
    private List<String> mTempRequestSolds;

    void requestAdNative(final String appId, final String slodId) {
        if (mTempRequestSolds.contains(appId)) {
            return;
        } else {
            mTempRequestSolds.add(appId);
        }
        final TAdNative tAdNative = new TAdNative(BaseApplication.getContext(), slodId);
        tAdNative.setAdListener(new TAdListener() {
            @Override
            public void onAdLoaded(Ad ad) {
                if (ad != null && ad instanceof TAdNativeInfo) {
                    TAdNativeInfo tAdNativeInfo = (TAdNativeInfo) ad;
                    //Log.i(TAG, "title:" + tAdNativeInfo.getTitle());
                    mNativeInfoMaps.put(appId, tAdNativeInfo);
                    mAdNativeMaps.put(tAdNativeInfo, tAdNative);
                } else {
                    mTempRequestSolds.remove(appId);
                }
            }

            @Override
            public void onError(TAdError adError) {
                // Log.i(TAG, "adError:" + adError.getErrorMessage() + adError.getErrorCode());
                mTempRequestSolds.remove(appId);
            }
        });
        tAdNative.loadAd();
    }
}
