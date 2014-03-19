package com.jibo.base.src.request.impl.db;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jibo.dbhelper.BaseSqlAdapter;
import com.jibo.util.Logs;

public class SQLiteAdapter extends BaseSqlAdapter {
	public String dbname;

	public SQLiteAdapter(String dbname) {
		super();
		this.dbname = dbname;
		Logs.i("--- db 0 " + dbname);
		if (new File(dbname).exists()) {
			Logs.i("--- db 1 " + dbname);
			this.setmDb(SQLiteDatabase.openOrCreateDatabase(dbname, null));
			SqliteAdapterCentre.getInstance().add(dbname, this);
		}
	}

}
