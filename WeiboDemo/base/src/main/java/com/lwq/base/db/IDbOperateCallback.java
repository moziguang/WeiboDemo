package com.lwq.base.db;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */

/**
 *
 * @param <T>
 */
public interface IDbOperateCallback<T> {

    void onResult(int code, T resultData);

}

