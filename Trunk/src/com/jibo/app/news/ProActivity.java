package com.jibo.app.news;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;
import com.jibo.app.AdaptUI;
import com.jibo.app.DetailsData;
import com.jibo.app.GBIRequest;
import com.jibo.app.news.AllActivity.DetailItemClickListener;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.NavigationNode;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.request.RequestInfos;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.base.src.request.ScrollCounter;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.NewsDetailParser;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;

public class ProActivity extends CatActivity {

	@Override
	public void firstLaunch(String[] cat) {
		// TODO Auto-generated method stub
		AdaptInfo catAdapt = new AdaptInfo();
		catAdapt.objectFields = new String[] { "name", "newsCount" };
		catAdapt.viewIds = new int[] { R.id.ListText1, R.id.ListText2 };
		catAdapt.listviewItemLayoutId = R.layout.list_item_text_text_icon;
		RequestInfos soapInfos1 = new RequestInfos();
		soapInfos1.putSrc(new SoapInfo(
				new String[] { SoapRes.KEY_NEWS_BIGCATEGORY }, cat,
				SoapRes.REQ_ID_GET_NEWS_CATEGORIES_BY_BIGCATEGROY,
				SoapRes.URLIMAGENews, "cat"), 1);
		RequestSrc line = new RequestSrc(soapInfos1, this, null,AdaptUI.genNewsAdapter()) {

			@Override
			public boolean shouldInstead(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub

				return true;
			}

			@Override
			public boolean shouldCache(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				sort(eob, entry);
				return true;
			}

			@Override
			public NavigationNode getNavigationNode() {
				// TODO Auto-generated method stub
				return super.getNavigationNode();
			}

			@Override
			public void preHandle() {
				// TODO Auto-generated method stub
				super.preHandle();
				this.asyncHandler.removeFooter();
			}

		};
		line.setToCache(true);
		line.setEntrance(new String[]{""});
		line.setLinkForVisit(new String[] { "name" });
		line.setLinkforLabel("name");
		line.mId = 0;
		RequestInfos soaps = new RequestInfos();
		soaps.putSrc(new SoapInfo(new String[] { SoapRes.KEY_NEWS_CATEGORY },
				new String[] { "?" }, SoapRes.REQ_ID_GET_IMAGELIST_TOP,
				SoapRes.URLStickNews, "head"), 1);
		List<ScrollCounter> count = new ArrayList<ScrollCounter>();
		count.add(new ScrollCounter(20, 1));
		count.add(new ScrollCounter(10, -1));
		soaps.putSrc(new SoapInfo(new String[] { SoapRes.KEY_SINCE_ID,
				SoapRes.KEY_MAX_ID, SoapRes.KEY_COUNT,
				SoapRes.KEY_NEWS_CATEGORY, SoapRes.KEY_SEARCHVALUE },
				new String[] { "", "", "", "?", "" },
				SoapRes.REQ_ID_GET_IMAGELIST, SoapRes.URLIMAGENews, count,
				"detail"), 1);
		RequestSrc dtl = new GBIRequest(soaps, this, null,AdaptUI.genNewsAdapter(),"") {
			boolean shouldReplace = false;

			@Override
			public boolean shouldInstead(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				if (entry.getKey().requestLog == SoapRes.REQ_ID_GET_IMAGELIST_TOP) {
					if (eob.isEmpty()) {
						return shouldReplace = false;
					}
					shouldReplace = true;
					return true;
				} else if (entry.getKey().requestLog == SoapRes.REQ_ID_GET_IMAGELIST) {
					if (shouldReplace) {
						return false;
					}
					Logs.i("--- info.propertyInfo cached i " + cached);
					if (cached) {// ת���滻����
						shouldReplace = false;
						cached = false;
						return true;
					}
					return shouldReplace = false;
				}
				return false;

			}
			boolean shouldCached;
			boolean shouldCache = false;
			@Override
			public boolean shouldCache(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				if (entry.getKey().requestLog == SoapRes.REQ_ID_GET_IMAGELIST) {
					if (!shouldCache) {
						shouldCache = true;
						// return false;
						return shouldCached = true;
					}
					return shouldCached = false;
				}
				return false;
			}

			@Override
			public void adjust4EmptyView() {
				// TODO Auto-generated method stub
				if(this.tmpSoap>=1&&shouldCached){
					super.adjust4EmptyView();					
				}
			}
			


			
		};
		
		dtl.setToCache(true);
		dtl.setOnItemClickListener(detailClickListener);
		srcRequests = new RequestController(view, this);
		srcRequests.setEmptyView(emptyView);
		srcRequests.addRequest(line);
		srcRequests.addRequest(dtl);
		enter();
	}

	protected void enter() {
		// TODO Auto-generated method stub
		String department = SharedPreferencesMgr.getDept();
		if(department.equals("")){
			srcRequests.startCat(new String[]{""});
		}else
		srcRequests.startCat(new String[]{department}, 1,department);
	}
	DetailItemClickListener detailClickListener = new DetailItemClickListener();

	protected class DetailItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			EntityObj nEntity = ((EntityObj) (parent
					.getItemAtPosition(position)));
			Logs.i(" nEntity " + nEntity);
			if (nEntity == null) {
				return;
			}
			DetailsData.viewedNews.add(nEntity.get("id"));

			final NewsAdapter nadapter = (NewsAdapter) ((HeaderViewListAdapter) parent
					.getAdapter()).getWrappedAdapter();
			try {
				DetailsData.entities = (List<EntityObj>) nadapter
						.getItemDataSrc().getContent();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			DetailsData.fetchDetailOnSoap(nEntity, ProActivity.this,
					DetailsData.entities, new BaseResponseHandler(
							(BaseActivity) context,
							DialogRes.DIALOG_WAITING_FOR_DATA) {

						@Override
						public void onSuccess(Object content, int reqId) {
							// TODO Auto-generated method stub
							super.onSuccess(content, reqId);
							NewsDetailParser data = (NewsDetailParser) content;
							DetailsData.tappedne = data.getEntityDetail();

							if (DetailsData.tappedne != null) {
								DetailsData
										.startActivityOfDetail(
												context,
												DetailsData.tappedne,
												DetailsData.entities = ((List) nadapter
														.getItemDataSrc()
														.getContent()));
							}
						}

					});
			DetailsData.tappedne.fieldContents.put("seq", position);
			((NewsAdapter) ((HeaderViewListAdapter) (parent.getAdapter()))
					.getWrappedAdapter()).notifyDataSetChanged();
		}
	};

	
	@Override
	public ListView getAdaptView() {
		// TODO Auto-generated method stub
		return  (view==null?null:(ListView)view.findViewById(R.id.lst_item));
	};
	
}
