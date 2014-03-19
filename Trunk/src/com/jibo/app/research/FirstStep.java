package com.jibo.app.research;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.activity.BaseActivity;
import com.jibo.app.AdaptUI;
import com.jibo.app.DetailsData;
import com.jibo.app.GBIRequest;
import com.jibo.app.invite.ViewUtil;
import com.jibo.base.adapter.ListenerBox;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.adapter.MapAdapter.ActionListener;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.search.SearchAdapter;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestController;
import com.jibo.base.src.request.LogicListener;
import com.jibo.base.src.request.RequestInfos;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.base.src.request.ScrollCounter;
import com.jibo.base.src.request.config.DBInfo;
import com.jibo.base.src.request.config.DescInfo;
import com.jibo.base.src.request.config.SoapInfo;
import com.jibo.common.SoapRes;
import com.jibo.dao.DBFactory;
import com.jibo.util.Logs;
import com.umeng.analytics.MobclickAgent;

public class FirstStep {
	public static final String DEPT = "dept";
	public static final String JOURNAL = "journal";
	public static final String MESH = "mesh";
	public static final String CLC = "clc";
	
	public static Set<String> categoryKeys = new HashSet<String>();
	List<ScrollCounter> count;
	static{
		categoryKeys.add(MESH);
		categoryKeys.add(CLC);
	}
	public void process(final BaseActivity context, String category,
			RequestController srcRequests) {
		if (count == null) {
			count = new ArrayList<ScrollCounter>();
			count.add(new ScrollCounter(20, 1));
			count.add(new ScrollCounter(10, -1));			
		}
		if (category.equalsIgnoreCase(CLC)) {
			configCLC(context, category, srcRequests);
		} else if (category.equalsIgnoreCase(MESH)) {
			configMesh(context, category, srcRequests);
		} else if (category.equalsIgnoreCase("zhJournal")) {
			configJournal("wanfang", context, category, srcRequests);
		}else if (category.equalsIgnoreCase("enJournal")) {
			configJournal("pubmed", context, category, srcRequests);
		}else if (category.equalsIgnoreCase(DEPT)) {
			configDept(context, category, srcRequests);
		}
	}

	private void configDept(final BaseActivity context, String category,
			RequestController srcRequests) {
		// TODO Auto-generated method stub
			RequestInfos soaps = new RequestInfos();
			AdaptInfo deptAdapt = new AdaptInfo();
			deptAdapt.objectFields = new String[] { "name" };
			deptAdapt.viewIds = new int[] { R.id.ListText1 };
			deptAdapt.listviewItemLayoutId = R.layout.list_item_text_text_icon;
			soaps.putSrc(
					new DBInfo(
							new String[] { "sql", },
							new String[] { " select department name,departmentEn from department " },
							-1, DBFactory.SDCard_DB_NAME, "detail"), 1);
			RequestSrc dept = new GBIRequest(soaps, context, deptAdapt,
					"research_browse_dept_dir") {
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
				public void onBack(RequestSrc targetSrc,
						RequestController srcRequests) {
					// TODO Auto-generated method stub
					context.finish();
				}

			};
//			dept.categoryViewClickable = false;
			dept.getNavigationNode().setCurrLyLabel(
					context.getString(R.string.dept));
			dept.setLinkForVisit(new String[] { "departmentEN" });
			dept.setLinkforLabel("name");
			srcRequests.addRequest(dept);

			RequestInfos dtlInfos = new RequestInfos();
			dtlInfos.putSrc(
					new SoapInfo(
							new String[] { "sign", "strSearch" },
							new String[] {
									"",
									"{\"fq\":\"Category:\\\"?\\\"\",\"start\":\"0\",\"rows\":\"20\",\"sort\":\"PubDate desc\"}" },
							SoapRes.REQ_ID_GET_PAPER_LIST, SoapRes.URLRESEARCH,
							count, "detail"), 1);
			RequestSrc dtl = new GBIRequest(dtlInfos, context, null,
					AdaptUI.genResearchAdapter(), "") {
				boolean shouldReplace = false;

				@Override
				public boolean shouldInstead(List<EntityObj> eob,
						Entry<DescInfo, Integer> entry) {
					// TODO Auto-generated method stub
					MobclickAgent.onEvent(context,
							"research_browse_dept_dir");
					context.uploadLoginLogNew("research_browse", DEPT, "dir", null);
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
					super.postHandle(eob);
					if (this.pieceIndex == 0) {
						if (DetailsData.entities == null) {
							DetailsData.entities = eob;
						}
					} else {
						DetailsData.entities.addAll(eob);
					}
				}

				@Override
				public void transferPageParams(RequestSrc currSrc, DescInfo info) {
					// TODO Auto-generated method stub
					super.lucene_nextpage(currSrc, info);

				}

			};
//			dtl.categoryViewClickable = false;
			srcRequests.addRequest(dtl);
			dtl.setItemClickListener(new DetailItemClickListener(
					"research_browse_dept_papers", "id"));
			dtl.setToCache(true);
			srcRequests.setEmptyView(emptyView);
			
		}

