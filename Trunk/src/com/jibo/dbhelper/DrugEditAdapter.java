package com.jibo.dbhelper;

import java.io.File;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DrugEditAdapter {
	private String userName;
	private String drugId;
	private Context ctx;
	private final static String DB_DRUG_EDIT_INFO = "drug_edit_info.db";
	private String dbName;
	private SQLiteDatabase sdb;
	public DrugEditAdapter(String userName, String id, Context ctx) {
		this.drugId = id;
		this.ctx = ctx;
		this.userName = userName;
		if (dbName == null)
			dbName = ctx.getFilesDir() + File.separator + DB_DRUG_EDIT_INFO;
		
		try {
			File f = new File(dbName);
			if(!f.exists()) {
				f.createNewFile();
				sdb = SQLiteDatabase.openOrCreateDatabase(f, null);
				String createSql = "CREATE TABLE IF NOT EXISTS drug_edit_info (uid varchar, drugid varchar, content text);";
				sdb.execSQL(createSql);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(sdb==null) {
			sdb = SQLiteDatabase.openOrCreateDatabase(dbName, null);
		}
	}
	
	public String getDrugEditInfo() {
		String result = "";
		String sql = "select content from drug_edit_info where uid='"+userName+"' and drugid='"+drugId+"'";
		Cursor cursor = sdb.rawQuery(sql, null);
		while(cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		
		return result;
	}
	
	public boolean updateDrugEditInfo(String content) {
		
		boolean result = true;
		try {
			String sql = "select content from drug_edit_info where uid='"+userName+"' and drugid='"+drugId+"'";
			Cursor cursor = sdb.rawQuery(sql, null);
			if(cursor.getCount()>0) {
				String updateStr = "update drug_edit_info set content='"+content+"' where uid='"+userName+"' and drugid='"+drugId+"'";
				sdb.execSQL(updateStr);
			} else {
				ContentValues cv = new ContentValues();
				cv.put("uid", userName);
				cv.put("drugid", drugId);
				cv.put("content", content);
				sdb.insert("drug_edit_info", null, cv);
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

