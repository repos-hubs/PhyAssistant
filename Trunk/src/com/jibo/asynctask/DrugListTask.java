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
//	int reqType;// ����0 ������ҩƷ 1
//	private DrugListCallBack callback;
//	static DrugAdapter dbAdapter;
//	
//	private DaoSession daoSession;
//
//	/**����ҩƷ�б��ʼ��*/
//	public static final int getCommonDrugList = 2;
//	/**����ҩƷ�б���ظ���*/
//	public static final int getCommonDrugListMore = 3;
//	/**����ҩƷ�б��ʼ��(���ļ�����TA4ID)*/
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
//		// type 2��ͨ 3��ͨ���� 4���� 5ҩƷ���� 6������ 7�������
//		String id = Constant.SPACE;
//		try {
//			// DrugReference.dataPreparation(getBaseContext(), id);
//			if (reqType == 0) {
//				// �ӷ���������б����ҩ���б�
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
//				//��װ��DrugListItem
//				if(drugList!=null&&drugList.size()>0){
//					for(DrugInfo obj : drugList){
//						list.add(new DrugListItem(null,obj,true));
//					}
//				}
//				
//				return list;
//			} else if (reqType == 2 || reqType == 3) {// ����ҩ���б�
//				int pageNumber = 0;
//				String searchKey = null;
//				if (params != null && params.length > 0) {
//					pageNumber = Integer.parseInt(params[0]);
//					searchKey = params[1];
//				}
//				List<DrugInfo> drugList = daoSession.getDrugInfoDao()
//						.queryDeep(pageNumber, searchKey);
//
//				// Ĭ�ϲ�ѯ����£���Ҫ������ϵ���̵����ݣ����ö�
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
//				//��������
//				List<DrugListItem> list = new ArrayList<DrugListItem>();
//				//��������
//				if(brandInfoList!=null&&brandInfoList.size()>0){
//					for(ManufutureBrandInfo obj : brandInfoList){
//						list.add(new DrugListItem(obj,null,false));
//					}
//				}
//				//ҩƷ����
//				if(drugList!=null&&drugList.size()>0){
//					for(DrugInfo obj : drugList){
//						list.add(new DrugListItem(null,obj,true));
//					}
//				}
//				
//				return list;
//			}
//			else if (reqType == 4) {
//				// ��ʾҩƷ����
//				id = params[0];// ҩƷ����ID
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
