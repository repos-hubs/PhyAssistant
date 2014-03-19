package com.jibo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.api.android.GBApp.R;
import com.jibo.activity.NewDrugReferenceActivity;
import com.jibo.common.Util;
import com.jibo.dao.DaoSession;
import com.jibo.entity.BrandInfo;
import com.jibo.entity.DrugListItem;
import com.jibo.entity.FavoritDrugEntity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/***
 * 药品收藏列表adapter
 * 
 * @author simon
 * 
 */
public class FavoritListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	private ArrayList<FavoritDrugEntity> list;
	private ForegroundColorSpan fgSpan ;
	private DaoSession dao;
	private boolean isZh;
	private ImageSpan span;
	

	public FavoritListAdapter(Context context,DaoSession dao,ArrayList<FavoritDrugEntity> favorList) {
		this.context = context;
		this.dao = dao;
		Drawable drawable = context.getResources().getDrawable(
				R.drawable.ahfs1);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		span = new ImageSpan(drawable,
				ImageSpan.ALIGN_BASELINE);
		isZh = Util.isZh();
		inflater = LayoutInflater.from(context);
		this.list = favorList;
		 fgSpan = new ForegroundColorSpan(context
	    			.getResources().getColor(R.color.gray));
	}

	@Override
	public int getCount() {
		if (list != null)
			return list.size();
		return 0;
	}

	@Override
	public FavoritDrugEntity getItem(int position) {
		if (list != null)
			return list.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.druglst_info, null);
			holder = new ViewHold();
			holder.text1 = (TextView) convertView.findViewById(R.id.ListText1);
			holder.text1.setTextColor(Color.parseColor("#004371"));
			holder.text2 = (TextView) convertView
					.findViewById(R.id.ListText2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		holder.text1.setText(getSpannable(list.get(position)));
		
		if (list.get(position).mode== NewDrugReferenceActivity.MODE_NORMAL) {
			holder.text2.setText(getBrandInfoString(list.get(position).getBrandInfo(dao)));
		}
		return convertView;
	}
	
	/***
	 * 品牌名
	 * 
	 * @param list
	 * @return
	 */
	private String getBrandInfoString(List<BrandInfo> list) {
		if (null == list || list.size() == 0)
			return "";
		StringBuilder builder = new StringBuilder();
		List<String> brandList = new ArrayList<String>();
		for (BrandInfo obj : list) {
			String name = isZh ? obj.getNameCn() : obj.getNameEn();
			if (null != name && !brandList.contains(name)) {
				builder.append(name);
				builder.append(",");
				brandList.add(name);
			}
		}
		String text = builder.toString();
		if (TextUtils.isEmpty(text))
			return "";
		text = text.substring(0, text.length() - 1);
		return text;
	}
	
	private CharSequence getSpannable(FavoritDrugEntity info) {
			String splitString = "   ";
			StringBuilder builder = new StringBuilder(info.drugName);
			String adminRouteContent = info.adminRouteName;
			boolean isShowAdminRoute = false;
			boolean isShowAHFS = false;

			// 检测是否包含给药类型的数据,并且数量不只1个
			if (!TextUtils.isEmpty(adminRouteContent)) {
				builder.append(splitString);
				builder.append(adminRouteContent);
				isShowAdminRoute = true;
			}
			// 检测是否包含AHFS相关数据
			if (info.ahfsInfo.equals("Y")) {
				builder.append(splitString);
				builder.append("temp");
				isShowAHFS = true;
			}

			String text = builder.toString();
			SpannableString spannable = new SpannableString(text);

			if (isShowAdminRoute) {
				ForegroundColorSpan fgSpan = new ForegroundColorSpan(context
						.getResources().getColor(R.color.gray));
				spannable.setSpan(fgSpan, text.indexOf(splitString)
						+ splitString.length(), text.indexOf(splitString)
						+ splitString.length() + adminRouteContent.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			if (isShowAHFS) {
				spannable.setSpan(span, text.lastIndexOf(splitString)
						+ splitString.length(), text.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
			return spannable;
		}
	
	

	class ViewHold {
		TextView text1;
		TextView text2;
	}
}
