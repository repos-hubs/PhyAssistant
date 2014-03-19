package com.jibo.app.interact;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.api.android.GBApp.R;
import com.jibo.activity.AHFSDisclaimerActivity;
import com.jibo.activity.BaseActivity;
import com.jibo.activity.ContactMufacturedetailActivity;
import com.jibo.activity.DrugDetailActivity;
import com.jibo.activity.DrugEditInfoActivity;
import com.jibo.activity.HomePageActivity;
import com.jibo.activity.LilyDrugDetailActivity;
import com.jibo.activity.NewDrugReferenceActivity;
import com.jibo.activity.UpdateInviteCodeActivity;
import com.jibo.app.AdaptUI;
import com.jibo.app.DetailsData;
import com.jibo.app.GBIRequest;
import com.jibo.app.invite.Data;
import com.jibo.app.invite.SearchActivity;
import com.jibo.app.research.DetailItemClickListener;
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
import com.jibo.common.Combination;
import com.jibo.common.ComparatorInteractionPid;
import com.jibo.common.ComparatorList;
import com.jibo.common.Constant;
import com.jibo.common.DeviceInfo;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.dao.DBFactory;
import com.jibo.data.GetgoogleParser;
import com.jibo.data.InteractionFindPaser;
import com.jibo.data.InteractionPaser;
import com.jibo.data.InviteFriendsPaser;
import com.jibo.data.entity.AshfEntity;
import com.jibo.data.entity.DrugInteractEntity;
import com.jibo.data.entity.InteractionPidsEntity;
import com.jibo.dbhelper.InteractAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;
import com.jibo.util.SharedPreferenceUtil;
import com.jibo.util.ThreadHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class InteractDetail extends BaseActivity implements OnClickListener {

	public boolean empty = false;
	private Context context;
	private Button btn1;
	private ToggleButton btn2;
	private Button btn3;
	private ToggleButton btn3toggle;
	private Button btn4;
	private LinearLayout btnLayout1;
	private LinearLayout btnLayout2;
	private LinearLayout btnLayout3;
	private LinearLayout btnToggleLayout3;
	private LinearLayout btnLayout4;
	private String cultureInfo = "en-US";//文章的语言，初始为英语
	public String content;
	public List<Integer> sizes = new ArrayList<Integer>();
	final Handler myHandler = new Handler();
	private WebView webView;
	private String title;
	private ImageButton home;
	private String drugName;
	private String drugId;
	private InteractAdapter dao;
	private Set<Map<String, String>> interactPIDList;
	private Set<String> pidSet = new LinkedHashSet<String>();
	private String eachMainDrugId;
	private String eachMainDrug;
	private boolean webHasCleared = false;// webview是否重新加载了，
	BaseResponseHandler baseHandler = new BaseResponseHandler(this,true);
	public String jsonEachDrug;
	public String jsonAllDrug;
	/** html页面相关参数 */
	private final String mimeType = "text/html";
	private final String encoding = "utf-8";
	/** AHFS说明书数据 */
	public ArrayList<AshfEntity> ashfDatas;
	private View dialogprogress;
	private String nowPage;
	private List<InteractionPidsEntity> interactionPidsEntityList;
	private List<String> pidList;
	public String ahfsInteractionStr = "";
	private boolean isnetwork;
	private String ahfsCultureInfo = "en-US"; //ahfs的语言
	boolean bPreMode = false ; //false为专业
	private TextView emptyView;
//	private AutoCompleteTextView drugSearch;
	private String localLanguage;
	
	public InteractDetail() {
		super();
	}

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.interat_detail_all_layout);
		context = this;
		init();
	}

	private void init() {
		isnetwork = new DeviceInfo(context).isNetWorkEnable();
		findview();
		dao = new InteractAdapter(context, Integer.parseInt(Constant.dbVersion));
		localLanguage = Locale.getDefault().getLanguage();
		if("en".equals(localLanguage)){
			localLanguage = "en-US";
		}else if("zh".equals(localLanguage)){
			localLanguage = "zh-CN";
		}
		loadData();
		loadWebView();
		setListener();
		navigate();
		if (!SharedPreferencesMgr.getInviteCode().equals("")){		
			bPreMode = true;
		}
	}

	private void findview() {
		home = (ImageButton) findViewById(R.id.imgbtn_home);
		webView = (WebView) findViewById(R.id.webView);
		emptyView = (TextView) findViewById(R.id.favorite_text);
//		drugSearch = (AutoCompleteTextView) findViewById(R.id.search_edit);
//		drugSearch.setFocusable(false);
	}

	private void loadData() {
		Intent i = getIntent();
		String style = i.getStringExtra("style");
		String zone = i.getStringExtra("zone");
		Set<String> tempPidSet = (Set<String>) i.getSerializableExtra("pidSet");
		pidList = new ArrayList(tempPidSet);
	//	if("local".equals(zone)){
		if(true){
			//调用本地
			interactPIDList = (Set<Map<String, String>>) i.getSerializableExtra("interactPIDList");
			List list = new ArrayList(interactPIDList);
			sortList(list);
			if ("each".equals(style)) {
				// 去单个界面
				eachMainDrugId = i.getStringExtra("mainDrugId");
				eachMainDrug = i.getStringExtra("mainDrug");
				getEachDrug(list);//主药+次药
				nowPage = "eachDrug";
			} else {
				getAllDrug(list);
				nowPage = "allDrug";
			}			
		}else{
			//调用服务器
			interactionPidsEntityList = (List<InteractionPidsEntity>) i.getSerializableExtra("interactionPidsEntityList");
			ComparatorInteractionPid comparator1 = new ComparatorInteractionPid();
			Collections.sort(interactionPidsEntityList,comparator1);
			if ("each".equals(style)) {
				// 去单个界面
				eachMainDrugId = i.getStringExtra("mainDrugId");
				eachMainDrug = i.getStringExtra("mainDrug");
				getRemoteEachDrug();//主药+次药
				nowPage = "eachDrug";
			} else {
				getRemoteAllDrug();
				nowPage = "allDrug";
			}		
		}
	}

	private void getRemoteAllDrug() {
		Iterator it = interactionPidsEntityList.iterator();
		JSONArray array = new JSONArray();
		while (it.hasNext()) {
			InteractionPidsEntity interactionPidsEntity = (InteractionPidsEntity) it.next();
			String pid1 = interactionPidsEntity.getPid1();
			String pid2 = interactionPidsEntity.getPid2();
			pidSet.add(pid1);
			pidSet.add(pid2);
			Map<String, String> m1 = new HashMap<String, String>();
			m1.put("pid", pid1);
			Map<String, String> m2 = new HashMap<String, String>();
			m2.put("pid", pid2);

			String description = interactionPidsEntity.getDescription();
			String comments = interactionPidsEntity.getComments();
			JSONObject item = new JSONObject();
			try {
				item.put("drug_title",  "<div class='drug_names'>"+interactionPidsEntity.getPid1DrugName() + " + "
						+ interactionPidsEntity.getPid2DrugName()+"</div>");
				String str = "<div class='description'><strong>"+context.getString(R.string.interaction)+":</strong><br/>";
				str += description + "</div><br/>";
				if(!TextUtils.isEmpty(comments)){
					str += "<div class='comments'>"+context.getString(R.string.comments)+":<br/>";
					str += comments + "</div>";
				}
				content = str;
				item.put("drug_content", content);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(item);
		}
		jsonAllDrug = array.toString();
	}

	private void getRemoteEachDrug() {
		Iterator<InteractionPidsEntity> it = interactionPidsEntityList.iterator();
		JSONArray array = new JSONArray();
		JSONObject item1 = new JSONObject();
		try {
			item1.put("drug_title", "<div class='drug_names'>"+eachMainDrug+"</div>");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		array.put(item1);
		while (it.hasNext()) {
			InteractionPidsEntity interactionPidsEntity = (InteractionPidsEntity) it.next();
			String pid1 = interactionPidsEntity.getPid1();
			String pid2 = interactionPidsEntity.getPid2();
			pidSet.add(pid1);
			pidSet.add(pid2);
			if (pid1.equals(eachMainDrugId)||pid2.equals(eachMainDrugId)) {
				String description = interactionPidsEntity.getDescription();
				String comments = interactionPidsEntity.getComments();
				String eachChildDrug = "";
				if (pid1.equals(eachMainDrugId)){
					eachChildDrug = interactionPidsEntity.getPid2DrugName();
				}else{
					eachChildDrug = interactionPidsEntity.getPid1DrugName();
				}
				JSONObject item = new JSONObject();
				try {
					String str = "<div class='eachChildDrug'> + "
							+ eachChildDrug + "</div><br/>";
					str += "<div class='description'><strong>"+context.getString(R.string.interaction)+":</strong><br/>";
					str += description + "</div><br/>";
					if(!TextUtils.isEmpty(comments)){
						str += "<div class='comments'>"+context.getString(R.string.comments)+":<br/>";
						str += comments + "</div>";
					}
					content = str;
					item.put("drug_content", content);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				array.put(item);
			} 
		}
		jsonEachDrug = array.toString();
	}
	public void sortList(List<Map<String,String>> list){
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Map<String, String> map = (Map<String, String>) it.next();
			String pid1 = map.get("pid1");
			String pid2 = map.get("pid2");
			String iid = map.get("iid");
			pidSet.add(pid1);
			pidSet.add(pid2);
			Map<String, String> m1 = new HashMap<String, String>();
			m1.put("pid", pid1);
			Map<String, String> m2 = new HashMap<String, String>();
			m2.put("pid", pid2);
			m1.put("CultureInfo", localLanguage);
			m2.put("CultureInfo", localLanguage);
			List<DrugInteractEntity> childDrug1 = dao
					.selectDrugNameAndPID(m1);
			String eachChildDrug1 = childDrug1.get(0).drugName;
			List<DrugInteractEntity> childDrug2 = dao
					.selectDrugNameAndPID(m2);
			String eachChildDrug2 = childDrug2.get(0).drugName;
			map.put("pid1Name", eachChildDrug1);
			map.put("pid2Name", eachChildDrug2);
		}
		ComparatorList comparator = new ComparatorList();
		Collections.sort(list, comparator);
	}
	private void getAllDrug(List<Map<String,String>> list) {
				Iterator it = list.iterator();
				JSONArray array = new JSONArray();
				while (it.hasNext()) {
					Map<String, String> map = (Map<String, String>) it.next();
					String pid1 = map.get("pid1");
					String pid2 = map.get("pid2");
					
					String eachChildDrug1 = map.get("pid1Name");
					String eachChildDrug2 =  map.get("pid2Name");

					Map<String, String> m3 = new HashMap<String, String>();
					String iid = map.get("iid");
					m3.put("iid", iid);
					List<DrugInteractEntity> childDrugInteraction = dao
							.selectInteractorDetail(m3);
					String description = childDrugInteraction.get(0).description;
					String comments = childDrugInteraction.get(0).comments;
					JSONObject item = new JSONObject();
					try {
						item.put("drug_title", "<div class='drug_names'>"+eachChildDrug1 + " + "
								+ eachChildDrug2+"</div>");
						String str = "<div class='description'><strong>"+context.getString(R.string.interaction)+":</strong><br/>";
						str += description + "</div><br/>";
						if(!TextUtils.isEmpty(comments)){
							str += "<div class='comments'>"+context.getString(R.string.comments)+":<br/>";
							str += comments + "</div>";
						}
						content = str;
						item.put("drug_content", content);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					array.put(item);
				}
				jsonAllDrug = array.toString();
	}

	private void getEachDrug(List<Map<String,String>> list) {
				Iterator<Map<String, String>> it = list.iterator();
				JSONArray array = new JSONArray();
				JSONObject item1 = new JSONObject();
				try {
					item1.put("drug_title", "<div class='drug_names'>"+eachMainDrug+"</div>");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				array.put(item1);
				while (it.hasNext()) {
					Map<String, String> map = (Map<String, String>) it.next();
					String pid1 = map.get("pid1");
					String pid2 = map.get("pid2");
//					pidSet.add(pid1);
//					pidSet.add(pid2);
//					Map<String, String> m = new HashMap<String, String>();
//					if (pid1.equals(eachMainDrugId)) {
//						// 根据pid获取其他药品名
//						m.put("pid", pid2);
//					} else if (pid2.equals(eachMainDrugId)) {
//						// 根据pid获取其他药品名
//						m.put("pid", pid1);
//					}
//					m.put("CultureInfo", cultureInfo);
//					List<DrugInteractEntity> childDrug = dao.selectDrugNameAndPID(m);
//					String eachChildDrug = childDrug.get(0).drugName;
					String eachChildDrug = "";
					if (pid1.equals(eachMainDrugId)) {
						// 根据pid获取其他药品名
						eachChildDrug = map.get("pid2Name");
					} else if (pid2.equals(eachMainDrugId)) {
						// 根据pid获取其他药品名
						eachChildDrug = map.get("pid1Name");
					}else{
						continue;
					}
					
					Map<String, String> m1 = new HashMap<String, String>();
					String iid = map.get("iid");
					m1.put("iid", iid);
					List<DrugInteractEntity> childDrugInteraction = dao
							.selectInteractorDetail(m1);
					String description = childDrugInteraction.get(0).description;
					String comments = childDrugInteraction.get(0).comments;
					JSONObject item = new JSONObject();
					try {
						String str = "<div class='eachChildDrug'> + "
								+ eachChildDrug + "</div><br/>";
						str += "<div class='description'><strong>"+context.getString(R.string.interaction)+":</strong><br/>";
						str += description + "</div><br/>";
						if(!TextUtils.isEmpty(comments)){
							str += "<div class='comments'>"+context.getString(R.string.comments)+":<br/>";
							str += comments + "</div>";
						}
						content = str;
						item.put("drug_content", content);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					array.put(item);
				}
				jsonEachDrug = array.toString();
	}
	private void loadWebView() {
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		// 设置javaScript可用
		webView.addJavascriptInterface(new ContactPlugin(), "contact");
		webView.loadUrl("file:///android_asset/webview/DrugInteraction.html");
		webView.setWebViewClient(new WebViewClient() {
		    public void onPageFinished(WebView view, String url) {
		    	myHandler.post(new Runnable() {
		        	public void run() {
		        		if(	nowPage == "eachDrug"){
		        			webView.loadUrl("javascript:show_detail()");
		        		}else if( nowPage == "allDrug"){
		        			webView.loadUrl("javascript:show_list()");
		        		}
		        	}
		        });
		    	super.onPageFinished(view, url);
		    }
		});
	}
	private final class ContactPlugin {
		public String getEachDrugs() {
			return jsonEachDrug;
		}

		public String getAllDrugs() {
			return jsonAllDrug;
		}
		public String getCompleteInteractionDetails() {
			return context.getString(R.string.complete_interaction_details);
		}
		public void nextPage() {
			if (!pidSet.isEmpty()) {
				MobclickAgent.onEvent(getApplicationContext(),"interaction_all_drug_more");
				uploadLoginLogNew("Interaction","interaction_all_drug_more", "interaction_all_drug_more_click", null);
				Iterator<String> it = pidSet.iterator();
				String id;
				ahfsInteractionStr = "";
				while (it.hasNext()) {
					id = it.next();
					checkAshfDatas(id);
					ahfsInteractionStr += buildAHFSString(false);
				}
				if(!TextUtils.isEmpty(ahfsInteractionStr)){
					if (!bPreMode)
						ahfsInteractionStr+=getResources()
						.getString(R.string.invite_notify_image)
						+ "<input type=\"image\" id=\"button\" onclick=\"window.jscall.more()\" src=\"file:///android_asset/more.png\"/>";
					updateWebContent(ahfsInteractionStr);
				}else{
					showEmptyView();
				}

			}
		}

		private void checkAshfDatas(String id) {
			// 获取 缓存
			try {
				ashfDatas = new ArrayList<AshfEntity>();
				AshfEntity entity = new AshfEntity();
				Object o = decryptZipFile(dao.selectfieldByid(id));
				String json = "";
				if(o!=null){
					json = o.toString();
					entity.paserJson(json);
					ashfDatas.add(entity);
					if (null != ashfDatas) {
						Util.ahfsMap.put(id, ashfDatas);
					}
				}else{
					json="";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showEmptyView() {
		String str = "<div style='position:relative;'> <img src='file:///android_asset/webview/b_bg.png' width='100%25' /> <div style='position:absolute; left:12%; top:30%; width:80%; opacity: 0.7;font-size:20px;color:#004371;font-weight:bold;'>"+
				context.getString(R.string.no_ahfs)+"</div> </div>";
		updateWebContent(str);
	}
	private void setListener() {
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, HomePageActivity.class);
				startActivity(i);
			}
		});
//		drugSearchDo();
	}
/*	private void drugSearchDo() {		
		drugSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, InteractSearch.class);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				startActivity(intent);
				int version = Integer.valueOf(android.os.Build.VERSION.SDK);
				if (version >= 5) {
					overridePendingTransition(R.anim.animation_left_in,
							R.anim.animation_right_out);
				}
				finish();
			}
		});

	}*/
	/***
	 * 翻译ahfs的药物相互作用
	 * 
	 * @param text
	 */

	private void translateAhfs() {
		if (!pidSet.isEmpty()) {
			Iterator<String> it = pidSet.iterator();
			String id;
			String str = "";
			while (it.hasNext()) {
				id = it.next();
				// ahfs相互作用的翻译
				reqAhfsTransContent(id);
			}
		}
	}

	/**
	 * 获取AHFS翻译后数据
	 * 
	 * @param index
	 */
	private void reqAhfsTransContent(String id) {
		Properties propertyInfo = new Properties();
		propertyInfo.put(SoapRes.KEY_D_DRGID, id);
		propertyInfo.put(SoapRes.KEY_D_TYPE, "interactions");
		sendRequest(SoapRes.URLDrug, SoapRes.REQ_ID_GETGOOLE, propertyInfo,
				baseHandler);
	}

	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			// 获取药物相互作用
			if (o instanceof InteractionFindPaser) {
				InteractionFindPaser codePaser = (InteractionFindPaser) o;
				String rescode = codePaser.getRescode();
				if ("200".equals(rescode)) {
					interactionPidsEntityList = codePaser.getCoauthorList() ;
					if(	nowPage == "eachDrug"){
						getRemoteEachDrug();
	        			webView.loadUrl("javascript:show_detail()");
	        		}else if( nowPage == "allDrug"){
	        			getRemoteAllDrug();
	        			webView.loadUrl("javascript:show_list()");
	        		}
				}
			}
			// 获取翻译AHFS数据后回调
			if (o instanceof GetgoogleParser) {
				googleTransData = (GetgoogleParser) o;
				String transferStr = "";
				if (googleTransData != null && googleTransData.list.size() > 0) {
					transferStr += buildAHFSString(true);
					if (!bPreMode)
						transferStr+=getResources()
								.getString(R.string.invite_notify_image)
								+ "<input type=\"image\" id=\"button\" onclick=\"window.jscall.more()\" src=\"file:///android_asset/more.png\"/>";

					updateWebContent(transferStr);
				}
			}
		}
	}

	private void updateWebContent(String text) {
		if (text == null)
			return;
		destoryWebView();
		webView.loadDataWithBaseURL(null, getWebHeadContent() + text
				+ getWebFootContent(), mimeType, encoding, null);
		Log.i("peter", "重新load页面");
	}

	int pos = 4;
	
			
	/** google翻译后的AHFS数据 */
	private GetgoogleParser googleTransData;

	private synchronized String buildAHFSString(boolean isTranslate) {

		StringBuffer sb = new StringBuffer();
		if (ashfDatas.size()!=0) {
			for (int i = 0; i < ashfDatas.size(); i++) {
				Log.i("ashfDatas", getASHFString(ashfDatas.get(i)) + "");
				if(getASHFString(ashfDatas.get(i))==null)
					return "";
//				sb.append(getTitle(ashfDatas.get(i).generalNameEn));
//				sb.append(getSplitLine());
				// 当前为标准版，限制信息
				// 需要显示翻译信息
				if (isTranslate) {
					Log.i("simon", "当前状态为需要翻译");
					if (googleTransData != null && googleTransData.list.size() > 0) {
						Log.i("simon", "当前状态为需要翻译,并且有翻译数据，直接显示");
						if (i < googleTransData.list.size()) {
							sb.append(getHtml_P_Text("<i>"
									+ getString(R.string.translate_head)
									+ " <a href=\"http://translate.google.com\"><input type=\"image\" src=\"file:///android_asset/google.png\"/></a>,"
									+ getString(R.string.translate_foot)
									+ "</i>"));
							sb.append(googleTransData.list.get(i));
							sb.append(getHtml_P_Text("<i>"
									+ getString(R.string.translate_original)
									+ "</i>"));
						}
					}
				} else {
					Log.i("simon", "当前状态为不    需要翻译");
				}
				sb.append(getASHFString(ashfDatas.get(i)));
			}

			}
		String str = sb.toString();
		str = str.replace("<blockquote>", "");
		str = str.replace("</blockquote>", "");
		str = str.replace("<blockquote/>", "");
		Log.i("str", str);
		return str;
	}

	private String getTitle(String text) {
		return "<h3 style=\"color:#004371;text-align: left ;margin:0;padding:2px;\">"
				+ text + "</h3>";
	}

	/**
	 * 将文本转换为html中的一个段落
	 * 
	 * @param text
	 *            文本内容
	 * @return
	 */
	private String getHtml_P_Text(String text) {
		return getHtml_P_Text(text, "#004371");
	}

	/**
	 * 将文本转换为html中的一个段落
	 * 
	 * @param text
	 *            文本内容
	 * @param colorString
	 *            颜色值
	 * @return
	 */
	private String getHtml_P_Text(String text, String colorString) {
		return "<p style=\"margin:0;padding:2px;color:" + colorString + ";\">"
				+ text + "</p>";
	}

	/***
	 * 分割线
	 */
	private String splitLine;

	/**
	 * 获取一条html分割线
	 * 
	 * @return
	 */
	private String getSplitLine() {
		if (null == splitLine)
			splitLine = "<hr style=\"filter: alpha(opacity=100,finishopacity=0,style=3)\" width=\"100%\" color=#b2b5b7 size=1/>";
		return splitLine;
	}

	/**
	 * 获取当前选项对应AHFS内容
	 * 
	 * @param entity
	 *            AHFS实体
	 * @return
	 */
	private String getASHFString(AshfEntity entity) {
		switch (pos) {
		case 0:
			if (bPreMode)
				return entity.indication_pre;
			else
				return entity.indication;
		case 1:
			if (bPreMode)
				return entity.dosage_pre;
			else
				return entity.dosage;

		case 2:
			return entity.ADR;
		case 3:
			return entity.contraindication;
		case 4:
			if (bPreMode)
				return entity.interactions_pre;
			else
				return entity.interactions;
		case 5:
			if (bPreMode)
				return entity.spefipop_pre;
			else
				return entity.spefipop;

		case 6:
			if (bPreMode)
				return entity.caution_pre;
			else
				return entity.caution;
		default:
			return "";
		}
	}

	// ////
	private void navigate() {
		// 底部菜单栏
		btnLayout1 = (LinearLayout) findViewById(R.id.menu_btn_layout1);
		btnLayout2 = (LinearLayout) findViewById(R.id.menu_btn_layout2);
		btnLayout3 = (LinearLayout) findViewById(R.id.menu_btn_layout3);
		btnToggleLayout3 = (LinearLayout) findViewById(R.id.menu_tgbtn_layout3);
		btnLayout4 = (LinearLayout) findViewById(R.id.menu_btn_layout4);
		/***
		 * 底部菜单栏按钮
		 */
		btn1 = (Button) findViewById(R.id.btn_1st);
		btn1.setOnClickListener(this);

		btn2 = (ToggleButton) findViewById(R.id.tgbtn_2nd);
		btn2.setOnClickListener(this);

		btn3 = (Button) findViewById(R.id.btn_3rd);
		btn3.setOnClickListener(this);

		btn3toggle = (ToggleButton) findViewById(R.id.tgbtn_3rd);
		btn3toggle.setOnClickListener(this);

		btn4 = (Button) findViewById(R.id.btn_share);
		btn4.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		/** 意见反馈或查看网页版厂商说明书 */
		case R.id.btn_1st:
			pressView(btnLayout1);
			MobclickAgent.onEvent(getApplicationContext(),"interaction_all_drug_feedback");
			uploadLoginLogNew("Interaction","interaction_all_drug_feedback", "interaction_all_drug_feedback_click", null);
			// 意见反馈
			UMFeedbackService.openUmengFeedbackSDK(context);
			break;
		/** 字体 */
		case R.id.tgbtn_2nd:
			pressView(btnLayout2);
			MobclickAgent.onEvent(getApplicationContext(),"interaction_all_drug_size");
			uploadLoginLogNew("Interaction","interaction_all_drug_size", "interaction_all_drug_size_click", null);
			if (sizes.isEmpty()) {
				sizes.add(125);
				sizes.add(150);
				sizes.add(100);
			}
			myHandler.post(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:doZoom('"
							+ sizes.get(getSize()) + "')");
				}
			});
			break;
		/** 翻译 */
		case R.id.tgbtn_3rd:
			pressView(btnToggleLayout3);
			MobclickAgent.onEvent(getApplicationContext(),"interaction_all_drug_translate");
			uploadLoginLogNew("Interaction","interaction_all_drug_translate", "interaction_all_drug_translate_click", null);

			if (webHasCleared) {
				if(!TextUtils.isEmpty(ahfsInteractionStr)){
					if(ahfsCultureInfo == "zh-CN"){
						ahfsCultureInfo = "en-US";
						updateWebContent(ahfsInteractionStr);
					}else if(ahfsCultureInfo == "en-US"){
						ahfsCultureInfo = "zh-CN";
						translateAhfs();
					}				
				}else{
					showEmptyView();
				}
			}else{
				if(cultureInfo == "zh-CN"){
					cultureInfo = "en-US";
				}else if(cultureInfo == "en-US"){
					cultureInfo = "zh-CN";
				}
				translate(cultureInfo);
			}
			break;

		/** 分享 */
		case R.id.btn_share:
			pressView(btnLayout4);
			MobclickAgent.onEvent(getApplicationContext(),"interaction_all_drug_share");
			uploadLoginLogNew("Interaction","interaction_all_drug_share", "interaction_all_drug_share_click", null);
			newSharing(R.array.items2, 0,"interaction_all_drug");
			break;

		/** 美国药师协会 使用提示 */
		case R.id.txt_link:
			Intent disclamer = new Intent(context, AHFSDisclaimerActivity.class);
			startActivity(disclamer);
			break;
		}
	}

	private void translate(String cultureInfo) {
		if(isnetwork){
			Properties propertyInfo = new Properties();
			String pids = pidList.toString().replace("[", "").replace("]", "");
			propertyInfo.put(SoapRes.CULTUREINFO, cultureInfo);
			propertyInfo.put(SoapRes.PIDS, pids);
			sendRequest(SoapRes.URLINTERACTION,
					SoapRes.REQ_ID_GET_INTERACTIONFIND, propertyInfo,baseHandler);
		}
	}

	/**
	 * 点击按钮变换背景色
	 * 
	 * @param viewGroup
	 */
	private void pressView(final ViewGroup viewGroup) {
		viewGroup.setBackgroundResource(R.drawable.bg_press);
		viewGroup.postDelayed(new Runnable() {
			@Override
			public void run() {
				viewGroup.setBackgroundResource(R.drawable.bg_normal);
			}
		}, 200);
	}

	int indicaterSize = -1;

	public int getSize() {

		if (indicaterSize == sizes.size() - 1) {
			indicaterSize = -1;
		}
		Logs.i("indicaterSize" + indicaterSize);
		return ++indicaterSize;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Logs.i("==aa" + webView.canGoBack());
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (webView.canGoBack()) {
				webHasCleared = false;
				// 返回键退回
				webView.goBack();
			} else {
				InteractDetail.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** Html标签头 */
	private String webHeadContent;
	/** Html标签尾 */
	private String webFootContent;

	/**
	 * 获取Html头部信息
	 */
	private String getWebHeadContent() {
		if (null == webHeadContent) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			buffer.append("<head>");
			buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			buffer.append("<script type=\"text/javascript\"> ");
			// 重排字体
			buffer.append("function doZoom(size){ ");
			buffer.append("document.getElementById('bodyid').style.fontSize=size+'%';");
			buffer.append("}");
			
			// 更新WebView内容function
			buffer.append("function updateContent(content){ ");
			buffer.append("  document.getElementById(\"content\").innerHTML = content; ");
			buffer.append("}");
			// 页面加载完成后,默认滚动到顶部
			buffer.append("function init(){ ");
			buffer.append("  window.scrollBy(0, 0); ");
			buffer.append("}");
			buffer.append("</script>");
			buffer.append("<title>无标题文档</title>");
			buffer.append("</head>");
			buffer.append("<body id='bodyid' onload=\"init();\">");
			buffer.append("<div id=\"content\">");
			webHeadContent = buffer.toString();
		}
		return webHeadContent;
	}

	/***
	 * 销毁webview
	 */
	private void destoryWebView() {
		if (webView != null)
			webHasCleared = true;
		webView.clearView();
	}

	/**
	 * 获取Html底部信息
	 */
	private String getWebFootContent() {
		if (null == webFootContent) {
			webFootContent = "</div></body></html>";
		}
		return webFootContent;
	}

}

