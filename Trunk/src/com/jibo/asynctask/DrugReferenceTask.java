//package com.jibo.asynctask;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import com.jibo.common.Constant;
//import com.jibo.data.entity.DosingData;
//import com.jibo.data.entity.DosingInfoEntity;
//import com.jibo.data.entity.DrugADRsEntity;
//import com.jibo.data.entity.DrugIndicationEntity;
//import com.jibo.data.entity.DrugInteractionEntity;
//import com.jibo.dbhelper.DrugAdapter;
///**
// * Drug信息数据库异步查找类
// * @author peter.pan
// * */
//public class DrugReferenceTask extends AsyncTask<String, Integer, Object > {
//
//    /**请求的类型 0药品详情； 1适应症 ；2 3不良反应；4 5药物相互作用 6**/
//	int reqType;//
//	private DrugReferenceCallBack callback;
//	static DrugAdapter dbAdapter;
//	
//	/**处理完成后的回调接口*/
//	public static interface DrugReferenceCallBack
//	{
//		void onDBSuccess(Object o,int type);
//		void onDBFailed(Exception e,int type);
//	}
//	public DrugReferenceTask( int type , Context context ,DrugReferenceCallBack call)
//	{
//		if(dbAdapter ==null)
//			dbAdapter=new DrugAdapter(context ,1);
//		callback =call;
//		reqType =type;
//	}
//	
//	@Override
//	protected void onPreExecute() {
//		//showDialog(Util.DIALOG_WAITING_FOR_DATA);
//	}
//
//
//	@Override
//	protected Object doInBackground(String... params) {	
//		//fill the data
//		String id = Constant.SPACE;
//		try {
//			switch(reqType)
//			{
//			case -1:
//				return dbAdapter.fillDrugsBrandFromById(params[0]);
//			case 0://药品详情
//				return dbAdapter.FillDrugsInfoDataByLocalDatabase(params[0]);
//			case 1:
//				DrugIndicationEntity en=dbAdapter.FillDrugsIndicationData(params[0]);
//				return en;
//			case 2:
//				DosingInfoEntity dosing =dbAdapter.FillDosingInfoDataByLocalDatabse(params[0], 0);
//				return dosing;
//			case 3:
//				DrugADRsEntity adr=dbAdapter.FillDrugsADRData(params[0]);
//				return adr;
//			case 4:
//				String ss=dbAdapter.FillDrugsContraindicationForTime(params[0],0);
//				return ss;
//			case 5:
//				DrugInteractionEntity die=dbAdapter.FillDrugsInteractionDataByLocalDatabase(params[0],0);
//				return die;
//			case 6:
//				String ptu[]=dbAdapter.FillDrugsDrugPedtricUseForTime(params[0], 0) ;
//				return ptu;
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
//        if(result instanceof Exception)
//        {
//        	if(callback!=null)
//        		callback.onDBFailed((Exception)result,reqType);
//        	return;
//        }
//        if(callback!=null)
//    		callback.onDBSuccess(result,reqType);
//	}
//
//	
//}
