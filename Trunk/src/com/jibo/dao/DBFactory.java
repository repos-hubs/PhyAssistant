package com.jibo.dao;

import java.io.File;

import com.jibo.base.src.request.impl.db.SqliteAdapterCentre;
import com.jibo.common.Constant;

import android.R;
import android.content.Context;
import android.database.Cursor;
/**
 * DB工厂
 * @author simon
 *
 */
public class DBFactory {
	public static final int SDCard_SCHEMA_VERSION = Integer.parseInt(Constant.dbVersion);
	public static String SDCard_DB_NAME = Constant.DATA_PATH_GBADATA+ "/jibo.db";
	public static final int LOCAL_SCHEMA_VERSION = Constant.MY_SQLITE_VESION;
	private final static String LOCAL_DB_NAME = "mysqllite.db";

	private static DBHelper localHelper;//---------内部创建的db
	private static DBHelper sdCardHelper;//--------sd卡上的下载db

	public static DBHelper getLocalDbHelper(Context context) {// 本地DB
		
		if (localHelper == null) {
			localHelper = new DBHelper(context, LOCAL_DB_NAME,
					LOCAL_SCHEMA_VERSION, false);
			localHelper.setSQLiteDatabase(localHelper.getWritableDatabase());
		}
		while (localHelper.isDBNotFree()) {
			// db is locked, keep looping
		}
		return localHelper;
	}

	public static DBHelper getSDCardDbHelper(Context context) {// 外部DB
		if (sdCardHelper == null) {
			sdCardHelper = new DBHelper(context,SDCard_DB_NAME, SDCard_SCHEMA_VERSION
					, true);
			SqliteAdapterCentre.getInstance().renew(DBFactory.SDCard_DB_NAME);
			
			try {
				sdCardHelper.setSQLiteDatabase(SqliteAdapterCentre.getInstance().get(DBFactory.SDCard_DB_NAME).getDb());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while (sdCardHelper.isDBNotFree()) {
			// db is locked, keep looping
		}
		return sdCardHelper;
	}

}
