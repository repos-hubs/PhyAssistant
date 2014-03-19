package com.jibo.activity;

import java.util.ArrayList;

import com.api.android.GBApp.R;
import com.jibo.adapter.FavoritListAdapter;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
//import com.jibo.data.entity.PromotionBrandInfoEntity;
//import com.jibo.dbhelper.DrugAdapter;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.entity.FavoritDrugEntity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyFavoriteListActivity extends BaseSearchActivity implements
		OnItemClickListener {
	private ListView mList;
	private ArrayAdapter<String> listAdapter;
	static final int DRUG_CATEGORY = 0;
	static final int TOOLACT_CATEGORY = 1;
	static final int NEWS_CATEGORY = 2;
	static final int ARTICLE_CATEGORY = 3;
	static final int EVENT_CATEGORY = 4;
	static final int DRUGALERT_CATEGORY = 5;
	// private DrugAdapter drugAdapter;
	FavoritDataAdapter favadpter = null;
	TextView tvtitle = null;
	Context context;
	SharedPreferences setting;
	public String userName;
	public static final String LOGIN_FLAG = "login_flag";
	public static final String USER_NAME = "USER_NAME";
	ArrayList<String> arrMain = new ArrayList<String>();
	ArrayList<FavoritDrugEntity> arrFavor = new ArrayList<FavoritDrugEntity>();
	ArrayList<String> arrFavorEvent = new ArrayList<String>();
	ArrayList<String> arrFavorNews = new ArrayList<String>();
	ArrayList<String> arrFavorResearch = new ArrayList<String>();
	ArrayList<String> arrFavorTabcalc = new ArrayList<String>();
	ArrayList<String> arrFavorTabTumor = new ArrayList<String>();
	ArrayList<String> arrFavorDrugAlerts = new ArrayList<String>();

	ArrayList<String> arrIdMain = new ArrayList<String>();
	ArrayList<String> arrFavorDrugId = new ArrayList<String>();
	ArrayList<String> arrFavorEventId = new ArrayList<String>();
	ArrayList<String> arrFavorNewsId = new ArrayList<String>();
	ArrayList<String> arrFavorResearchId = new ArrayList<String>();
	ArrayList<String> arrFavorTabcalcClass = new ArrayList<String>();
	ArrayList<String> arrFavorTabTumorRank = new ArrayList<String>();
	ArrayList<String> arrFavorDrugAlertTypeIds = new ArrayList<String>();

	FavoritListAdapter drugListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.myfavorite_list);
		super.onCreate(savedInstanceState);

		context = this;
		mList = (ListView) findViewById(R.id.lst_item);
		// Display display = getWindowManager().getDefaultDisplay();
		favadpter = new FavoritDataAdapter(this);
		tvtitle = (TextView) findViewById(R.id.txt_header_title);
		mList.setOnItemClickListener(this);
		setting = getSharedPreferences(LOGIN_FLAG, 0);
		userName = SharedPreferencesMgr.getUserName();
		// drugAdapter = new DrugAdapter(this, 1);

		if (Constant.myFavCategory == DRUG_CATEGORY) {
			arrFavor = favadpter.selectTabDrugRef(context, userName);
			tvtitle.setText(R.string.col_drug);
			// 药品的收藏方式不同
			drugListAdapter = new FavoritListAdapter(this, daoSession,arrFavor);
			mList.setAdapter(drugListAdapter);
			return;
		} else if (Constant.myFavCategory == TOOLACT_CATEGORY) {
			tvtitle.setText(R.string.col_tools);
			arrFavorTabcalc = favadpter.selectTabCalcc(context, userName);
			arrMain = arrFavorTabcalc;
			arrIdMain = arrFavorTabcalc;

		} else if (Constant.myFavCategory == NEWS_CATEGORY) {
			arrFavorNews = favadpter.selectNews(userName);
			arrMain = arrFavorNews;
			arrIdMain = HistoryFavoriteActivity.arrFavorNewsId;
			Log.e("arrMain", "arrMain");
			tvtitle.setText(R.string.col_news);
		} else if (Constant.myFavCategory == ARTICLE_CATEGORY) {
			arrFavorResearch = favadpter.selectResearchc(context, userName);
			arrMain = arrFavorResearch;
			arrIdMain = HistoryFavoriteActivity.arrFavorResearchId;
			tvtitle.setText(R.string.col_research);
		} else if (Constant.myFavCategory == EVENT_CATEGORY) {
			arrFavorEvent = favadpter.selectEventC(context, userName);
			arrMain = arrFavorEvent;
			arrIdMain = HistoryFavoriteActivity.arrFavorEventId;
			tvtitle.setText(R.string.col_events);
		} else if (Constant.myFavCategory == DRUGALERT_CATEGORY)// 不良反应通告 收藏
		{
			arrFavorDrugAlerts = favadpter.selectHistoryDrugAlert(context,
					userName);
			arrMain = arrFavorDrugAlerts;
			arrIdMain = HistoryFavoriteActivity.arrFavorDrugAlertTypeIds;
			tvtitle.setText(getString(R.string.drugalert));
		}

		listAdapter = new ArrayAdapter<String>(this, R.layout.list_item_text,
				arrMain);
		mList.setAdapter((ListAdapter) listAdapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (Constant.myFavCategory == DRUG_CATEGORY) {
			Intent intent = new Intent(this, NewDrugReferenceActivity.class);

			FavoritDrugEntity item = drugListAdapter.getItem(position);

			int mod = item.mode;
			intent.putExtra("mode", mod);
			if (mod == NewDrugReferenceActivity.MODE_BRAND) {// 厂商说明书模式进入
				intent.putExtra("brandId", item.drugId);
			} else if (mod == NewDrugReferenceActivity.MODE_NORMAL) {// 普通模式进入
				intent.putExtra("drugId", item.drugId);
				if (!TextUtils.isEmpty(item.adminRouteId))
					intent.putExtra("adminRouteId", item.adminRouteId);
			}
			startActivity(intent);
		} else if (Constant.myFavCategory == TOOLACT_CATEGORY) {
			Intent intent = new Intent(this, TabCalcInfoActivity2.class);
			intent.putExtra("name", arrIdMain.get(position));
			startActivity(intent);
		} else if (Constant.myFavCategory == NEWS_CATEGORY) {
			Intent intent = new Intent(this, RelatedNewsActivity.class);
			intent.putExtra("id", arrIdMain.get(position));
			startActivity(intent);
		} else if (Constant.myFavCategory == ARTICLE_CATEGORY) {
			Intent intent = new Intent(this, RelatedArticlesActivity.class);
			intent.putExtra("id", arrIdMain.get(position).split(",")[0]);
			intent.putExtra(CategoryArticlesActivity.TA_ID, "");
			intent.putExtra("articles_type", "");
			startActivity(intent);
		} else if (Constant.myFavCategory == EVENT_CATEGORY) {
			Intent intent = new Intent(this, RelatedEventsActivity.class);
			intent.putExtra("id", arrIdMain.get(position));
			Log.e("id", arrIdMain.get(position));
			startActivity(intent);
		} else if (Constant.myFavCategory == DRUGALERT_CATEGORY) {

			Intent intent = new Intent(this, DrugAlertsDetailActivity.class);
			// Log.e("arrFavorDrugAlertTypeIds",arrFavorDrugAlertTypeIds.get(arg3));

			intent.putExtra("typeID", arrIdMain.get(position));
			Log.e("typeId", arrIdMain.get(position));
			startActivity(intent);
			// final String typeId = arrIdMain.get(position);
			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// Message msg = new Message();
			// try {
			// DrugAlertsDetailActivity.dataPreparation(typeId);
			// } catch (Exception e) {
			// e.printStackTrace();
			// msg.what = 0;
			// }
			// msg.what = 1;
			// drugAlertHandler.sendMessage(msg);
			// }
			// }).start();

		}

	}

	// private Handler drugAlertHandler = new Handler(){
	//
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// MyFavoriteListActivity.this
	// .removeDialog(Util.DIALOG_WAITING_FOR_DATA);
	// switch (msg.what) {
	// case 0:
	// MyFavoriteListActivity.this.showDialog(Util.DIALOG_ERROR_PROMPT);
	// break;
	// case 1:
	// Intent intent = new Intent(MyFavoriteListActivity.this,
	// DrugAlertsDetail.class);
	// startActivity(intent);
	// break;
	// }
	// }
	//
	// };

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
		}
		return true;
	}
}
