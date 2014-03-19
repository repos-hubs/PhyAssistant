package com.jibo.app.research;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.BaseSearchActivity;
import com.jibo.activity.RelatedNewsActivity;
import com.jibo.app.push.PushConst;
import com.jibo.asynctask.DownloadAsyncTask1;
import com.jibo.asynctask.DownloaderResume;
import com.jibo.common.Constant;
import com.jibo.common.NetCheckReceiver;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.data.DownloadFullTextPaser;
import com.jibo.data.JournalSubscibePaser;
import com.jibo.data.PaperDetailPaser;
import com.jibo.data.entity.JournalSubscribeEntity;
import com.jibo.data.entity.PaperDetailEntity;
import com.jibo.data.entity.PaperDownloadEntity;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.dbhelper.PaperDownloadAdapter;
import com.jibo.dbhelper.ResearchAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;

/**
 * 论文详细页面
 * @description 
 * @author will
 * @create 2013-3-5 下午3:40:13
 */
public class PaperDetailActivity extends BaseSearchActivity implements
		OnClickListener {
	private View btnDownload, btnFavor, btnFonts, btnShare;
	public static boolean cacheEntered;
	private FavoritDataAdapter favoritAdpt;
	private PaperDownloadAdapter downloadAdpt;
//	public EntityObj currEntity;
	private PaperDetailEntity paperDetailEntity;
	private String paperID, author, jourName, tId, articlesTyp;
	
//	private FlipViewController flipView;
//	private View rootView;
//	private int offset = 0;
//	private TravelAdapter travelAdapter;
	private ResearchAdapter researchAdapter;
	private HistoryAdapter historyAdapter;
	private PaperHtmlPaser paperHtmlPaser;
	
	private List<Object> sizes = new ArrayList<Object>();
	private int indicaterSize = -1;
	
	private boolean isDownload = false;
	
	private WebView webView = null;
	private View progressView = null;
	private static final int JOURNAL_SUBSCIBE = 1;
	
	Handler favorHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
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
	
	public Handler downloadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(null == paperDetailEntity) return;
//			paperDetailEntity = (PaperDetailEntity) DetailsData.getEntities().get(DetailsData.getEntities().lastIndexOf(DetailsData.tappedne)).fieldContents.get("newsDetail");
//			if(paperDetailEntity == null) return;
			if(paperDetailEntity.pdfURL == null || paperDetailEntity.pdfURL.equals("")){
//				btnDownload.setBackgroundDrawable(getResources().getDrawable(R.drawable.download_paper_disabled));
//				btnDownload.setClickable(false);
			}else{
				btnDownload.setClickable(true);
				PaperDownloadEntity entity = downloadAdpt.selectPaperDownloadInfo(PaperDetailActivity.this, SharedPreferencesMgr.getUserName(), paperID);
				if(entity != null && entity.getPaperID() != null){
					isDownload = true;
//					btnDownload.setBackgroundDrawable(getResources().getDrawable(R.drawable.download_paper_selected));
				}else{
					isDownload = false;
//					btnDownload.setBackgroundDrawable(getResources().getDrawable(R.drawable.download_paper_normal));
				}
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paper_detail);
//		if(DetailsData.cacheEntities!=null&&DetailsData.cacheEntities.size()>0)cacheEntered = true;
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		String id = "";
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			id = extras.getString(Constant.ID);
			paperID = id;
		}
		if(paperID==null){
			id = intent.getStringExtra(Constant.ID);
			paperID = id;
		}
		Logs.e(" ---- paperID"+paperID);
//		DetailsData.viewedNews.add(paperID);
		inits();
