package com.lwq.base.event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.lwq.base.util.DefaultThreadFactory;
import com.lwq.base.util.Log;

/*
 * Description :
 *
 * Creation    : 2016/10/12
 * Author      : moziguang@126.com
 */
public class EventDispatcher implements IEventDispatcher {

    //最多同时存在一线程，线程空闲超过60秒则回收。
    private ExecutorService mExecutorService = null;

    private static final String TAG = EventDispatcher.class.getSimpleName();

    private Handler mDispatchHandler = null;
    private ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();

    private ArrayMap<String, List<EventListenerHolder>> mListenerHolder = new ArrayMap<>();

    public EventDispatcher() {
        this(null);
    }
    private ConcurrentHashMap<String,Integer> mDispatchingKeyMap = new ConcurrentHashMap<>();

    public EventDispatcher(Handler handler) {
        if (handler != null) {
            this.mDispatchHandler = handler;
        } else {
            this.mDispatchHandler = new Handler(Looper.getMainLooper());
        }
    }

    @Override
    public void distribute(@NonNull String eventType) {
        distribute(eventType,null);
    }

    /**
     * 定向分发事件。对指定dispatcher D派发类型为type T的事件。
     * D中所有关注事件类型T的listener都会收到事件调用。
     * 这种分发中,D的子dispatcher不参与分发。
     *
     * @param params        事件参数
     */
    @Override
    public <P> void distribute( @NonNull final String eventType, final P params) {
        if(mExecutorService ==null)
        {
            Log.i(TAG, String.format("[ distribute error mExecutorService==null. type = %s params = %s]",eventType, params));
            return;
        }
        Log.i(TAG, String.format("[ distribute. type = %s ]", eventType));

        try{
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(eventType)) {
                        Log.e(TAG, "[ unknown event type. stop distributing. ]");
                        return;
                    }
                    mLock.readLock().lock();
                    try {
                        List<EventListenerHolder> holders = mListenerHolder.get(eventType);
                        if (holders != null) {
                            for (EventListenerHolder holder : holders) {
                                if (holder != null) {
                                    final IEventListener listener = holder.listener;
                                    if (listener != null) {
                                        final String key = getDispatchingKey(eventType, listener);
                                        int count = mDispatchingKeyMap.containsKey(key) ? mDispatchingKeyMap.get(key)+1 : 1;
                                        mDispatchingKeyMap.put(key, count);
                                        mDispatchHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                int count = mDispatchingKeyMap.containsKey(key) ? mDispatchingKeyMap.get(key) : 0;
                                                if (count>0) {
                                                    listener.onEvent(eventType, params);
                                                } else {
                                                    Log.w(TAG, getDispatchingKey(eventType, listener) + " is removed from mDispatchingKeyMap");
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "distribute run Exception", e);
                    } finally {
                        mLock.readLock().unlock();
                    }
                }
            });
        }catch (Exception e){
            Log.e(TAG,"distribute Exception",e);
        }
    }

    @Override
    public void addEventListener(@NonNull String type, int priority,@NonNull IEventListener listener) {
//        Log.i(TAG, String.format("[ add event listener. type %s priority %d listener %s]",
//                type, priority, listener.toString()));
        mLock.writeLock().lock();
        try {
            List<EventListenerHolder> holderList = mListenerHolder.get(type);
            if (holderList == null) {
                holderList = new ArrayList<>();
                mListenerHolder.put(type, holderList);
            }
            EventListenerHolder holder = EventListenerHolder.obtain(priority, listener);
            int i=0;
            for(EventListenerHolder oldHolder:holderList)
            {
                if(oldHolder.priority<holder.priority)
                {
                    break;
                }
                i++;
            }
            holderList.add(i,holder);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public void addEventListener(@NonNull String type,@NonNull IEventListener listener) {
        addEventListener(type, 0, listener);
    }

    private List<EventListenerHolder> mWillRemoveHolderList = new ArrayList<>();

    @Override
    public void removeEventListener(@NonNull String type,@NonNull IEventListener listener) {
//        Log.i(TAG, String.format("[ remove event listener. type %s listener %s]",
//                type, listener.toString()));
        mLock.writeLock().lock();
        try{
            mWillRemoveHolderList.clear();
            List<EventListenerHolder> holderList = mListenerHolder.get(type);
            if (holderList != null) {
                for (EventListenerHolder holder : holderList) {
                    if (holder.listener == listener) {
                        mWillRemoveHolderList.add(holder);
                    }
                }
                for(EventListenerHolder holder : mWillRemoveHolderList){
                    mDispatchingKeyMap.remove(getDispatchingKey(type, holder.listener));
                    holderList.remove(holder);
//                    Log.d(TAG, " remove result = " + result);
                    holder.recycle();
                }
            }

            mWillRemoveHolderList.clear();
        }finally {
            mLock.writeLock().unlock();
        }

    }

    @Override
    public void removeEventListenersByType(@NonNull String type) {
//        Log.i(TAG, String.format("[ remove event listener by type %s ]",
//                type));
        mLock.writeLock().lock();
        try{
            List<EventListenerHolder> holderList = mListenerHolder.get(type);
            if (holderList != null) {
                Iterator<EventListenerHolder> iterator = holderList.iterator();
                while (iterator.hasNext()) {
                    EventListenerHolder holder = iterator.next();
                    mDispatchingKeyMap.remove(getDispatchingKey(type, holder.listener));
                    iterator.remove();
                    holder.recycle();
                }
            }
        }finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public void removeAllEventListeners() {
//        Log.i(TAG, "[ remove all event listeners ]");
        mLock.writeLock().lock();
        try{
            for (String type : mListenerHolder.keySet()) {
                List<EventListenerHolder> holderList = mListenerHolder.get(type);
                if (holderList != null) {
                    Iterator<EventListenerHolder> iterator = holderList.iterator();
                    while (iterator.hasNext()) {
                        EventListenerHolder holder = iterator.next();
                        mDispatchingKeyMap.remove(getDispatchingKey(type, holder.listener));
                        iterator.remove();
                        holder.recycle();
                    }
                }
            }
        }finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public boolean hasEventListener(@NonNull String type) {
//        Log.i(TAG, String.format("[ has event listener %s ]", type));
        mLock.readLock().lock();
        try {
            List<EventListenerHolder> holderList = mListenerHolder.get(type);
            return holderList != null && !holderList.isEmpty();
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void init() {
        if(mExecutorService ==null) {
            mExecutorService = new ThreadPoolExecutor(0, 1,
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),new DefaultThreadFactory("event_"));
        }
    }

    @Override
    public void shutdownNow() {
        if(mExecutorService !=null)
        {
            mExecutorService.shutdownNow();
            mExecutorService = null;
        }
        mDispatchingKeyMap.clear();
    }

    private String getDispatchingKey(String event,IEventListener listener){
        return event + "_" + listener.hashCode();
    }
}
