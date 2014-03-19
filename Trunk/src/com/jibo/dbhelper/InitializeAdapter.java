package com.jibo.dbhelper;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import com.jibo.common.Constant;


public class InitializeAdapter extends BaseSqlAdapter {
	private final static String version = "mysqllite.db";
	private static String dbName;
	private Context context;
	public InitializeAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = ctx.getFilesDir() + File.separator + version;
	}
	
	/**
	 * @author Rafeal Piao
	 * @description Get Current Ver Name
	 * @param void
	 * @return String: °æ±¾Ãû³Æ
	 * @Exception 
	 */
	public String getCurrentVerName() {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return verName;
	}
	
}
