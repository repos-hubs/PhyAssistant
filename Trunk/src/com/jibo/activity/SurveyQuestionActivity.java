package com.jibo.activity;

import java.util.ArrayList;
import java.util.Properties;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.data.SurveyQuestionPaser;
import com.jibo.data.SurveySubmitPaser;
import com.jibo.data.entity.SurveyEntity;
import com.jibo.data.entity.SurveyQuestionEntity;
import com.jibo.data.entity.SurveyQuestionItemEntity;
import com.jibo.dbhelper.SurveyAdapter;
import com.jibo.dbhelper.SurveyQuestionAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.net.BaseResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class SurveyQuestionActivity extends BaseSearchActivity implements
		OnClickListener, OnGestureListener, OnTouchListener {
	private GBApplication app;

	private TextView txtIntro;
	private TextView txtEstimateTime;
	private TextView txtPay;
	private TextView txtPersonCount;
	private TextView txtEndTime;
	private TextView txtRegion;
	private TextView txtDepartment;
	private TextView txtHospitalType;
	private Button btnConfirm;
	private SurveyEntity en;
	private LinearLayout surveyQuestionPanel;
	private ScrollView svQuestionContent;
	private LinearLayout lltMainLayout;

	private ArrayList<SurveyQuestionEntity> recheckQuestionList;
	private ArrayList<SurveyQuestionEntity> questionList;
	private static final int CHECK_RECHECK_RESULT = 0x123;
	private ArrayList<View> recheckViewList;
	private ArrayList<View> questionViewList;

	private ViewFlipper questionGallery;
	private GestureDetector mGestureDetector;
	private int position_flag = 1;
	private TextView tvLabel;
	private LinearLayout bar;
	private LayoutInflater inflater;
	private SurveyQuestionAdapter questionAdapter;
	private SurveyAdapter surveyAdapter;
	private String userName;

	private TextView txtPrompt;

	private String status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_survey_question);
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		inits();
	}

	private void inits() {
		bar = new LinearLayout(this);
		app = (GBApplication) getApplication();
		userName = app.getLogin().getGbUserName();
		surveyAdapter = new SurveyAdapter(this, 1);
		questionAdapter = new SurveyQuestionAdapter(this, 1);
		inflater = LayoutInflater.from(this);
		recheckQuestionList = new ArrayList<SurveyQuestionEntity>();
		questionList = new ArrayList<SurveyQuestionEntity>();
		recheckViewList = new ArrayList<View>();
		questionViewList = new ArrayList<View>();

		mGestureDetector = new GestureDetector(this);
		svQuestionContent = (ScrollView) findViewById(R.id.sv_survey_content);
		surveyQuestionPanel = (LinearLayout) findViewById(R.id.llt_survey_content);
		lltMainLayout = (LinearLayout) findViewById(R.id.llt_main_content);
		txtIntro = (TextView) findViewById(R.id.txt_intro);
		txtEstimateTime = (TextView) findViewById(R.id.txt_estimate);
		txtPay = (TextView) findViewById(R.id.txt_pay);
		txtPersonCount = (TextView) findViewById(R.id.txt_pCount);
		txtEndTime = (TextView) findViewById(R.id.txt_endTime);
		txtRegion = (TextView) findViewById(R.id.txt_region);
		txtDepartment = (TextView) findViewById(R.id.txt_city);
		txtHospitalType = (TextView) findViewById(R.id.txt_hospitalType);
		txtPrompt = (TextView) findViewById(R.id.txt_prompt);
		btnConfirm = (Button) findViewById(R.id.btn_confirm);

		TextView txtTitle = (TextView) findViewById(R.id.txt_header_title);
		txtTitle.setText(getString(R.string.survey));

		btnConfirm.setOnClickListener(this);

		en = getIntent().getParcelableExtra("en");
		txtIntro.setText("\t" + en.getContent());
		txtEstimateTime.setText(en.getEstimateTime());
		txtPay.setText(en.getPay());
		txtPersonCount.setText(en.getpCount());
		txtEndTime.setText(en.getEndTime());
		txtRegion.setText(getString(R.string.survey_target_place));
		txtRegion.append(":" + en.getRegion());
		txtDepartment.setText(getString(R.string.survey_target_dpt));
		txtDepartment.append(":" + en.getDepartment());
		txtHospitalType.setText(getString(R.string.survey_target_hospital));
		txtHospitalType.append(":" + en.getHospitalLevel());
		txtPrompt.setText("\t\t" + getString(R.string.survey_recheck_letter));
		status = questionAdapter.getRecheckStatus(en.getID(), "0", userName);
		if ("1".equals(status)) {
			svQuestionContent.removeAllViews();
			if (app.getDeviceInfo().isNetWorkEnable()) {
				app.getRecheckMap().put(en.getID(), "1");
				getSurveyRecheck();
			} else {
				try {
					questionList = questionAdapter.getQuestionList(en.getID(),
							"1", userName);
					createQuestionLayout(questionList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if (app.getDeviceInfo().isNetWorkEnable()) {
				recheckQuestionList = questionAdapter.getQuestionList(
						en.getID(), "0", userName);
				if (recheckQuestionList.size() > 0) {

				} else {
				}
				getSurveyRecheck();
			} else {
				recheckQuestionList = questionAdapter.getQuestionList(
						en.getID(), "0", userName);
				questionList = questionAdapter.getQuestionList(en.getID(), "1",
						userName);
				if (recheckQuestionList.size() > 0) {
					createRCLayout();
				} else {
					app.getRecheckMap().put(en.getID(), "1");
					createQuestionLayout(questionList);
				}

			}

			break;
		case CHECK_RECHECK_RESULT:
			boolean recheckResult = checkRecheck();
			if (recheckResult) {
				app.getRecheckMap().put(en.getID(), "1");
				questionAdapter.updateRecheckStatus("1", en.getID(), "0",
						userName);
				createQuestionLayout(questionList);
			} else {
				app.getRecheckMap().put(en.getID(), "2");
				questionAdapter.deleteSurvey(en.getID(), userName, "1");
				surveyAdapter.deleteSurvey(en.getID(), userName);
				lltMainLayout.removeAllViews();
				LinearLayout lltRecheckError = (LinearLayout) inflater.inflate(
						R.layout.survey_question_recheck_error, null);
				Button btnRecheckError = (Button) lltRecheckError
						.findViewById(R.id.btn_recheck_error);
				Button btnShare = (Button) lltRecheckError
						.findViewById(R.id.btn_share_survey);

				lltMainLayout.addView(lltRecheckError);
				btnRecheckError.setOnClickListener(this);
				btnShare.setOnClickListener(this);
				submitRecheckErrorToServer("");
			}
			break;
		case R.id.btn_submit_survey:
			
			v.setClickable(submitQuestion());
			break;
		case R.id.btn_share_survey:
			app.setSurveyID(en.getID());
			app.setSurveyTitle(en.getTitle());
			sharing(R.array.items2, 11);
			break;
		case R.id.btn_recheck_error:
			Intent intent = new Intent(this, NewSurveyActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void clickPositiveButton(int dialogId) {
		Intent intent = null;
		switch (dialogId) {
		case DialogRes.DIALOG_ID_SURVEY_SUBMIT_ERROR:
			intent = new Intent(this, NewSurveyActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case DialogRes.DIALOG_ID_SURVEY_SUBMIT_FULL:
			surveyAdapter.deleteSurvey(en.getID(), userName);
			questionAdapter.deleteSurvey(en.getID(), userName, "1");
			intent = new Intent(this, NewSurveyActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case DialogRes.DIALOG_ID_SURVEY_SUBMIT_HAVE_SUBMITTED:
			surveyAdapter.deleteSurvey(en.getID(), userName);
			questionAdapter.deleteSurvey(en.getID(), userName, "1");
			intent = new Intent(this, NewSurveyActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case DialogRes.DIALOG_ID_SHARE_AFTER_SUBMIT:
			app.setSurveyID(en.getID());
			app.setSurveyTitle(en.getTitle());
			sharing(R.array.items2, 11);
			break;
		case DialogRes.DIALOG_ID_SURVEY_NOT_COMPLETE:
			removeDialog(DialogRes.DIALOG_ID_SURVEY_NOT_COMPLETE);
		}
		super.clickPositiveButton(dialogId);
	}

	@Override
	public void clickNegativeButton(int dialogId) {
		switch (dialogId) {
		case DialogRes.DIALOG_ID_SHARE_AFTER_SUBMIT:
			Intent intent = new Intent(this, NewSurveyActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		super.clickNegativeButton(dialogId);
	}

	private class SubmitRecheckError extends AsyncSoapResponseHandler {
		@Override
		public void onSuccess(Object content) {
			super.onSuccess(content);
		}
	}

	private class SubmitQuestionHandler extends AsyncSoapResponseHandler {
		@Override
		public void onSuccess(Object content) {
			if (content instanceof SurveySubmitPaser) {
				System.out.println("1111");
				SurveySubmitPaser submitPaser = (SurveySubmitPaser) content;
				String flag_result = submitPaser.getSubmitResult();
				if ("1".equals(app.getRecheckMap().get(en.getID()).toString())) {
					System.out.println("2222");
					if ("0".equals(flag_result)) {
						showDialog(DialogRes.DIALOG_ID_SURVEY_SUBMIT_ERROR);
					} else if ("1".equals(flag_result)) {
						System.out.println("3333");
						questionAdapter.deleteSurvey(en.getID(), userName, "1");
						surveyAdapter.deleteSurvey(en.getID(), userName);
						showDialog(DialogRes.DIALOG_ID_SHARE_AFTER_SUBMIT);
					} else if ("3".equals(flag_result)) {
						System.out.println("4444");
						showDialog(DialogRes.DIALOG_ID_SURVEY_SUBMIT_HAVE_SUBMITTED);
					} else if ("4".equals(flag_result)) {
						System.out.println("555");
						showDialog(DialogRes.DIALOG_ID_SURVEY_SUBMIT_FULL);
					}
				} else {
					System.out.println("6666");
				}
			}
			super.onSuccess(content);
		}
	}

	private class RGChangeListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			RadioButton rb = (RadioButton) group.findViewById(checkedId);
			group.setTag(rb.getTag());
		}
	}

	private class CBQuestionChangeListener implements
			android.widget.CompoundButton.OnCheckedChangeListener {
		private SurveyQuestionItemEntity sqi;

		public CBQuestionChangeListener(SurveyQuestionItemEntity sqi) {
			this.sqi = sqi;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO
			if (isChecked) {
				buttonView.setTag(sqi.getOptionID());
			} else {
				buttonView.setTag("");
			}
		}
	}

	public ArrayList<SurveyQuestionEntity> createQuestionList(
			ArrayList<SurveyQuestionEntity> newList,
			ArrayList<SurveyQuestionEntity> storedList) {
		ArrayList<SurveyQuestionEntity> list = new ArrayList<SurveyQuestionEntity>();
		for (int i = 0; i < storedList.size(); i++) {
			SurveyQuestionEntity sqeOld = storedList.get(i);
			for (int j = 0; j < newList.size(); j++) {
				SurveyQuestionEntity sqeNew = newList.get(j);
				if (sqeNew.getId().equals(sqeOld.getId())) {
					list.add(sqeOld);
				} else {
					list.add(sqeNew);
				}
				break;
			}
		}

		return list;
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			if (o instanceof SurveyQuestionPaser) {
				SurveyQuestionPaser questionPaser = (SurveyQuestionPaser) o;
				recheckQuestionList = questionPaser.getRecheckQuestionList();
				ArrayList<SurveyQuestionEntity> newList = questionPaser
						.getQuestionList();
				questionAdapter.updateQuestionList(en.getID(),
						recheckQuestionList, "0", userName);
				questionAdapter.updateQuestionList(en.getID(), newList, "1",
						userName);
				if (questionAdapter.checkIfSurveyExist(en.getID(), "1",
						userName)) {
					questionList = questionAdapter.getQuestionList(en.getID(),
							"1", userName);
				}

				// createRCLayout();
				if ("1".equals(en.getIsVerify())) {
					if ("1".equals(status)) {
						app.getRecheckMap().put(en.getID(), "1");
						createQuestionLayout(questionList);
					} else {
						createRCLayout();
					}
				} else {
					app.getRecheckMap().put(en.getID(), "1");
					createQuestionLayout(questionList);
				}

			}
		}
		super.onReqResponse(o, methodId);
	}

	public void createRCLayout() {
		svQuestionContent.removeAllViews();
		LinearLayout lltContent = new LinearLayout(this);
		lltContent.setOrientation(LinearLayout.VERTICAL);
		int padding = 10;
		lltContent.setPadding(padding, padding, padding, padding);
		for (SurveyQuestionEntity recheckQuestion : recheckQuestionList) {
			lltContent.addView(createRecheckContent(recheckQuestion));
		}
		Button btnConfirm = new Button(this);
		btnConfirm.setText(R.string.ok);
		btnConfirm.setId(CHECK_RECHECK_RESULT);
		btnConfirm.setOnClickListener(this);
		lltContent.addView(btnConfirm);
		svQuestionContent.addView(lltContent);
	}

	private boolean checkRecheck() {
		boolean result = true;
		try {
			for (int i = 0; i < recheckViewList.size(); i++) {
				View view = recheckViewList.get(i);
				SurveyQuestionEntity recheckItem = recheckQuestionList.get(i);
				String tag = view.getTag().toString();
				String answer[] = recheckItem.getAnswer().split("/");
				ArrayList<String> answerList = new ArrayList<String>();
				for (String s : answer) {
					answerList.add(s);
				}
				if (answerList.contains(tag)) {

				} else {
					result = false;
				}
			}
		} catch (Exception e) {
			result = false;
		}

		System.out.println("result    **  " + result);
		return result;
	}

	private boolean submitQuestion() {
		ArrayList<String> questionValueList = new ArrayList<String>();

		for (int i = 0; i < questionViewList.size(); i++) {
			String value = "";
			View view = questionViewList.get(i);
			if (view instanceof RadioGroup) {
				value = view.getTag().toString();
			} else if (view instanceof LinearLayout) {
				LinearLayout llt = (LinearLayout) view;
				for (int j = 0; j < llt.getChildCount(); j++) {
					CheckBox cb = (CheckBox) llt.getChildAt(j);
					if (cb.getTag().toString().equals("")) {
						continue;
					} else {
						value = value + cb.getTag().toString() + "/";
					}
				}
				if(value.length()==0){
					return true;
				}
				value = value.substring(0, value.length() - 1);
			} else if (view instanceof EditText) {
				value = ((EditText) view).getText().toString();
			}

			questionValueList.add(value);
		}

		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < questionValueList.size(); i++) {
			sb.append("{");
			sb.append("\"surveyID\":" + "\"" + en.getID() + "\",");
			sb.append("\"qID\":" + "\"" + (i + 1) + "\",");
			sb.append("\"result\":" + "\"" + questionValueList.get(i) + "\"},");
		}
		sb.append("]");
		int blankAnswer = 0;
		for (int m = 0; m < questionValueList.size(); m++) {
			if ("".equals(questionValueList.get(m))) {
				blankAnswer++;
			}
		}
		if (blankAnswer == 0) {
			submitQuestionToServer(sb.toString());
		} else {
			showDialog(DialogRes.DIALOG_ID_SURVEY_NOT_COMPLETE);
		}
		return false;
	}

	private void submitRecheckErrorToServer(String qAnswer) {
		SubmitRecheckError sreHandler = new SubmitRecheckError();
		Properties pro = new Properties();
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_USERNAME, app.getLogin()
				.getGbUserName());
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_ID, en.getID());
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_LN, "");
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_RR,
				app.getRecheckMap().get(en.getID()));
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_RA, "");
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_AA, qAnswer);
		sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_NEW_SURVEY_SUBMIT, pro,
				sreHandler);
	}

	private void submitQuestionToServer(String qAnswer) {
		SubmitQuestionHandler handler = new SubmitQuestionHandler();
		Properties pro = new Properties();
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_USERNAME, app.getLogin()
				.getGbUserName());
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_ID, en.getID());
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_LN, "");
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_RR,
				app.getRecheckMap().get(en.getID()));
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_RA, "");
		pro.put(SoapRes.KEY_PAY_SURVEY_SUBMIT_AA, qAnswer);
		sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_NEW_SURVEY_SUBMIT, pro,
				handler);
	}

	private View createRecheckContent(SurveyQuestionEntity recheckQuestion) {
		LinearLayout lltContent = new LinearLayout(this);
		lltContent.setOrientation(LinearLayout.VERTICAL);

		String type = recheckQuestion.getType();
		String title = recheckQuestion.getTitle();

		TextView txt = new TextView(this);
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setTextColor(Color.BLACK);
		txt.setText(title);

		lltContent.addView(txt);

		View view = createRGRecheck(recheckQuestion);
		recheckViewList.add(view);
		if ("s".equals(type)) {
			lltContent.addView(view);
		}

		return lltContent;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			if (questionGallery != null) {
				if (position_flag == 1) {

				} else {
					questionGallery.setInAnimation(getApplicationContext(),
							R.anim.push_right_in);
					questionGallery.setOutAnimation(getApplicationContext(),
							R.anim.push_right_out);
					questionGallery.showPrevious();
					questionAdapter.updateAnswerList(en.getID(), userName, "1",
							questionViewList);
					position_flag--;
					tvLabel.setText(position_flag + "/"
							+ questionGallery.getChildCount());

					return true;
				}
			} else {
				Intent intent = new Intent(this, NewSurveyActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private View createRGRecheck(SurveyQuestionEntity recheckQuestion) {
		RadioGroup rg = new RadioGroup(this);
		rg.setOrientation(LinearLayout.VERTICAL);

		ArrayList<SurveyQuestionItemEntity> recheckItemList = recheckQuestion
				.getQuestionItemList();
		System.out.println(" 333333 " + recheckItemList.size());
		for (SurveyQuestionItemEntity item : recheckItemList) {
			RadioButton rb = new RadioButton(this);
			rb.setTag(item.getOptionID());
			rb.setText(item.getLabel());
			rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			rb.setTextColor(Color.BLACK);
			rg.addView(rb);
		}
		if (rg.getChildCount() > 0) {
			rg.getChildAt(0).setSelected(true);
			rg.setTag(rg.getChildAt(0).getTag());
		} else {

		}

		rg.setSelected(true);
		rg.setOnCheckedChangeListener(new RGChangeListener());
		return rg;
	}
	View pagebar;
	private void createQuestionLayout(
			ArrayList<SurveyQuestionEntity> questionList) {
		try{
		questionGallery = new ViewFlipper(this);
		System.out.println("questionList  size   " + questionList.size());
		lltMainLayout.removeAllViews();
		LinearLayout llt = new LinearLayout(this);
		llt.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < questionList.size(); i++) {
			SurveyQuestionEntity question = questionList.get(i);
			String type = question.getType();
			String title = question.getTitle();

			LinearLayout lltContent = new LinearLayout(this);
			lltContent.setOrientation(LinearLayout.VERTICAL);
			int padding = 10;
			lltContent.setPadding(padding, padding, padding, padding);
			TextView txtSurveyTitle = new TextView(this);
			LayoutParams surveyTitleLP = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			txtSurveyTitle.setBackgroundResource(R.drawable.survey_title_panel);
			txtSurveyTitle.setText(en.getTitle());
			txtSurveyTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			txtSurveyTitle.setTextColor(Color.WHITE);
			txtSurveyTitle.setPadding(20, 20, 20, 20);
			surveyTitleLP.leftMargin = 30;
			surveyTitleLP.rightMargin = 30;
			surveyTitleLP.topMargin = 20;
			surveyTitleLP.bottomMargin = 20;
			lltContent.addView(txtSurveyTitle, surveyTitleLP);
			TextView txt = new TextView(this);
			txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			txt.setTextColor(Color.BLACK);
			txt.setText("\t" + (i + 1) + ". " + question.getTitle());

			lltContent.addView(txt);
			// TODO
			if ("s".equals(type)) {
				View view = createRGQuestion(question);
				LayoutParams rgLP = new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT);
				rgLP.setMargins(40, 20, 0, 0);
				questionViewList.add(view);
				lltContent.addView(view, rgLP);
			} else if ("m".equals(type)) {
				View view = createCBQuestion(question);
				questionViewList.add(view);
				System.out.println("CBVIew   tag   " + view.getTag());
				lltContent.addView(view);
			} else if ("t".equals(type)) {
				View view = createETQuestion(question);
				questionViewList.add(view);
				LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
						(int) (app.getDeviceInfo().getScreenHeight() / 4));
				lltContent.addView(view, lp);
			}
			
			 ScrollView sv = new ScrollView(this);
			 sv.addView(lltContent);
			questionGallery.addView(sv);
		}
		LinearLayout lltAction = (LinearLayout) inflater.inflate(
				R.layout.survey_question_action, null);
		LayoutParams actionLP = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lltAction.setGravity(Gravity.CENTER_HORIZONTAL);
		Button btnSubmit = (Button) lltAction
				.findViewById(R.id.btn_submit_survey);
		Button btnShare = (Button) lltAction
				.findViewById(R.id.btn_share_survey);

		btnSubmit.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		questionGallery.addView(lltAction, actionLP);
		pagebar = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.pagebar, null);
		tvLabel = new TextView(this);
		tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvLabel.setTextColor(Color.BLACK);
		tvLabel.setText("1/" + questionGallery.getChildCount());
		tvLabel.setGravity(Gravity.CENTER_HORIZONTAL);
		lltMainLayout.addView(questionGallery, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
//rm tvlabel
//		lltMainLayout.addView(tvLabel);
		

		 lltMainLayout.addView(pagebar, new LayoutParams(
					LayoutParams.FILL_PARENT, 60));
		 ((TextView)pagebar.findViewById(R.id.prepage)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				movetoleft();
			}
			 
		 });
		 ((TextView)pagebar.findViewById(R.id.nextpage)).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					movetoright();
				}
				 
			 });
		 ((TextView)pagebar.findViewById(R.id.currpage)).setText("1/" + questionGallery.getChildCount());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private View createRGQuestion(SurveyQuestionEntity sq) {
		RadioGroup rg = new RadioGroup(this);
		rg.setOrientation(LinearLayout.VERTICAL);

		ArrayList<SurveyQuestionItemEntity> recheckItemList = sq
				.getQuestionItemList();
		int index = 0;
		for (int i = 0; i < recheckItemList.size(); i++) {
			SurveyQuestionItemEntity item = recheckItemList.get(i);
			RadioButton rb = new RadioButton(this);
			rb.setTag(item.getOptionID());
			rb.setText(item.getLabel());
			rb.setButtonDrawable(R.drawable.survey_rb);
			rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			rb.setTextColor(Color.BLACK);
			if (item.getOptionID().equals(sq.getValue())) {
				index = i;
			}
			rg.addView(rb);
		}
		((RadioButton) rg.getChildAt(index)).setChecked(true);
		rg.setTag(rg.getChildAt(index).getTag());
		rg.setOnCheckedChangeListener(new RGChangeListener());
		rg.setOnTouchListener(this);
		return rg;
	}

	private View createCBQuestion(SurveyQuestionEntity sq) {
		LinearLayout lltCB = new LinearLayout(this);
		lltCB.setOrientation(LinearLayout.VERTICAL);
		ArrayList<SurveyQuestionItemEntity> recheckItemList = sq
				.getQuestionItemList();
		System.out.println("sq CB  value   " + sq.getValue());
		String arr[] = sq.getValue().split("/");
		ArrayList<String> valueList = new ArrayList<String>();
		for (String s : arr) {
			valueList.add(s);
		}
		for (SurveyQuestionItemEntity item : recheckItemList) {
			CheckBox cb = new CheckBox(this);
			cb.setOnTouchListener(this);
			cb.setText(item.getLabel());
			cb.setTag("");
			cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			cb.setTextColor(Color.BLACK);
			if (valueList.contains(item.getOptionID().toUpperCase())
					|| valueList.contains(item.getOptionID().toLowerCase())) {
				System.out.println(item.getLabel() + "   ***   "
						+ item.getOptionID());
				cb.setChecked(true);
			}
			lltCB.addView(cb);
			cb.setOnCheckedChangeListener(new CBQuestionChangeListener(item));
		}
		lltCB.setTag(sq.getValue());
		lltCB.setOnTouchListener(this);
		return lltCB;
	}

	private View createETQuestion(SurveyQuestionEntity sq) {
		EditText edt = new EditText(this);
		edt.setGravity(Gravity.TOP);
		edt.setText(sq.getValue());
		edt.setLongClickable(true);
		edt.setOnTouchListener(this);
		return edt;
	}

	@Override
	protected void onPause() {
		// questionAdapter.updateAnswerList(en.getID(), userName, "1",
		// questionViewList);
		super.onPause();
	}

	private void getSurveyRecheck() {
		BaseResponseHandler baseHandler = new BaseResponseHandler(this);
		Properties pro = new Properties();
		pro.put(SoapRes.KEY_PAY_SURVEY_RECHECK_ID, en.getID());
		pro.put(SoapRes.KEY_PAY_SURVEY_RECHECK_LN, "");
		sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_GET_SURVEY_RECHECK, pro,
				baseHandler);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
			float arg3) {
		
		if (questionGallery != null) {
			questionAdapter.updateAnswerList(en.getID(), userName, "1",
					questionViewList);
			if (position_flag == 1) {
				if (e1.getX() > e2.getX() + 10) { // 向左滑，右来
					movetoleft();
				} else if (e1.getX() < e2.getX() + 10) { // ÏòÓÒ»®³¬¹ý10px
					questionGallery.clearAnimation();
				}
			} else if (position_flag == questionGallery.getChildCount()) {
				if (e1.getX() > e2.getX() + 10) { // Ïò×ó»®³¬¹ý10px
					questionGallery.clearAnimation();
				} else if (e1.getX() < e2.getX() + 10) { // ÏòÓÒ»®³¬¹ý10px
					movetoright();

				}
			} else {
				if (e1.getX() > e2.getX() + 10) { // Ïò×ó»®³¬¹ý10px
					movetoleft();
				} else if (e1.getX() < e2.getX() + 10) { // ÏòÓÒ»®³¬¹ý10px
					movetoright();

				}
			}

			tvLabel.setText(position_flag + "/"
					+ questionGallery.getChildCount());
			renewText();
		}

		return false;
	}

	private void renewText() {
		((TextView) pagebar.findViewById(R.id.currpage)).setText(position_flag + "/"
				+ questionGallery.getChildCount());
	}

	private void movetoright() {
		if(position_flag == questionGallery.getChildCount()){
			return;
		}
		questionAdapter.updateAnswerList(en.getID(), userName, "1",
				questionViewList);

		questionGallery.setInAnimation(getApplicationContext(),
				R.anim.push_left_in);
		questionGallery.setOutAnimation(getApplicationContext(),
				R.anim.push_left_out);
		questionGallery.showNext();
		position_flag++;
		renewText();
	}

	private void movetoleft() {
		if(position_flag == 1){
			return;
		}
		questionAdapter.updateAnswerList(en.getID(), userName, "1",
				questionViewList);
		questionGallery.setInAnimation(getApplicationContext(),
				R.anim.push_right_in);
		questionGallery.setOutAnimation(getApplicationContext(),
				R.anim.push_right_out);
		questionGallery.showPrevious();
		position_flag--;
		renewText();
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(arg1);
	}

}
