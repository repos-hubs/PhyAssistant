package com.jibo.app.research;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.BaseActivity;
import com.jibo.app.AdaptUI;
import com.jibo.app.ArticleActivity;
import com.jibo.app.DetailsData;
import com.jibo.app.GBIRequest;
import com.jibo.app.invite.ViewUtil;
import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.adapter.ListenerBox;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.adapter.MapAdapter.ActionListener;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.request.LogicListener;
import com.jibo.base.src.request.RequestInfos;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.base.src.request.ScrollCounter;
import com.jibo.base.src.request.config.DBInfo;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.base.src.request.impl.db.SqliteAdapterCentre;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.dao.DBFactory;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;

public class BrowseActivity extends ArticleActivity {

	private View detailist;
	public static RequestController srcRequests;
	boolean atTop;
	private View emptyView;

	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
		if (detailist == null) {
			getRequests();
			detailist = LayoutInflater.from(context).inflate(
					R.layout.detail_list, null);
			emptyView = LayoutInflater.from(context).inflate(
					R.layout.empty_frame, null);
			((TextView) emptyView.findViewById(R.id.emptytext))
					.setText(getString(R.string.empty_result));
		}
		entrance();
	}

	private void getRequests() {
		if (srcRequests == null) {
			srcRequests = new RequestController(detailist, this);
			srcRequests.dynActivity = true;
		}
	}

	List<ScrollCounter> count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		count = new ArrayList<ScrollCounter>();
		count.add(new ScrollCounter(20, 1));
		count.add(new ScrollCounter(10, -1));
	}

	private void entrance() {
		atTop = true;
		List<EntityObj> objs = new ArrayList<EntityObj>(0);
		EntityObj en = new EntityObj();
		en.fieldContents.put("title",
				getBaseContext().getString(R.string.browse_dept));
		en.fieldContents.put("drawable",
				R.drawable.research_browse_department_v2);
		objs.add(en);
		en = new EntityObj();
		en.fieldContents.put("title",
				getBaseContext().getString(R.string.en_journal));
		en.fieldContents.put("drawable", R.drawable.research_browse_journal_en);
		objs.add(en);
		en = new EntityObj();
		en.fieldContents.put("title",
				getBaseContext().getString(R.string.zh_cn_journal));

		en.fieldContents.put("drawable", R.drawable.research_browse_journal_cn);
		objs.add(en);
		en = new EntityObj();
		en.fieldContents.put("title",
				getBaseContext().getString(R.string.meshterm));

		en.fieldContents.put("drawable", R.drawable.research_browse_keywords);
		objs.add(en);
		en = new EntityObj();
		en.fieldContents.put("title", getBaseContext().getString(R.string.clc));

		en.fieldContents.put("drawable", R.drawable.research_browse_clc);
		objs.add(en);

		AdaptInfo adaptInfo = new AdaptInfo();
		adaptInfo.objectFields = new String[] { "drawable", "title" };
		adaptInfo.viewIds = new int[] { R.id.icon, R.id.title };
		adaptInfo.listviewItemLayoutId = R.layout.research_browse_list_item;
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(context).inflate(
				R.layout.journal_overview, null);
		this.setContentView(view);
		ListView listView = (ListView) view.findViewById(R.id.lst_item);
		MapAdapter adapter = new MapAdapter(this, adaptInfo);
		adapter.setItemDataSrc(new AdapterSrc(objs));
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(overviewItemClickListener);
	}

	OverviewItemClickListener overviewItemClickListener = new OverviewItemClickListener();

	protected class OverviewItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			getRequests();
			EntityObj nEntity = ((EntityObj) (parent
					.getItemAtPosition(position)));
			Logs.i(" nEntity " + nEntity);
			if (nEntity == null) {
				return;
			}

			String title = nEntity.fieldContents.get("title").toString();

			atTop = false;
			// 科室
			if (title.equals(view.getContext().getString(R.string.browse_dept))) {

				toCategory("dept",getString(R.string.browse_dept));

			} else if (title.equals(view.getContext().getString( // 外文期刊
					R.string.en_journal))) {
				toCategory("enJournal",getString(R.string.en_journal));
				MobclickAgent.onEvent(getApplicationContext(),
						"research_browse_enJournals_dir");
				uploadLoginLogNew("research_browse", "enJournals", "dir", null);
			} else if (title.equals(view.getContext().getString( // 中文期刊
					R.string.zh_cn_journal))) {
				toCategory("zhJournal",getString(R.string.zh_cn_journal));
				MobclickAgent.onEvent(getApplicationContext(),
						"research_browse_zhJournals_dir");
				uploadLoginLogNew("research_browse", "zhJournals", "dir", null);
			} else if (title.equals(view.getContext().getString(
					R.string.drugname))) { // 药品
				toDrug();
			} else if (title.equals(view.getContext().getString(
					R.string.meshterm))) { // 关键字
				toCategory("mesh",getString(R.string.meshterm));
				MobclickAgent.onEvent(getApplicationContext(),
						"research_browse_mesh_dir");
				uploadLoginLogNew("research_browse", "mesh", "dir", null);
			} else if (title.equals(view.getContext().getString(R.string.clc))) { // 中国图书馆分类法
				toCategory("clc",getString(R.string.clc));
				MobclickAgent.onEvent(getApplicationContext(),
						"research_browse_clc_dir");
				uploadLoginLogNew("research_browse", "clc", "dir", null);
			} else {
				atTop = true;
			}

		}

	}

	private void toCategory(String category,String title) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, CategoryActivity.class);
		intent.putExtra("category", category);
		intent.putExtra("title", title);
		startActivity(intent);
	}

	/**
	 * 期刊
	 * 
	 * @param name
	 * @param label
	 */
	public void toJournal(String name, String label) {
		// TODO Auto-generated method stub
		boolean isZh = name.equals("wanfang");
		String order = isZh ? " pinyin asc, " : "";
		RequestInfos is = new RequestInfos();
		AdaptInfo journalAdapt = new AdaptInfo();

		journalAdapt.objectFields = new String[] { "displayname" };
		journalAdapt.viewIds = new int[] { R.id.ListText1 };
		journalAdapt.listviewItemLayoutId = R.layout.mesh_cat;
		is.putSrc(new DBInfo(
				new String[] { "sql" },
				// new String[] {
				// " select c. from journal_category c, journalcategory_dept d where d.Category=c.'BroadTerms.Term'and d.Department ?"
				// },
				new String[] { " select Abbr displayname,_id journalId from "
						+ name + " c where Abbr not like '' order by " + order
						+ " c.Abbr asc" }, -1, DBFactory.SDCard_DB_NAME,
				"detail"), 1);
		RequestSrc journals = new GBIRequest(is, this, journalAdapt,
				"research_browse_" + (isZh ? "zh" : "en") + "Journals_dir") {
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

				return false;
			}

			@Override
			public void transferPageParams(RequestSrc currSrc, DescInfo info) {
				// TODO Auto-generated method stub
				super.lucene_nextpage(currSrc, info);
			}

			@Override
			public void onBack(RequestSrc targetSrc,
					RequestController srcRequests) {
				// TODO Auto-generated method stub
				entrance();
			}
		};
		journals.setLinkForVisit(new String[] { "journalId" });
		journals.setLinkforLabel("displayname");
		journals.getNavigationNode().setCurrLyLabel(label);

		AdaptInfo adaptInfo = new AdaptInfo();

		adaptInfo.objectFields = new String[] { "Title" };
		adaptInfo.viewIds = new int[] { R.id.title };
		adaptInfo.listviewItemLayoutId = R.layout.image_item_journal;

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
		dtlAdapt.objectFields = new String[] { "Title", "PublicDate", "Status" };
		dtlAdapt.viewIds = new int[] { R.id.ArticleTitle, R.id.date, R.id.Free };
		dtlAdapt.listviewItemLayoutId = R.layout.latest_list;
		RequestSrc dtl = new GBIRequest(dbs, this, dtlAdapt,
				new ResearchAdapter(GBApplication.gbapp, adaptInfo, false,
						DetailsData.viewedNews), "") {
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

			@Override
			public void postHandle(List<EntityObj> eob) {
				// TODO Auto-generated method stub
				super.postHandle(eob);
				if (this.pieceIndex == 0) {
					if (DetailsData.entities == null) {
						DetailsData.entities = eob;
					}
				} else {
					DetailsData.entities.addAll(eob);
				}
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
			public void transferPageParams(RequestSrc currSrc, DescInfo info) {
				// TODO Auto-generated method stub
				super.lucene_nextpage(currSrc, info);

			}
		};
		dtl.setEmptyview(emptyView);
		dtl.setItemClickListener(new DetailItemClickListener("research_browse_"
				+ (isZh ? "zh" : "en") + "Journals_paper", "id"));
		dtl.setToCache(true);
		srcRequests.setEmptyView(emptyView);
		srcRequests.addRequest(journals);
		srcRequests.addRequest(dtl);
		srcRequests.startCat(new String[] { "is not null" });

	}

	public void toDrug() {
		// TODO Auto-generated method stub

	}

	@Override
	public ListView getAdaptView() {
		// TODO Auto-generated method stub
		return detailist == null ? null : (ListView) detailist
				.findViewById(R.id.lst_item);
	}

	@Override
	public void onBack(Boolean stayTop, boolean isBackKey) {
		// TODO Auto-generated method stub
		if (atTop) {
			if (isBackKey)
				this.finishParentClass();
		} else if (srcRequests.onBack(true)) {
			entrance();
		}
	}
}
