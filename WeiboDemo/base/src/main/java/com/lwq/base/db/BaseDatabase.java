package com.lwq.base.db;

import java.util.*;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.OperationCanceledException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lwq.base.util.Log;
import com.lwq.base.util.StatusCodeDef;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public abstract class BaseDatabase {
    public static final String TAG = BaseDatabase.class.getSimpleName();

    public interface DatabaseTask {
        void process(SQLiteDatabase database);
    }

    private SQLiteDatabase mReadWriteDB;
    private SQLiteDatabase mReadOnlyDB;
    private ConnectionThread mDbConnection;
    private String mDbFilePrefix;

    public void init(Context context) {
        connectDB(context);
    }

    public void init(Context context,String dbFilePrefix) {
        this.mDbFilePrefix = dbFilePrefix;
        connectDB(context);
    }

    /**
     * Query the given URL, returning a {@link Cursor} over the result set.
     *
     * @param distinct      true if you want each row to be unique, false otherwise.
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will
     *                      return all columns, which is discouraged to prevent reading
     *                      data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null
     *                      will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor,
     *                      if row grouping is being used, formatted as an SQL HAVING
     *                      clause (excluding the HAVING itself). Passing null will cause
     *                      all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @param limit         Limits the number of rows returned by the query,
     *                      formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    public Cursor query(boolean distinct, String table, String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit) {
        SQLiteDatabase db = getReadConnection();
        if (db != null && db.isOpen()) {
            return db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy,
                    limit);
        }
        return null;
    }

    /**
     * Query the given URL, returning a {@link Cursor} over the result set.
     *
     * @param distinct           true if you want each row to be unique, false otherwise.
     * @param table              The table name to compile the query against.
     * @param columns            A list of which columns to return. Passing null will
     *                           return all columns, which is discouraged to prevent reading
     *                           data from storage that isn't going to be used.
     * @param selection          A filter declaring which rows to return, formatted as an
     *                           SQL WHERE clause (excluding the WHERE itself). Passing null
     *                           will return all rows for the given table.
     * @param selectionArgs      You may include ?s in selection, which will be
     *                           replaced by the values from selectionArgs, in order that they
     *                           appear in the selection. The values will be bound as Strings.
     * @param groupBy            A filter declaring how to group rows, formatted as an SQL
     *                           GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                           will cause the rows to not be grouped.
     * @param having             A filter declare which row groups to include in the cursor,
     *                           if row grouping is being used, formatted as an SQL HAVING
     *                           clause (excluding the HAVING itself). Passing null will cause
     *                           all row groups to be included, and is required when row
     *                           grouping is not being used.
     * @param orderBy            How to order the rows, formatted as an SQL ORDER BY clause
     *                           (excluding the ORDER BY itself). Passing null will use the
     *                           default sort order, which may be unordered.
     * @param limit              Limits the number of rows returned by the query,
     *                           formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @param cancellationSignal A signal to cancel the operation in progress, or null if none.
     *                           If the operation is canceled, then {@link OperationCanceledException} will be thrown
     *                           when the query is executed.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    @TargetApi(16)
    public Cursor query(boolean distinct, String table, String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        SQLiteDatabase db = getReadConnection();
        if (db != null && db.isOpen()) {
            return db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy,
                    limit, cancellationSignal);
        }
        return null;
    }

    /**
     * Query the given table, returning a {@link Cursor} over the result set.
     *
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will
     *                      return all columns, which is discouraged to prevent reading
     *                      data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null
     *                      will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor,
     *                      if row grouping is being used, formatted as an SQL HAVING
     *                      clause (excluding the HAVING itself). Passing null will cause
     *                      all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        SQLiteDatabase db = getReadConnection();
        if (db != null && db.isOpen()) {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
        Log.e(TAG, "db == null");
        return null;
    }

    /**
     * Query the given table, returning a {@link Cursor} over the result set.
     *
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will
     *                      return all columns, which is discouraged to prevent reading
     *                      data from storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null
     *                      will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor,
     *                      if row grouping is being used, formatted as an SQL HAVING
     *                      clause (excluding the HAVING itself). Passing null will cause
     *                      all row groups to be included, and is required when row
     *                      grouping is not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @param limit         Limits the number of rows returned by the query,
     *                      formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy, String limit) {
        SQLiteDatabase db = getReadConnection();
        if (db != null && db.isOpen()) {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        }
        return null;
    }

    /**
     * Runs the provided SQL and returns a {@link Cursor} over the result set.
     *
     * @param sql           the SQL query. The SQL string must not be ; terminated
     * @param selectionArgs You may include ?s in where clause in the query,
     *                      which will be replaced by the values from selectionArgs. The
     *                      values will be bound as Strings.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     */
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        SQLiteDatabase db = getReadConnection();
        if (db != null && db.isOpen()) {
            Log.i(TAG, "当前raw query SQL:" + sql + " 参数list:" + Arrays.toString(selectionArgs));
            return db.rawQuery(sql, selectionArgs);
        }
        return null;
    }

    /**
     * Runs the provided SQL and returns a {@link Cursor} over the result set.
     *
     * @param sql                the SQL query. The SQL string must not be ; terminated
     * @param selectionArgs      You may include ?s in where clause in the query,
     *                           which will be replaced by the values from selectionArgs. The
     *                           values will be bound as Strings.
     * @param cancellationSignal A signal to cancel the operation in progress, or null if none.
     *                           If the operation is canceled, then {@link OperationCanceledException} will be thrown
     *                           when the query is executed.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     */
    @TargetApi(16)
    public Cursor rawQuery(String sql, String[] selectionArgs,
                           CancellationSignal cancellationSignal) {
        SQLiteDatabase db = getReadConnection();
        if (db != null && db.isOpen()) {
            return db.rawQuery(sql, selectionArgs, cancellationSignal);
        }
        return null;
    }

    /**
     * Convenience method for replacing a row in the database.
     *
     * @param table          the table in which to replace the row
     * @param nullColumnHack optional; may be <code>null</code>.
     *                       SQL doesn't allow inserting a completely empty row without
     *                       naming at least one column name.  If your provided <code>initialValues</code> is
     *                       empty, no column names are known and an empty row can't be inserted.
     *                       If not set to null, the <code>nullColumnHack</code> parameter
     *                       provides the name of nullable column name to explicitly insert a NULL into
     *                       in the case where your <code>initialValues</code> is empty.
     * @param initialValues  this map contains the initial column values for
     *                       the row.
     * @param callback       the row ID of the newly inserted row, or -1 if an error occurred
     */
    public void replace(String table, String nullColumnHack, ContentValues initialValues,
                        IDbOperateCallback<Long> callback) {
        mDbConnection.replace(table, nullColumnHack, initialValues, callback);
    }

    /**
     * Convenience method for inserting a row into the database.
     *
     * @param table          the table to insert the row into
     * @param nullColumnHack optional; may be <code>null</code>.
     *                       SQL doesn't allow inserting a completely empty row without
     *                       naming at least one column name.  If your provided <code>values</code> is
     *                       empty, no column names are known and an empty row can't be inserted.
     *                       If not set to null, the <code>nullColumnHack</code> parameter
     *                       provides the name of nullable column name to explicitly insert a NULL into
     *                       in the case where your <code>values</code> is empty.
     * @param values         this map contains the initial column values for the
     *                       row. The keys should be the column names and the values the
     *                       column values
     * @param callback       the row ID of the newly inserted row, or -1 if an error occurred
     */
    public void insert(String table, String nullColumnHack, ContentValues values,
                       IDbOperateCallback<Long> callback) {
        mDbConnection.insert(table, nullColumnHack, values, callback);
    }

    /**
     * General method for inserting a row into the database.
     *
     * @param table             the table to insert the row into
     * @param nullColumnHack    optional; may be <code>null</code>.
     *                          SQL doesn't allow inserting a completely empty row without
     *                          naming at least one column name.  If your provided <code>initialValues</code> is
     *                          empty, no column names are known and an empty row can't be inserted.
     *                          If not set to null, the <code>nullColumnHack</code> parameter
     *                          provides the name of nullable column name to explicitly insert a NULL into
     *                          in the case where your <code>initialValues</code> is empty.
     * @param initialValues     this map contains the initial column values for the
     *                          row. The keys should be the column names and the values the
     *                          column values
     * @param conflictAlgorithm for insert conflict resolver
     * @param callback          the row ID of the newly inserted row
     *                          OR the primary key of the existing row if the input param 'conflictAlgorithm' =
     *                          CONFLICT_IGNORE
     *                          OR -1 if any error
     */
    public void insertWithOnConflict(String table, String nullColumnHack,
                                     ContentValues initialValues, int conflictAlgorithm,
                                     IDbOperateCallback<Long> callback) {
        mDbConnection.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm,
                callback);
    }

    /**
     * Convenience method for deleting rows in the database.
     *
     * @param table       the table to delete from
     * @param whereClause the optional WHERE clause to apply when deleting.
     *                    Passing null will delete all rows.
     * @param whereArgs   You may include ?s in the where clause, which
     *                    will be replaced by the values from whereArgs. The values
     *                    will be bound as Strings.
     * @param callback    the number of rows affected if a whereClause is passed in, 0
     *                    otherwise. To remove all rows and get a count pass "1" as the
     *                    whereClause.
     */
    public void delete(String table, String whereClause, String[] whereArgs,
                       IDbOperateCallback<Long> callback) {
        mDbConnection.delete(table, whereClause, whereArgs, callback);
    }

    /**
     * Convenience method for updating rows in the database.
     *
     * @param table       the table to update in
     * @param values      a map from column names to new column values. null is a
     *                    valid value that will be translated to NULL.
     * @param whereClause the optional WHERE clause to apply when updating.
     *                    Passing null will update all rows.
     * @param whereArgs   You may include ?s in the where clause, which
     *                    will be replaced by the values from whereArgs. The values
     *                    will be bound as Strings.
     * @param callback    the number of rows affected
     */
    public void update(String table, ContentValues values, String whereClause, String[] whereArgs,
                       IDbOperateCallback<Long> callback) {
        mDbConnection.update(table, values, whereClause, whereArgs, callback);
    }

    /**
     * Convenience method for updating rows in the database.
     *
     * @param table             the table to update in
     * @param values            a map from column names to new column values. null is a
     *                          valid value that will be translated to NULL.
     * @param whereClause       the optional WHERE clause to apply when updating.
     *                          Passing null will update all rows.
     * @param whereArgs         You may include ?s in the where clause, which
     *                          will be replaced by the values from whereArgs. The values
     *                          will be bound as Strings.
     * @param conflictAlgorithm for update conflict resolver
     * @param callback          the number of rows affected
     */
    public void updateWithOnConflict(String table, ContentValues values,
                                     String whereClause, String[] whereArgs, int conflictAlgorithm,
                                     IDbOperateCallback<Long> callback) {
        mDbConnection.updateWithOnConflict(table, values, whereClause, whereArgs,
                conflictAlgorithm, callback);
    }

    public void executeTask(DatabaseTask task) {
        mDbConnection.executeTask(task);
    }

    /**
     * @param sql      rawSql
     * @param callback //
     */
    public void execRawSQL(String sql, IDbOperateCallback<Long> callback) {
        mDbConnection.execRawSQL(sql, callback);
    }

    public void close() {
        mDbConnection.close();
    }

    private void connectDB(Context context) {
        DatabaseInfo info = new DatabaseInfo();
        info.storagePath = storagePath();
        if(TextUtils.isEmpty(mDbFilePrefix)) {
            info.databaseName = dbFileName();
        }else{
            info.databaseName = mDbFilePrefix + dbFileName();
        }
        info.databaseVersion = databaseVersion();
        info.staticTables = staticTables();
        mDbConnection = new ConnectionThread(context, "DBThread_" + hashCode(), info);
        mDbConnection.connectToDatabase();
        mDbConnection.start();
        mReadWriteDB = mDbConnection.readWriteDB;
        mReadOnlyDB = mDbConnection.readOnlyDB;
    }

    public abstract String storagePath();

    public abstract String databaseName();

    public abstract String dbFileName();

    public abstract ITable[] staticTables();

    public abstract int databaseVersion();

    @TargetApi(16)
    private SQLiteDatabase getReadConnection() {
//        Log.d(TAG,"getReadConnection mReadWriteDB = " + mReadWriteDB + " mReadOnlyDB = "+ mReadOnlyDB);
        if (Build.VERSION.SDK_INT >= 16) {
            return mReadWriteDB;
        } else {
            return mReadOnlyDB;
        }
    }

    private static final class DatabaseInfo {
        String storagePath;
        String databaseName;
        int databaseVersion;
        ITable[] staticTables;
        boolean readyOnly = false;

        public DatabaseInfo() {
        }

        public DatabaseInfo(String storagePath, String databaseName, int databaseVersion, ITable[] staticTables) {
            this.storagePath = storagePath;
            this.databaseName = databaseName;
            this.databaseVersion = databaseVersion;
            this.staticTables = staticTables;
        }

        public DatabaseInfo fromThis() {
            DatabaseInfo copyInfo = new DatabaseInfo();
            copyInfo.storagePath = this.storagePath;
            copyInfo.databaseName = this.databaseName;
            copyInfo.databaseVersion = this.databaseVersion;
            if (this.staticTables != null) {
                copyInfo.staticTables = new ITable[this.staticTables.length];
                System.arraycopy(this.staticTables, 0, copyInfo.staticTables,
                        0, this.staticTables.length);
            }
            copyInfo.readyOnly = this.readyOnly;

            return copyInfo;
        }

    }

    private static final class ConnectionThread extends HandlerThread {

        //        private static final int MSG_CONNECT_TO_DB = 1;
        private static final int MSG_INSERT = 2;
        private static final int MSG_INSERT_WITH_ON_CONFLICT = 3;
        private static final int MSG_DELETE = 4;
        private static final int MSG_UPDATE = 5;
        private static final int MSG_UPDATE_WITH_ON_CONFLICT = 6;
        private static final int MSG_EXEC_RAW_SQL = 7;
        private static final int MSG_CLOSE = 8;
        private static final int MSG_REPLACE = 9;

        private DatabaseInfo databaseInfo = null;
        private Handler eventHandler = null;
        private List<CustomSQLiteOpenHelper> connectionList = new ArrayList<>(2);
        private Context context;
        private SQLiteDatabase readWriteDB;
        private SQLiteDatabase readOnlyDB;

        public ConnectionThread(@NonNull Context context, @NonNull String name, @NonNull DatabaseInfo dbInfo) {
            super(name);
            init(context, dbInfo);
        }

        /**
         * Constructs a HandlerThread.
         *
         * @param name
         * @param priority The priority to run the thread at. The value supplied must be from
         *                 {@link android.os.Process} and not from java.lang.Thread.
         */
        public ConnectionThread(@NonNull Context context, @NonNull String name, int priority, @NonNull DatabaseInfo dbInfo) {
            super(name, priority);
            init(context, dbInfo);
        }

        private static class ParamInsert {
            public String table;
            public String nullColumnHack;
            public ContentValues values;
            public IDbOperateCallback<Long> callback;

            public ParamInsert() {
            }

            public ParamInsert(String table, String nullColumnHack, ContentValues values,
                               IDbOperateCallback<Long> callback) {
                this.table = table;
                this.nullColumnHack = nullColumnHack;
                this.values = values;
                this.callback = callback;
            }
        }

        private static class ParamsInsertWithOnConflict extends ParamInsert {
            public ParamsInsertWithOnConflict() {
            }

            public ParamsInsertWithOnConflict(String table, String nullColumnHack,
                                              ContentValues values,
                                              IDbOperateCallback<Long> callback,
                                              int conflictAlgorithm) {
                super(table, nullColumnHack, values, callback);
                this.conflictAlgorithm = conflictAlgorithm;
            }

            public int conflictAlgorithm;
        }

        private static class ParamDelete {
            public String table;
            public String whereClause;
            public String[] whereArgs;
            public IDbOperateCallback<Long> callback;

            public ParamDelete() {
            }

            public ParamDelete(String table, String whereClause, String[] whereArgs, IDbOperateCallback<Long> callback) {
                this.table = table;
                this.whereClause = whereClause;
                this.whereArgs = whereArgs;
                this.callback = callback;
            }
        }

        private static class ExecRawSQLParam {
            public String sql;
            public IDbOperateCallback<Long> callback;

            public ExecRawSQLParam() {
            }

            public ExecRawSQLParam(String s, IDbOperateCallback<Long> callback) {
                this.sql = s;
                this.callback = callback;
            }
        }

        private static class ParamUpdate {
            public String table;
            public ContentValues values;
            public String whereClause;
            public String[] whereArgs;
            public IDbOperateCallback<Long> callback;

            public ParamUpdate() {
            }

            public ParamUpdate(String table, ContentValues values, String whereClause,
                               String[] whereArgs, IDbOperateCallback<Long> callback) {
                this.table = table;
                this.values = values;
                this.whereClause = whereClause;
                this.whereArgs = whereArgs;
                this.callback = callback;
            }
        }

        private static class ParamUpdateWithOnConflict extends ParamUpdate {
            public int conflictAlgorithm;

            public ParamUpdateWithOnConflict() {
            }

            public ParamUpdateWithOnConflict(String table, ContentValues values, String whereClause,
                                             String[] whereArgs, IDbOperateCallback<Long> callback,
                                             int conflictAlgorithm) {
                super(table, values, whereClause, whereArgs, callback);
                this.conflictAlgorithm = conflictAlgorithm;
            }
        }

        @TargetApi(16)
        public void connectToDatabase() {
            if (Build.VERSION.SDK_INT >= 16) {
                connectToDatabaseCompat16();
            } else {
                connectToDatabaseCompat();
            }
        }

        private Handler connectionHandler;

        static class DbConnectionHandler extends Handler {
            private ConnectionThread dbThread;

            public DbConnectionHandler(ConnectionThread dbThread) {
                super(dbThread.getLooper());
                this.dbThread = dbThread;
            }

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_REPLACE:
                        dbThread.doReplace((ParamInsert) msg.obj);
                        break;
                    case MSG_INSERT:
                        dbThread.doInsert((ParamInsert) msg.obj);
                        break;
                    case MSG_INSERT_WITH_ON_CONFLICT:
                        dbThread.doInsertWithOnConflict((ParamsInsertWithOnConflict) msg.obj);
                        break;
                    case MSG_DELETE:
                        dbThread.doDelete((ParamDelete) msg.obj);
                        break;
                    case MSG_UPDATE:
                        dbThread.doUpdate((ParamUpdate) msg.obj);
                        break;
                    case MSG_UPDATE_WITH_ON_CONFLICT:
                        dbThread.doUpdateWithOnConflict((ParamUpdateWithOnConflict) msg.obj);
                        break;
                    case MSG_EXEC_RAW_SQL:
                        dbThread.doExecRawSQL((ExecRawSQLParam) msg.obj);
                        break;
                    case MSG_CLOSE:
                        removeAllMessage();
                        dbThread.doClose();
                        break;
                }
            }

            private void removeAllMessage() {
                removeMessages(MSG_REPLACE);
                removeMessages(MSG_EXEC_RAW_SQL);
                removeMessages(MSG_UPDATE_WITH_ON_CONFLICT);
                removeMessages(MSG_UPDATE);
                removeMessages(MSG_DELETE);
                removeMessages(MSG_INSERT_WITH_ON_CONFLICT);
                removeMessages(MSG_INSERT);
            }
        }

        @TargetApi(18)
        private void doClose() {
            if (this.isAlive()) {
                for (CustomSQLiteOpenHelper conn : connectionList) {
                    conn.close();
                }
                if (Build.VERSION.SDK_INT >= 18) {
                    this.quitSafely();
                } else {
                    this.quit();
                }

            }
        }

        private void doUpdateWithOnConflict(ParamUpdateWithOnConflict param) {
            long updatedNum = 0;
            int result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
            IDbOperateCallback<Long> callback = param.callback;
            if (readWriteDB != null && readWriteDB.isOpen()) {
                try {
                    updatedNum = readWriteDB.updateWithOnConflict(param.table, param.values,
                            param.whereClause, param.whereArgs, param.conflictAlgorithm);
                    result = StatusCodeDef.SUCCESS;
                } catch (Exception e) {
                    Log.e("BaseDataBase", "doExecRawSQL Exception", e);
                    result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                }
            }
            if (callback != null) {
                callback.onResult(result, updatedNum);
            }
        }

        private void doUpdate(ParamUpdate param) {
//            Log.d(TAG,"doUpdate");
            long updatedNum = 0;
            int result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
            IDbOperateCallback<Long> callback = param.callback;
            if (readWriteDB != null && readWriteDB.isOpen()) {
                try {
                    updatedNum = readWriteDB.update(param.table, param.values, param.whereClause, param.whereArgs);
                    result = StatusCodeDef.SUCCESS;
                } catch (Exception e) {
                    Log.e("BaseDataBase", "doExecRawSQL Exception", e);
                    result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                }
            }
            if (callback != null) {
                callback.onResult(result, updatedNum);
            }
        }

        private void doDelete(ParamDelete param) {
            long deleteNum = 0;
            int result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
            IDbOperateCallback<Long> callback = param.callback;
            if (readWriteDB != null && readWriteDB.isOpen()) {
                try {
                    deleteNum = readWriteDB.delete(param.table, param.whereClause, param.whereArgs);
                    result = StatusCodeDef.SUCCESS;
                } catch (Exception e) {
                    Log.e("BaseDataBase", "doExecRawSQL Exception", e);
                    result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                }
            }
            if (callback != null) {
                callback.onResult(result, deleteNum);
            }
        }

        private void doInsertWithOnConflict(ParamsInsertWithOnConflict param) {
            int result = StatusCodeDef.READ_WRITE_DB_NOT_FOUND;
            long rowId = -1;
            IDbOperateCallback<Long> cb = param.callback;
            if (readWriteDB != null && readWriteDB.isOpen()) {
                rowId = readWriteDB.insertWithOnConflict(param.table, param.nullColumnHack,
                        param.values, param.conflictAlgorithm);
                if (rowId < 0) {
                    result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                } else {
                    result = StatusCodeDef.SUCCESS;
                }
            }
            if (cb != null) {
                cb.onResult(result, rowId);
            }
        }

        private void doReplace(ParamInsert param) {
            int result = StatusCodeDef.READ_WRITE_DB_NOT_FOUND;
            long rowId = -1;
            IDbOperateCallback<Long> cb = param.callback;
            if (readWriteDB != null && readWriteDB.isOpen()) {
                try {
                    rowId = readWriteDB.replace(param.table, param.nullColumnHack, param.values);
                    if (rowId < 0) {
                        result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                    } else {
                        result = StatusCodeDef.SUCCESS;
                    }
                } catch (Exception e) {
                    Log.e("BaseDataBase", "doExecRawSQL Exception", e);
                    result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                }
            }

            if (cb != null) {
                cb.onResult(result, rowId);
            }
        }

        private void doInsert(ParamInsert param) {
            int result = StatusCodeDef.READ_WRITE_DB_NOT_FOUND;
            long rowId = -1;
            IDbOperateCallback<Long> cb = param.callback;
            if (readWriteDB != null && readWriteDB.isOpen()) {

                try {
                    rowId = readWriteDB.insert(param.table, param.nullColumnHack, param.values);
                    if (rowId < 0) {
                        result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                    } else {
                        result = StatusCodeDef.SUCCESS;
                    }
                } catch (Exception e) {
                    Log.e("BaseDataBase", "doExecRawSQL Exception", e);
                    result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                }
            }

            if (cb != null) {
                cb.onResult(result, rowId);
            }
        }

        private void doExecRawSQL(ExecRawSQLParam param) {
            int result = StatusCodeDef.READ_WRITE_DB_NOT_FOUND;
            IDbOperateCallback<Long> cb = param.callback;
            if (readWriteDB != null && readWriteDB.isOpen()) {
                try {
                    readWriteDB.execSQL(param.sql);
                    result = StatusCodeDef.SUCCESS;
                } catch (Exception e) {
                    Log.e("BaseDataBase", "doExecRawSQL Exception", e);
                    result = StatusCodeDef.INSERT_FAIL_WITH_ERROR;
                }
            }

            if (cb != null) {
                cb.onResult(result, (long) result);
            }
        }

        private void connectToDatabaseCompat() {
            Log.d(TAG, "connectToDatabaseCompat start");
            DatabaseConnection rwConnection = new DatabaseConnection(context, databaseInfo,
                                                                      eventHandler);
            connectionList.add(rwConnection);
            readWriteDB = rwConnection.getWritableDatabase();

            DatabaseInfo readOnlyDBInfo = databaseInfo.fromThis();
            readOnlyDBInfo.readyOnly = true;
            DatabaseConnection readOnlyConnection = new DatabaseConnection(context, readOnlyDBInfo,
                                                                            eventHandler);
            connectionList.add(readOnlyConnection);
            readOnlyDB = readOnlyConnection.getReadableDatabase();
            Log.d(TAG, "connectToDatabaseCompat end mReadWriteDB = " + readWriteDB + " mReadOnlyDB = " + readOnlyDB);
        }

        @TargetApi(16)
        private void connectToDatabaseCompat16() {
            Log.d(TAG, "connectToDatabaseCompat16 start");
            DatabaseConnection walConnection = new DatabaseConnection(context, databaseInfo,
                                                                       eventHandler);
            walConnection.setWriteAheadLoggingEnabled(true);
            connectionList.add(walConnection);
            readWriteDB = walConnection.getWritableDatabase();
            Log.d(TAG, "connectToDatabaseCompat16 end mReadWriteDB = " + readWriteDB);
        }

        private void init(Context context, DatabaseInfo dbInfo) {
            databaseInfo = dbInfo;
            this.context = context;
        }

        /**
         * Starts the new Thread of execution. The <code>run()</code> method of
         * the receiver will be called by the receiver Thread itself (and not the
         * Thread calling <code>start()</code>).
         *
         * @throws IllegalThreadStateException - if this thread has already started.
         * @see Thread#run
         */
        @Override
        public synchronized void start() {
            Log.d(TAG, "ConnectionThread start");
            super.start();
            this.connectionHandler = new DbConnectionHandler(this);
        }

        public void replace(String table, String nullColumnHack, ContentValues values,
                            IDbOperateCallback<Long> callback) {
            ParamInsert paramInsert = new ParamInsert(table, nullColumnHack, values, callback);
            Message message = Message.obtain(connectionHandler, MSG_REPLACE, paramInsert);
            connectionHandler.sendMessage(message);
        }

        public void insert(String table, String nullColumnHack, ContentValues values,
                           IDbOperateCallback<Long> callback) {
            ParamInsert paramInsert = new ParamInsert(table, nullColumnHack, values, callback);
            Message message = Message.obtain(connectionHandler, MSG_INSERT, paramInsert);
            connectionHandler.sendMessage(message);
        }

        public void insertWithOnConflict(String table, String nullColumnHack,
                                         ContentValues initialValues, int conflictAlgorithm,
                                         IDbOperateCallback<Long> callback) {
            ParamsInsertWithOnConflict paramsInsertWithOnConflict
                    = new ParamsInsertWithOnConflict(table, nullColumnHack, initialValues,
                    callback, conflictAlgorithm);
            Message message = Message.obtain(connectionHandler, MSG_INSERT_WITH_ON_CONFLICT,
                    paramsInsertWithOnConflict);
            connectionHandler.sendMessage(message);
        }

        public void delete(String table, String whereClause, String[] whereArgs,
                           IDbOperateCallback<Long> callback) {
            ParamDelete paramDelete = new ParamDelete(table, whereClause, whereArgs, callback);
            Message message = Message.obtain(connectionHandler, MSG_DELETE, paramDelete);
            connectionHandler.dispatchMessage(message);
        }

        public void update(String table, ContentValues values, String whereClause,
                           String[] whereArgs, IDbOperateCallback<Long> callback) {
            ParamUpdate paramUpdate = new ParamUpdate(table, values, whereClause, whereArgs, callback);
            Message message = Message.obtain(connectionHandler, MSG_UPDATE, paramUpdate);
            connectionHandler.sendMessage(message);
        }

        public void updateWithOnConflict(String table, ContentValues values, String whereClause,
                                         String[] whereArgs, int conflictAlgorithm, IDbOperateCallback<Long> callback) {
            ParamUpdateWithOnConflict paramUpdateWithOnConflict = new ParamUpdateWithOnConflict(table,
                    values, whereClause, whereArgs, callback, conflictAlgorithm);
            Message message = Message.obtain(connectionHandler, MSG_UPDATE_WITH_ON_CONFLICT,
                    paramUpdateWithOnConflict);
            connectionHandler.sendMessage(message);
        }

        public void executeTask(final DatabaseTask task) {
            connectionHandler.post(new Runnable() {
                @Override
                public void run() {
                    task.process(readWriteDB);
                }
            });
        }

        public void execRawSQL(String sql, IDbOperateCallback<Long> callback) {
            ExecRawSQLParam execRawSQLParam = new ExecRawSQLParam(sql, callback);
            Message message = Message.obtain(connectionHandler, MSG_EXEC_RAW_SQL, execRawSQLParam);
            connectionHandler.sendMessage(message);
        }

        public void close() {
            connectionHandler.sendMessageDelayed(Message.obtain(connectionHandler, MSG_CLOSE), 500);
        }
    }

    private static class DatabaseConnection extends CustomSQLiteOpenHelper {
        private DatabaseInfo mDbInfo = null;

        public DatabaseConnection(Context context, @NonNull DatabaseInfo dbInfo,
                                  @NonNull Handler handler) {
            super(context, dbInfo.storagePath, dbInfo.databaseName, null, dbInfo.databaseVersion);
            this.mDbInfo = dbInfo;
        }


        public DatabaseConnection(Context context, @NonNull DatabaseInfo dbInfo,
                                  @NonNull Handler handler,
                                  @Nullable DatabaseErrorHandler errorHandler) {
            super(context, dbInfo.storagePath, dbInfo.databaseName, null, dbInfo.databaseVersion,
                    errorHandler);
            this.mDbInfo = dbInfo;
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            if (mDbInfo.staticTables != null) {
                for (ITable table : mDbInfo.staticTables) {
                    db.execSQL(table.getCreateTableSQL());
                }
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            Log.i(TAG, " onOpen db =" + db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (mDbInfo.staticTables != null) {
                for (ITable table : mDbInfo.staticTables) {
                    //这张表创建时，数据库的版本
                    if(oldVersion > table.getAddTableDbVersion()){
                        String[] alterSQLs = table.getAlterTableSQL(oldVersion, newVersion);
                        if (alterSQLs != null) {
                            for (String sql : alterSQLs) {
                                db.execSQL(sql);
                            }
                        }
                    }
                }
            }
        }

    }

}
