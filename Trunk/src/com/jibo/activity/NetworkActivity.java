package com.jibo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.data.ProfilePaser;
import com.jibo.data.entity.ProfileEntity;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.net.BaseResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class NetworkActivity extends BaseSearchActivity implements OnItemClickListener{
	private ListView mNetworkList;
	private ArrayList<HashMap<String, String>> display;
	private RequestHandler baseHandler;
	private GBApplication app;

	private static final String COLUMN_TEXT_01 = "title";
	private static final String COLUMN_TEXT_02 = "number";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.network);
		super.onCreate(savedInstanceState);
		inits();
	}

	public void inits() {
		app = (GBApplication) getApplication();
		display = new ArrayList<HashMap<String, String>>();
		baseHandler = new RequestHandler();
		mNetworkList = (ListView) findViewById(R.id.NetworkList);
		TextView txtTitle = (TextView) findViewById(R.id.txt_header_title);
		txtTitle.setText(getString(R.string.network));
		mNetworkList.setOnItemClickListener(this);
		
		Properties propertyInfo = new Properties();
		System.out.println("doctorId   "+app.getLogin()
				.getDoctorId());
		propertyInfo.put(SoapRes.KEY_GETPROFILE_DOCTORID, app.getLogin()
				.getDoctorId());
		propertyInfo.put(SoapRes.KEY_GETPROFILE_USERNAME, app.getLogin()
				.getGbUserName());
		sendRequest(SoapRes.URLProfile, SoapRes.REQ_ID_GET_PROFILE,
				propertyInfo, baseHandler);

		setPopupWindowType(Constant.MENU_TYPE_4);
	}

	private class RequestHandler extends AsyncSoapResponseHandler {
		@Override
		public void onStart() {
			showDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
			super.onStart();
		}
		@Override
		public void onFailure(Throwable error, String content) {
			removeDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
			showDialog(DialogRes.DIALOG_ID_NO_LISENCE);
			super.onFailure(error, content);
		}
		@Override
		public void onSuccess(Object o) {
			removeDialog(DialogRes.DIALOG_WAITING_FOR_DATA);
			super.onSuccess(o);
			if (o != null) {
				if (o instanceof ProfilePaser) {
					ProfilePaser vd = (ProfilePaser) o;
					ProfileEntity en = vd.getEntity();

					if(en != null) {
						HashMap<String, String> map1 = new HashMap<String, String>();
						map1.put(COLUMN_TEXT_01, getString(R.string.coauthor)
								+ getString(R.string.colon));
						map1.put(COLUMN_TEXT_02, en.getCoauthorCount());
						display.add(map1);
						SimpleAdapter adapter = new SimpleAdapter(NetworkActivity.this, display,
								R.layout.list_item_text_text_icon, new String[] {
										COLUMN_TEXT_01, COLUMN_TEXT_02 }, new int[] {
										R.id.ListText1, R.id.ListText2 });
						mNetworkList.setAdapter(adapter);
					} else {
						showDialog(DialogRes.DIALOG_ID_NO_LISENCE);
					}
					
				}
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = null;
		intent = new Intent(this, NetworkCoauthorActivity.class);
		intent.putExtra("doctorID", app.getLogin().getDoctorId() );
		startActivity(intent);
	}
	@Override
	public void clickPositiveButton(int dialogId) {
		Intent intent = null;
		switch(dialogId) {
		case DialogRes.DIALOG_ID_NO_LISENCE:
			intent = new Intent();
			intent.setClass(this, Registration_updateActivity.class);
			intent.putExtra("isModify", true);
			startActivity(intent);
			break;
		}
		super.clickPositiveButton(dialogId);
	}
}
