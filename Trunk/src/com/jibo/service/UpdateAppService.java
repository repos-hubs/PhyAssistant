package com.jibo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.api.android.GBApp.R;
import com.jibo.activity.HomePageActivity;
import com.jibo.common.Constant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateAppService extends Service {

	// �ļ��洢
	private File updateFile;
	private File updateDir;

	/** ���ص�ַ */
	private String downloadUrl;

	// ֪ͨ��
	private NotificationManager updateNotificationManager;
	private Notification updateNotification;
	// ֪ͨ����תIntent
	private Intent updateIntent;
	private PendingIntent updatePendingIntent;

	private final int DOWNLOAD_ING = 0;// -----------��������
	private final int DOWNLOAD_COMPLETE = 1;// ------�������
	private final int DOWNLOAD_FAIL = 2;// ----------����ʧ��

	/** ֪ͨid */
	private final int downloadAppNoId = 100;

	private int updateCode;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		downloadUrl = intent.getStringExtra("url");
		updateCode = intent.getIntExtra("updateCode", 1);

		String fileName = downloadUrl
				.substring(downloadUrl.lastIndexOf("/") + 1);

		// �����ļ�
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Constant.DATA_PATH);
			updateFile = new File(updateDir.getPath(), fileName);
			cleanUpdateFile();
		}

		updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		updateNotificationManager.cancel(downloadAppNoId);

		updateNotification = new Notification(R.drawable.icon,
				getString(R.string.download_app), System.currentTimeMillis());

		// ������"��������"��Ŀ��
		updateNotification.flags = Notification.FLAG_ONGOING_EVENT;

		// �������ع����У����֪ͨ�����ص�������
		updateIntent = new Intent(this, HomePageActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, downloadAppNoId,
				updateIntent, 0);
		// ����֪ͨ����ʾ����
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.download_notification);
		updateNotification.contentView = contentView;
		updateNotification.contentIntent = updatePendingIntent;

		// ����֪ͨ
		updateNotificationManager.notify(downloadAppNoId, updateNotification);

		new Thread(new updateRunnable()).start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_ING:
				int downloadSize = (Integer) msg.obj;
				RemoteViews contentView = updateNotification.contentView;
				contentView.setTextViewText(R.id.rate, downloadSize + "%");
				contentView.setProgressBar(R.id.progress, 100, downloadSize,
						false);
				updateNotificationManager.notify(downloadAppNoId,
						updateNotification);
				break;
			case DOWNLOAD_COMPLETE:
				// �����װPendingIntent
				
				if (updateCode == 2) {// ǿ������
					Log.i("simon", "ǿ������");
					Uri uri = Uri.fromFile(updateFile);
					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					installIntent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(installIntent);
					updateNotificationManager.cancel(downloadAppNoId);
				} else {// ��ǿ�����������û��Լ������װ
					Log.i("simon", "��ǿ������");
					updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
					updateNotification.contentView = null;

					Uri uri = Uri.fromFile(updateFile);
					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					installIntent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					updatePendingIntent = PendingIntent.getActivity(
							UpdateAppService.this, downloadAppNoId,
							installIntent, 0);

					updateNotification.setLatestEventInfo(
							UpdateAppService.this,
							getString(R.string.app_name),
							getString(R.string.download_app_success),
							updatePendingIntent);
					updateNotificationManager.notify(downloadAppNoId,
							updateNotification);
				}
				// ֹͣ����
				stopSelf();
				break;
			case DOWNLOAD_FAIL:
				// ����ʧ��
				updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
				updateNotification.contentView = null;

				updateNotification.setLatestEventInfo(UpdateAppService.this,
						getString(R.string.app_name),
						getString(R.string.download_app_faild),
						updatePendingIntent);
				updateNotificationManager.notify(downloadAppNoId,
						updateNotification);
				stopSelf();
			default:
				stopSelf();
			}
		}
	};

	private void cleanUpdateFile() {
		if (updateFile.exists()) {
			updateFile.delete();
		}
	}

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {
				Thread.sleep(500);
				if (!updateDir.exists()) {
					updateDir.mkdirs();
				}

				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				long downloadSize = downloadUpdateFile(downloadUrl, updateFile);
				if (downloadSize > 0) {
					// ���سɹ�
					updateHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// ����ʧ��
				updateHandler.sendMessage(message);
			}
		}
	}

	public long downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// �ٷֱ�����33��֪ͨһ��
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 10 >= downloadCount) {
					downloadCount += 10;
					Thread.sleep(500);
					int downloadSize = (int) totalSize * 100 / updateTotalSize;
					Message msg = updateHandler.obtainMessage(DOWNLOAD_ING);
					msg.obj = downloadSize;
					updateHandler.sendMessage(msg);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

}
