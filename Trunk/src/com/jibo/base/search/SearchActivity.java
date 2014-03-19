/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.base.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;
import com.jibo.app.invite.Constants;
import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.ComparatorRepo;
import com.jibo.util.Logs;
import com.jibo.util.SharedPreferenceUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author chenliang
 */

public class SearchActivity extends BaseActivity {
	public List<EntityObj> result;
	public List<EntityObj> resultCache;
	private static final int TOSEND = 1;
	private static final int NAMEPUT = 2;
	public ViewHolder viewHolder;
	public ListView listview;
	public GridView gridview;
	public View list;
	protected AdaptInfo adaptInfo;
	public SearchAdapter adapter;
	public MapAdapter invitedAdapter;
	private AdaptInfo invitedAdaptInfo;
	int loadedNum = 0;
	EntityObj EntityObj;
	List<EntityObj> batchTmp;
	public int inviteSuccess = 111;
	public String username;
	List<EntityObj> selected = new ArrayList<EntityObj>(0);
	public ProgressDialog pd;

	AdapterView<?> parent;
	int position;
	public static String name;
	public static String retainName;

	public ViewHolder getViewHolder() {
		if (viewHolder == null) {
			viewHolder = newViewHolderInstance();
		}
		return viewHolder;
	}

	public AdaptInfo getAdaptInfo() {
		// TODO Auto-generated method stub
		if (adaptInfo == null) {
			adaptInfo = new AdaptInfo();
			adaptInfo.objectFields = new String[] { "name", };
			adaptInfo.listviewItemData = new AdapterSrc();
			adaptInfo.viewIds = new int[] { R.id.name, R.id.email, R.id.phone, };
			adaptInfo.listviewItemLayoutId = R.layout.invite_list_item;
		}

		return adaptInfo;
	}

	public void buildAdapter(Class<? extends MapAdapter> adpaterClass,
			AdaptInfo adaptInfo) {
		constructAdapter(adpaterClass, adaptInfo);
		// adapter.setmThumbnailLoader(new ThumbnailLoader(handler,
		// getContext(), ThumbnailLoader.MODE_LAZY, null));
		listview = (ListView) this.findViewById(R.id.lst_item);
		listview.setCacheColorHint(Color.TRANSPARENT);
	}



