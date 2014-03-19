package com.jibo.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.app.DetailsData;
import com.jibo.app.news.NewsDetailActivity;
import com.jibo.app.research.PaperDetailActivity;
import com.jibo.base.src.EntityObj;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.NewSyncFavPaser;
import com.jibo.data.SyncHistoryPaser;
import com.jibo.data.entity.NewFavItemEntity;
import com.jibo.data.entity.SyncHistoryEntity;
import com.jibo.dbhelper.DrugAlertSQLAdapter;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.entity.FavoritDrugEntity;
import com.jibo.net.BaseResponseHandler;
import com.jibo.ui.ViewHistory;

public class HistoryFavoriteActivity extends BaseSearchActivity implements
		OnClickListener {

	private final int HistoryId = 0x12345567;
	private final int FavoriteId = 0x12345567 + 1;
	private final int imgHistoryId = 0x12345567 + 2;
	private final int imgFavoriteId = 0x12345567 + 3;
	private final int txtHistoryId = 0x12345567 + 4;
	private final int txtFavoriteId = 0x12345567 + 5;
	private final int history = 0x123;
	private final int favorite = 0x123 + 1;
	private int cur_flag = history;

	public static ArrayList<String> arrFavorDrugId = new ArrayList<String>();// 获取收藏的药品的id列表，我的集博中也用到这个静态列表
	public static ArrayList<String> arrFavorEventId = new ArrayList<String>();// 获取收藏的会议的id列表，我的集博中也用到这个静态列表
	public static ArrayList<String> arrFavorNewsId = new ArrayList<String>();// 获取收藏的新闻的id列表，我的集博中也用到这个静态列表
	public static ArrayList<String> arrFavorResearchId = new ArrayList<String>();// 获取收藏的医学研究的id列表，我的集博中也用到这个静态列表
	public static ArrayList<String> arrFavorTabcalcClass = new ArrayList<String>();// 获取收藏的医学公式的id列表，我的集博中也用到这个静态列表
	public static ArrayList<String> arrFavorTabTumorRank = new ArrayList<String>();// 获取收藏的肿瘤的id列表，我的集博中也用到这个静态列表
	public static ArrayList<String> arrFavorTabHeartPos = new ArrayList<String>();// 获取收藏的心电图的id列表，我的集博中也用到这个静态列表
	public static ArrayList<String> arrFavorDrugAlertTypeIds = new ArrayList<String>();// 获取收藏的用药信息的id列表，我的集博中也用到这个静态列表

	static boolean fav = false;
	ArrayList<String> arrFavorEvent = new ArrayList<String>();// 获取医学会议列表的标题
	ArrayList<String> arrFavorNews = new ArrayList<String>();// 获取新闻列表的标题
	ArrayList<String> arrFavorResearch = new ArrayList<String>();// 获取医学研究的标题
	ArrayList<String> arrFavorTabcalc = new ArrayList<String>();// 获取医学公式的标题
	ArrayList<String> arrFavorTabTumor = new ArrayList<String>();// 获取肿瘤的标题
	ArrayList<String> arrFavorTabHeart = new ArrayList<String>();// 获取心电图的标题
	ArrayList<String> arrFavorDrugAlerts = new ArrayList<String>();// 获取用药的标题
	String[] arrFovEvent = null;
	String[] arrFovNews = null;
	String[] arrFovResearch = null;
	String[] arrFovTabcalc = null;
	String[] arrFovTabTumor = null;
	String[] arrFovDurgAlert = null;
	private FrameLayout flt;
	private ImageView imgHistory;
	private ImageView imgFavorite;
	private TextView txtFavorite;
	private TextView txtHistory;
	private TextView tvt;
	private ViewHistory view;
	private int screenWidth;
	private GBApplication app;
	FavoritDataAdapter fData = null;
	ExpandableListView elist;
	private LayoutInflater inflater;
	ExpandableListAdapter adapter = null;
	private HistoryAdapter historyAdapter;
	private BaseResponseHandler baseHandler;
	private BaseResponseHandler baseHand;
	Context context = null;
	private FavoritDataAdapter myFavoriteAdapter;
	private DrugAlertSQLAdapter dbAdapter;
	private ArrayList<FavoritDrugEntity> arrFavor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.history_favorite);
		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");
		super.onCreate(savedInstanceState);
		fData = new FavoritDataAdapter(this);
		baseHand = new BaseResponseHandler(this);
		myFavoriteAdapter = new FavoritDataAdapter(this);
		dbAdapter = new DrugAlertSQLAdapter(this);
		inits();
		context = this;
		if (fav) {
			cur_flag = favorite;
			flt.removeAllViews();
			tvt.setText(R.string.my_favorite);
			flt.addView(getfavView());
			fav = false;
		}
		
		uploadLoginLogNew("Activity", "HistoryAndFavorite", "create", null);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		uploadLoginLogNew("Activity", "HistoryAndFavorite", "end", null);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void inits() {
		app = (GBApplication) getApplication();
		baseHandler = new BaseResponseHandler(this);
		screenWidth = (int) app.getDeviceInfo().getScreenWidth();
		flt = (FrameLayout) findViewById(R.id.tabcontent);
		tvt = (TextView) findViewById(R.id.txt_header_title);
		tvt.setText(getString(R.string.my_history));
		view = new ViewHistory(this);
		// addToTabHost(this.getResources().getString(R.string.his),
		// R.drawable.view_history, 0, HistoryId);
		// addToTabHost(this.getResources().getString(R.string.fav),
		// R.drawable.view_favorite, 1, FavoriteId);
		createItem(this.getResources().getString(R.string.his),
				R.drawable.view_history, 0, HistoryId);
		createItem(this.getResources().getString(R.string.fav),
				R.drawable.view_favorite, 1, FavoriteId);
		imgHistory = (ImageView) this.findViewById(imgHistoryId);
		imgFavorite = (ImageView) this.findViewById(imgFavoriteId);
		txtFavorite = (TextView) this.findViewById(txtFavoriteId);
		txtHistory = (TextView) this.findViewById(txtHistoryId);
		flt.addView(view);
		flt.addView(view.getView(), new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		setPopupWindowType(Constant.MENU_TYPE_5);
	}

	/**
	 * @description 根据屏幕创建Tab
	 * @param String
	 *            name: 显示的字体
	 * @param int image: 显示图片id
	 * @param int number: Tab条目数量
	 * @param int id: 该条目布局id
	 * @return void
	 */
	private void createItem(String string, int image, int i, int id) {
		RelativeLayout rl = null;
		switch (i) {
		case 0:
			rl = (RelativeLayout) findViewById(R.id.rlt_history);
			break;
		case 1:
			rl = (RelativeLayout) findViewById(R.id.rlt_favorite);
			break;
		}

		rl.setId(id);
		rl.setOnClickListener(this);
		rl.addView(composeLayout(string, image, id),
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
	}

	/**
	 * @description 更新图标，在右上角显示数字
	 * @param String
	 *            name: 现实的名称
	 * @param int image: 图片id
	 * @param int rltId: 布局id
	 * @return View 条目布局
	 */
	private View composeLayout(String name, int image, int rltId) {
		LinearLayout layout = new LinearLayout(this);
		layout.setGravity(Gravity.CENTER);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		ImageView iv = new ImageView(this);
		TextView tv = new TextView(this);
		switch (rltId) {
		case HistoryId:
			iv.setId(imgHistoryId);
			tv.setId(txtHistoryId);
			iv.setBackgroundResource(R.drawable.view_history);
			tv.setTextColor(Color.BLACK);
			break;
		case FavoriteId:
			iv.setId(imgFavoriteId);
			tv.setId(txtFavoriteId);
			iv.setBackgroundResource(R.drawable.view_favorite_hover);
			tv.setTextColor(Color.BLACK);
			break;
		}
		layout.addView(iv, new LinearLayout.LayoutParams(screenWidth / 6,
				screenWidth / 7));

		tv.setGravity(Gravity.CENTER);
		tv.setSingleLine(true);
		tv.setPadding(10, 0, 0, 0);
		tv.setTextSize(18);
		tv.setText(name);
		tv.setTextColor(Color.BLACK);
		layout.addView(tv, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		return layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case HistoryId:
			cur_flag = history;
			flt.removeAllViews();
			tvt.setText(getString(R.string.my_history));
			imgHistory.setBackgroundResource(R.drawable.view_history);
			imgFavorite.setBackgroundResource(R.drawable.view_favorite_hover);
			txtHistory.setTextColor(Color.BLACK);
			txtFavorite.setTextColor(Color.BLACK);
			flt.addView(view.getView(), new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			break;
		case FavoriteId:
			cur_flag = favorite;
			flt.removeAllViews();
			tvt.setText(R.string.my_favorite);
			flt.addView(getfavView());
			break;
		}
	}

	public View getfavView() {
		inflater = LayoutInflater.from(this);
		View myView = inflater.inflate(R.layout.favorite, null);
		// handler=createHandler();
		elist = (ExpandableListView) myView.findViewById(R.id.ex_list);
		elist.setGroupIndicator(this.getResources().getDrawable(
				R.drawable.el_selector));
		elist.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				Intent intent;
				if (arg2 == 0) {
					// DrugInfoEntity en =
					// drugAdapter.FillDrugsInfoDataByLocalDatabase(arrFavorDrugId.get(arg3));
					// intent = new Intent(HistoryFavoriteActivity.this,
					// DrugReferenceActivity.class);
					// intent.putExtra("MAINPAGE", true);
					// intent.putExtra("drugInfo", en);
					// startActivity(intent);
					// DrugInfoEntity en =
					// drugAdapter.FillDrugsInfoDataByLocalDatabase(arrFavorDrugId.get(arg3));

					// arrFavorDrugId.get(arg3).length()>2&&arrFavorDrugId.get(arg3).substring(0,
					// 1).equals("2")
					intent = new Intent(HistoryFavoriteActivity.this,
							NewDrugReferenceActivity.class);

					FavoritDrugEntity item = arrFavor.get(arg3);

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
				}
				if (arg2 == 1) {

					intent = new Intent(HistoryFavoriteActivity.this,
							TabCalcInfoActivity2.class);
					String name = arrFovTabcalc[arg3];
					intent.putExtra("name", name);
					intent.setClass(context, TabCalcInfoActivity2.class);
					startActivity(intent);

				}
				if (arg2 == 2) {
					intent = new Intent(HistoryFavoriteActivity.this,
							NewsDetailActivity.class);
					intent.putExtra("id", arrFavorNewsId.get(arg3));
					convert(arrFavorNewsId);
					EntityObj pNewsEntity = new EntityObj();

					String id;pNewsEntity.fieldContents.put("id",

							id = arrFavorNewsId.get(arg3));
					DetailsData.fetchDetailOnSoap(id, (BaseActivity)context);
//					DetailsData.fetchDetailOnSoap(pNewsEntity,
//							((BaseActivity) context), DetailsData.entities,
//							new AsyncSoapResponseHandler() {
//
//								@Override
//								public void onSuccess(Object content, int reqId) {
//									// TODO Auto-generated method stub
//									super.onSuccess(content, reqId);
//									NewsDetailParser data = (NewsDetailParser) content;
//									DetailsData.tappedne = data
//											.getEntityDetail();
//
//									if (DetailsData.tappedne != null) {
//										DetailsData.startActivityOfDetail(
//												context, DetailsData.tappedne,
//												DetailsData.entities);
//									}
//								}
//
//							});
					// DetailsData.startActivityOfDetail(getBaseContext(),
					// arrFavorNewsId.get(arg3).toString(),new ArrayList());
					Log.e("ididi", arrFavorNewsId.get(arg3));

//					startActivity(intent);
				}
				if (arg2 == 3) {
					convertPaper(arrFavorResearchId);
					EntityObj pNewsEntity = new EntityObj();
					pNewsEntity.fieldContents.put("Id",arrFavorResearchId.get(arg3));
					DetailsData.tappedne = pNewsEntity;
					intent = new Intent(HistoryFavoriteActivity.this,
							PaperDetailActivity.class);
					intent.putExtra("id",
							arrFavorResearchId.get(arg3).split(",")[0]);
					intent.putExtra("ta", "");
					intent.putExtra("articles_type", "");
					startActivity(intent);
				}
				// if (arg2 == 4) {
				// intent = new Intent(HistoryFavoriteActivity.this,
				// RelatedEventsActivity.class);
				// intent.putExtra("id", arrFavorEventId.get(arg3));
				// intent.putExtra("position", -1);
				// startActivity(intent);
				// }
				if (arg2 == 4) {
					intent = new Intent(HistoryFavoriteActivity.this,
							DrugAlertsDetailActivity.class);
					Log.e("arrFavorDrugAlertTypeIds",
							arrFavorDrugAlertTypeIds.get(arg3));

					intent.putExtra("typeID", arrFavorDrugAlertTypeIds
							.get(arg3).trim());
					startActivity(intent);

				}

				return false;
			}

			private void convert(ArrayList<String> arrFavorNewsId) {
				// TODO Auto-generated method stub
				DetailsData.entities = new ArrayList();
				for (String id : arrFavorNewsId) {
					EntityObj ej = new EntityObj();
					ej.fieldContents.put("id", id);
					DetailsData.entities.add(ej);
				}
			}
			
			private void convertPaper(ArrayList<String> arrFavorNewsId) {
				// TODO Auto-generated method stub
				DetailsData.entities = new ArrayList();
				for (String id : arrFavorNewsId) {
					EntityObj ej = new EntityObj();
					ej.fieldContents.put("Id", id);
					DetailsData.entities.add(ej);
				}
			}

		});

		arrFavor = fData.selectTabDrugRef(this,
				SharedPreferencesMgr.getUserName());
		arrFavorEvent = fData.selectEventC(this,
				SharedPreferencesMgr.getUserName());
		arrFovEvent = new String[arrFavorEvent.size()];
		for (int i = 0; i < arrFavorEvent.size(); i++) {
			arrFovEvent[i] = arrFavorEvent.get(i);
		}

		arrFavorDrugAlerts = fData.selectHistoryDrugAlert(context,
				SharedPreferencesMgr.getUserName());
		arrFovDurgAlert = new String[arrFavorDrugAlerts.size()];
		for (int i = 0; i < arrFavorDrugAlerts.size(); i++) {
			arrFovDurgAlert[i] = arrFavorDrugAlerts.get(i);
		}
		// }
		arrFavorNews = fData.selectNews(SharedPreferencesMgr.getUserName());
		arrFovNews = new String[arrFavorNews.size()];
		for (int i1 = 0; i1 < arrFavorNews.size(); i1++) {
			arrFovNews[i1] = arrFavorNews.get(i1);
		}
		arrFavorResearch = fData.selectResearchc(this,
				SharedPreferencesMgr.getUserName());
		arrFovResearch = new String[arrFavorResearch.size()];
		for (int i = 0; i < arrFavorResearch.size(); i++) {
			arrFovResearch[i] = arrFavorResearch.get(i);
		}
		// arrFavorTabHeart=MySqlite.selectTabHeartCollect(context,userName);
		// Log.e("arrFavorTabHeart", String.valueOf(arrFavorTabHeart.size()));
		//
		// arrFavorTabTumor=MySqlite.selectTabCalcTumor(context,userName);
		arrFavorTabcalc = fData.selectTabCalcc(this,
				SharedPreferencesMgr.getUserName());

		Log.e("arrFavor1", String.valueOf(arrFavorTabcalc.size()));
		arrFavorTabcalc.addAll(arrFavorTabTumor);

		Log.e("arrFavor2", String.valueOf(arrFavorTabcalc.size()));
		arrFavorTabcalc.addAll(arrFavorTabHeart);
		Log.e("arrFavor3", String.valueOf(arrFavorTabcalc.size()));
		arrFovTabcalc = new String[arrFavorTabcalc.size()];
		for (int i1 = 0; i1 < arrFavorTabcalc.size(); i1++) {
			arrFovTabcalc[i1] = arrFavorTabcalc.get(i1);
		}
		adapter = new MyExpandableListAdapter();
		elist.setAdapter(adapter);
		// setListAdapter(adapter);
		registerForContextMenu(elist);
		imgFavorite.setBackgroundResource(R.drawable.view_favorite);
		imgHistory.setBackgroundResource(R.drawable.view_history_hover);
		txtFavorite.setTextColor(Color.BLACK);
		txtHistory.setTextColor(Color.BLACK);
		return myView;

	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		LayoutInflater layoutInflater = getLayoutInflater();
		// public String[] groups = { getString(R.string.drug),
		// getString(R.string.calculator), getString(R.string.news),
		// getString(R.string.research),
		// getString(R.string.event),getString(R.string.drugalert)};

		ForegroundColorSpan fgSpan = new ForegroundColorSpan(context
				.getResources().getColor(R.color.gray));

		public String[] groups = { getString(R.string.drug),
				getString(R.string.calculator), getString(R.string.news),
				getString(R.string.research), getString(R.string.drugalert) };
		// public String[][] children = { {}, {}, {}, {}, {},{}};
		public String[][] children = { {}, {}, {}, {}, {} };

		public CharSequence getChild(int groupPosition, int childPosition) {
			// children[4] = arrFovEvent;
			if (groupPosition == 0) {
				return getSpannable(arrFavor.get(childPosition));
			}

			children[1] = arrFovTabcalc;
			children[2] = arrFovNews;
			children[3] = arrFovResearch;
			children[4] = arrFovDurgAlert;

			return children[groupPosition][childPosition];
		}

		private CharSequence getSpannable(FavoritDrugEntity info) {
			CharSequence spannable = info.drugName;
			String adminRouteContent = info.adminRouteName;

			if (!TextUtils.isEmpty(adminRouteContent)) {
				adminRouteContent = "[" + adminRouteContent + "]";
				String splitString = "  ";
				String text = spannable + splitString + adminRouteContent;

				spannable = new SpannableString(text);
				((Spannable) spannable).setSpan(fgSpan,
						text.indexOf(splitString) + splitString.length(),
						text.indexOf(splitString) + splitString.length()
								+ adminRouteContent.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			return spannable;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			// children[4] = arrFovEvent;
			if (groupPosition == 0) {// 药品
				return arrFavor.size();
			}
			children[1] = arrFovTabcalc;
			children[2] = arrFovNews;
			children[3] = arrFovResearch;
			children[4] = arrFovDurgAlert;
			return children[groupPosition].length;
		}

		public TextView getGenericView() {
			TextView textView = (TextView) layoutInflater.inflate(
					R.layout.fav_text, null);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(getChild(groupPosition, childPosition).toString());
			textView.setPadding(20, 0, 0, 0);
			int res = 0;
			switch (groupPosition) {
			case 0:
				res = R.drawable.history_drug;
				break;
			case 1:
				res = R.drawable.history_calculator;
				break;
			case 2:
				res = R.drawable.history_news;
				break;
			case 3:
				res = R.drawable.history_research;
				break;
			case 4:
				res = R.drawable.history_drug_alert;
				break;
			}
			Drawable left = getResources().getDrawable(res);
			textView.setCompoundDrawablesWithIntrinsicBounds(left, null, null,
					null);
			return textView;
		}

		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		public int getGroupCount() {
			return groups.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setTextSize(22);
			textView.setHeight(45);
			textView.setPadding(40, 0, 0, 0);
			textView.setText(getGroup(groupPosition).toString());
			textView.setGravity(Gravity.CENTER_VERTICAL);
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		super.onReqResponse(o, methodId);
		if (o instanceof SyncHistoryPaser) {
			SyncHistoryPaser shp = (SyncHistoryPaser) o;
			ArrayList<SyncHistoryEntity> historyList = shp.getHistoryList();
			historyAdapter.syncDatabase();
			for (SyncHistoryEntity en : historyList) {
				historyAdapter.storeHistoryFromServer(en.getUsername(),
						en.getVid(), en.getvName(), en.getvColId(),
						en.getVparentId(), en.getTime());
			}
		}
		if (o instanceof NewSyncFavPaser) {
			NewSyncFavPaser nsf = (NewSyncFavPaser) o;
			ArrayList<NewFavItemEntity> all = nsf.al;
			Log.e("al", String.valueOf(all.size()) + "000000");

			myFavoriteAdapter.delAllNewsCollection(SharedPreferencesMgr
					.getUserName());
			myFavoriteAdapter.delAllTabDrugReference(SharedPreferencesMgr
					.getUserName());
			myFavoriteAdapter.delAllEventCollection(SharedPreferencesMgr
					.getUserName());
			myFavoriteAdapter.delAllResearchCollection(SharedPreferencesMgr
					.getUserName());
			myFavoriteAdapter.delAllTabCalc(SharedPreferencesMgr.getUserName());
			myFavoriteAdapter.delAllDurgAlert(SharedPreferencesMgr
					.getUserName());

			for (int i = 0; i < all.size(); i++) {
				Log.e("getCategoryId()", all.get(i).getCategoryId());
				if (all.get(i).getCategoryId().equals(Constant.NEWS_COLID))// 医学新闻
				{
					myFavoriteAdapter.insertTableNewsCollection(all.get(i)
							.getFavId(), all.get(i).getFavName(), "",
							SharedPreferencesMgr.getUserName());
				}
				if (all.get(i).getCategoryId().equals(Constant.DRUG_COLID))// 药品信息
				{
					Log.e("getCategoryId()", all.get(i).getCategoryId());
					myFavoriteAdapter.insertTabDrugReference(all.get(i)
							.getFavId(), all.get(i).getFavName(),
							SharedPreferencesMgr.getUserName(), null, null,
							null, 0);
				}
				if (all.get(i).getCategoryId().equals(Constant.EVENTS_COLID))// 会议
				{
					myFavoriteAdapter.insertTableEventCollection(all.get(i)
							.getFavId(), all.get(i).getFavName(), "", "",
							SharedPreferencesMgr.getUserName());
				}
				if (all.get(i).getCategoryId().equals(Constant.RESEARCH_COLID))// 医学研究
				{
					myFavoriteAdapter.insertTableResearchCollection(all.get(i)
							.getFavId(), all.get(i).getFavName(), "", "", "",
							"", SharedPreferencesMgr.getUserName());
				}
				if (all.get(i).getCategoryId().equals(Constant.CALC_COLID))// 医学公式
				{
					Log.e("calcName12314", all.get(i).getFavName());
					Log.e("calcId57585", all.get(i).getFavId());
					// if(all.get(i).getFavId().length()>3&&all.get(i).getFavId().substring(0,
					// 3).equals("com"))
					// {
					// myFavoriteAdapter.insertTabCalc( all.get(i).getFavName(),
					// all.get(i).getFavId(),SharedPreferencesMgr.getUserName());
					// }
					// else
					// if(all.get(i).getFavId().length()>3&&all.get(i).getFavId().substring(0,
					// 3).equals("CNC"))
					// {
					// myFavoriteAdapter.insertTabTumor(
					// all.get(i).getFavName(), "",
					// all.get(i).getFavId(),SharedPreferencesMgr.getUserName());
					// }
					// else
					// {
					// if(all.get(i).getFavId().equals(""))
					// {
					// myFavoriteAdapter.insertTableHeartCalc(
					// all.get(i).getFavName(),
					// Integer.valueOf(all.get(i).getFavId()),SharedPreferencesMgr.getUserName());
					// }
					//
					// }
				}

				if (all.get(i).getCategoryId()
						.equals(Constant.DRUG_ALERT_COLID)) {
					dbAdapter.insertDrugAlertCollection(all.get(i).getFavId(),
							all.get(i).getFavName());
				}

			}
		}
	}

	// public void synchronizeData() {
	// switch (cur_flag) {
	// case history:
	// HistoryAdapter historyAdapter = new HistoryAdapter(this, 1,
	// "mysqllite.db");
	// String currentStr = historyAdapter.generateXML();
	// System.out.println("currentStr   " + currentStr);
	// Properties propertyInfo = new Properties();
	// propertyInfo.put(SoapRes.KEY_NEW_VIEW_HISTORY, currentStr);
	// sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_SYNC_HISTORY_NEW,
	// propertyInfo, baseHandler);
	// break;
	// case favorite:
	// Properties property = new Properties();
	// myFavoriteAdapter.selectAllEventCollection(SharedPreferencesMgr
	// .getUserName());
	// myFavoriteAdapter.selectAllNewsCollection(SharedPreferencesMgr
	// .getUserName());
	// myFavoriteAdapter.selectAllResearchCollection(SharedPreferencesMgr
	// .getUserName());
	// myFavoriteAdapter.selectAllTabCalc(
	// SharedPreferencesMgr.getUserName(), this);
	// myFavoriteAdapter.selectAllTabDrugReference(SharedPreferencesMgr
	// .getUserName());
	// myFavoriteAdapter.selectAllDrugAlert(SharedPreferencesMgr
	// .getUserName());
	// String str = "";
	// if (FavoritDataAdapter.sbXml.equals("")) {
	// str = "<xml><item><userid>"
	// + SharedPreferencesMgr.getUserName()
	// + "</userid><favid></favid><categoryId></categoryId></item></xml>";
	// } else {
	// str = "<xml>" + FavoritDataAdapter.sbXml + "</xml>";
	// Log.e("sbXml", FavoritDataAdapter.sbXml);
	// }
	// property.put(SoapRes.KEY_FAVORITE_USERFAV, str);
	// property.put(SoapRes.KEY_FAVORITE_SPEC, "");
	// sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_FAVORITE, property,
	// baseHand);
	// Intent intent = new Intent(HistoryFavoriteActivity.this,
	// HistoryFavoriteActivity.class);
	// startActivity(intent);
	// fav = true;
	// break;
	// }
	// }
}
