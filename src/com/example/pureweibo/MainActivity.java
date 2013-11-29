package com.example.pureweibo;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.interfacetoweibo.initialweibolist;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;
//import com.weibo.sdk.android.util.AccessTokenKeeper;

public class MainActivity extends Activity {
	
    /** ��װ�� "access_token"��"expires_in"��"refresh_token"�����ṩ�����ǵĹ�����  */
    private Oauth2AccessToken mAccessToken;
    /** ΢��API�ӿ��࣬�ṩ��½�ȹ���  */
    private Weibo mWeibo;
    /** ע�⣺SsoHandler ����sdk֧��ssoʱ��Ч */
    private SsoHandler mSsoHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initToken();

    }
    public void   initToken()
    {        
        SharedPreferences sp = this.getApplicationContext().getSharedPreferences("weiboAccount",MODE_PRIVATE);
        String accessToken = sp.getString("accessToken","");
        String expiresTime = sp.getString("expiresTime","");
        if(accessToken.equals("")){
        	//sso handler,not implement OAuth2.0 now....so firstly, need  a sinaweibo APP in phone 
    		mWeibo = Weibo.getInstance(ConstantS.APP_KEY, ConstantS.REDIRECT_URL);
    		mSsoHandler = new SsoHandler(MainActivity.this, mWeibo);
        	Toast.makeText(this,"Auth register now",Toast.LENGTH_LONG).show();
    		mSsoHandler.authorize(new AuthDialogListener());
        }
    	else 
    	{   
            this.startInterFaceOfWeibo();
    		//Toast.makeText(this,"Auth register already",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * ΢����֤��Ȩ�ص��ࡣ
     * 1. SSO��½ʱ����Ҫ��{@link #onActivityResult}�е���mSsoHandler.authorizeCallBack��
     *    �ûص��Żᱻִ�С�
     * 2. ��SSO��½ʱ������Ȩ�󣬾ͻᱻִ�С�
     * ����Ȩ�ɹ����뱣���access_token��expires_in����Ϣ��SharedPreferences�С�
     */
    class AuthDialogListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            Log.e("test", "token"+token+"expires_in"+expires_in);

            mAccessToken = new Oauth2AccessToken(token, expires_in);
            if (mAccessToken.isSessionValid()) {
                //����acesstooken ��SharedPreferences
                SharedPreferences sp = MainActivity.this.getApplicationContext().getSharedPreferences("weiboAccount",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("accessToken",token);
                editor.putString("expiresTime", expires_in);
                editor.commit();
                Toast.makeText(MainActivity.this, "��֤�ɹ�", Toast.LENGTH_SHORT).show();
                MainActivity.this.startInterFaceOfWeibo();
            }
            else
            {
                Toast.makeText(MainActivity.this, "��֤������Чֵ", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(getApplicationContext(), 
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(), 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO ��Ȩ�ص�
        // ��Ҫ������ SSO ��½��Activity������дonActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
    private void startInterFaceOfWeibo()
    {
		// ����һ��Bundle����
        SharedPreferences sp = this.getApplicationContext().getSharedPreferences("weiboAccount",MODE_PRIVATE);
        String accessToken = sp.getString("accessToken","");
        String expiresTime = sp.getString("expiresTime","");
		Intent intent = new Intent(MainActivity.this,
				initialweibolist.class);
		intent.putExtra("expires_in",  expiresTime);  
		intent.putExtra("access_token",  accessToken);  

		// ����intent��Ӧ��Activity
		startActivity(intent);
		finish();
    }
    public void  newnote(View view){

        StatusesAPI statusesAPI = new StatusesAPI(this.mAccessToken);
        statusesAPI.update("this is a test weibo note from exampleweibo client", "0.0", "0.0", new RequestListener (){
            @Override	
            public void onComplete(String s) {
            	Log.i("TEST", "send note done");
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
}
