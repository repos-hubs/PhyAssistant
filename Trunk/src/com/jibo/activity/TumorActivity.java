package com.jibo.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.jibo.GBApplication;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.dbhelper.TNMAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.api.android.GBApp.R;
public class TumorActivity extends BaseSearchActivity implements OnItemClickListener{
	private TNMAdapter tnmAdapter;
	private GBApplication app;
	private ListView lvTnm;
	
	private String category;
	private ArrayList<String> nameList;
	private ArrayList<String> rankList;
	private HistoryAdapter historyAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tools_list);
		super.onCreate(savedInstanceState);
		inits();
	}

	@Override
	protected void onStop() {
		historyAdapter.closeHelp();
		tnmAdapter.closeHelp();
		super.onStop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void inits() {
		app = (GBApplication) getApplication();
		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");
		tnmAdapter = new TNMAdapter(this, 1);
		TextView txtTitle = (TextView) findViewById(R.id.txt_header_title);
		TextView txtSubTitle = (TextView) findViewById(R.id.sub_title);
		lvTnm = (ListView)findViewById(R.id.lst_item);
		
		category = tnmAdapter.getCategory();
		txtTitle.setText(getString(R.string.tool));
		txtSubTitle.setText(category);
		
		nameList = tnmAdapter.getNameListCategory(category);
		rankList = tnmAdapter.getRankListCategory(category);
		lvTnm.setAdapter((ListAdapter) new ArrayAdapter<String>(this,
				R.layout.list_item_text, nameList));
		lvTnm.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TextView tv = (TextView)arg1;
		Intent intent = new Intent(this, TumorArticleActivity1.class);
		int ecgID = Integer.parseInt(rankList.get(arg2).substring(3));
		int colID = -1;
		for(Entry en:app.getPdaColumnMap().entrySet()) {
			if(en.getValue().toString().equals(getString(R.string.tnm))) {
				colID = (Integer) en.getKey();
			}
		}
		historyAdapter.storeViewHistory(app.getLogin().getGbUserName(), ecgID, colID, -1, nameList.get(arg2));
		intent.putExtra("rank", rankList.get(arg2));
		intent.putExtra("rankTitle", nameList.get(arg2));
		
		System.out.println("rank   *** "+rankList.get(arg2));
		System.out.println("rankTitle   *** "+nameList.get(arg2));
		startActivity(intent);
	}
}
