package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;

import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.SpecialCollectionListEntity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


 /**
  *
  * 一个业务类
  */
 public class SpecialListDao extends BaseSqlAdapter{
	 private final static String TABLE_PROFILE = "mysqllite.db";
	 private static String dbName;
	 private Context context;
	 private String userName;

	 public SpecialListDao(Context ctx) {
		 this.context = ctx;
		 if (dbName == null)
			 dbName = ctx.getFilesDir() + File.separator + TABLE_PROFILE;
		 try {
			 mDbHelper = getConnection();
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 userName = SharedPreferencesMgr.getUserName().trim();
	 }
	 
	 private synchronized SQLiteOpenHelper getConnection()
			 throws Exception {
		 return new MySqlLiteHelper(context, Constant.MY_SQLITE_VESION);
	 }
		
     /**
      * 查看数据库中是否有某用户数据
      */
     public boolean isHasInfors() {
    	 SQLiteDatabase database = mDbHelper.getReadableDatabase();
    	 String sql = "select count(*)  from special_list where username=?";
    	 
    	 Cursor cursor;
    	 int count = 0;
    	 try{
    		 cursor= database.rawQuery(sql, new String[] { userName });
    		 cursor.moveToFirst();
    		 count = cursor.getInt(0);
    		 if(cursor!=null)
        		 cursor.close();
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    	 database.close();
    	 return count != 0;
     }
     
     /**
      * 保存 下载的具体信息
      */
     public synchronized void saveInfos(ArrayList<SpecialCollectionListEntity> infos) {
    	 SQLiteDatabase database = mDbHelper.getWritableDatabase();
    	 database.beginTransaction();
    	 try{
    		 for (SpecialCollectionListEntity info : infos) {
    			 String sql = "insert into special_list(key,companyName,department,name,iconUrl,downloadLink,activeStamp, invalidStamp, username) values (?,?,?,?,?,?,?,?,?)";
    			 Object[] bindArgs = { info.key, info.companyName,
    					 info.department, info.name, info.iconUrl, info.downloadLink, info.activeStamp, info.invalidStamp, userName};
    			 database.execSQL(sql, bindArgs);
    		 }
    		 database.setTransactionSuccessful();
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }finally{
    		 database.endTransaction();
    	 }
    	 database.close();
     }

     /**
      * 得到下载具体信息
      */
     public ArrayList<SpecialCollectionListEntity> getInfos() {
         ArrayList<SpecialCollectionListEntity> list = new ArrayList<SpecialCollectionListEntity>();
         SQLiteDatabase database = mDbHelper.getReadableDatabase();
         String sql = "select * from special_list where username=?";
         try{
        	 Cursor cursor = database.rawQuery(sql, new String[] { userName });
        	 while (cursor.moveToNext()) {
        		 SpecialCollectionListEntity info = new SpecialCollectionListEntity(cursor.getString(1),
        				cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),
        						 cursor.getString(6),cursor.getString(7),cursor.getString(8));
        		 list.add(info);
        	 }
        	 if(cursor!=null)
        		 cursor.close();
         }catch(Exception e){
        	 e.printStackTrace();
         }
         database.close();
         return list;
     }
     
     /**
      * 关闭数据库
      */
     public void closeDb() {
         mDbHelper.close();
    }

     /**
     * 下载完成后删除数据库中的数据
      */
     public synchronized void delete(String id) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        
        database.beginTransaction();
        try{
        	String sql = "delete from special_list where special_id=? and username=?";
//        	database.delete("download_info", "special_id=? and username=?", new String[] { id,userName });
        	database.execSQL(sql, new String[]{id,userName});
        	database.setTransactionSuccessful();
        }catch (Exception e) {
			e.printStackTrace();
		}finally{
			database.endTransaction();
		}
        database.close();
     }
     
     /**
      * 下载完成后删除数据库中的数据
       */
      public synchronized void delete() {
         SQLiteDatabase database = mDbHelper.getWritableDatabase();
         
         database.beginTransaction();
         try{
         	String sql = "delete from special_list where username=?";
//         	database.delete("download_info", "special_id=? and username=?", new String[] { id,userName });
         	database.execSQL(sql, new String[]{userName});
         	database.setTransactionSuccessful();
         }catch (Exception e) {
 			e.printStackTrace();
 		}finally{
 			database.endTransaction();
 		}
         database.close();
      }
 }
