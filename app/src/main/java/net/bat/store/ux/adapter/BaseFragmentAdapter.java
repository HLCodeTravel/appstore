package net.bat.store.ux.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.bat.store.BaseApplication;
import net.bat.store.R;
import net.bat.store.bean.group.PageGroupMapBean;
import net.bat.store.ux.fragment.ApplicationFragment;
import net.bat.store.ux.fragment.BaseFragment;
import net.bat.store.ux.fragment.GameFragment;
import net.bat.store.ux.fragment.HtmlFragment;
import net.bat.store.ux.fragment.SelectionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * usage
 *
 * @author 俞剑兵.
 * @data 2017/7/7.
 * ======================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */

public class BaseFragmentAdapter extends FragmentStatePagerAdapter {
    private FragmentManager mFragmentManager;
    private List<PageGroupMapBean> mPageGroupMapBeans;
    private List<BaseFragment> mFragmentList = new ArrayList<BaseFragment>();
    private Context mContext;

    public BaseFragmentAdapter(FragmentManager fm, List<PageGroupMapBean> pageGroupMapBeans, Context context) {
        super(fm);
        mFragmentManager = fm;
        mPageGroupMapBeans = pageGroupMapBeans;
        mContext = context;
    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_bottom, null);
        TextView textView = (TextView) v.findViewById(R.id.tv_bottom_desc);
        ImageView imageView = (ImageView) v.findViewById(R.id.iv_bottom_icon);

        String normalIconUrl = mPageGroupMapBeans.get(position).getIconUrl();
        String groupName =  mPageGroupMapBeans.get(position).getGroupName();
        textView.setText(groupName);
        //textView.setAlpha(0.4f);
        textView.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.bottom_title_unclick_color));
        Glide.with(BaseApplication.getContext()).load(normalIconUrl).centerCrop().into(imageView);
        return v;
    }


    public List<PageGroupMapBean> getDatas(){
        return mPageGroupMapBeans;
    }

    public void clearFragments(){
        if(mFragmentList != null) {
            mFragmentList.clear();
        }
    }

    @Override
    public int getCount() {
        return mPageGroupMapBeans == null ? 0 : mPageGroupMapBeans.size();
    }


    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public BaseFragment getItem(int position) {
        BaseFragment fragment = null;
        if(position > mFragmentList.size() -1) {  //如果 Fragment List里面没有该Position的Fragment，则创建
            PageGroupMapBean pageGroupMapBean = mPageGroupMapBeans.get(position);
            switch (position) {
                case 0:
                    SelectionFragment selectionFragment = SelectionFragment.newInstance();
//                    selectionFragment.bindData(pageGroupMapBean);
                    fragment = selectionFragment;
                    break;
                case 1:
                    fragment = GameFragment.newInstance(pageGroupMapBean,pageGroupMapBean.getGroupId());
                    break;
                case 2:
                    fragment = ApplicationFragment.newInstance(pageGroupMapBean,pageGroupMapBean.getGroupId());
                    break;
                case 3:
                    fragment = HtmlFragment.newInstance(pageGroupMapBean,pageGroupMapBean.getGroupId());
                    break;
            }
            mFragmentList.add(fragment);
        }
        else {
            if (position >= 0 && position < mFragmentList.size()){
                fragment = mFragmentList.get(position);
            }
        }
        return fragment;
    }

    /**
     * Fragment 绑定数据
     * */
    public void onFragmentBindData(int position){
        if (position >= 0 && position < mFragmentList.size()) {
            if (position < mPageGroupMapBeans.size()) {
                mFragmentList.get(position).bindData(mPageGroupMapBeans.get(position));
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
