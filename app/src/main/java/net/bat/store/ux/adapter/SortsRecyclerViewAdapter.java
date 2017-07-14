package net.bat.store.ux.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.bat.store.R;
import net.bat.store.bean.category.CategoryMapBean;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.adapter.viewholder.SortsViewHolder;
import net.bat.store.ux.listener.RecyclerViewItemClickListener;
import net.bat.store.ux.acticivty.SortDetailActivity;

import java.util.List;

/**
 * usage
 *
 * @author cheng.qian.
 * @date 2016/9/26.
 * ============================================
 * Copyright (c) 2016 TRANSSION.Co.Ltd.
 * All right reserved.
 **/


public class SortsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<CategoryMapBean> categoryMapBeanList;
    private String mGroupName;
    private String mSecondaryMenuName;

    public SortsRecyclerViewAdapter(Context context, List<CategoryMapBean> categoryMapBeanList, String groupname, String secondaryMenuName) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mGroupName = groupname;
        mSecondaryMenuName = secondaryMenuName;
        this.categoryMapBeanList = categoryMapBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SortsViewHolder viewHolder = new SortsViewHolder(layoutInflater.inflate(R.layout.item_recyclerview_singleline, parent, false));
        viewHolder.setItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoryMapBean categoryMapBean = categoryMapBeanList.get(position);
                UploadLogManager.getInstance().generateUseroperationlog(context, 3, mGroupName, mSecondaryMenuName, categoryMapBean.getResourceSnapshotId(), "", "");
                Intent intent = new Intent(context, SortDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("categoryMapBean", categoryMapBean);
                bundle.putString("groupName", mGroupName);
                intent.putExtras(bundle);
                context.startActivity(intent);
                Bundle param = new Bundle();
                param.putString(FirebaseAnalytics.Param.ITEM_ID, categoryMapBean.getAppCategory());
                param.putString(FirebaseAnalytics.Param.ITEM_NAME, position + "");
                param.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Category");
                FirebaseStat.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, param);

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SortsViewHolder viewHolder = (SortsViewHolder) holder;
        CategoryMapBean categoryMapBean = categoryMapBeanList.get(position);
        viewHolder.sortName.setText(categoryMapBean.getAppCategory());
        Glide.with(context).load(categoryMapBean.getIconUrl()).placeholder(R.drawable.image_default).error(R.drawable.image_default).centerCrop().into(viewHolder.icon);
    }

    @Override
    public int getItemCount() {
        if (categoryMapBeanList != null) {
            return categoryMapBeanList.size();
        }
        return 0;
    }
}
