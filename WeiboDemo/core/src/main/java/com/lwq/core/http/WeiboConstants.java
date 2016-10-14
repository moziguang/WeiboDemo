package com.lwq.core.http;
/*
 * Description : 
 *
 * Creation    : 2016/10/12
 * Author      : moziguang@126.com
 */

public class WeiboConstants {
    public static final String HTTP_BASE_URL = "https://api.weibo.com/2";
    public static final String PATH_HOME_TIMELINE = "statuses/home_timeline.json";
    public static final String PATH_COMMENTS_SHOW = "comments/show";

    public static final int ERROR_TOKEN_EXPIRED = 21327;
    public static final int ERROR_NOT_PERMISSIONS = 10014;//Insufficient app permissions
}
