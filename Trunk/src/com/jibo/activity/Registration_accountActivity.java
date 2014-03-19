package com.jibo.activity;

import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.data.RegistrationUserInfoPaser;
import com.jibo.data.entity.LoginConfigEntity;
import com.jibo.data.entity.LoginEntity;
import com.jibo.dbhelper.LoginSQLAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;
import com.weibo.net.Weibo;

/**
 * 注册界面
 * 
 * @author simon
 * 
 */
public class Registration_accountActivity extends BaseActivity implements
		OnClickListener {

	/** UI */
	private EditText accountInput;
	private EditText pwdInput;
	private Button submitBtn;
	private ImageButton sinaLogin;
	private ImageButton qqLogin;
	private CheckBox checkIsRead;
	private TextView txtRead;

	private Context context;
	/** 注册实体bean */
	private LoginEntity regZeroData;
	private LoginSQLAdapter dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registration_account);
		context = this;
		GBApplication.gbapp.addActivitToStack(this);
		dbHelper = new LoginSQLAdapter(this);
		// init UI
		accountInput = (EditText) findViewById(R.id.account_edittext);
		pwdInput = (EditText) findViewById(R.id.password_edittext);
		submitBtn = (Button) findViewById(R.id.register_submit);
		sinaLogin = (ImageButton) findViewById(R.id.sina_login);
		qqLogin = (ImageButton) findViewById(R.id.qq_login);
		checkIsRead = (CheckBox) findViewById(R.id.check_is_read);
		txtRead = (TextView) findViewById(R.id.txt_read_references);
		txtRead.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		txtRead.getPaint().setAntiAlias(true);

		submitBtn.setOnClickListener(this);
		sinaLogin.setOnClickListener(this);
		qqLogin.setOnClickListener(this);
		txtRead.setOnClickListener(this);
		MobclickAgent.onError(this);
	}

	@Override
	public void onClick(View v) {
		if (!v.isEnabled()) {
			Log.e("simon", "on click error, button is disabled");
			return;
		}
		if (!checkIsRead.isChecked()
				&& !(v.getId() == R.id.txt_read_references)){
			Toast.makeText(this, R.string.isAgree, Toast.LENGTH_LONG).show();
			return;
		}
		switch (v.getId()) {
		case R.id.register_submit: // 简易注册
			if (saveRegistrationZeroData()) {// 信息填写正确
				Properties info = new Properties();
				info.put("gbUserName", regZeroData.getGbUserName());
				info.put("password", Util.MD5(regZeroData.getGbPassword()));
				info.put("regSource", "android");
				sendRequest(SoapRes.URLCustomer,
						SoapRes.REQ_ID_REGISTRATION_USER_INFO, info,
						new BaseResponseHandler(this,
								DialogRes.DIALOG_SEND_REGISTER_REQUEST));
			}
			MobclickAgent.onEvent(this, "RegisterBtnClick", "RCommit1Btn", 1);// "SimpleButtonclick");
			uploadLoginLogNew("RegisterBtnClick", "", "RCommit1Btn", null);
			break;
		case R.id.sina_login: // sina 登入
			weiboLogin(Weibo.WEIBO_SINAL);
			MobclickAgent.onEvent(this, "RegisterBtnClick", "RSinaBtn", 1);// "SimpleButtonclick");
			uploadLoginLogNew("RegisterBtnClick", "", "RSinaBtn", null);
			break;
		case R.id.qq_login: // qq 登入
			weiboLogin(Weibo.WEIBO_QQ);
			MobclickAgent.onEvent(this, "RegisterBtnClick", "RQQBtn", 1);// "SimpleButtonclick");
			uploadLoginLogNew("RegisterBtnClick", "", "RQQBtn", null);
			break;
		case R.id.txt_read_references: // 查看免责申明
			Intent intent = new Intent(this, DisclaimerActivity.class);
			intent.putExtra("isShow", true);
			startActivity(intent);
			break;
		}
	}

	/**
	 * 检查表单是否符合规则
	 * 
	 * @return
	 */
	private boolean saveRegistrationZeroData() {
		regZeroData = new LoginEntity();
		String string;

		Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		if (accountInput.getText().toString().equals("")) {// 用户名不能为空
			// string = getString(R.string.username) +
			// getString(R.string.empty);
			// Toast.makeText(this, string, Toast.LENGTH_LONG).show();
			accountInput.requestFocus();
			accountInput.startAnimation(shake);
			return false;
		}
		if (!checkUserName()) {// 检查用户名是否有非法字符
			return false;
		}
		
		if (!Constant.DEBUG&&!Util.isEmail(accountInput.getText().toString().trim())) {
			string = getString(R.string.accountisnotemail);
			Toast.makeText(this, string, Toast.LENGTH_LONG).show();
			return false;
		}

		if (pwdInput.getText().toString().equals("")) {// 密码不能为空
			pwdInput.requestFocus();
			pwdInput.startAnimation(shake);
			// string = getString(R.string.password) +
			// getString(R.string.empty);
			// Toast.makeText(this, string, Toast.LENGTH_LONG).show();
			return false;
		}

		if (pwdInput.getText().toString().length() < 6) {// 密码不能小于6位
			string = getString(R.string.password)
					+ getString(R.string.passwordlen);
			Toast.makeText(this, string, Toast.LENGTH_LONG).show();
			return false;
		}

		// regZeroData.setMethodType(String.valueOf(0));
		regZeroData.setGbUserName(accountInput.getText().toString().trim());
		regZeroData.setGbPassword(pwdInput.getText().toString());
		// regZeroData.setMachineId(getLocalMacAddress());
		return true;
	}

	/**
	 * 检查用户名 是否符合规范
	 * 
	 * @return
	 */
	public boolean checkUserName() {
		String result = accountInput.getText().toString().replaceAll(" ", "");
		if (!Util.isEmail(result)){
			Toast toast = Toast.makeText(context,
					this.getString(R.string.check_email_address_new), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 220);
			toast.show();
			return false;
		}
		if (accountInput.getText().toString().length() > result.length()) {
			Toast toast = Toast.makeText(context,
					this.getString(R.string.accountrule), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 220);
			toast.show();
			return false;
		} else {
			return true;
		}
	}
	
	// /**
	// * 获取机器mac地址
	// *
	// * @return
	// */
	// private String getLocalMacAddress() {
	// String macAddress = "88888888";
	//
	// WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	// WifiInfo info = wifi.getConnectionInfo();
	// if (info.getMacAddress() != null) {
	// macAddress = info.getMacAddress();
	// }
	// return macAddress;
	// }
	public void updateUserDeviceToken(String userId){
		Properties info = new Properties();
		info.put("UserKey", userId);
		info.put("DeviceToken", Util.getDeviceId(context));
		info.put("Platform", "android");
		sendRequest(SoapRes.URLCustomer,
				SoapRes.REQ_ID_UPDATEUSERDEVICETOKEN, info,
				new BaseResponseHandler(this, DialogRes.DIALOG_ID_LOGINING));
	}
	/**
	 * 网络回调
	 * 
	 * @param o
	 * @param methodId
	 */
	@Override
	public void onReqResponse(Object o, int methodId) {
		super.onReqResponse(o, methodId);
		if (o != null) {
			if (o instanceof RegistrationUserInfoPaser) {// 注册

				RegistrationUserInfoPaser obj = ((RegistrationUserInfoPaser) o);

				if (obj.isSuccess()) {
					SharedPreferencesMgr.saveIsSave(true);
					SharedPreferencesMgr.saveIsAuto(true);

					SharedPreferencesMgr.setAccessToken_SINA("");
					SharedPreferencesMgr.setExpiresIn_SINA("");
					SharedPreferencesMgr.setUid_SINA("");

					SharedPreferencesMgr.setAccessToken_QQ("");
					SharedPreferencesMgr.setExpiresIn_QQ("");
					SharedPreferencesMgr.setOpenId_QQ("");

					SharedPreferencesMgr.setIsWeiboLogin(false);
					// SharedPreferencesMgr.setIsBindSuccess(false);

					LoginEntity entity = new LoginEntity();
					entity.setGbUserName(regZeroData.getGbUserName());
					entity.setGbPassword(regZeroData.getGbPassword());
					entity.setCustomerId(obj.getCustId());
					saveLoginInfoToLocal(entity);
					//发送devicetoken
					updateUserDeviceToken(obj.getCustId());
					LoginConfigEntity config = new LoginConfigEntity();
					config.userName = regZeroData.getGbUserName();
					config.passWord = regZeroData.getGbPassword();
					config.isSave = "1";
					config.isAuto = "1";
					dbHelper.insertLoginConfig(context, config);

					Intent intent = new Intent();
					intent.setClass(getBaseContext(),
							Registration_HospitalActivity.class);
					intent.putExtra("placeName", obj.getCity());
					startActivity(intent);

				} else {
					Toast.makeText(this, obj.getErrorMsg(), Toast.LENGTH_LONG)
							.show();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
