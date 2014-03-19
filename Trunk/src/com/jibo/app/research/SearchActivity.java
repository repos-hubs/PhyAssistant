package com.jibo.app.research;

import android.os.Bundle;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseSearchActivity;
import com.jibo.util.Logs;

public class SearchActivity extends LatestActivity {
	String searchvalue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		searchvalue = this.getIntent().getStringExtra(
				BaseSearchActivity.SEARCH_TEXT);
		Logs.i("=== text " + searchvalue);
		this.start();
	}


	@Override
	public void enableDropDown() {
		// TODO Auto-generated method stub
		view.setPullToRefreshEnabled(false);
	}


	public int getEmptyText() {
		return R.string.empty_search;
	}
	@Override
	public void searchText() {
		// TODO Auto-generated method stub
		
		String text = " Title:\\\"" + searchvalue + "\\\" OR Abstract:\\\"" + searchvalue+"\\\"";
		Logs.i("=== text " + text);
		if(searchvalue.equals("")){
			text = "*:*";
		}
		super.searchText(text);
		
	}

	@Override
	public void onBack(Boolean stayTop,boolean isBackKey) {
		// TODO Auto-generated method stub
		super.onBack(stayTop,isBackKey);
	}

	@Override
	public void change() {
		// TODO Auto-generated method stub
		initview = true;
		srcRequests.getRts().get(0).setToCache(false);
	}

}
