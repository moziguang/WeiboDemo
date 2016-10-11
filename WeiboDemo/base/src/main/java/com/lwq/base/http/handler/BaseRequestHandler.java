package com.lwq.base.http.handler;

import android.os.Handler;

import com.lwq.base.http.HttpRequestManager;
import com.lwq.base.http.listener.IHttpResponseListener;
import com.lwq.base.http.model.HttpRequest;
import com.lwq.base.util.Log;
import com.lwq.base.util.StatusCodeDef;


/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public abstract class BaseRequestHandler<T> {
    protected String TAG = ((Object) this).getClass().getSimpleName();
    protected IHttpResponseListener<T> mHttpResponseListener = null;
    protected Handler mHandler = null;

    private HttpRequest mRequest;
    private boolean isCancel;

    public int getSeq() {
        return seq;
    }

    private int seq = -1;

    /**
     * @param handler 如果handler为null，回调httpResponseListener会在后台线程进行，否则在handler所在的线程进行
     */
    public BaseRequestHandler(Handler handler, IHttpResponseListener<T> httpResponseListener) {
        this.mHttpResponseListener = httpResponseListener;
        this.mHandler = handler;
    }

    /**
     * 请注意，此方法每个实例只允许调用一次，否则mRequest不会再被发送
     * 如果需要发送多个相同请求，请创建多个HttpRequestHandler实例来实现
     */
    public int sendHttpRequest() {
        if (mRequest == null) {
            mRequest = getHttpRequest();
            int seq = HttpRequestManager.getInstance().sendHttpRequest(mRequest, this);
            if (this.seq == -1) {
                this.seq = seq;
            }
        }
        Log.i(TAG, "sending http request parameter:" + mRequest);
        return seq;
    }

    /**
     * 会触发onCancel
     */
    public void cancelRequest() {
        if (mRequest != null) {
            HttpRequestManager.getInstance().cancelHttpRequest(mRequest);
        }
    }

    public void onCancel() {
        if (mHttpResponseListener != null) {
            if (mHandler == null) {
                mHttpResponseListener.onCancel(this);
                mHttpResponseListener = null;
            } else {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (mHttpResponseListener != null) {
                            mHttpResponseListener.onCancel(BaseRequestHandler.this);
                            mHttpResponseListener = null;
                        }
                    }
                });
            }
        }
    }

    public void onProgress(final int totalSize, final int currentSize) {
        if (mHttpResponseListener != null) {
            if (mHandler == null) {
                mHttpResponseListener.onProgress(this, totalSize, currentSize);
            } else {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (mHttpResponseListener != null) {
                            mHttpResponseListener.onProgress(BaseRequestHandler.this, totalSize, currentSize);
                        }
                    }
                });
            }
        }
    }

    private int code = 0;
    private String msg = "";

    public int getErrorCode() {
        return code;
    }

    public String getErrorMsg() {
        return msg;
    }

    protected void setError(int code, String msg) {
        Log.w(TAG, " setError code:%d ,error msg:%s", code, msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * @param data
     * @param mimeType
     * @param errCode
     * @param errMsg
     * @param cacheFilePath
     */
    public final void onResult(byte[] data, String mimeType, int errCode, String errMsg,
                               String cacheFilePath) {
        T result = null;
        if (errCode == StatusCodeDef.HTTP_OK) {
            try {
                result = handleData(data, mimeType);
            } catch (Exception e) {
                Log.e(TAG, " handleData Exception:", e);
                setError(StatusCodeDef.ERROR_HANDLE_DATA_ERROR, "handleData Exception: " + e.getMessage());
            }
        } else {
            onHttpError(mRequest, code, msg);
            setError(errCode, errMsg);
        }
        notifyListener(result);
    }

    /**
     * @param result
     */
    private final void notifyListener(final T result) {
        if (isCancel || mHttpResponseListener == null) return;
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                public void run() {
                    if (mHttpResponseListener != null) {
                        mHttpResponseListener.onResult(BaseRequestHandler.this, code, msg, result);
                    }
                }
            });
        } else {
            mHttpResponseListener.onResult(BaseRequestHandler.this, code, msg, result);
        }
    }

    protected abstract HttpRequest getHttpRequest();

    protected void onHttpError(HttpRequest request, int code, String msg) {

    }

    /**
     * @param data
     * @param mimeType
     * @return
     * @throws Exception
     */
    protected abstract T handleData(byte[] data, String mimeType) throws Exception;

    protected void setSeq(int seq) {
        this.seq = seq;
    }
}
