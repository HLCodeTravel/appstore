package net.bat.store.ux.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.bat.store.R;
import net.bat.store.utils.FirebaseStat;
import net.bat.store.widget.PhotoViewAttacher;

/**
 * Created by bingbing.li on 2017/1/12.
 */

public class ImageDetailFragment extends Fragment {

    private String imageId;
    private ImageView mImageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;

    public static ImageDetailFragment newInstance(String url) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("url", url);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageId = getArguments() != null ? getArguments().getString("url") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_image_detail, container, false);
        mImageView = (ImageView) v.findViewById(R.id.image);

        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        Glide.with(this).load(imageId).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mAttacher = new PhotoViewAttacher(mImageView);

                mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

                    @Override
                    public void onPhotoTap(View arg0, float arg1, float arg2) {

                        getActivity().finish();
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        Bundle param = new Bundle();
                        param.putString("Action", "Image_resource_load");
                        FirebaseStat.logEvent("ImageFade", param);


                    }
                });
                return false;
            }
        }).into(mImageView);
        return v;
    }

}
