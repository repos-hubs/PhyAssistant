package com.jibo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.data.DrugAcademicPaser;
import com.jibo.data.entity.AcademicEntity;
import com.jibo.net.AsyncSoapResponseHandler;


public class DrugReferenceAcademicActivity extends BaseSearchActivity implements
		 OnItemClickListener,OnScrollListener {

	private static final String TAG = "DrugReferenceAcademicActivity";
	private static final String COLUMN_TEXT_01 = "title";
	private static final String COLUMN_TEXT_02 = "author";
	private static final String COLUMN_TEXT_03 = "journal_name";
	String did = "";
	boolean flag=true;
    int pageNum=0;
	SimpleAdapter adapter=null;

	private ListView mList;
	private Intent mIntent;
//	private LinearLayout layout;

	ArrayList<HashMap<String, String>> arrayList=new ArrayList<HashMap<String, String>>();
	ArrayList<String>   alId=new ArrayList<String>();
	private View footerView;
	// prime 2011-4-15
	
    public String drugName;
    
	AcademicEntity drugAcademicActivity;
	/** Called when the activity is first created. */
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drug_reference_academic_activity);
		super.onCreate(savedInstanceState);
		mIntent = getIntent();
		Bundle extras = mIntent.getExtras();
		/*用于存放药品信息中点击学术活动后下一个Activity的标题，add by Terry*/
		String myTitle = "";
		if (extras != null) {
			did= extras.getString("id");
			drugName  = extras.getString("drugname");
			myTitle = extras.getString("title");
		}
		TextView txtTitle = (TextView)findViewById(R.id.txt_header_title);
		if (null != myTitle){
			txtTitle.setText(myTitle);
		}else {
			txtTitle.setText(getString(R.string.drug_reference));
		}
		
		footerView = View.inflate(this,  
				R.layout.dialogprogress, null);  
			mList = (ListView) findViewById(R.id.AcademicActivityList);

//		layout = (LinearLayout) findViewById(R.id.dialogprogress);

		
		mList.setOnItemClickListener(this);
		adapter = new SimpleAdapter(this,arrayList,
				R.layout.list_item_vertical_text_text_text, new String[] {
						COLUMN_TEXT_01, COLUMN_TEXT_02, COLUMN_TEXT_03 },
				new int[] { R.id.ListText1, R.id.ListText2, R.id.ListText3 });
		
		mList.addFooterView(footerView);
		mList.setAdapter(adapter);
		
		mList.setOnScrollListener(this);
		
		showDrugName();
		runThread();
	}

	/**
	 * 显示药品名称
	 * */
	private void showDrugName() {
		TextView drugNameCn = (TextView) findViewById(R.id.DrugReferDetailsDrugNameCn);
		drugNameCn.setText(drugName);
	}

	/**
	 * 获取最新的学术活动
	 * */
	public synchronized void runThread() {
		pageNum++;	
		Properties propertyInfo = new Properties();
		propertyInfo.put("drugId", did);
		propertyInfo.put("pageSize",10);
		propertyInfo.put("pageNum",pageNum);	
		sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_DRUG_ACADEMIC,
				propertyInfo, new SoapCallBack());
	}

	private ArrayList<HashMap<String, String>> addItem() {
		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		String title, author, journalName;

		for (int i = 0; i < drugAcademicActivity.iRecordLength; i++) {
			title = getString(R.string.title) + getString(R.string.colon)
					+ getString(R.string.space)
					+ drugAcademicActivity.title.get(i);
			author = getString(R.string.author) + getString(R.string.colon)
					+ getString(R.string.space)
					+ drugAcademicActivity.authors.get(i);
			journalName = getString(R.string.journal_name)
					+ getString(R.string.colon) + getString(R.string.space)
					+ drugAcademicActivity.journalName.get(i);

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(COLUMN_TEXT_01, title);
			map.put(COLUMN_TEXT_02, author);
			map.put(COLUMN_TEXT_03, journalName);
			listItem.add(map);
		}

		return listItem;
	}
	

	/**
	 * 显示学术活动列表
	 * */
	private void showListView(ListView list,
			ArrayList<HashMap<String, String>> item) {
		ArrayList<HashMap<String, String>> display;
		display = new ArrayList<HashMap<String, String>>();
		display = item;
		for(int i=0;i<drugAcademicActivity.id.size();i++)
		{
			alId.add(drugAcademicActivity.id.get(i));
		}

        arrayList.addAll(display);
        adapter.notifyDataSetChanged();
	}
	/**
	 * 显示学术活动列表
	 * */
	private void showAcademicActivityList(String drugId) {
		// DrugData.FillDrugsAcademicActivityDataForTime(drugId, 0);
//		layout.setVisibility(View.GONE);
		showListView(mList, addItem());
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(position<alId.size()&&alId.get(position)!=null)
		{
			Intent intent = new Intent(this, RelatedArticlesActivity.class);
			intent.putExtra(RelatedArticlesActivity.ARTICLES_TYPE,
					getString(R.string.drug_reference));
			//点击跳转
			intent.putExtra("id",alId.get(position));
			startActivity(intent);
		}

	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	class SoapCallBack extends AsyncSoapResponseHandler {
		

		public void onStart() {
		}
		public void onSuccess(Object content, int methodId) 
		{
			if (methodId == SoapRes.REQ_ID_DRUG_ACADEMIC) {				
				drugAcademicActivity = ((DrugAcademicPaser) content).academicActivities;
				if(drugAcademicActivity.iRecordLength<10)
				{
					mList.removeFooterView(footerView);
				}
				else
				{
					flag=true;
					showAcademicActivityList(did);
				}                                                               

			}
		}
		public void onFinish() {
		}

		public void onFailure(Throwable error, String content) {
           //show err
			showDialog(DialogRes.DIALOG_ERROR_PROMPT);
			mList.removeFooterView(footerView);
		}
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(view.getLastVisiblePosition() == view.getCount()-1){
			if(flag){
				flag = false;
				runThread();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}
}
