package com.jibo.dbhelper;

import java.util.ArrayList;

import com.jibo.activity.HistoryFavoriteActivity;
import com.jibo.common.Constant;
import com.jibo.data.entity.PaperDetailEntity;
import com.jibo.data.entity.ResearchCategoryBean;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ResearchAdapter extends BaseSqlAdapter{

	public ResearchAdapter(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}
	
	
	public  int insertKWID(Context context,
			ArrayList<ResearchCategoryBean> values) {
		String sql = "";
		String[] params = null;
		try {

			for (int i = 0; i < values.size(); i++) {
				params = new String[4];
				params[0] = values.get(i).getKw_id();
				params[1] = values.get(i).getTa_id();
				params[2] = values.get(i).getKw();
				params[3] = values.get(i).getKwCount();
				sql = "INSERT INTO KW_TA(KW_ID, TA_ID, KW, COUNT) VALUES(?, ?, ?, ?)";
//				excuteSql(sql, params);
				excuteSql(sql,params);
			}

			Log.e("print", "insert_ok");

			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public ArrayList<ResearchCategoryBean> selectTACount(Context context, String ta_id) {
		int i = 0;
		ArrayList<ResearchCategoryBean> list=new ArrayList<ResearchCategoryBean>();
				Cursor cur = getCursor("SELECT KW_ID,KW,COUNT FROM KW_TA where TA_ID=" + "\"" + ta_id
						+ "\"" + "order by KW_ID asc",null);

		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						ResearchCategoryBean rb=new ResearchCategoryBean();
					    rb.setKw_id(cur.getString(0));
	                    rb.setKw(cur.getString(1));
						rb.setKwCount(String.valueOf(cur.getInt(2)));
						rb.setTa_id(ta_id);
		
//						if (i == 0) {
//							Research.KW_ID_STR = String.valueOf(cur.getInt(0));
//							Research.KW_COUNT_STR = String.valueOf(cur
//									.getInt(2));
//						} else {
//							Research.KW_ID_STR = Research.KW_ID_STR + ","
//									+ String.valueOf(cur.getInt(0));
//							Research.KW_COUNT_STR = Research.KW_COUNT_STR + ","
//									+ String.valueOf(cur.getInt(2));
//						}
						list.add(rb);
						i++;
					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Log.e("exception", );
		}

		return list;

	}
	
	/**
	 * 是否订阅
	 * @param periodicalID 期刊id
	 * @return
	 */
	public boolean isSubscribed(String periodicalID, String userName) {
		Cursor cur  = null;
		try{
			cur = getCursor(
					"select 1 from ResearchSubscribe where PeriodicalID='" + periodicalID + "' and userName ='"
							+ userName +"'", null);
			if(cur.getCount() > 0) return true;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cur != null){
				cur.close();
			}
		}
		return false;
	}
	
	public boolean insertSubscribe(String PeriodicalID, String userName) {
		Cursor cur  = null;
		try{
			cur = getCursor(
					"select * from ResearchSubscribe where PeriodicalID='" + PeriodicalID + "' and userName ='"
							+ userName +"'", null);
			if(cur.getCount() > 0) return true;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			cur.close();
		}
		
		String[] mValue = new String[]{PeriodicalID,userName};
		if (excuteSql("INSERT INTO ResearchSubscribe(PeriodicalID,userName) " +
				"values(?,?)", mValue))
			return true;

		return false;
	}
	
	public boolean deleteSubscribe(String PeriodicalID, String userName) {
		if (excuteSql("DELETE FROM ResearchSubscribe where PeriodicalID = '" + PeriodicalID + "' and userName ='"
				+ userName +"'"))
			return true;

		return false;
	}

	public boolean deleteTA(Context context, String ta_id) {
		String sql = "DELETE FROM KW_TA where TA_ID=" + "\"" + ta_id + "\"";

		try {
			excuteSql(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	public boolean insertPaperDetail(PaperDetailEntity entity) {
		deleteMoresResearchCollection();
		
		Cursor cur  = null;
		try{
			cur = getCursor(
					"select * from PaperDetail where id='" + entity.id +"'", null);
			if(cur.getCount() > 0) return true;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cur != null)
			cur.close();
		}
		
		String[] mValue = new String[]{entity.id,entity.title==null?"":entity.title,entity.journalID==null?"":entity.journalID,entity.publicDate==null?"":entity.publicDate,
				entity.keyWords==null?"":entity.keyWords.toString(),entity.topRank==null?"":entity.topRank,entity.rank==null?"":entity.rank,
				entity.isFreeFullText+"",entity.comments==null?"":entity.comments,entity.commentsType==null?"":entity.commentsType,
				entity.veiwedCount==null?"":entity.veiwedCount,entity.abstarct==null?"":entity.abstarct,entity.authors==null?"":entity.authors.toString(),
						entity.publicationType==null?"":entity.publicationType,
				entity.sourceURL==null?"":entity.sourceURL,entity.freeFullTextURL==null?"":entity.freeFullTextURL,
						entity.pdfURL==null?"":entity.pdfURL,entity.IF==null?"":entity.IF,
				entity.affiliations==null?"":entity.affiliations.toString(),entity.journalName==null?"":entity.journalName,entity.CLC==null?"":entity.CLC.toString(),
				entity.pubTypes==null?"":entity.pubTypes.toString(),entity.DOI==null?"":entity.DOI,entity.language==null?"":entity.language,entity.mesh==null?"":entity.mesh.toString(),
				entity.substances==null?"":entity.substances.toString(),entity.categorys==null?"":entity.categorys.toString()};
		if (excuteSql("INSERT INTO PaperDetail(id,title,journalID,publicDate,keyWords,topRank,rank," +
				"isFreeFullText,comments,commentsType,veiwedCount,abstarct,authors,publicationType," +
				"sourceURL,freeFullTextURL,pdfURL,IF,affiliations,journalName,CLC," +
				"pubTypes,DOI,language,mesh,substances,categorys)" +
				"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", mValue))
			return true;

		return false;
	}
	
	/**
	 * 删除PaperDetail多余的数据，如果超过200条,删掉50条
	 */
	private void deleteMoresResearchCollection(){
		int baseCount = 200;
		String sql = "SELECT count(*) AS totalcount FROM PaperDetail ";
		String deleteSql = "DELETE FROM PaperDetail WHERE mobileID IN (SELECT mobileID FROM PaperDetail ORDER BY mobileID LIMIT 0, 50)";
		int totalCount = 0;
		try {
			Cursor c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToFirst()){
					totalCount = c.getInt(c.getColumnIndex("totalcount"));
				}
				if(totalCount > baseCount){
					excuteSql(deleteSql);
				}
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
	}
	
	public PaperDetailEntity selectPaperDetail(String id) {
		PaperDetailEntity entity = null;
		Cursor cur  = null;
		try{
			cur = getCursor(
					"select * from PaperDetail where id='" + id +"'", null);
			if(cur != null && cur.moveToFirst()){
				entity = new PaperDetailEntity();
				entity.id = cur.getString(1);
				entity.title = cur.getString(2);
				entity.journalID = cur.getString(3);
				entity.publicDate = cur.getString(4);
				entity.keyWords =string2Arraylist(cur.getString(5));
				entity.topRank = cur.getString(6);
				entity.rank = cur.getString(7);
				entity.isFreeFullText = (cur.getString(8) == null || cur.getString(8).trim().equals("")) ? null : Boolean.parseBoolean(cur.getString(8));
				entity.comments = cur.getString(9);
				entity.commentsType = cur.getString(10);
				entity.veiwedCount = cur.getString(11);
				entity.abstarct = cur.getString(12);
				entity.authors = string2Arraylist(cur.getString(13));
				entity.publicationType = cur.getString(14);
				entity.sourceURL = cur.getString(15);
				entity.freeFullTextURL = cur.getString(16);
				entity.pdfURL = cur.getString(17);
				entity.IF = cur.getString(18);
				entity.affiliations = string2Arraylist(cur.getString(19));
				entity.journalName = cur.getString(20);
				entity.CLC = string2Arraylist(cur.getString(21));
				entity.pubTypes = string2Arraylist(cur.getString(22));
				entity.DOI = cur.getString(23);
				entity.language = cur.getString(24);
				entity.mesh = string2Arraylist(cur.getString(25));
				entity.substances = string2Arraylist(cur.getString(26));
				entity.categorys = string2Arraylist(cur.getString(27));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cur != null)
			cur.close();
		}
		
		return entity;
	}
	
	private ArrayList<String> string2Arraylist(String string){
		if(string == null || string.trim().equals("") || string.trim().equals("[]")){
			return null;
		}
		ArrayList<String> list = new ArrayList<String>();
		String[] str = (string.substring(1, string.toString().length() - 1)).split(",");
		for(int i=0; i<str.length; i++){
			list.add(str[i].trim());
		}
		return list;
	}
}
