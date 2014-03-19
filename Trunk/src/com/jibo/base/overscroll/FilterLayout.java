package com.jibo.base.overscroll;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.api.android.GBApp.R;
import com.jibo.base.src.request.impl.db.SqliteAdapterCentre;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.dao.DBFactory;
import com.jibo.ui.ButtonGroup;
import com.jibo.ui.MySeek;
import com.jibo.util.Logs;
import com.jibo.util.SharedPreferenceUtil;
import com.umeng.analytics.MobclickAgent;

public class FilterLayout extends FrameLayout implements
		SeekBar.OnSeekBarChangeListener, OnClickListener {
	private String pullLabel;
	private String releaseLabel;
	private LayoutInflater inflater;
	private ViewGroup header;
	private Context context;

	private ButtonGroup sourceBtnGroup, departmentBtnGroup, clinicalBtnGroup,
			freeBtnGroup, abstractsBtnGroup;
	private TextView ifText;
	private MySeek seekBar;
	private Button doneBtn;
	private Button addBtn, reduceBtn;
	private LinearLayout browseLayout;
	private EditText calendarEditText, searchKeyword;
	private Spinner spinner1, spinner2, spinner3, spinner4, spinner5, spinner6;
	private LinearLayout kw1Layout, kw2Layout, kw3Layout;
	private LinearLayout reduce1Layout, reduce2Layout, reduce3Layout;

	private SharedPreferences filterInfo;
	private boolean isfromBrowse = true;
	private static int sourceIndex = 0, clinicalIndex = 0, departmentIndex = 1,
			ifCount = 0, freeIndex = 1, abstractsIndex = 0;
	private String searchKey = "";
	private int keyWordCount = 0;
	private ArrayAdapter<CharSequence> adapter1, adapter2;

	public FilterLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		inits();
	}

	public FilterLayout(Context context, final int mode, String releaseLabel,
			String pullLabel, String refreshingLabel) {
		super(context);
		this.context = context;
		this.releaseLabel = releaseLabel;
		this.pullLabel = pullLabel;

		inits();

		switch (mode) {
		case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
			break;
		case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
			break;
		default:
			break;
		}
	}

	private void inits() {
		inflater = LayoutInflater.from(context);
		header = (ViewGroup) inflater.inflate(R.layout.filter_header, this);

		browseLayout = (LinearLayout) findViewById(R.id.browse_layout);
		// if(context instanceof ResearchPageActivityTest){
		// isfromBrowse = true;
		// browseLayout.setVisibility(View.VISIBLE);
		// }else{
		// isfromBrowse = false;
		// browseLayout.setVisibility(View.GONE);
		// }

		filterInfo = context.getSharedPreferences("filter_info", 0);
		loadLocalSettings();

		sourceBtnGroup = (ButtonGroup) findViewById(R.id.source);
		sourceBtnGroup.setBtnCount(3, sourceIndex);
		sourceBtnGroup.setText(R.id.btn_left,
				getResources().getString(R.string.source_all));
		sourceBtnGroup.setText(R.id.btn_right,
				getResources().getString(R.string.source_cnki));
		sourceBtnGroup.setText(R.id.btn_middle_01, getResources()
				.getString(R.string.source_pubmed));

		departmentBtnGroup = (ButtonGroup) findViewById(R.id.research);
		departmentBtnGroup.setBtnCount(2, departmentIndex);
		departmentBtnGroup.setText(R.id.btn_left,
				getResources().getString(R.string.research_all));
		departmentBtnGroup.setText(R.id.btn_right, getResources()
				.getString(R.string.research_self));

		clinicalBtnGroup = (ButtonGroup) findViewById(R.id.clinical);
		clinicalBtnGroup.setBtnCount(3, clinicalIndex);
		clinicalBtnGroup.setText(R.id.btn_left,
				getResources().getString(R.string.clinical_all));
		clinicalBtnGroup.setText(R.id.btn_right,
				getResources().getString(R.string.clinical_aim));
		clinicalBtnGroup.setText(R.id.btn_middle_01, getResources()
				.getString(R.string.clinical_basic));

		ifText = (TextView) findViewById(R.id.seekBarText);
		ifText.setTextColor(Color.BLACK);
		seekBar = (MySeek) findViewById(R.id.seek_layout);
		seekBar.setProgress(ifCount);

		doneBtn = (Button) findViewById(R.id.register_submit);
			if (isfromBrowse) {
//			freeBtnGroup = (ButtonGroup) findViewById(R.id.free);
//			freeBtnGroup.setBtnCount(2, freeIndex);
//			freeBtnGroup.setText(R.id.btn_left,
//					getResources().getString(R.string.free_yes));
//			freeBtnGroup.setText(R.id.btn_right,
//					getResources().getString(R.string.free_no));
//
//			abstractsBtnGroup = (ButtonGroup) findViewById(R.id.abstracts);
//			abstractsBtnGroup.setBtnCount(2, abstractsIndex);
//			abstractsBtnGroup.setText(R.id.btn_left,
//					getResources().getString(R.string.abstract_yes));
//			abstractsBtnGroup.setText(R.id.btn_right,
//					getResources().getString(R.string.abstract_no));
//
//			calendarEditText = (EditText) findViewById(R.id.calendar);
//			calendarEditText.setOnClickListener(this);

			addBtn = (Button) findViewById(R.id.add);
			reduceBtn = (Button) findViewById(R.id.reduce);
			addBtn.setOnClickListener(this);
			reduceBtn.setOnClickListener(this);

			kw1Layout = (LinearLayout) findViewById(R.id.kw1_layout);
//			kw2Layout = (LinearLayout) findViewById(R.id.kw2_layout);
//			kw3Layout = (LinearLayout) findViewById(R.id.kw3_layout);
			reduce1Layout = (LinearLayout) findViewById(R.id.reduce_layout1);
			reduce2Layout = (LinearLayout) findViewById(R.id.reduce_layout2);
			reduce3Layout = (LinearLayout) findViewById(R.id.reduce_layout3);
			reduce1Layout.findViewById(R.id.reduce).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {MobclickAgent.onEvent(v.getContext(), "research_latest_filter_reduce");
					reduce1Layout.setVisibility(View.GONE);
					keyWordCount --;
				}
			});
			reduce2Layout.findViewById(R.id.reduce).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {MobclickAgent.onEvent(v.getContext(), "research_latest_filter_reduce");
					reduce2Layout.setVisibility(View.GONE);
					keyWordCount --;
				}
			});
			reduce3Layout.findViewById(R.id.reduce).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {MobclickAgent.onEvent(v.getContext(), "research_latest_filter_reduce");
					reduce3Layout.setVisibility(View.GONE);
					keyWordCount --;
				}
			});
			if(!searchKey.equals("")){
				String[] str = searchKey.split("#@");
				reduce1Layout.setVisibility(View.VISIBLE);
				((TextView)reduce1Layout.findViewById(R.id.search_key)).setTag(str[0].substring(0,str[0].indexOf(" ")));
				((TextView)reduce1Layout.findViewById(R.id.search_key)).setText(str[0].substring(str[0].indexOf(" "),str[0].indexOf(":")));
				((EditText)reduce1Layout.findViewById(R.id.search_value)).setText(str[0].substring(str[0].indexOf(":") + 1));
				if(str.length > 1){
					reduce2Layout.setVisibility(View.VISIBLE);
					((TextView)reduce2Layout.findViewById(R.id.search_key)).setTag(str[1].substring(0,str[1].indexOf(" ")));
					((TextView)reduce2Layout.findViewById(R.id.search_key)).setText(str[1].substring(str[1].indexOf(" "),str[1].indexOf(":")));
					((EditText)reduce2Layout.findViewById(R.id.search_value)).setText(str[1].substring(str[1].indexOf(":") + 1));
				}
				if(str.length > 2){
					reduce3Layout.setVisibility(View.VISIBLE);
					((TextView)reduce3Layout.findViewById(R.id.search_key)).setTag(str[2].substring(0,str[2].indexOf(" ")));
					((TextView)reduce3Layout.findViewById(R.id.search_key)).setText(str[2].substring(str[2].indexOf(" "),str[2].indexOf(":")));
					((EditText)reduce3Layout.findViewById(R.id.search_value)).setText(str[2].substring(str[2].indexOf(":") + 1));
				}
			}
			spinner1 = (Spinner) kw1Layout.findViewById(R.id.spinner1);
			spinner2 = (Spinner) kw1Layout.findViewById(R.id.spinner2);
			searchKeyword = (EditText) kw1Layout.findViewById(R.id.search_keyword);
