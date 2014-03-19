package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.text.TextUtils;

import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.data.entity.ViewHistoryEntity;

public class HistoryAdapter extends BaseSqlAdapter {
	private static String dbName;
	private Context context;

	public HistoryAdapter(Context ctx, int vesion, String db) {
		this.context = ctx;
		if (dbName == null)
			dbName = ctx.getFilesDir() + File.separator + db;
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}

	/**
	 * @author Rafeal Piao
	 * @param Base
	 * @return String
	 * @Description generate xml string from database
	 */
	public String generateXML() {
		String colum[] = { "UID", "VID", "CONTENT", "COLID", "CATEGORY", "TIME" };
		GBApplication application = (GBApplication) ((Activity) context)
				.getApplication();
		String userName = "";
		if (application.getLogin() != null) {
			userName = application.getLogin().getGbUserName();
		}

		String sql = "select UID, VID, CONTENT, COLID, CATEGORY, TIME from view_history where UID ='"
				+ application.getLogin().getGbUserName() + "'";
		Cursor cursor = getCursor(sql, null);
		ArrayList<ViewHistoryEntity> lst = new ArrayList<ViewHistoryEntity>();
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		if (cursor.getCount() == 0) {
			sb.append("<item>");
			sb.append("<userid>");
			sb.append(userName);
			sb.append("</userid>");

			sb.append("<vid>");
			sb.append("</vid>");

			sb.append("<vname>");
			sb.append("</vname>");

			sb.append("<vcolid>");
			sb.append("</vcolid>");

			sb.append("<vparent>");
			sb.append("</vparent>");

			sb.append("<time>");
			sb.append("</time>");

			sb.append("</item>");
		} else {
			while (cursor.moveToNext()) {
				sb.append("<item>");

				sb.append("<userid>");
				sb.append(cursor.getString(0));
				sb.append("</userid>");

				sb.append("<vid>");
				sb.append(cursor.getString(1));
				sb.append("</vid>");

				sb.append("<vname>");
				sb.append(cursor.getString(2));
				sb.append("</vname>");

				sb.append("<vcolid>");
				sb.append(cursor.getString(3));
				sb.append("</vcolid>");

				sb.append("<vparent>");
				if (null == cursor.getString(4)) {
				} else {
					sb.append(cursor.getString(4));
				}
				sb.append("</vparent>");

				sb.append("<time>");
				sb.append(cursor.getString(5));
				sb.append("</time>");

				sb.append("</item>");
			}
		}
		sb.append("</xml>");
		cursor.close();
		closeDB();
		return sb.toString().trim();
	}

