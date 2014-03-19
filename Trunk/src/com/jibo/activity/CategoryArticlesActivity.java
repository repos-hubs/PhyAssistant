package com.jibo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.GetResearchSearch;
import com.jibo.data.ProfilePaser;
import com.jibo.data.ResearchGetArtParser;
import com.jibo.data.RetrieveCoauthorPaser;
import com.jibo.data.entity.ProfileEntity;
import com.jibo.data.entity.ResearchBean;
import com.jibo.data.entity.RetrieveArticleEntity;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.dbhelper.ProfileAdapter;
import com.jibo.net.BaseResponseHandler;
import com.umeng.analytics.MobclickAgent;
public class CategoryArticlesActivity extends BaseSearchActivity implements OnClickListener,
OnItemClickListener, OnScrollListener{

	private static final String TAG = "CategoryArticles";
	public static final String CATEGORY_ARTICLES_TYPE = "category_articles_type";
	public static final String ARTICLE_CATEGORIES_NAME = "article_categories_name";
	public static final String TA_ID = "ta";
	public static final String KW_ID = "kw";
	public static final String KW = "content";
	public static final String ARTICLE_TOTAL_NUMBER = "article_total_number";
	public static final String TYPE_RESULT = "type_result";
	public static final String TYPE_ARTICLE_CATEGORIES = "type_article_categories";
	public static final String TYPE_ACADEMIC_PROFILE = "type_academic_profile";
	public static final String TYPE_COAUTHORS_ARTICLES = "type_coauthors_articles";
	public static final String TYPE_ADVANCED_SEARCH = "type_advanced_search";
	ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
	public static final String COAUTHORS_NAME = "coauthors_name";
	public static final String DOCTOR_ID = "doctor_id";
	public static final String COAUTHORS_ID = "coauthors_id";
	public ArrayList<String> arrayArticles = new ArrayList<String>();
	public ArrayList<String> arraySearchArticles = new ArrayList<String>();
	public int pageNum = 1;
	public int pageNumber = 1;
	private String mTotalNum;

	/* map key */
	private static final String COLUMN_TEXT_01 = "title";
	private static final String COLUMN_TEXT_02 = "number";
	private static final int PAGESIZE = 10;
	private String ta = "";
	private String kw_id;
	private ListView mArticlesList;
	private TextView txtCoauthor;
	private boolean flag = true;
	private LinearLayout layout;
	private TextView tvTitle;
    public BaseResponseHandler baseHandler;
    public BaseResponseHandler bseHandler;
	private String mSearchText;
	private boolean[] mCheckedItemsArticle;
	private String mCoauthorName;
	private String mDoctorId;
	private String mCoauthorId;
	SimpleAdapter adapter = null;
	private String mType;
	private String mTypeName;
	private String kw = "";
	private boolean isMainPage = false;

	private static final String CHECKITEM = "GBACHECKITEM1";
	Context context=null;
	private ArrayList<RetrieveArticleEntity> entityList;
	private GBApplication app;
	private HistoryAdapter historyAdapter;
	private ArrayList<RetrieveArticleEntity> list;
	private ProfileAdapter profileAdapter;
	private Toast toast = null;//用于提示划屏到最后一条数据
	private boolean isTheLast = false;//listView显示没有到达最后一条

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.category_articles);
		super.onCreate(savedInstanceState);
		app = (GBApplication) getApplication();
		/*初始化toast   add by Terry*/
		toast = Toast.makeText(this, "已到达最后一条", Toast.LENGTH_SHORT);
		historyAdapter = new HistoryAdapter(this, 2, "mysqllite.db");
		mCheckedItemsArticle = new boolean[] {false, false, false, false, false, false};
		mCoauthorName = "";
		mDoctorId = "";
		mCoauthorId = "";
		context=this;
        baseHandler =new BaseResponseHandler(this);
        bseHandler=new BaseResponseHandler(this);
        profileAdapter = new ProfileAdapter(this, 2);
		txtCoauthor = (TextView) findViewById(R.id.txt_coauthor);
		mArticlesList = (ListView) findViewById(R.id.lst_item);
		layout = (LinearLayout) findViewById(R.id.dialogprogress);
		tvTitle = (TextView) findViewById(R.id.txt_header_title);
		adapter = new SimpleAdapter(this, listItem,
				R.layout.druglst_info, new String[] {
						COLUMN_TEXT_01, COLUMN_TEXT_02 }, new int[] {
						R.id.ListText1, R.id.ListText2 });
		txtCoauthor.setOnClickListener(this);
		
		Display display = getWindowManager().getDefaultDisplay();
		mArticlesList.setOnItemClickListener(this);
		
		tvTitle.setText(getString(R.string.linked_papers));

		Intent intent = getIntent();
		entityList = getIntent().getParcelableArrayListExtra("ArticleList");
		if (entityList != null) {
			mType = intent.getStringExtra(CATEGORY_ARTICLES_TYPE);
			for(RetrieveArticleEntity en:entityList) {
				System.out.println("en  "+en.getTitle());
			}
			addArticlesItem(entityList);
			mArticlesList.setAdapter(adapter);
		} else {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
			   mType = extras.getString(CATEGORY_ARTICLES_TYPE);
			   pageNum = 1;
			   mTypeName = extras.getString(ARTICLE_CATEGORIES_NAME);
			   mTotalNum = extras.getString(ARTICLE_TOTAL_NUMBER);
			   kw = extras.getString(KW);
			   tvTitle.setText(kw);
			   ta = extras.getString(TA_ID);
			   kw_id = extras.getString(KW_ID);
			   Log.e("1212121kw_id", String.valueOf(kw_id));
			   mCheckedItemsArticle=extras.getBooleanArray("select_item");
			   mSearchText = extras.getString("search_text");
			   mCoauthorName = extras.getString(COAUTHORS_NAME);
			   mDoctorId = extras.getString(DOCTOR_ID);
			   mCoauthorId = extras.getString(COAUTHORS_ID);
			   isMainPage = extras.getBoolean(Constant.ISMAINPAGE, false);
		    }
			if(mType.equals(TYPE_COAUTHORS_ARTICLES)) {
				tvTitle.setText(getString(R.string.linked_papers));
				String doctorID = getIntent().getStringExtra(DOCTOR_ID);
				String coauthorID = getIntent().getStringExtra(COAUTHORS_ID);
				String name = getIntent().getStringExtra(COAUTHORS_NAME);
				txtCoauthor.setText(name);
				txtCoauthor.setTag(coauthorID);
				txtCoauthor.setVisibility(LinearLayout.VISIBLE);
				
		    	Properties propertyInfo =new Properties();
		    	propertyInfo.put(SoapRes.KEY_DOCTOR_ID, doctorID);
		    	propertyInfo.put(SoapRes.KEY_COAUTHOR_ID, coauthorID);
		    	sendRequest(SoapRes.URLProfile, SoapRes.REQ_ID_RETRIEVE_COAUTHOR_ARTICLE,
						propertyInfo, baseHandler);//rafeal的学术背景列表
		    } else if(!mType.equals(TYPE_ADVANCED_SEARCH))
			{
				Properties propertyInfo =new Properties();//ta, kw_id, PAGESIZE, pageNum
				propertyInfo.put(SoapRes.KEY_RESEARCH_TA,ta);
				propertyInfo.put(SoapRes.KEY_REASEARCH_kw,kw_id);
				propertyInfo.put(SoapRes.KEY_REASEARCH_PS, PAGESIZE);
				propertyInfo.put(SoapRes.KEY_REASEARCH_PN, pageNum);
				sendRequest(SoapRes.URLResearch, SoapRes.REQ_ID_RESEARCH_TART,
						propertyInfo,
						baseHandler);//请求医学研究的列表
			}

		}
		
		if (!mType.equals(TYPE_ACADEMIC_PROFILE)
				&& !mType.equals(TYPE_COAUTHORS_ARTICLES)) {
			mArticlesList.setOnScrollListener(this);
		}

		if (mType.equals(TYPE_COAUTHORS_ARTICLES)) {
			mArticlesList.setAdapter(adapter);
		}
		if (mType.equals(TYPE_ACADEMIC_PROFILE)) {
		}
		if (mType.equals(TYPE_RESULT) || mType.equals(TYPE_COAUTHORS_ARTICLES)) {
		}
		if (mType.equals(TYPE_ARTICLE_CATEGORIES)) {
			mArticlesList.setAdapter(adapter);
		}

		if (mType.equals(TYPE_ADVANCED_SEARCH)) {
			tvTitle.setText(getString(R.string.search_results));
			   pageNum = 1;

				Log.e("mSearchText", mSearchText);
			Properties propertyInfo =new Properties();//ta, kw_id, PAGESIZE, pageNum
			propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_KWD,mSearchText);
			propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_TIT,mCheckedItemsArticle[0]);
			propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_AU, mCheckedItemsArticle[1]);
			propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_ENT, mCheckedItemsArticle[2]);
			propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_PUB, mCheckedItemsArticle[3]);
			propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_CATE, mCheckedItemsArticle[4]);
			propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_IKEY, mCheckedItemsArticle[5]);
			propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_PS, 10);
			propertyInfo.put(SoapRes.KEY_RESEARCH_SESRCH_PNUM, pageNum);
			sendRequest(SoapRes.URLResearch, SoapRes.REQ_ID_RESEARCH_SEARCH,
					propertyInfo,
					bseHandler);
			mArticlesList.setAdapter(adapter);
			mArticlesList.setOnScrollListener(this);
		Log.e("SEARCH_TIT", String.valueOf(mCheckedItemsArticle[0]));
		}
		
		
		Log.e("mTypemType", mType);
	}


	
	private ArrayList<HashMap<String, String>> addArticlesItemSch(ArrayList<ResearchBean> list) {
		int length=0;
		String[] title, authors, journalName;

       if(mType.equals(TYPE_ADVANCED_SEARCH))
       {
			length =list.size();
		for (int i = 0; i < length; i++) {
			arrayArticles.add(((ResearchBean)list.get(i)).getID());
		}
		// adapter.notifyDataSetChanged();
		for (int i = 0; i < length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			if (((ResearchBean)list.get(i)).getTitle() != null) {
				map.put(COLUMN_TEXT_01,((ResearchBean)list.get(i)).getTitle());
				map.put(COLUMN_TEXT_02, ((ResearchBean)list.get(i)).getAuthors() + getString(R.string.comma)
						+ ((ResearchBean)list.get(i)).getJournalName());
				Log.e("COLUMN_TEXT_01",((ResearchBean)list.get(i)).getTitle());
				listItem.add(map);
			}
		}
       }
		return listItem;
	}

	private ArrayList<HashMap<String, String>> addArticlesItem(ArrayList<RetrieveArticleEntity>list) {
		int length=0;
		String[] title, authors, journalName;
		ArrayList<ResearchBean> alsearch=new ArrayList<ResearchBean> ();
		ArrayList<RetrieveArticleEntity> all=new ArrayList<RetrieveArticleEntity> ();
			if (mType.equals(TYPE_COAUTHORS_ARTICLES)||mType.equals(TYPE_ARTICLE_CATEGORIES)||mType.equals(TYPE_ACADEMIC_PROFILE)) {
	
				length =list.size();
			for (int i = 0; i < length; i++) {
				arrayArticles.add(((RetrieveArticleEntity)list.get(i)).getId());
			}
			// adapter.notifyDataSetChanged();
			for (int i = 0; i < length; i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				if (((RetrieveArticleEntity)list.get(i)).getTitle() != null) {
					map.put(COLUMN_TEXT_01,((RetrieveArticleEntity)list.get(i)).getTitle());
					map.put(COLUMN_TEXT_02, ((RetrieveArticleEntity)list.get(i)).getAuthors() + getString(R.string.comma)
							+ ((RetrieveArticleEntity)list.get(i)).getJournalName());
					Log.e("COLUMN_TEXT_01",((RetrieveArticleEntity)list.get(i)).getTitle());
					listItem.add(map);
				}
			}
		} 
		return listItem;
	}
	
	
	
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Intent intent;
		String title = null;
		String ArticlesId = null;
		intent = new Intent(this, RelatedArticlesActivity.class);
		
		if (mType.equals(TYPE_ARTICLE_CATEGORIES)) {
			title = getString(R.string.research);
			if (position < arrayArticles.size()
					&& arrayArticles.get(position) != null) {
				ArticlesId = arrayArticles.get(position);
			} else {
				ArticlesId = null;
			}
		} else if (mType.equals(TYPE_ACADEMIC_PROFILE)) {
			title = getString(R.string.research);
			if (position < arrayArticles.size()
					&& arrayArticles.get(position) != null) {
				ArticlesId = arrayArticles.get(position);
			} else {
				ArticlesId = null;
			}
		}  
