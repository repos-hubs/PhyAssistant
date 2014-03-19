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
 * ��ҩ��ȫdb������
 * 
 * @author simon
 * 
 */
public class DrugAlertSQLAdapter extends BaseSqlAdapter {

	public DrugAlertSQLAdapter(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}

	/**
	 * ���ز�����Ӧͨ���¼
	 * 
	 * @param source
	 *            ���
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
	 * ����typeId ��ȡ������Ӧͨ��
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
	  * ��ȡ������Ϣͨ�� ���id
	  * 
	  * @param cacheCategory
	  *            ������� 0:ȫ�� ��1:EMA ��2:FDA �� 3:SFDA
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
	 * ��ձ��ػ��棬�����ȡ������20����¼
	 * 
	 * @param values
	 *            ͨ�漯
	 * @param cacheCategory
	 *            ������� 0:ȫ�� ��1:EMA ��2:FDA �� 3:SFDA
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
	 * �����ȡ���¼�¼(<20����¼)�������ֱ��ػ���Ϊ20��
	 * 
	 * @param values
	 * @param cacheCategory
	 *            ������� 0:ȫ�� ��1:EMA ��2:FDA �� 3:SFDA
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
		
		//��ȡ�軺���¼������Сid
		String selectSql = "SELECT * FROM DrugAlertNews where cacheCategory = '"
				+ cacheCategory + "' order  by id desc limit 0,20";
		Cursor c = getCursor(selectSql, null);
		if (c != null) {
			if (c.moveToLast()) {
				String lastId = c.getString(c.getColumnIndex("id"));
				//ɾ�����໺���¼�����ֱ���ֻ����20��
				String deleteSql = "DELETE FROM DrugAlertNews where cacheCategory = '"
						+ cacheCategory + "' and id < '" + lastId + "'";
				excuteSql(deleteSql);
			}
			c.close();
		}
		return true;
	}

	/**
	 * ��ȡ������Ϣͨ�� ��һ�εĸ���ʱ��
	 * 
	 * @param cacheCategory
	 *            ������� 0:ȫ�� ��1:EMA ��2:FDA �� 3:SFDA
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
	 * ɾ��ͨ�滺���¼
	 * 
	 * @param cacheCategory
	 *            ������� 0:ȫ�� ��1:EMA ��2:FDA �� 3:SFDA
	 * @return boolean
	 */
	public boolean deleteDrugAlerts(String cacheCategory) {
		String sql = "DELETE FROM DrugAlertNews where cacheCategory = '"
				+ cacheCategory + "'";
		return excuteSql(sql);
	}

	/**
	 * ��ȡ����ͨ����ϸ(DrugAlertDetailEntity)
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
	 * ɾ�����ػ��棬����Ĭ�ϱ��ر���3�죬�Ժ������չ�������û��������������(ͨ����ϸ)
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
	 * ɾ��ͨ����ϸ �����¼
	 * 
	 * @param typeId
	 *            ͨ��typeId
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
	 * �鿴ͨ��Detail ���� ʱ�䣬���ڱȽϷ���������
	 * 
	 * @param typeId
	 *            ͨ��typeId
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
	 * ���벻����Ӧͨ����ϸ
	 * 
	 * @param list
	 *            ͨ����ϸ�� (��ӦDrugAlertDetail��)
	 * @param typeId
	 *            ͨ��typeId (��ӦTopDrugAlertDetail��)
	 * @param newTime
	 *            ͨ�����ʱ�� (��ӦTopDrugAlertDetail��)
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
	 * �鿴ͨ����ϸ��¼��
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
	 * ͨ���Ƿ����ղ�
	 * 
	 * @param typeID
	 *            ͨ��typeID
	 * @return int --- 1�����ղ� 0 ��δ�ղ�
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
	 * ȡ���ղ�
	 * 
	 * @param typeID
	 *            ͨ��typeID
	 * @return boolean
	 */
	public boolean delDrugAlertCollection(String typeID) {
		String sql = "delete from DrugAlertCollection where typeID=" + "\""
				+ typeID + "\"" + " and userName=" + "\""
				+ SharedPreferencesMgr.getUserName() + "\"";

		return excuteSql(sql);
	}

	/**
	 * �ղش�ͨ��
	 * 
	 * @param typeID
	 *            ͨ��typeID
	 * @param title
	 *            ͨ�����title
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
	 * ��ȡ�ղص�ͨ�漯(����)
	 * 
	 * @param userName
	 *            �û���
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
	 * ��ȡͨ����ϸ Զ�����ݿ�
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
