package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.RadioGroup;

import com.api.android.GBApp.R;
import com.jibo.common.Constant;
import com.jibo.data.entity.TumorEntity;
import com.jibo.data.entity.TumorEntity1;

public class TNMAdapter extends BaseSqlAdapter {
	private final static String TABLE_CALCULATE = "tumor.db";
	private static String dbName;
	private Context context;
	public TNMAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = Constant.DATA_PATH_MARKET_INSTALL + File.separator + TABLE_CALCULATE;
		mDbHelper = new TNMHelper(ctx, dbName, null, vesion);
	}
	
	public ArrayList<String> getSubCategory(String rank){
		ArrayList<String> subcategoryList = new ArrayList<String>();
		String sql = " select distinct sub_category from [definition] " +
		 		" where [rank] = '" + rank +
		 		"' and sub_category != 'Note' " +
		 		" order by sub_category desc ";
		Cursor c = getCursor(sql, null);
		while(c.moveToNext()){
			subcategoryList.add(c.getString(c.getColumnIndex("sub_category")));
		}
		
		c.close();
		closeDB();
		return subcategoryList;
	}
	
	public boolean isIndexInRankLevel(String rank, String index, String indexValue){
		 index = String.valueOf(Integer.parseInt(index) - 1);
		 String sql = "select [level] from [ranklevel] where rank = '" + rank + "' and "
		 		+ "index" + index + "= '" + indexValue + "'";
		 Log.i("GAB", "isIndexInRankLevel Sql is " + sql);
		 Cursor c = getCursor(sql, null);
		 boolean result;
		 if(c != null){
			 if(c.getCount() != 0)
			 result = true;
			 else
				 result = false;
		 }else{
			 result = false;
		 }
		 c.close();
		 closeDB();
		 return result;
	 }
	
	public String getLevelByRank(String sql){
		Cursor c = getCursor(sql, null);
		String level = "";
		if(c!= null){
			while(c.moveToNext()){
				if(level.length() > 0)
					level = level + context.getString(R.string.or) + c.getString(c.getColumnIndex("level"));
				else
					level = c.getString(c.getColumnIndex("level"));
			}
			if(!c.isClosed()) c.close();
		}
		c.close();
		closeDB();
		return level;
	}
	
	public boolean getIndexInRankLevel(String rank, String index) {
		index = String.valueOf(Integer.parseInt(index) + 1);
		String sql = "select * from [ranklevel] where " + "index" + index
				+ " LIKE '%anyone%'";
		Cursor c = getCursor(sql, null);
		boolean result = false;
		if (c.getCount() != 0)
			result = true;
		else
			result =false;
		c.close();
		closeDB();
		return result;
	}

	public ArrayList<TumorEntity> getSignAndSignificanceByRankAndSubCategory( 
	 		String rank, List<String> subList){
		
		ArrayList<TumorEntity> tumorList = new ArrayList<TumorEntity>();
		ArrayList<String> indexList = new ArrayList<String>();
		ArrayList<String> signList = new ArrayList<String>();
		Iterator<String> iterator = subList.iterator();
		while(iterator.hasNext()){
			ArrayList<String> significanceList = new ArrayList<String>();
			String subCategory = iterator.next();
			String sql = "select * from [definition] where [rank] = '" + rank 
					+ "' and sub_category = '" + subCategory + "'";
			Cursor c = getCursor(sql, null);
			
			while(c.moveToNext()){
				indexList.add(c.getString(c.getColumnIndex("index")));
				signList.add(c.getString(c.getColumnIndex("sub_category_sign")));
				significanceList.add(c.getString(c.getColumnIndex("sub_category_sign")) + ": " + c.getString(c.getColumnIndex("significance")));
				
			}
			TumorEntity entity = new TumorEntity();
			entity.setIndexList(indexList);
			entity.setSignList(signList);
			entity.setSignificanceList(significanceList);
			tumorList.add(entity);
			c.close();
		}
		closeDB();
		return tumorList;
	}
	
	private ArrayList<String> getNoteByRank(SQLiteDatabase sdb, String rank){
		String note = "";
		ArrayList<String> noteList = new ArrayList<String>();
		
		String sql = "select * from [definition] where [rank] = '" + rank +
		 		"' and sub_category = 'Note'";
		Cursor c = getCursor(sql, null);
		while(c.moveToNext()){
			note = c.getString(c.getColumnIndex("significance"));
			noteList.add(note);
		}
		
		c.close();
		closeDB();
		return noteList;
	}
	
	public String getTumorTitle(String rId) {
		String sql = "select area_cn,area_en from tumorcategory where rank="+"\""+rId+"\"";
		Cursor cur = getCursor(sql, null);
		String cNtitle = "";
		try {
			while (cur.moveToNext()) {
				int indexdate = cur.getColumnIndex("area_cn");
				cNtitle = cur.getString(indexdate);
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return cNtitle;
	}
	
	public ArrayList<String> getRankListCategory(String category){
		String sql = "select [rank], area_cn, area_en from [tumorcategory] where category = '" + category + "'";
		Cursor c 	 = getCursor(sql, null);
		ArrayList<String> rankList = new ArrayList<String>();
		
		while(c.moveToNext()){
			rankList.add(c.getString(c.getColumnIndex("rank")));
		}
		c.close();
		closeDB();
		return rankList;
	}
	
	public ArrayList<String> getNameListCategory(String category){
		String sql = "select [rank], area_cn, area_en from [tumorcategory] where category = '" + category + "'";
		Cursor c 	 = getCursor(sql, null);
		ArrayList<String> nameList = new ArrayList<String>();
		
		while(c.moveToNext()){
			nameList.add(c.getString(c.getColumnIndex("area_cn")));
		}
		c.close();
		closeDB();
		return nameList;
	}
	
	public String getCategory(){
		String strTitle = null;
		try {
			String sql = "select distinct category from [tumorcategory]";
			Cursor c = getCursor(sql, null);
			if(c != null){
				while(c.moveToNext()){
					strTitle  = c.getString(c.getColumnIndex("category"));
				}
				c.close();
			}
		} catch(SQLiteException e) {
		}
		closeDB();
		return strTitle;
	}
	
	public String getTumor(Context context, String title) {
		Configuration config = context.getResources().getConfiguration();
		String sql2 = "";
		if(config.locale.toString().contains("zh")) {
			sql2 = "select rank from tumorcategory where area_cn = '"+title+"'";
		} else {
			sql2 = "select rank from tumorcategory where area_en = '"+title+"'";
		}
		
		Cursor cursor2 = getCursor(sql2, null);
		
		String tumor = null;
		while (cursor2.moveToNext()) {
			tumor = cursor2.getString(0);
		}
		
		cursor2.close();
		closeDB();
		return tumor;
	}
	//******************************************************
	public ArrayList<TumorEntity1> getTumorInfo(String rank) {
		String sql = "select * from definition where rank ='"+rank+"' order by [index]";
		Cursor cursor = getCursor(sql, null);
		ArrayList<TumorEntity1> tumorList = new ArrayList<TumorEntity1>();
		while(cursor.moveToNext()) {
			TumorEntity1 entity = new TumorEntity1();
			entity.setSub_category(cursor.getString(2));
			entity.setSub_category_sign(cursor.getString(3));
			entity.setSignificance(cursor.getString(4));
			entity.setIndex(cursor.getString(5));
			tumorList.add(entity);
		}
		
		cursor.close();
		closeDB();
		return tumorList;
	}
	
	public String getResult(ArrayList<RadioGroup> rgList, String rank) {
		String result = context.getString(R.string.noranklevel);
		String sql = "select level from ranklevel where ";
		for(int i=0; i<rgList.size(); i++) {
			if(null != rgList.get(i).getTag()) {
				String tag = rgList.get(i).getTag().toString();
				sql = sql+"(index"+(i+1)+"='"+tag+"' or index"+(i+1)+" like '%anyone%')";
				if(i!=rgList.size()-1) {
					sql = sql+" and ";
				}
			}
		}
		sql = sql + " and rank='"+rank+"'";
		System.out.println("sql    "+sql);
		Cursor cursor = getCursor(sql, null);
		String queryResult = "";
		while(cursor.moveToNext()) {
			queryResult = queryResult+"\n"+cursor.getString(0);
		}
		if(!"".equals(queryResult)) {
			result = queryResult;
		}
		cursor.close();
		closeDB();
		return result;
	}
	
	class TNMHelper extends SQLiteOpenHelper {
		public TNMHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}
	}
}
