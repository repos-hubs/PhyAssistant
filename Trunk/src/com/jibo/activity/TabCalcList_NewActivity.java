package com.jibo.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jibo.GBApplication;
import com.jibo.adapter.ViewPageAdapter;
import com.jibo.adapter.ViewPageChangeListener;
import com.jibo.adapter.ViewPageChangeListener.IPageChange;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.dao.FormulaDao;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.entity.CategoryFormulaMapping;
import com.jibo.entity.Formula;
import com.jibo.entity.FormulaCategory;
import com.jibo.ui.NavigateView;
import com.jibo.ui.NavigateView.GotoBackFirstInit;
import com.jibo.ui.NavigateView.OnChangeListener;
import com.jibo.util.tips.Mask;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;
import com.api.android.GBApp.R;


/**
 * ҽѧ��ʽ�б�������
 * 
 * @author simon
 * 
 */
public class TabCalcList_NewActivity extends BaseSearchActivity implements
		IPageChange {
	// �����⣺ҽѧ��ʽ
	private TextView txtTitle;

	private HistoryAdapter historyAdapter;
	private FavoritDataAdapter favadpter;
	private GBApplication app;
	private Context context;

	/**
	 * ������
	 */
	public NavigateView navigateView;

	/**
	 * ����
	 */
	public ViewPager viewPager;

	/** ���� */
	private ListView normalList;
	private List<Formula> normalListData;
	private View normolFooterView;

	/** ���� */
	private TextView cateGoryTitle;
	private ListView cateGoryListView;
	private View cateGoryFooterView;
	private List<FormulaCategory> categoryList;// �����������
	private FormulaCategory currentCategory;// ��ǰ���
	private List<CategoryFormulaMapping> currentFormulaList;// ��ǰ����Ӧ��ʽ����

	/** �ҵ��ղ� */
	private ListView myFavoriteList;
	private View myFavoriteFooterView;
	private ArrayList<String> arrFavorTabcalc;
	private TextView favoriteText;// ��û���ղؼ�¼ʱ��������Ϣ

	/** ��Ǹ�������view�Ƿ��ʼ�����ݣ�����Ѿ���ʼ�����ݣ�����ҳ�潫�����κβ��� */
	private boolean isNormalInit;
	private boolean isCategoryInit;
	private boolean isMyFavoriteInit;

	/** ���� */
	private ListView searchListView;
	private List<Formula> searchList;
	private String searchText;
	private View searchFooterView;
	
	/** tips*/
	@SuppressWarnings("unused")
	private Mask mask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tabcalclist_new);
		super.onCreate(savedInstanceState);
		context = this;
		inits();
		setPopupWindowType(Constant.MENU_TYPE_3);
		MobclickAgent.onError(this);
		uploadLoginLogNew("Activity", "Calc", "create", null);
	}


	@Override
	protected void treatMenuClick(int typeId) {
		if (typeId == Constant.FLAG_MY_JIBO) {
			Intent in = new Intent(this, MyFavoriteListActivity.class);// Util.myFavCategory
																		// =
																		// 1;//ҽѧ��ʽ
			Constant.myFavCategory = 1;
			startActivity(in);
		} else
			super.treatMenuClick(typeId);
	}

	/**
	 * ��ʼ��view
	 */
	public void inits() {
		LayoutInflater inflater = LayoutInflater.from(context);
		app = (GBApplication) getApplication();

		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");
		favadpter = new FavoritDataAdapter(this);

		txtTitle = (TextView) findViewById(R.id.txt_header_title);
		txtTitle.setText(getString(R.string.calculator));

		searchText = getIntent().getStringExtra(SEARCH_TEXT);
		if (searchText != null && !"".equals(searchText)) {// ������������
			findViewById(R.id.main_lay).setVisibility(View.GONE);
			findViewById(R.id.search_lay).setVisibility(View.VISIBLE);
			searchListView = (ListView) findViewById(R.id.lst_item);
			searchListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					goToDetail(searchList.get(position));
				}
			});
			searchFooterView = inflater.inflate(R.layout.dialogprogress, null);
			searchListView.addFooterView(searchFooterView);
			// searchListView.setAdapter(new CalculatorListAdapter(null));
			new DBTask().execute(new Integer[] { 4 });
		} else {// �������
			findViewById(R.id.main_lay).setVisibility(View.VISIBLE);
			findViewById(R.id.search_lay).setVisibility(View.GONE);

			navigateView = (NavigateView) findViewById(R.id.navigateView);
			navigateView.setOnChangeListener(new OnChangeListener() {
				@Override
				public void onChange(int type) {
					viewPager.setCurrentItem(type);
					initNavigateData(type);
				}
			});

			navigateView.setGotoBackListener(new GotoBackFirstInit() {

				@Override
				public void gotoBack(int type) {
					if (type == NavigateView.CATEGORY_TYPE) {// �����ʼ��
						goBackCategoryList();
					}
				}
			});

			viewPager = (ViewPager) findViewById(R.id.pagerGroup);

			// ����
			View normalView = inflater.inflate(R.layout.customize_listview,
					null);
			// ����
			View categoryView = inflater.inflate(
					R.layout.item_category_listview, null);

			// �ղ�
			View myFavoriteView = inflater
					.inflate(R.layout.favo_listview, null);

			// ÿ��view��Ӧ��ͼ���
			normalList = (ListView) normalView.findViewById(R.id.lst_item);
			cateGoryTitle = (TextView) categoryView
					.findViewById(R.id.chooseCategory);
			cateGoryListView = (ListView) categoryView
					.findViewById(R.id.lst_item);

			myFavoriteList = (ListView) myFavoriteView
					.findViewById(R.id.lst_item);
			favoriteText = (TextView) myFavoriteView
					.findViewById(R.id.favorite_text);
			favoriteText.setText(R.string.calculator_hint);

			// ��ʼ��������
			normolFooterView = inflater.inflate(R.layout.dialogprogress, null);
			cateGoryFooterView = inflater
					.inflate(R.layout.dialogprogress, null);
			myFavoriteFooterView = inflater.inflate(R.layout.dialogprogress,
					null);

			normalList.addFooterView(normolFooterView);
			normalList.setAdapter(new CalculatorListAdapter(null));
			cateGoryListView.addFooterView(cateGoryFooterView);
			cateGoryListView.setAdapter(new CalculatorListAdapter(null));
			myFavoriteList.addFooterView(myFavoriteFooterView);
			myFavoriteList.setAdapter(new CalculatorListAdapter(null));

			List<View> pageListView = new ArrayList<View>();
			pageListView.add(normalView);
			pageListView.add(categoryView);
			pageListView.add(myFavoriteView);

			// ��ʼ��viewPager������ֻ����View
			viewPager.setAdapter(new ViewPageAdapter(pageListView));
			viewPager.setOnPageChangeListener(new ViewPageChangeListener(this));

			// ��ʼ���¼�
			normalList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					goToDetail(normalListData.get(position));
				}
			});

			cateGoryTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goBackCategoryList();
				}
			});

			cateGoryListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (currentFormulaList != null
							&& currentFormulaList.size() > 0) {
						goToDetail(currentFormulaList.get(position)
								.getFormula());
					} else {
						// ���ظ�����µ�����
						currentCategory = categoryList.get(position);
						new DBTask().execute(new Integer[] { 2, 1 });
					}
				}
			});

			myFavoriteList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String name = arrFavorTabcalc.get(position);
					Intent intent = new Intent();
					intent.putExtra("name", name);
					intent.setClass(context, TabCalcInfoActivity2.class);
					startActivity(intent);
				}
			});

			String from = getIntent().getStringExtra("from");

			if ("homepage".equals(from))
				viewPager.setCurrentItem(1);
			else {
				// viewPager.setCurrentItem(0);
				setCurrentPoint(0);
			}
		}
	}

	/**
	 * ���ർ���ص���һ��(����ֻ������������Ϊԭʼ״̬����)
	 */
	private void goBackCategoryList() {
		cateGoryListView.setAdapter(new CalculatorListAdapter(categoryList));
		cateGoryTitle.setVisibility(View.GONE);
		currentFormulaList = null;
	}

	private void goToDetail(Formula entity) {
		int colID = app.getColID(getString(R.string.calculator));
		historyAdapter.storeViewHistory(SharedPreferencesMgr.getUserName(),
				entity.getId(), colID, -1, entity.getName());
		MobclickAgent.onEvent(context, "TabCalc", entity.getName(), 1);// "SimpleButtonclick");
		uploadLoginLogNew("Calc", entity.getName(), "TabCalc", null);
		Intent intent = new Intent();
		intent.putExtra("id", entity.getId());
		intent.setClass(context, TabCalcInfoActivity2.class);
		startActivity(intent);
	}

	/***
	 * �л�����ص�
	 */
	@Override
	public void setCurrentPoint(int index) {
		navigateView.changeUI(index);
		initNavigateData(index);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (isMyFavoriteInit == true) {// ����ղ��б���ǰ�Ѿ���ȡ��ˢ��
			new DBTask().execute(new Integer[] { 3 });
		}
	}

	private void initNavigateData(int index) {
		switch (index) {
		case 0:
			if (!isNormalInit)
				new DBTask().execute(new Integer[] { 1 });
			break;
		case 1:
			if (!isCategoryInit)
				new DBTask().execute(new Integer[] { 2, 0 });
			break;
		case 2:
			if (!isMyFavoriteInit)
				new DBTask().execute(new Integer[] { 3 });
			break;
		}
	}

	private class DBTask extends AsyncTask<Integer, Integer, Long> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected Long doInBackground(Integer... params) {
			Log.i("GBA", "doInBackground");
			int length = params.length;
			if (length == 0)
				return null;
			switch (params[0]) {
			case 1: {// ����
				normalListData = daoSession.getFormulaDao().queryDeep(
						" order by T0.fcount desc");
				return 1L;
			}
			case 2: {// ����
				if (params[1] == 0) {// ��ʼ������
					categoryList = daoSession.getFormulaCategoryDao()
							.queryBuilder().list();
					currentCategory = daoSession.getDptRelationDao()
							.getFormulaCategory();
					if (currentCategory != null)
						if (null==currentCategory||null==currentCategory.getId()||currentCategory.getId() >= 12)
							return 2L;
				}
				if (currentCategory != null) {
					currentFormulaList = daoSession
							.getCategoryFormulaMappingDao()
							.queryDeep(
									" where T0.id = ? order by T.forder",
									new String[] { "" + currentCategory.getId() });
					;
				}
				return 2L;
			}
			case 3: {// �ղ�
				arrFavorTabcalc = favadpter.selectTabCalcc(context,
						SharedPreferencesMgr.getUserName());
				return 3L;
			}
			case 4: {
				searchList = daoSession.getFormulaDao().queryBuilder()
						.where(FormulaDao.Properties.Name.like("%"+searchText+"%"))
						.list();
				return 4L;
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
			if (result == null)
				return;
			if (result == 1L) {
				normalList
						.setAdapter(new CalculatorListAdapter(normalListData));
				if (normalList.getFooterViewsCount() > 0)
					normalList.removeFooterView(normolFooterView);
				isNormalInit = true;
			} else if (result == 2L) {
				if (currentFormulaList != null && currentFormulaList.size() > 0) {
					cateGoryListView.setAdapter(new CalculatorListAdapter(
							currentFormulaList));
					cateGoryTitle.setVisibility(View.VISIBLE);
					cateGoryTitle.setText(currentCategory.getName());
				} else {
					cateGoryListView.setAdapter(new CalculatorListAdapter(
							categoryList));
					cateGoryTitle.setVisibility(View.GONE);
				}
				if (cateGoryListView.getFooterViewsCount() > 0)
					cateGoryListView.removeFooterView(cateGoryFooterView);
				isCategoryInit = true;
			} else if (result == 3L) {
				if (arrFavorTabcalc == null || arrFavorTabcalc.size() == 0) {
					favoriteText.setVisibility(View.VISIBLE);
				} else {
					favoriteText.setVisibility(View.GONE);
					myFavoriteList.setAdapter(new ArrayAdapter<String>(context,
							R.layout.list_item_text, arrFavorTabcalc));
					myFavoriteList.setEnabled(true);
				}

				if (myFavoriteList.getFooterViewsCount() > 0)
					myFavoriteList.removeFooterView(myFavoriteFooterView);
				// �ղض�̬����....�ڽ��빫ʽ�����ղأ�������������¡���Ҫ���¼����ղ�
				isMyFavoriteInit = true;
			} else if (result == 4L) {
				searchListView
						.setAdapter(new CalculatorListAdapter(searchList));
				if (searchListView.getFooterViewsCount() > 0)
					searchListView.removeFooterView(searchFooterView);
			}
		}
	}

	class CalculatorListAdapter extends BaseAdapter {
		public List<?> list;

		public CalculatorListAdapter(List<?> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			if (list == null)
				return 0;
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			if (list == null)
				return null;
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (list == null) {
				return null;
			}
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.list_item_group, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView
						.findViewById(R.id.drugalert_title);

				holder.title = (TextView) convertView
						.findViewById(R.id.lst_item);
				convertView.findViewById(R.id.lst_group).setVisibility(
						View.GONE);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(list.get(position).toString());
			return convertView;
		}

		class ViewHolder {
			TextView title;
		}
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		// ע��tip
		mask = new Mask(this, null);
		TipHelper.registerTips(this, 1);
		TipHelper.runSegments(this);
		this.findViewById(R.id.closeTips).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TipHelper.sign(false, true);
				TipHelper.disableTipViewOnScreenVisibility();
			}
		});
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
		historyAdapter.closeDB();
		favadpter.closeDB();
		uploadLoginLogNew("Activity", "Calc", "end", null);
	}

}