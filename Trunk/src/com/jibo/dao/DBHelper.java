package com.jibo.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private final static String CREATE_TABLE_KW_TA = "create table IF NOT EXISTS KW_TA"
			+ "(KW_ID int(11),"
			+ "TA_ID varchar(250),"
			+ "KW varchar(250),"
			+ "COUNT int(11))";
	private final static String CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE IF NOT EXISTS search_history "
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "title TEXT UNIQUE, " + "time TEXT);";

	private final static String CREATE_TABLE_VIEW_HISTORY = "CREATE TABLE IF NOT EXISTS view_history "
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, UID TEXT, VID TEXT, COLID INTEGER, CATEGORY INTEGER, CONTENT TEXT, TIME TEXT);";

	private final static String CREATE_TABLE_EVENTS = "create table IF NOT EXISTS Events"
			+ "(mobileID integer primary key,"
			+ "eventsID int(11),"
			+ "name varchar(250),"
			+ "content varchar(4000),"
			+ "organizer varchar(4000),"
			+ "tel varchar(250),"
			+ "fax varchar(250),"
			+ "email varchar(250),"
			+ "website varchar(250),"
			+ "place varchar(250),"
			+ "date varchar(250))";
	private final static String CREATE_TABLE_EVENTS_OLD = "create table IF NOT EXISTS OldEvents"
			+ "(mobileID integer primary key,"
			+ "eventsID int(11),"
			+ "name varchar(250),"
			+ "content varchar(4000),"
			+ "organizer varchar(4000),"
			+ "tel varchar(250),"
			+ "fax varchar(250),"
			+ "email varchar(250),"
			+ "website varchar(250),"
			+ "place varchar(250),"
			+ "date varchar(250))";
	private final static String CREATE_TABLE_QUESTION = "create table IF NOT EXISTS Question"
			+ "(Qid varchar(250) primary key,"
			+ "Qtype varchar(250),"
			+ "Qversion varchar(250),"
			+ "Qtitle varchar(250),"
			+ "Qcontent varchar(250))";
	private final static String CREATE_TABLE_ANSWER = "create table IF NOT EXISTS Answer"
			+ "(Aid varchar(250) primary key,"
			+ "Acontent varchar(250),"
			+ "Qid varchar(250))";
	private final static String CREATE_TABLE_QANSWER = "create table IF NOT EXISTS QAnswer"
			+ "(mobileID integer primary key,"
			+ "Qid varchar(250),"
			+ "Qtype varchar(250)," + "Aid varchar(250)," + "Uid varchar(250))";
	private final static String CREATE_TABLE_EVENTCOLLECTION = "create table IF NOT EXISTS EventCollection"
			+ "(mobileID integer primary key,"
			+ "eventsID int(11),"
			+ "name varchar(250),"
			+ "userName varchar(250),"
			+ "place varchar(250)," + "date varchar(250))";
	private final static String CREATE_TABLE_RESEARCHCOLLECTION = "create table IF NOT EXISTS ResearchCollection"
			+ "(mobileID integer primary key,"
			+ "articleID int(11),"
			+ "title varchar(250),"
			+ "userName varchar(250),"
			+ "jourName varchar(250),"
			+ "author varchar(250),"
			+ "taId varchar(250)," + "catetype varchar(250))";
	private final static String CREATE_TABLE_NEWSCOLLECTION = "create table IF NOT EXISTS NewsCollection"
			+ "(mobileID integer primary key,"
			+ "newsID int(11),"
			+ "title varchar(250),"
			+ "userName varchar(250),"
			+ "date varchar(250))";

	private final static String CREATE_TABLE_DRUGALERTCOLLECTION = "create table IF NOT EXISTS DrugAlertCollection"
			+ "(mobileID integer primary key,"
			+ "typeID varchar(250),"
			+ "title varchar(250)," + "userName varchar(250))";

	private final static String CREATE_TABLE_TABHEARTCOLLECTION = "create table IF NOT EXISTS TabHeartCollect"
			+ "(mobileID integer primary key,"
			+ "title varchar(250),"
			+ "userName varchar(250)," + "position int(25))";
	private final static String CREATE_TABLE_TABCalc = "create table IF NOT EXISTS TabCalc"
			+ "(mobileID integer primary key,"
			+ "Calctitle varchar(250),"
			+ "userName varchar(250)," + "class varchar(250))";
	private final static String CREATE_TABLE_TABTUMOR = "create table IF NOT EXISTS TabTumor"
			+ "(mobileID integer primary key,"
			+ "rank varchar(250),"
			+ "area_cn varchar(250),"
			+ "userName varchar(250),"
			+ "area_en varchar(250))";
	private final static String CREATE_TABLE_DRUGReference = "create table IF NOT EXISTS TabDrugReference"
			+ "(mobileID integer primary key,"
			+ "drugId varchar(250),"
			+ "userName varchar(250)," + "drugName varchar(250))";

	/**
	 * 用药安全
	 */
	/*----------------start-------------------*/
	private final static String CREATE_TABLE_DrugAlertNews = new StringBuffer(
			"CREATE TABLE IF NOT EXISTS DrugAlertNews (")
			.append(" _ID INTEGER PRIMARY KEY AUTOINCREMENT,")
			.append(" id INTEGER NOT NULL,").append(" Type_ID NVARCHAR(250),")
			.append(" title NVARCHAR(250) ,").append(" new_date TEXT,")
			.append(" new_time TEXT,").append(" source NVARCHAR(50),")
			.append(" content NVARCHAR(4000),")
			.append(" product_name_cn NVARCHAR(250),")
			.append(" category NVARCHAR(100), ")
			.append(" cacheCategory NVARCHAR(20))").toString();

	private final static String CREATE_TABLE_DrugAlertDetail = new StringBuffer(
			"CREATE TABLE IF NOT EXISTS DrugAlertDetail (")
			.append(" _ID INTEGER PRIMARY KEY AUTOINCREMENT,")
			.append(" TYPE_ID NVARCHAR(250) NOT NULL,")
			.append(" DETAIL_ID INTEGER NOT NULL,")
			.append(" DETAIL_TITLE NVARCHAR(250),")
			.append(" DETAIL_CONTENT TEXT)").toString();

	private final static String CREATE_TABLE_TopDrugAlertDetail = new StringBuffer(
			"CREATE TABLE IF NOT EXISTS TopDrugAlertDetail (")
			.append(" _ID INTEGER PRIMARY KEY AUTOINCREMENT,")
			.append(" TYPE_ID NVARCHAR(250) NOT NULL,")
			.append(" NEW_TIME TEXT NOT NULL,CREATE_TIME TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime')))")
			.toString();
	/*--------------end---------------------*/

	private final static String CREATE_TABLE_PROFILE = new StringBuffer(
			"CREATE TABLE IF NOT EXISTS PROFILE (")
			.append(" _ID INTEGER PRIMARY KEY,").append(" name TEXT UNIQUE,")
			.append(" rankInChina TEXT,").append(" rankInHospital TEXT,")
			.append(" paperCount TEXT,").append(" coauthorCount TEXT,")
			.append(" cahsp TEXT,").append(" caohsp TEXT,")
			.append(" keywords TEXT,").append(" cagrp TEXT,")
			.append(" bigSpecialty TEXT,").append(" specialty TEXT,")
			.append(" hospitalName TEXT)").toString();

	// 登录配置信息
	String CREATE_TABLE_LoginConfig = new StringBuffer(
			"CREATE TABLE IF NOT EXISTS LoginConfig (")
			.append(" _ID INTEGER PRIMARY KEY AUTOINCREMENT,")
			.append(" username NVARCHAR(250) NOT NULL,")
			.append(" password NVARCHAR(250) NOT NULL,")
			.append(" isSave VARCHAR(1) NOT NULL,")
			.append(" isAuto VARCHAR(1) NOT NULL )").toString();

	/**
	 * 新闻
	 */
	String CREATE_TABLE_NEWS_TOP = "CREATE TABLE IF NOT EXISTS TopNews ("
			+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT," + "id INTEGER NOT NULL,"
			+ "title NVARCHAR(250)," + "new_date TEXT,"
			+ "source NVARCHAR(250)," + "content NVARCHAR(4000),"
			+ "newesttime TEXT," + "typeID NVARCHAR(250),"
			+ "new_source NVARCHAR(250))";

	String CREATE_TABLE_NEWS = "CREATE TABLE IF NOT EXISTS News ("
			+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT," + "id INTEGER NOT NULL,"
			+ "title NVARCHAR(250) ," + "new_date TEXT,"
			+ "source NVARCHAR(250)," + "new_time TIMESTAMP,"
			+ "category NVARCHAR(100)," + "content NVARCHAR(4000),"
			+ "typeID NVARCHAR(250)," + "new_source NVARCHAR(250))";

	String CREATE_TABLE_NEWS_CATEGORIES = "CREATE TABLE IF NOT EXISTS NewsCategories ("
			+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "category NVARCHAR(250) ,"
			+ "newscount INTEGER,"
			+ "newesttime NVARCHAR(250)," + "bigCategory NVARCHAR(250)" + ")";
	String CREATE_TABLE_LOG = "CREATE TABLE IF NOT EXISTS Log ("
			+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "userName NVARCHAR(250)," + "licenseNumber NVARCHAR(250),"
			+ "page_ID NVARCHAR(250)," + "DetailID NVARCHAR(250),"
			+ "DetailIdName NVARCHAR(250)," + "Demo NVARCHAR(250)" + ")";

	String CREATE_TABLE_ADVERTISING = "CREATE TABLE IF NOT EXISTS ADVERTISING ("
			+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "username NVARCHAR(250),"
			+ "companyname NVARCHAR(250),"
			+ "imageurl NVARCHAR(250)" + ")";

	public DBHelper(Context context, String name, int version,
			boolean isSDCardDB) {
		super(context, name, null, version);
		this.dbname = name;
		this.dbversion = version;
		this.isSDCardDB = isSDCardDB;
	}

	private String dbname;// ------------数据库名
	private int dbversion;// ------------数据库版本
	private boolean isSDCardDB;// -------是否外部数据库
	private SQLiteDatabase db;

	public String getDbname() {
		return dbname;
	}

	public int getDbversion() {
		return dbversion;
	}

	public boolean isSDCardDB() {
		return isSDCardDB;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (!isSDCardDB) {
			db.execSQL(CREATE_TABLE_SEARCH_HISTORY);
			db.execSQL(CREATE_TABLE_VIEW_HISTORY);
			db.execSQL(CREATE_TABLE_KW_TA);
			db.execSQL(CREATE_TABLE_EVENTS);
			db.execSQL(CREATE_TABLE_EVENTS_OLD);
			db.execSQL(CREATE_TABLE_QUESTION);
			db.execSQL(CREATE_TABLE_ANSWER);
			db.execSQL(CREATE_TABLE_QANSWER);
			db.execSQL(CREATE_TABLE_EVENTCOLLECTION);
			db.execSQL(CREATE_TABLE_RESEARCHCOLLECTION);
			db.execSQL(CREATE_TABLE_NEWSCOLLECTION);
			db.execSQL(CREATE_TABLE_DRUGALERTCOLLECTION);
			db.execSQL(CREATE_TABLE_TABHEARTCOLLECTION);
			db.execSQL(CREATE_TABLE_TABCalc);
			db.execSQL(CREATE_TABLE_TABTUMOR);
			db.execSQL(CREATE_TABLE_DRUGReference);
			db.execSQL(CREATE_TABLE_DrugAlertNews);
			db.execSQL(CREATE_TABLE_DrugAlertDetail);
			db.execSQL(CREATE_TABLE_TopDrugAlertDetail);
			db.execSQL(CREATE_TABLE_PROFILE);
			db.execSQL(CREATE_TABLE_LoginConfig);
			db.execSQL(CREATE_TABLE_NEWS_TOP);
			db.execSQL(CREATE_TABLE_NEWS);
			db.execSQL(CREATE_TABLE_NEWS_CATEGORIES);
			db.execSQL(CREATE_TABLE_LOG);
			db.execSQL(CREATE_TABLE_ADVERTISING);// 广告页
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

	public void finalize() throws Throwable {
		close();
		Log.i("ssss", "数据库已经关闭");
	}

	public SQLiteDatabase getSQLiteDatabase() {
		db = getWritableDatabase();
		Log.i("ssss", "数据库已经重新打开");
		return db;
	}

	public void setSQLiteDatabase(SQLiteDatabase db) {
		this.db = db;
	}

	public boolean isDBNotFree() {
		return db != null
				&& (db.isDbLockedByCurrentThread() || db
						.isDbLockedByOtherThreads());
	}

}
