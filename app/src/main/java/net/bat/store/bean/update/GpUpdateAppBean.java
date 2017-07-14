package net.bat.store.bean.update;

/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/2/13
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */
public class GpUpdateAppBean {
    private String code;
    private String desc;
    private GpUpdateResultBean resultMap;

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

    public GpUpdateResultBean getResultMap() {
        return resultMap;
    }

    public void setResultMap(GpUpdateResultBean resultMap) {
        this.resultMap = resultMap;
    }
}
