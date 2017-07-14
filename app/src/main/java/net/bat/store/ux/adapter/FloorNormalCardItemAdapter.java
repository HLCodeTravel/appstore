package net.bat.store.ux.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.bat.store.R;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.ux.adapter.viewholder.FloorListCardItemHolder;
import net.bat.store.ux.adapter.viewholder.FloorNormalCardItemHolder;


/**
 * Created by liang.he on 2017/7/10.
 */

public class FloorNormalCardItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FloorMapBean mFloorBeanMap = null;
    Context mContext = null;
    private LayoutInflater mInflater = null;


    public FloorNormalCardItemAdapter(Context context, FloorMapBean floorMapBean) {
        mContext = context;
        mFloorBeanMap = floorMapBean;
        mInflater = LayoutInflater.from(context);
    }

//    public void setFloorListViewdata(List<AppMapBean> appMapList) {
//        mAppMapList = appMapList;
//        notifyDataSetChanged();
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FloorNormalCardItemHolder(mContext, mInflater.inflate(R.layout.floor_normal_card_item, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FloorNormalCardItemHolder) {
            FloorNormalCardItemHolder floorNormalCardItemHolder = (FloorNormalCardItemHolder) holder;
            floorNormalCardItemHolder.onBinderData(mFloorBeanMap.getAppMapList().get(position), position
                    , mFloorBeanMap.getIsShowTag(), mFloorBeanMap.getFloorId(), mFloorBeanMap.getFloorTitle());
        }
    }

    @Override
    public int getItemCount() {
        return mFloorBeanMap.getAppMapList().size();
    }


//    @Override
//    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//    }
}
