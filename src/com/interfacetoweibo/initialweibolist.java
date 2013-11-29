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
/* �ؼ���:		 
			mainlistadapter.java:
		 1. ��΢����ʵ����weiboStatusList��mainlistadapter��
		 2. listview(pulltorefreshview).setadapter(listadapter);
         pulltorefreshlistview.java: 
         3. ʵ��scroll��ontouchevent�ļ������Ѿ�����������imageview��text����Ӧ���²���
		 4.	touch event upʱ��ȡ΢����ʵ������
		 
		 5.�������ݼ��� weiboStatusList.addall
		 6.listadapter.notifyDataSetChanged
*/  
public class initialweibolist extends Activity {
    public Oauth2AccessToken mAccessToken;
    private Button footBtn;
    private PullToRefreshListView mainListView;
    private List<WeiboStatus> weiboStatusList;
    private MainListAdapter listAdapter;
    private Handler handler;
    private final int UPDATE_UI = 1; //����ui

    private int page = 1; //ҳ��
    public int flg_act = 0; //�����ˢ�»����������
    public int cnt_lst = 0; //ͳ��list�ϴε�����

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_list1);
        this.setAccessToken();
        this.initHandler();
        this.initialListAdapter();
        this.mainListView.startRefresh();//��һ��ˢ��

    }
    public void setAccessToken()
    {
		Intent intent = getIntent();
		String expires_in =intent.getStringExtra("expires_in");  
		String token = intent.getStringExtra("access_token");  
        Log.e("test","expires_in"+expires_in+"token"+token);
        mAccessToken = new Oauth2AccessToken(token, expires_in);

    }
    //��ʼ��Handler
    public void initHandler(){
        this.handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                  if (msg.what == UPDATE_UI){
                	  initialweibolist.this.listAdapter.notifyDataSetChanged();//֪ͨui��������
					  //notifyDataSetChanged: Notifies the attached observers that the underlying data has been changed 
					  //and any View reflecting the data set should refresh itself.
                	  initialweibolist.this.mainListView.onRefreshComplete();
                	  initialweibolist.this.mainListView.setSelection(initialweibolist.this.cnt_lst);
                	  initialweibolist.this.footBtn.setVisibility(View.VISIBLE);
                	  initialweibolist.this.showMsg("��Ϣ�������!");
                  }
            }
        };
    }
    //��ʾ��Ϣ
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
            //��Ӽ��ظ����¼�
            @Override
            public void onClick(View view) {
           initialweibolist.this.loadMore();
            }
        });
        //init main list view
 		//PullToRefreshListView�÷�:
 		//1.��ȡ����,2.����listenerToRefresh,3.���adapter,setAdpater
         this.mainListView = (PullToRefreshListView) findViewById(R.id.main_list_view);
         this.mainListView.addFooterView(this.footBtn);
         this.mainListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {/* �������¼��� */
             @Override
             public void onRefresh() {
			 RuntimeException here = new RuntimeException("here");
			 			here.fillInStackTrace();
				Log.w("test", "Called: " + this, here);
            	 initialweibolist.this.flg_act = 0;
            	 initialweibolist.this.page = 1;
            	 initialweibolist.this.getWeiboStatusList();
                 Log.i("test","��ʼˢ���ˡ�����");
             }
         });

         this.weiboStatusList = new ArrayList<WeiboStatus>();
 		//��listAdapter��weiboStatusList��ϵ��������Ҫ��Ϥһ��baseadapter��ʵ��ԭ��
         this.listAdapter = new MainListAdapter(getApplicationContext(),weiboStatusList);
         this.mainListView.setAdapter(listAdapter);//��adapter��䵽mainlistview(PullToRefreshView)
         
         //����listview�ĵ������
         this.mainListView.setOnItemClickListener (new AdapterView.OnItemClickListener() {
 			public void onItemClick(AdapterView<?> parent, View view,  int position, long id) {
 				Log.v("test", "click position"+position+"id"+id+"weibotext"+weiboStatusList.get(position-1).getText());
 				//�ڴ˽��е���΢�������ۣ�ת���Ȳ�������weiboStatusList��get(position)����
 				
 			}
 		});
    }
    //���ظ���
    public void loadMore(){
        this.flg_act = 1; //����Ϊ�������
        this.page++;
        this.getWeiboStatusList();
    }
    public void getWeiboStatusList(){

        //��ȡ��ҳ΢����Ϣ
        StatusesAPI statusesAPI = new StatusesAPI(this.mAccessToken);
		        //��ȡ��ҳ΢����Ϣ
        statusesAPI.homeTimeline(0,0,20,this.page,false, WeiboAPI.FEATURE.ALL,false,new RequestListener() {
            @Override	
            public void onComplete(String s) {

                WeiboStatusList weiboStatusList1 = new WeiboStatusList();
                List<WeiboStatus> weiboStatuses = weiboStatusList1.parser(s);//����ȡ��string ͨ��JASON�ĸ�ʽ�����������洢��list��
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

    //����΢����Ϣ
    public void updateWeiboStatusList(List<WeiboStatus> StatusList) {
        //��¼�ϴ�����
        this.cnt_lst = this.weiboStatusList.size();
        if (this.flg_act == 0){
            //�����ˢ�¾��������ݣ������list
           this.cnt_lst = 1;
           this.weiboStatusList.clear();
        }

        this.weiboStatusList.addAll(StatusList);//weiboStatusList���£���κ�listadapter��ϵ�������£��ο�����
        this.handler.sendEmptyMessageDelayed(UPDATE_UI,0);//����
    }
    public void update(View view){
        //֪ͨListˢ��
        this.mainListView.startRefresh();
    }

}