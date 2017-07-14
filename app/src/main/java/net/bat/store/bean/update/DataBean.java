package net.bat.store.bean.update;

/* Top Secret */

/**
 * usage
 *
 * @author 周粤琦
 * @date 2017/1/21
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved
 */
public class DataBean {
    private String md5;
    private long packageSize;
    private String packageName;
    private String downloadPackageUrl;
    private String versionName;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(long packageSize) {
        this.packageSize = packageSize;
    }

    public String getDownloadPackageUrl() {
        return downloadPackageUrl;
    }

    public void setDownloadPackageUrl(String downloadPackageUrl) {
        this.downloadPackageUrl = downloadPackageUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
