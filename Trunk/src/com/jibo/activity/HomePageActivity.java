package com.jibo.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.adapter.HomePageAdapter;
import com.jibo.app.interact.InteractIndex;
import com.jibo.app.push.PushConst;
import com.jibo.app.research.ResearchPageActivity;
import com.jibo.app.updatespot.SpotUtil;
import com.jibo.asynctask.DownloadDBScriptAsyncTask;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.request.impl.db.AutoCache;
import com.jibo.base.src.request.impl.db.SqliteAdapterCentre;
import com.jibo.common.BitmapManager;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.dao.DBFactory;
import com.jibo.data.DrugAlertDataPaser;
import com.jibo.data.GetCategoryUpdatePaser;
import com.jibo.data.InteractionPaser;
import com.jibo.data.NewDATAVersionDataPaser;
import com.jibo.data.NewsListPaser;
import com.jibo.data.ProfilePaser;
import com.jibo.data.entity.AdvertisingEntity;
import com.jibo.data.entity.DrugAlertEntity;
import com.jibo.data.entity.HomePageItemEntity;
import com.jibo.data.entity.ProfileEntity;
import com.jibo.dbhelper.DrugAlertSQLAdapter;
import com.jibo.dbhelper.LoginSQLAdapter;
import com.jibo.dbhelper.NewsSQLAdapter;
import com.jibo.dbhelper.ProfileAdapter;
import com.jibo.entity.Version;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.net.BaseResponseHandler;
import com.jibo.news.NewsPageActivity;
import com.jibo.service.CheckAPPUpdateService;
import com.jibo.service.ICallback;
import com.jibo.service.IService;
import com.jibo.service.UpdateAppService;
import com.jibo.ui.BitmapText;
import com.jibo.ui.CircleIndicator;
import com.jibo.ui.HomePageLayout;
import com.jibo.util.ActivityPool;
import com.jibo.util.Logs;
import com.jibo.util.SharedPreferenceUtil;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.ReportPolicy;

//import com.jibo.app.news.NewsActivity;

public class HomePageActivity extends BaseSearchActivity implements
		OnClickListener, OnFocusChangeListener, OnTouchListener {
	private GBApplication application;
	private LinearLayout lltBottom;
	private HomePageLayout homePage;
	private float scale;
	private ArrayList<HomePageItemEntity> itemList1;
	private ArrayList<HomePageItemEntity> itemList2;
	private NewsSQLAdapter newsAdapter;
	private DrugAlertSQLAdapter drugAlertAdapter;
	private LoginSQLAdapter dbHelper;
	private IService mService;
	private IService mNewsService;
	private IService mDrugAlertService;
	private static HashMap<Integer, ImageView> imgMap;
	AsyncSoapResponseHandler baseHandler = null;
	private ProfileAdapter profileAdapter;
	private AdvertisingEntity advert;
	private HashMap<String, Integer> pckgMap;
	private static final int PCKG_DRUG = 0x231;
	private static final int PCKG_TOOL = 0x231 + 1;
	private static final int PCKG_CALCULATOR = 0x231 + 2;
	private Notification ahfs_notification;
	private int downloadNo = 0;
	private LinearLayout lltHeader;
	private ImageButton imgbtnSearch;
	private AutoCompleteTextView actvInput;
	private Timer timer;
	private TimerTask timerTask;
	private final int version_img = 0x123;
	private final int alarm = 0x124;
	private int version_alpha = 255;
	private int version_flag = 1;
	private boolean isShowLogo;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case version_img:
				int alpha = (Integer) msg.obj;
				imgBG.setAlpha(alpha);
				imgBG.invalidate();
				break;
			case alarm:
				this.postDelayed(new Runnable() {
					@Override
					public void run() {
						Util.bindAlertAlarm(context);
					}
				}, 200);
				break;
			}
		};
	};

	/** 应用更新下载地址 */
	private String apkDownUrl;
	/** 程序更新code */
	private int updateCode;

	private ImageButton homeImageBtn = null;

	/**
	 * 回调方法
	 */
	private ICallback.Stub mDataUpdateCallback = new ICallback.Stub() {
		@Override
		public void showResult(int result, String content) {
			apkDownUrl = content;
			updateCode = result;
			switch (updateCode) {
			case CheckAPPUpdateService.NO_UPDATE:// 没有更新
				return;
			case CheckAPPUpdateService.NEED_TO_UPDATE:// 有更新
				showDialog(DialogRes.DIALOG_ID_HAS_UPDATE);
				break;
			case CheckAPPUpdateService.MUST_TO_UPDATE:// 强制更新
				showDialog(DialogRes.DIALOG_ID_MUST_UPDATE);
				dialog.setCancelable(false);
				break;
			}
		}
	};

	@Override
	public void setDownloadProgress(int progress, String title, int id) {
		ahfs_notification.contentView.setTextViewText(
				R.id.txt_notification_title,
				title.subSequence(0, title.length() - 4));
		ahfs_notification.contentView.setProgressBar(R.id.pb, 100, progress,
				false);
		ahfs_notification.contentView.setTextViewText(R.id.tv, progress + "%");
		if (progress == downloadNo) {
			// notificationManager.notify(Constant.DOWNLOAD_ID_AHFS,
			// ahfs_notification);
			downloadNo = downloadNo + 2;
		}
		super.setDownloadProgress(progress, title, id);
	}

	/**
	 * 回调方法
	 */
	private ICallback.Stub mNewsCountCallback = new ICallback.Stub() {
		@Override
		public void showResult(int result, String content) {
			updateBitmapStatus(content, 2);
		}
	};

	/**
	 * 回调方法
	 */
	private ICallback.Stub mDrugAlertCallback = new ICallback.Stub() {
		@Override
		public void showResult(int result, String content) {
			updateBitmapStatus(content, 8);
		}
	};

	/**
	 * 与Service的连接
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IService.Stub.asInterface(service);
			try {
				if (!application.isDataIsRegistered()) {
					mService.registerCallback(mDataUpdateCallback);
					application.setDataIsRegistered(true);
				}
			} catch (RemoteException e) {
			}
		}
	};

	/**
	 * 新的新闻数目更新
	 */
	private ServiceConnection mNewsConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mNewsService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mNewsService = IService.Stub.asInterface(service);
			try {
				if (!application.isNewsIsRegistered()) {
					application.setNewsIsRegistered(true);
					mNewsService.registerCallback(mNewsCountCallback);
				}

			} catch (RemoteException e) {
			}
		}
	};

	/**
	 * 新的用药安全更新
	 */
	private ServiceConnection mDrugAlertConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mDrugAlertService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mDrugAlertService = IService.Stub.asInterface(service);
			try {
				if (!application.isAlertIsRegistered()) {
					application.setAlertIsRegistered(true);
					mDrugAlertService.registerCallback(mDrugAlertCallback);
				}
			} catch (RemoteException e) {
			}
		}
	};

	private NotificationManager notificationManager;
	public RelativeLayout rlt;
	private ImageView imgBG;
	public ImageView imgVersion;
	private final int version_bg_id = 0x121;

	private Bitmap background;
	HomePageAdapter homePageAdapter1;
	HomePageAdapter homePageAdapter2;

	@Override
	protected void onDestroy() {
		try {
			if (timer != null) {
				timer.cancel();
			}
			if (mService != null) {
				try {
					if (application.isDataIsRegistered()) {
						mService.unregisterCallback(mDataUpdateCallback);
						application.setDataIsRegistered(false);
					} else if (application.isNewsIsRegistered()) {
						mNewsService.unregisterCallback(mNewsCountCallback);
						application.setNewsIsRegistered(false);
					} else if (application.isAlertIsRegistered()) {
						mDrugAlertService
								.unregisterCallback(mDrugAlertCallback);
						application.setAlertIsRegistered(false);
					}

				} catch (RemoteException e) {
				}
			}
			if (!application.isUnbinded()) {
				if (application.isDataIsBinded()) {
					unbindService(mConnection);
					application.setDataIsBinded(false);
				} else if (application.isNewsIsBinded()) {
					unbindService(mNewsConnection);
					application.setNewsIsBinded(false);
				} else if (application.isAlertIsBinded()) {
					unbindService(mDrugAlertConnection);
					application.setAlertIsBinded(false);
				}
				application.setUnbinded(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
		if (background != null)
			background.recycle();
		newsAdapter.closeDB();
		drugAlertAdapter.closeDB();
		dbHelper.closeDB();
	}

	Handler startHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			ahfs_notification.contentView = new RemoteViews(getPackageName(),
					R.layout.dialog_notification);
			ahfs_notification.contentView
					.setProgressBar(R.id.pb, 100, 0, false);

			ahfs_notification.contentIntent = PendingIntent.getActivity(
					getBaseContext(), 0, new Intent(), 0);

			tackaleNotify(getIntent().getStringExtra("FROM"));
			pckgMap.put("drug", R.string.drug);
			pckgMap.put("calculator", R.string.calculator);
			pckgMap.put("tool", R.string.tool);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.home_page);
		setContentView(R.layout.home_page);
		super.onCreate(savedInstanceState);
		ActivityPool.getInstance().activityMap.put(this.getClass(), this);
		initUMengSetting();
		try {
			// checkZipFile(null);
			application = (GBApplication) getApplication();
			imgMap = new HashMap<Integer, ImageView>();
			pckgMap = new HashMap<String, Integer>();
			lltBottom = (LinearLayout) findViewById(R.id.llt_main_bottom);
			lltHeader = (LinearLayout) findViewById(R.id.subSearchLayout);
			homeImageBtn = (ImageButton) findViewById(R.id.imgbtn_home);
			initVersionPic();
			setPopupWindowType(Constant.MENU_TYPE_1);
			prepareGVData();
			homePageAdapter1 = new HomePageAdapter(HomePageActivity.this,
					itemList1, 1);
			homePageAdapter2 = new HomePageAdapter(HomePageActivity.this,
					itemList2, 2);
			homePage = (HomePageLayout) findViewById(R.id.root_homePage);
			homePage.addView(generateGV(homePageAdapter1));
			homePage.addView(generateGV(homePageAdapter2));
			homePage.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					checkIsShowVersion();
					return false;
				}
			});
			new Thread(new Runnable() {

				@Override
				public void run() {
					HomePageLayout.s_pageID = 0;
					drugAlertAdapter = new DrugAlertSQLAdapter(getBaseContext());
					dbHelper = new LoginSQLAdapter(getBaseContext());

					newsAdapter = new NewsSQLAdapter(getBaseContext());
					profileAdapter = new ProfileAdapter(getBaseContext(),
							Constant.MY_SQLITE_VESION);
					baseHandler = new BaseResponseHandler(HomePageActivity.this);
					baseHandler = new AsyncSoapResponseHandler();
					startHandler.sendEmptyMessage(0);
					// 检查Interaction数据库是否有更新
					// new InteractUpdate(HomePageActivity.this);
				}

			}).start();
			inits();

			if (Util.isFirstLoadHomepage) {
				Util.isFirstLoadHomepage = false;
				if (SharedPreferencesMgr.isFirstLaunchedHome()) {
					Toast.makeText(context,
							getString(R.string.homepage_welcome),
							Toast.LENGTH_LONG).show();
					SharedPreferencesMgr.setFirstLaunchHome(false);
				}
			}

			// moveCalculatorDB();

			// 从外部进入应用

			// UmengUpdateAgent.setUpdateOnlyWifi(false);
			// UmengUpdateAgent.update(this);
			// Debug.stopMethodTracing();
