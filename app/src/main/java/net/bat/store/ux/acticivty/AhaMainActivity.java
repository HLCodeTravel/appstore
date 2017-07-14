package net.bat.store.ux.acticivty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.transsion.http.impl.StringCallback;

import net.bat.store.BaseApplication;
import net.bat.store.R;
import net.bat.store.bean.base.BaseBean;
import net.bat.store.bean.group.PageGroupMapBean;
import net.bat.store.bean.group.ResultPageGroupMapBean;
import net.bat.store.bean.update.AppUpdateBean;
import net.bat.store.bean.update.DataBean;
import net.bat.store.constans.AppStoreConstant;
import net.bat.store.download.DownloadNotificationListener;
import net.bat.store.download.DownloadState;
import net.bat.store.download.DownloadTask;
import net.bat.store.download.DownloadTaskManager;
import net.bat.store.download.InstallState;
import net.bat.store.AhaLogCallBack;
import net.bat.store.network.NetworkClient;
import net.bat.store.constans.NetworkConstant;
import net.bat.store.receiver.AppCenterDynamicBroadcastReceiver;
import net.bat.store.service.AppCenterNotifyService;
import net.bat.store.utils.CommonUtils;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.PreferencesUtils;
import net.bat.store.utils.StreamUtils;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.adapter.BaseFragmentAdapter;
import net.bat.store.ux.dataprovider.MainDataProvider;
import net.bat.store.ux.fragment.UpdateDialogFragment;
import net.bat.store.widget.TouchMaskRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class AhaMainActivity extends FragmentActivity implements View.OnClickListener, AppCenterDynamicBroadcastReceiver.AppCenterReceiverListener {

    private static final String TAG = "AhaMainActivity";
    private static final String UPDATE_MD5 = "update_md5";

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View toolbar_search;
    private ImageView iv_slidemenu;
    private TouchMaskRelativeLayout cl_search;
    //侧边栏
    private RelativeLayout rv_install_records;
    private RelativeLayout rv_uninstall;
    private RelativeLayout rv_check_update;
    private RelativeLayout rv_app_update;
    private View mRequestFailView;
    private View mNoNetWorkView;
    private ImageView iv_update_flag;
    private List<PageGroupMapBean> mPageGroupMapList;
    private BaseBean<ResultPageGroupMapBean> mNetWorkBaseBean;
    private BaseBean<ResultPageGroupMapBean> mLocalBaseBean;
    private boolean mIsNetworkAvailable;
    private Context mContext;
    private ImageView mLoadingProgress;
    private MainDataProvider mDataProvider;

    private StringCallback requestUpdateListener =  new StringCallback() {
        @Override
        public void onFailure(int i, String s, Throwable throwable) {

        }

        @Override
        public void onSuccess(int code, String ResponseCode) {
            if(200 == code){
                Gson gson = new Gson();
                try {
                    final AppUpdateBean result = gson.fromJson(ResponseCode, AppUpdateBean.class);
                    if (result != null && result.getStatus() == 1) {
                        findViewById(R.id.main_titlebar_side_layout_red_point).setVisibility(View.VISIBLE);
                        iv_update_flag.setVisibility(View.VISIBLE);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(UpdateDialogFragment.DIALOG_BACK, false);
                        bundle.putInt(UpdateDialogFragment.DIALOG_TYPE, 1);
                        bundle.putString(UpdateDialogFragment.DIALOG_TITLE, getString(R.string.check_update));
                        bundle.putString(UpdateDialogFragment.DIALOG_MSG, getString(R.string.get_new_version));
                        final UpdateDialogFragment fragment = UpdateDialogFragment.newInstance(UpdateDialogFragment.class, bundle);
                        fragment.setOnClickCancleDialogListener(new UpdateDialogFragment.OnClickCancleDialogListener() {
                            @Override
                            public void onClick(View view) {
                                fragment.dismiss();
                            }
                        });
                        fragment.setOnClickConfirmDialogListener(new UpdateDialogFragment.OnClickConfirmDialogListener() {
                            @Override
                            public void onClick(View view) {
                                fragment.dismiss();
                                boolean isDownloaded = false;
                                DataBean dataBean = result.getDataBean();
                                UploadLogManager.getInstance().generateAhaUpdateLog(mContext, dataBean.getVersionName(), 1);
                                String appLabel = BaseApplication.getContext().getPackageManager().getApplicationLabel(BaseApplication.getContext().getApplicationInfo()).toString();
                                DownloadTask task = new DownloadTask(dataBean.getDownloadPackageUrl() + "?md5=" + dataBean.getMd5(), dataBean.getPackageName(), ConstantUtils.getDownloadPath(),
                                        appLabel + ".apk", appLabel, null, null, null, null, 0, null, null, null);

//                        DownloadTask task = new DownloadTask(dataBean.getDownloadPackageUrl(),dataBean.getPackageName(), ConstantUtils.getDownloadPath(),
//                                appLabel,appLabel,null,null,null,null,0,null);
                                if (CommonUtils.getDwonloadTask().indexOf(task) != -1) {
                                    isDownloaded = true;
                                }
                                if (isDownloaded) {

                                    //通过md5判断已下载的更新包是否为最新版本
                                    if (PreferencesUtils.getString(BaseApplication.getContext(), UPDATE_MD5) != null &&
                                            PreferencesUtils.getString(BaseApplication.getContext(), UPDATE_MD5).equals(dataBean.getMd5())) {
                                        Toast.makeText(BaseApplication.getContext(), R.string.task_exist, Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(BaseApplication.getContext(), DownloadListActivity.class);
                                        startActivity(intent);
                                    } else {
                                        DownloadTaskManager.getInstance(BaseApplication.getContext()).deleteDownloadTask(task);
                                        DownloadTaskManager.getInstance(BaseApplication.getContext()).deleteDownloadTaskFile(task);

                                        DownloadTaskManager.getInstance(BaseApplication.getContext()).registerListener(task,
                                                new DownloadNotificationListener(BaseApplication.getContext(), task));

                                        task.setDownloadState(DownloadState.WAITING);
                                        task.setInstallState(InstallState.INIT);

                                        Toast.makeText(BaseApplication.getContext(), R.string.download_status_downloading, Toast.LENGTH_SHORT).show();
                                        CommonUtils.getDwonloadTask().add(task);

                                        if (!task.equals(DownloadTaskManager.getInstance(BaseApplication.getContext()).queryDownloadTask(task.getFileName()))) {
                                            DownloadTaskManager.getInstance(BaseApplication.getContext()).insertDownloadTask(task);
                                        }
                                        DownloadTaskManager.getInstance(BaseApplication.getContext()).updateDownloadTask(task);
                                        PreferencesUtils.putString(BaseApplication.getContext(), UPDATE_MD5, dataBean.getMd5());
                                        DownloadTaskManager.getInstance(BaseApplication.getContext()).startDownload(task);

                                        Intent intent = new Intent(BaseApplication.getContext(), DownloadListActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        BaseApplication.getContext().startActivity(intent);
                                    }
                                } else {
                                    DownloadTaskManager.getInstance(BaseApplication.getContext()).registerListener(task,
                                            new DownloadNotificationListener(BaseApplication.getContext(), task));

                                    task.setDownloadState(DownloadState.WAITING);
                                    task.setInstallState(InstallState.INIT);

                                    CommonUtils.getDwonloadTask().add(task);
                                    if (!task.equals(DownloadTaskManager.getInstance(BaseApplication.getContext()).queryDownloadTask(task.getFileName()))) {
                                        DownloadTaskManager.getInstance(BaseApplication.getContext()).insertDownloadTask(task);
                                    }
                                    DownloadTaskManager.getInstance(BaseApplication.getContext()).updateDownloadTask(task);
                                    Toast.makeText(BaseApplication.getContext(), R.string.download_status_downloading, Toast.LENGTH_SHORT).show();
                                    PreferencesUtils.putString(BaseApplication.getContext(), UPDATE_MD5, dataBean.getMd5());

                                    DownloadTaskManager.getInstance(BaseApplication.getContext()).startDownload(task);

                                    Intent intent = new Intent(BaseApplication.getContext(), DownloadListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    BaseApplication.getContext().startActivity(intent);
                                }
                            }
                        });
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(fragment, UpdateDialogFragment.class.getName());
                        if (!isFinishing() && !isDestroyed()) {
                            ft.commitAllowingStateLoss();
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void sendUiMessage(int status) {
        if (mUiHandler != null) {
            mUiHandler.sendEmptyMessage(status);
        }
    }

    private void initDataBindToView(BaseBean<ResultPageGroupMapBean> baseBean) {
        Log.i(TAG, "initDataBindToView: ");
        if (NetworkConstant.CODE_SUCCESS.equals(baseBean.getCode())) {
            mPageGroupMapList = baseBean.getResultMap().getPageGroupMapList();

            bindViewPager();
            if (mPageGroupMapList != null && mPageGroupMapList.size() > 0){
                UploadLogManager.getInstance().generateUseroperationlog(mContext, 1, mPageGroupMapList.get(0).getGroupNameEn(), "", "", "", "");
            }
            sendUiMessage(NetworkConstant.MSG_LOADED);
        }
    }

    /** 访问服务器获取最新数据*/
    private void sendRefreshRequest(){
        if (ConstantUtils.checkNetworkState(mContext)) {
            if (mUiHandler != null) {
                mUiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: delay request");
                       mDataProvider.refreshData();
                    }
                }, 5000);
            }
        }
    }

    private void initView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar_search = findViewById(R.id.toolbar_search);
        iv_slidemenu = (ImageView) toolbar_search.findViewById(R.id.main_titlebar_sidebar_btn);
        cl_search = (TouchMaskRelativeLayout) toolbar_search.findViewById(R.id.main_titlebar_search_layout);
        iv_slidemenu.setOnClickListener(this);
        cl_search.setOnClickListener(this);

        //侧边栏菜单
        View headView = navigationView.inflateHeaderView(R.layout.drawer_main);
        rv_check_update = (RelativeLayout) headView.findViewById(R.id.rv_check_update);
        rv_install_records = (RelativeLayout) headView.findViewById(R.id.rv_install_records);
        rv_uninstall = (RelativeLayout) headView.findViewById(R.id.rv_uninstall);
        rv_app_update = (RelativeLayout) headView.findViewById(R.id.rv_app_update);
        iv_update_flag = (ImageView) headView.findViewById(R.id.iv_update_flag);

        /** network request layout*/
        mLoadingProgress = (ImageView) this.findViewById(R.id.load_progress);
        Glide.with(this).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingProgress);

        mRequestFailView = findViewById(R.id.request_fail);
        Button refreshBt = (Button) findViewById(R.id.request_fail_refresh);

        mNoNetWorkView = this.findViewById(R.id.no_network);

        refreshBt.setOnClickListener(this);
        rv_uninstall.setOnClickListener(this);
        rv_install_records.setOnClickListener(this);
        rv_check_update.setOnClickListener(this);
        rv_app_update.setOnClickListener(this);
    }

    private void initLocalChange() {
        Log.i(TAG, "initLocalChange: ");
        String last_lan = PreferencesUtils.getString(mContext, AppStoreConstant.PRE_KEY_LAN_LAST_LANGUAGE, null);
        String new_lan = ConstantUtils.getLanguage();
        if (!new_lan.equals(last_lan)) {
            Log.i(TAG, "initLocalChange: not equal");
            PreferencesUtils.putString(mContext, AppStoreConstant.PRE_KEY_LAN_LAST_LANGUAGE, new_lan);
            String path = AppStoreConstant.APPCENTER_JSON_DIR + AppStoreConstant.APPCENTER_PAGEGROUP_JSON;
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void setPhoneDiaplay() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        String display_phone = display.getHeight() + "*" + display.getWidth();
        PreferencesUtils.putString(this, AppStoreConstant.PRE_KEY_PHONE_DISPLAY, display_phone);
    }

    private void cleanCacheFirstStart() {
        if (!PreferencesUtils.getBoolean(mContext, AppStoreConstant.PRE_KEY_HAVE_CLEAN_CACHE, false)) {
            File fileDir = new File(AppStoreConstant.APPCENTER_JSON_DIR);
            if (!fileDir.exists()) {
                PreferencesUtils.putBoolean(mContext, AppStoreConstant.PRE_KEY_HAVE_CLEAN_CACHE, true);
                return;
            }
            File[] listFiles = fileDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.length() > 32;
                }
            });
            for (File itemFile : listFiles) {
                itemFile.delete();
            }
            PreferencesUtils.putBoolean(mContext, AppStoreConstant.PRE_KEY_HAVE_CLEAN_CACHE, true);
        } else {
            Log.i(TAG, "cleanCacheFirstStart: have clean");
        }
    }

    private Handler mUiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetworkConstant.MSG_LOADING:
                    Log.i(TAG, "handleMessage: LOADING");
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    mNoNetWorkView.setVisibility(View.GONE);
                    mRequestFailView.setVisibility(View.GONE);
                    break;
                case NetworkConstant.MSG_LOADED:
                    Log.i(TAG, "handleMessage: LOADED");
                    mLoadingProgress.setVisibility(View.GONE);

                    if (mFragmentAdapter != null){
                        mFragmentAdapter.onFragmentBindData(mViewPager.getCurrentItem() < 0 ? 0 : mViewPager.getCurrentItem());

                    }
                    break;
                case NetworkConstant.MSG_NETWORK_ERROR:
                    Log.i(TAG, "handleMessage: NETWORK_ERROR");
                    mLoadingProgress.setVisibility(View.GONE);
                    mNoNetWorkView.setVisibility(View.VISIBLE);
                    mRequestFailView.setVisibility(View.GONE);
                    break;
                case NetworkConstant.MSG_REQUEST_FAIL:
                    Log.i(TAG, "handleMessage: REQUEST_FAIL");
                    mLoadingProgress.setVisibility(View.GONE);
                    mNoNetWorkView.setVisibility(View.GONE);
                    mRequestFailView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    private ViewPager mViewPager = null;
    private TabLayout mTabLayout = null;
    private BaseFragmentAdapter mFragmentAdapter = null;
    /**
     * 初始化ViewPager
     * usage
     *
     * @by 俞剑兵 on 2017/7/7.
     */
    private void initViewPager(){
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mViewPager.removeAllViews();
        mViewPager.removeAllViewsInLayout();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                    TabLayout.Tab tab = mTabLayout.getTabAt(i);
                    if (tab != null) {
                        View view = tab.getCustomView();
                        TextView descView = (TextView) view.findViewById(R.id.tv_bottom_desc);
                        ImageView iconView = (ImageView) view.findViewById(R.id.iv_bottom_icon);
                        if (position == i) {  //选中的tab
                            //descView.setAlpha(1.0f);
                            descView.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.bottom_title_click_color));
                            Glide.with(BaseApplication.getContext()).load(mPageGroupMapList.get(i).getActivatedIconUrl()).centerCrop().into(iconView);
                        }
                        else {
                            //descView.setAlpha(0.4f);
                            descView.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.bottom_title_unclick_color));

                            Glide.with(BaseApplication.getContext()).load(mPageGroupMapList.get(i).getIconUrl()).centerCrop().into(iconView);
                        }
                    }
                }
                if (mFragmentAdapter.getDatas().size() > position){
                    mFragmentAdapter.onFragmentBindData(position);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * ViewPager绑定数据
     * usage
     *
     * @by 俞剑兵 on 2017/7/7.
     */
    private void bindViewPager(){
        if (mPageGroupMapList.size()==0){
            return;
        }
        if(mTabLayout == null || mViewPager == null) {
            return;
        }
        mViewPager.removeAllViews();
        mViewPager.removeAllViewsInLayout();
        if(mFragmentAdapter != null){
            mFragmentAdapter.clearFragments();
            mFragmentAdapter = null;
        }
        mFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager(),
                mPageGroupMapList, this);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);//1)从ViewPager中获取TabLayout的title；2）ViewPager滑动时设置TabLayout的Title和indicator；3）点击Tablayout时ViewPager相应变化。
        mTabLayout.setScrollPosition(0, 0, true);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(mFragmentAdapter.getTabView(i));
            }
        }
        mViewPager.setCurrentItem(0, true);

        // 设置默认第一个选中位置的focus效果(由于第一次初始化为0时，并不会调用onPageSelected方法)
        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if (tab != null) {
            View view = tab.getCustomView();
            TextView descView = (TextView) view.findViewById(R.id.tv_bottom_desc);
            ImageView iconView = (ImageView) view.findViewById(R.id.iv_bottom_icon);
            //descView.setAlpha(1.0f);
            descView.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.bottom_title_click_color));
            Glide.with(BaseApplication.getContext()).load(mPageGroupMapList.get(0).getActivatedIconUrl()).centerCrop().into(iconView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = BaseApplication.getContext();
        initLocalChange();
        setPhoneDiaplay();
        UploadLogManager.getInstance().getUploadConfig(mContext, new AhaLogCallBack() {
            @Override
            public void callback() {
                Log.i(TAG, "callback: ");
                UploadLogManager.getInstance().uploadFirstStartLog(mContext);
                UploadLogManager.getInstance().uploadAhaLog(mContext);
            }
        });

        UploadLogManager.getInstance().generateStartOrExitRunLog(mContext, true);
        AppCenterDynamicBroadcastReceiver.registerListener(this);
        setContentView(R.layout.activity_main);

        mDataProvider = new MainDataProvider(this);
        mDataProvider.setDataProviderListener(mDataProviderListener);

        SaveStatus();
        Intent intent = new Intent(mContext, AppCenterNotifyService.class);
        startService(intent);

        initView();
        initViewPager();

        if (ConstantUtils.checkNetworkState(mContext)) {
            cleanCacheFirstStart();
            NetworkClient.startRequestQueryAppVersion(mContext, NetworkConstant.URL_QUERY_UPDATE, requestUpdateListener);
        }
        onNewIntent(getIntent());
    }

    private MainDataProvider.DataProviderListener mDataProviderListener = new MainDataProvider.DataProviderListener() {
        @Override
        public void onInitLocalDataResult(BaseBean<ResultPageGroupMapBean> data) {
            if (data == null) {
                sendUiMessage(NetworkConstant.MSG_REQUEST_FAIL);
                return;
            }
            initDataBindToView(data);
            sendRefreshRequest();
        }

        @Override
        public void onInitNetDataResult(BaseBean<ResultPageGroupMapBean> data) {
            if (data == null) {
                sendUiMessage(NetworkConstant.MSG_REQUEST_FAIL);
                return;
            }
            initDataBindToView(data);
        }

        @Override
        public void onRefreshNetDataResult(BaseBean<ResultPageGroupMapBean> data) {
            if (data == null) {
                sendUiMessage(NetworkConstant.MSG_REQUEST_FAIL);
                return;
            }
            initDataBindToView(data);
        }

        @Override
        public void onDataGetProgress(int progressCode) {
            sendUiMessage(progressCode);
        }
    };

    private void SaveStatus() {
        String operate = ConstantUtils.getSimOperatorName(mContext);
        FirebaseStat.setUserProperty("OperateName", operate);
        String operator = ConstantUtils.getCountryCode(mContext);
        String brand = ConstantUtils.getPhoneModel();
        FirebaseStat.setUserProperty("PhoneBrand", brand);
        if (TextUtils.isEmpty(operator)) {
            return;
        }
        String data = StreamUtils.get(mContext, R.raw.country);
        try {
            JSONArray json = new JSONArray(data);
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonob = new JSONObject(json.get(i).toString());
                String m = jsonob.getString("mcc");
                if (operator.equals(m)) {
                    String contry = jsonob.getString("country");
                    FirebaseStat.setUserProperty("CountryName", contry);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: initData");
        mDataProvider.initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_titlebar_search_layout:
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
                Bundle param1 = new Bundle();
                param1.putString("Action", "cl_search");
                FirebaseStat.logEvent("SearchEditClick", param1);
                break;
            case R.id.main_titlebar_sidebar_btn:
                drawer.openDrawer(GravityCompat.START);
                Bundle param2 = new Bundle();
                param2.putString("Action", "fl_show_slidemenu");
                FirebaseStat.logEvent("Leftmenu", param2);
                break;

            case R.id.rv_check_update:
                if (!ConstantUtils.checkNetworkState(mContext)) {
                    Toast.makeText(this, R.string.appcenter_no_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean(UpdateDialogFragment.DIALOG_BACK, true);
                bundle.putInt(UpdateDialogFragment.DIALOG_TYPE, 0);
                bundle.putString(UpdateDialogFragment.DIALOG_TITLE, getString(R.string.check_update));
                bundle.putString(UpdateDialogFragment.DIALOG_MSG, getString(R.string.get_update_msg));
                final UpdateDialogFragment fragment = UpdateDialogFragment.newInstance(UpdateDialogFragment.class, bundle);
                fragment.setOnClickCancleDialogListener(new UpdateDialogFragment.OnClickCancleDialogListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.dismiss();
                    }
                });
                fragment.show(getSupportFragmentManager(), "");

                iv_update_flag.setVisibility(View.GONE);
                if (iv_update_flag.getVisibility() == View.GONE) {
                    findViewById(R.id.main_titlebar_side_layout_red_point).setVisibility(View.GONE);
                }
                PreferencesUtils.putLong(mContext, "last_modify", System.currentTimeMillis());

                Bundle param3 = new Bundle();
                param3.putString("Action", "rv_check_update");
                FirebaseStat.logEvent("Checkupdate", param3);
                break;
            case R.id.rv_install_records:
                Intent downloadIntent = new Intent(mContext, DownloadListActivity.class);
                startActivity(downloadIntent);
                Bundle param4 = new Bundle();
                param4.putString("Action", "rv_install_records");
                FirebaseStat.logEvent("DownloadManager", param4);

                break;
            case R.id.rv_uninstall:
                Intent newIntent = new Intent(mContext, UninstallActivity.class);
                startActivity(newIntent);
                Bundle param5 = new Bundle();
                param5.putString("Action", "rv_uninstall");
                FirebaseStat.logEvent("Uninstall", param5);

                break;
            case R.id.rv_app_update:
                Intent updateIntent = new Intent(mContext, UpdateAcitivity.class);
                startActivity(updateIntent);
                Bundle param6 = new Bundle();
                param6.putString("Action", "rv_app_update");
                FirebaseStat.logEvent("AllAppUpdate", param6);
                break;
            case R.id.request_fail_refresh:
                mRequestFailView.setVisibility(View.GONE);
                mDataProvider.initData();
                Bundle param7 = new Bundle();
                param7.putString("Action", "request_fail_refresh");
                FirebaseStat.logEvent("MainActivityRefresh", param7);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConstantUtils.checkNetworkState(mContext)) {
            mIsNetworkAvailable = true;
        }
        FirebaseStat.setCurrentScreen(this, "AhaMainActivity", null);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "destroy");
        super.onDestroy();
        UploadLogManager.getInstance().generateStartOrExitRunLog(mContext, false);
        AppCenterDynamicBroadcastReceiver.unRegisterListener(this);
        mDataProvider.onDestroy();
        mLocalBaseBean = null;
        mNetWorkBaseBean = null;
        if (mUiHandler != null) {
            mUiHandler.removeCallbacksAndMessages(null);
            mUiHandler = null;
        }
        if (mPageGroupMapList != null) {
            mPageGroupMapList.clear();
            mPageGroupMapList = null;
        }
        requestUpdateListener = null;
    }


    @Override
    public void onNetworkChanged(boolean isAvailable) {
        if (isAvailable) {
            Log.e(TAG, "onNetworkChanged: true");
            if (!mIsNetworkAvailable) {
                Log.i(TAG, "onNetworkChanged: start");
                sendUiMessage(NetworkConstant.MSG_LOADING);
                mDataProvider.refreshData();
            }
            mIsNetworkAvailable = true;
        } else {
            Log.i(TAG, "onNetworkChanged: false");
            mIsNetworkAvailable = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e(TAG, "ON NEW INTENT");
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            if (intent.getExtras().containsKey("notificationurl")) {
                setIntent(intent);
                if (intent.getStringExtra("notificationurl").contains("leo.api.appDetail")) {
                    Intent pendintent = new Intent(mContext, AppDetailActivity.class);
                    pendintent.putExtra("notificationurl", intent.getStringExtra("notificationurl"));
                    startActivity(pendintent);
                }
                Bundle param1 = new Bundle();
                param1.putString("Action", "NotificationClick");
                FirebaseStat.logEvent(intent.getStringExtra("style"), param1);
            } else if (intent.getExtras().getBoolean("fromDownloadNotification", false)) {
                startActivity(new Intent(this, DownloadListActivity.class));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
    }
}
