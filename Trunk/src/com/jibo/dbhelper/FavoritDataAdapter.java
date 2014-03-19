package com.jibo.dbhelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jibo.activity.HistoryFavoriteActivity;
import com.jibo.base.src.EntityObj;
import com.jibo.common.Constant;
import com.jibo.entity.FavoritDrugEntity;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * NewsCollection表 存放新闻的收藏信息(内建) TabDrugReference表 存放药品信息的收藏信息(内建)
 * EventCollection 存放会议的收藏信息(内建) ResearchCollection 存放医学研究的收藏信息(内建) TabCalc表
 * 存储医学公司的收藏信息(内建) TabTumor表 存储肿瘤的收藏信息(内建) TabHeartCollect表 存储新电图的收藏信息(内建)
 * */
public class FavoritDataAdapter extends BaseSqlAdapter {

	public static String sbXml = "";

	public FavoritDataAdapter(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}

	public int insertTableEventCollection(String eventsID, String name,
			String place, String date, String userName) {
		String sql = "INSERT INTO EventCollection(eventsID, name,userName) values(\""
				+ eventsID + "\",\"" + name + "\",\"" + userName + "\")";

		if (excuteSql(sql))
			return 1;
		return 0;
	}

	public int insertTabDrugReference( String drugId,
			String drugName, String adminRouteId,String adminRouteName,String userName,String ahfsInfo,int mode) {
		String sql = "INSERT INTO TabDrugReference(drugId,drugName,userName,ahfsInfo,mode) values(\""
				+ drugId + "\",\"" + drugName + "\",\"" + userName + "\",\"" + ahfsInfo + "\","+mode+")";
		if(!TextUtils.isEmpty(adminRouteId)){
			sql = "INSERT INTO TabDrugReference(drugId,drugName,userName,adminRouteId,adminRouteName,ahfsInfo,mode) values(\""
					+ drugId + "\",\"" + drugName + "\",\"" + userName + "\",\"" + adminRouteId + "\",\"" + adminRouteName + "\",\"" + ahfsInfo + "\","+mode+")";
		}
		
       Log.e("drugId", drugId);
       Log.e("drugName", drugName);
       Log.e("userName", userName);
		if (excuteSql(sql))
			return 1;
		return 0;

	}
	

	
	public int selectDrugAlert(Context context, String dId,
			String userName) {
		int i = 0;
		int id = 0;
		Cursor cur = getCursor(
				"select mobileID from DrugAlertCollection where typeID=" + "\""
						+ dId + "\"" + " and userName=" + "\"" + userName
						+ "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("mobileID");
						id = cur.getInt(indexdate);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return id;

	}
	
	
	public int selectAllDrugAlert(String userName) {
		int i = 0;
		String typeID;
		StringBuffer sb = new StringBuffer();
		Cursor cur =  getCursor(
				"select typeID from DrugAlertCollection where userName="
						+ "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("typeID");
						typeID = cur.getString(indexdate);
						sb.append("<item>");
						sb.append("<userid>");
						sb.append(userName);
						sb.append("</userid>");
						sb.append("<favid>");
						sb.append(typeID);
						sb.append("</favid>");
						sb.append("<categoryId>");
						sb.append(Constant.DRUG_ALERT_COLID);
						sb.append("</categoryId>");
						sb.append("</item>");
						sbXml += sb.toString();

						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		return i;

	}
	
	
	public boolean delAllDurgAlert( String userName) {
		return excuteSql("delete from DrugAlertCollection where userName=" + "\""
				+ userName + "\"");
	}

	public int selectTabDrugReferenceByDrug(Context context, String dId,String adminRouteId,
			String userName) {
		int i = 0;
		int id = 0;
		
		String sql = "select mobileID from TabDrugReference where drugId=" + "\""
				+ dId + "\"" + " and userName=" + "\"" + userName
				+ "\"";
		
		if(!TextUtils.isEmpty(adminRouteId)){
			sql +=  " and adminRouteId=" + "\"" + adminRouteId
					+ "\"";
		}
		
		Cursor cur = getCursor(sql
				, null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("mobileID");
						id = cur.getInt(indexdate);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		System.out.println("id   *&& "+id);

		System.out.println("did   *&& "+dId);
		return id;

	}
	


	public boolean delTabDrugReference(Context context, String dId,String adminRouteId,
			String userName) {
		String sql = "delete from TabDrugReference where drugId=" + "\"" + dId
				+ "\"" + " and userName=" + "\"" + userName + "\"";
		if(!TextUtils.isEmpty(adminRouteId)){
			sql +=  " and adminRouteId=" + "\"" + adminRouteId
					+ "\"";
		}
		return excuteSql(sql);

	}
	


	
	public  boolean delAllTabDrugReference(
			String userName) {
		String sql = "delete from TabDrugReference where userName=" + "\""
				+ userName + "\"";
			return excuteSql(sql);

	}

	public ArrayList<FavoritDrugEntity> selectTabDrugRef(Context context, String userName) {
		ArrayList<FavoritDrugEntity> arrayList = new ArrayList<FavoritDrugEntity>();
		Cursor cur = getCursor(
				"select drugId,drugName,adminRouteId,adminRouteName,ahfsInfo,mode from TabDrugReference where userName="
						+ "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						
						FavoritDrugEntity entity = new FavoritDrugEntity();
						entity.drugId = cur.getString(cur.getColumnIndex("drugId"));
						entity.drugName = cur.getString(cur.getColumnIndex("drugName"));
						entity.adminRouteId = cur.getString(cur.getColumnIndex("adminRouteId"));
						entity.adminRouteName= cur.getString(cur.getColumnIndex("adminRouteName"));
						entity.ahfsInfo= cur.getString(cur.getColumnIndex("ahfsInfo"));
						entity.mode= cur.getInt(cur.getColumnIndex("mode"));
						
						arrayList.add(entity);
					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cur.close();
		return arrayList;

	}
	
	public ArrayList<String> selectTabDrugSpec(Context context, String userName) {
		int i = 0;
		String id, drugName;
		HistoryFavoriteActivity.arrFavorDrugId = new ArrayList<String>();
		ArrayList<String> arrayList = new ArrayList<String>();
		Cursor cur = getCursor(
				"select drugId,drugName from TabDrugReference where userName="
						+ "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdrugId = cur.getColumnIndex("drugId");
						id = cur.getString(indexdrugId);
						int indexdrugName = cur.getColumnIndex("drugName");
						drugName = cur.getString(indexdrugName);
						arrayList.add(drugName);

						HistoryFavoriteActivity.arrFavorDrugId.add(id);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		return arrayList;

	}

	


	public int selectEventCollection(Context context, String eId,
			String userName) {
		int i = 0;
		int id = 0;

		Cursor cur = getCursor(
				"select mobileID from EventCollection where eventsID=" + "\""
						+ eId + "\"" + " and userName=" + "\"" + userName
						+ "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("mobileID");
						id = cur.getInt(indexdate);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return id;

	}

	public ArrayList<String> selectEventC(Context context, String userName) {
		int i = 0;
		String eventsID, name;
		HistoryFavoriteActivity.arrFavorEventId = new ArrayList<String>();
		ArrayList<String> arrayList = new ArrayList<String>();
		Cursor cur = getCursor(
				"select eventsID,name from EventCollection where userName="
						+ "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdrugId = cur.getColumnIndex("eventsID");
						eventsID = cur.getString(indexdrugId);
						int indexdrugName = cur.getColumnIndex("name");
						name = cur.getString(indexdrugName);
						arrayList.add(name);

						HistoryFavoriteActivity.arrFavorEventId.add(eventsID);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		return arrayList;

	}
	
	public ArrayList<String> selectHistoryDrugAlert(Context context,
			String userName) {

		int i = 0;
		String typeID, title;
		HistoryFavoriteActivity.arrFavorDrugAlertTypeIds = new ArrayList<String>();
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
						HistoryFavoriteActivity.arrFavorDrugAlertTypeIds.add(typeID);
						i++;
					} while (c.moveToNext());
				}
				c.close();
			}
			return arrayList;
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
			return arrayList;
		} 
	}


	public int selectAllEventCollection(String userName
			) {
		int i = 0;
		String eventsID, name;
		StringBuffer sb = new StringBuffer();
		Cursor cur = getCursor(
				"select eventsID,name from EventCollection where userName="
						+ "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("eventsID");
						eventsID = cur.getString(indexdate);
						int indexname = cur.getColumnIndex("name");
						name = cur.getString(indexname);
						sb.append("<item>");
						sb.append("<userid>");
						sb.append(userName);
						sb.append("</userid>");
						sb.append("<favid>");
						sb.append(eventsID);
						sb.append("</favid>");
						sb.append("<categoryId>");
						sb.append(Constant.EVENTS_COLID);
						sb.append("</categoryId>");
						sb.append("</item>");
						sbXml += sb.toString();
						i++;
					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		sb.append("</xml>");
		cur.close();
		closeDB();
		return i;

	}

	public boolean delEventCollection(String eId, String userName) {
		return excuteSql("delete from EventCollection where eventsID=" + "\""
				+ eId + "\"" + " and userName=" + "\"" + userName + "\"");
	}

	public boolean delAllEventCollection(String userName) {

		return excuteSql("delete from EventCollection where userName=" + "\""
				+ userName + "\"");
	}

	public boolean delEventCollect() {
		return excuteSql("drop table IF EXISTS EventCollection");
	}

	public int insertTableResearchCollection(String PaperID, String Title,
			String JournalAbbrName, String JournalName, String IF, String PublicDate, String Status,
			String IsFreeFullText, String userName) {
		String[] mValue = new String[]{PaperID,Title,JournalAbbrName,JournalName,IF,PublicDate,Status,IsFreeFullText,userName};
		if (excuteSql("INSERT INTO ResearchCollection(PaperID,Title,JournalAbbrName,JournalName,IF,PublicDate,Status,IsFreeFullText,userName) " +
				"values(?,?,?,?,?,?,?,?,?)", mValue))

			return 1;

		return 0;
	}
	
	public int insertTableResearchCollection(String articleID, String title,
			String author, String jourName, String tId, String type,
			String userName) {

		if (excuteSql("INSERT INTO ResearchCollection(PaperID,Title,userName) values(\""
				+ articleID + "\",\"" + title + "\",\"" + userName + "\")"))

			return 1;

		return 0;
	}

	public ArrayList<EntityObj>  selectResearchc(Context context, String userName, String str) {
		int i = 0;
		String articleID;
		HistoryFavoriteActivity.arrFavorResearchId = new ArrayList<String>();
		Cursor cur = getCursor(
				"select * from ResearchCollection where userName="
						+ "\"" + userName + "\"", null);
		ArrayList<EntityObj> list = new ArrayList<EntityObj>();
		
		String name="";
		String value ="";
		Map<String, Class> nameTypePair = new HashMap<String, Class>();
		nameTypePair.put("Title", String.class);
		nameTypePair.put("JournalName", String.class);
		nameTypePair.put("JournalAbbrName", String.class);
		nameTypePair.put("IF", String.class);
		nameTypePair.put("PublicDate", String.class);
		nameTypePair.put("Status", String.class);
		nameTypePair.put("IsFreeFullText", String.class);
		Class type;
		if (cur.getColumnCount() > 0) {
			while (cur.moveToNext()) {
				EntityObj obj = new EntityObj();
				for (int j = 0; j < cur.getColumnCount(); j++) {
					name = cur.getColumnName(j);
					type = nameTypePair.get(name);
					try {
						if (type == String.class) {
							value = cur.getString(j);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					obj.fieldContents.put(name, value);
				}
				list.add(obj);
			}
		}
		
		
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdrugId = cur.getColumnIndex("PaperID");
						articleID = cur.getString(indexdrugId);

						HistoryFavoriteActivity.arrFavorResearchId
								.add(articleID);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		return list;
	}
	
	public ArrayList<String> selectResearchc(Context context, String userName) {
		int i = 0;
		String articleID, name;
		ArrayList<String> arrayList = new ArrayList<String>();
		HistoryFavoriteActivity.arrFavorResearchId = new ArrayList<String>();
		Cursor cur = getCursor(
				"select * from ResearchCollection where userName="
						+ "\"" + userName + "\"", null);
		
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdrugId = cur.getColumnIndex("PaperID");
						articleID = cur.getString(indexdrugId);
						int indexdrugName = cur.getColumnIndex("Title");
						name = cur.getString(indexdrugName);
						arrayList.add(name);

						HistoryFavoriteActivity.arrFavorResearchId
								.add(articleID);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		return arrayList;
	}

	public int selectResearchCollection(String aId, String userName) {
		int i = 0;
		int id = 0;

		Cursor cur = getCursor(
				"select mobileID from ResearchCollection where PaperID="
						+ "\"" + aId + "\"" + " and userName=" + "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("mobileID");
						id = cur.getInt(indexdate);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return id;

	}

	public int selectAllResearchCollection(String userName) {
		int i = 0;
		StringBuffer sb = new StringBuffer();
		String articleID, title, taId;
		Cursor cur = getCursor(
				"select articleID,title,taId from ResearchCollection where userName="
						+ "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("articleID");
						articleID = cur.getString(indexdate);
						int indextitle = cur.getColumnIndex("title");
						title = cur.getString(indextitle);
						int indextaId = cur.getColumnIndex("taId");
						taId = cur.getString(indextaId);
						sb.append("<item>");
						sb.append("<userid>");
						sb.append(userName);
						sb.append("</userid>");
						sb.append("<favid>");
						sb.append(articleID);
						sb.append("</favid>");
						sb.append("<categoryId>");
						sb.append(Constant.RESEARCH_COLID);
						sb.append("</categoryId>");
						sb.append("</item>");
						sbXml += sb.toString();

						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return i;

	}

	public boolean delResearchCollection(String aId, String userName) {
		return excuteSql("delete from ResearchCollection where PaperID="
				+ "\"" + aId + "\"" + " and userName=" + "\"" + userName + "\"");
	}

	public boolean delAllResearchCollection(String userName) {
		return excuteSql("delete from ResearchCollection where userName="
				+ "\"" + userName + "\"");
	}

	public boolean delResearchCollect() {
		return excuteSql("drop table IF EXISTS ResearchCollection");
	}

	public int insertTableNewsCollection(String newsID, String title,
			String date, String userName) {
		if (excuteSql("INSERT INTO NewsCollection(newsID,title,userName) values(\""
				+ newsID + "\",\"" + title + "\",\"" + userName + "\")"))
			return 1;
		return 0;
	}

	public ArrayList<String> selectNews(String userName) {
		int i = 0;
		String newsID, name;
		HistoryFavoriteActivity.arrFavorNewsId = new ArrayList<String>();
		ArrayList<String> arrayList = new ArrayList<String>();
		Cursor cur = getCursor(
				"select newsID,title from NewsCollection where userName="
						+ "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdrugId = cur.getColumnIndex("newsID");
						newsID = cur.getString(indexdrugId);
						int indexdrugName = cur.getColumnIndex("title");
						name = cur.getString(indexdrugName);
						arrayList.add(name);

						HistoryFavoriteActivity.arrFavorNewsId.add(newsID);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		return arrayList;

	}

	public int selectNewsCollection(Context context, String nId, String userName) {
		int i = 0;
		int id = 0;

		Cursor cur = getCursor(
				"select mobileID from NewsCollection where newsID=" + "\""
						+ nId + "\"" + " and userName=" + "\"" + userName
						+ "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("mobileID");
						id = cur.getInt(indexdate);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return id;

	}

	public int selectAllNewsCollection(String userName) {
		int i = 0;
		String newsId;

		StringBuffer sb = new StringBuffer();
		Cursor cur = getCursor(
				"select newsID from NewsCollection where userName=" + "\""
						+ userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("newsID");
						newsId = cur.getString(indexdate);
						sb.append("<item>");
						sb.append("<userid>");
						sb.append(userName);
						sb.append("</userid>");
						sb.append("<favid>");
						sb.append(newsId);
						sb.append("</favid>");
						sb.append("<categoryId>");
						sb.append(Constant.NEWS_COLID);
						sb.append("</categoryId>");
						sb.append("</item>");
						sbXml += sb.toString();
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return i;

	}

	public boolean delNewsCollection(String nId, String userName) {
		return excuteSql("delete from NewsCollection where newsID=" + "\""
				+ nId + "\"" + " and userName=" + "\"" + userName + "\"");
	}

	public boolean delNews() {
		return excuteSql("drop table IF EXISTS NewsCollection");
	}

	public boolean delAllNewsCollection(String userName) {
		return excuteSql("delete from NewsCollection where userName=" + "\""
				+ userName + "\"");
	}

	public int insertTableHeartCalc(String title, int position, String userName) {
		if (excuteSql("INSERT INTO TabHeartCollect(title,userName,position) values(\""
				+ title + "\",\"" + userName + "\",\"" + position + "\")"))
			return 1;
		return 0;
	}

	public int selectTableHeartCalc(int pId, String userName) {
		int i = 0;
		int id = 0;
		Log.e("pid", String.valueOf(pId));
		Cursor cur = getCursor(
				"select mobileID from TabHeartCollect where position=" + pId
						+ " and userName=" + "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("mobileID");
						id = cur.getInt(indexdate);

						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return id;

	}

	public int selectAllTabDrugReference(String userName) {
		int i = 0;
		String newsID;
		StringBuffer sb = new StringBuffer();
		Cursor cur =  getCursor(
				"select drugId from TabDrugReference where userName=" + "\""
						+ userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("drugId");
						newsID = cur.getString(indexdate);
						sb.append("<item>");
						sb.append("<userid>");
						sb.append(userName);
						sb.append("</userid>");
						sb.append("<favid>");
						sb.append(newsID);
						sb.append("</favid>");
						sb.append("<categoryId>");
						sb.append(Constant.DRUG_COLID);
						sb.append("</categoryId>");
						sb.append("</item>");
						sbXml += sb.toString();

						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		return i;

	}
	
	
	public int selectAllTableHeartCalc(String userName,
			String HistoryAndFavoritUserName) {
		int i = 0;
		String postion;
		StringBuffer sb = new StringBuffer();
		Cursor cur = getCursor(
				"select position from TabHeartCollect where userName=" + "\""
						+ userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("position");
						postion = cur.getString(indexdate);
						sb.append("<item>");
						sb.append("<userid>");
						sb.append(HistoryAndFavoritUserName);
						sb.append("</userid>");
						sb.append("<favid>");
						sb.append(postion);
						sb.append("</favid>");
						sb.append("<categoryId>");
						sb.append(Constant.CALC_COLID);
						sb.append("</categoryId>");
						sb.append("</item>");
						sbXml += sb.toString();

						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return i;

	}

	public boolean delTableHeartCalc(int pId, String userName) {
		return excuteSql("delete from TabHeartCollect where position=" + "\""
				+ pId + "\"" + " and userName=" + "\"" + userName + "\"");
	}

	public boolean delAllTableHeartCalc(String userName) {
		return excuteSql("delete from TabHeartCollect where userName=" + "\""
				+ userName + "\"");
	}

	public boolean delTableHeartCalc() {
		return excuteSql("drop table IF EXISTS TabHeartCollect");
	}

	public int insertTabCalc(String title, String classname, String userName) {
		if (excuteSql("INSERT INTO TabCalc(Calctitle,userName,class) values(\""
				+ title + "\",\"" + userName + "\",\"" + classname + "\")"))

			return 1;

		return 0;
	}

	public int selectTabCalc(String pId, String userName) {
		int i = 0;
		int id = 0;

		Cursor cur = getCursor(
				"select mobileID from TabCalc where Calctitle=" + "\"" + pId
						+ "\"" + " and userName=" + "\"" + userName + "\"",
				null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("mobileID");
						id = cur.getInt(indexdate);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return id;

	}

	public ArrayList<String> selectTabCalcc(Context context, String userName) {
		int i = 0;
		String name, className;
		ArrayList<String> arrayList = new ArrayList<String>();
		HistoryFavoriteActivity.arrFavorTabcalcClass = new ArrayList<String>();
		Cursor cur = getCursor(
				"select Calctitle,class from TabCalc where userName=" + "\""
						+ userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdrugId = cur.getColumnIndex("class");
						className = cur.getString(indexdrugId);
						int indexdrugName = cur.getColumnIndex("Calctitle");
						name = cur.getString(indexdrugName);
						arrayList.add(name);

						HistoryFavoriteActivity.arrFavorTabcalcClass
								.add(className);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		return arrayList;

	}

//	public int selectAllTabCalc(String userName,Context context) {
//		int i = 0;
//		String classname;
//		CalculatorAdapter calcDBAdapter = new CalculatorAdapter(context);
//		StringBuffer sb = new StringBuffer();
//		Cursor cur = getCursor("select Calctitle from TabCalc where userName="
//				+ "\"" + userName + "\"", null);
//		try {
//			if (cur != null) {
//				if (cur.moveToFirst()) {
//
//					do {
//						int indexdate = cur.getColumnIndex("Calctitle");
//						classname = cur.getString(indexdate);
//						sb.append("<item>");
//						sb.append("<userid>");
//						sb.append(userName);
//						sb.append("</userid>");
//						sb.append("<favid>");
//						Log.e("classname", classname);
//						sb.append( calcDBAdapter.getCalcIdFromName(classname));
//						sb.append("</favid>");
//						sb.append("<categoryId>");
//						sb.append(Constant.CALC_COLID);
//						sb.append("</categoryId>");
//						sb.append("</item>");
//						sbXml += sb.toString();
//						i++;
//					} while (cur.moveToNext());
//				}
//			}
//		} catch (Exception e) {
//			Log.e("exception", e.getMessage());
//		}
//		sb = null;
//		cur.close();
//		closeDB();
//		return i;
//
//	}

	public boolean delTabCalc(String pId, String userName) {
		return excuteSql("delete from TabCalc where Calctitle=" + "\"" + pId
				+ "\"" + " and userName=" + "\"" + userName + "\"");
	}

	public boolean delAllTabCalc( String userName) {
		return excuteSql("delete from TabCalc where userName=" + "\""
				+ userName + "\"");
	}

	public boolean delTabCal() {
		return excuteSql("drop table IF EXISTS TabCalc");
	}

	public int insertTabTumor(String cNtitle, String eNtitle, String rank,
			String userName) {
		if (excuteSql("INSERT INTO TabTumor(rank,area_cn,userName) values(\""
				+ rank + "\",\"" + cNtitle + "\",\"" + userName + "\"))"))
			return 1;

		return 0;
	}

	public int selectTabTumor(String rId, String userName) {
		int i = 0;
		int id = 0;

		Cursor cur = getCursor(
				"select mobileID from TabTumor where rank=" + "\"" + rId + "\""
						+ " and userName=" + "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("mobileID");
						id = cur.getInt(indexdate);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return id;

	}

	public int selectAllTabTumor(String userName,
			String HistoryAndFavoritUserName) {
		int i = 0;
		String position;
		StringBuffer sb = new StringBuffer();
		Cursor cur = getCursor("select rank from TabTumor where userName="
				+ "\"" + userName + "\"", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("rank");
						position = cur.getString(indexdate);
						sb.append("<item>");
						sb.append("<userid>");
						sb.append(HistoryAndFavoritUserName);
						sb.append("</userid>");
						sb.append("<favid>");
						sb.append(position);
						sb.append("</favid>");
						sb.append("<categoryId>");
						sb.append(Constant.CALC_COLID);
						sb.append("</categoryId>");
						sb.append("</item>");
						sbXml += sb.toString();
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return i;

	}

	public boolean delTabTumor(String rId, String userName) {
		return excuteSql("delete from TabTumor where rank=" + "\"" + rId + "\""
				+ " and userName=" + "\"" + userName + "\"");

	}

	public boolean delAllTabTumor(String userName) {
		return excuteSql("delete from TabTumor where userName=" + "\""
				+ userName + "\"");
	}

	public boolean delTabTum() {
		return excuteSql("drop table IF EXISTS TabTumor");
	}


}
