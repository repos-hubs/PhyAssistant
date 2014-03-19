package com.jibo.activity;

import java.util.ArrayList;
import java.util.Map;

import com.jibo.GBApplication;
import com.jibo.dbhelper.NCCNAdapter;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.api.android.GBApp.R;
public class NCCNListActivity extends BaseSearchActivity implements OnItemClickListener {
	private ListView lv;
	private ArrayList<String> diseaseNameLst;
	private ArrayList<String> diseaseVersionLst;
	private ArrayList<String> diseaseIDLst;
	private ArrayList<Map<String, Object>> data;
	private GBApplication application;
	private SimpleAdapter adapter;
	private NCCNAdapter nccnAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nccn_list);
		super.onCreate(savedInstanceState);
		inits();
		
	}
	
	@Override
	protected void onStop() {
		nccnAdapter.closeHelp();
		super.onStop();
	}
	
	public void inits() {
		application = (GBApplication) getApplication();
		nccnAdapter = new NCCNAdapter(this, 1);
		lv = (ListView) findViewById(R.id.lst_item);
		diseaseNameLst = new ArrayList<String>();
		diseaseVersionLst = new ArrayList<String>();
		diseaseIDLst = new ArrayList<String>();
		data = new ArrayList<Map<String,Object>>();
		TextView txtTitle = (TextView) findViewById(R.id.txt_header_title);
		TextView txtSubTitle = (TextView) findViewById(R.id.sub_title);
		txtTitle.setText(getString(R.string.tool));
		txtSubTitle.setText(getString(R.string.nccn_disease));
		
		nccnAdapter.getDiseaseList(diseaseIDLst,data);
		adapter = new SimpleAdapter(this,data,R.layout.druglst_info,
    			new String[]{"name","version"},new int[]{R.id.ListText1, R.id.ListText2});
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
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
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, NCCNDiseaseInfoActivity.class);
		intent.putExtra("id", diseaseIDLst.get(arg2));
		startActivity(intent);	

	}
}
