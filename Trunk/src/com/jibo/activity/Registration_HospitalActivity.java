package com.jibo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.common.DialogRes;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.common.Util;
import com.jibo.dao.DBFactory;
import com.jibo.dao.DaoManager;
import com.jibo.dao.HospitalCityDao;
import com.jibo.data.RegistrationUserInfo_hospitalPaser;
import com.jibo.data.UpdateUserAllInfoPaser;
import com.jibo.data.entity.AdvertisingEntity;
import com.jibo.data.entity.LoginEntity;
import com.jibo.dbhelper.HospitalDBAdapter;
import com.jibo.dbhelper.LoginSQLAdapter;
import com.jibo.entity.Hospital;
import com.jibo.entity.HospitalCity;
import com.jibo.entity.HospitalProvince;
import com.jibo.net.BaseResponseHandler;
import com.umeng.analytics.MobclickAgent;

/**
 * 注册后填写医院信息和邀请码
 * 
 * @author simon
 * 
 */
public class Registration_HospitalActivity extends BaseActivity implements
		OnClickListener, OnItemSelectedListener {

	/** UI */
	private Spinner mHospitalRegion;
	private Spinner mHospitalCity;
	private Spinner mJob;
	private Spinner mProtitle;
	private AutoCompleteTextView hospitalInput;
	private EditText invitationCodeInput;
	private Button slipBtn;// 跳过
	private Button submitBtn;// 确认
	private PopupWindow myimgpopupwindow;
	private RelativeLayout mainRlt;

	private Context context;

	private List<HospitalProvince> mRegions;
	private List<HospitalCity> cities;
	private List<Hospital> hospitals;

	private String provinceName;
	private String cityName;

	private Properties info;
	private Animation shake;
	
	private HospitalDBAdapter hospitalDBAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registration_hospital);
		context = this;
		hospitalDBAdapter = new HospitalDBAdapter(context);
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		parse(getIntent().getStringExtra("placeName"));

		// init UI
		try {
			initPlaceSpinner();
			initProTitleSpinner();
		} catch (Exception e) {
			e.printStackTrace();
		}

		hospitalInput = (AutoCompleteTextView) findViewById(R.id.hospital_edittext);
		submitBtn = (Button) findViewById(R.id.register_submit);
		slipBtn = (Button) findViewById(R.id.slip_submit);
		mainRlt = (RelativeLayout) findViewById(R.id.mai);

		invitationCodeInput = (EditText) findViewById(R.id.invitationCode_edittext);
		invitationCodeInput
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });

		// registration listener
		// hospitalInput.addTextChangedListener(new MyTextWatcher());
		hospitalInput.setOnClickListener(this);
		// hospitalInput.setOnTouchListener(new MyTouchListener());
		hospitalInput.setThreshold(1);

		submitBtn.setOnClickListener(this);
		slipBtn.setOnClickListener(this);
	}

	/**
	 * 解析用户ip地址
	 * 
	 * @param placeName
	 */
	private void parse(String placeName) {

		if (null == placeName || "".equals(placeName))
			return;

		ArrayList<String> regionCitys = new ArrayList<String>();
		regionCitys.add("上海");
		regionCitys.add("北京");
		regionCitys.add("天津");
		regionCitys.add("重庆");

		String provinceTag = "省";
		String cityTag = "市";
		String districtTag = "区";

		boolean isHasProvince = false;
		boolean isHasCity = false;

		if (placeName.contains(provinceTag)) {
			provinceName = placeName.substring(0,
					placeName.indexOf(provinceTag));
			isHasProvince = true;
		}

		if (placeName.contains(cityTag)) {
			if (isHasProvince) {
				cityName = placeName.substring(
						placeName.indexOf(provinceTag) + 1,
						placeName.indexOf(cityTag));
			} else {
				cityName = placeName.substring(0, placeName.indexOf(cityTag));
				if (regionCitys.contains(cityName)) {
					provinceName = cityName;
					if (placeName.contains(districtTag)) {
						cityName = placeName.substring(
								placeName.indexOf(cityTag) + 1,
								placeName.indexOf(districtTag) + 1);
					}
				}
			}
			isHasCity = true;
		}

		if (!isHasProvince && !isHasCity) {
			if (regionCitys.contains(placeName))
				provinceName = placeName;
		}
	}

	@Override
	public void onClick(View v) {
		if (!v.isEnabled()) {
			Log.e("simon", "on click error, button is disabled");
			return;
		}
		switch (v.getId()) {
		case R.id.register_submit:// 确认
			if (saveRegistrationZeroData()) {// 信息填写正确
				sendRequest(SoapRes.URLCustomer,
						SoapRes.REQ_ID_UPDATE_USER_ALL_INFO, info,
						new BaseResponseHandler(this,
								DialogRes.DIALOG_SEND_REGISTER_REQUEST));
			}
			break;
		case R.id.slip_submit:// 跳过
			uploadLoginLogNew("LRBtnClick", "", "RSkipBtn", null);
			MobclickAgent.onEvent(this, "LRBtnClick", "RSkipBtn", 1);
			// 跳转到免责申明
			SharedPreferencesMgr.setCity("");
			SharedPreferencesMgr.setHospitalName("");
			SharedPreferencesMgr.setInviteCode("");
			startActivity(new Intent().setClass(this, DisclaimerActivity.class));
			finish();
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
			break;
		default:
			break;
		}
	}

	private void setInputNotFocusable() {
		hospitalInput.setFocusable(false);
		hospitalInput.setLongClickable(false);
		hospitalInput.setFocusableInTouchMode(false);
	}

	/**
	 * 初始化医院所在地址spinner
	 * 
	 * @throws Exception
	 */
	private void initPlaceSpinner() throws Exception {
		mHospitalRegion = (Spinner) findViewById(R.id.region_spn);
		mHospitalCity = (Spinner) findViewById(R.id.city_spn);

		mRegions = new ArrayList<HospitalProvince>();
		mRegions.add(new HospitalProvince(context.getString(R.string.region)));

//		List<HospitalProvince> provinceList = daoSession
//				.getHospitalProvinceDao().loadAll();
		List<HospitalProvince> provinceList = hospitalDBAdapter.getProvince();
		if (null != provinceList) {
			mRegions.addAll(provinceList);
		}

		ArrayAdapter<HospitalProvince> adapter = new ArrayAdapter<HospitalProvince>(
				this, R.layout.gba_spinner_item, mRegions);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mHospitalRegion.setAdapter(adapter);
		mHospitalRegion.setOnItemSelectedListener(this);

		cities = new ArrayList<HospitalCity>();
		cities.add(new HospitalCity(context.getString(R.string.city)));
		ArrayAdapter<HospitalCity> adapter1 = new ArrayAdapter<HospitalCity>(
				this, R.layout.gba_spinner_item, cities);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mHospitalCity.setAdapter(adapter1);
		mHospitalCity.setOnItemSelectedListener(this);

	}

	private String[] jobArrIds;

	/**
	 * 初始化身份和职称
	 * 
	 * @throws Exception
	 */
	private void initProTitleSpinner() throws Exception {
		mJob = (Spinner) findViewById(R.id.job_spn);
		mProtitle = (Spinner) findViewById(R.id.proftitle_spn);

		jobArrIds = getResources().getStringArray(R.array.job_arr_id);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.gba_spinner_item, getResources().getStringArray(
						R.array.job_arr));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mJob.setAdapter(adapter);
		mJob.setOnItemSelectedListener(this);

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				R.layout.gba_spinner_item, getStringArr(jobArrIds[0]
						+ (Util.isZh() ? "_zh" : "_en")));
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mProtitle.setAdapter(adapter1);
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

	/**
	 * 检查表单是否符合规则
	 * 
	 * @return
	 */
	private boolean saveRegistrationZeroData() {
		info = new Properties();

		if (mHospitalRegion.getSelectedItem().toString()
				.equals(getString(R.string.region))) {
			mHospitalRegion.requestFocus();
			mHospitalRegion.startAnimation(shake);
			return false;
		}

		if (mHospitalCity.getSelectedItem().toString()
				.equals(getString(R.string.city))) {
			mHospitalCity.requestFocus();
			mHospitalCity.startAnimation(shake);
			return false;
		}

		if (hospitalInput.getText().toString().equals("")) {
			hospitalInput.requestFocus();
			hospitalInput.startAnimation(shake);
			return false;
		}

		if (!checkEditText(hospitalInput)) {
			return false;
		}

		// info.setProperty("userName", userName);
		info.setProperty("fileBase64", "");
		info.setProperty("contentType", "");
		info.setProperty("gbUserName", SharedPreferencesMgr.getUserName());
		info.setProperty("UserName", SharedPreferencesMgr.getName());
		info.setProperty("Email", SharedPreferencesMgr.getEmail());
		info.setProperty("ContactNumber", SharedPreferencesMgr.getContactNb());
		info.setProperty("LicenseNumber",
				SharedPreferencesMgr.getLicenseNumber());
		info.setProperty("HospitalRegion", mHospitalRegion.getSelectedItem()
				.toString());
		info.setProperty("HospitalCity", mHospitalCity.getSelectedItem()
				.toString());
		info.setProperty("HospitalName", hospitalInput.getText().toString()
				.trim());
		info.setProperty("bigDepartments",
				SharedPreferencesMgr.getBigDepartments());
		info.setProperty("Department", SharedPreferencesMgr.getDept());

		info.setProperty("job", mJob.getSelectedItem().toString());
		info.setProperty("professional_title", mProtitle.getSelectedItem()
				.toString());

		info.setProperty("inviteCode", invitationCodeInput.getText().toString()
				.trim());
		info.setProperty("sign", "");
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
			if (o instanceof UpdateUserAllInfoPaser) {// 注册
				UpdateUserAllInfoPaser obj = (UpdateUserAllInfoPaser) o;
				LoginEntity entity = obj.getLoginEntity();
				if (obj.isSuccess()) {
					uploadLoginLogNew("LRBtnClick", "", "RCommit2Btn", null);
					MobclickAgent.onEvent(this, "LRBtnClick", "RCommit2Btn", 1);

					saveLoginInfoToLocal(entity);// 保存用户信息

					// 跳转到免责申明
					LoginSQLAdapter dbHelper = new LoginSQLAdapter(context);
					String imagePath = entity.getImagePath();
					if (null != imagePath && !"".equals(imagePath)) {// 下载广告图片
						dbHelper.insertAdvertising(new AdvertisingEntity(entity
								.getGbUserName(), "", imagePath));
					}

					startActivity(new Intent().setClass(this,
							DisclaimerActivity.class));
				} else {
					Toast.makeText(this, obj.getErrorMsg(), Toast.LENGTH_LONG)
							.show();
				}
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.region_spn:// 医院所在省份
			if (mRegions.get(position).equals(getString(R.string.region))) {
				cities = new ArrayList<HospitalCity>();
				cities.add(new HospitalCity(context.getString(R.string.city)));
				ArrayAdapter<HospitalCity> adapter = new ArrayAdapter<HospitalCity>(
						this, R.layout.gba_spinner_item, cities);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mHospitalCity.setAdapter(adapter);
			} else {
//				List<HospitalCity> cityList = daoSession
//						.getHospitalCityDao()
//						.queryBuilder()
//						.where(HospitalCityDao.Properties.ProvinceName
//								.eq(mRegions.get(position).toString())).list();
				List<HospitalCity> cityList = hospitalDBAdapter.getCityByProvince(mRegions.get(position).toString());
				if (null != cityList) {
					cities = new ArrayList<HospitalCity>();
					cities.add(new HospitalCity(context
							.getString(R.string.city)));
					cities.addAll(cityList);
				}
			}

			ArrayAdapter<HospitalCity> adapter = new ArrayAdapter<HospitalCity>(
					this, R.layout.gba_spinner_item, cities);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mHospitalCity.setAdapter(adapter);

			if (null != provinceName && !"".equals(provinceName)
					&& null != cityName && !"".equals(cityName))
				mHospitalCity.setSelection(adapter
						.getPosition(new HospitalCity(cityName)));
			break;

		case R.id.city_spn:// 医院所在城市
			if (cities.get(position).equals(getString(R.string.city))) {
				return;
			}
			break;

		case R.id.job_spn:// 身份
			if(position < 1){
				ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
						R.layout.gba_spinner_item, new String[]{""});
				adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mProtitle.setAdapter(adapter1);
				mProtitle.setClickable(false);
			}else{
				String[] arr = getStringArr(jobArrIds[position - 1]
						+ (Util.isZh() ? "_zh" : "_en"));
				ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
						R.layout.gba_spinner_item, arr);
				adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mProtitle.setAdapter(adapter1);
				if(arr.length != 0 && arr != null && arr[0] !="" && arr[0] != null){
					mProtitle.setClickable(true);
				}else{
					mProtitle.setClickable(false);
				}
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private class MyTextWatcher implements TextWatcher {
		private ListView lv;

		private ArrayAdapter<Hospital> baseAdapter;
		private List<Hospital> baseHospitalList;

		public MyTextWatcher(ListView lv) {
			this.lv = lv;
//			baseHospitalList = daoSession.getHospitalDao().queryDeep(
//					mHospitalRegion.getSelectedItem().toString(),
//					mHospitalCity.getSelectedItem().toString(), null);
			baseHospitalList = hospitalDBAdapter.getHospitalName(mHospitalRegion.getSelectedItem().toString(), 
					mHospitalCity.getSelectedItem().toString(),
					null);
			hospitals = baseHospitalList;
			baseAdapter = new ArrayAdapter<Hospital>(context,
					R.layout.list_item_text, baseHospitalList);
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
//				hospitals = daoSession.getHospitalDao().queryDeep(
//						mHospitalRegion.getSelectedItem().toString(),
//						mHospitalCity.getSelectedItem().toString(),
//						s.toString());
				hospitals = hospitalDBAdapter.getHospitalName(mHospitalRegion.getSelectedItem().toString(), 
						mHospitalCity.getSelectedItem().toString(),
						s.toString());
				if (hospitals != null) {
					ArrayAdapter<Hospital> adapter = new ArrayAdapter<Hospital>(
							context, R.layout.list_item_text, hospitals);
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
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

}
