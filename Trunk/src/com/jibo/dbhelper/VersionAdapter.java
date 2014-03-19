/**
 * 
 */
package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.jibo.common.Constant;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author peter.pan Version.db²Ù×÷Àà
 * 
 */
public class VersionAdapter extends BaseSqlAdapter {

	private final static String DB_VERSION= "version.db";
	private static String dbName;
    private static String tableName ="version";
	public VersionAdapter(Context ctx, int vesion) {
		if (dbName == null)
			dbName =Constant.DATA_PATH+ File.separator + DB_VERSION;
		mDbHelper = new VersionHelper(ctx, dbName, null, vesion);
	}


	/**
	 * @author Rafeal Piao
	 * @description Get current database's version
	 * @param act: Context
	 * @return String£ºDatabase Version
	 * @Exception 
	 */
	public  String getCurrentDBCode() {
		String dbCode = Constant.CURRENT_DB;
		try {
			
			String sql = "select * from "+tableName;
			Cursor cursor =getCursor(sql, null);
			cursor.moveToFirst();
			if(cursor.getCount() > 0) {
				dbCode = cursor.getString(2);
				
			} 
			cursor.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbCode;
	}
	
	/**
	 * @author Rafeal Piao
	 * @description Set current database's version
	 * @param act: Context
	 * @return String£ºDatabase Version
	 * @Exception 
	 */
	public  void setCurrentDBCode(String dbVersion) {
		try {
			String sql = "update "+tableName+" set [database] ='"+dbVersion+"' where id =1";
			System.out.println("set CurrentVersion   ");
			excuteSql(sql);
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class VersionHelper extends SQLiteOpenHelper {

		public VersionHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}
}
