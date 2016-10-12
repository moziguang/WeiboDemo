package com.lwq.base.event;

import android.support.v4.util.Pools;

/*
 * Description :
 *
 * Creation    : 2016/10/12
 * Author      : moziguang@126.com
 */
public class EventListenerHolder /*implements Comparable<EventListenerHolder>*/ {

    private static final Pools.Pool<EventListenerHolder> holderPool
            = new Pools.SynchronizedPool<>(50);

    public static EventListenerHolder obtain() {
        EventListenerHolder holder = holderPool.acquire();
        if (holder != null) {
            return holder;
        }
        return new EventListenerHolder();
    }

    public static EventListenerHolder obtain(IEventListener listener) {
        EventListenerHolder holder = EventListenerHolder.obtain();
        holder.listener = listener;
        return holder;
    }

    public static EventListenerHolder obtain(int priority, IEventListener listener) {
        EventListenerHolder holder = EventListenerHolder.obtain();
        holder.listener = listener;
        holder.priority = priority;
        return holder;
    }

    public int priority = 0;
    public IEventListener listener = null;

    public void recycle() {
        this.priority = 0;
        this.listener = null;
        holderPool.release(this);
    }

}
