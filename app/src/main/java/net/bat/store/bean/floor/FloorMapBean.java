package net.bat.store.bean.floor;

import java.util.List;

/**
 * Created by bingbing.li on 2017/1/13.
 */
public class FloorMapBean {
    private int groupType;
    private int floorType;
    private int isShowTag;
    private String floorTitle;
    private String floorName;
    private String backgroundImageUrl;
    private String moreUrl;
    private String moreSnapshotId;
    private List<AppMapBean> appMapList;
    private List<BannerMapBean> bannerLinkImage;
    private String floorId;
    private int isShowTitle;
    private int isShowTitlePic;
    private String titlePicColor;
    private String titlePicUrl;

    public int getFloorType() {
        return floorType;
    }

    public void setFloorType(int floorType) {
        this.floorType = floorType;
    }

    public int getIsShowTag() {
        return isShowTag;
    }

    public void setIsShowTag(int isShowTag) {
        this.isShowTag = isShowTag;
    }

    public String getFloorTitle() {
        return floorTitle;
    }

    public void setFloorTitle(String floorTitle) {
        this.floorTitle = floorTitle;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getMoreUrl() {
        return moreUrl;
    }

    public void setMoreUrl(String moreUrl) {
        this.moreUrl = moreUrl;
    }

    public List<AppMapBean> getAppMapList() {
        return appMapList;
    }

    public void setAppMapList(List<AppMapBean> appMapList) {
        this.appMapList = appMapList;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public List<BannerMapBean> getBannerLinkImage() {
        return bannerLinkImage;
    }

    public void setBannerLinkImage(List<BannerMapBean> bannerLinkImage) {
        this.bannerLinkImage = bannerLinkImage;
    }

    public String getMoreSnapshotId() {
        return moreSnapshotId;
    }

    public void setMoreSnapshotId(String moreSnapshotId) {
        this.moreSnapshotId = moreSnapshotId;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    public int getIsShowTitle() {
        return isShowTitle;
    }

    public void setIsShowTitle(int isShowTitle) {
        this.isShowTitle = isShowTitle;
    }

    public int getIsShowTitlePic() {
        return isShowTitlePic;
    }

    public void setIsShowTitlePic(int isShowTitlePic) {
        this.isShowTitlePic = isShowTitlePic;
    }

    public String getTitlePicColor() {
        return titlePicColor;
    }

    public void setTitlePicColor(String titlePicColor) {
        this.titlePicColor = titlePicColor;
    }

    public String getTitlePicUrl() {
        return titlePicUrl;
    }

    public void setTitlePicUrl(String titlePicUrl) {
        this.titlePicUrl = titlePicUrl;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }
}
