package com.jibo.dbhelper;

import java.io.File;
import java.io.IOException;

import com.jibo.common.Constant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ConfigAdapter {
	private Context ctx;
	private final static String DB_CONFIG = "config.db";
	private String dbName;
	private SQLiteDatabase sdb;
	private String title;
	public ConfigAdapter(Context ctx, String title) {
		if (dbName == null)
			dbName = Constant.DATA_PATH + File.separator + DB_CONFIG;
		
		this.title = title;
		try {
			File f = new File(dbName);
			if(!f.exists()) {
				f.createNewFile();
				sdb = SQLiteDatabase.openOrCreateDatabase(f, null);
				String createSql = "CREATE TABLE IF NOT EXISTS config (title varchar, value text);";
				sdb.execSQL(createSql);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(sdb==null) {
			sdb = SQLiteDatabase.openOrCreateDatabase(dbName, null);
		}
	}
	
	public String getConfigInfo() {
		String result = "0";
		String sql = "select value from config where title='"+title+"'";
		Cursor cursor = sdb.rawQuery(sql, null);
		while(cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		
		return result;
	}
	
	public boolean updateConfigInfo(String content) {
		
		boolean result = true;
		try {
			String sql = "select value from config where title='"+title+"'";
			Cursor cursor = sdb.rawQuery(sql, null);
			if(cursor.getCount()>0) {
				String updateStr = "update config set value='"+content+"' where title='"+title+"'";
				sdb.execSQL(updateStr);
			} else {
				ContentValues cv = new ContentValues();
				cv.put("title", title);
				cv.put("value", content);
				sdb.insert("config", null, cv);
			}
			cursor.close();
		} catch(Exception e) {
			e.printStackTrace();
			result = false;
		} 
		
		return result;
	}
	
	public void closeDB() {
		if(sdb!=null) sdb.close();
	}
	
}

