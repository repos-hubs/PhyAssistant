package com.jibo.asynctask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;

/**
 * @author peter.pan
 * 
 * 将本地数据库移动到系统目录,并移动16位
 */
public class DBMoveAsyncTask extends AsyncTask<String, Integer, Integer> {

	public Context context;
	
	public static String databaseFile = Constant.DATA_PATH + File.separator + "GBADATA" + File.separator;
	
	CallBack call;
	public DBMoveAsyncTask(Context con,CallBack call)
	{
		context =con;
		this.call =call;
	}
	
	private static int SUCCESS=0;
	private static final int ERR_SECURITY=1;
	private static final int ERR_FILE_NOT_FOUND=2;
	private static final int ERR=3;
	private String[] dbNames;
	@Override
	protected void onProgressUpdate(Integer... values) {
		
		//可以设置数据库移动是否成功
	}

	/**
	 * 将本地数据库移动到系统目录
	 * @param params 
	 * @return
	 */
	@Override
	protected Integer doInBackground(String... params) {
		dbNames=params;
		int size = params.length;
		FileInputStream is1 =null;
		FileOutputStream fos = null;
		for(int i = 0;i< size; i++)
		{
			String databaseFilename = databaseFile + params[i];
			System.out.println("databaseFilename     "+databaseFilename);
			try{
				is1 = new FileInputStream(databaseFilename);
				fos = new FileOutputStream(context.getFilesDir()+ File.separator +params[i]);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is1.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				publishProgress(i);
			}
			catch (SecurityException e) {
				e.printStackTrace();
				SUCCESS = -1;
				return ERR_SECURITY;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				SUCCESS = -1;
				return ERR_FILE_NOT_FOUND;
			} catch (IOException e) {
				e.printStackTrace();
				SUCCESS = -1;
				return ERR;
			}
			finally
			{
				try {
					if(fos!=null)
						fos.close();
					if(is1!=null)
						is1.close();
				} catch (IOException e) {
					SUCCESS = -1;
					e.printStackTrace();
				}
			}
		}
		return SUCCESS;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		boolean success= (result==0?true:false);
		System.out.println("success     "+success);
		if(call!=null)
			call.onFinish(success);
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	public static interface CallBack
	{
		void onFinish(boolean b);
	}
}
