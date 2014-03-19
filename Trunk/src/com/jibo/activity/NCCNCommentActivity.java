package com.jibo.activity;

import com.jibo.dbhelper.NCCNAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.api.android.GBApp.R;

public class NCCNCommentActivity extends Activity {
	private String id = "";
	private TextView txtTitle;
	private TextView txtContent;
	private RelativeLayout rltMain;
	private NCCNAdapter nccnAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nccn_comment);
		initViews();
		TextView txt = new TextView(this);
		ScrollView sv = new ScrollView(this);
		sv.setVerticalScrollBarEnabled(true);
		txt.setBackgroundColor(Color.WHITE);
		txt.setTextColor(Color.BLACK);
		Intent intent = getIntent();
		if(intent != null) {
			id = intent.getStringExtra("id");
		}
		txtContent.setText(Html.fromHtml(nccnAdapter.getComments(id))+"(×¢ÊÍ)");
		txtTitle.setText(nccnAdapter.getDiseaseLst(id));
		sv.addView(txt);
	}
	
	public void initViews() {
		nccnAdapter = new NCCNAdapter(this, 1);
		rltMain = (RelativeLayout) findViewById(R.id.rlt_nccn_comment);
		txtTitle = (TextView) findViewById(R.id.txt_nccn_comment_title);
		txtContent = (TextView) findViewById(R.id.txt_nccn_comment_content);
		
		txtTitle.setBackgroundColor(Color.rgb(0, 85, 125));
		txtContent.setBackgroundColor(Color.rgb(199, 239, 255));
		rltMain.setBackgroundColor(Color.rgb(199, 239, 255));
		
	}
}
