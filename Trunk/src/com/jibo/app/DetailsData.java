package com.jibo.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;

import com.jibo.activity.BaseActivity;
import com.jibo.adapter.TravelAdapter.NewsDetailResponseHandler;
import com.jibo.adapter.TravelAdapter.OnDataSetListener;
import com.jibo.app.news.NewsDetailActivity;
import com.jibo.app.research.PaperDetailActivity;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.request.impl.db.AutoCache;
import com.jibo.base.src.request.impl.db.MySqlLiteAdapter;
import com.jibo.common.DialogRes;
import com.jibo.common.SoapRes;
import com.jibo.data.NewsDetailParser;
import com.jibo.data.entity.NewsDetail;
import com.jibo.data.entity.NewsEntity;
import com.jibo.data.entity.PaperDetailEntity;
import com.jibo.dbhelper.ResearchAdapter;
import com.jibo.net.AsyncSoapResponseHandler;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.ComparatorRepo;
import com.jibo.util.Logs;
import com.jibo.util.SharedPreferenceInfo;
import com.jibo.util.SharedPreferenceUtil;

public class DetailsData {
	static final int paper = 1;
	public static Set<String> readProfessional = new TreeSet(
			ComparatorRepo.stringKey);
	public static Object tmp;

	public static void laodReadFlags(Context context) {
		tmp = SharedPreferenceUtil.getValue(context, "GBApp",
				SharedPreferenceInfo.KEY_UNREAD_NEWS_PRO, String.class);
		readProfessional.addAll(tmp == null ? new ArrayList<String>() : Arrays
				.<String> asList(tmp.toString().split(",")));
	}

	public static List<EntityObj> entities = new ArrayList<EntityObj>();
	public static List<EntityObj> cacheEntities = new ArrayList<EntityObj>();

	public static List<EntityObj> getEntities() {
		return getEntities(false, paper);
	}

	public static List<EntityObj> getEntities(boolean setEmpty, int flag) {
		Logs.i("=== " + cacheEntities + " " + cacheEntities.size());
		if (flag == paper) {
			if (PaperDetailActivity.cacheEntered||(cacheEntities != null && cacheEntities.size() > 0)) {
				Logs.e("=== lower");
				if (setEmpty)
					cacheEntities.clear();
				return cacheEntities;
			} else {
				
				Logs.e("=== upper");
				return entities;

			}
		}
		return null;
	}

	public static void setCacheEntities(List<EntityObj> cacheEntities) {
		clearCacheEntities();
		DetailsData.cacheEntities
				.addAll(new ArrayList<EntityObj>(cacheEntities));
	}

	public static void clearCacheEntities() {
		if(PaperDetailActivity.cacheEntered)return;
		DetailsData.cacheEntities.clear();
	}

	public static EntityObj tappedne;
	public static Map<Integer, List<EntityObj>> onScreen = new HashMap<Integer, List<EntityObj>>();
	public static Set<String> viewedNews = new TreeSet(ComparatorRepo.stringKey);

	public static List<String> mIds = new ArrayList<String>();
	public static boolean activityStarted;

	public static void putAll(BaseActivity ba, List<EntityObj> pnews,
			AsyncSoapResponseHandler asyncSoapResponseHandler) {
		fetchDetailsOfListOnSoap(ba, pnews, asyncSoapResponseHandler);
	}

	public static Set<String> getViewedEntity() {
		if (viewedNews == null) {
			viewedNews = new TreeSet(ComparatorRepo.stringKey);
		}
		return viewedNews;
	}

	public static void startActivityOfDetail(final Context ctx,
			EntityObj pEntityObj) {
		startActivityOfDetail(ctx, pEntityObj, new ArrayList<EntityObj>());
	}
public static void startActivityOfDetail(final Context ctx,
			String id) {
		startActivityOfDetail(ctx, new EntityObj(id), new ArrayList<EntityObj>());
	}
	public static void startActivityOfDetail(final Context ctx, String id,
			List<EntityObj> newsCol) {
		DetailsData.entities = newsCol;
		if (!activityStarted) {
			activityStarted = true;
			Intent intent = new Intent(ctx, NewsDetailActivity.class);
			intent.putExtra("id", id);
			ctx.startActivity(intent);
		}
	}

