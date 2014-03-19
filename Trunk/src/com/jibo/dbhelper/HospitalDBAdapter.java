package com.jibo.dbhelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jibo.common.Constant;
import com.jibo.entity.Hospital;
import com.jibo.entity.HospitalCity;
import com.jibo.entity.HospitalProvince;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 注册医院数据库
 * @author will
 * @create 2013-4-23 下午2:08:19
 */
public class HospitalDBAdapter extends BaseSqlAdapter {
	public final String DB_NAME = "hospital.db";
	private Context context;
	private int BUFFER_SIZE = 1024;

	public HospitalDBAdapter(Context ctx) {
		this.context = ctx;
		String dataBaseName = context.getFilesDir() + "/" + DB_NAME;
		initDatabase(dataBaseName);
		mDbHelper = new DrugHelper(ctx, dataBaseName, null, 1);
	}

	/**
	 * 初始化数据库
	 * 
	 * @param context
	 */
	public void initDatabase(String dataBaseName) {
		if (!(new File(dataBaseName).exists())) {
			InputStream myInput;
			try {
				FileOutputStream myOutput = new FileOutputStream(dataBaseName);
				for (int i = 1; i <= 3; i++) {
					myInput = context.getAssets().open(DB_NAME + ".00" + i);
					byte[] buffer = new byte[BUFFER_SIZE];
					int length;
					while ((length = myInput.read(buffer)) > 0) {
						myOutput.write(buffer, 0, length);
					}
					myOutput.flush();
					myInput.close();
				}
				myOutput.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 取得省份
	 * @return
	 */
	public ArrayList<HospitalProvince> getProvince(){
		String sql = "select * from hospital_region";
		ArrayList<HospitalProvince> list = new ArrayList<HospitalProvince>();
		try {
			Cursor c = getCursor(sql, null);
			if (c != null && c.getCount() > 0) {
				while (c.moveToNext()) {
					HospitalProvince entity = new HospitalProvince();
					entity.setName(c.getString(c.getColumnIndex("add_province")));
					list.add(entity);
				}
				c.close();
			}
		} finally {
			closeDB();
		}
		return list;
	}
	
	/**
	 * 取得城市
	 * @param province
	 * @return
	 */
	public ArrayList<HospitalCity> getCityByProvince(String province){
		String sql = "select * from hospital_city where add_province = '" + province + "'";
		ArrayList<HospitalCity> list = new ArrayList<HospitalCity>();
		try {
			Cursor c = getCursor(sql, null);
			if (c != null && c.getCount() > 0) {
				while (c.moveToNext()) {
					HospitalCity entity = new HospitalCity();
					entity.setCityName(c.getString(c.getColumnIndex("add_city")));
					list.add(entity);
				}
				c.close();
			}
		} finally {
			closeDB();
		}
		return list;
	}
	
	public ArrayList<Hospital> getHospitalName(String province, String city, String key){
		String sql = "select * from hospital_name where add_province = '" 
				+ province + "' and add_city = '" + city + "'";
		if(!TextUtils.isEmpty(key)){
			sql = sql + " and name like '%" + key + "%'";
		}
		ArrayList<Hospital> list = new ArrayList<Hospital>();
		try {
			Cursor c = getCursor(sql, null);
			if (c != null && c.getCount() > 0) {
				while (c.moveToNext()) {
					Hospital entity = new Hospital();
					entity.setName(c.getString(c.getColumnIndex("name")));
					entity.setNamePY(c.getString(c.getColumnIndex("namePY")));
					list.add(entity);
				}
				c.close();
			}
		} finally {
			closeDB();
		}
		
		/** 按拼音首字母排序 */
		if(null != list && list.size() > 0){
			Collections.sort(list, new Comparator<Hospital>() {
				@Override
				public int compare(Hospital obj1, Hospital obj2) {
					return ((int) obj1.getNamePY().charAt(0))
							- ((int) obj2.getNamePY().charAt(0));
				}
			});
		}
		return list;
	}

	private class DrugHelper extends SQLiteOpenHelper {
		public DrugHelper(Context context, String name, CursorFactory factory,
				int version) {
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
