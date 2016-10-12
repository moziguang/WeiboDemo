package com.lwq.core.manager;

import java.util.*;

import com.lwq.core.model.WeiboInfo;

/*
 * Description : 
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public interface IAccountManager extends IManager {
    void initUser(String uid);
    void testHttp();
    void testDb();
    void refreshWeiboTimeline();
    void weiboTimelineNextPage();
    String getUid();
    List<WeiboInfo> getWeiboInfoList();
}
