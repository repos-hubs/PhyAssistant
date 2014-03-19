package com.jibo.dbhelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jibo.common.Constant;
import com.jibo.data.entity.DrugInteractEntity;
import com.jibo.util.Logs;

import android.content.Context;
import android.database.Cursor;

public class InteractAdapter1 extends BaseSqlAdapter {
	public InteractAdapter1(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
		createTemp();
	}

	// 第一次调用需要创建一个临时表
	public void createTemp() {
		String sql1 = "create table IF NOT EXISTS  TempInteraction (_ID INTEGER PRIMARY KEY AUTOINCREMENT,proid varchar(50),productID varchar(50),interaction varchar(100));";
		String sql2 = "DELETE from  TempInteraction ;";
		String sql3 = "insert into TempInteraction (productID,proid,Interaction) select DrugInteractionPID.ProductID as productID,DrugInteraction.proid as proid,DrugInteraction.Interaction as Interaction from DrugInteractionPID,DrugInteraction where DrugInteractionPID.\"ID in Reg or not\" ='Reg' and DrugInteractionPID.\"Drug/Test\" = DrugInteraction.Drug and DrugInteraction.proid is not NULL;";
		String sql = sql1 + sql2+ sql3;
		excuteSql(sql);
	}
	// 第一次调用需要创建一个临时表
		public void createInteractor() {
			String sql1 = "create table IF NOT EXISTS  TempInteraction (_ID INTEGER PRIMARY KEY AUTOINCREMENT,proid varchar(50),productID varchar(50),interaction varchar(100));";
			String sql2 = "DELETE from  TempInteraction ;";
			String sql3 = "insert into TempInteraction (productID,proid,Interaction) select DrugInteractionPID.ProductID as productID,DrugInteraction.proid as proid,DrugInteraction.Interaction as Interaction from DrugInteractionPID,DrugInteraction where DrugInteractionPID.\"ID in Reg or not\" ='Reg' and DrugInteractionPID.\"Drug/Test\" = DrugInteraction.Drug and DrugInteraction.proid is not NULL;";
			String sql = sql1 + sql2+ sql3;
			excuteSql(sql);
		}
	
