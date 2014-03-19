package com.jibo.base.src;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.app.research.CategoryActivity;
import com.jibo.app.research.ResearchIndpPage;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;

public class FloorActivity extends ResearchIndpPage {

	FloorPack floorPack = new FloorPack();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(getApplicationContext());
		String title = CategoryActivity.title;
		if(title!=null)
		((TextView)findViewById(R.id.moduletitle)).setText(title);
		try {
			String category = this.getIntent().getStringExtra("category");
			if (this instanceof CategoryActivity) {
				if (category.toLowerCase().contains("journal")) {
					this.findViewById(R.id.searcheditlayout).setVisibility(
							View.VISIBLE);
				} else
					this.findViewById(R.id.searcheditlayout).setVisibility(
							View.GONE);
			} else {
				this.findViewById(R.id.searcheditlayout).setVisibility(
						View.GONE);
				floorPack.startup(this, category, CategoryActivity.srcRequests);
			}
			
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CategoryActivity.srcRequests.backFloor();
			this.finish();
			return true;
		}
		return false;
	}


	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		if(CategoryActivity.srcRequests!=null)
		CategoryActivity.srcRequests.recur_enabled =true;
	}

}
