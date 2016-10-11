package com.lwq.base.db;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public interface ITable {
    String getTableName();
    int getAddTableDbVersion();
    String getCreateTableSQL();
    String[] getAlterTableSQL(int oldVersion, int newVersion);
}
