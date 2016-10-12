package com.lwq.demo.main;

import java.util.*;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwq.core.model.UserInfo;
import com.lwq.core.model.WeiboInfo;
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
    private List<WeiboInfo> mWeiboInfoList;

    public WeiboRecyclerAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mWeiboInfoList = new ArrayList<>();
    }

    @Override
    public WeiboRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_weibo, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WeiboRecyclerAdapter.MyViewHolder holder, int position) {
//        int i = mDataList.get(position);
//        if(i<0)
//        {
//            holder.mTitleTextView.setText("this is new item add " +i);
//        }else {
//            holder.mTitleTextView.setText("this is item " +i);
//        }
        WeiboInfo weiboInfo = mWeiboInfoList.get(position);
        UserInfo userInfo = weiboInfo.getUserInfo();
        holder.mTitleTv.setText(weiboInfo.getText());
        holder.mTimeTv.setText(weiboInfo.getCreatedAt());
        if(userInfo!=null) {
            holder.mUserNameTv.setText(userInfo.getName());
        }
        if(holder.mPosition!=position){
            holder.mAnimator.cancelExistingAnim();
        }
        holder.mPosition = position;
        if(mLastAnimItemPosition<position) {
            holder.mAnimator.startLeftInAnim(holder.itemView);
            mLastAnimItemPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mWeiboInfoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mUserNameTv;
        private TextView mTitleTv;
        private TextView mTimeTv;
        private ImageView mUserHeaderImg;
        private ImageView mWeiboImg_1_1;
        private ImageView mWeiboImg_1_2;
        private ImageView mWeiboImg_1_3;
        private ImageView mWeiboImg_2_1;
        private ImageView mWeiboImg_2_2;
        private ImageView mWeiboImg_2_3;
        private ImageView mWeiboImg_3_1;
        private ImageView mWeiboImg_3_2;
        private ImageView mWeiboImg_3_3;
        private AdapterAnimator mAnimator;
        private int mPosition;
        public MyViewHolder(View view)
        {
            super(view);
            mTitleTv = (TextView) view.findViewById(R.id.weibo_title_tv);
            mUserNameTv = (TextView) view.findViewById(R.id.user_name_tv);
            mTimeTv = (TextView) view.findViewById(R.id.user_time_tv);
            mUserHeaderImg = (ImageView) view.findViewById(R.id.user_header_img);
            mWeiboImg_1_1 = (ImageView) view.findViewById(R.id.weibo_img_1_1);
            mWeiboImg_1_2 = (ImageView) view.findViewById(R.id.weibo_img_1_2);
            mWeiboImg_1_3 = (ImageView) view.findViewById(R.id.weibo_img_1_3);
            mWeiboImg_2_1 = (ImageView) view.findViewById(R.id.weibo_img_2_1);
            mWeiboImg_2_2 = (ImageView) view.findViewById(R.id.weibo_img_2_2);
            mWeiboImg_2_3 = (ImageView) view.findViewById(R.id.weibo_img_2_3);
            mWeiboImg_3_1 = (ImageView) view.findViewById(R.id.weibo_img_3_1);
            mWeiboImg_3_2 = (ImageView) view.findViewById(R.id.weibo_img_3_2);
            mWeiboImg_3_3 = (ImageView) view.findViewById(R.id.weibo_img_3_3);
            mAnimator = new AdapterAnimator();
        }
    }

    public void setWeiboInfoList(List<WeiboInfo> weiboInfoList) {
        mWeiboInfoList = weiboInfoList;
        notifyDataSetChanged();
    }

    //    private int mAddItemCount = 1;
//    public void addItem(){
//        mLastAnimItemPosition++;
//        mDataList.add(0,-mAddItemCount++);
//        notifyItemInserted(0);
//    }
//
//    public void removeItem( int position){
//        mDataList.remove(position);
//        notifyItemRemoved(position);
//        mLastAnimItemPosition--;
//    }

}
