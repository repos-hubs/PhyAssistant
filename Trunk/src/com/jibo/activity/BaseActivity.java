package com.jibo.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import net.lingala.zip4j.core.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.asynctask.DownloadAsyncTask1;
import com.jibo.asynctask.DownloadAsyncTask1.CallBack;
import com.jibo.base.src.request.impl.soap.AsyncClient;
import com.jibo.base.src.request.impl.soap.AsyncRequest;
import com.jibo.common.BitmapManager;
import com.jibo.common.Constant;
import com.jibo.common.DeviceInfo;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.dao.DaoSession;
import com.jibo.data.GetZipFilePaser;
import com.jibo.data.WeiboLoginPaser;
import com.jibo.data.entity.AdvertisingEntity;
import com.jibo.data.entity.LoginEntity;
import com.jibo.dbhelper.ConfigAdapter;
import com.jibo.dbhelper.LoginSQLAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.net.BaseResponseHandler;
import com.jibo.share.weixin.WXEntryActivity;
import com.jibo.share.weixin.WeiXinTask;
import com.jibo.util.JiBoException;
import com.jibo.util.Logs;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.api.sns.UMSnsService;
import com.umeng.fb.UMFeedbackService;
import com.weibo.net.AccessToken;
import com.weibo.net.AsyncWeiboRunner.RequestListener;
import com.weibo.net.DialogError;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

/**
 * 脣霉脫脨脳脭露篓activity碌脛禄霉脌脿拢卢麓娄脌铆鹿芦脫脙dialog拢卢view拢卢menu碌脛脢脗录镁隆拢
 * 
 * @author peter.pan
 */
public class BaseActivity extends WXEntryActivity implements RequestListener {
	// soap loading dialog
	public ProgressDialog soapLoadingDialog;
	/**
	 * 录脟脗录碌卤脟掳碌脛脳麓脤卢
	 * */
	public int iCurState;

	public Context context;
	public static GBApplication app;
	public static String DOWNLOADURL = "http://www.jibo.cn/url.asp?p=";
	/**
	 * 碌卤脟掳Activity麓娄脫脷驴陋脢录脳麓脤卢
	 * */
	public static final int STATE_START = 1;
	/**
	 * 碌卤脟掳Activity麓娄脫脷脭脻脥拢脳麓脤卢
	 * */
	public static final int STATE_PAUSE = 2;
	/**
	 * 碌卤脟掳Activity麓娄脫脷脕卢陆脫脢媒戮脻脳麓脤卢
	 * */
	public static final int STATE_CONNECTING = 3;
	/**
	 * 碌卤脟掳Activity麓娄脫脷脧脭脢戮露脭禄掳驴貌脳麓脤卢
	 * */
	public static final int STATE_SHOW_DIALOG = 4;
	/**
	 * 碌卤脟掳Activity麓娄脫脷脥拢脰鹿脳麓脤卢
	 * */
	public static final int STATE_STOP = 5;

	/** 碌卤脟掳脟毛脟贸碌脛脧脽鲁脤脢媒 */
	public int curReqTimes;

	private Dialog lillyDialog;

