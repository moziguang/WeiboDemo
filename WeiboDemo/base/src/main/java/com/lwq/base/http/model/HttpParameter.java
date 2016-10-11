package com.lwq.base.http.model;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public class HttpParameter {

	/**
	 * @param key
	 * @param value
	 */
	public HttpParameter(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}
	public String key;
	public Object value;
}
