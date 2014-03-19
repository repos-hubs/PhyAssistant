package com.jibo.v4.pagerui;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.jibo.activity.BaseActivity;
import com.jibo.util.ActivityPool;
import com.jibo.util.tips.Mask;
import com.umeng.analytics.MobclickAgent;

public class PageActivity extends BaseActivity {
	public boolean started;
	private Mask mask;
	private boolean created;
	private Class parentClazz = null;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		resume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public boolean runStart() {
		// TODO Auto-generated method stub

		if (!this.started) {
			this.started = true;
			try {
				start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return started;
	}

	public void start() {
		// TODO Auto-generated method stub
	}

	public void resume() {
		// if (detail != null && detail.adapter != null)
		// detail.adapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		ActivityPool.getInstance().addActivity(this);
		MobclickAgent.onError(this);
		created = true;
		try {
			parentClazz = Class.forName(getIntent().getStringExtra(
					"parentClazz"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			onBack(false,true);
		return false;
	}

	public void onBack(Boolean stayTop,boolean isBackKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return super.dispatchKeyEvent(event);
	}

	public void finishParentClass() {
		// TODO Auto-generated method stub
		ActivityPool.getInstance().activityMap.get(getParentClazz()).finish();
	}

	public Class getParentClazz() {
		return parentClazz;
	}

	public void setParentClazz(Class parentClazz) {
		this.parentClazz = parentClazz;
	}

}
