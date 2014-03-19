package com.jibo.adapter;

import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.aphidmobile.flip.FlipViewController;
import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.BaseActivity;
import com.jibo.app.DetailsData;
import com.jibo.app.news.NewsDetailActivity;
import com.jibo.app.research.PaperDetailActivity;
import com.jibo.app.research.PaperHtmlPaser;
import com.jibo.base.src.EntityObj;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.entity.NewsDetail;
import com.jibo.data.entity.PaperDetailEntity;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.dbhelper.ResearchAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.HtmlContract;

/**
 * 新闻detail adapter
 * 
 * @author simon
 * 
 */
public class TravelAdapter extends BaseAdapter implements OnTouchListener,
		OnGestureListener {

	private BaseActivity ctx;
	private LayoutInflater inflater;

	private FlipViewController controller;
	private View rootView;

	private GestureDetector gd;

	private static final int CLICK_ON_WEBVIEW = 1;
	private static final int CLICK_ON_URL = 2;
	private static final int HIDE_PAGE_BAR = 3;
	private static final int JOURNAL_SUBSCIBE = 4;

	private PaperHtmlPaser paperHtmlPaser;
	private HistoryAdapter historyAdapter;
	private ResearchAdapter researchAdapter;

	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CLICK_ON_URL:
				handler.removeMessages(CLICK_ON_WEBVIEW);
				String url = (String) msg.obj;
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				ctx.startActivity(intent);
				break;

			case CLICK_ON_WEBVIEW:
				scaleView();
				break;

			case HIDE_PAGE_BAR:
				rootView.findViewById(R.id.pagebar).setVisibility(View.GONE);
				break;

			case JOURNAL_SUBSCIBE:
				String journalID = (String) msg.obj;
				Properties propertyInfo = new Properties();
				propertyInfo.put("sign", "");
				propertyInfo.put(SoapRes.KEY_SUBSCIBE_USER_NAME,
						SharedPreferencesMgr.getUserName());
				propertyInfo.put(SoapRes.KEY_SUBSCIBE_PERIODICAL_ID, journalID);
				propertyInfo.put(SoapRes.KEY_SUBSCIBE_STATUS, "1");
				ctx.sendRequest(SoapRes.URLResearchDetail,
						SoapRes.REQ_ID_JOURNAL_SUBSCIBE, propertyInfo,
						new BaseResponseHandler(ctx, false));
				break;

			default:
				break;
			}
		}
	};

	public TravelAdapter(BaseActivity context) {
		this.ctx = context;
	}

	public TravelAdapter(BaseActivity context, FlipViewController controller,
			View rootView) {
		this.ctx = context;
		inflater = LayoutInflater.from(context);
		gd = new GestureDetector(this);
		this.controller = controller;
		this.rootView = rootView;
		if (context instanceof PaperDetailActivity) {
			paperHtmlPaser = new PaperHtmlPaser(context);
			historyAdapter = new HistoryAdapter(context, 2, "mysqllite.db");
		}
		researchAdapter = new ResearchAdapter(context);
	}

	@Override
	public int getCount() {
		return DetailsData.getEntities().size();
	}

	@Override
	public EntityObj getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, final View convertView, ViewGroup parent) {

		LinearLayout layout = (LinearLayout) convertView;
		View progressView = null;
		if (convertView == null) {
			layout = new LinearLayout(ctx);
			progressView = (View) inflater.inflate(R.layout.flipprogress, null);
			layout.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT));
			layout.setGravity(Gravity.CENTER);
			layout.setBackgroundResource(R.drawable.light_bg);
			layout.addView(progressView);
		} else {
			WebView web = (WebView) layout.getChildAt(0);
			progressView = layout.getChildAt(1);
			// if (web != null)
			// web.destroy();
			layout.removeViewAt(0);
		}
		final LinearLayout layoutTemp = layout;
		final View progressViewTemp = progressView;
		final WebView web = createWebView();
		((LinearLayout) layout).addView(web, 0);
		web.setOnTouchListener(this);
		web.setId(100);
		web.setDrawingCacheEnabled(true);
		web.setAlwaysDrawnWithCacheEnabled(true);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(ctx instanceof PaperDetailActivity){
					progressViewTemp.setVisibility(View.VISIBLE);
					web.setVisibility(View.GONE);
					view.loadUrl(url);
				}else{
					Message msg = new Message();
					msg.what = CLICK_ON_URL;
					msg.obj = url;
					handler.sendMessage(msg);
				}
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progressViewTemp.setVisibility(View.GONE);
				web.setVisibility(View.VISIBLE);
			}
			
		});
		web.addJavascriptInterface(new Object() {
			public void journalSubscibe(String journalID) {
				Message msg1 = new Message();
				msg1.what = JOURNAL_SUBSCIBE;
				msg1.obj = journalID;
				handler.sendMessage(msg1);
			}
		}, "journal_info");

		web.setVisibility(View.GONE);
		progressView.setVisibility(View.VISIBLE);
		final View proView = progressView;
		
		web.post(new Runnable() {
			
			@Override
			public void run() {
				EntityObj nn = DetailsData.getEntities().get(position);
				PaperDetailEntity entity = researchAdapter.selectPaperDetail(nn.get(nn.getId()));
				if(entity != null && ctx instanceof PaperDetailActivity){
					int m = DetailsData.entities.lastIndexOf(nn);
					try {
						if (m >= 0) {
							DetailsData.entities.get(m).fieldContents.put("newsDetail",
									entity);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					loadHtml(nn, position, web, proView);
				}else{
					getItem(position, new OnDataSetListener() {

						@Override
						public void onDataSetSuccessListener(EntityObj obj, int position) {
							loadHtml(obj, position, web, proView);
						}

						@Override
						public void onDataSetFailedListener() {
						}
					}, ctx);
				}
			}
		});

		return layout;
	}
	
	public void loadHtml(EntityObj obj, int position, WebView web, View proView){
		if (obj != null) {
			web.setVisibility(View.VISIBLE);
			proView.setVisibility(View.GONE);
			if (ctx instanceof PaperDetailActivity) {
				// DetailsData.tappedne = obj;
				PaperDetailEntity entity = (PaperDetailEntity) obj.fieldContents
						.get("newsDetail");
				int colID = GBApplication.gbapp.getColID(ctx
						.getString(R.string.research));
				Message msg1 = new Message();
				((PaperDetailActivity) ctx).downloadHandler
						.sendMessage(msg1);
				if (entity != null) {
					historyAdapter.storeViewHistory(
							SharedPreferencesMgr.getUserName(),
							entity.id, colID, -1, entity.title);
					paperHtmlPaser.loadHtml(web, entity);
					
					researchAdapter.insertPaperDetail(entity);
				}
			} else {
				HtmlContract.updateWebContent(web, obj,
						inflater.inflate(R.layout.detail_header, null),
						rootView);
			}
			refreshPage();
		}
	}

	/**
	 * 当前视图发生变化时，重新绘制item位图，为翻页动画
	 */
	public void refreshPage() {
		controller.refreshPage(controller.getSelectedItemPosition());
	}

	/**
	 * 当前视图发生变化时，重新绘制上一个item的位图，翻页动画
	 */
	private void refreshPagefirst() {
		if (controller.getSelectedItemPosition() == 0)
			return;
		controller.refreshPage(controller.getSelectedItemPosition() - 1);
	}

	/**
	 * 当前视图发生变化时，重新绘制下一个item的位图，为翻页动画
	 */
	private void refreshPageback() {
		if (controller.getSelectedItemPosition() + 1 == DetailsData.getEntities()
				.size())
			return;
		controller.refreshPage(controller.getSelectedItemPosition() + 1);
	}

	/**
	 * 
	 */
	public void scaleView() {
		if (rootView.findViewById(R.id.counterfeitContent).getVisibility() == View.GONE) {
			rootView.findViewById(R.id.counterfeitContent).setVisibility(
					View.VISIBLE);
			rootView.findViewById(R.id.header_panel)
					.setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.pagebar).setVisibility(View.GONE);
		} else {
			rootView.findViewById(R.id.counterfeitContent).setVisibility(
					View.GONE);
			rootView.findViewById(R.id.header_panel).setVisibility(View.GONE);
		}
		refreshPage();
		refreshPagefirst();
		refreshPageback();
	}

	/**
	 * 获取对应detail数据
	 * 
	 * @param position
	 *            下标
	 * @param listener
	 *            监听器
	 * @param ctx
	 */
	synchronized static void getItem(int position, OnDataSetListener listener,
			BaseActivity ctx) {
		try{
		Log.i("simon", "position>>>>>>>>" + position);
		if (position == DetailsData.getEntities().size() || position == -1) {
			return;
		}
		EntityObj en = DetailsData.getEntities().get(position);
		if (en == null
				|| en.getObject("newsDetail") == null
				|| (ctx instanceof NewsDetailActivity && ((NewsDetail) en
						.getObject("newsDetail")).id == null)
				|| (ctx instanceof PaperDetailActivity && ((PaperDetailEntity) en
						.getObject("newsDetail")).id == null)) {
			DetailsData.fetchDetailsOnSoap(ctx, listener,
					new NewsDetailResponseHandler(ctx, listener, position),
					position);
		} else {
			listener.onDataSetSuccessListener(en, position);
		}}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取detail数据后回调
	 * 
	 * @author simon
	 * 
	 */
	public interface OnDataSetListener {
		void onDataSetSuccessListener(EntityObj obj, int position);

		void onDataSetFailedListener();
	}

	/**
	 * 网络请求回调
	 * 
	 * @author simon
	 * 
	 */
	public static class NewsDetailResponseHandler extends
			AsyncSoapResponseHandler {

		/** 发起请求的BaseActivity 实例 */
		private OnDataSetListener listener;
		private BaseActivity act;
		private int position;

		public NewsDetailResponseHandler(BaseActivity act,
				OnDataSetListener listener, int position) {
			super();
			this.listener = listener;
			this.act = act;
			this.position = position;
		}

		public void onStart() {
			if (act != null) {
				try {
					act.curReqTimes += 1;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		public void onFinish() {
			if (act != null && !act.isFinishing()) {
				act.curReqTimes -= 1;
			}
		}

		public void onSuccess(Object content, int methodId) {
			if (act != null && !act.isFinishing()) {
				if (DetailsData.getEntities().size() != 0) {
					try{
					listener.onDataSetSuccessListener(
							DetailsData.getEntities().get(position), position);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}

		public void onFailure(Throwable error, String content) {
			if (act != null && !act.isFinishing())
				listener.onDataSetFailedListener();
		}
	}

	/***
	 * New WebView
	 * 
	 * @return
	 */
	private WebView createWebView() {
		WebView web = (WebView) LayoutInflater.from(ctx)
				.inflate(R.layout.news_webview, null);
		web.setBackgroundColor(0);
		// web.setWebChromeClient(new MyWebChromeClient());
		WebSettings mWebSettings = web.getSettings();
		mWebSettings.setDefaultFontSize(19);
		// 加上这句话才能使用javascript方法
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setDomStorageEnabled(true);

		return web;
	}

	private float lastPositionX;
	private float lastPositionY;
	private boolean moved;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i("simon", "action_down");
			lastPositionX = event.getX();
			lastPositionY = event.getY();
			return gd.onTouchEvent(event);
		case MotionEvent.ACTION_MOVE:
			Log.i("simon", "action_move");
			float deltaX = lastPositionX - event.getX();
			float deltaY = lastPositionY - event.getY();

			if (Math.abs(deltaY) > 50 && Math.abs(deltaY) > Math.abs(deltaX))
				moved = true;
			return gd.onTouchEvent(event);
		case MotionEvent.ACTION_UP:
			Log.i("simon", "action_up_cancel");
			if (moved) {
				moved = false;
				refreshPage();
			} else {
				if (!(ctx instanceof PaperDetailActivity))
					handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
			}
			lastPositionX = 0;
			lastPositionY = 0;
			return gd.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int a = ((WebView) controller.getSelectedView().findViewById(100))
				.getHeight()
				+ ((WebView) controller.getSelectedView().findViewById(100))
						.getScrollY();
		int b = (int) (((WebView) controller.getSelectedView()
				.findViewById(100)).getContentHeight() * ((WebView) controller
				.getSelectedView().findViewById(100)).getScale());

		if (a == b
				&& rootView.findViewById(R.id.header_panel).getVisibility() == View.GONE) {
			rootView.findViewById(R.id.pagebar).setVisibility(View.VISIBLE);
			handler.sendEmptyMessageDelayed(HIDE_PAGE_BAR, 1000);
		}
		return false;
	}
}
