package net.bat.store.ux.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.bat.store.R;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.ux.adapter.FloorListCardItemAdapter;
import net.bat.store.ux.adapter.FloorNormalCardItemAdapter;

/**
 * Created by liang.he on 2017/7/7.
 */

public class FloorNormalCardHolder extends BaseFloorViewHolder {


    private RecyclerView mRecyclerView = null;


    public FloorNormalCardHolder(Context context, View itemView) {
        super(context, itemView);

        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.rv_normal);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(appContext, LinearLayoutManager.VERTICAL, false));
    }


    @Override
    public void onBindView(FloorMapBean floorMapBean) {
        FloorNormalCardItemAdapter mFloorNormalCardItemAdapter = new FloorNormalCardItemAdapter(appContext, floorMapBean);
        mRecyclerView.setAdapter(mFloorNormalCardItemAdapter);

    }
}
