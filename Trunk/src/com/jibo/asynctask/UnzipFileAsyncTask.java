package com.jibo.asynctask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import android.content.Context;
import android.os.AsyncTask;

import com.jibo.common.Constant;
import com.jibo.common.Util;
import com.jibo.dbhelper.InitializeAdapter;

public class UnzipFileAsyncTask extends AsyncTask<String, String, String> {
	private UnzipCallBack cb;
	private Context ctx;

	public UnzipFileAsyncTask(Context ctx, UnzipCallBack cb) {
		this.ctx = ctx;
		this.cb = cb;
	}

	@Override
	protected String doInBackground(String... arg0) {
		String taskResult = "";
		if (unzipFile())
			taskResult = "success";
		return taskResult;
	}

	@Override
	protected void onPostExecute(String result) {
		System.out.println("onPostExecute   taskResult   " + result);
		if ("success".equals(result)) {
			cb.onFinish(true);
		} else {
			cb.onFinish(false);
		}
		super.onPostExecute(result);
	}

	public interface UnzipCallBack {
		void onFinish(boolean b);
	}

	public boolean unzipFile() {
		boolean result = false;
		File file = new File(Constant.DATA_PATH);
		if(!file.exists())file.mkdirs();
		File fileList[] = file.listFiles();
		if (null != fileList) {
			for (File f : fileList) {
				String zipName = f.getAbsolutePath();
				String fName = f.getName();
				Pattern p = Pattern
						.compile("^GBApp_(?:test_)?db_(\\d\\.\\d+(\\.\\d+)?)\\.zip$");
				Matcher m = p.matcher(fName);
				if (m.matches()) {
					try {
						if (m.group(1).equals(Util.errorHandle2_0_2(Util.getCurrentVerName(ctx)))) {//°æ±¾ÕýÈ·
							unzipForDownloadData(zipName);
							result = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}
	
	
	private void unzipForDownloadData(String filePath) throws Exception {
		File outputFile = new File(filePath);
		try {
			ZipFile zf = new ZipFile(outputFile.getAbsolutePath());
			zf.extractAll(Constant.DATA_PATH);
		} catch (ZipException e) {
			e.printStackTrace();
		} finally {
			if (outputFile != null && outputFile.exists()) {
				outputFile.delete();
			}
		}
	}

//	private void unzipForDownloadData(String filePath) throws Exception {
//		BufferedOutputStream dest = null;
//		File outputFile = new File(filePath);
//		if (!outputFile.exists())
//			return;
//		FileInputStream fis = new FileInputStream(outputFile);
//		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
//		ZipEntry entry;
//		String zipFileName = "";
//		String path = Constant.DATA_PATH;
//		while ((entry = zis.getNextEntry()) != null) {
//			if (entry.isDirectory()) {
//				String name = entry.getName();
//				name = name.substring(0, name.length() - 1);
//				File file = new File(path + "/" + name);
//				file.mkdir();
//			} else {
//				int count = 0;
//				final int BUFFER = 4096;
//				byte data[] = new byte[BUFFER];
//				zipFileName = path + File.separator + entry.getName();
//				FileOutputStream fos = null;
//				String dbName = entry.getName().substring(
//						entry.getName().lastIndexOf("/") + 1);
//				fos = new FileOutputStream(path + File.separator
//						+ entry.getName());
//
//				dest = new BufferedOutputStream(fos, BUFFER);
//				while ((count = zis.read(data, 0, BUFFER)) != -1) {
//					dest.write(data, 0, count);
//				}
//				dest.flush();
//				dest.close();
//				fos.close();
//			}
//		}
//		zis.close();
//		if (outputFile != null && outputFile.exists()) {
//			outputFile.delete();
//		}
//	}
}
