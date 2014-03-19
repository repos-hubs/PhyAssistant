package com.jibo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.dao.DBFactory;
import com.jibo.dao.DBHelper;
import com.jibo.dao.DaoManager;
import com.jibo.dao.DaoSession;
import com.jibo.data.entity.ContactDrgEntity;
import com.jibo.entity.ContactManufuture;
import com.umeng.analytics.MobclickAgent;

public class ContactMufactrueActivity extends BaseSearchActivity implements
		OnItemClickListener {
	private ListView mEventsList;
	SimpleAdapter simpleAdapter;
	private final String COLUMN_TEXT_01 = "title";
	private final String COLUMN_TEXT_02 = "phone";
	private final String COLUMN_TEXT_03 = "message";
	TextView tvt = null;
	boolean isZh ;
	List<ContactManufuture> list;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contact_manu);
		super.onCreate(savedInstanceState);
		tvt = (TextView) findViewById(R.id.txt_header_title);
		tvt.setText(R.string.contactMufacture);
		isZh = Locale.getDefault().getLanguage().contains("zh");
		mEventsList = (ListView) findViewById(R.id.lst_item);
		init();
	}

	public void init() {// 初始化event列表
		List<Map<String, Object>> list = getData();
		simpleAdapter = new SimpleAdapter(this, list,
				R.layout.list_item_text_icon_icon, new String[] {
						COLUMN_TEXT_01, COLUMN_TEXT_02, COLUMN_TEXT_03 },
				new int[] { R.id.ListText, R.id.icon1, R.id.icon2 });
		mEventsList.setAdapter(simpleAdapter);
		mEventsList.setOnItemClickListener(this);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		list = daoSession.getContactManufutureDao().queryBuilder().list();
		
	    /**按拼音首字母排序*/
	    Collections.sort(list, new Comparator<ContactManufuture>() {
			@Override
			public int compare(ContactManufuture obj1, ContactManufuture obj2) {
				return ((int)obj1.getManufutureBrandInfo().getPyName().charAt(0)) - ((int)obj2.getManufutureBrandInfo().getPyName().charAt(0));
			}
		});
	    
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>(); // selectManufuture
			map.put(COLUMN_TEXT_01, isZh?list.get(i).getManufutureBrandInfo().getBrandName():list.get(i).getManufutureBrandInfo().getBrandNameEn());
			if(!TextUtils.isEmpty(list.get(i).getTelphone()))
			{
				map.put(COLUMN_TEXT_02, R.drawable.call);
			}
			if(!TextUtils.isEmpty(list.get(i).getEmail()))
			{
				map.put(COLUMN_TEXT_03, R.drawable.email);
			}
			mapList.add(map);
		}

		return mapList;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ContactManufuture entity = list.get(position);
		String drugId = entity.getManufutureBrandInfo().getProductId();
		MobclickAgent.onEvent(this,"branId",drugId,1);
		uploadLoginLogNew("Manufacturer", drugId, "branId", null);
		Intent intent = new Intent(this, ContactMufacturedetailActivity.class);
		intent.putExtra("manufuture",entity);
		startActivity(intent);
	}
	
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}