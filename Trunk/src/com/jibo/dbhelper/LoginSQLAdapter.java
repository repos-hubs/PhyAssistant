package com.jibo.dbhelper;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jibo.common.Constant;
import com.jibo.data.entity.AdvertisingEntity;
import com.jibo.data.entity.LoginConfigEntity;

/**
 * 登录界面db工具
 * 
 * @author simon
 * 
 */
public class LoginSQLAdapter extends BaseSqlAdapter {

	public LoginSQLAdapter(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}

	/**
	 * 插入登录信息
	 * 
	 * @param context
	 * @param config
	 * @return
	 */
	public boolean insertLoginConfig(Context context, LoginConfigEntity config) {
		String insertSql = "insert into LoginConfig (username,password,isSave,isAuto) values('"
				+ config.userName
				+ "','"
				+ config.passWord
				+ "','"
				+ config.isSave + "','" + config.isAuto + "')";
		String deleteSql = "delete from LoginConfig where username = '"
				+ config.userName + "'";
		if (excuteSql(deleteSql))
			return excuteSql(insertSql);
		return false;
	}

	/**
	 * 删除登录信息
	 * 
	 * @param context
	 * @param config
	 * @return
	 */
	public boolean deleteLoginConfig(Context context, String userName) {
		String deleteSql = "delete from LoginConfig where username = '"
				+ userName + "'";
		return excuteSql(deleteSql);
	}

	/**
	 * 获取登录历史信息
	 * 
	 * @param context
	 * @return
	 */
	public ArrayList<LoginConfigEntity> getLoginConfigs(Context context,
			String userName) {
		SQLiteDatabase db = null;
		ArrayList<LoginConfigEntity> list = null;

		try {
			list = new ArrayList<LoginConfigEntity>();
			String sql = "SELECT * FROM LoginConfig ";

			if (null != userName && !"".equals(userName)) {
				sql += " where username = '" + userName.trim() + "'";
			}
			Cursor c = getCursor(sql, null);
			if (c != null) {
				while (c.moveToNext()) {
					LoginConfigEntity config = new LoginConfigEntity();
					config.userName = c.getString(c.getColumnIndex("username"));
					config.passWord = c.getString(c.getColumnIndex("password"));
					config.isSave = c.getString(c.getColumnIndex("isSave"));
					config.isAuto = c.getString(c.getColumnIndex("isAuto"));
					list.add(config);
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return list;
	}

	/**
	 * 获取广告信息
	 * @param userName
	 * @return
	 */
	public AdvertisingEntity getAdvertising(String userName) {
		SQLiteDatabase db = null;
		AdvertisingEntity bean = null;
		try {
			String sql = "SELECT * FROM ADVERTISING ";
			sql += " where username = '" + userName.trim() + "'";
			
			Cursor c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToNext()) {
					bean = new AdvertisingEntity(c.getString(c
							.getColumnIndex("username")), c.getString(c
							.getColumnIndex("companyname")), c.getString(c
							.getColumnIndex("imageurl")));
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return bean;
	}

	/**
	 * 插入广告信息
	 * @param adver
	 * @return
	 */
	public boolean insertAdvertising(AdvertisingEntity adver) {
		String insertSql = "insert into ADVERTISING (username,companyname,imageurl) values('"
				+ adver.userName
				+ "','"
				+ adver.companyName
				+ "','"
				+ adver.imageUrl + "')";
		String deleteSql = "delete from ADVERTISING where username = '"
				+ adver.userName + "'";
		if (excuteSql(deleteSql))
			return excuteSql(insertSql);
		return false;
	}

}
