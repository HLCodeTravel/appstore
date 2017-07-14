package com.zz.store.core.main;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import net.bat.store.R;
import net.bat.store.BaseApplication;
import net.bat.store.ux.acticivty.AhaMainActivity;


/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/3/28
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */

public class MainScreenActivity extends AppCompatActivity {

    ImageView iv_animlist;

    TextView tv_tag;
    TextView tv_version;

    private static Handler handler;
    private  Runnable runnable;

    AnimationDrawable animationDrawable;
    int duration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("LOADING CREATE", "CREATE");

        setContentView(R.layout.activity_loading);
        iv_animlist = (ImageView) findViewById(R.id.iv_anim_list);
        tv_tag = (TextView) findViewById(R.id.tv_tag);
        tv_version = (TextView) findViewById(R.id.tv_version);

        try {
            tv_version.setText("Ver:" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            tv_version.setText("Ver:1.0.0");
        }

        duration = 0;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.e("loading", "focuschange");
        if (hasFocus) {
            if (handler != null) {
                return;
            }

            super.onWindowFocusChanged(hasFocus);
            iv_animlist.setBackgroundResource(R.drawable.frame_loading);
            animationDrawable = (AnimationDrawable) iv_animlist.getBackground();

            animationDrawable.start();


            for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
                duration += animationDrawable.getDuration(i);
            }
            handler = new Handler();

            runnable = new Runnable() {
                @Override
                public void run() {
                    Log.e("loading", "runnable");
                    //                Animation animation = AnimationUtils.loadAnimation(BaseApplication.getContext(),R.anim.text_ainm);
//                tv_tag.startAnimation(animation);
                    final ValueAnimator animator = ValueAnimator.ofFloat(20, 0);
                    animator.setDuration(1000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            //tv_tag.setTranslationY((float)animation.getAnimatedValue());

                            if ((float) animation.getAnimatedValue() > 5) {
                                tv_tag.setAlpha((20 - (float) animation.getAnimatedValue()) / 20);
                            } else {
                                tv_tag.setAlpha(1);
                            }
                        }

                    });

                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            Intent intent = new Intent(BaseApplication.getContext(), AhaMainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animator.start();
                }
            };
            handler.postDelayed(runnable, duration);

//        while (animationDrawable.isRunning()) {
//            Log.e("loading", "is loading");
//        }
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (handler != null ) {
            Log.e("loading", "stop");
            handler.removeCallbacksAndMessages(runnable);
            handler = null;
        }

        runnable=null;
        animationDrawable=null;

        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (handler != null ) {
            Log.e("loading", "destroy");
            handler.removeCallbacksAndMessages(runnable);
            handler = null;
        }

        runnable=null;

        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        animationDrawable=null;
    }
}
