package net.bat.store;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import net.bat.store.R;


/**
 * 遮罩绘制处理类
 * @author hogan
 *
 */
public class MaskManager {

	private View mView;

	private Drawable mMaskDrable;

	private Paint mPaint;

	public MaskManager(View mView) {
		this.mView = mView;
		//mView.setClickable(true);
	}

	public void createMarskDrawable(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppCenterTouchMaskLayout);
		mMaskDrable = a.getDrawable(R.styleable.AppCenterTouchMaskLayout_AppCenterMaskDrawable);
		if (null != mMaskDrable) {
			if (mMaskDrable instanceof ColorDrawable) {
				int color = a.getColor(R.styleable.AppCenterTouchMaskLayout_AppCenterMaskDrawable,
						android.R.color.transparent);
				mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				mPaint.setColor(color);
				mPaint.setStyle(Style.FILL);
			}
		}
		a.recycle();
	}

	public void draw(Canvas canvas) {
		
		
		
		if (mMaskDrable != null && mView.isPressed() && mView.isEnabled()) {
			
			if (mMaskDrable instanceof ColorDrawable) {
				canvas.drawRect(mView.getPaddingEnd(), mView.getPaddingTop(),
						mView.getMeasuredWidth() - mView.getPaddingEnd(),
						mView.getMeasuredHeight() - mView.getPaddingBottom(), mPaint);
			} else {
//				mMaskDrable.setBounds(mView.getPaddingRight(), mView.getPaddingTop(),
//						mView.getMeasuredWidth() - mView.getPaddingRight(),
//						mView.getMeasuredHeight() - mView.getPaddingBottom());
				
                mMaskDrable.setBounds(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
				mMaskDrable.draw(canvas);
			}
		}
	}

	public void setMaskDrable(Drawable mMaskDrable) {
		this.mMaskDrable = mMaskDrable;
	}

	public void refreshDrawableState() {
		mView.invalidate();
	}
	
	public void onDestroy() {
		mMaskDrable = null;
	}
}

