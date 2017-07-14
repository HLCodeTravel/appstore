package net.bat.store.widget.floorcard;

/**
 * Created by bingbing.li on 2017/1/23.
 */

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
/**
 * 处理在两个scrollview同时出现的情况
 * @author hogan
 *
 */
public class HorizontalScrollViewForCardView extends HorizontalScrollView {

    Context mContext;
    private int mDownX;
    private int mDownY;

//	private int mTouchSlop;// = ViewConfiguration.get(mContext).getScaledTouchSlop();
//	private int mTouchX;
//	boolean mIsUpOrDOwn = true;

    public HorizontalScrollViewForCardView(Context context, AttributeSet attrs,
                                           int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public HorizontalScrollViewForCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public HorizontalScrollViewForCardView(Context context) {
        super(context);
        mContext = context;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//		mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = (int) ev.getX();
            mDownY = (int) ev.getY();
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            Rect hitRect = new Rect();
            this.getHitRect(hitRect);
            int moveX = (int) ev.getX();
            int moveY = (int) ev.getY();
            if (hitRect.contains(moveX, moveY)) {
                if (Math.abs(moveX - mDownX) > Math.abs(moveY - mDownY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}

