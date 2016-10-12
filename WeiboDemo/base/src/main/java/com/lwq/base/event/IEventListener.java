package com.lwq.base.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/*
 * Description :
 *
 * Creation    : 2016/10/12
 * Author      : moziguang@126.com
 */
public interface IEventListener<P> {

    /**
     * @param eventType 事件类型
     * @param params
     */
    void onEvent(@NonNull String eventType,@Nullable P params);

}
