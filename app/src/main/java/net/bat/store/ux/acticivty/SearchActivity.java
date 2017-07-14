package net.bat.store.ux.acticivty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.constans.NetworkConstant;
import net.bat.store.utils.ConstantUtils;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.utils.UploadLogManager;
import net.bat.store.ux.adapter.SearchHistoryListAdapter;
import net.bat.store.ux.fragment.HotWordsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * <br>类描述:搜索活动，总体调度各Fragment的切换
 * <br>功能详细描述:
 * 
 * @author hogan
 * @date  [2014-12-1]
 */
public class SearchActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "SearchActivity";
    private FrameLayout mBackBtn;
    private EditText mSearchEdit;
    private ImageView mSearchBtn;
    private boolean mIsSearchHotWords = false;
    private HotWordsFragment mHotWordsFragment;
    private int SAVE_HISTORY_ITEM_MAX=6;
    private List<String> mHistoryKeywords=new ArrayList<String>();
    private SharedPreferences mSharedPreferenxes;
    private List<String> mSearchHistoryList;
    private String wordsname;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //设置键盘背景，防止收起出现黑影
        getWindow().setBackgroundDrawableResource(R.color.appcenter_common_bg_color_grey);
        setContentView(R.layout.activity_search);

        mSharedPreferenxes=getSharedPreferences("search_history", MODE_PRIVATE);

        initView();
        Timer timer = new Timer();
        timer.schedule(new TimerTask()   {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)mSearchEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mSearchEdit, 0);
            }
        }, 400);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                finish();
                Bundle param1 = new Bundle();
                param1.putString("Action", "back_btn");
                FirebaseStat.logEvent("SearchBackClick", param1);
                break;
            case R.id.main_titlebar_search_icon:
                goGoolgPlaySearch();
				saveSearchHistory();
                String  text=mSearchEdit.getText().toString();
                if("IMEI".equals(text)){
                    Savewords(1);

                }else if("Time".equals(text)){
                    Savewords(2);
                }else if("content".equals(text)){
                    Savewords(3);
                }else if("contry".equals(text)){
                    Savewords(4);
                }else if("model".equals(text)){
                    Savewords(5);
                }else if("brand".equals(text)){
                    Savewords(6);
                }
                break;
        }
    }

    private void Savewords(int i) {
        if(i==1){
            wordsname="IMEI";
        }else if(i==2){
            wordsname="Time";
        }else if(i==3){
            wordsname="content";
        }else if(i==4){
            wordsname="contry";
        }else if(i==5){
            wordsname="model";
        }else if(i==6){
            wordsname="brand";
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,wordsname);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,i+"");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Hotwords");
        FirebaseStat.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }



    private void initView() {
        mSearchEdit = (EditText) findViewById(R.id.search_edit);
        ImageView back_icon= (ImageView) findViewById(R.id.iv_back);
        if(ConstantUtils.isIconShouldRevert()){
            back_icon.setImageResource(R.drawable.icon_back_revert);
        }
        mSearchEdit.setFocusable(true);
        mSearchEdit.setFocusableInTouchMode(true);
        mSearchEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mHotWordsFragment = (HotWordsFragment) findViewById(R.id.appcenter_hotwords_interface);
        mBackBtn = (FrameLayout) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
        mSearchBtn = (ImageView) findViewById(R.id.main_titlebar_search_icon);
        mSearchBtn.setOnClickListener(this);

        mSearchEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        //过滤特殊字符
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String regEx = "[/\\:*,?<>|\"\n\t]";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                else return null;
            }
        };
        mSearchEdit.setFilters(new InputFilter[]{filter});

        mSearchEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    mHotWordsFragment.setVisibility(View.GONE);
                } else {
                    if(mSearchHistoryList!=null && mSearchHistoryList.size()>0){
                        mSearchHistoryList.clear();
                    }
                    initSearch();
                    mHotWordsFragment.mSearchHistoryListAdapter.updateList(mSearchHistoryList);
                    if (mSearchHistoryList!=null && mSearchHistoryList.size() > 0) {
                        mHotWordsFragment.mDeleteAllHistoryBtn.setVisibility(View.VISIBLE);
                        mHotWordsFragment.setVisibility(View.VISIBLE);
                    } else {
                        mHotWordsFragment.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSearchEdit.getText().toString().isEmpty()) {
                    //setNoSearchContentVisibility(false);
                } else {
                    //setNoSearchContentVisibility(true);
                    if (mIsSearchHotWords) {
                        Selection.setSelection(s, s.length());
                        mIsSearchHotWords = false;
                    }
                }
            }
        });
        initSearchHistory();
        //对输入法上的一些特殊按键进行处理
        mSearchEdit.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // TODO Auto-generated method stub
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || (actionId == EditorInfo.IME_ACTION_UNSPECIFIED && event
                                .getKeyCode() == KeyEvent.KEYCODE_ENTER)||actionId == EditorInfo.IME_ACTION_SEARCH ||actionId == EditorInfo.IME_ACTION_NEXT||actionId == EditorInfo.IME_ACTION_DONE) {
                    String  text=mSearchEdit.getText().toString();
                    goGoolgPlaySearch();
                    saveSearchHistory();
                    Bundle param2 = new Bundle();
                    param2.putString("Action", "search_btn");
                    FirebaseStat.logEvent("SearchEditClick", param2);
                    if("IMEI".equals(text)){
                        Savewords(1);
                    }else if("Time".equals(text)){
                        Savewords(2);
                    }else if("content".equals(text)){
                        Savewords(3);
                    }else if("contry".equals(text)){
                        Savewords(4);
                    }else if("model".equals(text)){
                        Savewords(5);
                    }else if("brand".equals(text)){
                        Savewords(6);
                    }
                }
                return false;
            }

        });

        mSearchEdit.setOnClickListener(this);
        mHotWordsFragment.mSearchHistoryListAdapter.setCallBack(callBack);
    }

    private SearchHistoryListAdapter.CallBack callBack=new SearchHistoryListAdapter.CallBack() {
        @Override
        public void callback() {
            mHotWordsFragment.mDeleteAllHistoryBtn.setVisibility(View.GONE);
        }
    };


    private void goGoolgPlaySearch(){
        String searchContent = mSearchEdit.getText().toString();
        if(TextUtils.isEmpty(searchContent)){
            Toast.makeText(this,R.string.warn_search_no_content,Toast.LENGTH_SHORT).show();
            return;
        }
        UploadLogManager.getInstance().generateSearchLog(BaseApplication.getContext(),searchContent);
        Log.i(TAG, "onEditorAction: "+searchContent);
        Uri uri = Uri.parse(NetworkConstant.URL_GOOGLE_SEARCH+searchContent);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHotWordsFragment=null;
        if(mSearchHistoryList!=null){
            mSearchHistoryList.clear();
            mSearchHistoryList=null;
        }
        if(mHistoryKeywords!=null){
            mHistoryKeywords.clear();
            mHistoryKeywords=null;
        }
        mSharedPreferenxes=null;
    }

    public void saveSearchHistory() {
       String  text=mSearchEdit.getText().toString();
        if (text.length() < 1) {
            return;
        }
        if(!isContains(text)){
            if(mHistoryKeywords!=null&&mHistoryKeywords.size()==SAVE_HISTORY_ITEM_MAX){
                mHistoryKeywords.remove(mHistoryKeywords.size()-1);
                mHistoryKeywords.add(0,text);
            }
        }
        SharedPreferences sp = getSharedPreferences("search_history", 0);
        String longhistory = sp.getString("search_history", "");
        String[] tmpHistory = longhistory.split(",");
        ArrayList<String> history = new ArrayList<String>(
                Arrays.asList(tmpHistory));
        int size=history.size();
        if (size>0) {
            if(history.size()==SAVE_HISTORY_ITEM_MAX){
                boolean flag=false;
                for (int i = 0; i < size; i++) {
                    if (text.equals(history.get(i))) {
                        history.remove(i);
                        flag=true;
                        break;
                    }
                }
                if(flag){
                    history.add(0, text);
                }else{
                    history.remove(size-1);
                    history.add(0, text);
                }
            }else{
                for (int i = 0; i < size; i++) {
                    if (text.equals(history.get(i))) {
                        history.remove(i);
                        break;
                    }
                }
                history.add(0, text);
            }
        }
        if (history.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < history.size(); i++) {
                sb.append(history.get(i) + ",");
            }
            sp.edit().putString("search_history", sb.toString()).commit();
        } else {
            sp.edit().putString("search_history", text + ",").commit();
        }
    }
    public boolean isContains(String text){
        for(String str:mHistoryKeywords){
            if(text.equals(str)){
                return true;
            }
        }
        return false;
    }


    public void initSearchHistory(){
        String history =mSharedPreferenxes.getString("search_history", "");
        if (!TextUtils.isEmpty(history)){
            List<String> list = new ArrayList<String>();
            for(Object o : history.split(",")) {
                list.add((String)o);
            }
            mHistoryKeywords = list;
        }
        if (mHistoryKeywords.size() > 0) {
            mHotWordsFragment.setVisibility(View.VISIBLE);
        } else {
            mHotWordsFragment.setVisibility(View.GONE);
        }

    }


    private void initSearch() {
        String history = mSharedPreferenxes.getString("search_history", "");
        if (!TextUtils.isEmpty(history)) {
            List<String> list = new ArrayList<String>();
            for (Object o : history.split(",")) {
                list.add((String) o);
            }
            mSearchHistoryList = list;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseStat.setCurrentScreen(this,"SearchActivity",null);
    }
}
