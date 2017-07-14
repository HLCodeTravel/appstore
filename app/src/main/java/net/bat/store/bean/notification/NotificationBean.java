package net.bat.store.bean.notification;

/**
 * usage
 *
 * @author 金新
 * @date 2017/2/20
 * =====================================
 * Copyright (c)2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */
public class NotificationBean{
    private String code;
    private NotificationResultMapBean resultMap;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



    public NotificationResultMapBean getResultMap() {
        return resultMap;
    }

    public void setResultMap(NotificationResultMapBean resultMap) {
        this.resultMap = resultMap;
    }
}
