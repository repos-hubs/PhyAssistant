package com.jibo.dbhelper;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.jibo.common.Constant;

public class PckgUpdateAdapter extends BaseSqlAdapter {
	private final static String TABLE_DB_DATA = "pckg_update.db";
	private static String dbName;
	private Context context;
	private int flag_no_data = 0;
	private int flag_installed_need_upgrade = 2;
	private int flag_installed_no_upgrade = 3;
	public PckgUpdateAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = Constant.DATA_PATH_GBADATA + File.separator + TABLE_DB_DATA;
		mDbHelper = new PckgUpdateHelper(ctx, dbName, null, vesion);
	}
	
	public void updatePckgVersion(String cate, String version) {
		String sql = "select * from pckg_update where category='"+cate+"'";
		Cursor cursor = getCursor(sql, null);
		if(cursor.getCount()>0) {
			sql = "update pckg_update set version='"+version+"' where category='"+cate+"'";
		} else {
			sql = "insert into pckg_update(category, version) values('"+cate+"','"+version+"')";
		}
		excuteSql(sql);
		cursor.close();
		closeDB();
	}
	
	public String getCategroyStr() {
		String sql = "select * from pckg_update";
		Cursor cursor = getCursor(sql, null);
		String result = "";
		//version=1.10&cate=1|2&cate=2|1
		while(cursor.moveToNext()) {
			String cate = cursor.getString(0);
			String version = cursor.getString(1);
			result=result+"cate="+cate+"|"+version+"&";
		}
		if(!result.equals("")) {
			result = result.substring(0, result.length()-1);
		}
		cursor.close();
		closeDB();
		return result;
	}
	
	class PckgUpdateHelper extends SQLiteOpenHelper {
		public PckgUpdateHelper(Context context, String name,
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
