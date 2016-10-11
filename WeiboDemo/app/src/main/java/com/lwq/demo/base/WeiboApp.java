package com.lwq.demo.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.lwq.base.http.HttpRequestManager;
import com.lwq.base.util.CacheDirManager;
import com.lwq.core.manager.ManagerProxy;

/*
 * Description : 
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */

public class WeiboApp extends Application {
    private static Context sContext;
    private static Handler sHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        sHandler = new Handler();
        CacheDirManager.init(this);
        HttpRequestManager.getInstance().init(this);
        ManagerProxy.loadManager();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ManagerProxy.onTrimMemory();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ManagerProxy.onTrimMemory();
    }

    public static Context getContext() {
        return sContext;
    }

    public static void runOnUiThread(Runnable runnable) {
        if(sHandler!=null&&runnable!=null) {
            sHandler.post(runnable);
        }
    }
}