//		DetailsData.getViewedEntity().add(paperID);
	}
	
	private void inits(){
		favoritAdpt = new FavoritDataAdapter(this);
		downloadAdpt = new PaperDownloadAdapter(this);
		researchAdapter = new ResearchAdapter(this);
		historyAdapter = new HistoryAdapter(this, 2, "mysqllite.db");
		paperHtmlPaser = new PaperHtmlPaser(context);
		
//		flipView = (FlipViewController) this.findViewById(R.id.filpview);
//		rootView = this.getWindow().getDecorView();
//		travelAdapter = new TravelAdapter(this, flipView, rootView);
//		offset = DetailsData.getEntities().lastIndexOf(DetailsData.tappedne);
//		flipView.setAdapter(travelAdapter, offset);
//		flipView.setOnViewFlipListener(new ViewFlipListener() {
//
//			@Override
//			public void onViewFlipped(View view, int position) {
//				try{
//				TextView tx1 = (TextView) rootView.findViewById(R.id.currpage);
//				if (position == 0) {
//					((TextView) rootView.findViewById(R.id.prepage))
//							.setTextColor(Color.WHITE);
//				} else {
//					((TextView) rootView.findViewById(R.id.prepage))
//							.setTextColor(Color.BLACK);
//				}
//				if (position + 1 == DetailsData.getEntities().size()) {
//					((TextView) rootView.findViewById(R.id.nextpage))
//							.setTextColor(Color.WHITE);
//				} else {
//					((TextView) rootView.findViewById(R.id.nextpage))
//							.setTextColor(Color.BLACK);
//				}
//				tx1.setText(position + 1 + "/" + DetailsData.getEntities().size());
//				if(position + 1 > DetailsData.getEntities().size()) return;
//				
//				DetailsData.tappedne = DetailsData.getEntities().get(position);
//				currEntity = DetailsData.tappedne;
//				DetailsData.viewedNews.add(currEntity.get(currEntity.getId()));
//				paperDetailEntity = (PaperDetailEntity) DetailsData.tappedne.fieldContents.get("newsDetail");
//				if(paperDetailEntity == null) return;
//				Message msg = new Message();
//				if (favoritAdpt.selectResearchCollection(paperDetailEntity.id,SharedPreferencesMgr.getUserName()) > 0) {
//					msg.obj = true;
//					favorHandler.sendMessage(msg);
//				}else{
//					msg.obj = false;
//					favorHandler.sendMessage(msg);
//				}
//				
//				if(paperDetailEntity.pdfURL == null || paperDetailEntity.pdfURL.equals("")){
////					btnDownload.setBackgroundDrawable(getResources().getDrawable(R.drawable.download_paper_disabled));
////					btnDownload.setClickable(false);
//				}else{
//					btnDownload.setClickable(true);
//					PaperDownloadEntity entity = downloadAdpt.selectPaperDownloadInfo(PaperDetailActivity.this, SharedPreferencesMgr.getUserName(), paperDetailEntity.id);
//					if(entity != null && entity.getPaperID() != null){
//						isDownload = true;
//					}else{
//						isDownload = false;
//					}
//				}
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//		});
		
		((TextView) findViewById(R.id.txt_header_title)).setText(R.string.research);
		webView = (WebView) findViewById(R.id.webview);
		WebSettings mWebSettings = webView.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setDomStorageEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Intent intent = new Intent(context, PaperDetailLinkActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
				return true;
			}
		});
		webView.addJavascriptInterface(new Object() {
			public void journalSubscibe(String journalID) {
				Message msg1 = new Message();
				msg1.what = JOURNAL_SUBSCIBE;
				msg1.obj = journalID;
				handler.sendMessage(msg1);
			}
		}, "journal_info");
		
		progressView = findViewById(R.id.progress);
		btnFavor = findViewById(R.id.tgbtn_2nd);
		btnDownload = findViewById(R.id.btn_1st);
		btnFonts = findViewById(R.id.btn_3rd);
		btnShare = findViewById(R.id.btn_share);
		btnFonts.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		
		paperDetailEntity = researchAdapter.selectPaperDetail(paperID);	// 读缓存
		if(null != paperDetailEntity){
			webView.post(new Runnable() {
				
				@Override
				public void run() {
					loadHtml();
				}
			});
		}else{
			Properties propertyInfo = new Properties();
			propertyInfo.put("sign", "");
			propertyInfo.put("userId", "");
			propertyInfo.put(SoapRes.KEY_PAPER_ID, paperID);
			this.sendRequest(SoapRes.URLResearchDetail,
					SoapRes.REQ_ID_GET_RESEARCH_DETAIL, propertyInfo,
					new BaseResponseHandler(this, false));
		}
		
		Message msg = new Message();
		if (favoritAdpt.selectResearchCollection(paperID,SharedPreferencesMgr.getUserName()) > 0) {
			msg.obj = true;
			favorHandler.sendMessage(msg);
		}else{
			msg.obj = false;
			favorHandler.sendMessage(msg);
		}
		btnFavor.setOnClickListener(this);
		btnDownload.setOnClickListener(this);
	}
	
	private void loadHtml(){
		webView.setVisibility(View.VISIBLE);
		progressView.setVisibility(View.GONE);
		int colID = GBApplication.gbapp.getColID(this.getString(R.string.research));
		Message msg1 = new Message();
		downloadHandler.sendMessage(msg1);
		if (paperDetailEntity != null) {
			if(PushConst.pushFlag!=null){
				historyAdapter.storeViewHistory(
						SharedPreferencesMgr.getUserName(),
						paperDetailEntity.id, colID, -1, paperDetailEntity.title);				
			}
			paperHtmlPaser.loadHtml(webView, paperDetailEntity);
			researchAdapter.insertPaperDetail(paperDetailEntity);
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(PushConst.pushFlag==null){
			startActivity(new Intent(this,ResearchPageActivity.class));
			finish();
			return;
		}
		super.onBackPressed();
	}

	/** 字体大小 */
	private int getSize() {
		if (indicaterSize == -1) {
//			indicaterSize = sizes.indexOf((((WebView) flipView.getSelectedView()
//					.findViewById(100)))
//					.getSettings().getTextSize());
			indicaterSize = sizes.indexOf(webView.getSettings().getTextSize());
		}
		if (indicaterSize == sizes.size() - 1) {
			indicaterSize = -1;
		}
		return ++indicaterSize;
	}
	
	/** 论文下载*/
	private void downloadPapers(final PaperDownloadEntity entity){
		
		if(downloadAdpt.insertInfo(this, entity)){
			DownloadAsyncTask1 downloadTask = new DownloadAsyncTask1(this,
					DownloadAsyncTask1.s_downLoadID, entity.getUrl(),
					new DownloadAsyncTask1.CallBack() {// 下载论文
						@Override
						public void onFinish(boolean result) {
							if(result){
								downloadAdpt.updateState(context, entity.getPaperID(), PaperDownloadEntity.READ, SharedPreferencesMgr.getUserName());
							}else{
								isDownload = false;
								Toast.makeText(context, context.getString(R.string.losedownload), 1).show();
//								btnDownload.setBackgroundDrawable(getResources().getDrawable(R.drawable.download_paper_normal));
								downloadAdpt.deletePaperDownloadInfo(context, entity.getPaperID(), SharedPreferencesMgr.getUserName());
							}
						}
					}, entity.getPaperID());
			downloadTask.execute();
		}
	}
	
//	@Override
//	public void finish() {
//		super.finish();
//		DetailsData.clearCacheEntities();
//		cacheEntered = false;
//	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		super.onReqResponse(o, methodId);
		if(o instanceof JournalSubscibePaser){
			JournalSubscibePaser paser = (JournalSubscibePaser) o;
			JournalSubscribeEntity entity= paser.entity;
			Log.i("will", "sendResponse");
			if(entity.resCode.equals("200")){
//				paperDetailEntity = (PaperDetailEntity) DetailsData.getEntities().get(DetailsData.getEntities().lastIndexOf(DetailsData.tappedne)).fieldContents.get("newsDetail");
//				WebView web = ((WebView) flipView.getSelectedView()
//						.findViewById(100));
				boolean isSubscribed = researchAdapter.isSubscribed(paperDetailEntity.journalID, SharedPreferencesMgr.getUserName());
				if(isSubscribed){
					webView.loadUrl("javascript:setSubscribeButtonStatus('"+ !isSubscribed +"')");
					researchAdapter.deleteSubscribe(paperDetailEntity.journalID, SharedPreferencesMgr.getUserName());
					Toast.makeText(context, getString(R.string.journal_un_subscribe), Toast.LENGTH_SHORT).show();
					MobclickAgent.onEvent(context, "journal_un_subscribe");
				}else{
					webView.loadUrl("javascript:setSubscribeButtonStatus('"+ !isSubscribed +"')");
					researchAdapter.insertSubscribe(paperDetailEntity.journalID, SharedPreferencesMgr.getUserName());
					Toast.makeText(context, getString(R.string.journal_subscribe), Toast.LENGTH_SHORT).show();
					MobclickAgent.onEvent(context, "journal_subscribe");
				}
				Log.i("will", "sendResponse");
			}
		}else if(o instanceof PaperDetailPaser){
			paperDetailEntity = ((PaperDetailPaser) o).entity;
			webView.post(new Runnable() {

				@Override
				public void run() {
					loadHtml();
				}
			});
		}
	}

	private void startDownload(){
		if(isDownload){
			Toast.makeText(context, getString(R.string.paper_downloading), Toast.LENGTH_SHORT).show();
		}
		if(paperDetailEntity == null || isDownload) return;
		MobclickAgent.onEvent(context, "research_detail_download");
		PaperDownloadEntity entity = new PaperDownloadEntity();
		//TODO
		paperDetailEntity.pdfURL = "http://192.168.0.60/download/Version_UpdateAndroid/Jibo.zip";
		if(paperDetailEntity.pdfURL == null){
			Toast.makeText(context, getString(R.string.paper_no_download), Toast.LENGTH_SHORT).show();
			return;
		}
		isDownload = true;
		Toast.makeText(context, getString(R.string.paper_downloading), Toast.LENGTH_SHORT).show();
		entity.setPaperID(paperDetailEntity.id);
		entity.setTitle(paperDetailEntity.title);
		entity.setRemarks("notes");
		entity.setUrl(paperDetailEntity.pdfURL);
		entity.setState(PaperDownloadEntity.PENDING);
		entity.setUsername(SharedPreferencesMgr.getUserName().trim());
		entity.setPeriodicalTitle(paperDetailEntity.journalName);
		entity.setIFCount(paperDetailEntity.IF);
		entity.setDate(paperDetailEntity.publicDate);
		if(paperDetailEntity.pdfURL != null && !paperDetailEntity.pdfURL.trim().equals(""))
			entity.setFileName(paperDetailEntity.pdfURL.substring(paperDetailEntity.pdfURL.lastIndexOf("/")+1));
//		downloadPapers(entity);
		downloadAdpt.insertInfo(PaperDetailActivity.this, entity);
		new RequestDownloadURLThread(entity).start();
//		if(!TextUtils.isEmpty(entity.getUrl())){
//			entity.setFileName(entity.getUrl().substring(entity.getUrl().lastIndexOf("/")+1));
//			startDownload(paperDetailEntity.pdfURL, paperDetailEntity.id, entity);
//		}
	}
	
	public class RequestDownloadURLThread extends Thread{
		private PaperDownloadEntity entity;
//		private boolean isBreak = false;
		private String paperID;
		private String urlstr;
		private String resCode;
		
		public RequestDownloadURLThread(PaperDownloadEntity entity){
			super();
			this.entity = entity;
			this.paperID = entity.getPaperID();
		}

		@Override
		public void run() {
			super.run();
//			while(!isBreak){
				
				Properties propertyInfo = new Properties();
				propertyInfo.put("sign", "");
				propertyInfo.put(SoapRes.KEY_FULLTEXT_USER_NAME ,
						SharedPreferencesMgr.getUserName());
				propertyInfo.put(SoapRes.KEY_FULLTEXT_PAPER_ID, paperID);
				propertyInfo.put(SoapRes.KEY_FULLTEXT_CULTURE , "en-US");
				PaperDetailActivity.this.sendRequest(SoapRes.URLResearchDetail,
						SoapRes.REQ_ID_DOWNLOAD_FULLTEXT, propertyInfo,
						new BaseResponseHandler(PaperDetailActivity.this, false){

							@Override
							public void onSuccess(Object content, int methodId) {
								super.onSuccess(content, methodId);
								DownloadFullTextPaser data = (DownloadFullTextPaser) content;
								if(data != null){
									resCode = data.resCode;
									if(resCode.trim().equals(DownloadFullTextPaser.ERROR_CODE_NORES)){
										downloadAdpt.updateState(PaperDetailActivity.this, paperID, PaperDownloadEntity.FAILURE, entity.getUsername());
									}else if(resCode.trim().equals(DownloadFullTextPaser.SUCCESS_CODE)){
										urlstr = data.url;
										startDownload(urlstr, paperID, entity);
									}
								}else{
									downloadAdpt.updateState(PaperDetailActivity.this, paperID, PaperDownloadEntity.FAILURE, entity.getUsername());
								}
							}
					
				});
//				try {
//					Thread.sleep(1000 * 60);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
		}
		
//		public void setBreak(){
//			isBreak = true;
//		}
		
	}
	
	private void startDownload(String url, final String specialID, PaperDownloadEntity entity) {
		String urlstr = url + ".zip";
		String fileName = urlstr.substring(urlstr.lastIndexOf("=") + 1);
		String localfile = CollectionActivity.SD_PATH + fileName;
		downloadAdpt.updateFilename(this, specialID, url, fileName, SharedPreferencesMgr.getUserName());
		// 设置下载线程数为10
		int threadcount = 1 ;
		// 初始化一个downloader下载器
		DownloaderResume downloader = Util.downloaders.get(url);
		if (downloader == null) {
			downloader = new DownloaderResume(url, specialID, localfile, threadcount, PaperDetailActivity.this);
			Util.downloaders.put(url, downloader);
		}
		if (downloader.isdownloading())
			return;
		downloadAdpt.updateState(PaperDetailActivity.this, specialID, PaperDownloadEntity.DOWNLOADING, entity.getUsername());
		// 得到下载信息类的个数组成集合
		downloader.getDownloaderInfors();
		// 调用方法开始下载
		downloader.download();
//		downloadAdpt.insertInfo(this, entity);
	}
	
	@Override
	public void onClick(View v) {
//		int length = DetailsData.getEntities().size();
//		int idx = DetailsData.getEntities().lastIndexOf(DetailsData.tappedne);
//		if(idx==-1||length<idx){
//			return;
//		}
//		paperDetailEntity = (PaperDetailEntity) DetailsData.getEntities().get(DetailsData.getEntities().lastIndexOf(DetailsData.tappedne)).fieldContents.get("newsDetail");
		if(null == paperDetailEntity) return;
		switch (v.getId()) {
		case R.id.btn_1st:
			PaperDownloadEntity entity = downloadAdpt
			.selectPaperDownloadInfo(context,
					SharedPreferencesMgr.getUserName(),
					paperID);
			if(!TextUtils.isEmpty(paperDetailEntity.pdfURL) && entity == null){
				new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.research_paper_available))
				.setItems(R.array.download_available,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							if(NetCheckReceiver.isWifi(context)){
								startDownload();
							}else{
								new AlertDialog.Builder(context)
						        .setTitle(getString(R.string.wifi_tips))
						        .setMessage(getString(R.string.wifi_fulltext))
						        .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
						            public void onClick(DialogInterface dialog, int whichButton) {
						            	dialog.cancel();
						            	startDownload();
						            }
						        })
						        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
						            public void onClick(DialogInterface dialog, int whichButton) {
						            	dialog.cancel();
						            }
						        })
						        .create().show();
							}
							break;
						case 1:
							openPDFReader();
							break;

						default:
							break;
						}
					}
				}).setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
					}
				}).show();
			}else if(!TextUtils.isEmpty(paperDetailEntity.pdfURL) && entity != null){
				new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.research_paper_available))
				.setItems(R.array.download_available_view,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							openPDFReader();
							break;

						default:
							break;
						}
					}
				}).setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
					}
				}).show();
			}else{
				new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.research_paper_unavailable))
				.setItems(R.array.download_not_available,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Properties propertyInfo = new Properties();
							propertyInfo.put(SoapRes.KEY_SING, "");
							propertyInfo.put(SoapRes.KEY_FULLTEXT_REQUEST_USER_NAME, SharedPreferencesMgr.getUserName());
							propertyInfo.put(SoapRes.KEY_FULLTEXT_REQUEST_PAPER_ID, paperID);
							PaperDetailActivity.this.sendRequest(SoapRes.URLResearchDetail,
									SoapRes.REQ_ID_REQUEST_FULLTEXT, propertyInfo,
									new BaseResponseHandler(PaperDetailActivity.this, false));
							
							new AlertDialog.Builder(context)
							.setTitle(getResources().getString(R.string.research_request_fulltext))
							.setMessage(getResources().getString(R.string.research_request_info))
							.setNegativeButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
							break;

						default:
							break;
						}
					}
				}).setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
					}
				}).show();
			}
			break;
			
		case R.id.tgbtn_2nd:
			MobclickAgent.onEvent(context, "research_detail_favorite");
			Boolean shine = null;
			uploadLoginLogNew("Research","ArtFavoritBtn","Favorite",null);
			if(paperDetailEntity != null){
				if (favoritAdpt.selectResearchCollection(paperDetailEntity.id,SharedPreferencesMgr.getUserName()) > 0) {
					if (favoritAdpt.delResearchCollection(paperDetailEntity.id,SharedPreferencesMgr.getUserName())) {
						Toast toast = Toast.makeText(context, context
								.getString(R.string.cancelFav), Toast.LENGTH_LONG);
						toast.show();
						shine = false;
					}

				} else {
					favoritAdpt.insertTableResearchCollection(paperDetailEntity.id, paperDetailEntity.title,
							paperDetailEntity.journalAbbrName, paperDetailEntity.journalName, paperDetailEntity.IF, paperDetailEntity.publicDate, 
							paperDetailEntity.pdfURL, paperDetailEntity.isFreeFullText+"",SharedPreferencesMgr.getUserName());
					Toast toast = Toast.makeText(context, context
							.getString(R.string.favorite), Toast.LENGTH_LONG);
					toast.show();
					shine = true;
				}
				Message msg = new Message();
				msg.obj = (Boolean) shine;
				favorHandler.sendMessage(msg);
			}
			break;
			
		case R.id.btn_3rd:
			MobclickAgent.onEvent(context, "research_detail_font");
			if (sizes.isEmpty()) {
				sizes.add(TextSize.NORMAL);
				sizes.add(TextSize.LARGER);
				sizes.add(TextSize.LARGEST);
			}

