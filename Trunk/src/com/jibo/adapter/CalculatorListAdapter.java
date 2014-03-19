package com.jibo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.api.android.GBApp.R;
public class CalculatorListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> itemLst;
	private ArrayList<Integer> groupLst;
	private int spinnerPosition;
	private TextView txtItem;
	private TextView txtGroup;
	public CalculatorListAdapter(ArrayList<String> itemLst, ArrayList<Integer> groupLst, int arg2, Context context) {
		this.mContext = context;
		this.itemLst = itemLst;
		this.groupLst = groupLst;
		this.spinnerPosition = arg2;
		for(Integer i:groupLst) {
			System.out.println("position    "+i);
		}
	}
	@Override
	public int getCount() {
		return itemLst.size();
	}

	@Override
	public Object getItem(int position) {
		return itemLst.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService (
				      Context.LAYOUT_INFLATER_SERVICE);
			convertView=(View) inflater.inflate(R.layout.list_item_group, null);
			txtItem = (TextView) convertView.findViewById(R.id.lst_item);
			txtGroup = (TextView) convertView.findViewById(R.id.lst_group);
		if(groupLst.contains(position)) {
			if(spinnerPosition == 0) {
				txtGroup.setText(itemLst.get(position));
				txtItem.setVisibility(LinearLayout.GONE);
			} else {
				txtGroup.setVisibility(LinearLayout.GONE);
				txtItem.setText(itemLst.get(position));
			}
		} else {
			txtGroup.setVisibility(LinearLayout.GONE);
			txtItem.setText(itemLst.get(position));
		}
//		
//		txt.setPadding(0, (int) (4*Util.scale), 0, (int) (2*Util.scale));
//		
//		
		return convertView;
	}

}
