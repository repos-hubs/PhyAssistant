package com.jibo.asynctask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.jibo.activity.BaseActivity;
import com.jibo.app.research.PaperDetailActivity;
import com.jibo.app.research.CollectionActivity.ContextInfo;
import com.jibo.base.dynaImage.FileUtil;
import com.jibo.common.Constant;
import com.jibo.common.FileManager;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.dbhelper.PaperDownloadAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.util.Logs;

/**
 * 下载and初始化应用程序所需db和files
 * 
 * @author Refeal
 * 
 */
public class DownloadAsyncTask1 extends AsyncTask<Void, Long, Boolean> {
	private Context mContext;
	public static Context collectionContext = null;
	public static ContextInfo contextInfo = null;
	private long packageLength;
	private long downloadBytes;
	private int id;
	private String dataPath;
	private String outputFileName;
	private String downloadUrl = "";
	private File outputFile;
	public static int s_downLoadID = 0x123;
	public CallBack cb;
	private NotificationManager notificationManager;
	private Notification notification;
	public boolean s_canDownload;
	public static String app_name;
	private String paperID;

	public DownloadAsyncTask1(Context mContext, int threadID, String url,
			AsyncSoapResponseHandler handler, CallBack callBack) {
		this.mContext = mContext;
		this.outputFileName = url.substring(url.lastIndexOf("/") + 1);
		this.downloadUrl = url;
		this.id = threadID;
		this.cb = callBack;
		s_canDownload = true;
		dataPath = Environment.getExternalStorageDirectory().toString();
		File file1 = new File(Constant.DATA_PATH);
		File file2 = new File(Constant.DRUG_AHFS);// AHFS文件夹
		File file3 = new File(Environment.getExternalStorageDirectory()
				+ "/ahfs.zip");// AHFS数据
		File file4 = new File(Constant.DATA_PATH_MARKET);// 工具市场文件夹
		File file5 = new File(Constant.DATA_PATH_MARKET + "/1");
		File file6 = new File(Constant.DATA_PATH_MARKET + "/2");
		File file7 = new File(Constant.DATA_PATH_MARKET + "/3");
		// if (file1.exists())
		// file1.delete();
		file1.mkdir();
		if (!file2.exists())
			file2.mkdir();
		if (file3.exists())
			file3.delete();
		if (!file4.exists())
			file4.mkdir();
		if (!file5.exists())
			file5.mkdir();
		if (!file6.exists())
			file6.mkdir();
		if (!file7.exists())
			file7.mkdir();
		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ outputFileName);
		if (f.exists())
			f.delete();
	}

	public DownloadAsyncTask1(Context mContext, int threadID, String url,
			CallBack callBack, boolean isInitData) {
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

	public DownloadAsyncTask1(Context mContext, int threadID, String url,
			CallBack callBack, String paperID) {
		this.mContext = mContext;
		this.downloadUrl = url;
		this.id = threadID;
		this.cb = callBack;
		this.paperID = paperID;
		s_canDownload = true;

		dataPath = Constant.PAPER_DOWNLOAD + "/"
				+ SharedPreferencesMgr.getUserName();
		File file = new File(dataPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		contextInfo = new ContextInfo() {

			@Override
			public Context getContext(Context context) {
				collectionContext = context;
				return null;
			}
		};
	}

	public DownloadAsyncTask1(Context mContext, int threadID, String url,
			CallBack callBack) {
		this.mContext = mContext;
		this.outputFileName = url.substring(url.lastIndexOf("/") + 1);
		this.downloadUrl = url;
		this.id = threadID;
		this.cb = callBack;
		s_canDownload = true;
		dataPath = Environment.getExternalStorageDirectory().toString();
		File file1 = new File(Constant.DATA_PATH);
		File file2 = new File(Constant.DRUG_AHFS);// AHFS文件夹
		File file3 = new File(Environment.getExternalStorageDirectory()
				+ "/ahfs.zip");// AHFS数据
		File file4 = new File(Constant.DATA_PATH_MARKET);// 工具市场文件夹
		File file5 = new File(Constant.DATA_PATH_MARKET + "/1");
		File file6 = new File(Constant.DATA_PATH_MARKET + "/2");
		File file7 = new File(Constant.DATA_PATH_MARKET + "/3");
		// if (file1.exists())
		// file1.delete();
		file1.mkdir();
		if (!file2.exists())
			file2.mkdir();
		if (file3.exists())
			file3.delete();
		if (!file4.exists())
			file4.mkdir();
		if (!file5.exists())
			file5.mkdir();
		if (!file6.exists())
			file6.mkdir();
		if (!file7.exists())
			file7.mkdir();
		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ outputFileName);
		if (f.exists())
			f.delete();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		File file = new File(outputFile.getAbsolutePath());
		if (!(mContext instanceof PaperDetailActivity) && file != null
				&& file.exists()) {
			file.delete();
		}
		if(!outputFile.isFile() && mContext instanceof PaperDetailActivity){
			result = false;
		}
		cb.onFinish(result);
	}

	@Override
	protected void onProgressUpdate(Long... values) {
		if (mContext instanceof PaperDetailActivity) {
			if (collectionContext != null) {
				((BaseActivity) collectionContext).setDownloadProgress(
						(int) (downloadBytes * 100 / packageLength),
						outputFileName, paperID);
			}
		} else if (mContext instanceof BaseActivity) {
			((BaseActivity) mContext).setDownloadProgress(
					(int) (downloadBytes * 100 / packageLength),
					outputFileName, id);
		}
		super.onProgressUpdate(values);
	}

	public void downloadFile() {
		RandomAccessFile outputStream = null;
		InputStream inputStream = null;

		try {
//			if (mContext instanceof PaperDetailActivity) {
//				Logs.i("==url " + downloadUrl);
//				URL url = new URL(downloadUrl);
//				HttpURLConnection conn = (HttpURLConnection) url
//						.openConnection();
//				conn.getResponseCode();
//				downloadUrl = conn.getURL().toString();
//				conn.disconnect();
//				outputFileName = downloadUrl.substring(downloadUrl
//						.lastIndexOf("/") + 1);
//
//				PaperDownloadAdapter downloadAdpt = new PaperDownloadAdapter(
//						mContext);
//				downloadAdpt.updateFilename(mContext, paperID, outputFileName,
//						SharedPreferencesMgr.getUserName());
//
//				File f = new File(dataPath + "/" + outputFileName);
//				if (f.exists())
//					f.delete();
//			}
			outputFile = new File(dataPath + "/" + outputFileName);
			downloadBytes = outputFile.length();
			app_name = outputFileName;
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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void unzip() {
		try {
			ZipFile zf = new ZipFile(outputFile.getAbsolutePath());
			String[] str = outputFileName.substring(outputFileName.lastIndexOf("_") + 1, outputFileName.lastIndexOf(".")).split("-");
			if(str.length == 3){
				FileManager.delAllFile(Constant.DATA_PATH_MARKET + "/" + str[0] + "/" + str[1]);
			}
			zf.extractAll(dataPath);
		} catch (ZipException e) {
			e.printStackTrace();
		} finally {
			outputFile.delete();
		}

	}

	private void unzipForDownloadData() throws Exception {
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(outputFile);
		if (outputFile.exists())
			System.out.println("outputFile exists   ");
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			System.out.println("entry name    " + entry.getName());
			if (entry.isDirectory()) {
				String name = entry.getName();
				name = name.substring(0, name.length() - 1);
				File file = new File(dataPath + "/" + name);
				file.mkdir();
			} else {
				int count = 0;
				final int BUFFER = 4096;
				byte data[] = new byte[BUFFER];
				FileOutputStream fos = null;

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
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			downloadFile();
			// unzipForDownloadData();
			if (!(mContext instanceof PaperDetailActivity)) {
				unzip();
			}

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
