package com.jibo.asynctask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.jibo.activity.BaseActivity;
import com.jibo.common.Constant;

/**
 * �ϵ�����
 * @description 
 * @author will
 * @create 2013-3-7 ����1:19:30
 */
public class DownloadBreakpointAsyncTask extends AsyncTask<Void, Long, Boolean> {
	private Context mContext;
	private long packageLength;
	private long downloadBytes;
	private int id;
	private String dataPath;
	private String outputFileName;
	private String downloadUrl;
	private File outputFile;
	public static int s_downLoadID = 0x123;
	public CallBack cb;
	public boolean s_canDownload;
	public static String app_name;

	public DownloadBreakpointAsyncTask(Context mContext, int threadID, String url,CallBack callBack,boolean isInitData) {
		this.mContext = mContext;
		this.outputFileName = url.substring(url.lastIndexOf("/") + 1);
		this.downloadUrl = url;
		this.id = threadID;
		this.cb = callBack;
		s_canDownload = true;
		dataPath = Environment.getExternalStorageDirectory().toString();
		File file = new File(Constant.DATA_PATH);
		file.mkdir();
		File dbFile = new File(Constant.DATA_PATH_GBADATA);
		 if (dbFile.exists())
			 dbFile.delete();
		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ outputFileName);
		if (f.exists())
			f.delete();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		File file = new File(outputFile.getAbsolutePath());
		if (file != null && file.exists()) {
			file.delete();
		}
		cb.onFinish(result);
	}

	@Override
	protected void onProgressUpdate(Long... values) {
		if (mContext instanceof BaseActivity) {
			((BaseActivity) mContext).setDownloadProgress(
					(int) (downloadBytes * 100 / packageLength),
					outputFileName, id);
		}
		super.onProgressUpdate(values);
	}

	public void downloadFile() throws Exception {
		outputFile = new File(dataPath + "/" + outputFileName);
		downloadBytes = outputFile.length();
		app_name = outputFileName;
		RandomAccessFile outputStream = null;
		InputStream inputStream = null;
		try {
			outputStream = new RandomAccessFile(outputFile, "rw");
			if (outputStream != null) {
				outputStream.seek(downloadBytes);
			}

			URL url = new URL(downloadUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty("RANGE", "bytes=" + downloadBytes
					+ "-");
			packageLength = connection.getContentLength() + downloadBytes;

			try {
				inputStream = connection.getInputStream();
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException  **&&&&");
			}
			
			byte[] buffer = new byte[1024];
			int hasRead = 0;
			while ((inputStream != null)
					&& ((hasRead = inputStream.read(buffer)) != -1)
					&& s_canDownload) {
				outputStream.write(buffer, 0, hasRead);
				downloadBytes = downloadBytes + hasRead;
				publishProgress(downloadBytes);
			}

		} finally {
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
		}
	}

	private void unzip() {
		try {
			ZipFile zf = new ZipFile(outputFile.getAbsolutePath());
			zf.extractAll(dataPath);
		} catch (ZipException e) {
			e.printStackTrace();
		} finally {
			outputFile.delete();
		}

	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			downloadFile();
			unzip();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public interface CallBack {
		void onFinish(boolean b);
	}
}
