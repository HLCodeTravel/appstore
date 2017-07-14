package net.bat.store.ux.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import net.bat.store.R;

/**
 * Created by liang.he on 2017/7/7.
 */

public class MainViewHolder extends RecyclerView.ViewHolder {

    public View mItemParent;
    public ImageView mBannerImageView;
    public ImageView mAppIconImageView;
    public TextView mAppNameTextView;
    public RatingBar mAppRatingBar;
    public Button mDownloadButton;
    public TextView mAppDetailsTextView;
    public TextView mAppSizeTextView;
    public TextView mCategoryName;
    public ProgressBar pb_current_size;
    public ImageView mImageViewIcon;


    public MainViewHolder(View itemView) {
        super(itemView);
        mAppSizeTextView = (TextView) itemView.findViewById(R.id.app_size_textview);
        mItemParent = itemView.findViewById(R.id.banner_parent);
        mBannerImageView = (ImageView) itemView.findViewById(R.id.banner_imageview);
        mAppIconImageView = (ImageView) itemView.findViewById(R.id.app_icon_imageview);
        mAppNameTextView = (TextView) itemView.findViewById(R.id.app_name_textview);
        mAppRatingBar = (RatingBar) itemView.findViewById(R.id.app_ratingBar);
        mDownloadButton = (Button) itemView.findViewById(R.id.download_button);
        mAppDetailsTextView = (TextView) itemView.findViewById(R.id.tv_description);
        pb_current_size = (ProgressBar) itemView.findViewById(R.id.pb_current_size);
        mImageViewIcon = (ImageView) itemView.findViewById(R.id.app_size_image);
        mCategoryName = (TextView) itemView.findViewById(R.id.category_name_textview);
    }
}