package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;
import android.util.Log;

import com.api.android.GBApp.R;import com.jibo.common.Constant;
import com.jibo.util.Logs;


public class StoreSearchHistoryAdapter extends BaseSqlAdapter {
	private final static String TABLE_CALCULATE = "mysqllite.db";
	private static String dbName;
	private Context context;
	public StoreSearchHistoryAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = ctx.getFilesDir() + File.separator + TABLE_CALCULATE;
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}
	public void storeSearchHistory(String a2,String category) {
		if(TextUtils.isEmpty(category)){
			storeSearchHistory(a2);
		}else{
			long time = System.currentTimeMillis();
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(time);
			int mMonth = mCalendar.get(Calendar.MONTH);
			int mDate = mCalendar.get(Calendar.DATE);

			try {
				String sqlstr = "INSERT INTO search_history (title, time, category)" + "VALUES ('" + a2 + "'," + "'" + mMonth
						+ ":" + mDate + "','" + category+ "')";
				Log.i("sqlstr", sqlstr);
				mDbHelper.getWritableDatabase().execSQL(sqlstr);
				
			} catch (SQLException e) {
				Logs.e(e.getMessage());
			} finally {
			}
		}
	}
	/**
	 * @author Rafeal Piao
	 * @Description Store search history in Application
	 * @param Context,
	 *            String
	 * @return void
	 */
	public void storeSearchHistory(String a2) {
		long time = System.currentTimeMillis();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		int mMonth = mCalendar.get(Calendar.MONTH);
		int mDate = mCalendar.get(Calendar.DATE);

		try {
			String sqlstr = "INSERT INTO search_history (title, time)" + "VALUES ('" + a2 + "'," + "'" + mMonth
			+ ":" + mDate + "')";
			Log.i("sqlstr", sqlstr);
			mDbHelper.getWritableDatabase().execSQL(sqlstr);
			
		} catch (SQLException e) {
			Logs.e(e.getMessage());
		} finally {
		}
	}
	
	/**
	 * @author Rafeal.Piao
	 * @Description Get Search History
	 * @param Context
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getHistory() {
		String sql = "select TITLE,TIME from search_history";
		Cursor cursor = getCursor(sql, null);
		ArrayList<String> lst = new ArrayList<String>();

		while (cursor.moveToNext()) {
			String result = cursor.getString(0)
					+ ((Activity) context).getResources().getString(
							R.string.str_split) + cursor.getString(1);
			lst.add(0, result);
		}
		cursor.close();
		closeDB();
		return lst;
	}
}
