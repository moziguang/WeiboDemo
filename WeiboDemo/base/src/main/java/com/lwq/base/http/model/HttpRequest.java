package com.lwq.base.http.model;

import java.util.List;
import java.util.Map;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public class HttpRequest {
	public static final int TYPE_GET = 0;
	public static final int TYPE_POST = 1;
	public static final int DEFAULT_PRIORITY = 10;
	public static final int DEFAULT_RETRY = 3;
	
	/**
	 * HTTP请求的url
	 */
	private final String mUrl;
	
	/**
	 * HTTP请求的类型，目前支持get及post，默认为get
	 */
	private final int mType;
	
	/**
	 * HTTP请求的参数
	 */
	private final List<HttpParameter> mRequestParamList;
	
	/**
	 * HTTP header的参数
	 */
	private Map<String, String> mRequestProperties;
	
	/**
	 * 指定缓存文件路径，如果不为null，请求得到的数据会被保存到此文件，而且数据不再传递给回调函数
	 * 此参数适用于大文件的下载
	 */
	private final String mCacheFilePath;
	
	/**
	 * 优先级数值越小，优先级越高，默认10
	 */
	private final int mPriority;
	
	private final int mRetry;
	
	/**
	 * 此参数只在type为post时生效，并且设置此参数后mRequestParamList参数不在起作用,type自动设置为post
	 */
	private final byte[] mPostData;

	/**
	 * @param url
	 * @param type HTTP请求的类型，目前支持get及post，默认为get
	 * @param requestParamList HTTP请求的参数
	 * @param requestProperties HTTP header的参数
	 * @param priority 优先级数值越小，优先级越高，默认10
	 * @param cacheFilePath 指定缓存文件路径，如果不为null，请求得到的数据会被保存到此文件，而且数据不再传递给回调函数. 此参数适用于大文件的下载
	 */
	public HttpRequest(String url, int type, List<HttpParameter> requestParamList, Map<String, String> requestProperties, int priority, String cacheFilePath, int retry) {
		this.mUrl = url;
		this.mType = type;
		this.mRequestProperties = requestProperties;
		this.mRequestParamList = requestParamList;
		this.mPriority = priority;
		this.mCacheFilePath = cacheFilePath;
		this.mRetry = retry;
		this.mPostData = null;
	}
	
	/**
	 * @param url
	 * @param postData HTTP 请求的参数
	 * @param requestProperties HTTP header的参数
	 * @param priority 优先级数值越小，优先级越高，默认10
	 * @param cacheFilePath 指定缓存文件路径，如果不为null，请求得到的数据会被保存到此文件，而且数据不再传递给回调函数. 此参数适用于大文件的下载
	 */
	public HttpRequest(String url, byte[] postData, Map<String, String> requestProperties, int priority, String cacheFilePath, int retry) {
		this.mUrl = url;
		this.mType = TYPE_POST;
		this.mRequestProperties = requestProperties;
		this.mRequestParamList = null;
		this.mPriority = priority;
		this.mCacheFilePath = cacheFilePath;
		this.mRetry = retry;
		this.mPostData = postData;
	}

	public String getUrl() {
           return mUrl;
	}

	public int getType() {
		return mType;
	}

	public List<HttpParameter> getRequestParamList() {
		return mRequestParamList;
	}

	public Map<String, String> getRequestProperties() {
		return mRequestProperties;
	}

	public String getCacheFilePath() {
		return mCacheFilePath;
	}

	public int getPriority() {
		return mPriority;
	}

	public int getRetry() {
		return mRetry;
	}

	public byte[] getPostData() {
		return mPostData;
	}

    public void setRequestProperties(Map<String, String> requestProperties) {
        this.mRequestProperties = requestProperties;
    }
}
