//package net.bat.store.network;
//
//
//import net.bat.store.constans.AppStoreConstant;
//
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class HttpResponse {
//
//    private String              url;
//    /** http response content **/
//    private String              responseBody;
//    private InputStream inputStream;
//    private Map<String, Object> responseHeaders;
//    /** type to mark this response **/
//    private int                 type;
//    /** expired time in milliseconds **/
//    private long                expiredTime;
//    /** this is a client mark, whether this response is in client cache **/
//    private boolean             isInCache;
//
//    private boolean             isInitExpiredTime;
//
//    private int                 responseCode = -1;
//
//    public HttpResponse(String url) {
//        this.url = url;
//        type = 0;
//        isInCache = false;
//        isInitExpiredTime = false;
//        responseHeaders = new HashMap<String, Object>();
//    }
//
//    public HttpResponse() {
//        responseHeaders = new HashMap<String, Object>();
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public String getResponseBody() {
//        return responseBody;
//    }
//
//    public void setResponseBody(String responseBody) {
//        this.responseBody = responseBody;
//    }
//
//    public void setInputStram(InputStream inputStram){
//        this.inputStream=inputStram;
//    }
//
//    public InputStream getInputStream(){
//        return inputStream;
//    }
//
//    /**
//     * get reponse code
//     *
//     * @return An <code>int</code> representing the three digit HTTP Status-Code.
//     *         <ul>
//     *         <li>1xx: Informational
//     *         <li>2xx: Success
//     *         <li>3xx: Redirection
//     *         <li>4xx: Client Error
//     *         <li>5xx: Server Error
//     *         <li>-1: http error
//     *         </ul>
//     */
//    public int getResponseCode() {
//        return responseCode;
//    }
//
//    public void setResponseCode(int responseCode) {
//        this.responseCode = responseCode;
//    }
//
//    /**
//     * not avaliable now
//     *
//     * @return
//     */
//    private Map<String, Object> getResponseHeaders() {
//        return responseHeaders;
//    }
//
//    public void setResponseHeaders(Map<String, Object> responseHeaders) {
//        this.responseHeaders = responseHeaders;
//    }
//
//    /**
//     * get type
//     * <ul>
//     * <li>type to mark this response, default is 0</li>
//     * <li>it will be used in {@link #(android.content.Context, int)}</li>
//     * </ul>
//     *
//     * @return the type
//     */
//    public int getType() {
//        return type;
//    }
//
//    /**
//     * set type
//     * <ul>
//     * <li>type to mark this response, default is 0, cannot be smaller than 0.</li>
//     * <li>it will be used in {@link #(android.content.Context, int)}</li>
//     * </ul>
//     *
//     * @param type the type to set
//     */
//    public void setType(int type) {
//        if (type < 0) {
//            throw new IllegalArgumentException("The type of HttpResponse cannot be smaller than 0.");
//        }
//        this.type = type;
//    }
//
//    /**
//     * set expired time in millis
//     *
//     * @param expiredTime
//     */
//    public void setExpiredTime(long expiredTime) {
//        isInitExpiredTime = true;
//        this.expiredTime = expiredTime;
//    }
//
//
//    /**
//     * get isInCache, this is a client mark, whethero is in client cache
//     *
//     * @return the isInCache
//     */
//    public boolean isInCache() {
//        return isInCache;
//    }
//
//    /**
//     * set isInCache, this is a client mark, whethero is in client cache
//     *
//     * @param isInCache the isInCache to set
//     * @return
//     */
//    public HttpResponse setInCache(boolean isInCache) {
//        this.isInCache = isInCache;
//        return this;
//    }
//
//    /**
//     * http expires in reponse header
//     *
//     * @return null represents http error or no expires in response headers
//     */
//    public String getExpiresHeader() {
//        try {
//            return responseHeaders == null ? null : (String)responseHeaders.get(AppStoreConstant.EXPIRES);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//
//
//
//
//    /**
//     * set response header
//     *
//     * @param field
//     * @param newValue
//     */
//    public void setResponseHeader(String field, String newValue) {
//        if (responseHeaders != null) {
//            responseHeaders.put(field, newValue);
//        }
//    }
//
//    /**
//     * get response header, not avaliable now
//     *
//     * @param field
//     */
//    private Object getResponseHeader(String field) {
//        return responseHeaders == null ? null : responseHeaders.get(field);
//    }
//}