	public static void startActivityOfDetail(final Context ctx,
			EntityObj pEntityObj, List<EntityObj> newsCol) {

		DetailsData.entities = newsCol == null ? new ArrayList(0) : newsCol;
		Intent intent = new Intent(ctx, NewsDetailActivity.class);
		try {
			ctx.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void fetchDetailOnSoap(EntityObj pNewsEntity,
			BaseActivity context, List<EntityObj> adapterList,
			AsyncSoapResponseHandler asyncSoapResponseHandler) {
		tappedne = pNewsEntity;
		if (entities.contains(pNewsEntity)) {
			NewsDetail ne = (NewsDetail) pNewsEntity.getObject("newsDetail");
			if (ne != null && ne.content != null && !ne.content.equals("")) {
				startActivityOfDetail(context, pNewsEntity, adapterList);
				return;
			}
		}
		Properties propertyInfo = new Properties();
		DetailsData.tappedne = pNewsEntity;
		propertyInfo.put(SoapRes.KEY_NEWS_DETAIL_ID, pNewsEntity.get("id"));
		context.sendRequest(SoapRes.URLNewsDetail, SoapRes.REQ_ID_NEWS_DETAIL,
				propertyInfo,
				asyncSoapResponseHandler == null ? new BaseResponseHandler(
						context, false) : asyncSoapResponseHandler);
	}

	/***
	 * 新闻详情的接口
	 * 
	 * @param id
	 * @param context
	 */
	public static void fetchDetailOnSoap(String id, final BaseActivity context,
			EntityObj pNewsEntity) {

		tappedne = pNewsEntity;
		String content;
		Object obj;
		if (entities.contains(pNewsEntity)
				&& (obj = (pNewsEntity.getObject("newsDetail"))) != null
				&& (content = ((NewsDetail) obj).content) != null
				&& !content.equals("")) {
			NewsDetail ne = (NewsDetail) pNewsEntity.getObject("newsDetail");
			if (ne != null && ne.content != null && !ne.content.equals("")) {
				startActivityOfDetail(context, pNewsEntity, new ArrayList(0));
				return;
			}
		}
		Properties propertyInfo = new Properties();
		DetailsData.tappedne = pNewsEntity;
		propertyInfo.put(SoapRes.KEY_NEWS_DETAIL_ID, pNewsEntity.get("id"));
		context.sendRequest(SoapRes.URLNewsDetail, SoapRes.REQ_ID_NEWS_DETAIL,
				propertyInfo, new BaseResponseHandler(context,
						DialogRes.DIALOG_WAITING_FOR_DATA) {

					@Override
					public void onSuccess(Object content, int methodId) {
						// TODO Auto-generated method stub
						super.onSuccess(content, methodId);
						NewsDetailParser data = (NewsDetailParser) content;
						DetailsData.tappedne = data.getEntityDetail();

						if (DetailsData.tappedne != null) {
							DetailsData.startActivityOfDetail(context,
									DetailsData.tappedne, DetailsData.entities);
						}
					}

				});
	}

	/***
	 * 新闻详情的接口
	 * 
	 * @param id
	 * @param context
	 */
	public static void fetchDetailOnSoap(String id, final BaseActivity context) {
		EntityObj pNewsEntity = new EntityObj();
		pNewsEntity.fieldContents.put("id", id);
		fetchDetailOnSoap(id, context, pNewsEntity);
	}

	public static void putInReadCache(Context ctx) {
		Object tmp = "";
		tmp = SharedPreferenceUtil.getValue(ctx, "GBApp",
				SharedPreferenceInfo.KEY_UNREAD_NEWS_TOP, String.class);
		DetailsData.viewedNews.addAll(tmp == null ? new ArrayList<String>()
				: Arrays.<String> asList(tmp.toString().split(",")));
	}

	public static void releaseToSharedPreference(Context ctx) {
		String value = DetailsData.viewedNews.toString().replaceAll("\\[", "")
				.replaceAll("\\]", "").replaceAll("[　 ]*", "");
		SharedPreferenceUtil.putValue(ctx, "GBApp",
				SharedPreferenceInfo.KEY_UNREAD_NEWS_TOP, value);
	}

	public static EntityObj getPreEntityObj() {
		if (tappedne == null) {
			throw new IllegalStateException("invalid start");
		}
		int i = entities.indexOf(tappedne);
		return entities.get(i < 2 ? 0 : i - 1);
	}

	public static EntityObj getNextEntityObj() {
		if (tappedne == null) {
			throw new IllegalStateException("invalid start");
		}
		return entities.get(entities.indexOf(tappedne) + 1);
	}

	public static void clear() {
		entities.clear();
	}

	public static boolean fetchDetailsOfListOnSoap(BaseActivity ctx,
			List<EntityObj> newsEntities,
			AsyncSoapResponseHandler asyncSoapResponseHandler) {
		return fetchDetailsOfListOnSoap(ctx, newsEntities,
				asyncSoapResponseHandler, false);
	}

	public static boolean fetchDetailsOfListOnSoap(BaseActivity ctx,
			List<EntityObj> newsEntities,
			AsyncSoapResponseHandler asyncSoapResponseHandler, boolean enable) {
		if (!enable) {
			return false;
		}

		EntityObj locate = DetailsData.tappedne;
		boolean detailsAllLoaded = true;
		int size = DetailsData.entities.size();
		int idx = DetailsData.entities.indexOf(locate);
		int dis = Math.max(idx, DetailsData.entities.size() - idx);
		Logs.i("--- idx " + idx + " dis " + dis);
		int neg = -1;
		int pos = -1;
		for (int i = 0; i < dis; i++) {
			neg = idx - i;
			pos = idx + i;
			goGetRemoteDetail(ctx, newsEntities, asyncSoapResponseHandler,
					detailsAllLoaded, size, neg);
			goGetRemoteDetail(ctx, newsEntities, asyncSoapResponseHandler,
					detailsAllLoaded, size, pos);

		}
		return detailsAllLoaded;
	}

	public static void fetchDetailsOnSoap(BaseActivity ctx,
			OnDataSetListener listener, NewsDetailResponseHandler handler,
			int position) {
		int size = DetailsData.entities.size();
		goGetRemoteDetail(ctx, DetailsData.entities, handler, true, size,
				position);
	}

	private static boolean goGetRemoteDetail(BaseActivity ctx,
			List<EntityObj> newsEntities,
			AsyncSoapResponseHandler asyncSoapResponseHandler,
			boolean detailsAllLoaded, int size, int neg) {
		if (0 <= neg && neg < size) {
			EntityObj nn = newsEntities.get(neg);
			putEntity(nn);
			if (nn.getObject("newsDetail") != null
					&& ((NewsDetail) nn.getObject("newsDetail")).id != null) {
				return detailsAllLoaded;
			}

			// 缓存
			// ResearchAdapter researchAdapter = new ResearchAdapter(ctx);
			// PaperDetailEntity entity =
			// researchAdapter.selectPaperDetail(nn.get("Id"));
			// if(entity != null){
			// int m = DetailsData.entities.lastIndexOf(nn);
			// try {
			// if (m >= 0) {
			// DetailsData.entities.get(m).fieldContents.put("newsDetail",
			// entity);
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// return detailsAllLoaded;
			// }

			detailsAllLoaded = false;
			Properties propertyInfo = new Properties();
			if (ctx instanceof NewsDetailActivity) {
				try {
					propertyInfo.put(SoapRes.KEY_NEWS_DETAIL_ID, nn.get("id"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				ctx.sendRequest(SoapRes.URLNewsDetail,
						SoapRes.REQ_ID_NEWS_DETAIL, propertyInfo,
						asyncSoapResponseHandler);
			} else if (ctx instanceof PaperDetailActivity) {
				propertyInfo.put("sign", "");
				propertyInfo.put("userId", "");
				propertyInfo.put(SoapRes.KEY_PAPER_ID, nn.get("Id"));
				ctx.sendRequest(SoapRes.URLResearchDetail,
						SoapRes.REQ_ID_GET_RESEARCH_DETAIL, propertyInfo,
						asyncSoapResponseHandler);
			}
		}
		return detailsAllLoaded;
	}

	public static void putEntity(EntityObj nn) {
		if (!entities.contains(nn)) {
			entities.add(nn);
		}
	}
}
