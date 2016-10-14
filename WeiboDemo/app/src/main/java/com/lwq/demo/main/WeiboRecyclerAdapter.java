package com.lwq.demo.main;

import java.util.*;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lwq.base.util.Log;
import com.lwq.core.manager.IImageManager;
import com.lwq.core.manager.ManagerProxy;
import com.lwq.core.model.UserInfo;
import com.lwq.core.model.WeiboInfo;
import com.lwq.demo.R;
import com.nostra13.universalimageloader.core.assist.ImageSize;

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
    private ImageSize mHeaderImageSize;
    private ImageSize mThumbnailImageSize;
    private ImageSize mBmiddleImageSize;
    private IImageManager mImageManager;
    private boolean mEnableAnim;
    private ItemTouchHelper mItemTouchHelper;

    public WeiboRecyclerAdapter(Context context, RecyclerView recyclerView) {
        mInflater = LayoutInflater.from(context);
        mWeiboInfoList = new ArrayList<>();
        int headerHeight = context.getResources().getDimensionPixelSize(R.dimen.weibo_user_header_height);
        int thumbnailHeight = context.getResources().getDimensionPixelSize(R.dimen.weibo_img_height_thumbnail);
        int bmiddleHeight = context.getResources().getDimensionPixelSize(R.dimen.weibo_img_height_bmiddle);
        mHeaderImageSize = new ImageSize(headerHeight, headerHeight);
        mThumbnailImageSize = new ImageSize(thumbnailHeight, thumbnailHeight);
        mBmiddleImageSize = new ImageSize(bmiddleHeight, bmiddleHeight);
        mImageManager = ManagerProxy.getManager(IImageManager.class);
        mEnableAnim = true;
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback();
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public WeiboRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_weibo, parent, false);
        view.setOnTouchListener(mOnTouchListener);
        MyViewHolder holder = new MyViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(WeiboRecyclerAdapter.MyViewHolder holder, int position) {
        WeiboInfo weiboInfo = mWeiboInfoList.get(position);
        UserInfo userInfo = weiboInfo.getUserInfo();
        holder.mTitleTv.setText(weiboInfo.getText());
        holder.mTimeTv.setText(weiboInfo.getCreatedAt());

        if (userInfo != null) {
            holder.mUserNameTv.setText(userInfo.getName());
            mImageManager.loadUserHeaderImg(holder.mUserHeaderImg, userInfo.getProfileImageUrl(), R.color.c4, mHeaderImageSize);
        }
        for (ImageView imageView : holder.mImageViewList_9) {
            imageView.setVisibility(View.GONE);
        }
        List<String> picUrlList = weiboInfo.getPicUrlList();
        if (picUrlList != null && picUrlList.size() > 0) {
            int len = picUrlList.size();
            if (len == 1) {
                showBmiddleImage(holder.mWeiboImg_1_1, picUrlList.get(0));
            } else if (len == 4) {
                for (int i = 0; i < len; i++) {
                    showThumbnailImage(holder.mImageViewList_4.get(i), picUrlList.get(i));
                }
            } else {
                for (int i = 0; i < len; i++) {
                    showThumbnailImage(holder.mImageViewList_9.get(i), picUrlList.get(i));
                }
            }

        }
        if (holder.mPosition != position) {
            holder.mAnimator.cancelExistingAnim();
        }
        holder.mPosition = position;
        if (mLastAnimItemPosition < position) {
            if(mEnableAnim) {
                holder.mAnimator.startLeftInAnim(holder.itemView);
            }
            mLastAnimItemPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mWeiboInfoList.size();
    }

    private void showThumbnailImage(ImageView imageView, String url) {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = mThumbnailImageSize.getWidth();
        layoutParams.height = mThumbnailImageSize.getHeight();
        imageView.setVisibility(View.VISIBLE);
        mImageManager.loadThumbnailImg(imageView, url, R.color.c4, mThumbnailImageSize);
    }

    private void showBmiddleImage(ImageView imageView, String url) {

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = mBmiddleImageSize.getWidth();
        layoutParams.height = mBmiddleImageSize.getHeight();
        imageView.setVisibility(View.VISIBLE);
        mImageManager.loadThumbnailImg(imageView, url, R.color.c4, mBmiddleImageSize);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
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

        private List<ImageView> mImageViewList_4;
        private List<ImageView> mImageViewList_9;
        private AdapterAnimator mAnimator;
        private int mPosition;

        public MyViewHolder(View view) {
            super(view);
            mImageViewList_4 = new ArrayList<>();
            mImageViewList_9 = new ArrayList<>();
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
            mImageViewList_9.add(mWeiboImg_1_1);
            mImageViewList_9.add(mWeiboImg_1_2);
            mImageViewList_9.add(mWeiboImg_1_3);
            mImageViewList_9.add(mWeiboImg_2_1);
            mImageViewList_9.add(mWeiboImg_2_2);
            mImageViewList_9.add(mWeiboImg_2_3);
            mImageViewList_9.add(mWeiboImg_3_1);
            mImageViewList_9.add(mWeiboImg_3_2);
            mImageViewList_9.add(mWeiboImg_3_3);
            mImageViewList_4.add(mWeiboImg_1_1);
            mImageViewList_4.add(mWeiboImg_1_2);
            mImageViewList_4.add(mWeiboImg_2_1);
            mImageViewList_4.add(mWeiboImg_2_2);
            mAnimator = new AdapterAnimator();
        }
    }

    public void setWeiboInfoList(List<WeiboInfo> weiboInfoList) {
        mWeiboInfoList = weiboInfoList;
        notifyDataSetChanged();
    }

    public void setEnableAnim(boolean enable) {
        mEnableAnim = enable;
    }

    //    private int mAddItemCount = 1;
    //    public void addItem(){
    //        mLastAnimItemPosition++;
    //        mDataList.add(0,-mAddItemCount++);
    //        notifyItemInserted(0);
    //    }
    //
    public void removeItem(int position) {
        mWeiboInfoList.remove(position);
        notifyItemRemoved(position);
        mLastAnimItemPosition--;
    }

    private boolean isDraging = false;
    private int mDownX;
    private int mDownY;
    private int mTouchSlop;
    private boolean mHandleTouchEvent = false;
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int actionMasked = event.getActionMasked();
            int deltaY;
            int deltaX;
            switch (actionMasked) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = (int) event.getY();
                    mDownX = (int) event.getX();
                    mHandleTouchEvent = true;
                    isDraging = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    final int y = (int) event.getY();
                    final int x = (int) event.getX();
                    deltaY = Math.abs(y - mDownY);
                    deltaX = Math.abs(x - mDownX);
                    mHandleTouchEvent = isDraging || deltaY<mTouchSlop;
                    if(!isDraging && deltaX>deltaY && deltaX>mTouchSlop)
                    {
                        isDraging = true;
                        mItemTouchHelper.startDrag((MyViewHolder) v.getTag());
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isDraging = false;
                    break;
                default:
                    break;
            }

            return mHandleTouchEvent;
        }
    };

    private class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
        /**这个方法是用来设置我们拖动的方向以及侧滑的方向的*/
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0,ItemTouchHelper.END);
        }
        /**当我们拖动item时会回调此方法*/
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.d(TAG, "onMove");
            return false;
        }
        /**当我们侧滑item时会回调此方法*/
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//            Log.d(TAG, "onSwiped");
            MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
            removeItem(myViewHolder.getAdapterPosition());
        }
    }
}
