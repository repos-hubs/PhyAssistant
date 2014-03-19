package com.jibo.activity;
import com.api.android.GBApp.R;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.jibo.GBApplication;
import com.jibo.adapter.CoauthorAdapter;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.data.CoauthorPaser;
import com.jibo.data.entity.CoauthorEntity;
import com.jibo.net.AsyncSoapResponseHandler;
public class NetworkCoauthorActivity extends BaseSearchActivity implements OnItemClickListener{
	private TextView mCoauthorTitle;
	private TextView mCoauthor;
	private ListView coauthorList;
	private GBApplication application;

	private String doctorID;
	private CoauthorRequestHandler inHospitalHandler;
	private CoauthorRequestHandler outHospitalHandler;
	private final int type_in_hospital = 1;
	private final int type_out_hospital = 2;
	private ArrayList<CoauthorEntity> coauthorInHospital;
	private ArrayList<CoauthorEntity> coauthorOutHospital;
	private ArrayList<CoauthorEntity> dataList;
	private ArrayList<Integer> groupList;
	private HashMap<Integer, Integer> groupMap;
	private HashMap<String, ArrayList<CoauthorEntity>> map;
	private CoauthorAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.network_coauthors);
		super.onCreate(savedInstanceState);
		inits();
	}

	private void inits() {
		adapter = new CoauthorAdapter(this);
		groupList = new ArrayList<Integer>();
		groupMap = new HashMap<Integer, Integer>();
		dataList = new ArrayList<CoauthorEntity>();
		coauthorInHospital = new ArrayList<CoauthorEntity>();
		coauthorOutHospital = new ArrayList<CoauthorEntity>();
		map = new HashMap<String, ArrayList<CoauthorEntity>>();
		inHospitalHandler = new CoauthorRequestHandler(type_in_hospital);
		outHospitalHandler = new CoauthorRequestHandler(type_out_hospital);
		application = (GBApplication)this.getApplication();
		mCoauthorTitle = (TextView) findViewById(R.id.txt_header_title);
		mCoauthor = (TextView) findViewById(R.id.txt_coauthor);
		coauthorList = (ListView) findViewById(R.id.lst_item);
		
		mCoauthor.setText(getString(R.string.coauthor));
		mCoauthorTitle.setText(getString(R.string.network));
		coauthorList.setOnItemClickListener(this);
		
		Intent intent = getIntent();
		doctorID = intent.getStringExtra("doctorID");
		/*被Terry注释，原因是偶尔会出现院内数据取得的是院外的数据，我觉得问题是propertyInfo没有分开，所以我试着分开*/
//		Properties propertyInfo =new Properties();
//		propertyInfo.put(SoapRes.KEY_GET_COAUTHOR_DOCTORID, doctorID);
//		propertyInfo.put(SoapRes.KEY_GET_COAUTHOR_INHOSPITAL, "true");
//		sendRequest(SoapRes.URLProfile, SoapRes.REQ_ID_GET_COAUTHOR, propertyInfo, inHospitalHandler);
//		
//		propertyInfo.put(SoapRes.KEY_GET_COAUTHOR_DOCTORID, doctorID);
//		propertyInfo.put(SoapRes.KEY_GET_COAUTHOR_INHOSPITAL, "false");
//		sendRequest(SoapRes.URLProfile, SoapRes.REQ_ID_GET_COAUTHOR, propertyInfo, outHospitalHandler);
		/*update by Teery*/
		/*院内的请求*/
		Properties propertyInfoIn =new Properties();
		propertyInfoIn.put(SoapRes.KEY_GET_COAUTHOR_DOCTORID, doctorID);
		propertyInfoIn.put(SoapRes.KEY_GET_COAUTHOR_INHOSPITAL, "true");
		sendRequest(SoapRes.URLProfile, SoapRes.REQ_ID_GET_COAUTHOR, propertyInfoIn, inHospitalHandler);
		/*院外的请求*/
		Properties propertyInfoOut =new Properties();
		propertyInfoOut.put(SoapRes.KEY_GET_COAUTHOR_DOCTORID, doctorID);
		propertyInfoOut.put(SoapRes.KEY_GET_COAUTHOR_INHOSPITAL, "false");
		sendRequest(SoapRes.URLProfile, SoapRes.REQ_ID_GET_COAUTHOR, propertyInfoOut, outHospitalHandler);
		
		setPopupWindowType(Constant.MENU_TYPE_4);
	}
	
	private void loadLstData( HashMap<String, ArrayList<CoauthorEntity>> dataMap) {
		HashMap<String, String> map = new HashMap<String, String>();
		if(dataMap.size() == 2) {
			map.put("title1", getString(R.string.ca_hsp)+":"+dataMap.get("in").size());
			groupList.add(0);
			adapter.addSeparatorItem(map);
			for(int i=0; i<dataMap.get("in").size(); i++) {
				adapter.addItem(dataMap.get("in").get(i));
			}
			coauthorList.setAdapter(adapter);
			map.put("title2", getString(R.string.cao_hsp)+":"+dataMap.get("out").size());
			groupList.add(dataMap.get("in").size()+1);
			adapter.addSeparatorItem(map);
			for(int i=0; i<dataMap.get("out").size(); i++) {
				adapter.addItem(dataMap.get("out").get(i));
			}
			coauthorList.setAdapter(adapter);
		}
	}
	
	private class CoauthorRequestHandler extends AsyncSoapResponseHandler {
		private int type;
		int dialogId = DialogRes.DIALOG_ID_NET_CONNECT;
		public CoauthorRequestHandler(int type) {
			this.type = type;
		}
		
		@Override
		public void onStart() {
			NetworkCoauthorActivity.this.showDialog(dialogId);
			super.onStart();
		}
		
		@Override
		public void onFinish() {
			NetworkCoauthorActivity.this.removeDialog(dialogId);
			super.onFinish();
		}

		@Override
		public void onSuccess(Object content, int reqId) {
			if(content instanceof CoauthorPaser) {
				CoauthorPaser paser = ((CoauthorPaser)content); 
				if(type == type_in_hospital) {
					coauthorInHospital = paser.getCoauthorList();
					System.out.println("in   "+coauthorInHospital.size());
					map.put("in", coauthorInHospital);
				} else if(type == type_out_hospital) {
					coauthorOutHospital = paser.getCoauthorList();
					System.out.println("in   "+coauthorOutHospital.size());
					map.put("out", coauthorOutHospital);
				}
				loadLstData(map);
			}
			super.onSuccess(content, reqId);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		for(Integer i:groupList) {
			System.out.println("groupList     "+i);
		}
		System.out.println("arg2      *   "+arg2);
		if(!groupList.contains(arg2)) {
			//TODO classCastException
			Object obj = adapter.getItem(arg2);
			if(obj instanceof CoauthorEntity) {
				CoauthorEntity en = (CoauthorEntity)obj;
				Intent intent = new Intent(this, CategoryArticlesActivity.class);
				intent.putExtra(CategoryArticlesActivity.CATEGORY_ARTICLES_TYPE, CategoryArticlesActivity.TYPE_COAUTHORS_ARTICLES);
				intent.putExtra(CategoryArticlesActivity.COAUTHORS_NAME, en.getCoauthorName());
				/*传递的ID 不应该总是第一次默认的医生ID， 而应该是动态的    被Terry修改*/
//				intent.putExtra(CategoryArticlesActivity.DOCTOR_ID, application.getLogin().getDoctorId());
				/*上一条代码修改后的代码，就是要获取当前圈子对应医生的ID update by Terry*/
				intent.putExtra(CategoryArticlesActivity.DOCTOR_ID, en.getDoctorID());
				intent.putExtra(CategoryArticlesActivity.COAUTHORS_ID, en.getCustomerID());
				System.out.println("application.getLogin().getDoctorId()    "+application.getLogin().getDoctorId());
				System.out.println("application.getLogin().getCustomerID()    "+en.getCustomerID());
				startActivity(intent);
			}
			
		}
		
	}
}

