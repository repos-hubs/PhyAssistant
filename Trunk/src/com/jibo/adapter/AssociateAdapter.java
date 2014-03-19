package com.jibo.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jibo.GBApplication;
import com.api.android.GBApp.R;import com.jibo.base.src.request.impl.db.SqliteAdapterCentre;
import com.jibo.common.Util;
import com.jibo.dao.DBFactory;
import com.jibo.util.Logs;

public class AssociateAdapter extends BaseAdapter implements Filterable{
	private Context context;
	private GBApplication application;
	private MyFilter mFilter;
	private final Object mLock = new Object();
	private List<String> mObjects;
	private String inputStr = "";
	public AssociateAdapter(Context context) {
		this.context = context;
		application = (GBApplication)((Activity)context).getApplication();
		if(mObjects != null) {
			mObjects.clear();
		}
		mObjects = new ArrayList<String>();
	}
	@Override
	public int getCount() {
		synchronized (mLock) {
			return mObjects.size();
		}
	}
  
	@Override
	public Object getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater)context.getSystemService (
				      Context.LAYOUT_INFLATER_SERVICE);
			convertView=(View) inflater.inflate(R.layout.search_history_item, null);
			holder.txtSearchItem = (TextView) convertView.findViewById(R.id.txt_search_item);
			holder.txtSearchTime = (TextView) convertView.findViewById(R.id.txt_search_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.txtSearchItem.setTextColor(Color.GRAY);
		if(mObjects.size() != 0) {
			SpannableString sp = new SpannableString(mObjects.get(position));
			if(inputStr.length() > mObjects.get(position).length()) {
				inputStr = mObjects.get(position);
			}
			sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, inputStr.length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			holder.txtSearchItem.setText(sp);
		}
		return convertView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return super.getDropDownView(position, convertView, parent);
	}
	@Override
	public Filter getFilter() {
		if(mFilter == null) {
			mFilter = new MyFilter();
		}
		return mFilter;
	}
	public void getRightString(String s) {
		String regEX = "(?i)\\b^"+s;
		Pattern p = Pattern.compile(regEX);
		
		synchronized (mLock) {
			mObjects.clear();
			for (int i = 0; i < application.getAssociateDataList().size(); i++) {
				if (application.getAssociateDataList().get(i) == null) {
					continue;
				}

				Matcher m = p.matcher(application.getAssociateDataList().get(i));
				if (m.find()) {
					mObjects.add(application.getAssociateDataList().get(i));
				}
			}
		}
		Collections.sort(mObjects, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});
	}
	class MyFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence s) {
			FilterResults fr = new FilterResults();
			Logs.i("s <==> "+s);
			Logs.i("================= end =====================");
		
            if (s == null || s.length() == 0) {
                synchronized (mLock) {
                    ArrayList<String> list = new ArrayList<String>(mObjects);
                    fr.values = list;
                    fr.count = list.size();
                    s = "";
                }
			} else {
				synchronized (mLock) {
					if (s.toString().matches("[\u4e00-\u9fa5]{1,5}")) {
						searchDictionary(s,"SearchDictionary");
						application.setAssociateDataList(application.getAssociateDataCNList());

					} else {
						searchDictionary(s,"SearchDictionary");
						application.setAssociateDataList(application.getAssociateDataENList());
					}
				}
				synchronized (mLock) {
					getRightString(s.toString());
					fr.values = mObjects;
					fr.count = mObjects.size();
				}

			}
				
            inputStr = s.toString();
			return fr;
		}

		private void searchDictionary(CharSequence s,String table) {
			Logs.i("s = "+s);
			SqliteAdapterCentre.getInstance().renew(DBFactory.SDCard_DB_NAME);
			Cursor cursor = SqliteAdapterCentre
					.getInstance()
					.get(DBFactory.SDCard_DB_NAME)
					.getCursor(
							"select AlternativeName from "+table+" where AlternativeName like '%"+s.toString()+"%' order by AlternativeName",
							null);
			Logs.i(" ==count "+cursor.getCount());
			if (cursor == null || cursor.getCount() == 0) {
				return;
			}
			while (cursor.moveToNext()) {				
				String name = cursor.getString(0);
//				if (table.toLowerCase().endsWith("cn")) {
				if (s.toString().matches("[\u4e00-\u9fa5]{1,5}")) {
					application.getAssociateDataCNList().add(name);
				}else{
//				} else if(table.toLowerCase().endsWith("en")){
					application.getAssociateDataENList().add(name);
				}
//				}
			}
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			mObjects = (List<String>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
	static class ViewHolder {
		TextView txtSearchItem;
		TextView txtSearchTime;
	}
}
