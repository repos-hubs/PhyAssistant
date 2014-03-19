package com.jibo.dbhelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.jibo.common.Constant;
import com.jibo.data.entity.MarketPackageEntity;

public class MarketAdapter extends BaseSqlAdapter {
	private String tbName = "market_info.db";
	private static String dbName;
	private Context context;
	public static final int BTN_ACTION_INSTALL = 0;
	public static final int BTN_ACTION_UPDATE = 1;
	public static final int BTN_ACTION_NOUPDATE = 2;
	public MarketAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = Constant.DATA_PATH_MARKET_INSTALL + File.separator + tbName;
		createMarketDB();
		mDbHelper = new MarketHelper(ctx, dbName, null, vesion);
		initMarketDB();
	}

	public void createMarketDB() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		File f = new File(dbName);// 创建文件
		File fDir = new File(Constant.DATA_PATH_MARKET_INSTALL);
		try {
			if(!fDir.exists()){
				fDir.mkdir();
			}
			if (!f.exists()) {
				f.createNewFile();// 创建文件
			}
			SQLiteDatabase sdbVersion = SQLiteDatabase.openOrCreateDatabase(f,
					null);
			String createSql = "CREATE TABLE IF NOT EXISTS market_info (product_id varchar PRIMARY KEY, title varchar, local_version varchar, server_version varchar, uninstall varchar, dbName varchar);";
			sdbVersion.execSQL(createSql);
			sdbVersion.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getMarketDataInfo() {
		String info = "";
		String sql = "select product_id,local_version from market_info";
		Cursor cursor = getCursor(sql, null);
		while (cursor.moveToNext()) {
			String lVersion = cursor.getString(1);
			if(lVersion==null||"null".equalsIgnoreCase(lVersion)||"".equalsIgnoreCase(lVersion)) {
				lVersion = "";
			} else {
				info = info + cursor.getString(0) + "-" + cursor.getString(1) + "|";
			}
		}
		if (!"".equals(info))
			info = info.substring(0, info.length() - 1);
		cursor.close();
		closeDB();
		System.out.println("info    "+info);
		return info;
	}

	public int getMarketAction(MarketPackageEntity en) {
		String productId = en.getCategory() + "-" + en.getProductID();
		String sql = "select local_version,server_version from market_info where product_id ='"
				+ productId + "'";
		Cursor cursor = getCursor(sql, null);
		int result = -1;
		if (cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				String lVersion = cursor.getString(0);
				String sVersion = cursor.getString(1);
				if(lVersion==null) {
					result = BTN_ACTION_UPDATE;
				} else {
					if(lVersion.equals(sVersion)) {
						result = BTN_ACTION_NOUPDATE;
					} else {
						result = BTN_ACTION_UPDATE;
					}
				}
				
			}
		} else {
			result = BTN_ACTION_INSTALL;
		}
		
		cursor.close();
		closeDB();
		return result;
	}
	
	public ArrayList<MarketPackageEntity> getMineList() {
		String sql = "select product_id,title,local_version,server_version,uninstall from market_info";
		Cursor cursor = getCursor(sql, null);
		ArrayList<MarketPackageEntity> lst = new ArrayList<MarketPackageEntity>();
		while(cursor.moveToNext()) {
			String productId = cursor.getString(0);
			String title = cursor.getString(1);
			String lVersion = cursor.getString(2);
			String sVersion = cursor.getString(3);
			String uninstall = cursor.getString(4);
			if(sVersion==null) sVersion = "";
			if(sVersion.equals(lVersion)) {
				MarketPackageEntity en = new MarketPackageEntity();
				String id = productId.split("-")[1];
				String cate = productId.split("-")[0];
				en.setCategory(cate);
				en.setProductID(id);
				en.setTitle(title);
				lst.add(en);
			}
		}
		return lst;
	}
	
	public void setLocalVersion(MarketPackageEntity en, String lversion) {
		String productId = en.getCategory() + "-" + en.getProductID();
		String sql = "select * from market_info where product_id ='"
				+ productId + "'";
		Cursor cursor = getCursor(sql, null);
		if (cursor.getCount() > 0) {
			String updateSql = "update market_info set local_version='"
					+ lversion + "' where product_id='"+productId+"'";
			excuteSql(updateSql);
		} else {
			ContentValues cv = new ContentValues();
			cv.put("product_id", productId);
			cv.put("local_version", lversion);
			insertSql("market_info", cv);
		}
		cursor.close();
		closeDB();
	}
	
	public void setServerVersion(MarketPackageEntity en) {
		String productId = en.getCategory() + "-" + en.getProductID();
		String sql = "select * from market_info where product_id ='"
				+ productId + "'";
		Cursor cursor = getCursor(sql, null);
		if (cursor.getCount() > 0) {
			String updateSql = "update market_info set server_version='"
					+ en.getVersion() + "' where product_id='"+productId+"'";
			excuteSql(updateSql);
		} else {
			ContentValues cv = new ContentValues();
			cv.put("product_id", productId);
			cv.put("title", en.getTitle());
			cv.put("server_version", en.getVersion());
			insertSql("market_info", cv);
		}
		cursor.close();
		closeDB();
	}
	
	public synchronized String getUninstallStr(MarketPackageEntity en) {
		String category = en.getCategory();
		String id = en.getProductID();
		String productId = category+"-"+id;
		String sql = "select uninstall from market_info where product_id='"+productId+"'";
		Cursor cursor = getCursor(sql,null);
		String uninstall = null;
		while(cursor.moveToNext()) {
			uninstall = cursor.getString(0);
		}
		cursor.close();
		closeDB();
		return uninstall;
	}
	
	public String getDBName(MarketPackageEntity en) {
		String category = en.getCategory();
		String id = en.getProductID();
		String productId = category+"-"+id;
		String sql = "select dbName from market_info where product_id='"+productId+"'";
		Cursor cursor = getCursor(sql,null);
		String uninstall = null;
		while(cursor.moveToNext()) {
			uninstall = cursor.getString(0);
		}
		cursor.close();
		closeDB();
		return uninstall;
	}
	
	public void updateDataTitle(MarketPackageEntity en) {
		String productId = en.getCategory() + "-" + en.getProductID();
		String sql = "select * from market_info where product_id ='"
				+ productId + "'";
		Cursor cursor = getCursor(sql, null);
		if (cursor.getCount() > 0) {
			
		} else {
			
		}
	}
	
	public void deleteProduct(String product_id) {
		String sql = "delete from market_info where product_id='"+product_id+"'";
		try {
			excuteSql(sql);
		} catch(Exception e) {
			
		}
		
		closeDB();
	}
	
	private void initMarketDB() {
		File fileTumor = new File(Constant.DATA_PATH_MARKET_INSTALL+"/tumor.db");
		File fileECG = new File(Constant.DATA_PATH_MARKET_INSTALL+"/ecg.db");
		File fileNCCN = new File(Constant.DATA_PATH_MARKET_INSTALL+"/nccn_disease.db");
		if(fileTumor.exists()) {
			initMarketDB("3-1","TNM肿瘤分期工具","", "", "1", "1");
		} 
		if(fileECG.exists()) {
			initMarketDB("3-2","典型异常心电图","", "", "1", "1");
		} 
		if(fileNCCN.exists()) {
			initMarketDB("3-3","肿瘤临床实践指南","", "", "1", "1");
		}  
		
		if(calculatorIsExists(3)) {
			initMarketDB("2-3","平均动脉压","jibo.db", "delete from formula2 where id=3", "1", "1");
		} 
		if(calculatorIsExists(30)) {
			initMarketDB("2-4","新生儿评分","jibo.db", "delete from formula2 where id=30", "1", "1");
		} 
		if(calculatorIsExists(4)) {
			initMarketDB("2-5","心脏指数","jibo.db", "delete from formula2 where id=4", "1", "1");
		} 
		if(calculatorIsExists(24)) {
			initMarketDB("2-6","肺容积计算器","jibo.db", "delete from formula2 where id=24", "1", "1");
		} 
		if(calculatorIsExists(27)) {
			initMarketDB("2-7","气胸程度","jibo.db", "delete from formula2 where id=27", "1", "1");
		} 
	}
	
	public boolean calculatorIsExists(int id) {
		boolean result = false;
		String sql = "select * from formula2 where id="+id;
		try{
			SQLiteDatabase sdb = SQLiteDatabase.openOrCreateDatabase(Constant.DATA_PATH_GBADATA+"/jibo.db", null);
			Cursor cursor = sdb.rawQuery(sql, null);
			if(cursor.getCount()>0) {
				result = true;
			}
			cursor.close();
			sdb.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void initMarketDB(String productId,String title,String dbName,String uninstall, String server_version, String local_version) {
		String sql = "select * from market_info where product_id='"+productId+"'";
		Cursor cursor = getCursor(sql, null);
		if(cursor.getCount()>0) {
			
		} else {
			ContentValues cv = new ContentValues();
			cv.put("product_id", productId);
			cv.put("uninstall", uninstall);
			cv.put("dbName", dbName);
			cv.put("server_version", server_version);
			cv.put("local_version", local_version);
			cv.put("title", title);
			insertSql("market_info", cv);
		}
		cursor.close();
		closeDB();
	}
	
	public void updateUnInstallSQL(MarketPackageEntity en, String dbName,String uninstall) {
		if(dbName == null || uninstall == null) return;
		String productId = en.getCategory() + "-" + en.getProductID();
		String sql = "select * from market_info where product_id ='"
				+ productId + "'";
		Cursor cursor = getCursor(sql, null);
			if (cursor.getCount() > 0) {
				String updateSql = "update market_info set uninstall='"+uninstall+"',dbName='"+dbName+"'where product_id='"+productId+"'";
				excuteSql(updateSql);
			} else {
				ContentValues cv = new ContentValues();
				cv.put("product_id", productId);
				cv.put("uninstall", uninstall);
				cv.put("dbName", dbName);
				insertSql("market_info", cv);
			}
		cursor.close();
		closeDB();
	}
 
	class MarketHelper extends SQLiteOpenHelper {
		public MarketHelper(Context context, String name,
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