//			WebView web = ((WebView) flipView.getSelectedView()
//					.findViewById(100));
			if (webView.getVisibility() == View.VISIBLE) {
				webView.getSettings().setTextSize((TextSize) sizes.get(getSize()));
				webView.postInvalidateDelayed(1000);
			}
			break;
			
		case R.id.btn_share:
			MobclickAgent.onEvent(context, "research_detail_share");
//			WebView webView = ((WebView) flipView.getSelectedView()
//					.findViewById(100));
			if (webView.getVisibility() == View.GONE || paperDetailEntity == null)
				return;
//			String text = "";
//			if(paperDetailEntity.abstarct != null){
//				text = paperDetailEntity.abstarct;
//			}
//			Spanned content = Html.fromHtml(text);
			String title = paperDetailEntity.title;

			String content ="[" + getString(R.string.app_name) + "]"
					+ getString(R.string.research)
					+ getString(R.string.paper_channel) + ":\n"
					+ title
					+ "\n     " + getString(R.string.paper_interlinking)
					+ context.getString(R.string.paper_share_url);
//					+ context.getString(R.string.share_url)
//					+ "40."
//					+ "."
//					+ SharedPreferencesMgr.getUserID() + ".";
			int len = content.length()-140;
			Logs.i("+++a"+content.length());
			if(len>0){
				content = "[" + getString(R.string.app_name) + "]"
						+ getString(R.string.research)
						+ getString(R.string.paper_channel) + ":\n"
						+ title.substring(0,title.length()-len-4)+"..."
						+ "\n     " + getString(R.string.paper_interlinking)
						+ context.getString(R.string.paper_share_url);
			}
			RelatedNewsActivity.sharing_inf = content;
			Logs.i("==="+len);
			Logs.i("+++b"+content.length());
			sharing(R.array.items2, 3);
			break;

		default:
			break;
		}
	}
	
	private void openPDFReader() {
		PaperDownloadEntity entity = downloadAdpt
				.selectPaperDownloadInfo(context,
						SharedPreferencesMgr.getUserName(),
						paperID);
		if(entity == null){
			Toast.makeText(this, R.string.research_detail_read_1, Toast.LENGTH_SHORT).show();
			return;
		}
		if (!entity.getState().equals(PaperDownloadEntity.READ)){
			Toast.makeText(this, R.string.research_detail_read_2, Toast.LENGTH_SHORT).show();
			return;
		}
//		File file = new File(Constant.PAPER_DOWNLOAD + "/"
//				+ SharedPreferencesMgr.getUserName() + "/"
//				+ entity.getFileName());
//		if (file.exists()) {
//			Uri path = Uri.fromFile(file);
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setDataAndType(path, "application/pdf");
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			try {
//				startActivity(intent);
//			} catch (ActivityNotFoundException e) {
//				Toast.makeText(this,
//						"No Application Available to View PDF",
//						Toast.LENGTH_SHORT).show();
//			}
//		}
		
		String fileDir = CollectionActivity.SD_PATH + "/" + entity.getPaperID().trim() + "/" + entity.getPaperID().trim() + ".xml";
		File file = new File(fileDir);
		if(file.exists()){
			Intent intent = new Intent(context, SpecialDetailActivity.class);
			intent.putExtra("articalHtmlDir", fileDir);
			context.startActivity(intent);
		}
	}
	
	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case JOURNAL_SUBSCIBE:
				String journalID = (String) msg.obj;
				Properties propertyInfo = new Properties();
				propertyInfo.put("sign", "");
				propertyInfo.put(SoapRes.KEY_SUBSCIBE_USER_NAME,
						SharedPreferencesMgr.getUserName());
				propertyInfo.put(SoapRes.KEY_SUBSCIBE_PERIODICAL_ID, journalID);
				propertyInfo.put(SoapRes.KEY_SUBSCIBE_STATUS, "1");
				PaperDetailActivity.this.sendRequest(SoapRes.URLResearchDetail,
						SoapRes.REQ_ID_JOURNAL_SUBSCIBE, propertyInfo,
						new BaseResponseHandler(PaperDetailActivity.this, false));
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
