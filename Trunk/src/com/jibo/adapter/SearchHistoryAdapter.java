package com.jibo.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jibo.GBApplication;
import com.api.android.GBApp.R;
public class SearchHistoryAdapter extends BaseAdapter implements Filterable{
	Context context;
	GBApplication application;
	MyFilter mFilter;
	String strSplit;
	private final Object mLock = new Object();
	private List<String> mObjects;
	private String inputStr = "";
	public SearchHistoryAdapter(Context context, ArrayList<String> list) {
		this.context = context;
		strSplit = ((Activity)context).getResources().getString(R.string.str_split);
		application = (GBApplication)((Activity)context).getApplication();
		if(mObjects != null) {
			mObjects.clear();
		}
		mObjects = new ArrayList<String>(list);
		
	}
	@Override
	public int getCount() {
		synchronized (mLock) {
			return mObjects.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return mObjects.get(position).split(strSplit)[0];
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
		
		String content[] = mObjects.get(position).split(strSplit);
		String timeArr[] = content[1].split(":");
		int month = Integer.parseInt(timeArr[0]);
		int date = Integer.parseInt(timeArr[1]);
		holder.txtSearchItem.setTextColor(Color.BLACK);
		holder.txtSearchTime.setTextColor(Color.GRAY);
		holder.txtSearchItem.setText(content[0]);
		holder.txtSearchTime.setText(getDate(month, date));
		return convertView;
	}
	
	public String getDate(int month, int date) {
		long time=System.currentTimeMillis();
        Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int currentMonth = mCalendar.get(Calendar.MONTH)+1;
        int currentDate = mCalendar.get(Calendar.DATE);
        String result = "";
        if(currentMonth == month) {
        	switch(Math.abs(date-currentDate)) {
        	case 0:
        		result = context.getResources().getString(R.string.vh_today);
        		break;
        	case 1:
        		result = context.getResources().getString(R.string.vh_yesterday);
        		break;
        	case 2:
        	case 3:
        	case 4:
        	case 5:
        	case 6:
        		result = context.getResources().getString(R.string.vh_week);
        		break;
        	default:
        		result = context.getResources().getString(R.string.vh_older);
            	break;
        	}
        } else {
        	if(date == 1) {
        		result = context.getResources().getString(R.string.vh_yesterday);
        	} else {
        		result = context.getResources().getString(R.string.vh_month);
        	}
        }
        return result;
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
	class MyFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence s) {
			FilterResults fr = new FilterResults();

            if (s == null || s.length() == 0) {
                synchronized (mLock) {
                    ArrayList<String> list = new ArrayList<String>(mObjects);
                    fr.values = list;
                    fr.count = list.size();
                    s = "";
                }
            } else {
            	fr.values = mObjects;
            	fr.count = mObjects.size();
            }
				
            inputStr = s.toString();
			return fr;
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
