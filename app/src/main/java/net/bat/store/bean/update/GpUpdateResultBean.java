package net.bat.store.bean.update;
/* Top Secret */

import java.util.List;

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/13
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */
public class GpUpdateResultBean {
    private List<UpdateAppInfo> gpAppDataList;

    public List<UpdateAppInfo> getGpAppDataList() {
        return gpAppDataList;
    }

    public void setGpAppDataList(List<UpdateAppInfo> gpAppDataList) {
        this.gpAppDataList = gpAppDataList;
    }
}
