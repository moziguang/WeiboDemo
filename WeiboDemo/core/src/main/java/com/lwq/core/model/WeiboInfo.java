package com.lwq.core.model;
/*
 * Description : 
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */

import java.util.*;

import android.content.ContentValues;

import com.lwq.base.util.JsonUtils;
import com.lwq.base.util.Log;
import com.lwq.core.db.table.WeiboTable;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeiboInfo {
    private static final String TAG = "WeiboInfo";
    private long mId;
    private long mMid;
    private String mIdstr;
    private String mText;
    private String mSource;
    private int mFavorited;
    private int mTruncated;
    private String mThumbnailPic;
    private String mBmiddlePic;
    private String mOriginalPic;
    private String mGeo;
    private String mUser;
    private String mRetweetedStatus;
    private long mRepostsCount;
    private long mCommentsCount;
    private long mAttitudesCount;
    private String mVisible;
    private String mPicUrlStr;
    private List<String> mPicUrlList = new ArrayList<>();
    private String mAd;
    private String mCreatedAt;
    private UserInfo mUserInfo;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getMid() {
        return mMid;
    }

    public void setMid(long mid) {
        this.mMid = mid;
    }

    public String getIdstr() {
        return mIdstr;
    }

    public void setIdstr(String idstr) {
        this.mIdstr = idstr;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        this.mSource = source;
    }

    public int getFavorited() {
        return mFavorited;
    }

    public void setFavorited(int favorited) {
        this.mFavorited = favorited;
    }

    public int getTruncated() {
        return mTruncated;
    }

    public void setTruncated(int truncated) {
        this.mTruncated = truncated;
    }

    public String getThumbnailPic() {
        return mThumbnailPic;
    }

    public void setThumbnailPic(String thumbnailPic) {
        this.mThumbnailPic = thumbnailPic;
    }

    public String getBmiddlePic() {
        return mBmiddlePic;
    }

    public void setBmiddlePic(String bmiddlePic) {
        this.mBmiddlePic = bmiddlePic;
    }

    public String getOriginalPic() {
        return mOriginalPic;
    }

    public void setOriginalPic(String originalPic) {
        this.mOriginalPic = originalPic;
    }

    public String getGeo() {
        return mGeo;
    }

    public void setGeo(String geo) {
        this.mGeo = geo;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        this.mUser = user;
    }

    public String getRetweetedStatus() {
        return mRetweetedStatus;
    }

    public void setRetweetedStatus(String retweetedStatus) {
        this.mRetweetedStatus = retweetedStatus;
    }

    public long getRepostsCount() {
        return mRepostsCount;
    }

    public void setRepostsCount(long repostsCount) {
        this.mRepostsCount = repostsCount;
    }

    public long getCommentsCount() {
        return mCommentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.mCommentsCount = commentsCount;
    }

    public long getAttitudesCount() {
        return mAttitudesCount;
    }

    public void setAttitudesCount(long attitudesCount) {
        this.mAttitudesCount = attitudesCount;
    }

    public String getVisible() {
        return mVisible;
    }

    public void setVisible(String visible) {
        this.mVisible = visible;
    }

    public String getPicUrlStr() {
        return mPicUrlStr;
    }

    public void setPicUrlStr(String picUrlStr) {
        this.mPicUrlStr = picUrlStr;
        JSONArray picUrls = JsonUtils.loadJsonArray(picUrlStr);
        if(picUrls!=null) {
            for (int i = 0; i < picUrls.length(); i++) {
                try {
                    JSONObject picJson = picUrls.getJSONObject(i);
                    mPicUrlList.add(JsonUtils.getString(picJson, "thumbnail_pic"));
                } catch (Exception e) {
                    Log.e(TAG, "setPicUrls Exception", e);
                }
            }
        }
    }

    public void setPicUrls(JSONArray picUrls) {
        if(picUrls!=null) {
            this.mPicUrlStr = picUrls.toString();
            for(int i=0;i<picUrls.length();i++) {
                try {
                    JSONObject picJson = picUrls.getJSONObject(i);
                    mPicUrlList.add(JsonUtils.getString(picJson,"thumbnail_pic"));
                }catch (Exception e){
                    Log.e(TAG,"setPicUrls Exception",e);
                }
            }
        }
    }

    public String getAd() {
        return mAd;
    }

    public void setAd(String ad) {
        this.mAd = ad;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
    }

    public UserInfo getUserInfo() {
        if(mUserInfo==null && mUser!=null){
            mUserInfo = UserInfo.parseFromJson(JsonUtils.load(mUser));
        }
        return mUserInfo;
    }

    public List<String> getPicUrlList() {
        return mPicUrlList;
    }

    @Override
    public String toString() {
        return "WeiboInfo{" +
                 "mId=" + mId +
                 ", mMid=" + mMid +
                 ", mIdstr='" + mIdstr + '\'' +
                 ", mText='" + mText + '\'' +
                 ", mSource='" + mSource + '\'' +
                 ", mFavorited=" + mFavorited +
                 ", mTruncated=" + mTruncated +
                 ", mThumbnailPic='" + mThumbnailPic + '\'' +
                 ", mBmiddlePic='" + mBmiddlePic + '\'' +
                 ", mOriginalPic='" + mOriginalPic + '\'' +
                 ", mGeo='" + mGeo + '\'' +
                 ", mUser='" + mUser + '\'' +
                 ", mRetweetedStatus='" + mRetweetedStatus + '\'' +
                 ", mRepostsCount=" + mRepostsCount +
                 ", mCommentsCount=" + mCommentsCount +
                 ", mAttitudesCount=" + mAttitudesCount +
                 ", mVisible='" + mVisible + '\'' +
                 ", mPicUrlStr='" + mPicUrlStr + '\'' +
                 ", mAd='" + mAd + '\'' +
                 ", mCreatedAt='" + mCreatedAt + '\'' +
                 '}';
    }

    public ContentValues toContentValues() {
        ContentValues c = new ContentValues();
        c.put(WeiboTable.COL_ID, mId);
        c.put(WeiboTable.COL_MID, mMid);
        c.put(WeiboTable.COL_IDSTR, mIdstr);
        c.put(WeiboTable.COL_TEXT, mText);
        c.put(WeiboTable.COL_SOURCE, mSource);
        c.put(WeiboTable.COL_FAVORITED, mFavorited);
        c.put(WeiboTable.COL_TRUNCATED, mTruncated);
        c.put(WeiboTable.COL_THUMBNAIL_PIC, mThumbnailPic);
        c.put(WeiboTable.COL_BMIDDLE_PIC, mBmiddlePic);
        c.put(WeiboTable.COL_ORIGINAL_PIC, mOriginalPic);
        c.put(WeiboTable.COL_GEO, mGeo);
        c.put(WeiboTable.COL_USER, mUser);
        c.put(WeiboTable.COL_RETWEETED_STATUS, mRetweetedStatus);
        c.put(WeiboTable.COL_REPOSTS_COUNT, mRepostsCount);
        c.put(WeiboTable.COL_COMMENTS_COUNT, mCommentsCount);
        c.put(WeiboTable.COL_ATTITUDES_COUNT, mAttitudesCount);
        c.put(WeiboTable.COL_VISIBLE, mVisible);
        c.put(WeiboTable.COL_PIC_IDS, mPicUrlStr);
        c.put(WeiboTable.COL_AD, mAd);
        c.put(WeiboTable.COL_CREATE_AT, mCreatedAt);
        return c;
    }

    public static WeiboInfo parseFromJson(JSONObject jsonObject){
        WeiboInfo weiboInfo = new WeiboInfo();
        weiboInfo.setId(JsonUtils.getLong(jsonObject,"id",0L));
        weiboInfo.setMid(JsonUtils.getLong(jsonObject,"mid",0L));
        weiboInfo.setIdstr(JsonUtils.getString(jsonObject,"idstr"));
        weiboInfo.setText(JsonUtils.getString(jsonObject,"text"));
        weiboInfo.setSource(JsonUtils.getString(jsonObject,"source"));
        weiboInfo.setFavorited(JsonUtils.getBoolean(jsonObject,"favorited",false) ? 1:0);
        weiboInfo.setTruncated(JsonUtils.getBoolean(jsonObject,"truncated",false) ? 1:0);
        weiboInfo.setThumbnailPic(JsonUtils.getString(jsonObject,"thumbnail_pic"));
        weiboInfo.setBmiddlePic(JsonUtils.getString(jsonObject,"bmiddle_pic"));
        weiboInfo.setOriginalPic(JsonUtils.getString(jsonObject,"original_pic"));
        weiboInfo.setGeo(JsonUtils.getString(jsonObject,"geo"));
        weiboInfo.setUser(JsonUtils.getString(jsonObject,"user"));
        weiboInfo.setRetweetedStatus(JsonUtils.getString(jsonObject,"retweeted_status"));
        weiboInfo.setRepostsCount(JsonUtils.getInt(jsonObject,"reposts_count",0));
        weiboInfo.setCommentsCount(JsonUtils.getInt(jsonObject,"comments_count",0));
        weiboInfo.setAttitudesCount(JsonUtils.getInt(jsonObject,"attitudes_count",0));
        weiboInfo.setVisible(JsonUtils.getString(jsonObject,"visible"));
        JSONArray picIdArray = JsonUtils.getJSONArray(jsonObject,"pic_urls");
        weiboInfo.setPicUrls(picIdArray);
        weiboInfo.setAd(JsonUtils.getString(jsonObject,"ad"));
        weiboInfo.setCreatedAt(JsonUtils.getString(jsonObject,"created_at"));
        return weiboInfo;
    }
}
