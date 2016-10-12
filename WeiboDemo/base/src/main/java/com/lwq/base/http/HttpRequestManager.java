package com.lwq.base.http;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.lwq.base.http.handler.BaseRequestHandler;
import com.lwq.base.http.model.FileHolder;
import com.lwq.base.http.model.HttpParameter;
import com.lwq.base.http.model.HttpRequest;
import com.lwq.base.util.AtomicIntegerUtil;
import com.lwq.base.util.DefaultThreadFactory;
import com.lwq.base.util.HttpUtils;
import com.lwq.base.util.Log;
import com.lwq.base.util.StatusCodeDef;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public class HttpRequestManager {

    protected static final String TAG = HttpRequestManager.class.getSimpleName();
    private static final int BUFFER_SIZE = 10 * 1024;
    private static final int SO_TIMEOUT = 8 * 1000;
    private static final int CON_TIMEOUT = 8 * 1000;
    private static final String KEY_SET_COOKIE = "Set-Cookie";
    private static final String KEY_COOKIE = "Cookie";
    /**
     * onProgress回调的触发频率
     */
    private static final int PROGRESS_RATE = 1000;

    private static final HttpRequestManager sInstance = new HttpRequestManager();

    /**
     * 3个常驻线程，最多3个线程
     */
    private ExecutorService mHttpExecutorService;

    private HashMap<HttpRequest, HttpRquestTask> mRequestToTaskMap;

    private final Object mRequestToTaskMapLock = new Object();

    private Context mContext;
    private CookieManager mWebkitCookieManager;

    public static HttpRequestManager getInstance() {
        return sInstance;
    }

    private HttpRequestManager() {
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mHttpExecutorService = new ThreadPoolExecutor(3, 3, 60L, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>(), new DefaultThreadFactory("http_"));
        mRequestToTaskMap = new HashMap<>();

        CookieSyncManager.createInstance(mContext);
        mWebkitCookieManager = CookieManager.getInstance();
        mWebkitCookieManager.setAcceptCookie(true);
    }

    private URL getAddress(int tryTime, HttpRequest request) throws MalformedURLException {
        String urlStr = request.getUrl();
        List<HttpParameter> parameterList = request.getRequestParamList();
        if (request.getType() == HttpRequest.TYPE_GET) {
            StringBuffer urlBuffer = new StringBuffer();
            urlBuffer.append(urlStr);
            if (parameterList != null) {
                urlBuffer.append("?");
                for (HttpParameter parameter : parameterList) {
                    urlBuffer.append(HttpUtils.getUrlEncodeStr(parameter.key));
                    urlBuffer.append("=");
                    if (parameter.value != null) {
                        urlBuffer.append(HttpUtils.getUrlEncodeStr(parameter.value.toString()));
                    }
                    urlBuffer.append("&");
                }
            }
            urlStr = urlBuffer.toString();
        }
        URL url = new URL(urlStr);
        Log.d(TAG, " getAddress url = " + url + " tryTime = " + tryTime);
        return url;
    }

    private void updateCookie(String url, String setCookieStr) {
        if (!TextUtils.isEmpty(setCookieStr)) {
            mWebkitCookieManager.setCookie(url, setCookieStr);
        }
        mWebkitCookieManager.removeExpiredCookie();
    }

    private String getCookie(String url) {
        mWebkitCookieManager.removeExpiredCookie();
        return mWebkitCookieManager.getCookie(url);
    }

    public int sendHttpRequest(HttpRequest request,
                               @SuppressWarnings("rawtypes") BaseRequestHandler httpDataListener) {
        if (request == null) {
            return -1;
        }
        synchronized (mRequestToTaskMapLock) {
            if (mRequestToTaskMap.containsKey(request)) {
                return -2;
            }

            HttpRquestTask task = new HttpRquestTask(request, httpDataListener);
            mRequestToTaskMap.put(request, task);
            mHttpExecutorService.execute(task);
        }
        return AtomicIntegerUtil.getAtomicInteger();
    }

    public void cancelHttpRequest(HttpRequest request) {
        if (request == null) {
            return;
        }
        synchronized (mRequestToTaskMapLock) {
            if (mRequestToTaskMap.containsKey(request)) {
                HttpRquestTask task = mRequestToTaskMap.get(request);
                task.cancel();
                mRequestToTaskMap.remove(request);
            }
        }
    }

    /**
     * 停止并清除所有HTTP请求 会触发oncancel回调
     */
    public void cancelAllRequest() {
        synchronized (mRequestToTaskMapLock) {

            for (HttpRequest request : mRequestToTaskMap.keySet()) {
                HttpRquestTask task = mRequestToTaskMap.get(request);
                task.cancel();
            }
            mRequestToTaskMap.clear();
        }
    }

    private class HttpRquestTask implements Runnable, Comparable<HttpRquestTask> {
        private boolean isCancel;
        private final int mPriority;
        private HttpRequest mRequest;
        private BaseRequestHandler mHttpRequestHandler;

        public HttpRquestTask(HttpRequest request, BaseRequestHandler httpRequestHandler) {
            this.mPriority = request.getPriority();
            this.mRequest = request;
            this.mHttpRequestHandler = httpRequestHandler;
        }

        public synchronized void cancel() {
            isCancel = true;
            if (mHttpRequestHandler != null) {
                mHttpRequestHandler.onCancel();
            }
        }

        public synchronized boolean isCancel() {
            return isCancel;
        }

        @Override
        public int compareTo(HttpRquestTask another) {
            return another.mPriority - mPriority;
        }

        private int setPostParams(OutputStream outputStream, List<HttpParameter> requestParamList) {
            int errorCode = StatusCodeDef.HTTP_OK;
            try {
                for (HttpParameter param : requestParamList) {
                    String temp = URLEncoder.encode(param.key, "UTF8") + "="
                                    + URLEncoder.encode(
                      param.value == null ? "" : param.value.toString(), "UTF8") + "&";
                    outputStream.write(temp.getBytes());
                }
                outputStream.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
                errorCode = StatusCodeDef.ERROR_WRITE_DATA_TO_SERVICE_ERROR;
            }
            return errorCode;
        }

        /**
         * 大文件上传时需要分片上传，否则内存溢出，暂不实现大文件上传。
         */
        private int setMultipartParams(OutputStream outputStream,
                                       List<HttpParameter> requestParamList, String boundary) {
            int errorCode = StatusCodeDef.HTTP_OK;
            try {
                for (HttpParameter param : requestParamList) {
                    if (isCancel()) return errorCode;
                    outputStream.write(("--" + boundary + "\r\n").getBytes());
                    if (param.value instanceof FileHolder) {
                        FileHolder fileHolder = (FileHolder) param.value;
                        outputStream.write(("Content-Disposition: form-data; name=\"" + URLEncoder.encode(param.key, "UTF8") + "\"; filename=\""
                                              + HttpUtils.getUrlEncodeStr(fileHolder.getFileName()) + "\"\r\n").getBytes());
                        outputStream.write(("Content-Type: " + fileHolder.getContentType() + "\r\n\r\n").getBytes());
                        byte[] data = fileHolder.getData();
                        if (data != null) {
                            outputStream.write(data, 0, data.length);
                            outputStream.flush();
                        } else {
                            File file = fileHolder.getFile();
                            if (file.exists() && file.canRead()) {
                                byte[] buf = new byte[BUFFER_SIZE];
                                FileInputStream fin = new FileInputStream(file);
                                try {
                                    int n = -1;
                                    while ((n = fin.read(buf)) > 0) {
                                        if (isCancel()) return errorCode;
                                        outputStream.write(buf, 0, n);
                                        outputStream.flush();
                                    }
                                    outputStream.flush();
                                } finally {
                                    HttpUtils.tryClose(fin);
                                }
                            } else {
                                errorCode = StatusCodeDef.ERROR_READ_LOCAL_FILE_ERROR;
                                break;
                            }
                        }
                    } else {
                        outputStream.write(("Content-Disposition: form-data; name=\"" + URLEncoder.encode(param.key, "UTF8") + "\"\r\n\r\n").getBytes());
                        outputStream.write(URLEncoder.encode(
                          param.value == null ? "" : param.value.toString(), "UTF8").getBytes());
                    }
                    outputStream.write(("\r\n").getBytes());
                    outputStream.flush();
                }
                outputStream.write(("--" + boundary + "--").getBytes());
                outputStream.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
                errorCode = StatusCodeDef.ERROR_WRITE_DATA_TO_SERVICE_ERROR;
            }
            return errorCode;
        }

        @Override
        public void run() {
            if (!isCancel()) {
                int errCode = StatusCodeDef.HTTP_OK;
                String errMsg = "";
                String mimeType = null;
                byte[] data = null;

                List<HttpParameter> parameterList = mRequest.getRequestParamList();
                Log.d(TAG, "get from network : " + mHttpRequestHandler + " type = " + mRequest.getType() + " url: " + mRequest.getUrl());
                if (mHttpRequestHandler.getErrorCode() != 0) {
                    errCode = mHttpRequestHandler.getErrorCode();
                    errMsg = mHttpRequestHandler.getErrorMsg();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "InterruptedException ", e);
                    }
                } else if (!HttpUtils.isConnect(mContext)) {
                    errCode = StatusCodeDef.ERROR_NO_NETWORK;
                    errMsg = "网络连接失败";
                } else {
                    HttpURLConnection conn = null;
                    InputStream inputStream = null;
                    InputStream in = null;
                    ByteArrayOutputStream bytes = null;
                    int curTime = 0;
                    boolean retry = true;

                    while (curTime < mRequest.getRetry() && retry && !isCancel()) {
                        try {
                            errCode = StatusCodeDef.HTTP_OK;
                            errMsg = "";
                            URL url = getAddress(curTime, mRequest);//new URL(urlStr);
                            File cacheFile = null;
                            if (!TextUtils.isEmpty(mRequest.getCacheFilePath())) {
                                String dir = HttpUtils.getDir(mRequest.getCacheFilePath());
                                File dirFile = new File(dir);
                                if (!dirFile.exists()) {
                                    dirFile.mkdirs();
                                }
                                if (!dirFile.exists()) {
                                    errCode = StatusCodeDef.ERROR_WRITE_DATA_TO_LOCAL_CACHE;
                                    errMsg = "create cache dir error : " + dir;
                                }
                                if (errCode == StatusCodeDef.HTTP_OK) {
                                    cacheFile = new File(dirFile, HttpUtils.hashKey(mRequest.getUrl()) + ".tmp");
                                }
                            }

                            if (errCode == StatusCodeDef.HTTP_OK) {
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setDoInput(true);
                                conn.setConnectTimeout(CON_TIMEOUT);
                                conn.setReadTimeout(SO_TIMEOUT);
                                Map<String, String> requestProperties = mRequest.getRequestProperties();
                                String cookieStr = getCookie(mRequest.getUrl());
                                if (requestProperties != null) {
                                    if (!TextUtils.isEmpty(cookieStr)) {
                                        if (requestProperties.containsKey(KEY_COOKIE)) {
                                            String cookie = requestProperties.get(KEY_COOKIE);
                                            requestProperties.put(KEY_COOKIE, cookie + ";" + cookieStr);
                                        } else {
                                            requestProperties.put(KEY_COOKIE, cookieStr);
                                        }
                                    }
                                    for (Map.Entry<String, String> param : requestProperties.entrySet()) {
                                        conn.addRequestProperty(param.getKey(), param.getValue());
                                    }
                                } else {
                                    if (!TextUtils.isEmpty(cookieStr)) {
                                        conn.addRequestProperty(KEY_COOKIE, cookieStr);
                                    }
                                }
                                conn.addRequestProperty("Accept-Encoding", "gzip");
                                if (cacheFile != null && cacheFile.exists() && cacheFile.length() > 0) {
                                    conn.setRequestProperty("Range", "bytes=" + cacheFile.length() + "-");//设置获取实体数据的范围 ,支持断点续传
                                    Log.d(TAG, "setRequestProperty Range=" + cacheFile.length() + " url = " + url);
                                }
                                if (mRequest.getType() == HttpRequest.TYPE_POST) {
                                    //								Log.d(TAG, "send post request url=" + url);
                                    conn.setDoOutput(true);
                                    conn.setRequestMethod("POST");
                                    //PostData不为null则只发送postData，RequestParamList参数不再生效
                                    if (mRequest.getPostData() != null) {
                                        conn.getOutputStream().write(mRequest.getPostData());
                                    } else if (parameterList != null) {
                                        if (HttpUtils.isMultipart(parameterList)) {
                                            conn.setUseCaches(false);// 禁用缓存
                                            String boundary = UUID.randomUUID().toString();
                                            conn.addRequestProperty("Content-type", "multipart/form-data;   boundary=" + boundary);
                                            Log.d(TAG, "send multipart/form-data;   boundary=" + boundary + " url = " + url);
                                            errCode = setMultipartParams(conn.getOutputStream(), mRequest.getRequestParamList(), boundary);
                                        } else {
                                            errCode = setPostParams(conn.getOutputStream(), mRequest.getRequestParamList());
                                        }

                                    }
                                    conn.getOutputStream().flush();
                                } else {
                                    conn.connect();
                                }
                            }

                            int respCode = conn.getResponseCode();
                            if (errCode == StatusCodeDef.HTTP_OK && respCode != -1) {
                                if (respCode == 200 || respCode == 206) {
                                    mimeType = conn.getContentType();
                                    String newCookieStr = conn.getHeaderField(KEY_SET_COOKIE);
                                    updateCookie(mRequest.getUrl(), newCookieStr);
                                    inputStream = conn.getInputStream();
                                    String string = conn.getHeaderField("content-encoding");
                                    if (string != null && string.trim().equals("gzip")) {
                                        in = new GZIPInputStream(inputStream);
                                    } else {
                                        in = inputStream;
                                    }
                                    byte[] buffer = new byte[BUFFER_SIZE];
                                    int total = conn.getContentLength();
                                    int progressSeed = total / PROGRESS_RATE;
                                    int nextNotifySize = progressSeed;
                                    int curSize = 0;

                                    if (cacheFile != null) {//使用文件缓存
                                        RandomAccessFile randomAccessCacheFile = null;
                                        FileOutputStream fout = null;
                                        try {
                                            int len = -1;

                                            long start = getRangeStart(conn);
                                            Log.d(TAG, " content range start :" + start + " cacheFile.length():" + cacheFile.length());
                                            if (start == cacheFile.length()) {
                                                total += start;
                                                curSize += start;
                                                progressSeed = total / PROGRESS_RATE;
                                                nextNotifySize = curSize / progressSeed * progressSeed + progressSeed;
                                                randomAccessCacheFile = new RandomAccessFile(cacheFile, "rw");
                                                randomAccessCacheFile.seek(start);
                                                while (!isCancel() && (len = in.read(buffer)) > 0) {
                                                    randomAccessCacheFile.write(buffer, 0, len);
                                                    curSize += len;
                                                    if (curSize >= nextNotifySize) {
                                                        notifyOnProgress(total, curSize);
                                                        nextNotifySize += progressSeed;
                                                        //                                                        nextNotifySize = nextNotifySize > curSize ? nextNotifySize : (curSize + progressSeed)/progressSeed*progressSeed;
                                                    }
                                                }
                                                randomAccessCacheFile.close();
                                                randomAccessCacheFile = null;
                                                if (!isCancel()) {
                                                    cacheFile.renameTo(new File(mRequest.getCacheFilePath()));
                                                }
                                            } else {
                                                fout = new FileOutputStream(cacheFile);
                                                while (!isCancel() && (len = in.read(buffer)) > 0) {
                                                    fout.write(buffer, 0, len);
                                                    fout.flush();
                                                    curSize += len;
                                                    if (curSize >= nextNotifySize) {
                                                        notifyOnProgress(total, curSize);
                                                        nextNotifySize += progressSeed;
                                                        //                                                        nextNotifySize = nextNotifySize > curSize ? nextNotifySize : (curSize + progressSeed)/progressSeed*progressSeed;
                                                    }
                                                }
                                                if (!isCancel()) {
                                                    cacheFile.renameTo(new File(mRequest.getCacheFilePath()));
                                                }
                                            }
                                        } finally {
                                            HttpUtils.tryClose(randomAccessCacheFile);
                                        }
                                    } else {
                                        bytes = new ByteArrayOutputStream();
                                        int len = -1;

                                        while (!isCancel() && (len = in.read(buffer)) != -1) {
                                            bytes.write(buffer, 0, len);
                                            curSize += len;
                                            if (curSize >= nextNotifySize) {
                                                notifyOnProgress(total, curSize);
                                                nextNotifySize += progressSeed;
                                                //                                                nextNotifySize = nextNotifySize > curSize ? nextNotifySize : (curSize + progressSeed)/progressSeed*progressSeed;
                                            }
                                        }
                                        bytes.flush();
                                        data = bytes.toByteArray();
                                    }
                                } else {
                                    data = null;
                                    errCode = respCode;
                                    errMsg = conn.getResponseMessage();
                                    Log.e(TAG, "http request error:" + errCode + " url:" + url);
                                }
                                if (errCode != 404) {
                                    retry = false;
                                }
                            } else {
                                data = null;
                                if (errCode == StatusCodeDef.HTTP_OK) {
                                    errCode = StatusCodeDef.ERROR_CONNECTION_TIMEOUT;
                                }
                                Log.e(TAG, "doGet error:" + errCode + " error msg:" + errMsg + " url:" + url);
                            }
                        } catch (UnknownHostException e) {
                            data = null;
                            errCode = StatusCodeDef.ERROR_UNKNOWN_HOST;
                            errMsg = e.getLocalizedMessage();
                            Log.e(TAG, "load url error:" + errCode + " error msg:" + errMsg + " url:" + mRequest.getUrl() + " try:" + curTime, e);
                        } catch (ConnectException e) {
                            data = null;
                            errCode = StatusCodeDef.ERROR_CONNECTION_TIMEOUT;
                            errMsg = e.getLocalizedMessage();
                            Log.e(TAG, "load url error:" + errCode + " error msg:" + errMsg + " url:" + mRequest.getUrl() + " try:" + curTime, e);
                        } catch (SocketException e) {
                            data = null;
                            errCode = StatusCodeDef.ERROR_SOCKET_EXCEPTION;
                            errMsg = e.getLocalizedMessage();
                            Log.e(TAG, "load url error:" + errCode + " error msg:" + errMsg + " url:" + mRequest.getUrl() + " try:" + curTime, e);
                        } catch (Exception e) {
                            data = null;
                            errCode = StatusCodeDef.ERROR_OTHER_EXCEPTION;
                            errMsg = e.getLocalizedMessage();
                            Log.e(TAG, "load url error:" + errCode + " error msg:" + errMsg + " url:" + mRequest.getUrl() + " try:" + curTime, e);
                        } finally {
                            curTime++;
                            HttpUtils.tryClose(in);
                            HttpUtils.tryClose(inputStream);
                            HttpUtils.tryClose(bytes);
                            if (conn != null) {
                                conn.disconnect();
                            }
                        }
                    }
                }
                if (!isCancel()) {
                    notifyListeners(data, mimeType, errCode, errMsg, mRequest.getCacheFilePath());
                }
            }
        }

        private long getRangeStart(URLConnection conn) {
            String rangeStr = conn.getHeaderField("Content-Range");
            Log.d(TAG, "getRangeStart rangeStr = " + rangeStr);
            long range = -1;
            if (!TextUtils.isEmpty(rangeStr)) {
                try {
                    rangeStr = rangeStr.substring("bytes ".length(), rangeStr.indexOf("-"));
                    range = Long.parseLong(rangeStr);
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "getRangeStart IndexOutOfBoundsException", e);
                }
            }
            return range;
        }

        /**
         * @param data
         * @param mimeType
         * @param errCode
         * @param errMsg
         * @param cacheFilePath
         */
        private void notifyListeners(byte[] data, String mimeType, int errCode, String errMsg,
                                     String cacheFilePath) {
            BaseRequestHandler handler = null;
            if (mHttpRequestHandler != null) {
                synchronized (mRequestToTaskMapLock) {
                    if (mRequestToTaskMap.containsKey(mRequest)) {
                        mRequestToTaskMap.remove(mRequest);
                        handler = mHttpRequestHandler;
                    }
                }
                if (handler != null) {
                    handler.onResult(data, mimeType, errCode, errMsg, cacheFilePath);
                }
            }
        }

        /**
         * @param total
         * @param curSize
         */
        private void notifyOnProgress(int total, int curSize) {
            if (mHttpRequestHandler != null) {
                mHttpRequestHandler.onProgress(total, curSize);
            }
        }

    }


}
