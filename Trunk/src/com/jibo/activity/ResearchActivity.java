package com.jibo.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jibo.asynctask.uploadDataNew;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.dao.DBFactory;
import com.jibo.dao.DBHelper;
import com.jibo.dao.DaoManager;
import com.jibo.dao.DaoSession;
import com.jibo.data.ResearchCheckParser;
import com.jibo.data.ResearchGetArtParser;
import com.jibo.data.ResearchParser;
import com.jibo.data.entity.ResearchCategoryBean;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.dbhelper.ResearchAdapter;
import com.jibo.entity.DptRelation;
import com.jibo.net.BaseResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.api.android.GBApp.R;

public class ResearchActivity extends BaseSearchActivity implements
		OnClickListener, OnItemClickListener, OnItemSelectedListener,
		OnScrollListener {

	private static final String TAG = "Research";
	/* map key */
	private static final String COLUMN_TEXT_01 = "title";
	private static final String COLUMN_TEXT_02 = "number";
	// private AutoCompleteTextView mSearchEdit;
	private ListView mSpecialtyList;
	private TextView tvTitle = null;
	String reId;
	private Spinner spinner;
	Context context = null;
	int len = 0;
	private EditText searchKey;
	private ImageButton pubmedsearchButton;
	public BaseResponseHandler baseHandler;
	public BaseResponseHandler getArtHandler;
	ArrayList<ResearchCategoryBean> alRes = new ArrayList<ResearchCategoryBean>();
	// prime 2011-4-15
	private ArrayAdapter<String> adapter;
	private static final String CHECKITEM = "GBACHECKITEM23";
	public static String KW_ID_STR = null;
	public static String KW_COUNT_STR = null;
	ResearchAdapter researchAdapt = null;
	boolean isMainPage = false;
	String kw_Id = null;
	String taid = null;
	private Toast toast = null;// 用于提示划屏显示到了最后一条

	/** db工具 */
	private DBHelper helper;
	private DaoSession daoSession;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.research);
		super.onCreate(savedInstanceState);
		context = this;
		openResources();
		/* 初始化toast add by Terry */
		toast = Toast.makeText(this, "已到达最后一条", Toast.LENGTH_SHORT);
		baseHandler = new BaseResponseHandler(this);
		getArtHandler = new BaseResponseHandler(this);
		researchAdapt = new ResearchAdapter(this);
		mSpecialtyList = (ListView) findViewById(R.id.lst_item);
		mSpecialtyList.setOnItemClickListener(this);
		mSpecialtyList.setOnScrollListener(this);
		researchId = getResources().getStringArray(R.array.researchid);
		researchCat = getResources().getStringArray(R.array.researchCat);
		spinner = (Spinner) findViewById(R.id.sp_category);
		pubmedsearchButton = (ImageButton) findViewById(R.id.pubmedsearch);
		pubmedsearchButton.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.txt_header_title);
		tvTitle.setText(R.string.research);
		searchKey = (EditText) findViewById(R.id.pubmedsearchkey);
		showText();
		MobclickAgent.onError(this);
		spinner.setOnItemSelectedListener((OnItemSelectedListener) this);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			isMainPage = extras.getBoolean(Constant.ISMAINPAGE, false);
		}

		adapter = new ArrayAdapter<String>(context, R.layout.gba_spinner_item,
				researchCat);// DrugData.ResearchTotalCategory.value);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		String departMent = SharedPreferencesMgr.getDept();
		if (!TextUtils.isEmpty(departMent)) {
			DptRelation dptObj = daoSession.getDptRelationDao()
					.load(departMent);
			if (null != dptObj) {
				taid = dptObj.getArticleCategoryId();
			}
			if (!TextUtils.isEmpty(taid)) {
				for (int i = 0; i < researchId.length; i++) {
					if (researchId[i].equals(taid)) {
						spinner.setSelection(i);
					}
				}
			}
		}

		spinner.setOnItemSelectedListener(this);
		setPopupWindowType(Constant.MENU_TYPE_3);
