package net.bat.store.ux.adapter.viewholder;

/**
 * Created by liang.he on 2017/7/7.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.bat.store.R;
import net.bat.store.bean.floor.AppMapBean;
import net.bat.store.bean.floor.FloorMapBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.widget.floorcard.HorizontalScrollCardView;
import net.bat.store.widget.floorcard.HorizontalScrollViewForCardView;

import java.util.List;

public class FloorHorizonGridViewHolder extends BaseFloorViewHolder {

    TextView tv_app_title;
    HorizontalScrollViewForCardView horizontalScrollViewForCardView;
    View rl_name;
    View topic_header;
    TextView topic_textview;
    ImageView iv_topic;
    HorizontalScrollCardView horizontalScrollCardView;

    public FloorHorizonGridViewHolder(Context context, View itemView) {
        super(context, itemView);
        tv_app_title = (TextView) itemView.findViewById(R.id.card_name);
        horizontalScrollViewForCardView = (HorizontalScrollViewForCardView) itemView.findViewById(R.id.scroll_background);
        rl_name = itemView.findViewById(R.id.re_title);
        horizontalScrollCardView = (HorizontalScrollCardView) itemView.findViewById(R.id.scrollCardView);
        topic_header = itemView.findViewById(R.id.v_card_topic_header);
        iv_topic = (ImageView) itemView.findViewById(R.id.iv_background);
        topic_textview = (TextView) itemView.findViewById(R.id.tv_card_name);
    }


    @Override
    public void onBindView(FloorMapBean data) {
        final FloorMapBean floorMapBean = data;
        final String card_title = floorMapBean.getFloorTitle();
        if (AppStoreConstant.FLAG_SHOW_TITLE == floorMapBean.getIsShowTitle()) {
            rl_name.setVisibility(View.VISIBLE);
            tv_app_title.setText(card_title);
        } else {
            rl_name.setVisibility(View.GONE);
        }
        String picTitleUrl = floorMapBean.getTitlePicUrl();
        String picTitleColor = floorMapBean.getTitlePicColor();
        if (AppStoreConstant.FLAG_SHOW_TITLE == floorMapBean.getIsShowTitlePic() && !TextUtils.isEmpty(picTitleUrl)) {
            rl_name.setVisibility(View.GONE);
            topic_header.setVisibility(View.VISIBLE);
            Glide.with(appContext).load(picTitleUrl).centerCrop().into(iv_topic);
            if (!TextUtils.isEmpty(picTitleColor)) {
                topic_textview.setTextColor(Color.parseColor(picTitleColor));
            }
            topic_textview.setText(card_title);
        } else {
            if (floorMapBean.getIsShowTitle() == AppStoreConstant.FLAG_SHOW_TITLE) {
                rl_name.setVisibility(View.VISIBLE);
            }
            topic_header.setVisibility(View.GONE);
        }

        Glide.with(appContext).load(floorMapBean.getBackgroundImageUrl()).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                horizontalScrollViewForCardView.setBackground(new BitmapDrawable(resource));
            }
        });
        List<AppMapBean> appMapBeanList = floorMapBean.getAppMapList();
        horizontalScrollCardView.addData(appMapBeanList, floorMapBean.getFloorId());
    }
}