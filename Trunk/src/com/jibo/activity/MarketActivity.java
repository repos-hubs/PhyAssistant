package com.jibo.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.asynctask.DownloadAsyncTask1;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.data.MarketPaserAdapter;
import com.jibo.data.entity.MarketPackageEntity;
import com.jibo.dbhelper.InitializeAdapter;
import com.jibo.dbhelper.MarketAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.tips.Mask;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;

public class MarketActivity extends BaseSearchActivity implements
		OnClickListener {
	private LinearLayout lltDataInfo;
	private LinearLayout lltMarketTitle;
	private LinearLayout lltMineTitle;
	private LinearLayout lltMarket;
	private LinearLayout lltMine;
	public LinearLayout lltMarketContent;
	private LinearLayout lltMineContent;
	private TextView txtTitle;
	private ImageView tabImgMarket;
	private ImageView tabImgMine;

	private BaseResponseHandler baseHandler;
	private InitializeAdapter initAdapter;
	private MarketAdapter marketAdapter;
	private ArrayList<MarketPackageEntity> marketAssociateEntityList;
	private ArrayList<MarketPackageEntity> marketUnAssociateEntityList;
	private ArrayList<MarketPackageEntity> mineEntityList;
	private HashMap<String, Button> marketDataMap;
	private HashMap<String, Button> mineDataMap;
	private ArrayList<View> packageViewList;

	private TreeMap<Integer, String> packageTreeMap;
	private GBApplication app;
	private final int PAGE_MARKET = 0;
	private final int PAGE_MINE = 1;
	private int PAGE_FLAG;

	private final String delete_file = "file";
	private final String delete_sql = "sql";
	private LayoutInflater inflater;
	private RelativeLayout contentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		inflater = LayoutInflater.from(this);
		contentView = (RelativeLayout) inflater.inflate(R.layout.app_market,
				null);
		setContentView(R.layout.app_market);
		// marketAdapter.getMarketAction(null);
		MobclickAgent.onError(this);

		super.onCreate(savedInstanceState);
		uploadLoginLogNew("Activity", "Market", "create", null);
	}

	@Override
	protected void onDestroy() {
		uploadLoginLogNew("Activity", "Market", "end", null);
		super.onDestroy();
	}

	public LinearLayout tipInstallBtnLay;

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		isInstall = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onStart() {
		inits();
		tabImgMarket.setBackgroundResource(R.drawable.tab_market_focus);
		tabImgMine.setBackgroundResource(R.drawable.tab_mine_normal);
		checkUpdate();
		super.onStart();
	}

	public void inits() {

		app = (GBApplication) getApplication();
		baseHandler = new BaseResponseHandler(this);
		initAdapter = new InitializeAdapter(this, 1);
		marketAdapter = new MarketAdapter(this, 2);
		marketDataMap = new HashMap<String, Button>();
		mineDataMap = new HashMap<String, Button>();
		packageTreeMap = new TreeMap<Integer, String>();
		mineEntityList = new ArrayList<MarketPackageEntity>();
		marketAssociateEntityList = new ArrayList<MarketPackageEntity>();
		marketUnAssociateEntityList = new ArrayList<MarketPackageEntity>();
		packageViewList = new ArrayList<View>();

		PAGE_FLAG = 0;

		lltMarketContent = new LinearLayout(this);
		lltMarketContent.setOrientation(LinearLayout.VERTICAL);
		lltMineContent = new LinearLayout(this);
		lltMineContent.setOrientation(LinearLayout.VERTICAL);

		lltDataInfo = (LinearLayout) findViewById(R.id.llt_data_info);
		tabImgMarket = (ImageView) findViewById(R.id.img_tab_market);
		tabImgMine = (ImageView) findViewById(R.id.img_tab_mine);
		txtTitle = (TextView) findViewById(R.id.txt_header_title);

		txtTitle.setText(R.string.market);
		lltDataInfo.removeAllViews();
		tabImgMarket.setOnClickListener(this);
		tabImgMine.setOnClickListener(this);
	}

	private boolean isInstall = false;

	@Override
	public void onClick(View v) {
		lltDataInfo.removeAllViews();
		switch (v.getId()) {
		case R.id.img_tab_market:
			isInstall = false;
			PAGE_FLAG = PAGE_MARKET;
			tabImgMarket.setBackgroundResource(R.drawable.tab_market_focus);
			tabImgMine.setBackgroundResource(R.drawable.tab_mine_normal);
			lltDataInfo.addView(lltMarketContent);
			break;
		case R.id.img_tab_mine:
			isInstall = true;
			PAGE_FLAG = PAGE_MINE;
			tabImgMarket.setBackgroundResource(R.drawable.tab_market_normal);
			tabImgMine.setBackgroundResource(R.drawable.tab_mine_press);
			ArrayList<MarketPackageEntity> list = marketAdapter.getMineList();
			ArrayList<MarketPackageEntity> finalList = new ArrayList<MarketPackageEntity>();

			for (int i = 0; i < mineEntityList.size(); i++) {
				for (MarketPackageEntity en2 : list) {
					MarketPackageEntity en1 = mineEntityList.get(i);
					if (en1.getCategory().equals(en2.getCategory())
							&& en1.getProductID().equals(en2.getProductID())) {
						list.remove(en2);
						break;
					}
				}
			}
			for (MarketPackageEntity en : mineEntityList) {
				finalList.add(en);
			}
			for (MarketPackageEntity en : list) {
				finalList.add(en);
			}
			System.out.println("mineEntityList     " + mineEntityList.size());
			System.out.println("list     " + list.size());
			lltMineContent = createMarketContentList(finalList, "");
			if(lltMineContent.getChildCount() == 1){
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				View view = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.empty_frame, null);
				((TextView) view.findViewById(R.id.emptytext))
				.setText(R.string.empty_market_mine);
				lltDataInfo.addView(view, params);
			}else{
				lltDataInfo.addView(lltMineContent);
			}
			break;
		}
	}

	@Override
	public void clickPositiveButton(int dialogId) {
		switch (dialogId) {
		case DialogRes.DIALOG_ID_NO_INVITATION:
			Intent intent = new Intent(this, Registration_updateActivity.class);
			intent.putExtra("isModify", true);
			startActivity(intent);
			finish();
			break;
		}
		super.clickPositiveButton(dialogId);
	}

	private class BtnUnInstallListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			MarketPackageEntity en = (MarketPackageEntity) v.getTag();
			String category = en.getCategory();
			String id = en.getProductID();
			MarketDeleteTask deleteTask = null;
			if ("1".equals(category)) {
				if ("1".equals(id)) {
					deleteTask = new MarketDeleteTask(en, delete_file, v);
					String file[] = { Constant.DRUG_AHFS };
					deleteTask.execute(file);
				}
			} else if ("2".equals(category)) {
				deleteTask = new MarketDeleteTask(en, delete_sql, v);
				deleteTask.execute(null);
			} else if ("3".equals(category)) {
				deleteTask = new MarketDeleteTask(en, delete_file, v);
				if ("1".equals(id)) {
					String file[] = { (Constant.DATA_PATH_MARKET_INSTALL + "/tumor.db") };
					deleteTask.execute(file);
				} else if ("2".equals(id)) {
					String file[] = { Constant.DATA_PATH_MARKET_INSTALL + "/ecg.db",
							Constant.DATA_PATH + "/ecg_img" };
					deleteTask.execute(file);
				} else if ("3".equals(id)) {
					String file[] = { (Constant.DATA_PATH_MARKET_INSTALL + "/nccn_disease.db") };
					deleteTask.execute(file);
				}
			}
		}

	}

	@Override
	public void setDownloadProgress(int progress, String title, int id) {
		String str = String.valueOf(id);
		String cat = str.substring(0, 1);
		String pID = str.substring(1);
		String ID = cat + "-" + pID;
		for (int i = 0; i < packageViewList.size(); i++) {
			LinearLayout llt = (LinearLayout) packageViewList.get(i);
			if (llt.getTag().toString().equals(ID)) {
				View view = llt.getChildAt(2);
				if (view instanceof ProgressBar) {
					ProgressBar pb = (ProgressBar) llt.getChildAt(2);
					pb.setProgress(progress);
				}

			}
		}

		super.setDownloadProgress(progress, title, id);
	}

	private class BtnOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			DownloadHandler handler = new DownloadHandler();
			final MarketPackageEntity en = (MarketPackageEntity) v.getTag();
			String cat = en.getCategory();
			String id = en.getProductID();
			int pId = Integer.parseInt(cat + id);
			
			if ("False".equalsIgnoreCase(en.getPermission())
					&& SharedPreferencesMgr.getInviteCode().equals("")) {
				showDialog(DialogRes.DIALOG_ID_NO_INVITATION);
			} else {
				DownloadAsyncTask1 task = new DownloadAsyncTask1(
						MarketActivity.this, pId, en.getUrl(), handler,
						new DownloadAsyncTask1.CallBack() {
							@Override
							public void onFinish(boolean b) {
								if (b) {
									String path = Constant.DATA_PATH_MARKET
											+ "/" + en.getCategory() + "/"
											+ en.getProductID();
									File file1 = new File(
											Constant.DATA_PATH_MARKET);
									File file2 = new File(
											Constant.DATA_PATH_MARKET + "/"
													+ en.getCategory());
									File file3 = new File(path);
									if (!file1.exists())
										file1.mkdir();
									if (!file2.exists())
										file2.mkdir();
									if (!file3.exists())
										file3.mkdir();
									MarketUpdateTask updateTask = new MarketUpdateTask(
											en);
									String pathArr[] = { path };
									updateTask.execute(pathArr);
								}
							}
						});
				task.execute(null);
				int lltItemid = Integer.parseInt(en.getCategory()
						+ en.getProductID());
				LinearLayout lltItem = (LinearLayout) MarketActivity.this
						.findViewById(lltItemid);
				if (PAGE_FLAG == PAGE_MARKET) {
					lltItem.removeViewAt(2);
					ProgressBar pb = new ProgressBar(MarketActivity.this, null,
							android.R.attr.progressBarStyleHorizontal);
					lltItem.addView(pb);
				} else {
					lltItem.removeViewAt(2);
					lltItem.removeViewAt(2);
					ProgressBar pb = new ProgressBar(MarketActivity.this, null,
							android.R.attr.progressBarStyleHorizontal);
					lltItem.addView(pb);
				}

			}
		}
	}

	private class ItemOnClickListener implements OnClickListener {
		private MarketPackageEntity en;

		public ItemOnClickListener(MarketPackageEntity en) {
			this.en = en;
		}

		@Override
		public void onClick(View v) {
			if (PAGE_FLAG == PAGE_MARKET) {
				MobclickAgent.onEvent(context, "market_detail",
						en.getCategory() + "-" + en.getProductID(), 1);
				app.setMarketEntity(en);
				Intent intent = new Intent(MarketActivity.this,
						MarketDetailInfoActivity.class);
				startActivity(intent);
			} else if (PAGE_FLAG == PAGE_MINE) {
				String category = en.getCategory();
				String id = en.getProductID();
				Intent intent = null;
				if ("1".equals(category)) {

				} else if ("2".equals(category)) {
					intent = new Intent(MarketActivity.this,
							TabCalcInfoActivity2.class);
					intent.putExtra("name", en.getTitle());
				} else if ("3".equals(category)) {
					if ("1".equals(id)) {
						intent = new Intent(MarketActivity.this,
								TumorActivity.class);
					} else if ("2".equals(id)) {
						intent = new Intent(MarketActivity.this,
								ECGListActivity.class);
					} else if ("3".equals(id)) {
						intent = new Intent(MarketActivity.this,
								NCCNListActivity.class);
					}
				}
				if (intent != null)
					startActivity(intent);
				System.out.println("ProductID     " + category + "-" + id);
			}
		}
	}

	private LinearLayout createMarketContentList(
			ArrayList<MarketPackageEntity> entityList, String panelTitle) {
		LinearLayout lltContent = new LinearLayout(this);
		lltContent.setOrientation(LinearLayout.VERTICAL);
		lltContent.setGravity(Gravity.CENTER_VERTICAL);

		TextView txtItemTitle = new TextView(this);
		txtItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txtItemTitle.setTextColor(Color.BLACK);
		txtItemTitle.getPaint().setFakeBoldText(true);
		txtItemTitle.setText(panelTitle);
		txtItemTitle.setGravity(Gravity.CENTER_HORIZONTAL);

		lltContent.addView(txtItemTitle);
		for (int i = 0; i < entityList.size(); i++) {
			MarketPackageEntity en = entityList.get(i);
			if (en.getVersion() != null) {
				marketAdapter.setServerVersion(en);
			}
			lltContent.addView(createMarketData(en));
		}

		return lltContent;
	}

	private class MarketDeleteTask extends AsyncTask<String, Void, Void> {
		private String action = null;
		private View view;
		private MarketPackageEntity en;

		public MarketDeleteTask(MarketPackageEntity en, String action, View v) {
			this.en = en;
			this.action = action;
			this.view = v;
		}

		@Override
		protected Void doInBackground(String... arg0) {
			if (delete_file.equals(action)) {
				for (String s : arg0) {
					Util.deleteDir(s);
				}
			} else if (delete_sql.equals(action)) {
				String uninstall = marketAdapter.getUninstallStr(en);
//				String dbName = marketAdapter.getDBName(en);
				String dbName = "jibo.db";
				if (dbName == null || uninstall == null) {
					return null;
				}
				System.out.println(dbName + "  ***  " + uninstall);
				SQLiteDatabase sdb = SQLiteDatabase.openOrCreateDatabase(
						Constant.DATA_PATH_GBADATA + "/" + dbName, null);
				if (sdb.isDbLockedByOtherThreads())
					return null;

				sdb.execSQL(uninstall);
				sdb.close();
			}
			marketAdapter.deleteProduct(en.getCategory() + "-"
					+ en.getProductID());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			removeDialog(DialogRes.DIALOG_ID_PACKAGE_UNINSTALLING);
			view.setEnabled(false);
			Intent intent = new Intent(MarketActivity.this,
					MarketActivity.class);
			startActivity(intent);
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			showDialog(DialogRes.DIALOG_ID_PACKAGE_UNINSTALLING);
			super.onPreExecute();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		inits();
		tabImgMarket.setBackgroundResource(R.drawable.tab_market_focus);
		tabImgMine.setBackgroundResource(R.drawable.tab_mine_normal);
		checkUpdate();
		super.onNewIntent(intent);
	}
	
	
	

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// ×¢²átip
		new Mask(this, null);
		TipHelper.registerTips(this, 1);
		TipHelper.runSegments(this);
		this.findViewById(R.id.closeTips).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						TipHelper.sign(false, true);
						TipHelper.disableTipViewOnScreenVisibility();
					}
		});
	}

	private class MarketUpdateTask extends AsyncTask<String, Void, Void> {
		File fileAll = null;
		File fileList[] = null;
		private AlertDialog.Builder ab;
		private AlertDialog ad;
		private MarketPackageEntity en;
		private String dataPath;

		public MarketUpdateTask(MarketPackageEntity en) {
			this.en = en;
			dataPath = Constant.DATA_PATH_MARKET + "/" + en.getCategory() + "/"
					+ en.getProductID();
			fileAll = new File(dataPath);
			fileList = fileAll.listFiles();
			for (File f : fileList) {
				String name = f.getName();
				if (name.endsWith(".zip")) {
					int index2 = name.lastIndexOf(".");
					int index1 = name.lastIndexOf("-");
					packageTreeMap.put(Integer.parseInt(name.substring(
							index1 + 1, index2)), f.getAbsolutePath());
				}
			}
		}

		@Override
		protected Void doInBackground(String... arg0) {
			String path = arg0[0];

			for (Entry e : packageTreeMap.entrySet()) {
				try {
					if (unzipForDownloadData(new File(e.getValue().toString()))) {
						fileList = fileAll.listFiles();
						for (int i = 0; i < fileList.length; i++) {
							File file = fileList[i];
							String fileName = file.getName();
							String filePath = file.getAbsolutePath();
							if (file.isDirectory()) {
								if ("apk".equalsIgnoreCase(fileName)) {

								} else if ("db".equalsIgnoreCase(fileName)) {
									File fileArr[] = file.listFiles();
									for (int j = 0; j < fileArr.length; j++) {
										File moveFile = fileArr[j];
										String moveFileName = moveFile
												.getName();
										moveMarketData(
												moveFile.getAbsolutePath(),
												moveFileName,
												Constant.DATA_PATH_MARKET_INSTALL);
									}
								} else if ("file".equalsIgnoreCase(fileName)) {
									File fileArr[] = file.listFiles();
									for (int j = 0; j < fileArr.length; j++) {
										File moveFile = fileArr[j];
										String moveFileName = moveFile
												.getName();
										if (moveFile.isDirectory()) {
											File fileFolder = new File(
													Constant.DATA_PATH + "/"
															+ moveFileName);
											System.out.println("path    "
													+ Constant.DATA_PATH + "/"
													+ moveFileName);
											if (!fileFolder.exists()) {
												boolean isCreated = fileFolder
														.mkdir();
												System.out
														.println("isCreated    "
																+ isCreated
																+ "=="
																+ fileFolder
																		.getAbsolutePath());
											}

											File moveFileArr[] = moveFile
													.listFiles();
											for (int k = 0; k < moveFileArr.length; k++) {
												moveMarketData(
														moveFileArr[k]
																.getAbsolutePath(),
														moveFileArr[k]
																.getName(),
														Constant.DATA_PATH
																+ "/"
																+ moveFileName);
											}
										} else {
											moveMarketData(
													moveFile.getAbsolutePath(),
													moveFileName,
													Constant.DATA_PATH);
										}
									}
								} else if ("script".equalsIgnoreCase(file
										.getName())) {
									File fileArr[] = file.listFiles();
									for (int j = 0; j < fileArr.length; j++) {
										updateDataInfo(fileArr[j]
												.getAbsolutePath());
									}
								}

							}

						}
						if (!Constant.isErrorInstall) {
							marketAdapter.setLocalVersion(en, en.getVersion());
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			int lltItemid = Integer.parseInt(en.getCategory()
					+ en.getProductID());
			LinearLayout lltItem = (LinearLayout) MarketActivity.this
					.findViewById(lltItemid);
			if (lltItem == null) {
				return;
			}
			if (!Constant.isErrorInstall) {
				lltItem.removeViewAt(2);
			}
			Button btn1 = new Button(MarketActivity.this);
			btn1.setBackgroundResource(R.drawable.pkg_action);
			btn1.setGravity(Gravity.CENTER);
			int action = marketAdapter.getMarketAction(en);
			if (action == MarketAdapter.BTN_ACTION_NOUPDATE) {
				btn1.setText(getString(R.string.pkg_installed));
				btn1.setEnabled(false);
				btn1.setVisibility(LinearLayout.GONE);
			} else if (action == MarketAdapter.BTN_ACTION_UPDATE) {
				btn1.setText(getString(R.string.pkg_to_upgrade));
			} else if (action == MarketAdapter.BTN_ACTION_INSTALL) {
				btn1.setText(getString(R.string.pkg_to_install));
			}
			if (PAGE_FLAG == PAGE_MINE) {
				// TODO
				lltItem.addView(btn1);
				Button btnUninstall = new Button(MarketActivity.this);

				btnUninstall.setTag(en);
				btnUninstall.setText(getString(R.string.pkg_to_uninstall));
				btnUninstall.setBackgroundResource(R.drawable.pkg_action);
				btnUninstall.setOnClickListener(new BtnUnInstallListener());
				btnUninstall.setGravity(Gravity.CENTER);
				lltItem.addView(btnUninstall);
			} else {

				lltItem.addView(btn1);
				lltItem.setVisibility(LinearLayout.GONE);
				marketAssociateEntityList.remove(en);
				marketUnAssociateEntityList.remove(en);
				if (lltMarketContent.getChildCount() > 1) {
					if (marketAssociateEntityList.size() == 0) {
						lltMarketContent.removeViewAt(0);
					} else if (marketUnAssociateEntityList.size() == 0) {
						lltMarketContent.removeViewAt(1);
					}
				} else {
					if (marketUnAssociateEntityList.size() == 0
							&& marketAssociateEntityList.size() == 0) {
						lltMarketContent.removeViewAt(0);
					}
				}

			}

			ad.cancel();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			try {
				if (ab == null) {
					ab = new AlertDialog.Builder(MarketActivity.this);
					ab.setMessage(getResources().getString(R.string.install_tip));
					ab.setOnKeyListener(new DialogInterface.OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {
							return false;
						}
					});
					ad = ab.create();
					ad.show();
				}
			} catch (Exception e) {

			}

			super.onPreExecute();
		}

		private void updateDataInfo(String path) {
			SQLiteDatabase sdb = SQLiteDatabase
					.openOrCreateDatabase(path, null);
			String sql = "select * from update_script";
			Cursor cursor = sdb.rawQuery(sql, null);
			try {
				while (cursor.moveToNext()) {
					String dbName = "jibo.db";
					String install_sql = cursor.getString(1);
					String uninstall_sql = cursor.getString(2);
					String version = cursor.getString(3);
					Log.e("xinxi-tools.......:", dbName + "," + install_sql
							+ "," + uninstall_sql + "," + version);
					SQLiteDatabase targetDB = SQLiteDatabase
							.openOrCreateDatabase(Constant.DATA_PATH_GBADATA
									+ "/" + dbName, null);
					Constant.isErrorInstall = false;
					marketAdapter.updateUnInstallSQL(en, dbName, uninstall_sql);
					if (!Constant.isErrorInstall) {
						targetDB.execSQL(install_sql);
						marketAdapter.setLocalVersion(en, version);
					}

					targetDB.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			cursor.close();
			sdb.close();
			File file = new File(path);
			if (file.exists())
				file.delete();
		}

		private boolean moveMarketData(String filePath, String fileName,
				String targetPath) {
			FileInputStream is1 = null;
			FileOutputStream fos = null;
			File file = new File(targetPath);
			if (file.exists() && file.isFile())
				file.delete();
			try {
				is1 = new FileInputStream(filePath);
				fos = new FileOutputStream(targetPath + File.separator
						+ fileName);
				byte[] buffer = new byte[10240];
				int count = 0;
				while ((count = is1.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null)
						fos.close();
					if (is1 != null)
						is1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
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

	private LinearLayout createMarketData(MarketPackageEntity en) {

		LinearLayout lltItem = new LinearLayout(this);
		lltItem.setOnClickListener(new ItemOnClickListener(en));
		lltItem.setPadding(10, 5, 10, 0);
		lltItem.setOrientation(LinearLayout.HORIZONTAL);
		lltItem.setGravity(Gravity.CENTER_VERTICAL);
		int lltItemid = Integer.parseInt(en.getCategory() + en.getProductID());
		lltItem.setId(lltItemid);
		LayoutParams lpInfo = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, 1);
		ImageView imgIcon = new ImageView(this);

		if ("1".equals(en.getCategory())) {
			imgIcon.setBackgroundResource(R.drawable.market_drug);
		} else if ("2".equals(en.getCategory())) {
			imgIcon.setBackgroundResource(R.drawable.market_calc);
		} else if ("3".equals(en.getCategory())) {
			imgIcon.setBackgroundResource(R.drawable.market_tool);
		}

		LinearLayout lltInfoPanel = new LinearLayout(this);
		lltInfoPanel.setOrientation(LinearLayout.VERTICAL);
		lltInfoPanel.setGravity(Gravity.CENTER_VERTICAL);

		TextView txtTitle = new TextView(this);
		txtTitle.setTextColor(Color.BLACK);
		txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
		txtTitle.setText(en.getTitle());
		LinearLayout lltDCRate = new LinearLayout(this);
		lltDCRate.setOrientation(LinearLayout.HORIZONTAL);
		lltDCRate.setGravity(Gravity.CENTER_VERTICAL);

		TextView txtDownload = new TextView(this);
		txtDownload.setGravity(Gravity.CENTER_VERTICAL);
		txtDownload.setText(en.getDownloadCount()
				+ getString(R.string.download_count));
		txtDownload.setTextColor(Color.BLACK);
		// RatingBar rb = new RatingBar(this);
		// rb.setMax(10);
		// int progress = 0;
		// if (!"".equals(en.getRates())) {
		// String rate = en.getRates();
		// if(rate==null||rate.equals("")||rate.equals("null")) rate="0";
		// progress = Integer.parseInt(rate);
		// }
		// rb.setProgress(progress);

		// lltDCRate.addView(txtDownload);
		// lltDCRate.addView(rb);

		lltInfoPanel.addView(txtTitle);
		lltInfoPanel.addView(lltDCRate);

		Button btnDownload = new Button(this);
		btnDownload.setId(100);
		btnDownload.setOnClickListener(new BtnOnClickListener());
		btnDownload.setBackgroundResource(R.drawable.pkg_action);
		btnDownload.setTag(en);
		btnDownload.setGravity(Gravity.CENTER);

		marketDataMap.put(en.getProductID(), btnDownload);
		if (en.getType() == null) {
			btnDownload.setText(getString(R.string.pkg_installed));
			btnDownload.setEnabled(false);
			btnDownload.setVisibility(LinearLayout.GONE);
		} else if (en.getType().equals("full") && !isInstall) {
			btnDownload.setText(getString(R.string.pkg_to_install));
		} else if (en.getType().equals("diff")) {
			btnDownload.setText(getString(R.string.pkg_to_upgrade));
		} else if (en.getType().equals("full") && isInstall) {
			btnDownload.setVisibility(LinearLayout.GONE);
		}


		Button btnUninstall = new Button(this);
		btnUninstall.setGravity(Gravity.CENTER);
		btnUninstall.setOnClickListener(new BtnUnInstallListener());
		btnUninstall.setTag(en);
		btnUninstall.setText(getString(R.string.pkg_to_uninstall));
		btnUninstall.setBackgroundResource(R.drawable.pkg_action);

		lltItem.addView(imgIcon);
		lltItem.addView(lltInfoPanel, lpInfo);
		lltItem.addView(btnDownload);
		if (PAGE_FLAG == PAGE_MARKET) {

		} else if (PAGE_FLAG == PAGE_MINE) {
			lltItem.addView(btnUninstall);
		}

		packageViewList.add(lltItem);
		lltItem.setTag(en.getCategory() + "-" + en.getProductID());
		return lltItem;
	}

	public static String dataInfo;

	private void checkUpdate() {
		String androidId = Settings.System.getString(getContentResolver(),
				Settings.Secure.ANDROID_ID);
		dataInfo = marketAdapter.getMarketDataInfo();
		if (dataInfo == null || dataInfo.equals("")) {
			dataInfo = "0-0-0";
		}
		Properties pro = new Properties();
		pro.put(SoapRes.KEY_MARKET_VERSION, initAdapter.getCurrentVerName());
		pro.put(SoapRes.KEY_MARKET_DEVICE_ID, androidId);
		pro.put(SoapRes.KEY_MARKET_DATA, dataInfo);
		Log.i("simon", "dataInfo    " + dataInfo);
		System.out.println("dataInfo    " + dataInfo);

		sendRequest(SoapRes.URLCustomer, SoapRes.REQ_ID_MARKET, pro,
				baseHandler);
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			if (o instanceof MarketPaserAdapter) {
				app.setMarketHasUpdated(true);
				MarketPaserAdapter adapter = (MarketPaserAdapter) o;
				marketAssociateEntityList = adapter
						.getMarketAssociateEntityList();
				marketUnAssociateEntityList = adapter
						.getMarketUnAssociateEntityList();
				mineEntityList = adapter.getMineEntityList();

				if (marketAssociateEntityList.size() > 0) {
					lltMarketContent.addView(createMarketContentList(
							marketAssociateEntityList,
							getString(R.string.market_associate)));
				}

				if (marketUnAssociateEntityList.size() > 0) {
					lltMarketContent.addView(createMarketContentList(
							marketUnAssociateEntityList,
							getString(R.string.market_unassociate)));
				}
				lltDataInfo.removeView(lltMarketContent);
				lltDataInfo.addView(lltMarketContent);
				
			}
		}
		super.onReqResponse(o, methodId);
	}

	private class DownloadHandler extends AsyncSoapResponseHandler {
		@Override
		public void onSuccess(Object content) {

			super.onSuccess(content);
		}
	}

}