//			spinner3 = (Spinner) kw2Layout.findViewById(R.id.spinner1);
//			spinner4 = (Spinner) kw2Layout.findViewById(R.id.spinner2);
//			spinner5 = (Spinner) kw3Layout.findViewById(R.id.spinner1);
//			spinner6 = (Spinner) kw3Layout.findViewById(R.id.spinner2);
			adapter1 = ArrayAdapter.createFromResource(context, R.array.and_or,
					android.R.layout.simple_spinner_item);
			adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner1.setAdapter(adapter1);
			spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					MobclickAgent.onEvent(context, "research_latest_filter_spinner1");
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
//			spinner3.setAdapter(adapter1);
//			spinner5.setAdapter(adapter1);
			adapter2 = ArrayAdapter.createFromResource(context,
					R.array.search_keywords,
					android.R.layout.simple_spinner_item);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner2.setAdapter(adapter2);
			spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					MobclickAgent.onEvent(context, "research_latest_filter_spinner2");
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
//			spinner4.setAdapter(adapter2);
//			spinner6.setAdapter(adapter2);
		}
	}
	
	private void loadLocalSettings() {
//		if (!isfromBrowse) {
//			sourceIndex = filterInfo.getInt("sourceIndex", 0);
//			clinicalIndex = filterInfo.getInt("clinicalIndex", 1);
//			departmentIndex = filterInfo.getInt("departmentIndex", 1);
//			ifCount = filterInfo.getInt("ifCount", 10);
//		} else {
			sourceIndex = filterInfo.getInt("sourceBrowseIndex", 0);
			clinicalIndex = filterInfo.getInt("clinicalBrowseIndex", 0);
			departmentIndex = filterInfo.getInt("departmentBrowseIndex", 1);
			ifCount = filterInfo.getInt("ifBrowseCount", 0);
			searchKey = filterInfo.getString("searchKey", "");
//			freeIndex = filterInfo.getInt("freeBrowseIndex", 1);
//			abstractsIndex = filterInfo.getInt("abstractsBrowseIndex", 0);
//		}
	}

	public String setLocalSettings() {
		MobclickAgent.onEvent(context, "research_latest_filter_save");
		sourceIndex = sourceBtnGroup.getSelectButtonIndex();
		departmentIndex = departmentBtnGroup.getSelectButtonIndex();
		clinicalIndex = clinicalBtnGroup.getSelectButtonIndex();
		ifCount =  seekBar.getProgress();
		searchKey = "";
		if(reduce1Layout.getVisibility() == View.VISIBLE){
			
			if(searchKey.equals("")){
				searchKey = ((TextView)reduce1Layout.findViewById(R.id.search_key)).getTag().toString()
						+ " " + ((TextView)reduce1Layout.findViewById(R.id.search_key)).getText().toString()
						+ ":" + ((EditText)reduce1Layout.findViewById(R.id.search_value)).getText().toString();
			}else{
				searchKey += "#@" + ((TextView)reduce1Layout.findViewById(R.id.search_key)).getTag().toString()
						+ " " + ((TextView)reduce1Layout.findViewById(R.id.search_key)).getText().toString()
						+ ":" + ((EditText)reduce1Layout.findViewById(R.id.search_value)).getText().toString();
			}
		}
		if(reduce2Layout.getVisibility() == View.VISIBLE){
			if(searchKey.equals("")){
				searchKey = ((TextView)reduce2Layout.findViewById(R.id.search_key)).getTag().toString()
						+ " " + ((TextView)reduce2Layout.findViewById(R.id.search_key)).getText().toString()
						+ ":" + ((EditText)reduce2Layout.findViewById(R.id.search_value)).getText().toString();
			}else{
				searchKey += "#@" + ((TextView)reduce2Layout.findViewById(R.id.search_key)).getTag().toString()
						+ " " + ((TextView)reduce2Layout.findViewById(R.id.search_key)).getText().toString()
						+ ":" + ((EditText)reduce2Layout.findViewById(R.id.search_value)).getText().toString();
			}
		}
		if(reduce3Layout.getVisibility() == View.VISIBLE){
			if(searchKey.equals("")){
				searchKey = ((TextView)reduce3Layout.findViewById(R.id.search_key)).getTag().toString()
						+ " " + ((TextView)reduce3Layout.findViewById(R.id.search_key)).getText().toString()
						+ ":" + ((EditText)reduce3Layout.findViewById(R.id.search_value)).getText().toString();
			}else{
				searchKey += "#@" + ((TextView)reduce3Layout.findViewById(R.id.search_key)).getTag().toString()
						+ " " + ((TextView)reduce3Layout.findViewById(R.id.search_key)).getText().toString()
						+ ":" + ((EditText)reduce3Layout.findViewById(R.id.search_value)).getText().toString();
			}
		}
		if(searchKey.equals("") && !searchKeyword.getText().toString().trim().equals("")){
			searchKey = replaceAndOr(spinner1.getSelectedItem().toString())
					+ " " + spinner2.getSelectedItem().toString()
					+ ":" + searchKeyword.getText().toString();
		}
//		freeIndex = freeBtnGroup.getSelectButtonIndex();
//		abstractsIndex = abstractsBtnGroup.getSelectButtonIndex();
//		if (!isfromBrowse) {
//			filterInfo.edit().putInt("sourceIndex", sourceIndex).commit();
//			filterInfo.edit().putInt("clinicalIndex", clinicalIndex).commit();
//			filterInfo.edit().putInt("departmentIndex", departmentIndex)
//					.commit();
//			filterInfo.edit().putInt("ifCount", ifCount).commit();
//		} else {
			filterInfo.edit().putInt("sourceBrowseIndex", sourceIndex).commit();
			filterInfo.edit().putInt("departmentBrowseIndex", departmentIndex)
					.commit();
			filterInfo.edit().putInt("clinicalBrowseIndex", clinicalIndex)
			.commit();
			filterInfo.edit().putInt("ifBrowseCount", ifCount).commit();
			filterInfo.edit().putString("searchKey", searchKey).commit();
			
			String str = "AbstractIsEmpty:false AND Source:";
			if(sourceIndex == 2){
				str += "wanfang";
			}else if(sourceIndex == 1){
				str += "pubmed";
			}else if(sourceIndex == 0){
				str += "*";
			}
			str +=" AND ";
			
			
			str += "Category:";
			if(departmentIndex == 1){
				SqliteAdapterCentre.getInstance().renew(DBFactory.SDCard_DB_NAME);
				Cursor cursor = SqliteAdapterCentre.getInstance().get(DBFactory.SDCard_DB_NAME).getCursor("select departmentEN from department where department = '" + SharedPreferencesMgr.getDept() + "'",null);
				String departmentEN = "";
				if (cursor != null&&cursor.getCount()>0) {
					cursor.moveToFirst();
					departmentEN= cursor.getString(0);
				}else{
					departmentEN = "*";
				}
				str += departmentEN;
			}else if(departmentIndex == 0){
				str += "*";
			}
			str +=" AND ";
			
			str += "AIM:";
			if(clinicalIndex == 2){
				str += "true";
			}else if(clinicalIndex == 1){
				str += "false";
			}else if(clinicalIndex == 0){
				str += "*";
			}
			str +=" AND ";
			str += "IF:["+this.seekBar.getProgress()+" TO *]";
			str += "";
			
			if(!searchKey.equals("")){
				searchKey = searchKey.replace("#@", " ");
				searchKey = repleaceAbstractOrTitle(searchKey);
				str += searchKey;
			}
			SharedPreferenceUtil.putValue(context, "filter_info", "search", str);
		return str;
	}
	
	private String repleaceAbstractOrTitle(String str){
		return str.replaceAll(context.getString(R.string.kw_title) + ":", "Title:").replaceAll(context.getString(R.string.abstracts_info) + ":", "Abstract:");
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_submit:
			setLocalSettings();
			break;

		case R.id.add:
			MobclickAgent.onEvent(this.getContext(), "research_latest_filter_add");
			if(searchKeyword.getText().toString() == null || searchKeyword.getText().toString().trim().equals("")){
				Toast.makeText(context, context.getString(R.string.kw_no_null), Toast.LENGTH_SHORT).show();
				return;
			}
			if(reduce1Layout.getVisibility() == View.GONE){
				reduce1Layout.setVisibility(View.VISIBLE);
				((TextView)reduce1Layout.findViewById(R.id.search_key)).setText(spinner2.getSelectedItem().toString());
				((TextView)reduce1Layout.findViewById(R.id.search_key)).setTag(replaceAndOr(spinner1.getSelectedItem().toString()));
				((EditText)reduce1Layout.findViewById(R.id.search_value)).setText(searchKeyword.getText());
				keyWordCount ++;
			}else if(reduce2Layout.getVisibility() == View.GONE){
				reduce2Layout.setVisibility(View.VISIBLE);
				((TextView)reduce2Layout.findViewById(R.id.search_key)).setText(spinner2.getSelectedItem().toString());
				((TextView)reduce2Layout.findViewById(R.id.search_key)).setTag(replaceAndOr(spinner1.getSelectedItem().toString()));
				((EditText)reduce2Layout.findViewById(R.id.search_value)).setText(searchKeyword.getText());
				keyWordCount ++;
			}else if(reduce3Layout.getVisibility() == View.GONE){
				reduce3Layout.setVisibility(View.VISIBLE);
				((TextView)reduce3Layout.findViewById(R.id.search_key)).setText(spinner2.getSelectedItem().toString());
				((TextView)reduce3Layout.findViewById(R.id.search_key)).setTag(replaceAndOr(spinner1.getSelectedItem().toString()));
				((EditText)reduce3Layout.findViewById(R.id.search_value)).setText(searchKeyword.getText());
				keyWordCount ++;
			}
			break;

		default:
			break;
		}
	}
	
	private String replaceAndOr(String string){
		String str = "";
		if(string.trim().equals(context.getString(R.string.filter_and))){
			str = "And";
		}else if(string.trim().equals(context.getString(R.string.filter_or))){
			str = "Or";
		}
		return str;
	}

	public void reset() {
		Logs.i(" --- " + this.getHeight() + " " + this.getLayoutParams().height
				+ " " + (this.getVisibility() == View.VISIBLE));
	}

	public void releaseToRefresh() {
	}

	public void setPullLabel(String pullLabel) {
		this.pullLabel = pullLabel;
	}

	public void refreshing() {
	}

	public void setReleaseLabel(String releaseLabel) {
		this.releaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
		
	}

	public void setTextColor(int color) {
	}
}
