package com.jibo.base.src.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;
import com.jibo.app.DetailsData;
import com.jibo.app.news.NewsAdapter;
import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.LinkRequestItemClicker;
import com.jibo.base.src.NavigationNode;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.SrcUtil;
import com.jibo.base.src.request.config.DBInfo;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.base.src.request.impl.db.AutoCache;
import com.jibo.base.src.request.impl.db.MySqlLiteAdapter;
import com.jibo.base.src.request.impl.soap.AsyncRequest;
import com.jibo.common.DeviceInfo;
import com.jibo.common.SoapRes;
import com.jibo.util.ComparatorRepo;
import com.jibo.util.Logs;

public class RequestSrc {

	public Object categoryLinkItem;
	public View view;
	public String flag;
	public View waitView;
	public View emptyview;
	public int layerIndex;
	public boolean categoryViewIsSetted = false;
	public boolean isInPaging = false;
	public boolean isPagingLoad = false;
	// navigation node
	public NavigationNode navigationNode = new NavigationNode();
	// ui config info
	public MapAdapter adapter;
	public AdaptInfo adaptInfo;
	// data config info
	public RequestInfos repo;

	public TextView categoryView;
	public ListView listView;
	public View footerView;
	public Integer batchcount;
	public int pieceIndex;
	public int tmpSoap;
	public boolean tmpSoapStayed = true;
	public List<Object> es;
	public boolean dataTail;
	public BaseActivity base;
	private boolean toCache;
	public String newFlag;

	public View getWaitView() {
		return waitView;
	}

	public void setWaitView(View waitView) {
		this.waitView = waitView;
	}

	public boolean categoryViewClickable = true;

	public boolean isCategoryViewClickable() {
		return categoryViewClickable;
	}

	public void setCategoryViewClickable(boolean categoryViewClickable) {
		this.categoryViewClickable = categoryViewClickable;
	}

	public void onReload() {
	}

	public View getEmptyview() {
		return emptyview != null ? emptyview : linkRequestItemClicker
				.getSrcRequests().getEmptyView();
	}

	public void setEmptyview(View emptyview) {

		this.emptyview = emptyview;
	}

	public void onFinish() {
		this.asyncHandler.removeFooter();
		this.runtimeReqLogIdentifier.clear();
	}

	public boolean isToCache() {
		return toCache;
	}

	public void setToCache(boolean toCache) {
		this.toCache = toCache;
	}

	public void adjust4EmptyView() {
		RequestSrc lis = this;
		int i = -1, j = -1, k = -1;
		for (; i < lis.vg.getChildCount(); i++) {
			if (lis.vg.getChildAt(i) == lis.listView) {
				j = i;
			} else if (lis.vg.getChildAt(i) == lis.getEmptyview()) {
				k = i;
			}
		}

		if (lis.adapter.getCount() == 0) {
			if (j != -1) {
				if (lis.getEmptyview() != null) {
					lis.listView.setVisibility(View.GONE);
					if (k == -1)
						lis.vg.addView(lis.getEmptyview(), j);
				}
			}
		} else {
			if (k != -1) {
				restoreAdapterView();
			}
		}
	}

	public void restoreAdapterView() {
		RequestSrc lis = this;
		if (lis.listView.getVisibility() == View.GONE) {
			lis.vg.removeView(lis.getEmptyview());
			lis.listView.setVisibility(View.VISIBLE);

		}
	}

	public int mId;
	public AutoCache lang;
	public AsycRequestHandler asyncHandler = new AsycRequestHandler(this);
	public LinkRequestItemClicker linkRequestItemClicker;

	public int getTmpSrcIndex() {
		if (es.size() <= tmpSoap)
			return tmpSoap = es.size() - 1;
		else
			return tmpSoap;
	}

	public void setTmpSoap(int tmpSoap) {
		this.tmpSoap = tmpSoap;
	}

	public boolean cached;

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public OnItemClickListener selfDefineditemClickListener;
	public boolean recursiveHead;

	public OnItemClickListener getSelfDefinedItemClickListener() {
		return selfDefineditemClickListener;
	}

	public void setItemClickListener(OnItemClickListener itemClickListener) {
		this.selfDefineditemClickListener = itemClickListener;
	}

	public LinkRequestItemClicker getLinkRequestItemClicker() {
		return linkRequestItemClicker;
	}

	public RequestInfos getRuntimeInfos() {
		return repo;
	}

	public void setRuntimeInfos(RequestInfos runtimeInfos) {
		this.repo = runtimeInfos;
	}

	public void setEmptyViewText(String text) {
		((TextView) (this.listView.getEmptyView())).setText(text);
	}

