package com.jibo.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.asynctask.uploadDataNew;
import com.jibo.common.BitmapManager;
import com.jibo.common.SharedPreferencesMgr;
import com.umeng.analytics.MobclickAgent;

/**
 * 广告界面
 * 
 * @author simon
 * 
 */
public class AdvertisingActivity extends BaseActivity {

	private Context context;

	private long time = 500;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.advertising);
		context = this;
		LinearLayout layout_advertising = (LinearLayout) findViewById(R.id.layout_advertising);
		Bitmap map = BitmapManager.loadBitmap(getIntent().getStringExtra(
				"imageUrl"));
		MobclickAgent.onEvent(this, "Advertising", "LilySplashOnClick", 1);// "SimpleButtonclick");
		MobclickAgent.onError(this);// LilySplash
									// Detail_ID_Name:splashOnClick,DetailID:splashUrl，Demo:"URL"

		uploadLoginLogNew("LilySplash", getIntent().getStringExtra("imageUrl"),
				"splashOnClick", null);
		final Intent intent = getIntent();
		if (map != null) {
			layout_advertising.setBackgroundDrawable(new BitmapDrawable(map));
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					intent.setClass(context,
							Class.forName(intent.getStringExtra("className")));
					startActivity(intent);
					AdvertisingActivity.this.finish();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}, time);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// if (null != SharedPreferencesMgr.getAdvertisingLink()) {
	// Intent intent = new Intent();
	// intent.setAction("android.intent.action.VIEW");
	// Uri content_url = Uri.parse("http://"+SharedPreferencesMgr
	// .getAdvertisingLink());
	// intent.setData(content_url);
	// startActivity(intent);
	// }
	// break;
	// }
	// return false;
	// }

}
