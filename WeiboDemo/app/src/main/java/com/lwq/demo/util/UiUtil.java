package com.lwq.demo.util;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.widget.Toast;

import com.lwq.base.util.Log;
import com.lwq.core.WeiboApp;
import com.lwq.demo.base.LoadingDialog;


/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public class UiUtil {

    private static final String TAG = "UiUtil";
    private static WeakReference<LoadingDialog> mProgressDialogRef;

    public static void showToast(int resId){
        Context context = WeiboApp.getContext();
        showToast(context.getResources().getString(resId));
    }

    public static void showToast(final String message) {
        Context context = WeiboApp.getContext();
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            WeiboApp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WeiboApp.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public static void showToast(final String message, final int errorCode) {
        Context context = WeiboApp.getContext();
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Toast.makeText(context, message + ":" + errorCode, Toast.LENGTH_SHORT).show();
        } else {
            WeiboApp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WeiboApp.getContext(), message + ":" + errorCode, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void showLongToast(final String message) {
        Context context = WeiboApp.getContext();
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            WeiboApp.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WeiboApp.getContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Loging弹框
     *
     * @param msg
     * @param dismissListener
     */
    public static void progressShow(String msg, Context ctx, DialogInterface.OnDismissListener dismissListener) {
        progressCancel();
        LoadingDialog progressDialog = new LoadingDialog(ctx, msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnDismissListener(dismissListener);
        progressDialog.show();
        mProgressDialogRef = new WeakReference<>(progressDialog);
    }

    /**
     * 取消等待弹框
     */
    public static void progressCancel() {
        try {
            if (mProgressDialogRef != null&&mProgressDialogRef.get()!=null&&mProgressDialogRef.get().isShowing()) {
                mProgressDialogRef.get().dismiss();
            }
        } catch (final Exception e) {
            Log.e(TAG,"progressCancel Exception",e);
        } finally {
            mProgressDialogRef = null;
        }
    }
}
