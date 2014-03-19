package com.jibo.dbhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.jibo.data.entity.SurveyQuestionEntity;
import com.jibo.data.entity.SurveyQuestionItemEntity;

public class SurveyQuestionAdapter extends BaseSqlAdapter {
	private String tbName = "jibo.db";
	private Context context;
	private String dbName;
	public SurveyQuestionAdapter(Context ctx, int vesion) {
		this.context = ctx;
		if (dbName == null)
			dbName = Constant.DATA_PATH_GBADATA + File.separator + tbName;
		mDbHelper = new SurveyListHelper(ctx, dbName, null, vesion);
	}
	
	public void deleteSurvey(String id, String userName, String flag) {
		String sql = "delete from survey_list where id="+id+" and username='"+userName+"' and flag='"+flag+"'";
		excuteSql(sql);
		closeDB();
	}
	
	//FLAG 0:RECHECK
	//FLAG 1:QUESTION
	public void updateQuestionList(String id,ArrayList<SurveyQuestionEntity> questionList, String flag, String username) {
		String sql = "select * from survey_list where id="+id+" and username='"+username+"' and flag='"+flag+"'";
		Cursor cursor = getCursor(sql, null);
		if(cursor.getCount()>0) {
			String update = "update survey_list set data='"+getDataFromQuestionList(questionList)+"' where id="+id+" and username='"+username+"' and flag='"+flag+"'";
			
		} else {
			ContentValues cv = new ContentValues();
			cv.put("id", id);
			cv.put("flag", flag);
			String data = getDataFromQuestionList(questionList);
			cv.put("data", data);
			cv.put("recheck_finish", "0");
			cv.put("username", username);
			
			insertSql("survey_list", cv);
		}
		cursor.close();
		closeDB();
	}
	
	public boolean checkIfSurveyExist(String id, String flag, String userName) {
		String sql = "select * from survey_list where id="+id+" and flag='"+flag+"' and username='"+userName+"' and recheck_finish='0'";
		Cursor cursor = getCursor(sql, null);
		int count = cursor.getCount();
		
		return count>0;
	}
	
