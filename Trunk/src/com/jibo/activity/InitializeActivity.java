package com.jibo.activity;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.app.DetailsData;
import com.jibo.app.news.AllActivity;
import com.jibo.app.news.NewsDetailActivity;
import com.jibo.app.push.PushConst;
import com.jibo.app.research.DetailItemClickListener;
import com.jibo.app.research.ResearchPageActivity;
import com.jibo.asynctask.DownloadAsyncTask1;
import com.jibo.asynctask.UnzipFileAsyncTask;
import com.jibo.base.src.EntityObj;
import com.jibo.common.BitmapManager;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.NetCheckReceiver;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.data.NewDBDataPaser;
import com.jibo.data.entity.AdvertisingEntity;
import com.jibo.dbhelper.LoginSQLAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * Ó¦ÓÃ³õÊ¼»¯Ò³Ãæ£¬¼ì²éÏà¹Ø²ÎÊý
 * 
 * @author simon
 * 
 */
public class InitializeActivity extends BaseActivity {

	/** Ò³ÃæÌáÊ¾ÎÄ×Ö */
	private TextView txtPreTint;
	/** ½ø¶ÈÌõÏà¹Ø */
	private LinearLayout lltProgressPanel;
	private TextView txtProgressText;
	private ProgressBar pbProgress;

	/** ÊÇ·ñÕýÔÚÏÂÔØ */
	public boolean isDownloading;

