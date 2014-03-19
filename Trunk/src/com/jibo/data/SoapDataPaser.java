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
 * SoapData ��������
 * 
 * @author peter.pan
 * 
 */
public abstract class SoapDataPaser implements RequestResult{

	/**
	 * �����Ƿ�ɹ�
	 * */
	public boolean bSuccess;

	/**
	 * ��������
	 * */
	public abstract void paser(SoapSerializationEnvelope response);

	public List<EntityObj> rslt = new ArrayList<EntityObj>(0);

	public List<EntityObj> getObjs() {
		return rslt;
	}
}
