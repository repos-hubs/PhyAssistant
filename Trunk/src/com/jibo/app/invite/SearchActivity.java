/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jibo.app.invite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseActivity;
import com.jibo.activity.Registration_updateActivity;
import com.jibo.activity.UpdateInviteCodeActivity;
import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.adapter.MapAdapter;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.GetFriendsPaser;
import com.jibo.data.InviteFriendsPaser;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.ComparatorRepo;
import com.jibo.util.Logs;
import com.jibo.util.SharedPreferenceUtil;
import com.jibo.util.tips.CancelClicker;

/**
 * 
 * @author chenliang
 */

public class SearchActivity extends BaseActivity {

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
	ContactInfo contactInfo;
	List<ContactInfo> batchTmp;
	public int inviteSuccess = 111;
	public String username;
	List<ContactInfo> selected = new ArrayList<ContactInfo>(0);
	public ProgressDialog pd;
	OnItemClickListener onItemClickListener = new ListItemClickListener();
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

	public AdaptInfo getInvitedAdaptInfo() {
		if (invitedAdaptInfo == null) {
			invitedAdaptInfo = new AdaptInfo();
			invitedAdaptInfo.objectFields = new String[] { "name"
			// , "email",
			// "phone"
			};
			invitedAdaptInfo.listviewItemData = new AdapterSrc();
			invitedAdaptInfo.viewIds = new int[] { R.id.name
			// , R.id.email,
			// R.id.phone
			};
			invitedAdaptInfo.listviewItemLayoutId = R.layout.invitedlist;
			// adaptInfo.actionListeners = getViewHandlers();
		}
		return invitedAdaptInfo;
	}

	public AdaptInfo getAdaptInfo() {
		// TODO Auto-generated method stub
		if (adaptInfo == null) {
			adaptInfo = new AdaptInfo();
			adaptInfo.objectFields = new String[] { "name", "email", "phone" };
			adaptInfo.listviewItemData = new AdapterSrc();
			adaptInfo.viewIds = new int[] { R.id.name, R.id.email, R.id.phone, };
			adaptInfo.listviewItemLayoutId = R.layout.invite_list_item;
			// adaptInfo.actionListeners = getViewHandlers();
		}

		return adaptInfo;
	}

	public void buildAdapter(Class<? extends MapAdapter> adpaterClass,
			AdaptInfo adaptInfo) {
		constructAdapter(adpaterClass, adaptInfo);
		// adapter.setmThumbnailLoader(new ThumbnailLoader(handler,
		// getContext(), ThumbnailLoader.MODE_LAZY, null));
		listview = (ListView) this.findViewById(R.id.fileListView);
		listview.setCacheColorHint(Color.TRANSPARENT);
		listview.setOnCreateContextMenuListener(this);
		listview.setOnItemClickListener(onItemClickListener);
	}

	private Object getOnClickListener() {
		// TODO Auto-generated method stub
		return new ListItemClickListener();
	}

	public void constructAdapter(Class<? extends MapAdapter> adapterClazz,
			AdaptInfo adaptInfo) {
		try {
			adapter = (SearchAdapter) adapterClazz.getConstructor(
					Context.class, AdaptInfo.class)
					.newInstance(this, adaptInfo);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected class ListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> paramparent, View view,
				int paramposition, long id) {
			parent = paramparent;
			position = paramposition;
			updateName(null);
			int dialog_res = name == null || name.equals("") ? NAMEPUT : TOSEND;
			if (dialog_res == NAMEPUT
					)
					  {
				showDialog(dialog_res);
			}else if((dialog_res == TOSEND && (retainName == null || retainName.equals("")))){
				showDialog(dialog_res);
			}else{
				send(parent, position);
			}
		}
	}

	public static void updateName(String paramName) {
		name = paramName != null ? paramName : SharedPreferencesMgr.getName();
	}

	private void send(AdapterView<?> parent, int position) {
		contactInfo = ((ContactInfo) (parent.getItemAtPosition(position)));
		inviteFriend(contactInfo, username, false);
	}

