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
public class UpdateAppInfo {
    private String name;
    private String url;
    private String recentChange;
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRecentChange() {
        return recentChange;
    }

    public void setRecentChange(String recentChange) {
        this.recentChange = recentChange;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
