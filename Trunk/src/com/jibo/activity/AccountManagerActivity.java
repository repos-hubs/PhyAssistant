package com.jibo.activity;

import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.util.Properties;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.adapter.AccountAdapter;
import com.jibo.common.Constant;
import com.jibo.common.DeviceInfo;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.data.NewDBDataPaser;
import com.jibo.dbhelper.InitializeAdapter;
import com.jibo.net.BaseResponseHandler;
import com.jibo.ui.SlipButton.OnChangedListener;
import com.jibo.util.tips.TipHelper;
import com.umeng.fb.UMFeedbackService;

/**
 * 设置页面
 * 
 * @author peter.pan
 */
public class AccountManagerActivity extends BaseSearchActivity implements
		OnItemSelectedListener, OnItemClickListener {

	private TextView updatetimeView;
	private ListView mList;
	private BaseResponseHandler baseHandler;
	private InitializeAdapter initializeAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.accountmanager);
		super.onCreate(savedInstanceState);
		try{
		init();
		}catch(Exception e){
			e.printStackTrace();
		}
		// Util.s_searchIndex = Util.TABLE_CALCULATIONS;
		// mSearchIndex = Util.DRUG_REFERENCE;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (this.findViewById(R.id.showversion).getVisibility() == View.VISIBLE) {
				this.findViewById(R.id.showversion).setVisibility(View.GONE);
				this.findViewById(R.id.mask).setVisibility(View.GONE);
				return true;
			}
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void init() {
		TextView txtTitle = (TextView) findViewById(R.id.txt_header_title);
		txtTitle.setText(R.string.accountmanagement);

		updatetimeView = (TextView) findViewById(R.id.updatetime);
		String time = SharedPreferencesMgr.getUpdateTime();
		if (!SharedPreferencesMgr.NULL.equals(time))
			updatetimeView.setText(time);
		else
			updatetimeView.setText(R.string.datanotset);

		mList = (ListView) findViewById(R.id.mList);
		mList.setOnItemClickListener(this);
		
		LinearLayout pushLay = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.push_lay, null);
		((ImageView)pushLay.findViewById(R.id.icon)).setImageResource(R.drawable.push_lock_ico);
		((TextView)pushLay.findViewById(R.id.text1)).setText(R.string.push_lock_title);
