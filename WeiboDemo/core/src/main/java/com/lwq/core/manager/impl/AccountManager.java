package com.lwq.core.manager.impl;

import java.util.*;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
    private static final int PAGE_SIZE = 100;
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
            Date date = new Date(mExpiresTime);
            Log.d(TAG,"init token = " + mToken + " expiresTime = " + mExpiresTime + " refreshToken = " + mRefreshToken + " uid = " + mUid + " date = " + date);
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
        final BaseDatabase db = DatabaseManager.dbAgent().getDatabase();
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

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                List<WeiboInfo> weiboInfoList = new ArrayList<WeiboInfo>();
                Cursor cursor = null;
                try {
                    cursor = db.query(WeiboTable.TABLE_NAME,null,null,null,null,null,WeiboTable.COL_ID + " DESC ");
                    if (cursor != null && cursor.moveToFirst()) {
                        do{
                            WeiboInfo weiboInfo = new WeiboInfo();
                            weiboInfo.setCreatedAt(cursor.getString(WeiboTable.INDEX_CREATE_AT));
                            weiboInfo.setId(cursor.getLong(WeiboTable.INDEX_ID));
                            weiboInfo.setAd(cursor.getString(WeiboTable.INDEX_AD));
                            weiboInfo.setPicUrlStr(cursor.getString(WeiboTable.INDEX_PIC_IDS));
                            weiboInfo.setVisible(cursor.getString(WeiboTable.INDEX_VISIBLE));
                            weiboInfo.setAttitudesCount(cursor.getInt(WeiboTable.INDEX_ATTITUDES_COUNT));
                            weiboInfo.setBmiddlePic(cursor.getString(WeiboTable.INDEX_BMIDDLE_PIC));
                            weiboInfo.setCommentsCount(cursor.getInt(WeiboTable.INDEX_COMMENTS_COUNT));
                            weiboInfo.setFavorited(cursor.getInt(WeiboTable.INDEX_FAVORITED));
                            weiboInfo.setGeo(cursor.getString(WeiboTable.INDEX_GEO));
                            weiboInfo.setIdstr(cursor.getString(WeiboTable.INDEX_IDSTR));
                            weiboInfo.setMid(cursor.getLong(WeiboTable.INDEX_MID));
                            weiboInfo.setOriginalPic(cursor.getString(WeiboTable.INDEX_ORIGINAL_PIC));
                            weiboInfo.setVisible(cursor.getString(WeiboTable.INDEX_VISIBLE));
                            weiboInfo.setUser(cursor.getString(WeiboTable.INDEX_USER));
                            weiboInfo.setTruncated(cursor.getInt(WeiboTable.INDEX_TRUNCATED));
                            weiboInfo.setThumbnailPic(cursor.getString(WeiboTable.INDEX_THUMBNAIL_PIC));
                            weiboInfo.setText(cursor.getString(WeiboTable.INDEX_TEXT));
                            weiboInfo.setSource(cursor.getString(WeiboTable.INDEX_SOURCE));
                            weiboInfo.setRetweetedStatus(cursor.getString(WeiboTable.INDEX_RETWEETED_STATUS));
                            weiboInfo.setRepostsCount(cursor.getInt(WeiboTable.INDEX_REPOSTS_COUNT));
                            weiboInfoList.add(weiboInfo);
                        }while (cursor.moveToNext());
                    }
                }catch (Exception e){
                    Log.e(TAG, "query mLocalMaxId error",e);
                }finally {
                    if(cursor!=null){
                        cursor.close();
                    }
                }
                setWeiboInfoList(weiboInfoList);
                refreshWeiboTimeline();
                return null;
            }
        }.execute();
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
                        addWeiboInfoList(weiboInfoList);
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

    private void addWeiboInfoList(List<WeiboInfo> weiboInfoList)
    {
        synchronized (mWeiboInfoListLock){
            if(weiboInfoList!=null) {
                mWeiboInfoList.addAll(weiboInfoList);
            }
        }
        EventManager.defaultAgent().distribute(AccountEvent.EVENT_REFRESH_WEIBO_TIMELINE);
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
