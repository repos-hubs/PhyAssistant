package com.jibo.dbhelper;


import java.util.ArrayList;

import com.jibo.common.Constant;
import com.jibo.data.entity.RelatedBeans;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
/**
 * Event oldEvent 三个表的操作类
 * */
public class EventAdapter extends BaseSqlAdapter{

	public EventAdapter(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}

	public boolean insertEvents(String eventsID, String name,
			String place, String date, String content, String organizer,
			String tel, String fax, String email, String website) {

		
		String[] params = new String[10];
		params[0] = eventsID;
		params[1] = name;
		params[2] = content;
		params[3] = organizer;
		params[4] = tel;
		params[5] = fax;
		params[6] = email;
		params[7] = website;
		params[8] = place;
		params[9] = date;
		// Log.e("content",content);
		excuteSql("insert into Events(eventsID,name,content,organizer,tel,fax,email,website,place,date)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",params);	
		return true;
	}

	public boolean delTabEvent() {
		return excuteSql("drop table IF EXISTS Events");
	}

	public  boolean delTabOldEvent() {
		return excuteSql("drop table IF EXISTS OldEvents");

	}

	public boolean insertOldEvents(String eventsID,
			String name, String place, String date, String content,
			String organizer, String tel, String fax, String email,
			String website) {
		String[] params = new String[10];
		params[0] = eventsID;
		params[1] = name;
		params[2] = content;
		params[3] = organizer;
		params[4] = tel;
		params[5] = fax;
		params[6] = email;
		params[7] = website;
		params[8] = place;
		params[9] = date;
		// Log.e("content",content);
		excuteSql("insert into OldEvents(eventsID,name,content,organizer,tel,fax,email,website,place,date)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",params);
		return true;

	}
	
	public String selectMaxDatetime() {
		int i = 0;
		String dateTime = "";
		Cursor cur = getCursor("select date from Events where mobileID =(select max(mobileID) from Events)",null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("date");
						dateTime = cur.getString(indexdate);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB();
		return dateTime;

	}

	public String selectMaxDatetimeOld() {
		int i = 0;
		String dateTime = null;
		Cursor cur = getCursor("select date from OldEvents where mobileID in(select min(mobileID) from Events)",
						null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("date");
						dateTime = cur.getString(indexdate);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}	
		cur.close();
		closeDB();
		return dateTime;

	}

	public  RelatedBeans selectEve(String eId) {
		// events[] list = new events[eventsLength];
		int i = 0;
		Cursor cur = getCursor(
				"select *from Events where eventsID=" + eId, null);
		RelatedBeans rb = new RelatedBeans();
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
		
						int indexTitle = cur.getColumnIndex("name");
						rb.setName(cur.getString(indexTitle));

						int indexPlace = cur.getColumnIndex("place");
						rb.setPlace(cur.getString(indexPlace));

						int indexdate = cur.getColumnIndex("date");
						rb.setEventDate(cur.getString(indexdate));

						int indexOrganizer = cur.getColumnIndex("organizer");
						rb.setOrganizer(cur.getString(indexOrganizer));
						int indexTel = cur.getColumnIndex("tel");
						rb.setTel(cur.getString(indexTel));
						int indexfax = cur.getColumnIndex("fax");
						rb.setFax(cur.getString(indexfax));
						int indexEmail = cur.getColumnIndex("email");
						rb.setEmail(cur.getString(indexEmail));
						int indexWebsite = cur.getColumnIndex("website");
						rb.setWebsite(cur.getString(indexWebsite));
						int indexContent = cur.getColumnIndex("content");
						rb.setIntroduction(cur.getString(indexContent));
						Log.e("website", cur.getString(indexWebsite) + "0");
						i++;
					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Log.e("exception", );
		}
		cur.close();
		closeDB();
		return rb;
	}

	public ArrayList<RelatedBeans> selectEvents() {
		// events[] list = new events[eventsLength];
		int i = 0;
		Cursor cur = getCursor(
				"select *from Events   limit 0,10 ", null);
		ArrayList<RelatedBeans> al =new ArrayList<RelatedBeans>();

		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						RelatedBeans rb=new RelatedBeans();
						int indexId = cur.getColumnIndex("eventsID");
						rb.setID(cur.getString(indexId));

						int indexTitle = cur.getColumnIndex("name");
						rb.setName(cur.getString(indexTitle));

						int indexPlace = cur.getColumnIndex("place");
						rb.setPlace(cur.getString(indexPlace));

						int indexdate = cur.getColumnIndex("date");
						rb.setEventDate(cur.getString(indexdate));
						al.add(rb);
						i++;

					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Log.e("exception", );
		}
		cur.close();
		closeDB();
		return al;
	}

	public ArrayList<RelatedBeans> selectOldEvents() {
		// events[] list = new events[eventsLength];
		int i = 0;
		Cursor cur = getCursor(
				"select *from OldEvents   limit 0,10 ", null);
		ArrayList<RelatedBeans> al =new ArrayList<RelatedBeans>();

		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						RelatedBeans rb=new RelatedBeans();
						int indexId = cur.getColumnIndex("eventsID");
                        rb.setID(cur.getString(indexId));
                        
						int indexTitle = cur.getColumnIndex("name");
						rb.setName(cur.getString(indexTitle));

						int indexPlace = cur.getColumnIndex("place");
						rb.setPlace(cur.getString(indexPlace));

						int indexdate = cur.getColumnIndex("date");
						rb.setEventDate(cur.getString(indexdate));
						al.add(rb);
						i++;


					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Log.e("exception", );
		}
		cur.close();
		closeDB();
		return al;
	}

	public  RelatedBeans selectOldEve(String eId) {
		// events[] list = new events[eventsLength];
		int i = 0;
		Cursor cur = getCursor(
				"select *from OldEvents where eventsID=" + eId, null);
		RelatedBeans rb = new RelatedBeans();
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexTitle = cur.getColumnIndex("name");
						rb.setName(cur.getString(indexTitle));

						int indexPlace = cur.getColumnIndex("place");
						rb.setPlace(cur.getString(indexPlace));

						int indexdate = cur.getColumnIndex("date");
						rb.setEventDate(cur.getString(indexdate));

						int indexOrganizer = cur.getColumnIndex("organizer");
						rb.setOrganizer(cur.getString(indexOrganizer));
						int indexTel = cur.getColumnIndex("tel");
						rb.setTel(cur.getString(indexTel));
						int indexfax = cur.getColumnIndex("fax");
						rb.setFax(cur.getString(indexfax));
						int indexEmail = cur.getColumnIndex("email");
						rb.setEmail(cur.getString(indexEmail));
						int indexWebsite = cur.getColumnIndex("website");
						rb.setWebsite(cur.getString(indexWebsite));
						int indexContent = cur.getColumnIndex("content");
						rb.setIntroduction(cur.getString(indexContent));
						Log.e("website", cur.getString(indexWebsite) + "0");
						i++;
					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Log.e("exception", );
		}
		cur.close();
		closeDB();
		return rb;
	}

	// "insert into
	// mobileDocTable(title,description,location,docType,tags,sync_status)
	// values(\""+title+"\",\""+description+"\",\""+location+"\","+docType+",\""+tags+"\","+sync_status+")";
	// mySqlite.update(id,title, description,tag,content);

	public void clearEvents(Context context) {
		excuteSql("delete from Events");

	}

	public void clearOldEvents(Context context) {
		excuteSql("delete from OldEvents");
	}

}
