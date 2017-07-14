package net.bat.store.bean.log;

/**
 * Created by bingbing.li on 2017/5/4.
 */
public class UploadOption {

    private long curTime;
    private String ip;
    private Option option;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getCurTime() {
        return curTime;
    }

    public void setCurTime(long curTime) {
        this.curTime = curTime;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }
}
