package com.jibo.common;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;

import com.jibo.GBApplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException������,��������Uncaught�쳣��ʱ��,�и��� ���ӹܳ���,����¼ ���ʹ��󱨸�.
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	/** Debug Log tag */
	public static final String TAG = "CrashHandler";
	/**
	 * �Ƿ�����־���,��Debug״̬�¿���, ��Release״̬�¹ر�����ʾ��������
	 * */
	public static final boolean DEBUG = true;
	/** ϵͳĬ�ϵ�UncaughtException������ */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandlerʵ�� */
	private static CrashHandler INSTANCE;
	/** �����Context���� */
	private Context mContext;

	/** ʹ��Properties�������豸����Ϣ�ʹ����ջ��Ϣ */
	private Properties mDeviceCrashInfo = new Properties();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	/** ���󱨸��ļ�����չ�� */
	private static final String CRASH_REPORTER_EXTENSION = ".cr";

	/** ��ֻ֤��һ��CrashHandlerʵ�� */
	private CrashHandler() {
	}

	/** ��ȡCrashHandlerʵ�� ,����ģʽ */
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * ��ʼ��,ע��Context����, ��ȡϵͳĬ�ϵ�UncaughtException������, ���ø�CrashHandlerΪ�����Ĭ�ϴ�����
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * ��UncaughtException����ʱ��ת��ú���������
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// ����û�û�д�������ϵͳĬ�ϵ��쳣������������
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleepһ����������
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				Log.e(TAG, "Error : ", e);
//			}
			GBApplication app = (GBApplication) mContext.getApplicationContext();
			app.quit();
		}

	}

	/**
	 * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����.
	 * 
	 * @param ex
	 * @return true:��������˸��쳣��Ϣ;���򷵻�false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		// ʹ��Toast����ʾ�쳣��Ϣ
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
//				Toast.makeText(mContext, "������ִ���:" + msg, Toast.LENGTH_LONG)
//						.show();
				Looper.loop();
			}

		}.start();
		// �ռ��豸��Ϣ
		collectCrashDeviceInfo(mContext);
		// ������󱨸��ļ�
		String crashFileName = saveCrashInfoToFile(ex);
		// ���ʹ��󱨸浽������
		sendCrashReportsToServer(mContext);
		return true;
	}

	/**
	 * �ڳ�������ʱ��, ���Ե��øú�����������ǰû�з��͵ı���
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(mContext);
	}

	/**
	 * �Ѵ��󱨸淢�͸�������,�����²����ĺ���ǰû���͵�.
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));

			for (String fileName : sortedFiles) {
				File cr = new File(ctx.getFilesDir(), fileName);
				postReport(cr);
				cr.delete();// ɾ���ѷ��͵ı���
			}
		}
	}

	private void postReport(File file) {
		// TODO ʹ��HTTP Post ���ʹ��󱨸浽������
		// ���ﲻ������,�����߿��Ը���OPhoneSDN�ϵ������������
		// �̳����ύ���󱨸�
	}

	/**
	 * ��ȡ���󱨸��ļ���
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		File filesDir = ctx.getFilesDir();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		return filesDir.list(filter);
	}

	/**
	 * ���������Ϣ���ļ���
	 * 
	 * @param ex
	 * @return
	 */
	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		printWriter.close();
		mDeviceCrashInfo.put(STACK_TRACE, result);

		try {
			long timestamp = System.currentTimeMillis();
			if (Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				File mk = new File("/sdcard/Jibo/Error/");
				if (!mk.exists()) {
					mk.mkdirs();
				}
				String fileName = "/sdcard/Jibo/Error/"
						+ timestamp + ".log";
				File ErrorLog = new File(fileName);
				if (!ErrorLog.exists()) {
					ErrorLog.createNewFile();
				}
				FileWriter fw = new FileWriter(ErrorLog, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write((new Date(timestamp)).toLocaleString() + "\r\n�쳣��Ϣ:"
						+ ex.getLocalizedMessage() + "\r\n��ջ��Ϣ:"
						+ mDeviceCrashInfo.getProperty(STACK_TRACE));
				bw.close();
				fw.close();
				return fileName;
			}
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
		}
		return null;
	}

	/**
	 * �ռ�����������豸��Ϣ
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo.put(VERSION_NAME,
						pi.versionName == null ? "not set" : pi.versionName);
				mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		// ʹ�÷������ռ��豸��Ϣ.��Build���а��������豸��Ϣ,
		// ����: ϵͳ�汾��,�豸������ �Ȱ������Գ����������Ϣ
		// ������Ϣ��ο�����Ľ�ͼ
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName(), field.get(null));
				if (DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}
		}
	}
}