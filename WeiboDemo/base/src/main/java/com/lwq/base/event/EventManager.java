package com.lwq.base.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/*
 * Description :
 *
 * Creation    : 2016/10/12
 * Author      : moziguang@126.com
 */
public class EventManager implements IEventDispatcher{

    private static final EventManager sInstance = new EventManager();
    private static EventDispatcher sDispatcher = null;
    private static volatile boolean mInited = false;
    public static EventManager defaultAgent() {
        return sInstance;
    }

    private EventManager() {
    }

    public void init()
    {
        if(!mInited) {
            sDispatcher = new EventDispatcher();
            sDispatcher.init();
            mInited = true;
        }
    }

    /**
     * 定向分发事件。对指定dispatcher D派发类型为type T的事件。
     * D中所有关注事件类型T的listener都会收到事件调用。
     *
     *
     * @param eventType 事件类型
     */
    @Override
    public void distribute(final @NonNull String eventType) {
        sDispatcher.distribute(eventType, null);
    }

    /**
     * 定向分发事件。对指定dispatcher D派发类型为type T的事件。
     * D中所有关注事件类型T的listener都会收到事件调用。
     *
     *
     * @param eventType 事件类型
     * @param params 事件参数
     */
    @Override
    public <P> void distribute(final @NonNull String eventType, @Nullable final P params) {
        sDispatcher.distribute(eventType, params);
    }

    @Override
    public void addEventListener(@NonNull String type, int priority,@NonNull IEventListener listener) {
        sDispatcher.addEventListener(type, priority, listener);
    }

    @Override
    public void addEventListener(@NonNull String type,@NonNull IEventListener listener) {
        sDispatcher.addEventListener(type, 0, listener);
    }

    @Override
    public void removeEventListener(@NonNull String type,@NonNull IEventListener listener) {
        sDispatcher.removeEventListener(type, listener);
    }

    @Override
    public void removeEventListenersByType(@NonNull String type) {
        sDispatcher.removeEventListenersByType(type);
    }

    @Override
    public void removeAllEventListeners() {
        sDispatcher.removeAllEventListeners();
    }

    @Override
    public boolean hasEventListener(@NonNull String type) {
        return sDispatcher.hasEventListener(type);
    }

    @Override
    public void shutdownNow() {
        if(sDispatcher !=null)
        {
            sDispatcher.shutdownNow();
        }
    }

}