	public String test(){
		String sql = "select * from DrugInteraction as a1,DrugInteraction  as a2 where a1.proid = 2009714 and a2.proid = 2010365";
		Cursor c = getCursor(sql, null);
		String Interaction="";
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					Interaction = c.getString(c.getColumnIndex("Interaction"));
					
				} while (c.moveToNext());
			}
		}

		c.close();
		closeDB();
		return Interaction;
	}
	/**
	 * 查询药物名字
	 * 
	 * @return
	 */
	public List<DrugInteractEntity> selectDrugName() {
		String sql = "select DrugID,DrugNameEN,DrugNameCN from DrugBasicInfo ";
		Cursor c = null;
		List<DrugInteractEntity> list = null;
		try {
			c = getCursor(sql, null);
			list = new ArrayList<DrugInteractEntity>();
			if (c != null) {
				if (c.moveToFirst()) {
					do {
//						String drugId = c.getString(c.getColumnIndex("DrugID"));
//						String drugNameEN = c.getString(c
//								.getColumnIndex("DrugNameEN"));
//						String drugName = c.getString(c
//								.getColumnIndex("DrugNameCN"));
//						list.add(new DrugInteractEntity(drugId, drugName,
//								drugNameEN));
					} while (c.moveToNext());
				}
			}

			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public int selectInteractorNum(String drugId){
		String sql = "select count(*) from TempInteraction where proid ="+ drugId+" and productID is not null;";
		Cursor c = null;
		int num = 0;
		try {
			c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToFirst()) {
					num = c.getInt((c.getColumnIndex("count(*)")));						
				}
			}
			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}
//	/**
//	 * 查询相关药物的drugId
//	 * 
//	 * @return
//	 */
//	public Map<String,Map<String,String>> selectInteractorDrugId(String drugId) {
//		String sql = "select productID from TempInteraction where proid ="+ drugId+" and productID is not null;";
//		List<DrugInteractEntity> list = null;
//	}
	/**
	 * 查询相关药物的名字 DrugBasicInfo DrugInteraction DrugInteractionPID
	 * 
	 * @return
	 */
	public Map<String,Map<String,String>> selectInteractorDrugName(String drugId) {
		String sql = "select productID from TempInteraction where proid ="+ drugId+" and productID is not null;";
		//String sql = "select productID,proid from TempInteraction where proid is not null and productID is not null;";
		Cursor c = null;
		Cursor c1 = null;
		//Map<productId,Map<DrgName,DrgNameEN>> Map的size就是与其关联的药物的数量
		Map<String,String> interactionName = new HashMap<String,String>();
		Map<String,Map<String,String>> interactionNameList = new HashMap<String,Map<String,String>>();
		try {
			c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						String productID = c.getString(c.getColumnIndex("productID"));
						//根据productID去表中查询DrugBasicInfo查询名字
						String sql1 = "select DrugNameEN,DrugNameCN from DrugBasicInfo where DrugID="+productID;
						c1 = getCursor(sql1, null);
						if(c1 != null){
							if(c1.moveToFirst()){
								String DrgName = c.getString(c.getColumnIndex("DrugNameCN"));
								String DrgNameEN = c.getString(c.getColumnIndex("DrugNameEN"));
								interactionName.put(DrgName, DrgNameEN);
							}
						}
						interactionNameList.put(productID, interactionName);
					} while (c.moveToNext());
				}
			}

			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return interactionNameList;
	}
	/**
	 * 查询相两种药物是否相关DrugBasicInfo DrugInteraction DrugInteractionPID
	 * 
	 * @return
	 */
	public boolean selectInteractorDetail(String drugId1,String drugId2) {
		String sql = "select count(*) from TempInteraction where proid ="+ drugId1+" and productID ="+drugId2;
		Cursor c = null;
		int num = 0;
		c = getCursor(sql, null);
		if (c != null) {
			if (c.moveToFirst()) {
				num = c.getInt(c.getColumnIndex("count(*)"));
			}
			c.close();
		}
		if(num>0) return true;
		return false;
	}
	/**
	 * 查询相关药物的DrugId
	 * 
	 * @return
	 */
	public List<String> selectInteractorDetail(String drugId) {
		String sql = "select productID from TempInteraction where proid ="+ drugId+" and productID is not null;";
		//String sql = "select productID,proid from TempInteraction where proid is not null and productID is not null;";
		Cursor c = null;
		List<String> list = new ArrayList<String>();
		try {
			c = getCursor(sql, null);
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						String productID = c.getString(c.getColumnIndex("productID"));
						list.add(productID);
					} while (c.moveToNext());
				}
			}

			c.close();
			closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}

/*
 * CREATE TABLE IF NOT EXISTS DrugInteraction (
_id INTEGER PRIMARY KEY AUTOINCREMENT,
CreatedStamp char(32),
LastUpdatedStamp char(32) NOT NULL DEFAULT '',
PID varchar,
IID varchar ,
Role int(11) ,
Status int(11) 
);
CREATE TABLE IF NOT EXISTS DrugInteractionDescription (
_id INTEGER PRIMARY KEY AUTOINCREMENT,
CreatedStamp char(32) NOT NULL DEFAULT '',
LastUpdatedStamp char(32) NOT NULL DEFAULT '',
IID varchar NOT NULL DEFAULT '',
CultureInfo varchar NOT NULL DEFAULT '',
Description varchar NOT NULL DEFAULT '',
Status int(11) NOT NULL DEFAULT ''
);
CREATE TABLE IF NOT EXISTS DrugInteractionProduct (
_id INTEGER PRIMARY KEY AUTOINCREMENT,
CreatedStamp char(32) NOT NULL DEFAULT '',
LastUpdatedStamp char(32) NOT NULL DEFAULT '',
PID varchar NOT NULL DEFAULT '',
Name varchar NOT NULL DEFAULT '',
CultureInfo varchar NOT NULL DEFAULT '',
Status int(11) NOT NULL DEFAULT ''
);
 */