	public void syncDatabase() {
		try {
			getDb().delete("view_history", null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		closeDB();
	}

	/**
	 * @author Rafeal Piao
	 * @Description Store search history in Application
	 * @param Context
	 *            , String
	 * @return void
	 */
	public void storeHistoryFromServer(String uid, Object vid, String content,
			int colId, int category, String time) {
		try {
			getDb().execSQL(
					"INSERT INTO view_history (UID, VID, CONTENT, COLID, CATEGORY, TIME) "
							+ "VALUES ('" + uid + "'," + vid.toString() + ",'" + content
							+ "'," + colId + "," + category + ",'" + time
							+ "')");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		closeDB();
	}

	/**
	 * @author Rafeal Piao
	 * @Description Get calculation target activity from calculation's id
	 * @param context
	 * @param id
	 *            : 计算器id
	 * @return String
	 */
	public String getCalcActFromId(Context context, int id) {
		String sql = "SELECT calMobile FROM calculator_Dict WHERE id =" + id;
		Cursor cursor = getCursor(sql, null);
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		String actName = "";
		while (cursor.moveToNext()) {
			actName = cursor.getString(0);
		}

		cursor.close();
		closeDB();
		return actName;
	}

	/**
	 * @author Rafeal Piao
	 * @Description Store search history in Application
	 * @param username
	 *            : 用户名
	 * @param vid
	 *            : 唯一标识id
	 * @param colID
	 *            : 大类型
	 * @param parentID
	 *            : 小类型
	 * @param title
	 *            : 显示标题
	 * @return void
	 */
	public void storeViewHistory(String username, Object vid, int colID,
			int parentID, String title) {
		storeViewHistory(username, vid, colID, parentID, title, null, null);
	}

	/**
	 * @author Rafeal Piao
	 * @Description Store search history in Application
	 * @param username
	 *            : 用户名
	 * @param vid
	 *            : 唯一标识id
	 * @param colID
	 *            : 大类型
	 * @param parentID
	 *            : 小类型
	 * @param title
	 *            : 显示标题
	 * @param field1
	 *            : 给药途径ID(药品模块)
	 * @param field2
	 *            : 给药途径内容
	 * @return void
	 */
	public void storeViewHistory(String username, Object vid, int colID,
			int parentID, String title, String field1, String field2) {
		long time = System.currentTimeMillis();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		int mYear = mCalendar.get(Calendar.YEAR);
		int mMonth = mCalendar.get(Calendar.MONTH) + 1;
		int mDate = mCalendar.get(Calendar.DATE);
		int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int mMinute = mCalendar.get(Calendar.MINUTE);
		int mSecond = mCalendar.get(Calendar.SECOND);
		Cursor cur = null;
		try {
			ContentValues cv = new ContentValues();
			cv.put("UID", username);
			cv.put("VID", vid.toString());
			cv.put("COLID", colID);
			cv.put("CATEGORY", parentID);
			cv.put("CONTENT", title);
			cv.put("TIME", mYear + "-" + mMonth + "-" + mDate + " " + mHour
					+ ":" + mMinute + ":" + mSecond);
			if (!TextUtils.isEmpty(field1))
				cv.put("field1", field1);
			if (!TextUtils.isEmpty(field2))
				cv.put("field2", field2);
			cur = getCursor(
					"select * from view_history where VID='" + vid.toString().trim() +"'", null);
			if(cur != null && cur.getCount() > 0){
				excuteSql("delete from view_history where VID=?", new String[]{vid.toString()});
//				if(excuteSql("update view_history set UID=?, COLID=?, CATEGORY=?, CONTENT=?, TIME=? where VID=?", 
//						new String[]{username,colID+"",parentID+"",title, mYear + "-" + mMonth + "-" + mDate + " " + mHour
//						+ ":" + mMinute + ":" + mSecond,vid.toString()}))
//					return;
			}
			insertSql("view_history", cv);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(cur != null) cur.close();
		}
	}

	/**
	 * @author Rafeal Piao
	 * @param void
	 * @return ArrayList<ViewHistoryBean>
	 * @Description get view history
	 */
	public ArrayList<ViewHistoryEntity> getViewHistory() {
		String sql = "select VID, COLID, CATEGORY, CONTENT, TIME,field1,field2 from view_history where UID='"
				+ ((GBApplication) ((Activity) context).getApplication())
						.getLogin().getGbUserName() + "'";
		ArrayList<ViewHistoryEntity> lst = new ArrayList<ViewHistoryEntity>();
		Cursor cursor = getCursor(sql, null);
		while (cursor.moveToNext()) {
			ViewHistoryEntity bean = new ViewHistoryEntity();
			bean.setvId(cursor.getString(0));
			bean.setColId(cursor.getInt(1));
			bean.setClassName(cursor.getInt(2));
			bean.setContent(cursor.getString(3));
			String time = cursor.getString(4);
			bean.setTime(time);
			String timeArr[] = time.split(" ");
			String timeArr_date[] = timeArr[0].split("-");
			String timeArr_hour[] = timeArr[1].split(":");
			bean.setYear(Integer.parseInt(timeArr_date[0]));
			bean.setMonth(Integer.parseInt(timeArr_date[1]));
			bean.setDate(Integer.parseInt(timeArr_date[2]));
			bean.setHour(Integer.parseInt(timeArr_hour[0]));
			bean.setMinute(Integer.parseInt(timeArr_hour[1]));
			bean.setSecond(Integer.parseInt(timeArr_hour[2]));
			bean.setField1(cursor.isNull(5) ? "" : cursor.getString(5));
			bean.setField2(cursor.isNull(6) ? "" : cursor.getString(6));

			lst.add(0, bean);
		}

		cursor.close();
		closeDB();
		return lst;
	}
}
