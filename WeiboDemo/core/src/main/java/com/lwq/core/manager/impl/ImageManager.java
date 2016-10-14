package com.lwq.core.manager.impl;

import java.io.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lwq.base.util.CacheDirManager;
import com.lwq.base.util.Log;
import com.lwq.core.WeiboApp;
import com.lwq.core.manager.BaseManager;
import com.lwq.core.manager.IImageManager;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.utils.StorageUtils;

/*
 * Description : 
 *
 * Creation    : 2016/10/13
 * Author      : moziguang@126.com
 */

public class ImageManager extends BaseManager implements IImageManager {
    private static final int IMG_DSK_SIZE = 100 * 1024 * 1024;
    private static final int IMG_THREAD_POOL_SIZE = 3;

    private static ImageSize sUserHeaderImageSize;
    private static ImageSize sThumbnailImageSize;
    private static DisplayImageOptions sUserHeaderOption;
    private static DisplayImageOptions sImageOption;

    /* ***************************************** 加载网络图片 ***************************************** */
    public static DisplayImageOptions options_cache_on_disk = new DisplayImageOptions.Builder()//
                                                                .cacheInMemory(true)
                                                                .cacheOnDisk(true)
                                                                .bitmapConfig(Bitmap.Config.RGB_565)
                                                                .build();

    @Override
    public void init() {
        super.init();
        Context context = WeiboApp.getContext();
        sImageOption = new DisplayImageOptions.Builder()
                         .cacheInMemory(true)
                         .cacheOnDisk(true)
                         .imageScaleType(ImageScaleType.EXACTLY)
                         .bitmapConfig(Bitmap.Config.RGB_565)
                         .build();
        sUserHeaderOption = new DisplayImageOptions.Builder()
                              .cacheInMemory(true)
                              .cacheOnDisk(true)
                              .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                              .bitmapConfig(Bitmap.Config.RGB_565)
                              .displayer(new CircleBitmapDisplayer())
                              .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                                                                                context)
                                            .memoryCacheSizePercentage(50)//用最大内存的1/2
                                            .threadPoolSize(IMG_THREAD_POOL_SIZE)
                                            .threadPriority(Thread.MIN_PRIORITY)
                                            .tasksProcessingOrder(QueueProcessingType.LIFO)//后进先出
                                            .defaultDisplayImageOptions(options_cache_on_disk)
                                            .diskCache(createDiskCache(context, new Md5FileNameGenerator(), IMG_DSK_SIZE, 0))
                                            .build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void addEvent() {

    }

    @Override
    protected void removeEvent() {

    }

    @Override
    public void onDbOpen() {

    }

    @Override
    public void loadUserHeaderImg(ImageView imageView, String url, int defaultResId, ImageSize imageSize) {
        if (imageView == null) {
            Log.d(TAG, "ImageView is null");
            return;
        }
        imageView.setImageResource(defaultResId);
        ImageLoader.getInstance().displayImage(url, new ImageViewAware(imageView), sUserHeaderOption, imageSize, null, null);
    }

    @Override
    public void loadThumbnailImg(ImageView imageView, String url, int defaultResId, ImageSize imageSize) {
        if(url!=null) url = url.replace("/thumbnail/","/bmiddle/");
        if (imageView == null) {
            Log.d(TAG, "ImageView is null");
            return;
        }
        imageView.setImageResource(defaultResId);
        ImageLoader.getInstance().displayImage(url, new ImageViewAware(imageView), sImageOption, imageSize, null, null);
    }

    private DiskCache createDiskCache(Context context, FileNameGenerator diskCacheFileNameGenerator,
                                      long diskCacheSize, int diskCacheFileCount) {
        DiskCache diskCache = null;
        File individualCacheDir = new File(CacheDirManager.sImagePath);
        File reserveCacheDir = StorageUtils.getIndividualCacheDirectory(context);
        try {
            diskCache = new LruDiskCache(individualCacheDir, reserveCacheDir, diskCacheFileNameGenerator, diskCacheSize, diskCacheFileCount);
        } catch (IOException e) {
            Log.e(TAG, "create Disk Cache Exception", e);
        }
        return diskCache;
    }

    @Override
    public void pause() {
        ImageLoader.getInstance().pause();
    }

    @Override
    public void resume() {
        ImageLoader.getInstance().resume();
    }
}
