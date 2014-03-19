package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.DownloadInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


 /**
  *
  * 一个业务类
  */
 public class DownloadResumeDao extends BaseSqlAdapter{
	 private final static String TABLE_PROFILE = "mysqllite.db";
	 private static String dbName;
	 private Context context;
	 private String userName;

	 public DownloadResumeDao(Context ctx) {
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
      * 查看数据库中是否有数据
      */
     public boolean isHasInfors(String urlstr) {
    	 SQLiteDatabase database = mDbHelper.getReadableDatabase();
    	 String sql = "select count(*)  from download_info where url=? and username=?";
    	 
    	 Cursor cursor;
    	 int count = 0;
    	 try{
    		 cursor= database.rawQuery(sql, new String[] { urlstr, userName });
    		 cursor.moveToFirst();
    		 count = cursor.getInt(0);
    		 if(cursor!=null)
        		 cursor.close();
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    	 database.close();
    	 return count == 0;
     }
     
     /**
      * 查看数据库中是否有某用户数据
      */
     public boolean isHasInfors() {
    	 SQLiteDatabase database = mDbHelper.getReadableDatabase();
    	 String sql = "select count(*)  from download_info where username=?";
    	 
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
      * 查看数据库中下载状态
      */
     public boolean getDownloadState(String special_id) {
    	 boolean isDownload = false;
    	 SQLiteDatabase database = mDbHelper.getReadableDatabase();
    	 String sql = "select download_state from download_info where special_id=? and username=?";
    	 Cursor cursor = database.rawQuery(sql, new String[] { special_id, userName });
    	 if(cursor != null && cursor.getCount()>0){
	    	 cursor.moveToFirst();
	    	 isDownload = Boolean.parseBoolean(cursor.getString(0));
    	 }
    	 cursor.close();
    	 database.close();
    	 return isDownload;
     }
     
     /**
      * 更新数据库中的下载信息
       */
      public synchronized void updateDownloadState(String special_id) {
    	  SQLiteDatabase database = mDbHelper.getWritableDatabase();
    	  String sql = "update download_info set download_state='true' where special_id=? and username=?";
    	  String[] bindArgs = new String[] { special_id, userName };
    	  
    	  database.beginTransaction();
    	  try{
    		  database.execSQL(sql, bindArgs);
    		  database.setTransactionSuccessful();
    	  }catch (Exception e) {
    		  e.printStackTrace();
    	  }finally{
    		  database.endTransaction();
    	  }
    	  database.close();
      }

     /**
      * 保存 下载的具体信息
      */
     public synchronized void saveInfos(List<DownloadInfo> infos) {
    	 SQLiteDatabase database = mDbHelper.getWritableDatabase();
    	 database.beginTransaction();
    	 try{
    		 for (DownloadInfo info : infos) {
    			 String sql = "insert into download_info(thread_id,start_pos,end_pos,compelete_size,url,special_id,file_size, username, title) values (?,?,?,?,?,?,?,?,?)";
    			 Object[] bindArgs = { info.getThreadId(), info.getStartPos(),
    					 info.getEndPos(), info.getCompeleteSize(), info.getUrl(), info.getSpecialID(), info.getFileSize(), userName, info.getTitle()};
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
     public List<DownloadInfo> getInfos(String urlstr) {
         List<DownloadInfo> list = new ArrayList<DownloadInfo>();
         SQLiteDatabase database = mDbHelper.getReadableDatabase();
         String sql = "select thread_id, start_pos, end_pos,compelete_size,url,file_size,special_id,title from download_info where url=? and username=?";
         try{
        	 Cursor cursor = database.rawQuery(sql, new String[] { urlstr, userName });
        	 while (cursor.moveToNext()) {
        		 DownloadInfo info = new DownloadInfo(cursor.getInt(0),
        				 cursor.getLong(1), cursor.getLong(2), cursor.getLong(3),
        				 cursor.getString(4), cursor.getLong(5), cursor.getString(6), cursor.getString(7));
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
      * 得到下载具体信息
      */
     public ArrayList<DownloadInfo> getInfos() {
         ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
         SQLiteDatabase database = mDbHelper.getReadableDatabase();
         String sql = "select thread_id, start_pos, end_pos,compelete_size,url,file_size,special_id,title from download_info where username=?";
         try{
        	 Cursor cursor = database.rawQuery(sql, new String[] { userName });
        	 while (cursor.moveToNext()) {
        		 DownloadInfo info = new DownloadInfo(cursor.getInt(0),
        				 cursor.getLong(1), cursor.getLong(2), cursor.getLong(3),
        				 cursor.getString(4), cursor.getLong(5), cursor.getString(6), cursor.getString(7));
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
      * 得到下载具体信息
      */
     public DownloadInfo getInfo(String specialID) {
         DownloadInfo info = null;
         SQLiteDatabase database = mDbHelper.getReadableDatabase();
         String sql = "select file_size,compelete_size,download_state from download_info where special_id=? and username=?";
         try{
        	 Cursor cursor = database.rawQuery(sql, new String[] { specialID, userName });
        	 while (cursor.moveToNext()) {
        		  info = new DownloadInfo(cursor.getLong(0),cursor.getLong(1),
        				 Boolean.parseBoolean(cursor.getString(2)));
        	 }
        	 if(cursor!=null)
        		 cursor.close();
         }catch(Exception e){
        	 e.printStackTrace();
         }
         database.close();
         return info;
     }
     
     /**
     * 更新数据库中的下载信息
      */
     public synchronized void updataInfos(int threadId, long compeleteSize, String urlstr) {
    	 SQLiteDatabase database = mDbHelper.getWritableDatabase();
    	 String sql = "update download_info set compelete_size=? where thread_id=? and url=? and username=?";
    	 Object[] bindArgs = { compeleteSize, threadId, urlstr, userName };
    	 
    	 database.beginTransaction();
    	 try{
    		 database.execSQL(sql, bindArgs);
    		 database.setTransactionSuccessful();
    	 }catch (Exception e) {
    		 e.printStackTrace();
    	 }finally{
    		 database.endTransaction();
    	 }
    	 database.close();
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
        	String sql = "delete from download_info where special_id=? and username=?";
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
 }
