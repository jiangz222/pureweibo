package com.example.pureweibo;

import com.weibo.sdk.android.Oauth2AccessToken;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class Login extends Activity {
	//private ScaleAnimation scaleani;
	private Animation scaleani;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		 this.welcomeimage();
		scaleani.setAnimationListener(new AnimationListener()			
		{
			@Override
			public void onAnimationEnd(Animation arg0) {		//lisnter to animation end
				Intent it=new Intent(Login.this,MainActivity.class);	
				startActivity(it);
				finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {		//空实现
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}			
		}
		);	
	}
	public void welcomeimage()
	{
		ImageView iv=(ImageView)this.findViewById(R.id.login_bg);
		/*
		scaleani=new ScaleAnimation(1.07f, 1.00f, 1.07f, 1.00f, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleani.setDuration(2000);	//milliseconds
		*/
		scaleani = AnimationUtils.loadAnimation(this,
				R.anim.my_anim);
		scaleani.setDuration(2000);
		scaleani.setFillAfter(true);//需要，否则会回到imageview的动画初始状态然后才是animation的end
		iv.startAnimation(scaleani);	//start animation
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
