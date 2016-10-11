package com.lwq.demo.main;

import java.util.*;

import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/*
 * Description : Demo to show the weibo data
 *
 * Creation    : 2016-10-10
 * Author      : moziguang@126.com
 */

public class MyDefaultItemAnimator extends DefaultItemAnimator {

    private static final String TAG = "MyItemAnimator";
    private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        Log.d(TAG,"animateRemove item = " + holder);
        resetAnimation(holder);
        mPendingRemovals.add(holder);
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX,
                               int toY) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder,
                                 RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop,
                                 int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        Log.d(TAG,"runPendingAnimations");
        for (RecyclerView.ViewHolder holder : mPendingRemovals) {
            animateRemoveImpl(holder);
        }
        mPendingRemovals.clear();
    }

    private void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        mRemoveAnimations.add(holder);
        animation.setDuration(getRemoveDuration());
        animation.alpha(0);
        animation.setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                dispatchRemoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(View view) {
                Log.d(TAG,"onAnimationEnd item = " + holder);
                animation.setListener(null);
                ViewCompat.setAlpha(view, 1);
                dispatchRemoveFinished(holder);
                mRemoveAnimations.remove(holder);
                dispatchFinishedWhenDone();
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        });
    }

    private void resetAnimation(RecyclerView.ViewHolder holder) {
        AnimatorCompatHelper.clearInterpolator(holder.itemView);
        endAnimation(holder);
    }

    private void dispatchFinishedWhenDone() {
        Log.d(TAG,"dispatchFinishedWhenDone isRunning() = " + isRunning());
        if (!isRunning()) {
            Log.d(TAG,"dispatchAnimationsFinished");
            dispatchAnimationsFinished();
        }
    }
}
