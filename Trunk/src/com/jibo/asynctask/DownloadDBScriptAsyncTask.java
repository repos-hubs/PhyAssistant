package com.jibo.asynctask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import android.os.AsyncTask;
import com.jibo.common.Constant;

/**
 * ����db���½ű�
 * 
 * @author Simon
 * 
 */
public class DownloadDBScriptAsyncTask extends AsyncTask<Void, Long, Void> {
	// ���ص�ַ
	private String downloadUrl;
	// �ļ��洢
	private File outputFile;
	//�ص��ӿ�
	public CallBack cb;

	public DownloadDBScriptAsyncTask(String url, CallBack callBack) {
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		this.downloadUrl = url;
		this.cb = callBack;
		outputFile = new File(Constant.DATA_PATH + "/" + fileName);
		if (outputFile.exists())
			outputFile.delete();
	}

	@Override
	protected void onPostExecute(Void result) {
		// outputFile.delete();
		cb.onFinish(true);
		super.onPostExecute(result);
	}

	/**
	 * ����db��source�ļ�
	 * @throws Exception
	 */
	public void downloadFile() throws Exception {
		RandomAccessFile outputStream = null;
		InputStream inputStream = null;
		try {
			outputStream = new RandomAccessFile(outputFile, "rw");
			long downloadBytes = outputFile.length();
			if (outputStream != null) {
				outputStream.seek(downloadBytes);
			}

			URL url = new URL(downloadUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestProperty("RANGE", "bytes=" + downloadBytes
					+ "-");
			try {
				inputStream = connection.getInputStream();
			} catch (FileNotFoundException e) {
				throw e;
			}
			byte[] buffer = new byte[1024];
			int hasRead = 0;
			while ((inputStream != null)
					&& ((hasRead = inputStream.read(buffer)) != -1)) {
				outputStream.write(buffer, 0, hasRead);
			}
		} finally {
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
		}
	}

	/**
	 * ѹ�����ذ�
	 * @throws Exception
	 */
	private void unzip() throws Exception {
		if (!outputFile.exists())
			throw new Exception("�����ļ�������");
		try {
			ZipFile zf = new ZipFile(outputFile.getAbsolutePath());
			List<FileHeader> headerList = zf.getFileHeaders();
			if (null != headerList) {
				for (FileHeader header : headerList) {
					File file = new File(Constant.DATA_PATH + File.separator
							+ header.getFileName());
					// ɾ�����ļ���
					if (file.isDirectory()) {
						if (file.exists())
							file.delete();
					}

					System.out.println("File Name: " + header.getFileName());// ����
					System.out.println("Compressed Size: "
							+ header.getCompressedSize());// ѹ�����С
					System.out.println("Uncompressed Size: "
							+ header.getUncompressedSize());// δѹ����С
					System.out.println("CRC: " + header.getCrc32());// crcУ����
				}
				// ��ѹ���ļ�
				zf.extractAll(Constant.DATA_PATH);
			}
		} catch (ZipException e) {
			throw e;
		} finally {
			outputFile.delete();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			downloadFile();
			unzip();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * �ص��ӿ�
	 * @author simon
	 *
	 */
	public interface CallBack {
		void onFinish(boolean b);
	}
}
