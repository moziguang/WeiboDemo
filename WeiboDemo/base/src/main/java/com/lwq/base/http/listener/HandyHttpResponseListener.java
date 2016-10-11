/**   
* @author Luoweiqiang
* @date 2015年5月11日 下午2:55:51 
* @version V1.0   
*/
package com.lwq.base.http.listener;

import com.lwq.base.http.handler.BaseRequestHandler;

/*
 * Description : 不需要onProgress及onCancel回调的Listener，可以通过继承此类来实现
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public abstract class HandyHttpResponseListener<T> implements IHttpResponseListener<T>{

	@Override
	public void onProgress(BaseRequestHandler<T> requestHandler, int totalSize, int currentSize) {
		
	}

	@Override
	public void onCancel(BaseRequestHandler<T> requestHandler) {
		
	}

}
