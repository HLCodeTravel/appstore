package net.bat.store.bean.group;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bingbing.li on 2017/1/13.
 */
public class PageGroupMapBean implements Serializable{
    private int containsSecondaryMenu;
    private String groupId;
    private String groupName;
    private String groupNameEn;
    private String iconUrl;
    private String resourceSnapshotId;
    private String activatedIconUrl;
    private List<PageMapBean> pageMapList;

    public int getContainsSecondaryMenu() {
        return containsSecondaryMenu;
    }

    public void setContainsSecondaryMenu(int containsSecondaryMenu) {
        this.containsSecondaryMenu = containsSecondaryMenu;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<PageMapBean> getPageMapList() {
        return pageMapList;
    }

    public void setPageMapList(List<PageMapBean> pageMapList) {
        this.pageMapList = pageMapList;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getActivatedIconUrl() {
        return activatedIconUrl;
    }

    public void setActivatedIconUrl(String activatedIconUrl) {
        this.activatedIconUrl = activatedIconUrl;
    }

    public String getResourceSnapshotId() {
        return resourceSnapshotId;
    }

    public void setResourceSnapshotId(String resourceSnapshotId) {
        this.resourceSnapshotId = resourceSnapshotId;
    }

    public String getGroupNameEn() {
        return groupNameEn;
    }

    public void setGroupNameEn(String groupNameEn) {
        this.groupNameEn = groupNameEn;
    }

    @Override
    public String toString() {
        return "PageGroupMapBean{" +
                "containsSecondaryMenu=" + containsSecondaryMenu +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", pageMapList=" + pageMapList +
                '}';
    }
}
