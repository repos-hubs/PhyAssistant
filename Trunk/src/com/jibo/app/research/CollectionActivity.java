package com.jibo.app.research;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.HistoryFavoriteActivity;
import com.jibo.app.AdaptUI;
import com.jibo.app.ArticleActivity;
import com.jibo.app.DetailsData;
import com.jibo.app.GBIRequest;
import com.jibo.app.updatespot.SpotUtil;
import com.jibo.asynctask.DownloaderResume;
import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.request.RequestInfos;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.base.src.request.ScrollCounter;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.common.Constant;
import com.jibo.common.NetCheckReceiver;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.data.DownloadFullTextPaser;
import com.jibo.data.SpecialCollectionPaser;
import com.jibo.data.entity.CollectionEntity;
import com.jibo.data.entity.DownloadInfo;
import com.jibo.data.entity.DownloadPaperEntity;
import com.jibo.data.entity.DownloadProgressInfo;
import com.jibo.data.entity.PaperDownloadEntity;
import com.jibo.data.entity.SpecialCollectionListEntity;
import com.jibo.dbhelper.DownloadResumeDao;
import com.jibo.dbhelper.FavoritDataAdapter;
import com.jibo.dbhelper.PaperDownloadAdapter;
import com.jibo.dbhelper.SpecialListDao;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.ActivityPool;
import com.jibo.util.SharedPreferenceUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @description
 * @author will
 * @create 2013-2-27 下午2:11:17
 */
