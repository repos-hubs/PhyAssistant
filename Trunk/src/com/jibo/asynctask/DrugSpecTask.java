//package com.jibo.asynctask;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import com.jibo.common.Constant;
//import com.jibo.data.entity.DrugInfoEntity; 
//import com.jibo.data.entity.SimpleDrugInfoEntity;
//import com.jibo.dbhelper.DrugAdapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.util.Log;
//
//public class DrugSpecTask extends AsyncTask<String, Integer, Object> {
//
//	private Intent intent;
//	int reqType;// ����0 ������ҩƷ 1
//	private DrugListCallBack callback;
//	static DrugAdapter dbAdapter;
//
//	public static interface DrugListCallBack {
//		void onDBSuccess(Object o, int type);
//
//		void onDBFailed(Exception e, int type);
//	}
//
//	public DrugSpecTask(int type, Context context, DrugListCallBack call) {
//		if (dbAdapter == null)
//			dbAdapter = new DrugAdapter(context, 1);
//		callback = call;
//		reqType = type;
//	}
//
//	@Override
//	protected void onPreExecute() {
//		// showDialog(Util.DIALOG_WAITING_FOR_DATA);
//	}
//
//	@Override
//	protected Object doInBackground(String... params) {
//		//type 2��ͨ 3��ͨ���� 4���� 5ҩƷ���� 6������ 7�������
//		String id = Constant.SPACE;
//		try {
//			// DrugReference.dataPreparation(getBaseContext(), id);
//			if (reqType == 0) {
//				// �ӷ���������б����ҩ���б�
//				ArrayList<SimpleDrugInfoEntity> list = dbAdapter
//						.fillDrugsDataFromSearchDrug(null, false, 0);
//				return list;
//			} else if (reqType == 6 || reqType == 7) {
//
//				int position = 0;
//				boolean isSearch = false;
//				String tagId = null;
//				String whos2n = null;
//				if (params != null && params.length > 0) {
//
//					tagId = params[0];
//					whos2n = params[1];
//					position = Integer.parseInt(params[2]);
//				}
//				return dbAdapter.fillDrugsDataBySearchDrug(tagId, whos2n,
//						position);
//			} else if (reqType == 2 || reqType == 3) {
//				// ����ҩ���б�
//				//
//				int position = 0;
//				boolean isSearch = false;
//				String searchKey = null;
//				if (params != null && params.length > 0) {
//					position = Integer.parseInt(params[0]);
//					searchKey = params[1];
//					if (searchKey != null)
//						isSearch = true;
//				}
//
//				ArrayList<SimpleDrugInfoEntity> drugList = dbAdapter
//						.fillDrugsDataFromSearchDrug(searchKey, isSearch,
//								position);
//				if (isSearch && reqType == 2) {
//					ArrayList<SimpleDrugInfoEntity> brandList = dbAdapter
//							.fillDrugsBrandFromSearchDrug(searchKey);
//					if (brandList != null && brandList.size() > 0) {
//						if (null != drugList && drugList.size() > 0)
//							brandList.addAll(drugList);
//						return brandList;
//					}
//				}
//				return drugList;
//			} else if (reqType == 4) {
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
//
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
