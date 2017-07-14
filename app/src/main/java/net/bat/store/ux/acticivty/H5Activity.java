package net.bat.store.ux.acticivty;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.view.ProgressWebView;

/**
 * Created by bingbing.li on 2017/2/9.
 */

public class H5Activity extends AppCompatActivity{

    private static final String TAG = "H5Activity";
    public static final String AD_URL = "h5_url";
    private ProgressWebView mWebView;
    private String mUrl;
    private ImageView mIvLoading;
    private boolean isOnPause;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        initView();
        loadData();
    }
    private void initView() {
        setContentView(R.layout.activity_h5_layout);
        mIvLoading= (ImageView) findViewById(R.id.loading);
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.web_layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        mWebView = new ProgressWebView(getApplicationContext(),this);
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);
        Glide.with(BaseApplication.getContext()).load(R.drawable.loading)
                .asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mIvLoading);
    }

    private void loadData() {
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        mUrl=bundle.getString("Url",null);
        if(!TextUtils.isEmpty(mUrl)){
            Bundle param1 = new Bundle();
            param1.putString("Action", "TopicAction");
            FirebaseStat.logEvent(bundle.getString("style"), param1);
            UploadLogManager.getInstance().generateResourceLog(this,6,bundle.getString("title",""),0,1,"","");
            mWebView.setWebChromeClient(webChromeClient);
            mWebView.loadUrl(mUrl);
        }else{
            mUrl = intent.getStringExtra(AD_URL);
            if (TextUtils.isEmpty(mUrl)) {
                finish();
            }
            mWebView.setWebChromeClient(webChromeClient);
            mWebView.loadUrl(mUrl);
        }
    }

    protected void onPause() {
        super.onPause();
        try {
            if (mWebView != null) {
                mWebView.getClass().getMethod("onPause").invoke(mWebView, (Object[]) null);
                isOnPause = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView = null;
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        isOnPause = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            }else{
                mWebView.clearHistory();
                mWebView.loadUrl(mUrl);
                H5Activity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged: ");
    }

    public static boolean openLink(Context context, String url) {
        if (context != null && !TextUtils.isEmpty(url)) {
            Intent intent = new Intent(context, H5Activity.class);
            intent.putExtra(AD_URL, url);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseStat.setCurrentScreen(this,"H5Activity",null);
        try {
            if (isOnPause) {
                if (mWebView != null) {
                    mWebView.getClass().getMethod("onResume").invoke(mWebView, (Object[]) null);
                }
                isOnPause = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    WebChromeClient webChromeClient = new WebChromeClient() {

        // 扩充数据库的容量（在WebChromeClinet中实现）
        @Override
        public void onExceededDatabaseQuota(String url,
                                            String databaseIdentifier, long currentQuota,
                                            long estimatedSize, long totalUsedQuota,
                                            WebStorage.QuotaUpdater quotaUpdater) {

            quotaUpdater.updateQuota(estimatedSize * 2);
        }

        // 扩充缓存的容量
        @Override
        public void onReachedMaxAppCacheSize(long spaceNeeded,
                                             long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {

            quotaUpdater.updateQuota(spaceNeeded * 2);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }


        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if(mWebView!=null){
                mWebView.setVisibility(View.GONE);
            }
            mIvLoading.setVisibility(View.VISIBLE);
            if (newProgress >30){
                if(mWebView!=null){
                    mWebView.setVisibility(View.VISIBLE);
                }
                mIvLoading.setVisibility(View.GONE);
            }
        }
    };
}
