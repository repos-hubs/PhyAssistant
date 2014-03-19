package com.jibo.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.adapter.SurveyHistoryAdapter;
import com.jibo.adapter.SurveyListAdapter;
import com.jibo.common.SoapRes;
import com.jibo.data.PayInfoSubmitPaser;
import com.jibo.data.SurveyGetPayInfo;
import com.jibo.data.SurveyInfoPaser;
import com.jibo.data.SurveyMineHistory;
import com.jibo.data.entity.PayInfoEntity;
import com.jibo.data.entity.SurveyEntity;
import com.jibo.data.entity.SurveyHistoryEntity;
import com.jibo.dbhelper.SurveyAdapter;
import com.jibo.dbhelper.SurveyQuestionAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.tips.Mask;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

public class NewSurveyActivity extends BaseSearchActivity implements
		OnClickListener {
	private LinearLayout lltSurveyContent;
	private GridView gvList;
	private ScrollView svMine;
	private ScrollView lltIntro;
	private RelativeLayout lltPayment;
	private LinearLayout lltPaymentInput;
	private RadioGroup rgPayment;
	private TextView txtPaymentInput;
	private TextView txtTitle;
	private GBApplication app;
	private String userName;

	private Button btnList;
	private Button btnMine;
	private Button btnPayment;
	private Button btnIntro;

	private Button btnPayConfirm;

	private LayoutInflater inflater;
	private SurveyListAdapter surveyListAdapter;
	private SurveyAdapter surveyAdapter;
	public ArrayList<SurveyEntity> surveyList;
	public HashSet<SurveyEntity> set = new HashSet<SurveyEntity>();
	private BaseResponseHandler baseHandler;
	private ArrayList<SurveyHistoryEntity> historyList;
	private SurveyHistoryAdapter historyAdapter;
	private SurveyQuestionAdapter questionAdapter;
	private EditText edtName;
	private EditText edtPhone;
	private EditText edtCity;
	private EditText edtHospital;
	private EditText edtDepartment;
	private EditText edtTitle;
	private EditText edtAccount;
	private RadioGroup rgPayWay;
	// private TimerTask iconTimer;
	// private Timer timer;
	private View empty_survey_view;
	private final int update_new_flag = 0;
	int i = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			i++;
			switch (msg.what) {
			case update_new_flag:
				ImageView img = (ImageView) msg.obj;
				if (img != null) {
					if (i % 2 == 0) {
						img.setVisibility(LinearLayout.VISIBLE);
					} else {
						img.setVisibility(LinearLayout.INVISIBLE);
					}
				}

				break;
			}
		};
	};

	// private BitmapText bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_survey);
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		try {

			inits();
			uploadLoginLogNew("Activity", "Survey", "create", null);
			if (app.getDeviceInfo().isNetWorkEnable()) {
				checkUpdate();
			} else {
				surveyList = surveyAdapter.getSurveyList(userName);
				surveyListAdapter = new SurveyListAdapter(this, surveyList,
						userName);
				surveyAdapter.updateSurvey(surveyList, app.getLogin()
						.getGbUserName());
				gvList.setAdapter(surveyListAdapter);
				// timer.schedule(iconTimer, 2000, 2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			if (o instanceof SurveyInfoPaser) {
				SurveyInfoPaser surveyInfoPaser = (SurveyInfoPaser) o;
				surveyList = surveyInfoPaser.getSurveyList();
				
				set.addAll(surveyList);
				
				set.addAll(surveyAdapter.getSurveyList(app.getLogin()
						.getGbUserName()));
				surveyList = new ArrayList<SurveyEntity>(set);
				System.out.println("surveyList   " + surveyList.size());
				if (surveyList.size() > 0) {
					surveyListAdapter = new SurveyListAdapter(this, surveyList,
							userName);
					surveyAdapter.updateSurvey(surveyList, app.getLogin()
							.getGbUserName());
					// bt = new BitmapText(this, "" + surveyList.size(),
					// R.drawable.survey_list_press);
					// Bitmap bm = Bitmap.createBitmap(bt.getBitmap(), 0, 0,
					// btnList.getWidth(), btnList.getHeight());
					// btnList.setBackgroundDrawable(bt);
					gvList.setAdapter(surveyListAdapter);
					// timer.schedule(iconTimer, 2000, 2000);
				} else {

					lltSurveyContent.removeAllViews();
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					empty_survey_view = LayoutInflater.from(
							getApplicationContext()).inflate(
							R.layout.empty_frame, null);
					((TextView) empty_survey_view.findViewById(R.id.emptytext))
							.setText(R.string.empty_survey);
					lltSurveyContent.addView(empty_survey_view, params);
				}

			} else if (o instanceof SurveyMineHistory) {
				SurveyMineHistory mineHistory = (SurveyMineHistory) o;
				historyList = mineHistory.getHistoryList();
				if (historyList.size() > 0) {
					lltSurveyContent.addView(createMineView());
				} else {
					lltSurveyContent.removeAllViews();
					TextView txt = new TextView(this);
					txt.setTextColor(Color.BLACK);
					txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					txt.setText(R.string.survey_no_history);
					lltSurveyContent.addView(txt);
				}
			} else if (o instanceof SurveyGetPayInfo) {
				SurveyGetPayInfo payInfoPaser = (SurveyGetPayInfo) o;
				PayInfoEntity en = payInfoPaser.getPayInfo();
				app.setPayInfo(en);
				lltSurveyContent.addView(lltPayment);

				Button btn = (Button) lltPayment
						.findViewById(R.id.btn_pay_confirm);
				ImageView imgIsCheck = (ImageView) lltPayment
						.findViewById(R.id.img_isCheck);
				if ("1".equals(en.getIsCheck()))
					imgIsCheck.setBackgroundResource(R.drawable.verified);

				btn.setOnClickListener(this);
				edtName.setText(en.getName());
				edtPhone.setText(en.getPhone());
				edtCity.setText(en.getCity());
				edtHospital.setText(en.getHospital());
				edtDepartment.setText(en.getDepartment());
				edtTitle.setText(en.getProfessional());

				edtName.setVisibility(LinearLayout.GONE);
				edtPhone.setVisibility(LinearLayout.GONE);
				edtCity.setVisibility(LinearLayout.GONE);
				edtHospital.setVisibility(LinearLayout.GONE);
				edtDepartment.setVisibility(LinearLayout.GONE);
				edtTitle.setVisibility(LinearLayout.GONE);
				if (en.getPayWay().equals("zfb")) {
					rgPayWay.setTag("zfb");
					((RadioButton) rgPayment.getChildAt(0)).setChecked(true);
					edtAccount.setText(en.getPayAccount());
				} else {
					rgPayWay.setTag("mobile");
					((RadioButton) rgPayment.getChildAt(1)).setChecked(true);
					if ("".equals(en.getPayAccount())) {
						edtAccount.setText(en.getPhone());
					} else {
						edtAccount.setText(en.getPayAccount());
					}

				}

			} else if (o instanceof PayInfoSubmitPaser) {
				PayInfoSubmitPaser paser = (PayInfoSubmitPaser) o;
				String submitResult = paser.getSubmitResult();
				if ("1".equals(submitResult)) {

				} else {

				}
			}
		}
		super.onReqResponse(o, methodId);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_survey_list:
			lltSurveyContent.removeAllViews();
			txtTitle.setText(getString(R.string.survey_title_list));
			lltSurveyContent.addView(gvList);
			btnList.setBackgroundResource(R.drawable.survey_list_press);
			btnMine.setBackgroundResource(R.drawable.survey_mine_normal);
			btnPayment.setBackgroundResource(R.drawable.survey_payment_normal);
			btnIntro.setBackgroundResource(R.drawable.survey_intro_normal);
			// if(){
			// empty_survey_view
			// }
			// if(timer == null) {
			// timer = new Timer();
			// timer.schedule(iconTimer, 2000, 2000);
			// }
			break;
		case R.id.btn_survey_mine:
			// timer.cancel();
			lltSurveyContent.removeAllViews();
			txtTitle.setText(getString(R.string.survey_title_history));
			btnList.setBackgroundResource(R.drawable.survey_list_normal);
			btnMine.setBackgroundResource(R.drawable.survey_mine_press);
			btnPayment.setBackgroundResource(R.drawable.survey_payment_normal);
			btnIntro.setBackgroundResource(R.drawable.survey_intro_normal);
			if (historyList == null) {
				checkHistoryMine();
			} else {
				lltSurveyContent.addView(createMineView());
			}
			break;
		case R.id.btn_survey_payment:
			// timer.cancel();
			lltSurveyContent.removeAllViews();
			btnList.setBackgroundResource(R.drawable.survey_list_normal);
			btnMine.setBackgroundResource(R.drawable.survey_mine_normal);
			btnPayment.setBackgroundResource(R.drawable.survey_payment_press);
			btnIntro.setBackgroundResource(R.drawable.survey_intro_normal);
			txtTitle.setText(getString(R.string.survey_title_setup));
			checkPayInfo();
			break;
		case R.id.btn_survey_intro:
			// timer.cancel();
			lltSurveyContent.removeAllViews();
			txtTitle.setText(getString(R.string.survey_title_intro));
			lltSurveyContent.addView(lltIntro);
			btnList.setBackgroundResource(R.drawable.survey_list_normal);
			btnMine.setBackgroundResource(R.drawable.survey_mine_normal);
			btnPayment.setBackgroundResource(R.drawable.survey_payment_normal);
			btnIntro.setBackgroundResource(R.drawable.survey_intro_press);
			Button btnFB = (Button) lltIntro.findViewById(R.id.btn_feedback);
			btnFB.setOnClickListener(this);
			break;
		case R.id.btn_pay_confirm:

			String payName = edtName.getText().toString();
			String payPhone = edtPhone.getText().toString();
			String payCity = edtCity.getText().toString();
			String payHospital = edtHospital.getText().toString();
			String payDpt = edtDepartment.getText().toString();
			String payTitle = edtTitle.getText().toString();
			String payAccount = edtAccount.getText().toString();

			app.getPayInfo().setName(payName);
			app.getPayInfo().setPhone(payPhone);
			app.getPayInfo().setCity(payCity);
			app.getPayInfo().setHospital(payHospital);
			app.getPayInfo().setDepartment(payDpt);
			app.getPayInfo().setProfessional(payTitle);
			app.getPayInfo().setPayAccount(payAccount);
			updateUserPayInfo(app.getPayInfo());
			break;
		case R.id.btn_feedback:
			UMFeedbackService.openUmengFeedbackSDK(context);
			finish();
			break;
		}
	}

	private class HistoryClickListener implements OnClickListener {
		private SurveyHistoryEntity en;

		public HistoryClickListener(SurveyHistoryEntity en) {
			this.en = en;
		}

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if ("0".equals(v.getTag().toString())) {
				v.setTag("1");
				v.setBackgroundResource(R.drawable.survey_history_bg_press);
				LinearLayout lltMine = (LinearLayout) svMine.getChildAt(0);
				LinearLayout lltItemDetail = (LinearLayout) lltMine
						.getChildAt(i + 1);
				lltItemDetail.setVisibility(LinearLayout.VISIBLE);
				TextView txtUpdateTime = new TextView(NewSurveyActivity.this);
				txtUpdateTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
				txtUpdateTime.setTextColor(Color.BLACK);
				txtUpdateTime.setText(getString(R.string.survey_status_change));
				txtUpdateTime.append(en.getUpdateTime());
				TextView txtUpdateReason = new TextView(NewSurveyActivity.this);
				txtUpdateReason.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
				txtUpdateReason.setTextColor(Color.BLACK);
				txtUpdateReason.setText(getString(R.string.survey_status_memo));
				txtUpdateReason.append(en.getResultRemark());
				lltItemDetail.addView(txtUpdateTime);
				lltItemDetail.addView(txtUpdateReason);
			} else if ("1".equals(v.getTag().toString())) {
				v.setTag("0");
				v.setBackgroundResource(R.drawable.survey_history_bg_normal);
				LinearLayout lltMine = (LinearLayout) svMine.getChildAt(0);
				LinearLayout lltItemDetail = (LinearLayout) lltMine
						.getChildAt(i + 1);
				lltItemDetail.removeAllViews();
				lltItemDetail.setVisibility(LinearLayout.GONE);
			}

		}
	}

	private class NewIconTimer extends TimerTask {
		@Override
		public void run() {
			System.out.println("gvList.getChildCount()   "
					+ gvList.getChildCount());
			for (int i = 0; i < gvList.getChildCount(); i++) {
				View view = gvList.getChildAt(i);
				System.out.println("view   " + view);
				ImageView img = (ImageView) view
						.findViewById(R.id.img_survey_new_icon);
				Message msg = handler.obtainMessage(update_new_flag, img);
				handler.sendMessage(msg);
			}

		}
	}

	private View createMineView() {
		svMine = new ScrollView(this);
		LinearLayout lltMine = new LinearLayout(this);
		lltMine.setPadding(20, 20, 20, 20);
		lltMine.setOrientation(LinearLayout.VERTICAL);
		int count = 0;
		for (int i = 0; i < historyList.size(); i++) {
			SurveyHistoryEntity en = historyList.get(i);
			LinearLayout lltItem = new LinearLayout(this);
			lltItem.setId(count);
			lltItem.setOrientation(LinearLayout.VERTICAL);
			lltItem.setBackgroundResource(R.drawable.survey_history_bg_normal);
			lltItem.setOnClickListener(new HistoryClickListener(en));
			lltItem.setTag("0");

			LinearLayout lltItem1 = new LinearLayout(this);
			lltItem1.setPadding(10, 0, 10, 0);
			LayoutParams lpItem1 = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			lltItem1.setOrientation(LinearLayout.HORIZONTAL);
			TextView txtTitle = new TextView(this);
			txtTitle.setText(en.getTitle());
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			txtTitle.setTextColor(Color.BLACK);
			LayoutParams lpTitle = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, 1);
			ImageView imgIcon = new ImageView(this);
			LayoutParams lpImg = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lpImg.rightMargin = 20;
			lpImg.topMargin = 10;
			setHistoryIcon(imgIcon, en);
			lltItem1.addView(txtTitle, lpTitle);
			lltItem1.addView(imgIcon);

			LinearLayout lltItem2 = new LinearLayout(this);
			lltItem2.setPadding(10, 0, 10, 0);
			lltItem2.setOrientation(LinearLayout.HORIZONTAL);
			TextView txtTime = new TextView(this);
			txtTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			txtTime.setTextColor(Color.BLACK);
			txtTime.setText(en.getSubmitTime());
			TextView txtPay = new TextView(this);
			txtPay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			txtPay.setTextColor(Color.BLACK);
			txtPay.setText(en.getPay());
			lltItem2.addView(txtTime);
			lltItem2.addView(txtPay);

			LinearLayout lltItemDetail = new LinearLayout(this);
			lltItemDetail.setOrientation(LinearLayout.VERTICAL);
			lltItemDetail
					.setBackgroundResource(R.drawable.academic_profile_content);
			lltItemDetail.setVisibility(LinearLayout.GONE);

			lltItem.addView(lltItem1, lpItem1);
			lltItem.addView(lltItem2);
			lltMine.addView(lltItem);
			lltMine.addView(lltItemDetail);
			count = count + 2;
		}

		svMine.addView(lltMine);
		return svMine;
	}

	private void setHistoryIcon(ImageView imgStatus, SurveyHistoryEntity en) {
		if ("0".equals(en.getSurveyStatus())) {
			imgStatus.setBackgroundResource(R.drawable.status_audit_fail);
		} else if ("1".equals(en.getSurveyStatus())) {
			imgStatus.setBackgroundResource(R.drawable.status_wait_audit);
		} else if ("2".equals(en.getSurveyStatus())) {
			imgStatus.setBackgroundResource(R.drawable.status_auditing);
		} else if ("3".equals(en.getSurveyStatus())) {
			imgStatus.setBackgroundResource(R.drawable.status_auditing);
		} else if ("4".equals(en.getSurveyStatus())) {
			imgStatus.setBackgroundResource(R.drawable.status_audit_fail);
		} else if ("5".equals(en.getSurveyStatus())) {
			imgStatus.setBackgroundResource(R.drawable.status_pay_success);
		}
	}

	private void checkPayInfo() {
		Properties pro = new Properties();
		pro.put("userName", app.getLogin().getGbUserName());
		sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_GET_PAY_INFO, pro,
				baseHandler);
	}

	private void checkHistoryMine() {
		Properties pro = new Properties();
		System.out.println("checkHistoryMine    "
				+ app.getLogin().getGbUserName());
		pro.put("userName", app.getLogin().getGbUserName());
		pro.put("RecordID", "");
		pro.put("pageSize", "");
		pro.put("pageType", "");
		pro.put("language", "");
		sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_GET_SURVEY_HISTORY, pro,
				baseHandler);
	}

	private void updateUserPayInfo(PayInfoEntity en) {
		Properties pro = new Properties();
		pro.put(SoapRes.KEY_PAY_INFO_USERNAME, app.getLogin().getGbUserName());
		pro.put(SoapRes.KEY_PAY_INFO_NAME, en.getName());
		pro.put(SoapRes.KEY_PAY_INFO_PHONE, en.getPhone());
		pro.put(SoapRes.KEY_PAY_INFO_REGION, en.getRegion());
		pro.put(SoapRes.KEY_PAY_INFO_CITY, en.getCity());
		pro.put(SoapRes.KEY_PAY_INFO_HOSPITAL, en.getHospital());
		pro.put(SoapRes.KEY_PAY_INFO_DPT, en.getDepartment());
		pro.put(SoapRes.KEY_PAY_INFO_TITLE, en.getProfessional());
		pro.put(SoapRes.KEY_PAY_INFO_WAY, en.getPayWay());
		pro.put(SoapRes.KEY_PAY_INFO_ACCOUNT, en.getPayAccount());
		sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_UPDATE_PAY_INFO, pro,
				baseHandler);
	}

	private class RGCheckedListner implements OnCheckedChangeListener {
		private EditText edt;

		public RGCheckedListner(EditText edt) {
			this.edt = edt;
		}

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			lltPaymentInput.setVisibility(LinearLayout.VISIBLE);
			edt.setText("");
			switch (checkedId) {
			case R.id.rbt_alipay:
				txtPaymentInput.setText(getString(R.string.survey_payway_zfb));
				app.getPayInfo().setPayWay("zfb");
				break;
			case R.id.rbt_mobile:
				txtPaymentInput
						.setText(getString(R.string.survey_payway_mobile));
				app.getPayInfo().setPayWay("mobile");
				break;
			}
		}
	}

	private class GVClickedListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			SurveyEntity en = surveyList.get(arg2);
			Intent intent = new Intent(NewSurveyActivity.this,
					SurveyQuestionActivity.class);
			intent.putExtra("en", en);
			MobclickAgent.onEvent(context, "survey_id", en.getID(), 1);
			surveyAdapter.setStatus(en.getID(), userName, "1");
			startActivity(intent);
		}
	}

	public void inits() {
		app = (GBApplication) getApplication();
		inflater = LayoutInflater.from(this);
		surveyAdapter = new SurveyAdapter(this, 1);
		userName = app.getLogin().getGbUserName();
		baseHandler = new BaseResponseHandler(this);
		questionAdapter = new SurveyQuestionAdapter(this, 1);
		surveyList = new ArrayList<SurveyEntity>();
		// iconTimer = new NewIconTimer();
		// timer = new Timer();

		// createSurveyList();

		lltSurveyContent = (LinearLayout) findViewById(R.id.llt_survey_content);
		gvList = (GridView) inflater
				.inflate(R.layout.survey_content_list, null);
		lltIntro = (ScrollView) inflater.inflate(R.layout.survey_content_intro,
				null);
		lltPayment = (RelativeLayout) inflater.inflate(
				R.layout.survey_content_payment, null);
		lltPaymentInput = (LinearLayout) lltPayment
				.findViewById(R.id.llt_payment_input);
		rgPayment = (RadioGroup) lltPayment.findViewById(R.id.rg_payment);
		txtPaymentInput = (TextView) lltPayment
				.findViewById(R.id.txt_payment_input);
		edtName = (EditText) lltPayment.findViewById(R.id.edt_userInfo_name);
		edtPhone = (EditText) lltPayment.findViewById(R.id.edt_userInfo_phone);
		edtCity = (EditText) lltPayment.findViewById(R.id.edt_userInfo_city);
		edtHospital = (EditText) lltPayment
				.findViewById(R.id.edt_userInfo_hospital);
		edtDepartment = (EditText) lltPayment
				.findViewById(R.id.edt_userInfo_dpt);
		edtTitle = (EditText) lltPayment.findViewById(R.id.edt_userInfo_title);
		edtAccount = (EditText) lltPayment.findViewById(R.id.edt_payment_input);
		rgPayWay = (RadioGroup) lltPayment.findViewById(R.id.rg_payment);

		txtTitle = (TextView) findViewById(R.id.txt_header_title);
		txtTitle.setText(getString(R.string.survey));
		rgPayment.setOnCheckedChangeListener(new RGCheckedListner(edtAccount));
		((Button) lltPayment
		.findViewById(R.id.btn_pay_confirm)).setText(R.string.update);
		((Button) lltPayment
				.findViewById(R.id.btn_pay_confirm)).setTextColor(Color.WHITE);
		btnList = (Button) findViewById(R.id.btn_survey_list);
		btnMine = (Button) findViewById(R.id.btn_survey_mine);
		btnPayment = (Button) findViewById(R.id.btn_survey_payment);
		btnIntro = (Button) findViewById(R.id.btn_survey_intro);

		btnList.setOnClickListener(this);
		btnMine.setOnClickListener(this);
		btnPayment.setOnClickListener(this);
		btnIntro.setOnClickListener(this);

		gvList.setOnItemClickListener(new GVClickedListener());

		lltSurveyContent.addView(gvList);
	}

	private void checkUpdate() {
		Properties pro = new Properties();
		String userName = app.getLogin().getGbUserName();
		pro.put(SoapRes.KEY_SURVEY_USERNAME, userName);
		pro.put(SoapRes.KEY_SURVEY_ID, surveyAdapter.getMaxSurveyId(userName));
		pro.put(SoapRes.KEY_SURVEY_PAGE_SIZE, "");
		pro.put(SoapRes.KEY_SURVEY_PAGE_TYPE, "1");
		pro.put(SoapRes.KEY_SURVEY_LAN, "");
		sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_SURVEY, pro, baseHandler);
	}

	@Override
	protected void onDestroy() {
		uploadLoginLogNew("Activity", "Survey", "end", null);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@SuppressWarnings("unused")
	private Mask mask;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		// ×¢²átip
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
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

}
