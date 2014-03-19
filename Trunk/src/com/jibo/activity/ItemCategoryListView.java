package com.jibo.activity;

import com.api.android.GBApp.R;

import android.R.integer;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/***
 * 分类子view
 * @author simon
 *
 */
public class ItemCategoryListView extends LinearLayout{

	/** 选择类别 */
	public TextView chooseCategory;
	/** 列表 */
	public ListView listView;
	private View footerView;// 加载条
	private Context context;

	@Override
	protected Parcelable onSaveInstanceState() {
		return super.onSaveInstanceState();
	}

	public ItemCategoryListView(Context context) {
		super(context);
		Init(context);
	}

	public ItemCategoryListView(Context context, AttributeSet attrs) {
		super(context);
		Init(context);
	}

	private void Init(Context context) {
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.item_category_listview, this);
		footerView = inflater.inflate(R.layout.dialogprogress, null);
//		chooseCategory = (TextView) this.findViewById(R.id.chooseCategory);
		listView = (ListView) this.findViewById(R.id.lst_item);
	}
	
	public void removeFooterView(){
		listView.removeView(footerView);
	}
	
	public void addFooterView(){
		listView.addFooterView(footerView);
	}

//	public void setItemClickListener(ItemClickListener listener) {
//		itemClickListener = listener;
//	}

//	private ItemClickListener itemClickListener;
//
//	public interface ItemClickListener {
//
//		public void itemClick(ListView view, int position, TextView title);
//
//	}
//
//	public void initBaseData(String[] categotyArr) {
//		this.categotyArr = categotyArr;
//		listView.removeFooterView(footerView);
//	}
//	
//	public void initCategoryData(BaseAdapter adapter,String[] categotyArr,String categotyArr) {
//		listView.setAdapter(adapter);
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		itemClickListener.itemClick(listView, position, chooseCategory);
//	}

}
