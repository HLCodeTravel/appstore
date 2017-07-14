package net.bat.store.ux.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.bat.store.bean.floor.FloorMapBean;


/**
 * Created by liang.he on 2017/7/7.
 */

public abstract class BaseFloorViewHolder extends RecyclerView.ViewHolder {

    Context appContext = null;

    public BaseFloorViewHolder(Context context, View itemView) {
        super(itemView);
        appContext = context.getApplicationContext();
    }

    public abstract void onBindView(FloorMapBean data);
}
