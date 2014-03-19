/**
 * 
 */
package com.jibo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.api.android.GBApp.R;
import com.jibo.app.DetailsData;
import com.jibo.base.src.request.impl.db.SQLiteAdapter;
import com.jibo.common.Constant;
import com.jibo.common.CrashHandler;
import com.jibo.common.DeviceInfo;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.dao.DBFactory;
import com.jibo.dao.DBHelper;
import com.jibo.dao.DaoManager;
import com.jibo.dao.DaoSession;
import com.jibo.data.entity.LoginEntity;
import com.jibo.data.entity.MarketPackageEntity;
import com.jibo.data.entity.PayInfoEntity;
import com.jibo.dbhelper.InitializeAdapter;
import com.jibo.dbhelper.UpLoadDataAdapter;
import com.jibo.net.AsyncSoapClient;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.util.Logs;
import com.jibo.util.tips.TipHelper;

/**
 * @author peter.pan
 * 
 */
public class GBApplication extends Application {

	/**
	 * Application ��ʵ��
	 * */
	public static GBApplication gbapp;
	/**
	 * Soap����ͻ���
	 * */
	public AsyncSoapClient soapClient;
	/** �ֻ��豸��ص���Ϣ */
	private DeviceInfo deviceInfo;
	private float scale;
	private LoginEntity login;
	// ҩƷ������touchĬ���б�
	private ArrayList<String> associateDataList;
	private ArrayList<String> associateDataCNList;
	private ArrayList<String> associateDataENList;
	private HashMap<Integer, String> pdaColumnMap;
	private TreeMap<Double, String> diffMap;
	private MarketPackageEntity marketEntity;

	private boolean isLaunched = false;
	private boolean isUnbinded = false;
	private boolean isStartActivity = false;
	private ArrayList<Activity> actList;

	private boolean dataIsRegistered = false;
	private boolean newsIsRegistered = false;
	private boolean alertIsRegistered = false;

	private boolean dataIsBinded = false;
	private boolean newsIsBinded = false;
	private boolean alertIsBinded = false;
	private boolean marketHasUpdated = false;

	private HashMap<String, String> recheckMap;
	private PayInfoEntity payInfo;
	private String surveyTitle;
	private String surveyID;

	protected DBHelper helper;
	protected DaoManager daoMaster;
	protected DaoSession daoSession;

	@Override
	public void onCreate() {
		super.onCreate();

		gbapp = this;
		soapClient = new AsyncSoapClient();
		deviceInfo = new DeviceInfo(this);
		// // ����ȫ���쳣�������
		if (!Constant.DEBUG) {// ����ģʽ�¹ر�
			CrashHandler crashHandler = CrashHandler.getInstance();
			crashHandler.init(getApplicationContext());
			crashHandler.sendPreviousReportsToServer();
		}

			
		
		actList = new ArrayList<Activity>();
		diffMap = new TreeMap<Double, String>();
		initializePDAColumn();
		new SharedPreferencesMgr(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				DetailsData.putInReadCache(getBaseContext());
			}

		}).start();
