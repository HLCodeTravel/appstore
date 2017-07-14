package net.bat.store.ux.adapter.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.bat.store.R;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.ux.adapter.FloorListCardItemAdapter;

/**
 * Created by liang.he on 2017/7/7.
 */

public class FloorListCardHolder extends BaseFloorViewHolder {

    private View NormalHader = null;
    private View TopicHander = null;
    private TextView tvNormalHaderName = null;
    private TextView tvTopicHanderName = null;
    private ImageView ivTopicHanderBackGround = null;
    private RecyclerView mRecyclerView = null;


    public FloorListCardHolder(Context context, View itemView) {
        super(context, itemView);
        NormalHader = itemView.findViewById(R.id.v_card_normal_header);
        tvNormalHaderName = (TextView) NormalHader.findViewById(R.id.tv_card_name);
        TopicHander = itemView.findViewById(R.id.v_card_topic_header);
        tvTopicHanderName = (TextView) TopicHander.findViewById(R.id.tv_card_name);
        ivTopicHanderBackGround = (ImageView) TopicHander.findViewById(R.id.iv_background);
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(appContext, LinearLayoutManager.VERTICAL, false));
    }


    @Override
    public void onBindView(FloorMapBean floorMapBean) {

        if (1 == floorMapBean.getIsShowTitle() && 0 == floorMapBean.getIsShowTitlePic()) {
            NormalHader.setVisibility(View.VISIBLE);
            TopicHander.setVisibility(View.GONE);
            tvNormalHaderName.setText(floorMapBean.getFloorTitle());

        } else if (1 == floorMapBean.getIsShowTitle() && 1 == floorMapBean.getIsShowTitlePic()) {
            NormalHader.setVisibility(View.GONE);
            TopicHander.setVisibility(View.VISIBLE);
            String picTitleUrl = floorMapBean.getTitlePicUrl();
            String picTitleColor = floorMapBean.getTitlePicColor();

            Glide.with(appContext).load(picTitleUrl).centerCrop().into(ivTopicHanderBackGround);
            if (!TextUtils.isEmpty(picTitleColor)) {
                tvTopicHanderName.setTextColor(Color.parseColor(picTitleColor));
            }
            tvTopicHanderName.setText(floorMapBean.getFloorTitle());

        } else {
            NormalHader.setVisibility(View.GONE);
            TopicHander.setVisibility(View.GONE);
        }

        FloorListCardItemAdapter mFloorListCardItemAdapter = new FloorListCardItemAdapter(appContext, floorMapBean);
        mRecyclerView.setAdapter(mFloorListCardItemAdapter);

    }
}
