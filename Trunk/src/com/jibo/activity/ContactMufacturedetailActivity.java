package com.jibo.activity;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.common.Constant;
import com.jibo.dao.ContactManufutureDao;
import com.jibo.dbhelper.HistoryAdapter;
import com.jibo.entity.ContactManufuture;
import com.jibo.entity.ManufutureBrandInfo;
import com.jibo.util.tips.Mask;
import com.jibo.util.tips.TipHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContactMufacturedetailActivity extends BaseSearchActivity
		implements OnClickListener {
	LinearLayout telsBodyLayout, phonesBodyLayout, emailBodyLayout;
	ImageView emailView = null, telView, lilyicon;
	ImageView phoneView = null;
	TextView tvt = null;
	TextView brandnametv = null, prointro_dtl = null, spinpos_dtl = null,
			spintro_dtl = null, wrktime_dtl = null, specplace_dtl = null,
			spec_name_dtl = null, spec_engname_dtl = null,
			spec_telphone_dtl = null, spec_phone_dtl = null,
			spec_email_dtl = null;
	private boolean isZh;
	private GBApplication app;
	private HistoryAdapter historyAdapter;

	private ManufutureBrandInfo brand;
	private ContactManufuture manufuture;

	private String drugName;
	private Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contactmanufacturedetail);
		super.onCreate(savedInstanceState);
		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");
		app = (GBApplication) getApplication();
		initUI();
		isZh = Locale.getDefault().getLanguage().contains("zh");

		Intent intent = getIntent();
		String brandId = intent.getStringExtra("brandId");
		manufuture = (ContactManufuture) intent
				.getParcelableExtra("manufuture");

		if (manufuture == null) {
			List<ContactManufuture> list = daoSession.getContactManufutureDao()
					.queryBuilder()
					.where(ContactManufutureDao.Properties.BrandId.eq(brandId))
					.list();
			if (null != list && list.size() > 0)
				manufuture = list.get(0);
		}
		manufuture.__setDaoSession(daoSession);
		brand = manufuture.getManufutureBrandInfo();

		String iconPath = Constant.DATA_PATH_Mufacture_doc + "/drug_"
				+ brand.getBrandId() + ".png";
		bitmap = BitmapFactory.decodeFile(iconPath);
		if (bitmap != null)
			lilyicon.setImageBitmap(bitmap);

		String manuFacturerName = null;
		if (isZh) {
			drugName = brand.getGeneralName();
			manuFacturerName = brand.getManufacturerNameCn();
		} else {
			drugName = brand.getEnName();
			manuFacturerName = brand.getManufacturerNameEn();
		}
		brandnametv.setText(drugName);// 对应的药品名
		prointro_dtl.setText(manuFacturerName);
		spinpos_dtl.setText(manufuture.getPosition());
		spec_name_dtl.setText(manufuture.getChinesename());
		spec_telphone_dtl.setText(manufuture.getTelphone());
		spec_email_dtl.setText(manufuture.getEmail());
	}
	
	private void initUI() {
		tvt = (TextView) findViewById(R.id.txt_header_title);
		tvt.setText(R.string.contactMufacture);
		telView = (ImageView) findViewById(R.id.tel);
		// phoneView = (ImageView) findViewById(R.id.phone);
		emailView = (ImageView) findViewById(R.id.email);
		lilyicon = (ImageView) findViewById(R.id.lilyicon);
		brandnametv = (TextView) findViewById(R.id.brandname);
		telsBodyLayout = (LinearLayout) findViewById(R.id.telsBodyLayout);
		// phonesBodyLayout=(LinearLayout) findViewById(R.id.phonesBodyLayout);
		emailBodyLayout = (LinearLayout) findViewById(R.id.emailBodyLayout);
		telsBodyLayout.setOnClickListener(this);
		// phonesBodyLayout.setOnClickListener(this);
		emailBodyLayout.setOnClickListener(this);
		prointro_dtl = (TextView) findViewById(R.id.prointro_dtl);
		spinpos_dtl = (TextView) findViewById(R.id.spinpos_dtl);
		// spintro_dtl = (TextView) findViewById(R.id.spintro_dtl);
		spec_name_dtl = (TextView) findViewById(R.id.spec_name_dtl);
		// wrktime_dtl = (TextView) findViewById(R.id.wrktime_dtl);
		// specplace_dtl = (TextView) findViewById(R.id.specplace_dtl);
		// spec_engname_dtl = (TextView) findViewById(R.id.spec_engname_dtl);
		spec_telphone_dtl = (TextView) findViewById(R.id.spec_telphone_dtl);
		// spec_phone_dtl = (TextView) findViewById(R.id.spec_phone_dtl);
		spec_email_dtl = (TextView) findViewById(R.id.spec_email_dtl);
		telView.setOnClickListener(this);
		// phoneView.setOnClickListener(this);
		emailView.setOnClickListener(this);
		findViewById(R.id.drugdtl).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.telsBodyLayout:
			Uri uri = Uri
					.parse("tel:" + spec_telphone_dtl.getText().toString());
			Intent it = new Intent(Intent.ACTION_DIAL, uri);
			startActivity(it);
			break;
		case R.id.emailBodyLayout:
			try {
				Uri u = Uri.parse("mailto:"
						+ spec_email_dtl.getText().toString());
				Intent inn = new Intent(Intent.ACTION_SENDTO, u);
				startActivity(inn);
			} catch (Exception e) {
				Toast toast = Toast.makeText(context,
						context.getString(R.string.emailerror),
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 100);
				toast.show();
				e.printStackTrace();
			}
			break;
		case R.id.drugdtl:
			Intent in = new Intent(ContactMufacturedetailActivity.this,
					NewDrugReferenceActivity.class);
			// in.putExtra("brandInfo",brandEntity);
			in.putExtra("brandId", brand.getBrandId());
			in.putExtra("mode", 2);
			int colID = -1;
			for (Entry<Integer, String> e : app.getPdaColumnMap().entrySet()) {
				if (e.getValue().toString().equals(getString(R.string.drug))) {
					colID = (Integer) e.getKey();
				}
			}
			System.out.println("brandId   **&&&   " + brand.getBrandId());
			historyAdapter.storeViewHistory(app.getLogin().getGbUserName(),
					Integer.parseInt(brand.getProductId()), colID, -1, "C2-"
							+ drugName);
			startActivity(in);
			break;
		}
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	@SuppressWarnings("unused")
	private Mask mask;
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mask = (Mask) findViewById(R.id.mask);
		mask = new Mask(this, null);
		TipHelper.registerTips(this, 1);
		TipHelper.runSegments(this);
		this.findViewById(R.id.closeTips).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TipHelper.sign(false, true);
				TipHelper.disableTipViewOnScreenVisibility();
			}
		});
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
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
	}

}