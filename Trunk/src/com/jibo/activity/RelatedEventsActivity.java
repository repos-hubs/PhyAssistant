package com.jibo.activity;

import java.text.ParseException;
import com.api.android.GBApp.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jibo.asynctask.uploadDataNew;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.EventInfParse;
import com.jibo.data.entity.RelatedBeans;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.net.BaseResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class RelatedEventsActivity extends BaseSearchActivity implements
		OnClickListener {

	private static final String TAG = "RelatedEvents";
	RelatedBeans rb = new RelatedBeans();;
	public static final String ARTICLES_TYPE = "articles_type";
	public static final String LOGIN_FLAG = "login_flag";
	public static final String USER_NAME = "USER_NAME";
	public static final int Sharing = Menu.FIRST + 0;
	public static final int ExitMenu = Menu.FIRST + 1;
	public static final int favorite = Menu.FIRST + 2;
	private ImageButton mSearch;
	private ImageButton mSearchSelect;
	private AutoCompleteTextView mSearchEdit;
	private ImageButton mHome;
	private Button attendBtn;
	private String eventName;
	private String eventDate;
	private String pl;
	String content;
	String title;
	String source = "http://www.jibo.cn/url.asp?p=";
	SharedPreferences setting;
	private String eventSource = "";
	private String username;
//	private LinearLayout layout;
	private Handler handler;
	ProgressDialog progressDialog = null;
	Context context = null;
	/* global variables */
	private int mSearchIndex;
	Handler newhandler = null;
	// prime 2011-4-15
	private static final String CHECKITEM = "GBACHECKITEM21";
	int pos = -1;
	Button collectBtn;
	TextView tvTitle;

	public static String sharing_inf = "";
	public BaseResponseHandler baseHandler;
	private FavoritDataAdapter eveAdpt;
	private String id;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.related_events);
		super.onCreate(savedInstanceState);
		baseHandler = new BaseResponseHandler(this);
		eveAdpt = new FavoritDataAdapter(this);
		// collectBtn.setVisibility(View.GONE);
