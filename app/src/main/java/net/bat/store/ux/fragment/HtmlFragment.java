package net.bat.store.ux.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bat.store.R;
import net.bat.store.bean.group.PageGroupMapBean;
import net.bat.store.bean.group.PageMapBean;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.adapter.CommonAdapter;

import java.util.List;

/**
 * Created by bingbing.li on 2017/1/21.
 */

public class HtmlFragment extends BaseFragment {

    public static HtmlFragment newInstance(PageGroupMapBean pageGroupMapBean, String itemDesc) {
        HtmlFragment fragment = new HtmlFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", pageGroupMapBean);
        bundle.putSerializable("name",itemDesc);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void bindData(PageGroupMapBean pageGroupMapBean) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_html, container, false);
        Bundle bundle = getArguments();
        final PageGroupMapBean pageGroupMapBean = (PageGroupMapBean) bundle.getSerializable("data");
        String flag=(String)bundle.getSerializable("name");
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.vp_html);
        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tbl_game);

        final List<PageMapBean> pageMapBeanList=pageGroupMapBean.getPageMapList();
        if(pageMapBeanList!=null&&pageMapBeanList.size()==1){
            root.findViewById(R.id.card_view).setVisibility(View.GONE);
        }else if(Build.VERSION.SDK_INT<=19){
            root.findViewById(R.id.app_bar).setBackgroundResource(R.color.appcenter_main_fragment_backgroud);
        }

        CommonAdapter adapter = new CommonAdapter(getChildFragmentManager(), getContext(), pageMapBeanList, R.id.vp_html,flag,pageGroupMapBean.getGroupNameEn());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                UploadLogManager.getInstance().generateUseroperationlog(getContext(),2,pageGroupMapBean.getGroupNameEn(),pageMapBeanList.get(position).getSecondaryMenuNameEn(),"","","");
            }

            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

