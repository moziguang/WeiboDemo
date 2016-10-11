package com.lwq.base.http.listener;

import com.lwq.base.http.handler.BaseRequestHandler;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public interface IHttpResponseListener<T> {
	public abstract void onResult(BaseRequestHandler<T> requestHandler, int errorCode,
								  String errorMsg, T object);
	
	public abstract void onProgress(BaseRequestHandler<T> requestHandler, int totalSize,
									int currentSize);
	
	public abstract void onCancel(BaseRequestHandler<T> requestHandler);
}
