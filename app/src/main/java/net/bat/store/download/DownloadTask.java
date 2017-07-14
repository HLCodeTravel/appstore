
package net.bat.store.download;

import android.text.TextUtils;

/**
 * DownloadTask class this class used to create a download task.
 * 
 * @author offbye@gmail.com
 */
public class DownloadTask {

    private int appAttribute;
    private String floortitle;
    private String appCategory;
    private String developer;
    private String downloadstyle;
    private String appId;
    private String appNameEn;
    /**
     * download url
     */
    private String url;

    /**
     * fileName
     */
    private String fileName;

    private String title;

    private String thumbnail;

    /**
     * 在编辑模式下是否选中
     */
    private boolean isSelected;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Download file save path,default path is "/sdcard/download"
     */
    private String filePath;

    /**
     * download finished Size
     */
    private long finishedSize;

    /**
     * total Size
     */
    private long totalSize;

    /**
     * finished percent
     */
    private int percent;

    private int speed;

    private String packageName;
    
    /**
     * download state
     */
    private volatile DownloadState downloadState;

    private InstallState installState;

    /**
     *签名之后正确的下载地址
     */
    private String realDownloadUrl;

    private String floorId;


    public DownloadTask(String url, String packageName, String filePath, String fileName, String title, String thumbnail, String floortitle, String appCategory, String developer, int appAttribute, String floorId, String appId,String appName) {

        if (TextUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("invalid fileName");
        }
        this.url = url;
        this.fileName = fileName;
        this.title = title;
        this.thumbnail = thumbnail;
        this.filePath = filePath;
        this.floortitle=floortitle;
        this.appCategory=appCategory;
        this.packageName = packageName;
        this.developer=developer;
        this.appAttribute=appAttribute;
        this.floorId = floorId;
        this.appId=appId;
        this.appNameEn=appName;
    }

    /**
     * get url
     * 
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * set url
     * 
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * get fileName
     * 
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * set fileName
     * 
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * get filePath
     * 
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * set filePath
     * 
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * get finishedSize
     * 
     * @return the finishedSize
     */
    public long getFinishedSize() {
        return finishedSize;
    }

    /**
     * set finishedSize
     * 
     * @param finishedSize the finishedSize to set
     */
    public void setFinishedSize(long finishedSize) {
        this.finishedSize = finishedSize;
    }

    /**
     * get totalSize
     * 
     * @return the totalSize
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * set totalSize
     * 
     * @param totalSize the totalSize to set
     */
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * get percent
     * 
     * @return the percent
     */
    public int getPercent() {
        return percent;
    }

    /**
     * set download percent
     * 
     * @param percent
     */
    public void setPercent(int percent) {
        this.percent = percent;
    }
    
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * get downloadState
     * 
     * @return the downloadState
     */
    public DownloadState getDownloadState() {
        return downloadState;
    }

    /**
     * set downloadState
     * 
     * @param downloadState the downloadState to set
     */
    public void setDownloadState(DownloadState downloadState) {
        this.downloadState = downloadState;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public String getFloortitle() {
        return floortitle;
    }

    public void setFloortitle(String floortitle) {
        this.floortitle = floortitle;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }
	
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public InstallState getInstallState() {
        return installState;
    }

    public void setInstallState(InstallState installState) {
        this.installState = installState;
    }
	
	  public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public int getAppAttribute() {
        return appAttribute;
    }

    public void setAppAttribute(int appAttribute) {
        this.appAttribute = appAttribute;
    }

    public String getRealDownloadUrl() {
        return realDownloadUrl;
    }

    public void setRealDownloadUrl(String realDownloadUrl) {
        this.realDownloadUrl = realDownloadUrl;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }


    public String getDownloadstyle() {
        return downloadstyle;
    }

    public void setDownloadstyle(String downloadstyle) {
        this.downloadstyle = downloadstyle;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
        //result = prime * result + ((floorId == null) ? 0 : floorId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DownloadTask other = (DownloadTask) obj;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (filePath == null) {
            if (other.filePath != null)
                return false;
        } else if (!filePath.equals(other.filePath))
            return false;
//        if (floorId == null) {
//            if (other.floorId != null)
//                return false;
//        } else if (!floorId.equals(other.floorId))
//            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DownloadTask [url=" + url + ", finishedSize=" + finishedSize + ", totalSize="
                + totalSize + ", dlPercent=" + percent + ", downloadState=" + downloadState
                + ", fileName=" + fileName + ", title=" + title + "]";
    }

}
