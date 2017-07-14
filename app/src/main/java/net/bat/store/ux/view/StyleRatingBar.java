package net.bat.store.ux.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.bat.store.R;


/**
 * custom style rating bar
 * custom star image, star size, star space, controllable
 * TODO: step set, animation, null check, default styles, set range, set padding
 *
 * @author cheng.qian.
 * @date 2016/9/23.
 * ============================================
 * Copyright (c) 2016 TRANSSION.Co.Ltd.
 * All right reserved.
 **/


public class StyleRatingBar extends View {
    private int starInterval = 0;
    private int starSize = 16;
    private int starCount = 5;
    private float rating = 0.0F;
    private boolean controllable = false;
    private Bitmap starFillBitmap;
    private Drawable starEmptyDrawable;
    private Paint paint;

    public StyleRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * init StyleRatingBar, get attrs, init paint
     *
     * @by cheng.qian on 2016/9/23
     */
    private void init(Context context, AttributeSet attrs) {
        setClickable(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StyleRatingBar);
        starInterval = (int) typedArray.getDimension(R.styleable.StyleRatingBar_starInterval, 0);
        starSize = (int) typedArray.getDimension(R.styleable.StyleRatingBar_starSize, 16);
        starCount = typedArray.getInt(R.styleable.StyleRatingBar_starCount, 5);
        rating = typedArray.getFloat(R.styleable.StyleRatingBar_rating, 0.0F);
        rating = Math.round(rating * 10) * 1.0f / 10; // set precision to 0.1
        controllable = typedArray.getBoolean(R.styleable.StyleRatingBar_controllable, false);
        starFillBitmap = drawableToBitmap(typedArray.getDrawable(R.styleable.StyleRatingBar_starFillImg));
        starEmptyDrawable = typedArray.getDrawable(R.styleable.StyleRatingBar_starEmptyImg);
        typedArray.recycle();

        // init Paint
        paint = new Paint();
        paint.setAntiAlias(true); // smooth out shape
        // set bitmap as a texture
        paint.setShader(new BitmapShader(starFillBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
    }

    interface OnStarChangeListener {
        void onStarChange(float mark);
    }

    /**
     * set rating mark and update the UI
     *
     * @by cheng.qian on 2016/9/23
     */
    public void setRating(float mark) {
        rating = Math.round(mark * 10) * 1.0f / 10; // set precision to 0.1
        // update UI as long as rating value has been set
        invalidate();
    }

    public float getRating() {
        return rating;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(starCount * (starSize + starInterval) - starInterval, starSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (starFillBitmap == null || starEmptyDrawable == null) {
            //TODO new default bitmap and drawable
            return;
        }
        // firstly, draw empty stars
        for (int i = 0; i < starCount; i++) {
            int left = i * (starInterval + starSize);
            int top = 0;
            int right = left + starSize;
            int bottom = starSize;
            starEmptyDrawable.setBounds(left, top, right, bottom);
            starEmptyDrawable.draw(canvas);
        }
        super.invalidate();
        // draw filled stars
        for (int i = 1; i <= rating; i++) {
            canvas.drawRect(0, 0, starSize, starSize, paint);
            canvas.translate(starInterval + starSize, 0);
        }
        // draw incomplete stars
        canvas.drawRect(0, 0, starSize * (rating - (int) rating), starSize, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (controllable) {
            int x = (int) event.getX();
            if (x < 0) {
                x = 0;
            }
            int measuredWidth = getMeasuredWidth();
            if (x > measuredWidth) {
                x = measuredWidth;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setRating(x * 1.0f / measuredWidth * starCount);
                    break;
                case MotionEvent.ACTION_MOVE:
                    setRating(x * 1.0f / measuredWidth * starCount);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }


    /**
     * drawable To Bitmap
     *
     * @by cheng.qian on 2016/9/23
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(starSize, starSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, starSize, starSize);
        drawable.draw(canvas);
        return bitmap;
    }
}
