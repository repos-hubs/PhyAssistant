package com.jibo.app.news;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.app.ImgAdapter;
import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.data.entity.NewsEntity;
import com.jibo.util.Logs;

public class NewsAdapter extends ImgAdapter {
	private Set<String> readTopNews;

	public NewsAdapter(Context context, AdaptInfo adaptInfo,
			Set<String> readTopNews) {
		super(context, adaptInfo, null);
		this.readTopNews = readTopNews;
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public List<NewsEntity> getList() {
		return list;
	}

	int color = Color.parseColor("#fbedc8");

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	@Override
	protected void getViewInDetail(Object item, int position, View convertView) {
		// TODO Auto-generated method stub
		Object stickOj = ((EntityObj) item).fieldContents.get("newStick");
		Boolean newStick = stickOj == null ? null : Boolean
				.parseBoolean(stickOj.toString());
		if (newStick != null && newStick) {
			convertView.findViewById(R.id.fileItemLayout).setBackgroundColor(
					color);
		} else
			convertView.findViewById(R.id.fileItemLayout).setBackgroundColor(
					Color.TRANSPARENT);

		super.getViewInDetail(item, position, convertView);

		Object id = ((EntityObj) item).fieldContents.get("id");

		if (id == null) {
			id = ((EntityObj) item).fieldContents.get("Id");
		}
		if (id == null) {
			return;
		}
		if (readTopNews != null) {
			
			if (readTopNews.contains(id.toString().trim())) {
				((TextView) convertView.findViewById(R.id.name))
						.setTextColor(Color.parseColor("#ff888888"));
			} else {
				((TextView) convertView.findViewById(R.id.name))
						.setTextColor(context.getResources().getColor(
								R.color.darkgreen));
			}
			this.notifyDataSetChanged();
		}

	}

	@Override
	protected void findAndBindView(View convertView, int pos, Object item,
			String name, Object value) {
		// TODO Auto-generated method stub
		if (name.equalsIgnoreCase("publicdate")) {
			value = value.toString().split("T")[0].replaceAll("-", "/");
		}else if (name.equals("stickMsg")) {
			special = convertView.findViewById(R.id.special);
			bg = convertView.findViewById(R.id.fileItemLayout);
			((TextView) special).setText(value.toString());
			Object newStick = ((Map) item).get("newStick");
			if (newStick == null) {
				isTop = false;
			} else if (newStick instanceof String) {
				isTop = Boolean.valueOf(newStick.toString());
			} else if (newStick instanceof Boolean) {
				isTop = newStick == null ? Boolean.FALSE : (Boolean) newStick;
			} else {
				isTop = false;
			}
			special.setVisibility((value != null && !value.toString().trim()
					.equals("")) ? View.VISIBLE : View.GONE);
			return;
		}
		super.findAndBindView(convertView, pos, item, name, value);

	}

	public NewsAdapter(Context context, AdaptInfo adaptInfo,
			Set<String> readTopNews, RequestSrc lis) {
		super(context, adaptInfo, lis);
		this.readTopNews = readTopNews;
		this.context = context;
	}

	public List setList(List<NewsEntity> list) {
		this.setItemDataSrc(new AdapterSrc(list));
		this.list = list;
		return list;
	}

	public void addList(List<NewsEntity> list) {
		if (this.getItemDataSrc() == null) {
			return;
		}
		((List) this.getItemDataSrc().getContent()).addAll(list);
		this.list = ((List) this.getItemDataSrc().getContent());
	}

	public List<NewsEntity> list = new ArrayList<NewsEntity>();
	private Context context;

	public String getFirstId() {
		if (null == list)
			return null;
		int length = list.size();
		if (length > 0)
			return list.get(0).getId();
		return "";
	}

	public String getLastItemId() {
		if (null == this.getItemDataSrc().getContent())
			return null;
		int length = this.getItemDataSrc().getCount();
		if (length > 0)
			return ((List<NewsEntity>) this.getItemDataSrc().getContent()).get(
					length - 1).getId();
		return null;
	}

}