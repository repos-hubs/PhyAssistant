package com.jibo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.dao.DrugDetailTypeInfoDao;
import com.jibo.data.DrugEDLPaser;
import com.jibo.data.DrugReimbursementPaser;
import com.jibo.data.GetgoogleParser;
import com.jibo.data.entity.AshfEntity;
import com.jibo.data.entity.DrugEDLEntity;
import com.jibo.data.entity.DrugIndicationEntity;
import com.jibo.data.entity.DrugInteractionEntity;
import com.jibo.data.entity.DrugReimbursementsEntity;
import com.jibo.data.entity.MfgrPriceEntity;
import com.jibo.data.entity.MfgrPricePaser;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.entity.AdminRouteInfo;
import com.jibo.entity.DrugDetailTypeInfo;
import com.jibo.entity.DrugInfo;
import com.jibo.entity.ManufutureBrandInfo;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

import de.greenrobot.dao.WhereCondition;

/**
 * 药品详情数据页面
 * 
 * @author simon
 * 
 */
public class DrugDetailActivity extends BaseSearchActivity implements
		OnTouchListener, OnGestureListener, OnClickListener {

	/** 页面主标题 */
	private TextView mainTitle;

	/** 当前应用是否为中文环境 */
	private boolean isZh;

	/** 说明书类型，普通，ASHF，品牌 */
	private static final int MODE_NORMAL = 0;
	private static final int MODE_AHFS = 1;
	private static final int MODE_BRAND = 2;
	/** 当前详细页对应的说明书类型 */
	private int currentMode;

	/** 说明书启动类型 */
	private int startMode;

	/**
	 * 以下三种详情页信息来自于网络数据
	 */
	/** 基本药物 */
	private DrugEDLEntity drugEDL;
	/** 医保目录 */
	private DrugReimbursementsEntity drugReimbursement;
	/** 定价信息 */
	private MfgrPriceEntity mMfgrPrice;

	/** 基本药物请求type */
	private final int TYPE_DRUG_EDL = 0;
	/** 医保目录 请求type */
	private final int TYPE_DRUG_REIMBURSEMENT = 1;
	/** 定价信息请求type */
	private final int TYPE_DRUG_DRUG_MANUFACTURE_PRICE = 2;

	/** WebView布局 */
	private LinearLayout webLay;
	private WebView web;

	/** 加载进度条 */
	private View progress;

	/** 底部菜单布局 */
	private LinearLayout btnLayout1;
	private LinearLayout btnLayout2;
	private LinearLayout btnLayout3;
	private LinearLayout btnToggleLayout3;
	private LinearLayout btnLayout4;
	/***
	 * 底部菜单按钮
	 */
	private Button btn1;
	private ToggleButton btn2;
	private Button btn3;
	private ToggleButton btn3toggle;
	private Button btn4;

	/** 滑动手势 */
	private GestureDetector returnGesture;

	/** 普通说明书选项列表items */
	private String[] normalItems;
	/** 普通说明书选项列表item的ids */
	private String[] normalItemsId;
	/** AHFS选项列表item的ids */
	private String[] ahfsItems;
	/** 厂商说明书选项列表items */
	private String[] brandItems;

	/** 药品基本信息数据 */
	private DrugInfo drugInfo;
	/** 药品给药途径 */
	private AdminRouteInfo adminRouteInfo;
	/** 厂商id */
	private String brandId;

	private String brandName;

	/** AHFS说明书数据 */
	public ArrayList<AshfEntity> ashfDatas;

	/** 厂商说明书数据 */
	private ManufutureBrandInfo brandEntity;

	/** 当前详细页对应的详情选项列表的下标值 */
	private int pos;

	/** 该用户是否是邀请码的用户，版本对应为专业版 */
	private boolean bPreMode;

	/** html页面相关参数 */
	private final String mimeType = "text/html";
	private final String encoding = "utf-8";

	/**
	 * 普通说明书类型下，数据库查询异步Task
	 * */
	private NormalReferenceTask task;

	/** db工具 */
	private FavoritDataAdapter favoritadpter;

	/** AHFS详情页获取网络翻译数据回调 */
	public BaseResponseHandler baseHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drug_detail);
		super.onCreate(savedInstanceState);
		favoritadpter = new FavoritDataAdapter(this);

		// 初始化组件和公共数据
		inits(getIntent());

		// 填充数据
		fillData(getIntent());

	}

	/***
	 * 初始化UI
	 * 
	 * @param intent
	 */
	public void inits(Intent intent) {

		// 页面标题
		mainTitle = (TextView) findViewById(R.id.txt_header_title);
		// 进度条
		progress = findViewById(R.id.dialogprogress);

		webLay = (LinearLayout) findViewById(R.id.webview_lay);

		baseHandler = new BaseResponseHandler(this);

		returnGesture = new GestureDetector(this);
		// webLay.setOnTouchListener(this);

		/***
		 * 底部菜单
		 */
		btnLayout1 = (LinearLayout) findViewById(R.id.menu_btn_layout1);
		btnLayout2 = (LinearLayout) findViewById(R.id.menu_btn_layout2);
		btnLayout3 = (LinearLayout) findViewById(R.id.menu_btn_layout3);
		btnToggleLayout3 = (LinearLayout) findViewById(R.id.menu_tgbtn_layout3);
		btnLayout4 = (LinearLayout) findViewById(R.id.menu_btn_layout4);

		btn1 = (Button) findViewById(R.id.btn_1st);
		btn2 = (ToggleButton) findViewById(R.id.tgbtn_2nd);
		btn3 = (Button) findViewById(R.id.btn_3rd);
		btn3toggle = (ToggleButton) findViewById(R.id.tgbtn_3rd);
		btn4 = (Button) findViewById(R.id.btn_share);

		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn3toggle.setOnClickListener(this);
		btn4.setOnClickListener(this);

	}

	/***
	 * New WebView
	 * 
	 * @return
	 */
	private WebView createWebView() {
		WebView web = new WebView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		web.setLayoutParams(params);
		web.setBackgroundColor(0);
		// web.setWebChromeClient(new MyWebChromeClient());
		WebSettings mWebSettings = web.getSettings();
		mWebSettings.setDefaultFontSize(18);
		// 加上这句话才能使用javascript方法
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setDomStorageEnabled(true);
		// 增加接口方法,让html页面调用
		web.addJavascriptInterface(new Object() {

			@SuppressWarnings("unused")
			public void more() {
				// 提醒用户去填注册码
				showDialog(DialogRes.DIALOG_INVITE_NOTIFY);
			}

			/***
			 * 下一个详细页
			 */
			@SuppressWarnings("unused")
			public void link() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						reqData(linkIndex);
					}

				});
			}
		}, "jscall");

		// initWebViewContent();
		return web;
	}

	/**
	 * 链接页index
	 */
	private int linkIndex;

	/**
	 * 跳转到链接页
	 * 
	 * @param linkIndex
	 */
	private void reqData(int linkIndex) {
		switch (currentMode) {
		case MODE_NORMAL:
			reqNormalData(linkIndex);
			break;
		case MODE_AHFS:
			reqAHFSData(linkIndex);
			break;
		case MODE_BRAND:
			reqBrandData(linkIndex);
			break;
		}
	}

	/***
	 * 
	 * 显示数据
	 * 
	 * @param intent
	 */
	private void fillData(Intent intent) {

		Resources res = getResources();
		isZh = Locale.getDefault().getLanguage().contains("zh");

		currentMode = intent.getIntExtra("mode", MODE_NORMAL);

		// 从药品列表进入药品详细的默认启动类型，非当前mode
		startMode = intent.getIntExtra("startMode", MODE_NORMAL);

		drugInfo = (DrugInfo) intent.getSerializableExtra("druginfo");

		adminRouteInfo = drugInfo.getAdminRouteInfo();

		brandId = intent.getStringExtra("brandId");

		// 用于收藏
		brandName = intent.getStringExtra("brandName");
		// 检查是否收藏
		checkFavor(startMode);
		// 改变底部菜单样式
		changeMenuBtn(currentMode);
		int index = intent.getIntExtra("pos", 0);

		switch (currentMode) {
		case MODE_NORMAL:
			normalItems = res.getStringArray(R.array.drug_list);
			normalItemsId = res.getStringArray(R.array.drug_list_id);
			reqNormalData(index);
			break;
		case MODE_AHFS:
			ahfsItems = res.getStringArray(R.array.ahfs_list);
			if (!SharedPreferencesMgr.getInviteCode().equals(""))
				bPreMode = true;
			isTranslate = intent.getBooleanExtra("trans", false);
			btn3toggle.setChecked(isTranslate);
			ashfDatas = Util.ahfsMap.get(drugInfo.getId());
			closeProgress();
			reqAHFSData(index);
			break;
		case MODE_BRAND:
			// 外部转入开始查找相关的brandEntity
			brandItems = res.getStringArray(R.array.brand_list);
			brandEntity = Util.getPaserObj(brandId);
			if (brandEntity == null) {
				brandEntity = daoSession.getManufutureBrandInfoDao().load(
						brandId);
				brandEntity.paserJson();
				Util.setPaserObj(brandEntity);
			}
			brandName = isZh ? brandEntity.getGeneralName() : brandEntity
					.getEnName();
			reqBrandData(index);
			break;
		}
	}

	/**
	 * 检查收藏状态
	 */
	private void checkFavor(int mode) {
		String id = null;
		String adminRouteId = null;
		switch (mode) {
		case MODE_NORMAL:
			id = drugInfo.getId();
			if (null != adminRouteInfo) {
				adminRouteId = adminRouteInfo.getId();
			}
			break;
		case MODE_BRAND:
			id = brandId;
			break;
		}

		if (isFavor(id, adminRouteId)) {
			btn2.setChecked(true);
		} else {
			btn2.setChecked(false);
		}
	}

	/**
	 * 切换说明书类型时改变底部菜单栏
	 * 
	 * @param mode
	 *            说明书类型
	 */
	private void changeMenuBtn(int mode) {

		switch (mode) {
		case MODE_NORMAL:
			btn1.setBackgroundResource(R.drawable.menu_infset_btn);
			btnLayout3.setVisibility(View.VISIBLE);
			btn3.setBackgroundResource(R.drawable.btn_note);
			// 隐藏翻译布局
			btnToggleLayout3.setVisibility(View.GONE);
			break;
		case MODE_AHFS:
			btn1.setBackgroundResource(R.drawable.menu_infset_btn);
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

	/** google翻译后的AHFS数据 */
	private GetgoogleParser transData;

	private boolean isTranslate;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_1st:
			pressView(btnLayout1);
			if (currentMode == MODE_BRAND) {
				Intent lily = new Intent(DrugDetailActivity.this,
						LilyDrugDetailActivity.class);
				lily.putExtra("brandId", brandEntity.getBrandId());
				lily.putExtra("productId", drugInfo.getId());
				startActivity(lily);
			} else {
				UMFeedbackService.openUmengFeedbackSDK(context);
			}
			// updateTextSize();
			break;
		// 收藏
		case R.id.tgbtn_2nd:
			pressView(btnLayout2);
			MobclickAgent.onEvent(context, "Favorite", "DrgFavoritBtn", 1);
			uploadLoginLogNew("Drug", "DrgFavoritBtn", "Favorite", null);
			// 启动类型如果是厂商的情况下，收藏厂商id
			switch (startMode) {
			case MODE_NORMAL:
				String adminRouteId = null;
				String adminRouteName = null;
				if (null != adminRouteInfo) {
					adminRouteId = adminRouteInfo.getId();
					adminRouteName = isZh ? adminRouteInfo.getNameCn()
							: adminRouteInfo.getNameEn();
				}

				changeFavor(drugInfo.getId(), isZh ? drugInfo.getNameCn()
						: drugInfo.getNameEn(), adminRouteId, adminRouteName,
						drugInfo.getAhfsInfo());
				break;
			case MODE_BRAND:
				changeFavor(brandEntity.getBrandId(), brandName, null, null,
						"N");
				break;
			}

			break;
		// 翻译
		case R.id.tgbtn_3rd:
			if (currentMode == MODE_AHFS) {
				pressView(btnToggleLayout3);
				isTranslate = !isTranslate;
				btn3toggle.setChecked(isTranslate);
				updateWebContent(buildAHFSString(isTranslate));
			}
			break;
		case R.id.btn_3rd:
			pressView(btnLayout3);
			if (currentMode == MODE_BRAND) {
				if (brandEntity != null) {
					Intent in = new Intent(DrugDetailActivity.this,
							ContactMufacturedetailActivity.class);
					in.putExtra("brandId", brandEntity.getBrandId());
					if (isZh)
						in.putExtra("brandName", brandEntity.getGeneralName());
					else
						in.putExtra("brandName", brandEntity.getEnName());
					startActivity(in);
				}
			} else {
				// 记笔记
				Intent intent = new Intent(DrugDetailActivity.this,
						DrugEditInfoActivity.class);
				intent.putExtra("drugId", drugInfo.getId());
				startActivity(intent);
			}
			break;

		case R.id.btn_share:
			pressView(btnLayout4);
			sharing(R.array.items2, 0);
			break;
		}
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
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			// 获取翻译AHFS数据后回调
			if (o instanceof GetgoogleParser) {
				transData = (GetgoogleParser) o;
				if (transData != null && transData.list.size() > 0) {
					updateWebContent(buildAHFSString(true));
				} else {
					Toast.makeText(context, getString(R.string.tanslatefaild),
							1).show();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			/** 若当前为AHFS说明书，返回时带上是否翻译参数 */
			if (currentMode == MODE_AHFS) {
				Intent intent = new Intent(this, NewDrugReferenceActivity.class);
				intent.putExtra("trans", isTranslate);
				setResult(RESULT_OK, intent);
			}
			finish();
		}
		return false;
	}

	/**
	 * 生成AHFS说明书当前选项下对应的内容HTML
	 * 
	 * @param isTranslate
	 * @return
	 */
	private synchronized String buildAHFSString(boolean isTranslate) {
		StringBuffer sb = new StringBuffer();
		if (ashfDatas != null) {
			if (isTranslate) {
				Log.i("simon", "当前状态为需要翻译");
				if (transData == null || transData.list.size() == 0) {
					Log.i("simon", "无翻译数据，网络请求");
					// 开启网络数据加载
					reqAhfsTransContent(pos);
				}
			}
			for (int i = 0; i < ashfDatas.size(); i++) {
				Log.i("ashfDatasj", getASHFString(ashfDatas.get(i))+"");
				sb.append(getTitle(ashfDatas.get(i).generalNameEn));
				sb.append(getSplitLine());
				// 当前为标准版，限制信息
				// 需要显示翻译信息
				if (isTranslate) {
					Log.i("simon", "当前状态为需要翻译");
					if (transData != null && transData.list.size() > 0) {
						Log.i("simon", "当前状态为需要翻译,并且有翻译数据，直接显示");
						if (i < transData.list.size()) {
							sb.append(getHtml_P_Text("<i>"
									+ getString(R.string.translate_head)
									+ " <a href=\"http://translate.google.com\"><input type=\"image\" src=\"file:///android_asset/google.png\"/></a>,"
									+ getString(R.string.translate_foot)
									+ "</i>"));
							sb.append(transData.list.get(i));
							sb.append(getHtml_P_Text("<i>"
									+ getString(R.string.translate_original)
									+ "</i>"));
						}
					}
				} else {
					Log.i("simon", "当前状态为不    需要翻译");
				}
				sb.append(getASHFString(ashfDatas.get(i)));
			}
			// 检查是否有相应链接
			linkIndex = getLinkIndex();

			if (linkIndex != -1) {
				sb.append("<br/><br/>");
				sb.append("<div style=\"color:#004371;\" onclick=\"window.jscall.link()\">");
				sb.append(getItemString(linkIndex));
				sb.append("&nbsp;<input type=\"image\" id=\"button\"  style =\"vertical-align:middle;\" src=\"file:///android_asset/arrow_01.png\"/></div>");
			}

			if (!bPreMode)
				sb.append(getResources()
						.getString(R.string.invite_notify_image)
						+ "<input type=\"image\" id=\"button\" onclick=\"window.jscall.more()\" src=\"file:///android_asset/more.png\"/>");
		}
		String str = sb.toString();
		str = str.replace("<blockquote>", "");
		str = str.replace("</blockquote>", "");
		str = str.replace("<blockquote/>", "");
		return str;
	}

	int fontSize = 17;

	/**
	 * 改变webview字体大小
	 */
	@SuppressWarnings("unused")
	private void updateTextSize() {
		web.getSettings().setDefaultFontSize(fontSize++);
	}

	/**
	 * 生成厂商说明书当前选项下对应的内容HTML
	 * 
	 * @param isTranslate
	 * @return
	 */
	private String buildBrandString() {
		String content = getBrandInfo();
		StringBuffer buffer = new StringBuffer();
		if (!TextUtils.isEmpty(content)) {
			// 药品名+分割线
			buffer.append(getDrugNameTitle());
			buffer.append(getSplitLine());
			buffer.append(content);
		} else {
			buffer.append(getHtml_P_Text(getString(R.string.nodata)));
		}
		// 检查是否有相应链接
		linkIndex = getLinkIndex();
		if (linkIndex != -1) {
			buffer.append("<br/><br/>");
			buffer.append("<div style=\"color:#004371;\" onclick=\"window.jscall.link()\">");
			buffer.append(getItemString(linkIndex));
			buffer.append("&nbsp;<input type=\"image\" id=\"button\"  style =\"vertical-align:middle;\" src=\"file:///android_asset/arrow_01.png\"/></div>");
		}
		return buffer.toString();

	}

	/**
	 * 获取选项index对应的内容信息
	 * 
	 * @param layerIndex
	 * @return
	 */
	private String getBrandInfo() {
		if (brandEntity == null)
			return "";
		switch (pos) {
		case 0:
			return brandEntity.getIndication();
		case 1:
			return brandEntity.getDosing();
		case 2:
			return brandEntity.getADR();
		case 3:
			return brandEntity.getContraindication();
		case 4:
			return brandEntity.getDrugInteraction();
		case 5:
			return brandEntity.getPediatric_use()
					+ brandEntity.getGeriatric_use()
					+ brandEntity.getPregnancydesc();
		case 6:
			return brandEntity.getOverDosage();
		default:
			return "";
		}

	}

	private void showProgress() {
		progress.setVisibility(View.VISIBLE);
		webLay.setVisibility(View.GONE);
	}

	private void closeProgress() {
		progress.setVisibility(View.GONE);
		webLay.setVisibility(View.VISIBLE);
	}

	DrugIndicationEntity drugIndications;

	String contrain;

	/** 药物相互作用数据 */
	DrugInteractionEntity drugInteractions;

	/**
	 * 获取普通说明书数据
	 * 
	 * @param index
	 *            详细页选项下标
	 * */
	private void reqNormalData(int index) {
		showProgress();
		if (index < 10) {
			task = new NormalReferenceTask(index);
			task.execute(drugInfo.getId(), adminRouteInfo == null ? ""
					: adminRouteInfo.getId(), normalItemsId[index]);
		} else {
			// soap 请求
			reqSoap(index, index - 10);
		}
	}

	/**
	 * 获取AHFS说明书数据
	 * 
	 * @param index
	 *            选项下标
	 */
	public void reqAHFSData(int index) {
		pos = index;
		transData = null;
		updateWebContent(buildAHFSString(isTranslate));
	}

	/**
	 * 获取AHFS翻译后数据
	 * 
	 * @param index
	 */
	private void reqAhfsTransContent(int index) {
		Properties propertyInfo = new Properties();
		propertyInfo.put(SoapRes.KEY_D_DRGID, drugInfo.getId());
		propertyInfo.put(SoapRes.KEY_D_TYPE, getTranslateKey(index));
		sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_GETGOOLE, propertyInfo,
				baseHandler);
	}

	/**
	 * 获取厂商说明书数据
	 * 
	 * @param index
	 *            选项下标
	 */
	private void reqBrandData(int index) {
		showProgress();
		if (index < 7) {
			pos = index;
			updateWebContent(buildBrandString());
		} else {
			// soap 请求
			reqSoap(index, index - 7);
		}
	}

	/**
	 * 请求网络数据(基本药物目录，医保目录，定价信息)
	 * 
	 * @param index
	 *            页面下标
	 * @param type
	 *            请求网络类型
	 */
	private void reqSoap(int index, int type) {
		int methodId = 0;
		switch (type) {
		case TYPE_DRUG_EDL:// 基本药物目录
			if (drugEDL != null) {
				updateWebContent(buildDrugEDL());
				return;
			}
			methodId = SoapRes.REQ_ID_DRUG_EDL;
			break;
		case TYPE_DRUG_REIMBURSEMENT:// 医保目录
			if (drugReimbursement != null) {
				updateWebContent(buildDrugReimbursement());
				return;
			}
			methodId = SoapRes.REQ_ID_DRUG_REIMBURSEMENT;
			break;
		case TYPE_DRUG_DRUG_MANUFACTURE_PRICE:// 定价信息
			if (mMfgrPrice != null) {
				updateWebContent(buildDrugManufacturersPrice());
				return;
			}
			methodId = SoapRes.REQ_ID_DRUG_MANUFACTURE_PRICE;
			break;
		}
		// 用户医院所在省份或直辖市
		String region = SharedPreferencesMgr.getRegion();
		if (region == null || region.equals("")) {
			// 新增如果没有docId，提示用户补全信息。
			showDialog(DialogRes.DIALOG_REGIST_NOTIFY);
			return;
		}

		Properties propertyInfo = new Properties();
		propertyInfo.put("drugId", drugInfo.getId());
		propertyInfo.put("Region", region);
		sendRequest(SoapRes.URLDrug, methodId, propertyInfo, new SoapCallBack(
				index));

	}

	/***
	 * 获取普通说明书详细信息
	 */
	class NormalReferenceTask extends AsyncTask<String, Integer, Object> {

		private int index;

		public NormalReferenceTask(int index) {
			super();
			this.index = index;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Object doInBackground(String... params) {

			List<DrugDetailTypeInfo> list = null;

			// 当InsertType = Special
			// Population,类型为"特殊人群"时，对应数据库的类型为三种PediatricUse(儿童)，GeriatricUse(老年人)，Pregnancy(妇女)
			try {
				list = daoSession
						.getDrugDetailTypeInfoDao()
						.queryBuilder()
						.where(new WhereCondition.StringCondition(
								" InsertID in (Select InsertID from InsertBasicInfo where DrugID = '"
										+ params[0] + "' and AdminRouteID = '"
										+ params[1] + "' )"),
								"Special Population".equals(params[2]) ? new WhereCondition.StringCondition(
										" (InsertType = 'PediatricUse' or InsertType = 'GeriatricUse' or InsertType = 'Pregnancy')")
										: DrugDetailTypeInfoDao.Properties.Type
												.eq(params[2])).list();

			} catch (Exception e) {
				e.printStackTrace();
				return e;
			}
			return list;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Object result) {
			Log.i("GBA", "onPostExecute");
			if (result instanceof Exception) {
				return;
			}
			pos = index;
			@SuppressWarnings("unchecked")
			List<DrugDetailTypeInfo> list = (List<DrugDetailTypeInfo>) result;
			updateWebContent(buildNormalString(list));
		}
	}

	/***
	 * 初始化webView内容(废弃：由于updateContent方法在某些情况下无法传值进入，所以相当郁闷，暂时不知道什么原因，可能是字符过长)
	 */
	@SuppressWarnings("unused")
	private void initWebViewContent(WebView web) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		buffer.append("<head>");
		buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		buffer.append("<script type=\"text/javascript\"> ");

		// 更新WebView内容function( 某些情况下失效)
		buffer.append("function updateTextSize(){ ");
		buffer.append(" document.getElementById(\"content\").style.webkitTextSizeAdjust = '%d%%'; ");
		buffer.append("}");

		// 更新WebView内容function( 某些情况下失效)
		buffer.append("function updateContent(content){ ");
		buffer.append("  document.getElementById(\"content\").innerHTML = content; ");
		buffer.append("}");
		// 页面加载完成后,默认滚动到顶部
		buffer.append("function init(){ ");
		buffer.append("  window.scrollBy(0, 0); ");
		buffer.append("}");
		// buffer.append("function ondownloading(){ ");
		// buffer.append("   document.getElementById(\"download\").disabled=\"disabled\";");
		// buffer.append("   document.getElementById(\"download\").value=\"下载中...\"");
		// buffer.append("}");
		// buffer.append(" function finishdownload(){");
		// buffer.append("   document.getElementById(\"download\").disabled=\"disabled\";");
		// buffer.append("   document.getElementById(\"download\").value=\"下载完成\"");
		// buffer.append("}");
		buffer.append("</script>");
		buffer.append("<title>无标题文档</title>");
		buffer.append("</head>");
		buffer.append("<body onload=\"init();\">");
		buffer.append("<div id=\"content\">");
		buffer.append("</div></body></html>");
		web.loadDataWithBaseURL(null, buffer.toString(), mimeType, encoding,
				null);
	}

	/** Html标签头 */
	private String webHeadContent;
	/** Html标签尾 */
	private String webFootContent;

	/**
	 * 获取Html头部信息
	 */
	private String getWebHeadContent() {
		if (null == webHeadContent) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			buffer.append("<head>");
			buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			buffer.append("<script type=\"text/javascript\"> ");
			// 重排字体
			buffer.append("function updateTextSize(){ ");
			buffer.append(" document.getElementById(\"content\").style.webkitTextSizeAdjust = '10%'; ");
			buffer.append("}");
			// 更新WebView内容function
			buffer.append("function updateContent(content){ ");
			buffer.append("  document.getElementById(\"content\").innerHTML = content; ");
			buffer.append("}");
			// 页面加载完成后,默认滚动到顶部
			buffer.append("function init(){ ");
			buffer.append("  window.scrollBy(0, 0); ");
			buffer.append("}");
			buffer.append("</script>");
			buffer.append("<title>无标题文档</title>");
			buffer.append("</head>");
			buffer.append("<body onload=\"init();\">");
			buffer.append("<div id=\"content\">");
			webHeadContent = buffer.toString();
		}
		return webHeadContent;
	}

	/***
	 * 获取Html底部信息
	 */
	private String getWebFootContent() {
		if (null == webFootContent) {
			webFootContent = "</div></body></html>";
		}
		return webFootContent;
	}

	/**
	 * 获取string对应本地资源字符
	 * 
	 * @param string
	 * @return
	 */
	private String getRightRate(String string) {
		string = string.toLowerCase();
		if (string.contains("part") && string.contains("all"))
			string = getString(R.string.allandpart);
		else if (string.contains("part"))
			string = getString(R.string.part);
		else if (string.contains("all"))
			string = getString(R.string.all);

		return string;
	}

	/**
	 * 合并 基本药物目录 信息(数据来源于网络)
	 * 
	 * @return
	 */
	private String buildDrugEDL() {

		String national, nFormulation, nCategoryOfUse;
		String regional, rFormulation, rCategoryOfUse;
		// String source;

		national = getString(R.string.national) + getString(R.string.colon)
				+ getString(R.string.space) + getString(R.string.no);
		String tempNational = national;
		nFormulation = getString(R.string.formulation)
				+ getString(R.string.colon) + getString(R.string.space);
		regional = getString(R.string.regional) + getString(R.string.colon)
				+ getString(R.string.space);
		String tempRegional = regional;
		rFormulation = getString(R.string.formulation)
				+ getString(R.string.colon) + getString(R.string.space);
		nCategoryOfUse = getString(R.string.category_of_use)
				+ getString(R.string.colon) + getString(R.string.space);
		rCategoryOfUse = getString(R.string.category_of_use)
				+ getString(R.string.colon) + getString(R.string.space);
		// source = getString(R.string.edl_source) + getString(R.string.colon)
		// + getString(R.string.space);

		for (int i = 0; i < drugEDL.iRecordLength; i++) {
			if (drugEDL.national.get(i).toLowerCase().equals("true")) {
				national = getString(R.string.national)
						+ getString(R.string.colon) + getString(R.string.space)
						+ getString(R.string.yes);

				if (!drugEDL.formulation.get(i).equals(Constant.SPACE)) {
					nFormulation = nFormulation + drugEDL.formulation.get(i)
							+ getString(R.string.comma);
				}

				if (!drugEDL.categoryOfUse.get(i).equals(Constant.SPACE)) {
					nCategoryOfUse = nCategoryOfUse
							+ drugEDL.categoryOfUse.get(i)
							+ getString(R.string.comma);
				}
			} else {
				if (!drugEDL.regional.get(i).equals(Constant.SPACE)) {
					regional = getString(R.string.regional)
							+ getString(R.string.colon)
							+ getString(R.string.space)
							+ drugEDL.regional.get(i);
				} else {
					regional = getString(R.string.regional)
							+ getString(R.string.colon)
							+ getString(R.string.space)
							+ getString(R.string.nodatainfo); // prime
				}

				if (!drugEDL.formulation.get(i).equals(Constant.SPACE)) {
					rFormulation = rFormulation + drugEDL.formulation.get(i)
							+ getString(R.string.comma);
				}

				if (!drugEDL.categoryOfUse.get(i).equals(Constant.SPACE)) {
					rCategoryOfUse = rCategoryOfUse
							+ drugEDL.categoryOfUse.get(i)
							+ getString(R.string.comma);
				}
			}

			// if (!drugEDL.source.get(i).equals(Constant.SPACE)) {
			// source = source + drugEDL.source.get(i)
			// + getString(R.string.comma);
			// }
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append(getDrugNameTitle());
		buffer.append(getSplitLine());
		if (!national.equals(tempNational) || !regional.equals(tempRegional)) {
			if (!national.equals(tempNational)) {// 国家层次
				buffer.append(getHtml_P_Text_with_blackColor(national));
				buffer.append(getHtml_P_Text_with_blackColor(nFormulation
						.substring(0, nFormulation.length() - 1)));
				buffer.append(getHtml_P_Text_with_blackColor(nCategoryOfUse
						.substring(0, nCategoryOfUse.length() - 1)));
				buffer.append(getSplitLine());
			}

			if (!regional.equals(tempRegional)) {// 地方层次
				buffer.append(getHtml_P_Text_with_blackColor(regional));
				buffer.append(getHtml_P_Text_with_blackColor(rFormulation
						.substring(0, rFormulation.length() - 1)));
				buffer.append(getHtml_P_Text_with_blackColor(rCategoryOfUse
						.substring(0, rCategoryOfUse.length() - 1)));
				// buffer.append(getHtml_P_Text(source));
			}
		} else {
			buffer.append(getHtml_P_Text_with_blackColor(getString(R.string.nodata)));
		}
		return buffer.toString();

	}

	/**
	 * 合并 医保目录 信息(数据来源于网络)
	 * 
	 * @return
	 */
	private String buildDrugReimbursement() {

		StringBuffer buffer = new StringBuffer();
		buffer.append(getDrugNameTitle());
		buffer.append(getSplitLine());

		if (drugReimbursement.iRecordLength <= 0) {
			buffer.append(getHtml_P_Text_with_blackColor(getString(R.string.nodata)));
		} else {

			String string;
			String text;
			String national = Constant.SPACE;
			String regional = Constant.SPACE;

			for (int i = 0; i < drugReimbursement.iRecordLength; i++) {
				if (i != 0) {
					buffer.append(getSplitLine());
				}
				text = drugReimbursement.national.get(i).toLowerCase();
				if (text.equals("true")) {
					if (!text.equals(national)) {
						string = getString(R.string.national)
								+ getString(R.string.colon)
								+ getString(R.string.space)
								+ getString(R.string.yes);
						buffer.append(getHtml_P_Text_with_blackColor(string));
					}
					national = text;

					string = getRightRate(drugReimbursement.reimbursementRate
							.get(i));

					string = getString(R.string.rate)
							+ getString(R.string.colon)
							+ getString(R.string.space) + string;
					buffer.append(getHtml_P_Text_with_blackColor(string));

					if (!TextUtils.isEmpty(drugReimbursement.type.get(i))) {
						string = getString(R.string.reimbursement_type)
								+ getString(R.string.colon)
								+ getString(R.string.space)
								+ drugReimbursement.type.get(i);
						buffer.append(getHtml_P_Text_with_blackColor(string));
					}

					if (!TextUtils.isEmpty(drugReimbursement.categoryOfUse
							.get(i))) {
						string = getString(R.string.category_of_use)
								+ getString(R.string.colon)
								+ getString(R.string.space)
								+ drugReimbursement.categoryOfUse.get(i);
						buffer.append(getHtml_P_Text_with_blackColor(string));
					}

					if (!TextUtils.isEmpty(drugReimbursement.pediatricSpecific
							.get(i))) {
						string = getString(R.string.pediatric_specific)
								+ getString(R.string.colon)
								+ getString(R.string.space)
								+ drugReimbursement.pediatricSpecific.get(i);
						buffer.append(getHtml_P_Text_with_blackColor(string));
					}

					if (!TextUtils
							.isEmpty(drugReimbursement.formulation.get(i))) {
						string = getString(R.string.formulation)
								+ getString(R.string.colon)
								+ getString(R.string.space)
								+ drugReimbursement.formulation.get(i);
						buffer.append(getHtml_P_Text_with_blackColor(string));
					}
				} else {
					if (i == 0) {
						if (!text.equals(national)) {
							// string = getString(R.string.national)
							// + getString(R.string.colon)
							// + getString(R.string.space)
							// + getString(R.string.no);
							// buffer.append(getHtml_P_Text_with_blackColor(string));
						}
						national = text;

						string = getString(R.string.rate)
								+ getString(R.string.colon);
						buffer.append(getHtml_P_Text_with_blackColor(string));

						string = getString(R.string.reimbursement_type)
								+ getString(R.string.colon);

						buffer.append(getHtml_P_Text_with_blackColor(string));

						string = getString(R.string.category_of_use)
								+ getString(R.string.colon);
						// Util.setTextProperties(this, mLayout, string, 10, 0);

						string = getString(R.string.pediatric_specific)
								+ getString(R.string.colon);
						buffer.append(getHtml_P_Text_with_blackColor(string));

						string = getString(R.string.formulation)
								+ getString(R.string.colon);
						buffer.append(getHtml_P_Text_with_blackColor(string));
					}

					text = drugReimbursement.regional.get(i);
					if (!text.equals(regional)) {
						string = getString(R.string.regional)
								+ getString(R.string.colon)
								+ getString(R.string.space)
								+ drugReimbursement.regional.get(i);

						buffer.append(getHtml_P_Text_with_blackColor(string));
					}
					regional = text;

					string = getString(R.string.formulation)
							+ getString(R.string.colon)
							+ getString(R.string.space)
							+ drugReimbursement.formulation.get(i);
					buffer.append(getHtml_P_Text_with_blackColor(string));

					string = getRightRate(drugReimbursement.reimbursementRate
							.get(i));
					string = getString(R.string.rate)
							+ getString(R.string.colon)
							+ getString(R.string.space) + string;
					buffer.append(getHtml_P_Text_with_blackColor(string));

					string = getString(R.string.reimbursement_type)
							+ getString(R.string.colon)
							+ getString(R.string.space)
							+ drugReimbursement.type.get(i);
					buffer.append(getHtml_P_Text_with_blackColor(string));
				}
			}

			string = getString(R.string.reimbursement_source);
			String url = drugReimbursement.source.get(0);

			if (!TextUtils.isEmpty(url) && Util.isUrl(url)) {
				buffer.append("<a href=\"");
				buffer.append(url);
				buffer.append("\">");
				buffer.append(string);
				buffer.append("</a> ");
			}
		}
		return buffer.toString();
	}

	/**
	 * 合并定价信息数据(数据来源于网络)
	 * 
	 * @return
	 */
	private String buildDrugManufacturersPrice() {

		StringBuffer buffer = new StringBuffer();

		buffer.append(getDrugNameTitle());
		buffer.append(getSplitLine());

		String string;
		string = getString(R.string.manufacturers) + getString(R.string.colon)
				+ getString(R.string.space) + getString(R.string.brand_drug);
		buffer.append(getHtml_P_Text_with_blackColor(string));
		buffer.append(getSplitLine());

		// 厂商名称
		String companyName = null;
		for (int i = 0; i < mMfgrPrice.mMfgrBrandsInfo.companyBrandsCount; i++) {
			companyName = mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].companyNameCn;
			if (TextUtils.isEmpty(companyName))
				break;

			boolean isHadContent = false;
			for (int j = 0; j < mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].brandsCount; j++) {
				isHadContent = true;
				string = mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].brands[j].nameCn;
				if (TextUtils.isEmpty(string))
					break;
				buffer.append("<br/><br/>");
				buffer.append(getHtml_P_Text(string));

				// add by peter.pan
				for (int k = 0; k < mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].brands[j].formulations.length; k++) {
					string = mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].brands[j].formulations[k].formul;
					buffer.append(getHtml_P_Text(string));
					for (int m = 0; m < mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].brands[j].formulations[k].specs.length; m++) {
						string = mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].brands[j].formulations[k].specs[m].spec
								+ getString(R.string.colon)
								+ getString(R.string.space)
								+ mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].brands[j].formulations[k].specs[m].maxRetailPricing
								+ getString(R.string.yuan)
								+ getString(R.string.space)
								+ mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].brands[j].formulations[k].specs[m].effectiveYear
								+ getString(R.string.space)
								+ mMfgrPrice.mMfgrBrandsInfo.companyBrands[i].brands[j].formulations[k].specs[m].area;
						buffer.append(getHtml_P_Text_with_blackColor(string));
					}
				}
				buffer.append("<br/>");
			}
			if (isHadContent) {
				buffer.append("<p style=\"margin:0;padding:2px;text-align: right;color:#004371;\">");
				buffer.append(companyName);
				buffer.append("</p>");
			}

		}
		buffer.append("<br/>");
		buffer.append(getSplitLine());

		/* Generic */
		string = getString(R.string.manufacturers) + getString(R.string.colon)
				+ getString(R.string.space) + getString(R.string.generic);
		buffer.append(getHtml_P_Text_with_blackColor(string));
		buffer.append(getSplitLine());

		for (int i = 0; i < mMfgrPrice.mPricing.genericPricing.formulationsCount; i++) {
			string = mMfgrPrice.mPricing.genericPricing.formulations[i].formul;
			buffer.append(getHtml_P_Text(string));
			for (int j = 0; j < mMfgrPrice.mPricing.genericPricing.formulations[i].specsCount; j++) {
				string = mMfgrPrice.mPricing.genericPricing.formulations[i].specs[j].spec
						+ getString(R.string.colon)
						+ getString(R.string.space)
						+ mMfgrPrice.mPricing.genericPricing.formulations[i].specs[j].maxRetailPricing
						+ getString(R.string.yuan)
						+ getString(R.string.space)
						+ mMfgrPrice.mPricing.genericPricing.formulations[i].specs[j].effectiveYear
						+ getString(R.string.space)
						+ mMfgrPrice.mPricing.genericPricing.formulations[i].specs[j].area;
				buffer.append(getHtml_P_Text_with_blackColor(string));
			}
			buffer.append("<br/>");
		}
		return buffer.toString();
	}

	/***
	 * 合并普通说明书详细内容(本地数据)
	 * 
	 * @param list
	 * @return
	 */
	private String buildNormalString(List<DrugDetailTypeInfo> list) {

		StringBuffer buffer = new StringBuffer();
		// 药品名+分割线
		buffer.append(getDrugNameTitle());
		buffer.append(getSplitLine());
		if (list != null && list.size() > 0) {
			if (daoSession != null) {
				for (int i = 0; i < list.size(); i++) {
					DrugDetailTypeInfo obj = list.get(i);
					// 实体内的会话要及时更新。因为activity周期变化，daoSession会产生变化，原db连接已经关闭
					obj.__setDaoSession(daoSession);
					// 给药类型+":"+药品剂型
					buffer.append(getHtml_P_Text((adminRouteInfo == null ? ""
							: (isZh ? adminRouteInfo.getNameCn()
									: adminRouteInfo.getNameEn()) + ":")// 给药类型
							+ (isZh ? obj.getDrugDetailInfo()
									.getFormulationInfo().getNameCn() : obj
									.getDrugDetailInfo().getFormulationInfo()
									.getNameEn()) + "<br/>"));// 药品剂型
					// 显示用法用量时，需要显示规格
					if (obj.getType().equals("Dosing")) {
						buffer.append(getHtml_P_Text(obj.getDrugDetailInfo()
								.getSpecification()));
					} else if (obj.getType().equals("PediatricUse")) {
						buffer.append(getHtml_P_Text(getString(R.string.pediatricUse_msg)));
					} else if (obj.getType().equals("GeriatricUse")) {
						buffer.append(getHtml_P_Text(getString(R.string.geriatricUse_msg)));
					} else if (obj.getType().equals("Pregnancy")) {
						buffer.append(getHtml_P_Text(getString(R.string.pregnancy_msg)));
					}
					buffer.append("<br/>");

					// 核心内容
					String content = obj.getContent();
					content = content.replaceAll("\\[nn\\]", "<br/>");
					buffer.append(getHtml_P_Text_with_blackColor(content));
					// 公司信息
					buffer.append("<p style=\"margin:20 0 0 0;padding:2px;font-size:15;text-align: right;color:#004371;\">");
					buffer.append(isZh ? obj.getDrugDetailInfo()
							.getCompanyInfo().getNameCn() : obj
							.getDrugDetailInfo().getCompanyInfo().getNameEn());
					buffer.append("</p>");
					// 时间
					buffer.append("<p style=\"margin:0 0 20 0 ;padding:2px;font-size:15;text-align: right;color:#004371;\">");
					buffer.append(getString(R.string.updatetime)
							+ obj.getDrugDetailInfo().getChangeDate());
					buffer.append("</p>");

					// 多个信息分割线
					if (i < list.size() - 1)
						buffer.append(getSplitLine());
				}
			}
		} else {
			buffer.append(getHtml_P_Text_with_blackColor(getString(R.string.nodata)));
		}
		// 检查是否有相应链接
		linkIndex = getLinkIndex();
		if (linkIndex != -1) {
			buffer.append("<br/><br/>");
			buffer.append("<div style=\"color:#004371;\" onclick=\"window.jscall.link()\">");
			buffer.append(getItemString(linkIndex));
			buffer.append("&nbsp;<input type=\"image\" id=\"button\"  style =\"vertical-align:middle;\" src=\"file:///android_asset/arrow_01.png\"/></div>");
		}
		return buffer.toString();
	}

	/***
	 * 药品名称html标签
	 */
	private String drugNameHtmlText;

	/**
	 * 获取药品名称Title
	 * 
	 * @param titleText
	 * @return
	 */
	private String getDrugNameTitle() {
		if (null == drugNameHtmlText)
			drugNameHtmlText = getTitle(isZh ? drugInfo.getNameCn() : drugInfo
					.getNameEn());
		return drugNameHtmlText;
	}

	private String getTitle(String text) {
		return "<h3 style=\"color:#004371;text-align: left ;margin:0;padding:2px;\">"
				+ text + "</h3>";
	}

	/**
	 * 将文本转换为html中的一个段落
	 * 
	 * @param text
	 *            文本内容
	 * @return
	 */
	private String getHtml_P_Text(String text) {
		return getHtml_P_Text(text, "#004371");
	}

	private String getHtml_P_Text_with_blackColor(String text) {
		return getHtml_P_Text(text, "#000000");
	}

	/**
	 * 将文本转换为html中的一个段落
	 * 
	 * @param text
	 *            文本内容
	 * @param colorString
	 *            颜色值
	 * @return
	 */
	private String getHtml_P_Text(String text, String colorString) {
		return "<p style=\"margin:0;padding:2px;color:" + colorString + ";\">"
				+ text + "</p>";
	}

	/***
	 * 分割线
	 */
	private String splitLine;

	/**
	 * 获取一条html分割线
	 * 
	 * @return
	 */
	private String getSplitLine() {
		if (null == splitLine)
			splitLine = "<hr style=\"filter: alpha(opacity=100,finishopacity=0,style=3)\" width=\"100%\" color=#b2b5b7 size=1/>";
		return splitLine;
	}

	/**
	 * 获取对应链接页面的下标, 详情页相互链接的规则涉及4个页面。 跳转规则为： 1。适应症<->禁忌症；2.用法用量<->特殊人群提示
	 * (此规则适用于三种不同说明书)
	 * 
	 * @return
	 */
	private int getLinkIndex() {
		// 相应选项下标在不同说明书下不同
		switch (currentMode) {
		case MODE_NORMAL:
			switch (pos) {
			case 0:// 适应症
				return 4;
			case 1:// 用法用量
				return 2;
			case 2:// 特殊人群提示
				return 1;
			case 4:// 禁忌症
				return 0;
			}
			break;
		case MODE_AHFS:
		case MODE_BRAND:
			switch (pos) {
			case 0:// 适应症
				return 3;
			case 1:// 用法用量
				return 5;
			case 3:// 禁忌症
				return 0;
			case 5:// 特殊人群提示
				return 1;
			}
			break;
		}
		return -1;
	}

	/***
	 * 销毁webview
	 */
	private void destoryWebView() {
		if (web != null)
			web.destroy();
	}

	/***
	 * 修改webView的内容
	 * 
	 * @param text
	 */
	private void updateWebContent(String text) {
		closeProgress();
		if (text == null)
			return;
		mainTitle.setText(getItemString(pos));
		// 废弃，在查看AHFS说明书时，有些类型的数据内容text，传不到js方法中，原因暂时未知
		// 改为每次重新load内容
		// web.loadUrl("javascript:updateContent('" + text + "')");
		webLay.removeAllViews();
		destoryWebView();
		web = createWebView();
		web.loadDataWithBaseURL(null, getWebHeadContent() + text
				+ getWebFootContent(), mimeType, encoding, null);
		webLay.addView(web);
		Log.i("simon", "重新load页面");
	}

	/**
	 * 获取对应下标的选项值
	 * 
	 * @param index
	 *            下标
	 * @return
	 */
	private String getItemString(int index) {
		switch (currentMode) {
		case MODE_NORMAL:
			return normalItems[index];
		case MODE_AHFS:
			return ahfsItems[index];
		case MODE_BRAND:
			return brandItems[index];
		default:
			return "";
		}
	}

	// /**
	// * 获取下一条详细内容的title
	// *
	// * @return
	// */
	// private String getNextItemString() {
	// switch (currentMode) {
	// case MODE_NORMAL:
	// return normalItems[getNextIndex()];
	// case MODE_AHFS:
	// return ahfsItems[getNextIndex()];
	// case MODE_BRAND:
	// return brandItems[getNextIndex()];
	// default:
	// return "";
	// }
	// }
	//
	// /***
	// * 获取下一条详细内容的index
	// *
	// * @return
	// */
	// private int getNextIndex() {
	// switch (currentMode) {
	// case MODE_NORMAL:
	// if (pos + 1 >= normalItems.length)
	// return 0;
	// return pos + 1;
	// case MODE_AHFS:
	// if (pos + 1 >= ahfsItems.length)
	// return 0;
	// return pos + 1;
	// case MODE_BRAND:
	// if (pos + 1 >= brandItems.length)
	// return 0;
	// return pos + 1;
	// default:
	// return 0;
	// }
	// }

	/**
	 * 获取当前选项对应AHFS内容
	 * 
	 * @param entity
	 *            AHFS实体
	 * @return
	 */
	private String getASHFString(AshfEntity entity) {
		switch (pos) {
		case 0:
			if (bPreMode)
				return entity.indication_pre;
			else
				return entity.indication;
		case 1:
			if (bPreMode)
				return entity.dosage_pre;
			else
				return entity.dosage;

		case 2:
			return entity.ADR;
		case 3:
			return entity.contraindication;
		case 4:
			if (bPreMode)
				return entity.interactions_pre;
			else
				return entity.interactions;
		case 5:
			if (bPreMode)
				return entity.spefipop_pre;
			else
				return entity.spefipop;

		case 6:
			if (bPreMode)
				return entity.caution_pre;
			else
				return entity.caution;
		default:
			return "";
		}
	}

	// public enum AHFSTranslateKey {
	//
	// KEY_INDICATION(0,true) {
	// @Override
	// String getInfo() {
	// return "uses_pre";
	// }
	// },
	//
	// KEY_INDICATION_1(0,false) {
	// @Override
	// String getInfo() {
	// return "uses";
	// }
	// };
	//
	//
	// int index;
	// boolean bPreMode;
	// private AHFSTranslateKey(int index,boolean bPreMode) {
	// this.index = index;
	// this.bPreMode = bPreMode;
	// }
	//
	// abstract String getInfo();
	//
	// }

	/**
	 * 获取下标对应的AHFS翻译的网络请求key
	 * 
	 * @param index
	 * @return
	 */
	private String getTranslateKey(int index) {
		String transKey = "";
		switch (index) {
		case 0:
			if (bPreMode)
				transKey = "uses_pre";
			else
				transKey = "uses";
			break;
		case 1:
			if (bPreMode)
				transKey = "dosage_pre";
			else
				transKey = "dosage";
			break;
		case 2:
			transKey = "ad";
			break;
		case 3:
			transKey = "contraindications";
			break;
		case 4:
			if (bPreMode)
				transKey = "interactions_pre";
			else
				transKey = "interactions";
			break;
		case 5:
			if (bPreMode)
				transKey = "spefipop_pre";
			else
				transKey = "spefipop";
			break;
		case 6:
			if (bPreMode)
				transKey = "cautions_pre";
			else
				transKey = "cautions";
			break;
		}
		return transKey;
	}

	/**
	 * soap数据处理类
	 * 
	 * 获取三个网络数据(基本药物,医保目录,定价信息)
	 * */
	class SoapCallBack extends AsyncSoapResponseHandler {
		/** 当前选项页下标index */
		int index;

		public SoapCallBack(int index) {
			this.index = index;
		}

		public void onStart() {
		}

		public void onSuccess(Object content, int methodId) {
			pos = index;
			if (methodId == SoapRes.REQ_ID_DRUG_EDL) {
				drugEDL = ((DrugEDLPaser) content).entity;
				updateWebContent(buildDrugEDL());
			} else if (methodId == SoapRes.REQ_ID_DRUG_REIMBURSEMENT) {
				drugReimbursement = ((DrugReimbursementPaser) content).drugreimbursements;
				updateWebContent(buildDrugReimbursement());
			} else if (methodId == SoapRes.REQ_ID_DRUG_MANUFACTURE_PRICE) {
				if (content != null) {
					mMfgrPrice = new MfgrPriceEntity();
					MfgrPricePaser paser = (MfgrPricePaser) content;
					try {
						mMfgrPrice.FillMfgrPriceData(paser);
					} catch (Exception e) {
						e.printStackTrace();
					}
					updateWebContent(buildDrugManufacturersPrice());
				}
			}
		}

		public void onFinish() {
		}

		public void onFailure(Throwable error, String content) {
			if (!isFinishing())
				showDialog(DialogRes.DIALOG_ERROR_PROMPT);
		}
	}

	@Override
	public void clickPositiveButton(int dialogId) {
		// TODO Auto-generated method stub
		super.clickPositiveButton(dialogId);
		if (DialogRes.DIALOG_REGIST_NOTIFY == dialogId) {
			Intent intent = new Intent();
			intent.setClass(DrugDetailActivity.this,
					Registration_updateActivity.class);
			intent.putExtra("isModify", true);
			startActivity(intent);
		} else if (DialogRes.DIALOG_INVITE_NOTIFY == dialogId) {
			Intent intent = new Intent();
			intent.setClass(DrugDetailActivity.this,
					UpdateInviteCodeActivity.class);
			startActivity(intent);
		}
	}

	protected void onResume() {
		super.onResume();
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
		destoryWebView();
		super.onDestroy();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX > 0 && Math.abs(e1.getX() - e2.getX()) > 300) {
			// Fling left
			GBApplication.gbapp.setHomeLaunched(false);
			GBApplication.gbapp.setStartActivity(true);
			this.finish();
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		returnGesture.onTouchEvent(event);
		return false;
	}

	@SuppressWarnings("unused")
	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onCloseWindow(WebView window) {
			super.onCloseWindow(window);
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean dialog,
				boolean userGesture, Message resultMsg) {
			return super.onCreateWindow(view, dialog, userGesture, resultMsg);
		}

		/**
		 * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
		 */
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());

			builder.setTitle("对话框").setMessage(message)
					.setPositiveButton("确定", null);

			// 不需要绑定按键事件
			// 屏蔽keycode等于84之类的按键
			builder.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					Log.v("onJsAlert", "keyCode==" + keyCode + "event=" + event);
					return true;
				}
			});
			// 禁止响应按back键的事件
			builder.setCancelable(false);
			AlertDialog dialog = builder.create();
			dialog.show();
			result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
			return true;
			// return super.onJsAlert(view, url, message, result);
		}

		public boolean onJsBeforeUnload(WebView view, String url,
				String message, JsResult result) {
			return super.onJsBeforeUnload(view, url, message, result);
		}

		/**
		 * 覆盖默认的window.confirm展示界面，避免title里显示为“：来自file:////”
		 */
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());
			builder.setTitle("对话框").setMessage(message)
					.setPositiveButton("确定", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					})
					.setNeutralButton("取消", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					});
			builder.setOnCancelListener(new AlertDialog.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					result.cancel();
				}
			});

			// 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
			builder.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					Log.v("onJsConfirm", "keyCode==" + keyCode + "event="
							+ event);
					return true;
				}
			});
			// 禁止响应按back键的事件
			// builder.setCancelable(false);
			AlertDialog dialog = builder.create();
			dialog.show();
			return true;
			// return super.onJsConfirm(view, url, message, result);
		}

		/**
		 * 覆盖默认的window.prompt展示界面，避免title里显示为“：来自file:////”
		 * window.prompt('请输入您的域名地址', '618119.com');
		 */
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, final JsPromptResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());

			builder.setTitle("对话框").setMessage(message);

			final EditText et = new EditText(view.getContext());
			et.setSingleLine();
			et.setText(defaultValue);
			builder.setView(et)
					.setPositiveButton("确定", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm(et.getText().toString());
						}

					})
					.setNeutralButton("取消", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					});

			// 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
			builder.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					Log.v("onJsPrompt", "keyCode==" + keyCode + "event="
							+ event);
					return true;
				}
			});

			// 禁止响应按back键的事件
			// builder.setCancelable(false);
			AlertDialog dialog = builder.create();
			dialog.show();
			return true;
			// return super.onJsPrompt(view, url, message, defaultValue,
			// result);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onReceivedIcon(WebView view, Bitmap icon) {
			super.onReceivedIcon(view, icon);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
		}

		@Override
		public void onRequestFocus(WebView view) {
			super.onRequestFocus(view);
		}
	}

}
