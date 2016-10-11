package com.lwq.demo.main;

import java.util.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lwq.demo.R;

/*
 * Description : Demo to show the weibo data
 *
 * Creation    : 2016-10-10
 * Author      : moziguang@126.com
 */

public class WeiboAdapter extends BaseAdapter {
    private static final String TAG = "WeiboAdapter";
    private LayoutInflater mInflater;
    private int mLastAnimItemPosition = 0;
    private List<Integer> mDataList;

    public WeiboAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mDataList = new ArrayList<>();
        for(int i=0;i<1000;i++){
            mDataList.add(i);
        }
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.item_weibo,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.mTitleTextView = (TextView) convertView.findViewById(R.id.weibo_title_tv);
            viewHolder.mAnimator = new AdapterAnimator();
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mTitleTextView.setText("测试、测试:"+getItem(position));
        if(mLastAnimItemPosition<position) {
            viewHolder.mAnimator.startLeftInAnim(convertView);
            mLastAnimItemPosition = position;
        }
        return convertView;
    }

    class ViewHolder {
        private TextView mTitleTextView;
        private AdapterAnimator mAnimator;
    }

    private int mAddItemCount = 1;
    public void AddItem(){
        mDataList.add(0,-mAddItemCount++);
        notifyDataSetChanged();
    }
}
