package com.jibo.ui;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.api.android.GBApp.R;
import com.jibo.adapter.ViewHistoryAdapter;
import com.jibo.common.Util;
import com.jibo.dbhelper.HistoryAdapter;

public class ViewHistory extends View implements OnClickListener, OnTouchListener {


	private ListView lvToday;
	private ListView lvYesterday;
	private ListView lvThisWeek;
	private ListView lvThisMonth;
	private ListView lvOlder;
	
	private ImageView imgToday;
	private ImageView imgYesterday;
	private ImageView imgThisWeek;
	private ImageView imgThisMonth;
	private ImageView imgOlder;
	
	public static final int VIEW_ACTION_TODAY = 0;
	public static final int VIEW_ACTION_YESTERDAY = 1;
	public static final int VIEW_ACTION_WEEK = 2;
	public static final int VIEW_ACTION_MONTH = 3;
	public static final int VIEW_ACTION_OLDER = 4;
//	private ImageButton imgbtnSync;
	
	private RelativeLayout rltToday;
	private RelativeLayout rltYesterday;
	private RelativeLayout rltThisWeek;
	private RelativeLayout rltThisMonth;
	private RelativeLayout rltOlder;
	
	private ViewHistoryAdapter adapter;
	private long time;
	private int mMonth;
	private int mDate;
	private View mainView;
	private ProgressDialog progressDialog;
	private Context mContext;
	private final static int SYNC_COMPLETED = 0x1234567;
	private HistoryAdapter historyAdapter;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case SYNC_COMPLETED:
				adapter.notifyDataSetChanged();
				progressDialog.cancel();
				
