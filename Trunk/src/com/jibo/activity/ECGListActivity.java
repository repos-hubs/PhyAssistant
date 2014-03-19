package com.jibo.activity;


import java.util.ArrayList;
import java.util.Map.Entry;


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

import com.jibo.GBApplication;
import com.jibo.data.entity.ECGEntity;
import com.jibo.dbhelper.ECGAdapter;
import com.jibo.dbhelper.HistoryAdapter;
import com.api.android.GBApp.R;
/**
 * 
 * @author gb
 * @description 当点击典型异常心电图按钮后跳转到该Activity
 * 
 */
public class ECGListActivity extends BaseSearchActivity implements OnItemClickListener{
	private GBApplication application;
	private ListView lvList;//疾病名称列表
	private ECGAdapter ecgAdapter;
	private ArrayList<ECGEntity> entityList;//疾病和描述组成的实体链表
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
		ecgAdapter.closeHelp();
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
		application = (GBApplication)this.getApplication();
		ecgAdapter = new ECGAdapter(this, 1);
		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");
		entityList = ecgAdapter.getECGList();//取得所有数据
		TextView txtTitle = (TextView) findViewById(R.id.txt_header_title);
		TextView txtSubTitle = (TextView) findViewById(R.id.sub_title);
		lvList = (ListView) findViewById(R.id.lst_item);
		
		lvList.setOnItemClickListener(this);
		txtTitle.setText(getString(R.string.tool));
		txtSubTitle.setText(getString(R.string.ecg));
		
		ArrayList<String> nameList = new ArrayList<String>();
		//遍历数据链表取得病症作为列表的内容
		for(ECGEntity en:entityList) {
			nameList.add(en.getTitle());
		}
		
		lvList.setAdapter((ListAdapter) new ArrayAdapter<String>(this, R.layout.list_item_text,nameList));
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();  
		String title = entityList.get(position).getTitle();
		int ecgID = Integer.parseInt(entityList.get(position).getId());
		int colID = -1;
		for(Entry en:application.getPdaColumnMap().entrySet()) {
			if(en.getValue().toString().equals(getString(R.string.ecg))) {
				colID = (Integer) en.getKey();
			}
		}
		historyAdapter.storeViewHistory(application.getLogin().getGbUserName(), ecgID, colID, -1, title);
		intent.setClass(this, ECGArticleActivity.class);//跳转到该病症对应的心电图和描述的Activity
		intent.putExtra("id", entityList.get(position).getId());
        intent.putExtra("title", entityList.get(position).getTitle());
        intent.putExtra("content", entityList.get(position).getContent());
		startActivity(intent);  
	}
}
