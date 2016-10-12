package com.lwq.core.manager.impl;

import java.util.*;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lwq.base.SharedPreferencesManager;
import com.lwq.base.db.BaseDatabase;
import com.lwq.base.event.EventManager;
import com.lwq.base.http.handler.BaseRequestHandler;
import com.lwq.base.http.listener.HandyHttpResponseListener;
import com.lwq.base.http.model.HttpParameter;
import com.lwq.base.util.JsonUtils;
import com.lwq.base.util.Log;
import com.lwq.base.util.StatusCodeDef;
import com.lwq.core.db.DatabaseManager;
import com.lwq.core.db.table.WeiboTable;
import com.lwq.core.http.WeiboConstants;
import com.lwq.core.http.WeiboHttpRequest;
import com.lwq.core.manager.BaseManager;
import com.lwq.core.manager.IAccountManager;
import com.lwq.core.manager.event.AccountEvent;
import com.lwq.core.model.WeiboInfo;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * Description : 
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public class AccountManager extends BaseManager implements IAccountManager {
    private static final int PAGE_SIZE = 30;
    private long mLocalMaxId = 0;
    private int mPageIndex = 0;
    private long mLocalSinceId = 0;
    private String mUid;
    private String mToken;
    private String mRefreshToken;
    private long mExpiresTime;
    private final Object mWeiboInfoListLock = new Object();
    private List<WeiboInfo> mWeiboInfoList = new ArrayList<>();

    @Override
    public void init() {
        super.init();
        mUid = SharedPreferencesManager.getInstance().getStringByKey(SharedPreferencesManager.KEY_SINA_UID,null);
        if(!TextUtils.isEmpty(mUid)) {
            mToken = SharedPreferencesManager.getInstance().getStringByKey(SharedPreferencesManager.KEY_SINA_ACCESS_TOKEN, null);
            mExpiresTime = SharedPreferencesManager.getInstance().getLongByKey(SharedPreferencesManager.KEY_SINA_EXPIRES_IN, 0);
            mRefreshToken = SharedPreferencesManager.getInstance().getStringByKey(SharedPreferencesManager.KEY_SINA_REFRESH_TOKEN, null);
            Log.d(TAG,"init token = " + mToken + " expiresTime = " + mExpiresTime + " refreshToken = " + mRefreshToken + " uid = " + mUid);
            initUser(mUid);
        }
    }

    @Override
    protected void addEvent() {

    }

    @Override
    protected void removeEvent() {

    }

    @Override
    public void onDbOpen() {
        Log.d(TAG, "onDbOpen");
        String sql = "select max(id) from " + WeiboTable.TABLE_NAME;
        BaseDatabase db = DatabaseManager.dbAgent().getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                mLocalSinceId = mLocalMaxId = cursor.getLong(0);
                Log.d(TAG, "mLocalMaxId = " + mLocalMaxId);
            }
        }catch (Exception e){
            Log.e(TAG, "query mLocalMaxId error",e);
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        refreshWeiboTimeline();
    }

    @Override
    public void initUser(String uid) {
        DatabaseManager.initPrivateDb(uid+"_");
    }

    @Override
    public void testHttp() {

    }

    @Override
    public void testDb() {

    }

    @Override
    public void refreshWeiboTimeline() {
        List<HttpParameter> paramList = new ArrayList<>();
        paramList.add(new HttpParameter("access_token",mToken));
        paramList.add(new HttpParameter("since_id",mLocalMaxId));
        paramList.add(new HttpParameter("count",PAGE_SIZE));
        WeiboHttpRequest request = new WeiboHttpRequest(WeiboConstants.PATH_HOME_TIMELINE, paramList, new HandyHttpResponseListener<JSONObject>() {
            @Override
            public void onResult(BaseRequestHandler<JSONObject> requestHandler, int errorCode,
                                 String errorMsg, JSONObject object) {
                if(errorCode == StatusCodeDef.SUCCESS && object!=null){
                    Log.d(TAG,"refreshWeiboTimeline onResult object = " + object.toString());
                    JSONArray statuses = JsonUtils.getJSONArray(object,"statuses");

                    if(statuses!=null) {
                        final List<WeiboInfo> weiboInfoList = new ArrayList<>();
                        try {
                            for (int i = 0; i < statuses.length(); i++) {
                                JSONObject weiboJson = statuses.getJSONObject(i);
                                WeiboInfo weiboInfo = WeiboInfo.parseFromJson(weiboJson);
                                weiboInfoList.add(weiboInfo);
                            }
                        }catch (Exception e){
                            Log.e(TAG,"refreshWeiboTimeline Exception",e);
                        }
                        setWeiboInfoList(weiboInfoList);
                        BaseDatabase db = DatabaseManager.dbAgent().getDatabase();
                        db.executeTask(new BaseDatabase.DatabaseTask() {
                            @Override
                            public void process(SQLiteDatabase database) {
                                database.beginTransaction();
                                try {
                                    for (WeiboInfo weiboInfo : weiboInfoList) {
                                        database.replace(WeiboTable.TABLE_NAME, null, weiboInfo.toContentValues());
                                    }
                                    database.setTransactionSuccessful();
                                }catch (Exception e){
                                    Log.e(TAG, "insert weiboinfo into db error",e);
                                }finally {
                                    Log.d(TAG, "endTransaction");
                                    database.endTransaction();
                                }
                            }
                        });
                    }
                }
            }
        });
        sendHttpRequest(request,null);
    }

    @Override
    public void weiboTimelineNextPage() {

    }

    @Override
    public String getUid() {
        return mUid;
    }

    private void setWeiboInfoList(List<WeiboInfo> weiboInfoList)
    {
        synchronized (mWeiboInfoListLock){
            mWeiboInfoList.clear();
            if(weiboInfoList!=null) {
                mWeiboInfoList.addAll(weiboInfoList);
            }
        }
        EventManager.defaultAgent().distribute(AccountEvent.EVENT_REFRESH_WEIBO_TIMELINE);
    }

    @Override
    public List<WeiboInfo> getWeiboInfoList() {
        List<WeiboInfo> weiboInfoList = new ArrayList<>();
        synchronized (mWeiboInfoListLock){
            weiboInfoList.addAll(mWeiboInfoList);
        }
        return weiboInfoList;
    }
}
