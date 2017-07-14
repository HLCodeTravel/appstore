package net.bat.store.bean.floor;

import java.io.Serializable;

/**
 * Created by bingbing.li on 2017/1/13.
 */
public class AppMapBean implements Serializable {
    private int downloadNumber;
    private String grading;
    private String appUrl;
    private String appName;
    private String appSize;
    private String appCategory;
    private String appCategoryIconUrl;
    private String detailPageUrl;
    private String price;
    private String appId;
    private String themeUrl;
    private String iconUrl;
    private int appAttribute;
    private String resourceSnapshotId;
    private String briefDesc;
    private String slotId;
    private String packageName;
    private String developer;
    private String floorId;
    private String appNameEn;

    public int getDownloadNumber() {
        return downloadNumber;
    }

    public void setDownloadNumber(int downloadNumber) {
        this.downloadNumber = downloadNumber;
    }

    public String getAppGrade() {
        return grading;
    }

    public void setAppGrade(String appGrade) {
        this.grading = appGrade;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getThemeUrl() {
        return themeUrl;
    }

    public void setThemeUrl(String themeUrl) {
        this.themeUrl = themeUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getResourceSnapshotId() {
        return resourceSnapshotId;
    }

    public void setResourceSnapshotId(String resourceSnapshotId) {
        this.resourceSnapshotId = resourceSnapshotId;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public String getDetailPageUrl() {
        return detailPageUrl;
    }

    public void setDetailPageUrl(String detailPageUrl) {
        this.detailPageUrl = detailPageUrl;
    }

    public int getAppAttribute() {
        return appAttribute;
    }

    public void setAppAttribute(int appAttribute) {
        this.appAttribute = appAttribute;
    }

    public String getAppCategoryIconUrl() {
        return appCategoryIconUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setAppCategoryIconUrl(String appCategoryIconUrl) {
        this.appCategoryIconUrl = appCategoryIconUrl;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }


    public String getGrading() {
        return grading;
    }

    public void setGrading(String grading) {
        this.grading = grading;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    public String getAppNameEn() {
        return appNameEn;
    }

    public void setAppNameEn(String appNameEn) {
        this.appNameEn = appNameEn;
    }
}
