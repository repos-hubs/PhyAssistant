package com.jibo.app.research;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.app.ImgAdapter;
import com.jibo.base.adapter.AdapterSrc;
import com.jibo.base.src.EntityObj;
import com.jibo.base.src.request.RequestSrc;
import com.jibo.data.entity.NewsEntity;
import com.jibo.util.Logs;

public class ResearchAdapter extends ImgAdapter {
	private Set<String> readTopNews;
	private Boolean IFVisible;

	public ResearchAdapter(Context context, AdaptInfo adaptInfo,
			Set<String> readTopNews) {
		super(context, adaptInfo, null);
		this.readTopNews = readTopNews;
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public ResearchAdapter(Context context, AdaptInfo adaptInfo,
			boolean IFVisible, Set<String> readTopNews) {
		super(context, adaptInfo, null);
		this.readTopNews = readTopNews;
		this.context = context;
		this.IFVisible = IFVisible;
		// TODO Auto-generated constructor stub
	}

	public List<NewsEntity> getList() {
		return list;
	}

	int color = Color.parseColor("#fbd665");

	/*
	 * 针对每一项
	 * 
	 * @see com.jibo.base.adapter.MapAdapter#getViewInDetail(java.lang.Object,
	 * int, android.view.View)
	 */
	@Override
	protected void getViewInDetail(Object item, int position, View convertView) {
		// TODO Auto-generated method stub
		String status;
		if (IFVisible != null) {
			convertView.findViewById(R.id.IF).setVisibility(
					IFVisible ? View.VISIBLE : View.GONE);
		} else {
			if (((EntityObj) item).fieldContents.containsKey("IF")) {
				convertView.findViewById(R.id.IF).setVisibility(View.VISIBLE);
			} else {
				convertView.findViewById(R.id.IF).setVisibility(View.GONE);
			}
		}if (IFVisible != null) {
			convertView.findViewById(R.id.PeriodicalTitle).setVisibility(
					IFVisible ? View.VISIBLE : View.GONE);
		} else {
		if (((EntityObj) item).fieldContents.containsKey("JournalName")
				|| ((EntityObj) item).fieldContents
						.containsKey("JournalAbbrName")) {
			convertView.findViewById(R.id.PeriodicalTitle).setVisibility(
					View.VISIBLE);
		} else {
			convertView.findViewById(R.id.PeriodicalTitle).setVisibility(
					View.GONE);
		}}
		super.getViewInDetail(item, position, convertView);

		String pubdate = ((EntityObj) item).get("PublicDate");
		pubdate = pubdate.length()>=10?pubdate.substring(0, 10):pubdate;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(pubdate);
			Log.i("time", date.getTime() + "");
			Log.i("nowstime", System.currentTimeMillis() + "");
			if (date.getTime() > System.currentTimeMillis()) {
				convertView.findViewById(R.id.epub).setVisibility(View.VISIBLE);
			} else {
				convertView.findViewById(R.id.epub).setVisibility(View.GONE);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		status = ((EntityObj) item).get("IsFreeFullText");

		if ((status = ((EntityObj) item).get("IsFreeFullText")) == null
				|| status.toLowerCase().equalsIgnoreCase("false")) {
			convertView.findViewById(R.id.download).setVisibility(View.GONE);
		} else {
			convertView.findViewById(R.id.download).setVisibility(View.VISIBLE);
		}
		// String ifItem = ((EntityObj) item).get("IF");
		// if(TextUtils.isEmpty(ifItem)){
		// convertView.findViewById(R.id.IF).setVisibility(View.GONE);
		// }else{
		// convertView.findViewById(R.id.IF).setVisibility(View.VISIBLE);
		// }
		Object id = ((EntityObj) item).fieldContents.get("id");

		if (id == null) {
			id = ((EntityObj) item).fieldContents.get("Id");
		}
		if (id == null) {
			return;
		}
		boolean turnGray = false;
		if (readTopNews != null) {
			if (readTopNews.contains(id.toString().trim())) {
				((TextView) convertView.findViewById(R.id.ArticleTitle))
						.setTextColor(Color.parseColor("#ff888888"));
				turnGray = true;
			} else {
				((TextView) convertView.findViewById(R.id.ArticleTitle))
						.setTextColor(context.getResources().getColor(
								R.color.darkgreen));
			}
		}

	}

	/*
	 * 针对每一个item里的小项目
	 * 
	 * @see com.jibo.app.news.ImgAdapter#findAndBindView(android.view.View, int,
	 * java.lang.Object, java.lang.String, java.lang.Object)
	 */
	@Override
	protected void findAndBindView(View convertView, int pos, Object item,
			String name, Object value) {
		Logs.i("--- isshow " + name + " " + value);
		if (name.equalsIgnoreCase("publicdate")) {
			if (value.equals("null")) {
				value = "";
			} else
				value = value.toString().split("T")[0].replaceAll("-", "/");
		} else if (name.equalsIgnoreCase("JournalName")) {
			Object abbr = ((Map) item).get("JournalAbbrName");
			if (abbr == null) {
				value = value == null ? "" : value.toString();
			} else {
				value = abbr.toString();
			}
			if (value.toString().trim().equals("")) {
				convertView.findViewById(R.id.PeriodicalTitle).setVisibility(
						View.GONE);
			} else {
				convertView.findViewById(R.id.PeriodicalTitle).setVisibility(
						View.VISIBLE);
			}

		}
		Boolean IFempty = null;
		if (name.equalsIgnoreCase("IF")) {
			Logs.i("=== IF " + value);
			final Object val = value;
			// if (!TextUtils.isEmpty(value==null?"":value.toString())) {
			if (value.equals("null") || value.toString().equals("0")
					|| value.equals("") || value.toString().equals("0.00")) {
				value = "";
				IFempty = true;
				// if(convertView.findViewById(R.id.PeriodicalTitle).getVisibility()==View.GONE){
				// convertView.findViewById(R.id.line).setVisibility(View.GONE);
				// }else{
				// convertView.findViewById(R.id.line).setVisibility(View.VISIBLE);
				// }
			} else
				IFempty = false;
		}
		super.findAndBindView(convertView, pos, item, name, value);
		if (IFempty != null) {
			if (IFempty) {
				convertView.findViewById(R.id.IF).setVisibility(View.GONE);
			} else {
				convertView.findViewById(R.id.IF).setVisibility(View.VISIBLE);
			}
		}
	}

	public ResearchAdapter(Context context, AdaptInfo adaptInfo,
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


}