package com.jibo.activity;

import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jibo.common.SoapRes;
import com.jibo.data.EmailAdancedPaser;
import com.jibo.net.BaseResponseHandler;
import com.api.android.GBApp.R;

public class EmailActivity extends BaseActivity implements OnClickListener {
	WebView mWebView = null;
	private Handler mHandler = new Handler();
	View prg = null;
	Button btn = null;
	EditText tvToBody, emailTitle;
	String toEmail;
	String subject;
	Context context;
	public static final String LOGIN_FLAG = "login_flag";
	public static SharedPreferences setting;
	String userId = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_PROGRESS);// prime
		setContentView(R.layout.emailactivity);
		setting = getSharedPreferences(LOGIN_FLAG, 0);
		userId = setting.getString("userId", "");
		mWebView = (WebView) findViewById(R.id.webview);
		context = this;
		btn = (Button) findViewById(R.id.helpBtn);
		tvToBody = (EditText) findViewById(R.id.sendTo);
		emailTitle = (EditText) findViewById(R.id.title);
		btn.setOnClickListener(this);
		prg = findViewById(R.id.dialogprogress);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new Object() {
			public void clickOnAndroid() {
				mHandler.post(new Runnable() {
					public void run() {
						mWebView.loadUrl("javascript:wave()");
					}
				});
			}
		}, "demo");
		mWebView.loadUrl("http://jibo.cn/d/email_share-download.htm");
		prg.setVisibility(prg.GONE);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int viewId = v.getId();
		switch (viewId) {
		case R.id.helpBtn: {
			toEmail = tvToBody.getText().toString();
			subject = emailTitle.getText().toString();
			// ·¢ËÍÓÊ¼þ
			Properties info = new Properties();
			info.put("userid", userId);
			info.put("sendTo", toEmail);
			info.put("title", subject);
			info.put("category", "1");
			info.put("body", "");
			sendRequest(SoapRes.URLSendMail, SoapRes.REQ_ID_SEND_MAIL, info,
					new BaseResponseHandler(this, false));
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onReqResponse(Object o, int methodId) {

		if (o != null) {
			if (o instanceof EmailAdancedPaser) {
				String string = "";
				if (((EmailAdancedPaser) o).isSuccess()) {
					string = context.getString(R.string.sendSuccessful);
				} else {
					string = context.getString(R.string.sendFailed);
				}
				Toast toast = Toast
						.makeText(context, string, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 220);
				toast.show();
				finish();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (isPopflg()) {
				dissMissPop();
			} else {
				if (iCurState == STATE_CONNECTING) {
					cancelConnection();
					iCurState = STATE_STOP;
				}
				this.finish();
			}
			return true;
		}
		return false;
	}

}