	public int nextSoap() {
		return tmpSoapStayed ? tmpSoap : tmpSoap + 1;
	}

	public BaseActivity getBaseAty() {
		return base;
	}

	public void clearStatus() {
		pieceIndex = 0;
		tmpSoap = 0;
		this.dataTail = false;
	}

	public RequestSrc(RequestInfos soapInfos, int tmpPiece, int tmpSoap,
			boolean dataTail, BaseActivity listActivity, int mId) {
		this(soapInfos, tmpPiece, tmpSoap, dataTail, listActivity);
		this.mId = mId;
		this.adaptInfo = null;
		lang = new AutoCache(MySqlLiteAdapter.getInstance());
	}

	public RequestSrc(RequestInfos soapInfos, BaseActivity listActivity,
			AdaptInfo adaptInfo) {
		this(soapInfos, 0, -1, false, listActivity);
		this.mId = -1;
		this.adaptInfo = adaptInfo;
		lang = new AutoCache(MySqlLiteAdapter.getInstance());
	}

	public RequestSrc(RequestInfos soapInfos, BaseActivity listActivity,
			AdaptInfo adaptInfo, MapAdapter adapter) {
		this(soapInfos, 0, -1, false, listActivity);
		this.mId = -1;
		this.adaptInfo = adaptInfo;
		this.adapter = adapter;
		lang = new AutoCache(MySqlLiteAdapter.getInstance());
	}

	public RequestSrc(RequestInfos soapInfos, BaseActivity listActivity,
			MapAdapter adapter) {
		this(soapInfos, 0, -1, false, listActivity);
		this.mId = -1;
		lang = new AutoCache(MySqlLiteAdapter.getInstance());
		this.adapter = adapter;
	}

	public RequestSrc(RequestInfos soapInfos, int tmpPiece, int tmpSoap,
			boolean dataTail, BaseActivity listActivity) {
		this.repo = soapInfos;
		this.pieceIndex = tmpPiece;
		this.tmpSoap = tmpSoap;
		this.dataTail = dataTail;
		this.base = listActivity;
		lang = new AutoCache(MySqlLiteAdapter.getInstance());
		for (DescInfo soap : soapInfos.requests.keySet())
			soap.responseHandler = asyncHandler;
	}

	public RequestSrc(RequestSrc src) {
		this.categoryViewListener = src.categoryViewListener;
		this.categoryViewIsSetted = src.categoryViewIsSetted;
		this.flag = src.flag;
		this.repo = src.repo;
		this.dataTail = src.dataTail;
		this.base = src.getBaseAty();
		this.categoryViewClickable = src.categoryViewClickable;
		AsycRequestHandler listAsyncSoapResponseHandler;
		DescInfo descInfo = null;
		lang = new AutoCache(MySqlLiteAdapter.getInstance());
		int val = 0;
		Logs.i("--- 0 lis "
				+ src.linkRequestItemClicker.getSrcRequests().rts.get(0).repo.requests
						.keySet().iterator().next().responseHandler.lis);
		for (DescInfo soap : repo.requests.keySet()) {
			val = repo.requests.get(soap);
			if (soap instanceof DBInfo) {
				descInfo = new DBInfo(soap);
			} else if (soap instanceof SoapInfo) {
				descInfo = new SoapInfo(soap);
			}
			repo.requests.remove(soap);

			descInfo.responseHandler = this.asyncHandler;
			Logs.i("--- 0 lis " + soap.responseHandler.lis);
			repo.requests.put(descInfo, val);
		}
		if (src.checkInfo != null) {

			if (src.checkInfo instanceof DBInfo) {
				this.checkInfo = new DBInfo(src.checkInfo);
			} else if (src.checkInfo instanceof SoapInfo) {
				this.checkInfo = new SoapInfo(src.checkInfo);
			}
		}
		if (checkInfo != null && src.checkInfo != null) {
			this.checkInfo.responseHandler = src.checkInfo.responseHandler;
		}

		this.callbackListener = src.callbackListener;

		this.recursiveBool = src.recursiveBool;
		this.linkRequestItemClicker = new LinkRequestItemClicker(
				src.linkRequestItemClicker.getSrcRequests());
		linkRequestItemClicker.src_click = src.linkRequestItemClicker.src_click;
		this.navigationNode = new NavigationNode(
				src.navigationNode.getArgPassedNames(),
				src.navigationNode.getLyLabel());
		this.navigationNode.setCurrVisitValue(new String[0]);
		this.navigationNode.setCurrLyLabel(null);
		this.runtimeReqLogIdentifier.clear();
		if (!src.linkRequestItemClicker.getSrcRequests().dynActivity) {
			this.listView = src.listView;
			this.adapter = src.adapter;
		}
		this.adaptInfo = src.adaptInfo;
		this.batchcount = -1;
		this.categoryView = src.categoryView;
		this.footerView = src.footerView;
		this.selfDefineditemClickListener = src.selfDefineditemClickListener;
		this.tailItemClickListener = src.tailItemClickListener;
		this.recursiveBool = src.recursiveBool;
		this.mId = src.mId;
	}

