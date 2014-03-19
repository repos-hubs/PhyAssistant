package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;

import com.jibo.common.Constant;
import com.jibo.data.entity.ECGEntity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;


public class ECGAdapter extends BaseSqlAdapter {
	private final static String TABLE_CALCULATE = "ecg.db";
	private static String dbName;
	private Context context;
	public ECGAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = Constant.DATA_PATH_MARKET_INSTALL + File.separator + TABLE_CALCULATE;
		mDbHelper = new ECGHelper(ctx, dbName, null, vesion);
	}
	
	/**
	 * @description Get ECG Content List
	 * @param void
	 * @return ArrayList<ECGEntity>: ECG对象列表
	 * @Exception 
	 */
	public ArrayList<ECGEntity> getECGList() {
		ArrayList<ECGEntity> ecgList = new ArrayList<ECGEntity>();
		String str = "select * from ecg";
		Cursor cursor = getCursor(str, null);
		while(cursor.moveToNext()) {
			ECGEntity entity = new ECGEntity();
			entity.setId(cursor.getString(0));
			entity.setTitle(cursor.getString(1));
			entity.setContent(cursor.getString(2));
			ecgList.add(entity);
		}
		cursor.close();
		closeDB();
		return ecgList;
	}
	
	/**
	 * @description Get ECG By ID
	 * @param id:ECG ID
	 * @return ECGEntity: ECG对象列表
	 * @Exception 
	 */
	public ECGEntity getECGByID(String id) {
		String str = "select * from ecg where ID ='"+id+"'";
		ECGEntity entity = null;
		Cursor cursor = getCursor(str, null);
		while(cursor.moveToNext()) {
			entity = new ECGEntity();
			entity.setId(cursor.getString(0));
			entity.setTitle(cursor.getString(1));
			entity.setContent(cursor.getString(2));
		}
		cursor.close();
		closeDB();
		return entity;
	}
	
	
	class ECGHelper extends SQLiteOpenHelper {
		public ECGHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
