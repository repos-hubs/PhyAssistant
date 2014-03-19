package com.jibo.activity;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.common.DialogRes;
import com.jibo.dbhelper.NCCNAdapter;
import com.jibo.dbhelper.TNMAdapter;
import com.umeng.analytics.MobclickAgent;
import com.api.android.GBApp.R;

/**
 * 
 * @author Terry
 * @description 锟斤拷锟斤拷锟揭窖э拷锟斤拷锟绞憋拷锟斤拷锟阶拷锟斤拷锟紸ctivity
 * 
 */
public class ToolsActivity extends BaseSearchActivity implements
		OnClickListener {
	private GBApplication application;
	private Button btnTNM;// TNM锟斤拷锟斤拷锟斤拷诠锟斤拷锟�
	private Button btnECG;// 锟斤拷锟斤拷锟届常锟侥碉拷图
	private Button btnNCCN;// 锟斤拷锟斤拷实锟斤拷指锟斤拷

	private boolean isTNMDBExsit = true;
	private boolean isECGDBExsit = true;
	private boolean isNCCNDBExsit = true;
	private TNMAdapter tnmAdapter;
	private NCCNAdapter nccnAdapter;
	Context context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tools);
		super.onCreate(savedInstanceState);
		inits();
		context = this;
		setPopupWindowType(Constant.MENU_TYPE_3);
		MobclickAgent.onError(this);

		uploadLoginLogNew("Activity", "Tool", "create", null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		uploadLoginLogNew("Activity", "Tool", "end", null);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		boolean isEnabled = true;
		switch (v.getId()) {
		case R.id.btn_ecg:
			if (isECGDBExsit) {
				intent = new Intent(this, ECGListActivity.class);// 锟斤拷锟斤拷锟届常锟侥碉拷图
				startActivity(intent);
				MobclickAgent.onEvent(this, "ToolsAct", "TumorAct", 1);// "SimpleButtonclick");
			}
			isEnabled = isECGDBExsit;
			break;
		case R.id.btn_nccn:
			if (isNCCNDBExsit) {
				intent = new Intent(this, NCCNListActivity.class);// 锟斤拷锟斤拷实锟斤拷指锟斤拷
				startActivity(intent);
				MobclickAgent.onEvent(this, "ToolsAct", "TabCalcHeart", 1);// "SimpleButtonclick");
			}
			isEnabled = isNCCNDBExsit;
			break;
		case R.id.btn_tnm:
			if (isTNMDBExsit) {
				intent = new Intent(this, TumorActivity.class);// TNM锟斤拷锟斤拷锟斤拷诠锟斤拷锟�
				startActivity(intent);
				MobclickAgent.onEvent(this, "ToolsAct", "NCCNAct", 1);// "SimpleButtonclick");
			}
			isEnabled = isTNMDBExsit;
			break;
		}
		if (!isEnabled) {
			showNoDbDialog();
		}
	}
	

	private void showNoDbDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(getResources().getDrawable(R.drawable.icon));
		builder.setTitle(getResources().getString(R.string.generalprompt));
		builder.setMessage(getResources().getString(R.string.tools_goto_install));
		builder.setPositiveButton(getResources().getString(R.string.go_to_install), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(ToolsActivity.this, MarketActivity.class);
				ToolsActivity.this.startActivity(intent);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	@Override
	protected void treatMenuClick(int typeId) {
		if (typeId == Constant.FLAG_MY_JIBO) {
			Intent in = new Intent(this, MyFavoriteListActivity.class);// Util.myFavCategory
			Constant.myFavCategory = 1;
			startActivity(in);
		} else
			super.treatMenuClick(typeId);
	}

	@Override
	protected void onStop() {
		tnmAdapter.closeHelp();
		nccnAdapter.closeHelp();
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void inits() {
		application = (GBApplication) getApplication();
		tnmAdapter = new TNMAdapter(this, 1);
		nccnAdapter = new NCCNAdapter(this, 1);
		btnTNM = (Button) findViewById(R.id.btn_tnm);
		btnNCCN = (Button) findViewById(R.id.btn_nccn);
		btnECG = (Button) findViewById(R.id.btn_ecg);
		TextView txtTitle = (TextView) findViewById(R.id.txt_header_title);
		txtTitle.setText(getResources().getString(R.string.tool));

		btnNCCN.setOnClickListener(this);
		btnECG.setOnClickListener(this);
		btnTNM.setOnClickListener(this);

		btnNCCN.setTextColor(Color.BLACK);
		btnECG.setTextColor(Color.BLACK);
		btnTNM.setTextColor(Color.BLACK);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		File tumorFile = new File(Constant.DATA_PATH_MARKET_INSTALL + File.separator
				+ "tumor.db");
		File nccnFile = new File(Constant.DATA_PATH_MARKET_INSTALL + File.separator
				+ "nccn_disease.db");
		File ecgFile = new File(Constant.DATA_PATH_MARKET_INSTALL + File.separator
				+ "ecg.db");
		if (!tumorFile.exists()) {
			btnTNM.setTextColor(Color.GRAY);
			isTNMDBExsit = false;
		}else if(tumorFile.exists()) {
			btnTNM.setTextColor(Color.BLACK);
			isTNMDBExsit = true;
		}
		if (!ecgFile.exists()) {
			btnECG.setTextColor(Color.GRAY);
			isECGDBExsit = false;
		}else if(ecgFile.exists()) {
			btnECG.setTextColor(Color.BLACK);
			isECGDBExsit = true;
		}
		if (!nccnFile.exists()) {
			btnNCCN.setTextColor(Color.GRAY);
			isNCCNDBExsit = false;
		}else if(nccnFile.exists()) {
			btnNCCN.setTextColor(Color.BLACK);
			isNCCNDBExsit = true;
		}
		
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