//		((TextView)pushLay.findViewById(R.id.text2)).setText(R.string.push_lock_msg);
		((com.jibo.ui.SlipButton)pushLay.findViewById(R.id.register_submit)).setChecked(!SharedPreferencesMgr.getPushIsLock());
		((com.jibo.ui.SlipButton)pushLay.findViewById(R.id.register_submit)).setOnChangedListener("", new OnChangedListener(){

	@Override
	public void OnChanged(String strName, boolean CheckState) {
		// TODO Auto-generated method stub
		SharedPreferencesMgr.setPushIsLock(!CheckState);
	}

});
//				CheckBox pushLock = (CheckBox)pushLay.findViewById(R.id.push_lock_btn);
//		pushLock.setChecked(SharedPreferencesMgr.getPushIsLock());
//		pushLock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
//			}
//		});
		
		mList.addFooterView(pushLay);
		
		
		showListView();
		boolean isFirst = SharedPreferencesMgr.getFirstAccount();
		if (isFirst) {
			SharedPreferencesMgr.setFirstAccount(false);
		}
		
		// setPopupWindowType(Constant.MENU_TYPE_5);
	}

	/**
	 * 显示列表
	 * */
	private void showListView() {
		int[] icons = new int[] {
				// R.drawable.set_up_check,//隐藏起来，跟ios一致
				R.drawable.set_up_about, R.drawable.set_up_password,
				R.drawable.set_up_userinfo, R.drawable.set_up_version,
				R.drawable.table_tips, R.drawable.set_up_feedback,
				R.drawable.set_up_share, R.drawable.set_up_logout };
		String[] texts = new String[] {
				// getString(R.string.accountcheck),//隐藏起来，跟ios一致
				getString(R.string.aboutus),
				getString(R.string.accountpassword),
				getString(R.string.accountregistration),
				getString(R.string.set_up_version),
				getString(R.string.ver_feature), getString(R.string.myinfset),
				getString(R.string.shrout), getString(R.string.lgout) };

		AccountAdapter accountAdapter = new AccountAdapter(this, icons, texts);
		mList.setAdapter((ListAdapter) accountAdapter);
		mList.setVisibility(View.VISIBLE);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		TextView v = (TextView) view;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = null;
		// getString(R.string.accountcheck),
		// getString(R.string.aboutus),
		// getString(R.string.accountpassword),
		// getString(R.string.accountregistration),
		// getString(R.string.feedback),
		// getString(R.string.shrout),
		// getString(R.string.lgout),
		switch (position) {
		// case 0://检查更新
		// checkUpdate();
		//
		// break;
		case 0:
			// TODO
			intent = new Intent(AccountManagerActivity.this,
					AboutGbiActivity.class);
			startActivity(intent);
			break;
		case 1:
			if (SharedPreferencesMgr.getIsWeiboLogin()) {// 微博账号，不能修改密码
				Toast.makeText(this,
						getString(R.string.weibocannotupdatepassword),
						Toast.LENGTH_SHORT).show();
				return;
			}
			intent = new Intent(AccountManagerActivity.this,
					Registration_ChangePwdActivity.class);
			intent.putExtra("isModify", true);
			startActivityForResult(intent, 10);
			break;
		case 2:
			intent = new Intent();
			intent.setClass(AccountManagerActivity.this,
					Registration_updateActivity.class);
			startActivityForResult(intent, 10);
			break;
		case 3:
			intent = new Intent();
			intent.setClass(AccountManagerActivity.this,
					UpdateInviteCodeActivity.class);
			startActivity(intent);
			break;
		case 4:
			TipHelper.addTip(R.id.mask, this);
			TipHelper.addTip(R.id.showversion, this);
			this.findViewById(R.id.switchTip).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							TipHelper.disableTipViewOnScreenVisibility();
							TipHelper.sign(true, true);
							TipHelper.getTraceRecorder().putAll(
									TipHelper.traceRecorderRecovery);
							TipHelper.traceRecorderRecovery.clear();
							TipHelper.getBackup().clear();
						}

					});
			this.findViewById(R.id.closeTip).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							findViewById(R.id.showversion).setVisibility(
									View.GONE);
							findViewById(R.id.mask).setVisibility(View.GONE);
							TipHelper.sign(false, true);
						}

					});
			break;
		case 5:
			UMFeedbackService.openUmengFeedbackSDK(context);
			break;
		case 6:
			sharing(R.array.items2, 0);
			break;
		case 7:
			showDialog(DialogRes.DIALOG_QUIT_PROMPT);
			break;
		default:
			break;
		}
	}

	/**
	 * 检验是否是最新的版本
	 * */
	private void checkUpdate() {
		try {
			if (initializeAdapter == null) {
				initializeAdapter = new InitializeAdapter(this, 1);
				baseHandler = new BaseResponseHandler(this);
			}
			if (DeviceInfo.instance.isNetWorkEnable()) {
				Properties propertyInfo = new Properties();
				String verName = initializeAdapter.getCurrentVerName();
				propertyInfo.put(SoapRes.KEY_NEW_P_VERSION, verName);
				sendRequest(SoapRes.URLNewVersion, SoapRes.REQ_ID_NEW_VERSION,
						propertyInfo, baseHandler);
			} else {
				showDialog(DialogRes.DIALOG_ID_NETWORK_NOT_AVALIABLE);
			}
		} catch (SQLiteException e) {

		}
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			if (o instanceof NewDBDataPaser) {
				NewDBDataPaser vd = (NewDBDataPaser) o;
				String type = vd.getType();
				if (type != null && type.equals("fulldata")) {
					showDialog(DialogRes.DIALOG_ID_HAS_UPDATE);
				} else {
					showDialog(DialogRes.DIALOG_ID_NO_UPDATE);
				}
			}
		}
	}

	public void onErrResponse(Throwable error, String content) {
		System.out.println("error   " + error);
		if (error instanceof FileNotFoundException) {
			System.out.println("***    FileNotFoundException     ***");
		} else if (error instanceof SocketTimeoutException) {
			System.out.println("***    SocketTimeoutException     ***");
		}

	}

	/**
	 * 点击dialog左键 子类需要重载
	 * 
	 * @param dialogId
	 *            对话框ID
	 * */
	public void clickPositiveButton(int dialogId) {
		switch (dialogId) {
		case DialogRes.DIALOG_QUIT_PROMPT:
			super.clickPositiveButton(DialogRes.DIALOG_QUIT_PROMPT);
			break;
		case DialogRes.DIALOG_ID_HAS_UPDATE:
			Intent intent = new Intent(AccountManagerActivity.this,
					InitializeActivity.class);
			intent.putExtra(Constant.CHECK_DATA_UPDATE, true);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			SharedPreferencesMgr.setFirstLaunch(true);
			intent.putExtra("checkupdate", true);
			startActivity(intent);
			finish();
			break;
		}
		removeDialog(dialogId);
	}

	@Override
	public void clickNegativeButton(int dialogId) {
		removeDialog(dialogId);
	}

}
