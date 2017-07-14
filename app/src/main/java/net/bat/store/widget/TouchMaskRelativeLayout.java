package net.bat.store.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import net.bat.store.MaskManager;

/**
 * 带有遮照效果的RelativeLayout
 * @author hogan
 * @add by zhuotao
 *
 */
public class TouchMaskRelativeLayout extends RelativeLayout {

	private MaskManager mMarskManager;

	public TouchMaskRelativeLayout(Context context) {
		super(context);
		mMarskManager = new MaskManager(this);
	}

	public TouchMaskRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mMarskManager = new MaskManager(this);
		if (null != attrs) {
			mMarskManager.createMarskDrawable(context, attrs);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (null != mMarskManager) {
			mMarskManager.draw(canvas);
		}
	}

	@Override
	public void refreshDrawableState() {
		super.refreshDrawableState();
		if (null != mMarskManager) {
			mMarskManager.refreshDrawableState();
		}
	}

	public void setMaskDrable(Drawable drawable) {
		mMarskManager.setMaskDrable(drawable);
	}
	
	public void onDestroy() {
		if (mMarskManager != null) {
			mMarskManager.onDestroy();
		}
	}

}

