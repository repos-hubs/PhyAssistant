package com.jibo.activity;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.data.RegistrationCheckTheUserIsValidPaser;
import com.jibo.data.RegistrationSubmitInfoPaser;
import com.jibo.data.UpdatePasswordPaser;
import com.jibo.data.entity.LoginConfigEntity;
import com.jibo.data.entity.RegistrationEntity;
//import com.jibo.dbhelper.DepartmentAdapter;
import com.jibo.dbhelper.LoginSQLAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.ui.HomePageLayout;

/**
 *修改密码
 * @author simon
 * 
 */
public class Registration_ChangePwdActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "Registration";

	/* view controls */
	private TextView accountTitle;
	private TextView oldPasswordTitle;
	private TextView newPasswordTitle;
	private TextView newPasswordTitle1;
	
	
	private TextView mAccountTextView;
	private EditText editOldPassword;
	private EditText editNewPassword;
	private EditText editNewPassword1;
	
	private final String NULL = "";
	private String newPassword = "";

	private Button mNext;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registration_00);
		context = this;

		initSubTitle();
	}
	

	private void initSubTitle() {
		String star = "";
		accountTitle = (TextView) findViewById(R.id.account);
		oldPasswordTitle = (TextView) findViewById(R.id.oldpasswordtitle);
		newPasswordTitle = (TextView) findViewById(R.id.newpasswordTitle);
		newPasswordTitle1 = (TextView) findViewById(R.id.newpasswordTitle1);

		accountTitle.setText(getString(R.string.account) + star
					+ getString(R.string.colon));
		oldPasswordTitle.setText(getString(R.string.oldpassword) + star
					+ getString(R.string.colon));
		newPasswordTitle.setText(getString(R.string.password2) + star
					+ getString(R.string.colon));
		newPasswordTitle1.setText(getString(R.string.confirmnewpassword)
					+ star + getString(R.string.colon));

		mAccountTextView = (TextView) findViewById(R.id.textAccount);
		editOldPassword = (EditText) findViewById(R.id.oldpassword);
		editNewPassword = (EditText) findViewById(R.id.newpassword);
		editNewPassword1 = (EditText) findViewById(R.id.newpassword1);
		mAccountTextView.setText(SharedPreferencesMgr.getUserName());
		mNext = (Button) findViewById(R.id.RegNext);
		mNext.setText(R.string.send);
		mNext.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		if (!v.isEnabled()) {
			Log.e(TAG, "on click error, button is disabled");
			return;
		}
		switch (v.getId()) {
		case R.id.RegNext:// 详细信息or发送
				updatePassword();
			break;
		}
	}

	/**
	 * 修改密码
	 */
	private void updatePassword() {
		String string;
		String newOldPass = editOldPassword.getText().toString().trim();
		if (newOldPass.equals(NULL)) {// 旧密码不能为空
			string = getString(R.string.oldpassword)
					+ getString(R.string.empty);
			Toast.makeText(this, string, Toast.LENGTH_LONG).show();
			return;
		}
		String oldPass = SharedPreferencesMgr.getPassword();
		
		if (!newOldPass.equals(oldPass)) {// 旧密码是否填写正确
			Toast.makeText(Registration_ChangePwdActivity.this,
					getString(R.string.verifypasserror), Toast.LENGTH_SHORT)
					.show();
			editOldPassword.setText(NULL);
			editOldPassword.setFocusableInTouchMode(true);
			editOldPassword.setFocusable(true);
			editOldPassword.requestFocus();
			return;
		}

		newPassword = editNewPassword.getText().toString().trim();
		String retrypassword = editNewPassword1.getText().toString().trim();
		
		if (newPassword.equals(NULL)) {// 新密码不能为空
			string = getString(R.string.password) + getString(R.string.empty);
			Toast.makeText(this, string, Toast.LENGTH_LONG).show();
			return;
		}
		if (newPassword.length() < 6) {
			string = getString(R.string.password)
					+ getString(R.string.passwordlen);
			Toast.makeText(this, string, Toast.LENGTH_LONG).show();
			return;
		}
		if (retrypassword.equals(NULL)) {
			string = getString(R.string.retrypassword)
					+ getString(R.string.empty);
			Toast.makeText(this, string, Toast.LENGTH_LONG).show();
			return;
		}
		if (retrypassword.length() < 6) {
			string = getString(R.string.password)
					+ getString(R.string.passwordlen);
			Toast.makeText(this, string, Toast.LENGTH_LONG).show();
			return;
		}

		if (!newPassword.equals(retrypassword)) {// 新密码是否填写一致
			Toast.makeText(this, getString(R.string.newpassnot),
					Toast.LENGTH_LONG).show();
			editNewPassword.setText(NULL);
			editNewPassword1.setText(NULL);
			editNewPassword.setFocusableInTouchMode(true);
			editNewPassword.setFocusable(true);
			editNewPassword.requestFocus();
			return;
		}

		Properties info = new Properties();
		info.put("userName", SharedPreferencesMgr.getUserName());
		info.put("oldPassword", Util.MD5(SharedPreferencesMgr.getPassword()));
		info.put("newPassword", Util.MD5(newPassword));
		sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_UPDATE_PASSWORD, info,
				new BaseResponseHandler(this,
						DialogRes.DIALOG_SEND_REGISTER_REQUEST));
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
			 if (o instanceof UpdatePasswordPaser) {
				if (((UpdatePasswordPaser) o).getIsSuccess()) {
					// 清空历史信息
					
					SharedPreferencesMgr.savePassword(newPassword);
					
					LoginSQLAdapter adapter = new LoginSQLAdapter(this);
					LoginConfigEntity config = new LoginConfigEntity();
					config.userName = SharedPreferencesMgr.getUserName();
					config.passWord = newPassword;
					config.isSave = "1";
					config.isAuto = "1";
					adapter.insertLoginConfig(context, config);
					Toast.makeText(this, R.string.updatePwdSuccess, Toast.LENGTH_LONG).show();
					this.finish();
				}else{
					Toast.makeText(this, R.string.updatePwdFailed, Toast.LENGTH_LONG).show();
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