	public ViewHolder newViewHolderInstance() {

		return new SearchListViewHolder(list, this);
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
		this.setContentView(R.layout.invitation);
		context = this;
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		buildInvitedAdapter();
		username = (String) SharedPreferenceUtil.getValue(
				getApplicationContext(), "GBAPP", "USER_NAME", String.class);

		Logs.i("username " + username);// this.findViewById(R.id.invite_exec).setOnClickListener(new
//this.findViewById(R.id.searchBox).setFocusable(false);
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

	public List<ContactInfo> getKeyContacts(List<ContactInfo> info, String name) {
		List<ContactInfo> result = new ArrayList<ContactInfo>(0);
		if (name.equals("")) {
			result.addAll(info);
		} else {
			ContactInfo ci;
			for (int i = 0; i < info.size(); i++) {
				ci = info.get(i);
				if (ci.sort_key.toUpperCase().startsWith(name.toUpperCase())
						|| ci.name.toUpperCase().contains(name.toUpperCase())
						|| ci.phone.toUpperCase().contains(name.toUpperCase())
						|| ci.email.toUpperCase().contains(name.toUpperCase())) {
					result.add(ci);
				}
			}

		}
		Logs.i("result.size " + result.size());
		return result;
	}

	static Set<String> names = new TreeSet<String>(ComparatorRepo.stringKey);

	public static List<ContactInfo> getSourceContacts(String name,Context context) {
		names.clear();
		if (name == null || name.trim().equals("")) {
			name = "%";
		}

		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { "_id", "sort_key", "display_name" }, null, null,
				null);
		List<ContactInfo> list = new ArrayList<ContactInfo>(0);
		while (cursor.moveToNext()) {
			ContactInfo ci = new ContactInfo();
			ci.name = cursor.getString(2);
			ci.sort_key = cursor.getString(1);
			int contractID = cursor.getInt(0);
			Cursor cursor1 = context.getContentResolver().query(
					ContactsContract.Data.CONTENT_URI,
					new String[] { "mimetype", "data1", "data2" },
					"raw_contact_id = ?", new String[] { contractID + "" },
					null);
			while (cursor1.moveToNext()) {
				String data1 = cursor1.getString(cursor1
						.getColumnIndex("data1"));
				String mimeType = cursor1.getString(cursor1
						.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/email_v2".equals(mimeType)) { // ÓÊÏä
					Logs.i(",email=" + data1);
					ci.email = data1;
				} else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { // ÊÖ»ú
					Logs.i(",phone=" + data1);
					ci.phone = data1;
				}
			}
			if (ci.name == null) {
				cursor1.close();
				continue;
			}
			if ((ci.email == null || ci.email.equals(""))
					&& (ci.phone == null || ci.phone.equals(""))) {
				cursor1.close();
				continue;
			}
			if (names.contains(ci.name)) {
				cursor1.close();
				continue;
			} else {
				names.add(ci.name);
			}
			if (!name.trim().equals("%")) {
				if (!ci.name.trim().equals("")
						&& !ci.name.toUpperCase().contains(name.toUpperCase())) {
					cursor1.close();
					continue;
				}
			}
			Logs.i("ci ------------- > " + ci);
			list.add(ci);
			cursor1.close();

		}
		cursor.close();
		return list;
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (listview != null && listview.getCount() > 0) {
			return;
		}
		viewHolder = getViewHolder();

		viewHolder.start();
		if (markfromPathActivity) {
			markfromPathActivity = false;
			objectWords = staticKeyWords;
		}
		EditText text = ((EditText) this.findViewById(R.id.searchBox));
		if (!(text.getText().toString().trim().equals("") && objectWords.trim()
				.equals(""))) {
			text.setText(objectWords);
			baseKeywordSearch(objectWords);
		}

		((ListView) this.findViewById(R.id.fileListView)).setEmptyView(null);
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
		this.findViewById(R.id.h_line).setVisibility(View.GONE);

		objectWords = "";
		baseKeywordSearch(objectWords);

	}

	public void buildInvitedAdapter() {
		try {
			invitedAdapter = MapAdapter.class.getConstructor(Context.class,
					AdaptInfo.class).newInstance(this, getInvitedAdaptInfo());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// adapter.setmThumbnailLoader(new ThumbnailLoader(handler,
		// getContext(), ThumbnailLoader.MODE_LAZY, null));
		gridview = (GridView) this.findViewById(R.id.invitedList);
		gridview.setCacheColorHint(Color.TRANSPARENT);
		gridview.setClickable(false);
		// if (getOnClickListener() != null) {
		// gridview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// });
		// }

		gridview.setAdapter(invitedAdapter);
		this.findViewById(R.id.invitedbanner).setOnClickListener(
				new OnClickListener() {
					View gridView;

					@Override
					public void onClick(View view) {
						if (gridView == null) {
							gridView = findViewById(R.id.invitedList);
						}
						// TODO Auto-generated method stub
						if (gridView.getVisibility() == View.GONE) {
							((ImageView) SearchActivity.this
									.findViewById(R.id.arrow))
									.setImageResource(R.drawable.arrow002);
							gridView.setVisibility(View.VISIBLE);
						} else {
							((ImageView) SearchActivity.this
									.findViewById(R.id.arrow))
									.setImageResource(R.drawable.arrow001);
							gridView.setVisibility(View.GONE);
						}
					}
				});
	}

	boolean isShowInput = true;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);

