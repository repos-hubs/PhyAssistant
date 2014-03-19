package com.jibo.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.Constant;

public class UpdateDataActivity extends BaseSearchActivity {
	private LinearLayout lltDataList;
	private GBApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.update_data);
		super.onCreate(savedInstanceState);
		inits();
		unzipPackage();
		generateItemList();
	}
	
	public void inits() {
		app = (GBApplication) getApplication();
		lltDataList = (LinearLayout) findViewById(R.id.llt_data_list);
	}
	
	public void generateItemList() {
		File file = new File(Constant.DPT_TMP_PATH);
		File fileList[] = file.listFiles();
		for(File f:fileList) {
			LinearLayout lltItem = new LinearLayout(this);
			LayoutParams lpTitle = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LayoutParams lpStatus = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			TextView txtTitle = new TextView(this);
			TextView txtStatus = new TextView(this);
			
			lpTitle.weight = 1;
			String dirName = f.getName();
			if("drug".equals(dirName)) {
				
			} else if("tool".equals(dirName)) {
				
			}
			lltItem.addView(txtTitle, lpTitle);
			lltItem.addView(txtStatus, lpStatus);
		}
	}
	
	public void unzipPackage() {
		File file = new File(Constant.DPT_TMP_PATH);
		File fileList[] = file.listFiles();
		for(File f:fileList) {
			try {
				unzipForDownloadData(f.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void unzipForDownloadData(String filePath) throws Exception {
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(filePath);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		String zipFileName = "";
		String dataPath = Constant.DPT_TMP_PATH;
		while ((entry = zis.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				String name = entry.getName();
				name = name.substring(0, name.length() - 1);
				File file = new File(dataPath+"/"+name);
				file.mkdir();
			} else {
				int count = 0;
				final int BUFFER = 4096;
				byte data[] = new byte[BUFFER];
				zipFileName = dataPath +File.separator + entry.getName();
				FileOutputStream fos = null;
				String dbName = entry.getName().substring(entry.getName().lastIndexOf("/")+1);
				fos = new FileOutputStream(dataPath +File.separator + entry.getName());
				
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
		File file = new File(filePath);
		if (file != null && file.exists()) {
			file.delete();
		}
	}
	
	public boolean drugUpdateExecute(String path) {
		boolean result = false;
		try {
			result = true;
		} catch(Exception e) {
			result = false;
		}
		return result;
	}
	
	public boolean toolUpdateExecute(String path) {
		boolean result = false;
		try {
			result = true;
		} catch(Exception e) {
			result = false;
		}
		return result;
	}
}
