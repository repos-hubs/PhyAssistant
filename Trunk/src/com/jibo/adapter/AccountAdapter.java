package com.jibo.adapter;

import com.api.android.GBApp.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountAdapter extends BaseAdapter {
	private int[] icons;
	private String[] texts;
	private Context context;
	private ImageView icon;
	private TextView text;
	LayoutInflater inflater;
	public AccountAdapter(Context context, int[] icons, String[] texts){
		inflater =LayoutInflater.from(context);
		this.context = context;
		this.texts = texts;
		this.icons = icons;
	}
	
	@Override
	public int getCount() {
		return texts.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.list_item_icon_text, null);
		icon = (ImageView)convertView.findViewById(R.id.icon);
		text = (TextView)convertView.findViewById(R.id.ListText);
		text.setText(texts[position]);
		icon.setImageResource(icons[position]);
		
		return convertView;
	}

}
