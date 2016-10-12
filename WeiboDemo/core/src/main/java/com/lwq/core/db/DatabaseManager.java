package com.lwq.core.db;

import java.util.*;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.lwq.base.db.BaseDatabase;
import com.lwq.base.util.Log;
import com.lwq.core.manager.ManagerProxy;


/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public class DatabaseManager {

    private static final Object sInitLock = new Object();
    private static final String TAG = DatabaseManager.class.getSimpleName();

    private static DBAgent sDbAgent;

    public static DBAgent dbAgent() {
        return sDbAgent;
    }

    public static void init(Context context) {
        synchronized (sInitLock) {
            if (sDbAgent == null) {
                sDbAgent = new DBAgent();
                sDbAgent.init(context);
            }
        }
        Log.i(TAG," db init end");
    }

    public static void uninit() {
        Log.i(TAG, " db uninit");
        synchronized (sInitLock) {
            if (sDbAgent != null) {
                sDbAgent.uninit();
                sDbAgent = null;
            }
        }
    }

    public static void initPrivateDb(String dbNamePrefix) {
        synchronized (sInitLock) {
            Log.d(TAG, " sDbAgent =" + sDbAgent);
            if (sDbAgent != null){
                sDbAgent.initPrivateDb(dbNamePrefix);
            }else{
                Log.e(TAG, " sDbAgent = null ");
            }
        }
        Log.i(TAG, " db initPrivateDb end");
    }

    public static void uninitPrivateDb() {
        Log.i(TAG, " db uninitPrivateDb");
        synchronized (sInitLock) {
            if (sDbAgent != null) {
                sDbAgent.uninitPrivateDb();
            }
        }
    }


    public static class DBAgent{
        private static final Object initPublicLock = new Object();
        private static final Object initPrivateLock = new Object();
        private static boolean isPrivateDbInited = false;

        private final BaseDatabase[] privateDBList = new BaseDatabase[]{
                new WeiboDb()
        };

        private Map<String, BaseDatabase> privateDbMap = new ArrayMap<>();
        private BaseDatabase publicDb;
        private Context mContext;


        private DBAgent() {
        }

        /**
         *  返回默认的DB coco_core,如果需要其他DB，使用 <br>getDatabase(String dbName)</br>
         * @return
         */
        public BaseDatabase getDatabase() {
             BaseDatabase baseDatabase = privateDbMap.get(WeiboDb.DB_NAME);
            return baseDatabase;
        }

        public BaseDatabase getDatabase(String dbName) {
            BaseDatabase baseDatabase = privateDbMap.get(dbName);
            return baseDatabase;
        }

        /**
         *  返回登录账号无关数据库,目前主要用于存放游戏数据
         * @return
         */
        public BaseDatabase getPublicDatabase() {
//            Log.d(TAG," getPublicDatabase publicDb = " + publicDb);
            return publicDb;
        }

        private void init(Context context) {
            mContext = context.getApplicationContext();
            initPublicDatabases(mContext);
        }

        private void initPublicDatabases(Context context) {
            synchronized (initPublicLock) {
                if (publicDb == null) {

                }
            }
        }

        private void uninit() {
            Log.d(TAG, " DBAgent uninit ");
            synchronized (initPublicLock) {
                if (publicDb != null) {
                    publicDb.close();
                    publicDb = null;
                }
            }
            uninitPrivateDb();
        }

        private void initPrivateDb(String dbNamePrefix) {
//            Log.d(TAG, " DBAgent init mContext = " + mContext);
            synchronized (initPrivateLock) {
                if(!isPrivateDbInited && mContext!=null) {
                    for (BaseDatabase db : privateDBList) {
                        try {
                            db.init(mContext,dbNamePrefix);
                            privateDbMap.put(db.databaseName(), db);
                        } catch (Exception e) {
                            Log.e(TAG, "initDatabases Exception", e);
                        }
                    }
                    isPrivateDbInited = true;
                    ManagerProxy.callOnDbOpen();
                }
            }
            Log.d(TAG, " DBAgent init end");
        }

        private void uninitPrivateDb() {
            synchronized (initPrivateLock) {
                if (isPrivateDbInited) {
                    for (Map.Entry<String, BaseDatabase> entry : privateDbMap.entrySet()) {
                        entry.getValue().close();
                    }
                    isPrivateDbInited = false;
                }
            }
        }
    }

}
