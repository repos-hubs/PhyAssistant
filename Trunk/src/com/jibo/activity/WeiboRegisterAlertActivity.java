package com.jibo.activity;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.common.SharedPreferencesMgr;
import com.weibo.net.AsyncWeiboRunner;
import com.weibo.net.AsyncWeiboRunner.RequestListener;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

/**
 * δ�󶨵�΢���˺ţ���һ�ΰ�ʹ��ʱ��������ʾ����
 * 
 * @author simon
 * 
 */
public class WeiboRegisterAlertActivity extends BaseActivity implements
		OnClickListener {

	private Intent intent;
	/** UI */
	private TextView titleText;
	private Button submitBtn;// ȷ��
	private CheckBox checkBox;
	private ImageView weiboImg;
	

//	private String sinaShareUrl = "http://t.cn/zWCqGXz";
//	private String qqShareUrl = "http://url.cn/4rXGJS";

	private String shareContent = "������ʹ��һ��ҽѧӦ��#����#������ʱ��ز�ѯҩƷ��Ϣ�����һʱ���˽�ҽѧǰ����Ѷ������ʱʹ��ҽѧ�������𣿿������ذɣ�"+DOWNLOADURL
			+ "101." + "0." + SharedPreferencesMgr.getUserID() + ".2.tsina";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weibo_register_alert);
		intent = getIntent();
		
		titleText = (TextView) findViewById(R.id.weibo_login_success_text);
		weiboImg = (ImageView) findViewById(R.id.weibo_img);
		checkBox = (CheckBox) findViewById(R.id.checkbox_item);
		submitBtn = (Button) findViewById(R.id.register_submit);
		
		
		titleText.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		titleText.getPaint().setFakeBoldText(true);
		
		switch (SharedPreferencesMgr.getCurrentWeiboPlatform()) {
		case Weibo.WEIBO_QQ:
			weiboImg.setBackgroundResource(R.drawable.weibo_loginsuccess_qq);
			titleText.setText("��Ѷ΢����Ȩ�ɹ�");
//			shareContent = shareContent + qqShareUrl;
			break;
		case Weibo.WEIBO_SINAL:
			weiboImg.setBackgroundResource(R.drawable.weibo_loginsuccess_sina);
			titleText.setText("����΢����Ȩ�ɹ�");
//			shareContent = shareContent + sinaShareUrl;
			break;
		}

		checkBox.setChecked(true);
		submitBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (!v.isEnabled()) {
			Log.e("simon", "on click error, button is disabled");
			return;
		}
		switch (v.getId()) {
		case R.id.register_submit:// ȷ��
			if (checkBox.isChecked()) {
				// ����΢��
				try {
					shareToWeibo(Weibo.getInstance(), shareContent, "", "",
							SharedPreferencesMgr.getCurrentWeiboPlatform());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (WeiboException e) {
					e.printStackTrace();
				}
			}
			intent.setClass(this, Registration_HospitalActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * ����
	 * 
	 * @param weibo
	 * @param content
	 * @param lon
	 * @param lat
	 * @param platForm
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws WeiboException
	 */
	public void shareToWeibo(final Weibo weibo, String content, String lon,
			String lat, int platForm) throws MalformedURLException,
			IOException, WeiboException {

		RequestListener listener = new RequestListener() {// ��̨�̷߳���΢�����ݲ�����

			@Override
			public void onComplete(String response, int platForm) {// �ɹ�����

			}

			@Override
			public void onIOException(IOException e) {

			}

			@Override
			public void onError(WeiboException e, int platForm) {// ������

			}
		};

		switch (platForm) {
		case Weibo.WEIBO_QQ:
			update_QQ(weibo, Weibo.getAppKey(), content, lon, lat, listener);
			break;
		case Weibo.WEIBO_SINAL:
			update_SINA(weibo, Weibo.getAppKey(), content, lon, lat, listener);
			break;
		}

	}

	/**
	 * �����ַ������ˣ�
	 * 
	 * @param weibo
	 * @param source
	 * @param status
	 * @param lon
	 * @param lat
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws WeiboException
	 */
	public void update_SINA(final Weibo weibo, String source, String status,
			String lon, String lat, RequestListener listener)
			throws MalformedURLException, IOException, WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		Utility.setAuthorization(new Oauth2AccessTokenHeader());
		bundle.add("source", source);
		bundle.add("status", status);
		if (!TextUtils.isEmpty(lon)) {
			bundle.add("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			bundle.add("lat", lat);
		}
		String url = Weibo.SERVER_SINA + "statuses/update.json";
		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
		weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST,
				listener, Weibo.WEIBO_SINAL);
	}

	/**
	 * �����ַ�����Ѷ��
	 * 
	 * @param weibo
	 * @param source
	 * @param status
	 * @param lon
	 * @param lat
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws WeiboException
	 */
	private void update_QQ(final Weibo weibo, String source, String status,
			String lon, String lat, RequestListener listener)
			throws MalformedURLException, IOException, WeiboException {

		WeiboParameters bundle = new WeiboParameters();
		bundle.add("oauth_consumer_key", source);
		bundle.add("access_token", Weibo.getInstance().getAccessToken()
				.getToken());
		bundle.add("openid", SharedPreferencesMgr.getOpenId_QQ());
		bundle.add("clientip", getNetIp("http://fw.qq.com/ipaddress"));
		bundle.add("oauth_version", "2.a");
		bundle.add("scope", "all");
		bundle.add("format", "json");
		bundle.add("content", status);
		bundle.add("syncflag", "0");
		if (!TextUtils.isEmpty(lon)) {
			bundle.add("jing", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			bundle.add("wei", lat);
		}

		String url = Weibo.SERVER_QQ + "t/add";
		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
		weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST,
				listener, Weibo.WEIBO_SINAL);
	}

}
