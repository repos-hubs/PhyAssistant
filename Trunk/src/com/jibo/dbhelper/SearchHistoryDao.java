package com.jibo.dbhelper;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.aphidmobile.utils.TextureUtils;
import com.api.android.GBApp.R;
import com.jibo.common.Constant;
import com.jibo.data.entity.SearchHistoryEntity;

public class SearchHistoryDao extends BaseSqlAdapter {
	public SearchHistoryDao(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}
	/**
	 * 插入搜索信息
	 * 
	 * @param context
	 * @param config
	 * @return
	 */
	public boolean insertSearchHistory(Context context, SearchHistoryEntity searchHistory) {
		String insertSql = "insert into search_history (title,time,category,isLatest) values('"
				+ searchHistory.getTitle()
				+ "','"
				+ searchHistory.getTime()
				+ "','"
				+ searchHistory.getCategory()
				+ "','"
				+ searchHistory.getIsLatest() + "')";
		return excuteSql(insertSql);
	}
	public boolean updateSearchHistory(Context context, SearchHistoryEntity searchHistory) {
		String insertSql = "update search_history set isLatest ='"
				+ searchHistory.getIsLatest() + "' where category = '"
				+ searchHistory.getCategory() + "'";
		String str = "";
		if(!TextUtils.isEmpty(searchHistory.getTitle())){
			str = " and title = '" + searchHistory.getTitle()+"'"; 
		}
		insertSql += str;
		return excuteSql(insertSql);
	}
	/**
	 * 查询搜索信息
	 * 
	 * @param context
	 * @param config
	 * @return
	 */
	public boolean selectHistoryTitle(Context context, SearchHistoryEntity searchHistory) {
		try{
			String selectSql = "select count(1) from search_history where category = '"
					+ searchHistory.getCategory() + "' and title='"+searchHistory.getTitle()+"'";
			Cursor cursor = getCursor(selectSql, null);
			if( cursor != null){
				cursor.moveToFirst();
				String result = cursor.getString(0);
				if("0".equals(result)){
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return true;
	}
	public ArrayList<String> selectSearchHistory(Context context, String category) {
		ArrayList<String>  list = null;
		try{
			list = new ArrayList<String>();
			String selectSql = "select TITLE,TIME from search_history where category = '"
					+ category + "'";
			Cursor cursor = getCursor(selectSql, null);
			if( cursor != null){
				while (cursor.moveToNext()) {
					String result = cursor.getString(0)
							+ ((Activity) context).getResources().getString(
									R.string.str_split) + cursor.getString(1);
					list.add(0, result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return list;
	}
	public ArrayList<String> selectSearchHistory(Context context, String category,String isLatest) {
		ArrayList<String>  list = null;
		try{
			list = new ArrayList<String>();
			String selectSql = "select TITLE from search_history where category = '"
					+ category + "' and isLatest='"+isLatest+"'";
			Cursor cursor = getCursor(selectSql, null);
			if( cursor != null){
				while (cursor.moveToNext()) {
					String result = cursor.getString(0);
					list.add(result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return list;
	}
	/**
	 * 删除搜索信息
	 * 
	 * @param context
	 * @param config
	 * @return
	 */
	public boolean delSearchHistory(Context context, SearchHistoryEntity searchHistory) {
		String insertSql = "delete from search_history where ";
		return excuteSql(insertSql);
	}
}
