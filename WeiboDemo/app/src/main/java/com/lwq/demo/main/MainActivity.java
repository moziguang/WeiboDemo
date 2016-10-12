package com.lwq.demo.main;

import java.util.*;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.lwq.base.event.EventManager;
import com.lwq.base.event.IEventListener;
import com.lwq.core.manager.IAccountManager;
import com.lwq.core.manager.ManagerProxy;
import com.lwq.core.manager.event.AccountEvent;
import com.lwq.core.model.WeiboInfo;
import com.lwq.demo.R;

/*
 * Description : Demo to show the weibo data
 *
 * Creation    : 2016-10-10
 * Author      : moziguang@126.com
 */
public class MainActivity extends AppCompatActivity {
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private WeiboRecyclerAdapter mRecyclerAdapter;
    private ListView mListView;
    private WeiboAdapter mAdapter;
//    private View mAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        refreshWeiboListview();
        addEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeEvent();
    }

    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.listview_main);
//        mRecyclerAdapter = new WeiboAdapter(this);
        mRecyclerAdapter = new WeiboRecyclerAdapter(this);
        mLinearLayoutManager = new SmoothScrollLinearLayoutManager(this);
//        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new MyItemAnimator());
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        mLinearLayoutManager.setStackFromEnd(true);
        View addBtn = findViewById(R.id.menu_add_btn_main);
        addBtn.setOnClickListener(mOnClickListener);
        View scrollBtn = findViewById(R.id.menu_scroll_to_top_btn_main);
        scrollBtn.setOnClickListener(mOnClickListener);
        View removeBtn = findViewById(R.id.menu_remove_btn_main);
        removeBtn.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.menu_add_btn_main:
//                    mRecyclerAdapter.addItem();
//                    mRecyclerView.smoothScrollBy(0,-getResources().getDimensionPixelSize(R.dimen.list_view_item_height));
//                    mRecyclerView.smoothScrollToPosition(0);
                    break;
                case R.id.menu_scroll_to_top_btn_main:
//                    mRecyclerView.smoothScrollToPosition(0);
                    break;
                case R.id.menu_remove_btn_main:
//                    mRecyclerAdapter.removeItem(2);
                    break;
                default:
                    break;
            }
        }
    };

    private void refreshWeiboListview(){
        List<WeiboInfo> weiboInfoList = ManagerProxy.getManager(IAccountManager.class).getWeiboInfoList();
        mRecyclerAdapter.setWeiboInfoList(weiboInfoList);
    }

    private IEventListener mRefreshEventListener = new IEventListener() {
        @Override
        public void onEvent(@NonNull String eventType, @Nullable Object params) {
            refreshWeiboListview();
        }
    };

    private void addEvent()
    {
        EventManager.defaultAgent().addEventListener(AccountEvent.EVENT_REFRESH_WEIBO_TIMELINE,mRefreshEventListener);
    }

    private void removeEvent()
    {
        EventManager.defaultAgent().removeEventListener(AccountEvent.EVENT_REFRESH_WEIBO_TIMELINE,mRefreshEventListener);
    }
}
