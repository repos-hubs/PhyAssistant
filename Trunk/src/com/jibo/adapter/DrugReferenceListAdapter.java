package com.jibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.api.android.GBApp.R;

public class DrugReferenceListAdapter extends BaseAdapter {

	LayoutInflater inflater;
    String [] titles;
	public DrugReferenceListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (titles != null)
			return titles.length;
		return 0;
	}

	@Override
	public String getItem(int position) {
		if (titles != null)
			return titles[position];
		return "";
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setListData(String []list) {
		titles = list;
		notifyDataSetChanged();
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_drugreference, null);
			holder = new ViewHold();
			holder.tx= (TextView) convertView.findViewById(R.id.txt_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}

		holder.tx.setText(titles[position]);
		return convertView;
	}

	class ViewHold {
		TextView tx;
	}
}
