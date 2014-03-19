package com.jibo.data;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.common.SoapRes;

/**
 * 用药安全 获取更新条数
 * 
 * @author simon
 * 
 */
public class DrugAlertNewestCountPaser extends SoapDataPaser {

 private String count;

 @Override
 public void paser(SoapSerializationEnvelope response) {

  SoapObject result = (SoapObject) response.bodyIn;
  Object detail =  result
    .getProperty(SoapRes.RESULT_PROPERTY_GET_DRUGALERT_NEWESTCOUNT);
  count = detail.toString();
 }

 public String getCount() {
  return count;
 }

}