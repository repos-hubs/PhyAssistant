package com.jibo.base.src.request.impl.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import android.database.Cursor;

import com.jibo.base.src.EntityObj;
import com.jibo.base.src.request.CursorParser;
import com.jibo.util.Logs;

public class AutoCache {
	public SQLiteAdapter mySqlLiteAdapter;

	public AutoCache(SQLiteAdapter mySqlLiteAdapter) {
		super();
		this.mySqlLiteAdapter = mySqlLiteAdapter;
	}

	public CacheInfo cacheInfo = new CacheInfo();

	public CacheInfo getCacheInfo() {
		return cacheInfo;
	}

	public static class CacheInfo {
		private String cacheKey;
		private String cacheTable;

		public String getCacheKey() {
			return cacheKey;
		}

		public void setCacheFromType(String cacheFromType) {
			this.cacheKey = cacheFromType;
		}

		public String getCacheTable() {
			return cacheTable;
		}

		public void setCacheTable(String cacheTable, String tag) {
			this.cacheTable = cacheTable;
			if (tag==null||tag.contains("*") || tag.contains(":")) {
				return;
			}
			try {
				HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
				hanyuPinyinOutputFormat.setCaseType(hanyuPinyinOutputFormat
						.getCaseType().LOWERCASE);
				hanyuPinyinOutputFormat.setVCharType(hanyuPinyinOutputFormat
						.getVCharType().WITH_V);
				hanyuPinyinOutputFormat.setToneType(hanyuPinyinOutputFormat
						.getToneType().WITHOUT_TONE);
				this.cacheTable += "_"
						+ PinyinHelper.toHanyuPinyinString(tag,
								hanyuPinyinOutputFormat, "");
				this.cacheTable = this.cacheTable.replaceAll(" ", "_");
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static final String CACHE_TYPE = "cache_type";
	public final String rawDetectTableExist = " select count(1) from sqlite_master where type='table' and name = ? --";
	public final String deleteTableData = " delete from ? --";
	public final String createTableQL = "CREATE TABLE IF NOT EXISTS ? ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ CACHE_TYPE + " TEXT, ? )";
	public final String insertDataQL = "insert into ? ( ? ) values( ? )";

	public void autoCreateTable(Map<String, Object> tableFields,
			String tableName) {
		Logs.i("--- tableName " + tableName);
		String createSql = createTableQL.replaceFirst("\\?", tableName);
		Object[] list_fields = tableFields.entrySet().toArray();
		String fields = "";
		String str_field = "";

		for (int i = 0; i < list_fields.length; i++) {
			Entry<String, Object> obj_field = (Entry<String, Object>) list_fields[i];
			if (obj_field.getValue() == null
					|| obj_field.getKey().equals("CONTENTS_FILE_DESCRIPTOR")
					|| obj_field.getKey().equals(
							"PARCELABLE_WRITE_RETURN_VALUE")
					|| obj_field.getKey().equals("CREATOR")) {
				continue;
			}
			str_field = obj_field.getKey();
			if (obj_field.getValue() instanceof Integer) {
				str_field += " INTEGER ";
			} else if (obj_field.getValue() instanceof String) {
				str_field += " TEXT ";
			} else if (obj_field.getValue() instanceof Date) {
				str_field += " TIMESTAMP ";
			} else {
				continue;
			}
			str_field += ",";
			fields += str_field;
		}
		fields = fields.substring(0, fields.length() - 1);
		createSql = createSql.replaceFirst("\\?", fields);
		Logs.i("--- " + createSql);
//		if (mySqlLiteAdapter.getmDb() == null) {
			mySqlLiteAdapter.excuteSql(createSql);
//		} else {
//			mySqlLiteAdapter.getmDb().execSQL(createSql);
//		}
	}

	public void insertData(Collection<EntityObj> dataCol, String tableName,
			String cache_type) {
		Object[] entities = dataCol.toArray();
		for (int i = 0; i < entities.length; i++) {
			EntityObj ent = (EntityObj) entities[i];

			Map<String, Object> tableFields = ent.fieldContents;
			Object[] list_fields = tableFields.entrySet().toArray();
			Entry<String, Object> obj_field = null;
			String str_names_field = "";
			String str_values_field = "";
			String str_name_field = "";
			String str_value_field = "";
			String insertDataSql = insertDataQL.replaceFirst("\\?", tableName);
			for (int j = 0; j < list_fields.length; j++) {
				obj_field = (Entry<String, Object>) list_fields[j];
				Class valueClass;
				if (obj_field.getValue() == null
						|| obj_field.getKey()
								.equals("CONTENTS_FILE_DESCRIPTOR")
						|| obj_field.getKey().equals(
								"PARCELABLE_WRITE_RETURN_VALUE")
						|| obj_field.getKey().equals("CREATOR")
						|| obj_field.getKey().equalsIgnoreCase("string")
						|| (!((valueClass = obj_field.getValue().getClass()) == Integer.class
								|| valueClass == String.class || valueClass == Date.class))) {
					continue;
				}
				str_names_field += obj_field.getKey() + " ,";
				str_values_field += "'"
						+ obj_field.getValue().toString().replaceAll("'", "''")
						+ "',";
			}
			str_names_field = str_names_field.substring(0,
					str_names_field.length() - 1);
			str_values_field = str_values_field.substring(0,
					str_values_field.length() - 1);
			insertDataSql = insertDataSql.replaceFirst("\\?", str_names_field);
			insertDataSql = insertDataSql.replaceFirst("\\?", str_values_field);
			Logs.i("--- " + insertDataSql);
			// if(mySqlLiteAdapter.getmDb()!=null){
			try {
				mySqlLiteAdapter.excuteSql(insertDataSql);
			} catch (Exception e) {
//				mySqlLiteAdapter.getmDb().execSQL(insertDataSql);
				e.printStackTrace();
			}
			// }else{
			// }
		}
	}

	public void deleteTableData(String tablename, String cache_type)
			throws Exception {
		try {
			mySqlLiteAdapter.getDb().execSQL(
					deleteTableData.replaceFirst("\\?", tablename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (e.getMessage().startsWith("no such table")) {
				return;
			}
		}
	}

	public List<EntityObj> selectData() {
		return selectData(cacheInfo.cacheTable, cacheInfo.cacheKey);
	}

	public List<EntityObj> selectData(String tablename, String cache_type) {
		Cursor cursor = null;
		try {
			cursor = mySqlLiteAdapter.getDb().rawQuery(
					"select * from "
							+ tablename
							+ (cache_type != null ? " where cache_type='"
									+ cache_type + "'" : ""), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (e.getMessage().startsWith("no such table")) {
				return new ArrayList<EntityObj>(0);
			}
		}
		List<EntityObj> selectData = CursorParser.parseCursor(cursor);
		return selectData;
	}

}
