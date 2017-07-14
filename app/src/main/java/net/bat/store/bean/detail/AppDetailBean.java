package net.bat.store.bean.detail;

import java.io.Serializable;

/**
 * Created by bingbing.li on 2017/1/13.
 */
public class AppDetailBean implements Serializable {
    private String appUrl;
    private String appVersionName;
    private String appSize;
    private String themeUrl;
    private String remark;
    private String iconUrl;
    private String detailDesc;
    private int downloadNumber;
    private String appName;
    private String grading;
    private String appCategory;
    private String price;
    private String screenshotUrl;
    private String resourceSnapshotId;
    private String commentNumber;
    private String briefDesc;
    private String developer;
    private String appCategoryIconUrl;
    private String packageName;
    private int appAttribute;
    private String appId;
    private String appNameEn;

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getThemeUrl() {
        return themeUrl;
    }

    public void setThemeUrl(String themeUrl) {
        this.themeUrl = themeUrl;
    }

    public String getRemark() {
        return remark;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDetailDesc() {
        return detailDesc;
    }

    public void setDetailDesc(String detailDesc) {
        this.detailDesc = detailDesc;
    }

    public int getDownloadNumber() {
        return downloadNumber;
    }

    public void setDownloadNumber(int downloadNumber) {
        this.downloadNumber = downloadNumber;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppGrade() {
        return grading;
    }

    public void setAppGrade(String appGrade) {
        this.grading = appGrade;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }

    public String getResourceSnapshotId() {
        return resourceSnapshotId;
    }

    public void setResourceSnapshotId(String resourceSnapshotId) {
        this.resourceSnapshotId = resourceSnapshotId;
    }

    public String getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "AppDetailBean{" +
                "appUrl='" + appUrl + '\'' +
                ", appVersionName='" + appVersionName + '\'' +
                ", appSize='" + appSize + '\'' +
                ", themeUrl='" + themeUrl + '\'' +
                ", remark='" + remark + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", detailDesc='" + detailDesc + '\'' +
                ", downloadNumber=" + downloadNumber +
                ", appName='" + appName + '\'' +
                ", appGrade='" + grading + '\'' +
                ", appCategory='" + appCategory + '\'' +
                ", price='" + price + '\'' +
                ", screenshotUrl='" + screenshotUrl + '\'' +
                ", resourceSnapshotId='" + resourceSnapshotId + '\'' +
                ", commentNumber='" + commentNumber + '\'' +
                ", briefDesc='" + briefDesc + '\'' +
                '}';
    }

    public String getAppCategoryIconUrl() {
        return appCategoryIconUrl;
    }

    public void setAppCategoryIconUrl(String appCategoryIconUrl) {
        this.appCategoryIconUrl = appCategoryIconUrl;
    }

    public int getAppAttribute() {
        return appAttribute;
    }

    public void setAppAttribute(int appAttribute) {
        this.appAttribute = appAttribute;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppNameEn() {
        return appNameEn;
    }

    public void setAppNameEn(String appNameEn) {
        this.appNameEn = appNameEn;
    }
}
