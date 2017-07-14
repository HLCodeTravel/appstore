package net.bat.store.ux.acticivty;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import net.bat.store.R;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.ux.fragment.ImageDetailFragment;
import net.bat.store.widget.HackyViewPager;

/**
 * Created by bingbing.li on 2017/1/12.
 */

public class ImagePageActivity extends AppCompatActivity {

    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    private String[] imageIds;
    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            getWindow().setStatusBarColor(Color.BLACK);
        }


        setContentView(R.layout.activity_image_page);
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        imageIds = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        mPager = (HackyViewPager) findViewById(R.id.pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(
                getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);

        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
                .getAdapter().getCount());
        indicator.setText(text);
        // 更新下标
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }


    private class ImagePagerAdapter extends FragmentStatePagerAdapter {


        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return imageIds.length;
        }

        @Override
        public Fragment getItem(int position) {
            String url = imageIds[position];
            return ImageDetailFragment.newInstance(url);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Bundle param= new Bundle();
        param.putString("Action", "BackButton");
        FirebaseStat.logEvent("ImagePageBack", param);
    }
    @Override
    protected void onResume() {
        super.onResume();
        FirebaseStat.setCurrentScreen(this,"ImagePageActivity",null);
    }
}
