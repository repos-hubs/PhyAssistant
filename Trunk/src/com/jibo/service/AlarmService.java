package com.jibo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.api.android.GBApp.R;
import com.jibo.activity.FirstStartForChooseActivity;
import com.jibo.activity.InitializeActivity;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.entity.AlarmNotificationInfo;
import com.jibo.util.Logs;

import de.greenrobot.dao.Property;

/**
 * 获取推送消息服务
 * 
 * @author simon
 * 
 */
public class AlarmService extends Service {

	private final String METHOD_NAME = "GetPushDataByUserKey";
	private final String SOAP_ACTION = "http://www.pda.com/pda/GetPushDataByUserKey";
	private final String RESULT_FLAG = "GetPushDataByUserKeyResult";
	private MediaPlayer mMediaPlayer;
	private Vibrator mVibrator;
	private final float IN_CALL_VOLUME = 0.125f;
	private final long[] sVibratePattern = new long[] { 500, 500 };
	public static boolean pushOpen;
	private Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		context = this;
		super.onStart(intent, startId);
		Logs.i("simon", "触发service");
		Logs.i("=== is push apprunning " + this.isAppRunning() + " lock "
				+ SharedPreferencesMgr.getPushIsLock());

		if (!SharedPreferencesMgr.getPushIsLock()) {// 判断推送是否打开

			startCheck();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private Handler hander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			List<AlarmNotificationInfo> infos = (List<AlarmNotificationInfo>) msg.obj;
			// // 屏幕高亮
			// AlarmAlertWakeLock.acquire(context);
			// 解锁
			KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
			KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager
					.newKeyguardLock("simon");
			mKeyguardLock.disableKeyguard();

			// SharedPreferencesMgr.setLastPushId(info.pid);

			for (AlarmNotificationInfo info : infos) {
				if (info == null)
					return;
				// if (info.pType.equals("20"))
				// saveInfo(info);
				startAlarm(info, context);
			}
		}

	};

	/***
	 * 开启线程网络请求
	 */
	private void startCheck() {
		new SharedPreferencesMgr(this);

		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// AlarmNotificationInfo info = dataPreparation();
		// if (info != null) {
		// Logs.i("simon", "信息获取成功");
		// Message msg = new Message();
		// msg.obj = info;
		// hander.sendMessage(msg);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }).start();
		FutureTask<List<AlarmNotificationInfo>> futureTask = new FutureTask<List<AlarmNotificationInfo>>(
				new Callable<List<AlarmNotificationInfo>>() {
					public List<AlarmNotificationInfo> call() {
						try {
							return retrieveNewsByCategoryResult(
									SharedPreferencesMgr.getDept(),
									SharedPreferencesMgr.getLastPushId());
						} catch (Exception e) {
							return null;
						}
					}
				});
		new Thread(futureTask).start();
		while (!futureTask.isDone()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			List<AlarmNotificationInfo> result = futureTask.get();
			if (result != null) {
				Logs.i("simon", "信息获取成功");
				Message msg = new Message();
				msg.obj = result;
				hander.sendMessage(msg);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	/***
	 * 判断应用当前是否在运行
	 * 
	 * @return
	 */
	private boolean isAppRunning() {
		String packageName = "com.api.android.GBApp";
		ActivityManager manager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = manager.getRunningTasks(100);
		if (null != list && list.size() > 0) {
			for (RunningTaskInfo info : list) {
				if (info.topActivity.getPackageName().equals(packageName)
						&& info.baseActivity.getPackageName().equals(
								packageName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 保存参数至本地
	 * 
	 * @param info
	 */
	private void saveInfo(AlarmNotificationInfo info) {

		String[] infos = info.updateInfo.split("\\|");
		String[] news = infos[0].split("\\:");
		int newsUpdateCount = Integer.parseInt(news[1]);
		String[] surveys = infos[1].split("\\:");
		int surveysUpdateCount = Integer.parseInt(surveys[1]);
		String[] drugAlerts = infos[2].split("\\:");
		int drugAlertsUpdateCount = Integer.parseInt(drugAlerts[1]);

		Logs.i("simon", newsUpdateCount + ">>>>" + surveysUpdateCount + ">>>>"
				+ drugAlertsUpdateCount);

		int newsUpdateCountOld = SharedPreferencesMgr.getNewsUpdateCount();
		SharedPreferencesMgr.setNewsUpdateCount(newsUpdateCount
				+ newsUpdateCountOld);
		int surveysUpdateCountOld = SharedPreferencesMgr
				.getSurveysUpdateCount();
		SharedPreferencesMgr.setSurveysUpdateCount(surveysUpdateCount
				+ surveysUpdateCountOld);
		int drugAlertsUpdateCountOld = SharedPreferencesMgr
				.getDrugAlertsUpdateCount();
		SharedPreferencesMgr.setDrugAlertsUpdateCount(drugAlertsUpdateCount
				+ drugAlertsUpdateCountOld);
	}

	/**
	 * 发送提醒通知
	 * 
	 * @param info
	 *            通知实体
	 * @param context
	 */
	private void startAlarm(AlarmNotificationInfo info, Context context) {
		// play(context);
		showNotification(context, info);
	}

	/***
	 * 提醒通知
	 * 
	 * @param context
	 * @param info
	 */
	public void showNotification(Context context, AlarmNotificationInfo info) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Resources resource = context.getResources();

		Notification notification = new Notification(R.drawable.icon,
				resource.getString(R.string.app_name),
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// 设置FLAG_NO_CLEAR表示此通知消息不能被清除
		// 设置FLAG_ONGOING_EVENT同样不能被清除，并且这条消息放在"正在运行"栏目里
		// notification.flags |= Notification.FLAG_NO_CLEAR;
		// notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.ledARGB = Color.BLUE;
		notification.ledOnMS = 5000;

		CharSequence contentTitle = resource.getString(R.string.app_name);
		// Intent intent = new Intent(context, Activity.class);
		// intent.putExtra("id", info.newsId);
		Intent intent = new Intent(context, InitializeActivity.class);
		intent.putExtra("isNotification", true);
		intent.putExtra("pType", info.pType);
		intent.putExtra("pId", info.pid);
		intent.putExtra("nType", info.nType);
		intent.putExtra("id", info.newsId);
		intent.addFlags(Intent.FILL_IN_DATA);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent contentItent = PendingIntent.getActivity(context,
				Constant.alarmId, intent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, info.updateMsg,
				contentItent);
		notificationManager.notify(Constant.getnextNotificationId(), notification);
	}

	/**
	 * 播放提醒声音
	 * 
	 * @param context
	 */
	@SuppressWarnings("unused")
	private void play(Context context) {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Logs.e("simon", "Error occurred while playing audio.");
				mp.stop();
				mp.release();
				mMediaPlayer = null;
				return true;
			}
		});

		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
				mMediaPlayer.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME);
			}
			mMediaPlayer.setDataSource(context, RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
			startPlayer(mMediaPlayer);
		} catch (Exception ex) {
			try {
				mMediaPlayer.setDataSource(context, RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
				startPlayer(mMediaPlayer);
			} catch (Exception ex2) {
				Logs.e("Failed to play fallback ringtone", ex2.getMessage());
			}
		}

		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		mVibrator.vibrate(sVibratePattern, 0);
		mVibrator.cancel();
	}

	/**
	 * 播放声效
	 * 
	 * @param player
	 * @throws java.io.IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void startPlayer(MediaPlayer player) throws java.io.IOException,
			IllegalArgumentException, IllegalStateException {
		player.setAudioStreamType(AudioManager.STREAM_ALARM);
		player.prepare();
		player.start();
	}

	/**
	 * 获取更新内容数据，设定超时时间为10s
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<AlarmNotificationInfo> dataPreparation() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		FutureTask<List<AlarmNotificationInfo>> futureTask = new FutureTask<List<AlarmNotificationInfo>>(
				new Callable<List<AlarmNotificationInfo>>() {
					public List<AlarmNotificationInfo> call() {
						try {
							return retrieveNewsByCategoryResult(
									SharedPreferencesMgr.getDept(),
									SharedPreferencesMgr.getLastPushId());
						} catch (Exception e) {
							return null;
						}
					}
				});
		try {
			executor.execute(futureTask);
			List<AlarmNotificationInfo> result = futureTask.get(10, TimeUnit.SECONDS);
			if (result.size()==0) {
				throw new Exception("simon --- 没有任何更新");
			}
			return result;
		} catch (InterruptedException e) {
			futureTask.cancel(true);
			Logs.i("simon", "==== " + "InterruptedException");
			throw e;
		} catch (ExecutionException e) {
			futureTask.cancel(true);
			Logs.i("simon", "==== " + "ExecutionException");
			throw e;
		} catch (TimeoutException e) {
			futureTask.cancel(true);
			Logs.i("simon", "==== " + "TimeoutException");
			throw e;
		} finally {
			executor.shutdown();
		}
	}

	/**
	 * 发送SOAP请求
	 * 
	 * @param dept
	 *            科室
	 * @param pushId
	 *            最后一次保存的推送ID
	 * @return
	 * @throws Exception
	 */
	private List<AlarmNotificationInfo> retrieveNewsByCategoryResult(
			String dept, String pushId) throws Exception {
		List<AlarmNotificationInfo> notifyInfos = new ArrayList<AlarmNotificationInfo>(
				0);
		try {

//			if (Constant.DEBUG) {
//				AlarmNotificationInfo info = new AlarmNotificationInfo();
//				info.rescode = 200;
//				info.updateMsg = "更新啦";
//				info.updateInfo = "news:2|survey:3|drugalert:4";
//				info.pType = "10";
//				info.pid = "10901";
//				return notifyInfos;
//			}
			Logs.i("simon", "lastTime 最后推送ID==== " + pushId);

			SoapObject rpc = new SoapObject(SoapRes.NAMESPACE, METHOD_NAME);
//			if (Constant.DEBUG) {
//				rpc.addProperty("category", "");
//				rpc.addProperty("id", "");
//			} else {
				rpc.addProperty("accountName",
						SharedPreferencesMgr.getUserName());// dept

//			}
			// rpc.addProperty("lastPushTime", "");
			AndroidHttpTransport ht = new AndroidHttpTransport(SoapRes.URLNews);
			ht.debug = true;

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);

			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);

			ht.call(SOAP_ACTION, envelope);

			SoapObject result = null;
			if (envelope.bodyIn instanceof SoapFault) {
				String str = ((SoapFault) envelope.bodyIn).faultstring;
				// Another way to travers through the SoapFault object
				Node details = ((SoapFault) envelope.bodyIn).detail;
				Element detailElem = (Element) details.getElement(0)
						.getChild(0);
				Element e = (Element) detailElem.getChild(2);
				return notifyInfos;
			} else {
				result = (SoapObject) envelope.bodyIn;
			}
			boolean skipped = false;
			SoapObject stat = ((SoapObject) ((SoapObject) result
					.getProperty(RESULT_FLAG)).getProperty("Result"));
			SoapObject list = ((SoapObject) ((SoapObject) result
					.getProperty(RESULT_FLAG)).getProperty("PushDataDetail"));

			PropertyInfo propertyInfo = new PropertyInfo();
			AlarmNotificationInfo info;
			if (list.getPropertyCount() > 0) {
				PropertyInfo propAttr = new PropertyInfo();
				SoapObject eachItem = null;
				
				String tmp = "";
				for (int i = 0; i < list.getPropertyCount(); i++) {
					eachItem = (SoapObject) list.getProperty(i);
					info = new AlarmNotificationInfo();
					int j = -1;
					do {
						if (skipped)
							break;
						try {j++;
							if (j >= eachItem.getPropertyCount()) {
								break;
							}
							eachItem.getPropertyInfo(j, propAttr);
							tmp = eachItem.getProperty(j).toString();
							if (tmp.equals("anyType{}"))
								tmp = "";
							if (propAttr.name.equals("rescode")) {
								info.rescode = TextUtils.isEmpty(tmp) ? 0
										: Integer.parseInt(tmp);
							} else if (propAttr.name.equals("error")) {
								info.error = tmp;
							} else if (propAttr.name
									.equalsIgnoreCase("updateMsg")) {
								info.updateMsg = tmp;
							} else if (propAttr.name
									.equalsIgnoreCase("updateInfo")) {
								info.updateInfo = tmp;
							} else if (propAttr.name.equalsIgnoreCase("pid")) {
								info.pid = tmp;
							} else if (propAttr.name.equalsIgnoreCase("pType")) {
								info.pType = tmp;
							} else if (propAttr.name.equalsIgnoreCase("newsId")) {
								info.newsId = tmp;
							} else if (propAttr.name.equalsIgnoreCase("nType")) {
								info.nType = tmp;
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
						
					} while (tmp != null);
					if(info.pid==null||info.pid.trim().equals("")){
						continue;
					}
					notifyInfos.add(info);
				}
			}
			return notifyInfos;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
