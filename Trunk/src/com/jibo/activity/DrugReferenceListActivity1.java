package com.jibo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.adapter.FavoritListAdapter;
import com.jibo.adapter.ViewPageAdapter;
import com.jibo.adapter.ViewPageChangeListener;
import com.jibo.adapter.ViewPageChangeListener.IPageChange;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.Util;
import com.jibo.dao.DrugInfoDao;
import com.jibo.dao.ManufutureBrandInfoDao;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.entity.DrugInfo;
import com.jibo.entity.BrandInfo;
import com.jibo.entity.DptRelation;
import com.jibo.entity.DrugListItem;
import com.jibo.entity.FavoritDrugEntity;
import com.jibo.entity.ManufutureBrandInfo;
import com.jibo.entity.TADrugRef;
import com.jibo.ui.NavigateView;
import com.jibo.ui.NavigateView.GotoBackFirstInit;
import com.jibo.ui.NavigateView.OnChangeListener;
import com.jibo.util.tips.Mask;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;

/**
 * 药品列表类。
 * 
 * @author simon
 */
public class DrugReferenceListActivity1 extends BaseSearchActivity implements
		OnItemClickListener, OnScrollListener, IPageChange {

	private Context context;
	private LayoutInflater inflater;
	private GBApplication app;
	/** 当前环境是否为中文 */
	private boolean isZh;

	/** 主界面标题 */
	private TextView txtCategory;

	/** 导航手势滑动分页 */
	private ViewPager viewPager;

	/** 导航条 */
	public NavigateView navigateView;

	/** 药品列表 */
	private ListView mListView;
	private int pageNumber;
	private View footerView;
	/** 常用药品列表适配器 */
	private DrugListAdapter drugListAdapter;
	/** 常用列表初始化是否成功 */
	private boolean isCommonLoadSuccess;

	/** 分类列表 */
	private ListView mListView1;
	private View footerView1;
	/** 分类中处于列表之上分类名称 */
	private TextView chooseCategoryName;
	/** 多级分类菜单存储栈 */
	private Stack<TADrugRef> categoryStack;
	/** 分类信息适配器 */
	private ArrayAdapter<TADrugRef> categoryAdapter;
	/** 分类下药品列表适配器 */
	private DrugListAdapter categoryDrugListAdapter;
	/** 分类列表初始化是否成功 */
	private boolean isCategoryLoadSuccess;

	/** 收藏列表 */
	private ListView mListView2;
	// private int pageNumber2;
	private View footerView2;
	/** 无收藏时提示信息 */
	private TextView favoritNoTV;
	private FavoritListAdapter listAdapter;
	private FavoritDataAdapter favadpter;

	/** 搜索关键字 */
	private String str_keyword;

	/** db工具 */
	private HistoryAdapter historyAdapter;

	/** 工作线程 */
	private DrugListTask curTask;
	
	private Mask mask = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drugreference_main);
		super.onCreate(savedInstanceState);
		app = (GBApplication) getApplication();
		MobclickAgent.onError(this);
		context = this;
		isZh = Util.isZh();

		// 是否为搜索页
		str_keyword = getIntent().getStringExtra("search_text");

		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");

		initUI();

		categoryStack = new Stack<TADrugRef>();
		categoryDrugListAdapter = new DrugListAdapter();

		boolean flag = true;
		// 取消导航条
		if (str_keyword != null) {
			// 搜索状态下，调整布局
			navigateView.setVisibility(View.GONE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewPager
					.getLayoutParams();
			params.topMargin = 0;
			viewPager.setLayoutParams(params);
			viewPager.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
		} else {
			// 用户注册科室时直接进入该科室
			String departMent = getIntent().getStringExtra("departMent");
			if (!TextUtils.isEmpty(departMent)) {
				DptRelation dptObj = daoSession.getDptRelationDao().load(
						departMent);
				if (null != dptObj) {
					if (!TextUtils.isEmpty(dptObj.getDrugCategoryId())) {
						TADrugRef entity = daoSession.gettADrugRefDao().load(
								dptObj.getDrugCategoryId());
						if (null != entity) {
							categoryStack.push(entity);
							chooseCategoryName.setVisibility(View.VISIBLE);
							chooseCategoryName.setText(isZh ? entity
									.getNameCn() : entity.getNameEn());
							viewPager.setCurrentItem(1);
							flag = false;
						}
					}
				}
			}
		}

		if (flag) {// 没有科室对应的相关数据，加载常用列表
			setCurrentPoint(0);
			initNavigateData(1);
		}

		setPopupWindowType(Constant.MENU_TYPE_3);
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		txtCategory = (TextView) findViewById(R.id.txt_header_title);
		txtCategory.setText(R.string.drug_reference);

		viewPager = (ViewPager) findViewById(R.id.pagerGroup);

		navigateView = (NavigateView) findViewById(R.id.navigateView);
		navigateView.setOnChangeListener(new OnChangeListener() {
			@Override
			public void onChange(int type) {
				viewPager.setCurrentItem(type);
			}
		});

		navigateView.setGotoBackListener(new GotoBackFirstInit() {

			@Override
			public void gotoBack(int type) {
				if (type == NavigateView.CATEGORY_TYPE)
					backToPreviousCategory();
			}
		});

		inflater = LayoutInflater.from(this);

		/** 常用 */
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.customize_listview, null);
		mListView = (ListView) layout.findViewById(R.id.lst_item);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		footerView = View.inflate(this, R.layout.dialogprogress, null);
		footerView.setClickable(false);
		mListView.addFooterView(footerView);
		/** 分类 */
		LinearLayout layout1 = (LinearLayout) inflater.inflate(
				R.layout.item_category_listview, null);
		chooseCategoryName = (TextView) layout1
				.findViewById(R.id.chooseCategory);
		chooseCategoryName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backToPreviousCategory();
			}
		});
		mListView1 = (ListView) layout1.findViewById(R.id.lst_item);
		mListView1.setOnItemClickListener(this);
		footerView1 = View.inflate(this, R.layout.dialogprogress, null);
		footerView1.setClickable(false);
		mListView1.addFooterView(footerView1);
		/***
		 * 分类加载取消分页显示
		 */
		// mListView1.setOnScrollListener(this);

		/** 收藏 */
		RelativeLayout layout2 = (RelativeLayout) inflater.inflate(
				R.layout.favo_listview, null);
		mListView2 = (ListView) layout2.findViewById(R.id.lst_item);
		mListView2.setOnItemClickListener(this);
		mListView2.setOnScrollListener(this);
		favoritNoTV = (TextView) layout2.findViewById(R.id.favorite_text);

		List<View> pageListView = new ArrayList<View>();
		pageListView.add(layout);
		pageListView.add(layout1);
		pageListView.add(layout2);

		// 初始化viewPager
		viewPager.setAdapter(new ViewPageAdapter(pageListView));
		viewPager.setOnPageChangeListener(new ViewPageChangeListener(this));

		footerView2 = View.inflate(this, R.layout.dialogprogress, null);
		footerView2.setClickable(false);
		mListView2.addFooterView(footerView2);

		favadpter = new FavoritDataAdapter(this);
	}

	/**
	 * 返回上级分类
	 * 
	 */
	private boolean backToPreviousCategory() {
		if (categoryStack.empty())
			return false;
		if (mListView1.getFooterViewsCount() > 0) {
			mListView1.removeFooterView(footerView1);
		}
		categoryStack.pop();
		TADrugRef entity = null;
		if (categoryStack.empty()) {// 返回到顶层
			chooseCategoryName.setVisibility(View.GONE);
			mListView1.setAdapter(getCategoryAdapter(null));
		} else {
			entity = categoryStack.peek();
			chooseCategoryName.setVisibility(View.VISIBLE);
			chooseCategoryName.setText(isZh ? entity.getNameCn() : entity
					.getNameEn());
			mListView1.setAdapter(getCategoryAdapter(entity));
		}
		return true;
	}

	/**
	 * 获取分类适配器
	 * 
	 * @param entity
	 * @return
	 */
	private ArrayAdapter<TADrugRef> getCategoryAdapter(TADrugRef entity) {
		categoryAdapter = new ArrayAdapter<TADrugRef>(context,
				R.layout.list_item_text, daoSession.gettADrugRefDao()
						.getTADrugDefList(entity));
		return categoryAdapter;
	}

	@Override
	protected void treatMenuClick(int typeId) {
		if (typeId == Constant.FLAG_MY_JIBO) {
			Intent in = new Intent(this, MyFavoriteListActivity.class);// Util.myFavCategory
																		// =
																		// 1;//医学公式
			Constant.myFavCategory = 0;// event
			startActivity(in);
		} else
			super.treatMenuClick(typeId);
	}

	/**
	 * 初始化导航下数据
	 * 
	 * @param type
	 */
	private void initNavigateData(int type) {
		switch (type) {
		case 0:
			if (isCommonLoadSuccess)
				return;
			drugListAdapter = new DrugListAdapter();
			mListView.setAdapter(drugListAdapter);
			startTask(ReqType.getCommonDrugList, str_keyword);
			break;
		case 1:
			if (isCategoryLoadSuccess)
				return;
			TADrugRef currentType = null;
			// 用户科室有对应的药品类别
			if (!categoryStack.isEmpty()) {
				currentType = categoryStack.peek();
			}
			mListView1.setAdapter(getCategoryAdapter(currentType));
			mListView1.removeFooterView(footerView1);
			isCategoryLoadSuccess = true;
			break;
		case 2:
			showFavorList();
			if (mListView2 == null || listAdapter.getCount() == 0) {
				favoritNoTV.setVisibility(View.VISIBLE);
				mListView2.setVisibility(View.GONE);
			} else {
				favoritNoTV.setVisibility(View.GONE);
				mListView2.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	/**
	 * 显示收藏的药品列表
	 * */
	private void showFavorList() {
		ArrayList<FavoritDrugEntity> favorList = favadpter.selectTabDrugRef(
				context, SharedPreferencesMgr.getUserName());
		listAdapter = new FavoritListAdapter(this, daoSession,favorList);
		mListView2.setAdapter(listAdapter);
		if (mListView2.getFooterViewsCount()> 0)
			mListView2.removeFooterView(footerView2);
		
	}

	/***
	 * 切换界面回调
	 */
	@Override
	public void setCurrentPoint(int index) {
		navigateView.changeUI(index);
		initNavigateData(index);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (lock
				&& (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE || scrollState == ListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
				&& view.getLastVisiblePosition() == view.getCount() - 1) {

			switch (viewPager.getCurrentItem()) {
			case 0:
				if (mListView.getFooterViewsCount() <= 0) {
					mListView.addFooterView(footerView);
					mListView.setSelection(view.getCount());
				}

				startTask(ReqType.getCommonDrugListMore, pageNumber + "",
						str_keyword);
				lock = false;
				break;
			// case 1:
			// if (lock1) {
			// if (mListView1.getFooterViewsCount() == 0) {
			// mListView1.addFooterView(footerView1);
			// mListView1.setSelection(view.getCount());
			// }
			// new DrugListTask(DrugListTask.getTADrugListMore, this,
			// new TaskCallBack(),
			// daoSession).execute(categoryStack.peek().getId());
			// lock1 = false;
			// }
			// break;
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// Log.i("simon",
		// firstVisibleItem+">>>"+visibleItemCount+">>>>"+totalItemCount);
		// if (firstVisibleItem + visibleItemCount == drugListAdapter.getCount()
		// && totalItemCount > 0) {
		// Log.i("simon",
		// firstVisibleItem+">>>"+visibleItemCount+">>>>"+totalItemCount);
		// isLastRow = true;
		// }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isPopflg()) {
				System.out.println("==== DrugReferenceListActivity  返回键1 ====");
				dissMissPop();
				return true;
			}
			// ，如果在分类菜单下，需要回退到上层分类
			if (navigateView.getCurrentType() != NavigateView.CATEGORY_TYPE
					|| !backToPreviousCategory()) {
				finish();
			}
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (viewPager.getCurrentItem()) {
		case 0:
			if (position >= drugListAdapter.getCount())
				return;
			DrugListItem itemInfo = drugListAdapter.getItem(position);
			if (itemInfo.isDrug) {// 药品
				gotoDrugDetail(itemInfo.drugInfo);
			} else {// 联系厂商
				saveHistory(itemInfo.brandInfo);
				Intent intent = new Intent(context,
						NewDrugReferenceActivity.class);
				intent.putExtra("mode", NewDrugReferenceActivity.MODE_BRAND);
				intent.putExtra("MAINPAGE", true);
				intent.putExtra("brandId", itemInfo.brandInfo.getBrandId());
				startActivity(intent);
			}
			break;
		case 1:
			if (categoryStack.isEmpty()) {// 一级分类列表，点击获取三级分类列表
				TADrugRef entity = categoryAdapter.getItem(position);
				mListView1.setAdapter(getCategoryAdapter(entity));
				pushCategory(entity);
			} else if (categoryStack.peek().getLevel().equals("1")) {// 当前处在三级分类列表(**列表内容为三级分类，title为一级分类**)，点击获取四级分类列表和分类下的药品信息
				String ta1ID = categoryStack.peek().getId();
				TADrugRef entity = categoryAdapter.getItem(position);
				pushCategory(entity);
				//三级分类id
				String ta3ID = entity.getId();
				categoryDrugListAdapter.clearList();
				mListView1.addFooterView(footerView1);
				mListView1.setAdapter(categoryDrugListAdapter);
				startTask(ReqType.getTADrugList, ta1ID,ta3ID);
			} else {// 分类下的药品列表,点击跳转
				if (position >= categoryDrugListAdapter.getCount())
					return;
				gotoDrugDetail(categoryDrugListAdapter.getItem(position).drugInfo);
			}
			break;
		case 2:
			Intent intent = new Intent(this, NewDrugReferenceActivity.class);

			FavoritDrugEntity item = listAdapter.getItem(position);

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
			break;
		}
	}

	/***
	 * 跳转到药品详情页
	 * 
	 * @param drugInfo
	 */
	private void gotoDrugDetail(DrugInfo drugInfo) {
		saveHistory(drugInfo);
		Intent intent = new Intent(context, NewDrugReferenceActivity.class);
		intent.putExtra("mode", NewDrugReferenceActivity.MODE_NORMAL);
		intent.putExtra("MAINPAGE", true);
		intent.putExtra("drugInfo", drugInfo);
		startActivity(intent);
	}

	/**
	 * 保存到历史记录
	 * 
	 * @param drugInfo
	 *            药品信息
	 */
	private void saveHistory(DrugInfo drugInfo) {
		int drugid = Integer.parseInt(drugInfo.getId());
		// C0表示当前为普通药品，进入详细页默认的启动MODE为0 (NewDrugReferenceActivity.MODE_NORMAL)
		String drugName = "C0-"
				+ (isZh ? drugInfo.getNameCn() : drugInfo.getNameEn());
		int colID = app.getColID(getString(R.string.drug));
		// 给药途径
		String adminRouteId = null;
		String adminRouteContent = null;

		if (null != drugInfo.getAdminRouteInfo()) {
			adminRouteId = drugInfo.getAdminRouteInfo().getId();
			adminRouteContent = isZh ? drugInfo.getAdminRouteInfo().getNameCn()
					: drugInfo.getAdminRouteInfo().getNameEn();
		}

		historyAdapter.storeViewHistory(app.getLogin().getGbUserName(), drugid,
				colID, -1, drugName, adminRouteId, adminRouteContent);
	}

	/**
	 * 保存到历史记录
	 * 
	 * @param brandInfo
	 *            厂商信息
	 */
	private void saveHistory(ManufutureBrandInfo brandInfo) {
		int brandId = Integer.parseInt(brandInfo.getBrandId());
		// C2表示当前为厂商信息，进入详细页默认的启动MODE为2 (NewDrugReferenceActivity.MODE_BRAND)
		String brandName = "C2-"
				+ (isZh ? brandInfo.getGeneralName() : brandInfo.getEnName());
		int colID = app.getColID(getString(R.string.drug));

		historyAdapter.storeViewHistory(app.getLogin().getGbUserName(),
				brandId, colID, -1, brandName);
	}

	/**
	 * 分类信息压栈
	 * 
	 * @param entity
	 */
	private void pushCategory(TADrugRef entity) {
		chooseCategoryName.setVisibility(View.VISIBLE);
		chooseCategoryName.setText(isZh ? entity.getNameCn() : entity
				.getNameEn());
		categoryStack.push(entity);
	}

	/***
	 * 启动工作线程加载数据
	 * 
	 * @param type
	 * @param param
	 */
	private void startTask(ReqType type, String... param) {
		endTask();
		curTask = new DrugListTask(type);
		curTask.execute(param);
	}

	/***
	 * 取消工作线程
	 */
	private void endTask() {
		if (curTask != null && !curTask.isCancelled())
			curTask.cancel(true);
	}

	/** 常用药品列表，加载更多Lock */
	boolean lock;

	/***
	 * 请求数据类型
	 * 
	 * @author simon
	 * 
	 */
	private enum ReqType {
		/** 常用药品列表或搜索列表首次加载 */
		getCommonDrugList,
		/** 常用药品列表或搜索列表加载更多 */
		getCommonDrugListMore,
		/** 分类药品列表加载，获取第四级分类以及分类下的药品(第三级分类TA3ID) */
		getTADrugList;
	}

	/**
	 * db加载
	 * @author simon
	 *
	 */
	class DrugListTask extends AsyncTask<String, Integer, Object> {

		/** 请求数据类型*/
		private ReqType type;

		public DrugListTask(ReqType type) {
			this.type = type;
		}

		@Override
		protected Object doInBackground(String... params) {

			try {
				List<DrugInfo> drugList = null;
				String searchKey = null;
				int pageNumber = 0;

				switch (type) {
				case getCommonDrugList:
					if (params != null && params.length > 0) {
						searchKey = params[0];
					}
					drugList = daoSession.getDrugInfoDao().queryDeep(0,
							searchKey);

					// 第一次加载查询列表情况下，检索出所有匹配的联系厂商的数据，并置顶
					List<ManufutureBrandInfo> brandInfoList = null;
					if (!TextUtils.isEmpty(searchKey)) {
						searchKey = "%" + searchKey + "%";
						brandInfoList = daoSession
								.getManufutureBrandInfoDao()
								.queryBuilder()
								.whereOr(
										ManufutureBrandInfoDao.Properties.GeneralName
												.like(searchKey),
										ManufutureBrandInfoDao.Properties.ZyName
												.like(searchKey),
										ManufutureBrandInfoDao.Properties.EnName
												.like(searchKey),
										ManufutureBrandInfoDao.Properties.PyName
												.like(searchKey),
										ManufutureBrandInfoDao.Properties.BrandName
												.like(searchKey),
										ManufutureBrandInfoDao.Properties.BrandNameEn
												.like(searchKey)).list();
					}
					return mergeList(drugList, brandInfoList);
				case getCommonDrugListMore:
					if (params != null && params.length > 1) {
						pageNumber = Integer.parseInt(params[0]);
						searchKey = params[1];
					}
					drugList = daoSession.getDrugInfoDao().queryDeep(
							pageNumber, searchKey);
					return mergeList(drugList);
				case getTADrugList:
					String ta1id = null;
					String ta3id = null;
					if (params != null && params.length > 1) {
						ta1id = params[0];
						ta3id = params[1];
					}

					drugList = daoSession.getDrugInfoDao().queryDeepByTa(ta1id,ta3id);

					return mergeList(drugList, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return e;
			}
			return null;
		}

		/**
		 * 合并数据，方便listView显示
		 * 
		 * @param drugList
		 *            药品列表
		 * @param brandInfoList
		 *            厂商信息列表
		 * @return
		 */
		private List<DrugListItem> mergeList(List<DrugInfo> drugList,
				List<ManufutureBrandInfo> brandInfoList) {

			List<DrugListItem> list = new ArrayList<DrugListItem>();
			// 厂商数据
			if (brandInfoList != null && brandInfoList.size() > 0) {
				for (ManufutureBrandInfo obj : brandInfoList) {
					list.add(new DrugListItem(obj, null, false));
				}
			}
			// 药品数据
			if (drugList != null && drugList.size() > 0) {
				for (DrugInfo obj : drugList) {
					list.add(new DrugListItem(null, obj, true));
				}
			}
			return list;
		}

		private List<DrugListItem> mergeList(List<DrugInfo> drugList) {
			return mergeList(drugList, null);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Object result) {
			Log.i("GBA", "onPostExecute");
			if (result instanceof Exception) {
				onDBFailed((Exception) result, type);
				return;
			}
			onDBSuccess(result, type);
		}
	}

	/**
	 * 查询本地数据成功回调
	 * @param o
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	public void onDBSuccess(Object o, ReqType type) {
		List<DrugListItem> temp = null;
		if (o != null) {
			temp = (List<DrugListItem>) o;
			boolean isLastData = temp.size() < DrugInfoDao.DB_ROW_COUNT;
			switch (type) {
			case getCommonDrugList:
				drugListAdapter.setListData(temp);
				pageNumber++;
				isCommonLoadSuccess = true;
				mListView.removeFooterView(footerView);
				if (isLastData)
					lock = false;
				else {
					lock = true;
				}
				break;
			case getCommonDrugListMore:
				drugListAdapter.addToList(temp);
				pageNumber++;
				mListView.removeFooterView(footerView);
				if (isLastData)
					lock = false;
				else
					lock = true;
				break;
			case getTADrugList:
				categoryDrugListAdapter.setListData(temp);
				mListView1.removeFooterView(footerView1);
				break;
			default:
				break;
			}
		} else {
			switch (type) {
			case getCommonDrugList:
			case getCommonDrugListMore:
				mListView.removeFooterView(footerView);
				lock = false;
				break;
			case getTADrugList:
				mListView1.removeFooterView(footerView1);
				break;
			default:
				break;
			}
		}
	}
	/**
	 * 查询本地数据失败回调
	 * @param o
	 * @param type
	 */
	public void onDBFailed(Exception e, ReqType type) {
		switch (type) {
		case getCommonDrugList:
		case getCommonDrugListMore:
			mListView.removeFooterView(footerView);
			lock = true;
			break;
		case getTADrugList:
			mListView1.removeFooterView(footerView1);
			break;
		default:
			break;
		}
		showDialog(DialogRes.DIALOG_ERROR_PROMPT);
	}

	/***
	 * 常用药品及搜索药品列表adapter
	 * 
	 * @author simon
	 * 
	 */
	private class DrugListAdapter extends BaseAdapter {

		private List<DrugListItem> dataList;
		/** 上条记录的分类id */
		private String lastCateGroupId;
		
		private ImageSpan span;

		private DrugListAdapter() {
			dataList = new ArrayList<DrugListItem>();
			isZh = Util.isZh();
			Drawable drawable = context.getResources().getDrawable(
					R.drawable.ahfs1);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			span = new ImageSpan(drawable,
					ImageSpan.ALIGN_BASELINE);
		}

		@Override
		public int getCount() {
			if (dataList != null)
				return dataList.size();
			return 0;
		}

		@Override
		public DrugListItem getItem(int position) {
			if (dataList != null)
				return dataList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * 初始化adapter的data
		 * @param list
		 */
		public void setListData(List<DrugListItem> list) {
			dataList = list;
			notifyDataSetChanged();
		}

		/***
		 * 加载更多，添加data
		 * @param list
		 */
		public void addToList(List<DrugListItem> list) {
			if (dataList != null)
				dataList.addAll(list);
			notifyDataSetChanged();
		}
		/**
		 * 清空data
		 */
		public void clearList() {
			if (dataList != null) {
				dataList.clear();
				dataList = null;
			}
			notifyDataSetChanged();
		}

		/**
		 * 
		 * 显示信息包括三种情况： <一>搜索情况下：联系厂商信息(置顶列表)+药品信息 <二> 通常情况下：药品信息
		 * <三>分类菜单下：类别信息(第四级分类，置顶该分类group)+药品信息
		 * 
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHold holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.druglst_info, null);
				holder = new ViewHold();
				holder.cateGroupLay = (LinearLayout) convertView
						.findViewById(R.id.category_group_lay);
				holder.cateGroup = (TextView) convertView
						.findViewById(R.id.category_group);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.tx1 = (TextView) convertView
						.findViewById(R.id.ListText1);
				holder.tx2 = (TextView) convertView
						.findViewById(R.id.ListText2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHold) convertView.getTag();
			}

			if (dataList.get(position).isDrug) {// 药品信息
				DrugInfo info = dataList.get(position).drugInfo;
				// daoSession发生改变时需要change实体内部的daoSession
				info.__setDaoSession(daoSession);

				// 获得上个分类的类别id
				if ((position > 0 ? dataList.get(position - 1).isDrug ? dataList
						.get(position - 1).drugInfo.getTaDrugRef() : null
						: null) != null) {
					lastCateGroupId = dataList.get(position - 1).drugInfo
							.getTaDrugRef().getId();
				} else {
					lastCateGroupId = "";
				}

				// Log.i("simon", "上个groupId>>>>>>>>" + lastCateGroupId);

				if (info.getTaDrugRef() != null) {
					// 不同于上个分类，显示分类信息
					if (!info.getTaDrugRef().getId().equals(lastCateGroupId)) {
						holder.cateGroupLay.setVisibility(View.VISIBLE);
						// Log.i("simon", "上个groupId>>>>>>>>" + lastCateGroupId
						// + ">>>>当前groupId>>>>>>"
						// + info.getTaDrugRef().getId());

						holder.cateGroup.setText(isZh ? info.getTaDrugRef()
								.getNameCn() : info.getTaDrugRef().getNameEn());
					} else {
						holder.cateGroupLay.setVisibility(View.GONE);
					}
				} else {
					holder.cateGroupLay.setVisibility(View.GONE);
				}

				holder.img.setVisibility(View.GONE);
				holder.tx1.setText(getSpannable(info));
				holder.tx2.setText(getBrandInfoString(info.getBrandInfoList()));
			} else {// 联系厂商信息
				holder.img.setVisibility(View.VISIBLE);
				holder.cateGroupLay.setVisibility(View.GONE);
				holder.tx1.setText(isZh ? dataList.get(position).brandInfo
						.getGeneralName() : dataList.get(position).brandInfo
						.getEnName());
				holder.tx2.setText(isZh ? dataList.get(position).brandInfo
						.getBrandName() : dataList.get(position).brandInfo
						.getBrandNameEn());
			}

			return convertView;
		}

		/***
		 * 品牌名
		 * 
		 * @param list
		 * @return
		 */
		private String getBrandInfoString(List<BrandInfo> list) {
			if (null == list || list.size() == 0)
				return "";
			StringBuilder builder = new StringBuilder();
			List<String> brandList = new ArrayList<String>();
			for (BrandInfo obj : list) {
				String name = isZh ? obj.getNameCn() : obj.getNameEn();
				if (null != name && !brandList.contains(name)) {
					builder.append(name);
					builder.append(",");
					brandList.add(name);
				}
			}
			String text = builder.toString();
			if (TextUtils.isEmpty(text))
				return "";
			text = text.substring(0, text.length() - 1);
			return text;
		}

		/***
		 * 
		 * 药品显示的信息
		 * 
		 * 药品名+给药类型+是否含AHFS数据标识
		 * 
		 * @param info
		 * @return
		 */
		private SpannableString getSpannable(DrugInfo info) {
			String splitString = "   ";
			StringBuilder builder = new StringBuilder(isZh ? info.getNameCn()
					: info.getNameEn());
			String adminRouteName = null != info.getAdminRouteInfo() ? isZh ? info
					.getAdminRouteInfo().getNameCn() : info.getAdminRouteInfo()
					.getNameEn()
					: "";
			boolean isShowAdminRoute = false;
			boolean isShowAHFS = false;

			// 检测是否包含给药类型的数据,并且数量不只1个
			if (info.getCount() > 1 && !TextUtils.isEmpty(adminRouteName)) {
//				adminRouteName = "[" + adminRouteName + "]";
				builder.append(splitString);
				builder.append(adminRouteName);
				isShowAdminRoute = true;
			}
			// 检测是否包含AHFS相关数据
			if (info.getAhfsInfo().equals("Y")) {
				builder.append(splitString);
				builder.append("temp");
				isShowAHFS = true;
			}

			String text = builder.toString();
			SpannableString spannable = new SpannableString(text);

			if (isShowAdminRoute) {
				ForegroundColorSpan fgSpan = new ForegroundColorSpan(context
						.getResources().getColor(R.color.gray));
				spannable.setSpan(fgSpan, text.indexOf(splitString)
						+ splitString.length(), text.indexOf(splitString)
						+ splitString.length() + adminRouteName.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			if (isShowAHFS) {
				spannable.setSpan(span, text.lastIndexOf(splitString)
						+ splitString.length(), text.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
			return spannable;
		}

		class ViewHold {
			/** 分类信息layout */
			LinearLayout cateGroupLay;
			/** 分类信息 */
			TextView cateGroup;
			/** 联系厂商信息的标识 */
			ImageView img;
			/** 药品信息 */
			TextView tx1;
			/** 药品对应的商品信息 */
			TextView tx2;
		}
	}
	
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (viewPager.getCurrentItem() == 2) {
			initNavigateData(2);
		}
		
		// 注册tip
		mask = (Mask) findViewById(R.id.mask);
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
	}

}
