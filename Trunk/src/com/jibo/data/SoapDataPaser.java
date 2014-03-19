/**
 * 
 */
package com.jibo.data;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.base.src.EntityObj;
import com.jibo.base.src.RequestResult;

/**
 * SoapData 解析基类
 * 
 * @author peter.pan
 * 
 */
public abstract class SoapDataPaser implements RequestResult{

	/**
	 * 解析是否成功
	 * */
	public boolean bSuccess;

	/**
	 * 解析数据
	 * */
	public abstract void paser(SoapSerializationEnvelope response);

	public List<EntityObj> rslt = new ArrayList<EntityObj>(0);

	public List<EntityObj> getObjs() {
		return rslt;
	}
}
