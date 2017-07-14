package net.bat.store.bean.group;

import java.io.Serializable;

/**
 * Created by bingbing.li on 2017/1/13.
 */
public class PageMapBean implements Serializable{
    private String pageUrl;
    private String pageId;
    private String secondaryMenuNameEn;
    private String secondaryMenuName;
    private String resourceSnapshotId;
    private int isThirdUrl;

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getSecondaryMenuName() {
        return secondaryMenuName;
    }

    public void setSecondaryMenuName(String secondaryMenuName) {
        this.secondaryMenuName = secondaryMenuName;
    }

    public String getResourceSnapshotId() {
        return resourceSnapshotId;
    }

    public void setResourceSnapshotId(String resourceSnapshotId) {
        this.resourceSnapshotId = resourceSnapshotId;
    }

    public String getSecondaryMenuNameEn() {
        return secondaryMenuNameEn;
    }

    public void setSecondaryMenuNameEn(String secondaryMenuNameEn) {
        this.secondaryMenuNameEn = secondaryMenuNameEn;
    }

    public int getIsThirdUrl() {
        return isThirdUrl;
    }

    public void setIsThirdUrl(int isThirdUrl) {
        this.isThirdUrl = isThirdUrl;
    }
}
