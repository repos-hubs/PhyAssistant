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
 * ҩƷ��������ҳ��
 * 
 * @author simon
 * 
 */
public class DrugDetailActivity extends BaseSearchActivity implements
		OnTouchListener, OnGestureListener, OnClickListener {

	/** ҳ�������� */
	private TextView mainTitle;

	/** ��ǰӦ���Ƿ�Ϊ���Ļ��� */
	private boolean isZh;

	/** ˵�������ͣ���ͨ��ASHF��Ʒ�� */
	private static final int MODE_NORMAL = 0;
	private static final int MODE_AHFS = 1;
	private static final int MODE_BRAND = 2;
	/** ��ǰ��ϸҳ��Ӧ��˵�������� */
	private int currentMode;

	/** ˵������������ */
	private int startMode;

	/**
	 * ������������ҳ��Ϣ��������������
	 */
	/** ����ҩ�� */
	private DrugEDLEntity drugEDL;
	/** ҽ��Ŀ¼ */
	private DrugReimbursementsEntity drugReimbursement;
	/** ������Ϣ */
	private MfgrPriceEntity mMfgrPrice;

	/** ����ҩ������type */
	private final int TYPE_DRUG_EDL = 0;
	/** ҽ��Ŀ¼ ����type */
	private final int TYPE_DRUG_REIMBURSEMENT = 1;
	/** ������Ϣ����type */
	private final int TYPE_DRUG_DRUG_MANUFACTURE_PRICE = 2;

	/** WebView���� */
	private LinearLayout webLay;
	private WebView web;

	/** ���ؽ����� */
	private View progress;

	/** �ײ��˵����� */
	private LinearLayout btnLayout1;
	private LinearLayout btnLayout2;
	private LinearLayout btnLayout3;
	private LinearLayout btnToggleLayout3;
	private LinearLayout btnLayout4;
	/***
	 * �ײ��˵���ť
	 */
	private Button btn1;
	private ToggleButton btn2;
	private Button btn3;
	private ToggleButton btn3toggle;
	private Button btn4;

	/** �������� */
	private GestureDetector returnGesture;

	/** ��ͨ˵����ѡ���б�items */
	private String[] normalItems;
	/** ��ͨ˵����ѡ���б�item��ids */
	private String[] normalItemsId;
	/** AHFSѡ���б�item��ids */
	private String[] ahfsItems;
	/** ����˵����ѡ���б�items */
	private String[] brandItems;

	/** ҩƷ������Ϣ���� */
	private DrugInfo drugInfo;
	/** ҩƷ��ҩ;�� */
	private AdminRouteInfo adminRouteInfo;
	/** ����id */
	private String brandId;

	private String brandName;

	/** AHFS˵�������� */
	public ArrayList<AshfEntity> ashfDatas;

	/** ����˵�������� */
	private ManufutureBrandInfo brandEntity;

	/** ��ǰ��ϸҳ��Ӧ������ѡ���б���±�ֵ */
	private int pos;

	/** ���û��Ƿ�����������û����汾��ӦΪרҵ�� */
	private boolean bPreMode;

	/** htmlҳ����ز��� */
	private final String mimeType = "text/html";
	private final String encoding = "utf-8";

	/**
	 * ��ͨ˵���������£����ݿ��ѯ�첽Task
	 * */
	private NormalReferenceTask task;

	/** db���� */
	private FavoritDataAdapter favoritadpter;

	/** AHFS����ҳ��ȡ���緭�����ݻص� */
	public BaseResponseHandler baseHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drug_detail);
		super.onCreate(savedInstanceState);
		favoritadpter = new FavoritDataAdapter(this);

		// ��ʼ������͹�������
		inits(getIntent());

		// �������
		fillData(getIntent());

	}

	/***
	 * ��ʼ��UI
	 * 
	 * @param intent
	 */
	public void inits(Intent intent) {

		// ҳ�����
		mainTitle = (TextView) findViewById(R.id.txt_header_title);
		// ������
		progress = findViewById(R.id.dialogprogress);

		webLay = (LinearLayout) findViewById(R.id.webview_lay);

		baseHandler = new BaseResponseHandler(this);

		returnGesture = new GestureDetector(this);
		// webLay.setOnTouchListener(this);

		/***
		 * �ײ��˵�
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
		// ������仰����ʹ��javascript����
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setDomStorageEnabled(true);
		// ���ӽӿڷ���,��htmlҳ�����
		web.addJavascriptInterface(new Object() {

			@SuppressWarnings("unused")
			public void more() {
				// �����û�ȥ��ע����
				showDialog(DialogRes.DIALOG_INVITE_NOTIFY);
			}

			/***
			 * ��һ����ϸҳ
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
	 * ����ҳindex
	 */
	private int linkIndex;

	/**
	 * ��ת������ҳ
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
	 * ��ʾ����
	 * 
	 * @param intent
	 */
	private void fillData(Intent intent) {

		Resources res = getResources();
		isZh = Locale.getDefault().getLanguage().contains("zh");

		currentMode = intent.getIntExtra("mode", MODE_NORMAL);

		// ��ҩƷ�б����ҩƷ��ϸ��Ĭ���������ͣ��ǵ�ǰmode
		startMode = intent.getIntExtra("startMode", MODE_NORMAL);

		drugInfo = (DrugInfo) intent.getSerializableExtra("druginfo");

		adminRouteInfo = drugInfo.getAdminRouteInfo();

		brandId = intent.getStringExtra("brandId");

		// �����ղ�
		brandName = intent.getStringExtra("brandName");
		// ����Ƿ��ղ�
		checkFavor(startMode);
		// �ı�ײ��˵���ʽ
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
			// �ⲿת�뿪ʼ������ص�brandEntity
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
	 * ����ղ�״̬
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
	 * �л�˵��������ʱ�ı�ײ��˵���
	 * 
	 * @param mode
	 *            ˵��������
	 */
	private void changeMenuBtn(int mode) {

		switch (mode) {
		case MODE_NORMAL:
			btn1.setBackgroundResource(R.drawable.menu_infset_btn);
			btnLayout3.setVisibility(View.VISIBLE);
			btn3.setBackgroundResource(R.drawable.btn_note);
			// ���ط��벼��
			btnToggleLayout3.setVisibility(View.GONE);
			break;
		case MODE_AHFS:
			btn1.setBackgroundResource(R.drawable.menu_infset_btn);
			// ��ʾ���벼��
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
	 * �ı��ղ�״̬
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
	 * ��ǰҩƷ������Ϣ�Ƿ��Ѿ����ղ�
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

	/** google������AHFS���� */
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
		// �ղ�
		case R.id.tgbtn_2nd:
			pressView(btnLayout2);
			MobclickAgent.onEvent(context, "Favorite", "DrgFavoritBtn", 1);
			uploadLoginLogNew("Drug", "DrgFavoritBtn", "Favorite", null);
			// ������������ǳ��̵�����£��ղس���id
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
		// ����
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
				// �Ǳʼ�
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
	 * �����ť�任����ɫ
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
			// ��ȡ����AHFS���ݺ�ص�
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
			/** ����ǰΪAHFS˵���飬����ʱ�����Ƿ������ */
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
	 * ����AHFS˵���鵱ǰѡ���¶�Ӧ������HTML
	 * 
	 * @param isTranslate
	 * @return
	 */
	private synchronized String buildAHFSString(boolean isTranslate) {
		StringBuffer sb = new StringBuffer();
		if (ashfDatas != null) {
			if (isTranslate) {
				Log.i("simon", "��ǰ״̬Ϊ��Ҫ����");
				if (transData == null || transData.list.size() == 0) {
					Log.i("simon", "�޷������ݣ���������");
					// �����������ݼ���
					reqAhfsTransContent(pos);
				}
			}
			for (int i = 0; i < ashfDatas.size(); i++) {
				Log.i("ashfDatasj", getASHFString(ashfDatas.get(i))+"");
				sb.append(getTitle(ashfDatas.get(i).generalNameEn));
				sb.append(getSplitLine());
				// ��ǰΪ��׼�棬������Ϣ
				// ��Ҫ��ʾ������Ϣ
				if (isTranslate) {
					Log.i("simon", "��ǰ״̬Ϊ��Ҫ����");
					if (transData != null && transData.list.size() > 0) {
						Log.i("simon", "��ǰ״̬Ϊ��Ҫ����,�����з������ݣ�ֱ����ʾ");
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
					Log.i("simon", "��ǰ״̬Ϊ��    ��Ҫ����");
				}
				sb.append(getASHFString(ashfDatas.get(i)));
			}
			// ����Ƿ�����Ӧ����
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
	 * �ı�webview�����С
	 */
	@SuppressWarnings("unused")
	private void updateTextSize() {
		web.getSettings().setDefaultFontSize(fontSize++);
	}

	/**
	 * ���ɳ���˵���鵱ǰѡ���¶�Ӧ������HTML
	 * 
	 * @param isTranslate
	 * @return
	 */
	private String buildBrandString() {
		String content = getBrandInfo();
		StringBuffer buffer = new StringBuffer();
		if (!TextUtils.isEmpty(content)) {
			// ҩƷ��+�ָ���
			buffer.append(getDrugNameTitle());
			buffer.append(getSplitLine());
			buffer.append(content);
		} else {
			buffer.append(getHtml_P_Text(getString(R.string.nodata)));
		}
		// ����Ƿ�����Ӧ����
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
	 * ��ȡѡ��index��Ӧ��������Ϣ
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

	/** ҩ���໥�������� */
	DrugInteractionEntity drugInteractions;

	/**
	 * ��ȡ��ͨ˵��������
	 * 
	 * @param index
	 *            ��ϸҳѡ���±�
	 * */
	private void reqNormalData(int index) {
		showProgress();
		if (index < 10) {
			task = new NormalReferenceTask(index);
			task.execute(drugInfo.getId(), adminRouteInfo == null ? ""
					: adminRouteInfo.getId(), normalItemsId[index]);
		} else {
			// soap ����
			reqSoap(index, index - 10);
		}
	}

	/**
	 * ��ȡAHFS˵��������
	 * 
	 * @param index
	 *            ѡ���±�
	 */
	public void reqAHFSData(int index) {
		pos = index;
		transData = null;
		updateWebContent(buildAHFSString(isTranslate));
	}

	/**
	 * ��ȡAHFS���������
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
	 * ��ȡ����˵��������
	 * 
	 * @param index
	 *            ѡ���±�
	 */
	private void reqBrandData(int index) {
		showProgress();
		if (index < 7) {
			pos = index;
			updateWebContent(buildBrandString());
		} else {
			// soap ����
			reqSoap(index, index - 7);
		}
	}

	/**
	 * ������������(����ҩ��Ŀ¼��ҽ��Ŀ¼��������Ϣ)
	 * 
	 * @param index
	 *            ҳ���±�
	 * @param type
	 *            ������������
	 */
	private void reqSoap(int index, int type) {
		int methodId = 0;
		switch (type) {
		case TYPE_DRUG_EDL:// ����ҩ��Ŀ¼
			if (drugEDL != null) {
				updateWebContent(buildDrugEDL());
				return;
			}
			methodId = SoapRes.REQ_ID_DRUG_EDL;
			break;
		case TYPE_DRUG_REIMBURSEMENT:// ҽ��Ŀ¼
			if (drugReimbursement != null) {
				updateWebContent(buildDrugReimbursement());
				return;
			}
			methodId = SoapRes.REQ_ID_DRUG_REIMBURSEMENT;
			break;
		case TYPE_DRUG_DRUG_MANUFACTURE_PRICE:// ������Ϣ
			if (mMfgrPrice != null) {
				updateWebContent(buildDrugManufacturersPrice());
				return;
			}
			methodId = SoapRes.REQ_ID_DRUG_MANUFACTURE_PRICE;
			break;
		}
		// �û�ҽԺ����ʡ�ݻ�ֱϽ��
		String region = SharedPreferencesMgr.getRegion();
		if (region == null || region.equals("")) {
			// �������û��docId����ʾ�û���ȫ��Ϣ��
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
	 * ��ȡ��ͨ˵������ϸ��Ϣ
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

			// ��InsertType = Special
			// Population,����Ϊ"������Ⱥ"ʱ����Ӧ���ݿ������Ϊ����PediatricUse(��ͯ)��GeriatricUse(������)��Pregnancy(��Ů)
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
	 * ��ʼ��webView����(����������updateContent������ĳЩ������޷���ֵ���룬�����൱���ƣ���ʱ��֪��ʲôԭ�򣬿������ַ�����)
	 */
	@SuppressWarnings("unused")
	private void initWebViewContent(WebView web) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		buffer.append("<head>");
		buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		buffer.append("<script type=\"text/javascript\"> ");

		// ����WebView����function( ĳЩ�����ʧЧ)
		buffer.append("function updateTextSize(){ ");
		buffer.append(" document.getElementById(\"content\").style.webkitTextSizeAdjust = '%d%%'; ");
		buffer.append("}");

		// ����WebView����function( ĳЩ�����ʧЧ)
		buffer.append("function updateContent(content){ ");
		buffer.append("  document.getElementById(\"content\").innerHTML = content; ");
		buffer.append("}");
		// ҳ�������ɺ�,Ĭ�Ϲ���������
		buffer.append("function init(){ ");
		buffer.append("  window.scrollBy(0, 0); ");
		buffer.append("}");
		// buffer.append("function ondownloading(){ ");
		// buffer.append("   document.getElementById(\"download\").disabled=\"disabled\";");
		// buffer.append("   document.getElementById(\"download\").value=\"������...\"");
		// buffer.append("}");
		// buffer.append(" function finishdownload(){");
		// buffer.append("   document.getElementById(\"download\").disabled=\"disabled\";");
		// buffer.append("   document.getElementById(\"download\").value=\"�������\"");
		// buffer.append("}");
		buffer.append("</script>");
		buffer.append("<title>�ޱ����ĵ�</title>");
		buffer.append("</head>");
		buffer.append("<body onload=\"init();\">");
		buffer.append("<div id=\"content\">");
		buffer.append("</div></body></html>");
		web.loadDataWithBaseURL(null, buffer.toString(), mimeType, encoding,
				null);
	}

	/** Html��ǩͷ */
	private String webHeadContent;
	/** Html��ǩβ */
	private String webFootContent;

	/**
	 * ��ȡHtmlͷ����Ϣ
	 */
	private String getWebHeadContent() {
		if (null == webHeadContent) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			buffer.append("<head>");
			buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			buffer.append("<script type=\"text/javascript\"> ");
			// ��������
			buffer.append("function updateTextSize(){ ");
			buffer.append(" document.getElementById(\"content\").style.webkitTextSizeAdjust = '10%'; ");
			buffer.append("}");
			// ����WebView����function
			buffer.append("function updateContent(content){ ");
			buffer.append("  document.getElementById(\"content\").innerHTML = content; ");
			buffer.append("}");
			// ҳ�������ɺ�,Ĭ�Ϲ���������
			buffer.append("function init(){ ");
			buffer.append("  window.scrollBy(0, 0); ");
			buffer.append("}");
			buffer.append("</script>");
			buffer.append("<title>�ޱ����ĵ�</title>");
			buffer.append("</head>");
			buffer.append("<body onload=\"init();\">");
			buffer.append("<div id=\"content\">");
			webHeadContent = buffer.toString();
		}
		return webHeadContent;
	}

	/***
	 * ��ȡHtml�ײ���Ϣ
	 */
	private String getWebFootContent() {
		if (null == webFootContent) {
			webFootContent = "</div></body></html>";
		}
		return webFootContent;
	}

	/**
	 * ��ȡstring��Ӧ������Դ�ַ�
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
	 * �ϲ� ����ҩ��Ŀ¼ ��Ϣ(������Դ������)
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
			if (!national.equals(tempNational)) {// ���Ҳ��
				buffer.append(getHtml_P_Text_with_blackColor(national));
				buffer.append(getHtml_P_Text_with_blackColor(nFormulation
						.substring(0, nFormulation.length() - 1)));
				buffer.append(getHtml_P_Text_with_blackColor(nCategoryOfUse
						.substring(0, nCategoryOfUse.length() - 1)));
				buffer.append(getSplitLine());
			}

			if (!regional.equals(tempRegional)) {// �ط����
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
	 * �ϲ� ҽ��Ŀ¼ ��Ϣ(������Դ������)
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
	 * �ϲ�������Ϣ����(������Դ������)
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

		// ��������
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
	 * �ϲ���ͨ˵������ϸ����(��������)
	 * 
	 * @param list
	 * @return
	 */
	private String buildNormalString(List<DrugDetailTypeInfo> list) {

		StringBuffer buffer = new StringBuffer();
		// ҩƷ��+�ָ���
		buffer.append(getDrugNameTitle());
		buffer.append(getSplitLine());
		if (list != null && list.size() > 0) {
			if (daoSession != null) {
				for (int i = 0; i < list.size(); i++) {
					DrugDetailTypeInfo obj = list.get(i);
					// ʵ���ڵĻỰҪ��ʱ���¡���Ϊactivity���ڱ仯��daoSession������仯��ԭdb�����Ѿ��ر�
					obj.__setDaoSession(daoSession);
					// ��ҩ����+":"+ҩƷ����
					buffer.append(getHtml_P_Text((adminRouteInfo == null ? ""
							: (isZh ? adminRouteInfo.getNameCn()
									: adminRouteInfo.getNameEn()) + ":")// ��ҩ����
							+ (isZh ? obj.getDrugDetailInfo()
									.getFormulationInfo().getNameCn() : obj
									.getDrugDetailInfo().getFormulationInfo()
									.getNameEn()) + "<br/>"));// ҩƷ����
					// ��ʾ�÷�����ʱ����Ҫ��ʾ���
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

					// ��������
					String content = obj.getContent();
					content = content.replaceAll("\\[nn\\]", "<br/>");
					buffer.append(getHtml_P_Text_with_blackColor(content));
					// ��˾��Ϣ
					buffer.append("<p style=\"margin:20 0 0 0;padding:2px;font-size:15;text-align: right;color:#004371;\">");
					buffer.append(isZh ? obj.getDrugDetailInfo()
							.getCompanyInfo().getNameCn() : obj
							.getDrugDetailInfo().getCompanyInfo().getNameEn());
					buffer.append("</p>");
					// ʱ��
					buffer.append("<p style=\"margin:0 0 20 0 ;padding:2px;font-size:15;text-align: right;color:#004371;\">");
					buffer.append(getString(R.string.updatetime)
							+ obj.getDrugDetailInfo().getChangeDate());
					buffer.append("</p>");

					// �����Ϣ�ָ���
					if (i < list.size() - 1)
						buffer.append(getSplitLine());
				}
			}
		} else {
			buffer.append(getHtml_P_Text_with_blackColor(getString(R.string.nodata)));
		}
		// ����Ƿ�����Ӧ����
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
	 * ҩƷ����html��ǩ
	 */
	private String drugNameHtmlText;

	/**
	 * ��ȡҩƷ����Title
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
	 * ���ı�ת��Ϊhtml�е�һ������
	 * 
	 * @param text
	 *            �ı�����
	 * @return
	 */
	private String getHtml_P_Text(String text) {
		return getHtml_P_Text(text, "#004371");
	}

	private String getHtml_P_Text_with_blackColor(String text) {
		return getHtml_P_Text(text, "#000000");
	}

	/**
	 * ���ı�ת��Ϊhtml�е�һ������
	 * 
	 * @param text
	 *            �ı�����
	 * @param colorString
	 *            ��ɫֵ
	 * @return
	 */
	private String getHtml_P_Text(String text, String colorString) {
		return "<p style=\"margin:0;padding:2px;color:" + colorString + ";\">"
				+ text + "</p>";
	}

	/***
	 * �ָ���
	 */
	private String splitLine;

	/**
	 * ��ȡһ��html�ָ���
	 * 
	 * @return
	 */
	private String getSplitLine() {
		if (null == splitLine)
			splitLine = "<hr style=\"filter: alpha(opacity=100,finishopacity=0,style=3)\" width=\"100%\" color=#b2b5b7 size=1/>";
		return splitLine;
	}

	/**
	 * ��ȡ��Ӧ����ҳ����±�, ����ҳ�໥���ӵĹ����漰4��ҳ�档 ��ת����Ϊ�� 1����Ӧ֢<->����֢��2.�÷�����<->������Ⱥ��ʾ
	 * (�˹������������ֲ�ͬ˵����)
	 * 
	 * @return
	 */
	private int getLinkIndex() {
		// ��Ӧѡ���±��ڲ�ͬ˵�����²�ͬ
		switch (currentMode) {
		case MODE_NORMAL:
			switch (pos) {
			case 0:// ��Ӧ֢
				return 4;
			case 1:// �÷�����
				return 2;
			case 2:// ������Ⱥ��ʾ
				return 1;
			case 4:// ����֢
				return 0;
			}
			break;
		case MODE_AHFS:
		case MODE_BRAND:
			switch (pos) {
			case 0:// ��Ӧ֢
				return 3;
			case 1:// �÷�����
				return 5;
			case 3:// ����֢
				return 0;
			case 5:// ������Ⱥ��ʾ
				return 1;
			}
			break;
		}
		return -1;
	}

	/***
	 * ����webview
	 */
	private void destoryWebView() {
		if (web != null)
			web.destroy();
	}

	/***
	 * �޸�webView������
	 * 
	 * @param text
	 */
	private void updateWebContent(String text) {
		closeProgress();
		if (text == null)
			return;
		mainTitle.setText(getItemString(pos));
		// �������ڲ鿴AHFS˵����ʱ����Щ���͵���������text��������js�����У�ԭ����ʱδ֪
		// ��Ϊÿ������load����
		// web.loadUrl("javascript:updateContent('" + text + "')");
		webLay.removeAllViews();
		destoryWebView();
		web = createWebView();
		web.loadDataWithBaseURL(null, getWebHeadContent() + text
				+ getWebFootContent(), mimeType, encoding, null);
		webLay.addView(web);
		Log.i("simon", "����loadҳ��");
	}

	/**
	 * ��ȡ��Ӧ�±��ѡ��ֵ
	 * 
	 * @param index
	 *            �±�
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
	// * ��ȡ��һ����ϸ���ݵ�title
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
	// * ��ȡ��һ����ϸ���ݵ�index
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
	 * ��ȡ��ǰѡ���ӦAHFS����
	 * 
	 * @param entity
	 *            AHFSʵ��
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
	 * ��ȡ�±��Ӧ��AHFS�������������key
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
	 * soap���ݴ�����
	 * 
	 * ��ȡ������������(����ҩ��,ҽ��Ŀ¼,������Ϣ)
	 * */
	class SoapCallBack extends AsyncSoapResponseHandler {
		/** ��ǰѡ��ҳ�±�index */
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
		 * ����Ĭ�ϵ�window.alertչʾ���棬����title����ʾΪ��������file:////��
		 */
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());

			builder.setTitle("�Ի���").setMessage(message)
					.setPositiveButton("ȷ��", null);

			// ����Ҫ�󶨰����¼�
			// ����keycode����84֮��İ���
			builder.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					Log.v("onJsAlert", "keyCode==" + keyCode + "event=" + event);
					return true;
				}
			});
			// ��ֹ��Ӧ��back�����¼�
			builder.setCancelable(false);
			AlertDialog dialog = builder.create();
			dialog.show();
			result.confirm();// ��Ϊû�а��¼�����Ҫǿ��confirm,����ҳ�������ʾ�������ݡ�
			return true;
			// return super.onJsAlert(view, url, message, result);
		}

		public boolean onJsBeforeUnload(WebView view, String url,
				String message, JsResult result) {
			return super.onJsBeforeUnload(view, url, message, result);
		}

		/**
		 * ����Ĭ�ϵ�window.confirmչʾ���棬����title����ʾΪ��������file:////��
		 */
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());
			builder.setTitle("�Ի���").setMessage(message)
					.setPositiveButton("ȷ��", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					})
					.setNeutralButton("ȡ��", new AlertDialog.OnClickListener() {
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

			// ����keycode����84֮��İ��������ⰴ�����¶Ի�����Ϣ��ҳ���޷��ٵ����Ի��������
			builder.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					Log.v("onJsConfirm", "keyCode==" + keyCode + "event="
							+ event);
					return true;
				}
			});
			// ��ֹ��Ӧ��back�����¼�
			// builder.setCancelable(false);
			AlertDialog dialog = builder.create();
			dialog.show();
			return true;
			// return super.onJsConfirm(view, url, message, result);
		}

		/**
		 * ����Ĭ�ϵ�window.promptչʾ���棬����title����ʾΪ��������file:////��
		 * window.prompt('����������������ַ', '618119.com');
		 */
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, final JsPromptResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());

			builder.setTitle("�Ի���").setMessage(message);

			final EditText et = new EditText(view.getContext());
			et.setSingleLine();
			et.setText(defaultValue);
			builder.setView(et)
					.setPositiveButton("ȷ��", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm(et.getText().toString());
						}

					})
					.setNeutralButton("ȡ��", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					});

			// ����keycode����84֮��İ��������ⰴ�����¶Ի�����Ϣ��ҳ���޷��ٵ����Ի��������
			builder.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					Log.v("onJsPrompt", "keyCode==" + keyCode + "event="
							+ event);
					return true;
				}
			});

			// ��ֹ��Ӧ��back�����¼�
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