	public void setEntrance(String[] pnValue) {
		this.getNavigationNode().setCurrVisitValue(pnValue);
		this.lang.cacheInfo.setCacheTable(
				SrcUtil.formatClassStr(this.base.getClass().getName()),
				SrcUtil.listFormat(this.getNavigationNode().getCurrVisitValue()
						.toString()));
	}

	public void setLinkRequestItemClicker(
			LinkRequestItemClicker linkRequestItemClicker) {
		this.linkRequestItemClicker = linkRequestItemClicker;
	}

	public String getKeyWord(String key) {
		Logs.i(" temp " + tmpSoap + " " + this.es.size());
		Object rlt = ((Entry<DescInfo, Integer>) this.es.get(tmpSoap)).getKey().propertyInfo
				.get(key);
		return rlt == null ? null : rlt.toString();
		// 每次saop后+1,所以上一次,减1了;
	}

	public int getBatchCount() {
		Logs.i(" temp batchCount" + tmpSoap + " " + this.es.size());
		List<ScrollCounter> batch = ((Entry<DescInfo, Integer>) this.es
				.get(tmpSoap)).getKey().batch;
		if (batch.isEmpty()) {
			return -1;
		}
		return batch.get(this.pieceIndex).count;
		// 每次saop后+1,所以上一次,减1了;
	}

	public CallBackListener callbackListener;
	public DescInfo checkInfo;
	public boolean recursiveBool;
	public OnItemClickListener tailItemClickListener;
	public Map<String, RequestLogInfo> runtimeReqLogIdentifier = new TreeMap<String, RequestLogInfo>(
			ComparatorRepo.stringKey);
	public ViewGroup vg;
	public String emptyViewText;

	public CallBackListener getCallbackListener() {
		return callbackListener;
	}

	public void setCallbackListener(CallBackListener callbackListener) {
		this.callbackListener = callbackListener;
	}

	public DescInfo getCheckInfo() {
		return checkInfo;
	}

	public void setCheckInfo(DescInfo checkInfo) {
		this.checkInfo = checkInfo;
	}

	public boolean isRecursiveBool() {
		return recursiveBool;
	}

	public static class RequestLogInfo {
		int count = 0;
		Integer type = -1;
		public boolean disabled;

		public RequestLogInfo(Integer type) {
			super();
			this.type = type;
		}

		public int getCount() {
			return count;
		}

	}

	public void setRecursiveBool(boolean recursiveBool) {
		this.recursiveBool = recursiveBool;
	}

	public static interface CallBack {
	}

	public void setLogicListener(LogicListener logicListener) {
		this.callbackListener.setLogicListener(logicListener);
	}

	public boolean shouldInstead(List<EntityObj> eob,
			Entry<DescInfo, Integer> entry) {
		return false;
		// TODO Auto-generated method stub

	}

	public boolean shouldCache(List<EntityObj> eob,
			Entry<DescInfo, Integer> entry) {
		return false;
		// TODO Auto-generated method stub

	}

	public boolean loadContinue(Object item) {
		// TODO Auto-generated method stub
		// if (isInRequest) {
		// return true;
		// }
		Logs.i(" --- a 222 " + cached);

		Logs.i(" --- a 333");
		if (this.cached) {
			return false;
		}
		if (continueItems.contains(item))
			return false;
		continueItems.add(item);
		if (dataTail) {
			if (this.listView.getFooterViewsCount() > 0) {
				this.listView.removeFooterView(footerView);
				// footerView.setVisibility(View.GONE);
			}
			return true;
		}
		Logs.i(" --- a 111");
		isInPaging = true;
		if (!DeviceInfo.instance.isNetWorkEnable())
			return true;
		RequestSrc data = this;
		if (data.tmpSoap > data.es.size() - 1 || data.tmpSoap < 0) {
			return true;
		}
		DescInfo sInfo = ((Entry<DescInfo, Integer>) data.es.get(data.tmpSoap))
				.getKey();
		transferNextPage(item, sInfo);
		Logs.i("--- info.propertyInfo " + cached + " " + dataTail);

		return sendRequest(data, sInfo, true);
	}

	public Set<Object> continueSet = new HashSet<Object>();