		if (isShowInput) {
			((EditText) this.findViewById(R.id.searchBox)).clearFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(((EditText) this
					.findViewById(R.id.searchBox)).getWindowToken(), 0);
			isShowInput = false;
		}

		if (((TextView) this.findViewById(R.id.invitedtitle)).getText().equals(
				"")) {
			setInvitedTitle(0);
		}
	}

	boolean invited = false;

	@SuppressWarnings("unused")
	public void setInvitedTitle(int count) {
		String color = "";
		int units = count > 9 ? 10 : count;
		int width = ((View) this.findViewById(R.id.invitedtitle)).getWidth();
		if (width == -1) {
			return;
		}
		int unitWidth = width / 10;
		View view = ((View) this.findViewById(R.id.progInvited));
		view.setLayoutParams(new RelativeLayout.LayoutParams(unitWidth * units,
				view.getHeight()));
		((TextView) this.findViewById(R.id.invitedtitle))
				.setText((count > 9 ? "" : count + " / 10 ")
						+ getString(R.string.colleagues_invited));
		if (count > 9) {
			this.findViewById(R.id.background_).setVisibility(View.VISIBLE);
			this.findViewById(R.id.arrow).setVisibility(View.VISIBLE);
		}
	}

	public void fetchInvitd(String username) {
		Properties pro = new Properties();
		pro.put(SoapRes.KEY_INVITE_GBUSERNAME, username);
		sendRequest(SoapRes.URLGETInvite, SoapRes.REQ_ID_GET_INVITE, pro,
				new BaseResponseHandler(this, false));

	}

	private void inviteFriend(ContactInfo contactInfo, String username,
			boolean head) {
		// Data.inviting = head?head:Data.inviting;
		String invitedName = contactInfo.name;
		String invitedPhone = contactInfo.phone;
		String invitedEmail = contactInfo.email;
		Properties pro = new Properties();
		pro.put(SoapRes.KEY_INVITE_EMAIL, invitedEmail);
		pro.put(SoapRes.KEY_INVITE_PHONE, invitedPhone);
		pro.put(SoapRes.KEY_INVITE_USERNAME, invitedName);
		pro.put(SoapRes.KEY_INVITE_GBUSERNAME, username);
		sendRequest(SoapRes.URLInvite, SoapRes.REQ_ID_INVITE, pro,
				new BaseResponseHandler(this, false));
		if (StatusCheckUtil.isSDCardAvailable()) {
			pd = new ProgressDialog(this);
			pd.setMessage(this.getString(R.string.progress_wait));
			pd.setTitle(this.getString(R.string.progress_wait_title));
			pd.setCancelable(true);
			pd.show();
		}

	}

	Comparator<ContactInfo> comparator = new Comparator<ContactInfo>() {

		@Override
		public int compare(ContactInfo object1, ContactInfo object2) {
			// TODO Auto-generated method stub
			return object1.loaded == object1.loaded ? 0 : 1;
		}

	};
	int countInvited;
	boolean hasInvited = false;

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			if (o instanceof InviteFriendsPaser) {
				InviteFriendsPaser codePaser = (InviteFriendsPaser) o;
				String rescode = codePaser.getRescode();
				String error = codePaser.getError();
				String inviteUserCount = codePaser.getInviteUserCount();
				if ("200".equals(rescode)) {

					Data.info.remove(contactInfo);
					// adapter.getItemDataSrc().clear();
					// adapter.notifyDataSetChanged();
					// adapter.getItemDataSrc().setContent(Data.info);
					// adapter.notifyDataSetChanged();
					refreshRequested();
					Object value = null;
					baseKeywordSearch(SearchActivity.staticKeyWords);
					if (!hasInvited) {
						this.countInvited++;
						hasInvited = true;
					}
					if (this.countInvited > 9) {
						if ((value = SharedPreferenceUtil.getValue(
								getApplicationContext(), "GBAPP",
								SharedPreferencesMgr.KEY_InviteCode,
								String.class)) == null
								|| value.toString().equals("")) {
							UpdateInviteCodeActivity.updateInviteCodeActivity.edtInviteCode
									.setText(codePaser.getInviteCode());
							UpdateInviteCodeActivity.updateInviteCodeActivity
									.updateInviteCode();
						}
						SharedPreferencesMgr.setInviteCode(codePaser
								.getInviteCode());
						SharedPreferencesMgr.setInviteCodeExpiredDate(codePaser
								.getExpiredDate());

					} else
						Toast.makeText(getApplicationContext(),
								R.string.invite_success, Toast.LENGTH_SHORT)
								.show();
				}
				Logs.i("------------rescode " + codePaser.getRescode() + " "
						+ codePaser.getError());
				pd.cancel();
				pd.dismiss();
				pd = null;
			}
			if (o instanceof GetFriendsPaser) {
				GetFriendsPaser codePaser = (GetFriendsPaser) o;
				String rescode = codePaser.getRescode();
				String error = codePaser.getError();
				if ("200".equals(rescode)) {
					boolean nullFlag = Data.cInfos == null;
					if (!nullFlag)
						Data.cInfos.clear();
					Data.cInfos = codePaser.getContactInfo();
					if (nullFlag)
						this.removeDuplicated(Data.cInfos, Data.info);
					Logs.i("------------rescode " + codePaser.getRescode()
							+ " " + codePaser.getError() + " " + Data.cInfos);
					this.countInvited = Data.cInfos.size();
					hasInvited = false;
					setInvitedTitle(Data.cInfos.size());
					invitedAdapter.getItemDataSrc().clear();
					invitedAdapter.notifyDataSetChanged();
					invitedAdapter.getItemDataSrc().setContent(Data.cInfos);
					invitedAdapter.notifyDataSetChanged();

				} else if ("506".equals(rescode)) {
					setInvitedTitle(0);
				}

			}
		}
		if (Data.inviting) {
			if (batchTmp == null) {
				batchTmp = ((List<ContactInfo>) this.adapter.getItemDataSrc()
						.getContent());
			} else {
				refreshRequested();
			}
			int i = Collections.binarySearch(batchTmp, new ContactInfo(false),
					comparator);
			if (i > -1) {
				contactInfo = batchTmp.get(i);
				// inviteFriend(contactInfo);
			} else {
				Data.inviting = false;
				batchTmp = null;
				refreshRequested();
				showDialog(inviteSuccess);
			}
		}
		super.onReqResponse(o, methodId);
	}

	public void removeDuplicated(List<ContactInfo> material,
			List<ContactInfo> remainner) {
		List<ContactInfo> cis = new ArrayList<ContactInfo>();
		for (ContactInfo param : material) {
			for (ContactInfo ci : remainner) {
				if (param.name.equals(ci.name)) {
					cis.add(ci);
				}
			}
		}
		remainner.removeAll(cis);
		viewHolder.dataChanged();
	}

	private void refreshRequested() {
		// TODO Auto-generated method stub
		fetchInvitd(username);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if (id == inviteSuccess) {
			return new AlertDialog.Builder(context)
					.setIcon(R.drawable.gba_login_bg)
					.setMessage(R.string.invite_success)
					.setNegativeButton(R.string.sure,
							new CancelClicker(context)).create();
		}
		return (dialog = (AlertDialog) prepareDialog(id)) == null ? super
				.onCreateDialog(id) : dialog;
	}

	private Dialog prepareDialog(int id) {
		if (id == TOSEND) {
			return new AlertDialog.Builder(context)
					.setIcon(R.drawable.gba_login_bg)
					.setMessage(getString(R.string.send_with_ur_name, name))
					.setPositiveButton(R.string.sendInstantly,
							new CancelClicker(context) {

								@Override
								public void onClick(DialogInterface dialog,
										int arg1) {
									// TODO Auto-generated method stub
									retainName = username;
									send(parent, position);
									super.onClick(dialog, arg1);
								}

							})
					.setNegativeButton(R.string.modifyInfo,
							new CancelClicker(context) {

								@Override
								public void onClick(DialogInterface dialog,
										int arg1) {
									// TODO Auto-generated method stub
									startRegistration_updateActivity();
									super.onClick(dialog, arg1);
								}

							}).create();
		}
		if (id == NAMEPUT) {
			return new AlertDialog.Builder(context)
					.setIcon(R.drawable.gba_login_bg)
					.setMessage(R.string.fillnameBeforeSent)
					.setPositiveButton(R.string.addInfo,
							new CancelClicker(context) {

								@Override
								public void onClick(DialogInterface dialog,
										int arg1) {
									// TODO Auto-generated method stub
									startRegistration_updateActivity();
									super.onClick(dialog, arg1);
								}

							})
					.setNegativeButton(R.string.later,
							new CancelClicker(context)).create();
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog pdialog) {
		// TODO Auto-generated method stub
		if (id == TOSEND) {

			((AlertDialog) pdialog).setMessage(getString(
					R.string.send_with_ur_name, name));

			return;
		}
		super.onPrepareDialog(id, pdialog);
	}

	public static final String FromSearchActivity = "fromSearchActivity";

	public void startRegistration_updateActivity() {
		Intent intent = new Intent(SearchActivity.this,
				Registration_updateActivity.class);
		intent.putExtra(FromSearchActivity, true);
		startActivity(intent);
	}
}