	public void constructAdapter(Class<? extends MapAdapter> adapterClazz,
			AdaptInfo adaptInfo) {
		try {
			adapter = ((SearchAdapter)((HeaderViewListAdapter)((ListView)findViewById(R.id.lst_item)).getAdapter()).getWrappedAdapter());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	

	public static void updateName(String paramName) {
		name = paramName != null ? paramName : SharedPreferencesMgr.getName();
	}

	

	public ViewHolder newViewHolderInstance() {
		return new SearchListViewHolder(this.getWindow().getDecorView(), this);
	}

	public static String staticKeyWords = "";
	public static boolean markfromPathActivity = false;
	boolean isNew;
	public String objectWords = "";

	List<QueryThread> queue = new ArrayList<QueryThread>(0);
	private Handler searchHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.OperationContants.SEARCHNOTIFY:
				Logs.i("rebind --");
				viewHolder.rebind();
				break;

			case Constants.OperationContants.SEARCH_NOTIFY_EMPTY:
				// if (adapter != null) {
				// adapter.getItemDataSrc().setContent(Data.info);
				// adapter.notifyDataSetChanged();
				// listview.invalidate();
				// }
				break;
			}
		}
	};
	public Map<String, Class> datamap;
	public SearchActivity context;

	QueryThread quertThread;
	QueryThread currThread;


	class QueryThread extends Thread {

		public boolean runned;

		public QueryThread(String inputkeyWords) {
			this.inputkeyWords = inputkeyWords;
		}

		public String getInputkeyWords() {
			return inputkeyWords;
		}

		public boolean isRunned() {
			return runned;
		}

		String inputkeyWords;

		@Override
		public void run() {
			try {
				Logs.i("inputkeyWords --" + inputkeyWords);
				// if (inputkeyWords.trim().equals("")) {
				// searchHandler
				// .sendEmptyMessage(Constants.OperationContants.SEARCH_NOTIFY_EMPTY);
				// } else {
				Logs.i("dataChanged -- b");
				viewHolder.dataChanged();
				Logs.i("dataChanged -- a");
				searchHandler
						.sendEmptyMessage(Constants.OperationContants.SEARCHNOTIFY);
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
			runned = true;
		}
	};

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		MobclickAgent.onError(this);
//		this.setContentView(R.layout.invitation);
		context = this;
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//		buildInvitedAdapter();
		username = (String) SharedPreferenceUtil.getValue(
				getApplicationContext(), "GBAPP", "USER_NAME", String.class);
	}

	public void baseKeywordSearch(String inputkeyWords) {
		queue.add(new QueryThread(inputkeyWords));
		QueryThread qthread = queue.get(queue.size() - 1);
		if (currThread != qthread) {
			if (currThread != null) {
				currThread.interrupt();
			}
			if (!qthread.isRunned()) {
				currThread = qthread;
				qthread.start();
			}
		}
	}

	static Set<String> names = new TreeSet<String>(ComparatorRepo.stringKey);

	protected void initSearch() {
		if (listview != null && listview.getCount() > 0) {
			return;
		}

		if(this.findViewById(R.id.searcheditlayout).getVisibility()==View.GONE){
			return;
		}
//		EditText text = ((EditText) this.findViewById(R.id.searchBox));
		viewHolder = getViewHolder();

		viewHolder.start();
		if (markfromPathActivity) {
			markfromPathActivity = false;
			objectWords = staticKeyWords;
		}
//		
//		if (!(text.getText().toString().trim().equals("") && objectWords.trim()
//				.equals(""))) {
//			
//			
//		}
		EditText searchBox = (EditText) this.findViewById(R.id.searchBox);
		searchBox.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable editable) {
				staticKeyWords = objectWords = editable.toString();
				// if(objectWords.trim().equals("")){
				// editable.append(context.getString(R.string.aboutus));
				//
				// }else{
				// editable.clear();
				// }
				baseKeywordSearch(objectWords);

			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			public void onTextChanged(CharSequence str, int start, int len,
					int arg3) {
				Logs.i("---------onTextChanged" + str + " arg1 " + start + " "
						+ len + " " + arg3);

			}
		});
	}

	boolean isShowInput = true;


	boolean invited = false;
	public void fetchInvitd(String username) {
		Properties pro = new Properties();
		pro.put(SoapRes.KEY_INVITE_GBUSERNAME, username);
		sendRequest(SoapRes.URLGETInvite, SoapRes.REQ_ID_GET_INVITE, pro,
				new BaseResponseHandler(this, false));

	}

	int countInvited;
	boolean hasInvited = false;

	public static final String FromSearchActivity = "fromSearchActivity";

	public List<EntityObj> getSourceContacts(String string,
			SearchActivity activity) {
		try{
		// TODO Auto-generated method stub
		return ((List<EntityObj>)((MapAdapter)((HeaderViewListAdapter)((ListView)activity.findViewById(R.id.lst_item)).getAdapter()).getWrappedAdapter()).getItemDataSrc().getContent());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
		}

	public List<EntityObj> getKeyContacts(List<EntityObj> result2, String objectWords2) {
		// TODO Auto-generated method stub
		List<EntityObj> entityObjs = new ArrayList<EntityObj>(); 
		for(EntityObj obj:result2){
			if(obj.get("name")==null||obj.get("name").toString().equals("")||obj.get("name").toString().toLowerCase().contains(objectWords2)){
				entityObjs.add(new EntityObj(obj));
			}
		}
		return entityObjs;
	}

}
