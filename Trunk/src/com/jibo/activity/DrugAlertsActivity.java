package com.jibo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.adapter.ViewPageAdapter;
import com.jibo.adapter.ViewPageChangeListener;
import com.jibo.adapter.ViewPageChangeListener.IPageChange;
import com.jibo.app.push.PushConst;
import com.jibo.app.research.PaperDetailActivity;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.data.DrugAlertDataPaser;
import com.jibo.data.DrugAlertDetailListPaser;
import com.jibo.data.entity.DrugAlertDetailEntity;
import com.jibo.data.entity.DrugAlertEntity;
import com.jibo.dbhelper.DrugAlertSQLAdapter;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.ui.HomePageLayout;
import com.jibo.ui.NavigateView;
import com.jibo.ui.NavigateView.OnChangeListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 用户安全(不良反应通告)
 * 
 * @author simon
 * 
 */
public class DrugAlertsActivity extends BaseSearchActivity implements
		OnScrollListener, OnItemClickListener, OnGestureListener, IPageChange
		{

	@SuppressWarnings("unused")
	private final String TAG = "DrugAlerts";

	private Context context;

	/**
	 * 锁定加载更多线程，防止重复加载
	 */
	private boolean flag = true;

	/**
	 * adapter数组
	 */
	private DrugAlertsAdapter[] adapter = new DrugAlertsAdapter[4];
	/**
	 * 是否做更新了，true表示更新过了，进入模块后每个tab卡默认会检查更新一次
	 */
	private boolean[] isCheckNewest = { false, false, false, false };
	/**
	 * listView数组
	 */
	private ListView[] lists;
	/**
	 * 标示在初始化时，当前tab下的列表是否有加载条。添加规则：数据是否超过20条，未超过则不添加footerView
	 */
	private boolean[] flags = { false, false, false, false };
	/**
	 * tab卡对应布局数组
	 */
//	private final int[] contentLayoutIds = { R.id.drugAlert_tabcontent1,
//			R.id.drugAlert_tabcontent2, R.id.drugAlert_tabcontent3,
//			R.id.drugAlert_tabcontent4 };
	/**
	 * listView id arrays
	 */
//	private final int[] listViewIds = { R.id.lst_item1, R.id.lst_item2,
//			R.id.lst_item3, R.id.lst_item4 };
	/**
	 * 加载条
	 */
	private View[] footerView;
	/**
	 * tab卡titles
	 */
	private String[] sources = { "全部", "EMA", "FDA", "CFDA" };
	private String[] sentSource = {"全部", "EMA", "FDA", "SFDA" };
	/**
	 * 当前视图对应的sources
	 */
	private int currentSources;
	// /**
	// * 当前页码
	// */
	// 废弃分页加载数据的方式
	// private int pageNum = 2;
	/**
	 * tab组件
	 */
//	private TabHost tabHost;
//	private TabWidget tabWidget;
	/**
	 * tabwidget内部变量，反射得到后 改变背景条
	 */
//	private Field mBottomLeftStrip;
//	private Field mBottomRightStrip;
	/**
	 * 任务bean
	 */
	private DrugAlertsTask task;

	/**
	 * db 工具类
	 */
	private DrugAlertSQLAdapter dbAdapter;

	// private Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// switch (msg.what) {
	// case 1:// 检测到服务器有更新。获取更新
	// Bundle data = msg.getData();
	// String localTime = data.getString("localTime");
	// int source = data.getInt("source");
	// Properties propertyInfo = new Properties();
	// propertyInfo.put(SoapRes.KEY_DRUGALERT_SOURCE,
	// currentSources == 0 ? "" : sources[currentSources]);
	// sendRequest(SoapRes.URLDrug,
	// SoapRes.REQ_ID_GET_DRUGALERT_TOP_TIME, propertyInfo,
	// new DrugAlertResponseHandler(DrugAlertsActivity.this,
	// source, localTime));
	// break;
	// }
	// }
	// };

	/**
	 * 列表显示的内容
	 */
	private ArrayList<DrugAlertEntity> list;
	/**
	 * 记录历史功能 db 工具类
	 */
	private HistoryAdapter historyAdapter;

	private GBApplication app;
	
	/** 导航手势滑动分页 */
	private ViewPager viewPager;
	
	/** 导航条 */
	private NavigateView navigateView;

	/**
	 * act 销毁时回调方法。
	 * 
	 * 这里将db close
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbAdapter.closeDB();
		historyAdapter.closeDB();
		uploadLoginLogNew( "Activity", "DrugAlerts", "end",null);
	}

	/**
	 * 自定义adapter
	 * 
	 */
	private class DrugAlertsAdapter extends BaseAdapter {
		public ArrayList<DrugAlertEntity> list;

		@Override
		public int getCount() {
			if (null == list)
				return 0;
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			if (null == list)
				return null;
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public String getLastItemId() {
			if (null == list)
				return null;
			int length = list.size();
			if (length > 0)
				return list.get(length - 1).getId();
			return null;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (list == null) {
				return null;
			}
			DrugAlertsListHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.drugalerts_listview_item, null);
				holder = new DrugAlertsListHolder();
				holder.alertTitle = (TextView) convertView
						.findViewById(R.id.drugalert_title);
				holder.alertDate = (TextView) convertView
						.findViewById(R.id.drugalert_date);

				holder.alertCategory = (TextView) convertView
						.findViewById(R.id.drugalert_category);

				convertView.setTag(holder);
			} else {
				holder = (DrugAlertsListHolder) convertView.getTag();
			}
			holder.alertTitle.setText(list.get(position).getTitle());
			holder.alertDate.setText(list.get(position).getDate());
			holder.alertCategory.setText(list.get(position).getCategory());
			return convertView;
		}

		class DrugAlertsListHolder {
			TextView alertTitle;
			TextView alertDate;
			TextView alertCategory;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drugalerts);
		super.onCreate(savedInstanceState);
		
		sources[0] = getResources().getString(R.string.alert_all);
		
		context = this;
		((TextView) findViewById(R.id.txt_header_title))
				.setText(R.string.drugalert);
		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");
		app = (GBApplication) getApplication();
		initPageView();
//		initTab();
		dbAdapter = new DrugAlertSQLAdapter(this);
		Intent intent = getIntent();
		ArrayList<?> data = (ArrayList<?>) intent
				.getParcelableArrayListExtra("list");
		list = (ArrayList<DrugAlertEntity>) data;
		boolean isLoadLocal = intent.getBooleanExtra("isLoadLocal", false);// 若数据从本地获取，则为true,默认需要检查更新
		isCheckNewest[currentSources] = !isLoadLocal;
		setPopupWindowType(Constant.MENU_TYPE_1);
		if (null == list)
			return;
		showListView(list, currentSources);
		if (isLoadLocal) {// 表明当前show的是本地数据，检查服务器是否有更新
			getRemoteAlertNewData(currentSources, list.get(0).getId());
		} else {
			saveCacheToLocalAll(list, 0);
		}
		
		DrugAlertsActivity.toast = Toast.makeText(getApplicationContext(), "已到达最后一条", Toast.LENGTH_SHORT);
		
		MobclickAgent.onError(this);
		if (PushConst.pushFlag) {
			if (PushConst.pushmodule == PushConst.News_MODULE) {
				startActivity(new Intent(context, DrugAlertsDetailActivity.class));
				PushConst.resetPush();
			}
		}
		uploadLoginLogNew("Activity", "DrugAlerts", "create", null);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!flag) {
			getRemoteAlertMoreData(currentSources,
					adapter[currentSources].getLastItemId());
		}
		MobclickAgent.onResume(this);

	}
	
	/**
	 * 初始化pageview
	 */
	private void initPageView(){
		viewPager = (ViewPager) findViewById(R.id.pagerGroup);
		navigateView = (NavigateView) findViewById(R.id.navigateView);
		navigateView.setTabCount(4);
		navigateView.setOnChangeListener(new OnChangeListener() {
			@Override
			public void onChange(int type) {
				viewPager.setCurrentItem(type);
			}
		});
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> pageListView = new ArrayList<View>();
		int len = sources.length;
		lists = new ListView[len];
		footerView = new View[len];
		for(int i=0; i<sources.length; i++){
			navigateView.setText(i, sources[i]);
			LinearLayout layout = (LinearLayout) inflater.inflate(
					R.layout.drugalert_listview, null);
			lists[i] = (ListView) layout.findViewById(R.id.lst_item);
			lists[i].setOnItemClickListener(this);
			lists[i].setOnScrollListener(this);
			footerView[i] = View.inflate(this, R.layout.dialogprogress, null);
			pageListView.add(lists[i]);
			layout.removeAllViews();
		}
		navigateView.changeUI(0);
		// 初始化viewPager
		viewPager.setAdapter(new ViewPageAdapter(pageListView));
		viewPager.setOnPageChangeListener(new ViewPageChangeListener(this));
	}
	
	/**
	 * 初始化tab页
	 */
	/*private void initTab() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
//		tabHost.setup();
		tabHost.bringToFront();

		int len = sources.length;
		lists = new ListView[len];
		footerView = new View[len];
		for (int i = 0; i < len; i++) {
			tabHost.addTab(tabHost.newTabSpec(sources[i])
					.setIndicator(sources[i]).setContent(contentLayoutIds[i]));

		}

		if (Integer.valueOf(Build.VERSION.SDK) <= 7) {
			try {
				mBottomLeftStrip = tabWidget.getClass().getDeclaredField(
						"mBottomLeftStrip");
				mBottomRightStrip = tabWidget.getClass().getDeclaredField(
						"mBottomRightStrip");
				if (!mBottomLeftStrip.isAccessible()) {
					mBottomLeftStrip.setAccessible(true);
				}
				if (!mBottomRightStrip.isAccessible()) {
					mBottomRightStrip.setAccessible(true);
				}
				mBottomLeftStrip.set(tabWidget,
						getResources().getDrawable(R.drawable.title_bg));
				mBottomRightStrip.set(tabWidget,
						getResources().getDrawable(R.drawable.title_bg));

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				mBottomLeftStrip = tabWidget.getClass().getDeclaredField(
						"mLeftStrip");
				mBottomRightStrip = tabWidget.getClass().getDeclaredField(
						"mRightStrip");
				if (!mBottomLeftStrip.isAccessible()) {
					mBottomLeftStrip.setAccessible(true);
				}
				if (!mBottomRightStrip.isAccessible()) {
					mBottomRightStrip.setAccessible(true);
				}
				mBottomLeftStrip.set(tabWidget,
						getResources().getDrawable(R.drawable.title_bg));
				mBottomRightStrip.set(tabWidget,
						getResources().getDrawable(R.drawable.title_bg));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < len; i++) {
			View view = tabWidget.getChildAt(i);
			view.layout(0, 0, 0, 0);
			if (currentSources == i) {
				view.setBackgroundResource(R.drawable.tabwidget_select);
			} else {
				view.setBackgroundResource(0);
			}

			final TextView tv = (TextView) view
					.findViewById(android.R.id.title);
			tv.setTextSize(18);
			tv.setTextColor(Color.BLACK);

			int Width = getWindowManager().getDefaultDisplay().getWidth();
			int Height = getWindowManager().getDefaultDisplay().getHeight();

			Log.i("GAB", "width=" + Width + "<>" + "height=" + Height);
			if ((Width * Height) <= (320 * 480)) {
				view.getLayoutParams().height = 40;
			} else {
				view.getLayoutParams().height = 60;
			}

			final ImageView image = (ImageView) view
					.findViewById(android.R.id.icon);
			image.getLayoutParams().height = 0;

			lists[i] = (ListView) findViewById(listViewIds[i]);
			lists[i].setOnItemClickListener(this);
			lists[i].setOnScrollListener(this);
			footerView[i] = View.inflate(this, R.layout.dialogprogress, null);
		}
		tabHost.setOnTabChangedListener(this);
	}*/
	private static Toast toast;
	private static boolean isMoveDown = true;//是否为向下划屏
	private int mMotionY = 0;//划屏后的y坐标
	/**
	 * 列表滑动监听，滑动到最底部时，自动开启线程加载更多
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		/*view的监听是为了判断手势的滑动是向上还是向下，向下时我们才提示到了最后 add by Terry*/
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
					deltaY = y - mMotionY;   //delta的正负就表示往下或往上
					if(deltaY > 0){
						isMoveDown = false;
					}else if(deltaY < 0){
						isMoveDown = true;
					}
					break;
					}
				}

				return false;
				}}
			);
		
		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE
				|| scrollState == ListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			/**
			 * 当前列表有footerView && 当前没有其他线程在加载更多 && 滑动到列表最后一列
			 */
			if (flags[currentSources] && flag
					&& view.getLastVisiblePosition() == view.getCount() - 1) {

				if (list.size() < 10) {
					if (lists[currentSources].getFooterViewsCount() > 0) {
						lists[currentSources]
								.removeFooterView(footerView[currentSources]);
						
						Log.i("simon", currentSources
								+ ">>>>>> removeFooterView" + ">>>>>>>>");
					}
					if(isMoveDown == true){
						toast.cancel();
						toast.show();
					}
				} else {
					if (lists[currentSources].getFooterViewsCount() <= 0) {
						lists[currentSources]
								.addFooterView(footerView[currentSources]);
						lists[currentSources].setSelection(view.getCount());
					}
					getRemoteAlertMoreData(currentSources,
							adapter[currentSources].getLastItemId());
					flag = false;// 限定同一时刻只能开一个线程去加载
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	/**
	 * 列表item项点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (position < adapter[currentSources].list.size()) {
			DrugAlertEntity entity = adapter[currentSources].list.get(position);
			// 跳转详细页
			task = new DrugAlertsTask(entity);
			task.execute(new Integer[] { 2 });
		}
	}

	/**
	 * 任务task
	 * 
	 */
	public class DrugAlertsTask extends AsyncTask<Integer, Integer, Long> {

		private Intent intent;
		private int source;
		private DrugAlertEntity entity;

		private String id;

		public DrugAlertsTask() {
		}

		public DrugAlertsTask(DrugAlertEntity entity) {
			this.entity = entity;
		}

		public DrugAlertsTask(int source) {
			this.source = source;
		}

		@Override
		protected void onPreExecute() {
			if (null != DrugAlertsActivity.this
					&& !DrugAlertsActivity.this.isFinishing())
				DrugAlertsActivity.this
						.showDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected Long doInBackground(Integer... params) {
			int length = params.length;
			if (length != 0) {
				switch (params[0]) {
				case 1:// 选择tab卡
					list = dbAdapter.getDrugAlertsByLocalDatabase(String
							.valueOf(source));// 检查本地缓存
					if (null != list && list.size() > 0) {
						id = list.get(0).getId();
						return 1L;
					} else {
						return 2L;
					}

				case 2:// 跳转详细页
					ArrayList<DrugAlertDetailEntity> detailList = dbAdapter
							.getDrugAlertsDetailByLocalDatabase(entity
									.getTypeId());
					if (null != detailList && detailList.size() > 0) {
						intent = new Intent(context,
								DrugAlertsDetailActivity.class);
						intent.putExtra("isLoadLocal", true);
						intent.putExtra("DrugAlertEntity", entity);
						intent.putExtra("fromActivity", "DrugAlertsActivity");
						intent.putExtra("DrugAlertDetailList", detailList);
						String title = entity.getTitle()
								+ getString(R.string.str_split)
								+ entity.getTypeId();
						int colID = -1;
						for (Entry<?, ?> en : app.getPdaColumnMap().entrySet()) {
							if (en.getValue().toString()
									.equals(getString(R.string.drugalert))) {
								colID = (Integer) en.getKey();
							}
						}
						historyAdapter.storeViewHistory(app.getLogin()
								.getGbUserName(), -1, colID, -1, title);
						return 3L;
					} else {
						return 4L;
					}
				}
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Long result) {
			Log.i("GBA", "onPostExecute");
			if (result == null) {
				DrugAlertsActivity.this
						.removeDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
				return;
			}
			if (result == 1L) {// 选择tab卡，本地有数据
				DrugAlertsActivity.this
						.removeDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
				showListView(list, source);
				if (!isCheckNewest[source]) {// 查看是否已经做过更新检查
					getRemoteAlertNewData(source, id);
					// checkIsNewestValues();
				}
			} else if (result == 2L) {// 选择tab卡，本地没数据
				getRemoteAlertTop20Data(source);
			} else if (result == 3L) {// 跳转至详细页面，本地有数据
				DrugAlertsActivity.this
						.removeDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
				startActivity(intent);
			} else if (result == 4L) {// 跳转至详细页面，本地没数据
				getRemoteAlertDetailData(entity);
			}
		}
	}

	/**
	 * 网络请求回调接口
	 * 
	 * @param o
	 *            解析类
	 * @param source
	 *            当前在哪个试图，0：全部；1：EMA；2：FDA 3：SFDA
	 * @param entity
	 *            跳转到详细时，点击的列表项对应实体bean
	 * @param localTime
	 *            当检查更新时，本地时间
	 */
	public void onReqResponse(int methodId, Object o, int source,
			DrugAlertEntity entity, String localTime) {

		if (o != null) {
			// if (o instanceof DrugAlertListDataTop20Paser) {// 获取更新，最新20条
			// // DrugAlertsActivity.this
			// // .removeDialog(DialogRes.DIALOG_UPDATE_FOR_DATA);
			// DrugAlertListDataTop20Paser data = (DrugAlertListDataTop20Paser)
			// o;
			// list = data.getList();
			// showListView(list, source);
			// // 插入到本地
			// saveCacheToLocalAll(list, source);
			// isCheckNewest[source] = true;// 标记检查过了
			// } else

			if (o instanceof DrugAlertDataPaser) {// 获取远程数据
				DrugAlertDataPaser paser = (DrugAlertDataPaser) o;
				switch (methodId) {
				case SoapRes.REQ_ID_GET_DRUGALERT_TOP_20:// 获取最新20条
					list = paser.getList();
					showListView(list, source);
					// 插入到本地
					saveCacheToLocalAll(list, source);
					isCheckNewest[source] = true;// 标记检查过了
					break;
				case SoapRes.REQ_ID_GET_DRUGALERTS_NEW_DATA:// 获取更新
					ArrayList<DrugAlertEntity> newList = paser.getList();
					if (newList.size() > 0) {
						if (newList.size() == 20) {
							list = newList;
							showListView(list, source);
							saveCacheToLocalAll(list, source);
						} else {
							adapter[source].list.addAll(0, newList);
							adapter[source].notifyDataSetChanged();
							saveCacheToLocalSome(newList, source);
						}
					}
					isCheckNewest[source] = true;
					break;
				case SoapRes.REQ_ID_GET_DRUGALERTS_MORE_DATA:// 获取更多
					list = paser.getList();
					adapter[source].list.addAll(list);
					adapter[source].notifyDataSetChanged();
					lists[source].removeFooterView(footerView[source]);
					flag = true;
					break;
				}
			}
			// else if (o instanceof DrugAlertCheckTimePaser) {// 获取最新时间
			// String time = ((DrugAlertCheckTimePaser) o).getTime();
			// if (time != null && !localTime.equals(time)) {// 比较时间
			// DrugAlertsActivity.this
			// .showDialog(DialogRes.DIALOG_UPDATE_FOR_DATA);
			// getRemoteAlertData(0, source);// 重新获取最新记录
			// }
			// }
			else if (o instanceof DrugAlertDetailListPaser) {// 获取详细记录，跳转到详细
				Intent intent = new Intent(context,
						DrugAlertsDetailActivity.class);
				intent.putExtra("isLoadLocal", false);
				intent.putExtra("DrugAlertEntity", entity);
				intent.putExtra("fromActivity", "DrugAlertsActivity");
				intent.putExtra("DrugAlertDetailList",
						((DrugAlertDetailListPaser) o).getList());

				String title = entity.getTitle()
						+ getString(R.string.str_split) + entity.getTypeId();
				int colID = -1;
				for (Entry<?, ?> en : app.getPdaColumnMap().entrySet()) {
					if (en.getValue().toString()
							.equals(getString(R.string.drugalert))) {
						colID = (Integer) en.getKey();
					}
				}
				historyAdapter.storeViewHistory(app.getLogin().getGbUserName(),
						Integer.parseInt(entity.getId()), colID, -1, title);

				startActivity(intent);
			}
		}
	}

	/**
	 * 网络请求发生错误时回调
	 */
	@Override
	public void onErrResponse(Throwable error, String content,boolean isBackGroundThread) {
		// 若在加载更多的网络请求出错，rollback...
		if (!flag) {
			// pageNum--;
			lists[currentSources].removeFooterView(footerView[currentSources]);
			flag = true;
		}
		super.onErrResponse(error, content,isBackGroundThread);
	}

	/**
	 * 获取服务器端最新20条数据
	 * 
	 * @param currentSource
	 */
	private void getRemoteAlertTop20Data(int currentSource) {
		String source = currentSource == 0 ? "" : sentSource[currentSource];
		Properties propertyInfo = new Properties();
		propertyInfo.put(SoapRes.KEY_DRUGALERT_TYPEID, "");
		propertyInfo.put(SoapRes.KEY_DRUGALERT_SOURCE, source);
		propertyInfo.put(SoapRes.KEY_SINCE_ID, "");
		propertyInfo.put(SoapRes.KEY_MAX_ID, "");
		propertyInfo.put(SoapRes.KEY_COUNT, "");
		sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_GET_DRUGALERT_TOP_20,
				propertyInfo, new DrugAlertResponseHandler(this, currentSource));

	}

	/**
	 * 加载更多
	 * 
	 * @param currentSource
	 * @param id
	 */
	private void getRemoteAlertMoreData(int currentSource, String id) {
		String source = currentSource == 0 ? "" : sentSource[currentSource];
		Properties propertyInfo = new Properties();
		propertyInfo.put(SoapRes.KEY_DRUGALERT_TYPEID, "");
		propertyInfo.put(SoapRes.KEY_DRUGALERT_SOURCE, source);
		propertyInfo.put(SoapRes.KEY_SINCE_ID, "");
		propertyInfo.put(SoapRes.KEY_MAX_ID, id);
		propertyInfo.put(SoapRes.KEY_COUNT, "10");
		sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_GET_DRUGALERTS_MORE_DATA,
				propertyInfo, new DrugAlertResponseHandler(this, currentSource));
	}

	/**
	 * 本地缓存有数据，检查更新
	 * 
	 * @param currentSource
	 * @param id
	 */
	private void getRemoteAlertNewData(int currentSource, String id) {
		String source = currentSource == 0 ? "" : sentSource[currentSource];
		Properties propertyInfo = new Properties();
		propertyInfo.put(SoapRes.KEY_DRUGALERT_TYPEID, "");
		propertyInfo.put(SoapRes.KEY_DRUGALERT_SOURCE, source);
		propertyInfo.put(SoapRes.KEY_SINCE_ID, id);
		propertyInfo.put(SoapRes.KEY_MAX_ID, "");
		propertyInfo.put(SoapRes.KEY_COUNT, "20");
		sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_GET_DRUGALERTS_NEW_DATA,
				propertyInfo, new DrugAlertResponseHandler(this, currentSource));
	}

	/**
	 * 获取远程list数据(通告详细)
	 * 
	 * @param entity
	 *            根据通告typeId获取详细
	 */
	private void getRemoteAlertDetailData(DrugAlertEntity entity) {
		Properties propertyInfo = new Properties();
		propertyInfo.put("Type_ID", entity.getTypeId());
		sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_GET_DRUGALERT_GET_DETAIL,
				propertyInfo, new DrugAlertResponseHandler(this, entity));
	}

	/**
	 * 全部缓存到本地数据库
	 * 
	 * @param list
	 * @param source
	 */
	private void saveCacheToLocalAll(final ArrayList<DrugAlertEntity> list,
			int source) {
		final String currentSource = String.valueOf(source);
		new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				dbAdapter.insertDrugAlertsAll(list, currentSource);
			}
		}).start();
	}

	/**
	 * 缓存到本地数据库，并保持缓存总数为20
	 * 
	 * @param list
	 * @param source
	 */
	private void saveCacheToLocalSome(final ArrayList<DrugAlertEntity> list,
			int source) {
		final String currentSource = String.valueOf(source);
		new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				dbAdapter.insertAndKeep20Caches(list, currentSource);
			}
		}).start();
	}

	// /**
	// * 检查最新时间
	// */
	// private void checkIsNewestValues(final int source,final String id) {
	// final String currentSource = String.valueOf(source);
	// new Thread(new Runnable() {
	// @Override
	// public synchronized void run() {
	// String newDate = dbAdapter.getDrugAlertsLastTime(currentSource);// 获取本地时间
	// if (null != newDate && !"".equals(newDate.trim())) {
	// Message msg = new Message();
	// msg.what = 1;
	// Bundle data = new Bundle();
	// data.putString("localTime", newDate);
	// data.putInt("source", source);
	// msg.setData(data);
	// handler.sendMessage(msg);
	// }
	// isCheckNewest[source] = true;// 标记检查过了
	// }
	// }).start();
	// }

	/**
	 * listView 显示
	 * 
	 * @param items
	 *            内容
	 * @param source
	 *            当前视图 0:全部 1:EMA 2:FDA 3:SFDA
	 */
	private void showListView(ArrayList<DrugAlertEntity> items, int source) {
		adapter[source] = new DrugAlertsAdapter();
		adapter[source].list = items;
		if (items.size() < 20) {
			if (lists[source].getFooterViewsCount() > 0) {
				Log.i("simon", source + ">>>>>> removeFooterView" + ">>>>>>>>");
				lists[source].removeFooterView(footerView[source]);
				flags[source] = false;
			}
		} else {
			if (lists[source].getFooterViewsCount() == 0) {
				Log.i("simon", source + ">>>>>> addFooterView" + ">>>>>>>>");
				lists[source].addFooterView(footerView[source]);
				flags[source] = true;
			}
		}
		lists[source].setAdapter(adapter[source]);
	}

	/**
	 * tab 卡选项切换 事件 监听
	 */
	/*@Override
	public void onTabChanged(String tabId) {
		// 切换tab页加载
		tabWidget.getChildAt(currentSources).setBackgroundResource(0);
		tabWidget.getChildAt(tabHost.getCurrentTab()).setBackgroundResource(
				R.drawable.tabwidget_select);
//		currentSources = tabHost.getCurrentTab();
		// 取消其他链接
		cancelConnection();
		task = new DrugAlertsTask(currentSources);
		task.execute(new Integer[] { 1 });
		// pageNum = 2;
		// }
	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (isPopflg()) {
				dissMissPop();
			} else {
				if (iCurState == STATE_CONNECTING) {
					cancelConnection();
					iCurState = STATE_STOP;
				}
				this.finish();
				Intent intentSend = new Intent(this, HomePageActivity.class);
				intentSend.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intentSend.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intentSend.putExtra("count", 0);
				HomePageLayout.s_pageID = 0;
				this.startActivity(intentSend);
			}
			return true;
		}
		return false;
	}

	/**
	 * 用药安全网络请求 回调handler
	 * 
	 * @author simon
	 * 
	 */
	private class DrugAlertResponseHandler extends AsyncSoapResponseHandler {
		private DrugAlertsActivity act;
		private DrugAlertEntity entity;
		private int source;// 当前类别
		private String localTime;// 本地 最新时间
		private boolean isShowDialog = true;// 当前是否已经开启dialog提示信息，默认true：已经打开

		public DrugAlertResponseHandler(DrugAlertsActivity act,
				DrugAlertEntity entity) {
			super();
			this.act = act;
			this.entity = entity;
		}

		public DrugAlertResponseHandler(DrugAlertsActivity act, int source) {
			super();
			this.act = act;
			this.source = source;
		}


		public void onStart() {
			if (act != null) {
				act.curReqTimes += 1;
			}
		}

		public void onFinish() {
			if (act != null) {
				act.curReqTimes -= 1;
				if (act.curReqTimes == 0 && isShowDialog)
					act.removeDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
			}
		}

		public void onSuccess(Object content, int methodId) {
			if (act != null) {
				act.onReqResponse(methodId, content, source, entity, localTime);
			}
		}

		public void onFailure(Throwable error, String content) {
			if (act != null)
				act.onErrResponse(error, content,false);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void treatMenuClick(int typeId) {
		if (typeId == Constant.FLAG_MY_JIBO) {
			Intent in = new Intent(this, MyFavoriteListActivity.class);// Util.myFavCategory
																		// =
																		// 1;//医学公式
			Constant.myFavCategory = 5;
			startActivity(in);
		} else
			super.treatMenuClick(typeId);
	}

	@Override
	public void setCurrentPoint(int index) {
		navigateView.changeUI(index);
		currentSources = index;
		cancelConnection();
		task = new DrugAlertsTask(currentSources);
		task.execute(new Integer[] { 1 });
	}
}
