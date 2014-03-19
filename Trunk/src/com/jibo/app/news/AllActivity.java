package com.jibo.app.news;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;
import com.jibo.activity.HomePageActivity;
import com.jibo.app.AdaptUI;
import com.jibo.app.ArticleActivity;
import com.jibo.app.DetailsData;
import com.jibo.app.GBIRequest;
import com.jibo.app.push.PushConst;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.request.RequestInfos;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.base.src.request.ScrollCounter;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.data.NewsDetailParser;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;
import com.jibo.util.tips.Mask;

public class AllActivity extends ArticleActivity {
	final int LEVL_CATEGORY = 0;
	final int LEVL_DETAIL = 1;
	int levl = -1;
	RequestSrc detail;
	RequestSrc uCat;
	public String bigCategory;
	TextView category;
	public boolean started;
	private Mask mask;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		resume();
	}

	public void start() {
		// TODO Auto-generated method stub
		toDl();
	}

	public void resume() {
		// if (detail != null && detail.adapter != null)
		// detail.adapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = LayoutInflater.from(context).inflate(R.layout.detail_list, null);
		this.setContentView(view);
		if (started) {
			started = true;
			this.start();
		}

	}

	protected View view;
	private RequestController srcRequests;

	public void toDl() {
		RequestInfos soaps = new RequestInfos();
		AdaptInfo dtlAdapt = new AdaptInfo();
		dtlAdapt.objectFields = new String[] { "imgUrl", "title", "newSummary",
				"stickMsg" };
		dtlAdapt.viewIds = new int[] { R.id.fileIcon, R.id.name, R.id.summary,
				R.id.special };
		dtlAdapt.listviewItemLayoutId = R.layout.image_item;

		soaps.putSrc(new SoapInfo(new String[] { SoapRes.KEY_NEWS_CATEGORY },
				new String[] { "" }, SoapRes.REQ_ID_GET_IMAGELIST_TOP,
				SoapRes.URLStickNews, "head"), 1);
		List<ScrollCounter> count = new ArrayList<ScrollCounter>();
		count.add(new ScrollCounter(20, 1));
		count.add(new ScrollCounter(10, -1));
		soaps.putSrc(new SoapInfo(new String[] { SoapRes.KEY_SINCE_ID,
				SoapRes.KEY_MAX_ID, SoapRes.KEY_COUNT,
				SoapRes.KEY_NEWS_CATEGORY, SoapRes.KEY_SEARCHVALUE },
				new String[] { "", "", "", "", "" },
				SoapRes.REQ_ID_GET_IMAGELIST, SoapRes.URLIMAGENews, count,
				"detail"), 1);

		RequestSrc dtl = new GBIRequest(soaps, this, null,
				AdaptUI.genNewsAdapter(), "") {
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
					if (cached) {// ×ªÒÆÌæ»»ÈÎÎñ
						shouldReplace = false;
						cached = false;
						return true;
					}
					return shouldReplace = false;
				}
				return false;

			}

			boolean shouldCache;

			@Override
			public boolean shouldCache(List<EntityObj> eob,
					Entry<DescInfo, Integer> entry) {
				// TODO Auto-generated method stub
				if (entry.getKey().requestLog == SoapRes.REQ_ID_GET_IMAGELIST) {
					if (!shouldCache) {
						shouldCache = true;
						// return false;
						return true;
					}
					return false;
				}
				return false;
			}

			@Override
			public void postHandle(List<EntityObj> eob) {
				// TODO Auto-generated method stub
				super.postHandle(eob);
				//
			}

		};
		dtl.setToCache(true);
		dtl.setItemClickListener(detailClickListener);
		srcRequests = new RequestController(view, this);
		srcRequests.addRequest(dtl);
		srcRequests.startCat(new String[] { "" });
	}

	DetailItemClickListener detailClickListener = new DetailItemClickListener();

	protected class DetailItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			EntityObj nEntity = ((EntityObj) (parent
					.getItemAtPosition(position)));
			final NewsAdapter nadapter = (NewsAdapter) ((HeaderViewListAdapter) parent
					.getAdapter()).getWrappedAdapter();
			try {
				DetailsData.entities = (List<EntityObj>) nadapter
						.getItemDataSrc().getContent();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			openNews(context, DetailsData.entities, nEntity);
			DetailsData.tappedne.fieldContents.put("seq", position);
			((NewsAdapter) ((HeaderViewListAdapter) (parent.getAdapter()))
					.getWrappedAdapter()).notifyDataSetChanged();
		}

	};

	public static void openNews(final Context context, final List list,
			EntityObj nEntity) {
		Logs.i(" nEntity " + nEntity);
		if (nEntity == null) {
			return;
		}
		DetailsData.viewedNews.add(nEntity.get("id"));

		DetailsData.fetchDetailOnSoap(nEntity, (BaseActivity) context, list,
				new BaseResponseHandler((BaseActivity) context,
						DialogRes.DIALOG_WAITING_FOR_DATA) {

					@Override
					public void onSuccess(Object content, int reqId) {
						// TODO Auto-generated method stub
						super.onSuccess(content, reqId);
						NewsDetailParser data = (NewsDetailParser) content;
						DetailsData.tappedne = data.getEntityDetail();

						if (DetailsData.tappedne != null) {
							DetailsData.startActivityOfDetail(context,
									DetailsData.tappedne,
									DetailsData.entities = list);
						}
					}

				});

	}

	@Override
	public void onBack(Boolean stayTop, boolean isBackKey) {
		// TODO Auto-generated method stub
		srcRequests.onBack(stayTop);
		if (PushConst.pushFlag == null || PushConst.pushFlag) {
			PushConst.pushmodule = PushConst.News_MODULE;
			if (PushConst.pushmodule == PushConst.News_MODULE) {
				startActivity(new Intent(this, HomePageActivity.class));
			}
		}
	}

	@Override
	public ListView getAdaptView() {
		// TODO Auto-generated method stub
		return (view == null ? null : (ListView) view
				.findViewById(R.id.lst_item));
	}

}
