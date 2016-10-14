package com.lwq.core.manager;

import java.util.*;

import android.view.View;
import android.widget.ImageView;

import com.lwq.core.model.WeiboInfo;
import com.nostra13.universalimageloader.core.assist.ImageSize;

/*
 * Description : 
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public interface IImageManager extends IManager {

    void loadUserHeaderImg(ImageView imageView, String url, int defaultResId, ImageSize imageSize);

    void loadThumbnailImg(ImageView imageView, String url, int defaultResId, ImageSize imageSize);

    void pause();

    void resume();
}
