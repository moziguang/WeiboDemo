package com.lwq.base.util;

import java.io.*;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

/*
 * Description : 管理cache路径，优先使用/android/data/com.lwq.demo/cache目录
 *
 * Creation    : 2016-10-11
 * Author      : moziguang@126.com
 */
public class CacheDirManager {
    protected static final String TAG = "CacheDirManager";

    /*
     *
     * 1.日志路径:sBasePath/Logger/
     * 2.Crash路径:sBasePath/CrashLogger/
     *
     */
    public static String sBasePath;              //Base 路径
    public static String sLogPath;            // Log路径
    public static String sCrashLogPath;         // Crash 路径
    public static String sImagePath;         // image cache 路径

    public static final String SD_CACHE_BASE_PATH_NAME = "ucdemo";
    public static final String CACHE_LOGGER_BASE_PATH_NAME = "Logger";
    public static final String CACHE_CRASH_LOGGER_BASE_PATH_NAME = "CrashLogger";
    public static final String IMAGE_CACHE_PATH_NAME = "image";


    public static void init(Context context) {
        sBasePath = getExternalCacheDirBasePath(context);
        if (TextUtils.isEmpty(sBasePath)) {
            sBasePath = context.getCacheDir() + File.separator;
        }
        Log.i(TAG, "cache base path:" + sBasePath);

        // 日志缓存
        sLogPath = sBasePath + CACHE_LOGGER_BASE_PATH_NAME + File.separator;
        File loggerFile = new File(sLogPath);
        if (!loggerFile.exists()) {
            loggerFile.mkdir();
        }
        Log.i(TAG, "log path:" + sLogPath);

        //crash logger 缓存
        sCrashLogPath = sBasePath + CACHE_CRASH_LOGGER_BASE_PATH_NAME + File.separator;
        File crashLoggerFile = new File(sCrashLogPath);
        if (!crashLoggerFile.exists()) {
            crashLoggerFile.mkdir();
        }
        Log.i(TAG, "crash log path:" + sCrashLogPath);

        //image cache 缓存
        sImagePath = sBasePath + IMAGE_CACHE_PATH_NAME + File.separator;
        File imageCacheFile = new File(sCrashLogPath);
        if (!imageCacheFile.exists()) {
            imageCacheFile.mkdir();
        }
        Log.i(TAG, "image cache path:" + sImagePath);
    }

    private static String getExternalCacheDirBasePath(Context context) {

        File file = context.getExternalCacheDir();
        if (file != null) {
            String d = file.getAbsolutePath() + File.separator;
            Log.i(TAG, "primary external  Storage:" + d);
            return d;

        } else {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String d = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + SD_CACHE_BASE_PATH_NAME + File.separator;
                Log.i(TAG, "secondary external Storage:" + d);
                return d;
            } else {
                return null;
            }
        }
    }

    public static String getFileNameByPath(String imagePath) {
        String fileName = "";
        if (TextUtils.isEmpty(imagePath)) {
            fileName = "";
            return fileName;
        } else {
            String[] sps = imagePath.split(File.separator);
            if (sps.length <= 0) {
                fileName = imagePath;
            } else {
                fileName = sps[sps.length - 1];
            }
            return fileName;
        }
    }

    /**
     * 获取目录文件大小
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

}
