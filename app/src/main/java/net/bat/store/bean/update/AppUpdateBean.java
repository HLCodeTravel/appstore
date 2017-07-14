package net.bat.store.bean.update;

/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/1/18
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */
public class AppUpdateBean {
    private int status;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getDataBean() {
        return data;
    }

    public void setDataBean(DataBean data) {
        this.data = data;
    }
}
