package com.jibo.activity;



import java.util.Map.Entry;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.NewsGetInfoByIdPaser;
import com.jibo.data.entity.NewsEntity;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.ui.TextField;
import com.umeng.analytics.MobclickAgent;
import com.api.android.GBApp.R;


/**
 * 
 * 行业新闻详细界面
 * 
 * @author simon
 * 
 */

public class RelatedNewsActivity extends BaseSearchActivity implements OnClickListener, OnTouchListener, OnGestureListener {

//	private static final String TAG = "RelatedNews";
	
	private RelativeLayout rltRelatedNewsDetailsLayout;
	private LinearLayout layout;
	private TextView tvTitle;
	private Button collectBtn = null;
	
	private Context context;
	private String nId;
	private String title;
	private String date;
	private Spanned content;
	public static String sharing_inf = "";
	private final String source = "http://www.jibo.cn/url.asp?p=";
	
	private NewsEntity news;

	private FavoritDataAdapter fData;
	private HistoryAdapter historyAdapter;
	private GBApplication app;
	
	private ScrollView newsDetailScroll = null;
	private GestureDetector returnGesture = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.related_news);
		super.onCreate(savedInstanceState);
		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");
		fData = new FavoritDataAdapter(this);
		collectBtn = (Button) findViewById(R.id.favoritBtn);
		collectBtn.setOnClickListener(this);
		app = (GBApplication) getApplication();
		rltRelatedNewsDetailsLayout = (RelativeLayout) findViewById(R.id.RelatedNewsDetailsLayout);
		newsDetailScroll = (ScrollView) findViewById(R.id.RelatedNewsDetailsScrollView);
		layout = (LinearLayout) findViewById(R.id.dialogprogress);
		context = this;
		tvTitle = (TextView) findViewById(R.id.txt_header_title);
		collectBtn.setOnClickListener(this);

		tvTitle.setText(R.string.news);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String id = null;
		if (extras != null) {
			news = extras.getParcelable("news");
			id = extras.getString("id");
			
		}
		
		returnGesture = new GestureDetector(this);
		newsDetailScroll.setOnTouchListener(this);
		newsDetailScroll.setLongClickable(true);
		
		nId = id==null?news.getId():id;
		
		if(id!=null){//历史界面跳转
			rltRelatedNewsDetailsLayout.setVisibility(LinearLayout.GONE);
			Properties info = new Properties();
			info.put("newsId", id);
			sendRequest(SoapRes.URLIMAGENews,
					SoapRes.REQ_ID_GET_NEWSDETAIL_BY_ID, info,
					new BaseResponseHandler(this,false));
		}else{
		      showNewsDetails();
		}
		if(fData.selectNewsCollection(context, nId,SharedPreferencesMgr.getUserName())>0)
		{
			Log.e("nId",nId);
			collectBtn.setBackgroundResource(R.drawable.btnunchg);
		}
		setPopupWindowType(Constant.MENU_TYPE_6);
		MobclickAgent.onEvent(context,"NewsId",nId,1);

		uploadLoginLogNew("News", nId,"NewsId", null);
	}
	

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			if (o instanceof NewsGetInfoByIdPaser) {// 获取更新，最新20条
				
				news = ((NewsGetInfoByIdPaser)o).getEntity();
				rltRelatedNewsDetailsLayout.setVisibility(LinearLayout.VISIBLE);
				showNewsDetails();

			}
		}
	}


	private void addText(String text, LinearLayout layout) {
		TextField textView = new TextField(this, null, text);
		int Width = 0;
		int Height = 0;
		Width = getWindowManager().getDefaultDisplay().getWidth();
		Height = getWindowManager().getDefaultDisplay().getHeight();

		if ((Width * Height) <= (320 * 480)) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
		} else {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25);
		}
		textView.setText(text);
		textView.setTextColor(Color.TRANSPARENT);
		layout.addView(textView);
	}

	private void showNewsDetails() {
		TextView newsTitleContent = (TextView) findViewById(R.id.NewsTitleContent);
		TextView newsContentTitle = (TextView) findViewById(R.id.NewsContentTitle);
		// TextView newsContent = (TextView) findViewById(R.id.NewsContent);
		LinearLayout newsContent = (LinearLayout) findViewById(R.id.NewsContent);
		TextView newsSourceTitle = (TextView) findViewById(R.id.NewsSourceTitle);
		TextView newsSource = (TextView) findViewById(R.id.NewsSource);
		TextView headTitle = (TextView) findViewById(R.id.txt_header_title);
		Button drugAlertDetailLink = (Button) findViewById(R.id.NewsToDrugAlertDetailButton);
		headTitle.setText(getString(R.string.news));
		
		if(null!=news.getNewSource()&&!"".equals(news.getNewSource())){//期刊来源
			LinearLayout magazineSourceLayout = (LinearLayout) findViewById(R.id.NewsMagazineSourceLayout);
			magazineSourceLayout.setVisibility(View.VISIBLE);
			TextView magazineSource = (TextView) findViewById(R.id.NewsMagazineSource);
			magazineSource.setText(news.getNewSource());
		}
		
		layout.setVisibility(View.GONE);
		rltRelatedNewsDetailsLayout.setVisibility(LinearLayout.VISIBLE);

		/*
		 * if(!isMainPage) //prime 2011-4-12. {
		 * //DrugData.FillNewsDetailsDataForTime(id, 0); }
		 */

		String text;
		text = news.getTitle();
		newsTitleContent.setText(text);
		title = text;
		text = getString(R.string.whole_body) + getString(R.string.colon);
		newsContentTitle.setText(text);
		text = news.getContent();

		final String typeID = news.getTypeID();
		if (null != typeID && !"".equals(typeID.trim()) && !"null".equals(typeID.trim())) {
			drugAlertDetailLink.setVisibility(View.VISIBLE);
			drugAlertDetailLink.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//跳转到用药安全
					Intent intent = new Intent(RelatedNewsActivity.this,DrugAlertsDetailActivity.class);
					intent.putExtra("typeID", typeID);
					startActivity(intent);
				}
			});
		}
		if(text == null) return;
		content = Html.fromHtml(text);
		System.out.println("text   " + text);
		if (text.length() > 0) {
			addText(text, newsContent);
		}
		text = getString(R.string.link_to_source) + getString(R.string.colon);
		newsSourceTitle.setText(text);
		text = news.getSource();
		newsSource.setText(text);
		
		String title = news.getTitle();
		int id1 = Integer.parseInt(news.getId());
		int colID = -1;
		for(Entry<?, ?> en:app.getPdaColumnMap().entrySet()) {
			if(en.getValue().toString().equals(getString(R.string.news))) {
				colID = (Integer) en.getKey();
			}
		}
		historyAdapter.storeViewHistory(app.getLogin().getGbUserName(), id1, colID, -1, title);
	
		
		
		if (content.length() > 20)// xinwen
		{
			sharing_inf = context.getString(R.string.newstitleMenu)
					+ " " + title + ":"
					+ content.toString().substring(0, 20) + "...\n"
					+ source + "40." + nId + "." + SharedPreferencesMgr.getUserID() + ".";
			Log.e("sharing_inf", sharing_inf);
		} else {
			sharing_inf = context.getString(R.string.newstitleMenu)
					+ " " + title + ":" + content + "...\n"
					+ source + "40." + nId + "." + SharedPreferencesMgr.getUserID() + ".";

		}//news
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("menu");// 必须创建一项

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.favoritBtn:
			MobclickAgent.onEvent(context,"Favorite","NewsFavoritBtn",1);
			
			uploadLoginLogNew("News", "NewsFavoritBtn", "Favorite",  null);
			if(fData.selectNewsCollection(context, nId,SharedPreferencesMgr.getUserName())>0)
			{
				if(fData.delNewsCollection( nId,SharedPreferencesMgr.getUserName()));
				{
					if (fData.delNewsCollection(nId,SharedPreferencesMgr.getUserName())) {
						Toast toast = Toast.makeText(context,
								context.getString(R.string.cancelFav),
								Toast.LENGTH_LONG);
						toast.setGravity(Gravity.TOP, 0, 220);
						toast.show();
						collectBtn.setBackgroundResource(R.drawable.btnchg);
					}

				}

			} else {
				Toast toast = Toast
						.makeText(context,
								context.getString(R.string.favorite),
								Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 220);
				toast.show();
				fData.insertTableNewsCollection( nId, title, date,SharedPreferencesMgr.getUserName());
				// btnFlag=true;
				collectBtn.setBackgroundResource(R.drawable.btnunchg);
			}

			break;
		}

	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		returnGesture.onTouchEvent(event);
		return false;
	}


	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e2.getX() - e1.getX() > 200
				&& Math.abs(velocityX) > 300) {
			// Fling left
			GBApplication.gbapp.setHomeLaunched(false);
			GBApplication.gbapp.setStartActivity(true);
			this.finish();
		}
		return false;
	}


	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
