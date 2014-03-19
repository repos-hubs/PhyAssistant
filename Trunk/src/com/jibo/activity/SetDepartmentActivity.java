package com.jibo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.dao.DBFactory;
import com.jibo.dao.DBHelper;
import com.jibo.dao.DaoManager;
import com.jibo.dao.DaoSession;
import com.jibo.dao.DepartmentDao;
import com.jibo.data.UpdateUserDepartmentPaser;
import com.jibo.entity.Department;
import com.jibo.net.BaseResponseHandler;

/**
 * 填写科室信息
 * 
 * @author simon
 * 
 */
public class SetDepartmentActivity extends BaseActivity implements
		OnClickListener, OnItemSelectedListener {

	/** UI */

	private Spinner mBigDepartment;
	private Spinner mSubDepartment;
	private Button submitBtn;// 确认

	private ArrayList<String> mBigDepartments;
	private List<Department> mSubDepartments;

	private Context context;
	private Animation shake;

	private Intent intent;

	private String bigDepartment;
	private String subDepartment;

	private DBHelper dbHelper;
	private DaoSession daoSession;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setdepartment);
		context = this;
		intent = getIntent();
		dbHelper = DBFactory.getSDCardDbHelper(context);
		daoSession = new DaoManager(dbHelper).newSession();

		try {
			initBigDepartmentSpinner();
		} catch (Exception e) {
			e.printStackTrace();
		}
		submitBtn = (Button) findViewById(R.id.register_submit);
		submitBtn.setOnClickListener(this);
	}

	/**
	 * 初始化科室信息
	 * 
	 * @throws Exception
	 */
	private void initBigDepartmentSpinner() throws Exception {
		mBigDepartment = (Spinner) findViewById(R.id.bigDepartment_spinner);
		mSubDepartment = (Spinner) findViewById(R.id.subDepartment_spinner);
		mBigDepartments = daoSession.getDepartmentDao().queryBigDepartment(
				context);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.gba_spinner_item, mBigDepartments);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mBigDepartment.setAdapter(adapter);
		mBigDepartment.setOnItemSelectedListener(this);
		mSubDepartments = new ArrayList<Department>(1);
		mSubDepartments.add(new Department(getString(R.string.departments)));
		ArrayAdapter<Department> adapter1 = new ArrayAdapter<Department>(this,
				R.layout.gba_spinner_item, mSubDepartments);
		mSubDepartment.setAdapter(adapter1);
	}

	@Override
	public void onClick(View v) {
		if (!v.isEnabled()) {
			Log.e("simon", "on click error, button is disabled");
			return;
		}
		switch (v.getId()) {
		case R.id.register_submit:// 确认
			shake = AnimationUtils.loadAnimation(context, R.anim.shake);

			bigDepartment = mBigDepartment.getSelectedItem().toString();
			subDepartment = mSubDepartment.getSelectedItem().toString();
			if (bigDepartment.equals(getString(R.string.category))) {
				mBigDepartment.startAnimation(shake);
				return;
			}
			if (subDepartment.equals(getString(R.string.departments))) {
				mSubDepartment.startAnimation(shake);
				return;
			}

			Properties info = new Properties();
			info.put("gbUserName", SharedPreferencesMgr.getUserName());
			info.put("bigdepartment", bigDepartment);
			info.put("Department", subDepartment);
			sendRequest(SoapRes.URLCustomer,
					SoapRes.REQ_ID_UPDATE_USER_DEPARTMENT, info,
					new BaseResponseHandler(this,
							DialogRes.DIALOG_SEND_REGISTER_REQUEST));

			break;
		default:
			break;
		}
	}

	/**
	 * 网络回调
	 * 
	 * @param o
	 * @param methodId
	 */
	@Override
	public void onReqResponse(Object o, int methodId) {
		super.onReqResponse(o, methodId);
		if (o != null) {
			if (o instanceof UpdateUserDepartmentPaser) {// 注册

				UpdateUserDepartmentPaser paser = (UpdateUserDepartmentPaser) o;
				boolean isSuccess = paser.isSuccess();
				if (isSuccess) {// 修改成功
					SharedPreferencesMgr.setBigDepartments(bigDepartment);
					SharedPreferencesMgr.saveDept(subDepartment);
					try {
						intent.setClass(context, Class.forName(intent
								.getStringExtra("className")));
						startActivity(intent);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					finish();
				} else {// 修改失败
					Toast.makeText(context, paser.getErrorMsg(), 2).show();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		ArrayAdapter<Department> adapter;
		switch (parent.getId()) {
		case R.id.bigDepartment_spinner:
			if (mBigDepartments.get(position).equals(
					getString(R.string.category))) {
				return;
			}
			List<Department> list = daoSession
					.getDepartmentDao()
					.queryBuilder()
					.where(DepartmentDao.Properties.BigDepartment
							.eq(mBigDepartment.getSelectedItem().toString()))
					.list();
			if (list != null)
				mSubDepartments.addAll(list);
			adapter = new ArrayAdapter<Department>(this,
					R.layout.gba_spinner_item, mSubDepartments);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSubDepartment.setAdapter(adapter);
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
