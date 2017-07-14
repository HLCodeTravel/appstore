//package net.bat.store.network;
//
//import java.net.URLConnection;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class HttpRequest {
//
//    private String              url;
//    private int                 connectTimeout;
//    private int                 readTimeout;
//    private Map<String, String> parasMap;
//    private Map<String, String> requestProperties;
//    private String logName;
//    private String logContent;
//
//    public HttpRequest(String url) {
//        this.url = url;
//        this.connectTimeout = -1;
//        this.readTimeout = -1;
//        requestProperties = new HashMap<String, String>();
//    }
//
//    public HttpRequest(String url, Map<String, String> parasMap) {
//        this.url = url;
//        this.parasMap = parasMap;
//        this.connectTimeout = -1;
//        this.readTimeout = -1;
//        requestProperties = new HashMap<String, String>();
//    }
//
//    public HttpRequest(String url,String logName,String logContent){
//        this.url = url;
//        this.logName=logName;
//        this.logContent=logContent;
//        this.connectTimeout = -1;
//        this.readTimeout = -1;
//        requestProperties = new HashMap<String, String>();
//    }
//
//    public String getLogName() {
//        return logName;
//    }
//
//    public void setLogName(String logName) {
//        this.logName = logName;
//    }
//
//    public String getLogContent() {
//        return logContent;
//    }
//
//    public void setLogContent(String logContent) {
//        this.logContent = logContent;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    /**
//     * @return
//     * @see URLConnection#getConnectTimeout()
//     */
//    public int getConnectTimeout() {
//        return connectTimeout;
//    }
//
//    /**
//     * @param timeoutMillis
//     * @see URLConnection#setConnectTimeout(int)
//     */
//    public void setConnectTimeout(int timeoutMillis) {
//        if (timeoutMillis < 0) {
//            throw new IllegalArgumentException("timeout can not be negative");
//        }
//        connectTimeout = timeoutMillis;
//    }
//
//    /**
//     * @return
//     * @see URLConnection#getReadTimeout()
//     */
//    public int getReadTimeout() {
//        return readTimeout;
//    }
//
//    /**
//     * @param timeoutMillis
//     * @see URLConnection#setReadTimeout(int)
//     */
//    public void setReadTimeout(int timeoutMillis) {
//        if (timeoutMillis < 0) {
//            throw new IllegalArgumentException("timeout can not be negative");
//        }
//        readTimeout = timeoutMillis;
//    }
//
//    /**
//     * get paras map
//     *
//     * @return
//     */
//    public Map<String, String> getParasMap() {
//        return parasMap;
//    }
//
//    /**
//     * set paras map
//     *
//     * @param parasMap
//     */
//    public void setParasMap(Map<String, String> parasMap) {
//        this.parasMap = parasMap;
//    }
//
//    /**
//     * @return paras as string
//     */
//    public String getParas() {
//        return HttpUtils.joinParasWithEncodedValue(parasMap);
//    }
//
//    /**
//     * @param field
//     * @param newValue
//     * @see URLConnection#setRequestProperty(String, String)
//     */
//    public void setRequestProperty(String field, String newValue) {
//        requestProperties.put(field, newValue);
//    }
//
//    /**
//     * @param field
//     * @see URLConnection#getRequestProperty(String)
//     */
//    public String getRequestProperty(String field) {
//        return requestProperties.get(field);
//    }
//
//    /**
//     * same to {@link #setRequestProperty(String, String)} filed is User-Agent
//     *
//     * @param value
//     * @see URLConnection#setRequestProperty(String, String)
//     */
//    public void setUserAgent(String value) {
//        requestProperties.put("User-Agent", value);
//    }
//
//    /**
//     * @return
//     */
//    public Map<String, String> getRequestProperties() {
//        return requestProperties;
//    }
//
//    /**
//     * @param requestProperties
//     */
//    public void setRequestProperties(Map<String, String> requestProperties) {
//        this.requestProperties = requestProperties;
//    }
//}
