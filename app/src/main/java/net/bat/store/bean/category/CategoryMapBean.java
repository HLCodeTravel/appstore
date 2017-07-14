package net.bat.store.bean.category;

import java.io.Serializable;

/**
 * Created by bingbing.li on 2017/1/13.
 */
public class CategoryMapBean implements Serializable{
    private String appCategory;
    private String pageUrl;
    private String iconUrl;

    public String getIsOperation() {
        return isOperation;
    }

    public void setIsOperation(String isOperation) {
        this.isOperation = isOperation;
    }

    private String resourceSnapshotId;
    private String isOperation;

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
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


}