//			if (PushConst.pushFlag==null||PushConst.pushFlag) {
//				if (PushConst.pushmodule == PushConst.News_MODULE) {
//					startActivity(new Intent(context, NewsPageActivity.class));
//				} else if (PushConst.pushmodule == PushConst.RESEARCH_MODULE) {
//					startActivity(new Intent(context,
//							ResearchPageActivity.class));
//				} else if (PushConst.pushmodule == PushConst.SECURE_MODULE) {
//					startActivity(new Intent(context, DrugAlertsActivity.class));
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class PrepareAssociateData implements Runnable {
		@Override
		public void run() {
			try {
				/***
				 * 准备药品搜索相关匹配数据
				 */
				Logs.i("================= start =====================");
				SqliteAdapterCentre.getInstance().renew(
						DBFactory.SDCard_DB_NAME);
				Cursor cursor = SqliteAdapterCentre
						.getInstance()
						.get(DBFactory.SDCard_DB_NAME)
						.getCursor(
								"select DrugID,AlternativeName,ChangeDate from SearchDictionary order by AlternativeName",
								null);

				if (cursor == null || cursor.getCount() == 0) {
					return;
				}
				List<EntityObj> obj = new ArrayList<EntityObj>();
				List<EntityObj> objEn = new ArrayList<EntityObj>();
				AutoCache lang = new AutoCache(SqliteAdapterCentre
						.getInstance().get(DBFactory.SDCard_DB_NAME));
				while (cursor.moveToNext()) {
					EntityObj ej = null;
					ej = new EntityObj();
					for (int i = 0; i < cursor.getColumnCount(); i++) {
						ej.fieldContents.put(cursor.getColumnName(i),
								cursor.getString(i));
					}

					String name = cursor.getString(1);
					if (name.matches("[\u4e00-\u9fa5]{1,5}")) {
						application.getAssociateDataCNList().add(name);
						if (obj.size() == 0) {
							lang.cacheInfo.setCacheTable("SearchDictionaryZh",
									"");
							lang.autoCreateTable(ej.fieldContents,
									"SearchDictionaryZh");
						}
						obj.add(ej);
					} else {
						application.getAssociateDataENList().add(name);
						if (objEn.size() == 0) {
							lang.cacheInfo.setCacheTable("SearchDictionaryEn",
									"");
							lang.autoCreateTable(ej.fieldContents,
									"SearchDictionaryEn");
						}
						objEn.add(ej);
					}
				}
				if (getResources().getConfiguration().locale == Locale.CHINESE) {

				} else if (getResources().getConfiguration().locale == Locale.ENGLISH) {

				}
				lang.insertData(obj, "SearchDictionaryZh", "");
				lang.insertData(objEn, "SearchDictionaryEn", "");
				Logs.i("================= end =====================");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void tackaleNotify(String comeFrom) {
		if (null != comeFrom && "GBApp".equals(comeFrom)) {
			// 每109次打开程序，弹窗填写评价
			int count = SharedPreferencesMgr.getApplicationUsedCount();
			count++;
			if (count % 10 == 0) {
				showDialog(DialogRes.DIALOG_GOTO_EVALUATE);
				count = 0;
			}
			SharedPreferencesMgr.saveApplicationUsedCount(count);
			showNotification();
			Logs.i("=== IsSetAlarmClock "
					+ SharedPreferencesMgr.getIsSetAlarmClock());
			if (!SharedPreferencesMgr.getIsSetAlarmClock()) {
				Message msg = new Message();
				msg.what = alarm;
				handler.sendMessage(msg);
			} else {
//				if (Constant.DEBUG)
					Util.bindAlertAlarm(this);
			}

			Thread prepareThread = new Thread(new PrepareAssociateData());
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// /***
			// * 准备药品搜索相关匹配数据
			// */
			// Logs.i("================= start =====================");
			// SqliteAdapterCentre.getInstance().renew(DBFactory.SDCard_DB_NAME);
			// Cursor cursor = SqliteAdapterCentre
			// .getInstance()
			// .get(DBFactory.SDCard_DB_NAME)
			// .getCursor(
			// "select DrugID,AlternativeName,ChangeDate from SearchDictionary order by AlternativeName",
			// null);
			//
			// if (cursor == null || cursor.getCount() == 0) {
			// return;
			// }
			// while (cursor.moveToNext()) {
			//
			// String name = cursor.getString(1);
			// if (name.matches("[\u4e00-\u9fa5]{1,5}")) {
			//
			// application.getAssociateDataCNList().add(name);
			// } else {
			// application.getAssociateDataENList().add(name);
			// }
			// }
			// Logs.i("================= end =====================");
			// }
			//
			// });
			// prepareThread.start();
			checkSqlscript();
			try {
				if (application.getDeviceInfo().isNetWorkEnable()) {
					Intent i = new Intent(this, CheckAPPUpdateService.class);
					if (!application.isDataIsBinded()) {
						bindService(i, mConnection, Context.BIND_AUTO_CREATE);
						application.setDataIsBinded(true);
					}
					// Intent iNews = new Intent(this,
					// CheckNewsCountService.class);
					// Intent iDrugAlert = new Intent(this,
					// CheckDrugAlertService.class);
					// if (!application.isNewsIsBinded()) {
					// bindService(iNews, mNewsConnection,
					// Context.BIND_AUTO_CREATE);
					// application.setNewsIsBinded(true);
					// }
					//
					// if (!application.isAlertIsBinded()) {
					// bindService(iDrugAlert, mDrugAlertConnection,
					// Context.BIND_AUTO_CREATE);
					// application.setAlertIsBinded(true);
					// }
					getAdvertisingImageUrl();
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								Thread.sleep(900);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							updateMsg();
						}

					}).start();
				}
			} catch (Exception e) {
			}
		}
	}

	/***
	 * 检查是否有db脚本需要执行
	 */
	private void checkSqlscript() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				File dir = new File(Constant.DATA_PATH_SQL);
				if (dir.exists()) {
					File[] files = dir.listFiles(new FileFilter() {
						@Override
						public boolean accept(File file) {
							if (file.getName().toLowerCase().endsWith(".sql"))
								return true;
							return false;
						}
					});
					if (null != files && files.length > 0) {
						BufferedReader buffer = null;
						FileReader reader = null;
						SQLiteDatabase db = daoSession.getDatabase();
						StringBuffer str = new StringBuffer();
						try {
							db.beginTransaction();
							for (File f : files) {
								try {
									reader = new FileReader(f);
									buffer = new BufferedReader(reader);
									while (buffer.ready()) {
										// Log.i("sql", buffer.readLine());
										// db.execSQL(buffer.readLine());
										str.append(buffer.readLine());
									}
									db.execSQL(str.toString());
								} catch (Exception e1) {
									e1.printStackTrace();
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
							db.setTransactionSuccessful();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							db.endTransaction();
						}
					}
				}
				// 当前本地数据版本
				List<Version> list = daoSession.getVersionDao().loadAll();
				// 检查是否有新的脚本
				if (null != list && list.size() > 0) {
					Message msg = new Message();
					msg.obj = list.get(0);
					updateSourceHandler.sendMessage(msg);
				}
			}
		}).start();
	}

	/***
	 * 发送请求检查服务器是否有资源更新
	 */
	private Handler updateSourceHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Version version = (Version) msg.obj;
			Properties propertyInfo = new Properties();
			propertyInfo.put("dataName", "exDrug");
			propertyInfo.put("drugVersion", version.getPatchVersionCode());
			propertyInfo.put("version", version.getVersionCode());
			propertyInfo.put("platform", "android");
			sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_DATA_NEW_VERSION,
					propertyInfo, new BaseResponseHandler(
							HomePageActivity.this, false));
		}
	};

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	public void tipImpl() {
		// TODO Auto-generated method stub

	}

	/**
	 * 评价
	 */
	private void evaluate() {
		// 本地
		// startActivity(new Intent(Intent.ACTION_VIEW,
		// Uri.parse("market://search?q=com.api.android.GBApp")));

		// 网络
		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.chooseMarket))
				.setItems(R.array.markets,
						new DialogInterface.OnClickListener() {
							// content
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String url = "";
								switch (which) {
								case 0:
									url = "https://play.google.com/store/apps/details?id=com.api.android.GBApp";
									break;
								case 1:
									url = "http://www.appchina.com/soft_detail_289428_0_10.html";
									break;
								case 2:
									url = "http://apk.gfan.com/Product/App168533.html";
									break;
								case 3:
									url = "http://www.anzhi.com/soft_181414.html";
									break;
								case 4:
									url = "http://mobile.91.com/Soft/Android/com.api.android.GBApp-1.10.html";
									break;
								}

								startActivity(new Intent(Intent.ACTION_VIEW,
										Uri.parse(url)));
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
	}

	// public void moveCalculatorDB() {
	// if (!Environment.getExternalStorageState().equals(
	// Environment.MEDIA_MOUNTED)) {
	// showDialog(DialogRes.DIALOG_ID_SDCARD_NOT_AVAILABLE);
	// } else {
	// File file = new File(getFilesDir() + File.separator
	// + "calculate.db");
	// if (file.exists()) {
	// file.delete();
	// }
	//
	// String db[] = { "calculate.db" };
	// DBMoveAsyncTask task = new DBMoveAsyncTask(HomePageActivity.this,
	// new DBMoveAsyncTask.CallBack() {
	// @Override
	// public void onFinish(boolean b) {
	// }
	// });
	// task.execute(db);
	//
	// }
	// }

	// 获取广告图片
	private void getAdvertisingImageUrl() {
		advert = dbHelper.getAdvertising(SharedPreferencesMgr.getUserName());
		if (null != advert && null != advert.imageUrl
				&& !"".equals(advert.imageUrl)) {
			Log.i("test", "图片url" + advert.imageUrl);
			Bitmap map = BitmapManager.loadBitmap(advert.imageUrl);
			if (map == null) {
				Log.i("test", "本地没有图片");
				new Thread(new Runnable() {
					@Override
					public void run() {
						BitmapManager.downloadBitmap(advert.imageUrl, 0, 0);
					}
				}).start();
			}
		}

		// else {
		// Properties propertyInfo = new Properties();
		// propertyInfo.put("company", advert.companyName);
		// sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_GET_IMAGE_PATH,
		// propertyInfo, new BaseResponseHandler(this, false));
		//
		// }
	}

	protected void updateMsg() {
		Properties propertyInfo = new Properties();
		propertyInfo.put(SoapRes.KEY_DEADLINE, "");
		propertyInfo.put(SoapRes.KEY_REGION, "");
		propertyInfo.put(SoapRes.KEY_DEPART, "");
		sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_GETMSG, propertyInfo,
				baseHandler);
	}

	@Override
	protected void onStop() {
		super.onStop();
		drugAlertAdapter.closeHelp();
		newsAdapter.closeHelp();
	}

	boolean fst = false;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		SpotUtil.spots.get(SpotUtil.LASTEST_SPOT).addSpotView(imgMap.get(4),this);
		SpotUtil.spots.get(SpotUtil.LASTEST_SPOT).doSpot(this);
		
		updateBitmapStatus(SharedPreferencesMgr.getNewsUpdateCount(), 2);
		updateBitmapStatus(SharedPreferencesMgr.getSurveysUpdateCount(), 6);
		updateBitmapStatus(SharedPreferencesMgr.getDrugAlertsUpdateCount(), 8);
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			drawCircle();

			if ("".equals(SharedPreferencesMgr.getInviteCode())) {
				imgVersion.setImageResource(R.drawable.version_standard);
				imgBG.setImageResource(R.drawable.version_standard_bg);
			} else {
				imgVersion.setImageResource(R.drawable.version_premium);
				imgBG.setImageResource(R.drawable.version_premium_bg);
			}

			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
			timer = new Timer();
			timerTask = new TimerTask() {
				@Override
				public synchronized void run() {
					if (version_flag == 1) {
						version_alpha = version_alpha - 51;
						Message msg = handler.obtainMessage(version_img,
								version_alpha);
						handler.sendMessage(msg);
						if (version_alpha == 0) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							version_flag = 0;
						}
					} else {
						version_alpha = version_alpha + 51;
						Message msg = handler.obtainMessage(version_img,
								version_alpha);
						handler.sendMessage(msg);
						if (version_alpha == 255) {
							version_flag = 1;
						}
					}
				}
			};

			timer.schedule(timerTask, 1000, 100);
			// timer.

			MobclickAgent.onResume(this);

			homeImageBtn.post(new Runnable() {
				@Override
				public void run() {
					SharedPreferences newUser = getSharedPreferences(
							"old_user_info", 0);
					if (newUser.getBoolean(SharedPreferencesMgr.getUserName(),
							true)) {
						newUser.edit()
								.putBoolean(SharedPreferencesMgr.getUserName(),
										false).commit();
						TipHelper.oldUserUseFlag = true;
					}

					if (TipHelper.newUserUseFlag) {
						TipHelper.disableTipViewOnScreenVisibility();
						TipHelper.sign(true, true);
						TipHelper.getTraceRecorder().putAll(
								TipHelper.traceRecorderRecovery);
						TipHelper.traceRecorderRecovery.clear();
						TipHelper.getBackup().clear();
						TipHelper.newUserUseFlag = false;
						TipHelper.oldUserUseFlag = false;
					}

					TipHelper.registerTips(HomePageActivity.this, 1);
					TipHelper.runSegments((Activity) HomePageActivity.this);
					HomePageActivity.this.findViewById(R.id.closeTips)
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									TipHelper.sign(false, true);
									TipHelper
											.disableTipViewOnScreenVisibility();
								}
							});

					if (TipHelper.oldUserUseFlag) {
						HomePageActivity.this.findViewById(R.id.mask)
								.setVisibility(View.VISIBLE);
						HomePageActivity.this.findViewById(R.id.showversion)
								.setVisibility(View.VISIBLE);
						HomePageActivity.this.findViewById(R.id.switchTip)
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										findViewById(R.id.showversion)
												.setVisibility(View.GONE);
										TipHelper.sign(true, true);
										TipHelper
												.runSegments((Activity) HomePageActivity.this);
										TipHelper.oldUserUseFlag = false;
									}
								});
						HomePageActivity.this.findViewById(R.id.closeTip)
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										findViewById(R.id.showversion)
												.setVisibility(View.GONE);
										TipHelper.sign(false, true);
										findViewById(R.id.mask).setVisibility(
												View.INVISIBLE);
										TipHelper.oldUserUseFlag = false;
									}
								});
						TipHelper.oldUserUseFlag = true;
						SharedPreferenceUtil.putValue(getApplicationContext(),
								TipHelper.sharedpreferenceFile,
								TipHelper.sharedpreferenceFirstUseKey,
								Boolean.FALSE);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initUMengSetting() {
		MobclickAgent.setDebugMode(true); // 设置是否debug模式
		MobclickAgent.setSessionContinueMillis(1000);// change 30000(default)
														// to// 1000
		MobclickAgent.updateOnlineConfig(this);
		com.umeng.common.Log.LOG = true;// 4.0版本以后需要设置
		MobclickAgent.setAutoLocation(false);// collect location info, need some
												// permission. = true(default)
		// set debug mode ,will print log in logcat(lable:MobclickAgent)
		// =true(default)
		// UmengConstants.enableCacheInUpdate = false;// 设置自动更新时是否使用缓存
		// MobclickAgent.update(this, 1000 * 60 * 60 * 24);// daily
		MobclickAgent.setDefaultReportPolicy(context,
				ReportPolicy.BATCH_AT_LAUNCH);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (!isPopflg()) {
				showDialog(DialogRes.DIALOG_QUIT_PROMPT);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void inits() {
		try {
			imgbtnSearch = (ImageButton) findViewById(R.id.imgbtn_search);
			actvInput = (AutoCompleteTextView) findViewById(R.id.actv_searchEdit);
			actvInput.setOnFocusChangeListener(this);
			actvInput.setOnTouchListener(this);
			scale = application.getDeviceInfo().getScale();
			int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
			int screenHeight = getWindowManager().getDefaultDisplay()
					.getHeight();
			application.getDeviceInfo().setScreenWidth(screenWidth);
			application.getDeviceInfo().setScreenHeight(screenHeight);

			imgbtnSearch.setVisibility(LinearLayout.GONE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点击dialog左键 子类需要重载
	 * */
	@Override
	public void clickPositiveButton(int dialogId) {
		Intent intent = null;
		removeDialog(dialogId);
		switch (dialogId) {
		case DialogRes.DIALOG_ID_MUST_UPDATE:
			intent = new Intent(this, UpdateAppService.class);
			intent.putExtra("updateCode", updateCode);
			intent.putExtra("url", apkDownUrl);
			startService(intent);
			application.quit();
			break;
		case DialogRes.DIALOG_ID_HAS_UPDATE:
			// intent = new Intent(this, InitializeActivity.class);
			// intent.putExtra(Constant.CHECK_DATA_UPDATE, true);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// intent.putExtra("checkupdate", true);
			// SharedPreferencesMgr.setFirstLaunch(true);
			// if (intent != null)
			// startActivity(intent);
			// finish();
			intent = new Intent(this, UpdateAppService.class);
			intent.putExtra("url", apkDownUrl);
			intent.putExtra("updateCode", updateCode);
			startService(intent);
			Toast.makeText(context, R.string.background_downlowd, 1).show();
			break;
		case DialogRes.DIALOG_QUIT_PROMPT:
		case DialogRes.DIALOG_ID_SDCARD_NOT_AVAILABLE:
			// intent = new Intent(this, InitializeActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// finish();
			super.clickPositiveButton(DialogRes.DIALOG_QUIT_PROMPT);
			break;
		case DialogRes.DIALOG_ID_NO_LISENCE:
		case DialogRes.DIALOG_ID_ERROR_LISENCE:
			intent = new Intent(this, Registration_updateActivity.class);
			intent.putExtra("isModify", true);
			startActivity(intent);
			break;
		case DialogRes.DIALOG_ID_SURVEY_DATA_LACK:
			intent = new Intent(this, Registration_updateActivity.class);
			intent.putExtra("isModify", true);
			startActivity(intent);
			break;
		case DialogRes.DIALOG_ID_NEW_DATA_AVAILABLE:
			// downloadData();
			break;
		case DialogRes.DIALOG_GOTO_EVALUATE:
			// evaluate();
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=com.api.android.GBApp")));
			break;
		}
	}

	/**
	 * 点击dialog右键 子类需要重载
	 * */
	@Override
	public void clickNegativeButton(int dialogId) {
		if (dialogId == DialogRes.DIALOG_ID_MUST_UPDATE)
			application.quit();
		else
			removeDialog(dialogId);
	}

	private void prepareGVData() {
		itemList1 = new ArrayList<HomePageItemEntity>();
		itemList2 = new ArrayList<HomePageItemEntity>();

		HomePageItemEntity item1 = new HomePageItemEntity();
		item1.setTitle(getString(R.string.drug));
		item1.setPicID(R.drawable.drug_reference);
		itemList1.add(item1);

		HomePageItemEntity item2 = new HomePageItemEntity();
		item2.setTitle(getString(R.string.calculator));
		item2.setPicID(R.drawable.tables_calculations);
		itemList1.add(item2);

		HomePageItemEntity item3 = new HomePageItemEntity();
		item3.setTitle(getString(R.string.news));
		item3.setPicID(R.drawable.news);
		itemList1.add(item3);

		HomePageItemEntity item4 = new HomePageItemEntity();
		item4.setTitle(getString(R.string.interaction));
		item4.setPicID(R.drawable.interaction_profile);
		itemList1.add(item4);

		HomePageItemEntity item5 = new HomePageItemEntity();
		item5.setTitle(getString(R.string.research));
		item5.setPicID(R.drawable.research);
		itemList1.add(item5);

		HomePageItemEntity item6 = new HomePageItemEntity();
		item6.setTitle(getString(R.string.contactMufacture));
		item6.setPicID(R.drawable.contact_manufuture);
		itemList1.add(item6);

		HomePageItemEntity item7 = new HomePageItemEntity();
		item7.setTitle(getString(R.string.survey));
		item7.setPicID(R.drawable.survey);
		itemList1.add(item7);

		HomePageItemEntity item8 = new HomePageItemEntity();
		item8.setTitle(getString(R.string.tool));
		item8.setPicID(R.drawable.tools_icon);
		itemList1.add(item8);

		HomePageItemEntity item9 = new HomePageItemEntity();
		item9.setTitle(getString(R.string.drugalert));
		item9.setPicID(R.drawable.drugalert);
		itemList1.add(item9);

		HomePageItemEntity item10 = new HomePageItemEntity();
		item10.setTitle(getString(R.string.history_favorite));
		item10.setPicID(R.drawable.history_favorite);
		itemList2.add(item10);

		HomePageItemEntity item11 = new HomePageItemEntity();
		item11.setTitle(getString(R.string.market));
		item11.setPicID(R.drawable.market);
		itemList2.add(item11);

		HomePageItemEntity item12 = new HomePageItemEntity();
		item12.setTitle(getString(R.string.set_up));
		item12.setPicID(R.drawable.set_up);
		itemList2.add(item12);

		HomePageItemEntity item13 = new HomePageItemEntity();
		item13.setTitle(getString(R.string.academic_profile));
		item13.setPicID(R.drawable.academic_profile);
		itemList2.add(item13);
		// HomePageItemEntity item11 = new HomePageItemEntity();
		// item11.setTitle(getString(R.string.network));
		// item11.setPicID(R.drawable.network);
		// itemList2.add(item11);
		// HomePageItemEntity item12=new HomePageItemEntity();
		// item12.setTitle(getString(R.string.event));
		// item12.setPicID(R.drawable.events);
		// itemList2.add(item12);
		ahfs_notification = new Notification(R.drawable.icon, "",
				System.currentTimeMillis());
		ahfs_notification.icon = R.drawable.icon;
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	}

	/**
	 * @description 在下方画出原型图标，指示当前界面
	 * @param void
	 * @return void
	 */
	public void drawCircle() {
		LayoutParams lp;
		float scale = application.getDeviceInfo().getScale();
		if (scale == 1.0) {
			lp = new LayoutParams(33, 13);
		} else {
			lp = new LayoutParams(55, 22);
		}
		lltBottom.removeAllViews();
		lltBottom.addView(new CircleIndicator(this, 2, scale), lp);
	}

	private View generateGV(HomePageAdapter adapter) {
		GridView gv = new GridView(this);
		gv.setSelector(android.R.color.transparent);
		gv.setCacheColorHint(getResources().getColor(R.color.transparent));
		gv.setPadding((int) (20 * scale), 0, (int) (20 * scale), 0);
		gv.setVerticalSpacing(5);
		gv.setNumColumns(3);
		gv.setAdapter(adapter);
		gv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}

		});
		gv.setOnItemClickListener(new GVItemClickListener());

		return gv;
	}

	public void showNotification() {

		Notification notification = new Notification(R.drawable.icon, this
				.getResources().getString(R.string.app_name),
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.ledARGB = Color.BLUE;
		notification.ledOnMS = 5000;

		CharSequence contentTitle = this.getResources().getString(
				R.string.app_name);
		CharSequence contentText = this.getResources().getString(
				R.string.notification_text);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(this, HomePageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		PendingIntent contentItent = PendingIntent.getActivity(this, 0, intent,
				0);
		notification.setLatestEventInfo(this, contentTitle, contentText,
				contentItent);

		notificationManager.notify(0, notification);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case version_bg_id:
			Intent intent = new Intent(this, UpdateInviteCodeActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// switch(v.getId()) {
		// case R.id.actv_searchEdit:
		// if(SharedPreferencesMgr.getIsFirstSearch()){
		// Intent tipsIntent = new Intent(this,DrugSearchTipsActivity.class);
		// startActivity(tipsIntent);
		// SharedPreferencesMgr.setFirstSearch(false);
		// }
		// break;
		// }
		return false;
	}

	/**
	 * 检查闪烁logo是否显示，如未显示，关闭软键盘，切换屏幕焦点，显示闪烁logo
	 */
	private void checkIsShowVersion() {
		if (!isShowLogo) {
			actvInput.clearFocus();// here forward onFocusChange
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(actvInput.getWindowToken(), 0);
		}
	}

	/**
	 * * 九宫格图标点击 * * @author GB
	 * */
	private class GVItemClickListener implements OnItemClickListener {
		private Intent intent;

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// checkIsShowVersion();
			switch (arg2) {
			case 0:
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					showDialog(DialogRes.DIALOG_ID_SDCARD_NOT_AVAILABLE);
				} else {
					if (HomePageLayout.s_pageID == 0) {
						if (checkDepartment(intent,
								"com.jibo.activity.DrugReferenceListActivity1"))
							return;
						else {
							intent = new Intent(HomePageActivity.this,
									DrugReferenceListActivity1.class);
							intent.putExtra("departMent",
									SharedPreferencesMgr.getDept());
							startActivity(intent);
						}
					} else {
						intent = new Intent(HomePageActivity.this,
								HistoryFavoriteActivity.class);
						startActivity(intent);
					}
				}
				break;
			case 1:
				if (HomePageLayout.s_pageID == 0) {
					if (!Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						showDialog(DialogRes.DIALOG_ID_SDCARD_NOT_AVAILABLE);
					} else {
						if (checkDepartment(intent,
								"com.jibo.activity.TabCalcList_NewActivity"))
							return;
						else {
							Intent intent = new Intent();
							intent.putExtra("from", "homepage");
							intent.setClass(HomePageActivity.this,
									TabCalcList_NewActivity.class);
							startActivity(intent);
						}

					}
				} else if (HomePageLayout.s_pageID == 1) {
					intent = new Intent(HomePageActivity.this,
							MarketActivity.class);
					startActivity(intent);
				}
				break;
			case 2:
				if (HomePageLayout.s_pageID == 0) {
					SharedPreferencesMgr.setNewsUpdateCount(0);
					// ArrayList<NewsEntity> news = newsAdapter
					// .getTopNewsByLocalDatabase();// 检查本地缓存
					// // if (null != news && news.size() > 0) {
					// intent = new Intent(HomePageActivity.this,
					// NewsActivity.class);
					// intent.putParcelableArrayListExtra("list", news);
					// intent.putExtra("isLoadLocal", true);
					// ((ImageView) imgMap.get(2))
					// .setVisibility(LinearLayout.GONE);
					// startActivity(intent);
					startActivity(new Intent(HomePageActivity.this,
							com.jibo.news.NewsPageActivity.class));
					// Properties propertyInfo = new Properties();
					// propertyInfo.put(SoapRes.KEY_SINCE_ID, "");
					// propertyInfo.put(SoapRes.KEY_MAX_ID, "");
					// propertyInfo.put(SoapRes.KEY_COUNT, "20");
					// propertyInfo.put(SoapRes.KEY_NEWS_CATEGORY, "");
					// propertyInfo.put(SoapRes.KEY_SEARCHVALUE, "");
					// sendRequest(SoapRes.URLNews,
					// SoapRes.REQ_ID_GET_NEWS_TOP_20, propertyInfo,
					// new BaseResponseHandler(HomePageActivity.this));
					// }
					break;
				} else if (HomePageLayout.s_pageID == 1) {
					intent = new Intent(HomePageActivity.this,
							AccountManagerActivity.class);
					startActivity(intent);
					break;
				}

			case 3:

				if (HomePageLayout.s_pageID == 0) {
					intent = new Intent(HomePageActivity.this,
							InteractIndex.class);
					MobclickAgent.onEvent(getApplicationContext(),"Interaction");
					uploadLoginLogNew("Interaction","Interaction", "homepageClick", null);
					startActivity(intent);
				} else if (HomePageLayout.s_pageID == 1) {
					// 下面内容原先是学术背景的，现改为药物相互作用
					ProfileEntity en = profileAdapter
							.getProfile(SharedPreferencesMgr.getDrugId());
					intent = new Intent(HomePageActivity.this,
							AcademicProfileActivity.class);
					if (en != null) {
						intent.putExtra("data", en);
						startActivity(intent);
					} else {
						Properties propertyInfo = new Properties();
						String doctorId = SharedPreferencesMgr.getDrugId();
						if (doctorId != null && !"".equals(doctorId)) {
							propertyInfo.put(SoapRes.KEY_GETPROFILE_DOCTORID,
									doctorId);
							propertyInfo.put(SoapRes.KEY_GETPROFILE_USERNAME,
									application.getLogin().getGbUserName());
							sendRequest(SoapRes.URLProfile,
									SoapRes.REQ_ID_GET_PROFILE, propertyInfo,
									new BaseResponseHandler(
											HomePageActivity.this));
						} else {
							showDialog(DialogRes.DIALOG_ID_NO_LISENCE);
						}
					}
				}
				break;
			case 4:
				// if (checkDepartment(intent,
				// "com.jibo.activity.ResearchActivity"))
				// return;
				// else {
				// intent = new Intent(HomePageActivity.this,
				// ResearchActivity.class);
				// startActivity(intent);
				// }
				SpotUtil.spots.get(SpotUtil.LASTEST_SPOT).clear(HomePageActivity.this);
				try {
					startActivity(new Intent(HomePageActivity.this,
							ResearchPageActivity.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 5:
				intent = new Intent(HomePageActivity.this,
						ContactMufactrueActivity.class);
				startActivity(intent);
				break;
			case 6:
				// TODO
				SharedPreferencesMgr.setSurveysUpdateCount(0);
				((ImageView) imgMap.get(6)).setVisibility(LinearLayout.GONE);
				if ("".equals(SharedPreferencesMgr.getHospitalName())
						|| "".equals(SharedPreferencesMgr.getRegion())
						|| "".equals(SharedPreferencesMgr.getDept())
						|| "".equals(SharedPreferencesMgr.getName())
						|| "".equals(SharedPreferencesMgr.getContactNb())) {
					showDialog(DialogRes.DIALOG_ID_SURVEY_DATA_LACK);
				} else {
					Intent intent6 = new Intent(HomePageActivity.this,
							NewSurveyActivity.class);
					startActivity(intent6);
				}

				break;
			case 7:
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					showDialog(DialogRes.DIALOG_ID_SDCARD_NOT_AVAILABLE);
				} else {
					intent = new Intent(HomePageActivity.this,
							ToolsActivity.class);
					startActivity(intent);
				}
				break;
			case 8:
				SharedPreferencesMgr.setDrugAlertsUpdateCount(0);
				ArrayList<DrugAlertEntity> lists = drugAlertAdapter
						.getDrugAlertsByLocalDatabase("0");// 检查本地缓存
				if (null != lists && lists.size() > 0) {
					intent = new Intent(HomePageActivity.this,
							DrugAlertsActivity.class);
					intent.putParcelableArrayListExtra("list", lists);
					intent.putExtra("isLoadLocal", true);
					((ImageView) imgMap.get(8))
							.setVisibility(LinearLayout.GONE);
					startActivity(intent);
				} else {
					Properties propertyInfo = new Properties();
					propertyInfo.put(SoapRes.KEY_DRUGALERT_TYPEID, "");
					propertyInfo.put(SoapRes.KEY_DRUGALERT_SOURCE, "");
					propertyInfo.put(SoapRes.KEY_SINCE_ID, "");
					propertyInfo.put(SoapRes.KEY_MAX_ID, "");
					propertyInfo.put(SoapRes.KEY_COUNT, "");
					sendRequest(SoapRes.URLDrug,
							SoapRes.REQ_ID_GET_DRUGALERT_TOP_20, propertyInfo,
							new BaseResponseHandler(HomePageActivity.this));
				}
				break;
			}
		}
	}

	/**
	 * 检查本地是否有科室信息
	 * 
	 * @return true：没有 false：有
	 */
	private boolean checkDepartment(Intent intent, String className) {
		if (TextUtils.isEmpty(SharedPreferencesMgr.getDept())) {
			if (null == intent)
				intent = new Intent();
			intent.setClass(HomePageActivity.this, SetDepartmentActivity.class);
			intent.putExtra("className", className);
			startActivity(intent);
			return true;
		}
		return false;
	}

	public void initVersionPic() {
		// TODO
		isShowLogo = true;
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp.rightMargin = -13;
		// lp.topMargin = -10;
		rlt = new RelativeLayout(this);
		imgBG = new ImageView(this);
		imgBG.setId(version_bg_id);
		imgBG.setOnClickListener(this);
		imgVersion = new ImageView(this);
		rlt.addView(imgBG);
		rlt.addView(imgVersion);
		lltHeader.addView(rlt, lp);
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o instanceof NewsListPaser) {// 服务器最新20条
			// NewsListPaser data = (NewsListPaser) o;
			// final ArrayList<NewsEntity> list = data.getList();
			// ((ImageView) imgMap.get(2)).setVisibility(LinearLayout.GONE);
			// Intent intent = new Intent(this, NewsActivity.class);
			// intent.putParcelableArrayListExtra("list", list);
			// intent.putExtra("isLoadLocal", false);
			// startActivity(intent);
		} else if (o instanceof DrugAlertDataPaser) {// 服务器最新20条
			DrugAlertDataPaser data = (DrugAlertDataPaser) o;
			ArrayList<DrugAlertEntity> list = data.getList();
			Intent intent = new Intent(this, DrugAlertsActivity.class);
			intent.putParcelableArrayListExtra("list", list);
			((ImageView) imgMap.get(8)).setVisibility(LinearLayout.GONE);
			intent.putExtra("isLoadLocal", false);
			startActivity(intent);
		} else if (o instanceof ProfilePaser) {
			ProfilePaser vd = (ProfilePaser) o;
			ProfileEntity en = vd.getEntity();
			if (en != null) {
				profileAdapter.setProfile(en);
				Intent intent = new Intent(HomePageActivity.this,
						AcademicProfileActivity.class);
				intent.putExtra("data", en);
				startActivity(intent);
			} else {
				showDialog(DialogRes.DIALOG_ID_ERROR_LISENCE);
			}
		} else if (o instanceof NewDATAVersionDataPaser) {// 检查是否有db脚本更新
			final NewDATAVersionDataPaser result = (NewDATAVersionDataPaser) o;
			if (!TextUtils.isEmpty(result.getRescode())
					&& result.getRescode().equals("200")) {
				if (!TextUtils.isEmpty(result.getUpdateCode())
						&& result.getUpdateCode().equals("1")) {
					Log.i("simon", "下载文件地址>>>" + result.getUrl());
					DownloadDBScriptAsyncTask task = new DownloadDBScriptAsyncTask(
							result.getUrl(),
							new DownloadDBScriptAsyncTask.CallBack() {
								@Override
								public void onFinish(boolean b) {
									if (b) {
										// 下载成功后，在下次打开应用时，checksqlscript方法里检查是否有需要更新的sql
										Log.i("simon", "下载文件成功");
									} else {
										Log.i("simon", "下载文件失败");
									}
								}
							});
					task.execute(null);
				}
			}
		}
		// else if (o instanceof GetAdvertisingImagePaser) {// 广告图片
		// final GetAdvertisingImagePaser data = (GetAdvertisingImagePaser) o;
		// String path = data.getImagePath();
		// if (null == path)
		// return;
		// if (null == advert.imageUrl || !path.equals(advert.imageUrl)) {
		// if (null != advert.imageUrl)
		// BitmapManager.deleteImage(advert.imageUrl);
		// dbHelper.insertAdvertising(new AdvertisingEntity(
		// SharedPreferencesMgr.getUserName(), advert.companyName,
		// path));
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// BitmapManager.downloadBitmap(data.getImagePath(), 0, 0);
		// }
		// }).start();
		// }
		// }
		else if (o instanceof GetCategoryUpdatePaser) {

		} else if (o instanceof InteractionPaser) {
			InteractionPaser codePaser = (InteractionPaser) o;
			System.out.println("xxxxxxxxxxxxxxxxxxx");
			String rescode = codePaser.getRescode();
			// if ("200".equals(rescode)) { // paser(SoapSerializationEnvelope
			// response) // } }

			super.onReqResponse(o, methodId);
		}
	}

	/**
	 * @description 更新图标，在右上角显示数字
	 * @param String
	 *            str: 要显示的数字
	 * @param int type: 在主页面中的位置
	 * @return void
	 */
	public void updateBitmapStatus(String str, int type) {
		BitmapText bt = null;
		if (Integer.parseInt(str) > 0) {
			if (type == 8) {
				bt = new BitmapText(this, str, R.drawable.drug_alert_normal,
						getBackBitmap(), 0, 0);
				((ImageView) imgMap.get(type)).setBackgroundDrawable(bt);
			} else if (type == 2) {
				Bitmap bitmap = ((BitmapDrawable) this.getResources()
						.getDrawable(R.drawable.news_normal)).getBitmap();
				bt = new BitmapText(this, str, R.drawable.news_normal,
						getBackBitmap(), 0, 0);
				((ImageView) imgMap.get(type)).setBackgroundDrawable(bt);
			}
		}
	}

	private Bitmap getBackBitmap() {
		if (background == null)
			background = BitmapFactory.decodeResource(getResources(),
					R.drawable.notification_background);
		return background;
	}

	public void updateBitmapStatus(int count, int type) {
		updateBitmapStatus(this, count, type);
	}

	public static void updateBitmapStatus(Context cx, int count, int type) {
		BitmapText bt = null;
		if (count > 0) {
			int drawableId = 0;
			switch (type) {
			case 2:
				// 新闻
				drawableId = R.drawable.news_normal;
				break;
			case 4:
				drawableId = R.drawable.u_spot;
				break;
			case 6:// 调查问卷
				drawableId = R.drawable.survey_normal;
				break;
			case 8:// 用药安全
				drawableId = R.drawable.drug_alert_normal;
				break;
			}
			String text = "";/* count > 99 ? "99+" : String.valueOf(count); */
			Bitmap background = null;
			if (background == null)
				background = BitmapFactory.decodeResource(cx.getResources(),
						R.drawable.u_spot);

			bt = new BitmapText(cx, text, R.drawable.u_spot, background,
					((ImageView) imgMap.get(type)).getWidth(),
					((ImageView) imgMap.get(type)).getHeight());
			((ImageView) imgMap.get(type)).setBackgroundDrawable(bt);
		}

	}

	/**
	 * @description 把位置及对应的ImageView对象添加到Map中
	 * @param int type: 在主页面中的位置
	 * @param ImageView
	 *            img: 主页面中的ImageView
	 * @return void
	 */
	public static void addImg(int type, ImageView img) {
		imgMap.put(type, img);
	}

	//
	// public void downloadData() {
	// Properties propertyInfo = new Properties();
	// InitializeAdapter initializeAdapter = new InitializeAdapter(this, 1);
	// String verName = initializeAdapter.getCurrentVerName();
	//
	// String dbCode = initializeAdapter.getCurrentDBCode();
	// DownloadDiffDB diffHandler = new DownloadDiffDB();
	// propertyInfo.put(SoapRes.KEY_P_VERSION, verName);
	// propertyInfo.put(SoapRes.KEY_D_VERSION, dbCode);
	// sendRequest(SoapRes.URLVersion, SoapRes.REQ_ID_VERSION, propertyInfo,
	// diffHandler);
	// }

	// private class DownloadDiffDB extends AsyncSoapResponseHandler {
	// @Override
	// public void onSuccess(Object o) {
	// if (o instanceof VersionDataParser) {
	// VersionDataParser vd = (VersionDataParser) o;
	// ArrayList<DownloadPacketEntity> list = vd.list;
	// if (list.get(0) != null) {
	// if (null == list.get(0).getAppURL()) {
	// if (Constant.FLAG_DIFF.equals(vd.action)) {
	// for (DownloadPacketEntity en : list) {
	// final ArrayList<PacketEntity> packetList = en
	// .getDataPacket();
	// downloadCount = 0;
	// for (final PacketEntity p : packetList) {
	// DownloadDiffDB diffHandler = new DownloadDiffDB();
	// DownloadAsyncTask downloadTask = new DownloadAsyncTask(
	// HomePageActivity.this,
	// DownloadAsyncTask.s_downLoadID,
	// vd.action, p.getVersionCode(),
	// p.getDownloadURL(), diffHandler,
	// new CallBack() {
	// @Override
	// public void onFinish(boolean b) {
	// downloadCount++;
	// if (downloadCount == packetList
	// .size()) {
	// if (updateDB()) {
	// Toast.makeText(
	// HomePageActivity.this,
	// R.string.data_update_success,
	// Toast.LENGTH_SHORT)
	// .show();
	// SharedPreferencesMgr
	// .setUpdateTime(Util
	// .getSystemTime());
	// }
	// }
	// }
	// });
	// downloadTask.execute(null);
	// }
	// }
	// }
	// }
	// }
	// }
	// super.onSuccess(o);
	// }
	//
	// @Override
	// public void onFinish() {
	// super.onFinish();
	// }
	// }

	private class UpdateDBTask extends AsyncTask<Integer, Integer, Integer> {
		private File file;
		private File fileList[];
		String dataPath = Environment.getExternalStorageDirectory()
				+ "/Jibo/update_tmp";
		private static final String UPDATE_DRUG = "drug";
		private static final String UPDATE_TOOL = "tool";
		private static final String UPDATE_CALCULATOR = "calculator";
		private HashMap<String, Boolean> updateProcessMap;
		private AlertDialog ad;
		private LinearLayout lltMain;
		private Button btnConfirm;

		public UpdateDBTask() {
			file = new File(dataPath);
			fileList = file.listFiles();

		}

		@Override
		protected void onPreExecute() {
			AlertDialog.Builder builder = new Builder(HomePageActivity.this);
			ad = builder.create();
			updateProcessMap = new HashMap<String, Boolean>();
			ad.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface arg0, int arg1,
						KeyEvent arg2) {
					if (arg1 == KeyEvent.KEYCODE_BACK) {
						return true;
					}
					return false;
				}
			});
			lltMain = new LinearLayout(HomePageActivity.this);
			lltMain.setOrientation(LinearLayout.VERTICAL);
			lltMain.setGravity(Gravity.CENTER);
			for (File f : fileList) {
				String fileName = f.getName().substring(0,
						f.getName().length() - 4);
				LinearLayout lltItem = new LinearLayout(HomePageActivity.this);
				lltItem.setPadding(10, 7, 10, 7);
				lltItem.setOrientation(LinearLayout.HORIZONTAL);
				TextView txtTitle = new TextView(HomePageActivity.this);
				TextView txtStatus = new TextView(HomePageActivity.this);

				if (UPDATE_DRUG.equals(fileName)) {
					txtTitle.setText(pckgMap.get(UPDATE_DRUG));
					lltItem.setId(PCKG_DRUG);
				} else if (UPDATE_TOOL.equals(fileName)) {
					txtTitle.setText(pckgMap.get(UPDATE_TOOL));
					lltItem.setId(PCKG_TOOL);
				} else if (UPDATE_CALCULATOR.equals(fileName)) {
					txtTitle.setText(pckgMap.get(UPDATE_CALCULATOR));
					lltItem.setId(PCKG_CALCULATOR);
				}
				lltItem.setOrientation(LinearLayout.HORIZONTAL);
				lltItem.setGravity(Gravity.CENTER_VERTICAL);

				txtTitle.setTextColor(Color.WHITE);
				txtStatus.setTextColor(Color.WHITE);
				txtStatus.setText(R.string.pckg_installing);

				LinearLayout.LayoutParams lpTitle = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
				LinearLayout.LayoutParams lpStatus = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lltItem.addView(txtTitle, lpTitle);
				lltItem.addView(txtStatus, lpStatus);
				lltMain.addView(lltItem);
			}
			btnConfirm = new Button(HomePageActivity.this);
			btnConfirm.setEnabled(false);
			btnConfirm.setText(R.string.ok);
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ad.dismiss();
				}
			});
			lltMain.addView(btnConfirm);
			ad.setView(lltMain);
			ad.setTitle(R.string.notification_text);
			ad.show();
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Integer... arg0) {
			for (File f : fileList) {
				try {
					unzipForDownloadData(f);
				} catch (Exception e) {

				}
			}

			File updateFile = new File(dataPath);
			File updateFileList[] = updateFile.listFiles();
			for (File f : updateFileList) {
				String fileName = f.getName();
				if (UPDATE_DRUG.equals(fileName)) {
					updateProcessMap.put(fileName, updateDrug(dataPath + "/"
							+ f.getPath()));
				} else if (UPDATE_TOOL.equals(fileName)) {
					updateProcessMap.put(fileName, updateTool(dataPath + "/"
							+ f.getPath()));
				} else if (UPDATE_CALCULATOR.equals(fileName)) {
					updateProcessMap.put(fileName, updateCalculator(dataPath
							+ "/" + f.getPath()));
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (fileList.length == updateProcessMap.size()) {
				for (Entry en : updateProcessMap.entrySet()) {
					if (UPDATE_DRUG.equals(en.getKey().toString())) {
						LinearLayout lltItem = (LinearLayout) lltMain
								.findViewById(PCKG_DRUG);
						if ((Boolean) en.getValue()) {
							((TextView) lltItem.getChildAt(1))
									.setText(R.string.pckg_install_success);
						} else {
							((TextView) lltItem.getChildAt(1))
									.setText(R.string.pckg_install_failure);
						}
					} else if (UPDATE_TOOL.equals(en.getKey().toString())) {
						LinearLayout lltItem = (LinearLayout) lltMain
								.findViewById(PCKG_TOOL);
						if ((Boolean) en.getValue()) {
							((TextView) lltItem.getChildAt(1))
									.setText(R.string.pckg_install_success);
						} else {
							((TextView) lltItem.getChildAt(1))
									.setText(R.string.pckg_install_failure);
						}
					} else if (UPDATE_CALCULATOR.equals(en.getKey().toString())) {
						LinearLayout lltItem = (LinearLayout) lltMain
								.findViewById(PCKG_CALCULATOR);
						if ((Boolean) en.getValue()) {
							((TextView) lltItem.getChildAt(1))
									.setText(R.string.pckg_install_success);
						} else {
							((TextView) lltItem.getChildAt(1))
									.setText(R.string.pckg_install_failure);
						}
					}
				}
				btnConfirm.setEnabled(true);
				Util.deleteDir(dataPath);
			}
			super.onPostExecute(result);
		}

		private boolean updateDrug(String path) {
			boolean result = true;
			try {

			} catch (Exception e) {
				result = false;
			}
			return result;
		}

		private boolean updateTool(String path) {
			boolean result = true;
			try {

			} catch (Exception e) {
				result = false;
			}
			return result;
		}

		private boolean updateCalculator(String path) {
			boolean result = true;
			try {

			} catch (Exception e) {
				result = false;
			}
			return result;
		}

		private boolean unzipForDownloadData(File outputFile) throws Exception {
			boolean result = true;
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(outputFile);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			String zipFileName = "";
			try {
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.isDirectory()) {
						String name = entry.getName();
						name = name.substring(0, name.length() - 1);
						File file = new File(dataPath + "/" + name);
						file.mkdir();
					} else {
						int count = 0;
						final int BUFFER = 4096;
						byte data[] = new byte[BUFFER];
						zipFileName = dataPath + File.separator
								+ entry.getName();
						FileOutputStream fos = null;
						String dbName = entry.getName().substring(
								entry.getName().lastIndexOf("/") + 1);
						fos = new FileOutputStream(dataPath + File.separator
								+ entry.getName());

						dest = new BufferedOutputStream(fos, BUFFER);
						while ((count = zis.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, count);
						}
						dest.flush();
						dest.close();
						fos.close();
					}
				}
				zis.close();
				File file = new File(outputFile.getAbsolutePath());
				if (file != null && file.exists()) {
					file.delete();
				}
			} catch (Exception e) {
				result = false;
			}
			return result;
		}
	}

	

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.actv_searchEdit:
			if (hasFocus) {
				imgbtnSearch.setVisibility(LinearLayout.VISIBLE);
				rlt.setVisibility(LinearLayout.GONE);
				isShowLogo = false;
			} else {
				imgbtnSearch.setVisibility(LinearLayout.GONE);
				rlt.setVisibility(LinearLayout.VISIBLE);
				isShowLogo = true;
			}
			break;
		}
	}

}
