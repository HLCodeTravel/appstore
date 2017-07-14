
package net.bat.store.download;

/**
 * download state enum
 * 
 * @author offbye@gmail.com
 */
public enum DownloadState {
    /**
     * init
     */
    WAITING,

    /**
     * downloading
     */
    DOWNLOADING,
    /**
     * download failed, the reason may be network error, file io error etc.
     */
    FAILED,
    /**
     * download finished
     */
    FINISHED,

    /**
     * download paused
     */
    PAUSE,

    STOP
}
