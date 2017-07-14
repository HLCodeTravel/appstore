package net.bat.store.ux.fragment;

import android.support.v4.app.Fragment;

import net.bat.store.bean.group.PageGroupMapBean;

/**
 * usage
 *
 * @author 俞剑兵.
 * @data 2017/7/7.
 * ======================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */

public abstract class BaseFragment extends Fragment{
    public abstract void bindData(PageGroupMapBean pageGroupMapBean);
}
