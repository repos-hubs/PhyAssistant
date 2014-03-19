package com.jibo.app.interact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;
import com.jibo.activity.HomePageActivity;
import com.jibo.adapter.SearchHistoryAdapter;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.common.Constant;
import com.jibo.data.entity.DrugInteractEntity;
import com.jibo.data.entity.SearchHistoryEntity;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.dbhelper.InteractAdapter;
import com.jibo.dbhelper.SearchHistoryDao;
import com.jibo.dbhelper.StoreSearchHistoryAdapter;
import com.jibo.ui.HomePageLayout;
import com.jibo.util.Logs;
import com.jibo.util.ThreadHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.common.net.s;

public class InteractSearch extends BaseActivity {

	private AutoCompleteTextView drugSearch;
	private SearchHistoryDao searchHistoryDao;
	private Button cancelBt;
	private ListView interatList;
	private HistoryAdapter historyAdapter;
	private Context context;
	private InteractAdapter dao;
	private List<DrugInteractEntity> list;
	private boolean isInputed = false;
	private ArrayList<String> historyList;
	private SimpleAdapter adapter;
	List<Map<String,String>> drugList1;
	List<Map<String,String>> drugList = new ArrayList<Map<String,String>>();
	private ImageButton img_home;
	private String localLanguage;
	private RelativeLayout head_layout;
	private boolean listClicked=false;
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.interat_search_layout);
		init();
	}

	private void init() {
		context = this;
		dao = new InteractAdapter(context,Integer.parseInt(Constant.dbVersion));
		localLanguage = Locale.getDefault().getLanguage();
		if("en".equals(localLanguage)){
			localLanguage = "en-US";
		}else if("zh".equals(localLanguage)){
			localLanguage = "zh-CN";
		}
		findViewById();		
		historyView();
		setListener();
	}

	private void findViewById() {
		head_layout = (RelativeLayout)findViewById(R.id.head_layout);
		drugSearch = (AutoCompleteTextView) findViewById(R.id.search_edit);
		drugSearch.requestFocus();
		cancelBt = (Button) findViewById(R.id.imgbtn_clear);	
		cancelBt.setText(R.string.cancel);
		img_home = (ImageButton)findViewById(R.id.imgbtn_home);
		searchHistoryDao = new SearchHistoryDao(context);
		interatList = (ListView)findViewById(R.id.interat_list);
	}

	private void setListener() {
		showDrugTips();
		cancelBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drugSearch.setText("");
				MobclickAgent.onEvent(getApplicationContext(),"interaction_btn_dialog_delete");
				uploadLoginLogNew("Interaction","interaction_btn_dialog_delete", "deleteSearchWords", null);
			}
		});
		drugSearch.addTextChangedListener(mTextWatcher);
//		drugSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				 showDrugTips();
//				 isInputed = true;
//			}
//		});
		interatList.setOnItemClickListener(new ItemClickListener());
		img_home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(context instanceof HomePageActivity)) {
					HomePageLayout.s_pageID = 0;
					Intent intent = new Intent(context, HomePageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			}
		});
	}
	
	private void showDrugTips() {
		new ThreadHandler() {
			@Override
			public void execute() {
				Map<String,String> mapLanguage = new HashMap<String, String>(); 
				mapLanguage.put("CultureInfo", localLanguage);		
                 list = dao.selectDrugNameAndPID(mapLanguage);
                 if(list!= null){
                	 for (DrugInteractEntity drug : list) {												// 循环遍历Person集合, 把每一个Person装入一个Map, Map再装入List
                		 Map<String, String> map = new HashMap<String, String>();
                		 map.put("id", drug.drugId);
                		 map.put("name", drug.drugName);
                		 drugList.add(map);
                	 }
                 }                 
			}
			@Override
			public void postexecute() {
				//暂时不显示
	//			adapter = new SimpleAdapter(context, drugList, R.layout.list_item_text, new String[]{"name"}, new int[]{R.id.ListText});
      //          interatList.setAdapter(adapter);
			}
		 };
	}
		TextWatcher mTextWatcher = new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(!TextUtils.isEmpty(s.toString())){
					final String str = s.toString();
					isInputed = true;
					Iterator it = drugList.iterator();
					drugList1 = new ArrayList<Map<String,String>>();
					while(it.hasNext()){
						Map<String,String> map =  (Map<String, String>) it.next();
						if(!TextUtils.isEmpty(map.get("name"))){
							if(map.get("name").toLowerCase().indexOf(s.toString().toLowerCase())>-1){
								drugList1.add(map);
							}
						}
					}
//					new Thread(){
//						public void run() {
			                getDrugNameByPinyin(str);
//			            }
//					}.start();
					adapter = new SimpleAdapter(context, drugList1, R.layout.list_item_text, new String[]{"name"}, new int[]{R.id.ListText});
					interatList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}else{
					isInputed = false;
					SearchHistoryAdapter historyAdapter = new SearchHistoryAdapter(
							context, historyList);
					interatList.setAdapter(historyAdapter);
				}
				
			}
		};
	 public void getDrugNameByPinyin(String str){
		 List<DrugInteractEntity> list = dao.selectDrugNameByPinyin(str,localLanguage);
		 Iterator it1 = list.iterator();
		 while(it1.hasNext()){
			 DrugInteractEntity drugInteractEntity =  (DrugInteractEntity) it1.next();
			 Map<String,String> map = new HashMap<String, String>();
			 map.put("id", drugInteractEntity.drugId);
    		 map.put("name", drugInteractEntity.drugName);
			 drugList1.add(map);
		 }
	 }
	public void historyView(){
		historyList = searchHistoryDao.selectSearchHistory(context,Constant.interation_history);
		SearchHistoryAdapter historyAdapter = new SearchHistoryAdapter(
				context, historyList);
		interatList.setAdapter(historyAdapter);
	}
	private class ItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			View v = (View) interatList.findViewWithTag("clicked");
			
			if(v==null&&!listClicked){	
				MobclickAgent.onEvent(getApplicationContext(),"interaction_search");
				uploadLoginLogNew("Interaction","interaction_search", "clickSearchResult", null);
				listClicked = true;
				view.setTag("clicked");
				if(isInputed){
					Intent intent = new Intent(context, InteractIndex.class);
					Bundle bundle = new Bundle();
					if(drugList1!=null){
						bundle.putString("drugId", drugList1.get(position).get("id"));
						bundle.putString("drugName", drugList1.get(position).get("name"));
					}else{
						bundle.putString("drugId", drugList.get(position).get("id"));
						bundle.putString("drugName", drugList.get(position).get("name"));
					}
					intent.putExtras(bundle);
					setResult(RESULT_OK,intent);
					finish();
				}else{
					Intent intent = new Intent(context, InteractIndex.class);
					Bundle bundle = new Bundle();
		    		bundle.putString("drugName", historyList.get(position));
					intent.putExtras(bundle);
					setResult(RESULT_OK,intent);
					finish();
				}	
			}
		}
	}
	/**
	 * @author Rafeal Piao
	 * @description 显示自动匹配
	 */
	private class ViewTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			SearchHistoryAdapter historyAdapter = new SearchHistoryAdapter(
					context, searchHistoryDao.selectSearchHistory(context,"1"));
			drugSearch.setAdapter(historyAdapter);
			drugSearch.showDropDown();
			return false;
		}
	}
}
