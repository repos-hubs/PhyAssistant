package com.jibo.dbhelper;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.jibo.common.Constant;
import com.jibo.data.entity.PaperDownloadEntity;

/**
 * 论文下载
 * @description 
 * @author will
 * @create 2013-3-7 下午4:31:23
 */
public class PaperDownloadAdapter extends BaseSqlAdapter {

	public PaperDownloadAdapter(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}

	public boolean insertInfo(Context context, PaperDownloadEntity entity) {
		String insertSql = "insert into PaperDownload (paperID,url,imageurl,fileName,state,title,remarks,others,username,periodicalTitle,IF,date) values('"
				+ entity.getPaperID()
				+ "','"	+ entity.getUrl()
				+ "','"	+ entity.getImageUrl() 
				+ "','"	+ entity.getFileName()
				+ "','"	+ entity.getState()
				+ "','"	+ entity.getTitle()
				+ "','"	+ entity.getRemarks()
				+ "','"	+ entity.getOthers()
				+ "','" + entity.getUsername() 
				+ "','" + entity.getPeriodicalTitle() 
				+ "','" + entity.getIFCount() 
				+ "','" + entity.getDate() 
				+ "')";
		return excuteSql(insertSql);
	}

	public boolean deletePaperDownloadInfo(Context context, String paperID, String username) {
		String deleteSql = "delete from PaperDownload where paperID = '"
				+ paperID + "' and username='" + username +"'";
		return excuteSql(deleteSql);
	}
	
	public boolean updateState(Context context, String paperID, String state, String username){
		String updateSql ="update PaperDownload set state='" + state + "' where paperID='"+ paperID 
				+"' and username = '" + username + "'";
		return excuteSql(updateSql);
	}
	
	public boolean updateFilename(Context context, String paperID, String url, String filename, String username){
		String updateSql ="update PaperDownload set fileName='" + filename + "',url='" + url + "' where paperID='"+ paperID 
				+"' and username = '" + username + "'";
		return excuteSql(updateSql);
	}
	
	public PaperDownloadEntity selectPaperDownloadInfo(Context context, String userName, String paperID){
		PaperDownloadEntity entity = null;
		String sql = "SELECT * FROM PaperDownload where paperID = '" + paperID + "'";

		if (null != userName && !"".equals(userName)) {
			sql += " and username = '" + userName.trim() + "'";
		}
		try{
			Cursor c = getCursor(sql, null);
			if (c != null && c.moveToFirst()) {
				c.moveToFirst();
				entity = new PaperDownloadEntity();
				entity.setPaperID(c.getString(c.getColumnIndex("paperID")));
				entity.setUrl(c.getString(c.getColumnIndex("url")));
				entity.setImageUrl(c.getString(c.getColumnIndex("imageurl")));
				entity.setFileName(c.getString(c.getColumnIndex("fileName")));
				entity.setState(c.getString(c.getColumnIndex("state")));
				entity.setTitle(c.getString(c.getColumnIndex("title")));
				entity.setRemarks(c.getString(c.getColumnIndex("remarks")));
				entity.setOthers(c.getString(c.getColumnIndex("others")));
				entity.setUsername(c.getString(c.getColumnIndex("username")));
				c.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeDB();
		}
		return entity;
	}

	public ArrayList<PaperDownloadEntity> getAllPaperDownloadInfo(Context context,
			String userName) {
		ArrayList<PaperDownloadEntity> list = null;

		try {
			list = new ArrayList<PaperDownloadEntity>();
			String sql = "SELECT * FROM PaperDownload ";

			if (null != userName && !"".equals(userName)) {
				sql += " where username = '" + userName.trim() + "'";
			}
			Cursor c = getCursor(sql, null);
			if (c != null && c.moveToFirst()) {
				do{
					PaperDownloadEntity entity = new PaperDownloadEntity();
					entity.setPaperID(c.getString(c.getColumnIndex("paperID")));
					entity.setUrl(c.getString(c.getColumnIndex("url")));
					entity.setImageUrl(c.getString(c.getColumnIndex("imageurl")));
					entity.setFileName(c.getString(c.getColumnIndex("fileName")));
					entity.setState(c.getString(c.getColumnIndex("state")));
					entity.setTitle(c.getString(c.getColumnIndex("title")));
					entity.setRemarks(c.getString(c.getColumnIndex("remarks")));
					entity.setOthers(c.getString(c.getColumnIndex("others")));
					entity.setUsername(c.getString(c.getColumnIndex("username")));
					entity.setPeriodicalTitle(c.getString(c.getColumnIndex("periodicalTitle")));
					entity.setIFCount(c.getString(c.getColumnIndex("IF")));
					entity.setDate(c.getString(c.getColumnIndex("date")));
					list.add(entity);
				}while(c.moveToNext());
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return list;
	}

}
