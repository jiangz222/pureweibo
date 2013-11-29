package com.interfacetoweibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.pureweibo.R;
//import com.example.pureweibo.MainActivity.AuthDialogListener;
//import com.sina.weibo.sdk.api.IWeiboAPI;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
//import com.weibo.sdk.android.Weibo;
//import com.weibo.sdk.android.api.StatusesAPI;
import com.widget.PullToRefreshListView;
import com.weibodatahandle.WeiboStatus;
import com.weibodatahandle.WeiboStatusList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.interfacetoweibo.MainListAdapter;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;
/* 关键点:		 
			mainlistadapter.java:
		 1. 将微博真实数据weiboStatusList和mainlistadapter绑定
		 2. listview(pulltorefreshview).setadapter(listadapter);
         pulltorefreshlistview.java: 
         3. 实现scroll和ontouchevent的监听，已经监听激活后的imageview和text的相应更新操作
		 4.	touch event up时获取微博的实际内容
		 
		 5.将新数据加入 weiboStatusList.addall
		 6.listadapter.notifyDataSetChanged
*/  
public class initialweibolist extends Activity {
    public Oauth2AccessToken mAccessToken;
    private Button footBtn;
    private PullToRefreshListView mainListView;
    private List<WeiboStatus> weiboStatusList;
    private MainListAdapter listAdapter;
    private Handler handler;
    private final int UPDATE_UI = 1; //更新ui

    private int page = 1; //页数
    public int flg_act = 0; //标记是刷新还是载入更多
    public int cnt_lst = 0; //统计list上次的总数

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_list1);
        this.setAccessToken();
        this.initHandler();
        this.initialListAdapter();
        this.mainListView.startRefresh();//第一次刷新

    }
    public void setAccessToken()
    {
		Intent intent = getIntent();
		String expires_in =intent.getStringExtra("expires_in");  
		String token = intent.getStringExtra("access_token");  
        Log.e("test","expires_in"+expires_in+"token"+token);
        mAccessToken = new Oauth2AccessToken(token, expires_in);

    }
    //初始化Handler
    public void initHandler(){
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                  if (msg.what == UPDATE_UI){
                	  initialweibolist.this.listAdapter.notifyDataSetChanged();//通知ui有新数据
					  //notifyDataSetChanged: Notifies the attached observers that the underlying data has been changed 
					  //and any View reflecting the data set should refresh itself.
                	  initialweibolist.this.mainListView.onRefreshComplete();
                	  initialweibolist.this.mainListView.setSelection(initialweibolist.this.cnt_lst);
                	  initialweibolist.this.footBtn.setVisibility(View.VISIBLE);
                	  initialweibolist.this.showMsg("信息加载完毕!");
                  }
            }
        };
    }
    //提示消息
    public void showMsg(String msg){
        Toast toast = Toast.makeText(this,msg,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL,Gravity.LEFT,0);
        toast.show();
    }
    public void initialListAdapter()
    {
        //init foot view
        this.footBtn = new Button(getApplicationContext());
        this.footBtn.setText(R.string.loader_more);
        this.footBtn.setVisibility(View.GONE);
        this.footBtn.setOnClickListener(new View.OnClickListener() {
            //添加加载更多事件
            @Override
            public void onClick(View view) {
           initialweibolist.this.loadMore();
            }
        });
        //init main list view
 		//PullToRefreshListView用法:
 		//1.获取布局,2.创建listenerToRefresh,3.填充adapter,setAdpater
         this.mainListView = (PullToRefreshListView) findViewById(R.id.main_list_view);
         this.mainListView.addFooterView(this.footBtn);
         this.mainListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {/* 下拉更新监听 */
             @Override
             public void onRefresh() {
			 RuntimeException here = new RuntimeException("here");
			 			here.fillInStackTrace();
				Log.w("test", "Called: " + this, here);
            	 initialweibolist.this.flg_act = 0;
            	 initialweibolist.this.page = 1;
            	 initialweibolist.this.getWeiboStatusList();
                 Log.i("test","开始刷新了。。。");
             }
         });

         this.weiboStatusList = new ArrayList<WeiboStatus>();
 		//将listAdapter和weiboStatusList联系起来，需要熟悉一下baseadapter的实现原理
         this.listAdapter = new MainListAdapter(getApplicationContext(),weiboStatusList);
         this.mainListView.setAdapter(listAdapter);//将adapter填充到mainlistview(PullToRefreshView)
         
         //监听listview的点击操作
         this.mainListView.setOnItemClickListener (new AdapterView.OnItemClickListener() {
 			public void onItemClick(AdapterView<?> parent, View view,  int position, long id) {
 				Log.v("test", "click position"+position+"id"+id+"weibotext"+weiboStatusList.get(position-1).getText());
 				//在此进行单个微博的评论，转发等操作，将weiboStatusList。get(position)传入
 				
 			}
 		});
    }
    //加载更多
    public void loadMore(){
        this.flg_act = 1; //设置为载入更多
        this.page++;
        this.getWeiboStatusList();
    }
    public void getWeiboStatusList(){

        //获取主页微博信息
        StatusesAPI statusesAPI = new StatusesAPI(this.mAccessToken);
		        //获取主页微博信息
        statusesAPI.homeTimeline(0,0,20,this.page,false, WeiboAPI.FEATURE.ALL,false,new RequestListener() {
            @Override	
            public void onComplete(String s) {

                WeiboStatusList weiboStatusList1 = new WeiboStatusList();
                List<WeiboStatus> weiboStatuses = weiboStatusList1.parser(s);//将获取的string 通过JASON的格式解析出来，存储在list中
                initialweibolist.this.updateWeiboStatusList(weiboStatuses);
            }
			@Override
			public void onError(WeiboException arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onIOException(IOException arg0) {
				// TODO Auto-generated method stub
				
			}
        });
    }

    //更新微博信息
    public void updateWeiboStatusList(List<WeiboStatus> StatusList) {
        //记录上次数量
        this.cnt_lst = this.weiboStatusList.size();
        if (this.flg_act == 0){
            //如果是刷新就重置数据，并清空list
           this.cnt_lst = 1;
           this.weiboStatusList.clear();
        }

        this.weiboStatusList.addAll(StatusList);//weiboStatusList更新，如何和listadapter联系起来更新？参考上面
        this.handler.sendEmptyMessageDelayed(UPDATE_UI,0);//更新
    }
    public void update(View view){
        //通知List刷新
        this.mainListView.startRefresh();
    }

}