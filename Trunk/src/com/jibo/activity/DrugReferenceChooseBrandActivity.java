package com.jibo.activity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.jibo.GBApplication;
import com.jibo.data.entity.DrugInfoEntity;
import com.jibo.data.entity.PromotionBrandInfoEntity;
import com.jibo.data.entity.SimpleDrugInfoEntity;
import com.jibo.dbhelper.HistoryAdapter;
import com.umeng.analytics.MobclickAgent;
import com.api.android.GBApp.R;

/**
 * 品牌选择页
 * 
 * @author simon
 * 
 */
public class DrugReferenceChooseBrandActivity extends BaseSearchActivity
		implements OnItemClickListener {

	private TextView title;
	private ListView mListView;
	private DrugInfoEntity entity;// 药品信息
	private ArrayList<PromotionBrandInfoEntity> brandList;// 品牌列表
	private GBApplication app;
	private HistoryAdapter historyAdapter;
	private ArrayList<SimpleDrugInfoEntity> drugInfoList;

	boolean fromDrugReference;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drug_choose_brand);
		super.onCreate(savedInstanceState);
		title = (TextView) findViewById(R.id.drug_choose_brand_title);
		mListView = (ListView) findViewById(R.id.lst_item);
		mListView.setOnItemClickListener(this);
		init();
	}

	/** 初始化UI */
	@SuppressWarnings("unchecked")
	private void init() {
		app = (GBApplication)getApplication();
		historyAdapter = new HistoryAdapter(this, 1, "mysqllite.db");
		Intent intent = getIntent();
		fromDrugReference =intent.getBooleanExtra("drugrefer",false );
		entity = (DrugInfoEntity) intent.getSerializableExtra("drugInfo");
		boolean isZh = Locale.getDefault().getLanguage().contains("zh");
		String titleMiddle = "";
		// 当前环境为中文
		if (isZh)
			titleMiddle = entity.nameCn;
		else
			titleMiddle = entity.nameEn;

		title.setText(getResources().getString(
				R.string.drug_choose_brand_title1)
				+ titleMiddle
				+ getResources().getString(R.string.drug_choose_brand_title2));

		ArrayList<?> data = (ArrayList<?>) intent
				.getParcelableArrayListExtra("promotionBrandList");
		brandList = (ArrayList<PromotionBrandInfoEntity>) data;
		drugInfoList = mergeListValues();
		DrugChooseAdapter adapter = new DrugChooseAdapter(this, drugInfoList);
		mListView.setAdapter(adapter);

	}

	/** 合并数据 */
	private ArrayList<SimpleDrugInfoEntity> mergeListValues() {
		ArrayList<SimpleDrugInfoEntity> items = new ArrayList<SimpleDrugInfoEntity>();
		SimpleDrugInfoEntity drugEntity = new SimpleDrugInfoEntity();
		if(entity!=null)
		{
			drugEntity.nameCn = entity.nameCn;
			drugEntity.flag = 0;
			items.add(drugEntity);
			System.out.println("nameCN####2    "+drugEntity.nameCn);
		}		
		for (PromotionBrandInfoEntity obj : brandList) {
			SimpleDrugInfoEntity brand = new SimpleDrugInfoEntity();
			System.out.println("brandName    "+obj.brandName);
			brand.nameCn = obj.brandName;
			System.out.println("nameCN####1    "+brand.nameCn);
			brand.flag = 1;
			items.add(brand);
		}
		return items;
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int colID = -1;
		for (Entry<?, ?> e : app.getPdaColumnMap().entrySet()) {
			if (e.getValue().toString()
					.equals(getString(R.string.drug))) {
				colID = (Integer) e.getKey();
			}
		}
		if (position == 0 && entity!=null) {
			Intent intent = new Intent(DrugReferenceChooseBrandActivity.this,
					NewDrugReferenceActivity.class);
			intent.putExtra("MAINPAGE", true);
			intent.putExtra("drugInfo", entity);
			intent.putExtra("hasbrand", true);
			intent.putExtra("mode", 0);
			System.out.println("#@@##nameCN1    "+drugInfoList.get(position).nameCn);
//			historyAdapter.storeViewHistory(app.getLogin()
//					.getGbUserName(), Integer.parseInt(entity.id), colID, -1,
//					"A-"+drugInfoList.get(position).nameCn);
			startActivity(intent);
		} else {
			Intent intent = new Intent(DrugReferenceChooseBrandActivity.this,
					NewDrugReferenceActivity.class);
			intent.putExtra("MAINPAGE", true);
			PromotionBrandInfoEntity temp =null;
			if(entity==null)
				temp=brandList.get(position );
			else
				temp=brandList.get(position-1 );
//			intent.putExtra("brandInfo",temp);
			intent.putExtra("hasbrand", true);
			intent.putExtra("mode", 2);
			for (Entry<?, ?> e : app.getPdaColumnMap().entrySet()) {
				if (e.getValue().toString()
						.equals(getString(R.string.drug))) {
					colID = (Integer) e.getKey();
				}
			}
			//System.out.println("#@@##nameCN    "+brandList.get(position-1).brandName);
			historyAdapter.storeViewHistory(app.getLogin()
					.getGbUserName(), Integer.parseInt(temp.brandId), colID, -1,
					"B-"+temp.brandName);
			if(fromDrugReference)
				setResult(10, intent);
			else
				startActivity(intent);
		}
	}
	
	
	
	
	class DrugChooseAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<SimpleDrugInfoEntity> dataList;


		public DrugChooseAdapter(Context context, ArrayList<SimpleDrugInfoEntity> list) {
			inflater = LayoutInflater.from(context);
			dataList = list;
		}

		@Override
		public int getCount() {
			if (dataList != null)
				return dataList.size();
			return 0;
		}

		@Override
		public SimpleDrugInfoEntity getItem(int position) {
			if (dataList != null)
				return dataList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHold holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.drugchoose, null);
				holder = new ViewHold();
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.tx1 = (TextView) convertView.findViewById(R.id.ListText1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHold) convertView.getTag();
			}
			if (dataList.get(position).flag == 1)
				holder.img.setVisibility(View.VISIBLE);
			else
				holder.img.setVisibility(View.GONE);
			holder.tx1.setText(dataList.get(position).nameCn);
			return convertView;
		}

		class ViewHold {
			ImageView img;
			TextView tx1;
		}
	}
}
