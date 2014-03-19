package com.jibo.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.api.android.GBApp.R;
import com.jibo.asynctask.DownloadAsyncTask1;
import com.jibo.common.Util;
import com.jibo.data.GetCategoryUpdatePaser;
import com.jibo.dbhelper.InitializeAdapter;
import com.jibo.dbhelper.PckgUpdateAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.update.UpdatePackageFactory;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class GBMarket extends BaseSearchActivity {
	private PckgUpdateAdapter pckgAdapter;
	private InitializeAdapter initializeAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void inits() {
		initializeAdapter = new InitializeAdapter(this, 1);
		pckgAdapter = new PckgUpdateAdapter(this, 1);
	}
	
	private class UpdateDBHandler extends AsyncSoapResponseHandler {
		@Override
		public void onFailure(Throwable error, String content) {
			System.out.println("error   " + error.toString());
			super.onFailure(error, content);
		}

		@Override
		public void onSuccess(Object content) {
			if (content != null && content instanceof GetCategoryUpdatePaser) {
				GetCategoryUpdatePaser paser = (GetCategoryUpdatePaser) content;
				final ArrayList<HashMap<String, String>> mapList = paser
						.getMapList();
				String url = paser.getUrl();
				UpdateDBHandler handler = new UpdateDBHandler();
				if (!"anyType{}".equals(url) && !"".equals(url) && url != null) {
					DownloadAsyncTask1 task = new DownloadAsyncTask1(
							GBMarket.this, 0, url, handler,
							new DownloadAsyncTask1.CallBack() {
								@Override
								public void onFinish(boolean b) {

									UpdateDBTask2 task2 = new UpdateDBTask2(
											mapList);
									task2.execute(null);
								}
							});
					task.execute(null);
				}

			}
			super.onSuccess(content);
		}
	}
	
	public void checkMarketUpdate() {
		UpdateDBHandler hand = new UpdateDBHandler();
		Properties pro = new Properties();
		String categoryStr = pckgAdapter.getCategroyStr();
		String str = "";
		if ("".equals(categoryStr)) {
			str = "version=" + initializeAdapter.getCurrentVerName();
		} else {
			str = "version=" + initializeAdapter.getCurrentVerName()
					+ "&" + categoryStr;
		}
		str = "version=1.12&cate=1|2&cate=2|3&cate=3|2&cate=4|1&cate=5|1";
//		System.out.println("strstrstr    &бнбн&*  "+str);
//		pro.setProperty(SoapRes.KEY_GET_CATEGORY_STR, str);
//		GBApplication.gbapp.soapClient.sendRequest(SoapRes.URLCustomer,
//				SoapRes.REQ_ID_GET_CATEGORY_UPDATE, pro, hand,
//				HomePageActivity.this);
	}
	
	private class UpdateDBTask2 extends AsyncTask<Integer, Integer, Integer> {
		private File file;
		private File fileList[];
		String dataPath = Environment.getExternalStorageDirectory()
				+ "/Jibo/update_tmp";
		private HashMap<String, Boolean> updateProcessMap;
		private AlertDialog ad;
		private LinearLayout lltMain;
		private Button btnConfirm;
		private ArrayList<HashMap<String, String>> mapList;

		public UpdateDBTask2(ArrayList<HashMap<String, String>> mapList) {
			file = new File(dataPath);
			fileList = file.listFiles();
			this.mapList = mapList;
		}

		@Override
		protected void onPreExecute() {
			AlertDialog.Builder builder = new Builder(GBMarket.this);
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
			lltMain = new LinearLayout(GBMarket.this);
			lltMain.setOrientation(LinearLayout.VERTICAL);
			lltMain.setGravity(Gravity.CENTER);
			for (File f : fileList) {
				String fileName = f.getName().substring(0,
						f.getName().length() - 4);
				LinearLayout lltItem = new LinearLayout(GBMarket.this);
				lltItem.setPadding(10, 7, 10, 7);
				lltItem.setOrientation(LinearLayout.HORIZONTAL);
				TextView txtTitle = new TextView(GBMarket.this);
				TextView txtStatus = new TextView(GBMarket.this);

				txtTitle.setText(UpdatePackageFactory.getPckgName(fileName));
				lltItem.setId(UpdatePackageFactory.getPackageID(fileName));
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
			btnConfirm = new Button(GBMarket.this);
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
				updateProcessMap.put(
						fileName,
						UpdatePackageFactory.updateData(fileName, dataPath
								+ "/" + f.getPath()));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			System.out.println("onPostExecute1");
			if (fileList.length == updateProcessMap.size()) {
				System.out.println("onPostExecute2");
				for (Entry en : updateProcessMap.entrySet()) {
					LinearLayout lltItem = (LinearLayout) lltMain
							.findViewById(UpdatePackageFactory.getPackageID(en
									.getKey().toString()));
					TextView txt = (TextView) lltItem.getChildAt(1);
					if ((Boolean) en.getValue()) {
						txt.setText(R.string.pckg_install_success);
					} else {
						txt.setText(R.string.pckg_install_failure);
					}
				}
				for (HashMap<String, String> map : mapList) {
					for (Entry en : map.entrySet()) {
						System.out.println("updatePckgVersion    "
								+ en.getKey() + "   **&&  " + en.getValue());
						pckgAdapter.updatePckgVersion(en.getKey().toString(),
								en.getValue().toString());
					}
				}
				btnConfirm.setEnabled(true);
				Util.deleteDir(dataPath);
			}
			super.onPostExecute(result);
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
}
