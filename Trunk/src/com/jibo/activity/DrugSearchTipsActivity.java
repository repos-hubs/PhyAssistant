package com.jibo.activity;

import com.api.android.GBApp.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

public class DrugSearchTipsActivity extends Activity {
	private LinearLayout ll = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_tips);
		
		ll = (LinearLayout) findViewById(R.id.layout_search_tips);
		ll.setClickable(true);
		ll.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				DrugSearchTipsActivity.this.finish();
				return false;
			}
		});
	}
	
}
