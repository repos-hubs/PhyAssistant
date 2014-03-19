package com.jibo.adapter;

import java.util.ArrayList;

import com.api.android.GBApp.R;
import com.jibo.data.entity.SimpleDrugInfoEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrugRefListAdapter extends BaseAdapter {

	LayoutInflater inflater;
	String []listTitle;

	public DrugRefListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}


	@Override
	public int getCount() {
		if (listTitle != null)
			return listTitle.length;
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setListData(String []s) {
		listTitle = s;
		notifyDataSetChanged();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.druglst_info, null);
			holder = new ViewHold();
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.tx1 = (TextView) convertView.findViewById(R.id.ListText1);
			holder.tx2 = (TextView) convertView.findViewById(R.id.ListText2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		
		return convertView;
	}

	class ViewHold {
		ImageView img;
		TextView tx1;
		TextView tx2;

	}
}
