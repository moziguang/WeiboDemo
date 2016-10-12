package com.lwq.demo.main;
/*
 * Description : Demo to show the weibo data
 *
 * Creation    : 2016-10-10
 * Author      : moziguang@126.com
 */

import java.lang.ref.WeakReference;

import android.animation.ValueAnimator;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class AdapterAnimator {
    private static final int STATUS_STOP = 0;
    private static final int STATUS_LEFT_IN = 1;
    private static final int STATUS_RIGHT_OUT = 2;
    private WeakReference<View> mViewRef;
    private ValueAnimator mAnimator;
    private int mStatus;

    public AdapterAnimator() {
        mStatus = STATUS_STOP;
        mAnimator = ValueAnimator.ofFloat(0f,1f);
        mAnimator.addUpdateListener(mlistener);
    }

    public void cancelExistingAnim(){
        if(mViewRef!=null)
        {
            View view = mViewRef.get();
            if(view!=null){
                ViewCompat.setTranslationX(view,0);
                ViewCompat.setAlpha(view,1);
            }
        }
        mViewRef = null;
        mAnimator.cancel();
        mAnimator.setFloatValues(0f,1f);

    }

    public void startLeftInAnim(View view){
        cancelExistingAnim();
        mViewRef = new WeakReference<>(view);
        mStatus = STATUS_LEFT_IN;
        mAnimator.start();
    }

    public void startRightOutAnim(View view){
        cancelExistingAnim();
        mViewRef = new WeakReference<>(view);
        mStatus = STATUS_RIGHT_OUT;
        mAnimator.start();
    }

    private final ValueAnimator.AnimatorUpdateListener mlistener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if(mViewRef!=null&&mViewRef.get()!=null){
                View view = mViewRef.get();
                float value = (float) animation.getAnimatedValue();
                if(mStatus == STATUS_LEFT_IN)
                {
                    updateLeftInAnimValue(value,view);
                }else if(mStatus == STATUS_RIGHT_OUT){
                    updateRightOutAnimValue(value,view);
                }
            }
        }
    };

    private void updateLeftInAnimValue(float value, View view){
        view.setTranslationX((value-1)*view.getWidth());
        view.setAlpha(value);
    }
    private void updateRightOutAnimValue(float value, View view){
        view.setTranslationX(value*view.getWidth());
        view.setAlpha(value);
    }
}
