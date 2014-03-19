package com.jibo.common;

import android.os.Environment;

public class Constant {
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory() + "/Jibo";
	public static final String DATA_PATH_GBADATA = DATA_PATH + "/GBADATA";
	public static final String DATA_PATH_MARKET_INSTALL = DATA_PATH + "/MarketInstall";
	public static final String DPT_TMP_PATH = DATA_PATH + "/dpt_tmp";
	public static final String DRUG_AHFS = DATA_PATH + "/ahfs";
	public static final String DATA_PATH_MARKET = DATA_PATH + "/Market";
	public static final String DATA_PATH_Mufacture_doc = DATA_PATH + "/mufacture_doc";
	public static final String DATA_PATH_SQL= DATA_PATH + "/sql";
	public static final String PAPER_DOWNLOAD = DATA_PATH + "/papers";
	public static final String FLAG_APP = "app";
	public static final String FLAG_DIFF = "Diff";
	public static final String FLAG_FULL = "Full";
	public static final String FLAG_PACKAGE = "package";
	public static final String HAS_READ = "hasRead";
	public static final String LENGTH = "length";
	public static final String SEARCH_TEXT = "search_mode";
	public static final String ISMAINPAGE = "MAINPAGE";
	public static final String ID = "id";

	public static final int DOWNLOAD_ID_AHFS = 0x111;

	public static final String CURRENT_DB = "1.00";
	public static final int NOTIFICATION_DOWNLOAD = 0x123;
	public static final String DRUG_AHFS_PW = "!g#B$%2^0&1(2)J$u%L#y^2!0)";
	public static final String PACKAGE_IS_DOWNLODED = "packageIsDownloaded";
	public static final String IS_FIRST_ON = "ISLOADFIRST";
	public static final String CHECK_DATA_UPDATE = "CHECKDATAUPDATE"; // using
																		// it in
																		// different
																		// way.

	public static final String DBCODE = "UPDATEDBGBAPPDBCODE";
	public static final String WELCOME_IMAGE = "WELCOMEIMAGE";
	public static final String LOGIN_FLAG = "login_flag";
	public static final String DownloadFirst = "ISDOWNLOADFIRST";

	/**
	 * Table Name
	 */
	public static final String TABLE_SEARCH_HISTORY = "search_history";
	public static final String TABLE_VIEW_HISTORY = "view_history";
	public static int myFavCategory = -1;
	/**
	 * Database Name
	 */
	public static final String DB_CALCULATE = "calculate.db";

	public static final int HANDLER_HIDE_PRETINT = 0x1234;
	public static final int HANDLER_UNZIP_COMPLETE = 0x1234 + 1;
	public static final int HANDLER_INIT_DATABASE = 0x1234 + 2;
	public static final int HANDLER_NO_INTERNET = 0x1234 + 3;
	public static final int HANDLER_DOWNLOAD_COMPLETE = 0x1234 + 4;
	public static final int HANDLER_NO_SPACE_LEFT = 0x1234 + 5;
	public static final int HANDLER_TO_BE_PROMPT = 0x1234 + 6;

	public static final int DRUG_COLID = 31;
	public static final int CALC_COLID = 32;
	public static final int NEWS_COLID = 40;
	public static final int EVENTS_COLID = 41;
	public static final int RESEARCH_COLID = 42;
	public static final int ECG_COLID = 11;
	public static final int TNM_COLID = 13;
	public static final int DRUG_ALERT_COLID = 12;


	/** 本地MySqlLite.db的版本 */
	public static final int MY_SQLITE_VESION = 8;

	/** MENU 标记 */

	public final static int FLAG_ABOUT = 0x1;

	public final static int FLAG_MY_JIBO = 0x2;

	public final static int FLAG_SUGGESTION = 0x4;
	/***/
	public final static int FLAG_SETUP = 0x8;

	public final static int FLAG_HELP = 0x10;

	public final static int FLAG_HISTROY = 0x20;

	public final static int FLAG_SHARE = 0x40;

	public final static int FLAG_SYNCH = 0x80;

	public final static int FLAG_DIALOG_QIUT = 0x100;

	public final static int MENU_TYPE_1 = FLAG_ABOUT | FLAG_SHARE | FLAG_SETUP
			| FLAG_DIALOG_QIUT;
	public final static int MENU_TYPE_2 = FLAG_SUGGESTION | FLAG_HELP
			| FLAG_SHARE | FLAG_DIALOG_QIUT;
	public final static int MENU_TYPE_3 = FLAG_MY_JIBO | FLAG_HELP
			| FLAG_HISTROY | FLAG_DIALOG_QIUT;
	public final static int MENU_TYPE_4 = FLAG_SUGGESTION | FLAG_HELP
			| FLAG_SETUP | FLAG_DIALOG_QIUT;
	public final static int MENU_TYPE_5 = FLAG_SUGGESTION | FLAG_HELP
			| FLAG_SYNCH | FLAG_DIALOG_QIUT;
	public final static int MENU_TYPE_6 = FLAG_SUGGESTION | FLAG_HISTROY
			| FLAG_SHARE | FLAG_SETUP;
	public final static String SPACE = "";
	

	// 新浪微博OAuth2.0
	public static final String CONSUMER_KEY_SINA = "3828310763";
	public static final String CONSUMER_SECRET_SINA = "8ea5b8ed555ceaa97bd0fc7784cfd485";
	public static final String CALLBACK_URL_SINA = "http://www.jibo.cn";// OAuth2.0新增的授权回调页，与开发者平台中设置的回调一致\

	public static final String CONSUMER_KEY_QQ = "801180236";
	public static final String CONSUMER_SECRET_QQ = "4d7150b3bce545ef524475f7d13d9ca8";
	public static final String CALLBACK_URL_QQ = "http://jibo.cn/ot/download.asp";
	
	public static final String JIBO_SINA_ID = "2332125541";// 集博新浪官方微博id

	public static boolean isErrorInstall = false;
	
	public static String alarmIntentAction = "com.jibo.gbapp.alarm.ALARM_ALERT";
	public static int alarmId = 10000;
	public static int getnextNotificationId(){
		return ++alarmId;
	}
	/**调试标记*/
	public static boolean DEBUG = false;
	public static boolean testinFlag = false;
	//当前程序对应db版本
	public static final String dbVersion = "3";
	/*闹钟间隔时间*/
	public static final long intervalTimeMills = (long) 1 * 60 * 1000;
	public static final String interation_history = "interaction";
	
}