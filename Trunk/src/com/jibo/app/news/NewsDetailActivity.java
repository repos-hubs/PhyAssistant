package com.jibo.app.news;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flip.FlipViewController.ViewFlipListener;
import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.BaseSearchActivity;
import com.jibo.activity.HistoryFavoriteActivity;
import com.jibo.activity.RelatedNewsActivity;
import com.jibo.adapter.TravelAdapter;
import com.jibo.app.DetailsData;
import com.jibo.app.push.PushConst;
import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.data.entity.NewsDetail;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.dbhelper.MySqlLiteHelper;
import com.jibo.ui.HomePageLayout;
import com.jibo.ui.ViewFlow;
import com.jibo.util.ComparatorRepo;
import com.jibo.util.Logs;
import com.jibo.util.tips.Mask;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

public class NewsDetailActivity extends BaseSearchActivity implements
		OnGestureListener {
	public static boolean activityStarted = false;
	public static Set<String> favors = new TreeSet<String>(
			ComparatorRepo.stringKey);
	Bundle savedInstanceState;
	private AdaptInfo adaptInfo;
	ViewFlow vf = null;
	private int lcoate;
	private List<Integer> pageIds;
	private List<EntityObj> detailEntities = new ArrayList<EntityObj>(0);

	private View btnFeedback;
	public View btnFavor;
	private View btnFonts;
	private View btnToggleLayout3;
	private View btnShare;
	// private DetailWebView tmpL;
	private FlipViewController flipView;
	private FavoritDataAdapter fData;
	Handler favorHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			boolean shine = (Boolean) (msg.obj);
			if (shine) {
				findViewById(R.id.tgbtn_2nd).setBackgroundResource(
						R.drawable.btn_favorite_select);
			} else
				findViewById(R.id.tgbtn_2nd).setBackgroundResource(
						R.drawable.btn_favorite_normal);
		}

	};
	private MySqlLiteHelper mDbHelper;
	private int fontSize;
	private boolean loadFlag;
	private GestureDetector returnGesture;
	private int offset;
	public List<Integer> sizes = new ArrayList<Integer>(0);
	public EntityObj currEntity;
	public ViewGroup header;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newsdetail);
		mDbHelper = new MySqlLiteHelper(this.getBaseContext(),
				Constant.MY_SQLITE_VESION);

		this.savedInstanceState = savedInstanceState;
		fData = new FavoritDataAdapter(this);
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(context);
		String id = getIntent().getStringExtra("id");
		if (id != null && !id.equals("-1")) {
			EntityObj ej = new EntityObj();
			ej.fieldContents.put("id", id);
			ej.fieldContents.put("newsDetail", new NewsDetail());
			DetailsData.tappedne = ej;
		}
		offset = DetailsData.getEntities().lastIndexOf(DetailsData.tappedne);
		((AutoCompleteTextView) findViewById(R.id.actv_searchEdit))
				.setHint(R.string.news);
		((TextView) findViewById(R.id.txt_header_title)).setText(R.string.news);
		btnFavor = findViewById(R.id.tgbtn_2nd);
		btnFeedback = findViewById(R.id.btn_1st);
		btnFonts = findViewById(R.id.btn_3rd);
		btnShare = findViewById(R.id.btn_share);
		header = (ViewGroup) getHeaderView(getBaseContext());

		flipView = (FlipViewController) this.findViewById(R.id.filpview);

		// tmpL = (DetailWebView) this.findViewById(R.id.vPager);
		if ((currEntity = DetailsData.tappedne) == null) {
			return;
		}

		// WebSettings mWebSettings = tmpL.getSettings();
		// // ������仰����ʹ��javascript����
		// mWebSettings.setJavaScriptEnabled(true);
		// mWebSettings.setDomStorageEnabled(true);
		// HtmlContract.updateWebContent(tmpL, this.currEntity,
		// header,this.getWindow().getDecorView());
		// tmpL.getSettings().setDefaultFontSize(19);
		this.findViewById(R.id.dialogprogress).setVisibility(View.GONE);
		((TextView) this.findViewById(R.id.currpage)).setText(offset + "/"
				+ DetailsData.getEntities().size());
		getContent();
		// tmpL.setActivity(this);
		// if (!loadFlag) {
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// loadFlag = true;
		// DetailsData
		// .fetchDetailsOfListOnSoap(NewsDetailActivity.this,
		// DetailsData.getEntities(),
		// new BaseResponseHandler(
		// NewsDetailActivity.this, -1), true);
		// }
		// }).start();
		// }
		returnGesture = new GestureDetector(this);

	}

	private View rootView;

	private void getContent() {
		detailEntities.clear();
		detailEntities.addAll(DetailsData.getEntities());
		rootView = this.getWindow().getDecorView();
		TravelAdapter adapter = new TravelAdapter(this, flipView, rootView);
		flipView.setAdapter(adapter, offset);
		flipView.setOnViewFlipListener(new ViewFlipListener() {

			@Override
			public void onViewFlipped(View view, int position) {
				TextView tx1 = (TextView) rootView.findViewById(R.id.currpage);
				if (position == 0) {
					((TextView) rootView.findViewById(R.id.prepage))
							.setTextColor(Color.WHITE);
				} else {
					((TextView) rootView.findViewById(R.id.prepage))
							.setTextColor(Color.BLACK);
				}
				if (position + 1 == DetailsData.getEntities().size()) {
					((TextView) rootView.findViewById(R.id.nextpage))
							.setTextColor(Color.WHITE);
				} else {
					((TextView) rootView.findViewById(R.id.nextpage))
							.setTextColor(Color.BLACK);
				}
				tx1.setText(position + 1 + "/" + DetailsData.getEntities().size());
				EntityObj current = DetailsData.getEntities().get(position);
				Logs.i("== news "+current.get(current.getId())+" "+DetailsData.viewedNews);
				DetailsData.viewedNews.add(current.get(current.getId()));
			}
		});

		// tmpL.setPageIds(detailEntities, offset);
	}

	int indicaterSize = -1;

	public int getSize() {
		if (indicaterSize == -1) {
			indicaterSize = sizes.indexOf((((WebView) flipView.getSelectedView()
					.findViewById(100)))
					.getSettings().getTextSize());
		}
		if (indicaterSize == sizes.size() - 1) {
			indicaterSize = -1;
		}
		return ++indicaterSize;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		currEntity = DetailsData.tappedne;
		if (currEntity == null) {
			return;
		}
	}

	private Mask mask;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mask = (Mask) findViewById(R.id.mask);
		mask = new Mask(this, null);
		TipHelper.registerTips(this, 1);
		TipHelper.runSegments(this);
		this.findViewById(R.id.closeTips).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						TipHelper.sign(false, true);
						TipHelper.disableTipViewOnScreenVisibility();
					}
				});
	}

	@Override
	protected void onStart() {

		super.onStart();

		// WebSettings mWebSettings = ((WebView) this.findViewById(R.id.vPager))
		// .getSettings();
		// mWebSettings.setJavaScriptEnabled(true);
		// mWebSettings.setDomStorageEnabled(true);
		// WebView wv;
		// HtmlContract.updateWebContent(wv = (WebView)
		// this.findViewById(R.id.vPager), this.currEntity,
		// getHeaderView(getBaseContext()),wv.getRootView());
		btnFavor = findViewById(R.id.tgbtn_2nd);
		// HistoryFavoriteActivity

		btnFeedback = findViewById(R.id.btn_1st);
		btnFonts = findViewById(R.id.btn_3rd);
		btnShare = findViewById(R.id.btn_share);
		btnFavor.setVisibility(View.VISIBLE);
		btnFeedback.setVisibility(View.VISIBLE);
		btnFonts.setVisibility(View.VISIBLE);
		btnShare.setVisibility(View.VISIBLE);

		btnFonts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sizes.isEmpty()) {
					sizes.add(25);
					sizes.add(32);
					sizes.add(38);
					sizes.add(19);

				}

				WebView web = ((WebView) flipView.getSelectedView()
						.findViewById(100));
				if (web.getVisibility() == View.VISIBLE) {
					web.getSettings().setDefaultFontSize(sizes.get(getSize()));
					web.postInvalidateDelayed(1000);
				}
			}

		});

		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WebView web = ((WebView) flipView.getSelectedView()
						.findViewById(100));
				if (web.getVisibility() == View.GONE)
					return;

				EntityObj news = DetailsData.getEntities().get(flipView
						.getSelectedItemPosition());
				String text = null;
				text = news.get("title");

				String title = text;
				text = ((NewsDetail) (news.getObject("newsDetail"))).content;

				final String typeID = news.get("typeID");
				// if (text == null)
				// return;
				Spanned content = Html.fromHtml(text);
				System.out.println("text   " + text);
				text = getString(R.string.link_to_source)
						+ getString(R.string.colon);

				text = news.get("newSource");

				title = news.get("title");
				int id1 = Integer.parseInt(news.get("id"));
				int colID = -1;

				if (content.length() > 20)// xinwen
				{
					RelatedNewsActivity.sharing_inf = context
							.getString(R.string.newstitleMenu)
							+ " "
							+ title
							+ ":"
							+ content.toString().substring(0, 20)
							+ "...\n"
							+ context.getString(R.string.share_url)
							+ "40."
							+ news.get("id")
							+ "."
							+ SharedPreferencesMgr.getUserID() + ".";
				} else {
					RelatedNewsActivity.sharing_inf = context
							.getString(R.string.newstitleMenu)
							+ " "
							+ title
							+ ":"
							+ content
							+ "...\n"
							+ context.getString(R.string.share_url)
							+ "40."
							+ news.get("id")
							+ "."
							+ SharedPreferencesMgr.getUserID() + ".";

				}// news
				sharing(R.array.items2, 1);
			}

		});
		btnFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UMFeedbackService.openUmengFeedbackSDK(context);
				// startActivity(new Intent(context, FeedBackActivity.class));
			}

		});
		btnFavor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Logs.i("--- favor ");
				Boolean shine = null;
				MobclickAgent.onEvent(context, "Favorite", "NewsFavoritBtn", 1);
				
				if (fData.selectNewsCollection(context, currEntity.get("id"),
						SharedPreferencesMgr.getUserName()) > 0) {
					if (fData.delNewsCollection(currEntity.get("id"),
							SharedPreferencesMgr.getUserName())) {
						Toast toast = Toast.makeText(context,
								context.getString(R.string.cancelFav),
								Toast.LENGTH_LONG);
						toast.setGravity(Gravity.TOP, 0, 220);
						toast.show();
						shine = false;
						HistoryFavoriteActivity.arrFavorNewsId
								.remove(currEntity.get("id"));
					}
				} else {
					Toast toast = Toast.makeText(context,
							context.getString(R.string.favorite),
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 220);
					toast.show();
					fData.insertTableNewsCollection(currEntity.get("id"),
							currEntity.get("title"), currEntity.get("date"),
							SharedPreferencesMgr.getUserName());
					// btnFlag=true;
					shine = true;
					HistoryFavoriteActivity.arrFavorNewsId.add(currEntity
							.get("id"));

				}
				cursorAddFavor(("where mobileID=" + currEntity.get("id")));
				Message msg = new Message();
				msg.obj = (Boolean) shine;
				favorHandler.sendMessage(msg);
			}

		});
	}

	public static void storeHistory(String id, String title, Context context,
			HistoryAdapter historyAdapter) {
		int colID = GBApplication.gbapp.getColID(context
				.getString(R.string.news));
		historyAdapter.storeViewHistory(SharedPreferencesMgr.getUserName(),
				Integer.parseInt(id), colID, -1, title);
	}

	public static View getHeaderView(Context context) {
		return LayoutInflater.from(context).inflate(R.layout.detail_header,
				null);
	}

	private void cursorAddFavor(String cond) {
		Cursor cur = fData.getCursor(
				"select distinct mobileID from NewsCollection " + cond, null);
		while (cur.moveToNext()) {
			favors.add(cur.getString(0));
		}
	}

	private void rmScreen(HomePageLayout homePage, int i) {
		// TODO Auto-generated method stub
		homePage.removeViewAt(i);
		homePage.invalidate();
	}

	public AdaptInfo getAdaptInfo() {
		// TODO Auto-generated method stub
		if (adaptInfo == null) {
			adaptInfo = new AdaptInfo();
			adaptInfo.objectFields = new String[] { "content" };
			adaptInfo.listviewItemData = new AdapterSrc();
			adaptInfo.viewIds = new int[] { R.id.webview };
			adaptInfo.listviewItemLayoutId = R.layout.detail_list_item;
			// adaptInfo.actionListeners = getViewHandlers();
		}

		return adaptInfo;
	}

	private int locate = -1;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

		}

	};
	List<Integer> slideList = new ArrayList<Integer>(0);
	int j = 5;
	int contentid = -1;
	int cntIdMinus = -1;

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		DetailsData.entities = null;
		DetailsData.activityStarted = false;
		DetailsData.tappedne = null;
		currEntity = null;
		slideList.clear();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(PushConst.pushFlag==null){
			startActivity(new Intent(this,com.jibo.news.NewsPageActivity.class));
			finish();
			return;
		}
		if (this.findViewById(R.id.counterfeitContent).getVisibility() == View.GONE) {
			this.findViewById(R.id.header_panel).setVisibility(View.VISIBLE);
			this.findViewById(R.id.counterfeitContent).setVisibility(
					View.VISIBLE);
			return;
		}
		super.onBackPressed();
		finish();
	}

	public static void update(View activity, EntityObj en) {
		((TextView) activity.findViewById(R.id.title)).setText(en.get("title"));
		((TextView) activity.findViewById(R.id.date)).setText(en.get("date"));
		((TextView) activity.findViewById(R.id.src)).setVisibility(en
				.get("stickMsg").trim().equals("") ? View.GONE : View.VISIBLE);
		((TextView) activity.findViewById(R.id.src))
				.setText(en.get("stickMsg"));
	}

	@Override
	public boolean onDown(MotionEvent e) {
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

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onTouch(View v, MotionEvent event) {
		returnGesture.onTouchEvent(event);
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		// Logs.i("c h "+(int)(tmpL.getContentHeight()*tmpL.getScale()) +" h "+
		// (tmpL.getHeight() + tmpL.getScrollY()));
		// if((int)(tmpL.getContentHeight()*tmpL.getScale()) ==
		// (tmpL.getHeight() + tmpL.getScrollY())){
		// ((View)tmpL.getParent()).findViewById(R.id.pagebar).setVisibility(View.VISIBLE);
		// }
		return false;
	}
}
