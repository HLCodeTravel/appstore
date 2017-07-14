package net.bat.store.bean.floor;

/**
 * Created by bingbing.li on 2017/1/20.
 */
public class BannerMapBean {

    private String imageUrl;
    private String link;
    private int rank;
    private int linkType;
    private int appAttribute;
    private String startTime;
    private String endTime;
    private String slotId;
    private String appId;

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public int getAppAttribute() {
        return appAttribute;
    }

    public void setAppAttribute(int appAttribute) {
        this.appAttribute = appAttribute;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
