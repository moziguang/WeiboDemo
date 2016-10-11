package com.lwq.demo.main;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

import com.lwq.base.util.Log;

/*
 * Description : Demo to show the weibo data
 *
 * Creation    : 2016-10-10
 * Author      : moziguang@126.com
 */

public class SmoothScrollLinearLayoutManager extends LinearLayoutManager {

    private static final int MIN_TIME_FOR_SCROLLING = 150;
    private static final int MAX_TIME_FOR_SCROLLING = 300;
    private static final String TAG = "SmoothScroll";

    public SmoothScrollLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        LinearSmoothScroller linearSmoothScroller =
          new LinearSmoothScroller(recyclerView.getContext()){
              @Override
              protected int calculateTimeForScrolling(int dx) {
                  int time = super.calculateTimeForScrolling(dx);
                  time = time<MIN_TIME_FOR_SCROLLING ? MIN_TIME_FOR_SCROLLING : time>MAX_TIME_FOR_SCROLLING ? MAX_TIME_FOR_SCROLLING:time;
                  Log.d(TAG,"calculateTimeForScrolling time = " + time);
                  return time;
              }
              @Override
              public PointF computeScrollVectorForPosition(int targetPosition) {
                  return SmoothScrollLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
              }
          };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }
}
