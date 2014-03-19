package com.jibo.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jibo.GBApplication;
import com.jibo.asynctask.uploadDataNew;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.data.EventListParser;
import com.jibo.data.GetEventSearchPaser;
import com.jibo.data.entity.RelatedBeans;
import com.jibo.dbhelper.EventAdapter;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.net.BaseResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.api.android.GBApp.R;

public class EventsActivity extends BaseSearchActivity implements
		OnItemClickListener, OnItemSelectedListener, OnScrollListener {

	private static final String TAG = "Events";
	private static final String COLUMN_TEXT_01 = "title";
	private static final String COLUMN_TEXT_02 = "place";
	private static final String COLUMN_TEXT_03 = "date";
	private static final String COLUMN_TEXT_04 = "id";
	static boolean addFlagOne = false;
	static boolean addFlagTwo = false;
	static boolean searching = false;
	static boolean updateFlag = false;
	static boolean webUpdateFlag = false;
	public boolean wFlag = true;
	int posFlag = -1;
	ArrayList<String> listArr;
	String dateTimeOne = "";
	String dateTimeTwo = "";
	static int dateTimeTwoFlag = 0;
	static int spinnerPosition = 0;
	// SharedPreferences preference=null;
	private static final int DATABASE_COUNT = 50;
	private ArrayList<HashMap<String, String>> listItem;
	private ListView mEventsList;
	static boolean BACKFLAG = false;
	SimpleAdapter simpleAdapter;
	static boolean[] arrayBl = new boolean[3];
	EventAdapter eventAdpt;
	public BaseResponseHandler baseHandler;
	public BaseResponseHandler basHdler;
	String[] events;
	String evnetsId = null;

	SharedPreferences settings = null;
	public static final String LOGIN_FLAG = "login_flag";
	private static String isType = "0";
	private Spinner spinner;
	private ArrayAdapter<String> adapters;
	private boolean isSearch = false;
	// private boolean isSearchFlag = false;
	private static final String TYPE = "search";
	private List<String> itemListId = new ArrayList<String>();
	private int pos = 0;
	private int dateNum = 0;
	Context context = null;
	private View footerView;
	private TextView tvTitle = null;
	private boolean isFlag = false;
	private boolean flag = true;
	public static int WEB_PAGE_COUNT = 1;
	boolean isMainPage = false; // prime
	String searchText;
	boolean inTitle, inRegion, inOrganizer, inCategory;
	private GBApplication app;
	private HistoryAdapter historyAdapter;
	private boolean[] mCheckedItemsEvent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.category_main);
		context = this;
		super.onCreate(savedInstanceState);
		app = (GBApplication) getApplication();
		mCheckedItemsEvent = new boolean[] { false, false, false, false };
		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");
		eventAdpt = new EventAdapter(this);
		baseHandler = new BaseResponseHandler(this);// event一般模式下获取列表数据的Handler
		basHdler = new BaseResponseHandler(this);// event搜索模式下获取列表数据的Handler

		settings = getSharedPreferences(LOGIN_FLAG, 0);
		mEventsList = (ListView) findViewById(R.id.lst_item);
		spinner = (Spinner) findViewById(R.id.sp_category);

		tvTitle = (TextView) findViewById(R.id.txt_header_title);
		tvTitle.setText(R.string.event);

		init();
		MobclickAgent.onError(this);
		mEventsList.setOnItemClickListener(this);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			searchText = extras.getString("search_text");
			isMainPage = extras.getBoolean(Constant.ISMAINPAGE, false); // prime
			mCheckedItemsEvent = extras.getBooleanArray("select_item");
			inRegion = mCheckedItemsEvent[1];
			inTitle = mCheckedItemsEvent[0];
			inOrganizer = mCheckedItemsEvent[2];
			inCategory = mCheckedItemsEvent[3];

		}
		if (searchText != null) {
			isSearch = true;
		}

		if (isSearch) {// 搜索的时候请求的webservice
			dateNum = 0;
			Properties propertyInfo = new Properties();
			propertyInfo.put(SoapRes.KEY_EVENT_KEYWORD, searchText);
			Log.e("searchText", searchText);
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_TITLE, inTitle);// KEY_EVENT_KEYWORD
			Log.e("inTitle", String.valueOf(inTitle));
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_REGION, inRegion);
			Log.e("inRegion", String.valueOf(inRegion));
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_ORGAN, inOrganizer);
			Log.e("inOrganizer", String.valueOf(inOrganizer));
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_CATEGORY, inCategory);
			Log.e("inCategory", String.valueOf(inCategory));
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_PAGESIZE, 10);
			Log.e("++dateNum", String.valueOf(++dateNum));
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_PAGNUM, ++dateNum);
			sendRequest(SoapRes.URLEvent, SoapRes.REQ_ID_EVENT_SEARCH,
					propertyInfo, basHdler);
		}else
		{
			events = getResources().getStringArray(R.array.events);
			adapters = new ArrayAdapter<String>(this, R.layout.gba_spinner_item,
					events);
			adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapters);
			spinner.setOnItemSelectedListener(this);
		}

		setPopupWindowType(Constant.MENU_TYPE_3);

		
		uploadLoginLogNew("Activity", "Events","create",null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		uploadLoginLogNew("Activity","Events","end",null);

	}

	@Override
	protected void treatMenuClick(int typeId) {
		if (typeId == Constant.FLAG_MY_JIBO) {
			Intent in = new Intent(this, MyFavoriteListActivity.class);// Util.myFavCategory
																		// =
																		// 1;//医学公式
			Constant.myFavCategory = 4;// Constant.myFavCategory=4代表event收藏夹
			startActivity(in);// 跳转到我的集博收藏夹
		} else
			super.treatMenuClick(typeId);
	}

	private ArrayList<HashMap<String, String>> addEventsItem(// 会议列表的显示
			ArrayList<RelatedBeans> eventList) {
		ArrayList<HashMap<String, String>> lisItem = new ArrayList<HashMap<String, String>>();
		listArr = new ArrayList<String>();

		String date;
		if (!isSearch) {
			if ((dateNum == 1) && isType.equals("0")) {
				if (updateFlag) {// !dateTimeOne.equals("") &&
					listItem.clear();
					itemListId.clear();
					updateFlag = false;

				}// 最新会议的第一页有更新
			} else if ((dateNum == 1) && isType.equals("1")) {
				if (updateFlag) {
					// !dateTimeOne.equals("") &&
					listItem.clear();
					itemListId.clear();
					Log.e("success", "itemListId.clear()");
					updateFlag = false;

				}// 往期会议的第一页有更新
			}
		}
		for (int i = 0; i < eventList.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(COLUMN_TEXT_01, eventList.get(i).getName());
			map.put(COLUMN_TEXT_02, eventList.get(i).getPlace());
//			if (eventList.get(i).getEventDate() != null
//					&& eventList.size() > 10) {
//				date = eventList.get(i).getEventDate().substring(0, 10);
//			} else {
//				date = eventList.get(i).getEventDate();
//			}
			
			String eventdt =null ;
			if(eventList.get(i).getEventDate() != null)
			{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				try {
					eventdt = sdf.format(sdf.parse(eventList.get(i).getEventDate()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			date=eventdt;

			map.put(COLUMN_TEXT_03, date);
			map.put(COLUMN_TEXT_04, eventList.get(i).getID());
			lisItem.add(map);
			listArr.add(eventList.get(i).getID());
		}
		Log.e("dateNum", String.valueOf(dateNum));

		return lisItem;
	}

	private void showListView(ListView list,
			ArrayList<HashMap<String, String>> item) {
		ArrayList<HashMap<String, String>> display;
		display = new ArrayList<HashMap<String, String>>();
		display = item;
		Log.e("display.size", String.valueOf(display.size()));
		listItem.addAll(display);
		itemListId.addAll(listArr);

		simpleAdapter.notifyDataSetChanged();

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	public void init() {// 初始化event列表
		listItem = new ArrayList<HashMap<String, String>>();
		isFlag = false;
		pos = 0;

		simpleAdapter = new SimpleAdapter(this, listItem,
				R.layout.list_item_vertical_text_text_text, new String[] {
						COLUMN_TEXT_01, COLUMN_TEXT_02, COLUMN_TEXT_03 },
				new int[] { R.id.ListText1, R.id.ListText2, R.id.ListText3 });
		mEventsList.setAdapter(simpleAdapter);
		mEventsList.setOnScrollListener(this);

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// Log.e()
		if (view.getLastVisiblePosition() == view.getCount() - 1 && !isSearch) {// 正常会议模式滚动获取列表数据

			Properties propertyInfo = new Properties();
			propertyInfo.put(SoapRes.KEY_EVENT_MAXDATE,
					settings.getString("versionDate", ""));
			propertyInfo.put(SoapRes.KEY_EVENT_TYPE, isType);
			propertyInfo.put(SoapRes.KEY_EVENT_PAGENUM, ++dateNum);
			Log.e("dateNum", String.valueOf(dateNum));
			sendRequest(SoapRes.URLEvent, SoapRes.REQ_ID_EVENT, propertyInfo,
					baseHandler);
		}
		if (view.getLastVisiblePosition() == view.getCount() - 1 && isSearch) {// event搜索模式下滚动获取列表数据
			Properties propertyInfo = new Properties();
			propertyInfo.put(SoapRes.KEY_EVENT_KEYWORD, searchText);
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_TITLE, inTitle);// KEY_EVENT_KEYWORD

			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_REGION, inRegion);
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_ORGAN, inOrganizer);
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_CATEGORY, inCategory);
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_PAGESIZE, 10);
			propertyInfo.put(SoapRes.KEY_EVENT_SEARCH_PAGNUM, ++dateNum);
			sendRequest(SoapRes.URLEvent, SoapRes.REQ_ID_EVENT_SEARCH,
					propertyInfo, basHdler);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (isSearch) {
				isSearch = false;
			}

			this.finish();
		}
		return false;
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		// TODO Auto-generated method stub
		if (o != null) {

			if (o instanceof EventListParser) {
				EventListParser data = (EventListParser) o;

				ArrayList<RelatedBeans> eL = data.list;
				if(dateNum==1)
				{
					if ( pos == 0)// 更新最新会议内建的10条数据
					{
						if (eL.size() > 0) {
							if (!eL.get(0)
									.getVersionDate()
									.equals(SharedPreferencesMgr
											.getEveVersionDate())) {
								SharedPreferencesMgr.setEveVersionDate(eL.get(0)
										.getVersionDate());
								updateFlag = true;
								eventAdpt.clearEvents(this);
								for (int i = 0; i < eL.size(); i++) {
									Log.e("123", "123");
									eventAdpt.insertEvents(eL.get(i).getID(), eL
											.get(i).getName(),
											eL.get(i).getPlace(), eL.get(i)
													.getEventDate(), eL.get(i)
													.getIntroduction(), eL.get(i)
													.getOrganizer(), eL.get(i)
													.getTel(), eL.get(i).getFax(),
											eL.get(i).getEmail(), eL.get(i)
													.getWebsite());

								}
								showListView(mEventsList,
										addEventsItem(eventAdpt.selectEvents()));

							}

						}

					} else if ( pos == 1)// 更新往期会议内建的10条数据
					{

						if (eL.size() > 0) {
							if (!eL.get(0)
									.getVersionDate()
									.equals(SharedPreferencesMgr
											.getEveVersionDate())) {
								SharedPreferencesMgr.setEveVersionDate(eL.get(0)
										.getVersionDate());
								updateFlag = true;
								eventAdpt.clearOldEvents(context);
								for (int i = 0; i < eL.size(); i++) {
									Log.e("123", "123");
									eventAdpt.insertOldEvents(eL.get(i).getID(), eL
											.get(i).getName(),
											eL.get(i).getPlace(), eL.get(i)
													.getEventDate(), eL.get(i)
													.getIntroduction(), eL.get(i)
													.getOrganizer(), eL.get(i)
													.getTel(), eL.get(i).getFax(),
											eL.get(i).getEmail(), eL.get(i)
													.getWebsite());
								}
								showListView(mEventsList,
										addEventsItem(eventAdpt.selectOldEvents()));

							}

						}
					} 
				}
				else {
					showListView(mEventsList, addEventsItem(data.list));
				}

			}
			if (o instanceof GetEventSearchPaser)// 获取event搜索中获取下来的数据
			{
				GetEventSearchPaser data = (GetEventSearchPaser) o;
				showListView(mEventsList, addEventsItem(data.al));
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		TextView v = (TextView)view	;
		Util.createSpaString(this, v, v.getText().toString());
		isType = String.valueOf(position);
		pos = position;
		dateNum = 0;
		listItem.clear();
		itemListId.clear();
		mEventsList.invalidate();
		if (!isSearch) {
			if (position == 0) {
				mEventsList.setOnScrollListener(this);
				ArrayList<RelatedBeans> alst = eventAdpt.selectEvents();
				Log.e("dateNum", String.valueOf(dateNum));
				if ((alst.size() > 0) && (++dateNum == 1)) {
					showListView(mEventsList, addEventsItem(alst));
					Log.e("dateNum1", String.valueOf(dateNum));
					Properties propertyInfo = new Properties();
					propertyInfo.put(SoapRes.KEY_EVENT_MAXDATE, "");
					propertyInfo.put(SoapRes.KEY_EVENT_TYPE, isType);
					propertyInfo.put(SoapRes.KEY_EVENT_PAGENUM, dateNum);
					sendRequest(SoapRes.URLEvent, SoapRes.REQ_ID_EVENT,
							propertyInfo, baseHandler);
				} 
				else {

					Properties propertyInfo = new Properties();
					propertyInfo.put(SoapRes.KEY_EVENT_MAXDATE,
							settings.getString("versionDate", ""));
					propertyInfo.put(SoapRes.KEY_EVENT_TYPE, isType);
					propertyInfo.put(SoapRes.KEY_EVENT_PAGENUM, ++dateNum);
					sendRequest(SoapRes.URLEvent, SoapRes.REQ_ID_EVENT,
							propertyInfo, baseHandler);
				}

			}else if (position == 1) {
				ArrayList<RelatedBeans> alst = eventAdpt.selectOldEvents();
				Log.e("dateNum", String.valueOf(dateNum));
				mEventsList.setOnScrollListener(this);
				if ((alst.size() > 0) && (++dateNum == 1))// 查看第一页数据是否有更新
				{
					showListView(mEventsList, addEventsItem(alst));
					Log.e("dateNum2", String.valueOf(dateNum));
					Properties propertyInfo = new Properties();
					propertyInfo.put(SoapRes.KEY_EVENT_MAXDATE, "");
					propertyInfo.put(SoapRes.KEY_EVENT_TYPE, isType);
					propertyInfo.put(SoapRes.KEY_EVENT_PAGENUM, dateNum);
					sendRequest(SoapRes.URLEvent, SoapRes.REQ_ID_EVENT,
							propertyInfo, baseHandler);

				}else {

					Properties propertyInfo = new Properties();
					propertyInfo.put(SoapRes.KEY_EVENT_MAXDATE,
							settings.getString("versionDate", ""));
					propertyInfo.put(SoapRes.KEY_EVENT_TYPE, isType);
					propertyInfo.put(SoapRes.KEY_EVENT_PAGENUM, ++dateNum);
					sendRequest(SoapRes.URLEvent, SoapRes.REQ_ID_EVENT,
							propertyInfo, baseHandler);
				} 

			}

		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Intent intent;
		intent = new Intent(this, RelatedEventsActivity.class);

		// if(BACKFLAG)
		// {
		// isSearch=false;
		// }
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		// if (isSearch) {
		//
		// } else {
		if (position < itemListId.size() && itemListId.get(position) != null) {
			// TODO
			System.out.println("id: " + itemListId.get(position) + "   ****   "
					+ "position: " + posFlag);
			TextView txt = (TextView) v.findViewById(R.id.ListText1);
			String title = txt.getText().toString();
//			int eventId = Integer.parseInt(itemListId.get(position));
			String eventId = itemListId.get(position);
			int colID = -1;
			for (Entry en : app.getPdaColumnMap().entrySet()) {
				if (en.getValue().toString().equals(getString(R.string.event))) {
					colID = (Integer) en.getKey();
				}
			}
			historyAdapter.storeViewHistory(app.getLogin().getGbUserName(),
					Integer.parseInt(itemListId.get(position)), colID, -1, title);
			intent.putExtra(Constant.ID, eventId);
			MobclickAgent.onEvent(context, "EventId", itemListId.get(position),
					1);// "SimpleButtonclick");
			evnetsId = itemListId.get(position);
			
			uploadLoginLogNew("Events", evnetsId, "EventId", null);
			intent.putExtra("position", posFlag);
			startActivity(intent);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {//eventAdpt
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	public void onStop() {
		super.onStop();
		eventAdpt.closeHelp(); 
	}

}
