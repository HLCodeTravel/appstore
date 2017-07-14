package net.bat.store.ux.acticivty;
/* Top Secret */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * app详情页滚动
 *
 * @author 孙志刚.
 * @date 2017/2/14.
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */
public class DetailScrollView extends ScrollView {
    onScrollListener listener;

    public DetailScrollView(Context context) {
        super(context);
    }

    public DetailScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (listener != null) {
            listener.onScroll(getScrollY());
        }
    }

    public void setOnScrollListener(onScrollListener listener) {
        this.listener = listener;
    }

    public interface onScrollListener {
        void onScroll(int offset);
    }
}
