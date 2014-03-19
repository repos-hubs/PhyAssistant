//package com.jibo.asynctask;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import com.jibo.common.Constant;
//import com.jibo.dao.DaoSession;
//import com.jibo.dao.ManufutureBrandInfoDao;
//import com.jibo.data.entity.DrugInfoEntity;
//import com.jibo.data.entity.SimpleDrugInfoEntity;
//import com.jibo.dbhelper.DrugAdapter;
//import com.jibo.entity.ContactManufuture;
//import com.jibo.entity.DrugInfo;
//import com.jibo.entity.DrugListItem;
//import com.jibo.entity.ManufutureBrandInfo;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.text.TextUtils;
//import android.util.Log;
//
//public class DrugListTask extends AsyncTask<String, Integer, Object> {
//
//	private Intent intent;
//	int reqType;// 分类0 ，所有药品 1
//	private DrugListCallBack callback;
//	static DrugAdapter dbAdapter;
//	
//	private DaoSession daoSession;
//
//	/**常用药品列表初始化*/
//	public static final int getCommonDrugList = 2;
//	/**常用药品列表加载更多*/
//	public static final int getCommonDrugListMore = 3;
//	/**分类药品列表初始化(第四级分类TA4ID)*/
//	public static final int getTADrugList = 6;
//
//	public static interface DrugListCallBack {
//		void onDBSuccess(Object o, int type);
//		void onDBFailed(Exception e, int type);
//	}
//
//	public DrugListTask(int type, Context context, DrugListCallBack call,
//			DaoSession daosession) {
//		if (dbAdapter == null)
//			dbAdapter = new DrugAdapter(context, 1);
//		callback = call;
//		reqType = type;
//		this.daoSession = daosession;
//		System.out.println("type   " + type);
//	}
//
//	@Override
//	protected void onPreExecute() {
//		// showDialog(Util.DIALOG_WAITING_FOR_DATA);
//	}
//
//	@Override
//	protected Object doInBackground(String... params) {
//		// type 2普通 3普通更多 4详情 5药品子类 6分类中 7分类更多
//		String id = Constant.SPACE;
//		try {
//			// DrugReference.dataPreparation(getBaseContext(), id);
//			if (reqType == 0) {
//				// 从分类的数据列表查找药物列表
//				ArrayList<SimpleDrugInfoEntity> list = dbAdapter
//						.fillDrugsDataFromSearchDrug(null, false, 0);
//				return list;
//			} else if (reqType == 6 ) {
//				String taid =  params[0];
//				
//				List<DrugInfo> drugList = daoSession.getDrugInfoDao()
//						.queryDeepByTa(taid);
//				
//				List<DrugListItem> list = new ArrayList<DrugListItem>();
//	
//				//封装成DrugListItem
//				if(drugList!=null&&drugList.size()>0){
//					for(DrugInfo obj : drugList){
//						list.add(new DrugListItem(null,obj,true));
//					}
//				}
//				
//				return list;
//			} else if (reqType == 2 || reqType == 3) {// 检索药物列表
//				int pageNumber = 0;
//				String searchKey = null;
//				if (params != null && params.length > 0) {
//					pageNumber = Integer.parseInt(params[0]);
//					searchKey = params[1];
//				}
//				List<DrugInfo> drugList = daoSession.getDrugInfoDao()
//						.queryDeep(pageNumber, searchKey);
//
//				// 默认查询情况下，需要检索联系厂商的数据，并置顶
//				List<ManufutureBrandInfo> brandInfoList = null;
//				if (reqType == 2&&!TextUtils.isEmpty(searchKey)) {
//					searchKey = "%" + searchKey + "%";
//					brandInfoList = daoSession
//							.getManufutureBrandInfoDao()
//							.queryBuilder()
//							.whereOr(
//									ManufutureBrandInfoDao.Properties.GeneralName
//											.like(searchKey),
//									ManufutureBrandInfoDao.Properties.ZyName
//											.like(searchKey),
//									ManufutureBrandInfoDao.Properties.EnName
//											.like(searchKey),
//									ManufutureBrandInfoDao.Properties.PyName
//											.like(searchKey),
//									ManufutureBrandInfoDao.Properties.BrandName
//											.like(searchKey),
//									ManufutureBrandInfoDao.Properties.BrandNameEn
//											.like(searchKey)).list();
//				}
//				
//				//整理数据
//				List<DrugListItem> list = new ArrayList<DrugListItem>();
//				//厂商数据
//				if(brandInfoList!=null&&brandInfoList.size()>0){
//					for(ManufutureBrandInfo obj : brandInfoList){
//						list.add(new DrugListItem(obj,null,false));
//					}
//				}
//				//药品数据
//				if(drugList!=null&&drugList.size()>0){
//					for(DrugInfo obj : drugList){
//						list.add(new DrugListItem(null,obj,true));
//					}
//				}
//				
//				return list;
//			}
//			else if (reqType == 4) {
//				// 显示药品详情
//				id = params[0];// 药品分类ID
//				int flag = Integer.valueOf(params[1]);
//				if (flag == 0)
//					return dbAdapter.FillDrugsInfoDataByLocalDatabase(id);
//				else
//					return dbAdapter.fillDrugsBrandFromById(id);
//			} else if (reqType == 5) {
//				id = params[0];
//				return dbAdapter.getCategoryByTAID(id);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return e;
//		}
//
//		return null;
//	}
//
//	@Override
//	protected void onCancelled() {
//		super.onCancelled();
//	}
//
//	@Override
//	protected void onPostExecute(Object result) {
//		Log.i("GBA", "onPostExecute");
//		if (result instanceof Exception) {
//			if (callback != null)
//				callback.onDBFailed((Exception) result, reqType);
//			return;
//		}
//		if (callback != null)
//			callback.onDBSuccess(result, reqType);
//	}
//
//}
