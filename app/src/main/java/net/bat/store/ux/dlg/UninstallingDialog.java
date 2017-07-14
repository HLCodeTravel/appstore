//package net.bat.store.ux.uninstall;
//
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import net.bat.store.R;
//import net.bat.store.base.BaseApplication;
//
//
///* Top Secret */
//
///**
// * usage
// *
// * @author 周粤琦
// * @date 2017/3/6
// * ==================================
// * Copyright (c) 2017 TRANSSION.Co.Ltd.
// * All rights reserved
// */
//
//public class UninstallingDialog extends DialogFragment {
//
//    TextView tv_app_label;
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.un_dialog,container);
//
//        tv_app_label = (TextView)view.findViewById(R.id.tv_app_name);
//
//        return view;
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        Dialog dialog = getDialog();
//        dialog.setCancelable(false);
//        if (dialog != null) {
//            DisplayMetrics dm = new DisplayMetrics();
//            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
//        }
//    }
//}