	public void configJournal(String name, final BaseActivity context,
			String category, RequestController srcRequests) {

		// TODO Auto-generated method stub
		boolean isZh = name.equals("wanfang");
		String label = isZh ? context.getString(R.string.zh_cn_journal)
				: context.getString(R.string.en_journal);
		String order = isZh ? " pinyin asc, " : "";
		RequestInfos is = new RequestInfos();
		AdaptInfo journalAdapt = new AdaptInfo();

		journalAdapt.objectFields = new String[] { "name" };
		journalAdapt.viewIds = new int[] { R.id.ListText1 };
		journalAdapt.listviewItemLayoutId = R.layout.mesh_cat;
		is.putSrc(new DBInfo(
				new String[] { "sql" },
				// new String[] {
				// " select c. from journal_category c, journalcategory_dept d where d.Category=c.'BroadTerms.Term'and d.Department ?"
				// },
				new String[] { " select Abbr name,_id journalId from "
						+ name + " c where Abbr not like '' order by " + order
						+ " c.Abbr asc" }, -1, DBFactory.SDCard_DB_NAME,
				"detail"), 1);
		RequestSrc journals = new GBIRequest(is, context,new SearchAdapter(context, journalAdapt),
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
				context.finish();
			}
		};
//		journals.categoryViewClickable = false;
		journals.setLinkForVisit(new String[] { "journalId" });
		journals.setLinkforLabel("name");
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
		RequestSrc dtl = new GBIRequest(dbs, context, dtlAdapt,
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
//		dtl.categoryViewClickable = false;
		View emptyView = getEmpty(context);
		dtl.setEmptyview(emptyView);
		dtl.setItemClickListener(new DetailItemClickListener("research_browse_"
				+ (isZh ? "zh" : "en") + "Journals_paper", "id"));
		dtl.setToCache(true);
		srcRequests.setEmptyView(emptyView);
		srcRequests.addRequest(journals);
		srcRequests.addRequest(dtl);
		

	}

	public void launch(String category, RequestController srcRequests) {
		if (category.equalsIgnoreCase(CLC)) {
			srcRequests.startCat(new String[] { "17417" });
		} else if (category.equalsIgnoreCase(MESH)) {
			srcRequests.startCat(new String[] { "" });
		}else if (category.toLowerCase().contains(JOURNAL)) {
			srcRequests.startCat(new String[] { "is not null" });
		}else if (category.toLowerCase().contains(DEPT)) {
			srcRequests.startDl();
		}
	}

