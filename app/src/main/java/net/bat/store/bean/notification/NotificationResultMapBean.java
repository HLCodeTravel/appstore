package net.bat.store.bean.notification;

/**
 * usage
 *
 * @author 金新
 * @date 2017/2/23
 * =====================================
 * Copyright (c)2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */
public class NotificationResultMapBean {

    private String linkUrl;
    private String title;
    private String iconUrl;
    private String description;
    private String linkType;
    private String messageType;
    private String bannerUrl;


    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }
}
