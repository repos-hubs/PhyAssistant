package com.jibo.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.GBApplication;
import com.jibo.data.entity.TumorEntity1;
import com.jibo.dbhelper.TNMAdapter;

public class TumorArticleActivity1 extends BaseSearchActivity implements OnClickListener{
	private GBApplication application;
	private TNMAdapter tnmAdapter;
	private ImageButton mConfirmButton;
	private ImageButton mClearButton;
	private TextView resultView;
	private TextView rankTitleView;
	private LinearLayout tumorLayout;
	private String rank;
	private String rankTitle;
	private ArrayList<TumorEntity1> tumorList;
	private TreeMap<Integer, TumorEntity1> tMap;
	private ArrayList<RadioGroup> rgList;
	private String subCategoryFlag = "";
	private int rg_id = 0x1234;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tumor_category);
		inits();
		createItem();
		super.onCreate(savedInstanceState);
	}
	
	private void inits() {
		application = (GBApplication)this.getApplication();
		tnmAdapter = new TNMAdapter(this, 1);
		mConfirmButton = (ImageButton) findViewById(R.id.TumorConfirm);
		mClearButton   = (ImageButton) findViewById(R.id.TumorClear);
		resultView 	   = (TextView) findViewById(R.id.TumorResult);
		rankTitleView  = (TextView) findViewById(R.id.sub_title);
		tumorLayout = (LinearLayout)findViewById(R.id.tumorCategoryLayout);
		TextView txtCategory = (TextView) findViewById(R.id.txt_header_title);
		
		mConfirmButton.setOnClickListener(this);
		mClearButton.setOnClickListener(this);
		tumorList = new ArrayList<TumorEntity1>();
		rgList = new ArrayList<RadioGroup>();
		
		txtCategory.setText(getString(R.string.tool));
		Intent intent = this.getIntent();
		rank = intent.getStringExtra("rank");
		rankTitle = intent.getStringExtra("rankTitle");
		rankTitleView.setText(rankTitle);
		
		tumorList = tnmAdapter.getTumorInfo(rank);
	}

	private void createItem() {
		RadioGroup rg = null;
		LinearLayout llt = null;
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams rbLP = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		rbLP.gravity = Gravity.TOP;
		rbLP.setMargins(0, 8, 0, 8);
		lp.setMargins(0, 0, 0, (int) (15*application.getDeviceInfo().getScale()));
		for(int i=0; i<tumorList.size(); i++) {
			TumorEntity1 ent = tumorList.get(i);
			if(subCategoryFlag.equals(ent.getSub_category())) {
				if(!"Note".equals(ent.getSub_category())) {
					RadioButton rb = new RadioButton(this);
					rb.setText(ent.getSub_category_sign()+"  "+ent.getSignificance());
					rb.setTextColor(Color.BLACK);
					rb.setTag(ent.getSub_category_sign());
					rb.setGravity(Gravity.TOP);
					rg.addView(rb, rbLP);
				}else {
					if(rg!=null) {
						rgList.add(rg);
						llt = new LinearLayout(this);
						llt.setBackgroundResource(R.drawable.academic_profile_content);
						llt.addView(rg);
						tumorLayout.addView(llt, lp);
						rg = null;
					}
					TextView txt1 = new TextView(this);
					txt1.setTextColor(Color.BLACK);
					txt1.setText(ent.getSignificance());
					llt = new LinearLayout(this);
					llt.setBackgroundResource(R.drawable.academic_profile_content);
					llt.addView(txt1);
					tumorLayout.addView(llt, lp);
				}
			} else {
				if("Note".equals(ent.getSub_category())) {
					if(rg!=null) {
						rgList.add(rg);
						llt = new LinearLayout(this);
						llt.setBackgroundResource(R.drawable.academic_profile_content);
						llt.addView(rg);
						tumorLayout.addView(llt, lp);
						rg = null;
					}
					TextView txt1 = new TextView(this);
					txt1.setTextColor(Color.BLACK);
					txt1.setText(ent.getSignificance());
					llt = new LinearLayout(this);
					llt.setBackgroundResource(R.drawable.academic_profile_content);
					llt.addView(txt1);
					tumorLayout.addView(llt, lp);
				} else {
					if(rg!=null) {
						rgList.add(rg);
						llt.addView(rg);
						tumorLayout.addView(llt, lp);
						rg = null;
					}
					llt = new LinearLayout(this);
					llt.setBackgroundResource(R.drawable.academic_profile_content);
					rg = new RadioGroup(this);
					
					rg.setGravity(Gravity.TOP);
					RGListener rgListener = new RGListener();
					rg.setOnCheckedChangeListener(rgListener);
					TextView txt = new TextView(this);
					txt.setTextColor(Color.BLACK);
					txt.setText(ent.getSub_category()+getString(R.string.series));
					txt.setTextSize(application.getDeviceInfo().getScreenWidth()/22);
					
					ImageView img = new ImageView(this);
					img.setBackgroundResource(R.drawable.cutting_line);
					tumorLayout.addView(txt);
//					tumorLayout.addView(img);
					RadioButton rb = new RadioButton(this);
					rb.setText(ent.getSub_category_sign()+"  "+ent.getSignificance());
					rb.setTextColor(Color.BLACK);
					rb.setTag(ent.getSub_category_sign());
					rg.setTag(rb.getTag());
					rgListener.onCheckedChanged(rg, 0);
					rb.setGravity(Gravity.TOP);
					rg.addView(rb, rbLP);
					
				}
				subCategoryFlag = ent.getSub_category();
			}
		}
		if(rg!=null) {
			rgList.add(rg);
			
			llt.addView(rg);
			tumorLayout.addView(llt);
		}
	}

	private class RGListener implements OnCheckedChangeListener {
		ArrayList<RadioGroup> rgList1 = new ArrayList<RadioGroup>();
		
		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			
			RadioButton rb = (RadioButton) arg0.findViewById(arg1);
			boolean isEmpty = false;
			for(RadioGroup rg:rgList) {
				if(rg.getCheckedRadioButtonId() == -1) {
					isEmpty = true;
					break;
				}
			}
			rgList1 = rgList;
			Iterator<RadioGroup> it = rgList1.iterator();
			if (null != rb){
				while (it.hasNext()){
					RadioGroup rg1 = it.next();
					if (rg1.getCheckedRadioButtonId() == arg1){
						rg1.setTag(rb.getTag());
					}
				}
			}
			
			if(rb != null&&!isEmpty) {
//				arg0.setTag(rb.getTag());
				String str = getString(R.string.tumorCategory);
				resultView.setText(str+tnmAdapter.getResult(rgList1, rank));
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.TumorConfirm:
			String str = getString(R.string.tumorCategory);
			resultView.setText(str+tnmAdapter.getResult(rgList, rank));
			break;
		case R.id.TumorClear:
			for(RadioGroup rg:rgList) {
				rg.clearCheck();
			}
			String str1 = getString(R.string.tumorCategory);
			resultView.setText(str1);
			break;
		}
	}
}