	public void configCLC(final BaseActivity context, String category,
			RequestController srcRequests) {
		// TODO Auto-generated method stub
		final RequestController ctrl = srcRequests;
		RequestInfos dbs = new RequestInfos();

		AdaptInfo adaptInfo = new AdaptInfo();
		adaptInfo.objectFields = new String[] { "name" };
		adaptInfo.viewIds = new int[] { R.id.ListText1 };
		adaptInfo.listviewItemLayoutId = R.layout.mesh_cat;
		adaptInfo.actionListeners = new ActionListener[] { new ActionListener(
				R.id.ListText2, ActionListener.OnClick) {

			@Override
			public void handle(View view, ListenerBox listener) {
				// TODO Auto-generated method stub
				MapAdapter baseAdapter = (MapAdapter) listener.getBaseAdapter();
				View v = ViewUtil.findListViewByItemView(view);
				if (v == null) {
					return;
				}
				try {
					ListView listview = (ListView) v.getParent();
					Integer pos = baseAdapter.getViewContentMap().get(v);
					// RequestSrc requestSrc = new RequestSrc(
					// ctrl.rts.get(ctrl.rts.size() - 1));
					String meshId = ((EntityObj) baseAdapter.getItem(pos)).get(
							"CLCId").toString();
					if (meshId != null && !meshId.equals("")) {
						ctrl.recur_enabled = false;
					}
					Logs.i("enter >>");
					// ctrl.addRequest(ctrl.rts.size() - 1, requestSrc);
					listview.performItemClick(view, pos, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} };
		MapAdapter clccat = new MapAdapter(context) {

			@Override
			protected void getViewInDetail(Object item, int position,
					View convertView) {
				// TODO Auto-generated method stub
				super.getViewInDetail(item, position, convertView);
				// if (((EntityObj) item).get("CLCId") == null
				// || ((EntityObj) item).get("CLCId").equals("")) {
				//
				// convertView.findViewById(R.id.ListText2).setVisibility(
				// View.GONE);
				// } else {
				// convertView.findViewById(R.id.ListText2).setVisibility(
				// View.VISIBLE);
				// }
			}

		};
		dbs.putSrc(
				new DBInfo(
						new String[] { "sql" },
						new String[] { " select clc_level_name name,clc_level_code ,clc_code CLCId from clc where parent_level_code=?" },
						-1, DBFactory.SDCard_DB_NAME, "detail"), 1);
		RequestSrc clc = new GBIRequest(dbs, context, adaptInfo, clccat,
				"research_browse_clc_dir") {
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
			public void onBack(RequestSrc targetSrc,
					RequestController srcRequests) {
				// TODO Auto-generated method stub
				if (this.recursiveHead) {
					((Activity) context).finish();
				} else
					super.onBack(targetSrc, srcRequests);
			}

		};

		clc.setLinkForVisit(new String[] { "clc_level_code" });
		clc.setLinkforLabel("name");
		clc.setRecusiveInfo(new DBInfo(
				new String[] { "sql" },
				new String[] { " select count(1) count from clc where parent_level_code=?" },
				-1, DBFactory.SDCard_DB_NAME, "check"));
		clc.setLogicListener(new LogicListener("CLCId") {

			@Override
			public boolean logic(Object result) {
				// TODO Auto-generated method stub

				Integer count = Integer.parseInt(((List<EntityObj>) result)
						.get(0).fieldContents.get("count").toString());
				return count > 0;
			}

		});
//		clc.categoryViewClickable = false;
		clc.getNavigationNode().setCurrLyLabel(context.getString(R.string.clc));
		srcRequests.addRequest(clc);

		RequestInfos infos = new RequestInfos();

		infos.putSrc(
				new SoapInfo(
						new String[] { "sign", "strSearch" },
						new String[] {
								"",
								"{\"fq\":\"CLCId:?\",\"start\":\"0\",\"rows\":\"20\",\"sort\":\"PubDate desc\"}" },
						SoapRes.REQ_ID_GET_PAPER_LIST, SoapRes.URLRESEARCH,
						count, "detail"), 1);
		RequestSrc dept = new GBIRequest(infos, context, null,
				AdaptUI.genResearchAdapter(), "") {
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
				super.postHandle(eob);
				if (this.pieceIndex == 0) {
					if (DetailsData.entities == null) {
						DetailsData.entities = eob;
					}
				} else {
					DetailsData.entities.addAll(eob);
				}
			}

			@Override
			public void transferPageParams(RequestSrc currSrc, DescInfo info) {
				// TODO Auto-generated method stub
				super.lucene_nextpage(currSrc, info);

			}

		};
//		dept.categoryViewClickable = true;
		View emptyView = getEmpty(context);
		dept.setEmptyview(emptyView);
		dept.setToCache(true);
		dept.setLinkForVisit(new String[] { "displayname" });
		dept.setLinkforLabel("displayname");
		dept.setOnItemClickListener(new DetailItemClickListener(
				"research_browse_CLC_papers", "CLCId"));

		srcRequests.setEmptyView(emptyView);
		srcRequests.addRequest(dept);

	}

	View emptyView;

	private View getEmpty(final BaseActivity context) {
		if (emptyView == null) {
			emptyView = LayoutInflater.from(context).inflate(
					R.layout.empty_frame, null);
			((TextView) emptyView.findViewById(R.id.emptytext))
					.setText(R.string.office_empty);
		}
		return emptyView;
	}

	public void configMesh(final BaseActivity context, String category,
			RequestController srcRequests) {

		// TODO Auto-generated method stub
		final RequestController ctrl = srcRequests;
		RequestInfos dbs = new RequestInfos();

		AdaptInfo adaptInfo = new AdaptInfo();
		adaptInfo.objectFields = new String[] { "name" };
		adaptInfo.viewIds = new int[] { R.id.ListText1 };
		adaptInfo.listviewItemLayoutId = R.layout.mesh_cat;
		dbs.putSrc(
				new DBInfo(
						new String[] { "sql" },
						new String[] { " select DescriptorName name,TreeNumber number,MeshId from mesh where ParentTreeNumber = '?'" },
						-1, DBFactory.SDCard_DB_NAME, "detail"), 1);
		adaptInfo.actionListeners = new ActionListener[] { new ActionListener(
				R.id.ListText2, ActionListener.OnClick) {

			@Override
			public void handle(View view, ListenerBox listener) {
				// TODO Auto-generated method stub
				MapAdapter baseAdapter = (MapAdapter) listener.getBaseAdapter();
				View v = ViewUtil.findListViewByItemView(view);
				if (v == null) {
					return;
				}
				try {
					ListView listview = (ListView) v.getParent();
					Integer pos = baseAdapter.getViewContentMap().get(v);
					// RequestSrc requestSrc = new RequestSrc(
					// ctrl.rts.get(ctrl.rts.size() - 1));
					String meshId = ((EntityObj) baseAdapter.getItem(pos)).get(
							"MeshId").toString();
					if (meshId != null && !meshId.equals("")) {
						ctrl.recur_enabled = false;
					}
					Logs.i("enter >>");
					// ctrl.addRequest(ctrl.rts.size() - 1, requestSrc);
					listview.performItemClick(view, pos, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} };
		MapAdapter meshcat = new MapAdapter(context) {

			@Override
			protected void getViewInDetail(Object item, int position,
					View convertView) {
				// TODO Auto-generated method stub
				super.getViewInDetail(item, position, convertView);
				// if (((EntityObj) item).get("MeshId") == null
				// || ((EntityObj) item).get("MeshId").equals("")) {
				// convertView.findViewById(R.id.ListText2).setVisibility(
				// View.GONE);
				// } else {
				// convertView.findViewById(R.id.ListText2).setVisibility(
				// View.VISIBLE);
				// }
			}

		};
		RequestSrc dtl = new GBIRequest(dbs, context, adaptInfo, meshcat,
				"research_browse_mesh_dir") {
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
			public void onBack(RequestSrc targetSrc,
					RequestController srcRequests) {
				// TODO Auto-generated method stub
				if (this.recursiveHead) {
					context.finish();
				} else
					super.onBack(targetSrc, srcRequests);
			}
		};
//		dtl.categoryViewClickable = false;
		dtl.setLinkForVisit(new String[] { "number" });
		dtl.setLinkforLabel("name");
		dtl.setRecusiveInfo(new DBInfo(
				new String[] { "sql" },
				new String[] { " select count(1) count from mesh where ParentTreeNumber='?'" },
				-1, DBFactory.SDCard_DB_NAME, "check"));
		dtl.setLogicListener(new LogicListener("MeshId") {

			@Override
			public boolean logic(Object result) {
				// TODO Auto-generated method stub

				Integer count = Integer.parseInt(((List<EntityObj>) result)
						.get(0).fieldContents.get("count").toString());
				return count > 0;
			}

		});

		dtl.getNavigationNode().setCurrLyLabel(
				context.getString(R.string.meshterm));

		srcRequests.addRequest(dtl);
		RequestInfos infos = new RequestInfos();

		infos.putSrc(
				new SoapInfo(
						new String[] { "sign", "strSearch" },
						new String[] {
								"",
								"{\"fq\":\"MeshId:?\",\"start\":\"0\",\"rows\":\"20\",\"sort\":\"PubDate desc\"}" },
						SoapRes.REQ_ID_GET_PAPER_LIST, SoapRes.URLRESEARCH,
						count, "detail"), 1);
		final RequestSrc dept = new GBIRequest(infos, context,
				AdaptUI.genResearchAdapter(), "") {
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
//		dept.categoryViewClickable = false;
		View emptyView = getEmpty(context);
		dept.setEmptyview(emptyView);
		dept.setLinkForVisit(new String[] { "displayname" });
		dept.setLinkforLabel("displayname");
		dept.setOnItemClickListener(new DetailItemClickListener(
				"research_browse_mesh_paper", "MeshId"));
		dept.setToCache(true);
		srcRequests.setEmptyView(emptyView);
		srcRequests.addRequest(dept);

	}

}
