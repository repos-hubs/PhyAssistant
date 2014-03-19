package com.jibo.base.src.request;

import java.util.ArrayList;
import java.util.List;

import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestResult;


import android.database.Cursor;

public class CursorParser {
	public static class CursorResult implements RequestResult{
		private String req;
		private List<EntityObj> result;
		private Cursor metaResult;
		public Cursor getMetaResult() {
			return metaResult;
		}
		public void setMetaResult(Cursor metaResult) {
			this.metaResult = metaResult;
		}
		public String getTag() {
			return req;
		}
		public void setTag(String req) {
			this.req = req;
		}
		public List<EntityObj> getResult() {
			return result;
		}
		public void setResult(List<EntityObj> result) {
			this.result = result;
		}
		@Override
		public List<EntityObj> getObjs() {
			// TODO Auto-generated method stub
			return getResult();
		}
		
	}
	public static CursorResult parseCursor(CursorResult cursorResult) {
		cursorResult.result = parseCursor(cursorResult.metaResult);
		return cursorResult;
	}
	public static List<EntityObj> parseCursor(Cursor cursor) {
		List<EntityObj> selectData = new ArrayList<EntityObj>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				EntityObj entityObj = new EntityObj();
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					Object object = cursor.getString(i);
					object = object == null ? "" : object;
					object = !object.equals("")
							&& object.toString().matches("[0-9]*") ? Integer
							.parseInt(object.toString()) : object;
					entityObj.fieldContents
							.put(cursor.getColumnName(i), object);
				}
				selectData.add(entityObj);
			}
		}
		return selectData;
	}
}
