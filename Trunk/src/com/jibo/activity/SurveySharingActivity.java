package com.jibo.activity;

import com.api.android.GBApp.R;
import com.jibo.common.SharedPreferencesMgr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SurveySharingActivity extends BaseSearchActivity implements OnClickListener{

	TextView tvTitle=null;
	TextView tvContent=null;
	Button invitebtn1=null;
	Button invitebtn2=null;
	TextView tvtxt=null;
	SharedPreferences setting;
	//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.survey_share_activity);
		super.onCreate(savedInstanceState);
		tvTitle=(TextView) findViewById(R.id.MyViewTitle);
		tvContent=(TextView) findViewById(R.id.MyViewContent);
		tvTitle.setText(SharedPreferencesMgr.getSharingTitle());

		tvContent.setText(SharedPreferencesMgr.getSharingContent());
		tvContent.setText(Html.fromHtml(SharedPreferencesMgr.getSharingContent()));
		invitebtn1=(Button) findViewById(R.id.invitebtn1);
		invitebtn2=(Button) findViewById(R.id.invitebtn2);
		invitebtn1.setOnClickListener(this);
		invitebtn2.setOnClickListener(this);
		tvtxt=(TextView) findViewById(R.id.txt_header_title);//getSurveyTopic()
		tvtxt.setText(SharedPreferencesMgr.getSurveyTopic());

		tvtxt.setTextSize(18);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.invitebtn1:
			sharing(R.array.items2, 0);
			break;
		case R.id.invitebtn2:
			Intent inte=new Intent(this,HomePageActivity.class);				
			startActivity(inte);
			break;
			
			
		}
			
	}
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				this.finish();
			
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
