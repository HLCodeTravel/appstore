package net.bat.store.bean.download;

/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/24
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */
public class DownloadUrlBean {
    String code;
    String desc;
    DownloadReusltBean resultMap;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DownloadReusltBean getResultMap() {
        return resultMap;
    }

    public void setResultMap(DownloadReusltBean resultMap) {
        this.resultMap = resultMap;
    }
}
