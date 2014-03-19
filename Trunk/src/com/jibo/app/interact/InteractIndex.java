package com.jibo.app.interact;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;
import com.jibo.activity.HomePageActivity;
import com.jibo.common.ComparatorDrugList;
import com.jibo.common.ComparatorList;
import com.jibo.common.Constant;
import com.jibo.common.DeviceInfo;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.InteractionDrugProductPaser;
import com.jibo.data.InteractionFindPaser;
import com.jibo.data.InteractionPaser;
import com.jibo.data.InteractionRelationshipPaser;
import com.jibo.data.entity.DrugInteractEntity;
import com.jibo.data.entity.InteractionEntity;
import com.jibo.data.entity.InteractionPidsEntity;
import com.jibo.data.entity.InteractionProductEntity;
import com.jibo.data.entity.InteractionRelationshipEntity;
import com.jibo.data.entity.SearchHistoryEntity;
import com.jibo.dbhelper.InteractAdapter;
import com.jibo.dbhelper.SearchHistoryDao;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;
import com.jibo.util.ThreadHandler;
import com.jibo.util.tips.TipHelper;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InteractIndex extends BaseActivity{

	private AutoCompleteTextView drugSearch;
	private ListView interat_list;
	private Button clearBtn;
	private Context context;
	private final int FLING_CLICK = 0;
	private final int FLING_LEFT = 1;
	private final int FLING_RIGHT = 2;
	private int flingState = FLING_CLICK;
	private InteractAdapter dao;
	private TextView searchResultSum;
	private String drugId="";
	private String drugName="";
	List<Map<String, String>> selectedDrugList= new ArrayList<Map<String, String>>();//药品列表
	Map<String,String> map;//装单个药品
	Map<String,Object> map1;//装单个药品
	private Set<Map<String, String>> interactPIDList;
	Set<String> pidSet = new HashSet<String>();
	public String rememberedNum;
	private BaseAdapter adapter;
	private LinearLayout searchResultLayout;
	private TextView interat_drug;
	private TextView interat_drug_title;
	private TextView interat_drug_num;
	private ImageView arrow_img;
	private String localLanguage;
	private TextView indication_notice_text;
	private int totalDrugInteractoinNum;
	public BaseResponseHandler baseHandler = new BaseResponseHandler(this, false);// event一般模式下获取列表数据的Handler
	public DeviceInfo isnetwork;
	public List<InteractionPidsEntity> interactionPidsEntityList;
	private SearchHistoryDao searchHistoryDao;
	private ImageView searchResultArrow;
	private InteractUpdate interactUpdate;
	private ImageButton home;
	private LinearLayout totalInteractlayout;
	public InteractIndex() {
		super();
	}

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.interat_declare_layout);		
		init();
	}

	private void init() {
		context = this;
		interactUpdate = new InteractUpdate(InteractIndex.this,context);
		isnetwork = new DeviceInfo(context);
		searchHistoryDao = new SearchHistoryDao(context);		
		dao = new InteractAdapter(context,Integer.parseInt(Constant.dbVersion));
		localLanguage = Locale.getDefault().getLanguage();
		if("en".equals(localLanguage)){
			localLanguage = "en-US";
		}else if("zh".equals(localLanguage)){
			localLanguage = "zh-CN";
		}
		//获取上一页面传递过来的数据
		Bundle bundle=getIntent().getExtras(); 
		if(bundle != null){
			if(localLanguage.equals("zh-CN")){
				drugName =bundle.getString("drugNameCn");
			}else{
				drugName =bundle.getString("drugNameEn");
			}
			insertListFromName(drugName, "");
			storeHistory(drugName);
			drugId = getDrugIdByName(drugName);
			pidSet.add(drugId);
		}
		List<String> drugNameList= searchHistoryDao.selectSearchHistory(context, "interaction","1");
		Iterator<String> it = drugNameList.iterator();
		while(it.hasNext()){
			String drugName = it.next();
			if(drugName.contains(context.getString(R.string.str_split))){
				drugName = drugName.split(context.getString(R.string.str_split))[0];
			}
			drugId = getDrugIdByName(drugName);
			pidSet.add(drugId);
			insertListFromName(drugName,drugId);
		}
		if(selectedDrugList.size()>1){
			doRequest();
		}
		
		findViewById();	
		setListener();
		loadList();
		checkUpdate();
	}

	private void checkUpdate() {
		//wifi环境下进行更新数据库
		if(isnetwork.isWifi()){
			new Thread(){
				public void run(){
					// 检查Interaction数据库是否有更新				
						interactUpdate.checkDrugInteraction();
				}
			}.start();
		}
	}
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		switch (requestCode) {
		case 1:
			if(data!=null){
				Bundle bundle = data.getExtras();
				if(bundle!=null) {
					drugId=bundle.getString("drugId");
					drugName = bundle.getString("drugName");
					storeHistory(drugName);
					if(pidSet.size()<=15){
						insertListFromName(drugName, drugId);
						pidSet.add(drugId);
						doRequest();
					}else{
						Toast.makeText(context, R.string.over_drug, 1).show();
					}
				}
			}
			break;
		}
	}

	private void doRequest() {
		//网络正常情况下，访问网络来判断是否存在相互作用
//		if(isnetwork.isNetWorkEnable()){
		if(false){
			if(pidSet.size()>1){
				Properties propertyInfo = new Properties();
				String pids = pidSet.toString().replace("[", "").replace("]", "");
				String cultureInfo = "en-US";
				propertyInfo.put(SoapRes.CULTUREINFO, cultureInfo);
				propertyInfo.put(SoapRes.PIDS, pids);
				sendRequest(SoapRes.URLINTERACTION,
						SoapRes.REQ_ID_GET_INTERACTIONFIND, propertyInfo,baseHandler);
			}else{
				notifyDataChange();
			}
		}else{
			backProcess();
		}
	}
	public void onReqResponse(Object o, int methodId) {
		if (o instanceof InteractionFindPaser) {
			InteractionFindPaser codePaser = (InteractionFindPaser) o;
			String rescode = codePaser.getRescode();
			interactionPidsEntityList = codePaser.getCoauthorList() ;
			totalDrugInteractoinNum = interactionPidsEntityList.size();
			if ("200".equals(rescode)) {
				doFindPids();				
			}else{
				clearDrugNum();
			}
			notifyDataChange();		
		}else{
			interactUpdate.update(o);
		}
	}

	private void doFindPids() {
		if(interactionPidsEntityList.size()>0){
			new ThreadHandler() {
				@Override
				public void execute() {
					clearDrugNum();
					//说明有相互作用
					Iterator it = interactionPidsEntityList.iterator();
					while(it.hasNext()){
						InteractionPidsEntity interactionPidsEntity = (InteractionPidsEntity) it.next();
						String pid1 = interactionPidsEntity.getPid1();
						String pid2 = interactionPidsEntity.getPid2();
						Iterator it1 = selectedDrugList.iterator();
						int a = 0;
						while(it1.hasNext()){
							Map m1 = (Map) it1.next();
							String drugId = m1.get("drugId")+"";								
							Object ob = m1.get("interatDrugNum");
							int interatDrugNum = Integer.parseInt(ob.toString());
							if(drugId.equals(pid1)||drugId.equals(pid2)){
								interatDrugNum ++;
								selectedDrugList.get(a).put("interatDrugNum", interatDrugNum+"");
							}										
							a++;
						}
					}
				}
				
				@Override
				public void postexecute() {	
				}
			};
		}		
	}
	private void clearDrugNum() {
		//清空interatDrugNum
		int b=0;
		Iterator it2 = selectedDrugList.iterator();
		while(it2.hasNext()){
			Map m1 = (Map) it2.next();
			String drugId = m1.get("drugId")+"";								
			Object ob = m1.get("interatDrugNum");
			selectedDrugList.get(b).put("interatDrugNum", "0");
			b++;
		}
	}
	
	private void loadList() {
		adapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
//				View view = convertView == null ? View.inflate(getApplicationContext(), R.layout.interat_item_layout, null) : convertView;
				View view = convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.interat_item_layout, null);
				interat_drug = (TextView) view.findViewById( R.id.interat_drug);
				interat_drug_num = (TextView) view
						.findViewById(R.id.interat_drug_num);
				arrow_img = (ImageView)view.findViewById(R.id.arrow_img);
				String drugName = selectedDrugList.get(position).get("drugName");
				if(drugName.length()>20){
					drugName = drugName.substring(0, 17)+"...";
				}
				interat_drug.setText(drugName);
				String interatDrugNum = selectedDrugList.get(position).get("interatDrugNum");
				
				if(interatDrugNum != null && interatDrugNum!="0"){
					interat_drug_num.setBackgroundResource(R.drawable.c03);
					interat_drug_num.setText(interatDrugNum);
					arrow_img.setVisibility(View.VISIBLE);
					arrow_img.setImageResource(R.drawable.arrow05);
				}else{
					interat_drug_num.setBackgroundResource(R.drawable.c01);
					interat_drug_num.setText("--");
					//arrow_img.setImageResource(R.drawable.arrow04);
					arrow_img.setVisibility(View.INVISIBLE);
				}
				return view;
			}
			
			public long getItemId(int position) {
				return position;
			}

			public Object getItem(int position) {
				
				return selectedDrugList.get(position);
				
			}

			public int getCount() {
				return selectedDrugList.size();
			}
		};
		interat_list.setAdapter(adapter);
		
	}

	private void backProcess() {
		new ThreadHandler() {
			Map<String,Map<String,String>> interactDrugList;
			@Override
			public void execute() {
				int b=0;
				Iterator it2 = selectedDrugList.iterator();
				while(it2.hasNext()){
					Map m1 = (Map) it2.next();
					String drugId = m1.get("drugId")+"";								
					Object ob = m1.get("interatDrugNum");
					selectedDrugList.get(b).put("interatDrugNum", "0");
					b++;
				}
				if(selectedDrugList.size()>1){
						//获取所有pid1,pid2,iid
					interactPIDList = dao.selectInteractionsRelationship(pidSet);
					totalDrugInteractoinNum = interactPIDList.size();					
					if(totalDrugInteractoinNum>0){
						Iterator it = interactPIDList.iterator();
						while(it.hasNext()){
							Map ma = (Map)it.next();
							String pid1 = ma.get("pid1")+"";
							String pid2 = ma.get("pid2")+"";
							Iterator it1 = selectedDrugList.iterator();
							int a = 0;
							while(it1.hasNext()){
								Map m1 = (Map) it1.next();
								String drugId = m1.get("drugId")+"";								
								Object ob = m1.get("interatDrugNum");
								int interatDrugNum = Integer.parseInt(ob.toString());
								if(drugId.equals(pid1)||drugId.equals(pid2)){
									interatDrugNum ++;
									selectedDrugList.get(a).put("interatDrugNum", interatDrugNum+"");
								}										
								a++;
							}
						}
					}
				}

			}
			@Override
			public void postexecute() {
				notifyDataChange();
			}
			
		};
	}
	private void notifyDataChange() {
		if(selectedDrugList.size()<3){
			showNotice();
		}
		if(totalDrugInteractoinNum>0){
			interat_drug_title.setText(R.string.yes_interations);
			totalInteractlayout.setVisibility(View.VISIBLE);
			searchResultSum.setVisibility(View.VISIBLE);
			searchResultArrow.setVisibility(View.VISIBLE);
			searchResultSum.setText(totalDrugInteractoinNum+"");
		}else{
			interat_drug_title.setText(R.string.no_interations);
			totalInteractlayout.setVisibility(View.VISIBLE);
			searchResultSum.setVisibility(View.INVISIBLE);
			searchResultArrow.setVisibility(View.INVISIBLE);
		}
		adapter.notifyDataSetChanged();
	}
	private void setListener() {
		interat_list.setOnItemClickListener(new InteratItemClickListener());
		interat_list.setOnItemLongClickListener(new InteratItemLongClickListener());
		//interat_list.setOnTouchListener(new InteractItemTouchListener());
		home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, HomePageActivity.class);
				startActivity(i);
			}
		});
		clearBtnDo();
		drugSearchDo();
		searchResultLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//正式版需要的
				if(totalDrugInteractoinNum>0){
					MobclickAgent.onEvent(getApplicationContext(),"interaction_all_drug");
					uploadLoginLogNew("Interaction","interaction_all_drug", "interaction_all_drug_click", null);
					Intent intent = new Intent(context,InteractDetail.class);
					Bundle bundle = new Bundle();
					bundle.putString("style", "all");
					bundle.putSerializable("pidSet", (Serializable) pidSet);
//					if(!isnetwork.isNetWorkEnable()){
					if(true){
						bundle.putString("zone", "local");
						bundle.putSerializable("interactPIDList", (Serializable) interactPIDList);
					}else{
						bundle.putString("zone", "remote");
						bundle.putSerializable("interactionPidsEntityList", (Serializable) interactionPidsEntityList);
					}
					intent.putExtras(bundle);
					startActivityForResult(intent, 2);
				}else{
					
				}
			}
		});
	}

	private void findViewById() {
		home = (ImageButton) findViewById(R.id.imgbtn_home);
		clearBtn = (Button) findViewById(R.id.imgbtn_clear);
		totalInteractlayout = (LinearLayout)findViewById(R.id.totalInteractlayout);
		interat_drug_title = (TextView)findViewById(R.id.interat_drug);
		searchResultSum = (TextView)findViewById(R.id.searchResultSum);
		searchResultArrow = (ImageView)findViewById(R.id.searchResultArrow);
		interat_list = (ListView) findViewById(R.id.interat_list_view);
		drugSearch = (AutoCompleteTextView) findViewById(R.id.search_edit);
		drugSearch.setFocusable(false);
		searchResultLayout = (LinearLayout) findViewById(R.id.searchResultLayout);
		indication_notice_text = (TextView) findViewById(R.id.notice).findViewById(R.id.indication_notice_text);
		showNotice();
	}
	public void clearHistoryDrug(String title){
		SearchHistoryEntity searchHistoryEntity = new SearchHistoryEntity();
		searchHistoryEntity.setIsLatest("0");
		searchHistoryEntity.setCategory("interaction");
		searchHistoryEntity.setTitle(title);
		searchHistoryDao.updateSearchHistory(context, searchHistoryEntity);
	}
	private void showNotice() {
		if(!"true".equals(SharedPreferencesMgr.getInteractionTips())){
			int size = selectedDrugList.size();
			TipHelper.addTip(R.id.mask, this);
			TipHelper.addTip(R.id.notice, this);
			if(size == 1){
				indication_notice_text.setText(R.string.interactions_notice_second);
				SharedPreferencesMgr.setInteractionTips("true");
			}else if(size == 2){
				indication_notice_text.setText(R.string.interactions_notice_third);
			}
		}else{
			TipHelper.disableTipViewOnScreenVisibility();
			findViewById(R.id.mask).setVisibility(View.GONE);
			findViewById(R.id.notice).setVisibility(View.GONE);
		}
	}

	private void clearBtnDo() {
		clearBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog show = new AlertDialog.Builder(context)
						.setMessage(context.getString(R.string.clear_all_drugs))
						.setPositiveButton(context.getString(R.string.clear),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										clearHistoryDrug("");
										selectedDrugList.clear();
										pidSet.clear();
										totalInteractlayout.setVisibility(View.INVISIBLE);
										totalDrugInteractoinNum=0;
										MobclickAgent.onEvent(getApplicationContext(),"interaction_btn_clear");
										uploadLoginLogNew("Interaction","interaction_btn_clear", "clickClear", null);
										notifyDataChange();
									}
								})
						.setNegativeButton(context.getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								}).show();
			}
		});

	}

	private void drugSearchDo() {		
		drugSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, InteractSearch.class);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
				int version = Integer.valueOf(android.os.Build.VERSION.SDK);
				if (version >= 5) {
					overridePendingTransition(R.anim.animation_right_in,
							R.anim.animation_left_out);
				}
			}
		});

	}
	

	private void insertListFromName(String drugName,String dId) {
		if(TextUtils.isEmpty(drugName)){
			return;
		}
		if(drugName.contains(context.getString(R.string.str_split))||TextUtils.isEmpty(dId)){
			drugName = drugName.split(context.getString(R.string.str_split))[0];
			drugId = getDrugIdByName(drugName);
			dId = drugId;
		}
		boolean i = false; //是否有重复的
		map = new HashMap<String, String>();
		if(selectedDrugList.size()>0){
			Iterator it = selectedDrugList.iterator();
			while(it.hasNext()){
				Map m1 = (Map)it.next();
				String dId1 = (String) m1.get("drugId");
				if(dId.equals(dId1)){
					i=true;
				}
			}
		}
		if(!i&&!TextUtils.isEmpty(dId)){
			map.put("drugName", drugName);
			map.put("drugId", dId);
		}
		if(!map.isEmpty()){
			selectedDrugList.add(map);
		}

		ComparatorDrugList comparator = new ComparatorDrugList();
		Collections.sort(selectedDrugList, comparator);
	}
	public void storeHistory(String title){
		SearchHistoryEntity searchHistoryEntity = new SearchHistoryEntity();
		searchHistoryEntity.category=Constant.interation_history;
		if(title.contains(context.getString(R.string.str_split))){
			title = title.split(context.getString(R.string.str_split))[0];
		}
		searchHistoryEntity.title = title;
		Date currentTime = new Date();
	    SimpleDateFormat formatter = new SimpleDateFormat("MM:dd");
		searchHistoryEntity.time = formatter.format(currentTime);
		searchHistoryEntity.isLatest = "1";
		boolean had = searchHistoryDao.selectHistoryTitle(context, searchHistoryEntity);
		if(had){
			searchHistoryDao.updateSearchHistory(context, searchHistoryEntity);
		}else{
			searchHistoryDao.insertSearchHistory(context, searchHistoryEntity);
		}
	}
	private String getDrugIdByName(String drugName) {
		Map<String,String> m = new HashMap<String, String>();
		m.put("Name", drugName);
		List<DrugInteractEntity> re = dao.selectDrugNameAndPID(m);
		if(re.size()==0){
			return "";
		}
		return re.get(0).drugId;
	}
	
	private class InteractItemTouchListener implements OnTouchListener{

		 float x, y, upx, upy;  
         public boolean onTouch(View view, MotionEvent event) {  
             if (event.getAction() == MotionEvent.ACTION_DOWN) {  
                 x = event.getX();  
                 y = event.getY();  
             }  
             if (event.getAction() == MotionEvent.ACTION_UP) {  
                 upx = event.getX();  
                 upy = event.getY();  
                 int position1 = ((ListView) view).pointToPosition((int) x, (int) y);  
                 int position2 = ((ListView) view).pointToPosition((int) upx,(int) upy);  
                 if(position1 == position2){
                	 if(Math.abs(x - upx) > 10){
                		 if (x > upx) {
              				Logs.i("MY_TAG", "Fling Left");
              				flingState = FLING_LEFT;
              			} else {
              				Logs.i("MY_TAG", "Fling Right");
              				flingState = FLING_RIGHT;
              			}
                	 }else{
                		 	Logs.i("MY_TAG", "Fling click");
             				flingState = FLING_CLICK;
                	 }
                 }
             }  
             return false;  
         }  
	}
	private class InteratItemLongClickListener implements OnItemLongClickListener {

		private TextView numView;
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, final View view, final int pos,
				long id) {
			numView = (TextView) view.findViewById(R.id.interat_drug_num);
			AlertDialog show = new AlertDialog.Builder(context)
			.setMessage(context.getString(R.string.clear_drug))
			.setPositiveButton(context.getString(R.string.delete),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
								removeListItem(view,pos);
						}
					})
			.setNegativeButton(context.getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					}).show();
			return true;
		}
		
	}
	private class InteratItemClickListener implements OnItemClickListener {

		private TextView numView;
		private View delView;
		private boolean delShow = false;

		@Override
		public void onItemClick(AdapterView<?> parent, final View view, final int pos,
				long id) {
			numView = (TextView) view.findViewById(R.id.interat_drug_num);
			delView = view.findViewById(R.id.interat_drug_del);
	        Log.v("MY_TAG", "onItemClick: state="+flingState+", pos="+pos);
			//delShow表示del图标是否显示
//			switch (flingState) {
//			// 处理左滑事件
//			case FLING_LEFT:
//				if(delShow){
//					delShow = false;
//				}else{
//					delShow = true;
//				}
//				Logs.i("shengxiaozuo"+delShow);
//				flingState = FLING_CLICK;
//				break;
//			// 处理右滑事件
//			case FLING_RIGHT:
//				if(delShow){
//					delShow = false;
//				}else{
//					delShow = true;
//				}
//				Logs.i("youhua"+delShow);
//				flingState = FLING_CLICK;
//				break;
//			// 处理点击事件
//			case FLING_CLICK:
//				if(delShow){
//					delShow = false;
//				}else{
					//正式版的时候需要的
					
					if(selectedDrugList.get(pos).get("interatDrugNum") != null && 
							selectedDrugList.get(pos).get("interatDrugNum") != "0"){
						Intent intent = new Intent(context,InteractDetail.class);
						Bundle bundle = new Bundle();	
						bundle.putSerializable("pidSet", (Serializable) pidSet);
						if(true){
//						if(!isnetwork.isNetWorkEnable()){
							bundle.putSerializable("interactPIDList", (Serializable) interactPIDList);
							bundle.putString("zone", "local");							
						}else{
							bundle.putString("zone", "remote");
							bundle.putSerializable("interactionPidsEntityList", (Serializable) interactionPidsEntityList);
						}
						bundle.putString("style", "each");
						bundle.putString("mainDrug",selectedDrugList.get(pos).get("drugName"));
						bundle.putString("mainDrugId",selectedDrugList.get(pos).get("drugId"));
						intent.putExtras(bundle);
						startActivityForResult(intent, 2);
					}
//				}
//				break;
//			}
//			if(delShow){
//				numView.setBackgroundResource(R.drawable.c04);
//				rememberedNum = numView.getText().toString();
//				numView.setTag("true");
//				numView.setText("");
//				numView.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						removeListItem(view,pos);
//					}
//				});
//			}else{
//				TextView itemText = ((TextView)interat_list.findViewWithTag("true"));
//				if(itemText!=null){
//					if("--".equals(rememberedNum)){
//						itemText.setBackgroundResource(R.drawable.c01);
//					}else{
//						itemText.setBackgroundResource(R.drawable.c03);
//					}
//					itemText.setText(rememberedNum);
//					itemText.setTag("false");
//				}
//			}
				
		}

	}		
	protected void removeListItem(View rowView, final int positon) {
		TranslateAnimation animation = new TranslateAnimation(0,1000,0,0);
		animation.setDuration(1000);
        animation.setAnimationListener(new AnimationListener() { 
            public void onAnimationStart(Animation animation) {} 
            public void onAnimationRepeat(Animation animation) {} 
            public void onAnimationEnd(Animation animation) { 
            	clearHistoryDrug(selectedDrugList.get(positon).get("drugName"));
            	pidSet.remove(selectedDrugList.get(positon).get("drugId"));
                selectedDrugList.remove(positon);
                if(selectedDrugList.size()>1){
        			doRequest();
        		}
                notifyDataChange();
                animation.cancel(); 
            } 
        }); 
        rowView.startAnimation(animation); 
    }

}

