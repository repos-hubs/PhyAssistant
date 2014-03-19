package com.jibo.dbhelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jibo.common.Constant;
import com.jibo.data.entity.DrugInteractEntity;
import com.jibo.data.entity.InteractionPidsEntity;
import com.jibo.util.Logs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.text.TextUtils;

public class InteractAdapter extends BaseSqlAdapter {
	private String tbName = "jibo.db";
	private Context context;
	private String dbName;
	public InteractAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = Constant.DATA_PATH_GBADATA + File.separator + tbName;
		mDbHelper = new InteractHelper(ctx, dbName, null, vesion);
	}
	public Set<Map<String, String>> selectInteractionsRelationship(Set pidSet) {
		List pidArr = new ArrayList(pidSet);  
		String str1 = "SELECT A.IID AS IID ,A.PID AS PID1 ,B.PID AS PID2 FROM DrugInteractionRelationship AS A INNER JOIN DrugInteractionRelationship AS B ON A.iid = B.iid WHERE ( ( A.pid = ";
		String str2 = "";
 		int n = pidArr.size();
 		for (int j = 0; j < n-1; j++) {
 			str2 += str1;
			for (int i = j; i < n; i++) {
				if (i == j+1) {
					str2 += " AND B.pid IN ( ";
				}
				str2 += "'"+pidArr.get(i)+"'";
				if (i >= j+1 && i < n-1) {
					str2 += " , ";
				}
				if(i == n-1){
					str2 += " ) )  and A.pid <> B.pid and A.role <> B.role) ";
				}
			}
			if(j < n-2)
				str2 += " union ";
 		}
 		String sql = str2;
		Set<Map<String, String>> list = new LinkedHashSet<Map<String, String>>();
		Cursor c = null;
		try {
			c = getCursor(sql, null);		
			if (c != null && c.moveToFirst()) {
				do{
						Map<String, String> map = new HashMap<String, String>();
						String pid1 = c.getString(c.getColumnIndex("PID1"));
						String pid2 = c.getString(c.getColumnIndex("PID2"));
						String iid = c.getString(c.getColumnIndex("IID"));
						map.put("pid1", pid1);
						map.put("pid2", pid2);
						map.put("iid", iid);
						list.add(map);
					}while(c.moveToNext());
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public Set<Map<String, String>> selectInteractionsRelationship_bak(Set pidSet) {
		List pidArr = new ArrayList(pidSet);  
		String sql = "Select A.PID as PID1 , B.PID as PID2, A.IID  from DrugInteractionRelationship as A inner join DrugInteractionRelationship as B on A.IID = B.IID where A.Role <> B.Role and (";
		String str = "";
		int n = pidArr.size();
		for (int i = 2; i <= n; i++) {
			if (i > 2) {
				str += " or ";
			}
			String str1 = "";
			for (int j = i; j <= n; j++) {
				str1 += "'" + pidArr.get(j - 1) + "'";
				if (j < n) {
					str1 += ",";
				}
			}
			str += "( A.PID = '" + pidArr.get(i - 2) + "' and B.PID in (" + str1
					+ ") )";
		}
		sql += str+")";
		//List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Set<Map<String, String>> list = new LinkedHashSet<Map<String, String>>();
		Cursor c = null;
		try {
			c = getCursor(sql, null);		
			if (c != null && c.moveToFirst()) {
				do{
						Map<String, String> map = new HashMap<String, String>();
						String pid1 = c.getString(c.getColumnIndex("PID1"));
						String pid2 = c.getString(c.getColumnIndex("PID2"));
						String iid = c.getString(c.getColumnIndex("IID"));
						map.put("pid1", pid1);
						map.put("pid2", pid2);
						map.put("iid", iid);
						list.add(map);
					}while(c.moveToNext());
			}
			c.close();
		//closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public List<DrugInteractEntity> selectDrugNameByPinyin(String str,String cultureInfo){
		String sql = "SELECT  d.* from DrugProduct d where d.PID in " +
				"(select DISTINCT DrugID from SearchDictionary where AlternativeName like '%"+str+"%') " +
						"and CultureInfo='"+cultureInfo+"' limit 0 ,24;";
		Cursor c = null;
		Set<DrugInteractEntity> set = null;
		try {
			c = getCursor(sql, null);
			set = new LinkedHashSet<DrugInteractEntity>();
			if (c != null && c.moveToFirst()) {
				do{
					String drugId = c.getString(c.getColumnIndex("PID"));
					String drugName = c.getString(c.getColumnIndex("Name"));
					set.add(new DrugInteractEntity(drugId, drugName));
				}while(c.moveToNext());
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<DrugInteractEntity> list = new ArrayList<DrugInteractEntity>(set);
		return list;
	}
	public boolean insertData(String table, Map<String, String> map) {
		Iterator iter = map.entrySet().iterator();
		String col = "";
		String value = "'";
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			col += entry.getKey() + ",";
			value += entry.getValue() + "','";
		}
		col = col.substring(0, col.length() - 1);
		value = value.substring(0, value.length() - 2);
		String sql = "insert into " + table + "(" + col + ") values(" + value
				+ ")";
		return excuteSql(sql);
	}
	/**检查最后一次更新时间
	 * @return
	 */
	public String checkLastSyncTime(String table){
		String sql = "select LastUpdatedStamp from "+table+" order by LastUpdatedStamp desc limit 0,1";
		Cursor c = null;
		String updatedStamp = "";
	    Date d = null;  
	    String oldUpdatedStamp = "";
		try {
			c = getCursor(sql, null);
			if (c != null) {
				if(c.moveToFirst()){
					 updatedStamp=c.getString(c.getColumnIndex("LastUpdatedStamp")).replace(" ", "_");
				}
			}
			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedStamp;
	}
	
	public boolean updateData(String table, Map<String, String> map,
			Map<String, String> wheres) {
		String str = "";
		if(map.size()>0){
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				str += entry.getKey() + "='" + entry.getValue() + "',";
			}
			str = str.substring(0, str.length() - 1);
		}
		str = str + " where ";
		Iterator iter1 = wheres.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry entry1 = (Map.Entry) iter1.next();
			str += entry1.getKey() + "='" + entry1.getValue() + "' and ";
		}
		str = str.substring(0, str.length() - 5);
		String sql = "update " + table + " SET " + str;
		return excuteSql(sql);
	}
	public boolean delData(String table, Map<String, String> wheres) {
		String str = " where ";
		Iterator iter1 = wheres.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry entry1 = (Map.Entry) iter1.next();
			str += entry1.getKey() + "='" + entry1.getValue() + "' and ";
		}
		str = str.substring(0, str.length() - 5);
		String sql = "delete from " + table + str;
		return excuteSql(sql);
	}
	
	public int selectData(String table, Map<String, String> wheres) {
		String str = " where ";
		Iterator iter1 = wheres.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry entry1 = (Map.Entry) iter1.next();
			str += entry1.getKey() + "='" + entry1.getValue() + "' and ";
		}
		str = str.substring(0, str.length() - 5);
		String sql = "select count(*) from " + table + str;
		Cursor c = null;
		int count = 0;
		try{
			c = getCursor(sql, null);
			if (c != null) {
				if(c.moveToFirst()){
					count = c.getInt(c.getColumnIndex("count(*)"));
				}
			}
			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	/**
	 * 查询药物名字
	 * 
	 * @return
	 */
	public List<DrugInteractEntity> selectDrugNameAndPID(Map<String, String> wheres) {
		String sql = "select PID,Name from DrugProduct ";
		if (wheres != null) {
			Iterator iter = wheres.entrySet().iterator();
			String str = "";
			String value = "";
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				str += entry.getKey() + "='" + entry.getValue() + "' and ";
			}
			str = str.substring(0, str.length() - 5);
			sql = sql + " where " + str;
		}
		sql += " order by name";
		Cursor c = null;
		Set<DrugInteractEntity> set = null;
		try {
			c = getCursor(sql, null);
			//list = new ArrayList<DrugInteractEntity>();
			set = new LinkedHashSet<DrugInteractEntity>();
			if (c != null) {
				while (c.moveToNext()) {
						String drugId = c.getString(c.getColumnIndex("PID"));
						String drugName = c.getString(c.getColumnIndex("Name"));
						set.add(new DrugInteractEntity(drugId, drugName));
					}
			}

			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<DrugInteractEntity> list = new ArrayList<DrugInteractEntity>(set);  
		return list;
	}

	/**
	 * 根具iis查询相关药物的相互作用
	 * 
	 * @return
	 */
	public List<DrugInteractEntity> selectInteractorDetail(
			Map<String, String> wheres) {
		String sql = "select * from DrugInteraction";
		Iterator iter = wheres.entrySet().iterator();
		String str = "";
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			str += entry.getKey() + "='" + entry.getValue() + "' and ";
		}
		str = str.substring(0, str.length() - 5);
		sql = sql + " where " + str;
		Cursor c = null;
		List<DrugInteractEntity> list = new ArrayList<DrugInteractEntity>();
		try {
			c = getCursor(sql, null);
			if (c != null) {
				while (c.moveToNext()) {
						DrugInteractEntity drugInteractEntity = new DrugInteractEntity();
						drugInteractEntity.description = c.getString(c.getColumnIndex("Description"));
						drugInteractEntity.comments = c.getString(c.getColumnIndex("Comments"));
						list.add(drugInteractEntity);
					}
			}

			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public InteractionPidsEntity selectPidsByIId(Map<String, String> wheres) {
		String sql = "select PID from DrugInteractionRelationship";
		Iterator iter = wheres.entrySet().iterator();
		String str = "";
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			str += entry.getKey() + "='" + entry.getValue() + "' and ";
		}
		str = str.substring(0, str.length() - 5);
		sql = sql + " where " + str;
		Cursor c = null;
		InteractionPidsEntity interactionPidsEntity = new InteractionPidsEntity();
		try {
			c = getCursor(sql, null);
			if (c != null) {
				while (c.moveToNext()) {
					if(TextUtils.isEmpty(interactionPidsEntity.getPid1())){
						interactionPidsEntity.setPid1(c.getString(c.getColumnIndex("PID")));
					}else{
						interactionPidsEntity.setPid2(c.getString(c.getColumnIndex("PID")));
					}
				}
			}

			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return interactionPidsEntity;
	}
	public String selectfieldByid(String id){
		String sql = "select fileId from ahfsdrug where pid = '"+id+"'";
		Cursor c = null;
		String fileId = "";
		try {
			c = getCursor(sql, null);
			if (c != null) {
				if(c.moveToFirst()){
					fileId = c.getString(c.getColumnIndex("fileId"));
				}
			}

			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileId;
	}
	class InteractHelper extends SQLiteOpenHelper {
		public InteractHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
