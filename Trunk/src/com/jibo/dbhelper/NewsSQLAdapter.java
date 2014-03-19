package com.jibo.dbhelper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jibo.common.Constant;
import com.jibo.data.entity.NewsCategoriesEntity;
import com.jibo.data.entity.NewsEntity;

/**
 * ����ģ��db����
 * 
 * @author simon
 * 
 */
public class NewsSQLAdapter extends BaseSqlAdapter {
	public static NewsSQLAdapter cacheHelper = new NewsSQLAdapter();

	private NewsSQLAdapter() {
	}

	public static NewsSQLAdapter getInstance(Context ctx) {
		if (cacheHelper == null) {
			cacheHelper = new NewsSQLAdapter(ctx);
		}
		return cacheHelper;
	}

	private Context context;
	private String[] newsCat = new String[] { "�ڿ�", "���", "�ٴ�ר��", "�ٴ����ҽѧ",
			"��������" };

	public NewsSQLAdapter(Context ctx) {
		this.context = ctx;
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}

	/**
	 * ��������ȫ��,����20������
	 * 
	 * @param list
	 * @param category
	 * @return
	 */
	public boolean insertNewsAll(List<NewsEntity> list, String category) {

		String deleteSql = "DELETE FROM News WHERE category = '" + category
				+ "'";
		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		sqlList.add(new SQLEntity(deleteSql, null));
		String sql = "INSERT INTO News (id, title, new_date, source, new_time, content ,typeID, category) "
				+ " VALUES (?, ?, ?, ?, ?, ?, ?,?)";
		for (NewsEntity news : list) {
			String[] params = new String[] { news.getId(), news.getTitle(),
					news.getDate(), news.getSource(), news.getTime(),
					news.getContent(), news.getTypeID(), category };
			sqlList.add(new SQLEntity(sql, params));
		}
		return excuteSql(sqlList);

	}

