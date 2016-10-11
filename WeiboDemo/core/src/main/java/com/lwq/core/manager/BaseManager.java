package com.lwq.core.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.lwq.base.http.handler.BaseRequestHandler;
import com.lwq.base.util.Log;
import com.lwq.base.util.StatusCodeDef;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public abstract class BaseManager implements IManager {
    protected final String TAG = BaseManager.this.getClass().getSimpleName();
    private Map<Integer, IOperateCallback> mCallbacks = new ConcurrentHashMap<>();
    private Map<Object, Set<Integer>> mCallbackOwners = new ConcurrentHashMap<Object, Set<Integer>>();

    protected void addCallback(int seq, IOperateCallback cb) {
        if (cb == null) {
            return;
        }
        cb.setSeq(seq);
        cb.onOperateStart();
        mCallbacks.put(seq, cb);
        if (cb.getOwner() == null) {
            return;
        }
        Set<Integer> keys = mCallbackOwners.get(cb.getOwner());
        if (keys == null) {
            keys = new HashSet<>();
            mCallbackOwners.put(cb.getOwner(), keys);
        }

        keys.add(seq);
    }

    IOperateCallback removeCallback(int seq) {
        IOperateCallback cb = mCallbacks.remove(seq);
        if (cb != null && cb.getOwner() != null) {
            Set<Integer> s = mCallbackOwners.get(cb.getOwner());
            if (s != null) {
                s.remove(seq);
                if(s.isEmpty())
                {
                    mCallbackOwners.remove(cb.getOwner());
                }
            }
        }

        return cb;
    }

    protected IOperateCallback getCallback(int seq) {
        return mCallbacks.get(seq);
    }

    public void removeAllCallbacks(Object owner) {
        if (owner == null) {
            Log.e(TAG, "removeAllCallbacks owner is null");
            return;
        }
        Set<Integer> keys = mCallbackOwners.remove(owner);
        if (keys != null) {
            for (Integer i : keys) {
                IOperateCallback callback = mCallbacks.remove(i);
                if(callback!=null) {
                    callback.cancel();
                }
            }
            keys.clear();
        }
    }

    protected int sendHttpRequest(BaseRequestHandler handler, IOperateCallback cb)
    {
        int seq = handler.sendHttpRequest();
        if(cb!=null) {
            if (seq >= 0) {
                addCallback(seq, cb);
            }else{
                notifyCallback(cb, StatusCodeDef.ERROR_ADD_HTTP_REQUEST,"添加HTTP请求到队列失败",null);
            }
        }
        return seq;
    }

    @Override
    public void init() {
        addEvent();
    }

    @Override
    public void uninit() {
        removeEvent();
    }

    protected abstract void addEvent();

    protected abstract void removeEvent();

    public void onTrimMemory(){

    }

    protected void notifyHttpCallback(BaseRequestHandler handler,int code,String msg,Object object)
    {
        IOperateCallback cb = getCallback(handler.getSeq());
        if(cb!=null)
        {
            cb.notifyOnResult(code, msg, object,this);
        }
    }

    protected void notifyCallback(IOperateCallback cb,int code,String msg,Object object)
    {
        if(cb!=null)
        {
            cb.notifyOnResult(code,msg,object,this);
        }
    }
}
