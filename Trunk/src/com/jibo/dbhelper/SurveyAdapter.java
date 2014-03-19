package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.jibo.common.Constant;
import com.jibo.data.entity.SurveyEntity;

public class SurveyAdapter extends BaseSqlAdapter {
	private String tbName = "jibo.db";
	private Context context;
	private String dbName;
	private final String STATUS_NEW = "1";
	public SurveyAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = Constant.DATA_PATH_GBADATA + File.separator + tbName;
		mDbHelper = new SurveyHelper(ctx, dbName, null, vesion);
	}
	
	public void deleteSurvey(String id, String userName) {
		String sql2 = "delete from survey_conf where id="+id+" and username='"+userName+"'";
		excuteSql(sql2);
		closeDB();
	}
	
	public String getStatus(String id, String Name) {
		String sql = "select status from survey_conf where username='"+Name+"' and id="+id;
		Cursor cursor=getCursor(sql, null);
		String status = "";
		while(cursor.moveToNext()) {
			if("1".equals(cursor.getString(0))) {
				status = "1";
			}
		}
		cursor.close();
		closeDB();
		return status;
	}
	
	public void setStatus(String id, String Name, String value) {
		String sql ="update survey_conf set status='"+value+"' where username='"+Name+"' and id="+id;
		System.out.println("sql    8&&^   "+sql);
		excuteSql(sql);
		closeDB();
	}
	
	public String getMaxSurveyId(String userName) {
		String sql1 = "select max(id) from survey_conf where username='"+userName+"'";
		String sql2 = "select * from survey_conf where username='"+userName+"'";
		Cursor cursor1 = getCursor(sql1, null);
		Cursor cursor2 = getCursor(sql2, null);
		int maxID = -1;
		String result = "";
		System.out.println("cursor   "+cursor2.getCount());
		if(cursor2.getCount()>0) {
			while(cursor1.moveToNext()) {
				maxID = cursor1.getInt(0);
				result = ""+maxID;
			}
		}
		
		if(maxID==-1) result="";
		cursor1.close();
		cursor2.close();
		closeDB();
		return result;
	}
	
	public ArrayList<SurveyEntity> getSurveyList(String username) {
		String sql = "select * from survey_conf where username='"+username+"'";
		Cursor cursor = getCursor(sql, null);
		ArrayList<SurveyEntity> list = new ArrayList<SurveyEntity>();
		while(cursor.moveToNext()) {
			SurveyEntity en = new SurveyEntity();
			en.setID(cursor.getString(0));
			en.setTitle(cursor.getString(1));
			en.setContent(cursor.getString(2));
			en.setKeyWords(cursor.getString(3));
			en.setEstimateTime(cursor.getString(4));
			en.setPay(cursor.getString(5));
			en.setPayUnit(cursor.getString(6));
			en.setRegion(cursor.getString(7));
			en.setCity(cursor.getString(8));
			en.setHospitalLevel(cursor.getString(9));
			en.setSurveySource(cursor.getString(10));
			en.setIsVerify(cursor.getString(11));
			en.setpCount(cursor.getString(12));
			en.setLimitPersonCount(cursor.getString(13));
			en.setStartTime(cursor.getString(14));
			en.setEndTime(cursor.getString(15));
			
			list.add(en);
		}
		cursor.close();
		closeDB();
		
		return list;
	}
	public void updateSurvey(ArrayList<SurveyEntity> list, String username) {
		for(SurveyEntity en:list) {
			String sql = "select * from survey_conf where id="+Long.parseLong(en.getID())+" and username='"+username+"'";
			
			Cursor cursor = getCursor(sql, null);
			System.out.println("updateSurvey   "+sql+"   cursor   *&&  "+cursor.getCount());
			if(cursor.getCount()>0) {
				
			} else {
				ContentValues cv = new ContentValues();
				cv.put("id", Long.parseLong(en.getID()));
				cv.put("title", en.getTitle());
				cv.put("content", en.getContent());
				cv.put("keyWords", en.getKeyWords());
				cv.put("estimatedTime", en.getEstimateTime());
				cv.put("pay", en.getPay());
				cv.put("payunit", en.getPayUnit());
				cv.put("region", en.getRegion());
				cv.put("city", en.getCity());
				cv.put("hospital", en.getHospitalLevel());
				cv.put("surveysource", en.getSurveySource());
				cv.put("isverify", en.getIsVerify());
				cv.put("pcount", en.getpCount());
				cv.put("limitcount", en.getLimitPersonCount());
				cv.put("begintime", en.getStartTime());
				cv.put("endtime", en.getEndTime());
				cv.put("username", username);
				insertSql("survey_conf", cv);
			}
			cursor.close();
		}

		
		closeDB();
	}
	
	class SurveyHelper extends SQLiteOpenHelper {
		public SurveyHelper(Context context, String name,
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
