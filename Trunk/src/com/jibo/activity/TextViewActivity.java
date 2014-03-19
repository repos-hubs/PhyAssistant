package com.jibo.activity;

import com.api.android.GBApp.R;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Button;

/**
 * 信息显示类
 * @author simon
 *
 */
public class TextViewActivity extends BaseActivity {

	public static final String TITLE = "title";
	public static final String CONTENT = "content";
	public static final String TitleIcon = "titleIcon";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.text_view_activity);
		String title = getIntent().getStringExtra(TITLE); 
		String content = getIntent().getStringExtra(CONTENT); 
		//0表示默认图标显示，-1表示不显示，其他如R.id.xxx表示就显示这个图片
		int titleIcon = getIntent().getIntExtra(TitleIcon, 0); 
		if(TextUtils.isEmpty(title)||TextUtils.isEmpty(content))
			finish();
		
		if(titleIcon != 0 ){
			if(titleIcon == -1){
				findViewById(R.id.btnClose).setVisibility(View.GONE);
			}else{
				findViewById(R.id.btnClose).setBackgroundResource(titleIcon);
			}
		}
		
		((TextView)findViewById(R.id.text_title)).setText(title);
		((TextView)findViewById(R.id.text_content)).setText(content);
		
		((Button)findViewById(R.id.read_ok)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextViewActivity.this.finish();
			}
		});
	}
}
