package com.jibo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.data.ForgotPwdPaser;
import com.jibo.data.ForgotPwdPaser_Email;
import com.jibo.data.ForgotPwdPaser_SMS;
import com.jibo.data.GetForgetPwdParser;
import com.jibo.data.LoginCheckPaser;
import com.jibo.data.RegistrationUserInfo_hospitalPaser;
import com.jibo.data.WeiboLoginPaser;
import com.jibo.data.entity.AdvertisingEntity;
import com.jibo.data.entity.LoginConfigEntity;
import com.jibo.data.entity.LoginEntity;
import com.jibo.dbhelper.LoginSQLAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.ui.HomePageLayout;
import com.umeng.analytics.MobclickAgent;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.AsyncWeiboRunner.RequestListener;

/**
 * 登陆界面
 * 
 * @author simon
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
		OnFocusChangeListener, OnTouchListener, OnGestureListener,
		RequestListener {

	/** UI组件 */

	// 登录界面
	private LinearLayout inputMainLayout;// 登录界面主布局
	private EditText mUserNameEdit;
	private EditText mPasswordEdit;
	private Button loginBtn;
	private TextView forgotPwd;// 忘记密码
	private LinearLayout loginNameLayout;// 姓名输入框的背景
	private RelativeLayout rltReg;
	private ImageButton chooseAccountButton;// 点击弹出 历史登录账号的信息列表
	private ImageButton sinaLogin;
	private ImageButton qqLogin;

	private ImageButton gobackBtn;

	// 忘记密码
	private LinearLayout forgotPwdLayout;
	private EditText forgotPwdInput;
	// private Button findPwdBtn;
	private LinearLayout getPwdByEmail = null;// 通过邮件取回密码 Terry
	private LinearLayout getPwdBySms = null; // 通过短信取回密码 Terry
	private TextView findPwdText;

	/** 历史账号弹出层 相关组件与变量 */
	private PopupWindow mPopupWindow;
	private View view;
	private ListView listView;
	private Context context;
	private ConfigAdapter adapter;
	private boolean isChanged;// 锁定用户名textChange事件

	/** else */
	private LoginSQLAdapter dbHelper;
	private ArrayList<LoginConfigEntity> list;// 历史登录账号的信息列表
	private String fromAct;
	private boolean isWelcome;

	public final String LOGINPAGE = "loginPage";
	public final String LICENSE = "LICENSENUMBER";
	private GBApplication application;
	private GestureDetector mGestureDetector;

	private ImageView logo;

	//标示是否忘记密码视图
	private boolean isForgotPwd;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.i("GBA", "GBA - " + msg.what);
			switch (msg.what) {
			case 1:
				if (mPopupWindow == null) {
					initPopupWindow(mUserNameEdit.getWidth()
							+ chooseAccountButton.getWidth());
				}
				showPopupWindow(mUserNameEdit, list);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		context = this;

		application = (GBApplication) getApplication();
		mGestureDetector = new GestureDetector(this);
		rltReg = (RelativeLayout) findViewById(R.id.reg);
		rltReg.setOnClickListener(this);
		rltReg.setOnTouchListener(this);
		rltReg.setLongClickable(true);

		Intent intent = getIntent();
		isWelcome = intent.getBooleanExtra("WELCOME", false);
		fromAct = getIntent().getStringExtra("FROM");
		dbHelper = new LoginSQLAdapter(this);
		list = dbHelper.getLoginConfigs(context, null);
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	/**
	 * 初始化界面组件
	 */
	private void init() {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		logo = (ImageView) findViewById(R.id.logo);
		logo.setAdjustViewBounds(true);
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) logo
				.getLayoutParams();
		params1.height = (height / 10) * 4;
		logo.setLayoutParams(params1);
		logo.setMaxWidth(width);
		logo.setMaxHeight((height / 10) * 4);

		// logo.setMinimumWidth(width);
		// logo.setMinimumHeight(height / 3);

		inputMainLayout = (LinearLayout) findViewById(R.id.inputLayout);
		loginNameLayout = (LinearLayout) findViewById(R.id.login_name_layout);
		chooseAccountButton = (ImageButton) findViewById(R.id.img_loginname);
		mUserNameEdit = (EditText) findViewById(R.id.loginname);
		mPasswordEdit = (EditText) findViewById(R.id.loginpassword);
		loginBtn = (Button) findViewById(R.id.btnlogin);
		sinaLogin = (ImageButton) findViewById(R.id.sina_login);
		qqLogin = (ImageButton) findViewById(R.id.qq_login);
		gobackBtn = (ImageButton) findViewById(R.id.goback_btn);
		forgotPwd = (TextView) findViewById(R.id.forgot_pwd);

		forgotPwdLayout = (LinearLayout) findViewById(R.id.forgotPwd_layout);
		forgotPwdInput = (EditText) findViewById(R.id.forgot_pwd_input);
		// findPwdBtn = (Button) findViewById(R.id.find_pwd_btn);
		// 通过短信或者邮件取回密码 Terry
		getPwdByEmail = (LinearLayout) findViewById(R.id.getback_pwd_by_email);
		getPwdBySms = (LinearLayout) findViewById(R.id.getback_pwd_by_sms);
		findPwdText = (TextView) findViewById(R.id.find_pwd_textview);

		loginBtn.setTextSize(17);
		// findPwdBtn.setTextSize(17);
		forgotPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		forgotPwd.getPaint().setAntiAlias(true);

		loginBtn.setOnClickListener(this);
		sinaLogin.setOnClickListener(this);
		qqLogin.setOnClickListener(this);
		gobackBtn.setOnClickListener(this);
		forgotPwd.setOnClickListener(this);
		// 对邮件和SMS两个布局设置监听 Terry
		getPwdByEmail.setOnClickListener(this);
		getPwdBySms.setOnClickListener(this);
		// findPwdBtn.setOnClickListener(this);
		chooseAccountButton.setOnClickListener(this);
		mUserNameEdit.setOnFocusChangeListener(this);
		mUserNameEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!isChanged) {
					String username = mUserNameEdit.getText().toString().trim();
					ArrayList<LoginConfigEntity> list = dbHelper
							.getLoginConfigs(context, username);
					if (null != list && list.size() == 1) {
						mPasswordEdit.setText(list.get(0).passWord);
					} else {
						mPasswordEdit.setText("");
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (null != mPopupWindow)
					mPopupWindow.dismiss();
			}

			@Override
			public void afterTextChanged(Editable s) {
				String username = mUserNameEdit.getText().toString().trim();
				if (username.equals("")) {
					mPasswordEdit.setText("");
				}
			}
		});

		//在主界面点击切换账号
		if (null != fromAct && "HomePage".equals(fromAct)) {
			mUserNameEdit.setText("");
			mPasswordEdit.setText("");
		}
		
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		view = mLayoutInflater.inflate(R.layout.login_pop, null);
		listView = (ListView) view.findViewById(R.id.lst_item);
		adapter = new ConfigAdapter(context, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPopupWindow.dismiss();
				isChanged = true;
				mUserNameEdit.setText(adapter.list.get(position).userName);
				mPasswordEdit.setText(adapter.list.get(position).passWord);
				isChanged = false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		String userName = null;
		Properties infor = new Properties();
		switch (v.getId()) {
		case R.id.img_loginname:// 点击弹出历史账号选择层
			if (null != list && list.size() > 0) {
				new Thread(new Runnable() {
					@Override
					public synchronized void run() {
						//关闭软键盘
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						boolean isCloseSuccess = imm.hideSoftInputFromWindow(
								mUserNameEdit.getWindowToken(), 0);
						Log.i("lushan", isCloseSuccess ? "1" : "0");
						if (isCloseSuccess) {//关闭成功后延迟2毫秒弹出历史账号选择框
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				}) {
				}.start();
			}
			break;
		case R.id.btnlogin: { // 登陆
			// String string;
			if (mUserNameEdit.getText().toString().equals("")) {
				// string = getString(R.string.username1)
				// + getString(R.string.empty);
				// Toast.makeText(this, string, Toast.LENGTH_LONG).show();
				mUserNameEdit.requestFocus();
				loginNameLayout.startAnimation(shake);
				return;
			}
			if (mPasswordEdit.getText().toString().equals("")) {
				// string = getString(R.string.password1)
				// + getString(R.string.empty);
				// Toast.makeText(this, string, Toast.LENGTH_LONG).show();
				mPasswordEdit.requestFocus();
				mPasswordEdit.startAnimation(shake);
				return;
			}
			MobclickAgent.onEvent(this, "LoginBtnClick", "LoginPlainBtn", 1);// "SimpleButtonclick");

			uploadLoginLogNew("LoginBtnClick", "", "LoginPlainBtn", null);
			login();
		}
			break;
		case R.id.forgot_pwd: {// 忘记密码
			isForgotPwd = true;
			inputMainLayout.setVisibility(View.GONE);
			forgotPwdLayout.setVisibility(View.VISIBLE);
			forgotPwdInput.setText("");

			imm.hideSoftInputFromWindow(forgotPwdInput.getWindowToken(), 0);
			// findPwdText.setVisibility(View.INVISIBLE);
			MobclickAgent.onEvent(this, "LRBtnClick", "LForgetPwdBtn", 1);// "SimpleButtonclick");
			uploadLoginLogNew("LRBtnClick", "", "LForgetPwdBtn", null);
		}
			break;

		// case R.id.find_pwd_btn: {// 重置密码按钮
		// String userName = forgotPwdInput.getText().toString().trim();
		// if (userName.equals("")) {
		// forgotPwdInput.requestFocus();
		// forgotPwdInput.startAnimation(shake);
		// return;
		// }
		//
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(
		// forgotPwdInput.getWindowToken(), 0);
		// Properties info = new Properties();
		// info.put("gbUserName", userName);
		// sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_FORGOT_PWD, info,
		// new BaseResponseHandler(this,
		// DialogRes.DIALOG_SEND_REGISTER_REQUEST));
		//
		// MobclickAgent.onEvent(this, "LRBtnClick", "LFCommitBtn", 1);//
		// "SimpleButtonclick");
		// // 发送请求。。。
		//
		// uploadLoginLogNew("LRBtnClick", "",
		// "LFCommitBtn", null);
		// }
		// break;
		case R.id.sina_login: {// sina 登入
			// Intent intent = new
			// Intent(this,Registration_HospitalActivity.class);
			// startActivity(intent);
			//
			weiboLogin(Weibo.WEIBO_SINAL);
			MobclickAgent.onEvent(this, "LoginBtnClick", "LSinaBtn", 1);// "SimpleButtonclick");
			uploadLoginLogNew("LoginBtnClick", "", "LSinaBtn", null);
		}
			break;
		case R.id.qq_login: {// qq 登入
			weiboLogin(Weibo.WEIBO_QQ);
			MobclickAgent.onEvent(this, "LoginBtnClick", "LQQBtn", 1);// "SimpleButtonclick");
			uploadLoginLogNew("LoginBtnClick", "", "LQQBtn", null);
		}
			break;
		case R.id.goback_btn: {// 返回
			Intent intent = new Intent().setClass(this,
					FirstStartForChooseActivity.class);
			intent.putExtra("loginBack", true);
			startActivity(intent);
			MobclickAgent.onEvent(this, "LRBtnClick", "LBack1Btn", 1);// "SimpleButtonclick");
			uploadLoginLogNew("LRBtnClick", "", "LBack1Btn", null);
			finish();
		}
			break;
		//点击输入框以外范围
		case R.id.reg:
			if (this.getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(this.getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		// 当点击通过邮件取回密码时
		case R.id.getback_pwd_by_email:
			userName = forgotPwdInput.getText().toString().trim();
			if (userName.equals("")) {
				forgotPwdInput.requestFocus();
				forgotPwdInput.startAnimation(shake);
				return;
			}
			infor.put("gbUserName", userName);
			sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_FORGOT_PWD_EMAIL,
					infor, new BaseResponseHandler(this,
							DialogRes.DIALOG_SEND_REGISTER_REQUEST));
			infor.clear();
			MobclickAgent.onEvent(this, "LRBtnClick", "LFCommitBtn", 1);

			uploadLoginLogNew("LRBtnClick", "", "LFCommitBtn", null);
			break;
		// 当点击通过短信取回密码时
		case R.id.getback_pwd_by_sms:
			userName = forgotPwdInput.getText().toString().trim();
			if (userName.equals("")) {
				forgotPwdInput.requestFocus();
				forgotPwdInput.startAnimation(shake);
				return;
			}
			infor.put("gbUserName", userName);
			sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_FORGOT_PWD_SMS,
					infor, new BaseResponseHandler(this,
							DialogRes.DIALOG_SEND_REGISTER_REQUEST));
			infor.clear();
			MobclickAgent.onEvent(this, "LRBtnClick", "LFCommitBtn", 1);

			uploadLoginLogNew("LRBtnClick", "", "LFCommitBtn", null);
			break;
		}
	}

	// @Override
	// public void onComplete(String response, int platForm) {
	// Toast.makeText(context,
	// R.string.createfriend_success+response, Toast.LENGTH_LONG)
	// .show();
	// }
	//
	// @Override
	// public void onIOException(IOException e) {
	//
	// }
	//
	// @Override
	// public void onError(WeiboException e, int platForm) {
	//
	// }

	/**
	 * 初始化历史登陆账号popWindow
	 * 
	 * @param width
	 * @param list
	 */
	private void initPopupWindow(int width) {
		mPopupWindow = new PopupWindow(view, width,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 必须设置background才能消失
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.dialogWindowAnim);
		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
	}

	/**
	 * 弹出历史登陆账号
	 * 
	 * @param dropView
	 *            在指定view下方弹出popwindow层
	 * @param list
	 *            登陆历史账号集合list
	 */
	private void showPopupWindow(View dropView,
			ArrayList<LoginConfigEntity> list) {
		if (!mPopupWindow.isShowing()) {
			if (null != list) {
				adapter = new ConfigAdapter(context, list);
				listView.setAdapter(adapter);
			}
			mPopupWindow.showAsDropDown(dropView, 0, 0);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (isForgotPwd) {//当前在忘记密码视图
				isForgotPwd = false;
				inputMainLayout.setVisibility(View.VISIBLE);
				forgotPwdLayout.setVisibility(View.GONE);
				MobclickAgent.onEvent(this, "LRBtnClick", "LBack2Btn", 1);// "SimpleButtonclick");
				uploadLoginLogNew("LRBtnClick", "", "LBack2Btn", null);
				return true;
			}
			Intent intent = new Intent().setClass(this,
					FirstStartForChooseActivity.class);
			intent.putExtra("loginBack", true);
			startActivity(intent);
			MobclickAgent.onEvent(this, "LRBtnClick", "LBack1Btn", 1);// "SimpleButtonclick");
			uploadLoginLogNew("LRBtnClick", "", "LBack1Btn", null);
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.loginname:// 账号输入框
			if (hasFocus) {
				loginNameLayout
						.setBackgroundResource(R.drawable.login_input_bg_select);
			} else {
				loginNameLayout
						.setBackgroundResource(R.drawable.login_input_bg_normal);
			}
			break;
		}
	}

	private String name;
	private String password;

	/**
	 * 封装登陆发送请求逻辑
	 */
	private void login() {
		name = mUserNameEdit.getText().toString();
		password = mPasswordEdit.getText().toString();
		Log.i("GBA", "name=" + name + "---" + "password=" + password);
		if (SharedPreferencesMgr.getUserName().equals(name)) {
			SharedPreferencesMgr.setIsNew(false);
		} else {
			SharedPreferencesMgr.setIsNew(true);
			SharedPreferencesMgr.setAcademicProfileIsSaved(false);
		}
		Properties info = new Properties();
		info.put("gbUserName", name);
		info.put("password", Util.MD5(password));
		String token = Util.getDeviceId(context);
		if(token!=null)
		info.put("devicetoken", token);
		sendRequest(SoapRes.URLCustomer,
				SoapRes.REQ_ID_LOGIN_CHECK_USERNAME_AND_PASSWORD, info,
				new BaseResponseHandler(this, DialogRes.DIALOG_ID_LOGINING));
	}

	/**
	 * 网络请求成功后回调
	 */
	@Override
	public void onReqResponse(Object o, int methodId) {
		if (null != o) {
			if (o instanceof LoginCheckPaser) {
				LoginCheckPaser loginPaser = (LoginCheckPaser) o;
				if (loginPaser.isSuccess()) {//登录成功
					LoginEntity entity = loginPaser.getLoginEntity();
					String tempUsername = SharedPreferencesMgr.getUserName();
					if (!tempUsername.equals(name)) {// 更换用户，将此前用户信息清空
						SharedPreferencesMgr.setErrorProfile(0);

						SharedPreferencesMgr.setAccessToken_SINA("");
						SharedPreferencesMgr.setExpiresIn_SINA("");
						SharedPreferencesMgr.setUid_SINA("");

						SharedPreferencesMgr.setAccessToken_QQ("");
						SharedPreferencesMgr.setExpiresIn_QQ("");
						SharedPreferencesMgr.setOpenId_QQ("");

						SharedPreferencesMgr.setIsWeiboLogin(false);
					}

					String imagePath = entity.getImagePath();
					if (!TextUtils.isEmpty(imagePath))
						dbHelper.insertAdvertising(new AdvertisingEntity(name,
								"", imagePath));

					// 保存配置信息
					SharedPreferencesMgr.saveIsSave(true);
					SharedPreferencesMgr.saveIsAuto(true);

					LoginConfigEntity config = new LoginConfigEntity();
					config.userName = name.trim();
					config.passWord = password.trim();
					config.isSave = "1";
					config.isAuto = "1";

					dbHelper.insertLoginConfig(context, config);
					
					SharedPreferencesMgr.savePassword(password);
					saveLoginInfoToLocal(entity);

					Intent intent = new Intent();
//					intent.setClass(getBaseContext(), HomePageActivity.class);
					intent.setClass(getBaseContext(), InitializeActivity.class);
					intent.putExtra(LOGINPAGE, true);
					intent.putExtra("FROM", "GBApp");
					HomePageLayout.s_pageID = 0;
					startActivity(intent);
					finish();
				} else {
					SharedPreferencesMgr.saveIsSave(false);
					SharedPreferencesMgr.saveIsAuto(false);
					mUserNameEdit.setFocusableInTouchMode(true);
					mUserNameEdit.setFocusable(true);
					int length = mUserNameEdit.getText().toString().trim()
							.length();
					mUserNameEdit.requestFocus();
					mUserNameEdit.setSelection(length - 1);
					Toast.makeText(context, R.string.preting, 1).show();
				}

			} else if (o instanceof WeiboLoginPaser) {
				super.onReqResponse(o, methodId);
			} else if (o instanceof ForgotPwdPaser) {
				ForgotPwdPaser obj = (ForgotPwdPaser) o;
				if (obj.isSuccess()) {
					findPwdText.setVisibility(View.VISIBLE);
				} else {
					String errorCode = obj.getErrorCode();
					String alertText = "";
					if (errorCode.equals("0")) {
						alertText = getString(R.string.emailIsNull);
					} else if (errorCode.equals("503")) {
						alertText = getString(R.string.accountIsNull);
					} else {
						alertText = obj.getErrorMsg();
					}
					findPwdText.setVisibility(View.GONE);
					Toast.makeText(this, alertText, Toast.LENGTH_LONG).show();
				}

			} else if (o instanceof ForgotPwdPaser_SMS) {
				ForgotPwdPaser_SMS obj = (ForgotPwdPaser_SMS) o;

				String errorCode = obj.getErrorCode();
				String alertText = "";
				if (errorCode.equals("800")) {
					alertText = obj.getErrorMsg();
				} else if (errorCode.equals("801")) {
					alertText = obj.getErrorMsg();
				} else if (errorCode.equals("802")) {
					alertText = obj.getErrorMsg();
				} else if (errorCode.equals("803")) {
					alertText = obj.getErrorMsg();
				} else if (errorCode.equals("503")) {
					alertText = obj.getErrorMsg();
				}
				Toast.makeText(this, alertText, Toast.LENGTH_LONG).show();

			} else if (o instanceof ForgotPwdPaser_Email) {
				ForgotPwdPaser_Email obj = (ForgotPwdPaser_Email) o;

				String errorCode = obj.getErrorCode();
				String alertText = "";
				if (errorCode.equals("810")) {
					alertText = obj.getErrorMsg();
				} else if (errorCode.equals("811")) {
					alertText = obj.getErrorMsg();
				} else if (errorCode.equals("812")) {
					alertText = obj.getErrorMsg();
				} else if (errorCode.equals("503")) {
					alertText = obj.getErrorMsg();
				}
				Toast.makeText(this, alertText, Toast.LENGTH_LONG).show();

			} else if (o instanceof GetForgetPwdParser) {
				GetForgetPwdParser obj = (GetForgetPwdParser) o;
				String resCode = obj.getResCode();
				String resMsg = "";
				resMsg = obj.getResmsg();
				if (resCode.equals("200")) {
					findPwdText.setVisibility(View.VISIBLE);
					findPwdText.setText(resMsg);
				} else {
					Toast.makeText(this, resMsg, Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	/**
	 * 网络(错误)回调
	 */
	@Override
	public void onErrResponse(Throwable error, String content,
			boolean isBackGroundThread) {
		Toast.makeText(context, R.string.networkNotReachable, 1).show();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);

	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() < e2.getX() && isWelcome) {
			Intent intent = new Intent(LoginActivity.this,
					UserGuideActivity.class);
			startActivity(intent);
			finish();
		}

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

	/**
	 * popwindow层中listView自定义适配器
	 * 
	 * @author simon
	 * 
	 */
	class ConfigAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context context;
		private ArrayList<LoginConfigEntity> list;

		public ConfigAdapter(Context context, ArrayList<LoginConfigEntity> list) {
			this.context = context;
			this.list = list;
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			Holder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.login_pop_itemview,
						null);
				holder = new Holder();
				holder.view = (TextView) convertView
						.findViewById(R.id.userName);
				holder.button = (ImageButton) convertView
						.findViewById(R.id.deleteUser);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			convertView.setId(position);
			holder.view.setText(list.get(position).userName);
			holder.button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPopupWindow.dismiss();
					final String userName = list.get(position).userName;
					final String thisShowName = mUserNameEdit.getText()
							.toString().trim();
					new AlertDialog.Builder(LoginActivity.this)
							.setTitle(R.string.deleteUser)
							.setMessage(
									context.getString(R.string.isDeleteUser_Sure)
											+ " " + userName + " ?")
							.setIcon(R.drawable.icon)
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (dbHelper.deleteLoginConfig(
													context, userName)) {
												LoginActivity.this.list
														.remove(position);
												// 删除了当前输入框显示的用户
												if (userName
														.equals(thisShowName)) {
													mUserNameEdit.setText("");
													mPasswordEdit.setText("");
												}
												String currentUseUsername = SharedPreferencesMgr
														.getUserName();
												// 删除了当前app正在使用的用户
												if (currentUseUsername
														.equals(userName)) {
													SharedPreferencesMgr
															.saveIsSave(false);
													SharedPreferencesMgr
															.saveIsAuto(false);
													SharedPreferencesMgr
															.saveUserName("");
													SharedPreferencesMgr
															.saveLicenseNumber("");
												}
											}
											dialog.dismiss();
										}
									})
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create().show();
				}
			});
			return convertView;
		}

		class Holder {
			TextView view;
			ImageButton button;
		}
	}

}
