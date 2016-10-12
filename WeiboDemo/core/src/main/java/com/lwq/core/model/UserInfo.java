package com.lwq.core.model;
/*
 * Description : 
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */

import com.lwq.base.util.JsonUtils;
import org.json.JSONObject;

/**
 * "user": {
 "id": 2488784290,
 "idstr": "2488784290",
 "class": 1,
 "screen_name": "生活百科收集",
 "name": "生活百科收集",
 "province": "43",
 "city": "3",
 "location": "湖南 湘潭",
 "description": "过日子就得要技术含量！怎么活的更潇洒，跟着百科君的脚步吧！",
 "url": "",
 "profile_image_url": "http://tva4.sinaimg.cn/crop.68.69.301.301.50/9457d5a2gw1em18fyj4aqj20c70brmxs.jpg",
 "cover_image_phone": "http://ww1.sinaimg.cn/crop.0.0.640.640.640/549d0121tw1egm1kjly3jj20hs0hsq4f.jpg",
 "profile_url": "u/2488784290",
 "domain": "",
 "weihao": "",
 "gender": "m",
 "followers_count": 2299752,
 "friends_count": 375,
 "pagefriends_count": 2,
 "statuses_count": 18457,
 "favourites_count": 5,
 "created_at": "Sun Oct 23 20:49:16 +0800 2011",
 "following": true,
 "allow_all_act_msg": false,
 "geo_enabled": true,
 "verified": false,
 "verified_type": -1,
 "remark": "",
 "ptype": 0,
 "allow_all_comment": true,
 "avatar_large": "http://tva4.sinaimg.cn/crop.68.69.301.301.180/9457d5a2gw1em18fyj4aqj20c70brmxs.jpg",
 "avatar_hd": "http://tva4.sinaimg.cn/crop.68.69.301.301.1024/9457d5a2gw1em18fyj4aqj20c70brmxs.jpg",
 "verified_reason": "",
 "verified_trade": "",
 "verified_reason_url": "",
 "verified_source": "",
 "verified_source_url": "",
 "follow_me": false,
 "online_status": 0,
 "bi_followers_count": 73,
 "lang": "zh-cn",
 "star": 0,
 "mbtype": 2,
 "mbrank": 4,
 "block_word": 0,
 "block_app": 0,
 "credit_score": 80,
 "user_ability": 1032,
 "urank": 30
 }
 */
public class UserInfo {
    private long mId;
    private String mIdstr;
    private String mName;
    private String mLocation;
    private String mDescription;
    private String mProfileImageUrl;
    private String mCoverImagePhone;
    private String mGender;
    private long mFollowersCount;
    private long mFriendsCount;
    private long mPagefriendsCount;
    private long mStatusesCount;
    private long mFavouritesCount;
    private boolean mFollowing;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getIdstr() {
        return mIdstr;
    }

    public void setIdstr(String idstr) {
        mIdstr = idstr;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        mProfileImageUrl = profileImageUrl;
    }

    public String getCoverImagePhone() {
        return mCoverImagePhone;
    }

    public void setCoverImagePhone(String coverImagePhone) {
        mCoverImagePhone = coverImagePhone;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public long getFollowersCount() {
        return mFollowersCount;
    }

    public void setFollowersCount(long followersCount) {
        mFollowersCount = followersCount;
    }

    public long getFriendsCount() {
        return mFriendsCount;
    }

    public void setFriendsCount(long friendsCount) {
        mFriendsCount = friendsCount;
    }

    public long getPagefriendsCount() {
        return mPagefriendsCount;
    }

    public void setPagefriendsCount(long pagefriendsCount) {
        mPagefriendsCount = pagefriendsCount;
    }

    public long getStatusesCount() {
        return mStatusesCount;
    }

    public void setStatusesCount(long statusesCount) {
        mStatusesCount = statusesCount;
    }

    public long getFavouritesCount() {
        return mFavouritesCount;
    }

    public void setFavouritesCount(long favouritesCount) {
        mFavouritesCount = favouritesCount;
    }

    public boolean isFollowing() {
        return mFollowing;
    }

    public void setFollowing(boolean following) {
        mFollowing = following;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                 "mId=" + mId +
                 ", mIdstr='" + mIdstr + '\'' +
                 ", mName='" + mName + '\'' +
                 ", mLocation='" + mLocation + '\'' +
                 ", mDescription='" + mDescription + '\'' +
                 ", mProfileImageUrl='" + mProfileImageUrl + '\'' +
                 ", mCoverImagePhone='" + mCoverImagePhone + '\'' +
                 ", mGender='" + mGender + '\'' +
                 ", mFollowersCount=" + mFollowersCount +
                 ", mFriendsCount=" + mFriendsCount +
                 ", mPagefriendsCount=" + mPagefriendsCount +
                 ", mStatusesCount=" + mStatusesCount +
                 ", mFavouritesCount=" + mFavouritesCount +
                 ", mFollowing=" + mFollowing +
                 '}';
    }

    public static UserInfo parseFromJson(JSONObject jsonObject){
        UserInfo weiboInfo = new UserInfo();
        weiboInfo.setId(JsonUtils.getLong(jsonObject,"id",0L));
        weiboInfo.setIdstr(JsonUtils.getString(jsonObject,"idstr"));
        weiboInfo.setName(JsonUtils.getString(jsonObject,"name"));
        weiboInfo.setLocation(JsonUtils.getString(jsonObject,"location"));
        weiboInfo.setDescription(JsonUtils.getString(jsonObject,"description"));
        weiboInfo.setProfileImageUrl(JsonUtils.getString(jsonObject,"profile_image_url"));
        weiboInfo.setCoverImagePhone(JsonUtils.getString(jsonObject,"mCoverImagePhone"));
        weiboInfo.setGender(JsonUtils.getString(jsonObject,"mGender"));
        weiboInfo.setFollowersCount(JsonUtils.getLong(jsonObject,"mFollowersCount",0L));
        weiboInfo.setFriendsCount(JsonUtils.getLong(jsonObject,"mFriendsCount",0L));
        weiboInfo.setPagefriendsCount(JsonUtils.getLong(jsonObject,"mPagefriendsCount",0L));
        weiboInfo.setStatusesCount(JsonUtils.getLong(jsonObject,"mStatusesCount",0L));
        weiboInfo.setFavouritesCount(JsonUtils.getInt(jsonObject,"mFavouritesCount",0));
        weiboInfo.setFollowing(JsonUtils.getBoolean(jsonObject,"mFollowing",false));

        return weiboInfo;
    }
}