	/**
	 * ��ȡ�������id
	 * 
	 * @param Base
	 * @return
	 */
	public String getLocalMaxId() {
		String sql = "SELECT * FROM TopNews order by id desc";
		String result = "";
		try {
			Cursor c = getCursor(sql, null);

			if (c != null) {
				if (c.moveToFirst())
					result = c.getString(c.getColumnIndex("id"));
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return result;
	}

	/**
	 * �����ȡ���¼�¼(<20����¼)�������ֱ��ػ���Ϊ20��
	 * 
	 * @param list
	 * @param category
	 * @return
	 */
	public boolean insertNewsSome(ArrayList<NewsEntity> list, String category) {
		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		String sql = "INSERT INTO News (id, title, new_date, source, new_time, content ,typeID,new_source,category) "
				+ " VALUES (?, ?, ?, ?, ?, ?, ?,?,?)";
		for (NewsEntity news : list) {
			String[] params = new String[] { news.getId(), news.getTitle(),
					news.getDate(), news.getSource(), news.getTime(),
					news.getContent(), news.getTypeID(), news.getNewSource(),
					category };
			sqlList.add(new SQLEntity(sql, params));
		}
		excuteSql(sqlList);

		// ��ȡ�軺���¼������Сid
		String selectSql = "SELECT * FROM News where category = '" + category
				+ "' order  by id desc limit 0,20";
		Cursor c = getCursor(selectSql, null);
		if (c != null) {
			if (c.moveToLast()) {
				String lastId = c.getString(c.getColumnIndex("id"));
				// ɾ�����໺���¼�����ֱ���ֻ����20��
				String deleteSql = "DELETE FROM News WHERE category = '"
						+ category + "' and id < '" + lastId + "'";
				excuteSql(deleteSql);
			}
			c.close();
		}
		return true;
	}

	public String getNewsIdByCategoryName(String categoryName) {
		try {
			String str = null;
			String sql = "SELECT id FROM News WHERE category = '"
					+ categoryName + "'" + " order by id desc ";
			Cursor c = getCursor(sql, null);
			if (c != null) {
				while (c.moveToNext()) {
					str = c.getString(c.getColumnIndex("id"));
					break;
				}
				c.close();
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeDB();
		}
	}

	/**
	 * ��ȡָ������µ����ţ����������ݿ⣩
	 * 
	 * @param Base
	 * @param category
	 * @return
	 */
	public ArrayList<NewsEntity> getNewsByLocalDatabase(String category) {

		ArrayList<NewsEntity> list = new ArrayList<NewsEntity>();

		try {

			String sql = "SELECT * FROM News WHERE category = '" + category
					+ "'" + " order by id desc ";
			Cursor c = getCursor(sql, null);
			if (c != null) {
				while (c.moveToNext()) {
					Log.i("GAB", "_ID = " + c.getString(c.getColumnIndex("id")));

					NewsEntity entity = new NewsEntity();
					entity.setId(c.getString(c.getColumnIndex("id")));
					entity.setTitle(c.getString(c.getColumnIndex("title")));
					entity.setDate(c.getString(c.getColumnIndex("new_date")));
					entity.setSource(c.getString(c.getColumnIndex("source")));
					entity.setContent(c.getString(c.getColumnIndex("content")));
					entity.setTime(c.getString(c.getColumnIndex("new_time")));
					entity.setTypeID(c.getString(c.getColumnIndex("typeID")));
					entity.setNewSource(c.getString(c
							.getColumnIndex("new_source")));
					list.add(entity);
				}
				c.close();
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeDB();
		}
	}

	/**
	 * ��ȡ�������������С���
	 * 
	 * @param Base
	 * @return
	 */
	public String getNewsTotalCount() {
		String sql = "SELECT count(*) AS totalcount FROM NewsCategories ";

		try {
			Cursor c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToFirst())
					return c.getString(c.getColumnIndex("totalcount"));
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeDB();
		}
		return null;
	}

	/**
	 * ��ȡĳһ����������������
	 * 
	 * @param Base
	 * @param bigCatgory
	 *            �����
	 * @return
	 */
	public String getNewsTotalCount(String bigCatgory) {
		String sql = "SELECT count(*) AS totalcount FROM NewsCategories WHERE bigCategory = '"
				+ bigCatgory + "'";
		try {
			Cursor c = getCursor(sql, null);

			if (c != null) {
				if (c.moveToFirst())
					return c.getString(c.getColumnIndex("totalcount"));
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return null;
	}

	/**
	 * ��ȡѧ������������������������
	 * 
	 * �����
	 * 
	 * @return
	 */
	public String getSpecialtyNewsTotalCount() {
		String sql = "SELECT count(*) AS totalcount FROM NewsCategories WHERE bigCategory = '"
				+ newsCat[0]
				+ "' or bigCategory = '"
				+ newsCat[1]
				+ "' or bigCategory = '"
				+ newsCat[2]
				+ "' or bigCategory = '"
				+ newsCat[3] + "' or bigCategory = '" + newsCat[4] + "'";
		try {
			Cursor c = getCursor(sql, null);

			if (c != null) {
				if (c.moveToFirst())
					return c.getString(c.getColumnIndex("totalcount"));
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return null;
	}

	/**
	 * ��ȡĳһ�����µ�������������
	 * 
	 * @param Base
	 * @param bigCat
	 * @return
	 */
	public String getNewsCategoriesCountByBigCategory(String bigCat) {
		String sql = "SELECT newscount FROM NewsCategories WHERE bigCategory = '"
				+ bigCat + "'";
		String returnValue = "";
		String temp = "";
		try {
			Cursor c = getCursor(sql, null);

			if (c != null) {
				while (c.moveToNext()) {
					temp = c.getString(c.getColumnIndex("newscount"));
					returnValue = returnValue + temp;
					if (c.isLast()) {
						returnValue = returnValue + ".";
					} else {
						returnValue = returnValue + ",";
					}

				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return returnValue;
	}

	/**
	 * ��ȡѧ���µ�������������
	 * 
	 * @return
	 */
	public String getNewsCategoriesCount() {
		String sql = "SELECT newscount FROM NewsCategories WHERE bigCategory = '"
				+ newsCat[0]
				+ "' or bigCategory = '"
				+ newsCat[1]
				+ "' or bigCategory = '"
				+ newsCat[2]
				+ "' or bigCategory = '"
				+ newsCat[3] + "' or bigCategory = '" + newsCat[4] + "'";
		String returnValue = "";
		String temp = "";
		try {
			Cursor c = getCursor(sql, null);

			if (c != null) {
				while (c.moveToNext()) {
					temp = c.getString(c.getColumnIndex("newscount"));
					returnValue = returnValue + temp;
					if (c.isLast()) {
						returnValue = returnValue + ".";
					} else {
						returnValue = returnValue + ",";
					}

				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return returnValue;
	}

	/**
	 * ��ȡ����ĳһ�����µ�����������ַ�������","�ָ�
	 * 
	 * @param Base
	 * @param bigCat
	 * @return
	 */
	public String getNewsCategoriesItemNames(String bigCat) {
		String sql = "SELECT category FROM NewsCategories WHERE bigCategory = '"
				+ bigCat + "'";
		String returnValue = "";
		String temp = "";
		try {
			Cursor c = getCursor(sql, null);

			if (c != null) {
				while (c.moveToNext()) {
					temp = c.getString(c.getColumnIndex("category"));
					returnValue = returnValue + temp;
					if (c.isLast()) {
						returnValue = returnValue + ".";
					} else {
						returnValue = returnValue + ",";
					}
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		Log.i("GAB", "returnValue === " + returnValue);
		return returnValue;
	}

	/**
	 * ��ȡѧ���µ�����������ַ�������","�ָ�
	 * 
	 * @return
	 */
	public String getNewsCategoriesItemNames() {
		String sql = "SELECT category FROM NewsCategories WHERE bigCategory = '"
				+ newsCat[0]
				+ "' or bigCategory = '"
				+ newsCat[1]
				+ "' or bigCategory = '"
				+ newsCat[2]
				+ "' or bigCategory = '"
				+ newsCat[3] + "' or bigCategory = '" + newsCat[4] + "'";
		String returnValue = "";
		String temp = "";
		try {
			Cursor c = getCursor(sql, null);

			if (c != null) {
				while (c.moveToNext()) {
					temp = c.getString(c.getColumnIndex("category"));
					returnValue = returnValue + temp;
					if (c.isLast()) {
						returnValue = returnValue + ".";
					} else {
						returnValue = returnValue + ",";
					}
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		Log.i("GAB", "returnValue === " + returnValue);
		return returnValue;
	}

	/**
	 * ��ȡ�����������
	 * 
	 * @param Base
	 * @param bigCategory
	 *            ����� ��������"�ڿ�", "���", "�ٴ�ר��", "�ٴ����ҽѧ", "��������","��ҵ��̬"
	 * @return
	 */
	public ArrayList<NewsCategoriesEntity> getCategoriesListByBigCategory(
			String bigCategory) {
		ArrayList<NewsCategoriesEntity> list = null;
		String sql = "SELECT * FROM NewsCategories WHERE bigCategory = '"
				+ bigCategory + "'";

		try {
			Cursor c = getCursor(sql, null);
			list = new ArrayList<NewsCategoriesEntity>();
			if (c != null) {
				while (c.moveToNext()) {
					Log.i("GAB",
							"_ID = " + c.getString(c.getColumnIndex("_ID")));
					NewsCategoriesEntity category = new NewsCategoriesEntity(
							c.getString(c.getColumnIndex("category")),
							c.getString(c.getColumnIndex("newscount")),
							c.getString(c.getColumnIndex("bigCategory")));
					list.add(category);
				}
				c.close();
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeDB();
		}
	}

	/**
	 * ��ȡ�������ŷ������
	 * 
	 * @param Base
	 * @param bigCategory
	 *            ����� ��������"�ڿ�", "���", "�ٴ�ר��", "�ٴ����ҽѧ", "��������"
	 * @return
	 */
	public ArrayList<NewsCategoriesEntity> getSpecialtyCategoriesList() {
		ArrayList<NewsCategoriesEntity> list = null;
		String sql = "SELECT * FROM NewsCategories WHERE bigCategory = '"
				+ newsCat[0] + "' or bigCategory = '" + newsCat[1]
				+ "' or bigCategory = '" + newsCat[2] + "' or bigCategory = '"
				+ newsCat[3] + "' or bigCategory = '" + newsCat[4] + "'";

		try {
			Cursor c = getCursor(sql, null);
			list = new ArrayList<NewsCategoriesEntity>();
			if (c != null) {
				while (c.moveToNext()) {
					Log.i("GAB",
							"_ID = " + c.getString(c.getColumnIndex("_ID")));
					NewsCategoriesEntity category = new NewsCategoriesEntity(
							c.getString(c.getColumnIndex("category")),
							c.getString(c.getColumnIndex("newscount")),
							c.getString(c.getColumnIndex("bigCategory")));
					list.add(category);
				}
				c.close();
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeDB();
		}
	}

	/**
	 * �����������(����)
	 * 
	 * @param Base
	 * @param values
	 * @return
	 */
	public boolean insertNewsCategories(ArrayList<NewsCategoriesEntity> list,
			int pageIndex) {
		String deleteSql = null;
		if (pageIndex == 1) {
			deleteSql = "DELETE FROM NewsCategories WHERE bigCategory = '"
					+ newsCat[0] + "' or bigCategory = '" + newsCat[1]
					+ "' or bigCategory = '" + newsCat[2]
					+ "' or bigCategory = '" + newsCat[3]
					+ "' or bigCategory = '" + newsCat[4] + "'";
		} else if (pageIndex == 2) {
			deleteSql = "DELETE FROM NewsCategories WHERE bigCategory = '��ҵ��̬'";
		}
		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		sqlList.add(new SQLEntity(deleteSql, null));
		String sql = "INSERT INTO NewsCategories(category, newscount, newesttime, bigCategory) VALUES(?, ?, ?, ?)";
		for (NewsCategoriesEntity entity : list) {
			String[] params = new String[] { entity.getName(),
					entity.getNewsCount(), entity.getNewsTime(),
					entity.getBigCategory() };
			sqlList.add(new SQLEntity(sql, params));
		}
		return excuteSql(sqlList);
	}

	/**
	 * ��ȡ�������¶�̬��������
	 * 
	 * @param Base
	 * @return
	 */
	public ArrayList<NewsEntity> getTopNewsByLocalDatabase() {
		ArrayList<NewsEntity> list = null;
		try {

			list = new ArrayList<NewsEntity>();
			String sql = "SELECT * FROM TopNews order by id desc";
			Cursor c = getCursor(sql, null);
			if (c != null) {
				while (c.moveToNext()) {
					Log.i("GAB",
							"_ID = " + c.getString(c.getColumnIndex("_ID")));
					NewsEntity entity = new NewsEntity();
					entity.setId(c.getString(c.getColumnIndex("id")));
					entity.setTitle(c.getString(c.getColumnIndex("title")));
					entity.setDate(c.getString(c.getColumnIndex("new_date")));
					entity.setContent(c.getString(c.getColumnIndex("content")));
					entity.setSource(c.getString(c.getColumnIndex("source")));
					entity.setTime(c.getString(c.getColumnIndex("newesttime")));
					entity.setTypeID(c.getString(c.getColumnIndex("typeID")));
					entity.setContent(c.getString(c.getColumnIndex("content")));
					entity.setNewSource(c.getString(c
							.getColumnIndex("new_source")));
					
					list.add(entity);
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeDB();
		}
		return list;
	}

	/**
	 * ��������20������
	 * 
	 * @param list
	 *            size == 20
	 * @return
	 */
	public boolean insertTopNewsAll(ArrayList<NewsEntity> list) {
		String deleteSql = "DELETE FROM TopNews";
		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		sqlList.add(new SQLEntity(deleteSql, null));
		String insertSql = "INSERT INTO TopNews(id, title, new_date, source, newesttime, content,typeID,new_source) VALUES(?, ?, ?, ?, ?, ?,?,?)";
		for (NewsEntity news : list) {
			String[] params = new String[] { news.getId(), news.getTitle(),
					news.getDate(), news.getSource(), news.getTime(),
					news.getContent(), news.getTypeID(), news.getNewSource() };
			sqlList.add(new SQLEntity(insertSql, params));
		}
		return excuteSql(sqlList);
	}

	/**
	 * ����������ݣ������ֻ���Ϊ20��
	 * 
	 * @param list
	 *            size<20
	 * @return
	 */
	public boolean insertTopNewsSome(ArrayList<NewsEntity> list) {
		ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
		String sql = "INSERT INTO TopNews (id, title, new_date, source, newesttime, content ,typeID,new_source) "
				+ " VALUES (?, ?, ?, ?, ?, ?, ?,?)";
		for (NewsEntity news : list) {
			String[] params = new String[] { news.getId(), news.getTitle(),
					news.getDate(), news.getSource(), news.getTime(),
					news.getContent(), news.getTypeID(), news.getNewSource() };
			sqlList.add(new SQLEntity(sql, params));
		}
		excuteSql(sqlList);

		// ��ȡ�軺���¼������Сid
		String selectSql = "SELECT * FROM TopNews order by id desc limit 0,20";
		Cursor c = getCursor(selectSql, null);
		if (c != null) {
			if (c.moveToLast()) {
				String lastId = c.getString(c.getColumnIndex("id"));
				// ɾ�����໺���¼�����ֱ���ֻ����20��
				String deleteSql = "DELETE FROM TopNews WHERE id < '" + lastId
						+ "'";
				excuteSql(deleteSql);
			}
			c.close();
		}
		return true;
	}

	/**
	 * ��ȡ��������cache����ʱ��
	 * 
	 * @param Base
	 * @return
	 */
	public String getTopNewsTime() {
		String sql = "SELECT newesttime FROM TopNews ";
		String result = "";
		try {
			Cursor c = getCursor(sql, null);

			if (c != null) {
				if (c.moveToFirst())
					result = c.getString(c.getColumnIndex("newesttime"));
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return result;
	}

	/**
	 * ��ȡĳһ�����������ʱ��
	 * 
	 * @param Base
	 * @param category
	 *            С���
	 * @return
	 */
	public String getNewsCategoriesTime(String category) {
		String sql = "SELECT new_time FROM News WHERE category = '" + category
				+ "'";

		try {
			Cursor c = getCursor(sql, null);

			if (c != null) {
				if (c.moveToFirst())
					return c.getString(c.getColumnIndex("new_time"));
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return null;
	}

}
