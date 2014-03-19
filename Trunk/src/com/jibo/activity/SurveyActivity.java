package com.jibo.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.jibo.asynctask.uploadDataNew;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.GetSurveyQueParser;
import com.jibo.data.SubmitResultParser;
import com.jibo.data.entity.DrugSurvey;
import com.jibo.dbhelper.QuestionAdapter;
import com.jibo.net.BaseResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.api.android.GBApp.R;
public class SurveyActivity extends BaseSearchActivity implements OnClickListener{
	private static final String TAG = "Survey";
	public static final String LOGIN_FLAG="login_flag";
	public static final String USER_NAME="USER_NAME";
	String SETTING_INFOS="SETTING_INFOS"; 
	public static final String TOPIC = "TOPIC";
	private boolean flag=true;
	
	public static final String REGION="region";
	public static final String DEPARTMENT="department";
	public static boolean UPDATEDB=false;
	SurveyActivity context;
	public int ridioCount = 0;
	public Button btn = null;
	public static String surveyVersion="";
	private ListView list = null;
	MyAdapter adapter = null;
	private boolean submitFlag;
	ArrayList<DrugSurvey> arrayList =new ArrayList<DrugSurvey>();
	ArrayList<DrugSurvey> arrayListTemp = new ArrayList<DrugSurvey>();
	TextView m_TextView,m_TextView_Topic;
	TextView titleTv;
	HashMap<Integer, String> hashMapRidio = new HashMap<Integer, String>();
	HashMap<Integer, String> hashMapCheckBox = new HashMap<Integer, String>();
	HashMap<String, String> simAnswer = new HashMap<String, String>();
	HashMap<String, String> mulAnswer = new HashMap<String, String>();
	// prime 2011-4-15
	SharedPreferences setting;
    public static String username="";
	private static final String CHECKITEM = "GBACHECKITEM24";
    static String region="";
    static String department="";
	private SharedPreferences sp;
    public BaseResponseHandler baseHandler;
    public BaseResponseHandler submitHandler;
    QuestionAdapter qaSqlite;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.survey);
		super.onCreate(savedInstanceState);
		qaSqlite=new QuestionAdapter(this);
		list = (ListView) findViewById(R.id.MyListView);
        baseHandler =new BaseResponseHandler(this);
        submitHandler=new BaseResponseHandler(this);
		List<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		context = this;
		btn = (Button) findViewById(R.id.submitBtn);
		
		m_TextView = (TextView) findViewById(R.id.TextView01);
		titleTv=(TextView) findViewById(R.id.txt_header_title);
		titleTv.setTextSize(18);
		titleTv.setText("");//R.string.surTitle
//		m_TextView.setTextColor(Color.BLACK);
		m_TextView_Topic=(TextView) findViewById(R.id.txt_header_title);
		
		setting = context.getSharedPreferences(SETTING_INFOS, 0);
		String name = setting.getString(TOPIC, ""); 
