package com.jibo.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.Handler;

import com.jibo.activity.BaseActivity;
import com.jibo.common.Constant;

public class AsyncDownloadRnnable implements Runnable {
	private Context mContext;
	private String action;
	private long packageLength;
	private long downloadBytes;
	private String dataPath;
	private String outputFileName;
	private String downloadUrl;
	private AsyncSoapResponseHandler handler;
	public AsyncDownloadRnnable(Context mContext, String action, String url, BaseResponseHandler handler) {
		this.mContext = mContext;
		this.outputFileName = url.substring(url.lastIndexOf("/") + 1);
		this.action = action;
		this.downloadUrl = url;
		this.handler = handler;
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
	public void run() {
		Exception ex = new FileNotFoundException();
		handler.sendFailureMessage(ex, null);
//			downloadFile();
	}

	public void downloadFile() throws Exception {
		File dataFile = new File(Constant.DATA_PATH);
		if (!dataFile.exists()) {
			dataFile.mkdir();
		}
		File fileTmp = new File(dataPath);
		if (!fileTmp.exists()) {
			fileTmp.mkdir();
		}
		File outputFile = new File(dataPath + "/" + outputFileName);
		RandomAccessFile outputStream = new RandomAccessFile(outputFile, "rw");
		downloadBytes = outputFile.length();
		if (outputStream != null) {
			outputStream.seek(downloadBytes);
		}

		URL url = new URL(downloadUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("RANGE", "bytes=" + downloadBytes + "-");
		packageLength = connection.getContentLength() + downloadBytes;

		InputStream inputStream = connection.getInputStream();
		byte[] buffer = new byte[1024];
		int hasRead = 0;
		while ((inputStream != null)
				&& ((hasRead = inputStream.read(buffer)) != -1)) {
			outputStream.write(buffer, 0, hasRead);
			downloadBytes = downloadBytes + hasRead;
		}
	}

}
