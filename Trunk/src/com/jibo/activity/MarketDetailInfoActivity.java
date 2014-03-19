package com.jibo.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.adapter.ImageAdapter;
import com.jibo.asynctask.DownloadAsyncTask1;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.MarketPackageEntity;
import com.jibo.dbhelper.MarketAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.ui.ScrollViewGallery;

public class MarketDetailInfoActivity extends BaseSearchActivity implements OnClickListener{
	private GBApplication app;
	private TextView txtTitle;
	private TextView txtIntro;
	private TextView txtDownloads;
	private ScrollViewGallery glrPreview;
	private ImageView imgIcon;
	private Button btnAction;
	private MarketPackageEntity en;
	private MarketAdapter marketAdapter;
	private TreeMap<Integer, String> packageTreeMap;
	private ProgressBar pb;
	private LinearLayout lltItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_market_info);
		super.onCreate(savedInstanceState);
		inits();
	}

	private void inits() {
		app = (GBApplication) getApplication();
		en = app.getMarketEntity();
		packageTreeMap = new TreeMap<Integer, String>();
		marketAdapter = new MarketAdapter(this, 2);
		
		txtTitle = (TextView) findViewById(R.id.txt_title);
		txtIntro = (TextView) findViewById(R.id.txt_intro);
		txtDownloads = (TextView) findViewById(R.id.txt_downloads);
		glrPreview = (ScrollViewGallery) findViewById(R.id.glr_preview);
		imgIcon = (ImageView) findViewById(R.id.img_icon);
		btnAction = (Button) findViewById(R.id.btn_action);
		lltItem = (LinearLayout) this.findViewById(R.id.llt_title_panel);

		btnAction.setOnClickListener(this);
		txtTitle.setText(en.getTitle());
		if(en.getTitle().equals(getResources().getString(R.string.ahfs_title))){
			txtIntro.setText(getResources().getString(R.string.ahfs_info));
		}else{
			txtIntro.setText(en.getIntro());
		}
		txtDownloads.setText(en.getDownloadCount());
		txtDownloads.setVisibility(LinearLayout.GONE);
		// TODO
		if (en.getIcon()!=null&&!en.getIcon().equals("")) {
			if ("1".equals(en.getCategory())) {
				imgIcon.setBackgroundResource(R.drawable.market_drug);
			} else if ("2".equals(en.getCategory())) {
				imgIcon.setBackgroundResource(R.drawable.market_calc);
			} else if ("3".equals(en.getCategory())) {
				imgIcon.setBackgroundResource(R.drawable.market_tool);
			}
		}
		ArrayList<String> imgList = en.getImgList();
		if(imgList!=null) {
			String img[] = new String[en.getImgList().size()];
			for(int i=0; i<en.getImgList().size(); i++) {
				String url = en.getImgList().get(i);
				img[i] = url;
			}
			DownloadImageTask imgTask = new DownloadImageTask();
			imgTask.execute(img);
		}
		
	}

	private class DownloadImageTask extends AsyncTask<String, Void, ArrayList<Drawable>> {
		ArrayList<Drawable> drawableList;
		
		protected ArrayList<Drawable> doInBackground(String... urls) {
			drawableList = new ArrayList<Drawable>();
			for(int i=0; i<urls.length; i++) {
				drawableList.add(loadImageFromNetwork(urls[i]));
			}
			return drawableList;
		}

		protected void onPostExecute(ArrayList<Drawable> result) {
			if(result.size()==en.getImgList().size()) {
				ImageAdapter imgAdapter = new ImageAdapter(MarketDetailInfoActivity.this, drawableList);
				glrPreview.setAdapter(imgAdapter);
			}
		}

		private Drawable loadImageFromNetwork(String imageUrl) {
			Drawable drawable = null;
			try {
				drawable = Drawable.createFromStream(
				new URL(imageUrl).openStream(), "image.gif");
			} catch (IOException e) {
			}
			return drawable;
		}
	}

	@Override
	public void onClick(View v) {
		DownloadHandler handler = new DownloadHandler();
		if("False".equalsIgnoreCase(en.getPermission())&&SharedPreferencesMgr.getInviteCode().equals("")) {
			showDialog(DialogRes.DIALOG_ID_NO_INVITATION);
		} else {
			DownloadAsyncTask1 task = new DownloadAsyncTask1(
					this, 0, en.getUrl(), handler,
					new DownloadAsyncTask1.CallBack() {
						@Override
						public void onFinish(boolean b) {
							if (b) {
								String path = Constant.DATA_PATH_MARKET+"/"+en.getCategory()+"/"+en.getProductID();
								File file1 = new File(Constant.DATA_PATH_MARKET);
								File file2 = new File(Constant.DATA_PATH_MARKET+"/"+en.getCategory());
								File file3 = new File(path);
								if (!file1.exists())
									file1.mkdir();
								if (!file2.exists())
									file2.mkdir();
								if (!file3.exists())
									file3.mkdir();
								MarketUpdateTask updateTask = new MarketUpdateTask(en);
								String pathArr[] = { path };
								updateTask.execute(pathArr);
							}
						}
					});
			task.execute(null);
			int lltItemid = Integer.parseInt(en.getCategory()+en.getProductID());
			
			lltItem.removeViewAt(2);
			pb = new ProgressBar(this);
			lltItem.addView(pb);
		}
		
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
					packageTreeMap.put(Integer.parseInt(name.substring(index1+1, index2)),
							f.getAbsolutePath());
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
										String moveFileName = moveFile.getName();
										moveMarketData(moveFile.getAbsolutePath(), moveFileName,Constant.DATA_PATH_GBADATA);
									}
								} else if ("file".equalsIgnoreCase(fileName)) {
									File fileArr[] = file.listFiles();
									for (int j = 0; j < fileArr.length; j++) {
										File moveFile = fileArr[j];
										String moveFileName = moveFile.getName();
										if (moveFile.isDirectory()) {
											File fileFolder = new File(Constant.DATA_PATH + "/"+ moveFileName);
											System.out.println("path    "+Constant.DATA_PATH + "/"+ moveFileName);
											if (!fileFolder.exists()) {
												boolean isCreated = fileFolder.mkdir();
												System.out.println("isCreated    "+isCreated+"=="+fileFolder.getAbsolutePath());
											}
												
											File moveFileArr[] = moveFile.listFiles();
											for (int k = 0; k < moveFileArr.length; k++) {
												moveMarketData(moveFileArr[k].getAbsolutePath(),moveFileArr[k].getName(),Constant.DATA_PATH + "/"+ moveFileName);
											}
										} else {
											moveMarketData(moveFile.getAbsolutePath(),moveFileName,Constant.DATA_PATH);
										}
									}
								} else if ("script".equalsIgnoreCase(file.getName())) {
									File fileArr[] = file.listFiles();
									for(int j=0; j<fileArr.length; j++) {
										updateDataInfo(fileArr[j].getAbsolutePath());
									}
								}

							}

						}
						
						marketAdapter.setLocalVersion(en, en.getVersion());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			btnAction.setText(getResources().getString(R.string.install_done));
			ad.cancel();
			lltItem.removeViewAt(2);
			lltItem.addView(btnAction);
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			try {
				if (ab == null) {
					ab = new AlertDialog.Builder(MarketDetailInfoActivity.this);
					ab.setMessage(getResources().getString(R.string.install_tip));
					ab.setOnKeyListener(new DialogInterface.OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode,
								KeyEvent event) {
							return false;
						}
					});
					ad = ab.create();
					ad.show();
				}
			} catch(Exception e) {
				
			}

			super.onPreExecute();
		}

		private void updateDataInfo(String path) {
			SQLiteDatabase sdb = SQLiteDatabase.openOrCreateDatabase(path, null);
			String sql = "select * from update_script";
			Cursor cursor = sdb.rawQuery(sql, null);
			try {
				while(cursor.moveToNext()) {
					String dbName = cursor.getString(0);
					String install_sql = cursor.getString(1);
					String uninstall_sql = cursor.getString(2);
					String version = cursor.getString(3);
					SQLiteDatabase targetDB = SQLiteDatabase.openOrCreateDatabase(Constant.DATA_PATH_GBADATA+"/"+dbName, null);
					
					marketAdapter.updateUnInstallSQL(en, dbName, uninstall_sql);
					targetDB.execSQL(install_sql);
					marketAdapter.setLocalVersion(en, version);
					
					targetDB.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			
			cursor.close();
			sdb.close();
			File file = new File(path);
			if(file.exists()) file.delete();
		}
		
		private boolean moveMarketData(String filePath, String fileName,
				String targetPath) {
			FileInputStream is1 = null;
			FileOutputStream fos = null;
			File file = new File(targetPath);
			if (file.exists()&&file.isFile())
				file.delete();
			try {
				is1 = new FileInputStream(filePath);
				fos = new FileOutputStream(targetPath + File.separator + fileName);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is1.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
			} catch (SecurityException e) {
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			} finally {
				try {
					if (fos != null)
						fos.close();
					if (is1 != null)
						is1.close();
				} catch (IOException e) {
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
	
	private class DownloadHandler extends AsyncSoapResponseHandler {
		@Override
		public void onSuccess(Object content) {

			super.onSuccess(content);
		}
	}
	
	
}