//		else {
//			title = getString(R.string.research);
//			if (position < arraySearchArticles.size()
//					&& arraySearchArticles.get(position) != null) {
//				ArticlesId = arraySearchArticles.get(position);
//			} else {
//				ArticlesId = null;
//			}
//		}
		if (mType.equals(TYPE_ADVANCED_SEARCH)) {
//			if (position < arraySearchArticles.size()
//					&& arraySearchArticles.get(position) != null) {
//				ArticlesId = arraySearchArticles.get(position);
//			} else {
//				ArticlesId = null;
//			}
			
			if (position < arrayArticles.size()
					&& arrayArticles.get(position) != null) {
				ArticlesId = arrayArticles.get(position);
				MobclickAgent.onEvent(context,"SearchArticlesId",ArticlesId,1);//"SimpleButtonclick");
			} else {
				ArticlesId = null;
			}
		} else if (mType.equals(TYPE_COAUTHORS_ARTICLES)) {
			title = mCoauthorName;
			ArticlesId = list.get(position).getId();

		}

		if (ArticlesId != null) {
			TextView txt = (TextView) v.findViewById(R.id.ListText1);
			String content = txt.getText().toString();
			int colID = -1;
			for(Entry en:app.getPdaColumnMap().entrySet()) {
				if(en.getValue().toString().equals(getString(R.string.research))) {
					colID = (Integer) en.getKey();
				}
			}
			historyAdapter.storeViewHistory(app.getLogin().getGbUserName(), Integer.parseInt(ArticlesId), colID, -1, content);
			
			intent.putExtra(RelatedArticlesActivity.ARTICLES_TYPE, title);
			intent.putExtra(Constant.ID, ArticlesId);
			intent.putExtra(TA_ID, ta);
			startActivity(intent);
		}

	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}
	
	private static boolean isMoveDown = true;
	private int mMotionY = 0;
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// && !mType.equals(TYPE_ADVANCED_SEARCH)
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				final int y = (int) ev.getY();
				
				int deltaY = 0;
				int action = ev.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN: {
					mMotionY = y;
					break;
				}

				case MotionEvent.ACTION_MOVE: {
					deltaY = y - mMotionY;   //deltaY的正负就表示往下或往上
					if(deltaY > 0){
						isMoveDown = false;
					}else if(deltaY < 0){
						isMoveDown = true;
					}
					break;
					}
				}

				return false;
				}}
			);
		
		Log.e("typetype", mType);
		if (view.getLastVisiblePosition() == view.getCount() - 1
				&& !mType.equals(TYPE_ADVANCED_SEARCH)
				&& !mType.equals(TYPE_RESULT)
				&& !mType.equals(TYPE_ACADEMIC_PROFILE)
				&& !mType.equals(TYPE_COAUTHORS_ARTICLES)) {
			if (flag == true) {
				 flag = false;
                Properties propertyInfo =new Properties();//ta, kw_id, PAGESIZE, pageNum
				propertyInfo.put(SoapRes.KEY_RESEARCH_TA,ta);
				propertyInfo.put(SoapRes.KEY_REASEARCH_kw,kw_id);
				propertyInfo.put(SoapRes.KEY_REASEARCH_PS, PAGESIZE);
				propertyInfo.put(SoapRes.KEY_REASEARCH_PN, ++pageNum);
				sendRequest(SoapRes.URLResearch, SoapRes.REQ_ID_RESEARCH_TART,
						propertyInfo, baseHandler);
			}
			/*如果列表显示到了最后一条就提示   Terry*/
			if (isTheLast && isMoveDown){
				toast.cancel();
				toast.show();
			}
		}

		if (view.getLastVisiblePosition() == view.getCount() - 1&&mType.equals(TYPE_ADVANCED_SEARCH)) {			
				Properties propertyInfo =new Properties();//ta, kw_id, PAGESIZE, pageNum
				propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_KWD,mSearchText);
				propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_TIT,mCheckedItemsArticle[0]);
				propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_AU, mCheckedItemsArticle[1]);
				propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_ENT, mCheckedItemsArticle[2]);
				propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_PUB, mCheckedItemsArticle[3]);
				propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_CATE, mCheckedItemsArticle[4]);
				propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_IKEY, mCheckedItemsArticle[5]);
				propertyInfo.put(SoapRes.KEY_RESEARCH_SEARCH_PS, 10);
				propertyInfo.put(SoapRes.KEY_RESEARCH_SESRCH_PNUM, ++pageNum);
				sendRequest(SoapRes.URLResearch, SoapRes.REQ_ID_RESEARCH_SEARCH,
						propertyInfo,
						bseHandler);

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


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				this.finish();
			
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onReqResponse(Object o,int methodId) {
		if(o!=null)
		{
			if(o instanceof ResearchGetArtParser){
				ResearchGetArtParser data= (ResearchGetArtParser) o;
				/*if代码块是后面添加的，原因是划屏到最后没数据了还去服务器中取数据 add by Terry*/
				if (0 == data.list.size()){
					flag = false;
					isTheLast = true;//如果读取到的数据位0时就说明显示到了最后一条
				}else {
					addArticlesItem(data.list);
					adapter.notifyDataSetChanged();
					flag=true;
				}
			
			} else if(o instanceof RetrieveCoauthorPaser) {
				RetrieveCoauthorPaser data= (RetrieveCoauthorPaser) o;
				list = data.getList();
				addArticlesItem(list);
				adapter.notifyDataSetChanged();
			} else if(o instanceof GetResearchSearch){
				GetResearchSearch data=(GetResearchSearch)o;
				addArticlesItemSch(data.list);

				adapter.notifyDataSetChanged();
			} else if (o instanceof ProfilePaser) {
				ProfilePaser vd = (ProfilePaser) o;
				ProfileEntity en = vd.getEntity();
				if (en != null) {
					profileAdapter.setProfile(en);
					Intent intent = new Intent(this,
							AcademicProfileActivity.class);
					intent.putExtra("data", en);
					startActivity(intent);
				} else {
					showDialog(DialogRes.DIALOG_ID_ERROR_LISENCE);
				}
			}
	     }
      }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.txt_coauthor:
			ProfileEntity en = profileAdapter.getProfile(v.getTag().toString());
			Intent intent = new Intent(this,AcademicProfileActivity.class);
			if (en != null) {
				intent.putExtra("data", en);
				startActivity(intent);
			} else {
				Properties propertyInfo = new Properties();
				String doctorId = v.getTag().toString();
				if (doctorId != null && !"".equals(doctorId)) {
					propertyInfo.put(SoapRes.KEY_GETPROFILE_DOCTORID,
							doctorId);
					propertyInfo.put(SoapRes.KEY_GETPROFILE_USERNAME,
							app.getLogin().getGbUserName());
					sendRequest(SoapRes.URLProfile,
							SoapRes.REQ_ID_GET_PROFILE, propertyInfo,
							new BaseResponseHandler(this));
				} else {
					showDialog(DialogRes.DIALOG_ID_NO_LISENCE);
				}

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

}
