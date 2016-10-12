package com.lwq.demo.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.lwq.base.SharedPreferencesManager;
import com.lwq.base.util.Log;
import com.lwq.core.manager.IAccountManager;
import com.lwq.core.manager.ManagerProxy;
import com.lwq.demo.R;
import com.lwq.demo.base.BaseActivity;
import com.lwq.demo.main.MainActivity;
import com.lwq.demo.util.UiUtil;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

/*
 * Description : 
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */

public class LoginActivity extends BaseActivity {
    private AuthInfo mAuthInfo;
    private SsoHandler mWBSsoHandler;
    private Oauth2AccessToken mWBAccessToken;
    private UsersAPI mWBAuthUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkToken();
        initSinaAuthSDK();
        View view = findViewById(R.id.weibo_login_btn);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wbLogin();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWBSsoHandler != null) {
            mWBSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private void checkToken(){
        String uid = ManagerProxy.getManager(IAccountManager.class).getUid();
        if(!TextUtils.isEmpty(uid)) {
            gotoMainActivity();
            finish();
        }
    }

    private void wbLogin(){
        mWBSsoHandler.authorize(new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle values) {
                mWBAccessToken = Oauth2AccessToken.parseAccessToken(values);
                Log.i(TAG, "sina authorize onComplete");
                UiUtil.showToast("授权成功");
                SharedPreferences.Editor editor = SharedPreferencesManager.getInstance().getEditor();
                editor.putString(SharedPreferencesManager.KEY_SINA_UID, mWBAccessToken.getUid());
                editor.putString(SharedPreferencesManager.KEY_SINA_ACCESS_TOKEN, mWBAccessToken.getToken());
                editor.putString(SharedPreferencesManager.KEY_SINA_REFRESH_TOKEN, mWBAccessToken.getRefreshToken());
                editor.putLong(SharedPreferencesManager.KEY_SINA_EXPIRES_IN, mWBAccessToken.getExpiresTime());
                editor.apply();
                ManagerProxy.getManager(IAccountManager.class).initUser(mWBAccessToken.getUid());
                gotoMainActivity();
            }

            @Override
            public void onWeiboException(WeiboException e) {
                UiUtil.progressCancel();
                Log.e(TAG, "onWeiboException",e);
                UiUtil.showToast("授权出错:"+e.getMessage());
            }

            @Override
            public void onCancel() {
                UiUtil.progressCancel();
                Log.i(TAG, "sina authorize onCancel");
                UiUtil.showToast("授权取消");
            }
        });
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 初始化sina sdk
     */
    private void initSinaAuthSDK() {
        // 获取当前已保存过的 Token
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY,Constants.REDIRECT_URL,Constants.SCOPE);
        mWBSsoHandler = new SsoHandler(LoginActivity.this, mAuthInfo);
    }
}
