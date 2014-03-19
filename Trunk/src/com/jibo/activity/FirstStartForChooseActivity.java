package com.jibo.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.app.DetailsData;
import com.jibo.app.news.AllActivity;
import com.jibo.app.push.PushConst;
import com.jibo.app.research.DetailItemClickListener;
import com.jibo.base.src.EntityObj;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.util.ActivityPool;
import com.umeng.analytics.MobclickAgent;

/**
 * 广告界面
 * 
 * @author simon
 * 
 */
public class FirstStartForChooseActivity extends BaseActivity implements
		OnClickListener {

	private ImageView newUser;
	private ImageView oldUser;
	private ImageView logo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SharedPreferencesMgr.setFirstLaunch(false);
		boolean loginBack = getIntent().getBooleanExtra("loginBack", false);
		if (SharedPreferencesMgr.getIsAuto() && !loginBack) {
			String name = SharedPreferencesMgr.getUserName();
			if (!"".equals(name)) {
				
				Intent in = new Intent(this, HomePageActivity.class);
				if (ActivityPool.getInstance().activityMap
						.containsKey(HomePageActivity.class)) {
					in.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				}
				startActivity(in);
				finish();
			}
		}

		setContentView(R.layout.firststartchooseactivity);
		newUser = (ImageView) findViewById(R.id.newuser_btn);
		oldUser = (ImageView) findViewById(R.id.olduser_btn);

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		width = width / 2 + width / 3;
		int height = wm.getDefaultDisplay().getHeight();

		logo = (ImageView) findViewById(R.id.logo);
		logo.setAdjustViewBounds(true);
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) logo
				.getLayoutParams();
		params1.height = (height / 10) * 4;
		logo.setLayoutParams(params1);
		logo.setMaxWidth(width);
		logo.setMaxHeight((height / 10) * 4);

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) newUser
				.getLayoutParams();
		params.width = width;

		newUser.setAdjustViewBounds(true);
		newUser.setLayoutParams(params);
		newUser.setMaxWidth(width);
		newUser.setMaxHeight(height);

		oldUser.setAdjustViewBounds(true);
		oldUser.setLayoutParams(params);
		oldUser.setMaxWidth(width);
		oldUser.setMaxHeight(height);

		newUser.setOnClickListener(this);
		oldUser.setOnClickListener(this);

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

	@Override
	public void onClick(View v) {
		if (!v.isEnabled()) {
			Log.e("simon", "on click error, button is disabled");
			return;
		}
		Intent intent = null;
		switch (v.getId()) {
		case R.id.newuser_btn:// 新用户
			intent = new Intent(this, Registration_accountActivity.class);
			MobclickAgent.onEvent(this, "LRBtnClick", "RegisterBtn", 1);// "SimpleButtonclick");

			uploadLoginLogNew("LRBtnClick", "", "RegisterBtn", null);
			break;
		case R.id.olduser_btn:// 老用户
			intent = new Intent(this, LoginActivity.class);
			MobclickAgent.onEvent(this, "LRBtnClick", "LoginBtn", 1);// "SimpleButtonclick");

			uploadLoginLogNew("LRBtnClick", "", "LoginBtn", null);
			break;
		default:
			break;
		}
		startActivity(intent);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			GBApplication.gbapp.quit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
