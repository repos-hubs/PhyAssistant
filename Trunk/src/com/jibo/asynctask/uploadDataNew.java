package com.jibo.asynctask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import com.jibo.common.SharedPreferencesMgr;
import com.jibo.common.SoapRes;
import com.jibo.dbhelper.*;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class uploadDataNew {
	//Change part begin 
	private static String LOG_TAG = "AcademicActivities";
    private static String METHOD_NAME = "recordUserInfoVersion"; 
    private static String SOAP_ACTION = "http://www.pda.com/pda/recordUserInfoVersion";
    
    private static int iRecordLength;
    static String licenseNumber = null;
    
    private static String retrieveResult(){
    	return "recordUserInfoVersionResult";
    }
    
    /*
     * @author 	prime
     * @param	userid  	登陆账号
     * @param	cert_nume	执业证号
     * @param 	page_ID		页面Id
     * @param	datetime	登录时间
     * @param	DetailID	描述
     * @param	DetailIdName页面详细信息Id(如productid, 医学公式Id等)
     * @param	Demo		备注，方便于后期扩充
     * @return	boolean
     */
    public static boolean uploadLoginLogNew(Context context,String dateTime,String page_ID, String DetailID, String DetailIdName, String Demo) {
    	String userid=null;
    	boolean success = true;
   		String userName = "";

   		if(userid == null)
   			userName = SharedPreferencesMgr.getUserName();
   		else
   			userName = userid;
   		
   		String[] log = {
				userName,
				licenseNumber,
				page_ID,
				DetailID,
				DetailIdName,
				dateTime
		};
   		
    	if(!isWifi(context) && !isMobile(context)){
    		success = false;
    	}else{
    		try {
    	   		//creating
    	   		/*RegistrationData.initRegistrationPreferences(Util.mAct);
    	   		String licenseNumber = RegistrationData.getLicenseNumber();
    	   		String userName = Util.NULL;

    	   		if(userid == null)
    	   			userName = RegistrationData.getUserName();
    	   		else
    	   			userName = userid;*/
    	   		
    			SoapObject rpc = new SoapObject(SoapRes.NAMESPACE, METHOD_NAME);
    			if(userName == null && licenseNumber == null)return false;
    			if(userName != null)		rpc.addProperty(getLoginName(), userName);
    			if(licenseNumber != null) 	rpc.addProperty(getNumber(), licenseNumber);
    			if(page_ID != null)			rpc.addProperty(getPageId(), page_ID);
    			if(DetailID != null)		rpc.addProperty(getDetailId(), DetailID);
    			if(DetailIdName != null)	rpc.addProperty(getDetailIdName(), DetailIdName);
    			if(Demo != null	)			rpc.addProperty(getRemark(), Demo);
    			if(dateTime!=null)          rpc.addProperty(getDateTime(), dateTime);
    			
    			rpc.addProperty(getSourceFrom(), "Android");
    			rpc.addProperty("Version",SharedPreferencesMgr.getVersion());
    			Log.e("12121versionversion",SharedPreferencesMgr.getVersion());
//URLUSERLOGINFO
    			//封装
    			AndroidHttpTransport ht = new AndroidHttpTransport(SoapRes.URLUSERLOGINFO);
    			ht.debug = true; //open debug info.

    			//提交并应答
    			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
    					SoapEnvelope.VER11); //Version 11.
    			envelope.bodyOut = rpc;
    			envelope.dotNet = true;
    			envelope.setOutputSoapObject(rpc);
    			ht.call(SOAP_ACTION, envelope);
    			
    			SoapRes.debug(LOG_TAG, "DUMP>> " + ht.requestDump);
    			SoapRes.debug(LOG_TAG, "DUMP<< " + ht.responseDump);
    		} catch (Exception e) {
    			e.printStackTrace();
    			success = false;
    		}
    	}
    	if(!success){
    		UpLoadDataAdapter uploadAdapter=new UpLoadDataAdapter(context);
    		uploadAdapter.insertLog(log);
    	}
    	
    	return success;
	}
    
    /**
     * @author
     * @Description
     * @param
     * @return
     */
    public static boolean uploadLoginLog(Context context,String userid, String license, String page_ID, 
			String DetailID, String DetailIdName, String Demo, boolean isFromSQL) {
    	boolean success = false;
    	licenseNumber = license;
    	if(isFromSQL){
    		success = uploadLoginLogNew(context,userid, page_ID, DetailID, DetailIdName, Demo);
    	}
    	return success;
    }

    private static String getMachineId() {
    	return "machineId";
    }
    
	private static String getLoginName() {
		return "userid";
	}
	
	private static String getNumber() {
		return "cert_nume";
	}
	
	private static String getPageId() {
		return "page_ID";
	}
	
	private static String getDetailId() {
		return "DetailID";
	}
	
	private static String getDetailIdName(){
		return "DetailIDName";
	}
	
	private static String getRemark() {
		return "Demo";
	}
	private static String getDateTime()
	{
		return "date_time";
	}
	
	private static String getSourceFrom(){
		return "sourcefrom";
	}
	
	/**
	 * @author Prime Zhang
	 * @Description Checking if wifi is available
	 * @param context Context
	 * @return true: wifi network is available, false: wifi network is unavailable.
	 */
	private static boolean isWifi(Context context){
		ConnectivityManager cm 	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni 			= cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiAvail 	= ni.isAvailable();
		boolean isWifiConnect 	= ni.isConnected();
		return isWifiAvail && isWifiConnect;
	}
	
	/**
	 * @author Prime Zhang
	 * @Description Checking if Mobile network is available
	 * @param context Context
	 * @return true: mobile network is available, false: mobile network is unavailable.
	 */
	private static boolean isMobile(Context context){
		ConnectivityManager cm 	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni			= cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileAvail 	= ni.isAvailable();
		boolean isMobileConnect = ni.isConnected();
		return isMobileAvail && isMobileConnect;
	}
}
