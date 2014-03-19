package com.jibo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.api.android.GBApp.R;import com.jibo.activity.HomePageActivity;
import com.jibo.data.entity.HomePageItemEntity;

public class HomePageAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HomePageItemEntity> list;
	private LayoutInflater inflater;
	private int index;
	public HomePageAdapter(Context context, ArrayList<HomePageItemEntity> list, int index) {
		this.context = context;
		this.list = list;
		this.index = index;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = (View) inflater.inflate(R.layout.gv_item_layout, null);
			holder = new ViewHolder();
			holder.imgV = (ImageView) convertView.findViewById(R.id.img_item);
			holder.imgV2 = (ImageView) convertView.findViewById(R.id.img_item_no);
			holder.text = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
	
		holder.imgV.setBackgroundResource(list.get(position).getPicID());
		holder.imgV2.setBackgroundResource(list.get(position).getPicID());
		holder.text.setText(list.get(position).getTitle());
		//TODO
		if(index==1) {
			((HomePageActivity)context).addImg(position, holder.imgV2);
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView imgV;
		ImageView imgV2;
		TextView text;
	}
}
