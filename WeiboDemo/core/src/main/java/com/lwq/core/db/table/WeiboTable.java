package com.lwq.core.db.table;

import com.lwq.base.db.ITable;
import com.lwq.core.db.WeiboDb;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */

public class WeiboTable implements ITable {
    public static final String TABLE_NAME = "weibo";

    public static String COL_ID = "id";//微博ID
    public static String COL_MID = "mid";//微博MID
    public static String COL_IDSTR = "idstr";//字符串型的微博ID
    public static String COL_TEXT = "text";//微博信息内容
    public static String COL_SOURCE = "source";//微博来源
    public static String COL_FAVORITED = "favorited";//是否已收藏，true：是，false：否
    public static String COL_TRUNCATED = "truncated";//是否被截断，true：是，false：否
    public static String COL_THUMBNAIL_PIC = "thumbnail_pic";//缩略图片地址，没有时不返回此字段
    public static String COL_BMIDDLE_PIC = "bmiddle_pic";//中等尺寸图片地址，没有时不返回此字段
    public static String COL_ORIGINAL_PIC = "original_pic";//原始图片地址，没有时不返回此字段
    public static String COL_CREATE_AT = "created_at";//微博创建时间
    public static String COL_GEO = "geo";//地理信息字段 详细
    public static String COL_USER = "user";//微博作者的用户信息字段 详细
    public static String COL_RETWEETED_STATUS = "retweeted_status";//被转发的原微博信息字段，当该微博为转发微博时返回
    public static String COL_REPOSTS_COUNT = "reposts_count";//转发数
    public static String COL_COMMENTS_COUNT = "comments_count";//评论数
    public static String COL_ATTITUDES_COUNT = "attitudes_count";//表态数
    public static String COL_VISIBLE = "visible";//微博的可见性及指定可见分组信息。该object中type取值，0：普通微博，1：私密微博，3：指定分组微博，4：密友微博；list_id为分组的组号
    public static String COL_PIC_IDS ="pic_ids";//微博配图ID。多图时返回多图ID，用来拼接图片url。用返回字段thumbnail_pic的地址配上该返回字段的图片ID，即可得到多个图片url。
    public static String COL_AD = "ad";

    public static final int INDEX_ID = 0;
    public static final int INDEX_MID = 1;
    public static final int INDEX_IDSTR = 2;
    public static final int INDEX_TEXT = 3;
    public static final int INDEX_SOURCE = 4;
    public static final int INDEX_FAVORITED = 5;
    public static final int INDEX_TRUNCATED = 6;
    public static final int INDEX_THUMBNAIL_PIC = 7;
    public static final int INDEX_BMIDDLE_PIC = 8;
    public static final int INDEX_ORIGINAL_PIC = 9;
    public static final int INDEX_CREATE_AT = 10;
    public static final int INDEX_GEO = 11;
    public static final int INDEX_USER = 12;
    public static final int INDEX_RETWEETED_STATUS = 13;
    public static final int INDEX_REPOSTS_COUNT = 14;
    public static final int INDEX_COMMENTS_COUNT = 15;
    public static final int INDEX_ATTITUDES_COUNT = 16;
    public static final int INDEX_VISIBLE = 17;
    public static final int INDEX_PIC_IDS = 18;
    public static final int INDEX_PIC_AD = 19;

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public int getAddTableDbVersion() {
        return WeiboDb.DB_VERSION;
    }

    @Override
    public String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS '" + getTableName() + "' ( "
                 + COL_ID + " number primary key, "
                 + COL_MID + " number, "
                 + COL_IDSTR + " text, "
                 + COL_TEXT + " text, "
                 + COL_SOURCE + " text, "
                 + COL_FAVORITED + " number, "
                 + COL_TRUNCATED + " number, "
                 + COL_THUMBNAIL_PIC + " text, "
                 + COL_BMIDDLE_PIC + " text, "
                 + COL_ORIGINAL_PIC + " text, "
                 + COL_CREATE_AT + " text, "
                 + COL_GEO + " text, "
                 + COL_USER + " text,"
                 + COL_RETWEETED_STATUS + " text, "
                 + COL_REPOSTS_COUNT + " number, "
                 + COL_COMMENTS_COUNT + " number, "
                 + COL_ATTITUDES_COUNT + " number, "
                 + COL_VISIBLE + " text, "
                 + COL_PIC_IDS + " text,"
                 + COL_AD + " text "
                 + ") ";
    }

    @Override
    public String[] getAlterTableSQL(int oldVersion, int newVersion) {
        return new String[0];
    }
}
