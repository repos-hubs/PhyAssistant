package com.jibo.base.src.request.impl.db;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jibo.util.ComparatorRepo;

public class SqliteAdapterCentre {
	public static SqliteAdapterCentre sqliteAdapterCentre = new SqliteAdapterCentre();

	public static SqliteAdapterCentre getInstance() {
		return sqliteAdapterCentre;
	}

	private Map<String, SQLiteAdapter> adapters = new TreeMap<String, SQLiteAdapter>(
			ComparatorRepo.stringKey);

	public Map<String, SQLiteAdapter> getAdapters() {
		return adapters;
	}

	public void setAdapters(Map<String, SQLiteAdapter> adapters) {
		this.adapters = adapters;
	}
	public void renew(String db) {
		SQLiteAdapter sqlteAdapter = this.adapters.remove(db);
		if(sqlteAdapter!=null){
			sqlteAdapter.closeDB();
		}
		this.adapters.put(db, new SQLiteAdapter(db));
	}
	public void add(String db, SQLiteAdapter sqliteAdapter) {
		this.adapters.put(db, sqliteAdapter);
	}
	public SQLiteAdapter get(String db) {
		
		 SQLiteAdapter sqliteAdapter = this.adapters.get(db);
		 if(sqliteAdapter==null){
			 add(db,new SQLiteAdapter(db));
		 }
		 else {
			 if(sqliteAdapter.mDb==null||!sqliteAdapter.mDb.isOpen()){
				 renew(db);
			 }
		 }
		 return this.adapters.get(db);
	}
}