//		submitFlag=setting.getBoolean("submitFlag", true);
//		flag=setting.getBoolean("flag",true);
		submitFlag=true;
		if(name!=null)
		{
			m_TextView_Topic.setText(name);
  		    m_TextView_Topic.setTextSize(18);
			Log.e("topic", name);
		}

		btn.setOnClickListener(this);
		
		arrayListTemp = qaSqlite.selectSurvey(context);
		surveyVersion=qaSqlite.selectSurveyVersion(context);
		SharedPreferences settings = getSharedPreferences(LOGIN_FLAG, 0); 
		MobclickAgent.onError(this);
		username = settings.getString(USER_NAME, "");
		Log.e("username", username);
		region=settings.getString(REGION,"");
		department=settings.getString(DEPARTMENT, "");
		adapter = new MyAdapter(context);
		list.setAdapter(adapter);
		if (arrayListTemp.size() == 0) {
			
			Properties propertyInfo =new Properties();
			propertyInfo.put(SoapRes.KEY_DEADLINE, "");
			propertyInfo.put(SoapRes.KEY_SURVEY_REGION, "");
			sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_SURVEY_GETQES,
					propertyInfo,
					baseHandler);
		} else {
			arrayList=new ArrayList<DrugSurvey>(); 

			Log.e("surveySize", String.valueOf(arrayListTemp.size()));
			for (int i = 0; i < arrayListTemp.size() - 1; i++) {

				DrugSurvey drugSur = new DrugSurvey();
				String subAContent = "";
				String subAid = "";
				drugSur.setQid(arrayListTemp.get(i).getQid());
				Log.e("qid", arrayListTemp.get(i).getQid());
				drugSur.setQcontent(arrayListTemp.get(i).getQcontent());
				drugSur.setQtype(arrayListTemp.get(i).getQtype());
				Log.e("Qtype",arrayListTemp.get(i).getQtype());
				drugSur.setQtitle(arrayListTemp.get(i).getQtitle());
				subAid = arrayListTemp.get(i).getAid();
				subAContent = arrayListTemp.get(i).getAconttent();
				for (int j = i + 1; j < arrayListTemp.size(); j++) {

					if (arrayListTemp.get(i).getQid().equals(
							arrayListTemp.get(j).getQid())) {
						subAContent = subAContent + ","
								+ arrayListTemp.get(j).getAconttent();
						subAid = subAid + "," + arrayListTemp.get(j).getAid();
						if(j==arrayListTemp.size()-1)
						{
							drugSur.setAconttent(subAContent);
							Log.e("subAContent", subAContent);
							drugSur.setAid(subAid);
							Log.e("subAid", subAid);
							arrayList.add(drugSur);
							i = j-1;
						}
					} else {

						drugSur.setAconttent(subAContent);
						Log.e("subAContent", subAContent);
						drugSur.setAid(subAid);
						Log.e("subAid", subAid);
						arrayList.add(drugSur);
						i = j-1;
						Log.e("i", String.valueOf(i));
						break;

					}

				}
			}
		}
		if(!surveyVersion.equals(""))
		{	
			Properties propertyInfo =new Properties();
			propertyInfo.put(SoapRes.KEY_SURVEY_DEPT, "");
			propertyInfo.put(SoapRes.KEY_SURVEY_REGION, "");
			sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_SURVEY_GETQES,
					propertyInfo,
					baseHandler);
		}
		setPopupWindowType(Constant.MENU_TYPE_1);
		
		uploadLoginLogNew("Activity","Survey", "create", null);
	}





	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		uploadLoginLogNew("Activity","Survey", "end", null);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!v.isEnabled()) {
			Log.e(TAG, "on click error, button is disabled");
			return;
		}
		Intent intent;
		switch (v.getId()) {
		case R.id.submitBtn: 

			
//			this.finish();
     	   /*					
     	   setting = context.getSharedPreferences(SETTING_INFOS, 0);    		    
		   setting.edit()    
		   .putString(TOPIC, DrugData.survey.qtopic[0]) 
		   .commit();
		   **/
	        if(submitFlag)
	        {
				
                if((simAnswer.size()+mulAnswer.size())<arrayList.size())
                {
					Toast toast = Toast.makeText(context, context.getString(R.string.answerNone),
							Toast.LENGTH_SHORT);
					//Toast.LENGTH_LONG
					toast.setGravity(Gravity.TOP, 0, 100);
					toast.show();
                	
                }else
                {
//                    what =2;
                	Log.e("runsimAnswer", String.valueOf(simAnswer));
                	Log.e("runmulAnswer", String.valueOf(mulAnswer));
                    if(simAnswer!=null)
                    {
        				Iterator itt = simAnswer.keySet().iterator();
        				while (itt.hasNext()) {
        					String str = (String) itt.next();
        					Log.e("key", str);
        					Log.e("value", simAnswer.get(str));
//        						mySqlite.insertSurveyResult(context, str, "S", simAnswer
//        								.get(str), username);	
        					Properties propertyInfo =new Properties();
        					Log.e("sp_userName",SharedPreferencesMgr.getUserName());
        					propertyInfo.put(SoapRes.KEY_SURVEY_UID, SharedPreferencesMgr.getUserName());
        					propertyInfo.put(SoapRes.KEY_SURVEY_QID, str);
        					propertyInfo.put(SoapRes.KEY_SURVEY_QTYPE, "S");
        					propertyInfo.put(SoapRes.KEY_SURVEY_AID, simAnswer.get(str));
        					sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_SURVEY_SUBMIT,
        							propertyInfo,
        							submitHandler);
        					
//        						dataUpdateNew(str, simAnswer.get(str),username, "S");
//        						if(DrugData.flagStr.equals("false"))
//        						{
//        							what=6;
//        		                	progressDialog.dismiss();
//        							break;
//        						}


        				}
                    }
                    if(mulAnswer!=null)
                    {
        				Iterator ittMul = mulAnswer.keySet().iterator();
        				while (ittMul.hasNext()) {
        					String str = (String) ittMul.next();
        					Log.e("key", str);
        					Log.e("value", mulAnswer.get(str));

//        						mySqlite.insertSurveyResult(context, str, "M", mulAnswer
//        								.get(str), username);
        					Properties propertyInfo =new Properties();
        					propertyInfo.put(SoapRes.KEY_SURVEY_UID, SharedPreferencesMgr.getUserName());
        					propertyInfo.put(SoapRes.KEY_SURVEY_QID, str);
        					propertyInfo.put(SoapRes.KEY_SURVEY_QTYPE, "M");
        					propertyInfo.put(SoapRes.KEY_SURVEY_AID, mulAnswer.get(str));
        					sendRequest(SoapRes.URLSurvey, SoapRes.REQ_ID_SURVEY_SUBMIT,
        							propertyInfo,
        							submitHandler);
//        						dataUpdateNew(str, mulAnswer.get(str), username, "M");
//        						if(DrugData.flagStr.equals("false"))
//        						{
//        							what=6;
//        		                	progressDialog.dismiss();
//        							break;
//        						}

        				}	
                    }
                    
        			MobclickAgent.onEvent(context,"SurUserName",SharedPreferencesMgr.getUserName(),1);//"SimpleButtonclick");
        			
        			uploadLoginLogNew("Survey",  SharedPreferencesMgr.getUserName(),"SurUserName", null);
                
                }
				
	        }else
	        {
	        	//progressDialog.dismiss();
				Toast toast = Toast.makeText(context, context.getString(R.string.existMsg),
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 100);
				toast.show();
	        }
			

			
			
			
			
			

			break;
		}
		
	}
	
	public class MyAdapter extends BaseAdapter {//自定义adapter
		private LayoutInflater inflater;
		public String[] qcontentArray = null;
		public String[] aidArray = null;
		public String checkStr = null;
		public String selectStr = null;
		public int[] ridioArray = null;
		public int checkId = 0;
		final HashMap<Integer,RadioButton> m_radioButton=new HashMap<Integer,RadioButton>();
		ArrayList<CheckBox> m_checkBox = new ArrayList<CheckBox>();
		CheckBox chk = null;

		public MyAdapter(Context c) {
			this.inflater = LayoutInflater.from(c);
		}

		public int getCount() {
			return arrayList.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View myView = inflater.inflate(R.layout.my_listitem, null);
			LinearLayout layout = (LinearLayout) myView
					.findViewById(R.id.myListItem);
			TextView m_TextView = new TextView(context);
			TextView c_TextView = new TextView(context);
			Log.e("arrayList", arrayList.get(arg0).getQcontent());
			Log.e("arrayList", String.valueOf(arg0));
			m_TextView.setTextSize(16);
			m_TextView.setTextColor(Color.BLACK);
			m_TextView.setText((arg0 + 1) + "."
					+ arrayList.get(arg0).getQcontent());
			Log.e("title", "title" + arrayList.get(arg0).getQtitle());
			c_TextView.setText("(" + arrayList.get(arg0).getQtitle() + ")");
			c_TextView.setTextSize(16);
			c_TextView.setTextColor(Color.BLACK);
			

			qcontentArray = arrayList.get(arg0).getAconttent().split(",");
			Log.e("tag", arrayList.get(arg0).getAid());
			aidArray = arrayList.get(arg0).getAid().split(",");

			ridioArray = new int[aidArray.length];
			layout.addView(m_TextView);
			layout.addView(c_TextView);
			RadioGroup m_RadioGroup = new RadioGroup(context);
			layout.addView(m_RadioGroup);
			for (int i = 0; i < aidArray.length; i++) {
				
				if (arrayList.get(arg0).getQtype().equals("S")) {
					RadioButton m_Radio = new RadioButton(context);
					 if(simAnswer.get(arrayList.get(arg0).getQid())!=null&&simAnswer.get(arrayList.get(arg0).getQid()).equals(aidArray[i]))
					 {
						 m_Radio.setChecked(true);
						 Log.e(simAnswer.get(arrayList.get(arg0).getQid())+"simanswer", simAnswer.get(arrayList.get(arg0).getQid()));
					 }



			
//					 Log.e("m_Radio.getId()", String.valueOf(m_Radio.getId()));
					m_Radio.setText(qcontentArray[i]);
					m_Radio.setTextColor(Color.BLACK);
					m_RadioGroup.addView(m_Radio);
					
					ridioArray[i] = m_Radio.getId();
					m_radioButton.put(m_Radio.getId(),m_Radio);
					hashMapRidio.put(m_Radio.getId(), arrayList.get(arg0)
							.getQid()
							+ "," + aidArray[i]);
					Log.e(String.valueOf(arrayList.get(arg0).getQid()),
							qcontentArray[i]);
					Log.e("m_Radio.getId()1", String.valueOf(m_Radio.getId()));
				}

			}
			if (arrayList.get(arg0).getQtype().equals("M")) {

				for (int i = 0; i < aidArray.length; i++) {
					CheckBox m_check = new CheckBox(context);
					if(mulAnswer.get(arrayList.get(arg0).getQid())!=null)
					{
						String[] checkid=mulAnswer.get(arrayList.get(arg0).getQid()).split(",");
						for(int j=0;j<checkid.length;j++)
						{
							if(checkid[j].equals(aidArray[i]))
								m_check.setChecked(true);	
						}
					}
						
					m_check.setText(qcontentArray[i]);
					m_check.setTextColor(Color.BLACK);
					hashMapCheckBox.put(checkId, arrayList.get(arg0).getQid()
							+ "," + aidArray[i]);
					Log.e("checkId:" + checkId, arrayList.get(arg0).getQid()
							+ "," + aidArray[i]);

					layout.addView(m_check);
					m_checkBox.add(m_check);
					checkId++;
					chk = m_check;
					checkStr = arrayList.get(arg0).getQid();
					chk
							.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {

									Log
											.e("checkbox", String
													.valueOf(isChecked));

									String strAnswer = "";
									for (int j = 0; j < m_checkBox.size(); j++) {
										if (m_checkBox.get(j).isChecked()) {
											strAnswer = strAnswer
													+ m_checkBox.get(j)
															.getText()
															.toString();
											String[] strMulArr = hashMapCheckBox
													.get(j).split(",");
											if (mulAnswer.get(strMulArr[0]) != null
													&& !mulAnswer
															.get(strMulArr[0])
															.contains(
																	strMulArr[1])) {
												mulAnswer
														.put(
																strMulArr[0],
																mulAnswer
																		.get(strMulArr[0])
																		+ ","
																		+ strMulArr[1]);
											} else {
												mulAnswer.put(strMulArr[0],
														strMulArr[1]);

											}
											Log.e("mulAnswer", strAnswer);
										}

									}

								}
							});
				}

			}

			ridioCount = m_RadioGroup.getChildCount();

			m_RadioGroup
					.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							String[] strArr = hashMapRidio.get(checkedId)
									.split(",");
							simAnswer.put(strArr[0], strArr[1]);
							Log.e("CheckedID", String.valueOf(checkedId));
						}

					});
			return myView;
		}}
	
	@Override
	public void onReqResponse(Object o,int methodId) {
		// TODO Auto-generated method stub
		if(o!=null)
		{
		
			if(o instanceof GetSurveyQueParser)//下载调查问卷题目
			{
				GetSurveyQueParser data = (GetSurveyQueParser) o;
				if (data.list.size() > 0&&!surveyVersion.equals("")&&!UPDATEDB) {
					qaSqlite.deleteSurveyAnswer(context);
					qaSqlite.deleteSurveyQAnswer(context);
					qaSqlite.deleteSurveyQuestion(context);
					for (int i = 0; i < data.list.size() ; i++) {
						qaSqlite.insertSurveyQuestion(
								data.list.get(i).getQid(),
								data.list.get(i).getQtitle(),
								data.list.get(i).getQtype(),
								data.list.get(i).getQversion(),
								data.list.get(i).getQcontent());
						String[] aidArray = data.list.get(i).getAid().split(",");
						String[] acontentArray = data.list.get(i).getAconttent()
								.split(",");
						
						for (int j = 0; j < aidArray.length; j++) {
							qaSqlite.insertSurveyAnswer(aidArray[j],
									acontentArray[j],data.list.get(i).getQid());
						}
					}
					setting = context.getSharedPreferences(SETTING_INFOS, 0);    
					    
					   setting.edit()    
					   .putString(TOPIC, data.list.get(0).getQtopic()) 
					   .commit();
					   setting.edit()    
					   .putBoolean("submitFlag", true)
					   .commit();
				}
				if (data.list.size() > 0&&surveyVersion.equals("")&&!UPDATEDB)
				{
					Log.e("list22","list22");
					for (int i = 0; i < data.list.size(); i++) {
						qaSqlite.insertSurveyQuestion(
								data.list.get(i).getQid(),
								data.list.get(i).getQtitle(),
								data.list.get(i).getQtype(),
								data.list.get(i).getQversion(),
								data.list.get(i).getQcontent());
						String[] aidArray = data.list.get(i).getAid().split(",");
						String[] acontentArray = data.list.get(i).getAconttent()
								.split(",");
						
						for (int j = 0; j < aidArray.length; j++) {
							qaSqlite.insertSurveyAnswer( aidArray[j],
									acontentArray[j], data.list.get(i).getQid());
						}
					}
					setting = context.getSharedPreferences(SETTING_INFOS, 0);    
					   
 
					   setting.edit()    
					   .putString(TOPIC, data.list.get(0).getQtopic()) 
					   .commit();
					   setting.edit()    
					   .putBoolean("submitFlag", true)
					   .commit();
				}
				arrayList = new ArrayList<DrugSurvey>();
				arrayListTemp = qaSqlite.selectSurvey(context);
				Log.e("size",String.valueOf(arrayListTemp.size()));
				Log.e("", arrayListTemp.get(4).getAconttent());
				for (int i = 0; i < arrayListTemp.size()-1 ; i++) {
					DrugSurvey drugSur = new DrugSurvey();
					String subAContent = "";
					String subAid = "";
					drugSur.setQid(arrayListTemp.get(i).getQid());
					drugSur.setQcontent(arrayListTemp.get(i).getQcontent());
					drugSur.setQtype(arrayListTemp.get(i).getQtype());
					subAContent = arrayListTemp.get(i).getAconttent();
					drugSur.setQtitle(arrayListTemp.get(i).getQtitle());

					Log.e("subAContent", subAContent);
					subAid = arrayListTemp.get(i).getAid();
					for (int j = i+1; j < arrayListTemp.size(); j++) {

						if (arrayListTemp.get(i).getQid().equals(
								arrayListTemp.get(j).getQid())) {
							subAContent = subAContent + ","
									+ arrayListTemp.get(j).getAconttent();
							Log.e("subAContent", subAContent);
							subAid = subAid + ","
									+ arrayListTemp.get(j).getAid();							
							if(j==arrayListTemp.size()-1)
							{
								drugSur.setAconttent(subAContent);
								Log.e("subAContent", subAContent);
								drugSur.setAid(subAid);
								Log.e("subAid", subAid);
								arrayList.add(drugSur);
								i = j-1;
							}
						}
						else {

							drugSur.setAconttent(subAContent);
							drugSur.setAid(subAid);
							Log.e("subAContent", subAContent);
							arrayList.add(drugSur);
							i = j - 1;
							Log.e("i", String.valueOf(i));
							break;

						}

					}
				}

				setting = getSharedPreferences(SETTING_INFOS, 0); 
				String name = setting.getString(TOPIC, ""); 
				m_TextView_Topic.setText(name);
				adapter.notifyDataSetChanged();
			}
			if(o instanceof SubmitResultParser)//提交调查问卷数据
			{
				SubmitResultParser data = (SubmitResultParser) o;
				
				
				if (data.submitFlag.equals("success")) {
					
					Toast toast = Toast.makeText(context, context.getString(R.string.submitSuccess),
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 100);
					toast.show();		//.setTitle("") 
//					new AlertDialog.Builder(this) 
//					.setMessage(R.string.submitSuccess) 
//					.setPositiveButton(R.string.yesbtn, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//					}
//					})
//					.setNegativeButton(R.string.nobtn, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//					}
//					})
//					.show();
					
				}else
				{
					Toast toast = Toast.makeText(context, context.getString(R.string.existMsg),
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 100);
					toast.show();
					
					
//					new AlertDialog.Builder(this) 
//					.setMessage(R.string.submitSuccess) 
//					.setPositiveButton(R.string.yesbtn, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//					}
//					})
//					.setNegativeButton(R.string.nobtn, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//					}
//					})
//					.show();
				}
				
				if(flag)
				{
					Intent inte=new Intent(this,SurveySharingActivity.class);				
					startActivity(inte);
					flag=false;
					   setting.edit()    
					   .putBoolean("flag", false)
					   .commit();
				}

				
			}
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	
				this.finish();
       
	}
		return false;
	}
	
	


}


