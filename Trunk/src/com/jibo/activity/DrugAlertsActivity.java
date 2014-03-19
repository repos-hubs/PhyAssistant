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
 * �û���ȫ(������Ӧͨ��)
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
	 * �������ظ����̣߳���ֹ�ظ�����
	 */
	private boolean flag = true;

	/**
	 * adapter����
	 */
	private DrugAlertsAdapter[] adapter = new DrugAlertsAdapter[4];
	/**
	 * �Ƿ��������ˣ�true��ʾ���¹��ˣ�����ģ���ÿ��tab��Ĭ�ϻ������һ��
	 */
	private boolean[] isCheckNewest = { false, false, false, false };
	/**
	 * listView����
	 */
	private ListView[] lists;
	/**
	 * ��ʾ�ڳ�ʼ��ʱ����ǰtab�µ��б��Ƿ��м���������ӹ��������Ƿ񳬹�20����δ���������footerView
	 */
	private boolean[] flags = { false, false, false, false };
	/**
	 * tab����Ӧ��������
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
	 * ������
	 */
	private View[] footerView;
	/**
	 * tab��titles
	 */
	private String[] sources = { "ȫ��", "EMA", "FDA", "CFDA" };
	private String[] sentSource = {"ȫ��", "EMA", "FDA", "SFDA" };
	/**
	 * ��ǰ��ͼ��Ӧ��sources
	 */
	private int currentSources;
	// /**
	// * ��ǰҳ��
	// */
	// ������ҳ�������ݵķ�ʽ
	// private int pageNum = 2;
	/**
	 * tab���
	 */
//	private TabHost tabHost;
//	private TabWidget tabWidget;
	/**
	 * tabwidget�ڲ�����������õ��� �ı䱳����
	 */
