package net.bat.store.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.bat.store.R;


/**
 * 应用更新说明控件
 * @author hogan
 *
 */
public class UpdateDescriptionView extends RelativeLayout {
	//更新说明View
    private View mUpdateDescriptionView;
    //箭头图标区域Layout
    private LinearLayout mDescriptionCtrlBtnLayout;
    //箭头图标
    private ImageView mDescriptionCtrlBtn;
    //更新内容TextView
    private TextView mDescriptionContentTextView;
    //是否显示所有更新说明
    private boolean mIsSeeAllDescription = false;
    //状态变化监听器
    private OnChangedListener mChangedListener;
    
    public UpdateDescriptionView(Context context) {
        super(context);
        //初始化View
        initView(context);
    }
    public UpdateDescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化View
        initView(context);
    }
    /**
     * 初始化View
     */
    private void initView(Context context) {
    	 LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         mUpdateDescriptionView = inflater.inflate(R.layout.appcenter_update_description_layout, this);
         //箭头图标区域Layout
         mDescriptionCtrlBtnLayout = (LinearLayout) mUpdateDescriptionView.findViewById(R.id.description_ctrl_btn_layout);
         //箭头图标
         mDescriptionCtrlBtn = (ImageView) mUpdateDescriptionView.findViewById(R.id.description_ctrl_btn);
         //更新内容TextView
         mDescriptionContentTextView = (TextView) mUpdateDescriptionView.findViewById(R.id.update_description_content_textview);
         
         mDescriptionCtrlBtnLayout.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
            	 //改变显示状态
            	 changedShowState();
             }
         });
    }
    /**
     * 改变显示状态
     */
    public void changedShowState() {
    	if (mDescriptionCtrlBtn == null || mDescriptionContentTextView == null) {
    		return;
    	}
    	if (mIsSeeAllDescription) {
    		//收起操作
          	mDescriptionCtrlBtn.setImageResource(R.drawable.appcenter_manager_update_see_all_down_icon);
          	//设置显示行数
          	mDescriptionContentTextView.setMaxLines(2);
          } else {
          	//打开操作
          	mDescriptionCtrlBtn.setImageResource(R.drawable.appcenter_manager_update_see_all_up_icon);
          	//设置显示行数
          	mDescriptionContentTextView.setMaxLines(Integer.MAX_VALUE);
          }
          mIsSeeAllDescription = !mIsSeeAllDescription;
          //状态变化
          if (mChangedListener != null) {
        	  mChangedListener.onChanged(mIsSeeAllDescription, toString(getTag()));
          }
    }
    /**
     * 打开或收起操作
     * @param isOpen 是否打开
     */
    public void openOrClose(boolean isOpen) {
    	if (isOpen) {
    		//打开操作
          	mDescriptionCtrlBtn.setImageResource(R.drawable.appcenter_manager_update_see_all_up_icon);
          	//设置显示行数
          	mDescriptionContentTextView.setMaxLines(Integer.MAX_VALUE);
    	} else {
    		//收起操作
          	mDescriptionCtrlBtn.setImageResource(R.drawable.appcenter_manager_update_see_all_down_icon);
          	//设置显示行数
          	mDescriptionContentTextView.setMaxLines(2);
    	}
    	mIsSeeAllDescription = isOpen;
    }
    /**
     * 设置显示内容
     * @param contentText
     */
    public void setDescriptionContentText(CharSequence contentText) {
        if (mDescriptionContentTextView != null) {
        	mDescriptionContentTextView.setText(contentText);
        }
    }
    /**
     * 设置显示内容
     * @param titleText
     */
    public void setDescriptionContentText(String titleText) {
        if (mDescriptionContentTextView != null) {
        	mDescriptionContentTextView.setText(titleText);
        }
    }
    /**
     * 设置显示内容
     * @param titleTextResId
     */
    public void setDescriptionContentText(int titleTextResId) {
        if (mDescriptionContentTextView != null) {
        	mDescriptionContentTextView.setText(titleTextResId);
        }
    }
    /**
     * 设置状态变化监听
     * @param changedListener
     */
    public void setChangedListener(OnChangedListener changedListener) {
		this.mChangedListener = changedListener;
	}

	/**
     * 状态变化监听器
     * @author hogan
     *
     */
    public interface OnChangedListener {
    	/**
    	 * 状态变化
    	 * @param isOpen 是否打开状态
    	 * @param key 标识
    	 */
    	public void onChanged(boolean isOpen, String key);
    }

    private String toString(Object obj){
        if (obj == null) {
            obj = "";
        }
        return obj.toString().trim();
    }
}