	protected DaoSession daoSession;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		GBApplication.gbapp.addActivitToStack(this);
		super.onCreate(savedInstanceState);
		app = (GBApplication) getApplication();
		daoSession = app.getDaoSession();
	}

	public void tiprun() {
		hdler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				hdler.sendEmptyMessage(0);
			}
		});
	}

	Handler hdler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (TipHelper.toLaunchTheActivityTip((Activity) context)) {
				tipImpl();
			}
		}

	};

	public void showSoapLoadingDialog() {
		// TODO Auto-generated method stub

	}

	private LoginSQLAdapter dbHelper;

	private LinearLayout llt;

	private final String isNotDownload = "0";
	private final String iIsDownloading = "1";
	private final String iIsDownLoaded = "2";
	private ConfigAdapter configAdapter;

	@Override
	protected void onStart() {
		if (app.isStartActivity()) {
			app.setStartActivity(false);
		}

		try {
			if (app.isHomeLaunched()
					&& !getIntent().getBooleanExtra("isNotification", false)) {// Home录眉脥脣鲁枚潞贸脭脵麓脦陆酶脠毛脫娄脫脙
				dbHelper = new LoginSQLAdapter(this);
				AdvertisingEntity advert = dbHelper
						.getAdvertising(SharedPreferencesMgr.getUserName());
				boolean flag = null != advert && null != advert.imageUrl
						&& !"".equals(advert.imageUrl);
				if (flag) {
					Bitmap map = BitmapManager.loadBitmap(advert.imageUrl);
					if (null != map) {

						if (lillyDialog == null) {
							llt = new LinearLayout(context);
							llt.setBackgroundDrawable(new BitmapDrawable(map));
							lillyDialog = new Dialog(context,
									R.style.Dialog_Fullscreen);
							lillyDialog.setContentView(llt);
						}
						if (lillyDialog.isShowing())
							lillyDialog.dismiss();
						uploadLoginLogNew("LilySplash", getIntent()
								.getStringExtra("imageUrl"), "splashOnClick",
								null);
						lillyDialog.show();
						Timer timer = new Timer();
						timer.schedule(new showLillyPic(), 2000);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onStart();

	}

	public void tipImpl() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		iCurState = STATE_START;
	}

	@Override
	protected void onStop() {
		if (iCurState == STATE_SHOW_DIALOG && dialog.isShowing())
			dialog.cancel();
		if (curReqTimes > 0)
			cancelConnection();
		iCurState = STATE_STOP;
		if (!app.isStartActivity()) {
			app.setHomeLaunched(true);
		}

		// 脠莽鹿没脫脨pop脧脭脢戮脭貌脪镁虏脴.
		dissMissPop();
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			app.setHomeLaunched(false);
			if (iCurState == STATE_CONNECTING) {
				// removeDialog(DialogRes.DIALOG_ID_NET_CONNECT);
				cancelConnection();
			}
			if (hasPop && popflg) {
				dissMissPop();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}

	/** dialog脰脨脫脨脪禄赂枚掳麓脜楼 */
	static final int ONE_BUTTON = 1;
	/** dialog脰脨脫脨脕陆赂枚掳麓脜楼 */
	static final int TOW_BUTTONS = 3;
	/** dialog脰脨脫脨脠媒赂枚掳麓脜楼 */
	static final int THREE_BUTTONS = 7;
	/** 麓铆脦贸碌脛脨脜脧垄碌脛脳脰路没麓庐 */
	private String strErr;
	/** 麓铆脦贸碌脛脨脜脧垄碌脛Res id */
	private int iErrRes;

	protected AlertDialog dialog;

	@Override
	protected Dialog onCreateDialog(final int id) {
		AlertDialog.Builder builder = DialogRes.getBuild(this, id, true);
		int flag = 0;
		switch (id) {
		case DialogRes.DIALOG_ID_ERR:
			// 脧脭脢戮麓铆脦贸脨脜脧垄
			if (strErr != null)
				builder.setMessage(strErr);
			else if (iErrRes > 0)
				builder.setMessage(iErrRes);
			break;
		case DialogRes.DIALOG_INVITATION_REQUEST:
		case DialogRes.DIALOG_ERROR_PROMPT:
		case DialogRes.DIALOG_ID_NETWORK_NOT_AVALIABLE:
		case DialogRes.DIALOG_ID_CHECK_USERNAME_ISVALID:// 录矛虏茅脫脙禄搂脙没脢脟路帽脰脴赂麓
		case DialogRes.DIALOG_SEND_REGISTER_REQUEST:// 脮媒脭脷路垄脣脥脟毛脟贸
		case DialogRes.DIALOG_ID_JUMP_TO:// 脮媒脭脷脤酶脳陋
		case DialogRes.DIALOG_UPDATE_FOR_DATA:
		case DialogRes.DIALOG_ID_NET_CONNECT:
		case DialogRes.DIALOG_WAITING_FOR_DATA:// 脮媒脭脷禄帽脠隆脢媒戮脻...progressDialog
		case DialogRes.DIALOG_ID_AUTO_LOGINING:// 脳脭露炉碌脟脗录...
		case DialogRes.DIALOG_ID_LOGINING:// 脮媒脭脷碌脟脗录...
			break;// 脙禄脫脨掳麓脜楼
		case DialogRes.DIALOG_ID_SDCARD_NOT_AVAILABLE:
		case DialogRes.DIALOG_ID_LOGINING_FAILD:// 碌脟脗录脢搂掳脺
		case DialogRes.DIALOG_ID_NO_UPDATE:
		case DialogRes.DIALOG_GOTO_EVALUATE:
		case DialogRes.DIALOG_ID_SURVEY_SUBMIT_ERROR:
		case DialogRes.DIALOG_ID_SURVEY_SUBMIT_FULL:
		case DialogRes.DIALOG_ID_SURVEY_SUBMIT_HAVE_SUBMITTED:
		case DialogRes.DIALOG_ID_SURVEY_NOT_COMPLETE:
		case DialogRes.DIALOG_ID_INVITECODE_BLANK:
		case DialogRes.DIALOG_ID_NO_DATA:
		case DialogRes.DIALOG_ID_DATA_FULL:
		case DialogRes.DIALOG_ID_INVITECODE_FAIL:
			flag = ONE_BUTTON;
			break;
		case DialogRes.DIALOG_ID_DOWNLOAD_FAILED:
		case DialogRes.DIALOG_ID_HAS_UPDATE:
		case DialogRes.DIALOG_ID_NO_LISENCE:
		case DialogRes.DIALOG_ID_ERROR_LISENCE:
		case DialogRes.DIALOG_ID_CHANGE_DPT:
		case DialogRes.DIALOG_ID_NEW_DATA_AVAILABLE:
		case DialogRes.DIALOG_ID_DATA_ERROR:
		case DialogRes.DIALOG_REGIST_NOTIFY:
		case DialogRes.DIALOG_ID_UPDATE_NEW_VERSION:
		case DialogRes.DIALOG_ID_NEED_UPDATE:
		case DialogRes.DIALOG_INVITE_NOTIFY:
		case DialogRes.DIALOG_ID_SHARE_AFTER_SUBMIT:
		case DialogRes.DIALOG_ID_NO_INVITATION:
		case DialogRes.DIALOG_ID_SURVEY_DATA_LACK:
		case DialogRes.DIALOG_ID_CANCEL_DOWNLOAD:
		case DialogRes.DIALOG_ID_MUST_UPDATE:
			flag = TOW_BUTTONS;
			break;
		case DialogRes.DIALOG_QUIT_PROMPT:
			flag = THREE_BUTTONS;
			break;
		}
		if ((flag & 1) > 0) {
			builder.setPositiveButton(DialogRes.getPositiveTxt(id),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							clickPositiveButton(id);
						}
					});
		}
		if ((flag & 2) > 0) {
			builder.setNegativeButton(DialogRes.getNegativeTxt(id),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							clickNegativeButton(id);
						}
					});
		}
		if ((flag & 4) > 0) {
			builder.setNeutralButton(DialogRes.getNeutralTxt(id),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							clickNeutralButton(id);
						}
					});
		}
		dialog = builder.create();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// app.setDialog(false);
				onCancelDialog(id);
			}

		});

		return dialog;

	}

	/**
	 * 露脭禄掳驴貌脠隆脧没脢卤禄谩碌梅
	 * 
	 * @param dialogId
	 *            露脭禄掳驴貌ID
	 * */
	public void onCancelDialog(int dialogId) {
		if (dialogId == DialogRes.DIALOG_ID_NET_CONNECT
				&& iCurState == STATE_CONNECTING) {
			iCurState = STATE_START;
		}
	}

	/**
	 * 路垄鲁枚脪禄赂枚soap碌脛脟毛脟贸
	 * 
	 * @param url
	 *            脟毛脟贸碌脛url拢卢脭脷SoapRes脰脨脫脨露篓脪氓
	 * @param methodId
	 *            路陆路篓碌脛id拢卢脭脷SoapRes脰脨脫脨露篓脪氓
	 * @param propertyInfo
	 *            脟毛脟贸脣霉脨猫碌脛虏脦脢媒
	 * @param responseHandler
	 *            脟毛脟贸陆谩鹿没麓娄脌铆碌脛handler
	 * @return 脢脟路帽陆酶脠毛脟毛脟贸露脫脕脨
	 * */
	public boolean sendRequest(String url, int methodId,
			Properties propertyInfo, AsyncSoapResponseHandler responseHandler,
			boolean islazy) {
		if (DeviceInfo.instance.isNetWorkEnable()) {
			iCurState = STATE_CONNECTING;
			GBApplication.gbapp.soapClient.sendRequest(url, methodId,
					propertyInfo, responseHandler, this, islazy);
			return true;
		} else {
			showDialog(DialogRes.DIALOG_ID_NETWORK_NOT_AVALIABLE);
			return false;
		}
	}

	public boolean sendRequest(String url, int methodId,
			Properties propertyInfo, AsyncSoapResponseHandler responseHandler) {
		return sendRequest(url, methodId, propertyInfo, responseHandler, false);
	}

	public boolean sendRequest(String url, int methodId,
			Properties propertyInfo, AsyncSoapResponseHandler responseHandler,
			int visitId, boolean islazy) {
		if (visitId == AsyncRequest.VISIT_TYPE_SOAP) {
			return sendRequest(url, methodId, propertyInfo, responseHandler,
					islazy);
		}
		if (DeviceInfo.instance.isNetWorkEnable()) {
			// showDialog(DialogRes.DIALOG_ID_NET_CONNECT);
			iCurState = STATE_CONNECTING;
			AsyncClient.getInstance().sendRequest(url, methodId, propertyInfo,
					responseHandler, this, visitId);
			return true;
		} else {
			showDialog(DialogRes.DIALOG_ID_NETWORK_NOT_AVALIABLE);
			return false;
		}
	}

	// 脧脗脭脴碌脛脢卤潞貌赂眉脨脗陆酶露脠脤玫
	public void setDownloadProgress(int progress, String title, int id) {

	}

	// 脧脗脭脴碌脛脢卤潞貌赂眉脨脗陆酶露脠脤玫
	public void setDownloadProgress(long completeSize, String title, String id) {

	}

	// 脧脗脭脴碌脛脢卤潞貌赂眉脨脗陆酶露脠脤玫
	public void setDownloadProgress(int progress, String title, String id) {

	}

	/**
	 * 脠隆脧没赂脙脪鲁脙忙脣霉脫脨脟毛脟贸碌脛脕麓陆脫
	 * */
	public void cancelConnection() {
		curReqTimes = 0;
		GBApplication.gbapp.soapClient.cancelRequests(this, true);
	}

	/**
	 * 碌茫禄梅dialog脳贸录眉 脳脫脌脿脨猫脪陋脰脴脭脴
	 * 
	 * @param dialogId
	 *            露脭禄掳驴貌ID
	 * */
	public void clickPositiveButton(int dialogId) {
		removeDialog(dialogId);
		if (dialogId == DialogRes.DIALOG_QUIT_PROMPT
				|| dialogId == DialogRes.DIALOG_ID_SDCARD_NOT_AVAILABLE) {
			// 脠莽鹿没脢脟脥脣鲁枚
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			GBApplication.gbapp.quit();
		}

	}

	/**
	 * 碌茫禄梅dialog脫脪录眉 脳脫脌脿脨猫脪陋脰脴脭脴
	 * 
	 * @param dialogId
	 *            露脭禄掳驴貌ID
	 * */
	public void clickNegativeButton(int dialogId) {
		removeDialog(dialogId);
	}

	/**
	 * 碌茫禄梅dialog脰脨录盲录眉 脳脫脌脿脨猫脪陋脰脴脭脴
	 * 
	 * @param dialogId
	 *            露脭禄掳驴貌ID
	 * */
	public void clickNeutralButton(int dialogId) {
		if (dialogId == DialogRes.DIALOG_QUIT_PROMPT) {
			removeDialog(dialogId);
			Intent intent = new Intent();
			intent.setClass(getBaseContext(), LoginActivity.class);
			intent.putExtra("FROM", "HomePage");
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 脟毛脟贸路镁脦帽脮媒鲁拢路碌禄脴陆谩鹿没拢卢脳脫脌脿脨猫脪陋赂虏赂脟麓脣路陆路篓隆拢
	 * 
	 * @param o
	 *            路碌禄脴碌脛脢媒戮脻露脭脧贸
	 * @param methodId
	 *            脟毛脟贸碌脛Soap路陆路篓ID
	 * 
	 * */
	public void onReqResponse(Object o, int methodId) {
		if (null != o) {
			if (o instanceof WeiboLoginPaser) {
				WeiboLoginPaser loginPaser = (WeiboLoginPaser) o;
				boolean isBindSuccess = loginPaser.isBindSuccess();
				boolean isGetUserInfoSuccess = loginPaser
						.isGetUserInfoSuccess();

				LoginEntity entity = loginPaser.getLoginEntity();
				SharedPreferencesMgr.setIsWeiboLogin(true);
				SharedPreferencesMgr.saveIsAuto(true);
				saveLoginInfoToLocal(entity);

				if (isBindSuccess) {// 掳贸露篓脫脙禄搂鲁脡鹿娄
					Intent intent = new Intent();
					intent.putExtra("placeName", loginPaser.getPlaceName());
					intent.setClass(BaseActivity.this,
							WeiboRegisterAlertActivity.class);
					startActivity(intent);
				} else if (isGetUserInfoSuccess) {// 脪脩戮颅掳贸露篓拢卢禄帽脠隆脨脜脧垄
					String company = entity.getCompanyName();
					final String imagePath = entity.getImagePath();
					if (!TextUtils.isEmpty(imagePath)) {// 脧脗脭脴鹿茫赂忙脥录脝卢
						dbHelper = new LoginSQLAdapter(this);
						dbHelper.insertAdvertising(new AdvertisingEntity(entity
								.getGbUserName(), company, imagePath));
						dbHelper.closeDB();
					}
					Intent intent = new Intent();
					intent.putExtra("FROM", "GBApp");
					intent.setClass(BaseActivity.this, HomePageActivity.class);
					startActivity(intent);
				}
			}
		}
	}

	/**
	 * 脟毛脟贸路镁脦帽脪矛鲁拢路碌禄脴陆谩鹿没拢卢脳脫脌脿驴脡脪脭赂虏赂脟麓脣路陆路篓隆拢
	 * 
	 * @param error
	 *            路碌禄脴碌脛麓铆脦贸露脭脧贸
	 * @param content
	 *            路碌禄脴碌脛麓铆脦贸脙猫脢枚
	 * @param isBackGroundThread
	 *            脢脟路帽潞贸脤篓脧脽鲁脤路脙脦脢脥酶脗莽 true:脢脟拢卢false:路帽
	 * 
	 */
	public void onErrResponse(Throwable error, String content,
			boolean isBackGroundThread) {
		iErrRes = -1;
		strErr = null;
		if (isBackGroundThread)
			return;
		if (error instanceof JiBoException) {
			JiBoException je = (JiBoException) error;
			je.printStackTrace();
			iErrRes = je.getErrRes();
		} else if (error instanceof FileNotFoundException) {
		} else
			strErr = content;
		try {
			if (!isFinishing())
				showDialog(DialogRes.DIALOG_ID_ERR);
		} catch (Exception e) {
		}

	}

	public void showErrString(String ss) {
		strErr = ss;
		if (!isFinishing())
			showDialog(DialogRes.DIALOG_ID_ERR);

	}

	// --------------脪脭脧脗脢脟popup menu脧脿鹿脴碌脛路陆路篓----------------//
	/** PopMenu脢脟路帽脧脭脢戮 */
	protected boolean popflg;
	/** PopWindow */
	private PopupWindow pop;
	/** 赂脙脪鲁脙忙脢脟路帽麓忙脭脷 PopMenu */
	protected boolean hasPop;
	/** 脣霉脫脨menu 碌脛脥录脝卢脳脢脭麓 */
	private final int MENU_DRAW_RES[] = { R.drawable.menu_abtusbtn,
			R.drawable.menu_myfavbtn, R.drawable.menu_myinfset,
			R.drawable.menu_accsetting, R.drawable.menu_myhelp,
			R.drawable.menu_myhisbtn, R.drawable.menu_share_btn,
			R.drawable.menu_syn, R.drawable.menu_logout_btn };
	/** 脣霉脫脨menu 碌脛脦脛脳脰脳脢脭麓 */
	private final int MENU_STR_RES[] = { R.string.abtus, R.string.mygbi,
			R.string.myinfset, R.string.setting, R.string.myhelp,
			R.string.myhisfav, R.string.shrout, R.string.syncc, R.string.lgout };
	/** menu item碌脛脢媒脕驴 */
	public final int FLAG_SIZE = 9;

	/**
	 * 脡猫脰脙pop menu 脌脿脨脥 ,脭脻脢卤脩脴脫脙脌脧麓煤脗毛碌脛6赂枚脌脿脨脥
	 * 
	 * @param type
	 *            menu碌脛脌脿脨脥
	 * */
	public void setPopupWindowType(int type) {
		hasPop = true;
		if (pop == null)
			initPopupWindow(type);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 卤脴脨毛麓麓陆篓脪禄脧卯
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 碌卤碌茫禄梅脕脣menu潞贸麓楼路垄pop menu碌炉鲁枚
	 * */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (hasPop) {
			popflg = true;
			if (!pop.isShowing()) {
				pop.showAtLocation(findViewById(R.id.mai), Gravity.BOTTOM, 0, 0);
			} else {
				pop.dismiss();
			}
		}
		return false;
	}

	/**
	 * 脥脣鲁枚pop menu
	 * */
	public void dissMissPop() {

		popflg = false;
		if (pop != null && pop.isShowing())
			pop.dismiss();
	}

	/**
	 * pop驴脴录镁鲁玫脢录禄炉
	 * 
	 * @param type
	 *            menu碌脛脌脿脨脥
	 */
	private void initPopupWindow(int type) {
		View view = getLayoutInflater().inflate(R.layout.mymenu, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
		Button btns[] = new Button[4];
		btns[0] = (Button) view.findViewById(R.id.btn_menu1);
		btns[1] = (Button) view.findViewById(R.id.btn_menu2);
		btns[2] = (Button) view.findViewById(R.id.btn_menu3);
		btns[3] = (Button) view.findViewById(R.id.btn_menu4);
		TextView txs[] = new TextView[4];
		txs[0] = (TextView) view.findViewById(R.id.tx_menu1);
		txs[1] = (TextView) view.findViewById(R.id.tx_menu2);
		txs[2] = (TextView) view.findViewById(R.id.tx_menu3);
		txs[3] = (TextView) view.findViewById(R.id.tx_menu4);

		int index = 0;
		MenuClick click = new MenuClick();
		int flag = 0;
		for (int i = 0; i < FLAG_SIZE; i++) {
			flag = 1 << i;
			if ((flag & type) > 0) {
				btns[index].setBackgroundResource(MENU_DRAW_RES[i]);
				btns[index].setOnClickListener(click);
				btns[index].setTag(flag);
				txs[index].setText(MENU_STR_RES[i]);
				index++;
				if (index == 4)
					break;// 脳卯露脿脣脛赂枚
			}
		}
	}

	class MenuClick implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int type = Integer.parseInt(v.getTag().toString());
			treatMenuClick(type);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (popflg && isOutOfBounds(event)
				&& event.getAction() == MotionEvent.ACTION_DOWN) {
			dissMissPop();
			return true;
		}
		return false;

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		dissMissPop();
		return super.dispatchTouchEvent(ev);
	}

	private boolean isOutOfBounds(MotionEvent event) {
		final int y = (int) event.getY();
		int p[] = new int[2];
		pop.getContentView().getLocationOnScreen(p);
		return y < p[1];
	}

	/**
	 * 麓娄脌铆虏脣碌楼碌脛碌茫禄梅脢脗录镁
	 * 
	 * @param typeId
	 *            碌茫禄梅碌脛menu id
	 * */
	protected void treatMenuClick(int typeId) {
		Intent intent = null;
		switch (typeId) {
		case Constant.FLAG_ABOUT:
			intent = new Intent(this, AboutGbiActivity.class);
			break;
		case Constant.FLAG_MY_JIBO:
			// intent = new Intent(BaseActivity.this,
			// MyFavoriteListActivity.class);
			break;
		case Constant.FLAG_SETUP:
			intent = new Intent(this, AccountManagerActivity.class);
			break;
		case Constant.FLAG_SUGGESTION:
			intent = new Intent(this, FeedBackActivity.class);
			break;
		case Constant.FLAG_HELP:
			intent = new Intent(this, HelpActivity.class);
			break;
		case Constant.FLAG_HISTROY:
			intent = new Intent(this, HistoryFavoriteActivity.class);
			break;
		case Constant.FLAG_SHARE:// 路脰脧铆
			if (context instanceof RelatedEventsActivity) {
				sharing(R.array.items, 2);
			} else if (context instanceof RelatedNewsActivity) {
				sharing(R.array.items, 1);
			} else {
				sharing(R.array.items2, 0);
				Log.e("0", "0");
			}
			break;
		case Constant.FLAG_SYNCH:
			synchronizeData();
			break;
		case Constant.FLAG_DIALOG_QIUT:
			showDialog(DialogRes.DIALOG_QUIT_PROMPT);
			break;
		}
		if (intent != null) {
			if (intent.getComponent().getClassName()
					.endsWith(FeedBackActivity.class.getSimpleName())) {
				UMFeedbackService.openUmengFeedbackSDK(context);
				return;
			}
			startActivity(intent);
		}
		dissMissPop();
	}

	private String shareContent = "";// 路脰脧铆碌脛脛脷脠脻
	public boolean sharing(final int itemId, final int type) {
		if (type == 1)// 脨脗脦脜碌脴脰路
		{
			shareContent = RelatedNewsActivity.sharing_inf;
		} else if (type == 2) {
			shareContent = RelatedEventsActivity.sharing_inf;
		} else if (type == 0) {
			String userId = SharedPreferencesMgr.getUserID();
			userId = "".equals(userId) ? "1" : userId;
			shareContent = SharedPreferencesMgr.getMsgContent() + DOWNLOADURL
					+ "101." + "0." + userId + ".";

			shareContent = SharedPreferencesMgr.getMsgContent() + DOWNLOADURL
					+ "101." + "0." + userId + ".";
			Log.e("shareContent", shareContent);
		} else if (type == 11) {
			shareContent = this.getString(R.string.survey_share_1)
					+ app.getSurveyTitle() + ","
					+ this.getString(R.string.survey_share_2)
					+ ":http://www.jibo.cn/ot/d.asp";
		} else if (type == 3) {
			shareContent = RelatedNewsActivity.sharing_inf;
		}
		
		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.chooseMenu))
				.setItems(itemId, new DialogInterface.OnClickListener() { // content
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Weibo weibo = Weibo.getInstance();
								String content = null;int idx1 = 0;
								int idx2 = 0;
								switch (which) {
								case 0:// 露脤脨脜路脰脧铆
									content = (type == 11 || type == 3) ? Html
											.fromHtml(shareContent).toString()
											: Html.fromHtml(shareContent)
													+ "1." + "msg";
//												idx1 = content.indexOf(":")+1;
//												idx2 = content.indexOf("...\n");
//												idx2 = idx2==-1
//												content = content.replaceAll(content.substring(idx1,idx2),"");
//											content = content.replaceAll(":", "");
//											content = content.replaceAll("...\n", "");
									MobclickAgent.onEvent(context,
											"SharingWay", "MessageSharing", 1);
									uploadLoginLogNew("Sharing",
											"MessageSharing", "SharingWay",
											null);
									Intent it = new Intent(Intent.ACTION_VIEW);
									it.putExtra("sms_body", content);
									it.setType("vnd.android-dir/mms-sms");
									startActivity(it);
									break;
								case 1:// 脫脢录镁路脰脧铆 禄貌 脝盲脣没路脰脧铆
									switch (itemId) {
									case R.array.items2:// 脫脢录镁路脰脧铆(脫脙脫脷脰梅陆莽脙忙HomePage)
										Intent inte = new Intent(context,
												EmailActivity.class);
										startActivity(inte);
										MobclickAgent
												.onEvent(context, "SharingWay",
														"EmailSharing", 1);

										uploadLoginLogNew("Sharing",
												"EmailSharing", "SharingWay",
												null);
										break;
									case R.array.items:// 脝盲脣没路脰脧铆
										content = (type == 11 || type == 3) ? shareContent
												: shareContent + "4." + "email";

										Intent eit = new Intent(
												Intent.ACTION_SEND);// 碌梅脫脙路垄脣脥Email碌脛鲁脤脨貌
										eit.putExtra(Intent.EXTRA_EMAIL, "");
										eit.putExtra(Intent.EXTRA_TEXT, content);
										eit.setType("text/plain");
										startActivity(Intent.createChooser(eit,
												"Choose Email Client"));
										MobclickAgent.onEvent(context,
												"SharingWay",
												"EmailOrOtherSharing", 1);
										uploadLoginLogNew("Sharing",
												"EmailOrOtherSharing",
												"SharingWay", null);
										break;
									}
									break;
								case 2:// 脨脗脌脣路脰脧铆

									content = (type == 11 || type == 3) ? Html
											.fromHtml(shareContent).toString()
											: Html.fromHtml(shareContent)
													+ "2.tsina";if(type==1||type==2){
												if(content.length()>140){
													idx1 = content.indexOf(":")+1;
													idx2 = content.indexOf("...\n");
													content = content.replaceAll(content.substring(idx1,idx2),"");
												}
												content = content.replaceAll(":", "");
												content = content.replaceAll("...\n", "");
											}
									MobclickAgent.onEvent(context,
											"SharingWay", "SinaSharing", 1);
									// new Thread() {
									//
									// @Override
									// public void run() {
									// uploadDataNew
									// .uploadLoginLogNew(
									// context,
									// new SimpleDateFormat(
									// "yyyy-MM-dd HH:mm:ss")
									// .format(new Date()),
									// "Sharing",
									// "SharingWay",
									// "SinaSharing", null);
									// }
									// }.start();

									weibo.setupConsumerConfig(
											Constant.CONSUMER_KEY_SINA,
											Constant.CONSUMER_SECRET_SINA);
									weibo.setRedirectUrl(Constant.CALLBACK_URL_SINA);

									if (!SharedPreferencesMgr
											.getAccessToken_SINA().equals("")
											&& !SharedPreferencesMgr
													.getExpiresIn_SINA()
													.equals("")) {
										initWeibo(weibo, SharedPreferencesMgr
												.getAccessToken_SINA(),
												SharedPreferencesMgr
														.getExpiresIn_SINA(),
												Constant.CONSUMER_SECRET_SINA,
												Weibo.WEIBO_SINAL);
										try {
											share2weibo(weibo, content, null,
													Weibo.WEIBO_SINAL);
										} catch (WeiboException e) {
											e.printStackTrace();
										}
									} else {
										weibo.authorize(BaseActivity.this,
												new AuthDialogListener(content,
														Weibo.WEIBO_SINAL),
												Weibo.WEIBO_SINAL);
									}

									break;

								// case 3:// 脠脣脠脣路脰脧铆
								//
								// UMSnsService
								// .shareToRenr(
								// BaseActivity.this,
								// (type == 11 || type == 3) ? Html.fromHtml(
								// shareContent)
								// .toString()
								// : Html.fromHtml(shareContent)
								// + "2.renren",
								// null);
								// uploadLoginLogNew("Sharing",
								// "RenrenSharing", "SharingWay", null);
								// break;
								case 3:// qq路脰脧铆
									content = (type == 11 || type == 3) ? Html
											.fromHtml(shareContent).toString()
											: Html.fromHtml(shareContent)
													+ "2.tqq";

									weibo.setupConsumerConfig(
											Constant.CONSUMER_KEY_QQ,
											Constant.CONSUMER_SECRET_QQ);
									weibo.setRedirectUrl(Constant.CALLBACK_URL_QQ);

									if (!SharedPreferencesMgr
											.getAccessToken_QQ().equals("")
											&& !SharedPreferencesMgr
													.getExpiresIn_QQ().equals(
															"")
											&& !SharedPreferencesMgr
													.getOpenId_QQ().equals("")) {
										initWeibo(weibo, SharedPreferencesMgr
												.getAccessToken_QQ(),
												SharedPreferencesMgr
														.getExpiresIn_QQ(),
												Constant.CONSUMER_SECRET_QQ,
												Weibo.WEIBO_QQ);
										try {
											share2weibo(weibo, content, null,
													Weibo.WEIBO_QQ);
										} catch (WeiboException e) {
											e.printStackTrace();
										}
									} else {
										weibo.authorize(BaseActivity.this,
												new AuthDialogListener(content,
														Weibo.WEIBO_QQ),
												Weibo.WEIBO_QQ);
									}
									break;
								case 4:// weixin路脰脧铆

									content = getString(R.string.weixin_share)+
									Html.fromHtml(DOWNLOADURL
											+ "101." + "0." + SharedPreferencesMgr.getUserID() + ".")
													+ "2.tqq";
									Logs.i("share 锛�"+content);
									weixinTask.regHandle(BaseActivity.this);
									weixinTask.sendReq(content);
									break;

								}
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
		return true;
	}


	public boolean newSharing(final int itemId, final int type,final String strFrom) {
		if (type == 1)// 脨脗脦脜碌脴脰路
		{
			shareContent = RelatedNewsActivity.sharing_inf;
		} else if (type == 2) {
			shareContent = RelatedEventsActivity.sharing_inf;
		} else if (type == 0) {
			String userId = SharedPreferencesMgr.getUserID();
			userId = "".equals(userId) ? "1" : userId;
			shareContent = SharedPreferencesMgr.getMsgContent() + DOWNLOADURL
					+ "101." + "0." + userId + ".";

			shareContent = SharedPreferencesMgr.getMsgContent() + DOWNLOADURL
					+ "101." + "0." + userId + ".";
			Log.e("shareContent", shareContent);
		} else if (type == 11) {
			shareContent = this.getString(R.string.survey_share_1)
					+ app.getSurveyTitle() + ","
					+ this.getString(R.string.survey_share_2)
					+ ":http://www.jibo.cn/ot/d.asp";
		} else if (type == 3) {
			shareContent = RelatedNewsActivity.sharing_inf;
		}
		
		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.chooseMenu))
				.setItems(itemId, new DialogInterface.OnClickListener() { // content
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Weibo weibo = Weibo.getInstance();
								String content = null;int idx1 = 0;
								int idx2 = 0;
								switch (which) {
								case 0:// 露脤脨脜路脰脧铆
									content = (type == 11 || type == 3) ? Html
											.fromHtml(shareContent).toString()
											: Html.fromHtml(shareContent)
													+ "1." + "msg";
//												idx1 = content.indexOf(":")+1;
//												idx2 = content.indexOf("...\n");
//												idx2 = idx2==-1
//												content = content.replaceAll(content.substring(idx1,idx2),"");
//											content = content.replaceAll(":", "");
//											content = content.replaceAll("...\n", "");
									MobclickAgent.onEvent(context,
											strFrom+"_drug_share_msg");
									uploadLoginLogNew("Sharing",
											strFrom+"_drug_share_msg", strFrom+"_drug_share_msg_click",
											null);
									Intent it = new Intent(Intent.ACTION_VIEW);
									it.putExtra("sms_body", content);
									it.setType("vnd.android-dir/mms-sms");
									startActivity(it);
									break;
								case 1:// 脫脢录镁路脰脧铆 禄貌 脝盲脣没路脰脧铆
									switch (itemId) {
									case R.array.items2:// 脫脢录镁路脰脧铆(脫脙脫脷脰梅陆莽脙忙HomePage)
										Intent inte = new Intent(context,
												EmailActivity.class);
										startActivity(inte);
										MobclickAgent.onEvent(context,
												strFrom+"_drug_share_mail");
										uploadLoginLogNew("Sharing",
												strFrom+"_drug_share_mail", strFrom+"_drug_share_msg_click", null);
										
										break;
									case R.array.items:// 脝盲脣没路脰脧铆
										content = (type == 11 || type == 3) ? shareContent
												: shareContent + "4." + "email";

										Intent eit = new Intent(
												Intent.ACTION_SEND);// 碌梅脫脙路垄脣脥Email碌脛鲁脤脨貌
										eit.putExtra(Intent.EXTRA_EMAIL, "");
										eit.putExtra(Intent.EXTRA_TEXT, content);
										eit.setType("text/plain");
										startActivity(Intent.createChooser(eit,
												"Choose Email Client"));
										MobclickAgent.onEvent(context,
												strFrom+"_drug_share_mailOrOtherSharing");
										uploadLoginLogNew("Sharing",
												strFrom+"_drug_share_mailOrOtherSharing", strFrom+"_drug_share_msgOrOtherSharing_click", null);
										break;
									}
									break;
								case 2:// 脨脗脌脣路脰脧铆

									content = (type == 11 || type == 3) ? Html
											.fromHtml(shareContent).toString()
											: Html.fromHtml(shareContent)
													+ "2.tsina";if(type==1||type==2){
												if(content.length()>140){
													idx1 = content.indexOf(":")+1;
													idx2 = content.indexOf("...\n");
													content = content.replaceAll(content.substring(idx1,idx2),"");
												}
												content = content.replaceAll(":", "");
												content = content.replaceAll("...\n", "");
											}
													MobclickAgent.onEvent(context,
															strFrom+"_drug_share_sina_weibo");
													uploadLoginLogNew("Sharing",
															strFrom+"_drug_share_sina_weibo", strFrom+"_drug_share_sina_weibo_click", null);
													
									// new Thread() {
									//
									// @Override
									// public void run() {
									// uploadDataNew
									// .uploadLoginLogNew(
									// context,
									// new SimpleDateFormat(
									// "yyyy-MM-dd HH:mm:ss")
									// .format(new Date()),
									// "Sharing",
									// "SharingWay",
									// "SinaSharing", null);
									// }
									// }.start();

									weibo.setupConsumerConfig(
											Constant.CONSUMER_KEY_SINA,
											Constant.CONSUMER_SECRET_SINA);
									weibo.setRedirectUrl(Constant.CALLBACK_URL_SINA);

									if (!SharedPreferencesMgr
											.getAccessToken_SINA().equals("")
											&& !SharedPreferencesMgr
													.getExpiresIn_SINA()
													.equals("")) {
										initWeibo(weibo, SharedPreferencesMgr
												.getAccessToken_SINA(),
												SharedPreferencesMgr
														.getExpiresIn_SINA(),
												Constant.CONSUMER_SECRET_SINA,
												Weibo.WEIBO_SINAL);
										try {
											share2weibo(weibo, content, null,
													Weibo.WEIBO_SINAL);
										} catch (WeiboException e) {
											e.printStackTrace();
										}
									} else {
										weibo.authorize(BaseActivity.this,
												new AuthDialogListener(content,
														Weibo.WEIBO_SINAL),
												Weibo.WEIBO_SINAL);
									}

									break;

								// case 3:// 脠脣脠脣路脰脧铆
								//
								// UMSnsService
								// .shareToRenr(
								// BaseActivity.this,
								// (type == 11 || type == 3) ? Html.fromHtml(
								// shareContent)
								// .toString()
								// : Html.fromHtml(shareContent)
								// + "2.renren",
								// null);
								// uploadLoginLogNew("Sharing",
								// "RenrenSharing", "SharingWay", null);
								// break;
								case 3:// qq路脰脧铆
									content = (type == 11 || type == 3) ? Html
											.fromHtml(shareContent).toString()
											: Html.fromHtml(shareContent)
													+ "2.tqq";
											MobclickAgent.onEvent(context,
													strFrom+"_share_tencent_weibo");
											uploadLoginLogNew("Sharing",
													strFrom+"_share_tencent_weibo", strFrom+"_share_tencent_weibo_click", null);
											
									weibo.setupConsumerConfig(
											Constant.CONSUMER_KEY_QQ,
											Constant.CONSUMER_SECRET_QQ);
									weibo.setRedirectUrl(Constant.CALLBACK_URL_QQ);

									if (!SharedPreferencesMgr
											.getAccessToken_QQ().equals("")
											&& !SharedPreferencesMgr
													.getExpiresIn_QQ().equals(
															"")
											&& !SharedPreferencesMgr
													.getOpenId_QQ().equals("")) {
										initWeibo(weibo, SharedPreferencesMgr
												.getAccessToken_QQ(),
												SharedPreferencesMgr
														.getExpiresIn_QQ(),
												Constant.CONSUMER_SECRET_QQ,
												Weibo.WEIBO_QQ);
										try {
											share2weibo(weibo, content, null,
													Weibo.WEIBO_QQ);
										} catch (WeiboException e) {
											e.printStackTrace();
										}
									} else {
										weibo.authorize(BaseActivity.this,
												new AuthDialogListener(content,
														Weibo.WEIBO_QQ),
												Weibo.WEIBO_QQ);
									}
									break;
								case 4:// weixin路脰脧铆

									content = getString(R.string.weixin_share)+
									Html.fromHtml(DOWNLOADURL
											+ "101." + "0." + SharedPreferencesMgr.getUserID() + ".")
													+ "2.tqq";
									MobclickAgent.onEvent(context,
											strFrom+"share_weixin");
									uploadLoginLogNew("Sharing",
											strFrom+"share_weixin", strFrom+"share_weixin_click", null);
									
									Logs.i("share 锛�"+content);
									weixinTask.regHandle(BaseActivity.this);
									weixinTask.sendReq(content);
									break;

								}
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
		return true;
	}


	/**
	 * menu脢脟路帽脧脭脢戮
	 * 
	 * @return menu脢脟路帽脧脭脢戮
	 */
	public boolean isPopflg() {
		return popflg;
	}

	public void shareDialog1(final String content) {
		final String[] strArr = getResources().getStringArray(R.array.share_1);
		new AlertDialog.Builder(this).setTitle(getString(R.string.chooseMenu))
				.setItems(strArr, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (getString(R.string.share_sina)
								.equals(strArr[which])) {
						} else if (getString(R.string.share_email).equals(
								strArr[which])) {
							Intent inte = new Intent(BaseActivity.this,
									EmailActivity.class);
							startActivity(inte);
						} else if (getString(R.string.share_message).equals(
								strArr[which])) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.putExtra("sms_body", content + "1.msg");
							intent.setType("vnd.android-dir/mms-sms");
							startActivity(intent);
						} else if (getString(R.string.follow_jibo).equals(
								strArr[which])) {

						} else if (getString(R.string.share_tx).equals(
								strArr[which])) {

						}
					}
				});
	}

	Handler lillyHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				if (lillyDialog.isShowing()) {
					llt.removeAllViews();
					lillyDialog.dismiss();
					lillyDialog = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		};
	};

	private class showLillyPic extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			lillyHandler.sendMessage(message);

		}
	}

	@Override
	public void startActivity(Intent intent) {
		app.setHomeLaunched(false);
		app.setStartActivity(true);
		super.startActivity(intent);
	}

	public void startActivityForResult(Intent intent, int requestCode) {
		app.setHomeLaunched(false);
		app.setStartActivity(true);
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onUserLeaveHint() {

		super.onUserLeaveHint();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			app.setHomeLaunched(false);
		}

		return super.dispatchKeyEvent(event);
	}

	public void synchronizeData() {

	}

	/**
	 * OAuth脟毛脟贸脩茅脰陇潞贸禄脴碌梅
	 * 
	 * @author simon
	 * 
	 */
	class AuthDialogListener implements WeiboDialogListener {

		private String content;// 路脰脧铆脛脷脠脻

		// private int type;//脌脿卤冒拢潞0麓煤卤铆路脰脧铆拢卢1麓煤卤铆鹿脴脳垄
		//
		// private String friendId;//卤禄鹿脴脳垄露脭脧贸ID

		private int platForm;// 路脰脧铆脝陆脤篓

		public AuthDialogListener(String content, int platForm) {
			this.content = content;
			this.platForm = platForm;
		}

		// public AuthDialogListener(String friendId, int type,int platForm) {
		// this.friendId = friendId;
		// this.type = type;
		// this.platForm = platForm;
		// }

		@Override
		public void onComplete(Bundle values) {// 脩茅脰陇脥篓鹿媒禄脴碌梅
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			String openId = values.getString("openid");
			String uid = values.getString("uid");

			Weibo weibo = Weibo.getInstance();
			try {
				switch (platForm) {
				case Weibo.WEIBO_SINAL:
					initWeibo(weibo, token, expires_in,
							Constant.CONSUMER_SECRET_SINA, Weibo.WEIBO_SINAL);

					SharedPreferencesMgr.setAccessToken_SINA(token);
					SharedPreferencesMgr.setExpiresIn_SINA(expires_in);
					SharedPreferencesMgr.setUid_SINA(uid);
					share2weibo(weibo, content, null, Weibo.WEIBO_SINAL);
					break;
				case Weibo.WEIBO_QQ:
					initWeibo(weibo, token, expires_in,
							Constant.CONSUMER_SECRET_QQ, Weibo.WEIBO_QQ);
					SharedPreferencesMgr.setAccessToken_QQ(token);
					SharedPreferencesMgr.setExpiresIn_QQ(expires_in);
					SharedPreferencesMgr.setOpenId_QQ(openId);
					share2weibo(weibo, content, null, Weibo.WEIBO_QQ);
					break;
				}
			} catch (WeiboException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onError(DialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(
					getApplicationContext(),
					"Auth exception : " + e.getMessage()
							+ ">>>>>>>>>errorStatusCode:" + e.getStatusCode(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	// /**
	// * 鲁玫脢录禄炉OAuth脨脜脧垄
	// * @param weibo
	// * @param token
	// * @param expires_in
	// */
	private void initWeibo(Weibo weibo, String token, String expires_in,
			String secret, int platForm) {
		AccessToken accessToken = new AccessToken(token, secret, platForm);
		accessToken.setExpiresIn(expires_in);
		weibo.setAccessToken(accessToken);
	}

	// /**
	// * 鹿脴脳垄脛鲁脠脣
	// * @param weibo
	// * @param friendId 卤禄鹿脴脳垄露脭脧贸id
	// * @throws WeiboException
	// */
	// private void createFriend(Weibo weibo, String friendId)
	// throws WeiboException {
	// WeiboParameters bundle = new WeiboParameters();
	// bundle.add("access_token", Weibo.getInstance().getAccessToken()
	// .getToken());
	// bundle.add("uid", friendId);
	// String url = Weibo.SERVER + "friendships/create.json";
	// AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
	// weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);
	// }

	/**
	 * 路脰脧铆脛脷脠脻
	 * 
	 * 
	 * @param weibo
	 * @param content
	 *            脦脛脳脰脛脷脠脻
	 * @param picPath
	 *            脥录脝卢sd驴篓脗路戮露
	 * @throws WeiboException
	 */
	private void share2weibo(Weibo weibo, String content, String picPath,
			int platForm) throws WeiboException {
		share2weibo(this, weibo.getAccessToken().getToken(), weibo
				.getAccessToken().getSecret(), content, picPath, platForm);
	}

	/**
	 * 路脰脧铆脛脷脠脻
	 * 
	 * @param activity
	 * @param accessToken
	 * @param tokenSecret
	 * @param content
	 * @param picPath
	 * @return
	 * @throws WeiboException
	 */
	public boolean share2weibo(Activity activity, String accessToken,
			String tokenSecret, String content, String picPath, int platForm)
			throws WeiboException {
		if (TextUtils.isEmpty(accessToken)) {
			throw new WeiboException("token can not be null!");
		}
		if (TextUtils.isEmpty(content) && TextUtils.isEmpty(picPath)) {
			throw new WeiboException("weibo content can not be null!");
		}
		Intent i = new Intent(activity, ShareActivity.class);
		i.putExtra(ShareActivity.EXTRA_WEIBO_CONTENT, content);
		i.putExtra(ShareActivity.EXTRA_PIC_URI, picPath);
		i.putExtra(ShareActivity.PLATFORM, platForm);
		activity.startActivity(i);
		return true;
	}

	/**
	 * 碌脷脠媒路陆碌脟脗录
	 * 
	 * @param platForm
	 *            脝陆脤篓
	 */
	protected void weiboLogin(int platForm) {
		String key = "";
		String secret = "";
		String callback = "";

		switch (platForm) {
		case Weibo.WEIBO_SINAL:
			key = Constant.CONSUMER_KEY_SINA;
			secret = Constant.CONSUMER_SECRET_SINA;
			callback = Constant.CALLBACK_URL_SINA;
			break;
		case Weibo.WEIBO_QQ:
			key = Constant.CONSUMER_KEY_QQ;
			secret = Constant.CONSUMER_SECRET_QQ;
			callback = Constant.CALLBACK_URL_QQ;
			break;
		}

		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(key, secret);
		weibo.setRedirectUrl(callback);
		weibo.authorize(BaseActivity.this, new WeiboLoginListener(platForm,
				context), platForm);
	}

	/**
	 * 卤拢麓忙脫脙禄搂脨脜脧垄脰脕卤戮碌脴拢篓脙脺脗毛鲁媒脥芒拢漏
	 * 
	 * @param entity
	 */
	protected void saveLoginInfoToLocal(LoginEntity entity) {
		SharedPreferencesMgr.saveUserName(entity.getGbUserName());
		SharedPreferencesMgr.saveUserID(entity.getCustomerId());
		SharedPreferencesMgr.setName(entity.getUserName());
		SharedPreferencesMgr.setEmail(entity.getEmail());
		SharedPreferencesMgr.saveLicenseNumber(entity.getLicenseNumber());
		SharedPreferencesMgr.setContactNb(entity.getContactNumber());
		SharedPreferencesMgr.setRegion(entity.getHospitalRegion());
		SharedPreferencesMgr.setCity(entity.getHospitalCity());
		SharedPreferencesMgr.setHospitalName(entity.getHospitalName());
		SharedPreferencesMgr.setBigDepartments(entity.getBigDepartments());
		SharedPreferencesMgr.saveDept(entity.getDepartment());
		SharedPreferencesMgr.saveDrugId(entity.getDoctorId());
		SharedPreferencesMgr.setJob(entity.getJob());
		SharedPreferencesMgr.setProfile(entity.getProfessional_title());
		SharedPreferencesMgr.setInviteCode(entity.getInviteCode());
		SharedPreferencesMgr.setInviteCodeExpiredDate(entity
				.getInviteCodeExpiredDate());
	}

	@Override
	public void onComplete(final String response, final int platForm) {// 碌脟脗录鲁脡鹿娄潞贸禄脴碌梅
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				SharedPreferencesMgr.setEmail(parseRes(response, platForm));
				bindUser();
			}
		});

	}

	/**
	 * 脦垄虏漏掳贸露篓脮脣潞脜(掳碌脧茫)
	 */
	private void bindUser() {
		String uid = "";
		String platFormString = "";
		String email = SharedPreferencesMgr.getEmail();

		switch (SharedPreferencesMgr.getCurrentWeiboPlatform()) {
		case Weibo.WEIBO_SINAL:// 脨脗脌脣
			uid = SharedPreferencesMgr.getUid_SINA();
			platFormString = "sina";
			break;
		case Weibo.WEIBO_QQ:// 脤脷脩露
			uid = SharedPreferencesMgr.getOpenId_QQ();
			platFormString = "qq";
			break;
		}

		// 路垄脣脥脟毛脟贸拢卢路碌禄脴脫脙禄搂脨脜脧垄禄貌掳贸露篓脫脙禄搂
		Properties info = new Properties();
		info.put("weiboid", uid);
		info.put("platform", platFormString);
		info.put("devicetype", "android");
		info.put("email", email);
		info.put("devicetoken", Util.getDeviceId(context));
		sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_WEIBO_LOGIN, info,
				new BaseResponseHandler(this, DialogRes.DIALOG_ID_JUMP_TO));
	}

	/**
	 * 脤脷脩露脦垄虏漏 脫脙禄搂脨脜脧垄陆芒脦枚
	 * 
	 * @param response
	 *            脟毛脟贸碌陆碌脛json脢媒戮脻
	 * @return
	 */
	private String parseRes(String response, int platForm) {

		String email = "";
		if (response != null) {
			if (platForm == Weibo.WEIBO_QQ) {
				try {
					JSONObject obj = new JSONObject(response)
							.getJSONObject("data");
					email = obj.optString("email", Constant.SPACE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return email;
	}

	public void onIOException(IOException e) {

	}

	public void onError(final WeiboException e, final int platForm) {// 麓铆脦贸脢卤禄脴碌梅

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				// 麓铆脦贸脗毛拢卢脧锚脟茅脟毛录没脨脗脌脣脦垄虏漏Android_SDK API
				switch (e.getStatusCode()) {
				case 21315:// token脪脩鹿媒脝脷
					Weibo weibo = Weibo.getInstance();
					weibo.setupConsumerConfig(Constant.CONSUMER_KEY_SINA,
							Constant.CONSUMER_SECRET_SINA);
					weibo.setRedirectUrl(Constant.CALLBACK_URL_SINA);
					weibo.authorize(BaseActivity.this, new AuthDialogListener(
							Constant.JIBO_SINA_ID, 1), Weibo.WEIBO_SINAL);
					break;
				// case 20506:// 脪脩戮颅鹿脴脳垄麓脣脫脙禄搂脕脣
				// Toast.makeText(
				// BaseActivity.this,
				// BaseActivity.this
				// .getString(R.string.alreadyfollowed),
				// Toast.LENGTH_LONG).show();
				// break;
				default:// 脝盲脣没麓铆脦贸
					// Toast.makeText(
					// BaseActivity.this,
					// BaseActivity.this
					// .getString(R.string.createfriend_failed),
					// Toast.LENGTH_LONG).show();
					e.printStackTrace();
					break;

				}

			}
		});
	}

	public void checkZipFile(ArrayList<String> str) {

		Properties pro = new Properties();
		String sendStr = "";

		if (str == null) {
			sendStr = "";
		} else if (str.size() == 1) {
			sendStr = str.get(0);
		} else {
			for (String s : str) {
				sendStr = sendStr + s + "|";
			}
			sendStr = sendStr.substring(0, sendStr.length() - 1);
		}

		GetZipFileHandler handler = new GetZipFileHandler(sendStr);
		pro.put(SoapRes.KEY_GET_ZIP_FILE_STR, sendStr);
		pro.put(SoapRes.KEY_GET_ZIP_FILE_TYPE, 0);
		sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_GET_ZIP_FILE, pro,
				handler);
	}

	private class GetZipFileHandler extends AsyncSoapResponseHandler {
		boolean bAll;

		public GetZipFileHandler(String str) {
			bAll = str.equals("");
		}

		@Override
		public void onSuccess(Object content) {
			if (content != null && content instanceof GetZipFilePaser) {
				GetZipFilePaser paser = (GetZipFilePaser) content;
				String url = paser.getUrl();
				if ("".equals(url)) {// 脧脗脭脴碌脴脰路脦陋驴脮
					if (bAll) {
						// failDownloadZipfile();
						ConfigAdapter ca = new ConfigAdapter(BaseActivity.this,
								"ahfs");
						ca.updateConfigInfo("0");
						ca.closeDB();
					}
					failDownloadZipfile();
				} else {
					DownloadAsyncTask1 task = new DownloadAsyncTask1(
							BaseActivity.this, Constant.DOWNLOAD_ID_AHFS, url,
							this, new CallBack() {
								@Override
								public void onFinish(boolean b) {
									// TODO
									if (b) {
										if (bAll) {
											ConfigAdapter ca = new ConfigAdapter(
													BaseActivity.this, "ahfs");
											ca.updateConfigInfo("2");
											ca.closeDB();
										}
										finishDownloadZipFile();
									} else {
										if (bAll) {
											// failDownloadZipfile();
											ConfigAdapter ca = new ConfigAdapter(
													BaseActivity.this, "ahfs");
											ca.updateConfigInfo("0");
											ca.closeDB();
										}
										failDownloadZipfile();
									}
								}
							});
					if (bAll) {
						ConfigAdapter ca = new ConfigAdapter(BaseActivity.this,
								"ahfs");
						String info = ca.getConfigInfo();
						if ("0".equals(info)) {
							ca.updateConfigInfo("1");
						}
						ca.closeDB();
					}
					task.execute();
				}

			}
			super.onSuccess(content);
		}
	}

	public void finishDownloadZipFile() {
	}

	public void failDownloadZipfile() {
	}

	public synchronized Object decryptZipFile(String name) throws Exception {
		String content = "";
		File file = new File(Constant.DRUG_AHFS + "/" + name + ".zip");
		if (!file.exists())
			return null;
		try {
			ZipFile zf = new ZipFile(file);
			zf.setPassword(Constant.DRUG_AHFS_PW);
			zf.extractAll(Constant.DRUG_AHFS);
			File fileArr[] = new File(Constant.DRUG_AHFS).listFiles();
			BufferedReader buffer = null;
			FileReader reader = null;
			if (fileArr != null) {
				for (File f : fileArr) {
					if (!f.getAbsolutePath().endsWith("zip")) {
						try {
							reader = new FileReader(f);
							buffer = new BufferedReader(reader);
							while (buffer.ready()) {
								content = buffer.readLine();
							}
						} finally {
							if (null != reader)
								reader.close();
							if (null != buffer) {
								buffer.close();
							}
						}
						if (f.exists())
							f.delete();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return content;
	}

	public void uploadLoginLogNew(String page_ID, String DetailID,
			String DetailIdName, String Demo) {
		app.uploadData(page_ID, DetailID, DetailIdName, Demo);
	}

	public void uploadLoginLog(String page_ID, String DetailID,
			String DetailIdName, String Demo, boolean isFromSQL) {
		if (isFromSQL) {
			uploadLoginLogNew(page_ID, DetailID, DetailIdName, Demo);
		}
	}

	/**
	 * 禄帽脠隆碌卤脟掳脢脰禄煤脣霉脭脷鹿芦脥酶ip
	 * 
	 * @param ipaddr
	 * @return
	 */
	public String getNetIp(String ipaddr) {
		URL infoUrl = null;
		InputStream inStream = null;
		try {
			infoUrl = new URL(ipaddr);
			URLConnection connection = infoUrl.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inStream, "utf-8"));
				StringBuilder strber = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null)
					strber.append(line + "\n");
				inStream.close();
				return strber.toString();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
