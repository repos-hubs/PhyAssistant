package com.jibo.asynctask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.RemoteViews;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.BaseActivity;
import com.jibo.activity.HomePageActivity;
import com.jibo.activity.InitializeActivity;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.net.AsyncSoapResponseHandler;

public class DownloadAsyncTask extends AsyncTask<Void, Long, Void> {
	private Context mContext;
	private String action;
	private long packageLength;
	private long downloadBytes;
	private int id;
	private String dataPath;
	private String outputFileName;
	private String downloadUrl;
	private String version;
	private AsyncSoapResponseHandler handler;
	private File outputFile;
	public static int s_downLoadID = 0x123;
	public CallBack cb;
	public static boolean s_canDownload;
	public static String app_name;
	private GBApplication app;
    private Notification notification = null;
    private NotificationManager manager = null;
	public DownloadAsyncTask(Context mContext, int threadID, String action,String version,
			String url, AsyncSoapResponseHandler handler, CallBack callBack) {
		this.mContext = mContext;
		this.outputFileName = url.substring(url.lastIndexOf("/") + 1);
		this.action = action;
		this.downloadUrl = url;
		this.handler = handler;
		this.id = threadID;
		this.cb = callBack;
		this.version = version;
		System.out.println("downloadUrl     "+downloadUrl);
		manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		s_canDownload = true;
		if(mContext instanceof InitializeActivity) {
			app = (GBApplication) ((InitializeActivity)mContext).getApplication();
		} else if(mContext instanceof HomePageActivity) {
			app = (GBApplication) ((HomePageActivity)mContext).getApplication();
		}
		if (action.equals(Constant.FLAG_DIFF)) {
			dataPath = Constant.DATA_PATH + "/tmp";
		} else if (action.equals(Constant.FLAG_FULL)) {
			dataPath = Constant.DATA_PATH;
		} else if (action.equals(Constant.FLAG_APP)) {
			dataPath = Constant.DATA_PATH;
		} else if (action.equals(Constant.FLAG_PACKAGE)) {
			dataPath = Constant.DATA_PATH + "/GBADATA";
		}
		 
	}

	@Override
	protected void onPostExecute(Void result) {
		try {
			if(Constant.FLAG_FULL.equals(action) || Constant.FLAG_DIFF.equals(action)) {
//				deleteDir(Constant.DATA_PATH);
				unzipForDownloadData();
			} else if(Constant.FLAG_PACKAGE.equals(action)) {
				unzipForDownloadData();
			}
			
			cb.onFinish(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}

	public void deleteDir(String path) {
    	File file = new File(path);
    	if(file.exists()) {
    		if(file.isDirectory()) {
        		File fileList[] = file.listFiles();
            	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            		for(File f:fileList) {
                		if(f.isDirectory()) {
                			deleteDir(f.getAbsolutePath());
                			f.delete();
                		} 
                	}
            	}
        	} else {
        		//file.delete();
        	}
    	}
    }
	@Override
	protected void onProgressUpdate(Long... values) {
		if(mContext instanceof BaseActivity) {
			((BaseActivity)mContext).setDownloadProgress((int) (downloadBytes*100/packageLength), outputFileName, id);
		}
		if (mContext instanceof InitializeActivity) {
			if(action.equals(Constant.FLAG_DIFF)) {
				setPropress((int) (downloadBytes/packageLength)*100, outputFileName);
			} else {
//				((InitializeActivity) mContext).setProgressBar(downloadBytes,
//						packageLength, false, outputFileName);
			}
		}
//		else if(mContext instanceof ModifyDepartmentActivity) {
//			setPropress((int) (downloadBytes/packageLength)*100, outputFileName);
//		}
		super.onProgressUpdate(values);
	}

	private void setPropress(int progress, String title) {
		notification = new Notification(R.drawable.icon, "", System
                .currentTimeMillis());
        notification.icon = R.drawable.icon;

        notification.contentView = new RemoteViews(mContext
                .getPackageName(), R.layout.dialog_notification);
        notification.contentView.setImageViewResource(R.id.img_notification, R.drawable.icon);
        notification.contentView.setTextViewText(R.id.txt_notification_title, title.subSequence(0, title.length()-4));
        notification.contentView.setProgressBar(R.id.pb, 100, 0, false);
        notification.contentView.setTextViewText(R.id.tv, progress
                + "%");
        notification.contentIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(), 0);
        
        if (progress == 100) {
        	notification.contentView.setTextViewText(R.id.tv, mContext.getString(R.string.pkg_installed));
//            manager.cancel(notification_id);
        }
        manager.notify(id, notification);
	}
	public void downloadFile() throws Exception {
		System.out.println("downloadFile  &&&&   ");
		File dataFile = new File(Constant.DATA_PATH);
		if (!dataFile.exists()) {
			dataFile.mkdir();
		}
		File fileTmp = new File(dataPath);
		if (!fileTmp.exists()) {
			fileTmp.mkdir();
		}
		outputFile = new File(dataPath + "/" + outputFileName);
		app_name = outputFileName;
		RandomAccessFile outputStream = new RandomAccessFile(outputFile, "rw");
		downloadBytes = outputFile.length();
		if (outputStream != null) {
			outputStream.seek(downloadBytes);
		}

		URL url = new URL(downloadUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("RANGE", "bytes=" + downloadBytes + "-");
		packageLength = connection.getContentLength() + downloadBytes;
		
		File file = new File(Constant.DATA_PATH);
		File fileList[] = file.listFiles();
		InputStream inputStream = connection.getInputStream();
		byte[] buffer = new byte[1024];
		int hasRead = 0;
		while ((inputStream != null)
				&& ((hasRead = inputStream.read(buffer)) != -1) && s_canDownload) {
			outputStream.write(buffer, 0, hasRead);
			downloadBytes = downloadBytes + hasRead;
			publishProgress(downloadBytes);
		}
	}

	private void unzipForDownloadData() throws Exception {
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(outputFile);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		String zipFileName = "";
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
				if (Constant.FLAG_DIFF.equals(action)) {
					app.getDiffMap().put(Double.parseDouble(version), entry.getName());
				}
				
			}
		}
		zis.close();
		File file = new File(outputFile.getAbsolutePath());
		if (file != null && file.exists()) {
			file.delete();
		}

		if (Constant.FLAG_DIFF.equals(action)) {
		} else if (Constant.FLAG_PACKAGE.equals(action)) {
		} else if (Constant.FLAG_FULL.equals(action)) {
			SharedPreferencesMgr.setLoadedDB(true);
		}
		
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			downloadFile();
		} catch (Exception e) {
			handler.sendFailureMessage(e, null);
		}
		return null;
	}
	
	public interface CallBack {
		void onFinish(boolean b);
	}
}
