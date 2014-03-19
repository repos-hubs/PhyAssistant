//package com.jibo.adapter;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.api.android.GBApp.R;
//import com.jibo.activity.BaseActivity;
//import com.jibo.activity.UpdatePackageActivity;
//import com.jibo.asynctask.DownloadAsyncTask1;
//import com.jibo.common.Constant;
//import com.jibo.common.Util;
//import com.jibo.data.entity.PackageInfoEntity;
//import com.jibo.dbhelper.PackageAdapter;
//import com.jibo.net.BaseResponseHandler;
//import com.jibo.ui.TextProgressBar;
//
//public class PackageDownloadAdapter extends BaseAdapter {
//
//	private Context context;
//	private ArrayList<PackageInfoEntity> packageInfoList;
//	private LayoutInflater inflater;
//	private ImageView img;
//	private BaseResponseHandler baseHandler;
//	private PackageAdapter packageAdapter;
//	
//	private int flag_current = 0;
//	private final int flag_no_data = 0;
//	private final int flag_installed_need_upgrade = 2;
//	private final int flag_installed_no_upgrade = 3;
//	private TextProgressBar tpb;
//	private HashMap<Integer, RelativeLayout> rltMap;
//	public PackageDownloadAdapter(Context context,
//			ArrayList<PackageInfoEntity> packageInfoList) {
//		inflater = LayoutInflater.from(context);
//		rltMap = new HashMap<Integer, RelativeLayout>();
//		packageAdapter = new PackageAdapter(context, 1);
//		baseHandler = new BaseResponseHandler((BaseActivity)context);
//		this.context = context;
//		this.packageInfoList = packageInfoList;
//	}
//
//	@Override
//	public int getCount() {
//		return packageInfoList.size();
//	}
//
//	@Override
//	public Object getItem(int arg0) {
//		return packageInfoList.get(arg0);
//	}
//
//	@Override
//	public long getItemId(int arg0) {
//		return 0;
//	}
//
//	@Override
//	public View getView(int arg0, View arg1, ViewGroup arg2) {
//		RelativeLayout rlt = (RelativeLayout) inflater.inflate(
//				R.layout.update_package_item, null);
//
//		PackageInfoEntity en = packageInfoList.get(arg0);
//		img = (ImageView) rlt.findViewById(R.id.img_icon);
//		img.setBackgroundResource(R.drawable.history_tool);
//		tpb = (TextProgressBar) rlt
//				.findViewById(R.id.tpb_progress);
//		TextView txtPackageName = (TextView) rlt
//				.findViewById(R.id.txt_package_name);
//		Button btn = (Button) rlt.findViewById(R.id.btn_package_status);
//		btn.setOnClickListener(new BtnClickListener(en, arg0));
//		btn.setTag(rlt);
//		//btn.setTag(en.getUrl());
//		flag_current = judgePackage(btn, en);
//		txtPackageName.setText(en.getTitle());
//		rltMap.put(arg0, rlt);
//		
//		return rlt;
//	}
//
//	private class BtnClickListener implements OnClickListener {
//		private PackageInfoEntity en;
//		private int position;
//		private String url;
//
//		public BtnClickListener(PackageInfoEntity en, int position) {
//			this.en = en;
//			this.position = position;
//		}
//
//		@Override
//		public void onClick(final View arg0) {
//			arg0.setVisibility(View.GONE);
//			RelativeLayout rlt =(RelativeLayout)arg0.getTag();
//			final TextProgressBar tpb = (TextProgressBar) rlt.findViewById(R.id.tpb_progress);
//			((UpdatePackageActivity)context).addRltItem(rlt, position);
//			
//			tpb.setVisibility(View.VISIBLE);
//			final int current = packageAdapter.getPackageStatus(en.getVersion(), en.getTitle());
//			url = "";
//			switch(current) {
//			case flag_no_data:
//				url = en.getFull_url();
//				break;
//			case flag_installed_need_upgrade:
//				url = en.getUpdate_url();
//				break;
//			}
//			DownloadAsyncTask1 downloadTask = new DownloadAsyncTask1(
//					context, position, url, baseHandler,
//					new DownloadAsyncTask1.CallBack() {
//						@Override
//						public void onFinish(boolean b) {
//							if(b) {
//								int lastIndex1 = url.lastIndexOf("/");
//								int lastIndex2 = url.lastIndexOf(".");
//								String fileName = url.substring(lastIndex1+1, lastIndex2);
//								switch(current) {
//								case flag_no_data:
//									packageAdapter.updateDBData(en.getTitle(), en.getVersion());
//									arg0.setVisibility(View.VISIBLE);
//									flag_current = judgePackage((Button) arg0, en);
//									tpb.setVisibility(View.GONE);
//									
//									break;
//								case flag_installed_need_upgrade:
//									
//									if(updateDB(en, url.substring(lastIndex1+1, lastIndex2))) {
//										arg0.setVisibility(View.VISIBLE);
//										flag_current = judgePackage((Button) arg0, en);
//										tpb.setVisibility(View.GONE);
//										Toast.makeText(context, context.getString(R.string.data_update_success), Toast.LENGTH_SHORT).show();
//									}
//									break;
//								}
//								Util.deleteDir(Constant.DPT_TMP_PATH+"/"+fileName+".db");
//								File file = new File(Constant.DPT_TMP_PATH);
//								if(file.listFiles().length == 0) {
//									file.delete();
//								}
//							}
//						}
//					});
//			downloadTask.execute(null);
//		}
//	}
//	
//	public boolean updateDB(PackageInfoEntity en, String tbName) {
//		boolean result = true;
//		boolean isFirst = true;
//		try {
//			SQLiteDatabase sdbTemp = SQLiteDatabase.openOrCreateDatabase(
//					Constant.DPT_TMP_PATH + "/" + tbName+".db", null);
//			String sql = "select * from "+tbName;
//			Cursor cursor = sdbTemp.rawQuery(sql, null);
//
//			while (cursor.moveToNext()) {
//				if (isFirst) {
//					packageAdapter.updateDBData(en.getTitle(), en.getVersion());
//					isFirst = false;
//				}
//
//				String targetTB = cursor.getString(2);
//				String targetSQL = cursor.getString(4);
//				SQLiteDatabase sdb = SQLiteDatabase
//						.openOrCreateDatabase(Constant.DATA_PATH_GBADATA
//								+ "/"+targetTB+".db", null);
//				sdb.execSQL(targetSQL);
//				sdb.close();
//			}
//			isFirst = true;
//			cursor.close();
//			sdbTemp.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			result = false;
//		}
//
//		return result;
//	}
//	public int judgePackage(Button btn, PackageInfoEntity en) {
//		int result = packageAdapter.getPackageStatus(en.getVersion(), en.getTitle());
//		switch(result) {
//		case flag_no_data:
//			btn.setText(context.getString(R.string.pkg_to_install));
//			btn.setEnabled(true);
//			break;
//		case flag_installed_need_upgrade:
//			btn.setText(context.getString(R.string.pkg_to_upgrade));
//			btn.setEnabled(true);
//			break;
//		case flag_installed_no_upgrade:
//			btn.setText(context.getString(R.string.pkg_installed));
//			btn.setEnabled(false);
//			break;
//		}
//		return result;
//	}
//}
