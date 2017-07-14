package net.bat.store.ux.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.bat.store.R;
import net.bat.store.constans.NetworkConstant;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.ux.adapter.SearchHistoryListAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class HotWordsFragment extends RelativeLayout implements OnClickListener {

    private SharedPreferences mSharedPreferenxes;
    private Context mContext;
    public ListView mHistoryListView;
    public SearchHistoryListAdapter mSearchHistoryListAdapter;
    private List<String> mSearchHistoryList;
    public Button mDeleteAllHistoryBtn;


    public HotWordsFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mSharedPreferenxes = mContext.getSharedPreferences("search_history", MODE_PRIVATE);
        LayoutInflater.from(context).inflate(R.layout.appcenter_search_hot_words_view, this, true);
        initView();
    }

    private void initData() {
        initSearchHistory();
    }

    private void initSearchHistory() {
        String history = mSharedPreferenxes.getString("search_history", "");
        if (!TextUtils.isEmpty(history)) {
            List<String> list = new ArrayList<String>();
            for (Object o : history.split(",")) {
                list.add((String) o);
            }
            mSearchHistoryList = list;
        }


    }

    private void initView() {
        mHistoryListView = (ListView) findViewById(R.id.search_history_list);
        mSearchHistoryList = new ArrayList<String>();

        initData();
        mSearchHistoryListAdapter = new SearchHistoryListAdapter(mContext, mSearchHistoryList);
        mHistoryListView.setAdapter(mSearchHistoryListAdapter);
        mHistoryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(mSearchHistoryList.size()==0){
                    return;
                }
                goGoolgPlaySearch(mSearchHistoryList.get(position));
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mSearchHistoryList.get(position));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, position + "");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SearchHistory");
                FirebaseStat.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }

        });
        mDeleteAllHistoryBtn = (Button) findViewById(R.id.delete_all_history_btn);
        mDeleteAllHistoryBtn.setOnClickListener(this);
    }

    private void goGoolgPlaySearch(String searchContent) {
        Log.i(TAG, "goGoolgPlaySearch: " + searchContent);
        Uri uri = Uri.parse(NetworkConstant.URL_GOOGLE_SEARCH + searchContent);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.delete_all_history_btn:
                SharedPreferences.Editor editor = mSharedPreferenxes.edit();
                editor.remove("search_history");
                editor.commit();
                mSearchHistoryList.clear();
                initData();
                mSearchHistoryListAdapter = new SearchHistoryListAdapter(mContext, mSearchHistoryList);
                mHistoryListView.setAdapter(mSearchHistoryListAdapter);
                mSearchHistoryListAdapter.notifyDataSetChanged();
                mDeleteAllHistoryBtn.setVisibility(View.GONE);
                Bundle param1 = new Bundle();
                param1.putString("Action", "delete_all_history_btn");
                FirebaseStat.logEvent("ClearHistoryClick", param1);
                break;
        }
    }
}