//		 uploadLoginLog("Activity",
//		 "Research","create", null, true);

		
	}

	/***
	 * 关闭db资源
	 */
	private void releaseResources() {
		try {
			helper.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		helper = null;
		daoSession = null;
	}

	/***
	 * 开启db资源
	 */
	public void openResources() {
		helper = DBFactory.getSDCardDbHelper(context);
		DaoManager daoMaster = new DaoManager(helper);
		daoSession = daoMaster.newSession();
	}

	@Override
	protected void treatMenuClick(int typeId) {
		if (typeId == Constant.FLAG_MY_JIBO) {
			Intent in = new Intent(this, MyFavoriteListActivity.class);// Util.myFavCategory
																		// =
																		// 1;//医学公式
			Constant.myFavCategory = 3;
			startActivity(in);
		} else
			super.treatMenuClick(typeId);
	}

	private void showText() {
		String text;
		text = getString(R.string.articles_by_specialty)
				+ getString(R.string.comma) + getString(R.string.space)
				+ getString(R.string.according);
	}

	private ArrayList<HashMap<String, String>> addSpecialtyItem(
			ArrayList<ResearchCategoryBean> list) {
		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		int length = list.size();
		Log.e(TAG, "articleCategories length : " + length);
		for (int i = 0; i < length; i++) {
			HashMap<String, String> map1 = new HashMap<String, String>();
			map1.put(COLUMN_TEXT_01, list.get(i).getKw());
			map1.put(COLUMN_TEXT_02, list.get(i).getKwCount());
			listItem.add(map1);
		}
		return listItem;
	}// 加载列表数据到页面上

	private void showListView(ListView list,
			ArrayList<HashMap<String, String>> item, int resid) {
		// 显示列表数据
		ArrayList<HashMap<String, String>> display;
		display = new ArrayList<HashMap<String, String>>();
		display = item;

		SimpleAdapter adapter = new SimpleAdapter(this, display, resid,
				new String[] { COLUMN_TEXT_01, COLUMN_TEXT_02 }, new int[] {
						R.id.ListText1, R.id.ListText2 });

		list.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!v.isEnabled()) {
			Log.e(TAG, "on click error, button is disabled");
			return;
		}

		Intent intent;
		switch (v.getId()) {
		case R.id.pubmedsearch:// pubmed搜索
			String key = "";
			if (searchKey.getText() != null) {
				key = searchKey.getText().toString();
			}
			intent = new Intent(this, WebViewActivity.class);
			intent.putExtra("searchkey", key);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		if (alRes.size() > 0) {
			Intent inten;
			inten = new Intent(ResearchActivity.this,
					CategoryArticlesActivity.class);
			inten.putExtra(CategoryArticlesActivity.CATEGORY_ARTICLES_TYPE,
					CategoryArticlesActivity.TYPE_ARTICLE_CATEGORIES);
			inten.putExtra(CategoryArticlesActivity.TA_ID, alRes.get(position)
					.getTa_id());
			Log.e("CategoryArticlesActivity.TA_ID", alRes.get(position)
					.getTa_id());
			Log.e("CategoryArticlesActivity.KW_ID", alRes.get(position)
					.getKw_id());
			MobclickAgent.onEvent(context, "ResearchKW_ID", alRes.get(position)
					.getKw_id(), 1);
			kw_Id = alRes.get(position).getKw_id();
			uploadLoginLogNew("Research", kw_Id, "ResearchKW_ID", null);

			inten.putExtra(CategoryArticlesActivity.KW_ID, alRes.get(position)
					.getKw_id());
			inten.putExtra(CategoryArticlesActivity.KW, alRes.get(position)
					.getKw());
			inten.putExtra(Constant.ISMAINPAGE, true);
			startActivity(inten);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
	}

	private static boolean isMoveUp = true;
	private int mMotionY = 0;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				final int y = (int) ev.getY();

				int deltaY = 0;
				int action = ev.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN: {
					mMotionY = y;
					break;
				}
				case MotionEvent.ACTION_MOVE: {
					deltaY = y - mMotionY; // deltaY的正负就表示往下或往上
					if (deltaY > 0) {
						isMoveUp = false;
					} else if (deltaY < 0) {
						isMoveUp = true;
					}
					break;
				}
				}
				return false;
			}
		});
		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
			/* 当前列表滑动到列表最后一列 add by Terry */
			if ((view.getLastVisiblePosition() == view.getCount() - 1)
					&& isMoveUp) {
				toast.cancel();
				toast.show();
			}
		}
	}

	private String[] researchId;
	private String[] researchCat;

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		TextView v = (TextView) view;
		Util.createSpaString(this, v, v.getText().toString());
		reId = researchId[position];
		MobclickAgent.onEvent(context, researchId[position],
				"ResearchMainCategory", 1);// "SimpleButtonclick");

		uploadLoginLogNew("Research", reId, "ResearchMainCategory", null);
		if (researchId[position] != null)// (DrugData.ResearchTotalCategory.id[position]!=null)
		{

			alRes = researchAdapt.selectTACount(context, researchId[position]);
			Log.e("alRes", String.valueOf(alRes.size()));
			if (alRes.size() > 0) {
				// showListView(mSpecialtyList, addSpecialtyItem(alRes),
				// R.layout.list_item_text_text_icon);
				Properties propertyInfo = new Properties();
				String kwIds = getKwIDs(alRes);
				String kwCounts = getKwCounts(alRes);
				propertyInfo.put(SoapRes.KEY_RESEARCH_TA_ID,
						researchId[position]);
				propertyInfo.put(SoapRes.KEY_RESEARCH_KW_ID, kwIds);
				propertyInfo.put(SoapRes.KEY_RESEARCH_KW_COUNT, kwCounts);
				sendRequest(SoapRes.URLResearch, SoapRes.REQ_ID_CHECK_TA,
						propertyInfo, baseHandler);

			} else {// 获取医学研究分类列表
				Properties propertyInfo = new Properties();
				propertyInfo.put(SoapRes.KEY_RESEARCH_TAID,
						researchId[position]);
				sendRequest(SoapRes.URLResearch, SoapRes.REQ_ID_RESEARCH_TA,
						propertyInfo, baseHandler);
			}
		}

	}

	/**
	 * 检查更新时拼接kwid字符串
	 * 
	 * @param alRes
	 * @return
	 */
	private String getKwIDs(ArrayList<ResearchCategoryBean> alRes) {
		StringBuilder str = null;
		for (int i = 0; i < alRes.size(); i++) {
			if (str == null) {
				str = new StringBuilder();
				str.append(alRes.get(i).getKw_id());
			} else {
				str.append("," + alRes.get(i).getKw_id());
			}
		}
		return str.toString();
	}

	/**
	 * 检查更新时拼接kwcount字符串
	 * 
	 * @param alRes
	 * @return
	 */
	private String getKwCounts(ArrayList<ResearchCategoryBean> alRes) {
		StringBuilder str = null;
		for (int i = 0; i < alRes.size(); i++) {
			if (str == null) {
				str = new StringBuilder();
				str.append(alRes.get(i).getKwCount());
			} else {
				str.append("," + alRes.get(i).getKwCount());
			}
		}
		return str.toString();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// uploadLoginLog("Activity",
		// "Research","end", null, true);

		uploadLoginLogNew("Activity", "Research", "end", null);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		// TODO Auto-generated method stub
		if (o != null) {

			if (o instanceof ResearchParser) {// 获取医学研究的列表
				ResearchParser data = (ResearchParser) o;
				Log.e("size34343", String.valueOf(data.list.size()) + "232");
				alRes = data.list;
				showListView(mSpecialtyList, addSpecialtyItem(data.list),
						R.layout.list_item_text_text_icon);
				// mSpecialtyList.removeFooterView(footerView);
				researchAdapt.insertKWID(context, alRes);
				for (int i = 0; i < data.list.size(); i++) {
					ResearchCategoryBean researchBean = new ResearchCategoryBean();
					researchBean.setKw_id(data.list.get(i).getKw_id());
					researchBean.setKwCount(data.list.get(i).getKwCount());
					researchBean.setTa_id(data.list.get(i).getTa_id());
					researchBean.setKw(data.list.get(i).getKw());

				}
			}

			if (o instanceof ResearchCheckParser) {
				ResearchCheckParser data = (ResearchCheckParser) o;
				Log.e("size34343", String.valueOf(data.list.size()) + "232");
				if (data.list.size() == 0) {
					showListView(mSpecialtyList, addSpecialtyItem(alRes),
							R.layout.list_item_text_text_icon);
					return;
				}
				alRes = data.list;
				showListView(mSpecialtyList, addSpecialtyItem(data.list),
						R.layout.list_item_text_text_icon);
				// mSpecialtyList.removeFooterView(footerView);
				researchAdapt.insertKWID(context, alRes);
				for (int i = 0; i < data.list.size(); i++) {
					ResearchCategoryBean researchBean = new ResearchCategoryBean();
					researchBean.setKw_id(data.list.get(i).getKw_id());
					researchBean.setKwCount(data.list.get(i).getKwCount());
					researchBean.setTa_id(data.list.get(i).getTa_id());
					researchBean.setKw(data.list.get(i).getKw());

				}
			}

			if (o instanceof ResearchGetArtParser) {
				ResearchGetArtParser data = (ResearchGetArtParser) o;
			}
		}
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
