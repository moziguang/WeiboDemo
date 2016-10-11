package com.lwq.demo.main;

import java.util.*;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lwq.demo.R;

/*
 * Description : Demo to show the weibo data
 *
 * Creation    : 2016-10-10
 * Author      : moziguang@126.com
 */

public class WeiboRecyclerAdapter extends RecyclerView.Adapter<WeiboRecyclerAdapter.MyViewHolder> {
    private static final String TAG = "WeiboAdapter";
    private LayoutInflater mInflater;
    private int mLastAnimItemPosition = 0;
    private List<Integer> mDataList;

    public WeiboRecyclerAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mDataList = new ArrayList<>();
        for(int i=0;i<1000;i++){
            mDataList.add(i);
        }
    }

    @Override
    public WeiboRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_weibo, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WeiboRecyclerAdapter.MyViewHolder holder, int position) {
        int i = mDataList.get(position);
        if(i<0)
        {
            holder.mTitleTextView.setText("this is new item add " +i);
        }else {
            holder.mTitleTextView.setText("this is item " +i);
        }
        if(mLastAnimItemPosition<position) {
            holder.mAnimator.startLeftInAnim(holder.itemView);
            mLastAnimItemPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTitleTextView;
        private AdapterAnimator mAnimator;
        public MyViewHolder(View view)
        {
            super(view);
            mTitleTextView = (TextView) view.findViewById(R.id.weibo_title_tv);
            mAnimator = new AdapterAnimator();
        }
    }

    private int mAddItemCount = 1;
    public void addItem(){
        mLastAnimItemPosition++;
        mDataList.add(0,-mAddItemCount++);
        notifyItemInserted(0);
    }

    public void removeItem( int position){
        mDataList.remove(position);
        notifyItemRemoved(position);
        mLastAnimItemPosition--;
    }

}