public class CollectionActivity extends ArticleActivity implements
		OnClickListener {
	private static final String SUBSCRIPTED = "subscripted";
	private ListView listView;
	private TextView categoryNameText;
	private View footerView;
	private View emptyView;
	private FavoritDataAdapter researchAdapter;
	private PaperDownloadAdapter downloadAdapter;
	
	private DownloadResumeDao downloadDao;
	private SpecialListDao specialListDao;
	public static final String SD_PATH = Constant.PAPER_DOWNLOAD + "/" + SharedPreferencesMgr.getUserName() + "/";
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				String url = (String) msg.obj;
				Bundle data = msg.getData();
				if(data.getString("isDone") != null && data.getString("isDone").equals("yes")){
					Toast.makeText(CollectionActivity.this, getString(R.string.download_success), 0).show();
					// 下载完成后清除进度条并将map中的数据清空
					Util.ProgressBars.remove(url);
					String specialID = data.getString("specialID");
//					downloadDao.updateDownloadState(specialID);
					for(int i=0; i<collectionListAdapter.specialList.size(); i++){
						if(collectionListAdapter.specialList.get(i).getSpecialID().equals(specialID)){
							collectionListAdapter.specialList.get(i).setDownloadState(true);
						}
					}
					collectionListAdapter.notifyDataSetChanged();
					//	downloaders.get(url).delete(url);
					//	downloaders.get(url).reset();
//					Util.downloaders.remove(url);
				}
			}
			if(msg.what == 2){
				String url = (String) msg.obj;
				Bundle data = msg.getData();
				if(data.getString("isDone") != null && data.getString("isDone").equals("yes")){
					Toast.makeText(CollectionActivity.this, getString(R.string.download_success), 0).show();
					String specialID = data.getString("specialID");
//					downloadDao.updateDownloadState(specialID);
//					downloadAdapter.updateState(context, specialID, PaperDownloadEntity.READ, SharedPreferencesMgr.getUserName());
					for(int i=0; i<downloadListAdapter.list.size(); i++){
						if(downloadListAdapter.list.get(i).getId().equals(specialID)){
							downloadListAdapter.list.get(i).setState(PaperDownloadEntity.READ);
						}
					}
					downloadListAdapter.notifyDataSetChanged();
//					Util.downloaders.remove(url);
				}
			}
			if(msg.what == 3){
				downloadListAdapter.notifyDataSetChanged();
			}
			// 更新厂商专辑包ui
			if(msg.what == 4){
				Bundle data = msg.getData();
				DownloadProgressInfo loadInfo = data.getParcelable("loadinfo");
				String urlstr = data.getString("urlstr");
				int index = data.getInt("index");
				// 显示进度条
				showProgress(loadInfo, urlstr, index);
				// 调用方法开始下载
				DownloaderResume downloader = Util.downloaders.get(urlstr);
				downloader.download();
			}
		}
	};
	
	private CollectionListAdapter collectionListAdapter;
	private ResearchAdapter favoriteListAdapter;
	private DownloadListAdapter downloadListAdapter;
	private ArrayList<CollectionEntity> collectionList;
	private ArrayList<DownloadInfo> specialList;
	private ArrayList<DownloadPaperEntity> downloadPaperList;

	public static Context mContext;
	private RequestController srcRequests;
	private View view;
	private boolean enteredSubScripted;
	private View downloadFavor;
	private List<ScrollCounter> count;
	private MapAdapter itemAdapter;
	private Handler itemHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			itemAdapter.notifyDataSetChanged();
		}

	};

	@Override
	public void start() {
		downloadFavor = LayoutInflater.from(context).inflate(
				R.layout.research_collection_listview, null);
		setContentView(downloadFavor);
		inits();
		if (count == null) {
			count = new ArrayList<ScrollCounter>();
			count.add(new ScrollCounter(20, 1));
			count.add(new ScrollCounter(10, -1));
		}
		emptyView = LayoutInflater.from(context).inflate(R.layout.empty_frame,
				null);

	}

	private void inits() {
		researchAdapter = new FavoritDataAdapter(this);
		downloadAdapter = new PaperDownloadAdapter(this);
		downloadDao = new DownloadResumeDao(this);
		specialListDao = new SpecialListDao(this);

		footerView = View.inflate(this, R.layout.dialogprogress, null);

		categoryNameText = (TextView) findViewById(R.id.chooseCategory);
		categoryNameText.setVisibility(View.GONE);
		categoryNameText.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.lst_item);
		collectionListAdapter = new CollectionListAdapter(this);
		listView.setAdapter(collectionListAdapter);
		listView.setOnItemClickListener(new CollectionItemClickListener());
		listView.setOnItemLongClickListener(null);
		initCollectionEntity();
		mContext = CollectionActivity.this;
	}

	private void showFavoriteList() {
		
		favoriteListAdapter = new ResearchAdapter(GBApplication.gbapp, AdaptUI.adaptInfo,false,
				DetailsData.viewedNews);
		ArrayList<EntityObj> list = researchAdapter.selectResearchc(this,
				SharedPreferencesMgr.getUserName(), null);
		if (list.size() == 0 || list == null) {
			//fix The specified child already has a parent bug -- start by Teddy 2013-06-17
			emptyView = LayoutInflater.from(context).inflate(R.layout.empty_frame, null);
			//fix The specified child already has a parent bug -- end by Teddy 2013-06-17
			((TextView) emptyView.findViewById(R.id.emptytext))
					.setText(R.string.favorite_empty);
			setContentView(emptyView);
		}
		favoriteListAdapter.setItemDataSrc(new AdapterSrc(list));
		listView.setAdapter(favoriteListAdapter);
		listView.setOnItemClickListener(new FavoriteItemClickListener());
		listView.setOnItemLongClickListener(null);
		categoryNameText.setVisibility(View.VISIBLE);
		categoryNameText.setText(getResources().getString(
				R.string.collection_favourit));
	}

	private void showDownLoadList() {
		downloadListAdapter = new DownloadListAdapter(this);
		downloadPaperList = new ArrayList<DownloadPaperEntity>();
		listView.setAdapter(downloadListAdapter);
		initDownloadEntity();
		listView.setOnItemClickListener(new DownLoadItemClickListener());
		listView.setOnItemLongClickListener(new DownLoadItemLongClickListener());
		
		if (downloadPaperList.size() == 0 || downloadPaperList == null) {
			//fix The specified child already has a parent bug -- start by Teddy 2013-06-17
			emptyView = LayoutInflater.from(context).inflate(R.layout.empty_frame, null);
			//fix The specified child already has a parent bug -- end by Teddy 2013-06-17
			((TextView) emptyView.findViewById(R.id.emptytext))
					.setText(R.string.download_empty);
			setContentView(emptyView);
		}

		categoryNameText.setVisibility(View.VISIBLE);
		categoryNameText.setText(getResources().getString(
				R.string.collection_download));

		mContext = CollectionActivity.this;
//		if (DownloadAsyncTask1.contextInfo != null) {
//			DownloadAsyncTask1.contextInfo.getContext(mContext);
//		}
	}

	private void initCollectionEntity() {
		collectionList = new ArrayList<CollectionEntity>();
		CollectionEntity entity = new CollectionEntity();
		entity.setTitle(getResources().getString(R.string.collection_favourit));
		entity.setImageId(R.drawable.ic_launcher);
		collectionList.add(entity);
		entity = new CollectionEntity();
		entity.setTitle(getResources().getString(R.string.collection_download));
		entity.setImageId(R.drawable.ic_launcher);
		collectionList.add(entity);
		entity = new CollectionEntity();
		entity.setTitle(getResources().getString(R.string.collection_subscibe));
		entity.setImageId(R.drawable.ic_launcher);
		collectionList.add(entity);
		
		showSpecialList();

		collectionListAdapter.list = collectionList;
	}
	
	/**
	 * 加载专辑包列表
	 */
	private void showSpecialList(){
		if(!specialListDao.isHasInfors()){
			if(listView.getFooterViewsCount() <= 0)
			listView.addFooterView(footerView,null,false);
			listView.setAdapter(collectionListAdapter);
		}else{
			specialList = new ArrayList<DownloadInfo>();
			ArrayList<SpecialCollectionListEntity> list = new ArrayList<SpecialCollectionListEntity>();
			list = specialListDao.getInfos();
			loadSpecialColltion(list);
		}
		collectionListAdapter.notifyDataSetChanged();
		
		Properties propertyInfo = new Properties();
		propertyInfo.put(SoapRes.KEY_SING, "");
		propertyInfo.put(SoapRes.KEY_SPECIAL_USER_NAME,
				SharedPreferencesMgr.getUserName());
		propertyInfo.put(SoapRes.KEY_SPECIAL_INVITE_CODE, SharedPreferencesMgr.getInviteCode());
		propertyInfo.put(SoapRes.KEY_SPECIAL_DEPARTMENT, "");
		sendRequest(SoapRes.URLResearchDetail,
				SoapRes.REQ_ID_SPECIAL_COLLECTION, propertyInfo,
				new BaseResponseHandler(this, false));
//		specialList = new ArrayList<DownloadInfo>();
//		DownloadInfo entity = new DownloadInfo();
//		entity.setTitle("小专辑包");
//		entity.setSpecialID("1");
//		entity.setUrl("http://192.168.0.60/download/Version_UpdateAndroid/Special_daiichi.zip");
//		DownloadInfo downloadEntity = downloadDao.getInfo(entity.getSpecialID());
//		if(downloadEntity!=null){
//			entity.setDownloadState(downloadEntity.isDownloadState());
//			entity.setCompeleteSize(downloadEntity.getCompeleteSize());
//			entity.setFileSize(downloadEntity.getFileSize());
//		}else{
//			entity.setDownloadState(false);
//		}
//		specialList.add(entity);
//		
//		entity = new DownloadInfo();
//		entity.setTitle("小小专辑包");
//		entity.setSpecialID("2");
//		entity.setUrl("http://192.168.0.60/download/Version_UpdateAndroid/Special_lilly.zip");
//		DownloadInfo downloadEntity1 = downloadDao.getInfo(entity.getSpecialID());
//		if(downloadEntity1!=null){
//			entity.setDownloadState(downloadEntity1.isDownloadState());
//			entity.setCompeleteSize(downloadEntity1.getCompeleteSize());
//			entity.setFileSize(downloadEntity1.getFileSize());
//		}else{
//			entity.setDownloadState(false);
//		}
//		specialList.add(entity);
//		for(int i=0; i<specialList.size(); i++){
//			if(Util.downloaders.containsKey(specialList.get(i).getUrl())){
//				Util.downloaders.get(specialList.get(i).getUrl()).setHandler(mHandler);
//				break;
//			}
//		}
//		collectionListAdapter.specialList = specialList;
	}

	private void initDownloadEntity() {
		ArrayList<PaperDownloadEntity> downloadList = downloadAdapter
				.getAllPaperDownloadInfo(context, SharedPreferencesMgr
						.getUserName().trim());
		for (int i = 0; i < downloadList.size(); i++) {
			DownloadPaperEntity entity = new DownloadPaperEntity();
			entity.setId(downloadList.get(i).getPaperID());
			entity.setTitle(downloadList.get(i).getTitle());
			entity.setNotes(downloadList.get(i).getRemarks());
			entity.setState(downloadList.get(i).getState());
			entity.setImageId(R.drawable.research_paper_pending);
			entity.setFileName(downloadList.get(i).getFileName());
			entity.setUrl(downloadList.get(i).getUrl());
			entity.setPeriodicalTitle(downloadList.get(i).getPeriodicalTitle());
			entity.setIFCount(downloadList.get(i).getIFCount());
			entity.setDate(downloadList.get(i).getDate());
			downloadPaperList.add(entity);
			if(Util.downloaders.containsKey(downloadList.get(i).getUrl())){
				Util.downloaders.get(downloadList.get(i).getUrl()).setHandler(mHandler);
			}
		}
		downloadListAdapter.list = downloadPaperList;
	}

	@Override
	public void setDownloadProgress(int progress, String url, String id) {
		if (listView.getAdapter() instanceof DownloadListAdapter) {
			for (int i = 0; i < downloadListAdapter.list.size(); i++) {
				if (id.equals(downloadListAdapter.list.get(i).getId())) {
					downloadListAdapter.list.get(i).setProgressRate(progress);
				}
			}
//			downloadListAdapter.notifyDataSetChanged();
			Message msg = Message.obtain();
			msg.what = 3;
			mHandler.sendMessage(msg);
		}
		super.setDownloadProgress(progress, url, id);
	}
	
	

	@Override
	public void setDownloadProgress(long completeSize, String title, String id) {
		if(listView.getAdapter() instanceof CollectionListAdapter ||
				((listView.getAdapter() instanceof HeaderViewListAdapter) && 
				((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter() instanceof CollectionListAdapter)){
			for (int i = 0; i < collectionListAdapter.specialList.size(); i++) {
				if (id.equals(collectionListAdapter.specialList.get(i).getSpecialID())) {
					collectionListAdapter.specialList.get(i).setCompeleteSize(completeSize);
					collectionListAdapter.setData(collectionListAdapter.specialList);
					if(collectionListAdapter.specialList.get(i).getFileSize() != 0){
						ProgressBar bar = (ProgressBar)listView.getChildAt(i+3).findViewById(R.id.download_progress);
						bar.setProgress((int)((collectionListAdapter.specialList.get(i).getCompeleteSize()*100)/collectionListAdapter.specialList.get(i).getFileSize()));
					}
//					collectionListAdapter.notifyDataSetChanged();
				}
			}
		}
		super.setDownloadProgress(completeSize, title, id);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(collectionListAdapter != null)
			collectionListAdapter.notifyDataSetChanged();
	}
	
	private void loadSpecialColltion(ArrayList<SpecialCollectionListEntity> list){
		if(listView.getFooterViewsCount() > 0){
			listView.removeFooterView(footerView);
		}
		if(list == null || list.size() <= 0){
			collectionListAdapter.specialList = new ArrayList<DownloadInfo>();
			collectionListAdapter.notifyDataSetChanged();
		}
		if(list != null && list.size() > 0){
			specialList = new ArrayList<DownloadInfo>();
			for(int i=0; i<list.size(); i++){
				DownloadInfo entity = new DownloadInfo();
				entity.setTitle(list.get(i).name);
				entity.setSpecialID(list.get(i).key);
				entity.setUrl(list.get(i).downloadLink);
				DownloadInfo downloadEntity = downloadDao.getInfo(entity.getSpecialID());
				if(downloadEntity!=null){
					entity.setDownloadState(downloadEntity.isDownloadState());
					entity.setCompeleteSize(downloadEntity.getCompeleteSize());
					entity.setFileSize(downloadEntity.getFileSize());
				}else{
					entity.setDownloadState(false);
				}
				specialList.add(entity);
			}
			for(int i=0; i<specialList.size(); i++){
				if(Util.downloaders.containsKey(specialList.get(i).getUrl())){
					Util.downloaders.get(specialList.get(i).getUrl()).setHandler(mHandler);
					break;
				}
			}
			collectionListAdapter.specialList = specialList;
			collectionListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		super.onReqResponse(o, methodId);
		if(o instanceof SpecialCollectionPaser){
			ArrayList<SpecialCollectionListEntity> list = new ArrayList<SpecialCollectionListEntity>();
			list = ((SpecialCollectionPaser) o).list;
			loadSpecialColltion(list);
			specialListDao.delete();
			specialListDao.saveInfos(list);
			collectionListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!escape()) {
				this.finishParentClass();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public int getEmptyText() {
		return R.string.empty_result;
	}

	public boolean escape() {
		if (listView.getAdapter() instanceof DownloadListAdapter
				|| listView.getAdapter() instanceof ResearchAdapter
				|| enteredSubScripted) {
			listView.setAdapter(collectionListAdapter);
			listView.setOnItemClickListener(new CollectionItemClickListener());
			listView.setOnItemLongClickListener(null);
			categoryNameText.setVisibility(View.GONE);
			collectionListAdapter.notifyDataSetChanged();
			enteredSubScripted = false;
			this.setContentView(downloadFavor);
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chooseCategory:
//			onKeyDown(KeyEvent.KEYCODE_BACK, null);
			break;

		default:
			break;
		}
	}

	@Override
	public void onBack(Boolean stayTop, boolean isBackKey) {
		
		super.onBack(stayTop, isBackKey);
	}

	private class CollectionItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, final View arg1, final int arg2,
				long arg3) {
			DetailsData.clearCacheEntities();
			switch (arg2) {
			case 0:
				showFavoriteList();
				MobclickAgent.onEvent(context, "research_others_favor_click");
				uploadLoginLogNew("research",
				           "others", "favor",
				           null);
				
				break;
			case 1:
				showDownLoadList();
				MobclickAgent.onEvent(context, "research_others_download_click");
				uploadLoginLogNew("research",
				           "others", "download",
				           null);
				break;
			case 2:
				toSubsrriptions();
				break;
			default:
				if(arg2 < 3) return;
				final int po = arg2 - 3;
				if(downloadDao.isHasInfors(specialList.get(po).getUrl())){
					showDialog(po, arg1, arg2, R.string.research_download_info, 1);
				}else if(!downloadDao.isHasInfors(specialList.get(po).getUrl()) 
						&& !downloadDao.getDownloadState(specialList.get(po).getSpecialID())
						&& Util.downloaders.containsKey(specialList.get(po).getUrl())){
					if(Util.downloaders.get(specialList.get(po).getUrl()).getDownloadState()
							!= DownloaderResume.PAUSE){
						showDialog(po, arg1, arg2, R.string.research_download_pause, 2);
					}else{
						showDialog(po, arg1, arg2, R.string.research_download_consume, 3);
					}
				}else if(!downloadDao.isHasInfors(specialList.get(po).getUrl()) 
						&& !downloadDao.getDownloadState(specialList.get(po).getSpecialID())
						&& !Util.downloaders.containsKey(specialList.get(po).getUrl())){
					showDialog(po, arg1, arg2, R.string.research_download_consume, 3);
				}else{
					Intent intent = new Intent(context, SpecialListActivity.class);
					String specialDir = getSpecialDir(arg2);
					intent.putExtra("specialDir", specialDir);
					startActivity(intent);
				}
				break;
			}
		}
		
		private void showDialog(final int po, final View arg1, final int arg2, int stringID, final int state){
			new AlertDialog.Builder(context)
			.setTitle(getResources().getString(R.string.research_download))
			.setMessage(getResources().getString(stringID))
			.setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					arg1.post(new Runnable() {
						@Override
						public void run() {
							switch (state) {
							case 1:
							case 3:
								if(NetCheckReceiver.isWifi(context)){
									arg1.post(new Runnable() {
										@Override
										public void run() {
											startDownload(specialList.get(po).getUrl(), specialList.get(po).getSpecialID(), specialList.get(po).getTitle(), arg2);
										}
									});
								}else{
									new AlertDialog.Builder(context)
							        .setTitle(getString(R.string.wifi_tips))
							        .setMessage(getString(R.string.wifi_fulltext))
							        .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
							            public void onClick(DialogInterface dialog, int whichButton) {
							            	dialog.cancel();
							            	arg1.post(new Runnable() {
												@Override
												public void run() {
													startDownload(specialList.get(po).getUrl(), specialList.get(po).getSpecialID(), specialList.get(po).getTitle(), arg2);
												}
											});
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
							case 2:
								pauseDownload(specialList.get(po).getUrl());
								break;
							default:
								break;
							}
						}
					});
				}
			})
			.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
				}
			}).show();
		}

	}
	
	
	
	private String getSpecialDir(int position){
		String dir = "";
		int index = position - 3;
		String url = specialList.get(index).getUrl();
		if(null != url){
			dir = SD_PATH + url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
		}
		return dir;
	}

	private void toSubsrriptions() {
		toDl();
	}

	/**
	 * 我的订阅
	 * 
	 */
	public void toDl() {
		SpotUtil.spots.get(SpotUtil.SUBSCRIBED_JOURNALS).clear(ActivityPool.getInstance().activityMap.get(ResearchPageActivity.class));
		if (view == null) {
			view = (LayoutInflater.from(context).inflate(R.layout.detail_list,
					null));
		}
		setContentView(view);
		srcRequests = new RequestController(view, this);
		enteredSubScripted = true;
		AdaptInfo adaptInfo = new AdaptInfo();
		adaptInfo.objectFields = new String[] { "PeriodicalName",
				"PeriodicalCount" };
		adaptInfo.viewIds = new int[] { R.id.ListText1, R.id.ListText2 };
		adaptInfo.listviewItemLayoutId = R.layout.list_item_text_text_icon;
		
		RequestInfos infos = new RequestInfos();
		infos.putSrc(
				new SoapInfo(new String[] { "sign", "userId", "updateTime" },
						new String[] {
								"",/*
									 * \"*:*\"
									 */
								"?",
								new java.text.SimpleDateFormat(
										"yyyy-MM-dd")
										.format(new Date()) },
						SoapRes.REQ_ID_GetUsersPeriodicalInfoByUserId,
						SoapRes.URLGETSUNSCRIPTIONS, "detail"), 1);

		final RequestSrc jnl = new GBIRequest(infos, this, null,
				itemAdapter = new MapAdapter(context, adaptInfo) {
					protected void findAndBindView(View convertView, int pos,
							Object item, String name, Object value) {
						if (name.equals("PeriodicalCount")) {
							if (value != null && value.equals("0")) {
								value = "";
							}
						}

						super.findAndBindView(convertView, pos, item, name,
								value);
					}
					protected void getViewInDetail(Object item, int position,
							View convertView) {
						super.getViewInDetail(item, position, convertView);
					}
				},"research_collection_subscribe_dir") {
			boolean shouldReplace = false;

			@Override
			public boolean shouldInstead(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				return true;

			}

			boolean shouldCache;

			@Override
			public boolean shouldCache(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub

				return true;
			}

			@Override
			public void onBack(RequestSrc targetSrc,
					RequestController srcRequests) {
				// TODO Auto-generated method stub
				escape();
			}

			@Override
			public void postHandle(List<EntityObj> eob) {
				// TODO Auto-generated method stub
				super.postHandle(eob);
				
			}

			@Override
			public void preHandle() {
				// TODO Auto-generated method stub
				super.preHandle();
				this.asyncHandler.removeFooter();
			}

		};
		jnl.setLinkForVisit(new String[] { "PeriodicalID" });
		jnl.setLinkforLabel("PeriodicalName");
		jnl.getNavigationNode().setCurrLyLabel(getString(R.string.collection_subscibe));
		((TextView) emptyView.findViewById(R.id.emptytext))
				.setText(R.string.subscription_empty);
		srcRequests.setEmptyView(emptyView);
		srcRequests.addRequest(jnl);
		RequestInfos dbs = new RequestInfos();

		dbs.putSrc(
				new SoapInfo(
						new String[] { "sign", "strSearch" },
						new String[] {
								"",
								"{\"fq\":\"JournalId:?\",\"start\":\"0\",\"rows\":\"20\",\"sort\":\"PubDate desc\"}" },
						SoapRes.REQ_ID_GET_PAPER_LIST, SoapRes.URLRESEARCH,
						count, "detail"), 1);
		AdaptInfo dtlAdapt = new AdaptInfo();
		dtlAdapt.objectFields = new String[] { "Title", "PublicDate", "Status"/*
																			 * ,
																			 * "journal"
																			 * ,
																			 * "keyword"
																			 */};
		dtlAdapt.viewIds = new int[] { R.id.ArticleTitle, R.id.date, R.id.Free /*
																				 * ,
																				 * R
																				 * .
																				 * id
																				 * .
																				 * journal
																				 * ,
																				 * R
																				 * .
																				 * id
																				 * .
																				 * keyword
																				 */};
		dtlAdapt.listviewItemLayoutId = R.layout.latest_list;
		subscriptAdapter = new ResearchAdapter(GBApplication.gbapp, adaptInfo,false,
				DetailsData.viewedNews) {

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				EntityObj obj = (EntityObj) super.getItem(position);
				final String id = obj.get("PeriodicalID");
				final String count = obj.get("PeriodicalCount");

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							String spid = SharedPreferenceUtil.getValue(
									view.getContext(), SUBSCRIPTED, id,
									String.class).toString();
							spid = spid.equals("") ? "0" : spid;
							SharedPreferenceUtil.putValue(
									view.getContext(),
									SUBSCRIPTED,
									id,
									Integer.parseInt(spid)
											+ Integer.parseInt(count.equals("") ? "0"
													: count) + "");
							handler.sendEmptyMessage(0);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}).start();
				return obj;
			}

		};
		RequestSrc dtl = new GBIRequest(dbs, this, dtlAdapt, subscriptAdapter,"") {
			boolean shouldReplace = false;

			@Override
			public boolean shouldInstead(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				// sort(eob, entry);
				DetailsData.entities = eob;
				if (cached) {// 转移替换任务
					shouldReplace = false;
					cached = false;
					return true;
				}
				return shouldReplace = false;

			}

			boolean shouldCache;

			@Override
			public boolean shouldCache(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				if (this.pieceIndex == 0) {
					return true;
				}
				return false;
			}

			@Override
			public void postHandle(List<EntityObj> eob) {
				// TODO Auto-generated method stub
				if (this.pieceIndex == 0) {
					if (DetailsData.entities == null) {
						DetailsData.entities = eob;
					}
				} else {
					DetailsData.entities.addAll(eob);
				}
				
			}
		};
		dtl.setItemClickListener(new DetailItemClickListener("research_collection_subscribe_paper","PeriodicalID") {

			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				super.onItemClick(parent, view, position, id);
				SharedPreferenceUtil.putValue(view.getContext(), SUBSCRIPTED,
						objId, 0);
			}

		});
		srcRequests.setEmptyView(emptyView);
		srcRequests.addRequest(dtl);
		srcRequests
				.startCat(new String[] { SharedPreferencesMgr.getUserName() });
	}

	ResearchAdapter subscriptAdapter;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			subscriptAdapter.notifyDataSetChanged();
		}

	};

	public void searchText(String text) {
		srcRequests.startCat(new String[] { text });
	}

	private class FavoriteItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			switch (arg2) {
			default:
				convertPaper(HistoryFavoriteActivity.arrFavorResearchId);
				EntityObj pNewsEntity = new EntityObj();
				pNewsEntity.fieldContents.put("Id",
						HistoryFavoriteActivity.arrFavorResearchId.get(arg2));
				DetailsData.tappedne = pNewsEntity;
				Intent intent = new Intent(CollectionActivity.this,
						PaperDetailActivity.class);
				intent.putExtra("id",
						HistoryFavoriteActivity.arrFavorResearchId.get(arg2)
								.split(",")[0]);
				intent.putExtra("ta", "");
				intent.putExtra("articles_type", "");
				startActivity(intent);
				break;
			}
		}

		private void convertPaper(ArrayList<String> arrFavorNewsId) {
			DetailsData.entities = new ArrayList<EntityObj>();
			for (String id : arrFavorNewsId) {
				EntityObj ej = new EntityObj();
				ej.fieldContents.put("Id", id);
				DetailsData.entities.add(ej);
			}
		}

	}

	private class DownLoadItemClickListener implements OnItemClickListener {
		private PaperDownloadEntity entity;
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			entity = downloadAdapter
					.selectPaperDownloadInfo(context,SharedPreferencesMgr.getUserName(),
							downloadListAdapter.list.get(arg2).getId());
			if(entity == null || TextUtils.isEmpty(entity.getState())) return;
			
			if(entity.getState().equals(PaperDownloadEntity.PENDING)){
				showDialog(arg1, arg2, R.string.research_download_resume, 0);
			}else if(entity.getState().equals(PaperDownloadEntity.DOWNLOADING)){
				showDialog(arg1, arg2, R.string.research_download_pause, 1);
			}else if(entity.getState().equals(PaperDownloadEntity.PAUSE)){
				showDialog(arg1, arg2, R.string.research_download_consume, 2);
			}else if(entity.getState().equals(PaperDownloadEntity.FAILURE)){
				showDialog(arg1, arg2, R.string.research_download_resume, 3);
			}else if(entity.getState().equals(PaperDownloadEntity.READ)){
				String fileDir = SD_PATH + "/" + entity.getPaperID().trim() + "/" + entity.getPaperID().trim() + ".xml";
				File file = new File(fileDir);
				if(file.exists()){
					Intent intent = new Intent(CollectionActivity.this, SpecialDetailActivity.class);
					intent.putExtra("articalHtmlDir", fileDir);
					CollectionActivity.this.startActivity(intent);
				}
			}
		}

		private void showDialog(final View arg1, final int arg2, int stringID, final int state){
			new AlertDialog.Builder(context)
			.setTitle(getResources().getString(R.string.research_download))
			.setMessage(getResources().getString(stringID))
			.setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					arg1.post(new Runnable() {
						@Override
						public void run() {
							switch (state) {
							case 0:
							case 3:
								new RequestDownloadURLThread(entity).start();
								break;
							case 1:
								pauseDownload(entity.getUrl());
								break;
							case 2:
								startDownload(entity.getUrl(), entity.getPaperID(), entity);
								break;
							default:
								break;
							}
						}
					});
				}
			})
			.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
				}
			}).show();
		}
		
		public class RequestDownloadURLThread extends Thread{
			private PaperDownloadEntity entity;
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
				
				if(downloadListAdapter != null){
					for(int i=0; i<downloadListAdapter.list.size(); i++){
						if(paperID.equals(downloadListAdapter.list.get(i).getId().trim())){
							downloadListAdapter.list.get(i).setState(PaperDownloadEntity.PENDING);
							downloadAdapter.updateState(context, paperID, PaperDownloadEntity.PENDING, entity.getUsername());
							Message msg = Message.obtain();
							msg.what = 3;
							mHandler.sendMessage(msg);
						}
					}
				}

				Properties propertyInfo = new Properties();
				propertyInfo.put("sign", "");
				propertyInfo.put(SoapRes.KEY_FULLTEXT_USER_NAME ,
						SharedPreferencesMgr.getUserName());
				propertyInfo.put(SoapRes.KEY_FULLTEXT_PAPER_ID, paperID);
				propertyInfo.put(SoapRes.KEY_FULLTEXT_CULTURE , "en-US");
				CollectionActivity.this.sendRequest(SoapRes.URLResearchDetail,
						SoapRes.REQ_ID_DOWNLOAD_FULLTEXT, propertyInfo,
						new BaseResponseHandler(CollectionActivity.this, false){

					@Override
					public void onSuccess(Object content, int methodId) {
						super.onSuccess(content, methodId);
						DownloadFullTextPaser data = (DownloadFullTextPaser) content;
						if(data != null){
							resCode = data.resCode;
							if(resCode.trim().equals(DownloadFullTextPaser.SUCCESS_CODE)){
								urlstr = data.url;
								startDownload(urlstr, paperID, entity);
							}else {
								downloadAdapter.updateState(context, paperID, PaperDownloadEntity.FAILURE, entity.getUsername());
								for(int i=0; i<downloadListAdapter.list.size(); i++){
									if(paperID.equals(downloadListAdapter.list.get(i).getId().trim())){
										downloadListAdapter.list.get(i).setState(PaperDownloadEntity.FAILURE);
									}
								}
							}
						}else{
							downloadAdapter.updateState(context, paperID, PaperDownloadEntity.FAILURE, entity.getUsername());
							for(int i=0; i<downloadListAdapter.list.size(); i++){
								if(paperID.equals(downloadListAdapter.list.get(i).getId().trim())){
									downloadListAdapter.list.get(i).setState(PaperDownloadEntity.FAILURE);
								}
							}
						}
						Message msg = Message.obtain();
						msg.what = 3;
						mHandler.sendMessage(msg);
					}

				});
			}
		}
		
		private void startDownload(String url, final String specialID, PaperDownloadEntity entity) {
			String urlstr = url + ".zip";
			String fileName = urlstr.substring(urlstr.lastIndexOf("=") + 1);
			String localfile = CollectionActivity.SD_PATH + fileName;
			downloadAdapter.updateFilename(context, specialID, url, fileName, SharedPreferencesMgr.getUserName());
			// 设置下载线程数为10
			int threadcount = 1 ;
			// 初始化一个downloader下载器
			DownloaderResume downloader = Util.downloaders.get(url);
			if (downloader == null) {
				downloader = new DownloaderResume(url, specialID, localfile, threadcount, context);
				downloader.setHandler(mHandler);
				Util.downloaders.put(url, downloader);
			}
			if (downloader.isdownloading())
				return;
			downloadAdapter.updateState(context, specialID, PaperDownloadEntity.DOWNLOADING, entity.getUsername());
			if(downloadListAdapter != null){
				for(int i=0; i<downloadListAdapter.list.size(); i++){
					if(url.equals(downloadListAdapter.list.get(i).getUrl().trim())){
						downloadListAdapter.list.get(i).setState(PaperDownloadEntity.DOWNLOADING);
						Message msg = Message.obtain();
						msg.what = 3;
						mHandler.sendMessage(msg);
					}
				}
			}
			// 得到下载信息类的个数组成集合
			downloader.getDownloaderInfors();
			// 调用方法开始下载
			downloader.download();
		}
		
		private void openPDFReader(int index) {
			PaperDownloadEntity entity = downloadAdapter
					.selectPaperDownloadInfo(context,
							SharedPreferencesMgr.getUserName(),
							downloadListAdapter.list.get(index).getId());
			if (!entity.getState().equals(PaperDownloadEntity.READ))
				return;
			File file = new File(Constant.PAPER_DOWNLOAD + "/"
					+ SharedPreferencesMgr.getUserName() + "/"
					+ downloadListAdapter.list.get(index).getFileName());
			if (file.exists()) {
				Uri path = Uri.fromFile(file);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(path, "application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(CollectionActivity.this,
							"No Application Available to View PDF",
							Toast.LENGTH_SHORT).show();
				}

			}
		}

	}

	private class DownLoadItemLongClickListener implements
			OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int arg2, long arg3) {
			new AlertDialog.Builder(CollectionActivity.this)
					.setIcon(R.drawable.icon)
					.setTitle(R.string.paper_delete_title)
					.setMessage(R.string.paper_delete_content)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String url = downloadListAdapter.list.get(arg2).getUrl();
									DownloaderResume downloader = Util.downloaders.get(url);
									if(downloader != null){
										downloader.pause();
										Util.downloaders.remove(url);
									}
									downloadAdapter.deletePaperDownloadInfo(
											context, downloadListAdapter.list
													.get(arg2).getId(),
											SharedPreferencesMgr.getUserName());
									downloadDao.delete(downloadListAdapter.list
											.get(arg2).getId());
									downloadListAdapter.list.remove(arg2);
									downloadListAdapter.notifyDataSetChanged();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
								}
							}).create().show();
			return false;
		}

	}

	private class DownloadListAdapter extends BaseAdapter {
		public ArrayList<DownloadPaperEntity> list;
		private LayoutInflater mInflater;
		private final int TYPE_1 = 0;
		private final int TYPE_2 = 1;

		public DownloadListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			if (null == list)
				return 0;
			return list.size();
		}

		public Object getItem(int position) {
			if (null == list)
				return null;
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
		
		//每个convert view都会调用此方法，获得当前所需要的view样式
		@Override
		public int getItemViewType(int position) {
			String state = list.get(position).getState();
			if(state.equals(PaperDownloadEntity.READ)){
				return TYPE_2;
			}else{
				return TYPE_1;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			ViewHolder holder = null;
			ViewHolderDone holderDone = null;
			String state = list.get(position).getState();

			if (convertView == null) {
				switch(type){
				case TYPE_1:
					convertView = mInflater.inflate(R.layout.research_download_list_item, null);

					holder = new ViewHolder();
					holder.titleText = (TextView) convertView.findViewById(R.id.title);
					holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
					holder.icon = (ImageView) convertView.findViewById(R.id.icon);
					holder.stateDownloadText = (TextView) convertView.findViewById(R.id.state);

					convertView.setTag(holder);
					break;
				case TYPE_2:
					convertView = mInflater.inflate(R.layout.research_special_list_item, null);

					holderDone = new ViewHolderDone();
					holderDone.titleText = (TextView) convertView.findViewById(R.id.ArticleTitle);
					holderDone.periodicalTitleText = (TextView) convertView.findViewById(R.id.PeriodicalTitle);
					holderDone.ifText = (TextView) convertView.findViewById(R.id.IF);
					holderDone.dateText = (TextView) convertView.findViewById(R.id.date);

	                convertView.setTag(holderDone);
					break;
				default: 
					break;
				}
			} else {
				switch(type){
				case TYPE_1:
					holder = (ViewHolder) convertView.getTag();
					break;
				case TYPE_2:
					holderDone = (ViewHolderDone) convertView.getTag();
					break;
				default: 
					break;
				}
			}

			switch(type){
			case TYPE_1:
				holder.titleText.setText(list.get(position).getTitle());
				if(state.equals(PaperDownloadEntity.DOWNLOADING) && !TextUtils.isEmpty(list.get(position).getUrl()) 
						&& !Util.downloaders.containsKey(list.get(position).getUrl().trim())){
					downloadAdapter.updateState(context, list.get(position).getId(), PaperDownloadEntity.PAUSE, SharedPreferencesMgr.getUserName().trim());
					list.get(position).setState(PaperDownloadEntity.PAUSE);
					holder.progressBar.setVisibility(View.GONE);
					holder.icon.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.research_paper_pause));
				}else if (state.equals(PaperDownloadEntity.DOWNLOADING)) {
					holder.progressBar.setVisibility(View.GONE);
					holder.progressBar.setProgress(list.get(position)
							.getProgressRate());
					holder.icon.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.research_paper_downloading));
				} else if(state.equals(PaperDownloadEntity.READ)) {
					holder.progressBar.setVisibility(View.GONE);
					holder.icon.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.research_paper_ready));
				}else if(state.equals(PaperDownloadEntity.PENDING)) {
					holder.progressBar.setVisibility(View.GONE);
					holder.icon.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.research_paper_pending));
				}else if(state.equals(PaperDownloadEntity.PAUSE)) {
					holder.progressBar.setVisibility(View.GONE);
					holder.icon.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.research_paper_pause));
				}else if(state.equals(PaperDownloadEntity.FAILURE)) {
					holder.progressBar.setVisibility(View.GONE);
					holder.icon.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.research_paper_failure));
				}

				if (holder.progressBar.getVisibility() == View.VISIBLE
						&& list.get(position).getProgressRate() == 100) {
					//					holder.progressBar.setVisibility(View.INVISIBLE);
					//					holder.stateDownloadText.setText(PaperDownloadEntity.READ);
					//					holder.icon.setBackgroundDrawable(getResources().getDrawable(
					//							R.drawable.research_paper_ready));
					Util.downloaders.remove(list.get(position).getUrl());
				} else {
					holder.stateDownloadText.setText(list.get(position).getState());
				}
				break;
			case TYPE_2:
				String date = list.get(position).getDate().substring(0, list.get(position).getDate().indexOf("T"));
				holderDone.titleText.setText(list.get(position).getTitle());
				if(TextUtils.isEmpty(list.get(position).getPeriodicalTitle())
						|| list.get(position).getPeriodicalTitle().equals("null")){
					holderDone.periodicalTitleText.setVisibility(View.GONE);
				}
				if(formatIF(list.get(position).getIFCount()).equals("0.00")){
					holderDone.ifText.setVisibility(View.GONE);
				}
				holderDone.periodicalTitleText.setText(list.get(position).getPeriodicalTitle());
				holderDone.ifText.setText(formatIF(list.get(position).getIFCount()));
				holderDone.dateText.setText(date);
				break;
			default: 
				break;
			}

			return convertView;
		}

		class ViewHolder {
			TextView titleText;
			ProgressBar progressBar;
			ImageView icon;
			TextView stateDownloadText;
		}
		
		class ViewHolderDone {
			TextView titleText;
			TextView periodicalTitleText;
			TextView ifText;
			TextView dateText;
		}

		private String formatIF(String IF){
			float x1 = 0.0f;
			try{
				x1 = Float.parseFloat(IF);
			}catch (Exception e) {
				e.printStackTrace();
			}
			IF = String.format("%.2f", x1);
			return IF;
		}
	}

	private class CollectionListAdapter extends BaseAdapter {
		public ArrayList<CollectionEntity> list;
		public ArrayList<DownloadInfo> specialList;
		private LayoutInflater mInflater;
		private ArrayList<Integer> iconUrl = new ArrayList<Integer>();

		public CollectionListAdapter(Context context) {
			specialList = new ArrayList<DownloadInfo>();
			list = new ArrayList<CollectionEntity>();
			mInflater = LayoutInflater.from(context);
			iconUrl.add(0, R.drawable.collection_favorite);
			iconUrl.add(1, R.drawable.collection_download);
			iconUrl.add(2, R.drawable.collection_journal);
		}
		
		 public void setData(ArrayList<DownloadInfo> specialList)
		 {
		  this.specialList = specialList;
		 }

		public int getCount() {
			if (null == list && null == specialList)
				return 0;
			return list.size() + specialList.size();
		}

		public Object getItem(int position) {
			if (null == list && null == specialList)
				return null;
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.research_collection_list_item, null);

				holder = new ViewHolder();
				holder.titleText = (TextView) convertView
						.findViewById(R.id.title);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.progressBar = (ProgressBar) convertView.findViewById(R.id.download_progress);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final int po = position - 3;
			if (position < 3) {
				holder.titleText.setText(list.get(position).getTitle());
				holder.icon.setBackgroundDrawable(context.getResources()
						.getDrawable(iconUrl.get(position)));
				holder.progressBar.setVisibility(View.GONE);
			} else {
				holder.titleText.setText(specialList.get(po).getTitle());
				holder.icon.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.special_collecion));

				if(!downloadDao.isHasInfors(specialList.get(po).getUrl()) 
						&& !specialList.get(po).isDownloadState()){
					holder.progressBar.setVisibility(View.VISIBLE);
//					holder.progressBar.setProgress((int)(100 * ((float)specialList.get(po).getCompeleteSize()
//							/(float)specialList.get(po).getFileSize())));
					if(specialList.get(po).getFileSize() != 0)
					holder.progressBar.setProgress((int)((specialList.get(po).getCompeleteSize()*100)/specialList.get(po).getFileSize()));
				}else{
					holder.progressBar.setVisibility(View.GONE);
				}
			}

			return convertView;
		}

		class ViewHolder {
			TextView titleText;
			ImageView icon;
			ProgressBar progressBar;
		}
	}

	public interface ContextInfo {
		Context getContext(Context context);
	}

	@Override
	public ListView getAdaptView() {
		return view == null ? null : (ListView) view
				.findViewById(R.id.lst_item);
	}
	
	/**
	 *  响应开始下载按钮的点击事件 
	 */
	private void startDownload(final String urlstr, final String specialID, String title, final int index) {
		String localfile = SD_PATH + urlstr.substring(urlstr.lastIndexOf("/") + 1);
		// 设置下载线程数为10
		int threadcount = 1 ;
		// 初始化一个downloader下载器
		DownloaderResume downloader = Util.downloaders.get(urlstr);
		if (downloader == null) {
			downloader = new DownloaderResume(urlstr, specialID, localfile, threadcount, this,
					mHandler,title, new DownloaderResume.FileSizeCallBack() {
						
						@Override
						public void onGetFileSize(long fileSize) {
							for(int i=0; i<collectionListAdapter.specialList.size(); i++){
								if(specialID.equals(collectionListAdapter.specialList.get(i).getSpecialID())){
									collectionListAdapter.specialList.get(i).setFileSize(fileSize);
								}
							}
						}
					});
			Util.downloaders.put(urlstr, downloader);
		}
		if (downloader.isdownloading())
			return;
		final DownloaderResume downloaderTemp = downloader;
		new Thread(){
			@Override
			public void run() {
				super.run();
				// 得到下载信息类的个数组成集合
				DownloadProgressInfo loadInfo = downloaderTemp.getDownloaderInfors();
				Message msg = Message.obtain();
				msg.what = 4;
				Bundle bundle = new Bundle();
				bundle.putParcelable("loadinfo", loadInfo);
				bundle.putString("urlstr", urlstr);
				bundle.putInt("index", index);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 显示进度条
	 */
	private void showProgress(DownloadProgressInfo loadInfo, String url, int index) {
		ProgressBar bar = Util.ProgressBars.get(url);
		if (bar == null) {
			bar = (ProgressBar) listView.getChildAt(index).findViewById(R.id.download_progress);
//			bar.setMax(loadInfo.getFileSize());
			bar.setMax(100);
//			bar.setProgress((int)(100 * ((float)loadInfo.getComplete()/(float)loadInfo.getFileSize())));
			if(loadInfo.getFileSize() != 0)
			bar.setProgress((int)((loadInfo.getComplete()*100)/loadInfo.getFileSize()));
			Util.ProgressBars.put(url, bar);
			bar.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 响应暂停下载按钮的点击事件
	 */
	public void pauseDownload(String urlstr) {
		Util.downloaders.get(urlstr).pause();
		if(downloadListAdapter != null){
			for(int i=0; i<downloadListAdapter.list.size(); i++){
				if(urlstr.equals(downloadListAdapter.list.get(i).getUrl().trim())){
					downloadAdapter.updateState(context, downloadListAdapter.list.get(i).getId(), PaperDownloadEntity.PAUSE, SharedPreferencesMgr.getUserName().trim());
					downloadListAdapter.list.get(i).setState(PaperDownloadEntity.PAUSE);
					Message msg = Message.obtain();
					msg.what = 3;
					mHandler.sendMessage(msg);
				}
			}
		}
	}

}
