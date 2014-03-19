package com.jibo.activity;

import com.api.android.GBApp.R;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class VersionIntroductionActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.version_intruduction);
		boolean isPremium = getIntent().getBooleanExtra("isPremium", false);
		if(isPremium)
		((TextView)this.findViewById(R.id.tv_text_limit)).setText(getString(R.string.professional_invite)+getString(R.string.difference_version));
		else 
		((TextView)this.findViewById(R.id.tv_text_limit)).setText(getString(R.string.normal_invite)+getString(R.string.difference_version));
	}
}
