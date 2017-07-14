package net.bat.store.bean.ad;

import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.bean.TAdNativeInfo;

/**
 * Created by bingbing.li on 2017/3/2.
 */

public class AdBean {
    private TAdNativeInfo tAdNativeInfo;
    private TAdNative tAdNative;

    public TAdNativeInfo gettAdNativeInfo() {
        return tAdNativeInfo;
    }

    public void settAdNativeInfo(TAdNativeInfo tAdNativeInfo) {
        this.tAdNativeInfo = tAdNativeInfo;
    }

    public TAdNative gettAdNative() {
        return tAdNative;
    }

    public void settAdNative(TAdNative tAdNative) {
        this.tAdNative = tAdNative;
    }
}
