/* Top Secret */
package net.bat.store.widget.banner.holder;
import android.content.Context;
import android.view.View;

import net.bat.store.bean.ad.AdBean;

import java.util.Map;

public interface Holder<T>{
    View createView(Context context,T data,Map<String, AdBean> map);
    void UpdateUI(Context context, int position, T data,Map<String, AdBean> map);
}