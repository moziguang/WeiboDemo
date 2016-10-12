package com.lwq.core.http;

import java.util.*;

import android.os.Handler;

import com.lwq.base.http.handler.HttpRequestHandler;
import com.lwq.base.http.listener.IHttpResponseListener;
import com.lwq.base.http.model.HttpParameter;
import com.lwq.base.http.model.HttpRequest;
import com.lwq.base.util.JsonUtils;
import com.lwq.base.util.Log;
import com.lwq.base.util.StatusCodeDef;
import org.json.JSONObject;

/*
 * Description : 
 *
 * Creation    : 2016/10/12
 * Author      : moziguang@126.com
 */

public class WeiboHttpRequest extends HttpRequestHandler<JSONObject> {
    private String mPath;
    private List<HttpParameter> mHttpParameters;
    private Map<String, String> mRequestProperties;

    public WeiboHttpRequest(String path, List<HttpParameter> httpParameters,
                            IHttpResponseListener<JSONObject> httpResponseListener) {
        this(path,httpParameters,null,httpResponseListener);
    }

    public WeiboHttpRequest(String path, List<HttpParameter> httpParameters, Map<String, String> requestProperties,
                            IHttpResponseListener<JSONObject> httpResponseListener) {
        super(null, httpResponseListener);
        this.mPath = path;
        this.mHttpParameters = httpParameters;
        this.mRequestProperties = requestProperties;
    }

    @Override
    protected String getServiceBaseUrl() {
        return WeiboConstants.HTTP_BASE_URL;
    }

    @Override
    protected int getRequestType() {
        return HttpRequest.TYPE_GET;
    }

    @Override
    protected String getRequestPath() {
        return mPath;
    }

    @Override
    protected List<HttpParameter> getRequestParameters() {
        return mHttpParameters;
    }

    @Override
    protected Map<String, String> getRequestProperties() {
        return mRequestProperties;
    }

    @Override
    protected String getCacheFilePath() {
        return null;
    }

    @Override
    protected JSONObject handleData(byte[] data, String mimeType) throws Exception {
        String str = new String(data);
        Log.d(TAG, str);
        JSONObject body = new JSONObject(str);
        int errorCode = JsonUtils.getInt(body,"error_code", StatusCodeDef.SUCCESS);

        if(errorCode!=StatusCodeDef.SUCCESS){
            String errorMsg = JsonUtils.getString(body,"error");
            setError(errorCode,errorMsg);
        }
        return body;
    }
}
