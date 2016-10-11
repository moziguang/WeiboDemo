package com.lwq.base.http.handler;

import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.text.TextUtils;

import com.lwq.base.http.listener.IHttpResponseListener;
import com.lwq.base.http.model.HttpParameter;
import com.lwq.base.http.model.HttpRequest;
import com.lwq.base.util.HttpUtils;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public abstract class HttpRequestHandler<T>  extends BaseRequestHandler<T>{

	/**
	 * @param httpResponseListener 
	 * @param handler 如果handler为null，回调httpResponseListener会在后台线程进行，否则在handler所在的线程进行
	 */
	public HttpRequestHandler(Handler handler,IHttpResponseListener<T> httpResponseListener) {
		super(handler, httpResponseListener);
	}
	
	protected final HttpRequest getHttpRequest(){
			StringBuffer urlBuffer = new StringBuffer();
			urlBuffer.append(getServiceBaseUrl());
			String path = getRequestPath();
			if(!TextUtils.isEmpty(path))
			{
				urlBuffer.append("/");
				urlBuffer.append(path);
			}
			int type = getRequestType();
			List<HttpParameter> requestParameters= getRequestParameters();
			Map<String,String> requestProperties = getRequestProperties();
			int priority = getPriority();
			String cacheFilePath = getCacheFilePath();
			int retry = getRetry();
			
			if(type==HttpRequest.TYPE_GET&&requestParameters!=null)
			{
				urlBuffer.append("?");
				for(HttpParameter parameter:requestParameters)
				{
					urlBuffer.append(HttpUtils.getUrlEncodeStr(parameter.key));
					urlBuffer.append("=");
					urlBuffer.append(HttpUtils.getUrlEncodeStr(parameter.value.toString()));
					urlBuffer.append("&");
				}
			}
			String url = urlBuffer.toString();
			return new HttpRequest(url ,type,requestParameters,requestProperties,priority,cacheFilePath,retry);
	}

	/**
	 * @return Url的domain部分,例如“http://www.abd.com/a/b/d?xx=1”中的“http://www.abd.com”
	 */
	protected abstract String getServiceBaseUrl();
	
	/**
	 * @return Request.TYPE_GET or Request.TYPE_POST
	 */
	protected abstract int getRequestType();
	
	/**
	 * @return Url的path部分,例如“http://www.abd.com/a/b/d?xx=1”中的“a/b/d”
	 */
	protected abstract String getRequestPath();

	/**
	 * @return 请求的参数
	 */
	protected abstract List<HttpParameter> getRequestParameters();
	
	/**
	 * @return Http头部参数
	 */
	protected abstract Map<String, String> getRequestProperties();
	
	/**
	 * @return 本地缓存的路径，通常用于文件的下载，设置后onResult回调的data为null
	 */
	protected abstract String getCacheFilePath();
	
	protected int getPriority(){
		return HttpRequest.DEFAULT_PRIORITY;
	}
	
	protected int getRetry(){
		return HttpRequest.DEFAULT_RETRY;
	}
}
