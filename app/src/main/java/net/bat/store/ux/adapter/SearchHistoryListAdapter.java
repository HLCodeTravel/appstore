package net.bat.store.ux.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.bat.store.R;

import java.util.ArrayList;
import java.util.List;


public class SearchHistoryListAdapter extends BaseAdapter {

    private static final String TAG = "SearchHistoryListAdapte";
    private Context mContext;
    private LayoutInflater mInflater = null;
    private SharedPreferences mSharedPrefer;
    private ArrayList<String> mDataSource = new ArrayList<String>();
    private CallBack mCallBack;

    public SearchHistoryListAdapter(Context activity,
                                    List<String> dataSource) {
        mContext = activity;
        mInflater = LayoutInflater.from(activity);
        mDataSource.clear();
        mDataSource.addAll(dataSource);
        mSharedPrefer = mContext.getSharedPreferences("search_history", 0);
    }

    public void updateList(List<String> dataSource) {
        if (dataSource != null) {
            mDataSource.clear();
            mDataSource.addAll(dataSource);
            notifyDataSetChanged();
        }
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (mDataSource == null) {
            return 0;
        }
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        try {
            if (mDataSource != null && position < mDataSource.size()) {
                return mDataSource.get(position);
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.appcenter_search_history_item_layout, null);
            viewHolder.historyIcon = (ImageView) convertView.findViewById(R.id.history_icon);
            viewHolder.historyText = (TextView) convertView.findViewById(R.id.history_text);
            viewHolder.deleteHistoryLayout = (RelativeLayout) convertView.findViewById(R.id.delete_history_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String historyText = mDataSource.get(position);
        if (historyText != null && !historyText.equals("")) {
            viewHolder.deleteHistoryLayout.setOnClickListener(new DeleteHistoryClickListener(position));
            viewHolder.historyText.setText(historyText);
        }

        return convertView;
    }


    class ViewHolder {
        public ImageView historyIcon;
        public TextView historyText;
        public RelativeLayout deleteHistoryLayout;
    }


    class DeleteHistoryClickListener implements OnClickListener {

        private int mPosition;

        public DeleteHistoryClickListener(int deleteItemPosition) {
            mPosition = deleteItemPosition;
        }

        @Override
        public void onClick(View v) {
            mDataSource.remove(mPosition);
            notifyDataSetChanged();
            updateSharedPreHistory();
            if (mDataSource.size() == 0) {
                mCallBack.callback();
            }
        }
    }

    public interface CallBack {
        void callback();
    }

    private void updateSharedPreHistory() {
        mSharedPrefer.edit().putString("search_history", "").commit();
        if (mDataSource.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mDataSource.size(); i++) {
                sb.append(mDataSource.get(i) + ",");
            }
            mSharedPrefer.edit().putString("search_history", sb.toString()).commit();
        }
    }
}