//	private Field mBottomLeftStrip;
//	private Field mBottomRightStrip;
	/**
	 * ����bean
	 */
	private DrugAlertsTask task;

	/**
	 * db ������
	 */
	private DrugAlertSQLAdapter dbAdapter;

	// private Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// switch (msg.what) {
	// case 1:// ��⵽�������и��¡���ȡ����
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
	 * �б���ʾ������
	 */
	private ArrayList<DrugAlertEntity> list;
	/**
	 * ��¼��ʷ���� db ������
	 */
	private HistoryAdapter historyAdapter;

	private GBApplication app;
	
	/** �������ƻ�����ҳ */
	private ViewPager viewPager;
	
	/** ������ */
	private NavigateView navigateView;

	/**
	 * act ����ʱ�ص�������
	 * 
	 * ���ｫdb close
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbAdapter.closeDB();
		historyAdapter.closeDB();
		uploadLoginLogNew( "Activity", "DrugAlerts", "end",null);
	}

	/**
	 * �Զ���adapter
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
		boolean isLoadLocal = intent.getBooleanExtra("isLoadLocal", false);// �����ݴӱ��ػ�ȡ����Ϊtrue,Ĭ����Ҫ������
		isCheckNewest[currentSources] = !isLoadLocal;
		setPopupWindowType(Constant.MENU_TYPE_1);
		if (null == list)
			return;
		showListView(list, currentSources);
		if (isLoadLocal) {// ������ǰshow���Ǳ������ݣ����������Ƿ��и���
			getRemoteAlertNewData(currentSources, list.get(0).getId());
		} else {
			saveCacheToLocalAll(list, 0);
		}
		
		DrugAlertsActivity.toast = Toast.makeText(getApplicationContext(), "�ѵ������һ��", Toast.LENGTH_SHORT);
		
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
	 * ��ʼ��pageview
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
		// ��ʼ��viewPager
		viewPager.setAdapter(new ViewPageAdapter(pageListView));
		viewPager.setOnPageChangeListener(new ViewPageChangeListener(this));
	}
	
	/**
	 * ��ʼ��tabҳ
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
	private static boolean isMoveDown = true;//�Ƿ�Ϊ���»���
	private int mMotionY = 0;//�������y����
	/**
	 * �б�����������������ײ�ʱ���Զ������̼߳��ظ���
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		/*view�ļ�����Ϊ���ж����ƵĻ��������ϻ������£�����ʱ���ǲ���ʾ������� add by Terry*/
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
					deltaY = y - mMotionY;   //delta�������ͱ�ʾ���»�����
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
			 * ��ǰ�б���footerView && ��ǰû�������߳��ڼ��ظ��� && �������б����һ��
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
					flag = false;// �޶�ͬһʱ��ֻ�ܿ�һ���߳�ȥ����
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	/**
	 * �б�item�����¼�
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (position < adapter[currentSources].list.size()) {
			DrugAlertEntity entity = adapter[currentSources].list.get(position);
			// ��ת��ϸҳ
			task = new DrugAlertsTask(entity);
			task.execute(new Integer[] { 2 });
		}
	}

	/**
	 * ����task
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
				case 1:// ѡ��tab��
					list = dbAdapter.getDrugAlertsByLocalDatabase(String
							.valueOf(source));// ��鱾�ػ���
					if (null != list && list.size() > 0) {
						id = list.get(0).getId();
						return 1L;
					} else {
						return 2L;
					}

				case 2:// ��ת��ϸҳ
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
			if (result == 1L) {// ѡ��tab��������������
				DrugAlertsActivity.this
						.removeDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
				showListView(list, source);
				if (!isCheckNewest[source]) {// �鿴�Ƿ��Ѿ��������¼��
					getRemoteAlertNewData(source, id);
					// checkIsNewestValues();
				}
			} else if (result == 2L) {// ѡ��tab��������û����
				getRemoteAlertTop20Data(source);
			} else if (result == 3L) {// ��ת����ϸҳ�棬����������
				DrugAlertsActivity.this
						.removeDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
				startActivity(intent);
			} else if (result == 4L) {// ��ת����ϸҳ�棬����û����
				getRemoteAlertDetailData(entity);
			}
		}
	}

	/**
	 * ��������ص��ӿ�
	 * 
	 * @param o
	 *            ������
	 * @param source
	 *            ��ǰ���ĸ���ͼ��0��ȫ����1��EMA��2��FDA 3��SFDA
	 * @param entity
	 *            ��ת����ϸʱ��������б����Ӧʵ��bean
	 * @param localTime
	 *            ��������ʱ������ʱ��
	 */
	public void onReqResponse(int methodId, Object o, int source,
			DrugAlertEntity entity, String localTime) {

		if (o != null) {
			// if (o instanceof DrugAlertListDataTop20Paser) {// ��ȡ���£�����20��
			// // DrugAlertsActivity.this
			// // .removeDialog(DialogRes.DIALOG_UPDATE_FOR_DATA);
			// DrugAlertListDataTop20Paser data = (DrugAlertListDataTop20Paser)
			// o;
			// list = data.getList();
			// showListView(list, source);
			// // ���뵽����
			// saveCacheToLocalAll(list, source);
			// isCheckNewest[source] = true;// ��Ǽ�����
			// } else

			if (o instanceof DrugAlertDataPaser) {// ��ȡԶ������
				DrugAlertDataPaser paser = (DrugAlertDataPaser) o;
				switch (methodId) {
				case SoapRes.REQ_ID_GET_DRUGALERT_TOP_20:// ��ȡ����20��
					list = paser.getList();
					showListView(list, source);
					// ���뵽����
					saveCacheToLocalAll(list, source);
					isCheckNewest[source] = true;// ��Ǽ�����
					break;
				case SoapRes.REQ_ID_GET_DRUGALERTS_NEW_DATA:// ��ȡ����
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
				case SoapRes.REQ_ID_GET_DRUGALERTS_MORE_DATA:// ��ȡ����
					list = paser.getList();
					adapter[source].list.addAll(list);
					adapter[source].notifyDataSetChanged();
					lists[source].removeFooterView(footerView[source]);
					flag = true;
					break;
				}
			}
			// else if (o instanceof DrugAlertCheckTimePaser) {// ��ȡ����ʱ��
			// String time = ((DrugAlertCheckTimePaser) o).getTime();
			// if (time != null && !localTime.equals(time)) {// �Ƚ�ʱ��
			// DrugAlertsActivity.this
			// .showDialog(DialogRes.DIALOG_UPDATE_FOR_DATA);
			// getRemoteAlertData(0, source);// ���»�ȡ���¼�¼
			// }
			// }
			else if (o instanceof DrugAlertDetailListPaser) {// ��ȡ��ϸ��¼����ת����ϸ
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
	 * ��������������ʱ�ص�
	 */
	@Override
	public void onErrResponse(Throwable error, String content,boolean isBackGroundThread) {
		// ���ڼ��ظ���������������rollback...
		if (!flag) {
			// pageNum--;
			lists[currentSources].removeFooterView(footerView[currentSources]);
			flag = true;
		}
		super.onErrResponse(error, content,isBackGroundThread);
	}

	/**
	 * ��ȡ������������20������
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
	 * ���ظ���
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
	 * ���ػ��������ݣ�������
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
	 * ��ȡԶ��list����(ͨ����ϸ)
	 * 
	 * @param entity
	 *            ����ͨ��typeId��ȡ��ϸ
	 */
	private void getRemoteAlertDetailData(DrugAlertEntity entity) {
		Properties propertyInfo = new Properties();
		propertyInfo.put("Type_ID", entity.getTypeId());
		sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_GET_DRUGALERT_GET_DETAIL,
				propertyInfo, new DrugAlertResponseHandler(this, entity));
	}

	/**
	 * ȫ�����浽�������ݿ�
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
	 * ���浽�������ݿ⣬�����ֻ�������Ϊ20
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
	// * �������ʱ��
	// */
	// private void checkIsNewestValues(final int source,final String id) {
	// final String currentSource = String.valueOf(source);
	// new Thread(new Runnable() {
	// @Override
	// public synchronized void run() {
	// String newDate = dbAdapter.getDrugAlertsLastTime(currentSource);// ��ȡ����ʱ��
	// if (null != newDate && !"".equals(newDate.trim())) {
	// Message msg = new Message();
	// msg.what = 1;
	// Bundle data = new Bundle();
	// data.putString("localTime", newDate);
	// data.putInt("source", source);
	// msg.setData(data);
	// handler.sendMessage(msg);
	// }
	// isCheckNewest[source] = true;// ��Ǽ�����
	// }
	// }).start();
	// }

	/**
	 * listView ��ʾ
	 * 
	 * @param items
	 *            ����
	 * @param source
	 *            ��ǰ��ͼ 0:ȫ�� 1:EMA 2:FDA 3:SFDA
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
	 * tab ��ѡ���л� �¼� ����
	 */
	/*@Override
	public void onTabChanged(String tabId) {
		// �л�tabҳ����
		tabWidget.getChildAt(currentSources).setBackgroundResource(0);
		tabWidget.getChildAt(tabHost.getCurrentTab()).setBackgroundResource(
				R.drawable.tabwidget_select);
//		currentSources = tabHost.getCurrentTab();
		// ȡ����������
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
	 * ��ҩ��ȫ�������� �ص�handler
	 * 
	 * @author simon
	 * 
	 */
	private class DrugAlertResponseHandler extends AsyncSoapResponseHandler {
		private DrugAlertsActivity act;
		private DrugAlertEntity entity;
		private int source;// ��ǰ���
		private String localTime;// ���� ����ʱ��
		private boolean isShowDialog = true;// ��ǰ�Ƿ��Ѿ�����dialog��ʾ��Ϣ��Ĭ��true���Ѿ���

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
																		// 1;//ҽѧ��ʽ
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
