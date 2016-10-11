package com.lwq.core.manager;

import java.util.*;

import com.lwq.base.util.Log;
import com.lwq.core.manager.impl.AccountManager;


/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public class ManagerProxy {
    private static final String TAG = ManagerProxy.class.getSimpleName();
    private static Map<Class<? extends IManager>, IManager> sManagerMap = new LinkedHashMap<Class<? extends IManager>, IManager>();

    public static void loadManager(){
        Log.i(TAG, "loadAppManager");
        AccountManager accountManager = new AccountManager();
        accountManager.init();
        sManagerMap.put(IAccountManager.class,accountManager);
    }

    public static void unLoadManager() {
        Log.i(TAG, "unLoadManager");
        List<Map.Entry<Class<? extends IManager>, IManager>> list = new ArrayList<>(sManagerMap.entrySet());
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<Class<? extends IManager>, IManager> entry = list.get(i);
            entry.getValue().uninit();
        }
        sManagerMap.clear();
    }

    public static <T extends IManager> T getManager(Class<T> apiClz) {
        IManager manager = sManagerMap.get(apiClz);
        T t = null;
        try {
            t = (T) manager;
        } catch (ClassCastException e) {
            Log.e(TAG, e);
        }
        return t;
    }

    public static void removeAllCallbacks(Object owner) {
        List<Map.Entry<Class<? extends IManager>, IManager>> list = new ArrayList<>(sManagerMap.entrySet());
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<Class<? extends IManager>, IManager> entry = list.get(i);
            entry.getValue().removeAllCallbacks(owner);
        }
    }

    public static void callOnDbOpen() {
        List<Map.Entry<Class<? extends IManager>, IManager>> list = new ArrayList<>(sManagerMap.entrySet());
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<Class<? extends IManager>, IManager> entry = list.get(i);
            Log.i(TAG, "callOnDbOpen entry = " + entry);
            entry.getValue().onDbOpen();
        }
    }

    public static void onTrimMemory() {
        List<Map.Entry<Class<? extends IManager>, IManager>> list = new ArrayList<>(sManagerMap.entrySet());
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<Class<? extends IManager>, IManager> entry = list.get(i);
            Log.i(TAG, "onTrimMemory entry = " + entry);
            entry.getValue().onTrimMemory();
        }
    }
}
