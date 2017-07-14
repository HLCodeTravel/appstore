package net.bat.store.ux.view;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

/**
 * Created by bingbing.li on 2017/2/9.
 */

public class ProgressWebView extends WebView {

    private Context mContext;
    private Activity mActivity;

    public ProgressWebView(Context context,Activity activity) {
        super(context);
        mContext = context;
        mActivity=activity;
        init();
    }

    public ProgressWebView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        WebSettings mWebSettings = getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);

        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH); // 设置渲染优先级
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);

        setDownloadListener(new AhaDownloadListener());
        newWin(mWebSettings);
        setWebViewClient(webViewClient);
    }

    /**
     * 多窗口的问题
     */
    private void newWin(WebSettings mWebSettings) {
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    WebViewClient webViewClient = new WebViewClient() {
        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            if (scheme != null && !scheme.startsWith("http")) {
                PackageManager pm = getContext().getPackageManager();
                List<ResolveInfo> lists = pm.queryIntentActivities(new Intent(Intent.ACTION_VIEW, uri), PackageManager.MATCH_DEFAULT_ONLY);
                if (lists != null && lists.size() > 0) {
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);
                        mActivity.finish();
                }
                return true;
            }

            //对google play网址做特殊处理(#孙志刚)
            if (url.startsWith("https://play.google.com/store/apps/")) {
                url = url.replaceAll("https://play.google.com/store/apps/", "market://");
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } catch (Exception e) {

                }
            }
            return false;
        }
    };




    class AhaDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
        }
    }
}
