package com.jibo.base.src.request.impl.db;

import android.content.Context;

import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.dbhelper.BaseSqlAdapter;
import com.jibo.dbhelper.MySqlLiteHelper;

public class MySqlLiteAdapter extends SQLiteAdapter {

	private Object context;
	public static MySqlLiteAdapter mySqlLiteAdapter = new MySqlLiteAdapter(
			GBApplication.gbapp,MySqlLiteHelper.DB_NAME);

	public static MySqlLiteAdapter getInstance(){
		return mySqlLiteAdapter;
	}
	
	public MySqlLiteAdapter(Context context,String dbname) {
		// TODO Auto-generated constructor stub
		super(dbname);
		this.context = context;
		mDbHelper = new MySqlLiteHelper(context, dbname,Constant.MY_SQLITE_VESION);
	}

}
