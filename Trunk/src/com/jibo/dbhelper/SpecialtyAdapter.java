package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;
import com.api.android.GBApp.R;
import com.jibo.common.Constant;
import com.jibo.data.entity.ECGEntity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

/**
 * 医院db工具类
 * 
 * @author simon
 * 
 */
public class SpecialtyAdapter extends BaseSqlAdapter {
	private final static String TABLE_CALCULATE = "jibo.db";
	private static String dbName;
	private Context context;

	public SpecialtyAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = Constant.DATA_PATH_GBADATA + File.separator
					+ TABLE_CALCULATE;
		mDbHelper = new SpecialtyHelper(ctx, dbName, null, vesion);
	}

	/**
	 * 获取专业 array
	 * 
	 * @return
	 */
	public String[] getSpecialtiesByLocalDatabase() {
		String sql = "SELECT distinct big_specialty FROM specialty WHERE 1=1 ORDER BY count(big_specialty) DESC";

		String[] specialties = null;
		int length = 0;
		try {
			Cursor c = getCursor(sql, null);
			int i = 1;
			if (c != null) {
				length = c.getCount();
			}
			if (length <= 0) {
				specialties = new String[1];
			} else {
				specialties = new String[length + 1];
			}
			specialties[0] = context.getString(R.string.specialty1);
			String specialty = null;
			if (c != null) {
				while (c.moveToNext()) {
					specialty = c.getString(c.getColumnIndex("big_specialty"));
					if (specialty != null)
						specialties[i] = specialty;
					else
						specialties[i] = "";
					i++;
				}
				c.close();
			}
		} finally {
			closeDB();
		}
		return specialties;
	}

	/**
	 * 获取子专业 array
	 * 
	 * @return
	 */
	public String[] getSubcategoriesByLocalDatabase(String specialty) {
		String sql = "SELECT distinct specialty FROM specialty WHERE big_specialty = '"
				+ specialty + "'";

		String[] specialties = null;
		int length = 0;
		try {
			Cursor c = getCursor(sql, null);
			int i = 1;
			if (c != null) {
				length = c.getCount();
			}
			if (length <= 0) {
				specialties = new String[1];
			} else {
				specialties = new String[length + 1];
			}
			specialties[0] = context.getString(R.string.specialty1);
			String name = null;
			if (c != null) {
				while (c.moveToNext()) {
					name = c.getString(c.getColumnIndex("specialty"));
					if (name != null)
						specialties[i] = name;
					else
						specialties[i] = "";
					i++;
				}
				c.close();
			}
		} finally {
			closeDB();
		}
		return specialties;
	}

	class SpecialtyHelper extends SQLiteOpenHelper {
		public SpecialtyHelper(Context context, String name,
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
