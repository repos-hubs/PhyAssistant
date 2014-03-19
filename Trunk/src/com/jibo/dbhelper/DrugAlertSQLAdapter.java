package com.jibo.dbhelper;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.DrugAlertDetailEntity;
import com.jibo.data.entity.DrugAlertEntity;

/**
 * 用药安全db工具类
 * 
 * @author simon
 * 
 */
public class DrugAlertSQLAdapter extends BaseSqlAdapter {

	public DrugAlertSQLAdapter(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}

	/**
	 * 本地不良反应通告记录
	 * 
	 * @param source
	 *            类别
	 * @return ArrayList<DrugAlertEntity>
	 */
	public ArrayList<DrugAlertEntity> getDrugAlertsByLocalDatabase(String source) {
		ArrayList<DrugAlertEntity> drugAlertList = new ArrayList<DrugAlertEntity>();
		try {
			String sql = "SELECT * FROM DrugAlertNews where cacheCategory = '"
					+ source + "' order  by id desc";
			Cursor c = getCursor(sql, null);

			if (c != null) {
				while (c.moveToNext()) {
					Log.i("GAB",
							"_ID = " + c.getString(c.getColumnIndex("_ID")));
					DrugAlertEntity alert = new DrugAlertEntity();
					alert.setId(c.getString(c.getColumnIndex("id")));
					alert.setTitle(c.getString(c.getColumnIndex("title")));
					alert.setTypeId(c.getString(c.getColumnIndex("Type_ID")));
					alert.setDate(c.getString(c.getColumnIndex("new_date")));
					alert.setTime(c.getString(c.getColumnIndex("new_time")));
					alert.setSource(c.getString(c.getColumnIndex("source")));
					alert.setContent(c.getString(c.getColumnIndex("content")));
					alert.setProductNameCN(c.getString(c
							.getColumnIndex("product_name_cn")));
					alert.setCategory(c.getString(c.getColumnIndex("category")));
					drugAlertList.add(alert);
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return drugAlertList;

	}

	/**
	 * 根据typeId 获取不良反应通告
	 * 
	 * @param typeId
	 * @return DrugAlertEntity
	 */
	public DrugAlertEntity getDrugAlertsByTypeId(String typeId) {
		try {
			String sql = "SELECT * FROM DrugAlertNews where Type_ID = '"
					+ typeId + "'";
			Cursor c = getCursor(sql, null);
			DrugAlertEntity alert = null;
			if (c != null) {
				while (c.moveToNext()) {
					alert = new DrugAlertEntity();
					Log.i("GAB",
							"_ID = " + c.getString(c.getColumnIndex("_ID")));
					alert.setId(c.getString(c.getColumnIndex("id")));
					alert.setTitle(c.getString(c.getColumnIndex("title")));
					alert.setTypeId(c.getString(c.getColumnIndex("Type_ID")));
					alert.setDate(c.getString(c.getColumnIndex("new_date")));
					alert.setTime(c.getString(c.getColumnIndex("new_time")));
					alert.setSource(c.getString(c.getColumnIndex("source")));
					alert.setContent(c.getString(c.getColumnIndex("content")));
					alert.setProductNameCN(c.getString(c
							.getColumnIndex("product_name_cn")));
					alert.setCategory(c.getString(c.getColumnIndex("category")));
				}
				c.close();
			}
			return alert;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	 /**
	  * 获取不良信息通告 最大id
	  * 
	  * @param cacheCategory
	  *            缓存类别 0:全部 ，1:EMA ，2:FDA ， 3:SFDA
	  * @return String
	  */
	 public String getDrugAlertsMaxId(String cacheCategory) {
	  String sql = "SELECT * FROM DrugAlertNews where cacheCategory = '"
	    + cacheCategory + "' order  by id desc";
	  String result = "";
	  Cursor c = null;
	  try {
	   c = getCursor(sql, null);
	   if (c != null) {
	    if (c.moveToFirst())
	     result = c.getString(c.getColumnIndex("id"));
	    c.close();
	   }
	   closeDB();
	  } catch (Exception e) {
	   e.printStackTrace();
	  }
	  return result;

	 }

	/**
	 * 清空本地缓存，插入获取的最新20条记录
	 * 
	 * @param values
	 *            通告集
	 * @param cacheCategory
	 *            缓存类别 0:全部 ，1:EMA ，2:FDA ， 3:SFDA
	 * @return boolean
	 */
	public boolean insertDrugAlertsAll(ArrayList<DrugAlertEntity> list,
			String cacheCategory) {

		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		String deleteSql = "DELETE FROM DrugAlertNews where cacheCategory = '"
				+ cacheCategory + "'";
		excuteSql(deleteSql);
		// sqlList.add(new SQLEntity(deleteSql, null));
		String sql = "INSERT INTO DrugAlertNews(id, title, Type_ID, new_date, new_time, source, content, product_name_cn, category,cacheCategory) VALUES(?, ?, ?, ?,?,?,?,?,?,?)";
		for (DrugAlertEntity alert : list) {
			String[] params = new String[] { alert.getId(), alert.getTitle(),
					alert.getTypeId(), alert.getDate(), alert.getTime(),
					alert.getSource(), alert.getContent(),
					alert.getProductNameCN(), alert.getCategory(),
					cacheCategory };
			sqlList.add(new SQLEntity(sql, params));
		}
		return excuteSql(sqlList);
	}

	/**
	 * 插入获取最新记录(<20条记录)，并保持本地缓存为20条
	 * 
	 * @param values
	 * @param cacheCategory
	 *            缓存类别 0:全部 ，1:EMA ，2:FDA ， 3:SFDA
	 * @return boolean
	 */
	public boolean insertAndKeep20Caches(ArrayList<DrugAlertEntity> list,
			String cacheCategory) {
		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		String sql = "INSERT INTO DrugAlertNews(id, title, Type_ID, new_date, new_time, source, content, product_name_cn, category,cacheCategory) VALUES(?, ?, ?, ?,?,?,?,?,?,?)";
		for (DrugAlertEntity alert : list) {
			String[] params = new String[] { alert.getId(), alert.getTitle(),
					alert.getTypeId(), alert.getDate(), alert.getTime(),
					alert.getSource(), alert.getContent(),
					alert.getProductNameCN(), alert.getCategory(),
					cacheCategory };
			sqlList.add(new SQLEntity(sql, params));
		}
		excuteSql(sqlList);
		
		//获取需缓存记录集的最小id
		String selectSql = "SELECT * FROM DrugAlertNews where cacheCategory = '"
				+ cacheCategory + "' order  by id desc limit 0,20";
		Cursor c = getCursor(selectSql, null);
		if (c != null) {
			if (c.moveToLast()) {
				String lastId = c.getString(c.getColumnIndex("id"));
				//删除多余缓存记录，保持本地只缓存20条
				String deleteSql = "DELETE FROM DrugAlertNews where cacheCategory = '"
						+ cacheCategory + "' and id < '" + lastId + "'";
				excuteSql(deleteSql);
			}
			c.close();
		}
		return true;
	}

	/**
	 * 获取不良信息通告 上一次的更新时间
	 * 
	 * @param cacheCategory
	 *            缓存类别 0:全部 ，1:EMA ，2:FDA ， 3:SFDA
	 * @return String
	 */
	public String getDrugAlertsLastTime(String cacheCategory) {
		String sql = "SELECT new_time FROM DrugAlertNews where cacheCategory = '"
				+ cacheCategory + "' ";
		String result = "";
		Cursor c = null;
		try {
			c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToFirst())
					result = c.getString(c.getColumnIndex("new_time"));
				c.close();
			}
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 删除通告缓存记录
	 * 
	 * @param cacheCategory
	 *            缓存类别 0:全部 ，1:EMA ，2:FDA ， 3:SFDA
	 * @return boolean
	 */
	public boolean deleteDrugAlerts(String cacheCategory) {
		String sql = "DELETE FROM DrugAlertNews where cacheCategory = '"
				+ cacheCategory + "'";
		return excuteSql(sql);
	}

	/**
	 * 获取本地通告详细(DrugAlertDetailEntity)
	 * 
	 * @param typeId
	 * @return ArrayList<DrugAlertDetailEntity>
	 */
	public ArrayList<DrugAlertDetailEntity> getDrugAlertsDetailByLocalDatabase(
			String typeId) {
		ArrayList<DrugAlertDetailEntity> drugAlertDetailList = new ArrayList<DrugAlertDetailEntity>();

		try {

			String sql = "SELECT * FROM DrugAlertDetail where TYPE_ID = '"
					+ typeId + "' order by DETAIL_ID";
			Cursor c = getCursor(sql, null);

			if (c != null) {
				while (c.moveToNext()) {
					Log.i("GAB",
							"_ID = " + c.getString(c.getColumnIndex("_ID")));
					DrugAlertDetailEntity detail = new DrugAlertDetailEntity();
					detail.setTypeId(c.getString(c.getColumnIndex("TYPE_ID")));
					detail.setDetailId(c.getString(c
							.getColumnIndex("DETAIL_ID")));
					detail.setDetailTitle(c.getString(c
							.getColumnIndex("DETAIL_TITLE")));
					detail.setDetailContent(c.getString(c
							.getColumnIndex("DETAIL_CONTENT")));
					drugAlertDetailList.add(detail);
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return drugAlertDetailList;
	}

	/**
	 * 删除本地缓存，缓存默认本地保存3天，以后可以拓展界面配置或服务器配置天数(通告详细)
	 * 
	 * @return boolean
	 */
	public boolean deleteDrugAlertDetailLocalCache() {
		String deleleTabDrugAlertDetailCache = "delete from DrugAlertDetail where TYPE_ID IN (SELECT TYPE_ID FROM TopDrugAlertDetail where CREATE_TIME < datetime('now','-3 day','localtime'))";
		String deleleTabTopDrugAlertDetailCache = "delete from TopDrugAlertDetail where CREATE_TIME < datetime('now','-3 day','localtime')";
		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		sqlList.add(new SQLEntity(deleleTabDrugAlertDetailCache, null));
		sqlList.add(new SQLEntity(deleleTabTopDrugAlertDetailCache, null));
		return excuteSql(sqlList);
	}

	/**
	 * 删除通告详细 缓存记录
	 * 
	 * @param typeId
	 *            通告typeId
	 * @return boolean
	 */
	public boolean deleteDrugAlertDetails(String typeId) {
		String sql = "DELETE FROM DrugAlertDetail where TYPE_ID = '" + typeId
				+ "'";
		String sql1 = "DELETE FROM TopDrugAlertDetail where TYPE_ID = '"
				+ typeId + "'";
		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		sqlList.add(new SQLEntity(sql, null));
		sqlList.add(new SQLEntity(sql1, null));
		return excuteSql(sqlList);
	}

	/**
	 * 查看通告Detail 缓存 时间，用于比较服务器更新
	 * 
	 * @param typeId
	 *            通告typeId
	 * @return String
	 */
	public String getDrugAlertsDetailTimeByTypeID(String typeId) {
		String sql = "SELECT NEW_TIME time FROM TopDrugAlertDetail WHERE TYPE_ID = '"
				+ typeId + "'";
		String result = null;
		try {
			Cursor c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToFirst())
					result = c.getString(c.getColumnIndex("time"));
				c.close();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}

	/**
	 * 插入不良反应通告详细
	 * 
	 * @param list
	 *            通告详细集 (对应DrugAlertDetail表)
	 * @param typeId
	 *            通告typeId (对应TopDrugAlertDetail表)
	 * @param newTime
	 *            通告更新时间 (对应TopDrugAlertDetail表)
	 * @return boolean
	 */
	public boolean insertDrugAlertDetails(
			ArrayList<DrugAlertDetailEntity> list, String typeId, String newTime) {
		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		String sql = "INSERT INTO DrugAlertDetail(TYPE_ID, DETAIL_ID, DETAIL_TITLE, DETAIL_CONTENT) VALUES(?, ?, ?, ?)";
		for (DrugAlertDetailEntity detail : list) {
			String[] params = new String[] { typeId, detail.getDetailId(),
					detail.getDetailTitle(), detail.getDetailContent() };
			sqlList.add(new SQLEntity(sql, params));
		}

		String sql1 = "INSERT INTO TopDrugAlertDetail(TYPE_ID, NEW_TIME) VALUES(?, ?)";
		String[] params1 = new String[] { typeId, newTime };
		sqlList.add(new SQLEntity(sql1, params1));
		return excuteSql(sqlList);
	}

	/**
	 * 查看通告详细记录数
	 * 
	 * @return int
	 */
	public int getDrugAlertsDetailCount() {
		String sql = "SELECT COUNT(*) as count FROM TopDrugAlertDetail";
		int result = 0;
		try {
			Cursor c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToFirst())
					result = c.getInt(c.getColumnIndex("count"));
				c.close();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 通告是否已收藏
	 * 
	 * @param typeID
	 *            通告typeID
	 * @return int --- 1：已收藏 0 ：未收藏
	 */
	public int selectDrugAlertCollection(String typeID) {

		int result = 0;
		int id = 0;
		int i = 0;

		Cursor c = getCursor(
				"select mobileID from DrugAlertCollection where typeID=" + "\""
						+ typeID + "\"" + " and userName=" + "\""
						+ SharedPreferencesMgr.getUserName() + "\"", null);
		try {
			if (c != null) {
				if (c.moveToFirst()) {

					do {
						int indexdate = c.getColumnIndex("mobileID");
						id = c.getInt(indexdate);
						i++;

					} while (c.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		Log.e("id", String.valueOf(id));
		return id;
	}

	/**
	 * 取消收藏
	 * 
	 * @param typeID
	 *            通告typeID
	 * @return boolean
	 */
	public boolean delDrugAlertCollection(String typeID) {
		String sql = "delete from DrugAlertCollection where typeID=" + "\""
				+ typeID + "\"" + " and userName=" + "\""
				+ SharedPreferencesMgr.getUserName() + "\"";

		return excuteSql(sql);
	}

	/**
	 * 收藏此通告
	 * 
	 * @param typeID
	 *            通告typeID
	 * @param title
	 *            通告标题title
	 * @return boolean
	 */
	public boolean insertDrugAlertCollection(String typeID, String title) {
		String sql = "INSERT INTO DrugAlertCollection(typeID,title,userName) values(\""
				+ typeID
				+ "\",\""
				+ title
				+ "\",\""
				+ SharedPreferencesMgr.getUserName() + "\")";
		return excuteSql(sql);
	}

	/**
	 * 获取收藏的通告集(标题)
	 * 
	 * @param userName
	 *            用户名
	 * @return ArrayList<String>
	 */
	public ArrayList<String> selectHistoryDrugAlert(String userName) {

		String typeID, title;
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			Cursor c = getCursor(
					"select typeID,title from DrugAlertCollection where userName="
							+ "\"" + userName + "\"", null);
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						typeID = c.getString(c.getColumnIndex("typeID"));
						title = c.getString(c.getColumnIndex("title"));
						arrayList.add(title);
					} while (c.moveToNext());
				}
				c.close();
			}
			return arrayList;
		} catch (Exception e) {
			e.printStackTrace();
			return arrayList;
		}
	}

	/**
	 * 获取通告详细 远程数据库
	 * 
	 * @param typeId
	 * @return DrugAlertEntity
	 */
	public DrugAlertEntity getDrugAlertsByLocal(String typeId) {
		DrugAlertEntity drugAlert = null;
		try {
			String sql = "SELECT *  FROM DrugAlertNews where Type_ID = '"
					+ typeId + "' ";
			Cursor c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToFirst()) {
					Log.i("GAB",
							"_ID = " + c.getString(c.getColumnIndex("_ID")));

					drugAlert = new DrugAlertEntity();
					drugAlert.setId(c.getString(c.getColumnIndex("id")));
					drugAlert.setTitle(c.getString(c.getColumnIndex("title")));
					drugAlert
							.setTypeId(c.getString(c.getColumnIndex("Type_ID")));
					drugAlert
							.setDate(c.getString(c.getColumnIndex("new_date")));
					drugAlert
							.setTime(c.getString(c.getColumnIndex("new_time")));
					drugAlert
							.setSource(c.getString(c.getColumnIndex("source")));
					drugAlert.setContent(c.getString(c
							.getColumnIndex("content")));
					drugAlert.setProductNameCN(c.getString(c
							.getColumnIndex("product_name_cn")));
					drugAlert.setCategory(c.getString(c
							.getColumnIndex("category")));

				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return drugAlert;
	}

}
