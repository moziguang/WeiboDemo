package com.lwq.core.model;
/*
 * Description : 
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */

import android.content.ContentValues;

import com.lwq.core.db.table.WeiboTable;

public class WeiboInfo {
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
    private String mPicIds;
    private String mAd;
    private String mCreatedAt;

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

    public String getPicIds() {
        return mPicIds;
    }

    public void setPicIds(String picIds) {
        this.mPicIds = picIds;
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
                 ", mPicIds='" + mPicIds + '\'' +
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
        c.put(WeiboTable.COL_PIC_IDS, mPicIds);
        c.put(WeiboTable.COL_AD, mAd);
        c.put(WeiboTable.COL_CREATE_AT, mCreatedAt);
        return c;
    }
}
