package com.lwq.base.http.model;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public class CocoHttpResponse {
	private int result;
	private String message;
	private JSONObject body;

	public static CocoHttpResponse decode(String jsonText) throws JSONException {
		CocoHttpResponse result = new CocoHttpResponse();
		JSONObject json = new JSONObject(jsonText);
		result.result = json.getInt("result");
		result.message = json.getString("message");
        result.body = json;
		return result;
	}

	public int getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	public JSONObject getBody() {
		return body;
	}

}
