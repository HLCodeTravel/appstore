package net.bat.store.ux.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.bat.store.bean.group.PageMapBean;
import net.bat.store.constans.NetworkConstant;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.ux.fragment.RecommendFragment;
import net.bat.store.ux.fragment.SortsFragment;

import java.util.List;

/**
 * Created by bingbing.li on 2017/1/21.
 */

public class CommonAdapter extends FragmentPagerAdapter{

    private static final String TAG = "CommonAdapter";
    private static final int LIMIT_TAB_NUMBERS=3;
    private final int TYPE_PAGE_DETAIL=0;
    private final int TYPE_APP_CATEGORY=1;
    private final String Flag;
    private Context context;
    private List<PageMapBean> pageMapList;
    private int viewpage_id;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurfragmentTransaction ;
    private Fragment mCurrentPrimaryItem;
    private String mGroupNameEn;

    public CommonAdapter(FragmentManager fm, Context context, List<PageMapBean> pageMapList,int id,String flag,String grounameEn) {
        super(fm);
        this.context = context;
        this.pageMapList=pageMapList;
        mCurfragmentTransaction=fm.beginTransaction();
        mFragmentManager=fm;
        viewpage_id=id;
        this.Flag=flag;
        this.mGroupNameEn=grounameEn;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==LIMIT_TAB_NUMBERS){
            return null;
        }
        PageMapBean pageMapBean = pageMapList.get(position);
        int type = getPageType(pageMapBean);
        if (type == TYPE_APP_CATEGORY) {
            Log.i(TAG, "getItem: TYPE_APP_CATEGORY");
            Bundle param = new Bundle();
            param.putString("Action", "TYPE_APP_CATEGORY");
            FirebaseStat.logEvent("page_"+Flag+"_CATEGORY", param);
            return getSortFragment(position);
        } else {
            Log.i(TAG, "getItem: REc");
            return getRecFragment(position,Flag);
        }
    }

    public RecommendFragment getRecFragment(int position, String flag){
        Log.i(TAG, "getRecFragment: ");
        PageMapBean pageMapBean=pageMapList.get(position);
        if(mCurfragmentTransaction!=null){
            RecommendFragment recommendFragment = (RecommendFragment) mFragmentManager
                    .findFragmentByTag(makeFragmentName(getItemId(position)));
            if(recommendFragment==null){
                Log.i(TAG, "getRecFragment: oncreate");
                recommendFragment=new RecommendFragment(mGroupNameEn);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",pageMapBean);
                recommendFragment.setArguments(bundle);
            }else{
                Log.i(TAG, "getRecFragment: not oncreta");
            }
            return recommendFragment;
        }
        return null;
    }

    public SortsFragment getSortFragment(int position){
        Log.i(TAG, "getSortFragment: ");
        PageMapBean pageMapBean=pageMapList.get(position);
        if(mCurfragmentTransaction!=null){
            SortsFragment sortsFragment = (SortsFragment) mFragmentManager
                    .findFragmentByTag(makeFragmentName(getItemId(position)));
            if(sortsFragment==null){
                Log.i(TAG, "getSortFragment: oncreate");
                sortsFragment=new SortsFragment();
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",pageMapBean);
                bundle.putString("groupname",mGroupNameEn);
                sortsFragment.setArguments(bundle);
            }else{
                Log.i(TAG, "getSortFragment:not oncreate");
            }
            return sortsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        if(pageMapList!=null){
            if(pageMapList.size()>LIMIT_TAB_NUMBERS){
                return LIMIT_TAB_NUMBERS;
            }
            return pageMapList.size();
        }else{
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageMapList.get(position).getSecondaryMenuName();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(position==LIMIT_TAB_NUMBERS){
            return null;
        }
        if (mCurfragmentTransaction == null) {
            mCurfragmentTransaction = mFragmentManager.beginTransaction();
        }
        final long itemId = getItemId(position);
        String name = makeFragmentName(itemId);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if(fragment!=null){
            mCurfragmentTransaction.attach(fragment);
        }else{
            fragment = getItem(position);
            mCurfragmentTransaction.add(container.getId(), fragment,
                    makeFragmentName(itemId));
        }
        if (fragment != mCurrentPrimaryItem) {
                        fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
        return fragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurfragmentTransaction != null) {
            mCurfragmentTransaction.commitAllowingStateLoss();
            mCurfragmentTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurfragmentTransaction == null) {
            mCurfragmentTransaction = mFragmentManager.beginTransaction();
        }
        mCurfragmentTransaction.detach((Fragment)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    private  String makeFragmentName(long id) {
        return "android:switcher:" + viewpage_id + ":" + id;
    }

    public int getPageType(PageMapBean pageMapBean){
        String url=pageMapBean.getPageUrl();
        if(url.contains(NetworkConstant.TYPE_PAGE_DETAIL)){
            return TYPE_PAGE_DETAIL;
        }else if(url.contains(NetworkConstant.TYPE_APP_CAGETORY)){
            return TYPE_APP_CATEGORY;
        }
        return TYPE_PAGE_DETAIL;
    }
}

