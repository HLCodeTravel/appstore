package net.bat.store.ux.acticivty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transsion.iad.core.TAdNative;
import com.transsion.iad.core.TNativeAdView;
import com.transsion.iad.core.bean.TAdNativeInfo;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.service.AppCenterNotifyService;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by bingbing.li on 2017/3/14.
 */

public class AdDetailActivity extends AppCompatActivity {

    private static final String TAG = "AdDetailActivity";
    private ImageView iv_top_banner;
    private TextView tv_grade_score;
    private TextView tv_grade_num;
    private ImageView iv_category;
    private TextView tv_category;
    private TextView tv_download_num;
    private RatingBar rb_score;
    private TextView tv_single_line_brief;
    private TextView tv_app_size;
    private TextView tv_app_name;
    private ImageView iv_app_icon;
    private TextView tv_app_version;
    private Button btn_download;
    private Context mContext;
    private TAdNativeInfo mNativeInfo;
    private TAdNative mAdNative;
    private TNativeAdView mNativeAdView;
    private String str;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_viewpager_item);
        str=getIntent().getStringExtra("flag");
        if(!TextUtils.isEmpty(str)){
            Bundle param1 = new Bundle();
            param1.putString("Action", "ToAd");
            FirebaseStat.logEvent("AdDetailClick", param1);
        }
        mNativeAdView= (TNativeAdView) LayoutInflater.from(this).inflate(R.layout.activity_ad_detail,null);
        mContext = BaseApplication.getContext();
        initView();
        initData();
    }

    private void initView(){
        mNativeAdView.findViewById(R.id.hs_screen_shots).setVisibility(View.INVISIBLE);
        mNativeAdView.findViewById(R.id.ll_shorts).setVisibility(View.INVISIBLE);
        iv_top_banner= (ImageView)mNativeAdView.findViewById(R.id.iv_top_banner);
        tv_grade_score= (TextView)mNativeAdView.findViewById(R.id.tv_grade_score);
        tv_grade_num= (TextView)mNativeAdView.findViewById(R.id.tv_grade_num);
        tv_app_name= (TextView)mNativeAdView.findViewById(R.id.tv_app_name);
        iv_app_icon= (ImageView)mNativeAdView.findViewById(R.id.iv_app_icon);
        iv_category= (ImageView)mNativeAdView.findViewById(R.id.iv_category);
        tv_single_line_brief= (TextView)mNativeAdView.findViewById(R.id.tv_single_line_brief);
        tv_app_version= (TextView)mNativeAdView.findViewById(R.id.tv_app_version);
        tv_category= (TextView)mNativeAdView.findViewById(R.id.tv_category);
        tv_app_size= (TextView)mNativeAdView.findViewById(R.id.tv_app_size);
        tv_download_num= (TextView)mNativeAdView.findViewById(R.id.tv_download_num);
        btn_download= (Button) mNativeAdView.findViewById(R.id.btn_download);
        rb_score= (RatingBar) mNativeAdView.findViewById(R.id.rb_score);
        initVersion();
        initSize();
        initDownloads();
        initGrad();
        iv_category.setBackgroundResource(R.mipmap.ad_label);
        tv_category.setText("AD");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initGrad(){
        int max=5;
        int min=3;
        Random random = new Random();
        int version_first = random.nextInt(max)%(max-min+1) + min;
        tv_grade_score.setText(version_first+"");
        rb_score.setRating(version_first);
    }

    private void initDownloads(){
        int max=5999;
        int min=199;
        Random random = new Random();
        int version_first = random.nextInt(max)%(max-min+1) + min;
        tv_download_num.setText(version_first+"");
        tv_grade_num.setText(version_first+"");
    }

    private void initSize(){
        double max=1.10;
        double min=6.80;
        Random random = new Random();
        double size=min + ((max - min) * random.nextDouble());
        DecimalFormat df = new DecimalFormat("#.00");
        tv_app_size.setText(df.format(size)+" MB");
    }

    private void initVersion(){
        int max=5;
        int min=1;
        Random random = new Random();
        int version_first = random.nextInt(max)%(max-min+1) + min;
        max=12;
        int version_second = random.nextInt(max)%(max-min+1) + min;
        max=40;
        int version_third = random.nextInt(max)%(max-min+1) + min;
        tv_app_version.setText(version_first+"."+version_second+"."+version_third);
    }

    private void initData(){
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mNativeInfo=AppCenterNotifyService.mNativeInfo;
        mAdNative=AppCenterNotifyService.mAdNative;
        if(mNativeInfo==null){
            Log.i(TAG, "initData: null");
            AdDetailActivity.this.finish();
            startActivity(new Intent(this, AhaMainActivity.class));
            return;
        }
        tv_app_name.setText(mNativeInfo.getTitle());
        actionBar.setTitle(mNativeInfo.getTitle());
        iv_app_icon.setImageBitmap(AppCenterNotifyService.mBitmap);
        tv_single_line_brief.setText(mNativeInfo.getDescription());
        btn_download.setText(mNativeInfo.getAdCallToAction());

        LinearLayout view= (LinearLayout) findViewById(R.id.root);
        mNativeAdView.addNativeAdView(view,mNativeInfo);
        mNativeAdView.setCallToActionView(btn_download);
        List<View> viewList=new ArrayList<>();
        viewList.add(btn_download);
        mAdNative.registerViews(mNativeAdView,viewList);
        Glide.with(mContext).load(mNativeInfo.getImage().getUrl()).into(iv_top_banner);
        UploadLogManager.getInstance().generateResourceLog(mContext,6,mNativeInfo.getTitle(),mNativeInfo.getFlag(),3,"","");
    }

}
