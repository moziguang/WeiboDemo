package com.lwq.base;
/*
 * Description : 
 *
 * Creation    : 2016/10/12
 * Author      : moziguang@126.com
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public class SharedPreferencesManager {
    private  static final String PREFERENCES_NAME = "com.lwq.demo";
    public static final String KEY_SINA_UID           = "sina_uid";
    public static final String KEY_SINA_ACCESS_TOKEN  = "sina_access_token";
    public static final String KEY_SINA_EXPIRES_IN    = "sina_expires_in";
    public static final String KEY_SINA_REFRESH_TOKEN    = "sina_refresh_token";

    private static final SharedPreferencesManager sInstance = new SharedPreferencesManager();

    private SharedPreferences mPreferences;

    public  static SharedPreferencesManager getInstance() {
        return sInstance;
    }

    public void init(Context context){
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
    }

    public String getStringByKey(String key, @Nullable String defValue){
        if(mPreferences!=null){
            return mPreferences.getString(key,defValue);
        }else{
            throw new IllegalStateException("Please call init() to init SharedPreferences!");
        }
    }

    public int getIntByKey(String key, @Nullable int defValue){
        if(mPreferences!=null){
            return mPreferences.getInt(key,defValue);
        }else{
            throw new IllegalStateException("Please call init() to init SharedPreferences!");
        }
    }

    public long getLongByKey(String key, @Nullable long defValue){
        if(mPreferences!=null){
            return mPreferences.getLong(key,defValue);
        }else{
            throw new IllegalStateException("Please call init() to init SharedPreferences!");
        }
    }

    public boolean getBooleanByKey(String key, @Nullable boolean defValue){
        if(mPreferences!=null){
            return mPreferences.getBoolean(key,defValue);
        }else{
            throw new IllegalStateException("Please call init() to init SharedPreferences!");
        }
    }

    public void saveStringWithKey(String key, String value)
    {
        if(mPreferences!=null){
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(key,value);
            editor.apply();
        }else{
            throw new IllegalStateException("Please call init() to init SharedPreferences!");
        }
    }

    public void saveIntWithKey(String key, int value)
    {
        if(mPreferences!=null){
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt(key,value);
            editor.apply();
        }else{
            throw new IllegalStateException("Please call init() to init SharedPreferences!");
        }
    }

    public void saveLongWithKey(String key, Long value)
    {
        if(mPreferences!=null){
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putLong(key,value);
            editor.apply();
        }else{
            throw new IllegalStateException("Please call init() to init SharedPreferences!");
        }
    }

    public void saveBooleanWithKey(String key, boolean value)
    {
        if(mPreferences!=null){
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(key,value);
            editor.apply();
        }else{
            throw new IllegalStateException("Please call init() to init SharedPreferences!");
        }
    }

    public SharedPreferences.Editor getEditor()
    {
        if(mPreferences!=null){
            SharedPreferences.Editor editor = mPreferences.edit();
            return editor;
        }else{
            throw new IllegalStateException("Please call init() to init SharedPreferences!");
        }
    }
}
