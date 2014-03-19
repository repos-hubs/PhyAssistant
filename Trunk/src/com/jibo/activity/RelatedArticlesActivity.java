package com.jibo.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jibo.GBApplication;
import com.jibo.asynctask.uploadDataNew;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.ResearchGetInf;
import com.jibo.data.entity.GetArticleInfoBeans;
import com.jibo.data.entity.RelatedDrugsBeans;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.ui.TextField;
import com.umeng.analytics.MobclickAgent;
import com.api.android.GBApp.R;

/**
 * unused
 * @author will
 * @create 2013-3-21 ÏÂÎç1:43:49
 */
public class RelatedArticlesActivity extends BaseSearchActivity implements OnClickListener {

	public static final String ARTICLES_TYPE = "articles_type";
	Button collectBtn = null;
	Context context;
	private String aId;
	private String tId;
	private String articlesTyp;//
	private String title="";
	private String author;
	private String jourName;
	public String articelSource="";
	boolean favBtn=false;
	String content;
	String tit;
	String source = "http://www.jibo.cn/url.asp?p=";
	public static String sharing_inf="";
	// prime 2011-4-15
	public static final int Sharing = Menu.FIRST + 0;
	public static final int ExitMenu = Menu.FIRST + 1;
	public static final int favorite=Menu.FIRST+2;
	public static final String LOGIN_FLAG = "login_flag";
	SharedPreferences setting;
	TextView tvTitle;
	private String ta = "";
	BaseResponseHandler baseHandler;
	private FavoritDataAdapter researchadpt;
	private GBApplication app;
	ArrayList<RelatedDrugsBeans> aRelDrg=new ArrayList<RelatedDrugsBeans>();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.related_articles);
		super.onCreate(savedInstanceState);
		app = (GBApplication) getApplication();
		context=this;
        context= RelatedArticlesActivity.this;
        researchadpt = new FavoritDataAdapter(this);
        baseHandler =new BaseResponseHandler(this);
		collectBtn = (Button) findViewById(R.id.favoritBtn);
		collectBtn.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.txt_header_title);
		Display display = getWindowManager().getDefaultDisplay();
		setting = getSharedPreferences(LOGIN_FLAG, 0);
		tvTitle.setText(R.string.col_research);
		collectBtn.setOnClickListener(this);
		tvTitle.setText(getString(R.string.research));
		String articlesType = getString(R.string.research);
		String id = "";
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			articlesType = extras.getString(ARTICLES_TYPE);
			articlesTyp = articlesType;
			id = extras.getString(Constant.ID);
			if(id == null) {
				id = ""+extras.getInt(Constant.ID);
			}
			aId = id;
			tId = ta;

		}
		Properties propertyInfo =new Properties();
		propertyInfo.put(SoapRes.KEY_RESEARCH_TAI,ta);
		propertyInfo.put(SoapRes.KET_RESEARCH_ARTID,aId);
		sendRequest(SoapRes.URLResearch,SoapRes.REQ_ID_RESEARCH_GETINFID,
				propertyInfo,
				baseHandler);
		MobclickAgent.onEvent(context,"ArticlesId",aId,1);//"SimpleButtonclick");
		MobclickAgent.onError(this);
		
		uploadLoginLogNew("Research", aId,"ArticlesId", null);
		if (researchadpt.selectResearchCollection(aId,SharedPreferencesMgr.getUserName()) > 0)
		{
			collectBtn.setBackgroundResource(R.drawable.btnunchg);
		}
		setPopupWindowType(Constant.MENU_TYPE_6);
	}



	private void showArticlesDetails(String id,GetArticleInfoBeans getarticleinfo,ArrayList<RelatedDrugsBeans> allRelDrg) {
		TextView ArticleTitle = (TextView) findViewById(R.id.ArticleTitle);
		TextView ArticlesAuthor = (TextView) findViewById(R.id.ArticlesAuthor);
		TextView JournalName = (TextView) findViewById(R.id.JournalName);
		TextView Entity = (TextView) findViewById(R.id.Entity);
		TextView PublishDate = (TextView) findViewById(R.id.PublishDate);
		TextView VolumeTitle = (TextView) findViewById(R.id.VolumeTitle);
		TextView Volume = (TextView) findViewById(R.id.Volume);
		LinearLayout Abstract = (LinearLayout) findViewById(R.id.Abstract);
		TextView OtherDrugsMentionedTitle = (TextView) findViewById(R.id.RelatedDrugsMentionedTitle);
		TextView LinkToSourceTitle = (TextView) findViewById(R.id.LinkToSourceTitle);
		TextView LinkToSource = (TextView) findViewById(R.id.LinkToSource);

		String text="";
		text = getarticleinfo.getTitle();
		if(text==null)
		{
			text="";
		}else
		{
			ArticleTitle.setText(text);
		}

		title=text;

		text = getString(R.string.author) + getString(R.string.colon)+"  "+getarticleinfo.getAuthors();
		
		if(text==null)
		{
			text="";
		}else
		{
			ArticlesAuthor.setText(text);
		}

		
		text = getString(R.string.journal_name) + getString(R.string.colon)+"  "+getarticleinfo.getJournalName();
		JournalName.setText(text);
		
		text = getString(R.string.entity) + getString(R.string.colon)+"  "+getarticleinfo.getAuthorEntities();
		Entity.setText(text);
		
		text = getString(R.string.keyword) + getString(R.string.colon)+"  "+getarticleinfo.getKeyWords();
		PublishDate.setText(text);
		
		text = getString(R.string.data_and_volume) + getString(R.string.colon);
		VolumeTitle.setText(text);
		text = getarticleinfo.getDateAndVolume();
		Volume.setText(text);
		
		text = getString(R.string.abstracts) + getString(R.string.colon)+"  "+getarticleinfo.getAbstract();
		TextField textView=new TextField(this, null, text);
		textView.setText(text);
		textView.setTextColor(Color.TRANSPARENT);
		Abstract.addView(textView);
		content = text;
		text = getString(R.string.other_drugs_mentioned)
				+ getString(R.string.colon);
		OtherDrugsMentionedTitle.setText(text);

		LinearLayout OtherDrugsMentioned = (LinearLayout) findViewById(R.id.RelatedDrugsMentionedLayout);
		OtherDrugsMentioned.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < allRelDrg.size(); i++) {
			TextView drugs = new TextView(this);
			drugs.setId(i);
			SpannableString sp = new SpannableString(allRelDrg.get(i).getName_CN());
			sp.setSpan(new UnderlineSpan(), 0, allRelDrg.get(i).getName_CN().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			drugs.setTextColor(Color.BLACK);
			int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
			drugs.setTextSize(18*(app.getDeviceInfo().getScale()));
			drugs.setText(sp);
			
			drugs.setOnClickListener(this);

			LinearLayout layout = new LinearLayout(this);
			layout.setPadding(0, 5, 10, 0);
			layout.addView(drugs);

			OtherDrugsMentioned.addView(layout);
		}

		text = getString(R.string.link_to_source) + getString(R.string.colon);
		LinkToSourceTitle.setText(text);
		text = getarticleinfo.getSource();
		articelSource = text;
		LinkToSource.setText(text);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.favoritBtn:
			MobclickAgent.onEvent(context,"Favorite","ArtFavoritBtn",1);
			
			uploadLoginLogNew("Research","ArtFavoritBtn","Favorite",null);
			if (researchadpt.selectResearchCollection(aId,SharedPreferencesMgr.getUserName()) > 0) {
				if (researchadpt.delResearchCollection(aId,SharedPreferencesMgr.getUserName())) {
					Toast toast = Toast.makeText(context, context
							.getString(R.string.cancelFav), Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 220);
					toast.show();
					collectBtn.setBackgroundResource(R.drawable.btnchg);
				}

			} else {

				collectBtn.setBackgroundResource(R.drawable.btnunchg);
				if (tId == null)
					tId = "";
				favBtn=true;
				if(!title.equals(""))
				{
//					researchadpt.insertTableResearchCollection(aId, title,
//							author, jourName, tId, articlesTyp,SharedPreferencesMgr.getUserName());
					Toast toast = Toast.makeText(context, context
							.getString(R.string.favorite), Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 220);
					toast.show();
				} else {
					collectBtn.setBackgroundResource(R.drawable.btnchg);
					Toast toast = Toast.makeText(context, context
							.getString(R.string.favfail), Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 220);
					toast.show();
				}
			}
			break;
		default:
			Log.e("123321", "123321");
			int buttonId;
			if (v.getId() >= aRelDrg.size())
				return;
			for (buttonId = 0; buttonId < aRelDrg.size(); buttonId++) {
				if (buttonId == v.getId())
					break;
			}
			Intent intent = new Intent(this, NewDrugReferenceActivity.class);
			intent.putExtra("drugId",aRelDrg.get(buttonId).getID());
			startActivity(intent);
			break;
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}


	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				this.finish();			
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public void onReqResponse(Object o,int methodId) {
		// TODO Auto-generated method stub
		if(o!=null)
		{
		
			if(o instanceof ResearchGetInf)
			{
				ResearchGetInf data= (ResearchGetInf) o;
				aRelDrg=data.allRelDrg;
				showArticlesDetails(aId,data.getarticleinfo,data.allRelDrg);
				Log.e("data.allRelDrg", String.valueOf(data.allRelDrg.size()));
		    }
			

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

	
	
	
}