	public void transferNextPage(Object lastitem, DescInfo sInfo) {
		if (((EntityObj) lastitem).get("id") != null) {
			sInfo.propertyInfo.put(SoapRes.KEY_MAX_ID,
					((EntityObj) lastitem).get("id"));
		}
	}

	public void cache(List<EntityObj> eob) {
		// TODO Auto-generated method stub

		try {
			if (this.isToCache()) {
				// if(lang.getCacheInfo().getCacheTable() == null){
				// lang.cacheInfo.setCacheTable(base
				// .getClass().getName().replaceAll("\\.", "_"), "");
				// }
				String table = lang.getCacheInfo().getCacheTable();
				if (eob.size() > 0) {
					lang.autoCreateTable(eob.get(0).fieldContents, table);
					lang.deleteTableData(table, lang.getCacheInfo()
							.getCacheKey());
					lang.insertData(eob, table, lang.getCacheInfo()
							.getCacheKey());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String toSign(String[] cats) {
		return Arrays.asList(cats).toString().replaceFirst("\\[", "")
				.replaceFirst("\\]", "").replaceAll(" ", "")
				.replaceAll(",", "\\_");
	}

	public void setCacheFromType(String[] cacheFromType) {

		this.lang.getCacheInfo().setCacheFromType(toSign(cacheFromType));
	}

	public void setCacheTable(String cacheTable, String tag) {
		this.lang.getCacheInfo().setCacheTable(cacheTable, tag);
	}

	protected void changeDataSrc(final RequestSrc detail, View view) {
		detail.listView = (ListView) view.findViewById(R.id.lst_item);
		detail.categoryView = (TextView) view.findViewById(R.id.category);

		detail.listView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					detail.listView.invalidateViews();
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});
		if (detail.vg == null) {
			detail.vg = ((ViewGroup) detail.listView.getParent());
		}
		if (detail.mId != -1) {
			if (detail.mId == 0) {
				detail.adapter = new MapAdapter(detail.base,
						getListAdaptInfo(detail));
			} else if (detail.mId == 1) {

				detail.adapter = new NewsAdapter(detail.base,
						this.getListAdaptInfo(detail), DetailsData.viewedNews,
						detail);

			} else if (detail.mId == 2) {
				detail.adapter = new NewsAdapter(detail.base,
						this.getListAdaptInfo(detail), null, detail);
			}
		} else {
			if (detail.adapter == null) {
				if (detail.adaptInfo != null) {
					detail.adapter = new MapAdapter(detail.base,
							detail.adaptInfo);
				}
			} else {
				if (detail.adaptInfo != null) {
					detail.adapter.changeAdaptInfo(adaptInfo);
				}
			}
		}
		if (detail.listView.getFooterViewsCount() == 0) {
			detail.footerView = LayoutInflater.from(detail.base).inflate(
					R.layout.dialogprogress, null);
			detail.footerView.setClickable(false);
			detail.listView.addFooterView(detail.footerView);
		}
		detail.adapter.setItemDataSrc(new AdapterSrc(
				new ArrayList<EntityObj>(0)) {

			@Override
			public void nextBatch(Object object) {
				// TODO Auto-generated method stub
				final Object obj = object;
				try {

					DescInfo info = getCurrentDataSrcInfo(detail);
					if (info != null && detail == null && !info.batch.isEmpty()
							&& getCurrentScrollCounter(info, detail) != -1) {
						return;
					}
					int lastIndex = detail.listView.getChildCount() - 1;
					Integer lastViewPosition = detail.adapter
							.getViewContentMap().get(
									detail.listView.getChildAt(lastIndex));
					Logs.i("== lastPos" + lastViewPosition + " "
							+ (detail.adapter.getCount() - 1));
					if (lastViewPosition == null
							|| lastViewPosition != detail.adapter.getCount() - 2)
						return;
					new Thread(new Runnable() {

						@Override
						public void run() {
							// try {
							// Thread.sleep(2500);
							// } catch (InterruptedException e) {
							// // TODO Auto-generated catch block
							// e.printStackTrace();
							// }
							// TODO Auto-generated method stub
							detail.loadContinue(obj);

						}

					}).start();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		detail.listView.setAdapter(detail.adapter);
	}

	int lastCount = -1;
	private Set continueItems = new HashSet();
	public Activity activity;
	public OnClickListener categoryViewListener;

	public DescInfo getCurrentDataSrcInfo(RequestSrc request) {
		DescInfo info = null;
		try {
			info = ((Entry<DescInfo, Integer>) request.es.get(request.tmpSoap))
					.getKey();
		} catch (Exception e) {
			return null;
		}
		return info;
	}

	public int getCurrentScrollCounter(DescInfo dataConfig, RequestSrc request) {
		int currCount = 0;
		try {
			currCount = ((ScrollCounter) dataConfig.batch
					.get(request.pieceIndex)).count;
		} catch (Exception e) {

			e.printStackTrace();
		}
		return currCount;
	}

	public static void enterSrc(final RequestSrc targetSrc,
			final RequestController srcRequests) {

		srcRequests.curRequestSrc = targetSrc;

		Logs.i("--- matchV " + targetSrc);
		configureListStyleDatasrc(targetSrc, srcRequests);

		if (targetSrc.getNavigationNode().getCurrLyLabel() != null
				&& !targetSrc.getNavigationNode().getCurrLyLabel().equals("")) {
			targetSrc.categoryView
					.setClickable(targetSrc.categoryViewClickable);
			targetSrc.categoryView.setVisibility(View.VISIBLE);
			((View) targetSrc.categoryView.getParent())
					.setVisibility(View.VISIBLE);
			if (srcRequests.rts.lastIndexOf(targetSrc) == 0) {
				((View) targetSrc.categoryView.getParent())
						.setVisibility(View.GONE);
			} else {
				((View) targetSrc.categoryView.getParent())
						.setVisibility(View.VISIBLE);
			}
			targetSrc.categoryView.setText(targetSrc.getNavigationNode()
					.getCurrLyLabel());
			if (targetSrc.categoryViewClickable) {
				if (!targetSrc.categoryViewIsSetted) {

					if (srcRequests.dynActivity){
						((View) targetSrc.categoryView.getParent())
						.findViewById(R.id.viewall)
						.setVisibility(View.GONE);
					}
//							|| srcRequests.rts.lastIndexOf(targetSrc) == srcRequests.rts
//									.size() - 1) {
						

					else{
						// if (srcRequests.dynActivity){
						// ((View)
						// targetSrc.categoryView.getParent()).setClickable(false);
						// }

						((View) targetSrc.categoryView.getParent())
								.findViewById(R.id.viewall).setVisibility(
										View.VISIBLE);
						targetSrc.categoryView
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
//										if (srcRequests.dynActivity) {
//											srcRequests.backFloor();
//											srcRequests.curRequestSrc.activity
//													.finish();
//											return;
//										}
										targetSrc
												.onBack(targetSrc, srcRequests);
									}

								});
					}
				} else {
					targetSrc.categoryView
							.setOnClickListener(targetSrc.categoryViewListener);
				}
			}
		} else {
			targetSrc.categoryView.setVisibility(View.GONE);
			((View) targetSrc.categoryView.getParent())
					.setVisibility(View.GONE);
		}

		if (targetSrc.isToCache()) {
			List en = targetSrc.lang.selectData();
			if (en.size() > 0) {
				targetSrc.setCached(true);
				DetailsData.setCacheEntities(en);
			}
			targetSrc.adapter.getItemDataSrc().setContent(en);
			targetSrc.adapter.notifyDataSetChanged();
		}
		// if (DeviceInfo.instance.isNetWorkEnable()) {
		//
		// if(targetSrc.listView.getFooterViewsCount()>0){
		// targetSrc.listView.removeFooterView(targetSrc.footerView);
		// }
		//
		// }

		targetSrc.start(targetSrc);
		// detail.setEmptyViewText(getString(R.string.netsoso));
		if (targetSrc.getSelfDefinedItemClickListener() != null)
			targetSrc.listView.setOnItemClickListener(targetSrc
					.getSelfDefinedItemClickListener());
		else
			targetSrc.listView.setOnItemClickListener(targetSrc
					.getLinkRequestItemClicker());

	}

	public static void configureListStyleDatasrc(final RequestSrc targetSrc,
			final RequestController srcRequests) {
		View view = null;
		if (targetSrc != null) {
			if (targetSrc.view == null) {

				if (srcRequests.dynActivity) {

					if (targetSrc != null && targetSrc.view != null) {
						view = targetSrc.view;
					}
				} else {
					if (srcRequests.view != null) {
						view = srcRequests.view;
					}
				}
				if (view == null) {
					throw new IllegalStateException("error view conf");
				}
			} else {
				view = targetSrc.view;
			}
		}
		targetSrc.changeDataSrc(targetSrc, view);
		targetSrc.adapter.getItemDataSrc().clear();
		targetSrc.adapter.notifyDataSetChanged();
	}

	public AdaptInfo getListAdaptInfo(RequestSrc lis) {
		if (lis.adaptInfo == null) {
			lis.adaptInfo = new AdaptInfo();
			configureAdaptInfo(lis);
		}
		return lis.adaptInfo;
	}

	public void onBack(final RequestSrc targetSrc,
			final RequestController srcRequests) {
		srcRequests.onBack(targetSrc, srcRequests, false);
	}

	public void configureAdaptInfo(RequestSrc lis) {
		final int LEVL_CATEGORY = 0;
		final int LEVL_DETAIL = 1;
		AdaptInfo adaptInfo = null;
		if (lis.mId != -1) {
			switch (lis.mId) {
			case LEVL_CATEGORY:
				adaptInfo = lis.adaptInfo;
				adaptInfo.objectFields = new String[] { "name", "newsCount" };
				adaptInfo.viewIds = new int[] { R.id.ListText1, R.id.ListText2 };
				adaptInfo.listviewItemLayoutId = R.layout.list_item_text_text_icon;
				break;
			case LEVL_DETAIL:
				lis.configureDetail(lis.adaptInfo);
				break;
			case 2:
				adaptInfo = lis.adaptInfo;
				adaptInfo.objectFields = new String[] { "Title", "PublicDate",
						"journal", "keyword", "ImgURL" };
				adaptInfo.viewIds = new int[] { R.id.title, R.id.time,
						R.id.journal, R.id.keyword, R.id.fileIcon };
				adaptInfo.listviewItemLayoutId = R.layout.image_item_journal;

				break;
			}
		}
	}

	public String getLyLabel() {
		return this.navigationNode.naviLinkLabel;
	}

	public List<String> getArgV() {
		return navigationNode.nextLyVal;
	}

	public void setArgValueByVisit(List<String> args) {
		this.navigationNode.nextLyVal = args;
	}

	public void setArgV(String[] args) {
		this.navigationNode.nextLyVal = Arrays.asList(args);
	}

	public RequestSrc insertSrc(RequestSrc after, RequestSrc insertion) {
		int index = this.linkRequestItemClicker.getSrcRequests().rts
				.indexOf(after);
		this.linkRequestItemClicker.getSrcRequests().addRequest(index + 1,
				insertion);
		RequestSrc mid = this.linkRequestItemClicker.getSrcRequests().rts
				.get(index + 1);
		return mid;
	}

	public void configureDetail(AdaptInfo adaptInfo) {
		adaptInfo.objectFields = new String[] { "imgUrl", "title",
				"newSummary", "stickMsg" };
		adaptInfo.viewIds = new int[] { R.id.fileIcon, R.id.name, R.id.summary,
				R.id.special };
		adaptInfo.listviewItemLayoutId = R.layout.image_item;
	}

	public static Boolean sendRequest(RequestSrc lis, DescInfo info,
			boolean isScroll) {
		if (info.batch.size() != 0) {
			ScrollCounter computecount = info.batch.get(lis.pieceIndex);
			if (computecount.runtimePiece < 1 && !isScroll)
				return null;
			while (computecount.runtimePiece < 1) {
				if (computecount.runtimePiece == -1) {
					computecount = info.batch.get(lis.pieceIndex);
					break;
				}
				if (lis.pieceIndex + 1 == info.batch.size())
					return false;
				lis.pieceIndex++;
				computecount = info.batch.get(lis.pieceIndex);
			}
			computecount.runtimePiece = computecount.runtimePiece == -1 ? -1
					: computecount.runtimePiece - 1;
			lis.tmpSoapStayed = true;
		} else
			lis.tmpSoapStayed = false;
		packRequest(lis, info, isScroll);
		return true;
	}

	public RequestSrc(DescInfo srcInfo) {
		repo.requests.clear();
		this.repo.putSrc(srcInfo, 1);
	}

	public void start(RequestSrc detail) {
		detail.setWaitView(waitView);
		// TODO Auto-generated method stub
		detail.es = Arrays.asList(detail.repo.requests.entrySet().toArray());
		// Logs.i("=== detail "
		// +
		// ((DBInfo)(repo.requests.keySet().toArray()[0])).responseHandler.lis);
		detail.clearStatus();
		detail.reset();
		sendRequest(detail,
				((Entry<DescInfo, Integer>) detail.es.get(detail.tmpSoap))
						.getKey(), false);
		Logs.i("--- tmpSoap " + detail);
	}

	public void launchRequest(DescInfo detail) {
		// TODO Auto-generated method stub
		detail.responseHandler = this.asyncHandler;

		detail.isCheck = true;
		sendRequest(this, detail, false);
	}

	public void reset() {
		// TODO Auto-generated method stub
		Logs.i("--- info.propertyInfo resetRntm");

		this.clearStatus();
		this.runtimeReqLogIdentifier.clear();
		if (es != null) {
			for (Object oj : this.es) {
				((DescInfo) ((Entry) oj).getKey()).propertyInfo.clear();
				for (ScrollCounter sc : ((DescInfo) ((Entry) oj).getKey()).batch) {
					sc.runtimePiece = sc.piece;
				}
			}
		}
		continueItems.clear();
	}

	public static void packRequest(RequestSrc currSrc, DescInfo info,
			boolean isScroll) {
		int visittype = -1;
		String reqLog = null;
		Integer logType = info.requestType;
		if (info instanceof DBInfo) {
			visittype = AsyncRequest.VISIT_TYPE_LOCAL_DB;
			if (currSrc.getNavigationNode().getCurrVisitValue().size() > 1) {
				throw new IllegalStateException("invalid reqlog arg");
			}

			String sqlTmplate = SrcUtil.seek(info.propertyKey,
					info.propertyVal, "table");
			if (sqlTmplate != null) {
				String sql = null;
				sql = "select * from " + sqlTmplate;
				int idx = info.propertyKey.indexOf("table");
				info.propertyKey.remove("table");
				info.propertyVal.remove(idx);
				info.propertyKey.add("sql");
				info.propertyVal.add(sql);
			}
			sqlTmplate = SrcUtil
					.seek(info.propertyKey, info.propertyVal, "sql");
			if (sqlTmplate.contains("?")) {
				int index = -1;
				int i = 0;
				List<String> cValues = currSrc.getNavigationNode()
						.getCurrVisitValue();
				if (info.isCheck) {
					cValues = ((RecursiveListener) currSrc.callbackListener).nextLayerValue;
				}
				while ((index = sqlTmplate.indexOf("?")) != -1) {
					sqlTmplate = sqlTmplate.replace("?", cValues.get(i));
				}
				int idx = info.propertyKey.indexOf("sql");
				Logs.i("--- tag " + cValues + " " + currSrc);
				if (idx != -1)
					info.runtimeVal.set(idx, sqlTmplate);

			}

			reqLog = info.requestUrl + "@" + sqlTmplate;
			info.putInProperties(info.propertyKey, info.runtimeVal);
			Logs.i("--- tag " + reqLog);
		} else if (info instanceof SoapInfo) {
			visittype = AsyncRequest.VISIT_TYPE_SOAP;
			List<String> matchV = currSrc.getNavigationNode()
					.getCurrVisitValue();
			SoapInfo soap = (SoapInfo) info;
			soap.getRuntimeVal().clear();
			soap.getRuntimeVal().addAll(soap.getPropertyVal());
			int lnth = soap.getPropertyVal().size();
			String vl = null;
			int j = 0;
			for (int i = 0; i < lnth; i++) {
				vl = soap.getPropertyVal().get(i);
				if (vl.contains("?")) {
					if (matchV == null)
						continue;
					Logs.i("--- soap " + currSrc + " "
							+ soap.getRuntimeVal().get(i) + " " + matchV);
					soap.getRuntimeVal().set(
							i,
							soap.getRuntimeVal().get(i)
									.replace("?", matchV.get(j)));
					j++;
				}
			}
			info.putInProperties(info.propertyKey, info.runtimeVal);
			if (!info.batch.isEmpty())
				currSrc.transferPageParams(currSrc, info);
			int i = info.requestUrl.lastIndexOf("=");
			if (i == -1)
				throw new IllegalStateException("parse error on url.");
			reqLog = info.requestUrl.substring(i + 1)
					+ info.propertyInfo.toString().replaceAll(",", ";")
							.replaceAll(" ", "");
			if (reqLog.endsWith("=}"))
				reqLog = reqLog.replaceAll("\\=\\}", "=;}");
			else if (reqLog.endsWith("}}"))
				reqLog = reqLog.replaceAll("\\}\\}", "};}");
			else if (!reqLog.endsWith(";}") && reqLog.endsWith("}"))
				reqLog = reqLog.replaceAll("\\}", ";}");
			Logs.i("--- log " + reqLog);

		}
		Logs.i("--- debug log "
				+ currSrc.runtimeReqLogIdentifier.containsKey(reqLog) + " "
				+ currSrc.runtimeReqLogIdentifier.keySet().toString());

		if (!currSrc.runtimeReqLogIdentifier.containsKey(reqLog)) {
			currSrc.runtimeReqLogIdentifier.put(reqLog, new RequestLogInfo(
					logType));
			Logs.i("--- bug 111 ");
		}
		currSrc.isInPaging = isScroll;
		if (isScroll) {
			currSrc.runtimeReqLogIdentifier.get(reqLog).type = DescInfo.REQUEST_TYPE_PAGED;
		}

		currSrc.checkHandler(info);
		currSrc.getBaseAty().sendRequest(info.requestUrl, info.requestLog,
				info.propertyInfo, info.responseHandler, visittype, false);
	}

	public void transferPageParams(RequestSrc currSrc, DescInfo info) {
		info.propertyInfo.put("count", info.batch.get(currSrc.pieceIndex).count
				+ "");
	}

	public void lucene_nextpage(RequestSrc currSrc, DescInfo info) {
		String searchText = null;
		String searchTextAys[] = (searchText = info.propertyInfo
				.get("strSearch").toString().replaceAll("\\{", "")
				.replaceAll("\\}", "")).split(",");
		String tmp = null;
		for (String str : searchTextAys) {
			String newTmp = "";
			String[] ays = str.split(":");
			tmp = ays[1];
			Logs.i(" --- this.adapter.getCount() " + this.adapter.getCount()
					+ this.cached);
			String toreplace = tmp.substring(1, tmp.length() - 1);
			if (str.startsWith("\"start")) {
				String replaceCount = cached ? "0" : this.adapter.getCount()
						+ "";
				newTmp = ays[0] + ":" + tmp.replaceAll(toreplace, replaceCount);
				searchText = searchText.replaceAll(str, newTmp);
			} else if (str.startsWith("\"rows")) {
				newTmp = ays[0]
						+ ":"
						+ tmp.replaceAll(toreplace,
								info.batch.get(currSrc.pieceIndex).count + "");
				searchText = searchText.replaceAll(str, newTmp);
			}
		}
		Logs.i("=== lucene text: " + searchText);
		searchText = "{" + searchText + "}";
		info.propertyInfo.put("strSearch", searchText);
	}

	public void checkHandler(DescInfo info) {
		// TODO Auto-generated method stub
		if (info.responseHandler.lis != this) {
			info.responseHandler.lis = this;
		}
	}

	public void setTailOnItemClickListener(OnItemClickListener itemClickListener) {
		// TODO Auto-generated method stub
		this.tailItemClickListener = itemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		// TODO Auto-generated method stub
		this.selfDefineditemClickListener = itemClickListener;
	}

	public List<String> getNormalLinkForVisit() {
		// TODO Auto-generated method stub
		return navigationNode.argNames;
	}

	public List<String> getRuntimeLinkForVisit() {
		// TODO Auto-generated method stub
		return navigationNode.getRuntimeCurrVisitValue();
	}

	public List<String> setLinkForVisit(String[] name) {
		// TODO Auto-generated method stub
		return navigationNode.argNames = Arrays.asList(name);
	}

	public void setRuntimeLinkForVisit(String[] name) {
		// TODO Auto-generated method stub
		navigationNode.setRuntimeCurrVisitValue(Arrays.asList(name));
	}

	public void setRuntimeLinkForVisit(List<String> name) {
		// TODO Auto-generated method stub
		navigationNode.setRuntimeCurrVisitValue(name);
	}

	public String getLinkKeyForLabel() {
		// TODO Auto-generated method stub
		return navigationNode.naviLinkLabel;
	}

	public void setLinkforLabel(String string) {
		// TODO Auto-generated method stub
		navigationNode.naviLinkLabel = string;
	}

	public void setNavigationNodeLabel(String string) {
		// TODO Auto-generated method stub
		navigationNode.naviLinkLabel = string;
	}

	public NavigationNode getNavigationNode() {
		return navigationNode;
	}

	public void setNavigationNode(NavigationNode navigationNode) {
		this.navigationNode = navigationNode;
	}

	public void setRecusiveInfo(DescInfo srcInfo) {
		this.recursiveBool = true;
		this.checkInfo = srcInfo;
		checkInfo.requestType = DescInfo.REQUEST_TYPE_LOGIC;
		recursiveHead = true;

		this.setCallbackListener(new RecursiveListener(this) {

			@Override
			public boolean recursive(RequestSrc currRequestSrc,
					RequestSrc nextRequestSrc) {
				return currRequestSrc.callbackListener.logicListener
						.logic(this.oj);
			}

			@Override
			public void stop(RequestSrc currRequestSrc,
					RequestSrc nextRequestSrc) {
				// TODO Auto-generated method stub

				currRequestSrc
						.setRuntimeLinkForVisit(new String[] { this.logicListener.elseVIsitLink });
				RequestController
						.next(currRequestSrc,
								((LinkRequestItemClicker) currRequestSrc.linkRequestItemClicker).clickitem,
								currRequestSrc.getLinkRequestItemClicker()
										.getSrcRequests());

			}

		});
		// TODO Auto-generated method stub
	}

	public void preHandle() {
		// TODO Auto-generated method stub

	}

	public void postHandle(List<EntityObj> eob) {
		// TODO Auto-generated method stub

	}

}
