package net.bat.store.bean.log;

/**
 * Created by bingbing.li on 2017/5/4.
 */
public class Option {

    private int mcc_country;
    private int switch_aha;
    private int duration;
    private int firststartlog;
    private int startrunlog;
    private int operationlog;
    private int resourcelog;
    private int searchlog;
    private int updatelog;

    public int getResourcelog() {
        return resourcelog;
    }

    public void setResourcelog(int resourcelog) {
        this.resourcelog = resourcelog;
    }

    public int getMcc_country() {
        return mcc_country;
    }

    public void setMcc_country(int mcc_country) {
        this.mcc_country = mcc_country;
    }

    public int getSwitch_aha() {
        return switch_aha;
    }

    public void setSwitch_aha(int switch_aha) {
        this.switch_aha = switch_aha;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFirststartlog() {
        return firststartlog;
    }

    public void setFirststartlog(int firststartlog) {
        this.firststartlog = firststartlog;
    }

    public int getStartrunlog() {
        return startrunlog;
    }

    public void setStartrunlog(int startrunlog) {
        this.startrunlog = startrunlog;
    }

    public int getOperationlog() {
        return operationlog;
    }

    public void setOperationlog(int operationlog) {
        this.operationlog = operationlog;
    }

    public int getSearchlog() {
        return searchlog;
    }

    public void setSearchlog(int searchlog) {
        this.searchlog = searchlog;
    }

    public int getUpdatelog() {
        return updatelog;
    }

    public void setUpdatelog(int updatelog) {
        this.updatelog = updatelog;
    }
}