//		layout = (LinearLayout) findViewById(R.id.dialogprogress);
		collectBtn = (Button) findViewById(R.id.favoritBtn);
		attendBtn = (Button) findViewById(R.id.attendBtn);
		Display display = getWindowManager().getDefaultDisplay();
		TextView txtSubTitle = (TextView) findViewById(R.id.txt_header_title);
		txtSubTitle.setText(getString(R.string.event));
		txtSubTitle.setTextColor(Color.WHITE);
		attendBtn.setOnClickListener(this);
		context = this;
		collectBtn.setOnClickListener(this);
		setting = getSharedPreferences(LOGIN_FLAG, 0);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		MobclickAgent.onError(this);
		if (extras != null) {
			id = extras.getString(Constant.ID);
//			pos = extras.getInt("position");

			Properties propertyInfo = new Properties();
			propertyInfo.put(SoapRes.KEY_EVENT_ID, id);
			sendRequest(SoapRes.URLEvent, SoapRes.REQ_ID_EVENT_INF,
					propertyInfo, baseHandler);
		}
		if (eveAdpt.selectEventCollection(context, ""+id, SharedPreferencesMgr.getUserName()) > 0) {// 查看是否收藏过、如果收藏过就改变收藏图标的颜色
			collectBtn.setBackgroundResource(R.drawable.btnunchg);
		}
		setPopupWindowType(Constant.MENU_TYPE_6);// 添加menu
	}

	protected void onResume() {
		super.onResume();
	}

	private void showEventsDetails(String id) {
		TextView name = (TextView) findViewById(R.id.EventName);
		TextView dateTitle = (TextView) findViewById(R.id.EventVenueTitle);
		TextView date = (TextView) findViewById(R.id.EventVenue);
		TextView placeTitle = (TextView) findViewById(R.id.EventDateTitle);
		TextView place = (TextView) findViewById(R.id.EventDate);
		TextView introductionTitle = (TextView) findViewById(R.id.EventIntroductionTitle);
		// LinearLayout EventIntroduction = (LinearLayout)
		// findViewById(R.id.EventIntroduction);
		TextView EventIntroduct = (TextView) findViewById(R.id.EventIntroduction);
		TextView organizerTitle = (TextView) findViewById(R.id.EventOrganizerTitle);
		TextView organizer = (TextView) findViewById(R.id.EventOrganizer);
		TextView telTitle = (TextView) findViewById(R.id.EventTelTitle);
		TextView tel = (TextView) findViewById(R.id.EventTel);
		TextView faxTitle = (TextView) findViewById(R.id.EventFaxTitle);
		TextView fax = (TextView) findViewById(R.id.EventFax);
		TextView emailTitle = (TextView) findViewById(R.id.EventEmailTitle);
		TextView email = (TextView) findViewById(R.id.EventEmail);
		TextView websiteTitle = (TextView) findViewById(R.id.EventWebsiteSourceTitle);
		TextView website = (TextView) findViewById(R.id.EventWebsiteSource);
//		layout.setVisibility(View.GONE);

		String text;

		text = getString(R.string.event_name) + getString(R.string.colon);
		if (rb.getName() != null) {
			text = rb.getName();
		}

		eventName = text;
		name.setText(text);
		title = text;

		text = getString(R.string.event_date) + getString(R.string.colon);
		dateTitle.setText(text);
		if (rb.getEventDate() != null) {
			text = rb.getEventDate();
		}
		eventDate = text;
		String eventdt =null ;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			eventdt = sdf.format(sdf.parse(eventDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// if(d.after(eventdt))
		// {
		// //TODO
		attendBtn.setVisibility(View.GONE);
		// }
        
		date.setText(" " + eventdt);
		
		
		
		text = getString(R.string.venue) + getString(R.string.colon);
		placeTitle.setText(text);
		if (rb.getPlace() != null) {
			text = rb.getPlace();
		}
		pl = text;
		place.setText(" " + text);
		text = getString(R.string.introduction) + getString(R.string.colon);
		introductionTitle.setText(text);
		if (rb.getIntroduction() != null) {
			text = rb.getIntroduction();
		}
		System.out.println("text   " + text);
		// TextField textView=new TextField(this, null, text);
		// TextView textView=new TextView(context);
		// textView.setText(" "+text);
		// textView.setTextColor(Color.TRANSPARENT);
		// EventIntroduction.addView(textView);
		EventIntroduct.setText(text);
		content = text;
		text = getString(R.string.Organizer) + getString(R.string.colon);
		organizerTitle.setText(text);
		if (rb.getOrganizer() != null) {
			text = rb.getOrganizer();
		}

		organizer.setText(" " + text);
		text = getString(R.string.tel) + getString(R.string.colon);
		telTitle.setText(text);
		if (rb.getTel() != null) {
			text = rb.getTel();
		}

		tel.setText(" " + text);
		text = getString(R.string.fax) + getString(R.string.colon);
		faxTitle.setText(text);
		if (rb.getFax() != null) {
			text = rb.getFax();
		}

		fax.setText(" " + text);
		text = getString(R.string.mail) + getString(R.string.colon);
		emailTitle.setText(text);
		if (rb.getEmail() != null) {
			text = rb.getEmail();
		}
		if (text == null||text.equals(getString(R.string.mail) + getString(R.string.colon))) {
			text = "";
		}
		email.setText(" " + text);
		text = getString(R.string.website_source) + getString(R.string.colon);
		websiteTitle.setText(text);
		if (rb.getWebsite() != null) {
			text = rb.getWebsite();
		}

		website.setText(" " + text);
		eventSource = text;

		website.setMovementMethod(LinkMovementMethod.getInstance());
		
		if (content!=null && content.length() > 20) {
			sharing_inf=context
			.getString(R.string.eventstitleMenu)
			+ " "
			+ title
			+ ":"
			+ content.substring(0, 20)
			+ "...\n"
			+ source
			+ "41."
			+ id
			+ "."
			+ SharedPreferencesMgr.getUserID() + ".";


		} else {
			sharing_inf=context
			.getString(R.string.eventstitleMenu)
			+ " "
			+ title
			+ ":"
			+ content
			+ "\n"
			+ source
			+ "41."
			+ id + "." + SharedPreferencesMgr.getUserID() + ".";

		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		// Util.pop.dismiss();
		// Util.popflg = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		// Util.pop.dismiss();
		// Util.popflg = false;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// Util.pop.dismiss();
		// Util.popflg = false;
		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// if(Util.popflg==true) {
			// Util.pop.dismiss();
			// Util.popflg=false;
			// } else
			{
				this.finish();
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		// TODO Auto-generated method stub
		if (o != null) {

			if (o instanceof EventInfParse) {
				EventInfParse data = (EventInfParse) o;// 获取event详细内容
				rb = data.relatedBean;
				showEventsDetails(rb.getID());

			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.favoritBtn:
			MobclickAgent.onEvent(context, "Favorite", "EveFavoritBtn", 1);// 友盟记录会议的收藏
			
			uploadLoginLogNew("Events","EveFavoritBtn", "Favorite",null);
			if (eveAdpt.selectEventCollection(context, "" + id, SharedPreferencesMgr.getUserName()) > 0) {// 如果收藏过就取消收藏
				if (eveAdpt.delEventCollection("" + id, SharedPreferencesMgr.getUserName())) {
					Toast toast = Toast.makeText(context,
							context.getString(R.string.cancelFav),
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 220);
					toast.show();
					collectBtn.setBackgroundResource(R.drawable.btnchg);

				}

			} else 
			{//如果没有收藏过就添加到收藏
				Toast toast = Toast
						.makeText(context,
								context.getString(R.string.favorite),
								Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 220);

				toast.show();
				eveAdpt.insertTableEventCollection("" + id, eventName, pl,
						eventDate, SharedPreferencesMgr.getUserName());
				collectBtn.setBackgroundResource(R.drawable.btnunchg);
			}

			break;
		}
	}
}
