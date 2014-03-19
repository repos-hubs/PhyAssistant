package com.jibo.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.adapter.DrugReferenceListAdapter;
import com.jibo.adapter.ViewPageAdapter;
import com.jibo.adapter.ViewPageChangeListener;
import com.jibo.adapter.ViewPageChangeListener.IPageChange;
import com.jibo.app.interact.InteractIndex;
import com.jibo.common.Constant;
import com.jibo.common.DeviceInfo;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.Util;
import com.jibo.dao.ManufutureBrandInfoDao;
import com.jibo.data.entity.AshfEntity;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.entity.AdminRouteInfo;
import com.jibo.entity.BrandInfo;
import com.jibo.entity.DrugInfo;
import com.jibo.entity.ManufutureBrandInfo;
import com.jibo.ui.ExpandLayout;
import com.jibo.ui.NavigateView;
import com.jibo.ui.NavigateView.OnChangeListener;
import com.jibo.util.tips.Mask;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

/**
 * 药品详情类,目前改为普通,ashf,品牌,改为列表显示方式，点击跳转。
 * 
 * @author peter.pan
 */
public class NewDrugReferenceActivity extends BaseSearchActivity implements
		OnTouchListener, OnGestureListener, OnItemClickListener,
		OnClickListener, IPageChange {

	private Context context;

	/** 页面标题 */
	private TextView txt;

	/** 当前应用是否为中文环境 */
	private boolean isZh;

	/** 普通说明书布局 */
	// private LinearLayout viewNormal;
	/** 普通说明书布局头 */
	private LinearLayout viewNormalHeader;
	/** 普通说明书详细页选项列表 */
	private ListView listView1;
	/** 普通说明书详细页选项列表适配器 */
	private DrugReferenceListAdapter listAdapter1;

	/** 美国药师协会说明书布局 */
	// private LinearLayout viewAshf;
	/** 美国药师协会说明书布局头 */
	private LinearLayout viewAshfHeader;
	/** 美国药师协会详细页选项列表 */
	private ListView listView2;
	/** 美国药师协会详细页选项列表适配器 */
	private DrugReferenceListAdapter listAdapter2;
	/** AHFS数据更新时间 */
	private TextView txUpdate;
	/** AHFS商品名 */
	private ExpandLayout ashfBrand;
	/** AHFS数据(数据来源于网络，默认读缓存数据包) */
	private ArrayList<AshfEntity> ashfDatas;
	/** 当前viewpage是否包含美国药师协会页 */
	private boolean hasASHF;

	/** 厂商说明书布局 */
	// private LinearLayout viewbrand;
	/** 厂商说明书布局头 */
	private LinearLayout viewbrandHeader;
	/** 厂商说明书详细页选项列表 */
	private ListView listView3;
	/** 厂商说明书详细页选项列表适配器 */
	private DrugReferenceListAdapter listAdapter3;

	/** 滑动分页容器 */
	private ViewPager viewPage;

	/** 导航条 */
	private NavigateView navigateView;

	/** 滑动手势 */
	private GestureDetector mGestureDetector;

	/***
	 * 底部菜单布局
	 */
	private LinearLayout btnLayout1;
	private LinearLayout btnLayout2;
	private LinearLayout btnLayout3;
	private LinearLayout btnToggleLayout3;
	@SuppressWarnings("unused")
	private LinearLayout btnLayout4;
	/***
	 * 底部菜单按钮
	 */
	private Button btn1;
	private ToggleButton btn2;
	private Button btn3;
	private ToggleButton btn3toggle;
	private Button btn4;

	/** 药品基本信息数据 */
	private DrugInfo drugInfo;
	/** 联系厂商数据 */
	private ManufutureBrandInfo brandEntity;

	/** 普通说明书页面是否初始化 */
	private boolean isLoadNomarlSuccess;
	/** 美国药师协会页面是否初始化 */
	private boolean isLoadAhfsSuccess;
	/** 厂商说明书页面是否初始化 */
	private boolean isLoadBrandSuccess;
	/** 多个商品名分割符 */
	private final String brandSplit = " ";

	/** 说明书类型，普通，ASHF，品牌 */
	public static final int MODE_NORMAL = 0;
	public static final int MODE_ASHF = 1;
	public static final int MODE_BRAND = 2;

	/** 当前分页对应的说明书类型 */
	private int currentMode;

	/** 当前页面启动类型，两种：普通说明书类型，厂商说明书类型.此类型为了收藏使用 */
	private int startMode;

	/** 请求code */
	private final int ASHF = 0x0000f;

	/** db工具 */
	private FavoritDataAdapter favoritadpter;

	/** 厂商LOGO */
	private Bitmap brandBitmap = null;

	@SuppressWarnings("unused")
	private Mask mask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_drug_reference);
		super.onCreate(savedInstanceState);
		context = this;
		isZh = Util.isZh();

		Intent mIntent = getIntent();

		favoritadpter = new FavoritDataAdapter(this);

		int mode = mIntent.getIntExtra("mode", 0);
		startMode = mode;
		switch (startMode) {
		case MODE_NORMAL:// 普通方式启动
			drugInfo = (DrugInfo) mIntent.getSerializableExtra("drugInfo");
			// 从收藏历史进入
			if (drugInfo == null) {
				String drugId = mIntent.getStringExtra("drugId");
				String adminRouteId = mIntent.getStringExtra("adminRouteId");
				drugInfo = daoSession.getDrugInfoDao().queryDeep(drugId,
						adminRouteId);
			}
			drugInfo.__setDaoSession(daoSession);

			List<ManufutureBrandInfo> list = daoSession
					.getManufutureBrandInfoDao()
					.queryBuilder()
					.where(ManufutureBrandInfoDao.Properties.ProductId
							.eq(drugInfo.getId())).list();

			if (null != list && list.size() > 0) {
				brandEntity = list.get(0);
				if (brandEntity != null) {
					brandEntity.paserJson();
					Util.setPaserObj(brandEntity);
				}
			}
			break;
		case MODE_BRAND:// 厂商说明书方式启动(ps:例如搜索列表的厂商数据或者联系厂商模块的查看说明书)
			String brandId = mIntent.getStringExtra("brandId");
			if (brandId != null) {
				brandEntity = Util.getPaserObj(brandId);
				if (brandEntity == null) {
					brandEntity = daoSession.getManufutureBrandInfoDao().load(
							brandId);
					brandEntity.paserJson();
					Util.setPaserObj(brandEntity);
				}
				drugInfo = daoSession.getDrugInfoDao().queryDeep(
						brandEntity.getProductId(), null);
			}
			break;
		}

		init();

		/* 翻页 */
		viewPage = (ViewPager) findViewById(R.id.pagerGroup);
		List<View> pageListView = new ArrayList<View>();
		pageListView.add(listView1);

		/** 是否有AHFS说明书 */
		if (drugInfo.getAhfsDrugInfoList() != null
				&& drugInfo.getAhfsDrugInfoList().size() > 0) {
			pageListView.add(listView2);
			hasASHF = true;
		} else {
			navigateView.setEnabled(NavigateView.CATEGORY_TYPE, false);
		}

		/** 是否 有厂商说明书 */
		if (null != brandEntity) {
			pageListView.add(listView3);
		} else {
			navigateView.setEnabled(NavigateView.OTHER_TYPE, false);
		}

		viewPage.setAdapter(new ViewPageAdapter(pageListView));
		viewPage.setOnPageChangeListener(new ViewPageChangeListener(this));

		// viewPage.setOnTouchListener(this);
		viewPage.setLongClickable(true);
		viewPage.setClickable(true);

		if (mode == MODE_BRAND) {
			viewPage.setCurrentItem(2);
		} else {
			setCurrentPoint(0);
		}

		MobclickAgent.onError(this);
		MobclickAgent.onEvent(this, "drugsId", drugInfo.getId(), 1);// "SimpleButtonclick");

		uploadLoginLogNew("Drug", drugInfo.getId(), "DrugsId", null);
	}

	/** 初始化UI */
	private void init() {
		txt = (TextView) findViewById(R.id.txt_header_title);
		txt.setText(R.string.drug_reference);

		mGestureDetector = new GestureDetector(this);

		LayoutInflater inflater = LayoutInflater.from(this);
		Resources res = getResources();

		/** 导航条 */
		navigateView = (NavigateView) findViewById(R.id.navigateView);
		navigateView.setText(NavigateView.NORMAL_TYPE, R.string.generic_cn);
		navigateView.setText(NavigateView.CATEGORY_TYPE, R.string.generic_us);
		navigateView.setText(NavigateView.OTHER_TYPE, R.string.brand);
		navigateView.setOnChangeListener(new OnChangeListener() {
			@Override
			public void onChange(int type) {
				viewPage.setCurrentItem(type);
			}
		});

		/** 普通说明书 */
		// viewNormal = (LinearLayout) inflater.inflate(
		// R.layout.customize_listview, null);
		viewNormalHeader = (LinearLayout) inflater.inflate(
				R.layout.drug_reference_normal, null);
		listView1 = (ListView) inflater.inflate(R.layout.drug_detail_listview,
				null);
		listView1.addHeaderView(viewNormalHeader);
		listView1.setOnItemClickListener(this);
		String ss[] = res.getStringArray(R.array.drug_list);
		listAdapter1 = new DrugReferenceListAdapter(this);
		listAdapter1.setListData(ss);
		listView1.setAdapter(listAdapter1);
		// Util.setListViewHeightBasedOnChildren(listView1);

		/** 美国药师协会 */
		// viewAshf = (LinearLayout) inflater.inflate(
		// R.layout.customize_listview, null);
		viewAshfHeader = (LinearLayout) inflater.inflate(
				R.layout.drug_reference_ashf, null);
		listView2 = (ListView) inflater.inflate(R.layout.drug_detail_listview,
				null);
		listView2.addHeaderView(viewAshfHeader);
		listView2.setOnItemClickListener(this);
		ss = res.getStringArray(R.array.ahfs_list);
		listAdapter2 = new DrugReferenceListAdapter(this);
		listAdapter2.setListData(ss);
		listView2.setAdapter(listAdapter2);
		// Util.setListViewHeightBasedOnChildren(listView2);

		/** 厂方说明书 */
		// viewbrand = (LinearLayout) inflater.inflate(
		// R.layout.customize_listview, null);
		viewbrandHeader = (LinearLayout) inflater.inflate(
				R.layout.drug_reference_brand, null);

		listView3 = (ListView) inflater.inflate(R.layout.drug_detail_listview,
				null);
		listView3.addHeaderView(viewbrandHeader);
		listView3.setOnItemClickListener(this);
		ss = res.getStringArray(R.array.brand_list);
		listAdapter3 = new DrugReferenceListAdapter(this);
		listAdapter3.setListData(ss);
		listView3.setAdapter(listAdapter3);
		// Util.setListViewHeightBasedOnChildren(listView3);

		// menuLayout = (LinearLayout) findViewById(R.id.counterfeitContent);
		// int w = View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED);
		// int h = View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED);
		// menuLayout.measure(w, h);
		// menuHeight = menuLayout.getMeasuredHeight();
		// menuWidth = menuLayout.getMeasuredWidth();

		// 底部菜单栏
		btnLayout1 = (LinearLayout) findViewById(R.id.menu_btn_layout1);
		btnLayout2 = (LinearLayout) findViewById(R.id.menu_btn_layout2);
		btnLayout3 = (LinearLayout) findViewById(R.id.menu_btn_layout3);
		btnToggleLayout3 = (LinearLayout) findViewById(R.id.menu_tgbtn_layout3);
		btnLayout4 = (LinearLayout) findViewById(R.id.menu_btn_layout4);

		/***
		 * 菜单按钮
		 */
		btn1 = (Button) findViewById(R.id.btn_1st);
		btn1.setOnClickListener(this);

		btn2 = (ToggleButton) findViewById(R.id.tgbtn_2nd);
		btn2.setOnClickListener(this);

		btn3 = (Button) findViewById(R.id.btn_3rd);
		btn3.setOnClickListener(this);

		btn3toggle = (ToggleButton) findViewById(R.id.tgbtn_3rd);
		btn3toggle.setOnClickListener(this);

		btn4 = (Button) findViewById(R.id.btn_share);
		btn4.setOnClickListener(this);

	}

	/***
	 * ViewPager共有三页: 1.普通说明书(任何情况下都必须存在); 2.美国药师协会(可能存在); 3.厂商说明书(可能存在)
	 * 
	 * 因此当前页面有以下几种情况： <一>只有一页：view ∈ {普通说明书} , index ∈ {0} <二>包含两页： ①view
	 * ∈{普通说明书，美国药师协会},index∈{0,1}; ②view ∈{普通说明书，厂商说明书},index ∈ {0,2};
	 * <三>包含三页：view ∈{普通说明书，美国药师协会,厂商说明书},index ∈ {0,1,2};
	 * 
	 * @param index
	 *            ** ViewPager current page index
	 */
	@Override
	public void setCurrentPoint(int index) {
		switch (index) {
		case 0:
			changeReferenceMode(MODE_NORMAL);
			break;
		case 1:
			// index∈{0,1} or index ∈ {0,1,2}
			if (hasASHF)
				changeReferenceMode(MODE_ASHF);
			// index ∈ {0,2};
			else
				changeReferenceMode(MODE_BRAND);
			break;
		case 2:
			changeReferenceMode(MODE_BRAND);
			break;
		}
	}

	/***
	 * 切换说明书类型
	 * 
	 * @param mode
	 */
	private void changeReferenceMode(int mode) {
		switch (mode) {
		case MODE_NORMAL:
			showDrugDescription();
			break;
		case MODE_ASHF:
			showASHFDescription();
			break;
		case MODE_BRAND:
			showBrandDescription();
			break;
		}
		currentMode = mode;
		changeMenuBtn(mode);
		navigateView.changeUI(mode);
	}

	/**
	 * 切换说明书类型时改变底部菜单栏
	 * */
	private void changeMenuBtn(int mode) {

		switch (mode) {
		case MODE_NORMAL:
			btn1.setBackgroundResource(R.drawable.btn_interaction);
			//btn1.setBackgroundResource(R.drawable.menu_infset_btn);
			btnLayout3.setVisibility(View.VISIBLE);
			btn3.setBackgroundResource(R.drawable.btn_note);
			// 隐藏翻译布局
			btnToggleLayout3.setVisibility(View.GONE);
			break;
		case MODE_ASHF:
			btn1.setBackgroundResource(R.drawable.btn_interaction);
			// 显示翻译布局
			btnToggleLayout3.setVisibility(View.VISIBLE);
			btnLayout3.setVisibility(View.GONE);
			break;
		case MODE_BRAND:
			btnToggleLayout3.setVisibility(View.GONE);
			btnLayout3.setVisibility(View.VISIBLE);
			btn1.setBackgroundResource(R.drawable.menu_introduce_btn);
			btn3.setBackgroundResource(R.drawable.menu_contact_btn);
			break;
		}
	}

	/** 显示普通说明书信息 */
	private void showDrugDescription() {

		if (!isLoadNomarlSuccess) {
			if (drugInfo != null) {
				TextView drugNameCn = (TextView) viewNormalHeader
						.findViewById(R.id.DescriptionDrugNameCn);
				TextView drugNameEn = (TextView) viewNormalHeader
						.findViewById(R.id.DescriptionDrugNameEn);
				TextView adminRouteInfo = (TextView) viewNormalHeader
						.findViewById(R.id.DescriptionAdminRouteInfo);

				TextView adminRouteInfoTitle = (TextView) viewNormalHeader
						.findViewById(R.id.DescriptionAdminRouteInfoTitle);
				TextView brandNameTitle = (TextView) viewNormalHeader
						.findViewById(R.id.DescriptionBrandNameTitle);
				ExpandLayout brandNameCn = (ExpandLayout) viewNormalHeader
						.findViewById(R.id.BrandNameTextLay);

				// 药品名
				drugNameCn.setText(getDrugNameCnSpannableString());
				drugNameEn.setText(drugInfo.getNameEn());
				boldText(drugNameCn);
				boldText(drugNameEn);

				String text;
				/** 给药类型 */
				text = getString(R.string.adminRouteInfo_title);
				adminRouteInfoTitle.setText(text);
				boldText(adminRouteInfoTitle);
				AdminRouteInfo adminRoute = drugInfo.getAdminRouteInfo();
				if (null != adminRoute)
					adminRouteInfo.setText(isZh ? adminRoute.getNameCn()
							: adminRoute.getNameEn());

				boldText(adminRouteInfo);

				/** 品牌 */
				text = getString(R.string.associated_brand_names1);
				brandNameTitle.setText(text);
				boldText(brandNameTitle);
				drugInfo.__setDaoSession(daoSession);
				text = getBrandInfoString(drugInfo.getBrandInfoList());

				brandNameCn.setText(text);
				isLoadNomarlSuccess = true;

			}
		}
	}

	/**
	 * 让TextView中的字体加粗
	 * 
	 * @param text
	 */
	private void boldText(TextView text) {
		// 暂不需要加粗
		// text.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		// TextPaint tp = text.getPaint();
		// tp.setFakeBoldText(true);
	}

	/***
	 * 拼接品牌名
	 * 
	 * @param list
	 *            品牌集合
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
				builder.append(brandSplit);
				brandList.add(name);
			}
		}
		String text = builder.toString();
		if (TextUtils.isEmpty(text))
			return "";
		text = text.substring(0, text.length() - 1);
		return text;
	}

	/** 显示美国药师协会信息 */
	private void showASHFDescription() {
		if (!isLoadAhfsSuccess) {
			TextView ashfDrugName = (TextView) viewAshfHeader
					.findViewById(R.id.DescriptionDrugNameCn);

			TextView ashfDrugNameEn = (TextView) viewAshfHeader
					.findViewById(R.id.DescriptionDrugNameEn);
			TextView brandNameTitle = (TextView) viewAshfHeader
					.findViewById(R.id.DescriptionBrandNameTitle);
			ashfBrand = (ExpandLayout) viewAshfHeader
					.findViewById(R.id.BrandNameTextLay);

			TextView txUpdateTitle = (TextView) viewAshfHeader
					.findViewById(R.id.txt_update_date_title);

			txUpdate = (TextView) viewAshfHeader
					.findViewById(R.id.txt_update_date);

			boldText(ashfDrugName);
			boldText(ashfDrugNameEn);
			boldText(brandNameTitle);
			boldText(txUpdateTitle);
			boldText(txUpdate);

			ImageView txLink = (ImageView) viewAshfHeader
					.findViewById(R.id.txt_link);
			// txLink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			txLink.setOnClickListener(this);

			// 药品名
			ashfDrugName.setText(getDrugNameCnSpannableString());
			ashfDrugNameEn.setText(drugInfo.getNameEn());

			// 品牌
			String text = getString(R.string.associated_brand_names1);
			brandNameTitle.setText(text);
			checkAshfDatas();
			isLoadAhfsSuccess = true;
		}
	}

	/***
	 * 药品中文名+处方或非处方标识
	 */
	private SpannableString drugNameCnSpannableString;

	/***
	 * 获取药品中文名+处方或非处方标识
	 * 
	 * @return
	 */
	private SpannableString getDrugNameCnSpannableString() {
		if (drugNameCnSpannableString == null) {
			String splitString = " ";
			StringBuilder builder = new StringBuilder(drugInfo.getNameCn());
			builder.append(splitString);
			builder.append("temp");

			String text = builder.toString();
			drugNameCnSpannableString = new SpannableString(text);

			Drawable drawable = null;

			if (drugInfo.getOtc() != null && drugInfo.getOtc().equals("Y")) {
				drawable = context.getResources().getDrawable(R.drawable.otc);
			} else {
				drawable = context.getResources().getDrawable(R.drawable.rx);
			}
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
			drugNameCnSpannableString.setSpan(span,
					text.lastIndexOf(splitString) + splitString.length(),
					text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		return drugNameCnSpannableString;
	}

	/** 显示厂商说明书信息 */
	private void showBrandDescription() {

		if (!isLoadBrandSuccess) {
			TextView generalname = (TextView) viewbrandHeader
					.findViewById(R.id.DescriptionDrugNameCn);
			// TextView brandCode = (TextView) viewbrand
			// .findViewById(R.id.DescriptionCode);

			TextView brandSpecTitle = (TextView) viewbrandHeader
					.findViewById(R.id.DescriptionSpecTitle);

			TextView brandSpec = (TextView) viewbrandHeader
					.findViewById(R.id.DescriptionSpec);
			ImageView brandImg = (ImageView) viewbrandHeader
					.findViewById(R.id.brand_img);

			boldText(generalname);
			boldText(brandSpecTitle);
			boldText(brandSpec);

			// TextView codeTitle = (TextView) viewbrand
			// .findViewById(R.id.DescriptionCodeTitle);
			String iconPath = Constant.DATA_PATH_Mufacture_doc + "/drug_"
					+ brandEntity.getBrandId() + ".png";

			brandBitmap = BitmapFactory.decodeFile(iconPath);
			if (brandBitmap != null)
				brandImg.setImageBitmap(brandBitmap);
			String text;
			text = brandEntity.getGeneralName();

			if (drugInfo.getOtc() != null && drugInfo.getOtc().equals("Y")) {
				text = text + "(OTC)";
			} else {
				text = text + "(Rx)";
			}
			generalname.setText(text);//

			// // ---分类
			// text = getString(R.string.act_code_and_description)
			// + getString(R.string.colon);
			// codeTitle.setText(text);
			// // brandCode.setText(drugInfo.);
			text = "";
			int size = brandEntity.getSpecList().size();
			for (int i = 0; i < size; i++) {
				if (i > 0)
					text += '\n';
				text += brandEntity.getSpecList().get(i);
			}
			brandSpec.setText(text);
			isLoadBrandSuccess = true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			navigateView.NORMAL_IS_ENABLED = true;
			navigateView.CATEGORY_IS_ENABLED = true;
			navigateView.OTHER_IS_ENABLED = true;
			GBApplication.gbapp.setHomeLaunched(false);
			GBApplication.gbapp.setStartActivity(true);
			this.finish();
		}
		return false;
	}

	/**
	 * 点击按钮变换背景色
	 * 
	 * @param viewGroup
	 */
	private void pressView(final ViewGroup viewGroup) {
		viewGroup.setBackgroundResource(R.drawable.bg_press);
		viewGroup.postDelayed(new Runnable() {
			@Override
			public void run() {
				viewGroup.setBackgroundResource(R.drawable.bg_normal);
			}
		}, 200);
	}

	@Override
	public void onClick(View v) {
		if (null == drugInfo)
			return;
		int id = v.getId();
		switch (id) {
		/** 意见反馈或查看网页版厂商说明书 */
		case R.id.btn_1st:
			pressView(btnLayout1);
			// 查看网页版厂商说明书
			if (currentMode == MODE_BRAND) {
				Intent lily = new Intent(NewDrugReferenceActivity.this,
						LilyDrugDetailActivity.class);
				lily.putExtra("brandId", brandEntity.getBrandId());
				lily.putExtra("productId", drugInfo.getId());
				startActivity(lily);
			}else if(currentMode == MODE_ASHF||currentMode == MODE_NORMAL){ 
				//进入药物相互作用
				Intent lily = new Intent(NewDrugReferenceActivity.this,
						InteractIndex.class);
				lily.putExtra("drugNameEn", drugInfo.getNameEn());
				lily.putExtra("drugNameCn", drugInfo.getNameCn());
				startActivity(lily);
			}else {
				// 意见反馈
				UMFeedbackService.openUmengFeedbackSDK(context);
			}
			break;
		/** 收藏 */
		case R.id.tgbtn_2nd:
			pressView(btnLayout2);
			MobclickAgent.onEvent(context, "Favorite", "DrgFavoritBtn", 1);
			uploadLoginLogNew("Drug", "DrgFavoritBtn", "Favorite", null);

			switch (startMode) {
			case MODE_NORMAL:
				String adminRouteId = null;
				String adminRouteName = null;
				if (null != drugInfo.getAdminRouteInfo()) {
					adminRouteId = drugInfo.getAdminRouteInfo().getId();
					adminRouteName = isZh ? drugInfo.getAdminRouteInfo()
							.getNameCn() : drugInfo.getAdminRouteInfo()
							.getNameEn();
				}
				changeFavor(drugInfo.getId(), isZh ? drugInfo.getNameCn()
						: drugInfo.getNameEn(), adminRouteId, adminRouteName,
						drugInfo.getAhfsInfo());
				break;
			case MODE_BRAND:
				changeFavor(
						brandEntity.getBrandId(),
						isZh ? brandEntity.getGeneralName() : brandEntity
								.getEnName(), null, null, "N");
				break;
			}

			break;
		/** 翻译 */
		case R.id.tgbtn_3rd:
			if (currentMode == MODE_ASHF) {
				pressView(btnLayout3);
			}
			break;
		/** 查看联系厂商数据或记笔记 */
		case R.id.btn_3rd:
			pressView(btnLayout3);

			if (currentMode == MODE_BRAND) {
				// 查看联系厂商数据
				Intent in = new Intent(NewDrugReferenceActivity.this,
						ContactMufacturedetailActivity.class);
				in.putExtra("brandId", brandEntity.getBrandId());
				if (brandEntity != null) {
					if (isZh)
						in.putExtra("brandName", brandEntity.getGeneralName());
					else
						in.putExtra("brandName", brandEntity.getEnName());
				}
				startActivity(in);
			} else {
				// 记笔记
				Intent intent = new Intent(NewDrugReferenceActivity.this,
						DrugEditInfoActivity.class);
				intent.putExtra("drugId", drugInfo.getId());
				startActivity(intent);
			}
			break;

		/** 分享 */
		case R.id.btn_share:
			sharing(R.array.items2, 0);
			break;

		/** 美国药师协会 使用提示 */
		case R.id.txt_link:
			Intent disclamer = new Intent(NewDrugReferenceActivity.this,
					AHFSDisclaimerActivity.class);
			startActivity(disclamer);
			break;
		}
	}

	/**
	 * 改变收藏状态
	 * 
	 * @param id
	 * @param name
	 * @param adminRouteId
	 * @param adminRouteName
	 * @param mode
	 */
	private void changeFavor(String id, String name, String adminRouteId,
			String adminRouteName, String ahfsInfo) {
		if (isFavor(id, adminRouteId)) {
			if (favoritadpter.delTabDrugReference(context, id, adminRouteId,
					SharedPreferencesMgr.getUserName())) {
				Toast toast = Toast.makeText(context, R.string.cancelFav,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 220);
				toast.show();
			}
		} else {
			Toast toast = Toast.makeText(context, R.string.favorite,
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 220);
			toast.show();
			if (null != drugInfo)
				favoritadpter.insertTabDrugReference(id, name, adminRouteId,
						adminRouteName, SharedPreferencesMgr.getUserName(),
						ahfsInfo, startMode);
		}

	}

	/***
	 * 当前药品或厂商信息是否已经被收藏
	 * 
	 * @param id
	 * @param name
	 * @param adminRouteId
	 * @param adminRouteName
	 * @param mode
	 * @return
	 */
	private boolean isFavor(String id, String adminRouteId) {
		if (favoritadpter.selectTabDrugReferenceByDrug(context, id,
				adminRouteId, SharedPreferencesMgr.getUserName()) > 0)
			return true;
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		if (pos == 0)
			return;
		if (null == drugInfo)
			return;

		Intent intent = new Intent(context, DrugDetailActivity.class);
		// <!-公共参数-->
		intent.putExtra("druginfo", drugInfo);
		intent.putExtra("mode", currentMode);// 当前类型
		intent.putExtra("startMode", startMode);// 启动类型
		if (null != brandEntity) {
			intent.putExtra("brandId", brandEntity.getBrandId());
			intent.putExtra("brandName", isZh ? brandEntity.getGeneralName()
					: brandEntity.getEnName());
		}
		intent.putExtra("pos", pos - 1);// 点击选项列表下标值(排除当前listview包含的headerview后的下标)

		if (currentMode != MODE_ASHF) {// 普通或者厂商说明书模式下
			int count = currentMode == MODE_NORMAL ? listAdapter1.getCount()
					: listAdapter3.getCount();
			if ((pos - 1 == count && currentMode == MODE_NORMAL) || 
					(pos - 1 == count && currentMode == MODE_BRAND)) {// 学术活动
				Intent mIntent = new Intent(NewDrugReferenceActivity.this,
						DrugReferenceAcademicActivity.class);
				mIntent.putExtra("title",
						listAdapter1.getItem(listAdapter1.getCount() - 1));
				mIntent.putExtra("id", drugInfo.getId());
				mIntent.putExtra("drugname", isZh ? drugInfo.getNameCn()
						: drugInfo.getNameEn());
				startActivity(mIntent);
				return;
			} else if (pos < count && pos > count - 4) {// 获取药品网络数据(基本药物目录，医保目录，定价信息)，需要用户省份信息
				// 用户医院所在省份或直辖市
				String region = SharedPreferencesMgr.getRegion().trim();
				if (TextUtils.isEmpty(region)) {
					// 提示用户补全信息
					showDialog(DialogRes.DIALOG_REGIST_NOTIFY);
					return;
				}
			}
			startActivity(intent);
		} else {// AHFS说明书
			if (ashfDatas != null) {
//				intent.putExtra("ashf", ashfDatas);
				if (btn3toggle.isChecked()) {
					if (!DeviceInfo.instance.isNetWorkEnable()) {
						// showErrString( "没有网络无法使用该功能！");
						Toast.makeText(NewDrugReferenceActivity.this,
								R.string.hasnotnetworkcannotuse,
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				intent.putExtra("trans", btn3toggle.isChecked());
				startActivityForResult(intent, ASHF);
			}
		}

	}

	/***
	 * 下载AHFS数据回调(下载成功)
	 */
	@Override
	public void finishDownloadZipFile() {
		getAshfDatas();
	}

	/***
	 * 下载AHFS数据回调(下载失败)
	 */
	@Override
	public void failDownloadZipfile() {
		// Toast.makeText(context, getString(R.string.netexception), 1).show();
	}

	/**
	 * 检查是不是有所需的ASHF数据
	 * */
	private void checkAshfDatas() {

		// 待下载的文件id集
		ArrayList<String> downFileList = null;
		for (int i = 0; i < drugInfo.getAhfsDrugInfoList().size(); i++) {
			File file = new File(Constant.DRUG_AHFS + "/"
					+ drugInfo.getAhfsDrugInfoList().get(i).getFileId()
					+ ".zip");
			// 文件不存在
			if (!file.exists()) {
				if (downFileList == null)
					downFileList = new ArrayList<String>();
				downFileList.add(drugInfo.getAhfsDrugInfoList().get(i)
						.getFileId());
			}
		}
		// 本地有AHFS的数据包(没有需要下载的数据包)
		if (downFileList == null) {
			getAshfDatas();
		} else {// 本地没有，网络下载(回调->finishDownloadZipFile)
			checkZipFile(downFileList);
		}
	}

	/**
	 * 解析ASHF数据并显示
	 * */
	private void getAshfDatas() {
		// 获取 缓存
		ashfDatas = Util.ahfsMap.get(drugInfo.getId());
		if (null == ashfDatas) {
			try {
				ashfDatas = new ArrayList<AshfEntity>();
				String json;
				AshfEntity entity;
				for (int i = 0; i < drugInfo.getAhfsDrugInfoList().size(); i++) {
					entity = new AshfEntity();
					if (drugInfo.getAhfsDrugInfoList().get(i) != null) {
						json = decryptZipFile(
								drugInfo.getAhfsDrugInfoList().get(i)
										.getFileId()).toString();

						entity.paserJson(json);
						entity.generalNameEn = drugInfo.getAhfsDrugInfoList()
								.get(i).getDrugName();
					}
					ashfDatas.add(entity);
				}
				
				if(null != ashfDatas){
					Util.ahfsMap.put(drugInfo.getId(),ashfDatas);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		showAHFSInfo();
	}

	/** 显示AHFS信息 */
	private void showAHFSInfo() {
		if (ashfDatas != null && ashfDatas.size() > 0) {
			long showTime = 0;
			long tempTime = 0;

			StringBuffer text = new StringBuffer();
			SimpleDateFormat tsdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			for (int i = 0; i < ashfDatas.size(); i++) {
				if (!TextUtils.isEmpty(ashfDatas.get(i).brandName))
					text.append(ashfDatas.get(i).brandName).append(brandSplit);
				try {
					tempTime = tsdf.parse(
							drugInfo.getAhfsDrugInfoList().get(i)
									.getUpdateTime()).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (tempTime > showTime)
					showTime = tempTime;
			}

			ashfBrand.setText(text.toString());

			if (showTime != 0) {
				java.util.Date d = new java.util.Date(showTime);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				String sDateTime = sdf.format(d);
				txUpdate.setVisibility(View.VISIBLE);
				txUpdate.setText(sDateTime);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (null == drugInfo)
			return;
		String id = null;
		String adminRouteId = null;

		switch (startMode) {
		case MODE_NORMAL:
			id = drugInfo.getId();
			if (null != drugInfo.getAdminRouteInfo()) {
				adminRouteId = drugInfo.getAdminRouteInfo().getId();
			}
			break;
		case MODE_BRAND:
			id = brandEntity.getBrandId();
			break;
		}

		if (isFavor(id, adminRouteId)) {
			btn2.setChecked(true);
		} else {
			btn2.setChecked(false);
		}
	}

	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// 注册tip
		mask = (Mask) findViewById(R.id.mask);
		mask = new Mask(this, null);
		TipHelper.registerTips(this, 1);
		TipHelper.runSegments(this);
		this.findViewById(R.id.closeTips).setOnClickListener(
				new OnClickListener() {
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
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (brandBitmap != null)
			brandBitmap.recycle();
	}

	@Override
	public void clickPositiveButton(int dialogId) {
		super.clickPositiveButton(dialogId);
		if (DialogRes.DIALOG_REGIST_NOTIFY == dialogId) {
			Intent intent = new Intent();
			intent.setClass(NewDrugReferenceActivity.this,
					Registration_updateActivity.class);
			intent.putExtra("isModify", true);
			startActivity(intent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ASHF) {
				btn3toggle.setChecked(data.getBooleanExtra("trans", false));
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return false;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (viewPage.getCurrentItem() == 0
				&& (velocityX > 0 && Math.abs(velocityX) > 300)) {
			// Fling left
			GBApplication.gbapp.setHomeLaunched(false);
			GBApplication.gbapp.setStartActivity(true);
			this.finish();
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
