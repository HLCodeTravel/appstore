package net.bat.store.ux.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.bat.store.R;
import net.bat.store.BaseApplication;

import java.util.List;


/**
 * Created by bingbing.li on 2017/2/10.
 */


public class BottomBar extends LinearLayout {
    private List<BottomItem> items;
    private onSelectedListener listener;
    private int mCurrentIndex = 0;

    public BottomBar(Context context) {
        this(context, null, 0);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    public void setBottomItems(List<BottomItem> items) {
        this.items = items;
        removeAllViews();
        if (items != null) {
            setBackgroundResource(R.color.color_google_play);
            final int item_size=items.size();
            for (int n = 0; n < item_size; n++) {
                final int m = n;
                LinearLayout item = (LinearLayout) View.inflate(getContext(), R.layout.item_bottom, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                addView(item, lp);

                updateItem(m);
                item.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCurrentIndex == m || m>=item_size) {
                            return;
                        }

                        if (listener != null) {
                            listener.onSelected(m);
                        }

                        int last = mCurrentIndex;
                        mCurrentIndex = m;
                        updateItem(last);
                        updateItem(mCurrentIndex);
                    }
                });
            }
        }
    }

    private void updateItem(int index) {
        View childView=getChildAt(index);
        if(childView==null){
            return;
        }
        ImageView icon = (ImageView) childView.findViewById(R.id.iv_bottom_icon);
        TextView desc = ((TextView) childView.findViewById(R.id.tv_bottom_desc));

        icon.setImageBitmap(null);
        if (mCurrentIndex == index) {
            Glide.with(BaseApplication.getContext()).load(items.get(index).getSelectedIconUrl()).centerCrop().into(icon);
            desc.setAlpha(1.0f);
        } else {
            Glide.with(BaseApplication.getContext()).load(items.get(index).getNormalIconUrl()).centerCrop().into(icon);
            desc.setAlpha(0.4f);
        }
        desc.setText(items.get(index).getItemDesc());
    }

    public void setListener(onSelectedListener listener) {
            this.listener = listener;
    }

    public static class BottomItem {
        private String normalIconUrl;
        private String selectedIconUrl;
        private String itemDesc;

        public BottomItem(String normalIconUrl, String selectedIconUrl, String itemDesc) {
            this.normalIconUrl = normalIconUrl;
            this.selectedIconUrl = selectedIconUrl;
            this.itemDesc = itemDesc;
        }

        public String getNormalIconUrl() {
            return normalIconUrl;
        }

        public String getSelectedIconUrl() {
            return selectedIconUrl;
        }

        public String getItemDesc() {
            return itemDesc;
        }
    }

    public interface onSelectedListener {
        void onSelected(int index);
    }
}
