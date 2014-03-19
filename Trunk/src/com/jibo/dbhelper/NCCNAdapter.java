package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jibo.common.Constant;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


public class NCCNAdapter extends BaseSqlAdapter {
	private final static String TABLE_CALCULATE = "nccn_disease.db";
	private static String dbName;
	private Context context;
	public NCCNAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = Constant.DATA_PATH_MARKET_INSTALL + File.separator + TABLE_CALCULATE;
		mDbHelper = new NCCNHelper(ctx, dbName, null, vesion);
	}
	/**
	 * @author Rafeal Piao
	 * @description Judge if database have loaded
	 * @param void
	 * @return boolean
	 * @Exception 
	 */
	public boolean dbIsLoaded() {
    	String sql = "select * from NCCN_Disease_name";
    	boolean resultFlag = false;
    	try {
    		Cursor cursor = getCursor(sql, null);
    		if(cursor.getCount() > 0) {
    			resultFlag = true;
    		}
    		
        	cursor.close();
    	} catch (SQLiteException e) {
    		resultFlag = false;
    	}
    	closeDB();
    	return resultFlag;
	}
	
	/**
     * @author Rafeal Piao
     * @Description Get Content from Node Id
     * @param node: node Id
     * @param diseaseID: Disease ID
     * @return strContent
     */
    public String getContentFromNode(String node, String diseaseID) {
    	String sql = "select * from NCCN_Disease_content where [NodeID] = '"+node+"' and DiseaseID='"+diseaseID+"'";
    	Cursor cursor = getCursor(sql, null);
    	String strContent = "";
    	
    	while(cursor.moveToNext()) {
    		strContent = cursor.getString(3);
    	}
    	cursor.close();
    	closeDB();
    	return strContent;
    }
    
    /**
	 * @author Rafeal Piao
	 * @Description Get Link Id from sub_node
	 * @param sId: sub node id
	 * @param diseaseID: Disease id
	 * @return String linkID
	 */
	public String getLinkFromSubId(String sId, String diseaseID) {
		String sql = "select Link from NCCN_Disease_relationship where [Sub_Node] = '"+sId+"' and Disease_ID ='"+diseaseID+"'";
		Cursor cursor = getCursor(sql, null);
		String linkId = "";
		while(cursor.moveToNext()) {
			linkId = cursor.getString(0);
		}
		cursor.close();
		closeDB();
		
		return linkId;
	}
	
	/**
	 * @author Rafeal Piao
	 * @description Get Comments by disease id
	 * @param id: Disease id
	 * @return String: Comment
	 * @Exception 
	 */
	public String getComments(String id) {
    	String sql = "select * from NCCN_Disease_content where [NodeID] = 'M' and DiseaseID ='"+id+"'";
    	Cursor cursor = getCursor(sql, null);
    	String strContent = "";
    	
    	while(cursor.moveToNext()) {
    		strContent = strContent + cursor.getString(3) +"<br>";
    	}
    	cursor.close();
    	closeDB();
    	return strContent;
	}
	
	public String getDiseaseLst(String id) {
    	String sql = "select * from NCCN_Disease_name where Disease_ID ='"+id+"'";
    	Cursor cursor = getCursor(sql, null);
    	String name = "";
    	while(cursor.moveToNext()) {
			name = cursor.getString(1);
    	}
    	cursor.close();
    	closeDB();
    	return name;
	}
	
	/**
     * @author Rafeal Piao
     * @Description Get Disease Name from id
     * @param id: Disease id
     * @return String Disease Name
     */
    public String getDiseaseName(String id) {
    	String sql = "select Disease_Name from NCCN_Disease_name where Disease_ID = '"+id+"'";
    	Cursor cursor = getCursor(sql, null);
    	String diseaseName = "";
    	while(cursor.moveToNext()) {
    		diseaseName = cursor.getString(0);
    	}
    	
    	cursor.close();
    	closeDB();
    	return diseaseName;
    }
    
    /**
     * @author Rafeal Piao
     * @Description Get SectorName from NodeID
     * @param nodeID
     * @return String
     */
    public String getSectorName(String nodeID, String diseaseID) {
    	String sql = "select Sector_name from NCCN_Disease_relationship where Node = '"+nodeID+"' and Disease_ID ='"+diseaseID+"'";
    	Cursor cursor = getCursor(sql, null);
    	String sectorName = "";
    	while(cursor.moveToNext()) {
    		sectorName = cursor.getString(0);
    	}
    	cursor.close();
    	closeDB();
    	return sectorName;
    }
    
	public String getFirstNode(String id) {
    	String sql = "select Node from NCCN_Disease_relationship where Disease_ID = '"+id+"' and [Order]='1'";
    	Cursor cursor = getCursor(sql, null);
    	String fistNode = "";
    	while(cursor.moveToNext()) {
    		fistNode = cursor.getString(0);
    	}
    	
    	System.out.println("fistNode   "+fistNode);
    	cursor.close();
    	closeDB();
    	return fistNode;
    }
	
	public void getDiseaseList(ArrayList<String> diseaseIDLst, ArrayList<Map<String,Object>> data) {
		String sql = "select * from NCCN_Disease_name";
    	try {
    		Cursor cursor = getCursor(sql, null);
    		while(cursor.moveToNext()) {
        		Map<String,Object> item=new HashMap<String,Object>();
        			diseaseIDLst.add(cursor.getString(2));
            		item.put("name", cursor.getString(1));
            		Configuration config = context.getResources().getConfiguration();
            		if(config.locale.toString().contains("zh")) {
            			item.put("version", cursor.getString(4));
            		} else {
            			item.put("version", cursor.getString(3));
            		}
            		
            		data.add(item);
        	}
    		
        	cursor.close();
        	closeDB();
    	} catch (SQLiteException e) {
    	}
	}
	
	class NCCNHelper extends SQLiteOpenHelper {
		public NCCNHelper(Context context, String name,
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