	public HashMap<Integer, Integer> getAnswerCount(String id, String flag, String userName) {
		String sql = "select data from survey_list where id="+id+" and flag='"+flag+"' and username='"+userName+"' and recheck_finish='0'";
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		Cursor cursor = getCursor(sql, null);
		int result = 0;
		int total = 0;
		while(cursor.moveToNext()) {
			String data = cursor.getString(0);
			try {
				JSONArray jsa = new JSONArray(data);
				total = jsa.length();
				for(int i=0; i<jsa.length(); i++) {
					JSONObject jo = (JSONObject) jsa.get(i);
					if(!"".equals(jo.get("qValue").toString())) {
						result++;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		map.put(result, total);
		return map;
	}

	public String getRecheckStatus(String id, String flag, String userName) {
		String sql = "select recheck_finish from survey_list where id="+id+" and flag='"+flag+"' and username='"+userName+"'";
		Cursor cursor = getCursor(sql, null);
		String status = "";
		while(cursor.moveToNext()) {
			status = cursor.getString(0);
		}
		cursor.close();
		closeDB();
		return status;
	}
	
	public void updateRecheckStatus(String recheckStatus, String id, String flag, String userName) {
		String sql = "update survey_list set recheck_finish='"+recheckStatus+"' where id="+id+" and username='"+userName+"' and flag='"+flag+"'";
		excuteSql(sql);
		closeDB();
	}
	
	public ArrayList<SurveyQuestionEntity> getQuestionList(String id, String flag, String userName) {
		String sql = "select data from survey_list where id="+id+" and flag='"+flag+"' and username='"+userName+"' and recheck_finish='0'";
		Cursor cursor = getCursor(sql, null);
		ArrayList<SurveyQuestionEntity> questionList = new ArrayList<SurveyQuestionEntity>();
		while(cursor.moveToNext()) {
			String data = cursor.getString(0);
			try {
				JSONArray jsa = new JSONArray(data);
				for(int i=0; i<jsa.length(); i++) {
					JSONObject jo = (JSONObject) jsa.get(i);
					SurveyQuestionEntity sqe = new SurveyQuestionEntity();
					sqe.setId(jo.get("qID").toString());
					sqe.setTitle(jo.get("qTitle").toString());
					sqe.setContent(jo.get("qContent").toString());
					sqe.setType(jo.get("qType").toString());
					sqe.setAnswer(jo.get("qAnswerRange").toString());
					sqe.setValue(jo.get("qValue").toString());
					String answerList = jo.get("answerList").toString();
					JSONArray jsa2 = new JSONArray(answerList);
					ArrayList<SurveyQuestionItemEntity> sqieList = new ArrayList<SurveyQuestionItemEntity>();
					for(int j=0; j<jsa2.length(); j++) {
						JSONObject jo2 = (JSONObject) jsa2.get(j);
						SurveyQuestionItemEntity sqie = new SurveyQuestionItemEntity();
						sqie.setOptionID(jo2.get("optionsID").toString());
						sqie.setLabel(jo2.get("answer").toString());
						sqie.setJump(jo2.get("qJump").toString());
						sqieList.add(sqie);
					}
					sqe.setQuestionItemList(sqieList);
					questionList.add(sqe);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		cursor.close();
		closeDB();
		return questionList;
	}
	
	public void updateAnswerList(String id, String userName, String flag, ArrayList<View> viewList) {
		String sql = "select data from survey_list where id="+id+" and username='"+userName+"' and flag='"+flag+"'";
		Cursor cursor = getCursor(sql, null);
		ArrayList<String> valueList = getValueFromView(viewList);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String data = cursor.getString(0);
				try {
					JSONArray jsa = new JSONArray(data);
					for (int i = 0; i < jsa.length(); i++) {
						JSONObject jo = (JSONObject) jsa.get(i);
						jo.put("qValue", valueList.get(i));
					}
					String updateSql = "update survey_list set data='"+jsa.toString()+"' where id="+id+" and username='"+userName+"' and flag='"+flag+"'";
					excuteSql(updateSql);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

			}
		}
		
		cursor.close();
		closeDB();
		
	}
	
	public ArrayList<String> getValueFromView(ArrayList<View> questionViewList) {
		ArrayList<String> questionValueList = new ArrayList<String>();
		for(int i=0; i<questionViewList.size(); i++) {
			String value = "";
			View view = questionViewList.get(i);
			if(view instanceof RadioGroup) {
				value = view.getTag().toString();
			} else if(view instanceof LinearLayout) {
				LinearLayout llt = (LinearLayout) view;
				if(llt.getTag()!=null&&!llt.getTag().equals("")) {
					value = llt.getTag().toString();
				} else {
					for(int j=0; j<llt.getChildCount(); j++) {
						CheckBox cb = (CheckBox) llt.getChildAt(j);
						if(cb.getTag().toString().equals("")) {
							continue;
						} else {
							value = value+cb.getTag().toString()+"/";
						}
					}
					if(value.length()>0) value = value.substring(0, value.length()-1);
					
				}
				
			} else if(view instanceof EditText) {
				value = ((EditText) view).getText().toString();
			}
			
			questionValueList.add(value);
		}
		return questionValueList;
	}
	
	public String getDataFromQuestionList(ArrayList<SurveyQuestionEntity> questionList) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i=0; i<questionList.size(); i++) {
			SurveyQuestionEntity en = questionList.get(i);
			sb.append("{");
			sb.append("\"qID\":\""+en.getId()+"\",");
			sb.append("\"qTitle\":\""+en.getTitle()+"\",");
			sb.append("\"qContent\":\""+en.getContent()+"\",");
			sb.append("\"qType\":\""+en.getType()+"\",");
			sb.append("\"answerList\":"+createAnswerList(en.getQuestionItemList())+",");
			sb.append("\"qAnswerRange\":\""+en.getAnswer()+"\",");
			sb.append("\"qValue\":\""+"\"},");
		}
		if(sb.toString().length()>1) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("]");
		result = sb.toString();
		
		return result;
	}
	
	public String createAnswerList(ArrayList<SurveyQuestionItemEntity> itemList) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i=0; i<itemList.size(); i++) {
			SurveyQuestionItemEntity en = itemList.get(i);
			sb.append("{");
			sb.append("\"optionsID\":\""+en.getOptionID()+"\",");
			sb.append("\"answer\":\""+en.getLabel()+"\",");
			sb.append("\"qJump\":\""+en.getJump()+"\"},");
		}
		if(sb.toString().length()>1) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("]");
		result = sb.toString();
		return result;
	}
	
	class SurveyListHelper extends SQLiteOpenHelper {
		public SurveyListHelper(Context context, String name,
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