	/** ÓÃ»§µÇÂ¼ÐÅÏ¢±¾µØdb²Ù×÷ */
	private LoginSQLAdapter dbHelper;
	/** ½âÑ¹Ëõdb zip */
	private UnzipFileAsyncTask unzipTask;
	/** ÏÂÔØÈÎÎñmap */
	private HashMap<Integer, DownloadAsyncTask1> taskMap;
	/** ÍøÂçÇëÇó»Øµ÷ */
	private BaseResponseHandler baseHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gba);
		MobclickAgent.onError(this);

		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Constant.alarmId);
		Intent intent = getIntent();

		boolean isNotification = intent
				.getBooleanExtra("isNotification", false);
		Logs.i("=== isNotification " + isNotification);

		String pType = intent.getStringExtra("nType");
		String pid = intent.getStringExtra("pId");
		String id = intent.getStringExtra("id");
		Logs.i("=== pType " + pType);
		if (isNotification) {// Í³¼ÆÍÆËÍ
			Map<String, String> map = new HashMap<String, String>();
			map.put("pID", pid);
			map.put("platform", "android");
			map.put("deviceToken", Util.getDeviceId(context));
			MobclickAgent.onEvent(this, "PushEvent", map);// "SimpleButtonclick");
			uploadLoginLogNew("PushEvent", pid, "pID", null);
		}
		// unknow = 0,
		// 论文信息 = 1,
		// 问卷调查 = 2,
		// 医学新闻 = 3,
		// 用药安全 = 4,
		// 专题新闻 = 5
		if (isNotification) {
			PushConst.pushFlag = true;
			if (!TextUtils.isEmpty(pType) && pType.equals("3")) {
				PushConst.pushmodule = PushConst.News_MODULE;
			} else if (!TextUtils.isEmpty(pType) && pType.equals("1")) {
				PushConst.pushmodule = PushConst.RESEARCH_MODULE;
			} else if (!TextUtils.isEmpty(pType) && pType.equals("###")) {
				PushConst.pushmodule = PushConst.RESEARCH_MODULE;
			}
			PushConst.id = id;
		}
		inits();
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			showDialog(DialogRes.DIALOG_ID_SDCARD_NOT_AVAILABLE);
		} else {
			if (checkUserInfo()) {
				skipToNextPage();
			} else {
				if (!checkUserInfo()) {
					Intent intent1 = new Intent(this,
							FirstStartForChooseActivity.class);
					startActivity(intent1);
				}
			}
		}

	}

	/***
	 * ³õÊ¼»¯×é¼þ
	 */
	public void inits() {
		dbHelper = new LoginSQLAdapter(this);
		baseHandler = new BaseResponseHandler(this, false);
		txtPreTint = (TextView) findViewById(R.id.pretint);
		lltProgressPanel = (LinearLayout) findViewById(R.id.dialogupgrade);
		txtProgressText = (TextView) lltProgressPanel
				.findViewById(R.id.txt_progressText);
		pbProgress = (ProgressBar) lltProgressPanel
				.findViewById(R.id.pb_gbaProgress);
		pbProgress.setMax(100);
		taskMap = new HashMap<Integer, DownloadAsyncTask1>();
	}

	/**
	 * ¼ì²édb,Èç¹ûÊý¾ÝÕý³££¬Ìø×ªµ½ÏÂÒ»Ò³£¬Èç¹ûdbÒì³££¬³¢ÊÔ²éÕÒÊÇ·ñÓÐdb
	 * zip°ü²¢½âÑ¹
	 */
	public void skipToNextPage() {
		if (Util.checkData(context)) {
			startNext();
		} else {
			unzipDbZip();
		}
	}

	/***
	 * ³¢ÊÔ½âÑ¹JIBOÎÄ¼þ¼ÐÄÚµÄdb zip°ü
	 */
	private void unzipDbZip() {
		unzipTask = new UnzipFileAsyncTask(this,
				new UnzipFileAsyncTask.UnzipCallBack() {
					@Override
					public void onFinish(boolean result) {
						// ²»ÂÛ½âÑ¹ÊÇ·ñ³É¹¦£¬ÔÙ´Î¼ì²édb
						if (Util.checkData(context)) {
							startNext();
						} else {// Èôdb»¹²»ÕýÈ·,ÏÂÔØÓ¦ÓÃ¶ÔÓ¦µÄdb°æ±¾zip°ü
							if (NetCheckReceiver.isWifi(context)) {
								updateDBFile();
							} else {
								new AlertDialog.Builder(context)
										.setTitle(
												context.getResources()
														.getString(
																R.string.wifi_tips))
										.setMessage(
												context.getResources()
														.getString(
																R.string.wifi_content))
										.setPositiveButton(
												context.getResources()
														.getString(
																R.string.wifi_ok),
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
														updateDBFile();
														dialog.cancel();
													}
												})
										.setNegativeButton(
												context.getResources()
														.getString(
																R.string.wifi_later),
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
														dialog.cancel();
													}
												}).create().show();
							}
						}
					}
				});
		unzipTask.execute();
	}

	/***
	 * »ñÈ¡Ó¦ÓÃ¶ÔÓ¦µÄdbÏÂÔØµØÖ·
	 */
	public void updateDBFile() {
		Properties propertyInfo = new Properties();
		propertyInfo.put(SoapRes.KEY_NEW_P_VERSION,
				Util.errorHandle2_0_2(Util.getCurrentVerName(this)));
		sendRequest(SoapRes.URLNewVersion, SoapRes.REQ_ID_NEW_VERSION,
				propertyInfo, baseHandler);
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			if (o instanceof NewDBDataPaser) {// »ñÈ¡dbÏÂÔØµØÖ·
				NewDBDataPaser dataPaser = (NewDBDataPaser) o;
				DownloadAsyncTask1 downloadTask = new DownloadAsyncTask1(this,
						DownloadAsyncTask1.s_downLoadID, dataPaser.getUrl(),
						new DownloadAsyncTask1.CallBack() {// ÏÂÔØdb
							@Override
							public void onFinish(boolean result) {
								if (result) {
									/** ÏÂÔØ×ÊÔ´ºÍdb³É¹¦£¬½âÑ¹dbÎÄ¼þ */
									unzipDbZip();
								} else {
									Toast.makeText(
											context,
											context.getString(R.string.losedownload),
											1).show();
								}
							}
						}, true);
				downloadTask.execute();
				taskMap.put(DownloadAsyncTask1.s_downLoadID, downloadTask);
			}
		}
	}

	/**
	 * Ìø×ªÏÂÒ»Ò³Ãæ£¬²¢ÈÃµ±Ç°Ò³ÃæÍ£Áô0.5Ãë
	 */
	private void startNext() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = getForwardIntent();
				if (intent == null) {
					return;
				}
				startActivity(intent);
				finish();
			}
		}, 500);
	}

	/**
	 * ¼ì²éÓÃ»§ÃûÊÇ·ñÓÐÐ§
	 * 
	 * @return
	 */
	private boolean checkUserInfo() {
		String name;
		if (Constant.DEBUG)
			name = "kkk3";
		else
			name = SharedPreferencesMgr.getUserName();
		// String pwd = SharedPreferencesMgr.getPassword();
		// ¼ì²éÓÃ»§ÃûÃÜÂëÊÇ·ñÓÐÐ§
		return !TextUtils.isEmpty(name);
	}

	/**
	 * »ñÈ¡Ìø×ªintent
	 * 
	 * @return
	 */
	private Intent getForwardIntent() {
		Intent intent = null;

		// ³õ´ÎÊ¹ÓÃ£¬Ìø×ªÖÁÑ¡ÔñÒ³
		if (SharedPreferencesMgr.isFirstLaunched()) {
			intent = new Intent(this, FirstStartForChooseActivity.class);
			return intent;
		}

		// µ±Ç°ÓÃ»§ÃûºÍÃÜÂëÎÞÐ§Ê±£¬Ìø×ªÖÁµÇÂ¼Ò³
		if (!checkUserInfo()) {
			intent = new Intent(this, LoginActivity.class);
			return intent;
		}

		AdvertisingEntity advert = dbHelper.getAdvertising(SharedPreferencesMgr
				.getUserName());

		// ±¾µØÊÇ·ñÓÐ»º´æ¹ã¸æÍ¼Æ¬
		boolean isHasImage = null != advert && null != advert.imageUrl
				&& !"".equals(advert.imageUrl)
				&& null != BitmapManager.loadBitmap(advert.imageUrl);

		// Ä¬ÈÏ×Ô¶¯µÇÂ¼
		txtPreTint.setText(getString(R.string.logining));
		if (isHasImage) {
			intent = new Intent(this, AdvertisingActivity.class);
			intent.putExtra("className", "com.jibo.activity.HomePageActivity");
			intent.putExtra("imageUrl", advert.imageUrl);
		} else {
			if (PushConst.pushFlag != null && PushConst.pushFlag) {
				if (PushConst.pushmodule == PushConst.RESEARCH_MODULE) {
					EntityObj eo = new EntityObj(PushConst.id);
					if (DetailsData.cacheEntities == null) {
						DetailsData.cacheEntities = new ArrayList();
					}
					if (DetailsData.entities == null) {
						DetailsData.entities = new ArrayList();
					}
					if (!DetailsData.getEntities().contains(eo)) {
						DetailsData.getEntities().add(eo);
					}
					DetailItemClickListener.openResearch(context, eo, null,
							null);
				} else if (PushConst.pushmodule == PushConst.News_MODULE) {
					// EntityObj eo = new EntityObj(PushConst.id);
					// if (DetailsData.entities == null) {
					// DetailsData.entities = new ArrayList();
					// }
					// if (!DetailsData.entities.contains(eo)) {
					// DetailsData.entities.add(eo);
					// }
					// AllActivity.openNews(context, DetailsData.entities, eo);
					startActivity(new Intent(this, ResearchPageActivity.class));
				}
				PushConst.resetPush();
				this.finish();
				return null;
			}
			intent = new Intent(this, HomePageActivity.class);
		}
		intent.putExtra("FROM", "GBApp");
		return intent;
	}

	@Override
	public void setDownloadProgress(int progress, String title, int id) {
		if (getString(R.string.checkdbversion).equals(
				txtPreTint.getText().toString())) {
			txtPreTint.setText("");
			isDownloading = true;
		}
		if (progress == 100) {
			pbProgress.setProgress((int) progress);
			txtProgressText.setText(getString(R.string.unziping));
		} else {
			if (lltProgressPanel.getVisibility() == View.GONE) {
				lltProgressPanel.setVisibility(View.VISIBLE);
			}
			pbProgress.setProgress((int) progress);
			txtProgressText.setText(getString(R.string.downloading) + " "
					+ progress + "%");
		}
		super.setDownloadProgress(progress, title, id);
	}

	@Override
	public void clickPositiveButton(int dialogId) {
		switch (dialogId) {
		case DialogRes.DIALOG_ID_CANCEL_DOWNLOAD:
			if (taskMap.size() > 0) {
				for (Entry<Integer, DownloadAsyncTask1> en : taskMap.entrySet()) {
					if (!((DownloadAsyncTask1) en.getValue()).isCancelled()) {
						((DownloadAsyncTask1) en.getValue()).cancel(true);
						((DownloadAsyncTask1) en.getValue()).s_canDownload = false;
					}
				}
			}
			GBApplication.gbapp.quit();
			finish();
			break;
		case DialogRes.DIALOG_ID_NETWORK_NOT_AVALIABLE:
			if (Util.checkData(context)) {
				skipToNextPage();
			} else {
				GBApplication.gbapp.quit();
				finish();
			}
			break;
		}
		super.clickPositiveButton(dialogId);
	}

	public void onErrResponse(Throwable error, String content) {
		if (error instanceof FileNotFoundException) {
			System.out.println("***    FileNotFoundException     ***");
		} else if (error instanceof SocketTimeoutException) {
			System.out.println("***    SocketTimeoutException     ***");
		} else if (error instanceof SocketException) {
			System.out.println("***    SocketException     ***");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (isDownloading) {
				showDialog(DialogRes.DIALOG_ID_CANCEL_DOWNLOAD);
			} else {
				GBApplication.gbapp.quit();
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
