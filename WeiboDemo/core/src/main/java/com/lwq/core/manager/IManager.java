package com.lwq.core.manager;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public interface IManager {

    void init();
    void uninit();
    void removeAllCallbacks(Object owner);
    void onDbOpen();
    void onTrimMemory();
}
