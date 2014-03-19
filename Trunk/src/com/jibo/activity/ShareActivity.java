/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jibo.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.weibo.net.AsyncWeiboRunner;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

/**
 * A dialog activity for sharing any text or image message to weibo. Three
 * parameters , accessToken, tokenSecret, consumer_key, are needed, otherwise a
 * WeiboException will be throwed.
 * 
 * ShareActivity should implement an interface, RequestListener which will
 * return the request result.
 * 
 * @author ZhangJie (zhangjie2@staff.sina.com.cn)
 */

public class ShareActivity extends BaseActivity implements OnClickListener {

	private TextView mTextNum;
	private Button mSend;
	private EditText mEdit;
	private FrameLayout mPiclayout;
	private TextView shareTitle;
	
	private String mPicPath = "";
	private String mContent = "";
	private int platForm;

	public static final String EXTRA_WEIBO_CONTENT = "com.weibo.android.content";
	public static final String EXTRA_PIC_URI = "com.weibo.android.pic.uri";
	
	public static final String PLATFORM = "platForm";

	public static final int WEIBO_MAX_LENGTH = 140;

	private ProgressDialog proDialog;

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.shareactivity);
		
		proDialog = new ProgressDialog(this);
		proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		proDialog.setMessage(getResources().getString(R.string.shareing));
		proDialog.setIndeterminate(false);
		proDialog.setCancelable(true);
		proDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		Intent in = this.getIntent();
		mPicPath = in.getStringExtra(EXTRA_PIC_URI);
		mContent = in.getStringExtra(EXTRA_WEIBO_CONTENT);
		platForm = in.getIntExtra(PLATFORM,0);

		ImageButton close = (ImageButton) this
				.findViewById(R.id.btnClose);
		close.setOnClickListener(this);
		mSend = (Button) this.findViewById(R.id.btnSend);
		mSend.setOnClickListener(this);
		LinearLayout total = (LinearLayout) this
				.findViewById(R.id.ll_text_limit_unit);
		total.setOnClickListener(this);
		mTextNum = (TextView) this
				.findViewById(R.id.tv_text_limit);
		shareTitle= (TextView) this
				.findViewById(R.id.share_title);
		
		ImageView picture = (ImageView) this
				.findViewById(R.id.ivDelPic);
		picture.setOnClickListener(this);

		switch(platForm){
		case Weibo.WEIBO_SINAL:
			shareTitle.setText(R.string.share_dialog_title_sina);
			break;
		case Weibo.WEIBO_QQ:
			shareTitle.setText(R.string.share_dialog_title_qq);
			break;
		}
		
		
		mEdit = (EditText) this.findViewById(R.id.etEdit);
		mEdit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {// 记录字数
				String mText = mEdit.getText().toString();
				int len = mText.length();
				if (len <= WEIBO_MAX_LENGTH) {
					len = WEIBO_MAX_LENGTH - len;
					mTextNum.setTextColor(R.color.text_num_gray);
					if (!mSend.isEnabled())
						mSend.setEnabled(true);
				} else {// 超出最大限制
					len = len - WEIBO_MAX_LENGTH;

					mTextNum.setTextColor(Color.RED);
					if (mSend.isEnabled())
						mSend.setEnabled(false);
				}
				mTextNum.setText(String.valueOf(len));
			}
		});
		mEdit.setText(mContent);
		mPiclayout = (FrameLayout) ShareActivity.this
				.findViewById(R.id.flPic);
		if (TextUtils.isEmpty(this.mPicPath)) {
			mPiclayout.setVisibility(View.GONE);
		} else {
			mPiclayout.setVisibility(View.VISIBLE);
			File file = new File(mPicPath);
			if (file.exists()) {
				Bitmap pic = BitmapFactory.decodeFile(this.mPicPath);
				ImageView image = (ImageView) this
						.findViewById(R.id.ivImage);
				image.setImageBitmap(pic);
			} else {
				mPiclayout.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		if (viewId == R.id.btnClose) {// 关闭
			finish();
		} else if (viewId == R.id.btnSend) {// 发送
			proDialog.show();
			Weibo weibo = Weibo.getInstance();
			try {
				if (!TextUtils.isEmpty((String) (weibo.getAccessToken()
						.getToken()))) {
					this.mContent = mEdit.getText().toString();
					if (!TextUtils.isEmpty(mPicPath)) {
						upload(weibo, Weibo.getAppKey(), this.mPicPath,
								this.mContent, "", "");

					} else {
						// Just update a text weibo!
						update(weibo, Weibo.getAppKey(), mContent, "", "",platForm);
					}
				} else {
					Toast.makeText(this, this.getString(R.string.please_login),
							Toast.LENGTH_LONG);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WeiboException e) {
				e.printStackTrace();
			}
		} else if (viewId == R.id.ll_text_limit_unit) {// 删除内容
			Dialog dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.attention)
					.setMessage(R.string.delete_all)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									mEdit.setText("");
								}
							}).setNegativeButton(R.string.cancel_weibo, null)
					.create();
			dialog.show();
		} else if (viewId == R.id.ivDelPic) {// 删除图片
			Dialog dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.attention)
					.setMessage(R.string.del_pic)
					.setPositiveButton(R.string.weibo_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									mPiclayout.setVisibility(View.GONE);
								}
							}).setNegativeButton(R.string.cancel, null)
					.create();
			dialog.show();
		}
	}

	/**
	 * 带图片分享
	 * 
	 * @param weibo
	 * @param source
	 * @param file
	 * @param status
	 * @param lon
	 * @param lat
	 * @throws WeiboException
	 */
	private void upload(Weibo weibo, String source, String file, String status,
			String lon, String lat) throws WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", source);
		bundle.add("pic", file);
		bundle.add("status", status);
		if (!TextUtils.isEmpty(lon)) {
			bundle.add("lon", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			bundle.add("lat", lat);
		}
		String url = Weibo.SERVER_SINA + "statuses/upload.json";
		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
		weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this,platForm);

	}

	private void update(final Weibo weibo, String source, String content,
			String lon, String lat,int platFrom) throws MalformedURLException, IOException,
			WeiboException {
		switch(platFrom){
		case Weibo.WEIBO_SINAL:
			update_SINA(weibo, Weibo.getAppKey(), content, lon, lat);
			break;
		case Weibo.WEIBO_QQ:
			update_QQ(weibo, Weibo.getAppKey(), content, lon, lat);
			break;
		}
	}
	
	
	/**
	 * 纯文字分享（新浪）
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
			String lon, String lat) throws MalformedURLException, IOException,
			WeiboException {
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
		weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this,platForm);
	}
	
	/**
	 * 纯文字分享（腾讯）
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
			String lon, String lat) throws MalformedURLException, IOException,
			WeiboException {
		
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
		weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this,platForm);
	}
	

	@Override
	public void onComplete(String response,int platFrom) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				proDialog.dismiss();
				Toast.makeText(ShareActivity.this, R.string.send_sucess,
						Toast.LENGTH_LONG).show();
			}
		});
		GBApplication.gbapp.setHomeLaunched(false);
		GBApplication.gbapp.setStartActivity(true);
		this.finish();

	}

	@Override
	public void onIOException(IOException e) {

	}

	@Override
	public void onError(final WeiboException e,final int platFrom) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				proDialog.dismiss();
				Weibo weibo = Weibo.getInstance();
				Log.i("simon", "errorCode:"+e.getStatusCode());
				switch (e.getStatusCode()) {
				case 37:
					weibo.setupConsumerConfig(Constant.CONSUMER_KEY_QQ,
							Constant.CONSUMER_SECRET_QQ);
					weibo.setRedirectUrl(Constant.CALLBACK_URL_QQ);
					weibo.authorize(ShareActivity.this, new AuthDialogListener(
							mContent,Weibo.WEIBO_QQ),Weibo.WEIBO_QQ);
					break;
				case 21315:// token失效
					weibo.setupConsumerConfig(Constant.CONSUMER_KEY_SINA,
							Constant.CONSUMER_SECRET_SINA);
					weibo.setRedirectUrl(Constant.CALLBACK_URL_SINA);
					weibo.authorize(ShareActivity.this, new AuthDialogListener(
							mContent,Weibo.WEIBO_SINAL),Weibo.WEIBO_SINAL);
					break;
				case 20019:// 连续重复分享同样的内容
					Toast.makeText(ShareActivity.this, R.string.repeat_content,
							Toast.LENGTH_LONG).show();
					break;
				default:
					Toast.makeText(ShareActivity.this,
							e.getMessage(),
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
					break;

				}
				//
				// Toast.makeText(
				// ShareActivity.this,
				// String.format(
				// ShareActivity.this
				// .getString(R.string.send_failed)
				// + ":%s", e.getMessage()),
				// Toast.LENGTH_LONG).show();
			}
		});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
