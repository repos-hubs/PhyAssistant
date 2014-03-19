package com.jibo.dbhelper;

import java.util.ArrayList;

import com.jibo.common.Constant;
import com.jibo.data.entity.DrugSurvey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Question Answer QAnswer 三个表的操作类
 * */
public class QuestionAdapter extends BaseSqlAdapter{

	public QuestionAdapter(Context ctx) {
		mDbHelper = new MySqlLiteHelper(ctx, Constant.MY_SQLITE_VESION);
	}

	public boolean insertSurveyQuestion(String qid, String qcontent,
			String qtype, String qversion, String qtitle) {
		String sql = "insert into Question(Qid,Qtype,Qtitle,Qversion,Qcontent) values(\""
				+ qid
				+ "\",\""
				+ qtype
				+ "\",\""
				+ qtitle
				+ "\",\""
				+ qversion
				+ "\",\"" + qcontent + "\")";
		excuteSql(sql);
		Log.v("ok", "insert Table t_user ok");

		return false;
	}

	public boolean insertSurveyAnswer(String aid, String acontent, String qid) {
		String sql = "insert into Answer(Aid,Acontent,Qid) values(\"" + aid
				+ "\",\"" + acontent + "\",\"" + qid + "\")";
		excuteSql(sql);
		return false;
	}

	public boolean insertSurveyResult(String qid, String qtype, String aid,
			String uid) {
		String sql = "insert into QAnswer(Qid,Qtype,Aid,Uid) values(\"" + qid
				+ "\",\"" + qtype + "\",\"" + aid + "\",\"" + uid + "\")";
		excuteSql(sql);
		return false;
	}
	public String selectSurveyVersion(Context context) {
		int i = 0;
		String dateTime = "";
		Cursor cur = getCursor("select distinct Qversion From Question", null);
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {
						int indexdate = cur.getColumnIndex("Qversion");
						dateTime = cur.getString(indexdate);
						i++;
						Log.e("dateTime", dateTime + "version");
					} while (cur.moveToNext());
				}
			}
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
		cur.close();
		closeDB() ;
		return dateTime;

	}
	public ArrayList<DrugSurvey> selectSurvey(Context context) {
		// events[] list = new events[eventsLength];
		int i = 0;
		Cursor cur = getCursor(
						"select Question.Qid,Aid,Qtitle,Qcontent,Qtype,Acontent from Question,Answer where Question.Qid=Answer.Qid ",
						null);
		ArrayList<DrugSurvey> arrayList = new ArrayList<DrugSurvey>();
		try {
			if (cur != null) {
				if (cur.moveToFirst()) {

					do {

						DrugSurvey drugSurvey = new DrugSurvey();
						drugSurvey.setQid(cur.getString(0));

						drugSurvey.setAid(cur.getString(1));

						drugSurvey.setQtitle(cur.getString(2));


						drugSurvey.setQcontent(cur.getString(3));

						drugSurvey.setQtype(cur.getString(4));


						drugSurvey.setAconttent(cur.getString(5));
						arrayList.add(drugSurvey);
						i++;


						Log.e("i", String.valueOf(i));

					} while (cur.moveToNext());
				}
			}
			if (i > 0) {
//				Survey.UPDATEDB = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Log.e("exception", );
		}
		
		cur.close();
		closeDB();
		return arrayList;
	}
	
	public  boolean deleteSurveyQuestion(Context context) {
//		return excuteSql("drop table IF EXISTS Question");
		return excuteSql("DELETE FROM Question");
	}

	public  boolean deleteSurveyAnswer(Context context) {
		return excuteSql("DELETE FROM Answer");
	}

	public  boolean deleteSurveyQAnswer(Context context) {
		return excuteSql("DELETE FROM QAnswer");
	}

}
