package com.lwq.core.db;

import java.util.*;

import com.lwq.base.db.BaseDatabase;
import com.lwq.base.db.ITable;
import com.lwq.core.db.table.WeiboTable;

/*
 * Description : 
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */

public class WeiboDb extends BaseDatabase {
    public static final String DB_NAME = "weibodemo.db";
    public static final int DB_VERSION = 0;

    @Override
    public String storagePath() {
        return null;
    }

    @Override
    public String databaseName() {
        return DB_NAME;
    }

    @Override
    public String dbFileName() {
        return DB_NAME;
    }

    @Override
    public ITable[] staticTables() {
        return new ITable[]{
          new WeiboTable()
        };
    }

    @Override
    public int databaseVersion() {
        return DB_VERSION;
    }
}