//		 Logs.setLevel(Logs.LOG_ERROR);

		new SQLiteAdapter(DBFactory.SDCard_DB_NAME);
		if(Util.getCurrentVerName(this).equals("2.1.3")){
			SharedPreferencesMgr.setLastPushId("-1");
				}
	}

	/***
	 * ����db��Դ
	 */
	protected void openDBResources() {
		if (helper == null && daoSession == null) {
			helper = DBFactory.getSDCardDbHelper(this);
			daoMaster = new DaoManager(helper);
			daoSession = daoMaster.newSession();
		}
	}

	/***
	 * �ر�db��Դ
	 */
	protected void releaseDBResources() {
		if (helper != null && daoSession != null) {
			try {
				helper.finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			helper = null;
			daoMaster = null;
			daoSession = null;
		}
	}

	public DaoSession getDaoSession() {
		if (Util.isDBFileExist()) {
			openDBResources();
		}
		return daoSession;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public boolean isMarketHasUpdated() {
		return marketHasUpdated;
	}

	public void setMarketHasUpdated(boolean marketHasUpdated) {
		this.marketHasUpdated = marketHasUpdated;
	}

	public HashMap<String, String> getRecheckMap() {
		if (recheckMap == null) {
			recheckMap = new HashMap<String, String>();
		}
		return recheckMap;
	}

	public void setRecheckMap(HashMap<String, String> recheckMap) {
		this.recheckMap = recheckMap;
	}

	public PayInfoEntity getPayInfo() {
		return payInfo;
	}

	public void setPayInfo(PayInfoEntity payInfo) {
		this.payInfo = payInfo;
	}

	public MarketPackageEntity getMarketEntity() {
		return marketEntity;
	}

	public void setMarketEntity(MarketPackageEntity marketEntity) {
		this.marketEntity = marketEntity;
	}

	public HashMap<Integer, String> getPdaColumnMap() {
		return pdaColumnMap;
	}

	public TreeMap<Double, String> getDiffMap() {
		return diffMap;
	}

	public void setDiffMap(TreeMap<Double, String> diffMap) {
		this.diffMap = diffMap;
	}

	public boolean isHomeLaunched() {
		return isLaunched;
	}

	public void setHomeLaunched(boolean isLaunched) {
		this.isLaunched = isLaunched;
	}

	public boolean isStartActivity() {
		return isStartActivity;
	}

	public void setStartActivity(boolean isStartActivity) {
		this.isStartActivity = isStartActivity;
	}

	public boolean isUnbinded() {
		return isUnbinded;
	}

	public void setUnbinded(boolean isUnbinded) {
		this.isUnbinded = isUnbinded;
	}

	public boolean isDataIsRegistered() {
		return dataIsRegistered;
	}

	public boolean isNewsIsRegistered() {
		return newsIsRegistered;
	}

	public boolean isAlertIsRegistered() {
		return alertIsRegistered;
	}

	public boolean isDataIsBinded() {
		return dataIsBinded;
	}

	public boolean isNewsIsBinded() {
		return newsIsBinded;
	}

	public boolean isAlertIsBinded() {
		return alertIsBinded;
	}

	public void setDataIsRegistered(boolean dataIsRegistered) {
		this.dataIsRegistered = dataIsRegistered;
	}

	public void setNewsIsRegistered(boolean newsIsRegistered) {
		this.newsIsRegistered = newsIsRegistered;
	}

	public void setAlertIsRegistered(boolean alertIsRegistered) {
		this.alertIsRegistered = alertIsRegistered;
	}

	public void setDataIsBinded(boolean dataIsBinded) {
		this.dataIsBinded = dataIsBinded;
	}

	public void setNewsIsBinded(boolean newsIsBinded) {
		this.newsIsBinded = newsIsBinded;
	}

	public void setAlertIsBinded(boolean alertIsBinded) {
		this.alertIsBinded = alertIsBinded;
	}

	public String getSurveyTitle() {
		return surveyTitle;
	}

	public void setSurveyTitle(String surveyTitle) {
		this.surveyTitle = surveyTitle;
	}

	public String getSurveyID() {
		return surveyID;
	}

	public void setSurveyID(String surveyID) {
		this.surveyID = surveyID;
	}

	public void initializePDAColumn() {
		pdaColumnMap = new HashMap<Integer, String>();
		pdaColumnMap.put(Constant.CALC_COLID, getString(R.string.calculator));
		pdaColumnMap.put(Constant.RESEARCH_COLID, getString(R.string.research));
		pdaColumnMap.put(Constant.NEWS_COLID, getString(R.string.news));
		pdaColumnMap.put(Constant.EVENTS_COLID, getString(R.string.event));
		pdaColumnMap.put(Constant.ECG_COLID, getString(R.string.ecg));
		pdaColumnMap.put(Constant.TNM_COLID, getString(R.string.tnm));
		pdaColumnMap.put(Constant.DRUG_COLID, getString(R.string.drug));
		pdaColumnMap.put(Constant.DRUG_ALERT_COLID,
				getString(R.string.drugalert));
	}

	public Activity getActivity(Class clazz) {
		for (Activity acty : actList) {
			if (acty.getClass() == clazz) {
				return acty;
			}
		}
		return null;
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public ArrayList<String> getAssociateDataList() {
		if (associateDataList == null) {
			associateDataList = new ArrayList<String>();
		}
		return associateDataList;
	}

	public void setAssociateDataList(ArrayList<String> associateDataList) {
		this.associateDataList = associateDataList;
	}

	public ArrayList<String> getAssociateDataCNList() {
		if (associateDataCNList == null) {
			associateDataCNList = new ArrayList<String>();
		}
		return associateDataCNList;
	}

	public void setAssociateDataCNList(ArrayList<String> associateDataCNList) {
		this.associateDataCNList = associateDataCNList;
	}

	public ArrayList<String> getAssociateDataENList() {
		if (associateDataENList == null) {
			associateDataENList = new ArrayList<String>();
		}
		return associateDataENList;
	}

	public void setAssociateDataENList(ArrayList<String> associateDataENList) {
		this.associateDataENList = associateDataENList;
	}

	public static int getVerCode(Activity act) {
		int verCode = -1;
		try {
			verCode = act.getPackageManager().getPackageInfo(
					"com.api.android.GBApp", 0).versionCode;
			Log.i("GBA", "vercode == " + verCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return verCode;
	}

	public static String getVerName(Activity act) {
		String verName = "";
		try {
			verName = act.getPackageManager().getPackageInfo(
					"com.api.android.GBApp", 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return verName;
	}

	/**
	 * �˳�
	 * */
	public void quit() {
		TipHelper.sign(false, true);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		// ��������������
		soapClient.cancelAll();
		soapClient = null;
		// ����ֻ�Ӧ����Ϣ
		deviceInfo = null;
		// todo ֹͣ���񡣡���

		int version = android.os.Build.VERSION.SDK_INT;
		if (version <= 7) {
			System.out.println("   version  < 7");
			ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			manager.restartPackage(getPackageName());
		} else {

		}
		for (Activity act : actList) {
			if (!act.isFinishing())
				act.finish();
		}
		actList.clear();
		releaseDBResources();
		// Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.addCategory(Intent.CATEGORY_HOME);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		DetailsData.releaseToSharedPreference(getBaseContext());
		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
		System.exit(0);
	}

	public void addActivitToStack(Activity act) {
		if (actList.contains(act)) {
			actList.remove(act);
		}
		actList.add(act);
	}

	public void exit() {
		for (Activity act : actList) {
			if (!act.isFinishing())
				act.finish();
		}
		System.exit(0);
	}

	public LoginEntity getLogin() {
		if (null == login) {
			LoginEntity newLogin = new LoginEntity();
			newLogin.setGbUserName(SharedPreferencesMgr.getUserName());
			newLogin.setLicenseNumber(SharedPreferencesMgr.getLicenseNumber());
			newLogin.setDoctorId(SharedPreferencesMgr.getDrugId());
			newLogin.setGbPassword(SharedPreferencesMgr.getPassword());
			newLogin.setHospitalRegion(SharedPreferencesMgr.getRegion());
			return newLogin;
		}
		return login;
	}

	// �ж��Ƿ�Ϊ��,loginΪ�������������һ��ע��֮��ֱ���������棬�����Զ���½
	public boolean isLoginNotNull() {
		return login != null;
	}

	public void setLogin(LoginEntity login) {
		this.login = login;
	}

	public void deleteDir(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			File fileList[] = file.listFiles();
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				for (File f : fileList) {
					if (f.isDirectory()) {
						deleteDir(f.getAbsolutePath());
						f.delete();
					} else {
						f.delete();
					}
				}
				file.delete();
			}
		} else {
			file.delete();
		}
	}

	// ---------------��¼�ϴ���Ϣ----------------------//
	UpLoadDataAdapter uploadAdapter;

	public void uploadData(String page_ID, String DetailID,
			String DetailIdName, String Demo) {
		String userName = SharedPreferencesMgr.getUserName();
		String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		final String[] log = { userName, null, page_ID, DetailID, DetailIdName,
				dateTime };
		Properties p = new Properties();
		if (userName != null)
			p.setProperty("userid", userName);
		if (page_ID != null)
			p.setProperty("page_ID", page_ID);
		if (DetailID != null)
			p.setProperty("DetailID", DetailID);
		if (DetailIdName != null)
			p.setProperty("DetailIDName", DetailIdName);
		if (Demo != null)
			p.setProperty("Demo", Demo);
		if (dateTime != null)
			p.setProperty("date_time", dateTime);
		p.setProperty("sourcefrom", "Android");
		InitializeAdapter initializeAdapter = new InitializeAdapter(this, 1);
		String verName = initializeAdapter.getCurrentVerName();
		Log.e("verName", verName);
		p.setProperty("Version", verName);
		if (DeviceInfo.instance.isNetWorkEnable()) {
			gbapp.soapClient.sendRequest(SoapRes.URLUSERLOGINFO,
					SoapRes.REQ_ID_RECORD_USER_INFO, p,
					new AsyncSoapResponseHandler() {
						public void onFailure(Throwable error, String content) {
							// ʧ��Ҳ����ݿ�
							insertLog(log);
						}
					}, this);
		} else {
			// ���������ݿ�
			insertLog(log);
		}
	}

	/***
	 * ��ȡ��ģ��Ķ�Ӧid��ţ�������ʷ���ղ�
	 * 
	 * @param name
	 * @return
	 */
	public int getColID(String name) {
		int colID = -1;
		for (Entry<Integer, String> en : pdaColumnMap.entrySet()) {
			if (en.getValue().toString().equals(name)) {
				colID = (Integer) en.getKey();
			}
		}
		return colID;
	}

	public void insertLog(String[] log) {
		if (uploadAdapter == null)
			uploadAdapter = new UpLoadDataAdapter(this);
		uploadAdapter.insertLog(log);
	}

	

}
