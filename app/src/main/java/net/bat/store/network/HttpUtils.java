//package net.bat.store.network;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import net.bat.store.constans.AppStoreConstant;
//import net.bat.store.utils.ConstantUtils;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.UUID;
//
//public class HttpUtils {
//
//    private static final String TAG = "HttpUtils";
//    /** parameters separator **/
//    public static final String PARAMETERS_SEPARATOR   = "&";
//    /** equal sign **/
//    public static final String EQUAL_SIGN             = "=";
//    private static HttpUtils sInstance;
//
//    private HttpUtils() {
//    }
//
//    public static HttpUtils getInstance(){
//        if(sInstance==null){
//            synchronized (HttpUtils.class){
//                if(sInstance==null){
//                    sInstance=new HttpUtils();
//                }
//            }
//        }
//        return sInstance;
//    }
//
//    public static HttpResponse httpGet(HttpRequest request) {
//        Log.i(TAG, "httpGet: ");
//        if (request == null) {
//            return null;
//        }
//        BufferedReader input = null;
//        HttpURLConnection con = null;
//        try {
//            URL url = new URL(request.getUrl());
//            try {
//                HttpResponse response = new HttpResponse(request.getUrl());
//                // default gzip encode
//                con = (HttpURLConnection)url.openConnection();
//                setURLConnection(request, con);
//                input = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                StringBuilder sb = new StringBuilder();
//                String s;
//                while ((s = input.readLine()) != null) {
//                    sb.append(s).append("\n");
//                }
//                response.setResponseBody(sb.toString());
//                setHttpResponse(con, response);
//                return response;
//            } catch (IOException e) {
//                Log.i(TAG, "httpGet: "+e.toString());
//                e.printStackTrace();
//            }
//        } catch (MalformedURLException e1) {
//            e1.printStackTrace();
//        } finally {
//            IOUtils.closeQuietly(input);
//            if (con != null) {
//                con.disconnect();
//            }
//        }
//        return null;
//    }
//
//    public static HttpResponse httpGet(String httpUrl) {
//        return httpGet(new HttpRequest(httpUrl));
//    }
//
//    public static void httpGet(String url, HttpListener listener) {
//        new HttpStringAsyncTask(listener).execute(url);
//    }
//
//
//    public static HttpResponse httpPost(HttpRequest request) {
//        if (request == null) {
//            return null;
//        }
//        String end = "\r\n";
//        String twoHyphens = "--";
//        String boundary = UUID.randomUUID().toString();
//        BufferedReader input = null;
//        HttpURLConnection con = null;
//        InputStream inpustream=null;
//        DataOutputStream ds=null;
//        try {
//            URL url = new URL(request.getUrl());
//            try {
//                HttpResponse response = new HttpResponse(request.getUrl());
//                con = (HttpURLConnection)url.openConnection();
//                setURLConnection(request, con);
//                con.setDoInput(true);
//                con.setDoOutput(true);
//                con.setUseCaches(false);
//                con.setRequestMethod("POST");
//                con.setRequestProperty("Connection", "Keep-Alive");
//                con.setRequestProperty("Charset", "UTF-8");
//                con.setRequestProperty("Content-Type",
//                        "multipart/form-data;boundary=" + boundary );
//                ds = new DataOutputStream(con.getOutputStream());
//
//                ds.writeBytes(twoHyphens + boundary + end);
//                ds.writeBytes("Content-Disposition: form-data;name=\"" + request.getLogName() +
//                        "\"" + end);
//                ds.writeBytes("Content-Type: text/plain; charset=US-ASCII" + end);
//                ds.writeBytes("Content-Transfer-Encoding: 8bit" + end);
//                ds.writeBytes(end);
//                ds.writeBytes(request.getLogContent());
//                ds.writeBytes(end);
//                ds.writeBytes(twoHyphens + boundary + "-" + twoHyphens + end);
//                ds.flush();
//                response.setResponseCode(con.getResponseCode());
//                return response;
//            } catch (IOException e) {
//                Log.i(TAG, "httpPost: --"+e.toString());
//                e.printStackTrace();
//            }
//        } catch (MalformedURLException e1) {
//            e1.printStackTrace();
//        } finally {
//            IOUtils.closeQuietly(input);
//            IOUtils.closeQuietly(inpustream);
//            if (con != null) {
//                con.disconnect();
//            }
//        }
//        return null;
//    }
//
//    public static HttpResponse requestPost(HttpRequest request) {
//        if (request == null) {
//            return null;
//        }
//        Log.i(TAG, "requestPost: ");
//        BufferedReader input = null;
//        HttpURLConnection con = null;
//        InputStream inpustream=null;
//        try {
//            URL url = new URL(request.getUrl());
//            try {
//                HttpResponse response = new HttpResponse(request.getUrl());
//                con = (HttpURLConnection)url.openConnection();
//                setURLConnection(request, con);
//                con.setRequestMethod("POST");
//                con.setDoOutput(true);
//                String paras = request.getParas();
//                if (!isEmpty(paras)) {
//                    con.getOutputStream().write(paras.getBytes());
//                }
//                inpustream=con.getInputStream();
//                input = new BufferedReader(new InputStreamReader(inpustream));
//                StringBuilder sb = new StringBuilder();
//                String s;
//                while ((s = input.readLine()) != null) {
//                    sb.append(s).append("\n");
//                }
//                response.setResponseBody(sb.toString());
//                setHttpResponse(con, response);
//                return response;
//            } catch (IOException e) {
//                Log.i(TAG, "httpPost: --"+e.toString());
//                e.printStackTrace();
//            }
//        } catch (MalformedURLException e1) {
//            Log.i(TAG, "requestPost: "+e1.toString());
//            e1.printStackTrace();
//        } finally {
//            IOUtils.closeQuietly(input);
//            IOUtils.closeQuietly(inpustream);
//            if (con != null) {
//                con.disconnect();
//            }
//        }
//        return null;
//    }
//
//
//    public void httpPostString(String httpUrl,String logName,String logContent,HttpListener listener) {
//        HttpRequest httpRequest=new HttpRequest(httpUrl, logName,logContent);
//        HttpRequestAsyncTask httpRequestAsyncTask=new HttpRequestAsyncTask(listener);
//        httpRequestAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,httpRequest);
//    }
//
//    public void httpPostString(String httpUrl, Map<String, String> parasMap,HttpListener listener) {
//        HttpRequest httpRequest=new HttpRequest(httpUrl, parasMap);
//        httpRequest.setConnectTimeout(AppStoreConstant.CONNECT_TIME_OUT);
//        httpRequest.setReadTimeout(AppStoreConstant.READ_TIME_OUT);
//        HttpRequestAsyncTask httpRequestAsyncTask=new HttpRequestAsyncTask(listener);
//        httpRequestAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,httpRequest);
//    }
//
//    private static void setURLConnection(HttpRequest request, HttpURLConnection urlConnection) {
//        if (request == null || urlConnection == null) {
//            return;
//        }
//
//        setURLConnection(request.getRequestProperties(), urlConnection);
//        if (request.getConnectTimeout() >= 0) {
//            urlConnection.setConnectTimeout(request.getConnectTimeout());
//        }
//        if (request.getReadTimeout() >= 0) {
//            urlConnection.setReadTimeout(request.getReadTimeout());
//        }
//    }
//
//    public static void setURLConnection(Map<String, String> requestProperties, HttpURLConnection urlConnection) {
//        if (ConstantUtils.isEmpty(requestProperties) || urlConnection == null) {
//            return;
//        }
//
//        for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
//            String str=entry.getKey();
//            if (!(str == null || str.length() == 0)) {
//                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
//            }
//        }
//    }
//
//    private static void setHttpResponse(HttpURLConnection urlConnection, HttpResponse response) {
//        if (response == null || urlConnection == null) {
//            return;
//        }
//        try {
//            response.setResponseCode(urlConnection.getResponseCode());
//        } catch (IOException e) {
//            response.setResponseCode(-1);
//        }
//        response.setResponseHeader(AppStoreConstant.EXPIRES, urlConnection.getHeaderField("Expires"));
//        response.setResponseHeader(AppStoreConstant.CACHE_CONTROL, urlConnection.getHeaderField("Cache-Control"));
//    }
//
//
//    public static String joinParasWithEncodedValue(Map<String, String> parasMap) {
//        StringBuilder paras = new StringBuilder("");
//        if (parasMap != null && parasMap.size() > 0) {
//            Iterator<Map.Entry<String, String>> ite = parasMap.entrySet().iterator();
//            try {
//                while (ite.hasNext()) {
//                    Map.Entry<String, String> entry = (Map.Entry<String, String>)ite.next();
//                    paras.append(entry.getKey()).append(EQUAL_SIGN).append(utf8Encode(entry.getValue()));
//                    if (ite.hasNext()) {
//                        paras.append(PARAMETERS_SEPARATOR);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return paras.toString();
//    }
//
//    private class HttpRequestAsyncTask extends AsyncTask<HttpRequest, Void, HttpResponse> {
//
//        private HttpListener listener;
//
//        public HttpRequestAsyncTask(HttpListener listener) {
//            this.listener = listener;
//        }
//
//        protected HttpResponse doInBackground(HttpRequest... httpRequest) {
//            if (isEmpty(httpRequest)) {
//                Log.i(TAG, "doInBackground: httpRequest is null");
//                return null;
//            }
//            if(httpRequest[0].getLogName()!=null){
//                return httpPost(httpRequest[0]);
//            }else{
//                return requestPost(httpRequest[0]);
//            }
//        }
//
//        protected void onPreExecute() {
//            if (listener != null) {
//                listener.onPreGet();
//            }
//        }
//
//        protected void onPostExecute(HttpResponse httpResponse) {
//            if (listener != null) {
//                listener.onPostGet(httpResponse);
//            }
//        }
//    }
//
//    private static class HttpStringAsyncTask extends AsyncTask<String, Void, HttpResponse> {
//
//        private HttpListener listener;
//
//        public HttpStringAsyncTask(HttpListener listener) {
//            this.listener = listener;
//        }
//
//        protected HttpResponse doInBackground(String... param) {
//            if (isEmpty(param)) {
//                return null;
//            }
//            return httpGet(param[0]);
//        }
//
//        protected void onPreExecute() {
//            if (listener != null) {
//                listener.onPreGet();
//            }
//        }
//
//        protected void onPostExecute(HttpResponse httpResponse) {
//            if (listener != null) {
//                listener.onPostGet(httpResponse);
//            }
//        }
//    }
//
//    public static abstract class HttpListener {
//        protected void onPreGet() {}
//        protected void onPostGet(HttpResponse httpResponse) {}
//    }
//
//
//    public static boolean isEmpty(CharSequence str) {
//        return (str == null || str.length() == 0);
//    }
//
//    public static <V> boolean isEmpty(V[] sourceArray) {
//        return (sourceArray == null || sourceArray.length == 0);
//    }
//
//
//    public static String utf8Encode(String str) {
//        if (!isEmpty(str) && str.getBytes().length != str.length()) {
//            try {
//                return URLEncoder.encode(str, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
//            }
//        }
//        return str;
//    }
//
//}