				break;
			}
			super.handleMessage(msg);
		}
	};
	public ViewHistory(Context context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(mContext);
        mainView = inflater.inflate(R.layout.view_history, null);
        inits();
	}
	

	public View getView() {
		return mainView;
	}
	
	private void inits() {
		historyAdapter = new HistoryAdapter(mContext, 1, "mysqllite.db");
		lvToday = (ListView) mainView.findViewById(R.id.lv_today);
		lvYesterday = (ListView) mainView.findViewById(R.id.lv_yesterday);
		lvThisWeek = (ListView) mainView.findViewById(R.id.lv_this_week);
		lvThisMonth = (ListView) mainView.findViewById(R.id.lv_this_month);
		lvOlder = (ListView) mainView.findViewById(R.id.lv_older);
		
		time=System.currentTimeMillis();
        Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDate = mCalendar.get(Calendar.DATE);
        
		rltToday = (RelativeLayout) mainView.findViewById(R.id.rlt_today);
		rltYesterday = (RelativeLayout) mainView.findViewById(R.id.rlt_yesterday);
		rltThisWeek = (RelativeLayout) mainView.findViewById(R.id.rlt_this_week);
		rltThisMonth = (RelativeLayout) mainView.findViewById(R.id.rlt_this_month);
		rltOlder = (RelativeLayout) mainView.findViewById(R.id.rlt_older);
		
		imgToday = (ImageView) mainView.findViewById(R.id.img_today);
		imgYesterday = (ImageView) mainView.findViewById(R.id.img_yesterday);
		imgThisWeek = (ImageView) mainView.findViewById(R.id.img_this_week);
		imgThisMonth = (ImageView) mainView.findViewById(R.id.img_this_month);
		imgOlder = (ImageView) mainView.findViewById(R.id.img_older);
		
		rltToday.setOnClickListener(this);
		rltYesterday.setOnClickListener(this);
		rltThisWeek.setOnClickListener(this);
		rltThisMonth.setOnClickListener(this);
		rltOlder.setOnClickListener(this);
	}

	public void onClick(View v) {
		showAndHide(v);
		switch(v.getId()) {
		case R.id.rlt_today:
			adapter = new ViewHistoryAdapter(mContext, historyAdapter.getViewHistory(), VIEW_ACTION_TODAY);
			lvToday.setAdapter(adapter);
			break;
		case R.id.rlt_yesterday:
			adapter = new ViewHistoryAdapter(mContext, historyAdapter.getViewHistory(), VIEW_ACTION_YESTERDAY);
			lvYesterday.setAdapter(adapter);
			break;
		case R.id.rlt_this_week:
			adapter = new ViewHistoryAdapter(mContext, historyAdapter.getViewHistory(), VIEW_ACTION_WEEK);
			lvThisWeek.setAdapter(adapter);
			break;
		case R.id.rlt_this_month:
			adapter = new ViewHistoryAdapter(mContext, historyAdapter.getViewHistory(), VIEW_ACTION_MONTH);
			lvThisMonth.setAdapter(adapter);
			break;
		case R.id.rlt_older:
			adapter = new ViewHistoryAdapter(mContext, historyAdapter.getViewHistory(), VIEW_ACTION_OLDER);
			lvOlder.setAdapter(adapter);
			break;
//		case R.id.imgbtn_sync:
//			showWaitingDialog();
//			new requestInternet().start();
//			break;
		}
	}
	
	
	public void showAndHide(View v) {
		RelativeLayout rltList[] = {rltToday, rltYesterday, rltThisWeek
				,rltThisMonth, rltOlder};
		ListView lvList[] = {lvToday, lvYesterday, lvThisWeek
				,lvThisMonth, lvOlder};
		ImageView imgList[] = {imgToday, imgYesterday, imgThisWeek
				,imgThisMonth, imgOlder};
		for(int i=0; i<rltList.length; i++) {
			if(rltList[i] == v) {
				if(lvList[i].getVisibility() == View.GONE) {
					imgList[i].setBackgroundResource(R.drawable.lv_click);
					lvList[i].setVisibility(View.VISIBLE);
				} else {
					imgList[i].setBackgroundResource(R.drawable.lv_normal);
					lvList[i].setVisibility(View.GONE);
				}
			} else {
				lvList[i].setVisibility(View.GONE);
				imgList[i].setBackgroundResource(R.drawable.lv_normal);
			}
		}
	}
    /**
     * @param a
     * @return Judge if the colid is effective
     */
    public boolean isContainColId(int a) {
    	//TODO
    	boolean result = false;
//    	SQLiteDatabase dataBaseDBAColumn = SQLiteDatabase.openOrCreateDatabase(
//						"/data/data/com.api.android.GBApp/databases/"
//								+ Util.TABLE_PDA_COLUMN, null);
//    	String str = "select * from "+ Util.TABLE_PDA_COLUMN +" where colID ="+a;
//    	Cursor cursor = dataBaseDBAColumn.rawQuery(str, null);
//    	System.out.println("cursor$$$$$$  "+cursor.getCount());
    	ArrayList<Integer> lst = new ArrayList<Integer>();
    	lst.add(31);
    	lst.add(32);
    	lst.add(40);
    	lst.add(41);
    	lst.add(42);
    	lst.add(123);
    	if(lst.contains(a)) {
    		result = true;
    	}
    	return result;
    }
	public void showWaitingDialog() {
		progressDialog = ProgressDialog.show(mContext,
				mContext.getResources().getString(R.string.synchronize_title),
				mContext.getResources().getString(R.string.synchronize_message), true, false);  
		progressDialog.show();
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
//		if(event.getAction()==MotionEvent.ACTION_DOWN ||event.getAction()==MotionEvent.ACTION_MOVE) {
//			switch(v.getId()) {
////			case R.id.imgbtn_sync:
////				imgbtnSync.setBackgroundResource(R.drawable.sync);
//				break;
//			}
//		} else {
//			switch(v.getId()) {
////			case R.id.imgbtn_sync:
////				imgbtnSync.setBackgroundResource(R.drawable.sync_hover);
//				break;
//			}
//		}
		return false;
	}
	
}
