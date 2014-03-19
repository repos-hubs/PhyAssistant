package com.jibo.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.BitmapManager;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.dao.DepartmentDao;
import com.jibo.dao.HospitalCityDao;
import com.jibo.data.CheckUserInfoStatusPaser;
import com.jibo.data.UpdateUserAllInfoPaser;
import com.jibo.data.entity.AdvertisingEntity;
import com.jibo.data.entity.LoginEntity;
import com.jibo.dbhelper.LoginSQLAdapter;
import com.jibo.entity.Department;
import com.jibo.entity.Hospital;
import com.jibo.entity.HospitalCity;
import com.jibo.entity.HospitalProvince;
import com.jibo.net.BaseResponseHandler;
import com.jibo.util.Logs;

/**
 * 详细注册or注册内容修改界面 <一>
 * 
 * @author simon
 * 
 */
public class Registration_updateActivity extends BaseActivity implements
		OnClickListener, OnItemSelectedListener {

	private static final String TAG = "Registration_One";

	private ScrollView view;
	private RelativeLayout mainRlt;
	private EditText mNameEdit;
	private EditText mEMail;
	private EditText mContactNb;

	private EditText licenseNb;
	private Spinner mHospitalRegion;
	private Spinner mHospitalCity;
	private AutoCompleteTextView hospitalInput;
	private Spinner mBigDepartment;
	private Spinner mSubDepartment;
	private Spinner mJob;
	private Spinner mProfileSpinner;
	private EditText inviteCode;
	private Button submit;

	private ArrayList<String> mBigDepartments;
	private List<Department> mSubDepartments;
	private List<HospitalProvince> mRegions;
	private List<HospitalCity> cities;
	private List<Hospital> hospitals;

	private LoginEntity rData;

	private LoginSQLAdapter dbHelper;

	private boolean initInviteCodeIsNull = true;

	private Animation shake;

	boolean bModify;

	private PopupWindow myimgpopupwindow;

	private String fileBase64 = "" ;
	private String contentType = "";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registration_update);
		dbHelper = new LoginSQLAdapter(this);
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		bModify = getIntent().getBooleanExtra("isModify", false);
		
		if(!SharedPreferencesMgr.getLicenseNumberCheckStatus().equals("200")){
			checkLicenseNumberStatus();
		}
		
		initData();
		initComponents();
	}
	
	private void checkLicenseNumberStatus(){
		Properties info = new Properties();
		info.put("userName", SharedPreferencesMgr.getUserName());
		info.put("sigh", "");
		sendRequest(SoapRes.URLCustomer,
				SoapRes.REQ_ID_CHECK_USER_INFO_STATUS, info,
				new BaseResponseHandler(this,
						DialogRes.DIALOG_UPDATE_FOR_DATA));
	}

	@Override
	public void onReqResponse(Object o, int methodId) {
		if (o != null) {
			if (o instanceof UpdateUserAllInfoPaser) {// 修改注册信息
				UpdateUserAllInfoPaser paser = (UpdateUserAllInfoPaser) o;
				boolean isSuccess = paser.isSuccess();
				if (isSuccess) {// 修改成功

					LoginEntity entity = paser.getLoginEntity();
					Log.i("fuwuqi", entity.getLicenseNumber());
					saveLoginInfoToLocal(entity);// 保存用户信息

					if (initInviteCodeIsNull)
						downLoadImage(entity.getGbUserName(),
								entity.getCompanyName(), entity.getImagePath());// 下载广告图片

					String string = getString(R.string.update_userinfo_success);
					if (paser.getCode().equals("202")) {
						string = getString(R.string.errorlicense);
					}
					Toast.makeText(context, string, 2).show();
					GBApplication.gbapp.setHomeLaunched(false);
					GBApplication.gbapp.setStartActivity(true);
					setResult(RESULT_OK);

					finish();
				} else {// 修改失败
					Toast.makeText(context, paser.getErrorMsg(), 2).show();
				}
			}else if (o instanceof CheckUserInfoStatusPaser) {//检查用户信息验证状态
				CheckUserInfoStatusPaser paser = (CheckUserInfoStatusPaser) o;
				boolean isSuccess = paser.isSuccess();
				if (isSuccess) {
					SharedPreferencesMgr.setLicenseNumberCheckStatus(paser.getCheckStatus());
					SharedPreferencesMgr.saveLicenseNumber(paser.getLicenseNumber());
					SharedPreferencesMgr.saveLicenseCheckInfo(paser.getCheckInfo());
					if(paser.getCheckStatus().equals(200)){
						((CheckBox)findViewById(R.id.licNo_verified_status)).setChecked(true);
					}
					licenseNb.setText(paser.getLicenseNumber());
				}
			}
		}
	}
	
	private final int editLicenseNumber = 1;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case editLicenseNumber:
				
				String licenseNumber = data.getStringExtra("licenseNumber");
				if(!TextUtils.isEmpty(licenseNumber)){
					licenseNb.setText(licenseNumber);
					rData.setLicenseNumber(licenseNb.getText().toString().trim());
					SharedPreferencesMgr.saveLicenseNumber(rData.getLicenseNumber());
								
				}
				String photoPath = data.getStringExtra("photoPath");
				if(!TextUtils.isEmpty(photoPath)){
					SharedPreferencesMgr.saveLicensePhoto(photoPath); 
					
				}else{
					photoPath = SharedPreferencesMgr.getLicensePhoto();					
				}
				fileBase64 = Util.fileBase64(Util.compressImage(photoPath,this));
				Logs.i("fileBase64=="+fileBase64);
				//如果大于1M
				if(fileBase64.length()>1024*1024&& Util.isMobile(this)){
			    	Toast.makeText(this, R.string.wifi_down, 2).show();
			    }
				contentType = photoPath.substring(photoPath.lastIndexOf(".") + 1);
				break;
			default:
				break;
			}
		}
	}

	private void downLoadImage(String gbUserName, String company,
			final String imagePath) {
		if (null != company && !"".equals(company) && null != imagePath
				&& !"".equals(imagePath)) {// 下载广告图片
			dbHelper.insertAdvertising(new AdvertisingEntity(gbUserName,
					company, imagePath));
			new Thread(new Runnable() {
				@Override
				public void run() {
					BitmapManager.downloadBitmap(imagePath, 0, 0);
				}
			}).start();
		}
	}

	/**
	 * @author simon 2012-7-23
	 * @Description initialized data
	 * @param
	 * @return void
	 */

	private void initData() {
		rData = new LoginEntity();
		rData.setGbUserName(SharedPreferencesMgr.getUserName());
		rData.setUserName(SharedPreferencesMgr.getName());
		rData.setEmail(SharedPreferencesMgr.getEmail());
		rData.setContactNumber(SharedPreferencesMgr.getContactNb());
		rData.setLicenseNumber(SharedPreferencesMgr.getLicenseNumber());
		rData.setHospitalRegion(SharedPreferencesMgr.getRegion());
		rData.setHospitalCity(SharedPreferencesMgr.getCity());
		rData.setHospitalName(SharedPreferencesMgr.getHospitalName());
		rData.setBigDepartments(SharedPreferencesMgr.getBigDepartments());
		rData.setDepartment(SharedPreferencesMgr.getDept());
		rData.setJob(SharedPreferencesMgr.getJob());
		rData.setProfessional_title(SharedPreferencesMgr.getProfile());
		rData.setInviteCode(SharedPreferencesMgr.getInviteCode());
	}

	/**
	 * @author simon 2012-7-23
	 * @Description initialized components
	 * @param
	 * @return void
	 */
	private void initComponents() {

		view = (ScrollView) findViewById(R.id.main_scrollView);
		// view.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// if (event.getAction() == MotionEvent.ACTION_MOVE)
		// flag = true;
		// if (event.getAction() == MotionEvent.ACTION_UP)
		// flag = false;
		// return false;
		// }
		// });

		mNameEdit = (EditText) findViewById(R.id.name_edittext);
		mEMail = (EditText) findViewById(R.id.email_edittext);
		mContactNb = (EditText) findViewById(R.id.contactNumber_edittext);
		licenseNb = (EditText) findViewById(R.id.licenseNumber_edittext);
		hospitalInput = (AutoCompleteTextView) findViewById(R.id.hospital_edittext);
		inviteCode = (EditText) findViewById(R.id.invitecode_edittext);
		submit = (Button) findViewById(R.id.register_submit);
		mainRlt = (RelativeLayout) findViewById(R.id.mai);
		mNameEdit.setText(rData.getUserName());
		mEMail.setText(rData.getEmail());
		mContactNb.setText(rData.getContactNumber());
		Log.i("linum", rData.getLicenseNumber());
		licenseNb.setText(rData.getLicenseNumber());
		hospitalInput.setText(rData.getHospitalName());
		licenseNb.setLongClickable(false);
		licenseNb.setFocusable(false);
		licenseNb.setOnClickListener(this);

		hospitalInput.setOnClickListener(this);
		// hospitalInput.addTextChangedListener(new MyTextWatcher());
		// hospitalInput.setThreshold(1);

		inviteCode.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				8) });
		submit.setOnClickListener(this);

		if (!rData.getInviteCode().equals("")) {
			inviteCode.setFocusable(false);
			inviteCode.setEnabled(false);
			inviteCode.setText(rData.getInviteCode());
			initInviteCodeIsNull = false;
		}
		if (bModify)
			inviteCode.requestFocus();
		try {
			initBigDepartmentSpinner();
			initPlaceSpinner();
			initProfileSpinner();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化地域信息
	 * 
	 * @throws Exception
	 */
	private void initPlaceSpinner() throws Exception {
		mHospitalRegion = (Spinner) findViewById(R.id.region_spn);
		mHospitalCity = (Spinner) findViewById(R.id.city_spn);

		mRegions = new ArrayList<HospitalProvince>();
		mRegions.add(new HospitalProvince(context.getString(R.string.region)));

		List<HospitalProvince> provinceList = daoSession
				.getHospitalProvinceDao().loadAll();
		if (null != provinceList) {
			mRegions.addAll(provinceList);
		}

		ArrayAdapter<HospitalProvince> adapter = new ArrayAdapter<HospitalProvince>(
				this, R.layout.gba_spinner_item, mRegions);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mHospitalRegion.setAdapter(adapter);

		if (null != rData.getHospitalRegion()
				&& !"".equals(rData.getHospitalRegion().trim()))
			mHospitalRegion.setSelection(adapter
					.getPosition(new HospitalProvince(rData.getHospitalRegion()
							.trim())));
		else
			setInputNotFocusable();
		mHospitalRegion.setOnItemSelectedListener(this);

		cities = new ArrayList<HospitalCity>();
		cities.add(new HospitalCity(context.getString(R.string.city)));
		ArrayAdapter<HospitalCity> adapter1 = new ArrayAdapter<HospitalCity>(
				this, R.layout.gba_spinner_item, cities);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mHospitalCity.setAdapter(adapter1);
		mHospitalCity.setOnItemSelectedListener(this);

	}

	private void setInputNotFocusable() {
		hospitalInput.setFocusable(false);
		hospitalInput.setLongClickable(false);
		hospitalInput.setFocusableInTouchMode(false);
	}

	private void setInputHasFocusable() {
		hospitalInput.setFocusable(true);
		hospitalInput.setLongClickable(false);
		hospitalInput.setFocusableInTouchMode(true);
		hospitalInput.requestFocus();
		InputMethodManager m = (InputMethodManager) context
				.getSystemService(INPUT_METHOD_SERVICE);
		m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private String[] jobArrIds;
	private String[] jobArrValues;

	/**
	 * 初始化职称信息
	 * 
	 * @throws Exception
	 */
	private void initProfileSpinner() throws Exception {
		mJob = (Spinner) findViewById(R.id.job_spn);

		jobArrIds = getResources().getStringArray(R.array.job_arr_id);
		jobArrValues = getResources().getStringArray(R.array.job_arr);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.gba_spinner_item, jobArrValues);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mJob.setAdapter(adapter);

		if (!TextUtils.isEmpty(rData.getJob())) {
			mJob.setSelection(getIndex(rData.getJob(), jobArrValues));
		}
		mJob.setOnItemSelectedListener(this);
		
		mProfileSpinner = (Spinner) findViewById(R.id.profile_spinner);
		if(mJob.getSelectedItemPosition() == 0) return;
		final String[] arr = getStringArr(jobArrIds[mJob.getSelectedItemPosition() - 1]
				+ (Util.isZh() ? "_zh" : "_en"));
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				R.layout.gba_spinner_item, arr);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mProfileSpinner.setAdapter(adapter1);
		if (!TextUtils.isEmpty(rData.getProfessional_title())){
			mProfileSpinner.setSelection(getIndex(rData.getProfessional_title(), arr), true);
		}
		mProfileSpinner.setOnItemSelectedListener(this);
	}

	private String[] getStringArr(String key) {
		try {
			String[] arr = getResources().getStringArray(
					(R.array.class.getField(key)).getInt(null));
			return arr;
		} catch (Exception e) {
			e.printStackTrace();
			return new String[] { "" };
		}
	}

	private int getIndex(String job, String[] jobArrValues) {
		if (null == jobArrValues)
			return 0;
		for (int i = 0; i < jobArrValues.length; i++) {
			if (jobArrValues[i].equals(job))
				return i;
		}
		return 0;
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
		if (null != rData.getBigDepartments()
				&& !"".equals(rData.getBigDepartments()))
			mBigDepartment.setSelection(adapter.getPosition(rData
					.getBigDepartments()));

		mSubDepartments = new ArrayList<Department>(1);
		mSubDepartments.add(new Department(getString(R.string.departments)));
		ArrayAdapter<Department> adapter1 = new ArrayAdapter<Department>(this,
				R.layout.gba_spinner_item, mSubDepartments);
		mSubDepartment.setAdapter(adapter1);
	}


	/**
	 * 检查用户名 是否符合规范
	 * 
	 * @return
	 */
	public boolean checkUserName() {
		String result = mNameEdit.getText().toString().replaceAll(" ", "");
		if (mNameEdit.getText().toString().length() > result.length()) {
			Toast toast = Toast.makeText(context,
					this.getString(R.string.usernamerule), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 220);
			toast.show();
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 检查用户输入是否正确
	 * 
	 * @return
	 */
	private boolean checkAndSaveData() {

		String string;
		if (!mNameEdit.getText().toString().equals("")) {
			if (!checkUserName()) {
				mNameEdit.requestLayout();
				mNameEdit.requestFocus();
				return false;
			}

		}

		if (!mEMail.getText().toString().equals("")) {
			if (!Util.isEmail(mEMail.getText().toString())) {
				mEMail.requestLayout();
				mEMail.requestFocus();
				string = getString(R.string.check_email_address);
				Toast.makeText(this, string, Toast.LENGTH_LONG).show();
				return false;
			}
		}

		if (!mContactNb.getText().toString().trim().equals("")) {
			if (!Util.isPhoneNumber(mContactNb.getText().toString().trim())) {
				mContactNb.requestLayout();
				mContactNb.requestFocus();
				string = getString(R.string.isNotPhoneNumber);
				Toast.makeText(this, string, Toast.LENGTH_LONG).show();
				return false;
			}
		}

		if (hospitalInput.getText().toString().equals("")) {
			if (!checkEditText(hospitalInput)) {
				hospitalInput.requestLayout();
				hospitalInput.requestFocus();
				return false;
			}
		}

		String region = "";
		String city = "";
		String bigDepartment = "";
		String subDepartment = "";
		if (!mHospitalRegion.getSelectedItem().toString()
				.equals(getString(R.string.region))) {
			region = mHospitalRegion.getSelectedItem().toString();
		}

		if (!mHospitalCity.getSelectedItem().toString()
				.equals(getString(R.string.city))) {
			city = mHospitalCity.getSelectedItem().toString();
		}

		if (!mBigDepartment.getSelectedItem().toString()
				.equals(getString(R.string.category))) {
			bigDepartment = mBigDepartment.getSelectedItem().toString();
		}

		if (!mSubDepartment.getSelectedItem().toString()
				.equals(getString(R.string.departments))) {
			subDepartment = mSubDepartment.getSelectedItem().toString();
		}

		rData.setUserName(mNameEdit.getText().toString().trim());
		rData.setContactNumber(mContactNb.getText().toString().trim());
		rData.setEmail(mEMail.getText().toString().trim());
		rData.setLicenseNumber(rData.getLicenseNumber());
	//	rData.setLicenseNumber(licenseNb.getText().toString().trim());
		rData.setHospitalRegion(region);
		rData.setHospitalCity(city);
		rData.setHospitalName(hospitalInput.getText().toString().trim());
		rData.setInviteCode(inviteCode.getText().toString().trim());
		rData.setBigDepartments(bigDepartment);
		rData.setDepartment(subDepartment);
		rData.setJob(mJob.getSelectedItem().toString());
//		SharedPreferencesMgr.setJob(mJob.getSelectedItem().toString());
		rData.setProfessional_title(mProfileSpinner.getSelectedItem().toString());
//		SharedPreferencesMgr.setJob(mProfileSpinner.getSelectedItem().toString());

		return true;
	}

	/**
	 * 检查医院名称 是否非法
	 * 
	 * @return
	 */
	public boolean checkEditText(EditText view) {
		String result = view.getText().toString().replaceAll(" ", "");
		if (view.getText().toString().length() > result.length()) {
			Toast toast = Toast.makeText(context,
					getString(R.string.hospital_is_illegal), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 220);
			toast.show();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		if (!v.isEnabled()) {
			Log.e(TAG, "on click error, button is disabled");
			return;
		}
		switch (v.getId()) {
		case R.id.register_submit:
			if (checkAndSaveData() && rData != null) {// 检查表单是否合法
				try{
				sendRequest(SoapRes.URLCustomer,
						SoapRes.REQ_ID_UPDATE_USER_ALL_INFO,
						dataToProperties(rData), new BaseResponseHandler(this,
								DialogRes.DIALOG_SEND_REGISTER_REQUEST));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			break;
		case R.id.licenseNumber_edittext:
			Intent intent = new Intent(this,LicenseNumberEditActivity.class);
			intent.putExtra("licenumber", licenseNb.getText().toString());
			intent.putExtra("status", SharedPreferencesMgr.getLicenseNumberCheckStatus());
			intent.putExtra("checkInfo", SharedPreferencesMgr.getLicenseCheckInfo());
			//把证书号和照片传给下一个界面
			intent.putExtra("photoPath", SharedPreferencesMgr.getLicensePhoto());
			startActivityForResult(intent, editLicenseNumber);
			break;
		case R.id.hospital_edittext:
			if (mHospitalRegion.getSelectedItem().toString()
					.equals(getString(R.string.region))) {
				setInputNotFocusable();
				mHospitalRegion.requestFocus();
				mHospitalRegion.startAnimation(shake);
				return;
			}

			if (mHospitalCity.getSelectedItem().toString()
					.equals(getString(R.string.city))) {
				setInputNotFocusable();
				mHospitalCity.requestFocus();
				mHospitalCity.startAnimation(shake);
				return;
			}
			
			if(myimgpopupwindow != null && myimgpopupwindow.isShowing()) return;

			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.reg_hospital_select, null);
			myimgpopupwindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true);
			myimgpopupwindow.setAnimationStyle(R.style.mypopwindow_anim_style);
			myimgpopupwindow.showAtLocation(mainRlt, Gravity.CENTER, 0, 0);
			final EditText edtInput = (EditText) view
					.findViewById(R.id.edt_hospital_input);
			ListView lvHospitalList = (ListView) view.findViewById(R.id.lv_hs);
			TextView txt = (TextView) view.findViewById(R.id.txt_header_title);
			Button sumbit = (Button) view.findViewById(R.id.submit);
			sumbit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					hospitalInput.setText(edtInput.getText().toString());
					if (myimgpopupwindow.isShowing()) {
						myimgpopupwindow.dismiss();
					}
				}
			});

			txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

			txt.setText(getString(R.string.hospital));
			edtInput.addTextChangedListener(new MyTextWatcher(lvHospitalList));
			String defValue = hospitalInput.getText().toString();
			edtInput.setText(defValue);
			edtInput.setSelection(defValue.length());
			edtInput.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& event.getAction() == KeyEvent.ACTION_DOWN) {
						if (myimgpopupwindow.isShowing()) {
							myimgpopupwindow.dismiss();
						}
					}
					return false;
				}
			});
			lvHospitalList
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							hospitalInput
									.setText(hospitals.get(arg2).getName());
							if (myimgpopupwindow.isShowing()) {
								myimgpopupwindow.dismiss();
							}
						}
					});
			// TODO

			//
			// if (!hospitalInput.isFocusable()) {
			// setInputHasFocusable();
			// }
			// if (null != basicAdapter) {
			// if (hospitalInput.getText().toString().equals("")) {
			// hospitalInput.setAdapter(basicAdapter);
			// hospitalInput.showDropDown();
			// }
			// }
			break;
		}
	}
	

	/**
	 * 将数据转成soap参数
	 * 
	 * @param data
	 * @return
	 */
	private Properties dataToProperties(LoginEntity data) {
		Properties info = new Properties();
		info.put("gbUserName", data.getGbUserName());
		info.put("UserName", data.getUserName());
		info.put("Email", data.getEmail());
		info.put("ContactNumber", data.getContactNumber());
		info.put("LicenseNumber", data.getLicenseNumber());
		info.put("HospitalName", data.getHospitalName());
		info.put("HospitalRegion", data.getHospitalRegion());
		info.put("HospitalCity", data.getHospitalCity());
		info.put("bigDepartments", data.getBigDepartments());
		info.put("Department", data.getDepartment());
		info.put("job", data.getJob());
		info.put("professional_title", data.getProfessional_title());
		info.put("inviteCode", data.getInviteCode());
		info.setProperty("sign", "");
		//
		info.setProperty("fileBase64", fileBase64);
		info.setProperty("contentType", contentType);

		return info;
	}
	
	
	

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("myimgpopupwindow   dismiss1");
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {

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
			if (list != null) {
				mSubDepartments = new ArrayList<Department>(1);
				mSubDepartments.add(new Department(
						getString(R.string.departments)));
				mSubDepartments.addAll(list);
			}
			ArrayAdapter<Department> adapter1 = new ArrayAdapter<Department>(
					this, R.layout.gba_spinner_item, mSubDepartments);
			adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSubDepartment.setAdapter(adapter1);
			if (null != rData.getDepartment()
					&& !"".equals(rData.getDepartment())) {
				mSubDepartment.setSelection(adapter1
						.getPosition(new Department(rData.getDepartment())),
						true);
			}
			break;
		case R.id.region_spn:// 医院所在省份
			ArrayAdapter<HospitalCity> adapter;
			if (mRegions.get(position).equals(getString(R.string.region))) {
				setInputNotFocusable();
				cities = new ArrayList<HospitalCity>();
				cities.add(new HospitalCity(context.getString(R.string.city)));
				adapter = new ArrayAdapter<HospitalCity>(this,
						R.layout.gba_spinner_item, cities);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mHospitalCity.setAdapter(adapter);
			} else {
				List<HospitalCity> cityList = daoSession
						.getHospitalCityDao()
						.queryBuilder()
						.where(HospitalCityDao.Properties.ProvinceName
								.eq(mRegions.get(position).toString())).list();
				if (null != cityList) {
					cities = new ArrayList<HospitalCity>();
					cities.add(new HospitalCity(context
							.getString(R.string.city)));
					cities.addAll(cityList);
				}
			}

			adapter = new ArrayAdapter<HospitalCity>(this,
					R.layout.gba_spinner_item, cities);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mHospitalCity.setAdapter(adapter);

			if (null != rData.getHospitalCity()
					&& !"".equals(rData.getHospitalCity().trim()))
				mHospitalCity.setSelection(adapter
						.getPosition(new HospitalCity(rData.getHospitalCity()
								.trim())));

			break;
		case R.id.city_spn:// 医院所在城市
			if (cities.get(position).equals(getString(R.string.city))) {
				setInputNotFocusable();
				return;
			}
			break;
			
		case R.id.job_spn:// 身份
			if(position < 1){
				ArrayAdapter<String> proAdapter = new ArrayAdapter<String>(this,
						R.layout.gba_spinner_item, new String[]{""});
				proAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mProfileSpinner.setAdapter(proAdapter);
				mProfileSpinner.setClickable(false);
			}else{
				String [] arr = getStringArr(jobArrIds[position-1]+(Util.isZh()?"_zh":"_en"));
				ArrayAdapter<String> proAdapter = new ArrayAdapter<String>(this,
						R.layout.gba_spinner_item, arr);
				proAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mProfileSpinner.setAdapter(proAdapter);
				if(arr.length != 0 && arr != null && arr[0] !="" && arr[0] != null){
					mProfileSpinner.setClickable(true);
				}else{
					mProfileSpinner.setClickable(false);
				}
				if (!TextUtils.isEmpty(rData.getProfessional_title())){
					mProfileSpinner.setSelection(getIndex(rData.getProfessional_title(), arr), true);
				}
			}
			
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private class MyTextWatcher implements TextWatcher {
		private ListView lv;

		private ArrayAdapter<Hospital> baseAdapter;
		private List<Hospital> baseHospitalList;

		public MyTextWatcher(ListView lv) {
			this.lv = lv;
			baseHospitalList = daoSession.getHospitalDao().queryDeep(
					mHospitalRegion.getSelectedItem().toString(),
					mHospitalCity.getSelectedItem().toString(), null);
			hospitals = baseHospitalList;
			baseAdapter = new ArrayAdapter<Hospital>(
					Registration_updateActivity.this, R.layout.list_item_text,
					baseHospitalList);
			lv.setAdapter(baseAdapter);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (!"".equals(s.toString())) {
				hospitals = daoSession.getHospitalDao().queryDeep(
						mHospitalRegion.getSelectedItem().toString(),
						mHospitalCity.getSelectedItem().toString(),
						s.toString());
				if (hospitals != null) {
					ArrayAdapter<Hospital> adapter = new ArrayAdapter<Hospital>(
							Registration_updateActivity.this,
							R.layout.list_item_text, hospitals);
					lv.setAdapter(adapter);
				} else {
					lv.setAdapter(null);
				}
			} else {
				hospitals = baseHospitalList;
				lv.setAdapter(baseAdapter);
			}

		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}

	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// /**
	// * 检查用户输入是否正确
	// *
	// * @return
	// */
	// private boolean checkAndSaveData() {
	//
	// String string;
	// if (mNameEdit.getText().toString().equals("")) {
	// mNameEdit.requestLayout();
	// mNameEdit.requestFocus();
	// mNameEdit.startAnimation(shake);
	// return false;
	// }
	//
	// if (mEMail.getText().toString().equals("")) {
	// mEMail.requestLayout();
	// mEMail.requestFocus();
	// mEMail.startAnimation(shake);
	// return false;
	// }
	//
	// if (!isEmail(mEMail.getText().toString())) {
	// string = getString(R.string.check_email_address);
	// Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	// return false;
	// }
	//
	// if (!mContactNb.getText().toString().trim().equals("")) {
	// if (!isPhoneNumber(mContactNb.getText().toString().trim())) {
	// string = getString(R.string.isNotPhoneNumber);
	// Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	// return false;
	// }
	// }
	//
	// if (!licenseNb.getText().toString().trim().equals("")) {
	// if (licenseNb.getText().toString().trim().length() != 15) {
	// string = getString(R.string.license_number)
	// + getString(R.string.must_be_15);
	// Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	// return false;
	// }
	// }
	//
	// if (mHospitalRegion.getSelectedItem().toString()
	// .equals(getString(R.string.region))) {
	// mHospitalRegion.startAnimation(shake);
	// return false;
	// }
	//
	// if (mHospitalCity.getSelectedItem().toString()
	// .equals(getString(R.string.city))) {
	// mHospitalRegion.startAnimation(shake);
	// return false;
	// }
	//
	// if (hospitalInput.getText().toString().equals("")) {
	// hospitalInput.requestLayout();
	// hospitalInput.requestFocus();
	// hospitalInput.startAnimation(shake);
	// return false;
	// }
	//
	// if (!checkEditText(hospitalInput)) {
	// return false;
	// }
	//
	// if (mBigDepartment.getSelectedItem().toString()
	// .equals(getString(R.string.category))) {
	// mBigDepartment.requestFocus();
	// mBigDepartment.startAnimation(shake);
	// return false;
	// }
	//
	// if (mSubDepartment.getSelectedItem().toString()
	// .equals(getString(R.string.departments))) {
	// mSubDepartment.requestFocus();
	// mSubDepartment.startAnimation(shake);
	// return false;
	// }
	//
	// if (mProfileSpinner.getSelectedItem().toString()
	// .equals(getString(R.string.proftitle))) {
	// mProfileSpinner.requestFocus();
	// mProfileSpinner.startAnimation(shake);
	// return false;
	// }
	//
	// rData.setUserName(mNameEdit.getText().toString().trim());
	// rData.setContactNumber(mContactNb.getText().toString().trim());
	// rData.setEmail(mEMail.getText().toString().trim());
	// rData.setLicenseNumber(licenseNb.getText().toString().trim());
	// rData.setHospitalRegion(mHospitalRegion.getSelectedItem().toString());
	// rData.setHospitalCity(mHospitalCity.getSelectedItem().toString());
	// rData.setHospitalName(hospitalInput.getText().toString().trim());
	// rData.setInviteCode(inviteCode.getText().toString().trim());
	//
	// rData.setBigDepartments(mBigDepartment.getSelectedItem().toString());
	// rData.setDepartment(mSubDepartment.getSelectedItem().toString());
	// rData.setProfessional_title(profTitles[mProfileSpinner
	// .getSelectedItemPosition()]);
	//
	// return true;
	// }
}