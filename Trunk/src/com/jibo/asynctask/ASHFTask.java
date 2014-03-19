//package com.jibo.asynctask;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.jibo.common.Constant;
//import com.jibo.dbhelper.ASHFAdapter;
//
///**
// * Drug��Ϣ���ݿ��첽������
// * 
// * @author peter.pan
// * */
//public class ASHFTask extends AsyncTask<String, Integer, Object> {
//
//	/** ��������� **/
//	int reqType;//
//	private ASHFReferenceCallBack callback;
//	private ASHFAdapter dbAdapter;
//
//	/** ������ɺ�Ļص��ӿ� */
//	public static interface ASHFReferenceCallBack {
//		void onDBSuccess(Object o, int type);
//
//		void onDBFailed(Exception e, int type);
//	}
//
//	public ASHFTask(int type, Context context, ASHFReferenceCallBack call) {
//		if (dbAdapter == null)
//			dbAdapter = new ASHFAdapter(context, 1);
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
//		try {
//			if (params == null)
//				return null;
//			return dbAdapter.selectASHFdatas(params[0]);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return e;
//		}
//
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
