//package com.jibo.adapter;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Color;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.style.ForegroundColorSpan;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.TextView;
//
//import com.jibo.GBApplication;
//import com.api.android.GBApp.R;
//import com.jibo.common.Util;
//import com.jibo.dbhelper.HospitalAdapter;
//
///**
// * * @deprecated ·ÏÆú
// * @author simon
// *
// */
//public class HosipitalAutoCompleteAdapter extends BaseAdapter implements
//		Filterable {
//	private Context context;
//	private MyFilter mFilter;
//	private final Object mLock = new Object();
//	private ArrayList<String> mObjects;
//	private HospitalAdapter hospitalHelper;
//	private String province;
//	private String city;
//
//	public HosipitalAutoCompleteAdapter(Context context, String province,
//			String city) {
//		this(context, province, city, null);
//	}
//
//	public HosipitalAutoCompleteAdapter(Context context, String province,
//			String city, String[] baseArray) {
//		this.context = context;
//		hospitalHelper = new HospitalAdapter(context, 1);
//		this.province = province;
//		this.city = city;
//		if (mObjects != null) {
//			mObjects.clear();
//		}
//
//		mObjects = new ArrayList<String>();
//
//		if (baseArray != null && baseArray.length > 0) {
//			int size = baseArray.length;
//			for (int i = 0; i < size; i++) {
//				mObjects.add(baseArray[i]);
//			}
//		}
//
//	}
//
//	@Override
//	public int getCount() {
//		synchronized (mLock) {
//			if (null != mObjects)
//				return mObjects.size();
//			return 0;
//		}
//	}
//
//	@Override
//	public Object getItem(int position) {
//		if (null != mObjects)
//			return mObjects.get(position);
//		return null;
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		if (mObjects.size() == 0)
//			return null;
//
//		ViewHolder holder;
//		if (convertView == null) {
//			holder = new ViewHolder();
//			LayoutInflater inflater = (LayoutInflater) context
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			convertView = (View) inflater.inflate(
//					R.layout.hospital_search_item, null);
//			holder.txtSearchItem = (TextView) convertView
//					.findViewById(R.id.txt_search_item);
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//
//		holder.txtSearchItem.setTextColor(Color.BLACK);
//		holder.txtSearchItem.setText(mObjects.get(position));
//		return convertView;
//	}
//
//	@Override
//	public View getDropDownView(int position, View convertView, ViewGroup parent) {
//		return super.getDropDownView(position, convertView, parent);
//	}
//
//	@Override
//	public Filter getFilter() {
//		if (mFilter == null) {
//			mFilter = new MyFilter();
//		}
//		return mFilter;
//	}
//
//	class MyFilter extends Filter {
//		@Override
//		protected FilterResults performFiltering(CharSequence s) {
//			FilterResults fr = new FilterResults();
//
//			if (s == null || s.length() == 0) {
//				synchronized (mLock) {
//					ArrayList<String> list = new ArrayList<String>(mObjects);
//					fr.values = list;
//					fr.count = list.size();
//					s = "";
//				}
//			} else {
//				synchronized (mLock) {
//					String hospitalNameArray[][] = hospitalHelper
//							.getHospitalByLocalDatabase(province, city,
//									s.toString());
//					String[] hospitalNames = hospitalNameArray[1];
//					int length = hospitalNames.length;
//					for (int i = 0; i < length; i++) {
//						mObjects.add(hospitalNames[i]);
//					}
//					fr.values = mObjects;
//					fr.count = mObjects.size();
//				}
//
//			}
//
//			return fr;
//		}
//
//		@Override
//		protected void publishResults(CharSequence constraint,
//				FilterResults results) {
//			mObjects = (ArrayList<String>) results.values;
//			if (results.count > 0) {
//				notifyDataSetChanged();
//			} else {
//				notifyDataSetInvalidated();
//			}
//		}
//	}
//
//	static class ViewHolder {
//		TextView txtSearchItem;
//	}
//}
