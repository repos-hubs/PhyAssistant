package com.jibo.activity;

import com.api.android.GBApp.R;
import com.jibo.ui.TextField;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * ��������
 * @author simon
 *
 */
public class DisclaimerActivity extends BaseActivity implements OnClickListener {
	TextView title;
	TextView prompt;
	LinearLayout content;
	Button btnHelp;
	private LinearLayout contentLayout;
	private boolean isShow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.disclaimer);
		
		//��Registration_accountActivity��ת
		isShow = getIntent().getBooleanExtra("isShow", false);

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		contentLayout = (LinearLayout) findViewById(R.id.content_layout);

		LayoutParams params = (LayoutParams) contentLayout.getLayoutParams();
		params.height = wm.getDefaultDisplay().getHeight() * 2 / 3;
		contentLayout.setLayoutParams(params);

		btnHelp = (Button) findViewById(R.id.helpBtn);
		title = (TextView) findViewById(R.id.disclaimer_title);
		prompt = (TextView) findViewById(R.id.disclaimer_prompt);
		content = (LinearLayout) findViewById(R.id.layout_disclaimer_content);
		title.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		title.getPaint().setFakeBoldText(true);
		prompt.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		prompt.getPaint().setFakeBoldText(true);
		prompt.setText(R.string.disclaimer_prompt);

		// TextField textView = new TextField(this, null,
		// this.getString(R.string.disclaimer_content),true,wm.getDefaultDisplay()
		// .getWidth()-40);
		// textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 10);
		// textView.setTextColor(Color.TRANSPARENT);
		TextView textView = new TextView(this);
		textView.setText(this.getString(R.string.disclaimer_content));
		textView.setTextSize(15);
		textView.setTextColor(Color.BLACK);
		content.addView(textView);
		btnHelp.setOnClickListener(this);

//		if (isShow) {
//			btnHelp.setText(getString(R.string.ok_yes));
//			findViewById(R.id.pageIndex3).setVisibility(View.GONE);
//		}
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.helpBtn:
			if (!isShow) {
				MobclickAgent.onEvent(this, "LRBtnClick", "RAgreeBtn", 1);// "SimpleButtonclick");
				uploadLoginLogNew("LRBtnClick", "", "RAgreeBtn", null);
				TipHelper.newUserUseFlag = true;
				Intent intent = new Intent();
//				intent.setClass(this, HomePageActivity.class);
				intent.setClass(this, InitializeActivity.class);
				intent.putExtra("FROM", "GBApp");
				startActivity(intent);
			}
			finish();
			break;
		default:
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			uploadLoginLogNew("LRBtnClick", "", "RBack3Btn", null);
			MobclickAgent.onEvent(this, "LRBtnClick", "RBack3Btn", 1);
			this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
