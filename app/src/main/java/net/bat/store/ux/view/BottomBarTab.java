package net.bat.store.ux.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.bat.store.R;


/**
 * usage
 *
 * @author cheng.qian.
 * @date 2016/9/26.
 * ============================================
 * Copyright (c) 2016 TRANSSION.Co.Ltd.
 * All right reserved.
 **/


public class BottomBarTab extends LinearLayout {
    public int getSelectedColor() {
        return selectedColor;
    }

    private Drawable selectedIcon;
    private Drawable unselectedIcon;
    private boolean isSelected;

    private int selectedColor;

    public BottomBarTab(Context context) {
        super(context);
    }

    public BottomBarTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    protected void init(Context context,AttributeSet attrs) {
        this.setOrientation(VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomBarTab);
        selectedColor = typedArray.getInt(R.styleable.BottomBarTab_selectedColor, 0);
        selectedIcon = typedArray.getDrawable(R.styleable.BottomBarTab_selectedIcon);
        unselectedIcon = typedArray.getDrawable(R.styleable.BottomBarTab_unselectedIcon);
        typedArray.recycle();
        Log.d("bbt", "init: " + selectedColor);
    }

    public void setIconSelected() {
        try {
            ImageView iconView = (ImageView) this.getChildAt(0);
            iconView.setImageDrawable(selectedIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIconUnselected() {
        try {
            ImageView iconView = (ImageView) this.getChildAt(0);
            iconView.setImageDrawable(unselectedIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIconSelected(Drawable selectedIcon) {
        try {
            ImageView iconView = (ImageView) this.getChildAt(0);
            iconView.setImageDrawable(selectedIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIconUnselected(Drawable unselectedIcon) {
        try {
            ImageView iconView = (ImageView) this.getChildAt(0);
            iconView.setImageDrawable(unselectedIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
