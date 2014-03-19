package com.jibo.dbhelper;

import com.jibo.common.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqlLiteHelper extends SQLiteOpenHelper {

	private static int version;
	public final static String DB_NAME = "mysqllite.db";
	public boolean bFirstCreate;
	private final static String CREATE_TABLE_KW_TA = "create table IF NOT EXISTS KW_TA"
			+ "(KW_ID int(11),"
			+ "TA_ID varchar(250),"
			+ "KW varchar(250),"
			+ "COUNT int(11))";
	private final static String CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE IF NOT EXISTS search_history "
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "title TEXT UNIQUE, " + "time TEXT);";

	private final static String CREATE_TABLE_VIEW_HISTORY = "CREATE TABLE IF NOT EXISTS view_history (_id INTEGER PRIMARY KEY AUTOINCREMENT, UID TEXT, VID TEXT, COLID INTEGER, CATEGORY INTEGER, CONTENT TEXT, TIME TEXT,field1 TEXT,field2 TEXT,field3 TEXT,field4 TEXT,field5 TEXT);";

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
			+ "PaperID varchar(250),"
			+ "Title varchar(250),"
			+ "JournalName varchar(250),"
			+ "JournalAbbrName varchar(250),"
			+ "IF varchar(250),"
			+ "PublicDate varchar(250),"
			+ "Status varchar(250),"
			+ "IsFreeFullText varchar(250)," + "userName varchar(250))";
	private final static String CREATE_TABLE_PAPER_DETAIL = "create table IF NOT EXISTS PaperDetail"
			+ "(mobileID integer primary key,"
			+ "id TEXT,"
			+ "title TEXT,"
			+ "journalID TEXT,"
			+ "publicDate TEXT,"
			+ "keyWords TEXT,"
			+ "topRank TEXT,"
			+ "rank TEXT,"
			+ "isFreeFullText TEXT,"
			+ "comments TEXT,"
			+ "commentsType TEXT,"
			+ "veiwedCount TEXT,"
			+ "abstarct TEXT,"
			+ "authors TEXT,"
			+ "publicationType TEXT,"
			+ "sourceURL TEXT,"
			+ "freeFullTextURL TEXT,"
			+ "pdfURL TEXT,"
			+ "IF TEXT,"
			+ "affiliations TEXT,"
			+ "journalName TEXT,"
			+ "CLC TEXT,"
			+ "pubTypes TEXT,"
			+ "DOI TEXT,"
			+ "language TEXT,"
			+ "mesh TEXT," + "substances TEXT," + "categorys TEXT)";
	private final static String CREATE_TABLE_SUBSCRIBE = "create table IF NOT EXISTS ResearchSubscribe"
			+ "(mobileID integer primary key,"
			+ "SubscibeStatus varchar(250),"
			+ "PeriodicalID varchar(250),"
			+ "PeriodicalName varchar(250),"
			+ "userName varchar(250),"
			+ "PeriodicalCount varchar(250),"
			+ "UpdateTime varchar(250))";
	// private final static String CREATE_TABLE_RESEARCHCOLLECTION =
	// "create table IF NOT EXISTS ResearchCollection"
	// + "(mobileID integer primary key,"
	// + "articleID int(11),"
	// + "title varchar(250),"
	// + "userName varchar(250),"
	// + "jourName varchar(250),"
	// + "author varchar(250),"
	// + "taId varchar(250)," + "catetype varchar(250))";

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
			+ "userName varchar(250),"
			+ "drugName varchar(250),adminRouteId varchar(10),adminRouteName varchar(100),ahfsInfo varchar(5),mode integer)";

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
	/** 论文下载 */
	String CREATE_TABLE_PAPER_DOWNLOAD = "CREATE TABLE IF NOT EXISTS PaperDownload ("
			+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "paperID NVARCHAR(250),"
			+ "url NVARCHAR(250),"
			+ "imageurl NVARCHAR(250),"
			+ "fileName NVARCHAR(250),"
			+ "state NVARCHAR(250),"
			+ "title NVARCHAR(250),"
			+ "remarks NVARCHAR(250),"
			+ "others NVARCHAR(250),"
			+ "username NVARCHAR(250)," 
			+ "periodicalTitle NVARCHAR(250)," 
			+ "IF NVARCHAR(250)," 
			+ "date NVARCHAR(250)" 
			+ ")";
	/** 断点续传下载*/
	private String CREATE_TABLE_DOWNLOAD_RESUME = "CREATE TABLE IF NOT EXISTS download_info(" +
			"_id integer PRIMARY KEY AUTOINCREMENT, " +
			"thread_id integer," + 
			"start_pos long," +
			"end_pos long," +
			"compelete_size long," +
			"file_size long," +
			"url char," +
			"special_id TEXT," +
			"download_state boolean default 'false'," +
			"title TEXT," +
			"username TEXT" +
			")";
	/** 厂商专辑包列表 */
	private String CREATE_TABLE_SPECIAL_COLLECTION = "CREATE TABLE IF NOT EXISTS special_list("
			+ "_id integer PRIMARY KEY AUTOINCREMENT, "
			+ "key TEXT,"
			+ "companyName TEXT,"
			+ "department TEXT,"
			+ "name TEXT,"
			+ "iconUrl TEXT,"
			+ "downloadLink TEXT,"
			+ "activeStamp TEXT,"
			+ "invalidStamp TEXT," + "username TEXT" + ")";

	public MySqlLiteHelper(Context context, int version) {
		// 第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
		super(context, DB_NAME, null, version);
	}

	public MySqlLiteHelper(Context context, String dbname, int version) {
		// 第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
		super(context, dbname, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
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
		db.execSQL(CREATE_TABLE_SUBSCRIBE);
		db.execSQL(CREATE_TABLE_PAPER_DETAIL);

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

		db.execSQL(CREATE_TABLE_PAPER_DOWNLOAD);
		db.execSQL(CREATE_TABLE_DOWNLOAD_RESUME);
		db.execSQL(CREATE_TABLE_SPECIAL_COLLECTION);
		db.execSQL("alter table search_history add category CHAR(11)");
		db.execSQL("alter table search_history add isLatest char(2) default '0'");// 默认是0,1表示最近查询
		bFirstCreate = false;
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("drop table IF EXISTS 'view_history'");
		db.execSQL("drop table IF EXISTS 'TabDrugReference'");

		db.execSQL(CREATE_TABLE_DRUGReference);
		db.execSQL(CREATE_TABLE_VIEW_HISTORY);
		db.execSQL("alter table search_history add category CHAR(11)");
		db.execSQL("alter table search_history add isLatest char(1) default '0'");// 默认是0,1表示最近查询

		if (Constant.MY_SQLITE_VESION < 7) {
			db.execSQL("drop table IF EXISTS 'ResearchCollection'");
		}

		onCreate(db);
	}
}
