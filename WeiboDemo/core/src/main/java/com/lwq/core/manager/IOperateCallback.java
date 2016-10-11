package com.lwq.core.manager;

import android.os.Handler;
import android.os.Looper;

/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public abstract class IOperateCallback<T> {

    private static final String TAG = IOperateCallback.class.getSimpleName();
    private int seq;//在被添加到manager时赋值。用于移出回调
    private Object owner;
    protected Handler handler = new Handler(Looper.getMainLooper());
    protected long operateStartTime = -1;
    private boolean isCancel;

    public IOperateCallback(Object owner) {
        this.owner = owner;
    }

    Object getOwner() {
        return owner;
    }

    public abstract void onResult(int code, String msg, T data);

    protected void postToMainThread(Runnable runnable) {
        handler.post(runnable);
    }

    void notifyOnResult(final int code,final String msg,final T data,final BaseManager manager){
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if(!isCancel()) {
                manager.removeCallback(seq);
                onResult(code, msg, data);
            }
        } else {
            postToMainThread(new Runnable() {
                @Override
                public void run() {
                    if(!isCancel()) {
                        manager.removeCallback(seq);
                        onResult(code, msg, data);
                    }
                }
            });
        }
    }

    void onOperateStart()
    {
        operateStartTime = System.currentTimeMillis();
    }

    public boolean isCancel() {
        return isCancel;
    }

    void cancel() {
        this.isCancel = true;
    }

    void setSeq(int seq){
        this.seq = seq;
    }
}
