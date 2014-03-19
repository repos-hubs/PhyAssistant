package com.jibo.activity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.api.android.GBApp.R;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.jibo.GBApplication;
import com.jibo.data.entity.TumorEntity;
import com.jibo.dbhelper.TNMAdapter;

public class TumorArticleActivity extends BaseSearchActivity implements OnClickListener
										, OnCheckedChangeListener{
	private ImageButton mConfirmButton;
	private ImageButton mClearButton;
	private TextView resultView;
	private TextView rankTitleView;
	private LinearLayout tumorLayout;
	private TNMAdapter tnmAdapter;
	private GBApplication application;
	private ArrayList<ArrayList<String>> vSignifcanceList;
	private ArrayList<TumorEntity> tumorList;
	private ArrayList<Integer> checkList;
	private String rank;
	private String rankTitle;
	private String cnTitle;
	private int totalCount;
	
	private ArrayList<String> noteList;
	private ArrayList<String> subcategoryList;
	private ArrayList<String> signList;
	private ArrayList<String> indexList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tumor_category);
		super.onCreate(savedInstanceState);
		inits();
	}

	@Override
	protected void onStop() {
		tnmAdapter.closeHelp();
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
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()) {
		case R.id.TumorConfirm:
			computeResult();
			break;
		case R.id.TumorClear:
			intent = new Intent(this, TumorArticleActivity.class);
			intent.putExtra("rank", rank);
			intent.putExtra("rankTitle", rankTitle);
			startActivity(intent);
			finish();
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.getId() > totalCount)
			return;
		if(isChecked)
			checkList.add(buttonView.getId());
		else
			checkList.remove(new Integer(buttonView.getId()));
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
		
		subcategoryList = new ArrayList<String>();
		indexList = new ArrayList<String>();
		signList = new ArrayList<String>();
		noteList = new ArrayList<String>();
		checkList = new ArrayList<Integer>();
		vSignifcanceList = new ArrayList<ArrayList<String>>();
		
		Intent intent = this.getIntent();
		rank = intent.getStringExtra("rank");
		rankTitle = intent.getStringExtra("rankTitle");
		
		rankTitleView.setText(rankTitle);
		txtCategory.setText(getString(R.string.tool));
		subcategoryList = tnmAdapter.getSubCategory(rank);
		int subCount = subcategoryList.size();
		
		tumorList = tnmAdapter.getSignAndSignificanceByRankAndSubCategory(rank, subcategoryList);
		noteList = tnmAdapter.getRankListCategory(rank);
		cnTitle = tnmAdapter.getTumorTitle(rank);
		
		for(TumorEntity en:tumorList) {
			vSignifcanceList.add(en.getSignificanceList());
			for(int i=0; i<en.getIndexList().size(); i++) {
				indexList.add(en.getIndexList().get(i));
				signList.add(en.getSignList().get(i));
			}
		}
		for(int i=0; i < subCount; i++){
			addCheckBox(subcategoryList.get(i), vSignifcanceList.get(i));
			addCuttingLine();
		}
	}

	private void addSubTitle(String label){
		TextView subTitle = new TextView(this);
		subTitle.setPadding(30, 10, 0, 0);
		subTitle.setTextSize(16);
		subTitle.setTypeface(null, Typeface.BOLD);
		subTitle.setTextColor(Color.BLACK);
		//label = label + getString(R.string.series);
		subTitle.setText(label);
		tumorLayout.addView(subTitle);
	}
	
	private void addCuttingLine() {
		ImageView cuttingLine = new ImageView(this);

		int fc = LinearLayout.LayoutParams.FILL_PARENT;
		int wc = LinearLayout.LayoutParams.WRAP_CONTENT;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(fc, wc);
		params.topMargin = 5;
		cuttingLine.setLayoutParams(params);
		cuttingLine.setImageResource(R.drawable.cutting_line);
		tumorLayout.addView(cuttingLine);
	}
	
	/**
	 * Adding CheckBox and TextView control dynamically.
	 * @param label  T,N,M
	 * @param count  T,N,M每个分类的种类数
	 * @param values 临床意义
	 */
	private void addCheckBox(String label, List<String> values){
		int count = values.size();
		if(count <= 0){
			return;
		}
		
		label = label + getString(R.string.series);
		addSubTitle(label);
		addCuttingLine();
		
		for(int i = totalCount; i < count + totalCount; i++){
			LinearLayout layout = new LinearLayout(this);
			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
										LayoutParams.WRAP_CONTENT));
			layout.setOrientation(LinearLayout.HORIZONTAL);
			layout.setPadding(30, 10, 0, 0);
			CheckBox cb = new CheckBox(this);
			cb.setId(i);
			cb.setOnCheckedChangeListener(this);
			layout.addView(cb);
			TextView subTitle1 = new TextView(this);
			subTitle1.setText(values.get(i-totalCount));
			subTitle1.setTypeface(null, Typeface.BOLD);
			subTitle1.setTextSize(16);
			subTitle1.setPadding(0, 0, 0, 20);
			subTitle1.setTextColor(Color.BLACK);
			layout.addView(subTitle1);
			tumorLayout.addView(layout);
		}
		totalCount = count + totalCount;
	}
	
	private void computeResult() {
		boolean inRankLevel = false;
		String preSub = null;
		String sub = null;
		String[] tempSign = new String[5];
		boolean equal = false;
		Collections.sort(checkList);
		
		for(int i=0; i < checkList.size(); i++){
			int id = checkList.get(i);
			sub = indexList.get(id);
			inRankLevel = tnmAdapter.isIndexInRankLevel(rank, indexList.get(id), signList.get(id));
			if(preSub != null){
				if(sub != null) {
					equal = sub.equals(preSub);
					if(equal || !inRankLevel){
						tempSign[Integer.parseInt(sub) - 2] = "anyone";
					}else{
						tempSign[Integer.parseInt(sub) - 2] = signList.get(id);
					}
				}
			}else{
				tempSign[Integer.parseInt(sub) - 2] = signList.get(id);
			}
			preSub = sub;
		}
		String sql = getSQLByIndex(rank, tempSign);
		String level = tnmAdapter.getLevelByRank(sql);
		String[] temp = new String[5];
		String[] anyones = new String[5];
		System.arraycopy(tempSign, 0, temp, 0, tempSign.length);
		if(level == null || level.length() <= 0){
			for(int i=0; i < tempSign.length; i++){
				boolean indexInRankLevel = false;
				if(tempSign[i] != null && tempSign[i].length() > 0)
					indexInRankLevel = tnmAdapter.getIndexInRankLevel(rank, String.valueOf(i));
				if(indexInRankLevel){
					temp[i] = "anyone";
					anyones[i] = "anyone";
					sql = getSQLByIndex(rank, temp);
					level = tnmAdapter.getLevelByRank(sql);
					if(level != null && level.length() > 0)
						break;
					System.arraycopy(tempSign, 0, temp, 0, tempSign.length);
				}
			}
		}
		
		if(level == null || level.length() <= 0){
			for(int i=0; i < tempSign.length; i++){
				boolean indexInRankLevel = false;
				if(tempSign[i] != null && tempSign[i].length() > 0)
					indexInRankLevel = tnmAdapter.getIndexInRankLevel(rank, String.valueOf(i));
				if(indexInRankLevel){
					temp[i] = "anyone";
					anyones[i] = "anyone";
					sql = getSQLByIndex(rank, temp);
					level = tnmAdapter.getLevelByRank(sql);
					if(level != null && level.length() > 0)
						break;
				}
			}
		}
		
		if(level == null || level.length() <= 0)
			level = getString(R.string.noranklevel);
		resultView.setText(getString(R.string.tumorCategory) + level);
	}
	
	public String getSQLByIndex(String rank, String[] values){
		 String sql = "select [level] from [ranklevel] where rank = '" + rank + "'";
		 String tempSql = sql;
		 
		 for(int i=0; i < values.length; i++){
			 if(values[i] != null){
				 if(values[i].equals("anyone")){
					 sql = sql + " and index"+ String.valueOf(i+1) + " LIKE '%" + values[i] +"%'";
				 }else{
					 sql = sql + " and index"+ String.valueOf(i+1) + "= '" + values[i] +"'";
				 }
			 }else{
				 sql = sql + " and index"+ String.valueOf(i+1) + " is null ";
			 }
		 }
			 
		 if(tempSql.equals(sql)){
			 sql = "select [level] from [ranklevel] where 1 <>1";
		 }
		 
		 return sql;
	 }
}
