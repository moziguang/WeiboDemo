package com.lwq.base.event;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/*
 * Description :
 *
 * Creation    : 2016/10/12
 * Author      : moziguang@126.com
 */
public interface IEventDispatcher {

    /**
     * 定向分发事件。对指定dispatcher D派发类型为type T的事件。
     * D中所有关注事件类型T的listener都会收到事件调用。
     * @param eventType 事件类型
     */
     void distribute(@NonNull String eventType);

    /**
     * 定向分发事件。对指定dispatcher D派发类型为type T的事件。
     * D中所有关注事件类型T的listener都会收到事件调用。
     * @param eventType 事件类型
     * @param params 事件参数
     * @param <P>   事件参数类型
     */
    <P> void distribute(@NonNull String eventType,@Nullable P params);

    void addEventListener(@NonNull String type, int priority,@NonNull IEventListener listener);
    void addEventListener(@NonNull String type,@NonNull IEventListener listener);
    void removeEventListener(@NonNull String type,@NonNull IEventListener listener);
    void removeEventListenersByType(@NonNull String type);
    void removeAllEventListeners();
    boolean hasEventListener(@NonNull String type);
    void init();
    void shutdownNow();
}
