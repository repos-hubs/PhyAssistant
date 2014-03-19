package com.jibo.activity;
import java.util.ArrayList;
import java.util.Properties;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.data.NewsListPaser;
import com.jibo.data.VersionDataParser;
import com.jibo.data.entity.DrugAlertEntity;
import com.jibo.data.entity.NewsEntity;
import com.jibo.dbhelper.DrugAlertSQLAdapter;
import com.jibo.net.BaseResponseHandler;
/**
 * @author peter.pan
 *
 */
public class GBAppMainActivity extends BaseActivity {
	private DrugAlertSQLAdapter dbAdapter ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//TODO
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        baseHandler =new BaseResponseHandler(this);
        Click click =new Click();
        findViewById(R.id.btn_req).setOnClickListener(click);
        findViewById(R.id.btn_quit).setOnClickListener(click);
        findViewById(R.id.btn_dialog).setOnClickListener(click);
        
        dbAdapter = new DrugAlertSQLAdapter(this);
        
    	t1 =(TextView)findViewById(R.id.tx_action);
		t2 =(TextView)findViewById(R.id.tx_dbxml);
    }
    public BaseResponseHandler baseHandler;
     class Click implements View.OnClickListener
    {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id =v.getId();
			if(id == R.id.btn_req)
			{
				
				Properties propertyInfo =new Properties();
				propertyInfo.put(SoapRes.KEY_D_VERSION, "11");
				propertyInfo.put(SoapRes.KEY_P_VERSION, "1.05");
				sendRequest(SoapRes.URLVersion, SoapRes.REQ_ID_VERSION,
						propertyInfo,
						baseHandler);
			}
			else if(id == R.id.btn_dialog)
			{
				showDialog(DialogRes.DIALOG_ID_DOWNLOAD_FAILED);
			}
			else if (id == R.id.btn_quit)
			{
//				finish();
				//用药安全测试
				ArrayList<DrugAlertEntity> drugs = dbAdapter.getDrugAlertsByLocalDatabase("");// 检查本地缓存
				if (null != drugs && drugs.size() > 0){
					Intent intent = new Intent(GBAppMainActivity.this,DrugAlertsActivity.class);
					intent.putParcelableArrayListExtra("list", drugs);
					intent.putExtra("isLoadLocal", true);
					startActivity(intent);
				}
				else{
			 		Properties propertyInfo = new Properties();
			 		propertyInfo.put(SoapRes.KEY_DRUGALERT_SOURCE, "");
			 		sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_GET_DRUGALERT_TOP_20, propertyInfo,
			 				new BaseResponseHandler(GBAppMainActivity.this));
				}
				
//				//行业新闻测试
//				ArrayList<NewsEntity> drugs = MySqlite.getTopNewsByLocalDatabase(GBAppMainActivity.this);// 检查本地缓存
//				if (null != drugs && drugs.size() > 0){
//					Intent intent = new Intent(GBAppMainActivity.this,DrugAlertsActivity.class);
//					intent.putParcelableArrayListExtra("list", drugs);
//					intent.putExtra("isLoadLocal", true);
//					startActivity(intent);
//				}
//				else{
//					
//					sendRequest(SoapRes.URLNews,
//							SoapRes.REQ_ID_GET_NEWS_TOP_20, null,
//							new BaseResponseHandler(GBAppMainActivity.this));
//				}
			}
		}
    }
     
     
     TextView t1;
     TextView t2;
	@Override
	public void onReqResponse(Object o,int methodId) {
		// TODO Auto-generated method stub
		if(o!=null)
		{
		
			if(o instanceof VersionDataParser)
			{
				VersionDataParser vd =(VersionDataParser)o;
				t1.setText("action="+vd.action);
				t2.setText("DBXML size="+vd.list.size()+" "+vd.list.get(0).getAppURL());
			}
//			else if (o instanceof DrugAlertListDataTop20Paser) {// 服务器最新20条
//				DrugAlertListDataTop20Paser data = (DrugAlertListDataTop20Paser) o;
//				ArrayList<DrugAlertEntity> list = data.getList();
//				Intent intent = new Intent(GBAppMainActivity.this,DrugAlertsActivity.class);
//				intent.putParcelableArrayListExtra("list", list);
//				intent.putExtra("isLoadLocal", false);
//				startActivity(intent);
//			}
			else if (o instanceof NewsListPaser) {// 服务器最新20条
//				NewsListPaser data = (NewsListPaser) o;
//				ArrayList<NewsEntity> list = data.getList();
//				Intent intent = new Intent(GBAppMainActivity.this,NewsActivity.class);
//				intent.putParcelableArrayListExtra("list", list);
//				intent.putExtra("isLoadLocal", false);
//				startActivity(intent);
			}
		}
	}
	
	
	
	@Override
	public void onCancelDialog(int dialogId) {
		// TODO Auto-generated method stub
		System.out.println("cancel dialog id="+dialogId);
		super.onCancelDialog(dialogId);
		
	}



	@Override
	public void clickPositiveButton(int dialogId) {
		// TODO Auto-generated method stub
		
		t1.setText("按了左键"+dialogId);
		super.clickPositiveButton(dialogId);
	}

	

	@Override
	public void clickNegativeButton(int dialogId) {
		// TODO Auto-generated method stub
		t1.setText("按了右键"+dialogId);
		super.clickNegativeButton(dialogId);
	}



	@Override
	public void clickNeutralButton(int dialogId) {
		super.clickNeutralButton(dialogId);
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		GBApplication.gbapp.quit();
	}
     
}