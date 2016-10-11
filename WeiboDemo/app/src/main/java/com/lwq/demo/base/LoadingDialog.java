package com.lwq.demo.base;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lwq.demo.R;


/**
 * Created by Luoweiqiang on 2015/7/18 0018.
 */
public class LoadingDialog extends Dialog {
    private ProgressBar mProgressView;
    public LoadingDialog(Context context, String strMessage) {
        this(context, R.style.loadDialog, strMessage);
    }

    public LoadingDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.dialog_loading);
        mProgressView = (ProgressBar) findViewById(R.id.dialog_progress);
        TextView tvMsg = (TextView) this.findViewById(R.id.loading_text);
        if (tvMsg != null&&!TextUtils.isEmpty(strMessage)) {
            tvMsg.setText(strMessage);
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
