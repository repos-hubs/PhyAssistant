package com.jibo.dbhelper;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jibo.data.entity.ProfileEntity;

public class ProfileAdapter extends BaseSqlAdapter{
	private final static String TABLE_PROFILE = "mysqllite.db";
	private static String dbName;
	private Context context;
	public ProfileAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = ctx.getFilesDir() + File.separator + TABLE_PROFILE;
		mDbHelper = new MySqlLiteHelper(ctx, vesion);
	}
	
	public ProfileEntity getProfile(String doctorID) {
		ProfileEntity entity = null;
		String str = "select * from PROFILE where _ID ='"+doctorID+"'";
		try {
			Cursor cursor = getCursor(str, null);
			cursor.moveToNext();
			if(cursor.getCount() > 0) {
				entity = new ProfileEntity();
				entity.setId(doctorID);
				entity.setName(cursor.getString(1));
				entity.setRankInChina(cursor.getString(2));
				entity.setRankInHospital(cursor.getString(3));
				entity.setPaperCount(cursor.getString(4));
				entity.setCoauthorCount(cursor.getString(5));
				entity.setCahsp(cursor.getString(6));
				entity.setCaohsp(cursor.getString(7));
				entity.setKeywords(cursor.getString(8));
				entity.setCagrp(cursor.getString(9));
				entity.setBigSpecialty(cursor.getString(10));
				entity.setSpecialty(cursor.getString(11));
				entity.setHospitalName(cursor.getString(12));
			}
			
			cursor.close();
			closeDB();
		} catch(Exception e) {
			
		}
		
		return entity;
	}
	
	public void setProfile(ProfileEntity entity) {
		String str = "select * from PROFILE where _ID ='"+entity.getId()+"'";
		Cursor cursor = getCursor(str, null);
		ContentValues cv = new ContentValues();
		cv.put("_ID", entity.getId());
		cv.put("name", entity.getName());
		cv.put("rankInChina", entity.getRankInChina());
		cv.put("rankInHospital", entity.getRankInHospital());
		cv.put("paperCount", entity.getPaperCount());
		cv.put("coauthorCount", entity.getCoauthorCount());
		cv.put("cahsp", entity.getCahsp());
		cv.put("caohsp", entity.getCaohsp());
		cv.put("keywords", entity.getKeywords());
		cv.put("cagrp", entity.getCagrp());
		cv.put("bigSpecialty", entity.getBigSpecialty());
		cv.put("specialty", entity.getSpecialty());
		cv.put("hospitalName", entity.getHospitalName());
		if(cursor.getCount() == 0) {
			insertSql("PROFILE", cv);
		} else {
			try {
				getDb().update("PROFILE", cv, null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		closeDB();
	}
}
