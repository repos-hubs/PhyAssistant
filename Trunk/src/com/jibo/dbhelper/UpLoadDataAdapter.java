package com.jibo.dbhelper;

import android.content.Context;

import com.jibo.common.Constant;

public class UpLoadDataAdapter extends BaseSqlAdapter {
	public UpLoadDataAdapter(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}
	
	
	public  int insertLog( String[] log) {
		try {
			String sql = "INSERT INTO Log (userName, licenseNumber, page_ID, DetailID, DetailIdName, Demo) "
					+ " VALUES (?, ?, ?, ?, ?, ?)";

			excuteSql(sql, log);

			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			// close();
		}
	}
	
	
}
